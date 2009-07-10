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
    onFocus : function(){
        Ext.get(Ext.isIE ? document.body : document).on("mousedown", this.triggerBlur, this, {delay: 10});
        Aurora.TriggerField.superclass.onFocus.call(this);
    },
    triggerBlur : function(e){
    	if(!this.wrap.contains(e.target)){
            if(this.isExpanded()){
	    		this.popup.hide();
	    	}
	    	Ext.get(Ext.isIE ? document.body : document).un("mousedown", this.triggerBlur, this);
        }
    },
    onTriggerClick : function(){
    	this.el.focus();
    	if(this.isExpanded()){
    		this.popup.hide();
    	}else{
    		this.popup.show();
    	}
    }
});