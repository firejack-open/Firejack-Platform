package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Class represents value object for a process custom field with Date value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomDateFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = -2398631940885560410L;

	/** Constructor without the arguments */
	public ProcessCustomDateFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       Date value of the process custom field
	 */
	public ProcessCustomDateFieldVO(String fieldLookup, Date value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return Date value of the process custom field
	 */
	@XmlElement(name = "dateValue")
	public Date getValue() {
		return (Date) value;
	}

	/**
	 * Sets the value
	 *
	 * @param value Date value of the process custom field
	 */
	public void setValue(Date value) {
		this.value = value;
	}

}
