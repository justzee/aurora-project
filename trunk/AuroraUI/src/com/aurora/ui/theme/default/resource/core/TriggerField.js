Aurora.TriggerField = Ext.extend(Aurora.TextField,{
	constructor: function(config) {
        Aurora.TriggerField.superclass.constructor.call(this, config);
    },
    initComponent : function(){
    	Aurora.TriggerField.superclass.initComponent.call(this);
    	this.trigger = this.wrap.child('div[atype=triggerfield.trigger]'); 
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
        Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this, {delay: 10});
        Aurora.TriggerField.superclass.onFocus.call(this);
    },
    onBlur : function(){
    	Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
    	Aurora.TriggerField.superclass.onBlur.call(this);
    },
    triggerBlur : function(e){
    	if(!this.wrap.contains(e.target)){
            if(this.isExpanded()){
	    		this.collapse();
	    	}	    	
        }
    },
    collapse : function(){
    	this.wrap.setStyle("z-index",20);
    	this.popup.hide();
    },
    expand : function(){
    	this.wrap.setStyle("z-index",25);
    	this.popup.show();
    },
    onTriggerClick : function(){
    	if(this.readOnly) return;
    	this.el.focus();
    	if(this.isExpanded()){
    		this.collapse();
    	}else{
    		this.expand();
    	}
    }
});