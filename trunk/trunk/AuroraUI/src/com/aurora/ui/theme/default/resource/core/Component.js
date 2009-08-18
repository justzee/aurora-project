Aurora.Component = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        Aurora.Component.superclass.constructor.call(this);
        this.id = config.id;
        window[this.id] = this;		
		this.initConfig=config;
		this.initComponent(config);
        this.initEvents();
    },
    initComponent : function(config){ 
		config = config || {};
        Ext.apply(this, config);
    },
    initEvents : function(){
    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    bind : function(ds, name){
    	this.binder = {
    		ds: ds,
    		name:name
    	}
    	ds.on('metachange', this.onRefresh, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onRefresh, this);
    },
    onRefresh : function(ds){
		var record = ds.getCurrentRecord();
		var value = record.get(this.binder.name);
		var field = record.getMeta().getField(this.binder.name);		
		var config={};
		Ext.apply(config,this.initConfig);		
		Ext.apply(config, field.snap);		
		this.initComponent(config);
		this.setValue(value,true);
    },
    onFieldChange : function(ds, record, field){
    	if(this.binder.ds == ds && this.binder.name == field.name){
	    	this.onRefresh(ds);   	
    	}
    },
    setValue : function(v, silent){
    	this.value = v;
    	if(silent === true)return;
    	if(this.binder){
    		this.binder.ds.getCurrentRecord().set(this.binder.name,v);
    	}
    },
    initMeta : function(){},
    setDefault : function(){},
    setRequired : function(){},
    onDataChange : function(){}
});