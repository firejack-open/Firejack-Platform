package net.firejack.platform.api.process.domain;

import javax.xml.bind.annotation.XmlElement;

public class ProcessFieldCaseBooleanValue extends ProcessFieldCaseValue {

    @Override
    public Boolean getValue() {
        return (Boolean)super.getValue();
    }

    @XmlElement(name="booleanValue")
    public void setValue(Boolean value) {
        super.setValue(value);
    }

}
