package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.annotation.NotNull;
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
public class PackageAction extends AbstractDTO {
	private static final long serialVersionUID = 7294894019135571493L;

	private Long id;
    private List<DatabaseAction> databaseActions;

    public PackageAction() {
    }

    public PackageAction(Long id) {
        this.id = id;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DatabaseAction> getDatabaseActions() {
        return databaseActions;
    }

    public void setDatabaseActions(List<DatabaseAction> databaseActions) {
        this.databaseActions = databaseActions;
    }
}
