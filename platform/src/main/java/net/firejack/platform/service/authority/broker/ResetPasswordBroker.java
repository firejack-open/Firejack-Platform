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

package net.firejack.platform.service.authority.broker;


import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.model.service.mail.IUserMailService;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@TrackDetails
@Component("resetPasswordBroker")
public class ResetPasswordBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse> {

    public static final String PARAM_TOKEN = "token";

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IUserMailService userMailService;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues<String>> request) throws Exception {
        ServiceResponse response;
        try {
            String token = request.getData().get(PARAM_TOKEN);
            String cryptKey = token.substring(0, 8);
            String encryptedToken = token.substring(32);
            String tokenDate = SecurityUtils.decryptData(encryptedToken, cryptKey);

            Date date = ForgotPasswordBroker.DATE_TOKEN_FORMAT.parse(tokenDate);
            date = DateUtils.addDays(date, 1);

            if (date.after(new Date())) {
                UserModel userModel = userStore.findUserByToken(token);
                if (userModel != null) {
                    String newPassword = SecurityHelper.generateRandomSequence(8);
                    String hashPassword = SecurityHelper.hash(newPassword);

                    userModel.setPassword(hashPassword);
                    userModel.setResetPasswordToken(null);
                    userStore.saveOrUpdate(userModel);

                    userMailService.sendGeneratedPasswordMail(userModel, newPassword);

                    response = new ServiceResponse("Email with new generated password has been sent.", true);
                } else {
                    response = new ServiceResponse("Can't find User by token: " + token, false);
                }
            } else {
                response = new ServiceResponse("Token has expired.", false);
            }
        } catch (Exception e) {
            response = new ServiceResponse("Token is not valid.", false);
        }
        return response;
    }

}