package net.firejack.platform.api.registry.domain;
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


import net.firejack.platform.api.registry.model.DatabaseActionType;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.annotation.NotNull;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseAction extends AbstractDTO {
	private static final long serialVersionUID = -2304970404973575572L;

	private Long id;
	private Long sourceId;
	private DatabaseActionType action;
    private Database database;

    @NotNull
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    @NotNull
	public DatabaseActionType getAction() {
		return action;
	}

	public void setAction(DatabaseActionType action) {
		this.action = action;
	}

    @JsonIgnore
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
}
