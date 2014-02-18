package net.firejack.platform.model.config.servlet.preprocessor;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.utils.WebUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class WorkflowProcessor implements IGatewayPreProcessor {

    private static final Logger logger = Logger.getLogger(WorkflowProcessor.class);

    @Override
    public void execute(Map<String, String> map, HttpServletRequest request, HttpServletResponse response, NavigationElement currentNavigationElement) {
        if (currentNavigationElement != null && NavigationElementType.WORKFLOW.equals(currentNavigationElement.getElementType())) {
            String processLookup = currentNavigationElement.getUrlParams();

            Long activityActionId = ServletRequestUtils.getLongParameter(request, "activityActionId", 0);

            ServiceResponse<Process> serviceResponse;
            if (activityActionId == 0) {
                serviceResponse = OPFEngine.ProcessService.readProcessWithStartActivity(processLookup);
            } else {
                serviceResponse = OPFEngine.ProcessService.readProcessWithActionActivity(activityActionId);
            }
            if (serviceResponse.isSuccess()) {
                Process process = serviceResponse.getItem();
                try {
                    String processJson = WebUtils.serializeObjectToJSON(process);
                    map.put("process", processJson);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.error(serviceResponse.getMessage());
                throw new BusinessFunctionException(serviceResponse.getMessage());
            }

            Lookup main = currentNavigationElement.getMain();
            if (main != null) {
                try {
                    String entityJson = WebUtils.serializeObjectToJSON(main);
                    map.put("entity", entityJson);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
