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

package net.firejack.platform.model.upgrader.operator.bean;

import net.firejack.platform.model.upgrader.bean.INamedUpgradeModel;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for packageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="create-table" type="{}create-tableType"/>
 *         &lt;element name="modify-column" type="{}modify-columnType"/>
 *         &lt;element name="add-column" type="{}add-columnType"/>
 *         &lt;element name="drop-column" type="{}drop-columnType"/>
 *         &lt;element name="drop-table" type="{}drop-tableType"/>
 *         &lt;element name="add-foreign-key" type="{}add-foreign-keyType"/>
 *         &lt;element name="drop-foreign-key" type="{}drop-foreign-keyType"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fromVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="toVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "packageType", propOrder = {
        "dataSourceTypes"
})
@XmlRootElement(name = "package")
public class PackageType implements INamedUpgradeModel {

    @XmlElements({
        @XmlElement(name = "data-source", type = DataSourceType.class)
    })
    protected List<DataSourceType> dataSourceTypes;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String path;
    @XmlAttribute
    protected String fromVersion;
    @XmlAttribute
    protected String toVersion;
    @XmlAttribute
    protected String prefix;

    /**
     * Gets the value of the createTableOrModifyColumnOrAddColumn property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the createTableOrModifyColumnOrAddColumn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreateTableOrModifyColumnOrAddColumn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModifyColumnType }
     * {@link CreateTableType }
     * {@link DropColumnType }
     * {@link AddColumnType }
     * {@link DropTableType }
     * 
     * @return List of children model elements
     */
    public List<DataSourceType> getDataSourceTypes() {
        if (dataSourceTypes == null) {
            dataSourceTypes = new ArrayList<DataSourceType>();
        }
        return this.dataSourceTypes;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the fromVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromVersion() {
        return fromVersion;
    }

    /**
     * Sets the value of the fromVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromVersion(String value) {
        this.fromVersion = value;
    }

    /**
     * Gets the value of the toVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToVersion() {
        return toVersion;
    }

    /**
     * Sets the value of the toVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToVersion(String value) {
        this.toVersion = value;
    }

    /**
     * Gets the value of the prefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the value of the prefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefix(String value) {
        this.prefix = value;
    }

}
