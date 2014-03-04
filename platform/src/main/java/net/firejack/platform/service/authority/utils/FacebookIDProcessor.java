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

package net.firejack.platform.service.authority.utils;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.content.domain.ImageFileInfo;
import net.firejack.platform.api.content.domain.ImageResourceVersion;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.resource.Cultures;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.socials.facebook.FacebookConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FacebookIDProcessor {

    private static final Logger logger = Logger.getLogger(FacebookIDProcessor.class);

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("directoryStore")
    private IRegistryNodeStore<DirectoryModel> directoryStore;

    public UserModel findUserByFacebookId(Long facebookId) {
        return userStore.findUserByFacebookId(facebookId);
    }

    public Tuple<UserModel, List<RoleModel>> createUserFromFacebookIDAttributes(Long facebookId, Map<String, String> values) {
        String username = values.get(FacebookConstants.FB_ATTR_USERNAME).toLowerCase().replaceAll("[\\s\\p{Punct}]+", "");
        String firstName = values.get(FacebookConstants.FB_ATTR_FIRST_NAME);
        String lastName = values.get(FacebookConstants.FB_ATTR_LAST_NAME);

        if (StringUtils.isBlank(username)) {
            if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
                username = (firstName + lastName).toLowerCase();
            }
        }
        int usernameSuffix = 1;
        boolean isUsernameUnique = false;
        while (!isUsernameUnique) {
            String checkUsername = username;
            if (usernameSuffix > 1) {
                checkUsername += usernameSuffix;
            }
            UserModel user = userStore.findUserByUsername(checkUsername);
            if (user == null) {
                username = checkUsername;
                isUsernameUnique = true;
            } else {
                usernameSuffix++;
            }
        }

        UserModel newUser = new UserModel();
        newUser.setUsername(username);
        newUser.setEmail(username + "@change.me");
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setFacebookId(facebookId);

        List<RoleModel> roles = roleStore.findByName(OpenFlame.USER_ROLE_NAME);
        List<UserRoleModel> userRoles = new ArrayList<UserRoleModel>();
        for (RoleModel role : roles) {
            UserRoleModel userRole = new UserRoleModel(newUser, role);
            userRoles.add(userRole);
        }
        newUser.setUserRoles(userRoles);

        DirectoryModel directory = directoryStore.findByLookup(OpenFlame.SIGN_UP_DIRECTORY);
        newUser.setRegistryNode(directory);

        userStore.save(newUser);

//        String squarePicUrl = values.get(FacebookConstants.FB_ATTR_PIC_SQUARE);
        String bigPicUrl = values.get(FacebookConstants.FB_ATTR_PIC_BIG);
        if (StringUtils.isNotBlank(bigPicUrl)) {
            try {
                URL url = new URL(bigPicUrl);
                InputStream in = new BufferedInputStream(url.openStream());
                String imageFilename = bigPicUrl.substring(bigPicUrl.lastIndexOf("/"));
                ServiceResponse<FileInfo> uploadResponse =
                        OPFEngine.ContentService.uploadResourceFile(ResourceType.IMAGE.name(), null, in, imageFilename);
                if (uploadResponse.isSuccess()) {
                    ImageFileInfo temporaryImage = (ImageFileInfo) uploadResponse.getItem();

                    String photoResourcePath = OpenFlame.SIGN_UP_DIRECTORY + ".opf-resource.photo.photo-" + newUser.getId();

                    ImageResourceVersion imageResourceVersion = new ImageResourceVersion();
                    imageResourceVersion.setTitle(newUser.getFirstName() + " " + newUser.getLastName() + " Photo");
                    imageResourceVersion.setResourceFileTemporaryName(temporaryImage.getFilename());
                    imageResourceVersion.setCulture(Cultures.AMERICAN);
                    imageResourceVersion.setResourceFileOriginalName(temporaryImage.getOrgFilename());
                    imageResourceVersion.setHeight(temporaryImage.getHeight());
                    imageResourceVersion.setWidth(temporaryImage.getWidth());

                    ServiceResponse<ImageResourceVersion> response =
                        OPFEngine.ContentService.createImageResourceVersionByLookup(photoResourcePath, imageResourceVersion);
                    if (!response.isSuccess()) {
                        logger.error("Can't create image resource version for save user avatar from facebook. " + response.getMessage());
                    }
                } else {
                    logger.error(uploadResponse.getMessage());
                }
            } catch (IOException e) {
                logger.error("Can't download image from facebook.");
                logger.error(e.getMessage(), e);
            }
        }

        return new Tuple<UserModel, List<RoleModel>>(newUser, roles);
    }

}