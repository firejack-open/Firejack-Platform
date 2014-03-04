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

package net.firejack.platform.core.domain;

import java.util.HashMap;
import java.util.Map;

public class NamedValues<P> extends AbstractDTO {
    private static final long serialVersionUID = -3495811542543276085L;

    private Map<String, P> parameters = new HashMap<String, P>();

    public P get(String key) {
        return parameters.get(key);
    }

    public void put(String key, P value) {
        parameters.put(key, value);
    }

	public Map<String, P> getAll() {
		return parameters;
	}

}
