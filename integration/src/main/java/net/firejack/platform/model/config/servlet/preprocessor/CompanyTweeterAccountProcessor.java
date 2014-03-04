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

package net.firejack.platform.model.config.servlet.preprocessor;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.processor.cache.ConfigCacheManager;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class CompanyTweeterAccountProcessor implements IGatewayPreProcessor {

    @Override
    public void execute(Map<String, String> map, HttpServletRequest request, HttpServletResponse response, NavigationElement currentNavigationElement) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        Config config = configCacheManager.getConfig(OpenFlameSecurityConstants.getPackageLookup() + ".company-tweeter-account");
        if (config != null) {
			map.put("companyTweeterAccount", "'" + config.getValue() + "'");
		}
    }
}
