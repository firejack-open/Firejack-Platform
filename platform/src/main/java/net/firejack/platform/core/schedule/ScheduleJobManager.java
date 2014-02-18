package net.firejack.platform.core.schedule;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleHistoryModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IActionStore;
import net.firejack.platform.core.store.registry.IScheduleHistoryStore;
import net.firejack.platform.core.store.user.IBaseUserStore;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.provider.XMLProvider;
import net.firejack.platform.web.mina.bean.Status;
import net.firejack.platform.web.mina.bean.StatusType;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.*;


@Component("scheduleJobManager")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ScheduleJobManager {

    protected static final int TIMEOUT = 60000;
    protected static final int COUNT_OF_EMPTY_REQUESTS = 3600; // it is about one hour

    private static final Logger logger = Logger.getLogger(ScheduleJobManager.class);

    private Map<Long, ScheduleJobStatus> schedulerJobStatusMap = new HashMap<Long, ScheduleJobStatus>();

    @Autowired
    private IActionStore actionStore;
    @Autowired
    private IScheduleHistoryStore scheduleHistoryStore;
    @Autowired
    private IBaseUserStore<BaseUserModel> baseUserStore;


    public void executeJob(ScheduleModel scheduleModel, Long userOriginalCallerId) {
        ActionModel actionModel = scheduleModel.getAction();

        ScheduleJobStatus scheduleJobStatus = schedulerJobStatusMap.get(scheduleModel.getId());
        if (scheduleJobStatus == null) {
            scheduleJobStatus = new ScheduleJobStatus(scheduleModel, 0,
                    "Starting to execute the '" + scheduleModel.getName() + "' schedule job with id: " + scheduleModel.getId());
            schedulerJobStatusMap.put(scheduleModel.getId(), scheduleJobStatus);

            logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Scheduled Job is running for action: '" + actionModel.getLookup() + "'");

            actionModel = actionStore.findById(actionModel.getId());
            scheduleModel.setAction(actionModel);

            try {
                AuthenticationToken authenticationToken = getAuthenticationToken();
                scheduleJobStatus.setToken(authenticationToken.getToken());

                if (userOriginalCallerId == null) {
                    userOriginalCallerId = authenticationToken.getUser().getId();
                }
                BaseUserModel userModel = baseUserStore.findById(userOriginalCallerId);

                startScheduleHistory(scheduleJobStatus, userModel);

                ServiceResponse response = doRequest(actionModel, scheduleJobStatus);
                if (response.isSuccess()) {
                    if (response.getItem() instanceof Status) {
                        Status status = (Status) response.getItem();
                        logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Progress response: '" + status.getTitle() + "' with percents: " + status.getPercent() + "%");
                        startProgressStatusTask(scheduleJobStatus);
                    } else {
                        logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Immediate response: " + response.getMessage());
                        finishScheduleHistory(scheduleJobStatus, response);
                        schedulerJobStatusMap.remove(scheduleModel.getId());
                    }
                } else {
                    logger.warn("[" + scheduleJobStatus.getRequestUID() + "]: Response: " + response.getMessage());
                    finishScheduleHistory(scheduleJobStatus, response);
                    schedulerJobStatusMap.remove(scheduleModel.getId());
                }
            } catch (Exception e) {
                logger.error("[" + scheduleJobStatus.getRequestUID() + "]: Exception: " + e.getMessage());
                finishScheduleHistory(scheduleJobStatus, new ServiceResponse(e.getMessage(), false));
                schedulerJobStatusMap.remove(scheduleModel.getId());
            }
        } else {
            logger.warn("Scheduled Job: '" + scheduleModel.getName() + "' with id: " + scheduleModel.getId() + " is working now. Completed only " + scheduleJobStatus.getPercents() + "%");
        }
    }

    public ScheduleJobStatus getJobStatus(Long scheduleId) {
        return schedulerJobStatusMap.get(scheduleId);
    }

    private ServiceResponse doRequest(ActionModel action, ScheduleJobStatus scheduleJobStatus) {
        if (HTTPMethod.GET.equals(action.getMethod())) {
            String url = "http://" + action.getServerName() + ":" + action.getPort() + action.getParentPath() + action.getUrlPath();

            ClientConfig config = new DefaultClientConfig();
            config.getClasses().add(JacksonJsonProvider.class);
            config.getClasses().add(XMLProvider.class);
            config.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, getBeans()) {});

            WebResource webResource = Client.create(config).resource(url);
            webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
            webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
            WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE).type(MediaType.APPLICATION_XML_TYPE);

            Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, scheduleJobStatus.getToken());
            builder.cookie(cookie);
            builder.header("Page-UID", scheduleJobStatus.getPageUID());
            return builder.get(ServiceResponse.class);
        } else {
            throw new BusinessFunctionException("This action: '" + action.getLookup() + "' doesn't supported.");
        }
    }

    private AuthenticationToken getAuthenticationToken() {
        File keyStore = InstallUtils.getKeyStore();
        if (keyStore.exists()) {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                KeyPair keyPair = KeyUtils.load(keyStore);
                if (keyPair == null) {
                    throw new IllegalStateException("Key not found");
                }

                X509Certificate certificate = KeyUtils.generateCertificate("", 1, keyPair);
                String cert = new String(Base64.encode(certificate.getEncoded()));

                ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processSTSCertSignIn(OpenFlame.PACKAGE, hostName, cert);
                if (response.isSuccess()) {
                    AuthenticationToken authenticationToken = response.getItem();
                    if (authenticationToken.getToken() != null) {
                        return authenticationToken;
                    } else {
                        throw new BusinessFunctionException("Could not get authentication token.");
                    }
                } else {
                    throw new BusinessFunctionException("Could not get authentication token.");
                }
            } catch (Exception e) {
                logger.error(e);
                throw new BusinessFunctionException(e.getMessage(), e);
            }
        } else {
            throw new BusinessFunctionException("Could not find key store file.");
        }
    }

    private Class[] getBeans() {
        Map<String, AbstractDTO> map = OpenFlameSpringContext.getContext().getBeansOfType(AbstractDTO.class);
        Class[] classes = new Class[map.size()];
        int i = 0;
        for (AbstractDTO dto : map.values()) {
            classes[i++] = dto.getClass();
        }
        return classes;
    }

    private void startProgressStatusTask(final ScheduleJobStatus scheduleJobStatus) {
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                boolean needToContinue = doProgressStatusRequest(scheduleJobStatus);
                if (!needToContinue) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private boolean doProgressStatusRequest(ScheduleJobStatus scheduleJobStatus) {
        boolean needToContinue = false;
        try {
            ClientConfig config = new DefaultClientConfig();
            config.getClasses().add(JacksonJsonProvider.class);
            config.getClasses().add(XMLProvider.class);
            config.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, getBeans()) {});

            ActionModel action = scheduleJobStatus.getScheduleModel().getAction();
            String url = "http://" + action.getServerName() + ":" + action.getPort() + action.getParentPath() + "/rest/progress/status";
            WebResource webResource = Client.create(config).resource(url);
            webResource.setProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIMEOUT);
            webResource.setProperty(ClientConfig.PROPERTY_READ_TIMEOUT, TIMEOUT);
            WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_XML_TYPE).type(MediaType.APPLICATION_XML_TYPE);

            Cookie cookie = new Cookie(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE, scheduleJobStatus.getToken());
            builder.cookie(cookie);
            builder.header("Page-UID", scheduleJobStatus.getPageUID());
            ServiceResponse response = builder.get(ServiceResponse.class);
            if (response.isSuccess()) {
                List responseData = response.getData();
                if (responseData == null || responseData.isEmpty()) {
                    logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Progress response: 'Progress status list is empty' current percents: " + scheduleJobStatus.getPercents() + "%");
                    scheduleJobStatus.incrementCountOfEmptyRequests();
                    if (scheduleJobStatus.getCountOfEmptyRequests() < COUNT_OF_EMPTY_REQUESTS) {
                        needToContinue = true;
                    } else {
                        String message = "'" + scheduleJobStatus.getScheduleModel().getName() + "' has not been responding for long time: " + scheduleJobStatus.getDurationTimeOfEmptyRequests();
                        logger.warn("[" + scheduleJobStatus.getRequestUID() + "]: Timeout response: " + message);
                        finishScheduleHistory(scheduleJobStatus, new ServiceResponse(message, false));
                        schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
                    }
                } else {
                    for (Object data : responseData) {
                        AbstractDTO item = ((ServiceResponse) data).getItem();
                        if (item instanceof Status) {
                            Status status = (Status) item;
                            if (StatusType.ERROR.equals(status.getType())) {
                                logger.warn("[" + scheduleJobStatus.getRequestUID() + "]: Error response: " + status.getTitle());
                                finishScheduleHistory(scheduleJobStatus, new ServiceResponse(status.getTitle(), false));
                                schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
                                break;
                            } else {
                                logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Progress response: '" + status.getTitle() + "' with percents: " + status.getPercent() + "%");
                                scheduleJobStatus.setPercents(status.getPercent());
                                scheduleJobStatus.setMessage(status.getTitle());
                                if (status.getPercent() < 100) {
                                    needToContinue = true;
                                    scheduleJobStatus.resetCountOfEmptyRequests();
                                } else {
                                    needToContinue = false;
                                    finishScheduleHistory(scheduleJobStatus, response);
                                    schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
                                    break;
                                }
                            }
                        } else {
                            needToContinue = false;
                            logger.info("[" + scheduleJobStatus.getRequestUID() + "]: Completed response: " + response.getMessage());
                            finishScheduleHistory(scheduleJobStatus, response);
                            schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
                            break;
                        }
                    }
                }
            } else {
                logger.warn("[" + scheduleJobStatus.getRequestUID() + "]: Failure response: " + response.getMessage());
                finishScheduleHistory(scheduleJobStatus, response);
                schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
            }
        } catch (Exception e) {
            logger.error("[" + scheduleJobStatus.getRequestUID() + "]: Exception: " + e.getMessage());
            finishScheduleHistory(scheduleJobStatus, new ServiceResponse(e.getMessage(), false));
            schedulerJobStatusMap.remove(scheduleJobStatus.getScheduleModel().getId());
        }
        return needToContinue;
    }

    private void startScheduleHistory(ScheduleJobStatus scheduleJobStatus, BaseUserModel userModel) {
        ScheduleHistoryModel scheduleHistoryModel = new ScheduleHistoryModel();
        scheduleHistoryModel.setStartTime(new Date());
        scheduleHistoryModel.setSchedule(scheduleJobStatus.getScheduleModel());
        scheduleHistoryModel.setUser(userModel);
        scheduleHistoryStore.saveScheduleHistory(scheduleHistoryModel);
        scheduleJobStatus.setScheduleHistoryModel(scheduleHistoryModel);
    }

    private void finishScheduleHistory(ScheduleJobStatus scheduleJobStatus, ServiceResponse response) {
        ScheduleHistoryModel scheduleHistoryModel = scheduleJobStatus.getScheduleHistoryModel();
        scheduleHistoryModel.setEndTime(new Date());
        scheduleHistoryModel.setSuccess(response.isSuccess());
        String responseMessage = StringUtils.defaultIfEmpty(response.getMessage(), "No message");
        String message = StringUtils.cutting(responseMessage, 1000);
        scheduleHistoryModel.setMessage(message);
        scheduleHistoryStore.saveScheduleHistory(scheduleHistoryModel);
    }
}
