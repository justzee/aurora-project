Aurora = {version: '3.0'};

Aurora.onReady = Ext.onReady;
Aurora.decode = Ext.decode;
Aurora.Element = Ext.Element;
Aurora.Template = Ext.Template
Aurora.apply = Ext.apply;
Aurora.isEmpty = Ext.isEmpty;
Aurora.fly = Ext.fly;
Aurora.get= Ext.get;

Aurora.fireWindowResize = function(){
	Aurora.Mask.resizeMask();
}
Ext.fly(window).on("resize", Aurora.fireWindowResize, this);

Aurora.cmps = {};
Aurora.CmpManager = function(){
    return {
        put : function(id, cmp){
        	if(!this.cache) this.cache = {};
        	if(this.cache[id] != null) {
	        	alert("错误: ID为' " + id +" '的组件已经存在!");
	        	return;
	        }
        	this.cache[id]=cmp;
        },
        getAll : function(){
        	return this.cache;
        },
        remove : function(id){
        	delete this.cache[id];
        },
        get : function(id){
        	if(!this.cache) return null;
        	return this.cache[id];
        }
    };
}();


Ext.Ajax.on("requestexception", function(conn, response, options){
	switch(response.status){
		case 404:
			alert('状态 404: 未找到"'+ response.statusText+'"');
			break;
		default:
			alert('状态 '+ response.status + ' 服务器端错误!');
			break;
	}	
}, this);
$ = Aurora.getCmp = function(id){
	var cmp = Aurora.CmpManager.get(id)
	if(!cmp){
		cmp = Aurora.DataSetManager.get(id)
	}
	return cmp;
}
Aurora.getViewportHeight = function(){
    if(Ext.isIE){
        return Ext.isStrict ? document.documentElement.clientHeight :
                 document.body.clientHeight;
    }else{
        return self.innerHeight;
    }
}
Aurora.getViewportWidth = function() {
    if(Ext.isIE){
        return Ext.isStrict ? document.documentElement.clientWidth :
                 document.body.clientWidth;
    }else{
        return self.innerWidth;
    }
}
Aurora.request = function(url, para, success, failed, scope){
	Ext.Ajax.request({
			url: url,
			method: 'POST',
			params:{_request_data:Ext.util.JSON.encode({parameter:para})},
			success: function(response){
				if(response && response.responseText){
					var res = null;
					try {
						res = Ext.decode(response.responseText);
					}catch(e){
						alert('返回格式不正确!')
					}
					if(res && !res.success){							
						if(res.error){//								
							if(failed)failed.call(scope, res);
						}								    						    
					} else {
						if(success)success.call(scope,res);
					}
				}
			},
			scope: scope
		});
}

Ext.applyIf(Array.prototype, {
	add : function(o){
		this[this.length] = o;
	}
});

