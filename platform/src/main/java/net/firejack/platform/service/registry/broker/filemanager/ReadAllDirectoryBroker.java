/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.registry.broker.filemanager;

import net.firejack.platform.api.registry.domain.FileTree;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("readAllDirectoryBroker")
public class ReadAllDirectoryBroker
        extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileTree>> {

    protected static final String NODE_FILE = "file";
    protected static final String NODE_FOLDER = "folder";

    @Override
    protected ServiceResponse<FileTree> perform(ServiceRequest<NamedValues> request) throws Exception {
        String path = (String) request.getData().get("path");
        final Boolean directoryOnly = (Boolean) request.getData().get("directoryOnly");
        File[] files = new File[0];
        if (StringUtils.isBlank(path)) {
            files = File.listRoots();
        } else {
            File folder = new File(path);
            if (folder.isDirectory()) {
                files = folder.listFiles( new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return !file.isHidden() && (file.isDirectory() || !directoryOnly);
                    }
                });
            }
        }
        List<FileTree> fileTrees = new ArrayList<FileTree>();
        List<File> fileList = Arrays.asList(files);
        for (File file : fileList) {
            fileTrees.add(convertToDTO(file));
        }
        return new ServiceResponse<FileTree>(fileTrees, "Success", true);
    }

    private FileTree convertToDTO(File file) {
        FileTree fileTree = new FileTree();
        fileTree.setPath(file.getAbsolutePath());
        String name;
        if (file.getParent() != null) {
            name = file.getName();
        } else {
            name = file.getAbsolutePath();
            name = name.replaceAll("[/\\\\]", "");
        }
        fileTree.setText(name);
        if (file.isDirectory()) {
            fileTree.setCls(NODE_FOLDER);
            fileTree.setLeaf(false);
            fileTree.setExpandable(true);
        } else {
            fileTree.setCls(NODE_FILE);
            fileTree.setLeaf(true);
        }
        return fileTree;
    }

}