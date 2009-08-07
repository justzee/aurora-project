Aurora.DatePicker = Ext.extend(Aurora.TriggerField,{
	constructor: function(elId, config) {
		this.config = config;
        Aurora.DatePicker.superclass.constructor.call(this, elId, config);        
    },
    initComponent : function(){
    	Aurora.DatePicker.superclass.initComponent.call(this);
    	this.dateField = new Aurora.DateField(this.popup, this.config);
    	this.dateField.on("select", this.onSelect, this);
    },
    onSelect: function(dateField, date){
    	this.setValue(date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate())
    	this.collapse();
    }
});