Aurora.AUTO_ID = 1000;
Aurora.DataSet = Ext.extend(Ext.util.Observable,{
	constructor: function(datas,fields) {
    	this.data = [];
    	this.qpara = {};
    	this.spara = {};
    	this.fields = {};
    	this.currentIndex = 0;
    	this.modified = [];
    	this.initEvents();
    	if(fields)this.initFields(fields)
    	if(datas)this.loadData(datas)
    },
    initEvents : function(){
    	this.addEvents(
	        'datachanged',
	        'metachange',
	        'fieldchange',
	        'add',
	        'remove',
	        'update',
	        'clear',
	        'load',
	        'indexchange',
	        'reject'
		);    	
    },
    initFields : function(fields){
    	for(var i = 0, len = fields.length; i < len; i++){
    		var field = new Aurora.Record.Field(fields[i]);
	        this.fields[field.name] = field;
        }
    },
    getField : function(name){
    	return this.fields[field.name];
    },
    loadData : function(datas){
        this.currentIndex = 0;
        this.data = [];
        this.modified = [];
    	for(var i = 0, len = datas.length; i < len; i++){
    		var record = new Aurora.Record(datas[i].data,datas[i].field);
            record.setDataSet(this);
	        this.data.add(record);
        }
        this.fireEvent("load", this, datas);
        this.locate(0)
    },
    
    /** ------------------数据操作------------------ **/    
    add : function(records){
        records = [].concat(records);
        if(records.length < 1){
            return;
        }
        for(var i = 0, len = records.length; i < len; i++){
            records[i].setDataSet(this);
        }
        var index = this.data.length;
        this.data = this.data.concat(records);
        this.fireEvent("add", this, records, index);
    },
    getCurrentRecord : function(){
    	return this.data[this.currentIndex];
    },
    insert : function(index, records){
        records = [].concat(records);
        var splice = this.data.splice(index,this.data.length);
        for(var i = 0, len = records.length; i < len; i++){
            records[i].setDataSet(this);
            this.data.add(records[i]);
        }
        this.data = this.data.concat(splice);
        this.fireEvent("add", this, records, index);
    },
    remove : function(record){
        var index = this.data.indexOf(record);
        this.data.remove(record);
        if(this.pruneModifiedRecords){
            this.modified.remove(record);
        }
        this.fireEvent("remove", this, record, index);
    },
    getAll : function(){
    	return this.data;    	
    },
    find : function(property, value){
    	var r = null;
    	this.each(function(record){
    		var v = record.get(property);
    		if(v ==value){
    			r = record;
    			return false;    			
    		}
    	}, this)
    	return r;
    },
    removeAll : function(){
    	this.currentIndex = 0;
        this.data = [];
        this.modified = [];
        this.fireEvent("clear", this);
    },
    indexOf : function(record){
        return this.data.indexOf(record);
    },
    getAt : function(index){
        return this.data[index];
    },
    each : function(fn, scope){
        var items = [].concat(this.data); // each safe for removal
        for(var i = 0, len = items.length; i < len; i++){
            if(fn.call(scope || items[i], items[i], i, len) === false){
                break;
            }
        }
    },

    
    /** ------------------导航函数------------------ **/
    locate : function(index){
    	if(index != -1 && index < this.data.length) {
    		this.currentIndex = index;
    		this.fireEvent("indexchange", this, index);
    	}
    },
    pre : function(){
    	this.locate(this.currentIndex-1);    	
    },
    next : function(){
    	this.locate(this.currentIndex+1);
    },
    
    /** ------------------ajax函数------------------ **/
    setQueryParameter : function(para, value){
        this.qpara[para] = value;
    },
    setSubmitParameter : function(para, value){
        this.spara[para] = value;
    },
    query : function(url){
    	Aurora.request(url, this.qpara, this.onLoadSuccess, this.onLoadFailed, this);
    },
    submit : function(url){
    	var datas = [];
    	for(var i=0,l=this.data.length;i<l;i++){
    		var r = this.data[i];
	    	datas.push(r.data);
    	}
    	alert(Ext.util.JSON.encode(datas));
    	//Aurora.request(url, this.spara, this.onSubmitSuccess, this.onSubmitFailed, this);
    },
    
    /** ------------------事件函数------------------ **/
    afterEdit : function(record) {
    	if(this.modified.indexOf(record) == -1){
            this.modified.push(record);
        }
        this.fireEvent("update", this, record);
    },
    afterReject : function(record){
    	this.modified.remove(record);
    	this.fireEvent("reject", this, record);
    },
    onSubmitSuccess : function(res){    
    
    },
    onSubmitFailed : function(res){
    
    },
    onLoadSuccess : function(res){
    	this.loadData(res.result.list.record)
    },
    onLoadFailed : function(res){
    	
    },
    onFieldChange : function(record,field,type,value) {
    	this.fireEvent('fieldchange', this, record, field, type, value)
    },
    onMetaChange : function(record,meta,type,value) {
    	this.fireEvent('metachange', this, record, meta, type, value)
    }
});


