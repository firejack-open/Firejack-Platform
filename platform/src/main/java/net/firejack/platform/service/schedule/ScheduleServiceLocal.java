package net.firejack.platform.service.schedule;

import net.firejack.platform.api.schedule.IScheduleService;
import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.schedule.broker.schedule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

@Component
public class ScheduleServiceLocal implements IScheduleService {

	@Autowired
	private CreateScheduleBroker createScheduleBroker;
	@Autowired
	private ReadScheduleBroker readScheduleBroker;
	@Autowired
	private ReadAllScheduleBroker readAllScheduleBroker;
    @Autowired
    private ReadScheduleListByRegistryNodeBroker readScheduleListByRegistryNodeBroker;
	@Autowired
	private UpdateScheduleBroker updateScheduleBroker;
	@Autowired
	private DeleteScheduleBroker deleteScheduleBroker;
	@Autowired
	private SearchScheduleBroker searchScheduleBroker;
	@Autowired
	private AdvancedSearchScheduleBroker advancedSearchScheduleBroker;
    @Autowired
	private ExecuteScheduleBroker executeScheduleBroker;
    @Autowired
    private ReadAllScheduleHistoryByScheduleBroker readAllScheduleHistoryByScheduleBroker;
    @Autowired
    private GetScheduleProgressStatusBroker getScheduleProgressStatusBroker;


	/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@Override
	public ServiceResponse<Schedule> createSchedule(Schedule data) {
		return createScheduleBroker.execute(new ServiceRequest<Schedule>(data));
	}
	/**
	*This action allows an authorized client to read an existing Schedule 
	*by providing the id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@Override
	public ServiceResponse<Schedule> readSchedule(Long id) {
		return readScheduleBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(id)));
	}
	/**
	*This action allows an authorized client to read all existing Schedules. 
	*Required parameters and their descriptions are provided in 
	*the parameters list below. 	
*/

	@Override
	public ServiceResponse<Schedule> readAllSchedule(Integer offset, Integer limit, String sortColumn, String sortDirection) {
		NamedValues values = new NamedValues();
		values.put("offset",offset);
		values.put("limit",limit);
		values.put("sortColumn",sortColumn);
		values.put("sortDirection",sortDirection);
		return readAllScheduleBroker.execute(new ServiceRequest<NamedValues>(values));
	}

    @Override
   	public ServiceResponse<Schedule> readAllSchedulesByRegistryNodeId(Long registryNodeId) {
   		return readScheduleListByRegistryNodeBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
   	}

	/**
	*This action allows an authorized client to update an existing Schedule 
	*by providing all required data. Required parameters and their 
	*descriptions are provided in the parameters list below. 	
*/

	@Override
	public ServiceResponse<Schedule> updateSchedule(Long id, Schedule data) {
		return updateScheduleBroker.execute(new ServiceRequest<Schedule>(data));
	}
	/**
	*This action allows an authorized client to delete an existing Schedule 
	*by providing its id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

	@Override
	public ServiceResponse<Schedule> deleteSchedule(Long id) {
		return deleteScheduleBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(id)));
	}
	/**
	*This action allows an authorized client to search for existing Schedules 
	*by providing search terms and searchable parameters. Required 
	*parameters and their descriptions are provided in the parameters 
	*list below. 	
*/

	@Override
	public ServiceResponse<Schedule> searchSchedule(String terms, Integer offset, Integer limit, String sortColumn, String sortDirection) {
		NamedValues values = new NamedValues();
		values.put("terms",terms);
		values.put("offset",offset);
		values.put("limit",limit);
		values.put("sortColumn",sortColumn);
		values.put("sortDirection",sortDirection);
		return searchScheduleBroker.execute(new ServiceRequest<NamedValues<Object>>(values));
	}
	
	@Override
	public ServiceResponse<Schedule> advancedSearchSchedule(String queryParameters, Integer offset, Integer limit, String sortOrders) {
		NamedValues values = new NamedValues();
		values.put("queryParameters",queryParameters);
		values.put("offset",offset);
		values.put("limit",limit);
		values.put("sortOrders",sortOrders);
		return advancedSearchScheduleBroker.execute(new ServiceRequest<NamedValues>(values));
	}

    @Override
    public ServiceResponse<Schedule> executeSchedule(Long id) {
        return executeScheduleBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(id)));
    }

    @Override
    public ServiceResponse<ScheduleHistory> readAllScheduleHistoryBySchedule(Long scheduleId, Integer offset, Integer limit, String sortColumn, String sortDirection) {
        NamedValues values = new NamedValues();
        values.put("scheduleId", scheduleId);
        values.put("offset", offset);
        values.put("limit", limit);
        values.put("sortColumn", sortColumn);
        values.put("sortDirection", sortDirection);
        return readAllScheduleHistoryByScheduleBroker.execute(new ServiceRequest<NamedValues>(values));
    }

    @Override
    public ServiceResponse<Schedule> getScheduleProgressStatus(List<Long> scheduleIds) {
        NamedValues<List<Long>> values = new NamedValues<List<Long>>();
        values.put("scheduleIds", scheduleIds);
        return getScheduleProgressStatusBroker.execute(new ServiceRequest<NamedValues<List<Long>>>(values));
    }



}
