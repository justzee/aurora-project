Aurora.ComboBox = Ext.extend(Aurora.TriggerField,{
	constructor: function(elId, config) {
        Aurora.ComboBox.superclass.constructor.call(this, elId, config);        
    },
    initComponent : function(){
    	Aurora.ComboBox.superclass.initComponent.call(this);
    },
    isExpanded : function(){
        return this.popup && this.popup.isVisible();
    },
    onTriggerClick: function(){
    	if(this.isExpanded()){
    		this.popup.hide();
    	}else{
    		this.popup.show();
    	}
    }
});