Aurora = {version: '3.0'};

Aurora.onReady = Ext.onReady;
Aurora.decode = Ext.decode;
Aurora.Element = Ext.Element;
Aurora.Template = Ext.Template
Aurora.apply = Ext.apply;
Aurora.isEmpty = Ext.isEmpty;
Aurora.fly = Ext.fly;
Aurora.get= Ext.get;

Aurora.winContainers = [];
Aurora.cmps = {};

Ext.Ajax.on("requestexception", function(conn, response, options){
	alert('服务器端错误!');
}, this);
$ = Aurora.getCmp = function(name){
	var cmp = Aurora.cmps[name]
	if(!cmp){
		cmp = Aurora.DataSetManager.get(name)
	}
	return cmp;
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
			if(!window._mask) {
				var p = '<DIV style="left:0px;top:0px;width:100%;height:100%;POSITION: absolute;FILTER: alpha(opacity=50);BACKGROUND-COLOR: #cccccc; opacity: 0.5; MozOpacity: 0.5" unselectable="on"></DIV>';
				window._mask = Ext.get(Ext.DomHelper.append(Ext.getBody(),p));
			}
	    	window._mask.setStyle('z-index', Ext.fly(el).getStyle('z-index') - 1);
		},
		unmask : function(el){
			if(window._mask) {
				Ext.fly(window._mask).remove();
				window._mask = null;
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
            	if(this.readyState && this.readyState == "loading") return; 
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
        }
        
        var el = document.getElementById(id);
        if(el){Ext.removeNode(el);}
        if(typeof callback == "function"){
            callback();
        }
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