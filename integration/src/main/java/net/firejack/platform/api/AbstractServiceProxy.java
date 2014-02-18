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

package net.firejack.platform.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.multipart.*;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.provider.XMLProvider;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.handler.Builder;
import net.firejack.platform.web.handler.ErrorHandler;
import net.firejack.platform.web.security.SecurityUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.SystemPrincipal;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import sun.security.x509.X500Name;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractServiceProxy {

    public static final String ORIGINAL_FILENAME = "originalFilename";

	protected static final String REST_API_URL_SUFFIX = "/rest";
	protected static final int TIMEOUT = 60000;

    protected String baseUrl;
	protected ClientConfig config;
	protected static final Logger logger = Logger.getLogger(AbstractServiceProxy.class);
    private String systemUserSessionToken;

	protected AbstractServiceProxy() {
		config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        config.getClasses().add(XMLProvider.class);
		File keyStore = InstallUtils.getKeyStore();

		try {
			X500Name info = KeyUtils.getInfo(keyStore);
			if (info != null && StringUtils.isNotBlank(info.getDomain())) {
				baseUrl = info.getDomain();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected AbstractServiceProxy(String baseUrl) {
		this();
		this.baseUrl = baseUrl;
	}

	protected AbstractServiceProxy(Class[] classes) {
		this();
		if (classes != null) {
			config.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, classes) {});
		}
	}

	protected AbstractServiceProxy(String baseUrl, Class[] classes) {
		this();
		this.baseUrl = baseUrl;
		if (classes != null) {
			config.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, classes) {});
		}
	}

	public abstract String getServiceUrlSuffix();

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> get(String path, Object... queries) {
        Builder builder = prepare(path, queries);
        return doGet(builder, ServiceResponse.class);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> getAsSystem(String sessionToken, String path, Object... queries) {
        Builder builder = prepare1(sessionToken, path, queries);
        return doGet(builder, ServiceResponse.class);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> getWithHeaders(
        String path,Map<String, String> headers, Object... queries) {
        Builder builder = prepare(path,queries);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }
        return doGet(builder, ServiceResponse.class);
    }

	protected InputStream getStream(String path, InputStream stream, Object... queries) {
		FormDataMultiPart part = new FormDataMultiPart().field("file", stream, MediaType.TEXT_PLAIN_TYPE);
        Builder builder = prepare(path, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE, queries);
        return doPost(builder, InputStream.class, part);
	}

    protected InputStream getStream(String path, Object... queries) {
        Builder builder = prepare(path, MediaType.APPLICATION_OCTET_STREAM_TYPE, queries);
        return doGet(builder, InputStream.class);
	}

    protected InputStream getStream(String path, List<String > formdata, Object... queries) {
		Form form = new Form();
		form.add("columns", formdata);
        Builder builder = prepare(path, MediaType.APPLICATION_OCTET_STREAM_TYPE, queries);
        return doPost(builder, InputStream.class, form);
	}

    protected <RQDTO extends AbstractDTO> InputStream getStream(String path, RQDTO dto, Object... queries) {
        Builder builder = prepare(path, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.APPLICATION_XML_TYPE, queries);
        return doPost(builder, InputStream.class, new ServiceRequest<RQDTO>(dto));
	}

    protected ServiceResponse<FileInfo> getFile(String path, Object... queries) {
        Builder builder = prepare(path, MediaType.APPLICATION_OCTET_STREAM_TYPE, queries);
        ClientResponse response = doGet(builder, ClientResponse.class);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setStream(response.getEntityInputStream());
        fileInfo.setFilename(response.getHeaders().getFirst("OPF-Filename"));
        fileInfo.setOrgFilename(response.getHeaders().getFirst("OPF-OrgFilename"));
        String updated = response.getHeaders().getFirst("OPF-Updated");
        if (StringUtils.isNumeric(updated)) {
            fileInfo.setUpdated(Long.parseLong(updated));
        }

        ServiceResponse<FileInfo> serviceResponse = new ServiceResponse<FileInfo>();
        serviceResponse.addItem(fileInfo);
        serviceResponse.setSuccess(true);
        return serviceResponse;
    }

	protected <DTO extends AbstractDTO> ServiceResponse<DTO> post(String path, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPost(builder, ServiceResponse.class);
	}

	protected <DTO extends AbstractDTO> ServiceResponse<DTO> postWithHeaders(
        String path, Map<String, String> headers, Object... queries) {
        Builder builder = prepare(path,queries);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }
        return doPost(builder, ServiceResponse.class);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> post(String path, DTO dto, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPost(builder, ServiceResponse.class, new ServiceRequest<DTO>(dto));
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> post(String path, List<DTO> dtoList, Object... queries) {
        ServiceRequest<DTO> request = new ServiceRequest<DTO>();
        request.setDataList(dtoList);
        Builder builder = prepare(path, queries);
        return doPost(builder, ServiceResponse.class, request);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> post3(String path, AbstractDTO dto, Object... queries) {
        ServiceRequest request = new ServiceRequest();
        request.setData(dto);
        Builder builder = prepare(path, queries);
        return doPost(builder, ServiceResponse.class, request);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> put(String path, List<DTO> dtoList, Object... queries) {
        ServiceRequest<DTO> request = new ServiceRequest<DTO>();
        request.setDataList(dtoList);
        Builder builder = prepare(path, queries);
        return doPut(builder, ServiceResponse.class, request);
    }

    protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> post2(String path, RQDTO dto, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPost(builder, ServiceResponse.class, new ServiceRequest<RQDTO>(dto));
    }

    protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> post2AsSystem(
            String systemSessionToken, String path, RQDTO dto, Object... queries) {
        Builder builder = prepare1(systemSessionToken, path, queries);
        return doPost(builder, ServiceResponse.class, new ServiceRequest<RQDTO>(dto));
    }

    protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> post2AsSystem(
            String systemSessionToken, String path, List<RQDTO> dtoList, Object... queries) {
        Builder builder = prepare1(systemSessionToken, path, queries);
        ServiceRequest<RQDTO> request = new ServiceRequest<RQDTO>();
        request.setDataList(dtoList);
        return doPost(builder, ServiceResponse.class, request);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> put(String path, DTO dto, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPut(builder, ServiceResponse.class, new ServiceRequest<DTO>(dto));
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> put(String path, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPut(builder, ServiceResponse.class);
    }

	protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> put2(String path, RQDTO dto, Object... queries) {
        Builder builder = prepare(path, queries);
        return doPut(builder, ServiceResponse.class, new ServiceRequest<RQDTO>(dto));
	}

	protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> put2AsSystem(
            String systemSessionToken, String path, RQDTO dto, Object... queries) {
        Builder builder = prepare1(systemSessionToken, path, queries);
        return doPut(builder, ServiceResponse.class, new ServiceRequest<RQDTO>(dto));
	}

    protected <RQDTO extends AbstractDTO, RSDTO extends AbstractDTO> ServiceResponse<RSDTO> put2AsSystem(
            String systemSessionToken, String path, List<RQDTO> dtoList, Object... queries) {
        Builder builder = prepare1(systemSessionToken, path, queries);
        ServiceRequest<RQDTO> request = new ServiceRequest<RQDTO>();
        request.setDataList(dtoList);
        return doPut(builder, ServiceResponse.class, request);
    }

	protected <DTO extends AbstractDTO> ServiceResponse<DTO> delete(String path, Object... queries) {
        Builder builder = prepare(path, queries);
        return doDelete(builder, ServiceResponse.class);
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> delete(String path, DTO dto, Object... queries) {
        Builder builder = prepare(path, queries);
        return doDelete(builder, ServiceResponse.class, new ServiceRequest<DTO>(dto));
    }

    protected <DTO extends AbstractDTO> ServiceResponse<DTO> delete(String path, List<DTO> dtoList, Object... queries) {
        ServiceRequest<DTO> request = new ServiceRequest<DTO>();
        request.setDataList(dtoList);
        Builder builder = prepare(path, queries);
        return doDelete(builder, ServiceResponse.class, request);
    }

	protected ServiceResponse upload1(String path, Map<String, InputStream> streams, Object... queries) {
		MultiPart multiPart = buildMultiPart(streams);
		Builder builder = prepare(path, MediaType.APPLICATION_JSON_TYPE, MultiPartMediaTypes.MULTIPART_MIXED_TYPE, queries);
		return doPost(builder, ServiceResponse.class, multiPart);
	}

    protected ServiceResponse upload2(String path, Map<String, InputStream> streams, Object... queries) {
		MultiPart multiPart = buildMultiPart(streams);

		Builder builder = prepare(path, MediaType.APPLICATION_JSON_TYPE, MultiPartMediaTypes.MULTIPART_MIXED_TYPE, queries);
		return doPut(builder, ServiceResponse.class, multiPart);
	}

	private MultiPart buildMultiPart(Map<String, InputStream> streams) {
		MultiPart multiPart = new MultiPart();
		for (Map.Entry<String, InputStream> entry : streams.entrySet()) {
			try {
				BodyPart part = new BodyPart(entry.getValue(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
				part.setContentDisposition(new ContentDisposition("multipart-data;filename=" + entry.getKey()));
				multiPart.bodyPart(part);
			} catch (ParseException e) {
				logger.error(e);
			}
		}
		return multiPart;
	}

	protected ServiceResponse upload1(String path, InputStream inputStream, Class<? extends AbstractDTO> clazz, Object... queries) {
		try {

			FormDataMultiPart part = new FormDataMultiPart().field("file", inputStream, MediaType.TEXT_PLAIN_TYPE);

			Builder builder = prepare(path, MediaType.APPLICATION_JSON_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE, queries);
			addCookie(builder);
			String post = doPost(builder, String.class, part);

			return WebUtils.deserializeObjectFromJSON(post, ServiceResponse.class, clazz);
		} catch (IOException e) {
			throw new BusinessFunctionException(e);
		}
	}

    protected ServiceResponse upload0(String path, InputStream inputStream, Class<? extends AbstractDTO> clazz, Object... queries) {
		try {

			FormDataMultiPart part = new FormDataMultiPart().field("file", inputStream, MediaType.TEXT_PLAIN_TYPE);

			Builder builder = prepare(path, MediaType.TEXT_HTML_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE, queries);
			addCookie(builder);
			String post = doPost(builder, String.class, part);

			return WebUtils.deserializeObjectFromJSON(post, ServiceResponse.class, clazz);
		} catch (IOException e) {
			throw new BusinessFunctionException(e);
		}
	}

	protected String upload(String path, InputStream inputStream, Map<String, Object> parameters, Object... queries) {
        FormDataMultiPart part = new FormDataMultiPart();

        FormDataContentDisposition.FormDataContentDispositionBuilder file = FormDataContentDisposition.name("file");
        if (parameters != null && parameters.get(ORIGINAL_FILENAME) != null) {
            file.fileName((String) parameters.get(ORIGINAL_FILENAME));
        }
        FormDataContentDisposition build = file.creationDate(new Date()).build();

        FormDataBodyPart formDataBodyPart = new FormDataBodyPart(build, inputStream, MediaType.TEXT_PLAIN_TYPE);
        part.bodyPart(formDataBodyPart);

        if (parameters != null) {
            for (Map.Entry<String, Object> entry: parameters.entrySet()) {
                formDataBodyPart = new FormDataBodyPart(entry.getKey(), entry.getValue(), MediaType.TEXT_PLAIN_TYPE);
                part.bodyPart(formDataBodyPart);
            }
        }

		Builder builder = prepare(path, MediaType.TEXT_HTML_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE, queries);
		addCookie(builder);
		return doPost(builder, String.class, part);
    }

	protected Builder prepare1(String sessionToken, String path, Object... queries) {
		return prepare1(sessionToken, path, MediaType.APPLICATION_XML_TYPE, queries);
	}

	protected Builder prepare(String path, Object... queries) {
		return prepare(path, MediaType.APPLICATION_XML_TYPE, queries);
	}

	protected Builder prepare(String path, MediaType mediaType, Object... queries) {
		return prepare(path, mediaType, mediaType, queries);
	}

	protected Builder prepare1(String sessionToken, String path, MediaType mediaType, Object... queries) {
		return prepare1(sessionToken, path, mediaType, mediaType, queries);
	}

	protected Builder prepare1(String sessionToken, String path,
                               MediaType acceptMediaType, MediaType typeMediaType, Object... queries) {
		path = query(path,queries);
        String url = getUrlForSystemUser() + REST_API_URL_SUFFIX + getServiceUrlSuffix() + path;
        WebResource webResource = Client.create(config).resource(url);
        webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
        webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
        WebResource.Builder builder = webResource.accept(acceptMediaType).type(typeMediaType);

        Builder proxy = ErrorHandler.getProxy(builder);
        Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, sessionToken);
        proxy.cookie(cookie);
		addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
		//addClientIpInfo(proxy);
		return proxy;
    }

	protected Builder prepare(String path, MediaType acceptMediaType, MediaType typeMediaType,Object... queries) {
		path = query(path,queries);
        String url = getUrl() + REST_API_URL_SUFFIX + getServiceUrlSuffix() + path;

        WebResource webResource = Client.create(config).resource(url);
		webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
		webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
        WebResource.Builder builder = webResource.accept(acceptMediaType).type(typeMediaType);

		Builder proxy = ErrorHandler.getProxy(builder);
		addCookie(proxy);
		addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
		addClientIpInfo(proxy);
		return proxy;
    }

    protected Builder prepareBaseBuilder(
            String baseUrl, String path, MediaType acceptMediaType, MediaType typeMediaType,Object... queries) {
        path = query(path,queries);
        String url = baseUrl + REST_API_URL_SUFFIX + getServiceUrlSuffix() + path;

        WebResource webResource = Client.create(config).resource(url);
        webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
        webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
        WebResource.Builder builder = webResource.accept(acceptMediaType).type(typeMediaType);

        Builder proxy = ErrorHandler.getProxy(builder);
        addHeader(OpenFlameSecurityConstants.MARKER_HEADER, this.getClass().getName(), proxy);
        addClientIpInfo(proxy);
        return proxy;
    }

	protected void addClientIpInfo(Builder builder) {
		String clientIp = getClientIp();
		if (clientIp != null) {
			addHeader(OpenFlameSecurityConstants.CLIENT_INFO_HEADER, clientIp, builder);
		}
	}

	public void addCookie(Builder builder) {
		if (OPFContext.isInitialized()) {
		    try {
		        String sessionToken = getSessionToken();
		        if (sessionToken != null) {
		            Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, sessionToken);
		            builder.cookie(cookie);
		        }
		    } catch (ContextLookupException e) {
		        logger.error(e.getMessage());
		    }
		}
	}

	protected void addHeader(String key, String value, Builder builder) {
        if (StringUtils.isNotBlank(key)) {
            builder.header(key, value);
        }
    }

	protected String query(String path, Object... queries) {
		char prefix = path.contains("?") ? '&' : '?';
		for (int i = 0; i < queries.length; i += 2) {
			String name = null;
			try {
				name = URLEncoder.encode((String) queries[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.warn(e);
			}
			Object value = queries[i + 1];
			if (value != null) {
				if (value instanceof Iterable) {
					for (Object o : (Iterable) value) {
						try {
							path += prefix + name + "=" + URLEncoder.encode(o.toString(),"UTF-8");
							prefix = '&';
						} catch (UnsupportedEncodingException e) {
							logger.warn(e);
						}
					}
				} else if (value instanceof Object[]){
					for (Object o : (Object[]) value) {
						try {
							path += prefix + name + "=" + URLEncoder.encode(o.toString(),"UTF-8");
							prefix = '&';
						} catch (UnsupportedEncodingException e) {
							logger.warn(e);
						}
					}
				} else {
					try {
						path += prefix + name + "=" + URLEncoder.encode(value.toString(),"UTF-8");
						prefix = '&';
					} catch (Exception e) {
						logger.warn(e);
					}
				}
			}
		}
		return path;
	}

	protected String getSessionToken() {
		return OPFContext.getContext().getSessionToken();
	}

	protected String getClientIp() {
		try {
            if (OPFContext.isInitialized()) {
                OPFContext context = OPFContext.getContext();
                if (context != null && context.getSessionToken() != null && context.getBrowserIpAddress() != null) {
                    String sessionToken = context.getSessionToken();
                    String key = sessionToken.substring(4, 12);
                    return SecurityUtils.encryptData(context.getBrowserIpAddress(), key);
                }
            }
        } catch (ContextLookupException e) {
			return null;
		}
		return null;
	}

	public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

	public void setClasses(Class[] classes) {
		if (classes != null) {
			config.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, classes) {});
		}
	}

	protected String getUrlForSystemUser() {
        String url;
        if (OpenFlameSecurityConstants.isSiteMinderAuthSupported()) {
            OPFContext context = OpenFlameSecurityConstants.getSystemUserContext();
            if (context == null || StringUtils.isBlank(OpenFlameSecurityConstants.getOpfDirectUrl())) {
                url = baseUrl == null ? Env.FIREJACK_URL.getValue() : baseUrl;
            } else {
                url = OpenFlameSecurityConstants.getOpfDirectUrl();
            }
            verboseSMInfo(context, url);
        } else {
            url = baseUrl == null ? Env.FIREJACK_URL.getValue() : baseUrl;
        }
		return url;
	}

	protected String getUrl() {
        String url;
        if (OpenFlameSecurityConstants.isSiteMinderAuthSupported()) {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || !(context.getPrincipal() instanceof SystemPrincipal) ||
                    StringUtils.isBlank(OpenFlameSecurityConstants.getOpfDirectUrl())) {
                url = baseUrl == null ? Env.FIREJACK_URL.getValue() : baseUrl;
            } else {
                url = OpenFlameSecurityConstants.getOpfDirectUrl();
            }
            verboseSMInfo(context, url);
        } else {
            url = baseUrl == null ? Env.FIREJACK_URL.getValue() : baseUrl;
        }
		return url;
	}

    protected <T> T doGet(final Builder builder, final Class<T> clazz) {
        return new ServiceProxyExecutor<T>() {
            @Override
            public T doRequest(Builder builder) {
                return builder.get(clazz);
            }
        }.request(builder);
    }

    protected <T> T doPost(Builder builder, Class<T> clazz) {
        return doPost(builder, clazz, null);
    }

    protected <T> T doPost(final Builder builder, final Class<T> clazz, final Object part) {
        return new ServiceProxyExecutor<T>() {
            @Override
            public T doRequest(Builder builder) {
                return builder.post(clazz, part);
            }
        }.request(builder);
    }

    protected <T> T doPut(Builder builder, Class<T> clazz) {
        return doPut(builder, clazz, null);
    }

    protected <T> T doPut(final Builder builder, final Class<T> clazz, final Object part) {
        return new ServiceProxyExecutor<T>() {
            @Override
            public T doRequest(Builder builder) {
                return builder.put(clazz, part);
            }
        }.request(builder);
    }

    protected <T> T doDelete(Builder builder, Class<T> clazz) {
        return doDelete(builder, clazz, null);
    }

    protected <T> T doDelete(final Builder builder, final Class<T> clazz, final Object part) {
        return new ServiceProxyExecutor<T>() {
            @Override
            public T doRequest(Builder builder) {
                return builder.delete(clazz, part);
            }
        }.request(builder);
    }

    public String getSystemUserSessionToken() {
        if (StringUtils.isBlank(this.systemUserSessionToken)) {
            synchronized(OpenFlameSecurityConstants.class) {
                OPFContext systemLevelContext = OpenFlameSecurityConstants.getSystemUserContext();
                if (systemLevelContext == null) {
                    File keystore = InstallUtils.getKeyStore();

                    if (!keystore.exists()) return getSessionToken();

                    try {
                        String hostName = InetAddress.getLocalHost().getHostName();
                        //String hostName = InetAddress.getLocalHost().getHostName() + "_slave";  //TODO [CLUSTER] don't commit "_slave"

                        KeyPair keyPair = KeyUtils.load(keystore);

                        if (keyPair == null) {
                            throw new IllegalStateException("Key not found");
                        }

                        X509Certificate certificate = KeyUtils.generateCertificate("", 1, keyPair);
                        String cert = new String(Base64.encode(certificate.getEncoded()));

                        ServiceResponse<AuthenticationToken> response =
                                OPFEngine.AuthorityService.processSTSCertSignIn(
                                        OpenFlameSecurityConstants.getPackageLookup(), hostName, cert);
                        if (response.isSuccess()) {
                            AuthenticationToken authenticationToken = response.getItem();
                            this.systemUserSessionToken = authenticationToken.getToken();
                        } else {
                            logger.error("Failed to authenticate system user using node certificate. Reason: " + response.getMessage());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }  else {
                    this.systemUserSessionToken = systemLevelContext.getSessionToken();
                }
            }
        }
        return this.systemUserSessionToken;
    }

    private void verboseSMInfo(OPFContext context, String url) {
        if (OpenFlameSecurityConstants.isSiteMinderDebugMode()) {
            StringBuilder sb = new StringBuilder("SM: ");
            sb.append("Service-Proxy = ").append(this.getClass().getSimpleName());
            if (context == null) {
                sb.append("; context is null");
            } else {
                sb.append("; context is not null; user-type = ");
                sb.append(context.getPrincipal().getClass().getSimpleName());
            }
            sb.append("; baseUrl-to-use = ").append(url);
            logger.info(sb.toString());
        }
    }

}
