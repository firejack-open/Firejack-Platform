/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.store.registry.resource;

import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.UID;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.resource.CollectionMemberModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.store.registry.RegistryNodeStore;
import net.firejack.platform.core.utils.CollectionUtils;
import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/** Class provides access to the data for the registry nodes of the collection type */
@Component("collectionStore")
public class CollectionStore extends RegistryNodeStore<CollectionModel> implements ICollectionStore {

	public static final String DEFAULT_COLLECTION_DOC_NAME = "documentation";

	@Autowired
	@Qualifier("folderStore")
	private IFolderStore folderStore;

	/** Initializes the class */
	@PostConstruct
	public void init() {
		setClazz(CollectionModel.class);
	}

	@Override
	@Transactional(readOnly = true)
	public CollectionModel findById(Long id) {
		CollectionModel collection = super.findByIdWithParent(id);
		return initReference(collection);
	}

	@Override
	@Transactional(readOnly = true)
	public CollectionModel findByLookup(String lookup) {
		CollectionModel collection = super.findByLookup(lookup);
		return initReference(collection);
	}

	private CollectionModel initReference(CollectionModel collection) {
		if (collection != null) {
			List<CollectionMemberModel> members = collection.getCollectionMembers();
			if (members != null) {
				Hibernate.initialize(members);
				for (CollectionMemberModel member : members) {
					Hibernate.initialize(member.getReference());
				}
			}
		}
		return collection;
	}

    @Override
    @Transactional(readOnly = true)
    public CollectionModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(getClazz());
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        criteria.setFetchMode("collectionMembers", FetchMode.JOIN);
        CollectionModel collection = (CollectionModel) criteria.uniqueResult();
        if (collection.getCollectionMembers() != null) {
            for (CollectionMemberModel collectionMember : collection.getCollectionMembers()) {
                Hibernate.initialize(collectionMember.getReference());
            }
        }
        return collection;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionModel> findAllByLikeLookupPrefix(String lookupPrefix) {
        List<CollectionModel> collectionList = super.findAllByLikeLookupPrefix(lookupPrefix);
        if (collectionList != null) {
            for (CollectionModel collection : collectionList) {
                if (collection.getCollectionMembers() != null) {
                    Hibernate.initialize(collection.getCollectionMembers());
                    for (CollectionMemberModel collectionMember : collection.getCollectionMembers()) {
                        Hibernate.initialize(collectionMember.getReference());
                    }
                }
            }
        }
        return collectionList;
    }

