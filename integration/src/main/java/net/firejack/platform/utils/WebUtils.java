/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.MultiPart;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
public class WebUtils {

    private static final Logger logger = Logger.getLogger(WebUtils.class);

    /**
     * @param request
     * @return
     */
    public static String getApplicationFullPath(HttpServletRequest request) {
        String pageFullPath = request.getRequestURL().toString();
        String requestURI = request.getRequestURI();

        if (pageFullPath.endsWith(requestURI)) {
            String contextPath = request.getContextPath();
            return pageFullPath.substring(0, pageFullPath.length() - requestURI.length()) + contextPath;
        }

        return null;
    }

    /**
     * @param request
     * @return
     */
    public static String getDomainPath(HttpServletRequest request) {
        String pageFullPath = request.getRequestURL().toString();
        String requestURI = request.getRequestURI();
        if (pageFullPath.endsWith(requestURI)) {
            return pageFullPath.substring(0, pageFullPath.length() - requestURI.length());
        }
        return null;
    }

    /**
     * @param request
     * @return
     */
    public static String getPathWithParameters(HttpServletRequest request) {
        return request != null ?
                request.getServletPath() + (request.getQueryString() != null ?
                        "?" + request.getQueryString() : "") : null;
    }

    /**
     * @param request
     * @return
     * @throws java.io.IOException
     */
    public static String serializeObjectToJSON(Object request) throws IOException {
        StringWriter sWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        objectMapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        objectMapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        objectMapper.writeValue(sWriter, request);
        String s = sWriter.toString();
        logger.debug("Serialized JSON object: [" + s + "]");
        return s;
    }

    /**
     * @param objValue
     * @param clazz
     * @return
     * @throws java.io.IOException
     */
    public static <T> T deserializeObjectFromJSON(String objValue, Class<T> clazz) throws IOException {
        logger.debug("Deserialized object: [" + objValue + "].");
        ObjectMapper objectMapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        objectMapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        objectMapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        return objectMapper.readValue(objValue, clazz);
    }

	/**
	 * @param objValue
	 * @param clazz
	 * @param generic
	 *
	 * @return
	 *
	 * @throws java.io.IOException
	 */
	public static <T, G> T deserializeObjectFromJSON(String objValue, Class<T> clazz, Class<G> generic) throws IOException {
		logger.info("Deserialized object: [" + objValue + "].");
		ObjectMapper objectMapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		objectMapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
		objectMapper.getSerializationConfig().setAnnotationIntrospector(introspector);
		if (generic != null) {
			return objectMapper.readValue(objValue, TypeFactory.parametricType(clazz, generic));
		} else {
			return objectMapper.readValue(objValue, clazz);
		}
	}

