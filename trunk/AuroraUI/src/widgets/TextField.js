Aurora.TextField = Ext.extend(Aurora.Field,{
	constructor: function(elId, config) {
        config = config || {};
        Ext.apply(this, config);
        Aurora.TextField.superclass.constructor.call(this, elId, config);
        this.addEvents('keydown','keyup','keypress');
    },
    initEvents : function(){
    	Aurora.TextField.superclass.initEvents.call(this);    	
    }
})