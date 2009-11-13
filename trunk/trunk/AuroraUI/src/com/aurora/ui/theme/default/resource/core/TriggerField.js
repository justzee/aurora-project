Aurora.TriggerField = Ext.extend(Aurora.TextField,{
	constructor: function(config) {
        Aurora.TriggerField.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	Aurora.TriggerField.superclass.initComponent.call(this, config);
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
    setWidth: function(w){
		this.wrap.setStyle("width",(w+3)+"px");
		this.el.setStyle("width",(w-20)+"px");
	},
    onFocus : function(){
        Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this, {delay: 10});
        Aurora.TriggerField.superclass.onFocus.call(this);
        if(!this.isExpanded())this.expand();
    },
    onBlur : function(){
    	this.hasFocus = false;
        this.wrap.removeClass(this.focusCss);
        this.fireEvent("blur", this);
    },
	destroy : function(){
		if(this.isExpanded()){
    		this.collapse();
    	}
    	this.trigger.un('click',this.onTriggerClick, this)
    	delete this.trigger;
    	delete this.popup;
    	Aurora.TriggerField.superclass.destroy.call(this);
	},
    triggerBlur : function(e){
    	if(!this.wrap.contains(e.target)){
    		Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
            if(this.isExpanded()){
	    		this.collapse();
	    	}	    	
        }
    },
    setVisible : function(v){
    	Aurora.TriggerField.superclass.setVisible.call(this,v);
    	if(v == false && this.isExpanded()){
    		this.collapse();
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
    	if(this.readonly) return;
    	if(this.isExpanded()){
    		this.collapse();
    	}else{
	    	this.el.focus();
    		this.expand();
    	}
    }
});