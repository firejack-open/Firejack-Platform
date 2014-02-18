package net.firejack.platform.core.config.meta.element.schedule;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;

public class ScheduleElement extends PackageDescriptorElement<ScheduleModel> {

    private String actionRef;

    private String cronExpression;

    private String emailFailure;

    private Boolean active;

    public String getActionRef() {
        return actionRef;
    }

    public void setActionRef(String actionRef) {
        this.actionRef = actionRef;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getEmailFailure() {
        return emailFailure;
    }

    public void setEmailFailure(String emailFailure) {
        this.emailFailure = emailFailure;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Class<ScheduleModel> getEntityClass() {
        return ScheduleModel.class;
    }

}
