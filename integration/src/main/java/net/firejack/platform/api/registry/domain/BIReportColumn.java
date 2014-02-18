package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.registry.model.BIReportLocation;
import net.firejack.platform.core.domain.TreeNode;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@Component("com.coolmovies.coolmovies.movies.domain.BIReportColumn")
@XmlRootElement(namespace = "com.coolmovies.coolmovies.movies.domain")
@XmlType(namespace = "com.coolmovies.coolmovies.movies.domain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class BIReportColumn extends TreeNode<BIReportColumn> {

    private String name;
    private Integer columnIndex;
    private BIReportLocation type;
    private Boolean expanded;
    private Integer unShift;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public BIReportLocation getType() {
        return type;
    }

    public void setType(BIReportLocation type) {
        this.type = type;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Integer getUnShift() {
        return unShift;
    }

    public void setUnShift(Integer unShift) {
        this.unShift = unShift;
    }
}
