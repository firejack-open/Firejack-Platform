package net.firejack.platform.api.schedule;

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.response.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

public class ScheduleServiceProxy extends AbstractServiceProxy implements IScheduleService {

    public ScheduleServiceProxy(Class[] classes) {
        super(classes);
    }

    @Override
    public String getServiceUrlSuffix() {
        return "/schedule";
    }
    /**
	*This action allows an authorized client to create a new Schedule
	*by providing all required data. Required parameters and their descriptions
	*are provided in the parameters list below.
*/

	@Override
	public ServiceResponse<Schedule> createSchedule(Schedule data) {
		return post2("/schedule", data);
	}
    /**
	*This action allows an authorized client to read an existing Schedule
	*by providing the id. Required parameters and their descriptions
	*are provided in the parameters list below.
*/

	@Override
	public ServiceResponse<Schedule> readSchedule(Long id) {
		return get("/schedule/" + id);
	}
    /**
	*This action allows an authorized client to read all existing Schedules.
	*Required parameters and their descriptions are provided in
	*the parameters list below.
*/

	@Override
	public ServiceResponse<Schedule> readAllSchedule(Integer offset, Integer limit, String sortColumn, String sortDirection) {
		return get("/schedule","offset",offset,"limit",limit,"sortColumn",sortColumn,"sortDirection",sortDirection);
	}

    @Override
    public ServiceResponse<Schedule> readAllSchedulesByRegistryNodeId(Long registryNodeId) {
        return get("/schedule/node/" + registryNodeId);
    }

    /**
	*This action allows an authorized client to update an existing Schedule
	*by providing all required data. Required parameters and their
	*descriptions are provided in the parameters list below.
*/

	@Override
	public ServiceResponse<Schedule> updateSchedule(Long id, Schedule data) {
		return put2("/schedule/" + id, data);
	}
    /**
	*This action allows an authorized client to delete an existing Schedule
	*by providing its id. Required parameters and their descriptions
	*are provided in the parameters list below.
*/

	@Override
	public ServiceResponse<Schedule> deleteSchedule(Long id) {
		return delete("/schedule/" + id);
	}

	/**
	*This action allows an authorized client to search for existing Schedules
	*by providing search terms and searchable parameters. Required
	*parameters and their descriptions are provided in the parameters
	*list below.
*/

	@Override
	public ServiceResponse<Schedule> searchSchedule(String terms, Integer offset, Integer limit, String sortColumn, String sortDirection) {
		return get("/schedule/search/" + terms,"offset",offset,"limit",limit,"sortColumn",sortColumn,"sortDirection",sortDirection);
	}

	@Override
	public ServiceResponse<Schedule> advancedSearchSchedule(String queryParameters, Integer offset, Integer limit, String sortOrders) {
		return get("/schedule/advanced-search","queryParameters",queryParameters,"offset",offset,"limit",limit,"sortOrders",sortOrders);
	}

    @Override
    public ServiceResponse<Schedule> executeSchedule(Long id) {
        return get("/schedule/execute" + id);
    }

    @Override
    public ServiceResponse<ScheduleHistory> readAllScheduleHistoryBySchedule(Long scheduleId, Integer offset, Integer limit, String sortColumn, String sortDirection) {
        return get("/schedule-history/schedule/" + scheduleId, "offset", offset, "limit", limit, "sortColumn", sortColumn, "sortDirection", sortDirection);
    }

    @Override
    public ServiceResponse<Schedule> getScheduleProgressStatus(List<Long> scheduleIds) {
        List<String> queries = new ArrayList<String>();
        for (Long scheduleId : scheduleIds) {
            queries.add("scheduleId");
            queries.add(scheduleId.toString());
        }
        return get("/schedule/statuses", queries);
    }
}