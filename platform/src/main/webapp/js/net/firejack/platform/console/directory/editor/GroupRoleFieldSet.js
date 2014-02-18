/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

/**
 * The GroupRoleFieldSet component inherited from BaseRoleFieldSet component and
 * containing the available and assigned user role grids.
 */
Ext.define('OPF.console.directory.editor.GroupRoleFieldSet', {
    extend: 'OPF.console.directory.editor.BaseRoleFieldSet',

    getRoleListUrl: function(searchPhrase) {//use the same method that is used in UserRoleFieldSet.js for now
        return OPF.isBlank(searchPhrase) ?
            OPF.core.utils.RegistryNodeType.USER.generateUrl('/global/roles') :
            OPF.core.utils.RegistryNodeType.USER.generateUrl('/global/roles/search/' + escape(searchPhrase));
    }

});