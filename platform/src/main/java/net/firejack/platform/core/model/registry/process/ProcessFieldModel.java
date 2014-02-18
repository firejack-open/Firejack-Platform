package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.UIDModel;
import net.firejack.platform.core.model.registry.ProcessFieldType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "opf_process_field")
public class ProcessFieldModel extends UIDModel {

    private ProcessModel process;
    private FieldModel field;
    private String name;
    private boolean global;
    private String valueType;
    private Integer orderPosition;
    private EntityModel registryNodeType;
    private String format;

    @Transient
    public String getValueColumn() {
        return ProcessFieldType.valueOf(getValueType()).getColumnName();
    }

    /**
     * Gets the process
     * @return - process for the processEntity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_process")
    @ForeignKey(name = "FK_PROCESS_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessModel getProcess() {
        return process;
    }

    /**
     * Sets the process
     * @param process - process for the processEntity
     */
    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    @ManyToOne(targetEntity = FieldModel.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registry_node_field")
    @ForeignKey(name = "FK_PROCESS_FIELD_REGISTRY_NODE_FIELD")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public FieldModel getField() {
        return field;
    }

    public void setField(FieldModel field) {
        this.field = field;
    }

    @Column(length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @Column(name = "order_position", nullable = false, columnDefinition = "INTEGER UNSIGNED DEFAULT 0")
    public Integer getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registry_node_type")
    @ForeignKey(name = "fk_process_field_registry_node_entity")
    public EntityModel getRegistryNodeType() {
        return registryNodeType;
    }

    public void setRegistryNodeType(EntityModel registryNodeType) {
        this.registryNodeType = registryNodeType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
}