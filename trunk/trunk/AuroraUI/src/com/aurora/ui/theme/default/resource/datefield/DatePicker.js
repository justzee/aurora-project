Aurora.DatePicker = Ext.extend(Aurora.TriggerField,{
	constructor: function(config) {
        Aurora.DatePicker.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	Aurora.DatePicker.superclass.initComponent.call(this,config);
    	this.dateField = new Aurora.DateField(this.popup, config);
    	this.dateField.on("select", this.onSelect, this);
    },
    onSelect: function(dateField, date){
    	this.setValue(date)
    	this.collapse();
    },
    formatValue : function(date){
    	if(date instanceof Date) {
    		return date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate()
    	}else{
    		return date;
    	}
    }
});