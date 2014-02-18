package net.firejack.platform.core.config.export;

import org.springframework.stereotype.Component;

@Component("upgradePackageExporter")
public class UpgradePackageExporter extends DefaultPackageExporter {

//    protected List<IEntityElement> getChildEntities(RegistryNodeModel parent, Map<Long, IEntityElement> cachedEntities) {
//        List<IEntityElement> configuredEntities = new ArrayList<IEntityElement>();
//        List<EntityModel> entityList = entityStore.findEntriesByParentId(parent, null, null);
//        if (entityList != null) {
//            for (EntityModel entity : entityList) {
//                if (!Boolean.TRUE.equals(entity.getReverseEngineer())) {
//                    getChildEntity(cachedEntities, configuredEntities, entity);
//                }
//            }
//        }
//        return configuredEntities;
//    }
//
//    protected List<IRelationshipElement> getRelationshipElements(String packageLookupPrefix) {
//        List<RelationshipModel> relationships = relationshipStore.findAllByLikeLookupPrefix(packageLookupPrefix);
//        List<IRelationshipElement> relationshipElements = new ArrayList<IRelationshipElement>();
//        for (RelationshipModel relationship : relationships) {
//            if (!Boolean.TRUE.equals(relationship.getReverseEngineer())) {
//                getRelationshipElement(relationshipElements, relationship);
//            }
//        }
//        return relationshipElements;
//    }

}
