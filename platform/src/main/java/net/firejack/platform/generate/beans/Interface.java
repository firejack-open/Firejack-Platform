package net.firejack.platform.generate.beans;

import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

public abstract class Interface extends Base {
    private List<Method> methods;

    protected Interface() {
    }

    protected Interface(String name) {
        this.name = name;
    }

    protected Interface(Base base, List<Method> methods) {
        super(base);
        this.serviceName = base.getServiceName();
        this.methods = methods;

        if (methods != null) {
            for (Method method : methods) {
                TreeSet<Param> params = method.getParams();
                Param returnType = method.getReturnType();
                if (returnType != null) {
                    addImport(returnType.getDomain());
                }
                if (params != null) {
                    for (Param param : params) {
                        addImport(param.getDomain());
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public List<Method> getMethods() {
        return methods;
    }

    public void addMethods(List<Method> methods) {
        if (this.methods == null)
            this.methods = new ArrayList<Method>();
        this.methods.addAll(methods);
    }

    public void addMethod(Method method) {
        if (this.methods == null)
            this.methods = new ArrayList<Method>();
        this.methods.add(method);
    }
}
