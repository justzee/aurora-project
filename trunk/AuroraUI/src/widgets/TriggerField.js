Aurora.TriggerField = Ext.extend(Aurora.TextField,{
	constructor: function(elId, config) {
        Aurora.TriggerField.superclass.constructor.call(this, elId, config);
    },
    initComponent : function(){
    	Aurora.TriggerField.superclass.initComponent.call(this);
    	this.trigger = this.wrap.first('button.item-trigger'); 
    	this.popup = this.wrap.first('div.item-popup'); 
    },
    initEvents : function(){
    	Aurora.TriggerField.superclass.initEvents.call(this);    
    	this.trigger.on('click',this.onTriggerClick, this, {preventDefault:true})
    },
    onTriggerClick : Ext.emptyFn
});