package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents value object for a process custom field with Integer value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomIntegerFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = -6770074781764669842L;

	/** Constructor without the arguments */
	public ProcessCustomIntegerFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       Integer value of the process custom field
	 */
	public ProcessCustomIntegerFieldVO(String fieldLookup, Integer value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return Integer value of the process custom field
	 */
	@XmlElement(name = "integerValue")
	public Integer getValue() {
		return (Integer) value;
	}

	/**
	 * Sets the value
	 *
	 * @param value Integer value of the process custom field
	 */
	public void setValue(Integer value) {
		this.value = value;
	}

}
