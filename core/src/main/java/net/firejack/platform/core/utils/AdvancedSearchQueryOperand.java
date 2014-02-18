package net.firejack.platform.core.utils;

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
@XmlAccessorType(XmlAccessType.FIELD)
public class AdvancedSearchQueryOperand extends AbstractDTO {

    //private List<SearchQuery> criteriaList;
    private List<String> criteriaList;

    public List<String> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<String> criteriaList) {
        this.criteriaList = criteriaList;
    }
}