package net.firejack.platform.core.config.translate;

import net.firejack.platform.core.config.meta.diff.IElementDiffInfoContainer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Scope("prototype")
@Component("activatePackageVersionTranslator")
public class ActivatePackageVersionTranslator extends XmlToRegistryTranslator {

    @Override
    protected void beforeTranslate(
            IElementDiffInfoContainer diffContainer, StatusProviderTranslationResult resultState) {
        super.beforeTranslate(diffContainer, resultState);
        //handle deletion of all sub domains
    }

}