Aurora.TextField = Ext.extend(Aurora.Field,{
	constructor: function(elId, config) {
        Aurora.TextField.superclass.constructor.call(this, elId, config);        
    },
    initComponent : function(){
    	Aurora.TextField.superclass.initComponent.call(this);    	
    },
    initEvents : function(){
    	Aurora.TextField.superclass.initEvents.call(this);    	
    }
})