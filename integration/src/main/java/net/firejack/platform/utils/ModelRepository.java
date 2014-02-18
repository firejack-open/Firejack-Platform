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