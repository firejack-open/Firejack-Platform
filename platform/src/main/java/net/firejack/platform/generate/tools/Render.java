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

package net.firejack.platform.generate.tools;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.ipad.ClientMethod;
import net.firejack.platform.generate.beans.ipad.FileReference;
import net.firejack.platform.generate.beans.ipad.Property;
import net.firejack.platform.generate.beans.ipad.PropertyType;
import net.firejack.platform.generate.beans.web.api.LocalMethod;
import net.firejack.platform.generate.beans.web.api.ServiceParam;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.ModelType;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.column.MappedType;
import net.firejack.platform.generate.beans.web.model.key.FieldKey;
import net.firejack.platform.generate.beans.web.model.key.Key;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.stereotype.Component;

import java.util.*;

import static net.firejack.platform.api.registry.model.FieldType.*;
import static net.firejack.platform.core.model.registry.ParameterTransmissionType.PATH;
import static net.firejack.platform.core.model.registry.ParameterTransmissionType.QUERY;
import static net.firejack.platform.generate.beans.ipad.iPad.*;


@Component
public class Render {
    private Random random = new Random();
    private ThreadLocal<Map<String, String>> settings = new ThreadLocal<Map<String, String>>();
    private ThreadLocal<StrSubstitutor> parser = new ThreadLocal<StrSubstitutor>();

    /**
     * @param field
     * @return
     */
    public String renderColumn(Field field, Key key) {
        StringBuilder builder = new StringBuilder();

        if (field.getMapped() == null) {
            if (field.getIndex() == IndexType.PRIMARY) {
                builder.append("@Id");
                if (key instanceof FieldKey) {
                    builder.append("\n\t@GeneratedValue");
                }
                return builder.toString();
            }

            builder.append("@Column(");

            if (field.getColumn() != null) {
                builder.append("name = \"").append(field.getColumn()).append("\"");
            }

            if (field.getType().getLength() != null) {
                builder.append(",");
                builder.append(" length = ").append(field.getType().getLength());
            }

            if (!field.isNullable()) {
                builder.append(",");
                builder.append(" nullable = false");
            }

            if (!field.isInsertable()) {
                builder.append(",");
                builder.append(" insertable = false");
            }

            if (!field.isUpdatable()) {
                builder.append(",");
                builder.append(" updatable = false");
            }

            if (field.isDefinition()) {
                builder.append(",");
                builder.append(" columnDefinition = \"").append(field.getType().getDbtype()).append("\"");
            }

            builder.append(")");
        } else if (field.getMapped() == MappedType.ManyToOne) {
            if (field.getIndex() == IndexType.PRIMARY)
                builder.append("@Id\n\t");

            builder.append("@").append(field.getMapped().name());
            if (field.isLazy()) {
                builder.append("(fetch = FetchType.LAZY)");
            }
            builder.append("\n\t@JoinColumn(");

            if (field.getColumn() != null) {
                builder.append("name = \"").append(field.getColumn()).append("\"");
            }

            if (!field.isNullable()) {
                builder.append(", nullable = false");
            }

            if (!field.isInsertable()) {
                builder.append(", insertable = false");
            }

            if (!field.isUpdatable()) {
                builder.append(", updatable = false");
            }

            if (field.isDefinition()) {
                builder.append(", columnDefinition = \"").append(field.getType().getDbtype()).append("\"");
            }

            builder.append(")");

            if (field.isDeleteCascade()) {
                builder.append("\n\t@OnDelete(action = OnDeleteAction.CASCADE)");
            }

        } else if (field.getMapped() == MappedType.ManyToMany) {
            builder.append("@").append(field.getMapped().name());
            if (!field.isLazy()) {
                builder.append("(fetch = FetchType.EAGER)");
            }
            builder.append("\n\t@Fetch(value = FetchMode.SELECT)\n\t");
            builder.append("@JoinTable(");

            Model source = field.getSource();
            if (source != null) {
                builder.append("name = \"").append(field.getTable()).append("\"\n\t\t");
            }

            if (field.getJoin() != null) {
                builder.append(", joinColumns = @JoinColumn( name = \"").append(field.getJoin()).append("\")\n\t\t");
            }

            if (field.getInverseJoin() != null) {
                builder.append(", inverseJoinColumns = @JoinColumn( name = \"").append(field.getInverseJoin()).append("\")\n\t");
            }
            builder.append(")");

        }
        return builder.toString();
    }

