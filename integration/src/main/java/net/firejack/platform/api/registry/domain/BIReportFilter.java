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

@Component("com.coolmovies.coolmovies.movies.domain.BiReportFilter")
@XmlRootElement(namespace = "com.coolmovies.coolmovies.movies.domain")
@XmlType(namespace = "com.coolmovies.coolmovies.movies.domain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class BIReportFilter extends AbstractDTO {

    private String name;
    private List<String> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