Aurora.TextMetrics = function(){
    var shared;
    return {
        measure : function(el, text, fixedWidth){
            if(!shared){
                shared = Aurora.TextMetrics.Instance(el, fixedWidth);
            }
            shared.bind(el);
            shared.setFixedWidth(fixedWidth || 'auto');
            return shared.getSize(text);
        }
    };
}();
Aurora.TextMetrics.Instance = function(bindTo, fixedWidth){
    var ml = new Aurora.Element(document.createElement('div'));
    document.body.appendChild(ml.dom);
    ml.position('absolute');
    ml.setLeft(-1000);
    ml.setTop(-1000);    
    ml.hide();
    if(fixedWidth){
        ml.setWidth(fixedWidth);
    }
    var instance = {      
        getSize : function(text){
            ml.update(text);            
            var s=new Object();
            s.width=ml.getWidth();
            s.height=ml.getHeight();
            ml.update('');
            return s;
        },       
        bind : function(el){
        	var a=new Array('font-size','font-style', 'font-weight', 'font-family','line-height', 'text-transform', 'letter-spacing');	
        	var len = a.length, r = {};
        	for(var i = 0; i < len; i++){
                r[a[i]] = Aurora.fly(el).getStyle(a[i]);
            }
            ml.setStyle(r);           
        },       
        setFixedWidth : function(width){
            ml.setWidth(width);
        }       
    };
    instance.bind(bindTo);
    return instance;
};
Aurora.ToolTip = function(){
	q = {
		init: function(){
			var qdom = Ext.DomHelper.append(
			    Ext.getBody(),
			    {
				    tag: 'div',
				    cls: 'tip-wrap',
				    children: [{tag: 'div', cls:'tip-header', html:'<strong>提示信息</strong>'},
				    		   {tag: 'div', cls:'tip-body'},
				    		   {tag: 'div', cls:'tip-arrow'}]
			    }
			);
			this.tip = Ext.get(qdom);
			this.header = this.tip.first("div.tip-header");
			this.body = this.tip.first("div.tip-body");
		},
		show: function(el, text){
			if(this.tip == null){
				this.init();
			}
			this.tip.show();
			this.body.update(text)
			var ele;
			if(typeof(el)=="string"){
				if($(el)){
					if($(el).wrap){
						ele = $(el).wrap;
					}else{
						
					}
				}else{
					ele = Ext.get(el);
				}				
			}
			this.tip.setWidth(ele.getWidth());
			this.header.setWidth(ele.getWidth());
			this.body.setWidth(ele.getWidth());
			this.tip.setX(ele.getX());
			this.tip.setY(ele.getY()-this.tip.getHeight());
		},
		hide: function(){
			if(this.tip != null) this.tip.hide();
		}
	}
	return q
}();
Aurora.Mask = function(){
	var m = {
		container: {},
		mask : function(el){
			var screenWidth = Aurora.getViewportWidth();
    		var screenHeight = Aurora.getViewportHeight();
			if(!window._mask) {
				var p = '<DIV style="left:0px;top:0px;width:'+screenWidth+'px;height:'+screenHeight+'px;POSITION: absolute;FILTER: alpha(opacity=40);BACKGROUND-COLOR: #000000; opacity: 0.4; MozOpacity: 0.4" unselectable="on"></DIV>';
				window._mask = Ext.get(Ext.DomHelper.append(Ext.getBody(),p));
			}
	    	window._mask.setStyle('z-index', Ext.fly(el).getStyle('z-index') - 1);
		},
		unmask : function(el){
			if(window._mask) {
				Ext.fly(window._mask).remove();
				window._mask = null;
			}
		},
		resizeMask : function(){
			if(window._mask) {
				var screenWidth = Aurora.getViewportWidth();
    			var screenHeight = Aurora.getViewportHeight();
				Ext.fly(window._mask).setWidth(screenWidth);
				Ext.fly(window._mask).setHeight(screenHeight);
			}			
		}
	}
	return m;
}();
Ext.Element.prototype.update = function(html, loadScripts, callback){
    if(typeof html == "undefined"){
        html = "";
    }
    if(loadScripts !== true){
        this.dom.innerHTML = html;
        if(typeof callback == "function"){
            callback();
        }
        return this;
    }
    var id = Ext.id();
    var dom = this.dom;

    html += '<span id="' + id + '"></span>';
    Ext.lib.Event.onAvailable(id, function(){
    	var links = [];
    	var scripts = [];
        var hd = document.getElementsByTagName("head")[0];
        for(var i=0;i<hd.childNodes.length;i++){
        	var he = hd.childNodes[i];
        	if(he.tagName == 'LINK') {
        		links.push(he.href);
        	}else if(he.tagName == 'SCRIPT'){
        		scripts.push(he.src);
        	}
        }
        var jsre = /(?:<script([^>]*)?>)((\n|\r|.)*?)(?:<\/script>)/ig;
        var jsSrcRe = /\ssrc=([\'\"])(.*?)\1/i;
        
        var cssre = /(?:<link([^>]*)?>)((\n|\r|.)*?)/ig;
        var cssHreRe = /\shref=([\'\"])(.*?)\1/i;
		
		var cssm;
		while(cssm = cssre.exec(html)){
			var attrs = cssm[1];
			var srcMatch = attrs ? attrs.match(cssHreRe) : false;
			if(srcMatch && srcMatch[2]){
				var included = false;
				for(var i=0;i<links.length;i++){
					var link = links[i];
					if(link.indexOf(srcMatch[2]) != -1){
						included = true;
						break;
					}
				}
				if(!included) {
                	var s = document.createElement("link");
					s.type = 'text/css';
					s.rel = 'stylesheet';
                   	s.href = srcMatch[2];
                   	hd.appendChild(s);
                }
			}
		}
        var match;
        var jslink = [];
        var jsscript = [];
        while(match = jsre.exec(html)){
            var attrs = match[1];
            var srcMatch = attrs ? attrs.match(jsSrcRe) : false;
            if(srcMatch && srcMatch[2]){
            	var included = false;
				for(var i=0;i<scripts.length;i++){
					var script = scripts[i];
					if(script.indexOf(srcMatch[2]) != -1){
						included = true;
						break;
					}
				}
               	if(!included) {
               		jslink[jslink.length] = {
               			src:srcMatch[2],
               			type:'text/javascript'
               		}
               	} 
            }else if(match[2] && match[2].length > 0){
            	jsscript[jsscript.length] = match[2];
            }
        }
        var loaded = 0;
        for(var i = 0,l=jslink.length;i<l;i++){
        	var js = jslink[i];
        	var s = document.createElement("script");
            s.src = js.src;
            s.type = js.type;
            s[Ext.isIE ? "onreadystatechange" : "onload"] = function(){
            	var isready = Ext.isIE ? (this.readyState == "complete") : true;
            	if(isready) {            		
	            	loaded ++;
	            	if(loaded==jslink.length) {
	                    for(j=0,k=jsscript.length;j<k;j++){
		                	var jst = jsscript[j];
		                	if(window.execScript) {	
		                    	window.execScript(jst);
		                    } else {
		                    	window.eval(jst);
		                    }
		                }
		                if(typeof callback == "function"){
				            callback();
				        }
	            	}
            	}
            };
			hd.appendChild(s);
        }
        if(jslink.length ==0) {
        	for(j=0,k=jsscript.length;j<k;j++){
            	var jst = jsscript[j];
            	if(window.execScript) {
                   window.execScript(jst);
                } else {
                   window.eval(jst);
                }
            }
            if(typeof callback == "function"){
	            callback();
	        }
        }        
        var el = document.getElementById(id);
        if(el){Ext.removeNode(el);}        
        Ext.fly(dom).setStyle('display', '');
    });
    Ext.fly(dom).setStyle('display', 'none');
    dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "").replace(/(?:<link.*?>)((\n|\r|.)*?)/ig, "");
    return this;
}
Aurora.DataSetManager = function(){
    return {
        reg : function(ds){
        	if(!this.cache) this.cache = [];
        	this.cache.add(ds)
        },
        getAll : function(){
        	return this.cache;
        },
        get : function(name){
        	if(!this.cache) return null;
        	var ds = null;
        	for(var i = 0;i<this.cache.length;i++){
    			if(this.cache[i].id == name) {
	        		ds = this.cache[i];
    				break;      			
        		}
        	}
        	return ds;
        },
        isModified : function(){
        	var modified = false;
        	for(var i = 0;i<this.cache.length;i++){
        		var ds = this.cache[i];
    			if(ds.modified) {
    				modified = true;
    				break;      			
        		}
        	}
        	return modified;
        }
    };
}();


Aurora.parseDate = function(str){      
  if(typeof str == 'string'){      
    var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);      
    if(results && results.length>3)      
      return new Date(parseInt(results[1]),parseInt(results[2]) -1,parseInt(results[3]));       
    results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);      
    if(results && results.length>6)      
      return new Date(parseInt(results[1]),parseInt(results[2]) -1,parseInt(results[3]),parseInt(results[4]),parseInt(results[5]),parseInt(results[6]));       
  }      
  return null;      
}
Aurora.formateDate = function(date){
	return date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate()
}
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
    		var data = datas[i].data||datas[i];
    		for(var key in this.fields){
    			var field = this.fields[key];
    			var datatype = field.getPropertity('datatype');
    			if(datatype == 'date'){
    				var d = Aurora.parseDate(data[key])
    				data[key] = d;
    			}
    		}
    		var record = new Aurora.Record(data,datas[i].field);
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
//    this.type = c.type;
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
Aurora.Component = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
        Aurora.Component.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        Aurora.CmpManager.put(this.id,this)
		this.initConfig=config;
		this.initComponent(config);
        this.initEvents();
    }, 
    initComponent : function(config){ 
		config = config || {};
        Ext.apply(this, config);
        this.wrap = Ext.get(this.id);
    },
    initEvents : function(){
    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    bind : function(ds, name){
    	this.removeDataSetListener();
    	this.binder = {
    		ds: ds,
    		name:name
    	}
    	this.record = ds.getCurrentRecord();
    	var field =  ds.fields[this.binder.name];
    	if(field) {
			var config={};
			Ext.apply(config,this.initConfig);
			Ext.apply(config, field.pro);
			delete config.name;
			delete config.type;
			this.initComponent(config);
			
    	}
    	ds.on('metachange', this.onRefresh, this);
    	ds.on('create', this.onCreate, this);
    	ds.on('load', this.onRefresh, this);
    	ds.on('valid', this.onValid, this);
    	ds.on('remove', this.onRemove, this);
    	ds.on('clear', this.onClear, this);
    	ds.on('update', this.onUpdate, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onRefresh, this);
    },
    removeDataSetListener : function(){
    	if(this.binder) {
    		var bds = this.binder.ds;
    		bds.un('metachange', this.onRefresh, this);
	    	bds.un('create', this.onCreate, this);
	    	bds.un('load', this.onRefresh, this);
	    	bds.un('valid', this.onValid, this);
	    	bds.un('remove', this.onRemove, this);
	    	bds.un('clear', this.onClear, this);
	    	bds.un('update', this.onUpdate, this);
	    	bds.un('fieldchange', this.onFieldChange, this);
	    	bds.un('indexchange', this.onRefresh, this);
    	}    	
    },
    destroy : function(){
//    	alert('destroy ' + this.id)
    	Aurora.CmpManager.remove(this.id);
    	this.removeDataSetListener();
    	delete this.wrap;
    },
    onRemove : function(ds, record){
    	if(this.binder.ds == ds && this.record == record){
    		this.clearValue();
    	}
    },
    onCreate : function(ds){
    	this.clearInvalid();
    	this.record = ds.getCurrentRecord();
    	this.setValue('',true);
    	this.fireEvent('valid', this, this.record, this.binder.name)
    },
    onRefresh : function(ds){
    	
    	this.clearInvalid();
		this.record = ds.getCurrentRecord();
		
		if(this.record) {
			var value = this.record.get(this.binder.name);			
			var field = this.record.getMeta().getField(this.binder.name);		
			var config={};
			Ext.apply(config,this.initConfig);		
			Ext.apply(config, field.snap);		
			this.initComponent(config);
			if(this.value == value) return;
			this.setValue(value,true);
		}else{
			this.setValue('',true);		
		}
//    	this.fireEvent('valid', this, this.record, this.binder.name)
    },
    onValid : function(ds, record, name, valid){
    	if(this.binder.ds == ds && this.binder.name == name && this.record == record){
	    	if(valid){
	    		this.fireEvent('valid', this, this.record, this.binder.name)
    			this.clearInvalid();
	    	}else{
	    		this.fireEvent('invalid', this, this.record, this.binder.name);
	    		this.markInvalid();
	    	}
    	}    	
    },
    onUpdate : function(ds, record, name,value){
    	if(this.binder.ds == ds && this.binder.name == name){
	    	this.setValue(value, true);
    	}
    },
    onFieldChange : function(ds, record, field){
    	if(this.binder.ds == ds && this.binder.name == field.name){
	    	this.onRefresh(ds);   	
    	}
    },
    onClear : function(ds){
    	this.clearValue();    
    },    
    setValue : function(v, silent){
    	this.value = v;
    	if(silent === true)return;
    	if(this.binder){
    		this.record = this.binder.ds.getCurrentRecord();
    		if(this.record == null){
    			var data = {};
    			data[this.binder.name] = v;
    			this.record  = this.binder.ds.create(data,false);
    			this.record.validate(this.binder.name);
    		}else{
    			this.record.set(this.binder.name,v);
    		}
    		if(v=='') delete this.record.data[this.binder.name];
    	}
    },
    clearInvalid : function(){},
    markInvalid : function(){},
    clearValue : function(){},
    initMeta : function(){},
    setDefault : function(){},
    setRequired : function(){},
    onDataChange : function(){}
});

