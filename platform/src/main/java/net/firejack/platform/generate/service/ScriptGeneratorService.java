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

package net.firejack.platform.generate.service;

import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.construct.WizardElement;
import net.firejack.platform.core.config.meta.construct.WizardFieldElement;
import net.firejack.platform.core.config.meta.construct.WizardFormElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.js.*;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.column.MappedType;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.beans.web.wizard.Wizard;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.utils.generate.FormattingUtils;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.firejack.platform.generate.beans.Import.DOT;

@Component
public class ScriptGeneratorService extends BaseGeneratorService implements IScriptGeneratorService {

    private static final String JS_COMPONENTS_BASE = "/net/firejack/platform/prometheus";

    @ProgressStatus(weight = 4, description = "Generate JavaScript Entity")
    public void generateScript(Api api, Structure structure) throws IOException {
        Map<String, DomainView> domainView = new HashMap<String, DomainView>();

        createView(api.getModels());

        for (Model<Model> model : api.getModels()) {
            prepareFields(model.getView(), model);
            if (model.isSubclasses())
                for (Model subclass : model.getSubclass())
                    prepareFields(subclass.getView(), subclass);
        }

        for (Model model : api.getModels()) {


            ViewModel viewModel = model.getView();

            if (!model.isSingle() && !model.isAbstracts()) {
                EntityView view = new EntityView(viewModel);
                Controller controller = new Controller(view);
                Application application = new Application(controller);

                if (model instanceof Report) {
                    Report report = (Report) model;
                    if (report.isBiReport()) {
                        ViewModel factModel = report.getModel().getView();

                        viewModel.setFactModel(factModel);
                        List<AssociationsField> associations = factModel.getAssociations();
                        controller.setAssociations(associations);

                        generate(view, "templates/code/web/js/BIReportView.vsl", structure.getJS());
                    } else {
                        generate(view, "templates/code/web/js/ReportView.vsl", structure.getJS());
                    }
                } else {
                    generate(view, "templates/code/web/js/EntityView.vsl", structure.getJS());
                }

                generate(controller, "templates/code/web/js/Controller.vsl", structure.getJS());
                generate(application, "templates/code/web/js/Application.vsl", structure.getJS());
                generate(viewModel, "templates/code/web/js/Model.vsl", structure.getJS());
            }

            if (StringUtils.isNotBlank(model.getClassPath())) {
                DomainView domain = domainView.get(model.getClassPath());
                if (domain == null) {
                    String[] split = model.getClassPath().split("\\.");
                    for (int i = 0; i < split.length; i++) {
                        String classPath = StringUtils.join(split, '.', 0, i + 1);
                        if (!domainView.containsKey(classPath)) {
                            String path = StringUtils.join(split, '.', 0, i);
                            domain = new DomainView(model.getProjectPath(), split[i], path);

                            domainView.put(classPath, domain);
                        }
                    }
                }

                if (!model.isSingle()) {
                    domain = domainView.get(model.getClassPath());
                    domain.addModel(viewModel);
                }
            }
        }

        LoginView loginView = new LoginView(api.getProjectPath(), "login");
        Controller loginController = new Controller(loginView, null);
        Application loginApplication = new Application(loginController);

        generate(loginView, "templates/code/web/js/LoginView.vsl", structure.getJS());
        generate(loginController, "templates/code/web/js/Controller.vsl", structure.getJS());
        generate(loginApplication, "templates/code/web/js/Application.vsl", structure.getJS());


        HomeView homeView = new HomeView(api.getProjectPath(), "home", domainView.values());
        Controller homeController = new Controller(homeView, null);
        Application homeApplication = new Application(homeController);

        generate(homeView, "templates/code/web/js/HomeView.vsl", structure.getJS());
        generate(homeController, "templates/code/web/js/Controller.vsl", structure.getJS());
        generate(homeApplication, "templates/code/web/js/Application.vsl", structure.getJS());


        ForgotPasswordView forgotPasswordView = new ForgotPasswordView(api.getProjectPath(), "Forgot Password");
        Controller forgotPasswordController = new Controller(forgotPasswordView, null);
        Application forgotPasswordApplication = new Application(forgotPasswordController);

        generate(forgotPasswordView, "templates/code/web/js/ForgotPasswordView.vsl", structure.getJS());
        generate(forgotPasswordController, "templates/code/web/js/Controller.vsl", structure.getJS());
        generate(forgotPasswordApplication, "templates/code/web/js/Application.vsl", structure.getJS());


        InboxView inboxView = new InboxView(api.getProjectPath(), "Inbox");
        Controller inboxController = new Controller(inboxView, null);
        Application inboxApplication = new Application(inboxController);

        generate(inboxApplication, "templates/code/web/js/InboxApplication.vsl", structure.getJS());


        for (DomainView view : domainView.values()) {
            Controller controller = new Controller(view, null);
            Application application = new Application(controller);

            generate(view, "templates/code/web/js/DomainView.vsl", structure.getJS());
            generate(controller, "templates/code/web/js/Controller.vsl", structure.getJS());
            generate(application, "templates/code/web/js/Application.vsl", structure.getJS());
        }
        copyBasicComponents(structure);
    }

