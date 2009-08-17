Aurora.Component = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        config = config || {};
        Ext.apply(this, config);
        this.id = config.id;
        window[this.id] = this;
        Aurora.Component.superclass.constructor.call(this);		
    },
    initComponent : function(){    	
    },
    initEvents : function(){
    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    bind : function(ds, name){
    	this.bind = {
    		ds: ds,
    		name:name
    	}
    	ds.on('indexchange', this.onRefresh, this);
    	ds.on('metachange', this.onRefresh, this);
    	ds.on('fieldchange', this.onFieldChange, this)
    },
    onRefresh : function(ds){
		var record = ds.getCurrentRecord();
		var value = record.get(this.bind.name);
		var field = record.getMeta().getField(this.bind.name);
		this.setValue(value);
		this.onFieldChange(ds,record,field);
    },
    onFieldChange : function(ds, record, field){
    	if(this.bind.ds == ds && this.bind.name == field.name){
	    	this.setDefault();
	    	this.initMeta(ds, field);    	
    	}
    },
    setValue : function(){},
    initMeta : function(){},
    setDefault : function(){},
    setRequired : function(){},
    onDataChange : function(){}
});