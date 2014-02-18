package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.store.BaseStore;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("activityActionStore")
public class ActivityActionStore extends BaseStore<ActivityActionModel, Long> implements IActivityActionStore {

    @PostConstruct
    public void init() {
        setClazz(ActivityActionModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityActionModel> findActionsFromActivity(Long activityId) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("activityFrom", "from");
        aliases.put("activityTo", "to");

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        criterions.add(Restrictions.eq("from.id", activityId));

        return search(criterions, aliases, null);
    }

}
