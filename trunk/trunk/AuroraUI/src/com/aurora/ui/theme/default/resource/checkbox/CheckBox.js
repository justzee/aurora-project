Aurora.CheckBox = Ext.extend(Aurora.Component,{
	readOnly:false,	
	checked:false,
	constructor: function(config){
		Aurora.CheckBox.superclass.constructor.call(this,config);	
	    this.initConfig=config;
		this.initComponent(config);
		this.initEvents();
	},
	initComponent:function(config){
		Aurora.CheckBox.superclass.initComponent.call(this, config);	
		this.checked=config.value==this.checkValue?true:false;		
		this.wrap=Aurora.get(this.id);
		this.el=this.wrap.child('div[atype=checkbox]');	
		this.setClass();
	},
	initEvents:function(){
		Aurora.CheckBox.superclass.initEvents.call(this);
		if (!this.readOnly) {
			this.el.on('click',function(){
				if(this.checked){
					this.el.removeClass('item-checkbox-c');
					this.el.addClass('item-checkbox-u');
					this.checked=false;			
				}else{
					this.el.removeClass('item-checkbox-u');	
					this.el.addClass('item-checkbox-c');
					this.checked=true;							
				}
				this.fireEvent('click',this,this.checked);
			},this);
		}    	
		this.addEvents('click');    
	},
	setValue:function(v){	
		if(typeof(v)==='boolean'){
			if(v){
				this.checked=true;
			}else{
				this.checked=false;
			}
		}else{					
			if(v===this.checkValue){
				this.checked=true;
			}else{
				this.checked=false;
			}
		}
		this.setClass();
		this.value=this.checked==true?this.checkValue:this.unCheckValue;
		this.wrap.child('input[type=hidden]').dom.value=this.value;
	},
	getValue:function(){
		return this.value;
	},
	setReadOnly:function(b){
		if(typeof(b)==='boolean'){
			if(b){
				this.readOnly=true;
			}else{
				this.readOnly=false;	
			}
		}
		this.setClass();
	},
	onRefresh:function(ds){
		var record = ds.getCurrentRecord();
		var value = record.get(this.bind.name);
		var field = record.getMeta().getField(this.bind.name);
		var config={};
		Ext.apply(field.snap,{value:value});
		Ext.apply(config,this.initConfig);		
		Ext.apply(config, field.snap);			
		this.initComponent(config);
	},	
	/*
	 * private
	 */
	setClass:function(){
		this.el.removeClass('item-checkbox-c');
		this.el.removeClass('item-checkbox-u');
		this.el.removeClass('item-checkbox-readonly-c');
		this.el.removeClass('item-checkbox-readonly-u');
		if (this.readOnly) {
			if (this.checked) {				
				this.el.addClass('item-checkbox-readonly-c');
			}
			else {			
				this.el.addClass('item-checkbox-readonly-u');
			}
		}else{
			if (this.checked) {				
				this.el.addClass('item-checkbox-c');
			}
			else {			
				this.el.addClass('item-checkbox-u');
			}
		}		
	}			
});