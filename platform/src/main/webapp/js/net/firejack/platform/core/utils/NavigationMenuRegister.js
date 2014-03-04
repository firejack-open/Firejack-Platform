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


Ext.define('OPF.core.utils.NavigationMenuRegister', {

    navigationElements: [],

    add: function(navigationElement) {
        this.navigationElements.push(navigationElement);
    },

    findByLookup: function(lookup) {
        var foundNavigationElement = null;
        Ext.each(this.navigationElements, function(navigationElement) {
            if (navigationElement.lookup == lookup) {
                foundNavigationElement = navigationElement;
            }
        });
        return foundNavigationElement;
    }

});
OPF.NavigationMenuRegister = new OPF.core.utils.NavigationMenuRegister();