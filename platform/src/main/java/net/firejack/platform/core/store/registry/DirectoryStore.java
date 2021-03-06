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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.store.user.IBaseUserStore;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.List;

@Component("directoryStore")
@SuppressWarnings("unused")
public class DirectoryStore extends FieldContainerStore<DirectoryModel> implements IDirectoryStore {

    @Autowired
    @Qualifier("baseUserStore")
    private IBaseUserStore userStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(DirectoryModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DirectoryModel findByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(DirectoryModel.class);
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        return (DirectoryModel) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public DirectoryModel deleteByUID(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Empty UID parameter.");
        }
        Criteria criteria = getSession().createCriteria(DirectoryModel.class);
        criteria.createAlias("uid", "uid");
        criteria.add(Restrictions.eq("uid.uid", uid));
        DirectoryModel directoryToDelete = (DirectoryModel) criteria.uniqueResult();
        if (directoryToDelete != null) {
            deleteRecursively(directoryToDelete);
        }
        return directoryToDelete;
    }

    @Override
    @Transactional
    public void delete(DirectoryModel registryNode) {
        userStore.deleteAllByRegistryNodeId(registryNode.getId());
        super.delete(registryNode);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
	    userStore.deleteAllByRegistryNodeId(id);
        super.deleteRecursiveById(id);
	    super.deleteById(id);
    }

    @Override
    @Transactional
    public void save(DirectoryModel directory) {
        if (directory.getId() == null) {
            Integer directoryMaxOrderPosition = this.findMaxOrderPosition(null);
            directory.setSortPosition(directoryMaxOrderPosition + 1);
        }
        super.save(directory);
    }

    @Override
    @Transactional
    public void saveForGenerator(DirectoryModel directory) {
        super.saveForGenerator(directory);
        boolean isNew = directory.getId() == null;
        List<FieldModel> fields = directory.getFields();
        directory.setFields(null);

        super.save(directory, false);

        saveFields(directory, isNew, fields);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectoryModel> findOrderedDirectoryList() {
        return find(null, null, "Directory.findAllOrdered");
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DirectoryModel> findOrderedDirectoryList(final Integer sortPosStart, final Integer sortPosEnd) {
        return getHibernateTemplate().execute(new HibernateCallback<List<DirectoryModel>>() {
            public List<DirectoryModel> doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.add(Restrictions.ge("sortPosition", sortPosStart));
                criteria.add(Restrictions.le("sortPosition", sortPosEnd));
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional
    public void moveDirectory(Long directoryId, Integer newPosition) {
        DirectoryModel dir = findById(directoryId);
        if (dir != null && !dir.getSortPosition().equals(newPosition)) {
            List<DirectoryModel> orderedDirectoryList;
            if (dir.getSortPosition() > newPosition) {
                orderedDirectoryList = findOrderedDirectoryList(newPosition, dir.getSortPosition() - 1);
                for (DirectoryModel d : orderedDirectoryList) {
                    d.setSortPosition(d.getSortPosition() + 1);
                    saveOrUpdate(d);
                }
            } else {
                orderedDirectoryList = findOrderedDirectoryList(dir.getSortPosition() + 1, newPosition);
                for (DirectoryModel d : orderedDirectoryList) {
                    d.setSortPosition(d.getSortPosition() - 1);
                    saveOrUpdate(d);
                }
            }
            dir.setSortPosition(newPosition);
            saveOrUpdate(dir);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findMaxOrderPosition(final Long parentId) {
        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(getClazz());
                criteria.setProjection(Projections.max("sortPosition"));
                return (Integer) criteria.uniqueResult();
            }
        });
    }

}