	public static <T> T deserializeJSON(String json, Class... clazz) throws IOException {
		if (StringUtils.isBlank(json)) {
			return clazz.length == 0 || List.class.isAssignableFrom(clazz[0]) ? (T) Collections.emptyList() : null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		objectMapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
		objectMapper.getSerializationConfig().setAnnotationIntrospector(introspector);

		if (clazz.length == 1) {
			return (T) objectMapper.readValue(json, clazz[0]);
		} else {
			return (T) objectMapper.readValue(json, build(clazz, 0));
		}
	}

	private static JavaType build(Class[] clazz, int index) {
		if (clazz.length == index + 2) {
			return TypeFactory.parametricType(clazz[index], clazz[index + 1]);
		} else {
			return TypeFactory.parametricType(clazz[index], build(clazz, ++index));
		}
	}

	/**
     * @param request
     * @return
     */
    public static String getRequestUrl(HttpServletRequest request) {
        return request != null ? request.getRequestURL().toString() : null;
    }

    /**
     * @param httpRequest
     * @return
     */
    public static String getRequestPath(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().toLowerCase()
                .substring(httpRequest.getContextPath().length());
    }

    /**
     * @param request
     * @param cookieName
     * @return
     */
    public static Cookie getRequestCookie(HttpServletRequest request, String cookieName) {
        Cookie resultCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    resultCookie = cookie;
                    break;
                }
            }
        }
        return resultCookie;
    }

    /**
     * @param url
     * @param types
     * @return
     */
    public static WebResource.Builder prepareSecuredRequestBuilder(String url, MediaType... types) {
        OPFContext context = OPFContext.getContext();
        String sessionToken = context.getSessionToken();

        WebResource webResource = Client.create(getConfig()).resource(url);
//        webResource.accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML);
        WebResource.Builder builder = webResource.accept(types);
        if (StringUtils.isNotBlank(sessionToken)) {
            javax.ws.rs.core.Cookie c = new javax.ws.rs.core.Cookie(
                    OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, sessionToken);
            builder = builder.cookie(c);
        }

        return builder;
    }

    /**
     * @param url
     * @param token
     * @return
     */
    public static WebResource.Builder postRequest(String url, String token) {
        WebResource webResource = Client.create(getConfig()).resource(url);
        WebResource.Builder builder = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE);
        if (StringUtils.isNotBlank(token)) {
            javax.ws.rs.core.Cookie c = new javax.ws.rs.core.Cookie(
                    OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, token);
            builder = builder.cookie(c);
        }

        return builder;
    }

    /**
     * @param url
     * @param token
     * @return
     */
    public static WebResource.Builder prepareGetRequest(String url, String token) {
        WebResource webResource = Client.create(getConfig()).resource(url);
        WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE);
        if (StringUtils.isNotBlank(token)) {
            builder = builder.cookie(new javax.ws.rs.core.Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, token));
        }
        return builder;
    }

    /**
     * @param url
     * @param token
     * @return
     */
    public static WebResource.Builder preparePOSTRequest(String url, String token) {
        WebResource webResource = Client.create(getConfig()).resource(url);
        WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE);
        webResource.type(MediaType.APPLICATION_XML);
        if (StringUtils.isNotBlank(token)) {
            builder = builder.cookie(new javax.ws.rs.core.Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, token));
        }
        return builder;
    }

    private static ClientConfig getConfig() {

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);

        return config;
    }

    /**
     * @param login username
     * @param password password
     * @param clientIpAddress browser or another client ip address
     * @return
     */
    public static AuthenticationToken getAuthenticationToken(String login, String password, String clientIpAddress) {
        ServiceResponse<AuthenticationToken> response =
                OPFEngine.AuthorityService.processSTSSignIn(login, password, clientIpAddress);
        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("AuthorityService API returned failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

	/**
	 * @param domainUrl domain url
	 * @param port port number
	 * @param contextUrl context url
	 *
	 * @return normalized url
	 */
	public static String getNormalizedUrl(String domainUrl, Integer port, String contextUrl) {
		if (port != null) {
			return getNormalizedUrl(domainUrl, port.toString(), contextUrl);
		}
		return "";
	}

	public static String getNormalizedUrl(String domainUrl, String port, String contextUrl) {
		String url = "";
		if (StringUtils.isNotBlank(domainUrl) && StringUtils.isNotBlank(port)) {
            if (StringUtils.isNotBlank(contextUrl)) {
                contextUrl = contextUrl.startsWith("/") ? contextUrl : "/" + contextUrl;
            } else {
                contextUrl = "";
            }
            if ("80".equals(port)) {
				url = String.format("http://%s%s", domainUrl, contextUrl);
			} else {
				url = String.format("http://%s:%s%s", domainUrl, port, contextUrl);
			}
		}
		return url;
	}

    /**
     * @param args
     */
    public static void main(String[] args) {
        Env.FIREJACK_URL.setValue("http://localhost:8080/platform");
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            AuthenticationToken admin = getAuthenticationToken("admin", "123123", localHost.getHostAddress());
            System.out.println(admin.getToken());
        } catch (UnknownHostException e) {
            e.printStackTrace(System.err);
        }
    }

    public static String getQueryParam(HttpServletRequest httpRequest, String name) {
        String query = httpRequest.getQueryString();
        String value = null;
        if (query != null) {
            Map<String, String> queryParams = getQueryMap(query);
            value = queryParams.get(name);
        }
        return value;
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
	        String[] strings = param.split("=");
	        String name = strings[0];
            String value = strings.length ==2 ?strings[1]:null;
            map.put(name, value);
        }
        return map;
    }

	public static Map<String, InputStream> getStream(MultiPart multiPart) {
		Map<String, InputStream> map = new HashMap<String, InputStream>();

		List<BodyPart> bodyParts = multiPart.getBodyParts();
		if (bodyParts != null && !bodyParts.isEmpty()) {
			for (BodyPart part : bodyParts) {
				BodyPartEntity bpe = (BodyPartEntity) part.getEntity();

				String name = part.getContentDisposition().getFileName();
				InputStream stream = bpe.getInputStream();
				map.put(name, stream);
			}
		}
		return map;
	}
}
