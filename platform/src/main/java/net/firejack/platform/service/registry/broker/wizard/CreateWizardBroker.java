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

package net.firejack.platform.service.registry.broker.wizard;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.Wizard;
import net.firejack.platform.api.registry.domain.WizardField;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.wizard.WizardFieldModel;
import net.firejack.platform.core.model.registry.wizard.WizardModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IWizardStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("createWizardBroker")
@Changes("'New Wizard '+ name +' has been created'")
public class CreateWizardBroker extends SaveBroker<WizardModel, Wizard, RegistryNodeTree> {

	@Autowired
	private IWizardStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Report has been created successfully.";
	}

    @Override
    protected ServiceResponse<RegistryNodeTree> perform(ServiceRequest<Wizard> request) throws Exception {
        ServiceResponse<RegistryNodeTree> response;
        Wizard wizard = request.getData();
        if (wizard.getMain() == null || wizard.getMain().getId() == null) {
            response = new ServiceResponse<RegistryNodeTree>("Wizard's Data attribute is not set.", false);
        } else {
            response = super.perform(request);
        }
        return response;
    }

    @Override
	protected WizardModel convertToEntity(Wizard wizard) {
        List<WizardFieldModel> wizardFieldModels = new ArrayList<WizardFieldModel>();
        List<WizardField> wizardForms = wizard.getFields();
        for (WizardField wizardForm : wizardForms) {
            WizardFieldModel wizardFormModel = factory.convertFrom(WizardFieldModel.class, wizardForm);
            wizardFieldModels.add(wizardFormModel);
            List<WizardField> wizardFields = wizardForm.getFields();
            for (WizardField wizardField : wizardFields) {
                WizardFieldModel wizardFieldModel = factory.convertFrom(WizardFieldModel.class, wizardField);
                wizardFieldModel.setForm(wizardFormModel);
                wizardFieldModels.add(wizardFieldModel);
            }
        }
        wizard.setFields(null);

        WizardModel wizardModel = factory.convertFrom(WizardModel.class, wizard);
        wizardModel.setFields(wizardFieldModels);
        return wizardModel;
	}

	@Override
	protected RegistryNodeTree convertToModel(WizardModel model) {
		return treeNodeFactory.convertTo(model);
	}

	@Override
	protected void save(WizardModel model) throws Exception {
		store.save(model);
	}
}
