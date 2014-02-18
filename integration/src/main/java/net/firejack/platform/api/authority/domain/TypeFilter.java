package net.firejack.platform.api.authority.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.IdFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeFilter extends AbstractDTO {
	private static final long serialVersionUID = 4206124757492110009L;
	private String type;
    private IdFilter idFilter;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IdFilter getIdFilter() {
        return idFilter;
    }

    public void setIdFilter(IdFilter idFilter) {
        this.idFilter = idFilter;
    }
}