Aurora.Field = Ext.extend(Aurora.Component,{	
	validators: [],
	requiredCss:'item-notBlank',
	focusCss:'item-focus',
	readOnlyCss:'item-readOnly',
	emptyTextCss:'item-emptyText',
	invalidCss:'item-invalid',
	constructor: function(config) {
		config.required = config.required || false;
		config.readonly = config.readonly || false;
        Aurora.Field.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	Aurora.Field.superclass.initComponent.call(this, config);
        this.el = this.wrap.child('input[atype=field.input]'); 
    	this.originalValue = this.getValue();
    	this.applyEmptyText();
    	this.initStatus();
    	if(this.hidden == true){
    		this.setVisible(false)
    	}
    },
    initEvents : function(){
    	Aurora.Field.superclass.initEvents.call(this);
        this.addEvents('keydown','keyup','keypress');
    	this.el.on(Ext.isIE || Ext.isSafari3 ? "keydown" : "keypress", this.fireKey,  this);
    	this.el.on("focus", this.onFocus,  this);
    	this.el.on("blur", this.onBlur,  this);
    	this.el.on("change", this.onChange, this);
    	this.el.on("keyup", this.onKeyUp, this);
        this.el.on("keydown", this.onKeyDown, this);
        this.el.on("keypress", this.onKeyPress, this);
        this.el.on("mouseover", this.onMouseOver, this);
        this.el.on("mouseout", this.onMouseOut, this);
    	
    },
    destroy : function(){
    	Aurora.Field.superclass.destroy.call(this);
    	this.el.un(Ext.isIE || Ext.isSafari3 ? "keydown" : "keypress", this.fireKey,  this);
    	this.el.un("focus", this.onFocus,  this);
    	this.el.un("blur", this.onBlur,  this);
    	this.el.un("change", this.onChange, this);
    	this.el.un("keyup", this.onKeyUp, this);
        this.el.un("keydown", this.onKeyDown, this);
        this.el.un("keypress", this.onKeyPress, this);
        this.el.un("mouseover", this.onMouseOver, this);
        this.el.un("mouseout", this.onMouseOut, this);
    	delete this.el;
    },
	setWidth: function(w){
		this.wrap.setStyle("width",(w+3)+"px");
		this.el.setStyle("width",w+"px");
	},
	setHeight: function(h){
		this.wrap.setStyle("height",h+"px");
		this.el.setStyle("height",(h-1)+"px");
	},
	move: function(x,y){
		this.wrap.setX(x);
		this.wrap.setY(y);
	},
	setVisible: function(v){
		if(v==true)
			this.wrap.show();
		else
			this.wrap.hide();
	},
    initStatus : function(){
    	this.setRequired(this.required);
    	this.setReadOnly(this.readonly);
    },
    onMouseOver : function(e){
    	//Aurora.ToolTip.show(this.id, "测试");
    },
    onMouseOut : function(e){
    	//Aurora.ToolTip.hide();
    },
    onChange : function(e){
//    	this.setValue(this.getValue());    
    },
    onKeyUp : function(e){
        this.fireEvent('keyup', this, e);
    },
    onKeyDown : function(e){
        this.fireEvent('keydown', this, e);
        if(e.keyCode == 13) {
        	e.keyCode = 9;
        	e.browserEvent.keyCode = 9;
        }
    },
    onKeyPress : function(e){
        this.fireEvent('keypress', this, e);
    },
    fireKey : function(e){
      this.fireEvent("keydown", this, e);
    },
    onFocus : function(e){
    	if(this.readonly) return;
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.select.defer(10,this);
            this.fireEvent("focus", this);
            if(this.emptytext){
	            if(this.el.dom.value == this.emptytext){
	                this.setRawValue('');
	            }
	            this.wrap.removeClass(this.emptyTextCss);
	        }
	        this.wrap.addClass(this.focusCss);
        }
    },
    processValue : function(v){
    	return v;
    },
    onBlur : function(e){
        this.hasFocus = false;
//        this.validate();
        var rv = this.getRawValue();
        rv = this.processValue(rv);
        if(String(rv) !== String(this.startValue)){
            this.fireEvent('change', this, rv, this.startValue);
        }
//        this.applyEmptyText();
        this.setValue(rv);
        this.wrap.removeClass(this.focusCss);
        this.fireEvent("blur", this);
    },
    setValue : function(v, silent){
    	Aurora.Field.superclass.setValue.call(this,v, silent);
    	if(this.emptytext && this.el && v !== undefined && v !== null && v !== ''){
            this.wrap.removeClass(this.emptyTextCss);
        }
        this.el.dom.value = this.formatValue((v === null || v === undefined ? '' : v));
        this.applyEmptyText();
    },
    formatValue : function(v){
    	return v;
    },
    getRawValue : function(){
        var v = this.el.getValue();
        if(v === this.emptytext || v === undefined){
            v = '';
        }
        return v;
    },
    getValue : function(){
    	var v= this.value;
		v=(v === null || v === undefined ? '' : v);
		return v;
    },
    setRequired : function(required){
    	if(this.crrentRequired == required)return;
		this.clearInvalid();    	
    	this.crrentRequired = required;
    	if(required){
    		this.wrap.addClass(this.requiredCss);
    	}else{
    		this.wrap.removeClass(this.requiredCss);
    	}
    },
    setReadOnly : function(readonly){
    	if(this.currentReadOnly == readonly)return;
    	this.currentReadOnly = readonly;
    	this.el.dom.readOnly = readonly;
    	if(readonly){
    		this.wrap.addClass(this.readOnlyCss);
    	}else{
    		this.wrap.removeClass(this.readOnlyCss);
    	}
    },
    applyEmptyText : function(){
        if(this.emptytext && this.getRawValue().length < 1){
            this.setRawValue(this.emptytext);
            this.wrap.addClass(this.emptyTextCss);
        }
    },
//    validate : function(){
//        if(this.readonly || this.validateValue(this.getValue())){
//            this.clearInvalid();
//            return true;
//        }
//        return false;
//    },
    clearInvalid : function(){
    	this.invalidMsg = null;
    	this.wrap.removeClass(this.invalidCss);
//    	this.fireEvent('valid', this);
    },
    markInvalid : function(msg){
    	this.invalidMsg = msg;
    	this.wrap.addClass(this.invalidCss);
//    	this.fireEvent('invalid', this, msg);
    },
//    validateValue : function(value){    
//    	if(value.length < 1 || value === this.emptyText){ // if it's blank
//        	if(!this.required){
//                this.clearInvalid();
//                return true;
//        	}else{
//                this.markInvalid('字段费控');//TODO:测试
//        		return false;
//        	}
//        }
//    	Ext.each(this.validators.each, function(validator){
//    		var vr = validator.validate(value)
//    		if(vr !== true){
//    			//TODO:
//    			return false;
//    		}    		
//    	})
//        return true;
//    },
    select : function(start, end){
    	var v = this.getRawValue();
        if(v.length > 0){
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if(d.setSelectionRange){  
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart("character", start);
                range.moveEnd("character", end-v.length);
                range.select();
            }
        }
    },
    setRawValue : function(v){
        return this.el.dom.value = (v === null || v === undefined ? '' : v);
    },
    reset : function(){
    	this.setValue(this.originalValue);
        this.clearInvalid();
        this.applyEmptyText();
    },
    focus : function(){
    	if(this.readonly) return;
    	this.el.dom.focus();
    	var sf = this;
        setTimeout(function(){
        	sf.el.dom.select();
        },10)
    },
    blur : function(){
    	if(this.readonly) return;
    	this.el.blur();
    },
    clearValue : function(){
    	this.setValue('', true);
    	this.clearInvalid();
        this.applyEmptyText();
    }
})
Aurora.TextField = Ext.extend(Aurora.Field,{
	constructor: function(config) {
        Aurora.TextField.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	Aurora.TextField.superclass.initComponent.call(this, config);    	
    },
    initEvents : function(){
    	Aurora.TextField.superclass.initEvents.call(this);    	
    }
//    ,getValue : function(){
//    	return this.getRawValue();
//    }
})
Aurora.TriggerField = Ext.extend(Aurora.TextField,{
	constructor: function(config) {
        Aurora.TriggerField.superclass.constructor.call(this, config);
    },
    initComponent : function(config){
    	Aurora.TriggerField.superclass.initComponent.call(this, config);
    	this.trigger = this.wrap.child('div[atype=triggerfield.trigger]'); 
    	this.popup = this.wrap.child('div[atype=triggerfield.popup]'); 
    },
    initEvents : function(){
    	Aurora.TriggerField.superclass.initEvents.call(this);    
    	this.trigger.on('click',this.onTriggerClick, this, {preventDefault:true})
    },
    isExpanded : function(){    	
        return this.popup && this.popup.isVisible();
    },
    setWidth: function(w){
		this.wrap.setStyle("width",(w+3)+"px");
		this.el.setStyle("width",(w-20)+"px");
	},
    onFocus : function(){
        Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this, {delay: 10});
        Aurora.TriggerField.superclass.onFocus.call(this);
        if(!this.isExpanded())this.expand();
    },
    onBlur : function(){
    	this.hasFocus = false;
        this.wrap.removeClass(this.focusCss);
        this.fireEvent("blur", this);
    },
	destroy : function(){
		if(this.isExpanded()){
    		this.collapse();
    	}
    	this.trigger.un('click',this.onTriggerClick, this)
    	delete this.trigger;
    	delete this.popup;
    	Aurora.TriggerField.superclass.destroy.call(this);
	},
    triggerBlur : function(e){
    	if(!this.wrap.contains(e.target)){
    		Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
            if(this.isExpanded()){
	    		this.collapse();
	    	}	    	
        }
    },
    setVisible : function(v){
    	Aurora.TriggerField.superclass.setVisible.call(this,v);
    	if(v == false && this.isExpanded()){
    		this.collapse();
    	}
    },
    collapse : function(){
    	this.wrap.setStyle("z-index",20);
    	this.popup.hide();
    },
    expand : function(){
    	this.wrap.setStyle("z-index",25);
    	this.popup.show();
    },
    onTriggerClick : function(){
    	if(this.readonly) return;
    	if(this.isExpanded()){
    		this.collapse();
    	}else{
	    	this.el.focus();
    		this.expand();
    	}
    }
});
Aurora.Box = Ext.extend(Aurora.Component,{
	constructor: function(config) {
        this.errors = [];
        Aurora.Box.superclass.constructor.call(this,config);
    },
//    initComponent : function(config){ 
//		config = config || {};
//        Ext.apply(this, config); 
        //TODO:所有的组件?
//        for(var i=0;i<this.cmps.length;i++){
//    		var cmp = $(this.cmps[i]);
//    		if(cmp){
//	    		cmp.on('valid', this.onValid, this)
//	    		cmp.on('invalid', this.onInvalid,this)
//    		}
//    	}
//    },
    initEvents : function(){
//    	this.addEvents('focus','blur','change','invalid','valid');    	
    },
    onValid : function(cmp, record, name){
    	this.clearError(cmp.id);
    },
    onInvalid : function(cmp, record, name){
    	var error = record.errors[name];
    	if(error){
    		this.showError(cmp.id,error.message)
    	}
    },
    showError : function(id, msg){
    	Ext.fly(id+'_vmsg').update(msg)
    },
    clearError : function(id){
    	Ext.fly(id+'_vmsg').update('')
    },
    clearAllError : function(){
    	for(var i=0;i<this.errors.length;i++){
    		this.clearError(this.errors[i])
    	}
    }
});
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
		if(this.rendered===false)this.initQuery();
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
			ds = $(name);
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
        	this.popup.update('<ul></ul>');
			this.view=this.popup.child('ul');
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
		var value =target.attributes['itemValue'].value;
		this.setValue(value);
