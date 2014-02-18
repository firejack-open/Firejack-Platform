package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WizardField extends AbstractDTO {

    @Property
    private Wizard wizard;
    @Property
    private WizardField form;
    @Property
    private Field field;
    @Property
    private Relationship relationship;
    @Property
    private String displayName;
    @Property
    private Boolean editable;
    @Property
    private Integer position;
    @Property
    private String defaultValue;

    private List<WizardField> fields;

    public Wizard getWizard() {
        return wizard;
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    public WizardField getForm() {
        return form;
    }

    public void setForm(WizardField form) {
        this.form = form;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<WizardField> getFields() {
        return fields;
    }

    public void setFields(List<WizardField> fields) {
        this.fields = fields;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
