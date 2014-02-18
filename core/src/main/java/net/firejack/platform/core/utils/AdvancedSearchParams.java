package net.firejack.platform.core.utils;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class AdvancedSearchParams extends AbstractDTO {

    @XmlElementRef
    @XmlElementWrapper(name = "searchQueries")
    private List<AdvancedSearchQueryOperand> searchQueries;
    @XmlElementRef
    @XmlElementWrapper(name = "sortFields")
    private List<SortField> sortFields;

    public List<AdvancedSearchQueryOperand> getSearchQueries() {
        return searchQueries;
    }

    public void setSearchQueries(List<AdvancedSearchQueryOperand> searchQueries) {
        this.searchQueries = searchQueries;
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

}