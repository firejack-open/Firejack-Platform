package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.ProcessFieldCaseValue;
import net.firejack.platform.core.store.BaseStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class provides access to process fields values for cases
 */
@Component("processFieldCaseValueStore")
public class ProcessFieldCaseValueStore extends BaseStore<ProcessFieldCaseValue, Long> implements IProcessFieldCaseValueStore {

    /**
     * Generates and executes an HQL query for finding a value of a given field for a given case
     * @see IProcessFieldCaseValueStore#findByFieldAndCase(java.lang.Long, java.lang.Long)
     * @param fieldId - ID of the field whose value is being retrieved
     * @param caseId - ID of the case for which the value is being retrieved
     * @return process field value for the case
     */
    @Override
    @Transactional
    public ProcessFieldCaseValue findByFieldAndCase(Long fieldId, Long caseId) {
        String query = "FROM ProcessFieldCaseValue pfcv WHERE pfcv.case.id = :caseId AND pfcv.processField.id = :fieldId";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("fieldId", fieldId);
        queryParams.put("caseId", caseId);
        List<ProcessFieldCaseValue> processFieldCaseValues = findByQuery(null, null, query, queryParams, null);
        if (processFieldCaseValues.isEmpty()) {
            return null;
        } 
        return processFieldCaseValues.get(0);
    }
    
}
