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

package net.firejack.platform.installation.processor;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.ServerNodeConfig;
import net.firejack.platform.api.registry.model.ServerNodeType;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.installation.processor.event.InstallSlaveEvent;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import sun.security.x509.X500Name;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Map;

@Component
public class InstallSlaveProcessor implements ApplicationListener<InstallSlaveEvent> {
	private static final Logger logger = Logger.getLogger(InstallSlaveProcessor.class);

	@Autowired
	private OpenFlameDataSource source;

	@Override
	public void onApplicationEvent(InstallSlaveEvent event) {
		File keystore = InstallUtils.getKeyStore();

		String url = OpenFlameConfig.MASTER_URL.getValue();
		String admin = OpenFlameConfig.APP_ADMIN_NAME.getValue();
		String password = OpenFlameConfig.APP_ADMIN_PASSWORD.getValue();

		try {
			if (keystore.exists()) {
				X500Name info = KeyUtils.getInfo(keystore);
				url = info.getDomain();
			}

			String hostName = InetAddress.getLocalHost().getHostName();

			KeyPair keyPair = KeyUtils.generate(keystore);

			if (keyPair == null) {
				throw new IllegalStateException("Key not found");
			}

			X509Certificate certificate = KeyUtils.generateCertificate(url, 1, keyPair);

			if (StringUtils.isBlank(password)) {
				String cert = new String(Base64.encode(certificate.getEncoded()));
				OPFEngine.init(url, OpenFlame.PACKAGE, hostName, cert);
			} else {
				OPFEngine.init(url, admin, password);
			}

			ServerNodeConfig config = new ServerNodeConfig();

			config.setServerName(hostName);
//			config.setServerName(hostName + "_slave"); //TODO [CLUSTER] don't commit this line
			config.setHost(InetAddress.getLocalHost().getHostAddress());
			config.setPort(Integer.decode(OpenFlameConfig.PORT.getValue()));
			config.setNodeType(ServerNodeType.OPF_SLAVE);
			config.setLookup(OpenFlame.PACKAGE);
			config.setCert(certificate.getEncoded());

			InputStream stream = OPFEngine.RegistryService.registerSlaveNode(config);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(stream, output);
			byte[] decrypted = KeyUtils.decrypt(keyPair.getPrivate(), output.toByteArray());

			Map<String, String> map = EnvironmentsUtils.convertFromXml(new ByteArrayInputStream(decrypted));
			ConfigContainer.putAll(map);

			source.refreshDBProperties();
			OPFEngine.release();

			KeyUtils.add(keystore, keyPair, url);
			FileUtils.deleteQuietly(InstallUtils.getPropEnv());
		} catch (Exception e) {
			logger.error(e);
			throw new IllegalStateException(e);
		}

		OPFEngine.initialize();
	}
}
