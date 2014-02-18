package net.firejack.platform.api.schedule.domain;

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.DefaultValue;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

@Component
@XmlRootElement
@RuleSource("OPF.schedule.Schedule")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Schedule extends Lookup {
	private static final long serialVersionUID = -9075956443143491695L;

	@Property
	private String cronExpression;

	@Property
	private String emailFailure;

	@Property
	private Action action;

    @Property
    private boolean active;

    private String message;
    private Integer percent;

	@NotBlank
	@Length(maxLength = 255)
	@DefaultValue("0 0 0 * * ?")
	public String getCronExpression() {
		return this.cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Length(maxLength = 255)
	public String getEmailFailure() {
		return this.emailFailure;
	}

	public void setEmailFailure(String emailFailure) {
		this.emailFailure = emailFailure;
	}

    @NotNull
	public Action getAction() {
		return this.action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}