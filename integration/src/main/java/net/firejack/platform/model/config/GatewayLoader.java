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
