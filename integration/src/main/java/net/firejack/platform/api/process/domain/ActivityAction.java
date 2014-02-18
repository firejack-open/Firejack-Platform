package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
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

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@RuleSource("OPF.process.ActivityAction")
public class ActivityAction extends BaseEntity {
    private static final long serialVersionUID = 3439798256177992840L;

    @Property
    private String name;
    @Property
    private String description;
    @Property
    private Status status;
    @Property
    private Activity activityFrom;
    @Property
    private Activity activityTo;

    @NotBlank
    @Length(maxLength = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank
    @Length(maxLength = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    public Activity getActivityFrom() {
        return activityFrom;
    }

    public void setActivityFrom(Activity activityFrom) {
        this.activityFrom = activityFrom;
    }

    @NotNull
    public Activity getActivityTo() {
        return activityTo;
    }

    public void setActivityTo(Activity activityTo) {
        this.activityTo = activityTo;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}