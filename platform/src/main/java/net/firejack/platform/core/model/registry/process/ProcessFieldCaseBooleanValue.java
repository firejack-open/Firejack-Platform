package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Class represents process field case value entity of Boolean type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.BOOLEAN_CONST)
public class ProcessFieldCaseBooleanValue extends ProcessFieldCaseValue {

    private Boolean booleanValue;

    /**
     * Gets Boolean value
     * @return process field value for a case
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * Sets Boolean value
     * @param booleanValue process field value for a case
     */
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * Gets the value
     * @return Boolean value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getBooleanValue();
    }

    /**
     * Sets the value
     * @param value Boolean value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.booleanValue = (Boolean) value;
    }
    
}
