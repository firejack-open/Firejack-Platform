Ext.define('OPF.core.component.form.SubLabelable', {

    initSubLabelable: function() {
        var fieldLabel = '';
        this.fieldOrgLabel = this.fieldLabel;
        if (OPF.isNotBlank(this.fieldLabel)) {
            fieldLabel += '<span class="main-label">' + this.fieldLabel + this.labelSeparator + '</span>';
        }
        if (OPF.isNotBlank(this.subFieldLabel)) {
            fieldLabel += '<span class="sub-label">' + this.subFieldLabel + '</span>';
        }
        this.fieldLabel = fieldLabel;
        this.labelSeparator = '';
    }

});