//		this.focus()
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
		this.litp=new Aurora.Template('<li tabIndex="{index}" itemValue="{'+this.valuefield+'}">{'+this.displayfield+'}&#160;</li>');
		var datas = this.optionDataSet.getAll();
		var l=datas.length;
		var sb = [];
		for(var i=0;i<l;i++){
			var d = Aurora.apply(datas[i].data, {index:i})
			sb.add(this.litp.applyTemplate(d));	//等数据源明确以后再修改		
		}
		if(l!=0){
			this.view.update(sb.join(''));			
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
	destroy : function(){
		if(this.view){
			this.view.un('click', this.onViewClick,this);
			this.view.un('mouseover',this.onViewOver,this);
			this.view.un('mousemove',this.onViewMove,this);
		}
		delete this.view;
    	Aurora.ComboBox.superclass.destroy.call(this);
	},
	getText : function() {		
		return this.text;
	},
	processValue : function(rv){
		var r = this.optionDataSet == null ? null : this.optionDataSet.find(this.displayfield, rv);
		if(r != null){
			return r.get(this.valuefield);
		}else{
			return this.value;
		}
	},
	formatValue : function(){
		var v = this.getValue();
		var r = this.optionDataSet == null ? null : this.optionDataSet.find(this.valuefield, v);
		this.text = '';
		if(r != null){
			this.text = r.get(this.displayfield);
		}else{
//			this.value = ''
		}
		return this.text;
	},
//	setValue:function(v,silent){
//        Aurora.ComboBox.superclass.setValue.call(this, v, silent);
//	},
	getIndex:function(v){
		var datas = this.optionDataSet.getAll();		
		var l=datas.length;
		for(var i=0;i<l;i++){
			if(datas[i].data[this.valuefield]==v){				
				return i;
			}
		}		
	}
});
Aurora.DateField = Ext.extend(Aurora.Component, {
	constructor: function(config) {
        Aurora.DateField.superclass.constructor.call(this,config); 
		this.draw();
    },
    initComponent : function(config){
    	Aurora.DateField.superclass.initComponent.call(this, config);
    	this.wrap = typeof(config.container) == "string" ? Ext.get(config.container) : config.container;
        this.table = this.wrap.child("table");        
        this.tbody = this.wrap.child("tbody").dom;
    	this.days = [];
    	this.selectDays = this.selectDays||[];
    	this.date = this.date||new Date();
		this.year = this.date.getFullYear();
		this.month = this.date.getMonth() + 1;
    	this.preMonthBtn = this.wrap.child("div.item-dateField-pre");
    	this.nextMonthBtn = this.wrap.child("div.item-dateField-next");
    	this.yearSpan = this.wrap.child("span.item-dateField-year");
    	this.monthSpan = this.wrap.child("span.item-dateField-month");
    },
    initEvents : function(){
    	Aurora.DateField.superclass.initEvents.call(this);    
    	this.preMonthBtn.on("click", this.preMonth, this);
    	this.nextMonthBtn.on("click", this.nextMonth, this);
    	this.table.on("click", this.onSelect, this);
    	this.table.on("mouseover", this.mouseOver, this);
    	this.table.on("mouseout", this.mouseOut, this)
    	this.addEvents('select');
    },
    destroy : function(){
    	this.preMonthBtn.un("click", this.preMonth, this);
    	this.nextMonthBtn.un("click", this.nextMonth, this);
    	this.table.un("click", this.onSelect, this);
    	this.table.un("mouseover", this.mouseOver, this);
    	this.table.un("mouseout", this.mouseOut, this)
		delete this.preMonthBtn;
    	delete this.nextMonthBtn;
    	delete this.yearSpan;
    	delete this.monthSpan; 
    	delete this.table;        
        delete this.tbody;
    	Aurora.DateField.superclass.destroy.call(this);
	},
    mouseOut: function(e){
    	if(this.overTd) Ext.fly(this.overTd).removeClass('dateover');
    },
    mouseOver: function(e){
    	if(this.overTd) Ext.fly(this.overTd).removeClass('dateover');
    	if(Ext.fly(e.target).hasClass('item-day') && e.target.date != 0){
    		this.overTd = e.target; 
    		Ext.fly(this.overTd).addClass('dateover');
    	}
    	
    },
    onSelect: function(e){
    	if(this.singleSelect === false){
    		
    	}else{
    		if(this.selectedDay) Ext.fly(this.selectedDay).removeClass('onSelect');
    		if(Ext.fly(e.target).hasClass('item-day') && e.target.date != 0){
	    		this.selectedDay = e.target; 
	    		this.onSelectDay(this.selectedDay);
	    		this.fireEvent('select', this, this.selectedDay.date);
	    	}
    	}
    },
	onSelectDay: function(o){
		if(!Ext.fly(o).hasClass('onSelect'))Ext.fly(o).addClass('onSelect');
	},
	//在选择日期触发
	onToday: function(o){
		o.className = "onToday";
	},//在当天日期触发
	afterFinish: function(){
		for(var i=0;i<this.selectDays.length;i++){
			var d = this.selectDays[i];
			if(d.getFullYear() == this.year && d.getMonth()+1 == this.month){
				this.onSelectDay(this.days[d.getDate()]);
			}
		}		
	},
    //当前月
	nowMonth: function() {
		this.predraw(new Date());
	},
	//上一月
	preMonth: function() {
		this.predraw(new Date(this.year, this.month - 2, 1));
	},
	//下一月
	nextMonth: function() {
		this.predraw(new Date(this.year, this.month, 1));
	},
	//上一年
	preYear: function() {
		this.predraw(new Date(this.year - 1, this.month - 1, 1));
	},
	//下一年
	nextYear: function() {
		this.predraw(new Date(this.year + 1, this.month - 1, 1));
	},
  	//根据日期画日历
  	predraw: function(date) {
		//再设置属性
		this.year = date.getFullYear(); this.month = date.getMonth() + 1;
		//重新画日历
		this.draw();
  	},
  	//画日历
	draw: function() {
//		return;
		//用来保存日期列表
		var arr = [];
		//用当月第一天在一周中的日期值作为当月离第一天的天数
		for(var i = 1, firstDay = new Date(this.year, this.month - 1, 1).getDay(); i <= firstDay; i++){ 
			arr.push(0); 
		}
		//用当月最后一天在一个月中的日期值作为当月的天数
		for(var i = 1, monthDay = new Date(this.year, this.month, 0).getDate(); i <= monthDay; i++){ 
			arr.push(i); 
		}
		//清空原来的日期对象列表
		this.days = [];
		//先清空内容再插入(ie的table不能用innerHTML)
		while(this.tbody.hasChildNodes()){ 
			this.tbody.removeChild(this.tbody.firstChild); 
		}
		
		//插入日期
//		if(!this.tbody) this.tbody = document.createElement("TBODY");
		while(arr.length){
			//每个星期插入一个tr
			var row = document.createElement("tr");
			//每个星期有7天
			for(var i = 1; i <= 7; i++){
				var cell = document.createElement("td"); 
				cell.className = "item-day";
				cell.innerHTML = "&nbsp;";
				cell.date=0;
				if(arr.length){
					var d = arr.shift();
					if(d){
						cell.innerHTML = d;
						this.days[d] = cell;
						var on = new Date(this.year, this.month - 1, d);
						cell.date=on;
						//判断是否今日
						this.isSame(on, new Date()) && this.onToday(cell);
						//判断是否选择日期
						this.selectDay && this.isSame(on, this.selectDay) && this.onSelectDay(cell);
					}
				}
				row.appendChild(cell);
			}
			this.tbody.appendChild(row);
		}
		
		
		this.yearSpan.dom.innerHTML = this.year; 
		this.monthSpan.dom.innerHTML = this.month;
		this.afterFinish();
	},
	//判断是否同一日
	isSame: function(d1, d2) {
		return (d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate());
	}
});
Aurora.DatePicker = Ext.extend(Aurora.TriggerField,{
	constructor: function(config) {
        Aurora.DatePicker.superclass.constructor.call(this, config);        
    },
    initComponent : function(config){
    	Aurora.DatePicker.superclass.initComponent.call(this,config);
    	if(!this.dateField){
    		var cfg = {id:this.id+'_df',container:this.popup}
	    	this.dateField = new Aurora.DateField(cfg);
	    	this.dateField.on("select", this.onSelect, this);
    	}
    },
    onSelect: function(dateField, date){
    	this.setValue(date)
    	this.collapse();
    },
    setValue:function(v,silent){
        Aurora.DatePicker.superclass.setValue.call(this, v, silent);
        this.dateField.selectDay = this.getValue();
        this.dateField.predraw(this.getValue());
	},
    formatValue : function(date){
    	if(date instanceof Date) {
    		return Aurora.formateDate(date);
    	}else{
    		return date;
    	}
    },
    destroy : function(){
    	Aurora.DatePicker.superclass.destroy.call(this);
	}
});