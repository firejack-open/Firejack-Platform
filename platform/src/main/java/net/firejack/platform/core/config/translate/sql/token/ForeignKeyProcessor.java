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

package net.firejack.platform.core.config.translate.sql.token;

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.translate.sql.ISqlSupport;


public class ForeignKeyProcessor extends SqlToken {

    /***/
    public ForeignKeyProcessor() {
    }

    /**
     * @param sb
     */
    public ForeignKeyProcessor(StringBuilder sb) {
        super(sb);
    }

    /**
     * @param sqlSupport
     */
    public void setSqlDialect(ISqlSupport sqlSupport) {
        this.sqlSupport = sqlSupport;
    }

    /**
     * @param options
     * @return
     */
    public ForeignKeyProcessor addOnUpdateOption(RelationshipOption options) {
        this.sqlSupport.addOnUpdateSection(this, options);
        return this;
    }

    /**
     * @param options
     * @return
     */
    public ForeignKeyProcessor addOnDeleteOption(RelationshipOption options) {
        this.sqlSupport.addOnDeleteSection(this, options);
        return this;
    }

    @Override
    public ForeignKeyProcessor append(Object obj) {
        return (ForeignKeyProcessor) super.append(obj);
    }

    @Override
    public ForeignKeyProcessor space() {
        return (ForeignKeyProcessor) super.space();
    }

    @Override
    public ForeignKeyProcessor inBraces(Object obj) {
        return (ForeignKeyProcessor) super.inBraces(obj);
    }
}