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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the operator package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Package_QNAME = new QName("", "package");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: operator
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ModifyColumnType }
     *
     * @return
     */
    public ModifyColumnType createModifyColumnType() {
        return new ModifyColumnType();
    }

    /**
     * Create an instance of {@link DropColumnType }
     *
     * @return
     */
    public DropColumnType createDropColumnType() {
        return new DropColumnType();
    }

    /**
     * Create an instance of {@link DropTableType }
     *
     * @return
     */
    public DropTableType createDropTableType() {
        return new DropTableType();
    }

    /**
     * Create an instance of {@link AddColumnType }
     *
     * @return
     */
    public AddColumnType createAddColumnType() {
        return new AddColumnType();
    }

    /**
     * Create an instance of {@link AddForeignKeyType }
     *
     * @return
     */
    public AddForeignKeyType createAddForeignKeyType() {
        return new AddForeignKeyType();
    }

    /**
     * Create an instance of {@link DropForeignKeyType }
     *
     * @return
     */
    public DropForeignKeyType createDropForeignKeyType() {
        return new DropForeignKeyType();
    }

    /**
     * Create an instance of {@link ColumnType }
     *
     * @return
     */
    public ColumnType createColumnType() {
        return new ColumnType();
    }

    /**
     * Create an instance of {@link PackageType }
     *
     * @return
     */
    public PackageType createPackageType() {
        return new PackageType();
    }

    /**
     * Create an instance of {@link CreateTableType }
     *
     * @return
     */
    public CreateTableType createCreateTableType() {
        return new CreateTableType();
    }

    /**
     * Create an instance of {@link DataSourceType }
     *
     * @return
     */
    public DataSourceType createDataSourceType() {
        return new DataSourceType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link PackageType }{@code >}}
     *
     * @param value
     * @return
     */
    @XmlElementDecl(namespace = "", name = "package")
    public JAXBElement<PackageType> createPackage(PackageType value) {
        return new JAXBElement<PackageType>(_Package_QNAME, PackageType.class, null, value);
    }

}
