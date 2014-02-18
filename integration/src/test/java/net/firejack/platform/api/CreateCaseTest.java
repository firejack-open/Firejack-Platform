/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api;


import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.RootDomain;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Tet Case populates test data for the inbox page
 */
public class CreateCaseTest {

    @Test
    public void createInboxPageData() {
        try {
            //OPFEngine.init("http://192.168.0.190:8080/platform", "admin", "111111");
            //OPFEngine.init("http://50.28.29.190:8080/platform", "admin", "111111");
            OPFEngine.init("http://web.firejack.net:8080/platform", "admin", "111111");
            //OPFEngine.init("admin", "123123");

            //Preparing necessary registry meta data in order to populate dummy cases for the inbox page
            //----------------------------------------------------------------------
            RootDomain testRootDomain = readOrCreateRootDomain();
            if (testRootDomain != null) {
                Package pkg = readOrCreatePackage(testRootDomain);
                if (pkg != null) {
                    Domain domain = readOrCreateDomain(pkg);
                    Map<Integer, Actor> actorsMap = new HashMap<Integer, Actor>();
                    boolean actorsPopulated = readOrCreateActor(domain, actorsMap, 1) &&
                            readOrCreateActor(domain, actorsMap, 2) &&
                            readOrCreateActor(domain, actorsMap, 3);
                    if (actorsPopulated) {
                        Process process = readOrCreateProcess(domain, actorsMap);
                        if (process == null) {
                            System.err.println("Failed to prepare registry data for the Inbox Page.");
                        } else {
                            populateCases("com.test.pkg.domain.process");
                            System.out.println("Registry data for the Inbox Page were prepared successfully.");
                        }
                    }
                }
            }
            OPFEngine.release();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }

    private void populateCases(String processLookup) {
        //create necessary cases
        CaseOperationsParams params = new CaseOperationsParams();
        //params.setProcessLookup("com.test.pkg.domain.process");
        params.setProcessLookup(processLookup);
        params.setAssigneeId(OPFContext.getContext().getPrincipal().getUserInfoProvider().getId());
        for (int i = 0; i < 30; i++) {
            ServiceResponse<SimpleIdentifier<Long>> response = OPFEngine.ProcessService.startCase(params);
            System.out.println("Response status = " + (response.isSuccess() ? "success" : "failure") +
                    "; response message = " + response.getMessage());
        }
    }

    private RootDomain readOrCreateRootDomain() {
        System.out.println("Trying to read all root domains.");
        RootDomain foundRootDomain = null;
        ServiceResponse<RootDomain> rootDomainsResponse = OPFEngine.RegistryService.readAllRootDomains();
        if (rootDomainsResponse.isSuccess()) {
            if (rootDomainsResponse.getData() != null && rootDomainsResponse.getData().size() > 0) {
                for (RootDomain rootDomain : rootDomainsResponse.getData()) {
                    if (rootDomain.getLookup().equals("com.test")) {
                        foundRootDomain = rootDomain;
                        break;
                    }
                }
                if (foundRootDomain == null) {
                    System.out.println("Test root domain has not found. Trying to create test root domains.");
                    foundRootDomain = new RootDomain();
                    foundRootDomain.setName("test.com");
                    foundRootDomain.setLookup("com.test");
                    foundRootDomain.setDescription("Test root domain for generating process cases.");
                    ServiceResponse<RegistryNodeTree> createRootDomainResp =
                            OPFEngine.RegistryService.createRootDomain(foundRootDomain);
                    if (createRootDomainResp.isSuccess()) {
                        foundRootDomain.setId(createRootDomainResp.getData().get(0).getId());
                    } else {
                        foundRootDomain = null;
                        System.err.println(createRootDomainResp.getMessage());
                    }
                }
            }
        } else {
            System.err.println(rootDomainsResponse.getMessage());
        }
        return foundRootDomain;
    }

    private Package readOrCreatePackage(RootDomain rootDomain) {
        System.out.println("Trying to read all packages...");
        ServiceResponse<Package> resp = OPFEngine.RegistryService.readAllPackages();
        Package pkg = null;
        if (resp.isSuccess()) {
            if (resp.getData() != null && resp.getData().size() > 0) {
                for (Package p : resp.getData()) {
                    if (p.getLookup().equals("com.test.pkg")) {
                        pkg = p;
                        break;
                    }
                }
                if (pkg == null) {
                    System.out.println("Test package has not found. Trying to create package...");
                    pkg = new Package();
                    pkg.setName("pkg");
                    pkg.setPath("com.test");
                    pkg.setLookup("com.test.pkg");
                    pkg.setPrefix("pkg");
                    pkg.setUrlPath("/pkg");
                    pkg.setParentId(rootDomain.getId());
                    ServiceResponse<RegistryNodeTree> createDomainResp = OPFEngine.RegistryService.createPackage(pkg);
                    if (createDomainResp.isSuccess()) {
                        pkg.setId(createDomainResp.getItem().getId());
                    } else {
                        pkg = null;
                        System.err.println(createDomainResp.getMessage());
                    }
                }
            }
        } else {
            System.err.println(resp.getMessage());
        }
        return pkg;
    }

    private Domain readOrCreateDomain(Package pkg) {
        System.out.println("Trying to read all domains...");
        ServiceResponse<Domain> resp = OPFEngine.RegistryService.readAllDomains();
        Domain domain = null;
        if (resp.isSuccess()) {
            if (resp.getData() != null && resp.getData().size() > 0) {
                for (Domain d : resp.getData()) {
                    if (d.getLookup().equals("com.test.pkg.domain")) {
                        domain = d;
                        break;
                    }
                }
                if (domain == null) {
                    System.out.println("Test domain has not found. Trying to create domain...");
                    domain = new Domain();
                    domain.setName("domain");
                    domain.setPath("com.test.pkg");
                    domain.setLookup("com.test.pkg.domain");
                    domain.setParentId(pkg.getId());
                    ServiceResponse<RegistryNodeTree> createDomainResp = OPFEngine.RegistryService.createDomain(domain);
                    if (createDomainResp.isSuccess()) {
                        domain.setId(createDomainResp.getItem().getId());
                    } else {
                        domain = null;
                        System.err.println(createDomainResp.getMessage());
                    }
                }
            }
        } else {
            System.err.println(resp.getMessage());
        }
        return domain;
    }

    private Process readOrCreateProcess(Domain domain, Map<Integer, Actor> actorsMap) {
        System.out.println("Trying to search for the process...");
        ServiceResponse<Process> resp = OPFEngine.ProcessService.searchProcesses("com.test.pkg.domain.process", null);
        Process process = null;
        if (resp.isSuccess()) {
            if (resp.getData() != null && resp.getData().size() > 0) {
                for (Process p : resp.getData()) {
                    if (p.getLookup().equals("com.test.pkg.domain.process")) {
                        process = p;
                        break;
                    }
                }
            }
            if (process == null) {
                System.out.println("Test process has not found. Trying to create process...");
                process = new Process();
                process.setName("Process");
                process.setPath("com.test.pkg.domain");
                process.setLookup("com.test.pkg.domain.process");
                process.setParentId(domain.getId());

                List<Activity> activities = new ArrayList<Activity>();
                List<Status> statuses = new ArrayList<Status>();
                List<CaseExplanation> explanations = new ArrayList<CaseExplanation>();
                for (Map.Entry<Integer, Actor> actorEntry : actorsMap.entrySet()) {
                    Activity activity = prepareActivity(process, actorEntry.getValue(), actorEntry.getKey());
                    activities.add(activity);
                    statuses.add(prepareStatus(process, actorEntry.getKey()));
                    explanations.add(prepareExplanation(actorEntry.getKey()));
                }
                process.setActivities(activities);
                process.setStatuses(statuses);
                process.setExplanations(explanations);

                ServiceResponse<Process> createProcessResp = OPFEngine.ProcessService.createProcess(process);
                if (createProcessResp.isSuccess()) {
                    process = createProcessResp.getItem();
                } else {
                    process = null;
                    System.err.println(createProcessResp.getMessage());
                }
            }
        } else {
            System.err.println(resp.getMessage());
        }
        return process;
    }

    private boolean readOrCreateActor(Domain domain, Map<Integer, Actor> actorsMap, Integer sortPosition) {
        String actorName = "Actor" + sortPosition;
        String normalizedName = StringUtils.normalize(actorName);
        String lookup = domain.getLookup() + '.' + normalizedName;
        System.out.println("Trying to find the actor...");
        ServiceResponse<Actor> response = OPFEngine.ProcessService.readActorByLookup(lookup);
        Actor actor = null;
        if (response.isSuccess()) {
            actor = response.getItem();
            if (actor == null) {
                System.out.println("The actor was not found. Trying to create the actor...");
                actor = new Actor();
                actor.setName(actorName);
                actor.setPath(domain.getLookup());
                actor.setLookup(lookup);
                actor.setParentId(domain.getId());
                actor.setDescription("Actor for testing purpose.");
                actor.setDistributionEmail(normalizedName + "@actor.act");
                ServiceResponse<Actor> createActorResponse = OPFEngine.ProcessService.createActor(actor);
                if (createActorResponse.isSuccess()) {
                    actor = createActorResponse.getItem();
                } else {
                    System.err.println("Failed to create the actor...");
                    actor = null;
                }
            }
            if (actor != null) {
                actorsMap.put(sortPosition, actor);
            }
        } else {
            System.err.println(response.getMessage());
        }
        return actor != null;
    }

    private Status prepareStatus(Process process, Integer sortPosition) {
        Status status = new Status();
        status.setName("Status" + sortPosition);
        status.setPath(process.getLookup());
        status.setLookup(process.getLookup() + '.' + StringUtils.normalize(status.getName()));
        status.setDescription(status.getName() + " description.");
        status.setSortPosition(sortPosition);
        return status;
    }

    private Activity prepareActivity(Process process, Actor actor, Integer sortPosition) {
        Activity activity = new Activity();
        activity.setName("Activity" + sortPosition);
        activity.setPath(process.getLookup());
        activity.setLookup(process.getLookup() + '.' + StringUtils.normalize(activity.getName()));
        activity.setDescription(activity.getName() + " description.");
        activity.setActivityType(ActivityType.HUMAN);
        activity.setNotify(true);
        activity.setSortPosition(sortPosition);
        activity.setActor(actor);
        Status status = prepareStatus(process, sortPosition);
        status.setId(-sortPosition.longValue());
        activity.setStatus(status);
        return activity;
    }

    private CaseExplanation prepareExplanation(Integer sortPosition) {
        CaseExplanation explanation = new CaseExplanation();
        explanation.setShortDescription("Explanation" + sortPosition);
        explanation.setLongDescription(explanation.getShortDescription() + " description");
        return explanation;
    }

}