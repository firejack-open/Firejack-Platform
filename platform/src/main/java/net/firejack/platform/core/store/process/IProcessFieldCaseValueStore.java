package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ProcessFieldCaseValue;
import net.firejack.platform.core.store.IStore;

/**
 * Interface provides access to process fields values for cases
 */
public interface IProcessFieldCaseValueStore extends IStore<ProcessFieldCaseValue, Long> {

    /**
     * Finds a value of a specified field for a given case
     * @param fieldId - ID of the field whose value is being retrieved
     * @param caseId - ID of the case for which the value is being retrieved
     * @return process field value for the case
     */
    ProcessFieldCaseValue findByFieldAndCase(Long fieldId, Long caseId) ;
    
}
