package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.TreeNode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("com.coolmovies.coolmovies.movies.domain.BIReportRow")
@XmlRootElement(namespace = "com.coolmovies.coolmovies.movies.domain")
@XmlType(namespace = "com.coolmovies.coolmovies.movies.domain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BIReportRow extends TreeNode<BIReportRow> {
    private static final long serialVersionUID = 4621765704945578914L;

    private String name;
    private boolean expanded;
    private boolean leaf;
    private Integer depth;
    private Integer columnIndex;
    private Map<Integer, Double> columnIndexValues;

    private Map<String, BIReportRow> biReportRowMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Map<Integer, Double> getColumnIndexValues() {
        return columnIndexValues;
    }

    public void setColumnIndexValues(Map<Integer, Double> columnIndexValues) {
        this.columnIndexValues = columnIndexValues;
    }

    @Override
    public List<BIReportRow> getChildren() {
        return new ArrayList<BIReportRow>(biReportRowMap.values());
    }

    @XmlTransient
    @JsonIgnore
    public Map<String, BIReportRow> getBiReportRowMap() {
        return biReportRowMap;
    }

    public void setBiReportRowMap(Map<String, BIReportRow> biReportRowMap) {
        this.biReportRowMap = biReportRowMap;
    }
}
