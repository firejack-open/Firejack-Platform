package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
import net.firejack.platform.core.store.IStore;

import java.util.List;

public interface IActivityFieldStore extends IStore<ActivityFieldModel, Long> {

    public List<ActivityFieldModel> findActivityFieldsByActivityId(Long activityId);

}
