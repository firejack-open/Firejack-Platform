package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Class represents process field case value entity of Date type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.DATE_CONST)
public class ProcessFieldCaseDateValue extends ProcessFieldCaseValue {

    private Date dateValue;

     /**
     * Gets Date value
     * @return process field value for a case
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * Sets Date value
     * @param dateValue process field value for a case
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Gets the value
     * @return Date value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getDateValue();
    }

    /**
     * Sets the value
     * @param value Date value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.dateValue = (Date) value;
    }

}
