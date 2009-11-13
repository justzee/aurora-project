Aurora.DatePicker = Ext.extend(Aurora.TriggerField,{
	constructor: function(config) {
        Aurora.DatePicker.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	Aurora.DatePicker.superclass.initComponent.call(this,config);
    	if(!this.dateField){
    		var cfg = {id:this.id+'_df',container:this.popup}
	    	this.dateField = new Aurora.DateField(cfg);
	    	this.dateField.on("select", this.onSelect, this);
    	}
    },
    onSelect: function(dateField, date){
    	this.setValue(date)
    	this.collapse();
    },
    setValue:function(v,silent){
        Aurora.DatePicker.superclass.setValue.call(this, v, silent);
        this.dateField.selectDay = this.getValue();
        this.dateField.predraw(this.getValue());
	},
    formatValue : function(date){
    	if(date instanceof Date) {
    		return Aurora.formateDate(date);
    	}else{
    		return date;
    	}
    },
    destroy : function(){
    	Aurora.DatePicker.superclass.destroy.call(this);
	}
});