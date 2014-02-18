package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Class represents process field case value entity of Double type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.DOUBLE_CONST)
public class ProcessFieldCaseDoubleValue extends ProcessFieldCaseValue {

    private Double doubleValue;

    /**
     * Gets Double value
     * @return process field value for a case
     */
    public Double getDoubleValue() {
        return doubleValue;
    }

    /**
     * Sets Double value
     * @param doubleValue process field value for a case
     */
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    /**
     * Gets the value
     * @return Double value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getDoubleValue();
    }

    /**
     * Sets the value
     * @param value Double value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.doubleValue = (Double) value;
    }

}
