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

package net.firejack.platform.generate.service;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.VelocityGenerator;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.EmptyProperties;
import net.firejack.platform.generate.beans.ipad.iPad;
import net.firejack.platform.generate.beans.web.model.Domain;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.ModelType;
import net.firejack.platform.generate.beans.web.model.key.FieldKey;
import net.firejack.platform.generate.tools.Render;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.util.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class BaseGeneratorService implements ICacheService {

    public static final String ABSTRACT = "net.firejack.platform.core.model.abstractmodel";

    @Autowired
    protected VelocityGenerator generator;
    @Autowired
    protected Render render;

    protected ThreadLocal<Map<String, Object>> cache;

    public void setCache(ThreadLocal<Map<String, Object>> cache) {
        this.cache = cache;
    }

    protected void generateBaseEntity(List<Model> models) {
        Model model = new Model();

        model.setType(ModelType.MappedSuperclass);
        model.setProjectPath("net.firejack.platform.core.model");
        model.setName("AbstractModel");
        model.setLookup(ABSTRACT);
        model.setKey(new FieldKey());
        model.setProperties(new EmptyProperties());

        Domain domain = new Domain();
        domain.setModel(model);
        domain.setProjectPath("net.firejack.platform.core.domain");
        domain.setName("AbstractDTO");

        model.setDomain(domain);
        models.add(model);

        addCacheItem(model.getLookup(), model);
    }

    protected void generate(Base base, String template, File dir) throws IOException {
        if (base != null && base.getClassPath() != null) {
            dir = new File(dir, base.getFilePosition());
            FileUtils.forceMkdir(dir);
            generator.compose(template, base, new File(dir, base.getFileName()), false);
        }
    }

    protected void generate(iPad base, String template, File dir, String extension) throws IOException {
        String filePosition = base.getFilePosition();
        if (StringUtils.isNotBlank(filePosition)) {
            filePosition = render.replace(filePosition);
            dir = new File(dir, filePosition);
        }
        FileUtils.forceMkdir(dir);
        generator.compose(template, base, new File(dir, base.getName() + extension));
    }

    /**
     * @param name
     * @return
     */
    public InputStream getResource(String name) {
        return ClassUtils.getResourceAsStream(this.getClass(), name);
    }

    protected <E> E getCacheItem(String lookup) {
        return (E) cache.get().get(lookup);
    }

    protected void addCacheItem(String lookup, Object data) {
        cache.get().put(lookup, data);
    }
}
