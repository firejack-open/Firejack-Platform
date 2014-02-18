package net.firejack.platform.api.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */

@XmlEnum
@XmlType(name = "reference")
public enum RelationshipOption {

    RESTRICT,
	CASCADE,
	SET_NULL,
	NO_ACTION,
    SET_DEFAULT
}