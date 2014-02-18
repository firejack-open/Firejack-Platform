package net.firejack.platform.core.model.registry.schedule;


import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "opf_schedule_history")
public class ScheduleHistoryModel extends BaseEntityModel {

    private Date startTime;

    private Date endTime;

    private ScheduleModel schedule;

    private BaseUserModel user;

    private boolean success;

    private String message;


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_schedule")
    @ForeignKey(name = "fk_schedule_history_schedule")
    public ScheduleModel getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleModel schedule) {
        this.schedule = schedule;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @ForeignKey(name = "fk_schedule_history_user")
    public BaseUserModel getUser() {
        return user;
    }

    public void setUser(BaseUserModel user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Column(length = 1024)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
