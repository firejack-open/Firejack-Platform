package net.firejack.platform.service.schedule.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

@Component
@Path("/schedule")
public class ScheduleEndpoint implements IScheduleEndpoint {

	/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
    */

	@POST
	@Path("/schedule")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> createSchedule(ServiceRequest<Schedule> data) {
		return OPFEngine.ScheduleService.createSchedule( data.getData());
	}
	/**
	*This action allows an authorized client to read an existing Schedule 
	*by providing the id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@GET
	@Path("/schedule/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> readSchedule(@PathParam("id") Long id) {
		return OPFEngine.ScheduleService.readSchedule( id);
	}
	/**
	*This action allows an authorized client to read all existing Schedules. 
	*Required parameters and their descriptions are provided in 
	*the parameters list below. 	
*/

	@GET
	@Path("/schedule")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> readAllSchedule(@QueryParam("offset") Integer offset,@QueryParam("limit") Integer limit,@QueryParam("sortColumn") String sortColumn,@QueryParam("sortDirection") String sortDirection) {
		return OPFEngine.ScheduleService.readAllSchedule( offset,  limit,  sortColumn,  sortDirection);
	}

    /**
   	 * Read all by parent id
   	 *
   	 * @param registryNodeId parent id
   	 *
   	 * @return founded configs
   	 */
   	@GET
   	@Path("/schedule/node/{registryNodeId}")
   	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public ServiceResponse<Schedule> readAllSchedulesByRegistryNodeId(@PathParam("registryNodeId") Long registryNodeId) {
   		return OPFEngine.ScheduleService.readAllSchedulesByRegistryNodeId(registryNodeId);
   	}

	/**
	*This action allows an authorized client to update an existing Schedule 
	*by providing all required data. Required parameters and their 
	*descriptions are provided in the parameters list below. 	
*/

	@PUT
	@Path("/schedule/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> updateSchedule(@PathParam("id") Long id,ServiceRequest<Schedule> data) {
		return OPFEngine.ScheduleService.updateSchedule( id,  data.getData());
	}
	/**
	*This action allows an authorized client to delete an existing Schedule 
	*by providing its id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@DELETE
	@Path("/schedule/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> deleteSchedule(@PathParam("id") Long id) {
		return OPFEngine.ScheduleService.deleteSchedule( id);
	}
	/**
	*This action allows an authorized client to search for existing Schedules 
	*by providing search terms and searchable parameters. Required 
	*parameters and their descriptions are provided in the parameters 
	*list below. 	
*/

	@GET
	@Path("/schedule/search/{terms}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> searchSchedule(@PathParam("terms") String terms,@QueryParam("offset") Integer offset,@QueryParam("limit") Integer limit,@QueryParam("sortColumn") String sortColumn,@QueryParam("sortDirection") String sortDirection) {
		return OPFEngine.ScheduleService.searchSchedule( terms,  offset,  limit,  sortColumn,  sortDirection);
	}
	
	@GET
	@Path("/schedule/advanced-search")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Schedule> advancedSearchSchedule(@QueryParam("queryParameters") String queryParameters,@QueryParam("offset") Integer offset,@QueryParam("limit") Integer limit,@QueryParam("sortOrders") String sortOrders) {
		return OPFEngine.ScheduleService.advancedSearchSchedule( queryParameters,  offset,  limit,  sortOrders);
	}

    @GET
    @Path("/schedule/execute/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Schedule> executeSchedule(@PathParam("id") Long id) {
        return OPFEngine.ScheduleService.executeSchedule(id);
    }

    @GET
    @Path("/schedule-history/schedule/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ScheduleHistory> readAllScheduleHistoryBySchedule(
            @PathParam("scheduleId") Long scheduleId,
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit,
            @QueryParam("sortColumn") String sortColumn,
            @QueryParam("sortDirection") String sortDirection) {
        return OPFEngine.ScheduleService.readAllScheduleHistoryBySchedule(scheduleId, offset, limit, sortColumn, sortDirection);
    }


    @GET
    @Path("/schedule/statuses")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Schedule> getScheduleProgressStatus(
            @QueryParam("scheduleId") List<Long> scheduleIds) {
        return OPFEngine.ScheduleService.getScheduleProgressStatus(scheduleIds);
    }

}
