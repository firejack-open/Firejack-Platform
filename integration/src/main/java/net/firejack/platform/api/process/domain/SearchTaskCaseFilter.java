package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchTaskCaseFilter extends AbstractDTO {
    private static final long serialVersionUID = 3312348791764929923L;

    private Long assigneeId;
    private Long processId;
    private Long activityId;
    private Long statusId;
    private Boolean active;
    private Date startDate;
    private Date endDate;
    private String lookupPrefix;
    private String description;
    private List<SearchCaseCustomField> customFields;

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLookupPrefix() {
        return lookupPrefix;
    }

    public void setLookupPrefix(String lookupPrefix) {
        this.lookupPrefix = lookupPrefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SearchCaseCustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<SearchCaseCustomField> customFields) {
        this.customFields = customFields;
    }

}
