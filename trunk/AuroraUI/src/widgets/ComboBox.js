Aurora.ComboBox = Ext.extend(Aurora.TriggerField,{
	constructor: function(elId, config) {
        Aurora.ComboBox.superclass.constructor.call(this, elId, config);        
    },
    initComponent : function(){
    	Aurora.ComboBox.superclass.initComponent.call(this);
    },    
    onTriggerClick: function(){
    	Aurora.ComboBox.superclass.onTriggerClick.call(this);
    	if(this.isExpanded()){
    		this.popup.hide();
    	}else{
    		this.popup.show();
    	}
    }
});