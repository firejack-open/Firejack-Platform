package net.firejack.platform.core.config.meta.construct;

import java.util.List;

public class WizardFormElement {

    private String displayName;
    private List<WizardFieldElement> fields;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<WizardFieldElement> getFields() {
        return fields;
    }

    public void setFields(List<WizardFieldElement> fields) {
        this.fields = fields;
    }
}
