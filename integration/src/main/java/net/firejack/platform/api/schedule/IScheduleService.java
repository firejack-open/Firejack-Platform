package net.firejack.platform.api.schedule;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.List;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

public interface IScheduleService {
	/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	ServiceResponse<Schedule> createSchedule(Schedule data);
	/**
	*This action allows an authorized client to read an existing Schedule 
	*by providing the id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	ServiceResponse<Schedule> readSchedule(Long id);
	/**
	*This action allows an authorized client to read all existing Schedules. 
	*Required parameters and their descriptions are provided in 
	*the parameters list below. 	
*/

	ServiceResponse<Schedule> readAllSchedule(Integer offset, Integer limit, String sortColumn, String sortDirection);

    ServiceResponse<Schedule> readAllSchedulesByRegistryNodeId(Long registryNodeId);
	/**
	*This action allows an authorized client to update an existing Schedule 
	*by providing all required data. Required parameters and their 
	*descriptions are provided in the parameters list below. 	
*/

	ServiceResponse<Schedule> updateSchedule(Long id, Schedule data);
	/**
	*This action allows an authorized client to delete an existing Schedule 
	*by providing its id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	ServiceResponse<Schedule> deleteSchedule(Long id);
	/**
	*This action allows an authorized client to search for existing Schedules 
	*by providing search terms and searchable parameters. Required 
	*parameters and their descriptions are provided in the parameters 
	*list below. 	
*/

	ServiceResponse<Schedule> searchSchedule(String terms, Integer offset, Integer limit, String sortColumn, String sortDirection);
	
	ServiceResponse<Schedule> advancedSearchSchedule(String queryParameters, Integer offset, Integer limit, String sortOrders);

    ServiceResponse<Schedule> executeSchedule(Long id);

    ServiceResponse<ScheduleHistory> readAllScheduleHistoryBySchedule(Long scheduleId, Integer offset, Integer limit, String sortColumn, String sortDirection);

    ServiceResponse<Schedule> getScheduleProgressStatus(List<Long> scheduleIds);

}