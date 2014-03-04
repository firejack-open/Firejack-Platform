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

package net.firejack.platform.model.event;

import net.firejack.platform.api.config.domain.Config;
import org.springframework.context.ApplicationEvent;

public class ChangeConfigEvent extends ApplicationEvent {
    private static final long serialVersionUID = 398043607878768371L;

    private Config config;

    /**
     * @param config
     */
    public ChangeConfigEvent(Config config) {
        super(config);
        this.config = config;
    }

    /**
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

}
