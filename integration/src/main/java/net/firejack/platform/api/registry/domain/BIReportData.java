package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@Component("com.coolmovies.coolmovies.movies.domain.BIReport")
@XmlRootElement(namespace = "com.coolmovies.coolmovies.movies.domain")
@XmlType(namespace = "com.coolmovies.coolmovies.movies.domain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class BIReportData extends AbstractDTO {
    private static final long serialVersionUID = 5995637513338389270L;

    private List<BIReportColumn> columns;
    private List<BIReportRow> rows;
    private String filter;
    private Integer countOfLevels;

    public List<BIReportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<BIReportColumn> columns) {
        this.columns = columns;
    }

    public List<BIReportRow> getRows() {
        return rows;
    }

    public void setRows(List<BIReportRow> rows) {
        this.rows = rows;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getCountOfLevels() {
        return countOfLevels;
    }

    public void setCountOfLevels(Integer countOfLevels) {
        this.countOfLevels = countOfLevels;
    }
}
