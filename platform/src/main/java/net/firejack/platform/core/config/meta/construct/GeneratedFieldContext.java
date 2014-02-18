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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.patch.EntityElementManager;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;

public class GeneratedFieldContext {

    private IEntityElement entity;
    private ISqlNameResolver nameResolver;
    private EntityElementManager entityElementManager;
    private Reference reference;

    /**
     * @param entity
     * @param nameResolver
     */
    public GeneratedFieldContext(IEntityElement entity, ISqlNameResolver nameResolver) {
        this.entity = entity;
        this.nameResolver = nameResolver;
    }

    /**
     * @return
     */
    public IEntityElement getEntity() {
        return entity;
    }

    /**
     * @param entity
     */
    public void setEntity(IEntityElement entity) {
        this.entity = entity;
    }

    /**
     * @return
     */
    public ISqlNameResolver getNameResolver() {
        return nameResolver;
    }

    /**
     * @param nameResolver
     */
    public void setNameResolver(ISqlNameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }

    /**
     * @return
     */
    public EntityElementManager getEntityElementManager() {
        return entityElementManager;
    }

    /**
     * @param entityElementManager
     */
    public void setEntityElementManager(EntityElementManager entityElementManager) {
        this.entityElementManager = entityElementManager;
    }

    /**
     * @return
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * @param reference
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

}