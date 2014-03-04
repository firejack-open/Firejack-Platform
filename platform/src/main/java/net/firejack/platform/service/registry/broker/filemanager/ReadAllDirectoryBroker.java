/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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