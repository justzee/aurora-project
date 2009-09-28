Aurora.Grid = Ext.extend(Aurora.Component,{
	constructor: function(config){
		Aurora.Grid.superclass.constructor.call(this,config);
	},
	initComponent:function(config){
		Aurora.Grid.superclass.initComponent.call(this, config);
	},
	initEvents:function(){
		Aurora.Grid.superclass.initEvents.call(this);  
	},
	bind : function(ds){
		this.dataset = ds;
		ds.on('metachange', this.onRefresh, this);
    	ds.on('create', this.onCreate, this);
    	ds.on('load', this.onLoad, this);
    	ds.on('valid', this.onValid, this);
    	ds.on('remove', this.onRemove, this);
    	ds.on('clear', this.onClear, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onRefresh, this);
	},
	onLoad : function(){
	
	},
	onRefresh : function(){
		
	},
	onCreate : function(){
		
	},
	onValid : function(){
		
	},
	onRemove : function(){
		
	},
	onClear : function(){
		
	},
	onFieldChange : function(){
		
	}
	
	
});