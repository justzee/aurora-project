Aurora.TextField = Ext.extend(Aurora.Field,{
	constructor: function(config) {
        Aurora.TextField.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	Aurora.TextField.superclass.initComponent.call(this, config);    	
    },
    initEvents : function(){
    	Aurora.TextField.superclass.initEvents.call(this);    	
    },
    onChange : function(e){
    	this.setValue(this.getValue());
    }
})