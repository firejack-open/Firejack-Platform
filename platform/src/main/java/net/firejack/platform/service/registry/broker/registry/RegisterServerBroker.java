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

package net.firejack.platform.service.registry.broker.registry;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.registry.domain.ServerNodeConfig;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.security.x509.X509CertImpl;

import java.security.PublicKey;
import java.security.cert.CertificateException;


@Component
public class RegisterServerBroker extends ServiceBroker<ServiceRequest<ServerNodeConfig>, ServiceResponse> {

    @Autowired
    private IServerStore serverStore;
    @Autowired
    private ISystemStore systemStore;
    @Autowired
    private IPackageStore packageStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<ServerNodeConfig> request)
            throws BusinessFunctionException {
        ServerNodeConfig data = request.getData();

        ServiceResponse response;
        try {
	        PackageModel model = packageStore.findByLookup(data.getLookup());
	        if (model.getSystem() == null) {
		        throw new IllegalStateException("Couldn't find an associated system by the specified package: " + data.getLookup());
	        }

	        SystemModel system = systemStore.findById(model.getSystem().getId());
	        if (system != null) {
	            ServerModel server = new ServerModel();
	            server.setName(data.getServerName());
	            server.setPath(system.getLookup());
	            server.setParent(system);
	            server.setServerName(data.getHost());
	            server.setPort(data.getPort());
	            server.setActive(false);
	            X509CertImpl cert = new X509CertImpl(data.getCert());
	            PublicKey publicKey = cert.getPublicKey();
	            byte[] encodedPubKey = Base64.encode(publicKey.getEncoded());
	            server.setPublicKey(new String(encodedPubKey));

	            try {
	                serverStore.save(server);
	                response = new ServiceResponse("Server Node registered.", true);
	            } catch (Exception e) {
	                logger.error(e.getMessage(), e);
	                response = new ServiceResponse(e.getMessage(), false);
	            }
	        } else {
	            response = new ServiceResponse("Failed to find parent system.", false);
	        }
        } catch (CertificateException e) {
            logger.error(e.getMessage(), e);
            response = new ServiceResponse(e.getMessage(), false);
        }
        return response;
    }

}