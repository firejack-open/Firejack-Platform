package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.schedule.ScheduleHistoryModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.Paging;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleHistoryStore extends BaseStore<ScheduleHistoryModel, Long> implements IScheduleHistoryStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(ScheduleHistoryModel.class);
    }

    @Override
    @Transactional
    public void saveScheduleHistory(ScheduleHistoryModel scheduleHistoryModel) {
        saveOrUpdate(scheduleHistoryModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countBySchedule(Long scheduleId) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("schedule", "s");

        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("s.id", scheduleId));

        return count(criterions, aliases, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleHistoryModel> findBySchedule(Long scheduleId, Paging paging) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("schedule", "s");
        aliases.put("user", "u");

        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("s.id", scheduleId));

        List<String> fetchPaths = new ArrayList<String>();
        fetchPaths.add("user");

        return findAllWithFilter(criterions, aliases, null, fetchPaths, paging);
    }
}