Aurora.Record = function(data, fields){
    this.id = ++Aurora.AUTO_ID;
    this.data = data;
    this.fields = {};
    this.meta = new Aurora.Record.Meta(this);
    if(fields)this.initFields(fields);
};
Aurora.Record.prototype = {
	dirty : false,
	editing : false,
	modified: null,
	initFields : function(fields){
		for(var i=0,l=fields.length;i<l;i++){
			var f = new Aurora.Record.Field(fields[i]);
			f.record = this;
			this.fields[f.name] = f;
		}
	},
    setDataSet : function(ds){
        this.ds = ds;
    },
    getMeta : function(){
    	return this.meta;
    },    
	set : function(name, value){
        if(String(this.data[name]) == String(value)){
            return;
        }
        this.dirty = true;
        if(!this.modified){
            this.modified = {};
        }
        if(typeof this.modified[name] == 'undefined'){
            this.modified[name] = this.data[name];
        }
        this.data[name] = value;
        if(!this.editing && this.ds){
           this.ds.afterEdit(this);
        }
    },
    get : function(name){
        return this.data[name];
    },
    reject : function(silent){
        var m = this.modified;
        for(var n in m){
            if(typeof m[n] != "function"){
                this.data[n] = m[n];
            }
        }
        delete this.modified;
        this.editing = false;
        if(this.dirty && this.ds){
            this.ds.afterReject(this);
        }
        this.dirty = false;
    },
    beginEdit : function(){
        this.editing = true;
        this.modified = {};
    },
    cancelEdit : function(){
        this.editing = false;
        delete this.modified;
    },
    endEdit : function(){
        delete this.modified;
        this.editing = false;
        if(this.dirty && this.ds){
            this.ds.afterEdit(this);
        }        
    },
    onFieldChange : function(name, type, value){
    	var field = this.getMeta().getField(name);
    	this.ds.onFieldChange(this, field, type, value);
    },
    onFieldClear : function(name){
    	var field = this.getMeta().getField(name);
    	this.ds.onFieldChange(this, field);
    },
    onMetaChange : function(meta, type, value){
    	this.ds.onMetaChange(this,meta, type, value);
    },
    onMetaClear : function(meta){
    	this.ds.onMetaChange(this,meta);
    }
}
Aurora.Record.Meta = function(r){
	this.record = r;
	this.pro = {};
}
Aurora.Record.Meta.prototype = {
	clear : function(){
		this.pro = {};
		this.record.onMetaClear(this);
	},
	getField : function(name){
    	var f = this.record.fields[name];
		var df = this.record.ds.fields[name];
		var rf;
    	if(!f){
    		if(df){
    			f = new Aurora.Record.Field({name:df.name,type:df.type});
    		}else{
    			f = new Aurora.Record.Field({name:name,type:'string'});
    		}
			f.record = this.record;
			this.record.fields[f.name]=f;
    	}
    	
    	var pro = {};
    	if(df) pro = Ext.apply(pro, df.pro);
    	pro = Ext.apply(pro, this.pro);
    	pro = Ext.apply(pro, f.pro);
    	f.snap = pro;
    	return f;
    },
	setRequired : function(r){
		var op = this.pro['required'];
		if(op !== r){
			this.pro['required'] = r;
			this.record.onMetaChange(this, 'required', r);
		}
	},
	setReadOnly : function(r){		
		var op = this.pro['readonly'];
		if(op !== r){
			this.pro['readonly'] = r;
			this.record.onMetaChange(this,'readonly', r);
		}
	}
}

Aurora.Record.Field = function(c){
    this.name = c.name;
    this.type = c.type;
    this.pro = c||{};
    this.record;
};
Aurora.Record.Field.prototype = {
	clear : function(){
		this.pro = {};
		this.record.onFieldClear(this.name);
	},
	setPropertity : function(value,type){
		var op = this.pro[type];
		if(op !== value){
			this.pro[type] = value;
			this.record.onFieldChange(this.name, type, value);
		}
	},
	setRequired : function(r){
		this.setPropertity(r, 'required');
	},
	setReadOnly : function(r){	
		this.setPropertity(r, 'readonly');
	},
	setOptions : function(r){
		this.setPropertity(r, 'options');
	}
}