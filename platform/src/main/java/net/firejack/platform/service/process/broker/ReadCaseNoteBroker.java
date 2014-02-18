package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseNote;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.process.ICaseNoteStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of retrieving a case note
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readCaseNoteBroker")
public class ReadCaseNoteBroker extends ReadBroker<CaseNoteModel, CaseNote> {

    @Autowired
    @Qualifier("caseNoteStore")
    private ICaseNoteStore caseNoteStore;

    @Override
    protected IStore<CaseNoteModel, Long> getStore() {
        return caseNoteStore;
    }

}
