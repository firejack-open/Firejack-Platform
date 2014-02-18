package net.firejack.platform.provider;

import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartConfig;
import com.sun.jersey.multipart.impl.MultiPartReaderClientSide;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import org.jvnet.mimepull.MIMEParsingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
@Provider
@Consumes("multipart/*")
public class OpenflameMultipartProvider extends MultiPartReaderClientSide {

    private static final String CONTENT_LENGTH = "content-length";
    private static final String BEAN_PROGRESS_ASPECT = "progressAspect";

    private ProgressListener listener;

    /**
     * @param providers
     * @param config
     */
    public OpenflameMultipartProvider(@Context Providers providers, @Context MultiPartConfig config) {
        super(providers, config);
    }

    protected MultiPart readMultiPart(Class<MultiPart> type, Type genericType,
                                      Annotation[] annotations, MediaType mediaType,
                                      MultivaluedMap<String, String> headers,
                                      InputStream stream) throws IOException, MIMEParsingException {
        String length = headers.get(CONTENT_LENGTH).get(0);
        if (listener == null) {
            definitionListener();
        }
        MultipartInputStream inputStream = new MultipartInputStream(stream, listener, Long.valueOf(length));
        return super.readMultiPart(type, genericType, annotations, mediaType, headers, inputStream);
    }

    private void definitionListener() {
        if (OpenFlameSpringContext.getContext().containsBean(BEAN_PROGRESS_ASPECT)) {
            this.listener = (ProgressListener) OpenFlameSpringContext.getBean(BEAN_PROGRESS_ASPECT);
        }
    }

}