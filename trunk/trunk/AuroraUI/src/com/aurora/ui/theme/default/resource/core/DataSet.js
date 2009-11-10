Aurora.AUTO_ID = 1000;
Aurora.DataSet = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {//datas,fields, type
		Aurora.DataSet.superclass.constructor.call(this);
		Aurora.DataSetManager.reg(this);
		config = config || {};
    	this.data = [];
    	this.qpara = {};
    	this.id = config.id || Ext.id();		
    	this.qds = $(config.queryDataSet) || null;
    	this.spara = {};
    	this.pageSize = config.pageSize || 10;
    	this.fields = {};
    	this.gotoPage = 1;
    	this.currentPage = 1;
    	this.currentIndex = 1;
    	this.totalCount = 0;
    	this.totalPage = 0;
    	this.initEvents();
    	if(config.fields)this.initFields(config.fields)
    	this.submitUrl = config.submitUrl || '';
    	this.queryUrl = config.queryUrl || '';
    	this.fetchAll = config.fetchAll;
    	this.autoCount = config.autoCount;
    	if(config.datas && config.datas.length != 0) {
    		this.loadData(config.datas);
    		//this.locate(this.currentIndex); //不确定有没有影响
    	}
    },
    reConfig : function(config){
    	this.resetConfig();
    	Ext.apply(this, config);
    },
    bind : function(name, ds){
    	var field = new Aurora.Record.Field({
    		name:name,
    		type:'dataset',
    		dataset:ds
    	});
	    this.fields[field.name] = field;
    },
    resetConfig : function(){
    	this.data = [];
    	this.gotoPage = 1;
    	this.currentPage = 1;
    	this.currentIndex = 1;
    	this.totalCount = 0;
    	this.totalPage = 0;
    },
    getConfig : function(){
    	var c = {};
    	c.id = this.id;
    	c.xtype = 'dataset';
    	c.data = this.data;
    	c.gotoPage = this.gotoPage;
    	c.currentPage = this.currentPage;
    	c.currentIndex = this.currentIndex;
    	c.totalCount = this.totalCount;
    	c.totalPage = this.totalPage;
    	return c;
    },
    initEvents : function(){
    	this.addEvents(
	        'metachange',
	        'fieldchange',
	        'create',
	        'add',
	        'remove',
	        'update',
	        'clear',
	        'load',
	        'valid',
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
    	return this.fields[name];
    },
    loadData : function(datas, num){
        this.data = [];
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
        this.fireEvent("load", this);
    },
    
    /** ------------------数据操作------------------ **/ 
    create : function(data, valid){
    	if(valid !== false) if(!this.validCurrent())return;
    	var record = new Aurora.Record(data||{});
        this.add(record); 
        this.locate(this.data.length, false)
        this.fireEvent("create", this, record);
        return record;
    },
    validCurrent : function(){
    	var c = this.getCurrentRecord();
    	if(c==null)return true;
    	return c.validateRecord();
    },
    add : function(record){
    	record.isNew = true;
        record.setDataSet(this);
        var index = this.data.length;
        this.data.add(record);
        for(var k in this.fields){
    		var field = this.fields[k];
    		if(field.type == 'dataset'){
    			var ds = field.pro['dataset'];
    			ds.resetConfig()
    			record.data[field.name] = ds.getConfig();    			
    		}
    	}
        this.fireEvent("add", this, record, index);
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
    	var index;
    	if(!record){
    		record = this.getCurrentRecord();
    	}
    	index = this.data.indexOf(record);
    	
    	if(index == -1)return;
        this.data.remove(record);
        this.fireEvent("remove", this, record, index);
        if(index< this.data.length) {
        	this.next();        	
        }else{
        	this.pre();
        }
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
    findById : function(id){
    	var find = null;
    	for(var i = 0,len = this.data.length; i < len; i++){
            if(this.data[i].id == id){
            	find = this.data[i]
                break;
            }
        }
        return find;
    },
    removeAll : function(){
    	this.currentIndex = 1;
        this.data = [];
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
    processCurrentRow : function(){
    	var r = this.getCurrentRecord();
    	for(var k in this.fields){
    		var field = this.fields[k];
    		if(field.type == 'dataset'){
    			var ds = field.pro['dataset'];
    			if(r){
    				ds.reConfig(r.data[field.name]);
    			}else{
    				ds.resetConfig();
    			}
    			ds.processCurrentRow();
    		}
    	}
    	this.fireEvent("indexchange", this, r);
    },
    /** ------------------导航函数------------------ **/
    locate : function(index, valid){
//    	if(index == this.currentIndex) return;
    	if(valid !== false) if(!this.validCurrent())return;
    	
    	if(index <=0 || (index > this.totalCount))return;
    	
    	var lindex = index - (this.currentPage-1)*this.pageSize;
    	if(this.data[lindex - 1]){
//    		this.currentPage =  Math.ceil(index/this.pageSize);
	    	this.currentIndex = index;
    	}else{
//    		if(index > this.totalCount && this.totalCount != 0) {
//				return;
////    			index = this.totalCount;
//			}
			this.currentIndex = index;
			this.currentPage =  Math.ceil(index/this.pageSize);
			this.query(this.currentPage);
			return;
    	}
    	
//    	if(this.queryUrl){
//    		if(index >(this.currentPage-1)*this.pageSize && index <= Math.min(this.totalCount,this.currentPage*this.pageSize)) {
//	    		this.currentIndex = index;
//    		}else{
//    			if(index > this.totalCount && this.totalCount != 0) {
//    				return;
////    				index = this.totalCount;
//    			}
//    			this.currentIndex = index;
//    			this.currentPage =  Math.ceil(index/this.pageSize);
//    			this.query(this.currentPage);
//    			return;
//    		}
//    	}else{
//    		if(index >0 && index <= this.data.length) {
//    			this.currentPage =  Math.ceil(index/this.pageSize);
//	    		this.currentIndex = index;	    		
//    		}
//    	}
    	this.processCurrentRow();
//    	this.fireEvent("indexchange", this, this.getCurrentRecord());
    },
    
    goPage : function(page){
    	if(page >0) {
    		this.gotoPage = page;
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
    
    validate : function(){
    	var valid = true;
    	var records = this.getAll();
    	if(records.length == 0) this.create({})
		for(var k = 0,l=records.length;k<l;k++){
			var record = records[k];
			record.validateRecord();
			if(valid == true){
				valid = record.valid;
			}
		}
		return valid;
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
    	if(!this.qds) return;
//    	if(this.qds.getCurrentRecord() == null) this.qds.create();
    	if(!this.qds.validate()) return;
    	if(!this.queryUrl) return;
    	if(!page) this.currentIndex = 1;
    	this.currentPage = page || 1;
    	
    	var q = {};
    	var r = this.qds.getCurrentRecord();
    	if(r != null)
    	Ext.apply(q, r.data);
    	
    	Ext.apply(q, this.qpara);
//    	q['pagesize']= this.pageSize;
//    	q['pagenum']=this.currentPage;
//    	q['_fecthall']=this.fetchAll;
//    	q['_autocount']=this.autoCount;
//    	q['_rootpath']='list';
    	var para = 'pagesize='+this.pageSize + 
    				  '&pagenum='+this.currentPage+
    				  '&_fecthall='+this.fetchAll+
    				  '&_autocount='+this.autoCount+
    				  '&_rootpath=list'
    	var url = '';
    	if(this.queryUrl.indexOf('?') == -1){
    		url = this.queryUrl + '?' + para;
    	}else{
    		url = this.queryUrl + '&' + para;
    	}
    	
    	Aurora.request(url, q, this.onLoadSuccess, this.onLoadFailed, this);
    },
    isModified : function(){
    	var modified = false;
    	var records = this.getAll();
		for(var k = 0,l=records.length;k<l;k++){
			var record = records[k];
			if(record.modified) {
				modified = true;
				break;
			}       			
		}
		return modified;
    },
    getJsonData : function(){
    	var datas = [];
    	for(var i=0,l=this.data.length;i<l;i++){
    		var r = this.data[i];    		
    		if(r.dirty || r.isNew){
    			var d = Ext.apply({}, r.data);
    			d['_id'] = r.id;
    			d['_status'] = r.isNew ? 'new' : 'update';
    			for(var k in r.data){
    				var item = d[k]; 
    				if(item.xtype == 'dataset'){
    					var ds =$(item.id);
    					ds.reConfig(item)
    					d[k] = ds.getJsonData();
    				}
    			}
		    	datas.push(d);    			
    		}
    	}
    	return datas;
    },
    submit : function(url){
    	this.submitUrl = url||this.submitUrl;
    	if(this.submitUrl == '') return;
    	var p = this.getJsonData();
    	for(var i=0;i<p.length;i++){
    		p[i] = Ext.apply(p[i],this.spara)
    	}
//    	alert(Ext.util.JSON.encode(p));return;
    	Aurora.request(this.submitUrl, p, this.onSubmitSuccess, this.onSubmitFailed, this);
    },
    
    /** ------------------事件函数------------------ **/
    afterEdit : function(record, name, value) {
        this.fireEvent("update", this, record, name, value);
    },
    afterReject : function(record){
    	this.fireEvent("reject", this, record);
    },
    onSubmitSuccess : function(res){
    	var datas = [].concat(res.result.record);
    	this.refreshRecord(datas)
    },
    refreshRecord : function(datas){
    	//this.resetConfig();
    	for(var i=0,l=datas.length;i<l;i++){
    		var data = datas[i];
	    	var r = this.findById(data['_id']);
	    	if(!r) return;
	    	r.clear();
	    	for(var k in data){
				var f = this.fields[k];
				if(f && f.type == 'dataset'){
					var ds = f.pro['dataset'];
					if(r){
	    				ds.reConfig(r.data[f.name]);
	    			}
	    			if(data[k].record)
					ds.refreshRecord([].concat(data[k].record))
				}else{
					r.data[k] = data[k];
				}
	       	}
    	}
    	this.fireEvent("indexchange", this, this.getCurrentRecord());
    },
    onSubmitFailed : function(res){
    	alert(res.error.message);    
    },
    onLoadSuccess : function(res){
    	if(res == null) return;
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
    	}else if(records.length == 0){
//    		this.removeAll();
    		this.currentIndex  = 1
    	}
    	this.loadData(datas, total);
	    this.locate(this.currentIndex);
    },
    onLoadFailed : function(res){
    	alert(res.error.message)
    },
    onFieldChange : function(record,field,type,value) {
    	this.fireEvent('fieldchange', this, record, field, type, value)
    },
    onMetaChange : function(record,meta,type,value) {
    	this.fireEvent('metachange', this, record, meta, type, value)
    },
    onRecordValid : function(record, name, valid){
    	this.fireEvent('valid', this, record, name, valid)
    }
});


Aurora.Record = function(data, fields){
    this.id = ++Aurora.AUTO_ID;
    this.data = data;
    this.fields = {};
    this.errors = {};
    this.meta = new Aurora.Record.Meta(this);
    if(fields)this.initFields(fields);
};
Aurora.Record.prototype = {
	isNew : false,
	dirty : false,
	valid : true,
	editing : false,
	modified: null,
	clear : function() {
		this.editing = false;
		this.valid = true;
		this.isNew = false;
		this.dirty = false;
		this.modified = null;
		this.errors = {};
	},
	initFields : function(fields){
		for(var i=0,l=fields.length;i<l;i++){
			var f = new Aurora.Record.Field(fields[i]);
			f.record = this;
			this.fields[f.name] = f;
		}
	},
	validateRecord : function(){
		this.errors = {};
		this.valid = true;
		var df = this.ds.fields;
		var rf = this.fields;
		var names = [];
		for(var k in df){
			names.add(k);
		}
		for(var k in rf){
			if(names.indexOf(k) == -1){
				names.add(k);
			}
		}
		for(var i=0,l=names.length;i<l;i++){
			if(this.valid == true) {
				this.valid = this.validate(names[i]);
			} else {
				this.validate(names[i]);
			}
		}
		return this.valid;
	},
	validate : function(name){
		var valid = true;
		var v = this.get(name);
		var field = this.getMeta().getField(name)
		if(!v && field.snap.required == true){
			this.errors[name] = {
				message:'此字段不能为空',
				code:'001',
				field:name
			};
			valid =  false;
		}else{
			//加入其他验证信息			
			valid =  true;
		}
		this.ds.onRecordValid(this,name,valid)
		return valid;
	},
    setDataSet : function(ds){
        this.ds = ds;
    },
    getMeta : function(){
    	return this.meta;
    },    
	set : function(name, value){
        if(this.data[name] == value){
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
           this.ds.afterEdit(this, name, value);
        }
        
        this.validate(name)
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
//    beginEdit : function(){
//        this.editing = true;
//        this.modified = {};
//    },
//    cancelEdit : function(){
//        this.editing = false;
//        delete this.modified;
//    },
//    endEdit : function(){
//        delete this.modified;
//        this.editing = false;
//        if(this.dirty && this.ds){
//            this.ds.afterEdit(this);//name,value怎么处理?
//        }        
//    },
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
	getPropertity : function(name){
		return this.pro[name]
	},
	setRequired : function(r){
		this.setPropertity(r, 'required');
	},
	setReadOnly : function(r){	
		this.setPropertity(r, 'readonly');
	},
	setOptions : function(r){
		this.setPropertity(r, 'options');
	},
	getOptions : function(){
		return this.getPropertity('options');
	}
}