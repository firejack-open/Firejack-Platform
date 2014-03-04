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

package net.firejack.platform.model.config;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.ServerNodeConfig;
import net.firejack.platform.api.registry.model.ServerNodeType;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.siteminder.SiteMinderAuthenticationProcessor;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.*;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.*;

public class GatewayLoader extends TimerTask {

	public static final String CONFIG = "properties";
	public static final String LOOKUP = "lookup";
	public static final String TIMEOUT = "timeout";

	private final Log logger = LogFactory.getLog(getClass());
	private static GatewayLoader instance = new GatewayLoader();

	private final Object lock = new Object();
	private Timer timer = new Timer();
	private List<GatewayListener> listeners = new ArrayList<GatewayListener>();
	private String lookup;
	private Integer port;

	private long timeout = 10 * 1000l;

	/**
	 * @return
	 */
	public static GatewayLoader getInstance() {
		return instance;
	}

	private GatewayLoader() {
	}

	/**
	 * @param lookup
	 */
	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	/**
	 * @param port
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @param listener
	 */
	public void addListener(GatewayListener listener) {
		if (listeners == null) {
			listener.start();
			return;
		}

		synchronized (lock) {
			listeners.add(listener);
		}
	}

	@Override
	public void run() {
		Environments environments = getConfig(lookup, port);
		if (environments != null && !environments.isEmpty()) {
			save(environments);
			timer.cancel();

			synchronized (lock) {
				for (GatewayListener listener : listeners) {
					listener.start();
				}
			}
			listeners = null;
		}
	}

	public void save(Environments environments) {
		Map<String, String> map = ClassUtils.transformPlaceHolderEntity(environments.getEnvironments(), false);
		ConfigContainer.putAll(map);

		String value = map.get("base.url");
		if (value != null) {
			OpenFlameSecurityConstants.setBaseUrl(value);
		}
	}

	/**
	 * @param lookup
	 * @param port
	 * @return
	 */
	public Environments getConfig(String lookup, Integer port) {
        processSiteMinderConfigs();
		try {
			File keystore = InstallUtils.getKeyStore();

			String url = Env.FIREJACK_URL.getValue();

			logger.info("Load config from: "+ url);

			KeyPair keyPair = KeyUtils.generate(keystore);

			if (keyPair == null) {
				throw new IllegalStateException("Key not found");
			}

			String name = InetAddress.getLocalHost().getHostName();
			X509Certificate certificate = KeyUtils.generateCertificate(url, 1, keyPair);

			String cert = new String(Base64.encode(certificate.getEncoded()));
			OPFEngine.init(url, lookup, name, cert);

			ServerNodeConfig config = new ServerNodeConfig();
			config.setServerName(name);
			config.setHost(InetAddress.getLocalHost().getHostAddress());
			config.setPort(port);
			config.setNodeType(ServerNodeType.GATEWAY);
			config.setLookup(lookup);
			config.setCert(certificate.getEncoded());

			InputStream stream = OPFEngine.RegistryService.registerSlaveNode(config);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(stream, output);
			byte[] decrypted = KeyUtils.decrypt(keyPair.getPrivate(), output.toByteArray());

			return InstallUtils.deserialize(new ByteArrayInputStream(decrypted));
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

    private void processSiteMinderConfigs() {
        Tuple<File, Map<String, String>> fileMapTuple = null;
        try {
            fileMapTuple = SiteMinderAuthenticationProcessor.loadSMProperties();
            if (fileMapTuple != null) {
                OpenFlameSecurityConstants.setSiteMinderAuthSupported(true);
                Map<String, String> properties = fileMapTuple.getValue();
                String standardIdHeader = properties.get(SiteMinderAuthenticationProcessor.PROP_STANDARD_ID_HEADER);
                String opfDirectUrl = properties.get(SiteMinderAuthenticationProcessor.PROP_DIRECT_OPF_URL);
                OpenFlameSecurityConstants.setSiteMinderAuthIdHeader(standardIdHeader);
                OpenFlameSecurityConstants.setOpfDirectUrl(opfDirectUrl);
            }
        } catch (Throwable th) {
            if (fileMapTuple == null) {
                logger.error(th);
            } else {
                logger.error(th.getMessage(), th);
            }
        }
    }

	/**
	 * @param config
	 * @throws javax.servlet.ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		String gateway = config.getInitParameter(CONFIG);
		File resource = FileUtils.getResource(gateway);

		try {
			FileInputStream stream = FileUtils.openInputStream(resource);

			Properties properties = new Properties();
			properties.load(stream);
			IOUtils.closeQuietly(stream);

			this.port = TomcatUtils.getCatalinaPort(config);
			this.lookup = properties.getProperty(LOOKUP);
			OpenFlameSecurityConstants.setPackageLookup(this.lookup);

			String start = properties.getProperty(TIMEOUT);

			OpenFlameConfig.MC_SERVER_URL.setValue(properties.getProperty(OpenFlameConfig.MC_SERVER_URL.getKey()));
			OpenFlameConfig.MC_PORT.setValue(properties.getProperty(OpenFlameConfig.MC_PORT.getKey()));

			if (StringUtils.isNotBlank(start)) {
				this.timeout = Integer.parseInt(start);
			}
			if (StringUtils.isBlank(lookup)) {
				logger.error("Not found servlet parameter 'lookup'");
			}

			init();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/***/
	public void init() {
		this.timer.schedule(this, 0, timeout);
	}

	/***/
	public void destroy() {
		timer.cancel();
	}
}
