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