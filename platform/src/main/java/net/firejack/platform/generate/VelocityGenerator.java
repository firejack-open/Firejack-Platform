package net.firejack.platform.generate;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.ipad.iPad;
import net.firejack.platform.generate.tools.Render;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

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

@Component
public class VelocityGenerator {

    @Value("${log.directory}")
    private String logDirectory;

    @Autowired
    @Qualifier("velocityEngine")
    private VelocityEngine velocity;

    @Autowired
    private Render render;

    /**
     * @param name
     * @param generate
     * @param file
     * @param prepare
     */
    public void compose(String name, Base generate, File file, boolean prepare) {
        compose(name, generate, null, file, prepare);
    }

    /**
     * @param name
     * @param generate
     * @param model
     * @param file
     * @param prepare
     */
    public void compose(String name, Base generate, Map model, File file, boolean prepare) {
        try {
            Map describe = PropertyUtils.describe(generate);

            if (model != null) {
                describe.putAll(model);
            }

            compose(name, describe, file, prepare);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void compose(String name, iPad generate, File file) {
        try {
            Map describe = PropertyUtils.describe(generate);
            compose(name, describe, file, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name
     * @param model
     * @param file
     * @param prepare
     */
    public void compose(String name, Map model, File file, boolean prepare) {
        Writer writer = null;
        try {
            model.put("render", render);
            model.put("date", new Date());

            String content = VelocityEngineUtils.mergeTemplateIntoString(velocity, name, model);

            if (prepare) {
                Template template = generateTemplateFromString(content, "Template");
                writer = new FileWriter(file);
                template.merge(new VelocityContext(model), writer);
            } else {
                FileUtils.writeStringToFile(file, content);
            }
        } catch (VelocityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public String compose(String name, Object model) {
        try {
            Map describe = PropertyUtils.describe(model);
            return compose(name, describe);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String compose(String name, Map model) {
        try {
            return VelocityEngineUtils.mergeTemplateIntoString(velocity, name, model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param templateName
     * @param templateSource
     * @return
     * @throws org.apache.velocity.runtime.parser.ParseException
     *
     */
    public Template generateTemplateFromString(String templateName, String templateSource) throws ParseException {
        Properties props = new Properties();
        props.put("runtime.log", logDirectory + File.separator + "velocity.log");
        RuntimeSingleton.init(props);
        RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
        StringReader reader = new StringReader(templateSource);
        SimpleNode node = runtimeServices.parse(reader, templateName);
        Template template = new Template();
        template.setRuntimeServices(runtimeServices);
        template.setData(node);
        template.initDocument();
        return template;
    }

    /**
     * @param model
     * @param template
     * @return
     */
    public String mergeTemplate(Map model, Template template) {
        VelocityContext velocityContext = new VelocityContext(model);
        StringWriter writer = new StringWriter();
        template.merge(velocityContext, writer);
        return writer.toString();
    }

}
