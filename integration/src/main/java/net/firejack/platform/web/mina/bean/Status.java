package net.firejack.platform.web.mina.bean;

import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@Component("progressStatus")
@XmlRootElement(namespace = "net.firejack.platform.web.mina.bean")
@XmlType(namespace = "net.firejack.platform.web.mina.bean")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Status extends AbstractDTO {
	private static final long serialVersionUID = 1671148026827850991L;

    private String dtoName = this.getClass().getName();
	private String title;
	private StatusType type;
	private int percent;
	private int weight;
    private LogLevel logLevel;
    private boolean showLogs = false;

	public Status() {
	}

	/**
	 * @param title
	 * @param percent
	 * @param type
	 */
	public Status(String title, int percent, StatusType type) {
        this(title, percent, 0,type, LogLevel.NONE, false);
	}

    /**
	 * @param title
	 * @param percent
	 * @param type
	 */
	public Status(String title, int percent, StatusType type, boolean showLogs) {
        this(title, percent, 0,type, LogLevel.NONE, showLogs);
	}

	/**
	 * @param title
	 * @param percent
	 * @param weight
	 * @param type
	 */
	public Status(String title, int percent, int weight, StatusType type) {
        this(title, percent, weight,type, LogLevel.NONE, false);
	}

    /**
   	 * @param title
   	 * @param percent
   	 * @param weight
   	 * @param type
   	 * @param logLevel
   	 */
   	public Status(String title, int percent, int weight, StatusType type, LogLevel logLevel) {
        this(title, percent, weight, type, logLevel, false);
   	}

    public Status(String title, int percent, int weight, StatusType type, LogLevel logLevel, boolean showLogs) {
        this.title = title;
        this.percent = percent;
        this.weight = weight;
        this.type = type;
        this.logLevel = logLevel;
        this.showLogs = showLogs;
    }

    public String getDtoName() {
        return dtoName;
    }

    public void setDtoName(String dtoName) {
        this.dtoName = dtoName;
    }

    public Status(StatusType type) {
		this.type = type;
	}

	/** @return  */
	public String getTitle() {
		return title;
	}

	/** @param title  */
	public void setTitle(String title) {
		this.title = title;
	}

	public StatusType getType() {
		return type;
	}

	public void setType(StatusType type) {
		this.type = type;
	}

	/** @return  */
	public int getPercent() {
		return percent;
	}

	/** @param percent  */
	public void setPercent(byte percent) {
		this.percent = percent;
	}

	/** @return  */
	public int getWeight() {
		return weight;
	}

	/** @param weight  */
	public void setWeight(int weight) {
		this.weight = weight;
	}

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean showLogs) {
        this.showLogs = showLogs;
    }

    @Override
    public String toString() {
        return "Status{" +
                "dtoName='" + dtoName + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", percent=" + percent +
                ", weight=" + weight +
                ", logLevel=" + logLevel +
                ", showLogs=" + showLogs +
                '}';
    }
}
