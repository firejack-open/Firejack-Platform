package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.store.IStore;

import java.util.List;

public interface IActivityActionStore extends IStore<ActivityActionModel, Long> {

    List<ActivityActionModel> findActionsFromActivity(Long activityId);

}
