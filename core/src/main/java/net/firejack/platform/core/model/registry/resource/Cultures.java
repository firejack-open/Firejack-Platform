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

package net.firejack.platform.core.model.registry.resource;

import java.util.Locale;


public enum Cultures {

    AMERICAN(Locale.US),

    BRITISH(Locale.UK),

    CHINESE(Locale.CHINA),

    FRENCH(Locale.FRANCE),

    GERMAN(Locale.GERMANY),

    GREEK(new Locale("el", "GR")),

    ITALIAN(Locale.ITALY),

    JAPANESE(Locale.JAPAN),

    PORTUGESE(new Locale("pt", "PT")),

    RUSSIAN(new Locale("ru", "RU")),

    SPANISH(new Locale("es", "ES"));

    private Locale locale;

    Cultures(Locale locale) {
        this.locale = locale;
    }

    /**
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * @return
     */
    public String getName() {
        return this.name();
    }

    /**
     * @param name
     * @return
     */
    public static Cultures findByName(String name) {
        Cultures value = null;
        for (Cultures e : values()) {
            if (e.name().equals(name)) {
                value = e;
                break;
            }
        }
        return value;
    }

    /**
     * @param locale
     * @return
     */
    public static Cultures findByCountry(String locale) {
        Cultures value = null;
        for (Cultures e : values()) {
            if (e.getLocale().getCountry().equalsIgnoreCase(locale)) {
                value = e;
                break;
            }
        }
        return value;
    }

    /**
     * @return
     */
    public static Cultures getDefault() {
        return AMERICAN;
    }

}
