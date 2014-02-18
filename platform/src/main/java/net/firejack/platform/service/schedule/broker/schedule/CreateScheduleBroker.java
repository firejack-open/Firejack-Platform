package net.firejack.platform.service.schedule.broker.schedule;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

@TrackDetails
@Component("createScheduleBroker")
public class CreateScheduleBroker extends OPFSaveBroker<ScheduleModel, Schedule, Schedule> {

    @Autowired
	private IScheduleStore store;


    @Override
    protected ScheduleModel convertToEntity(Schedule dto) {
        return factory.convertFrom(ScheduleModel.class, dto);
    }

    @Override
    protected Schedule convertToModel(ScheduleModel model) {
        return factory.convertTo(Schedule.class, model);
    }

    @Override
    protected void save(ScheduleModel scheduleModel) throws Exception {
        store.save(scheduleModel);
    }
}