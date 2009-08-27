Aurora.AUTO_ID = 1000;
Aurora.DataSet = Ext.extend(Ext.util.Observable,{
	constructor: function(datas,fields) {
    	this.data = [];
    	this.qpara = {};
    	this.qds = null;
    	this.spara = {};
    	this.pageSize = 10;
    	this.fields = {};
    	this.goPage = 1;
    	this.currentPage = 1;
    	this.currentIndex = 1;
    	this.totalCount = 0;
    	this.totalPage = 0;
    	this.modified = [];
    	this.initEvents();
    	if(fields)this.initFields(fields)
    	if(datas)this.loadData(datas)
    },
    initEvents : function(){
    	this.addEvents(
	        'metachange',
	        'fieldchange',
	        'new',
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
    loadData : function(datas, num){
        this.data = [];
        this.modified = [];
        if(num) {
        	this.totalCount = num;
        }else{
        	this.totalCount = datas.length;
        }
    	this.totalPage = Math.ceil(this.totalCount/this.pageSize)
    	for(var i = 0, len = datas.length; i < len; i++){
    		var record = new Aurora.Record(datas[i].data||datas[i],datas[i].field);
            record.setDataSet(this);
	        this.data.add(record);
        }
        this.fireEvent("load", this, datas);
    },
    
    /** ------------------数据操作------------------ **/ 
    newRecord : function(){
    	var record = new Aurora.Record({});
        this.add(record); 
        this.fireEvent("load", this, record);
        return record;
    },
    add : function(records){
        records = [].concat(records);
        if(records.length < 1){
            return;
        }
        for(var i = 0, len = records.length; i < len; i++){
        	records[i].isNew = true;
            records[i].setDataSet(this);
        }
        var index = this.data.length;
        this.data = this.data.concat(records);
        this.fireEvent("add", this, records, index);
    },
    getCurrentRecord : function(){
    	if(this.data.length ==0) return null;
    	return this.data[this.currentIndex - (this.currentPage-1)*this.pageSize -1];
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
    	this.currentIndex = 1;
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
    	if(index <=0)return;
    	if(this.queryUrl){
    		//this.data[index-1] && this.goPage == this.currentPage){
    		if(index >(this.currentPage-1)*this.pageSize && index <= Math.min(this.totalCount,this.currentPage*this.pageSize)) {
	    		this.currentIndex = index;
	    		this.fireEvent("indexchange", this, index);
    		}else{
    			if(index > this.totalCount && this.totalCount != 0) {
    				index = this.totalCount;
    			}
    			this.currentIndex = index;
    			this.currentPage =  Math.ceil(index/this.pageSize);
    			this.query(this.url);
    		}
    	}else{
    		if(index >0 && index <= this.data.length) {
    			this.currentPage =  Math.ceil(index/this.pageSize);
	    		this.currentIndex = index;    		
	    		this.fireEvent("indexchange", this, index);
    		}
    	}
    },
    goPage : function(page){
    	if(page >0) {
    		this.goPage = page;
	    	var go = (page-1)*this.pageSize+1;
	    	this.locate(go);
    	}
    },
    first : function(){
    	this.locate(1);
    },
    pre : function(){
    	this.locate(this.currentIndex-1);    	
    },
    next : function(){
    	this.locate(this.currentIndex+1);
    },
    prePage : function(){
    	this.goPage(this.currentPage -1);
    },
    nextPage : function(){
    	this.goPage(this.currentPage +1);
    },
    /** ------------------ajax函数------------------ **/
    setQueryUrl : function(url){
    	this.queryUrl = url;
    },
    setQueryParameter : function(para, value){
        this.qpara[para] = value;
    },
    setQueryDataSet : function(ds){ 
    	this.qds = ds;
    },
    setSubmitUrl : function(url){
    	this.submitUrl = url;
    },
    setSubmitParameter : function(para, value){
        this.spara[para] = value;
    },
    query : function(page){
    	if(!this.queryUrl) return;
    	if(page){
    		this.currentPage = page;
    		this.currentIndex = (page-1)*this.pageSize+1;
    	}
    	var q = {};
    	if(this.qds) {
	    	var r = this.qds.getCurrentRecord();
	    	if(r != null)
	    	Ext.apply(q, r.data);
    	}
    	Ext.apply(q, this.qpara);
    	q['pagesize']= this.pageSize;
    	q['pagenum']=this.currentPage;
    	
    	Aurora.request(this.queryUrl, q, this.onLoadSuccess, this.onLoadFailed, this);
    },
    submit : function(url){
    	this.submitUrl = url||this.submitUrl;
    	var datas = [];
    	for(var i=0,l=this.data.length;i<l;i++){
    		var r = this.data[i];
    		if(r.dirty || r.isNew){
		    	datas.push(r.data);    			
    		}
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
    	var records = res.result.list.record;
    	var total = res.result.list.totalCount;
    	var datas = [];
    	if(records.length > 0){
    		for(var i=0,l=records.length;i<l;i++){
	    		var item = {
	    			data:records[i]	    		
	    		}
    			datas.push(item);
    		}
	    	this.loadData(datas, total);
	    	this.locate(this.currentIndex);
    	}else if(records.length == 0){
    		this.removeAll();
    	}
    },
    onLoadFailed : function(res){
    	alert(res.error.message)
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
	isNew : false,
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
    	delete pro.name;
		delete pro.type;
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