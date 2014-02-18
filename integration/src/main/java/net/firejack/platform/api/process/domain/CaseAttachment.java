package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.NotBlank;
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
@RuleSource("OPF.process.CaseAttachment")
public class CaseAttachment extends BaseEntity {

    @Property
    private String filename;

    @Property
    private String name;

    @Property
    private String description;

    @Property
    private Case processCase;

    @Length(maxLength = 2048)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @NotBlank
    @Length(maxLength = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(maxLength = 3072)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Case getProcessCase() {
        return processCase;
    }

    public void setProcessCase(Case processCase) {
        this.processCase = processCase;
    }
    
}
