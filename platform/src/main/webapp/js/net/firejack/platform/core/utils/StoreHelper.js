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

/**
 * Utils functions that manipulate models
 */
Ext.define('OPF.core.utils.StoreHelper', {
    statics: {

        /**
         * Clone the data in one store to a newly created store.  If the store to clone is not loaded yet, the
         * new store will not be loaded with data until after the clone store is loaded.  If a store id is specified,
         * and a store exists with that id, it will be returned immediately without loading data. Otherwise, it will be
         * created and loaded with the data in the storeToClone.
         * @param storeToClone The store that contains the data to clone
         * @param configForNewStore (Optional) The config for the new store
         * @param storeId (Optional) Id of the new store
         * @param uniqueKeyField (Optional) If specified, any duplicate entries will not be added to the new store
         *                                  that have the same value for the specified property
         * @return {Ext.data.Store}
         */
        cloneStore: function(storeToClone, configForNewStore, storeId, uniqueKeyField) {
            if (Ext.isString(storeToClone)) {
                storeToClone = Ext.StoreManager.get(storeToClone);
            }

            var modelName = storeToClone.model.modelName;
            var newStore = new Ext.data.Store(Ext.apply({
                model: modelName,
                storeId: storeId
            }, configForNewStore));

            OPF.core.utils.StoreHelper.delayUntilLoaded(storeToClone, function() {
                var records = [];
                var foundRecords = {};

                storeToClone.each(function(r){
                    //If we have a unique key field, don't add values that have already been added
                    if (uniqueKeyField) {
                        var keyValue = r.get(uniqueKeyField);
                        if (foundRecords[keyValue]) {
                            return;
                        } else {
                            foundRecords[keyValue] = true;
                        }
                    }

                    var copy = r.copy();
                    var associations = copy.associations.items,
                        i, association, mine, theirs, instanceName;

                    for (i=0; i<associations.length; i++) {
                        association = associations[i];
                        instanceName = association.instanceName;
                        theirs = r[instanceName];
                        if (theirs) {
                            copy[instanceName] = theirs;
                        }
                    }
                    records.push(copy);
                });
                newStore.loadRecords(records);
            });
            return newStore;
        },

        copyRecords: function(sourceStore, targetStore) {
            if (Ext.isString(sourceStore)) {
                sourceStore = Ext.StoreManager.get(sourceStore);
            }

            if (Ext.isString(targetStore)) {
                targetStore = Ext.StoreManager.get(targetStore);
            }

            var records = [];
            var foundRecords = {};

            sourceStore.each(function(r) {
                var copy = r.copy();
                var associations = copy.associations.items,
                    i, association, theirs, instanceName;

                for (i = 0; i < associations.length; i++) {
                    association = associations[i];
                    instanceName = association.instanceName;
                    theirs = r[instanceName];
                    if (theirs) {
                        copy[instanceName] = theirs;
                    }
                }
                records.push(copy);
            });
            targetStore.loadRecords(records);
        },

        /**
         * Call the specified function after all the stores are fully loaded
         * @param store A store or an array of stores to load.  Each element in the array can be
         *              a name of the store that will be looked up in teh store manager, an instance of a
         *              store or a function to call that will be passed a function and scope that should be
         *              fired when the load event completes for a particular object.
         * @param fn The function to call when the store(s) are loaded
         * @param scope The scope of the function call
         */
        delayUntilLoaded: function(store, fn, scope) {
            var waitForLoad = 0;
            if (!Ext.isArray(store)) {
                store = [store];
            }

            function storeLoaded() {
                waitForLoad--;
                if (waitForLoad <= 0) {
                    fn.apply(scope, store);
                }
            }

            for (var i=0;i<store.length;i++) {
                var s = store[i];
                //If this is an id, get the registered store for it
                if (Ext.isString(s)) {
                    s = Ext.StoreManager.get(s);
                } else if (Ext.isFunction(s)) {
                    if (!s(storeLoaded, undefiend)) {
                        waitForLoad++;
                    }
                    break;
                }
                if (s.getCount() == 0 || s.loading) {
                    waitForLoad++;
                    s.on('load', storeLoaded, undefined, {
                        single: true
                    });
                }
            }

            //If everything is already loaded, call the success function
            if (waitForLoad == 0) {
                fn.apply(scope, store);
            }
        }

    }
});
OPF.StoreHelper = OPF.core.utils.StoreHelper;