package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents value object for a process custom field with Boolean value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomBooleanFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = -6639964884778443651L;

	/** Constructor without the arguments */
	public ProcessCustomBooleanFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       Boolean value of the process custom field
	 */
	public ProcessCustomBooleanFieldVO(String fieldLookup, Boolean value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return Boolean value of the process custom field
	 */
	@XmlElement(name = "booleanValue")
	public Boolean getValue() {
		return (Boolean) value;
	}

	/**
	 * Sets the value
	 *
	 * @param value Boolean value of the process custom field
	 */
	public void setValue(Boolean value) {
		this.value = value;
	}

}
