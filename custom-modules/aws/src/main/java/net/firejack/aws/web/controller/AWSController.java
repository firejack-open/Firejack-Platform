/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.aws.web.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Region;
import net.firejack.aws.web.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;


@Controller
@Scope("session")
@RequestMapping(value = {"/", "/aws"})
public class AWSController {
    private AmazonEC2 amazonEC2;

    @Value("${owner}")
    private String owner;

    @Value("${instance.type}")
    private String instanceType;

    @Value("${instance.ami}")
    private String instanceAmi;

    @Value("${instance.keyPair.name}")
    private String keyPairName;

    @Value("${instance.securityGroup.name}")
    private String securityGroupName;

    @Value("${instance.region}")
    private String instanceRegion;

    @RequestMapping(method = RequestMethod.GET)
    public String init() {
        return "aws";
    }

    @ResponseBody
    @RequestMapping(value = "auth", method = RequestMethod.POST)
    public List<Dropdown> auth(@RequestBody Auth auth) {
        if (!auth.isValid())
            throw new AmazonServiceException("Access or Secret Key is empty");

        if (amazonEC2 != null)
            amazonEC2.shutdown();

        amazonEC2 = new AmazonEC2Client(new BasicAWSCredentials(auth.getAccessKey(), auth.getSecretKey()));

        DescribeRegionsResult result = amazonEC2.describeRegions();
        List<Region> resultRegions = result.getRegions();

        List<Dropdown> regions = new ArrayList<Dropdown>(resultRegions.size());
        for (Region region : resultRegions)
            regions.add(new Dropdown(region.getRegionName(), region.getEndpoint()));

        return regions;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @RequestMapping(value = "region/{region}", method = RequestMethod.GET)
    public InstanceDetails changeRegion(@PathVariable("region") String region) {
        if (amazonEC2 == null)
            throw new AmazonServiceException("Amazon service can't initialize");
        if (region.isEmpty())
            throw new AmazonServiceException("Region is empty");

        amazonEC2.setRegion(RegionUtils.getRegion(region));

        InstanceDetails details = new InstanceDetails();

        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setOwners(Arrays.asList(owner));
        DescribeImagesResult result = amazonEC2.describeImages(request);

        List<Image> images = result.getImages();
        List<Dropdown> amis = new ArrayList<Dropdown>(images.size());

        for (Image image : images)
            amis.add(new Dropdown(image.getImageId(), image.getName()));
        details.setAmis(amis);

        DescribeSecurityGroupsResult securityGroupsResult = amazonEC2.describeSecurityGroups();

        List<SecurityGroup> securityGroups = securityGroupsResult.getSecurityGroups();
        List<Dropdown> groups = new ArrayList<Dropdown>(securityGroups.size());

        for (SecurityGroup group : securityGroups)
            groups.add(new Dropdown(group.getGroupId(), group.getGroupName()));
        details.setSecurityGroups(groups);


        DescribeKeyPairsResult keyPairsResult = amazonEC2.describeKeyPairs();

        List<KeyPairInfo> keyPairInfos = keyPairsResult.getKeyPairs();
        List<Dropdown> keyPairs = new ArrayList<Dropdown>(keyPairInfos.size());

        for (KeyPairInfo keyPairInfo : keyPairInfos)
            keyPairs.add(new Dropdown(keyPairInfo.getKeyName(), keyPairInfo.getKeyName()));
        details.setKeys(keyPairs);
        details.setInstanceTypes(InstanceType.values());

        return details;
    }

    @ResponseBody
    @RequestMapping(value = "instance", method = RequestMethod.POST)
    public Status spotInstance(@RequestBody InstanceModel instance) {
        if (amazonEC2 == null)
            throw new AmazonServiceException("Amazon service can't initialize");
        if (!instance.isValid())
            throw new AmazonServiceException("Invalid message");

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.setInstanceType(instance.getInstanceType());
        runInstancesRequest.setImageId(instance.getAmi());
        runInstancesRequest.setMinCount(1);
        runInstancesRequest.setMaxCount(1);
        runInstancesRequest.setKeyName(instance.getKey());
        runInstancesRequest.setSecurityGroupIds(Arrays.asList(instance.getSecurityGroup()));

        amazonEC2.runInstances(runInstancesRequest);

        return new Status("Server has been started");
    }

    @ResponseBody
    @RequestMapping(value = "install", method = RequestMethod.POST)
    public Status startInstance(@RequestBody Auth auth) {
        if (!auth.isValid())
            throw new AmazonServiceException("Access or Secret Key is empty");

        if (amazonEC2 != null) {
            amazonEC2.shutdown();
        }

        amazonEC2 = new AmazonEC2Client(new BasicAWSCredentials(auth.getAccessKey(), auth.getSecretKey()));
        amazonEC2.setRegion(RegionUtils.getRegion(instanceRegion));

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.setInstanceType(InstanceType.fromValue(instanceType));
        runInstancesRequest.setImageId(instanceAmi);
        runInstancesRequest.setMinCount(1);
        runInstancesRequest.setMaxCount(1);

        KeyPair keyPair = createKeyPair();
        String privateKey = keyPair.getKeyMaterial();
        String fileName;
        try {
            fileName = saveKeyFile(keyPair.getKeyName(), privateKey);
        } catch (FileNotFoundException e) {
            throw new AmazonServiceException("Could not create the key file");
        } catch (UnsupportedEncodingException e) {
            throw new AmazonServiceException("Could not create the key file");
        }
        runInstancesRequest.setKeyName(keyPair.getKeyName());

        CreateSecurityGroupResult securityGroupResult = createSecurityGroupWithRules();
        Collection securityGroupIds = new ArrayList();
        securityGroupIds.add(securityGroupResult.getGroupId());
        runInstancesRequest.setSecurityGroupIds(securityGroupIds);

        amazonEC2.runInstances(runInstancesRequest);

        return new Status("Server has been started", fileName);
    }

    @RequestMapping(value = "/install/downloadKey", method = RequestMethod.GET)
    public void downloadLicenseFile(@RequestParam(value="file") String file, HttpServletResponse response) throws Exception {

        File licenseFile = new File ("/tmp/" + file);
        InputStream is = new FileInputStream(licenseFile);

        // set file as attached data and copy file data to response output stream
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
        FileCopyUtils.copy(is, response.getOutputStream());

        // delete file on server file system
        licenseFile.delete();

        // close stream and return to view
        response.flushBuffer();
    }

    @ExceptionHandler(AmazonServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(AmazonServiceException ex) {
        String message = ex.getMessage();
        return new ModelAndView(new MappingJacksonJsonView(), Collections.singletonMap("error", message.replaceAll(".*?AWS Error Message: ","")));
    }

    private KeyPair createKeyPair() {
        if (amazonEC2 == null) {
            throw new AmazonServiceException("Amazon service can't initialize");
        }

        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        String name = keyPairName + "-" + genRandomString();
        createKeyPairRequest.withKeyName(name);
        CreateKeyPairResult createKeyPairResult = amazonEC2.createKeyPair(createKeyPairRequest);
        KeyPair keyPair = createKeyPairResult.getKeyPair();

        return keyPair;
    }

    private CreateSecurityGroupResult createSecurityGroupWithRules() {
        if (amazonEC2 == null) {
            throw new AmazonServiceException("Amazon service can't initialize");
        }

        CreateSecurityGroupRequest createSecurityGroupRequest =
                new CreateSecurityGroupRequest();
        String name = securityGroupName + "-" + genRandomString();
        createSecurityGroupRequest.withGroupName(name)
                .withDescription("Security Group for Platform running");
        CreateSecurityGroupResult securityGroupResult =
                amazonEC2.createSecurityGroup(createSecurityGroupRequest);

        IpPermission sshPermission = new IpPermission();
        sshPermission.withIpRanges("0.0.0.0/0")
                .withIpProtocol("tcp")
                .withFromPort(22)
                .withToPort(22);

        IpPermission httpPermission = new IpPermission();
        httpPermission.withIpRanges("0.0.0.0/0")
                .withIpProtocol("tcp")
                .withFromPort(8080)
                .withToPort(8080);

        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
                new AuthorizeSecurityGroupIngressRequest();

        authorizeSecurityGroupIngressRequest.withGroupName(name)
                .withIpPermissions(sshPermission, httpPermission);

        amazonEC2.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

        return securityGroupResult;
    }

    private String saveKeyFile(String keyName, String body) throws FileNotFoundException, UnsupportedEncodingException {
        String fileName = keyName + ".pem";
        String path = "/tmp/" + fileName;
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.print(body);
        writer.close();
        return fileName;
    }

    private String genRandomString() {
        return new BigInteger(130, new SecureRandom()).toString(32).substring(0, 10);
    }
}