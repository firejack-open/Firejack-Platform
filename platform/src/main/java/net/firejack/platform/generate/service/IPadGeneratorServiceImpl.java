package net.firejack.platform.generate.service;
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


import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.utils.ArchiveUtils;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.ipad.*;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.api.LocalMethod;
import net.firejack.platform.generate.beans.web.api.LocalService;
import net.firejack.platform.generate.beans.web.api.ServiceParam;
import net.firejack.platform.generate.beans.web.model.Domain;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.column.MappedType;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static net.firejack.platform.generate.beans.web.store.MethodType.*;

@Component
public class IPadGeneratorServiceImpl extends BaseGeneratorService implements IPadGeneratorService {

    public void prepareStructure(Structure structure) throws IOException {
        final File project = structure.getIpad();

        ArchiveUtils.unzip(getResource("templates/code/ipad/resources.zip"), new ArchiveUtils.ArchiveCallback() {
            @Override
            public void callback(String dir, String name, InputStream stream) {
                dir = render.replace(dir);
                name = render.replace(name);

                try {
                    FileOutputStream output = FileUtils.openOutputStream(FileUtils.create(project, dir, name));
                    IOUtils.copy(stream, output);
                    IOUtils.closeQuietly(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void generateModel(Api api, Structure structure) throws IOException {
        Map<Model, Entity> temp = new HashMap<Model, Entity>();
        List<FileReference> entities = new ArrayList<FileReference>();
        List<FileReference> controllers = new ArrayList<FileReference>();

        Collection<Model> models = api.getModels();
        for (Model model : models) {
            if (model.isSubclasses()) {
                FileUtils.forceDelete(structure.getIpad());
                return;
            }
        }

        for (Model model : models) {
            if (model.getClassPath() != null) {
                Entity entity = new Entity();
                entity.setPrefix(model.getPrefix());
                entity.setName(model.getSimpleName());
                entity.setLookup(model.getLookup());
                entity.setReport(model instanceof Report);
                temp.put(model, entity);
            }
        }

        for (Map.Entry<Model, Entity> entry : temp.entrySet()) {
            Model model = entry.getKey();
            Entity header = entry.getValue();

            Entity extend = temp.get(model.getParent());
            if (extend != null) {
                header.setExtend(extend);
                header.addImport(extend);
            } else {
                header.addImport(iPad.BASE_ENTITY_CLASS);
            }

            List<Field> fields = model.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    Property property = new Property();

                    String name = field.getName();
                    if (name.equalsIgnoreCase("id")) {
                        property.setName("pk");
                        property.setExclude(true);
                    } else {
                        property.setName(name);
                    }

                    property.setColumn(StringUtils.defaultIfEmpty(field.getDisplayName(), StringUtils.capitalize(field.getName())));
                    property.setAutoGenerated(field.isAutoGenerated());
                    if (field.getMapped() == MappedType.ManyToOne) {
                        RelationshipType type = field.getRelationshipType();
                        if (type == RelationshipType.PARENT_CHILD) {
                            property.setType(PropertyType.CHILD);
                        } else if (type == RelationshipType.TREE) {
                            property.setType(PropertyType.TREE);
                        }
                        property.setFieldType(FieldType.OBJECT);
                    } else {
                        property.setFieldType(field.getType());
                    }

                    Model target = field.getTarget();
                    if (target != null) {
                        Entity entity = temp.get(target);
                        property.setEntity(entity);
                        property.setColumn(Utils.displayNameFormattingWithPluralEnding(target.getSimpleName()));
                        header.addClass(entity);

                        if (property.getType() == PropertyType.CHILD) {
                            Property parent = new Property();
                            parent.setName(property.getName());
                            parent.setType(PropertyType.PARENT);
                            parent.setEntity(header);
                            parent.setExclude(true);

                            entity.addProperty(parent);
                        }
                    }
                    header.addProperty(property);
                }
            }

            header.setHeading(model.getReferenceHeading());
            header.setSubHeading(model.getReferenceSubHeading());
            header.setDescription(model.getReferenceDescription());

            if (header.isEmptyReferenceObject()) {
                header.setHeading("{" + header.getDisplayName() + "}");
            }

            entities.add(new FileReference(header.getName() + ".h"));
            entities.add(new FileReference(header.getName() + ".m"));

            generate(header, "templates/code/ipad/entity.h.vsl", structure.getIpad(), ".h");
            generate(header, "templates/code/ipad/entity.m.vsl", structure.getIpad(), ".m");
        }

        generateController(temp.values(), controllers, structure);
        generateApi(api, temp, structure);
        generateMetadata(temp, structure);
        generateProjectFiles(entities, controllers, structure);
        generateRootSettingFiles(api, structure);
    }

    private void generateController(Collection<Entity> values, List<FileReference> controllers, Structure structure) throws IOException {
        for (Entity entity : values) {
            EditController editController = new EditController();
            editController.setSuffix("EditViewController");
            editController.setName(entity.getName());

            GridController gridController = new GridController();
            gridController.setSuffix("ViewController");
            gridController.setName(entity.getName());
            gridController.setEdit(editController);

            entity.setEdit(editController);
            entity.setGrid(gridController);

            controllers.add(new FileReference(gridController.getName() + ".h"));
            controllers.add(new FileReference(gridController.getName() + ".m"));

            if (entity.isReport()) {
                generate(gridController, "templates/code/ipad/ReportController.h.vsl", structure.getIpad(), ".h");
                generate(gridController, "templates/code/ipad/ReportController.m.vsl", structure.getIpad(), ".m");
            } else {
                controllers.add(new FileReference(editController.getName() + ".h"));
                controllers.add(new FileReference(editController.getName() + ".m"));

                generate(gridController, "templates/code/ipad/GridController.h.vsl", structure.getIpad(), ".h");
                generate(gridController, "templates/code/ipad/GridController.m.vsl", structure.getIpad(), ".m");
                generate(editController, "templates/code/ipad/EditController.h.vsl", structure.getIpad(), ".h");
                generate(editController, "templates/code/ipad/EditController.m.vsl", structure.getIpad(), ".m");
            }
        }
    }

    private void generateApi(Api api, Map<Model, Entity> entities, Structure structure) throws IOException {
        ClientApi clientApi = new ClientApi();
        clientApi.setName("API");

        List<ClientMethod> methods = new ArrayList<ClientMethod>();
        clientApi.setMethods(methods);

        List<LocalService> locals = api.getLocals();
        if (locals == null) return;

        for (LocalService local : locals) {
            String path = render.convertPath(local.getClassPath(), local.getServiceName());
            for (Method method : local.getMethods()) {
                LocalMethod localMethod = (LocalMethod) method;
                MethodType type = localMethod.getType();
                Entity parent = entities.get(localMethod.getBroker().getResponse());
                if (parent == null) continue;

                ClientMethod clientMethod = new ClientMethod();
                if (StringUtils.isBlank(parent.getPrefix())) {
                    clientMethod.setName(localMethod.getName());
                } else {
                    clientMethod.setName(localMethod.getName() + StringUtils.capitalize(parent.getPrefix()));
                }
                clientMethod.setPath("/rest/" + path + localMethod.getPath());
                clientMethod.setType(type);
                clientMethod.setMethod(localMethod.getBroker().getHttpMethod());
                parent.addMethods(clientMethod);

                TreeSet<Param> params = localMethod.getParams();
                if (params != null) {
                    List<Property> properties = new ArrayList<Property>();
                    for (Param param : params) {
                        Property property = new Property();
                        String name = param.getName();
                        if (name.equalsIgnoreCase("id")) name = "pk";
                        property.setName(name);
                        property.setFieldType(param.getType());
                        property.setLocation(((ServiceParam) param).getLocation());

                        Base domain = param.getDomain();
                        if (domain != null && domain instanceof Domain) {
                            Entity entity = entities.get(((Domain) domain).getModel());
                            property.setEntity(entity);
                            clientApi.addImport(entity);
                        }
                        properties.add(property);
                    }

                    if (type == readAll || type == search || type == advancedSearch) {
                        Property property = new Property();
                        property.setName("total");
                        property.setFieldType(FieldType.NUMERIC_ID);
                        property.setAddress(true);
                        properties.add(property);
                    }

                    clientMethod.setParams(properties);
                }

                Param returnType = localMethod.getReturnType();
                if (returnType != null) {
                    Property property = new Property();
                    property.setFieldType(returnType.getType());
                    Domain domain = (Domain) returnType.getDomain();
                    if (domain != null) {
                        Entity entity = entities.get(domain.getModel());
                        property.setEntity(entity);
                        clientApi.addImport(entity);
                    }
                    clientMethod.setReturnType(property);
                }

                methods.add(clientMethod);
            }
        }

        generate(clientApi, "templates/code/ipad/api.h.vsl", structure.getIpad(), ".h");
        generate(clientApi, "templates/code/ipad/api.m.vsl", structure.getIpad(), ".m");
    }

    private void generateMetadata(Map<Model, Entity> entities, Structure structure) throws IOException {
        ModelManager manager = new ModelManager();
        manager.setName("ModelManager");
        manager.setEntities(entities.values());

        generate(manager, "templates/code/ipad/ModelManager.m.vsl", structure.getIpad(), ".m");
    }

    private void generateProjectFiles(List<FileReference> entities, List<FileReference> controllers, Structure structure) throws IOException {
        Project project = new Project();
        project.setName("project");
        project.setProject(structure.getName());
        project.setEntities(entities);
        project.setControllers(controllers);

        Contents contents = new Contents();
        contents.setName("contents");
        contents.setProject(structure.getName());

        generate(project, "templates/code/ipad/project.vsl", structure.getIpad(), ".pbxproj");
        generate(contents, "templates/code/ipad/contents.vsl", structure.getIpad(), ".xcworkspacedata");
    }

    private void generateRootSettingFiles(Api api, Structure structure) throws IOException {
        RootSettings settings = new RootSettings();
        settings.setName("Root");
        settings.setBase(Env.FIREJACK_URL.getValue());
        settings.setGateway(api.getUrl());
        settings.setPkg(api.getProjectPath());

        generate(settings, "templates/code/ipad/Root.vsl", structure.getIpad(), ".plist");
    }
}
