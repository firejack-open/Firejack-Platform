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
