package net.firejack.platform.service.schedule.broker.schedule;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
	*This action allows an authorized client to read an existing Schedule 
	*by providing the id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

@TrackDetails
@Component("readScheduleBroker")
public class ReadScheduleBroker extends ReadBroker<ScheduleModel, Schedule> {

	@Autowired
	private IScheduleStore store;

	@Override
	protected IStore<ScheduleModel, Long> getStore() {
		return store;
	}

}