    public String renderTable(ModelType type, String name, String table, String unique, boolean isAbstract) {
        StringBuilder builder = new StringBuilder();

        if (type.equals(ModelType.MappedSuperclass)) {
            builder.append("@MappedSuperclass\n");
            builder.append("@AccessType(\"property\")\n");
        } else {
            builder.append("@Entity\n");
        }

        if (type.equals(ModelType.DiscriminatorColumn)) {
            builder.append("@Table(name = \"").append(table).append("\"");
            if (!unique.isEmpty()) builder.append(", uniqueConstraints = {").append(unique).append("}");
            builder.append(")\n");
            builder.append("@Inheritance(strategy = InheritanceType.SINGLE_TABLE)\n");
            builder.append("@DiscriminatorColumn(name = \"type\", discriminatorType = DiscriminatorType.STRING)\n");
            if (!isAbstract) {
                builder.append("@DiscriminatorValue(\"").append(Utils.createDiscriminatorValue(name)).append("\")\n");
            }
        } else if (type.equals(ModelType.DiscriminatorValue)) {
            builder.append("@DiscriminatorValue(\"").append(Utils.createDiscriminatorValue(name)).append("\")\n");
        } else if (type.equals(ModelType.Table)) {
            builder.append("@Table(name = \"").append(table).append("\"");
            if (!unique.isEmpty()) builder.append(", uniqueConstraints = {").append(unique).append("}");
            builder.append(")\n");
        }

        return builder.toString().trim();
    }

    public String renderValidateAnnotation(Field field) {
        StringBuilder builder = new StringBuilder();
        if (!field.isNullable() && field.getType().isString()) {
            builder.append("@NotBlank");
        } else if (!field.isNullable()) {
            builder.append(field.isAutoGenerated() ? "@NotNull(autoGeneratedField = true)" : "@NotNull");
        }
        if (field.getType().getLength() != null) {
            builder.append("\n\t@Length(maxLength = ").append(field.getType().getLength()).append(")");
        }
        if (StringUtils.isNotBlank(field.getValue()) && !field.getValue().matches(".*\\(\\)$")) {
            builder.append("\n\t@DefaultValue(\"").append(field.getValue()).append("\")");
        }
        if (field.getAllowValues() != null) {
            builder.append("\n\t@AllowValue({");
            for (String value : field.getAllowValues()) {
                builder.append("\"").append(value).append("\",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("})");
        }
        return builder.toString();
    }

    /**
     * @param method
     * @return
     */
    public String renderTransaction(Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append("@Transactional");
        if (method.getType().isReadOnly()) {
            builder.append("(readOnly = true)");
        }

        return builder.toString();
    }

