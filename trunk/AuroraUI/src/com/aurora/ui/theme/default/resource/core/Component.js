Aurora.Component = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        Aurora.Component.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        if(Aurora.cmps[this.id] != null) {
        	alert("错误: ID为' " + this.id +" '的组件已经存在!");
        	return;
        }
        Aurora.cmps[this.id] = this;		
		this.initConfig=config;
		this.initComponent(config);
        this.initEvents();
    }, 
    initComponent : function(config){ 
		config = config || {};
        Ext.apply(this, config);
        this.wrap = Ext.get(this.id);
    },
    initEvents : function(){
    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    bind : function(ds, name){
    	if(this.binder) {
    		var bds = this.binder.ds;
    		bds.un('metachange', this.onRefresh, this);
	    	bds.un('create', this.onCreate, this);
	    	bds.un('load', this.onRefresh, this);
	    	bds.un('valid', this.onValid, this);
	    	bds.un('remove', this.onRemove, this);
	    	bds.un('clear', this.onClear, this);
	    	bds.un('update', this.onUpdate, this);
	    	bds.un('fieldchange', this.onFieldChange, this);
	    	bds.un('indexchange', this.onRefresh, this);
    	}
    	this.binder = {
    		ds: ds,
    		name:name
    	}
    	this.record = ds.getCurrentRecord();
    	var field =  ds.fields[this.binder.name];
    	if(field) {
			var config={};
			Ext.apply(config,this.initConfig);
			Ext.apply(config, field.pro);
			delete config.name;
			delete config.type;
			this.initComponent(config);
			
    	}
    	ds.on('metachange', this.onRefresh, this);
    	ds.on('create', this.onCreate, this);
    	ds.on('load', this.onRefresh, this);
    	ds.on('valid', this.onValid, this);
    	ds.on('remove', this.onRemove, this);
    	ds.on('clear', this.onClear, this);
    	ds.on('update', this.onUpdate, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onRefresh, this);
    },
    onRemove : function(ds, record){
    	if(this.binder.ds == ds && this.record == record){
    		this.clearValue();
    	}
    },
    onCreate : function(ds){
    	this.clearInvalid();
    	this.record = ds.getCurrentRecord();
    	this.setValue('',true);
    	this.fireEvent('valid', this, this.record, this.binder.name)
    },
    onRefresh : function(ds){
    	
    	this.clearInvalid();
		this.record = ds.getCurrentRecord();
		
		if(this.record) {
			var value = this.record.get(this.binder.name);			
			var field = this.record.getMeta().getField(this.binder.name);		
			var config={};
			Ext.apply(config,this.initConfig);		
			Ext.apply(config, field.snap);		
			this.initComponent(config);
			if(this.value == value) return;
			this.setValue(value,true);
		}else{
			this.setValue('',true);		
		}
//    	this.fireEvent('valid', this, this.record, this.binder.name)
    },
    onValid : function(ds, record, name, valid){
    	if(this.binder.ds == ds && this.binder.name == name && this.record == record){
	    	if(valid){
	    		this.fireEvent('valid', this, this.record, this.binder.name)
    			this.clearInvalid();
	    	}else{
	    		this.fireEvent('invalid', this, this.record, this.binder.name);
	    		this.markInvalid();
	    	}
    	}    	
    },
    onUpdate : function(ds, record, name,value){
    	if(this.binder.ds == ds && this.binder.name == name){
	    	this.setValue(value, true);
    	}
    },
    onFieldChange : function(ds, record, field){
    	if(this.binder.ds == ds && this.binder.name == field.name){
	    	this.onRefresh(ds);   	
    	}
    },
    onClear : function(ds){
    	this.clearValue();    
    },    
    setValue : function(v, silent){
    	this.value = v;
    	if(silent === true)return;
    	if(this.binder){
    		this.record = this.binder.ds.getCurrentRecord();
    		if(this.record == null){
    			var data = {};
    			data[this.binder.name] = v;
    			this.record  = this.binder.ds.create(data,false);
    			this.record.validate(this.binder.name);
    		}else{
    			this.record.set(this.binder.name,v);
    		}
    		if(v=='') delete this.record.data[this.binder.name];
    	}
    },
    clearInvalid : function(){},
    markInvalid : function(){},
    clearValue : function(){},
    initMeta : function(){},
    setDefault : function(){},
    setRequired : function(){},
    onDataChange : function(){}
});