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
    isExpanded : function(){
        return this.popup && this.popup.isVisible();
    },
    onBlur : function(e){
    	if(this.isExpanded()){
    		this.popup.hide();
    	}
    	Aurora.TriggerField.superclass.onBlur.call(this, e);
    },
    onTriggerClick : function(){
    	this.el.focus();
    }
});