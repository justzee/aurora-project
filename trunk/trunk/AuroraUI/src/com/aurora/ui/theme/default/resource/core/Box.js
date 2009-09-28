Aurora.Box = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        Aurora.Box.superclass.constructor.call(this);
        this.id = config.id || Ext.id();		
        Aurora.cmps[this.id] = this;
		this.initComponent(config);
        this.initEvents();
        this.errors = [];
    },
    initComponent : function(config){ 
		config = config || {};
        Ext.apply(this, config);
        for(var i=0;i<this.cmps.length;i++){
    		var cmp = $(this.cmps[i]);
    		if(cmp){
	    		cmp.on('valid', this.onValid, this)
	    		cmp.on('invalid', this.onInvalid,this)
    		}
    	}
    },
    initEvents : function(){
//    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    onValid : function(cmp, record, name){
    	this.clearError(cmp.id);
    },
    onInvalid : function(cmp, record, name){
    	var error = record.errors[name];
    	if(error){
    		this.showError(cmp.id,error.message)
    	}
    },
    showError : function(id, msg){
    	Ext.fly(id+'_vmsg').update(msg)
    },
    clearError : function(id){
    	Ext.fly(id+'_vmsg').update('')
    },
    clearAllError : function(){
    	for(var i=0;i<this.errors.length;i++){
    		this.clearError(this.errors[i])
    	}
    }
});