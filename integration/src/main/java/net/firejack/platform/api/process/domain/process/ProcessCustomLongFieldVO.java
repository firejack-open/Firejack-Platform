package net.firejack.platform.api.process.domain.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents value object for a process custom field with Long value.
 * It derives from <code>net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO</code>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProcessCustomLongFieldVO extends ProcessCustomFieldVO {

	private static final long serialVersionUID = 8404483269232017435L;

	/** Constructor without the arguments */
	public ProcessCustomLongFieldVO() {
	}

	/**
	 * Constructor setting the field lookup and value
	 *
	 * @param fieldLookup lookup of the process custom field
	 * @param value       Long value of the process custom field
	 */
	public ProcessCustomLongFieldVO(String fieldLookup, Long value) {
		this.setFieldLookup(fieldLookup);
		this.setValue(value);
	}

	/**
	 * Gets the value
	 *
	 * @return Long value of the process custom field
	 */
	@XmlElement(name = "longValue")
	public Long getValue() {
		return (Long) value;
	}

	/**
	 * Sets the value
	 *
	 * @param value Long value of the process custom field
	 */
	public void setValue(Long value) {
		this.value = value;
	}

}
