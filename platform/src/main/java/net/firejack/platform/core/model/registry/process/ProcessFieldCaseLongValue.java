package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Class represents process field case value entity of Long type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.LONG_CONST)
public class ProcessFieldCaseLongValue extends ProcessFieldCaseValue {

    private Long longValue;

    /**
     * Gets Long value
     * @return process field value for a case
     */
    public Long getLongValue() {
        return longValue;
    }

    /**
     * Sets Long value
     * @param longValue process field value for a case
     */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    /**
     * Gets the value
     * @return Long value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getLongValue();
    }

    /**
     * Sets the value
     * @param value Long value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.longValue = (Long) value;
    }

}
