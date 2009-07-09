Aurora.ComboBox = Ext.extend(Aurora.TriggerField,{
	constructor: function(elId, config) {
        Aurora.ComboBox.superclass.constructor.call(this, elId, config);        
    },
    initComponent : function(){
    	Aurora.ComboBox.superclass.initComponent.call(this);
    	this.list = this.wrap.first('div.item-comboList'); 
    },
    isExpanded : function(){
        return this.list && this.list.isVisible();
    },
    onTriggerClick: function(){
    	if(this.isExpanded()){
    		this.list.hide();
    	}else{
    		this.list.show();
    	}
    }
});