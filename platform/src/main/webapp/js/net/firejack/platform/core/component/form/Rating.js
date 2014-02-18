Ext.define('OPF.core.component.form.Rating', {
    extend: 'Ext.form.field.Base',
    alias : ['widget.opf-form-rating', 'widget.opf-rating'],

    cls: 'opf-rating-field',

    ratingPoints: 10,
    ratingImageUrl: null,
    ratingImageHeight: 32,
    ratingImageWidth: 32,
    ratingImagePadding: {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0
    },

    fieldSubTpl: [
        '<input id="{id}" type="hidden" {inputAttrTpl}',
            '<tpl if="name"> name="{name}"</tpl>',
            '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
        '/>',
        '<div class="rating-column" style="position: relative">',
            '<div class="rating-points" style="height: {ratingHeight}px; width: {ratingWidth}px; background: url({ratingImageUrl}) repeat-x 0 0;"></div>',
            '<div class="rating-active" style="position: absolute; top: 0; left: 0; height: {ratingHeight}px; width: {ratingActiveWidth}px; background: url({ratingImageUrl}) repeat scroll left ',
                '<tpl if="isSelectedRating">center<tpl else>bottom</tpl>',
                ' transparent;"></div>',
        '</div>',
        {
            disableFormats: true
        }
    ],

    mixins: {
        subLabelable: 'OPF.core.component.form.SubLabelable',
        errorHandler: 'OPF.core.component.form.ErrorHandler'
    },

    selectedRating: null,
    isSelectedRating: null,

    initComponent: function() {
        this.addEvents(
            'ratingclick'
        );

        this.initSubLabelable();
        this.callParent();

        this.resetTask = new Ext.util.DelayedTask(function() {
            this.resetSelectedRating(false);
        }, this);
    },

    getSubTplData: function() {
        var data = this.callParent();

        this.isSelectedRating = true;
        var value = Ext.isNumeric(this.getSelectedRating()) ? parseFloat(this.getSelectedRating()) : null;
        if (!value) {
            value = Ext.isNumeric(this.getValue()) ? parseFloat(this.getValue()) : 0;
            this.isSelectedRating = false;
        }

        var ratingActiveWidth = this.calculateWidth(value);

        var ratingData = {
            isSelectedRating: this.isSelectedRating,
            ratingHeight: this.ratingImageHeight,
            ratingWidth: this.ratingImageWidth * this.ratingPoints,
            ratingImageUrl: this.ratingImageUrl,
            ratingActiveWidth: ratingActiveWidth,
            tooltip: 'Rating: ' + Math.round(value * 100) / 100
        };

        return Ext.apply(data, ratingData);
    },

    getErrors: function(value) {
        return this.getValidatorErrors(value);
    },

    afterRender: function(container) {
        this.callParent(arguments);

        this.initRatingMouseEvents();
    },

    initRatingMouseEvents: function() {
        this.ratingPointsEl = this.getEl().down('.rating-points');
        this.ratingActiveEl = this.getEl().down('.rating-active');

        if (!this.isSelectedRating && !this.readOnly) {
            this.ratingPointsEl.on({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });

            this.ratingActiveEl.on({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });
        }
    },

    onClick: function() {
        if (this.fireEvent('ratingclick', this, this.getValue(), this.selectedRating) !== false) {
            this.oldValue = this.getValue();
            this.setValue(this.selectedRating);
            this.isSelectedRating = true;

            this.ratingPointsEl.un({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });

            this.ratingActiveEl.un({
                mouseover: this.onMouseOver,
                mousemove: this.onMouseOver,
                mouseout:  this.onMouseOut,
                click:     this.onClick,
                scope: this
            });
        }
    },

    onMouseOver: function(event, tag) {
        this.resetTask.cancel();
        var tagXY = Ext.fly(tag).getXY();
        var mouseXY = [event.browserEvent.clientX, event.browserEvent.clientY];
        var width = (mouseXY[0] - tagXY[0]);
        this.selectedRating = this.calculateValue(width);
    },

    onMouseOut: function() {
        this.resetTask.delay(100);
    },

    getSelectedRating: function() {
        return this.selectedRating;
    },

    setSelectedRating: function(value) {
        this.selectedRating = value;
        this.isSelectedRating = true;

        var ratingActiveWidth = this.calculateWidth(value);
        this.ratingActiveEl.setWidth(ratingActiveWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left center'
        });
    },

    refreshRating: function() {
        var value = Ext.isNumeric(this.getValue()) ? parseFloat(this.getValue()) : 0;
        var ratingActiveWidth = this.calculateWidth(value);
        this.ratingActiveEl.setWidth(ratingActiveWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left bottom'
        });
    },

    calculateValue: function(width) {
        var ratingWidth = this.ratingImageWidth * this.ratingPoints;
        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);

        var intPoints = Math.ceil(width / this.ratingImageWidth);
        var selectedPointsWidth = intPoints * this.ratingImageWidth;
        var piecePointWidth = width - selectedPointsWidth;
        if (piecePointWidth > this.ratingImagePadding.left) {
            selectedPointsWidth += this.ratingImageWidth;
            intPoints++;
        }
        this.ratingActiveEl.setWidth(selectedPointsWidth);
        this.ratingActiveEl.setStyle({
            'background-position': 'left center'
        });
        return intPoints;
    },

    calculateWidth: function(value) {
        var intValue = Math.floor(value);
        var decValue = value - Math.floor(value);

        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);
        return Math.round(this.ratingImageWidth * intValue + this.ratingImagePadding.left + pointWidth * decValue);
    },

    resetSelectedRating: function(force) {
        if (!this.isSelectedRating || force) {
            this.isSelectedRating = false;
            this.refreshRating();
        }
    },

    reset: function() {
        this.setValue(this.oldValue);
        this.resetSelectedRating(true);
        this.initRatingMouseEvents();
    }

});