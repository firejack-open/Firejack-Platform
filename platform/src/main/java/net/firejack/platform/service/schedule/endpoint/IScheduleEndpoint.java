package net.firejack.platform.service.schedule.endpoint;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.schedule.endpoint.IScheduleEndpoint")
public interface IScheduleEndpoint {

	/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> createSchedule(@WebParam(name = "request") ServiceRequest<Schedule> data);
	/**
	*This action allows an authorized client to read an existing Schedule 
	*by providing the id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> readSchedule(@WebParam(name = "id") Long id);
	/**
	*This action allows an authorized client to read all existing Schedules. 
	*Required parameters and their descriptions are provided in 
	*the parameters list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> readAllSchedule(@WebParam(name = "offset") Integer offset,@WebParam(name = "limit") Integer limit,@WebParam(name = "sortColumn") String sortColumn,@WebParam(name = "sortDirection") String sortDirection);

    @WebMethod
    public ServiceResponse<Schedule> readAllSchedulesByRegistryNodeId(@WebParam(name = "registryNodeId") Long registryNodeId);

	/**
	*This action allows an authorized client to update an existing Schedule 
	*by providing all required data. Required parameters and their 
	*descriptions are provided in the parameters list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> updateSchedule(@WebParam(name = "id") Long id,@WebParam(name = "request") ServiceRequest<Schedule> data);
	/**
	*This action allows an authorized client to delete an existing Schedule 
	*by providing its id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> deleteSchedule(@WebParam(name = "id") Long id);
	/**
	*This action allows an authorized client to search for existing Schedules 
	*by providing search terms and searchable parameters. Required 
	*parameters and their descriptions are provided in the parameters 
	*list below. 	
*/

	@WebMethod
	public ServiceResponse<Schedule> searchSchedule(@WebParam(name = "terms") String terms,@WebParam(name = "offset") Integer offset,@WebParam(name = "limit") Integer limit,@WebParam(name = "sortColumn") String sortColumn,@WebParam(name = "sortDirection") String sortDirection);
	
	@WebMethod
	public ServiceResponse<Schedule> advancedSearchSchedule(@WebParam(name = "queryParameters") String queryParameters,@WebParam(name = "offset") Integer offset,@WebParam(name = "limit") Integer limit,@WebParam(name = "sortOrders") String sortOrders);

    @WebMethod
    ServiceResponse<Schedule> executeSchedule(@WebParam(name = "id") Long id);

    @WebMethod
    ServiceResponse<ScheduleHistory> readAllScheduleHistoryBySchedule(
            @WebParam(name = "scheduleId") Long scheduleId,
            @WebParam(name = "offset") Integer offset,
            @WebParam(name = "limit") Integer limit,
            @WebParam(name = "sortColumn") String sortColumn,
            @WebParam(name = "sortDirection") String sortDirection);

    ServiceResponse<Schedule> getScheduleProgressStatus(
            @WebParam(name = "scheduleIds") List<Long> scheduleIds);
}
