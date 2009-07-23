Aurora.TriggerField = Ext.extend(Aurora.TextField,{
	constructor: function(elId, config) {
        Aurora.TriggerField.superclass.constructor.call(this, elId, config);
    },
    initComponent : function(){
    	Aurora.TriggerField.superclass.initComponent.call(this);
    	this.trigger = this.wrap.child('button[atype=triggerfield.trigger]'); 
    	this.popup = this.wrap.child('div[atype=triggerfield.popup]'); 
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
	    		this.collapse();
	    	}
	    	Ext.get(Ext.isIE ? document.body : document).un("mousedown", this.triggerBlur, this);
        }
    },
    collapse : function(){
    	this.wrap.setStyle("z-index",20);
    	this.popup.hide();
    },
    expand : function(){
    	this.wrap.setStyle("z-index",10000);
    	this.popup.show();
    },
    onTriggerClick : function(){
    	this.el.focus();
    	if(this.isExpanded()){
    		this.collapse();
    	}else{
    		this.expand();
    	}
    }
});