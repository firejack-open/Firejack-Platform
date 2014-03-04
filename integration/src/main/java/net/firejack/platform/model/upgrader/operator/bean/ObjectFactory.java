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
