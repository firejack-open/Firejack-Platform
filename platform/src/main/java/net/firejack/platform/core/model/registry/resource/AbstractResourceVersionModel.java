/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.model.registry.resource;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;


@javax.persistence.Entity
@Table(name = "opf_resource_version",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"version", "culture", "id_resource"})
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractResourceVersionModel<R extends AbstractResourceModel> extends BaseEntityModel {

    private static final long serialVersionUID = 5032302221461336952L;
    private R resource;
    private Integer version;
    private Cultures culture;
    private ResourceStatus status;
    private Date updated;

    /**
     * @return
     */
    @ManyToOne(targetEntity = AbstractResourceModel.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resource")
    @ForeignKey(name = "fk_resource_version_resource")
    public R getResource() {
        return resource;
    }

    /**
     * @param resource
     */
    public void setResource(R resource) {
        this.resource = resource;
    }

    /**
     * @return
     */
    @Column(nullable = false)
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return
     */
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    public Cultures getCulture() {
        return culture;
    }

    /**
     * @param culture
     */
    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    /**
     * @return
     */
    @Column(nullable = false)
    @Enumerated
    public ResourceStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    /**
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Transient
    public String getResourceStoredFileName() {
        return (new StringBuilder(String.valueOf(this.getId())))
                .append('_').append(this.getVersion()).append('_')
                .append(this.getCulture().name()).toString();
    }

}
