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
 * Helper functions that manipulate models
 */
Ext.define('OPF.core.utils.ModelHelper', {
    statics: {

        getCompositeId: function(record, associationFields, reloadFromRaw) {
            var compositeIdKeys = [];
            if (record) {
                if (reloadFromRaw && record.raw) {
                    record = this.createModelFromData(record, record.raw);
                }

                Ext.each(associationFields, function(associationField) {
                    var association = record.associations.get(associationField);
                    if (association) {
                        compositeIdKeys.push(associationField);
                        var associationModel = Ext.create(association.model);
                        var id = OPF.ModelHelper.getFk(record, associationField, associationModel.idProperty);
                        compositeIdKeys.push(id);
                    }
                });
            }
            return compositeIdKeys.join("/");
        },

        /**
         * Return a property value given a record and complex property name that contains ., it will look at associations for anything
         * before a . in the complex property
         * @param record
         * @param complexProperty A fully qualified property name. i.e. 'foreignKey.name' or an array containing
         *                        the property and sub properties to return.
         * @return {*}
         */
        getProperty: function (record, complexProperty) {
            if (!complexProperty) {
                Ext.log.warn("Empty complex property sent in for a getRecord call!");
                return;
            }
            var properties;
            if (Ext.isArray(complexProperty)) {
                properties = complexProperty;
            } else {
                properties = complexProperty.split('.');
            }
            if (properties.length == 1) {
                var retVal = record.get(properties[0]);

                //If we were unable to get a regular property, try to see if its a foreign key
                if (retVal === null || retVal === undefined) {
                    retVal = OPF.ModelHelper.getFk(record, properties[0], null);
                }
                return retVal;
            } else {

                var association = record.associations.get(properties[0]);
                if (association) {
                    var fk = record[association.instanceName];
                    if (fk) {
                        //remove the first record
                        properties = properties.splice(1);
                        return OPF.ModelHelper.getProperty(fk, properties);
                    }
                }
            }
        },

        /**
         * A helper function that will safely return a foreign key or a value on    it for a particular record.  This function
         * basically helps guard against undefined exceptions as it will return undefined fi the foreign key is null
         * @param record
         * @param foreignKeyName
         * @param foreignKeyProperty
         * @return {*}
         */
        getFk: function(record, foreignKeyName, foreignKeyProperty) {
            if (!record)
                return;
            var association = record.associations.get(foreignKeyName);
            if (association) {
                if (association.instanceName && record[association.instanceName]) {
                    var fk = record['get' + foreignKeyName]();
                    if (fk && foreignKeyProperty) {
                        var retVal = fk.get(foreignKeyProperty);

                        //If we were unable to get a regular property, try to see if its a foreign key
                        if (retVal === null || retVal === undefined) {
                            retVal = OPF.ModelHelper.getFk(fk, foreignKeyProperty);
                        }
                        return retVal;
                    } else {
                        return fk;
                    }
                }
            }
        },

        setFk: function(record, foreignKeyName, foreignKeyProperty, value) {
            if (!record)
                return;

            var association = record.associations.get(foreignKeyName);
            if (association) {
                if (association.instanceName && record['get' + foreignKeyName]) {
                    var fk = record['get' + foreignKeyName]();
                    fk.set(foreignKeyProperty, value);
                    return;
                }
            }
            Ext.log.warn('Could not find an appropriate record in the store to set!');
        },

        setFkModel: function(record, foreignKeyName, fkData, isModel) {
            if (!record) {
                return;
            }
            var association = record.associations.get(foreignKeyName);
            if (association && association.instanceName && record['get' + foreignKeyName]) {
                if (isModel) {
                    record[association.instanceName] = fkData;
                } else {
                    record[association.instanceName] = OPF.ModelHelper.createModelFromData(association.model, fkData);
                }
            }
        },

        createModelFromData: function(model, jsonData) {
            if (Ext.isString(model)) {
                var reader = Ext.create('Ext.data.reader.Json', {
                    model: model
                });
                var resultSet = reader.read(jsonData);
                model = resultSet.records[0];
            }

            Ext.each(model.associations.items, function(association) {
                var associationModel = Ext.create(association.model);

                var associationData = jsonData[association.name];
                if (associationData) {
                    if (association.type == 'belongsTo') {
                        model[association.instanceName] = OPF.ModelHelper.createModelFromData(association.model, associationData);
                    } else if (association.type == 'hasMany') {
                        var associationModels = [];
                        Ext.each(associationData, function(associationItem) {
                            var associationModel = OPF.ModelHelper.createModelFromData(association.model, associationItem);
                            associationModels.push(associationModel);
                        });
                        var store = Ext.create('Ext.data.Store', {
                            model: association.model
                        });
                        store.add(associationModels);
                        model[association.name + 'Store'] = store;
                    }
                }
            });
            return model;
        },

        createAssociationProperty: function() {

        },

        getModelDataFromStore: function(store) {
            var data = [];
            var records = store.getRange();
            for (var i = 0; i < records.length; i++) {
                data.push(records[i].getData(true));
            }
            return data;
        },

        getData: function(record, includeAssociated, includeNotPersisted){
            var fields = record.fields.items,
                fLen   = fields.length,
                data   = {},
                name, f;

            for (f = 0; f < fLen; f++) {
                if (fields[f].persist !== false || includeNotPersisted) {
                    name = fields[f].name;
                    data[name] = record.get(name);
                }
            }

            if (includeAssociated === true) {
                Ext.apply(data, OPF.ModelHelper.getAssociatedData(record, {}, 1, includeNotPersisted));
            }
            return data;
        },

        /**
         * @private
         */
        getAssociatedData: function(record, seenKeys, depth, includeNotPersisted) {

            var associations     = record.associations.items,
                associationCount = associations.length,
                associationData  = {},
                toRead           = [],
                toReadKey        = [],
                toReadIndex      = [],
                associatedStore, associatedRecords, associatedRecord, o, index, result, seenDepth,
                associationId, associatedRecordCount, association, i, j, type, name;

            for (i = 0; i < associationCount; i++) {
                association = associations[i];
                associationId = association.associationId;

                seenDepth = seenKeys[associationId];
                if (seenDepth && seenDepth !== depth) {
                    continue;
                }
                seenKeys[associationId] = depth;

                type = association.type;
                name = association.name;
                if (type == 'hasMany') {
                    associatedStore = record[association.storeName];
                    associationData[name] = [];

                    if (associatedStore && associatedStore.getCount() > 0) {
                        associatedRecords = associatedStore.data.items;
                        associatedRecordCount = associatedRecords.length;
                        for (j = 0; j < associatedRecordCount; j++) {
                            associatedRecord = associatedRecords[j];
                            associationData[name][j] = OPF.ModelHelper.getData(associatedRecord, false, includeNotPersisted);
                            toRead.push(associatedRecord);
                            toReadKey.push(name);
                            toReadIndex.push(j);
                        }
                    }
                } else if (type == 'belongsTo' || type == 'hasOne') {
                    associatedRecord = record[association.instanceName];
                    if (associatedRecord !== undefined) {
                        associationData[name] = OPF.ModelHelper.getData(associatedRecord, false, includeNotPersisted);
                        toRead.push(associatedRecord);
                        toReadKey.push(name);
                        toReadIndex.push(-1);
                    }
                }
            }

            for (i = 0, associatedRecordCount = toRead.length; i < associatedRecordCount; ++i) {
                associatedRecord = toRead[i];
                o = associationData[toReadKey[i]];
                index = toReadIndex[i];
                result = OPF.ModelHelper.getAssociatedData(associatedRecord, seenKeys, depth + 1, includeNotPersisted);
                if (index === -1) {
                    Ext.apply(o, result);
                } else {
                    Ext.apply(o[index], result);
                }
            }
            return associationData;
        }
    }

});

OPF.ModelHelper = OPF.core.utils.ModelHelper;