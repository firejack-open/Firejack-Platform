package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
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

@Component("activityFieldStore")
@SuppressWarnings("unused")
public class ActivityFieldStore extends BaseStore<ActivityFieldModel, Long> implements IActivityFieldStore {

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(ActivityFieldModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityFieldModel> findActivityFieldsByActivityId(Long activityId) {
        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("activity", "actv");
        aliases.put("field", "fld");
        aliases.put("relationship", "rsp");
        aliases.put("rsp.sourceEntity", "se");
        aliases.put("rsp.targetEntity", "te");

        LinkedList<Criterion> criterions = new LinkedList<Criterion>();
        criterions.add(Restrictions.eq("actv.id", activityId));

        return search(criterions, aliases, null, false, true);
    }

}
