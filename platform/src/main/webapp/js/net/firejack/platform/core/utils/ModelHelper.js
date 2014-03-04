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