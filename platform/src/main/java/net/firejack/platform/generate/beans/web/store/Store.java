package net.firejack.platform.generate.beans.web.store;

import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.Interface;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;

import java.util.ArrayList;
import java.util.List;

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

@Properties(subpackage = "store", prefix = "Basic", suffix = "Store")
public class Store extends Base {
    private Model model;

    private Interface Interface;
    private List<Method> methods;

    /***/
    public Store() {
    }

    /**
     * @param model
     */
    public Store(Model model) {
        super(model);
        this.model = model;
        addImport(model.getKey());
        addImport(model);
    }

    public Model getModel() {
        return model;
    }

    /**
     * @return
     */
    public Interface getInterface() {
        return Interface;
    }

    /***/
    public void createInterface() {
        Interface = new StoreInterface(this, methods);
    }

    /**
     * @return
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * @param method
     */
    public void addMethods(Method method) {
        if (this.methods == null) {
            this.methods = new ArrayList<Method>();
        }

        this.methods.add(method);
    }

    /**
     * @param type
     * @return
     */
    public Method find(MethodType type) {
        if (methods != null) {
            for (Method method : methods) {
                if (method.getType().equals(type)) {
                    return method;
                }
            }
        }
        return null;
    }
}
