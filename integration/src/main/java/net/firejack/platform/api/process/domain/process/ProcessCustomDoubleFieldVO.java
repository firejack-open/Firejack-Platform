package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents value object for a process custom field with Double value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomDoubleFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = 4058606682425887077L;

	/** Constructor without the arguments */
	public ProcessCustomDoubleFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       Double value of the process custom field
	 */
	public ProcessCustomDoubleFieldVO(String fieldLookup, Double value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return Double value of the process custom field
	 */
	@XmlElement(name = "doubleValue")
	public Double getValue() {
		return (Double) value;
	}

	/**
	 * Sets the value
	 *
	 * @param value Double value of the process custom field
	 */
	public void setValue(Double value) {
		this.value = value;
	}

}