package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.domain.ReferenceObjectModel;
import net.firejack.platform.core.store.BaseStore;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ReferenceObjectStore extends BaseStore<ReferenceObjectModel, Long> implements IReferenceObjectStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(ReferenceObjectModel.class);
    }

}

