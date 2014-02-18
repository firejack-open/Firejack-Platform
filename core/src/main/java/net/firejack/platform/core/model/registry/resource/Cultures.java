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
