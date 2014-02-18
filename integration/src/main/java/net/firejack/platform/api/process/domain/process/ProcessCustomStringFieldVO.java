package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents value object for a process custom field with String value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomStringFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = 3498830052910453562L;

	/** Constructor without the arguments */
	public ProcessCustomStringFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       String value of the process custom field
	 */
	public ProcessCustomStringFieldVO(String fieldLookup, String value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return String value of the process custom field
	 */
	@XmlElement(name = "stringValue")
	public String getValue() {
		return (String) value;
	}

	/**
	 * Gets the value
	 *
	 * @return String value of the process custom field
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
