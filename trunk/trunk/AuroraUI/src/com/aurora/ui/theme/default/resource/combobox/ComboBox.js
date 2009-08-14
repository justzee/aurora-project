Aurora.ComboBox = Ext.extend(Aurora.TriggerField, {	
	maxHeight:200,
	blankOption:true,
	rendered:false,
	selectedClass:'item-comboBox-selected',	
	constructor : function(config) {	
		Aurora.ComboBox.superclass.constructor.call(this, config);
		this.initOptions = this.options;
		//if(this.options) this.setOptions(this.options);
	},	
	onTriggerClick : function() {
		Aurora.ComboBox.superclass.onTriggerClick.call(this);		
		this.doQuery('',true);		
	},
	setOptions : function(ds){
		if(this.options != ds){
			this.rendered = false;
			this.options = ds;
		}
	},
	onRender:function(){			
        if(!this.view){
        	this.view=new Aurora.Element(document.createElement('ul'));
			this.view.on('click', this.onViewClick,this);
			this.view.on('mouseover',this.onViewOver,this);
			this.view.on('mousemove',this.onViewMove,this);			
        }
        if(!this.rendered && this.options){        	
			this.initList();
			var l = this.options.getAll().length;
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
		this.popup.hide();		
	},	
	onViewOver:function(e,t){
		this.inKeyMode = false;
	},
	onViewMove:function(e,t){	
		if(this.inKeyMode){ // prevent key nav and mouse over conflicts
            return;
        }		
        var index = t.tabIndex;        
        this.select(index);        
	},
	onSelect:function(target){
		this.text=target.innerHTML;			
		this.setText(this.text);
		this.value=target.value;	
		this.setValue(this.value);		
		this.el.dom.select();
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
		this.litp=new Aurora.Template('<li tabIndex="{index}" value="{'+this.valueField+'}">{'+this.displayField+'}</li>');
		var datas = this.options.getAll();
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
	select:function(index){
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
	setText:function(v){
		var v=(v === null || v === undefined ? '' : v);
		if(this.emptyText && this.el && v !== undefined && v !== null && v !== ''){
            this.el.removeClass(this.emptyTextCss);
        }
        this.text = v;
        this.el.dom.value = v;        
        this.applyEmptyText();
	},
	getValue : function() {
		var v=this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
	},
	setValue:function(v){
		Aurora.ComboBox.superclass.setValue.call(this, v);
		v=(v === null || v === undefined ? '' : v);
        this.wrap.child('input[type=hidden]').dom.value = v;
        this.value=v;
	},
	setDefault : function(){
    	Aurora.ComboBox.superclass.setDefault.call(this);
    	if(this.initOptions) {
    		this.setOptions(this.initOptions)
    	}
    },
	initMeta : function(ds, field){
		Aurora.ComboBox.superclass.initMeta.call(this, ds, field);
		var p = field.snap;
		var options = p['options'];
		if(options) {
			this.setOptions(options);			
		}  	
    }
});