    private void createView(Collection<Model> models) {
        for (Model model : models) {
            new ViewModel(model);
            if (model.isSubclasses())
                createView(model.getSubclass());
        }
    }

    @Override
    public void generateWizard(IPackageDescriptor descriptor, Structure structure) throws IOException {
        WizardElement[] wizardElements = descriptor.getWizardElements();
        if (wizardElements != null) {
            String _package = DiffUtils.lookup(descriptor.getPath(), descriptor.getName());
            Map<String, Object> map = cache.get();


            for (WizardElement element : wizardElements) {
                String name = element.getName();
                String path = element.getPath();
                String lookup = DiffUtils.lookup(path, name);

                Wizard wizard = new Wizard();
                wizard.setName(Utils.classFormatting(element.getName()));
                wizard.setNormalize(StringUtils.normalize(name));
                wizard.setOriginalName(name);
                wizard.setLookup(lookup);
                wizard.setClassPath(lookup.replace(_package + DOT, ""));
                wizard.setProjectPath(_package);

                Model model = (Model) map.get(element.getModel());
                wizard.setModel(model.getView());
                wizard.setForms(element.getForms());

                for (WizardFormElement formElement : element.getForms()) {
                    formElement.setDisplayName(FormattingUtils.escape(formElement.getDisplayName()));
                    for (WizardFieldElement fieldElement : formElement.getFields()) {
                        String key = fieldElement.getField() != null ? fieldElement.getField() : fieldElement.getRelationship();
                        Field field = (Field) map.get(key);
                        fieldElement.setField(field.getName());
                        fieldElement.setDisplayName(FormattingUtils.escape(fieldElement.getDisplayName()));
                    }
                }

                generate(wizard, "templates/code/web/js/Wizard.vsl", structure.getJS());
            }
        }
    }

    private void prepareFields(ViewModel viewModel, Model model) {
        if (model != null) {
            List<Field> fields = model.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (field.getMapped() == MappedType.ManyToMany) {
                        viewModel.addAssociations(new AssociationsField("hasMany", field));
                    } else if (field.getMapped() == MappedType.ManyToOne) {
                        viewModel.addAssociations(new AssociationsField("belongsTo", field));
                        if (field.getRelationshipType() == RelationshipType.PARENT_CHILD) {
                            field.getTarget().getView().addRelatedChildren(field);
                        }
                    } else {
                        viewModel.addField(field);
                    }
                }
            }
            prepareFields(viewModel, model.getParent());
        }
    }

    private void copyBasicComponents(Structure structure) throws IOException {
        File webappFolder = FileUtils.getWebappFolder();
        File componentsSourceDir = new File(webappFolder, "/js" + JS_COMPONENTS_BASE);
        File targetDir = new File(structure.getJS(), JS_COMPONENTS_BASE);
        FileUtils.forceMkdir(targetDir);
        FileUtils.copyDirectory(componentsSourceDir, targetDir);

        File templateFolder = new File(structure.getResource(), "templates");
        FileUtils.forceMkdir(templateFolder);
        URL templateURL = this.getClass().getResource("/templates/servlet.vsl");
        FileUtils.copyURLToFile(templateURL, new File(templateFolder, "servlet.vsl"));
    }

}
