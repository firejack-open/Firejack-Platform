package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.core.model.BaseEntityModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "opf_reference_object")
public class ReferenceObjectModel extends BaseEntityModel {

    private String heading;
    private String subHeading;
    private String description;

    @Column(length = 1024)
    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    @Column(name = "subheading", length = 1024)
    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    @Column(length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
