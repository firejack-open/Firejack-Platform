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