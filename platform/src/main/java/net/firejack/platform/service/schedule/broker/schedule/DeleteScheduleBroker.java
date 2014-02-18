package net.firejack.platform.service.schedule.broker.schedule;

import net.firejack.platform.core.broker.DeleteLookupModelBroker;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*This action allows an authorized client to delete an existing Schedule 
	*by providing its id. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

@TrackDetails
@Component
public class DeleteScheduleBroker extends DeleteLookupModelBroker<ScheduleModel> {

	@Autowired
	private IScheduleStore store;

	@Override
	protected String getSuccessMessage() {
		return "Schedule item has deleted successfully";
	}

    @Override
    protected IStore<ScheduleModel, Long> getStore() {
        return store;
    }

    @Override
	protected void delete(Long id) {
        store.deleteById(id);
	}
}