    /**
     * @param params
     * @return
     */
    public String renderParams(TreeSet<Param> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (Param param : params) {
            builder.append(renderType(param));
            builder.append(" ").append(param.getName());
            if (i < params.size() - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.toString();
    }

    /**
     * @param param
     * @return
     */
    public String renderType(Param param) {
        StringBuilder builder = new StringBuilder();

        if (param.isType(OBJECT)) {
            builder.append(param.getDomain().getName());
        } else if (param.isType(FieldType.LIST) && param.getDomain() == null) {
            builder.append("List");
        } else if (param.isType(FieldType.LIST)) {
            builder.append("List<");
            builder.append(param.getDomain().getName());
            builder.append(">");
        } else {
            builder.append(param.getType().getClassName());
        }

        return builder.toString();
    }

    public void prepareDefaultType(MethodType type, Collection<Param> params) {
        if (type == MethodType.search) {
            Param terms = new Param("terms", FieldType.SHORT_TEXT);
            if (!params.contains(terms)) params.add(terms);
        }
        if (type == MethodType.readAll || type == MethodType.search) {
            Param offset = new Param("offset", FieldType.INTEGER_NUMBER);
            Param limit = new Param("limit", FieldType.INTEGER_NUMBER);
            Param sortColumn = new Param("sortColumn", FieldType.SHORT_TEXT);
            Param sortDirection = new Param("sortDirection", FieldType.SHORT_TEXT);
            if (!params.contains(offset)) params.add(offset);
            if (!params.contains(limit)) params.add(limit);
            if (!params.contains(sortColumn)) params.add(sortColumn);
            if (!params.contains(sortDirection)) params.add(sortDirection);
        }
    }

    /**
     * @param method
     * @return
     */
    public String renderMethod(Method method) {
        StringBuilder builder = new StringBuilder();
        MethodType type = method.getType();

        StringBuilder searchableFields = new StringBuilder();
        if (method.getReturnType() != null) {
            Model model = (Model) method.getReturnType().getDomain();
            List<Field> fields = new ArrayList<Field>();

            searchFields(model, fields);

            if (type == MethodType.search || type == MethodType.searchCount || type == MethodType.searchWithFilter || type == MethodType.searchCountWithFilter) {
                for (Field field : fields)
                    if (field.isSearchable())
                        searchableFields.append("\"").append(field.getName()).append("\",");

                if (searchableFields.length() != 0)
                    searchableFields.deleteCharAt(searchableFields.length() - 1);
            }

            builder.append("return ");
        }

        if (type == MethodType.create) {
            builder.append("super.saveOrUpdate(").append(method.getParams().first().getName()).append(")");
        } else if (type == MethodType.update) {
            builder.append("super.saveOrUpdate(").append(method.getParams().first().getName()).append(")");
        } else if (type == MethodType.delete) {
            builder.append("super.deleteById(").append(method.getParams().first().getName()).append(")");
        } else if (type == MethodType.read) {
            builder.append("super.findById(").append(method.getParams().first().getName()).append(")");
        } else if (type == MethodType.readAll) {
            builder.append("super.search(null, paging)");
        } else if (type == MethodType.readAllWithFilter) {
            builder.append("super.findWithFilter(null, null, idsFilter, paging)");
        } else if (type == MethodType.search) {
            builder.append("super.simpleSearch(term, Arrays.<String>asList(").append(searchableFields).append("), paging, null)");
        } else if (type == MethodType.searchCount) {
            builder.append("super.simpleSearchCount(term, Arrays.<String>asList(").append(searchableFields).append("), null)");
        } else if (type == MethodType.searchWithFilter) {
            builder.append("super.simpleSearch(term, Arrays.<String>asList(").append(searchableFields).append("), paging, idsFilter)");
        } else if (type == MethodType.searchCountWithFilter) {
            builder.append("super.simpleSearchCount(term, Arrays.<String>asList(").append(searchableFields).append("), idsFilter)");
        } else if (type == MethodType.advancedSearch) {
            builder.append("super.advancedSearch(queryParameters, paging)");
        } else if (type == MethodType.advancedSearchCount) {
            builder.append("super.advancedSearchCount(queryParameters)");
        } else if (type == MethodType.advancedSearchWithIdsFilter) {
            builder.append("super.advancedSearchWithIdsFilter(queryParameters, paging, idsFilter)");
        } else if (type == MethodType.advancedSearchCountWithIdsFilter) {
            builder.append("super.advancedSearchCountWithIdsFilter(queryParameters, idsFilter)");
        }
        return builder.toString().trim();
    }

    private void searchFields(Model model, List<Field> fields) {
        if (model != null) {
            if (model.getFields() != null) {
                fields.addAll(model.getFields());
            }
            searchFields(model.getParent(), fields);
        }
    }

    /**
     * @param key
     * @param map
     * @return
     */
    public String renderProperties(String key, Map map) {
        if (map != null && map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "${" + key + "}";
    }

    /**
     * @param method
     * @param view
     * @return
     */
    public String renderBrokerInput(HTTPMethod method, Model view, Collection<ServiceParam> params) {
        if (method == HTTPMethod.GET || method == HTTPMethod.DELETE || params.size() > 1) {
            return "NamedValues";
        } else {
            return view.getName();
        }
    }

    /**
     * @param params
     * @return
     */
    public String renderEndpointParams(TreeSet<ServiceParam> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (ServiceParam param : params) {
            String name = param.getName();
            ParameterTransmissionType location = param.getLocation();
            if (location == null) {
                builder.append("ServiceRequest<");
            } else if (location.equals(PATH)) {
                builder.append("@PathParam(\"").append(name).append("\") ");
            } else if (location.equals(ParameterTransmissionType.QUERY)) {
                builder.append("@QueryParam(\"").append(name).append("\") ");
            }

            builder.append(renderType(param));
            if (location == null) builder.append(">");
            builder.append(" ").append(name);
            if (i < params.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        return builder.toString();
    }

    /**
     * @param params
     * @return
     */
    public String renderWebServiceParams(TreeSet<ServiceParam> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (ServiceParam param : params) {
            String name = param.getName();
            ParameterTransmissionType location = param.getLocation();
            if (location == null) {
                builder.append("@WebParam(name = \"request\") ServiceRequest<");
            } else {
                builder.append("@WebParam(name = \"").append(name).append("\") ");
            }

            builder.append(renderType(param));
            if (location == null) builder.append(">");
            builder.append(" ").append(name);
            if (i < params.size() - 1) {
                builder.append(",");
            }
            i++;
        }
        return builder.toString();
    }

    public String renderEndpointArguments(TreeSet<ServiceParam> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (ServiceParam param : params) {
            builder.append(" ").append(param.getName());
            if (param.getLocation() == null) {
                builder.append(".getData()");
            }
            if (i < params.size() - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.toString();
    }

    /**
     * @param params
     * @return
     */
    public String renderServiceParams(TreeSet<ServiceParam> params) {
        if (params == null || params.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (ServiceParam param : params) {
            builder.append(renderType(param));
            builder.append(" ").append(param.getName());
            if (i < params.size() - 1) {
                builder.append(", ");
            }
            i++;
        }
        return builder.toString();
    }

    /**
     * @param method
     * @return
     */
    public String renderEndpointPath(LocalMethod method) {
        StringBuilder path = new StringBuilder();

        path.append(method.getPath());
        TreeSet<Param> params = method.getParams();
        if (params != null) {
            for (Param param : params) {
                ParameterTransmissionType location = ((ServiceParam) param).getLocation();
                if (location != null && location == PATH) {
                    if (param.isType(OBJECT))
                        path.append("/{").append(param.getName()).append(":.*}");
                    else
                        path.append("/{").append(param.getName()).append("}");
                }
            }
        }

        return path.toString();
    }

    public String renderProxyBody(LocalMethod method) {
        StringBuilder body = new StringBuilder();
        StringBuilder queries = new StringBuilder();

        switch (method.getBroker().getHttpMethod()) {
            case GET:
                body.append("get(\"");
                break;
            case POST:
                body.append("post2(\"");
                break;
            case PUT:
                body.append("put2(\"");
                break;
            case DELETE:
                body.append("delete(\"");
                break;
        }

        body.append(method.getPath());
        TreeSet<Param> params = method.getParams();
        boolean data = false;
        boolean end = true;
        if (params != null) {
            for (Param param : params) {
                ParameterTransmissionType location = ((ServiceParam) param).getLocation();
                if (location == null) {
                    data = true;
                } else if (location == PATH) {
                    if (!end) body.append(" + \"");
                    body.append("/\" + ").append(param.getName());
                    end = false;
                } else if (location == ParameterTransmissionType.QUERY) {
                    queries.append(",\"").append(param.getName()).append("\",").append(param.getName());
                }
            }
        }
        if (end) body.append("\"");
        if (data) body.append(", data");
        body.append(queries);
        body.append(");");

        return body.toString();
    }

    /**
     * @param path
     * @return
     */
    public String convertPath(String path, String defaultPath) {
        return !path.isEmpty() ? path.replaceAll("\\.", "/") : defaultPath.toLowerCase().replaceAll("\\.", "/");
    }

    /**
     * @param name
     * @return
     */
    public String lower(String name) {
        return name.toLowerCase();
    }

    /**
     * @param params
     * @return
     */
    public String getParamNameId(Collection<Param> params) {
        for (Param param : params) {
            if (param.isKey()) {
                return param.getName();
            }
        }
        return "null";
    }

    public Long generateSerialUID() {
        return random.nextLong();
    }

    public String signatureMethod(ClientMethod method) {
        StringBuilder signature = new StringBuilder();
        signature.append("- (");

        Property returnType = method.getReturnType();
        if (returnType == null) {
            signature.append("void) ");
        } else {
            FieldType type = returnType.getFieldType();
            if (type == OBJECT) {
                signature.append(returnType.getEntity().getName()).append(" *) ");
            } else {
                signature.append(type.getObjectiveC()).append(") ");
            }
        }

        signature.append(method.getName());

        List<Property> params = method.getParams();
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Property property = params.get(i);
                if (i != 0) {
                    signature.append(" ").append(property.getName());
                }

                signature.append(":(");

                FieldType type = property.getFieldType();
                if (property.isAddress()) {
                    signature.append(type.getObjectiveC()).append("*) ");
                } else if (type == OBJECT) {
                    signature.append(returnType.getEntity().getName()).append(" *) ");
                } else {
                    signature.append(type.getObjectiveC()).append(") ");
                }

                signature.append(property.getName());
            }
        }
        return signature.toString();
    }

    public String requestData(ClientMethod method) {
        List<Property> params = method.getParams();
        if (params != null) {
            for (Property param : params) {
                if (param.getFieldType() == OBJECT) {
                    return param.getName();
                }
            }
        }
        return "";
    }

    public String requestParameter(ClientMethod method) {
        StringBuilder request = new StringBuilder();
        request.append("[Util append:_gatewayUrl, @\"").append(method.getPath()).append("\"");
        List<Property> params = method.getParams();
        if (params != null) {
            for (Property param : params) {
                if (param.getLocation() == PATH) {
                    if (param.getFieldType().isString()) {
                        request.append(", @\"/\"").append(", [Util escape:").append(param.getName()).append("]");
                    } else {
                        request.append(", @\"/\"").append(", ").append(param.getName());
                    }
                }
            }


            int i = 0;
            for (Property property : params) {
                if (property.getLocation() == QUERY) {
                    if (i == 0)
                        request.append(", @\"?");
                    else
                        request.append(", @\"&");

                    request.append(property.getName()).append("=\", ");

                    if (property.getFieldType().isString())
                        request.append("[Util escape:").append(property.getName()).append("]");
                    else
                        request.append(property.getName());

                    i++;
                }
            }
        }
        request.append(", nil];");
        return request.toString();
    }

    public void putSetting(String key, String value) {
        Map<String, String> map = settings.get();
        if (map == null) {
            map = new HashMap<String, String>();
            settings.set(map);
        }
        map.put(key, value);
    }

    public String replace(String value) {
        Map<String, String> map = settings.get();
        StrSubstitutor substitutor = parser.get();
        if (substitutor == null && map != null) {
            substitutor = new StrSubstitutor(map);
            parser.set(substitutor);
            return substitutor.replace(value);
        } else if (map != null && substitutor != null) {
            return substitutor.replace(value);
        }
        return value;
    }

    public String encoding(FileReference reference) {
        if (reference.getName().matches(".*\\.(h|m)")) {
            return "fileEncoding = 4;";
        }
        return "";
    }

    public String type(FileReference reference) {
        if (reference.getName().endsWith(".h")) {
            return "lastKnownFileType = sourcecode.c.h;";
        } else if (reference.getName().endsWith(".m")) {
            return "lastKnownFileType = sourcecode.c.objc;";
        } else if (reference.getName().endsWith(".xib")) {
            return "lastKnownFileType = file.xib;";
        } else if (reference.getName().endsWith(".png")) {
            return "lastKnownFileType = image.png;";
        }
        return "";
    }

    public String metadataType(Property property) {
        FieldType type = property.getFieldType();

        if (property.getType() == PropertyType.ENUM) {
            return GWPROPERTY_TYPE_ENUM;
        } else if (property.getType() == PropertyType.TREE) {
            return GWPROPERTY_TYPE_TREE;
        } else if (property.getType() == PropertyType.PARENT) {
            return GWPROPERTY_TYPE_PARENT;
        } else if (type == IMAGE_FILE) {
            return GWPROPERTY_TYPE_IMAGE;
        } else if (type == DESCRIPTION || type == UNLIMITED_TEXT || type == RICH_TEXT) {
            return GWPROPERTY_TYPE_TEXT;
        } else if (type == PASSWORD) {
            return GWPROPERTY_TYPE_PASSWORD;
        } else if (type == PHONE_NUMBER) {
            return GWPROPERTY_TYPE_PHONE_NUMBER;
        } else if (type == SSN) {
            return GWPROPERTY_TYPE_SSN;
        } else if (type.isString()) {
            return GWPROPERTY_TYPE_STRING;
        } else if (type.isNumber()) {
            return GWPROPERTY_TYPE_NUMBER;
        } else if (type == DATE) {
            return GWPROPERTY_TYPE_DATE;
        } else if (type == TIME) {
            return GWPROPERTY_TYPE_TIME;
        } else if (type.isTimeRelated()) {
            return GWPROPERTY_TYPE_FULL_TIME;
        } else if (type.isBoolean()) {
            return GWPROPERTY_TYPE_FLAG;
        } else if (type.isObject()) {
            return GWPROPERTY_TYPE_OBJECT;
        } else if (type.isList()) {
            return GWPROPERTY_TYPE_LIST;
        }

        return GWPROPERTY_TYPE_UNKNOWN;
    }

    public String metadataAttribute(Property property) {
        return property.isAutoGenerated() ? GWPROPERTY_ATTRIBUTE_AUTOGENERATED : GWPROPERTY_ATTRIBUTE_DEFAULT;
    }

    public String actionType(ClientMethod method) {
        MethodType type = method.getType();
        switch (type) {
            case create:
                return GWACTION_TYPE_CREATE;
            case update:
                return GWACTION_TYPE_UPDATE;
            case delete:
                return GWACTION_TYPE_DELETE;
            case read:
                return GWACTION_TYPE_READ;
            case readAll:
                return GWACTION_TYPE_READ_ALL;
            case search:
                return GWACTION_TYPE_SEARCH;
            case advancedSearch:
                return GWACTION_TYPE_ADVANCED_SEARCH;
        }
        return "";
    }

    public String methodSelector(ClientMethod method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getName());

        List<Property> params = method.getParams();
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Property property = params.get(i);
                if (i != 0) {
                    signature.append(property.getName());
                }

                signature.append(":");
            }
        }
        return signature.toString();
    }

    public String reverse(String lookup) {
        String[] names = lookup.split(DiffUtils.PATH_SEPARATOR_REGEXP);
        ArrayUtils.reverse(names);
        return StringUtils.join(names, DiffUtils.PATH_SEPARATOR);
    }

    public boolean isRetain(String type) {
        return type.endsWith("*");
    }

    public void clean() {
        settings.remove();
        parser.remove();
    }
}
