package net.firejack.platform.api.process.domain;

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
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
@RuleSource("OPF.process.CaseNote")
public class CaseNote extends BaseEntity {

    @Property
    private Case processCase;

    @Property(name = "taskModel")
    private Task task;

    @Property
    private User user;

    @Property
    private String text;

    public Case getProcessCase() {
        return processCase;
    }

    public void setProcessCase(Case processCase) {
        this.processCase = processCase;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NotBlank
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}