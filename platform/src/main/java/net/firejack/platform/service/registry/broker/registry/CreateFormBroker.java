package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.Form;
import net.firejack.platform.api.registry.domain.Relationship;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@ProgressComponent(weight = 20000)
@Component("createFormBroker")
public class CreateFormBroker extends ServiceBroker<ServiceRequest<Form>, ServiceResponse<Form>> {

    @Autowired
    private IEntityStore entityStore;

    @Override
    protected ServiceResponse<Form> perform(ServiceRequest<Form> request) throws Exception {
        Form form = request.getData();

        Domain domain = form.getDomain();
        DomainModel domainModel = factory.convertFrom(DomainModel.class, domain);

        List<Entity> entities = form.getEntities();
        List<EntityModel> entityModels = factory.convertFrom(EntityModel.class, entities);

        List<Relationship> relationships = form.getRelationships();
        List<RelationshipModel> relationshipModels = factory.convertFrom(RelationshipModel.class, relationships);

        entityStore.saveForm(domainModel, entityModels, relationshipModels);

        return new ServiceResponse<Form>("All Entities with Relationships have been created successfully.", true);
    }

}
