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