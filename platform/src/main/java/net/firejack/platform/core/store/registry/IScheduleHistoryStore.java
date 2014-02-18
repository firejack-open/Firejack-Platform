package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.schedule.ScheduleHistoryModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.utils.Paging;

import java.util.List;

public interface IScheduleHistoryStore extends IStore<ScheduleHistoryModel, Long> {

    void saveScheduleHistory(ScheduleHistoryModel scheduleHistoryModel);

    Integer countBySchedule(Long scheduleId);

    List<ScheduleHistoryModel> findBySchedule(Long scheduleId, Paging paging);

}