    @Override
    @Transactional
    public CollectionModel findOrCreateCollection(RegistryNodeModel registryNode) {
        String collectionLookup = registryNode.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + DEFAULT_COLLECTION_DOC_NAME;
        CollectionModel collection = findByLookup(collectionLookup);
        if (collection == null) {
            FolderModel folder;
            if (registryNode.getType() == RegistryNodeType.ACTION) {
                folder = folderStore.findOrCreateActionFolder((ActionModel) registryNode);
            } else {
                folder = folderStore.findOrCreateFolder(registryNode);
            }

            collection = new CollectionModel();
            collection.setName(DEFAULT_COLLECTION_DOC_NAME);
            collection.setParent(folder);
            save(collection);
        } else {
            Hibernate.initialize(collection.getParent());
        }
        return collection;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistryNodeModel> findReferences(Long collectionId) {
        return findByQuery(null, null, "Collection.findReferences",
                "collectionId", collectionId);
    }

    @Override
    @Transactional
    public void save(CollectionModel collection, boolean isCreateAutoDescription) {
        validate(collection);
        super.save(collection, isCreateAutoDescription);
    }

    @Override
	@Transactional
    public void mergeForGenerator(CollectionModel collection) {
        CollectionModel oldCollection = findByUID(collection.getUid().getUid());
        if (oldCollection == null) {
            throw new BusinessFunctionException("Collection to merge was not found.");
        }
        collection.setId(oldCollection.getId());
        collection.setUid(oldCollection.getUid());
        if (collection.getCollectionMembers() == null) {
            collection.setCollectionMembers(Collections.<CollectionMemberModel>emptyList());
        }
        save(collection);
    }

    @Override
    @Transactional
    public void saveForGenerator(CollectionModel collection) {
        //as soon as collection automatically could be created by other store classes we have to check
        //if there is a collection with the same lookup already exist in the database.
        CollectionModel oldCollection = findByLookup(collection.getLookup());
        if (oldCollection == null) {
            super.saveForGenerator(collection);
        } else {
            Hibernate.initialize(oldCollection.getUid());
            if (!collection.getUid().getUid().equals(oldCollection.getUid().getUid())) {
                logger.error("Collection with the same lookup already exist and having different uid value.");
                UID foundUid = uidById(collection.getUid().getUid());
                if (foundUid == null) {
                    foundUid = collection.getUid();
                    getHibernateTemplate().saveOrUpdate(foundUid);
                }
                oldCollection.setUid(foundUid);
            }
            oldCollection.setDescription(collection.getDescription());
            super.saveOrUpdate(oldCollection);
        }
    }

    @Override
	@Transactional
	public void associateCollectionWithReference(CollectionModel collection, RegistryNodeModel reference) {
		List<CollectionMemberModel> collectionMembers = collection.getCollectionMembers();
		if (collectionMembers == null) {
			collectionMembers = new ArrayList<CollectionMemberModel>();
			collection.setCollectionMembers(collectionMembers);
		} else {
			for (CollectionMemberModel collectionMember : collectionMembers) {
				if (collectionMember.getReference().equals(reference)) {
					return;
				}
			}
		}
		CollectionMemberModel collectionMember = new CollectionMemberModel();
		collectionMember.setCollection(collection);
		collectionMember.setReference(reference);
		collectionMember.setOrder(collectionMembers.size());
		collectionMembers.add(collectionMember);
		save(collection);
	}

	@Override
	@Transactional
	public void swapCollectionMemberships(Long collectionId, Long oneRefId, Long twoRefId) {
		CollectionModel collection = findById(collectionId);
		List<CollectionMemberModel> members = collection.getCollectionMembers();
		if (members != null) {
			CollectionMemberModel oneRef = null;
			CollectionMemberModel twoRef = null;
			for (CollectionMemberModel member : members) {
				if (member.getReference().getId().equals(oneRefId)) {
					oneRef = member;
				}
				if (member.getReference().getId().equals(twoRefId)) {
					twoRef = member;
				}
			}
			if (oneRef != null && twoRef != null) {
				int temp = oneRef.getOrder();
				oneRef.setOrder(twoRef.getOrder());
				twoRef.setOrder(temp);
				save(collection);
			}
		}
	}

    private void validate(CollectionModel src) {
        Long srcId = src.getId();
        if (srcId != null && src.getCollectionMembers() != null) {
            boolean checkCycle = false;
            CollectionModel collection = findById(srcId);
            List<CollectionMemberModel> srcMembers = src.getCollectionMembers();
            List<CollectionMemberModel> members = collection.getCollectionMembers();
            if (members != null) {
                for (CollectionMemberModel srcMember : srcMembers) {
                    for (CollectionMemberModel member : members) {
                        if (srcMember.getId() == null && srcMember.compare(member)) {
                            srcMember.setId(member.getId());
                            break;
                        }
                    }
                    if (srcMember.getId() == null) {
                        checkCycle = true;
                    }
                }
            }

            if (checkCycle) {
                List<Object[]> membersList = findByQuery(null, null, "Collections.findAllRefCollectionId");
                MultiValueMap down = CollectionUtils.create(membersList, 0, 1);
                for (CollectionMemberModel member : srcMembers) {
                    if (member.getId() == null) {
                        checkCycle(down, member.getReference().getId(), srcId);
                    }
                }
            }
        }
    }

    private void checkCycle(MultiValueMap map, Long currentId, Long srcId) {
        if (map.containsKey(currentId)) {
            List collections = (List) map.getCollection(currentId);
            if (collections.contains(srcId)) {
                throw new BusinessFunctionException("Exist cycle collections");
            } else {
                for (Object collection : collections) {
                    checkCycle(map, (Long) collection, srcId);
                }
            }
        }
    }
}
