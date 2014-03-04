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

package net.firejack.platform.utils;

import net.firejack.platform.core.model.IIdentifierContainer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class ModelRepository {

    private static final Map<Class<?>, Map<Serializable, Object>> CACHED_MODELS =
            new HashMap<Class<?>, Map<Serializable, Object>>();

    public static <T> T getObject(Class<T> modelClass, Serializable key) {
        T model;
        if (modelClass == null || key == null) {
            model = null;
        } else {
            synchronized (ModelRepository.CACHED_MODELS) {
                Map<Serializable, Object> modelsById = CACHED_MODELS.get(modelClass);
                if (modelsById == null) {
                    model = null;
                } else {
                    model = (T) modelsById.get(key);
                }
            }
        }
        return model;
    }

    public static <T> void saveObject(Serializable key, T object) {
        if (object != null && key != null) {
            synchronized (ModelRepository.CACHED_MODELS) {
                Map<Serializable, Object> modelsById = CACHED_MODELS.get(object.getClass());
                if (modelsById == null) {
                    modelsById = new HashMap<Serializable, Object>();
                    ModelRepository.CACHED_MODELS.put(object.getClass(), modelsById);
                }
                modelsById.put(key, object);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IIdentifierContainer> T getModel(Class<T> modelClass, Long id) {
        T model;
        if (modelClass == null || id == null) {
            model = null;
        } else {
            synchronized (ModelRepository.CACHED_MODELS) {
                Map<Serializable, Object> modelsById = CACHED_MODELS.get(modelClass);
                if (modelsById == null) {
                    model = null;
                } else {
                    model = (T) modelsById.get(id);
                }
            }
        }
        return model;
    }

    public static <T extends IIdentifierContainer> void saveModel(T model) {
        if (model != null && model.getId() != null) {
            synchronized (ModelRepository.CACHED_MODELS) {
                Map<Serializable, Object> modelsById = CACHED_MODELS.get(model.getClass());
                if (modelsById == null) {
                    modelsById = new HashMap<Serializable, Object>();
                    ModelRepository.CACHED_MODELS.put(model.getClass(), modelsById);
                }
                modelsById.put(model.getId(), model);
            }
        }
    }

}