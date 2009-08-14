Aurora.DatePicker = Ext.extend(Aurora.TriggerField,{
	constructor: function(config) {
		this.config = config;
        Aurora.DatePicker.superclass.constructor.call(this, config);        
    },
    initComponent : function(){
    	Aurora.DatePicker.superclass.initComponent.call(this);
    	this.dateField = new Aurora.DateField(this.popup, this.config);
    	this.dateField.on("select", this.onSelect, this);
    },
    onSelect: function(dateField, date){
    	this.setValue(date)
    	this.collapse();
    },
    formatValue : function(date){
    	return date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate()
    }
});