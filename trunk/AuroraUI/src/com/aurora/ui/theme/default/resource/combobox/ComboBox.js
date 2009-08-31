Aurora.ComboBox = Ext.extend(Aurora.TriggerField, {	
	maxHeight:200,
	blankOption:true,
	rendered:false,
	selectedClass:'item-comboBox-selected',	
	currentNodeClass:'item-comboBox-current',
	constructor : function(config) {
		Aurora.ComboBox.superclass.constructor.call(this, config);		
	},
	initComponent:function(config){
		Aurora.ComboBox.superclass.initComponent.call(this, config);
		if(config.options) this.setOptions(config.options);		
	},
	initEvents:function(){
		Aurora.ComboBox.superclass.initEvents.call(this);  
	},
	onTriggerClick : function() {
		this.doQuery('',true);
		Aurora.ComboBox.superclass.onTriggerClick.call(this);		
	},
	expand:function(){
		if(!this.optionDataSet)return;
		Aurora.ComboBox.superclass.expand.call(this);
		var v=this.getValue();
		this.currentIndex = this.getIndex(v);		
		if (!Ext.isEmpty(v)) {				
			if(this.selectedIndex)Ext.fly(this.getNode(this.selectedIndex)).removeClass(this.selectedClass);
			Ext.fly(this.getNode(this.currentIndex)).addClass(this.currentNodeClass);
			this.selectedIndex = this.currentIndex;
		}		
	},
	collapse:function(){
		Aurora.ComboBox.superclass.collapse.call(this);
		if(this.currentIndex!==undefined)
		Ext.fly(this.getNode(this.currentIndex)).removeClass(this.currentNodeClass);		
	},
	setOptions : function(name){
		var ds = name
		if(typeof(name)==='string'){
			ds = window[name];
		}
		if(this.currentOptions != ds){
			this.optionDataSet = ds;
			this.rendered = false;
			this.currentOptions = ds;
		}
		if(!Ext.isEmpty(this.value))this.setValue(this.value, true)
	},
	onRender:function(){			
        if(!this.view){
        	this.view=new Aurora.Element(document.createElement('ul'));
			this.view.on('click', this.onViewClick,this);
			this.view.on('mouseover',this.onViewOver,this);
			this.view.on('mousemove',this.onViewMove,this);			
        }
        if(this.rendered===false && this.optionDataSet){
			this.initList();
			var l = this.optionDataSet.getAll().length;
			var widthArray = [];
			for(var i=0;i<l;i++){
				var li=this.view.dom.childNodes[i];
				var width=Aurora.TextMetrics.measure(li,li.innerHTML).width;
				widthArray.push(width);
			}		
			if(l==0){				
//				this.popup.setHeight(this.miniHeight);
//				this.popup.setWidth(this.wrap.getWidth());
			}else{
				widthArray=widthArray.sort(function(a,b){return a-b});
				var maxWdith=widthArray[l-1]+20;			
				this.popup.setWidth(Math.max(this.wrap.getWidth(),maxWdith));
				if(this.popup.getHeight()>this.maxHeight){				
					this.popup.setHeight(this.maxHeight);
				}
			}
			this.rendered = true;
		}       
	},
	onViewClick:function(e,t){	
		if(t.tagName!='LI'){
		    return;
		}		
		this.onSelect(t);
		this.collapse();		
	},	
	onViewOver:function(e,t){
		this.inKeyMode = false;
	},
	onViewMove:function(e,t){	
		if(this.inKeyMode){ // prevent key nav and mouse over conflicts
            return;
        }
        var index = t.tabIndex;        
        this.selectItem(index);        
	},
	onSelect:function(target){
		this.value=target.attributes['itemValue'].value;	
		this.setValue(this.value);
		this.focus()
	},
	initQuery:function(){//事件定义中调用
		this.doQuery(this.getText());
	},
	doQuery : function(q,forceAll) {		
		if(q === undefined || q === null){
			q = '';
	    }		
//		if(forceAll){
//            this.store.clearFilter();
//        }else{
//            this.store.filter(this.displayField, q);
//        }
        
		//值过滤先不添加
		this.onRender();	
	},
	initList: function(){	
		this.refresh();
		this.litp=new Aurora.Template('<li tabIndex="{index}" itemValue="{'+this.valueField+'}">{'+this.displayField+'}&#160;</li>');
		var datas = this.optionDataSet.getAll();
		var l=datas.length;
		for(var i=0;i<l;i++){
			var d = Aurora.apply(datas[i].data, {index:i})
			this.litp.append(this.view,d);	//等数据源明确以后再修改		
		}
		if(l!=0){
			this.view.appendTo(this.popup);	
		}
	},
	refresh:function(){
		this.view.update('');
		this.selectedIndex = null;
	},
	selectItem:function(index){
		if(Aurora.isEmpty(index)){
			return;
		}	
		var node = this.getNode(index);			
		if(node.tabIndex!=this.selectedIndex){
			if(!Aurora.isEmpty(this.selectedIndex)){							
				Aurora.fly(this.getNode(this.selectedIndex)).removeClass(this.selectedClass);
			}
			this.selectedIndex=node.tabIndex;			
			Aurora.fly(node).addClass(this.selectedClass);					
		}			
	},
	getNode:function(index){		
		return this.view.dom.childNodes[index];
	},	
//	onDestroy:function(){
//		if(this.view){
//			Aurora.destroy(this.view);			     
//		}		
//	},
	getText : function() {		
		return this.text;
	},	
	getValue : function() {
		var v=this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
	},
	initDisplay : function(){
		var v = this.getValue();
		var r = this.optionDataSet.find(this.valueField, v);
		this.text = '';
		if(r != null){
			this.text = r.get(this.displayField);
		}else{
			this.value = ''
		}
		this.setRawValue(this.text);
	},
	setValue:function(v,silent){
        Aurora.ComboBox.superclass.setValue.call(this, v, silent);		
		this.initDisplay();
	},
	getIndex:function(v){
		var datas = this.optionDataSet.getAll();		
		var l=datas.length;
		for(var i=0;i<l;i++){
			if(datas[i].data[this.valueField]==v){				
				return i;
			}
		}		
	}
});