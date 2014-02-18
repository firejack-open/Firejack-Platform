package net.firejack.platform.model.wsdl;
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


import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.model.wsdl.bean.Parameter;
import net.firejack.platform.model.wsdl.bean.Service;
import net.firejack.platform.model.wsdl.bean.Wsdl;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.jws.WebParam;
import javax.xml.ws.Holder;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Invoker implements FilenameFilter {
    private static final Logger logger = Logger.getLogger(Invoker.class);
    private final Map<Class, Member> cache = new HashMap<Class, Member>();
    private final MultiValueMap<String, Member> search = new LinkedMultiValueMap<String, Member>();

    @Autowired
    private Factory factory;

    @PostConstruct
    public void init() throws Exception {
        File dir = FileUtils.getResource("wsdl");
        if (dir == null || !dir.exists())
            return;

        File[] files = dir.listFiles(this);
        for (File file : files) {
            Wsdl wsdl = FileUtils.readJAXB(Wsdl.class, file);
            loadMethods(wsdl);
        }
    }

    public <T> T invoke(String name, Object request, Class<T> response) {
        return invoke(null, name, request, response);
    }

    public <T> T invoke(Class endpoint, String name, Object request, Class<T> response) {
        Member member = cache.get(request.getClass());
        if (member == null)
            member = registryMember(endpoint, name, request.getClass());
        return member.invoke(request, response);
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".sch");
    }

    private Object getPortType(Wsdl wsdl) throws Exception {
        Method[] declaredMethods = wsdl.getEndpoint().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getReturnType() == wsdl.getService() && declaredMethod.getParameterTypes().length == 0) {
                return declaredMethod.invoke(wsdl.getEndpoint().newInstance());
            }
        }
        return null;
    }

    private void loadMethods(Wsdl wsdl) throws Exception {
        Class service = wsdl.getService();
        FastClass fastClass = FastClass.create(service);
        Method[] methods = service.getDeclaredMethods();
        List<Service> services = wsdl.getServices();

        Object portType = getPortType(wsdl);
        MultiValueMap<String, Method> search = new LinkedMultiValueMap<String, Method>();
        for (Method method : methods)
            search.add(method.getName(), method);

        for (Service item : services) {
            item.setWsdl(wsdl);
            List<Method> methodList = search.get(item.getName());
            if (methodList != null && methodList.size() == 1) {
                this.search.add(item.getName(), new Member(portType, item, fastClass.getMethod(methodList.get(0))));
            } else if (methodList != null && methodList.size() > 1) {
                List<Parameter> parameters = item.getParameters();
                for (Method method : methodList) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length == parameters.size()) {
                        this.search.add(item.getName(), new Member(portType, item, fastClass.getMethod(method)));
                    }
                }
            }
        }
    }

    private Member registryMember(Class endpoint, String name, Class requestClass) {
        List<Member> memberList = search.get(name);
        if (memberList == null)
            throw new IllegalStateException("Can't find any method by name: " + name);

        Member member = null;
        if (memberList.size() == 1) {
            member = memberList.get(0);
            cache.put(requestClass, member);
            search.remove(name);
        } else {
            for (Member next : memberList) {
                if (next.isMember(requestClass, endpoint)) {
                    if (member != null)
                        throw new IllegalStateException("Found many identical method for : " + requestClass.getName() + " , use invoke(Class endpoint, String name, Object request, Class<T> response)");
                    member = next;
                }
            }

            if (member != null) {
                cache.put(requestClass, member);
                memberList.remove(member);
            }

            if (memberList.size() == 0)
                search.remove(name);
        }

        return member;
    }

    private class Member {
        private Object instance;
        private Service service;
        private FastMethod method;
        private List<FastMethod> read;
        private Map<Parameter, FastMethod> write;

        private Member(Object instance, Service service, FastMethod method) {
            this.instance = instance;
            this.service = service;
            this.method = method;
        }

        public boolean isMember(Class requestClass, Class endpoint) {
            List<Field> fields = getField(requestClass);
            List<Parameter> parameters = service.getParameters();

            if (fields != null && parameters != null && fields.size() != parameters.size() || fields == null ^ parameters == null)
                return false;

            if (endpoint != null && service.getWsdl().getEndpoint() != endpoint)
                return false;

            if (fields != null) {
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    Parameter parameter = parameters.get(i);
                    if (!field.getName().equalsIgnoreCase(parameter.getName())) {
                        return false;
                    }
                }
            }
            return true;
        }

        private List<Field> getField(Class _class) {
            Field[] fields = _class.getDeclaredFields();
            List<Field> list = null;
            for (Field field : fields) {
                if (!field.getName().equals("serialVersionUID") && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    if (list == null)
                        list = new ArrayList<Field>();
                    list.add(field);
                }
            }

            return list;
        }

        private Object[] fastInvokeRequestArgs(Object request) throws InvocationTargetException {
            Class<?> clazz = request.getClass();
            List<Parameter> parameters = service.getParameters();

            if (parameters == null || parameters.isEmpty())
                return null;

            if (read == null) {
                read = new ArrayList<FastMethod>(parameters.size());

                FastClass fastClass = FastClass.create(clazz);
                for (Parameter parameter : parameters) {
                    PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, parameter.getName());
                    FastMethod fastMethod = fastClass.getMethod(descriptor.getReadMethod());
                    read.add(fastMethod);
                }
            }

            Object[] args = new Object[parameters.size()];
            for (int i = 0; i < parameters.size(); i++) {
                Parameter parameter = parameters.get(i);
                FastMethod fastMethod = read.get(i);

                Object arg = fastMethod.invoke(request, null);
                if (parameter.getType().isEnum()) {
                    arg = Enum.valueOf(parameter.getType(), ((Enum) arg).name());
                }
                if (parameter.isBean()) {
                    arg = parameter.isList() ? factory.convertFrom(parameter.getType(), (List) arg) : factory.convertFrom(parameter.getType(), arg);
                }

                if (parameter.isHolder()) {
                    arg = new Holder(arg);
                }
                args[i] = arg;
            }

            return args;
        }

        private <T> T fastInvokeResponseArgs(Class<T> response, Object _return, Object[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            List<Parameter> parameters = new ArrayList<Parameter>();
            if (args != null) {
                parameters.addAll(service.getParameters());
                args = ArrayUtils.add(args, _return);
            } else {
                args = new Object[]{_return};
            }

            parameters.add(service.getReturn());

            T resp = response.newInstance();

            if (write == null) {
                write = new HashMap<Parameter, FastMethod>();

                FastClass fastClass = FastClass.create(response);
                for (Parameter parameter : parameters) {
                    PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(response, parameter.getName());
                    if (descriptor != null) {
                        FastMethod fastMethod = fastClass.getMethod(descriptor.getWriteMethod());
                        write.put(parameter, fastMethod);
                    }
                }
            }

            for (int i = 0; i < parameters.size(); i++) {
                Parameter parameter = parameters.get(i);
                if (parameter.getMode() != WebParam.Mode.IN) {
                    FastMethod fastMethod = write.get(parameter);
                    Class type = fastMethod.getParameterTypes()[0];
                    Object value = args[i];

                    if (parameter.isHolder()) {
                        value = ((Holder) value).value;
                    }
                    if (parameter.isBean()) {
                        value = parameter.isList() ? factory.convertTo(type, (List) value) : factory.convertTo(type, value);
                    }

                    if (type.isEnum()) {
                        value = Enum.valueOf(type, ((Enum) value).name());
                    }

                    fastMethod.invoke(resp, new Object[]{value});
                }
            }

            return resp;
        }

        public <T> T invoke(Object request, Class<T> response) {
            try {
                Object[] args = fastInvokeRequestArgs(request);
                Object invoke = method.invoke(instance, args);
                return fastInvokeResponseArgs(response, invoke, args);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
}
