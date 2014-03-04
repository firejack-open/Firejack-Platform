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

package net.firejack.platform.generate.beans;

public class ImportString implements Import {
    private String _package;
    private String name;

    /**
     * @param _package
     * @param name
     */
    public ImportString(String _package, String name) {
        this._package = _package;
        this.name = name;
    }

    public ImportString(Class _class) {
        this._package = _class.getPackage().getName();
        this.name = _class.getSimpleName();
    }

    @Override
    public String getPackage() {
        return this._package;
    }

    @Override
    public String getFullName() {
        return _package + DOT + name;
    }

    @Override
    public int compareTo(Import path) {
        return getFullName().compareTo(path.getFullName());
    }
}
