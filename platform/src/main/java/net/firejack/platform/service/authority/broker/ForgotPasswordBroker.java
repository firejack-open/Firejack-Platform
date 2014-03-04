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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@TrackDetails
@Component("forgotPasswordBroker")
public class ForgotPasswordBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse> {

    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_RESET_PASSWORD_PAGE_URL = "resetPasswordPageUrl";
    public static SimpleDateFormat DATE_TOKEN_FORMAT = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IUserMailService userMailService;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues<String>> request) throws Exception {
        ServiceResponse response;

        String email = request.getData().get(PARAM_EMAIL);
        String resetPasswordPageUrl = request.getData().get(PARAM_RESET_PASSWORD_PAGE_URL);

        UserModel userModel = userStore.findUserByEmail(email);
        if (userModel != null) {
            String token = SecurityHelper.generateRandomSequence(32);
            String cryptKey = token.substring(0, 8);
            String tokenDate = DATE_TOKEN_FORMAT.format(new Date());
            String resetPasswordToken = token + SecurityUtils.encryptData(tokenDate, cryptKey);

            userModel.setResetPasswordToken(resetPasswordToken);
            userStore.saveOrUpdate(userModel);

            userMailService.sendResetPasswordMail(userModel, resetPasswordPageUrl);

            response = new ServiceResponse("Reset Password Email has been sent. Please check your email.", true);
        } else {
            response = new ServiceResponse("Can't find User by email: " + email, false);
        }
        return response;
    }

}