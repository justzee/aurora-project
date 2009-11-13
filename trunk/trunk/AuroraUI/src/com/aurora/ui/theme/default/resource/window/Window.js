Aurora.WindowManager = function(){
    return {
        put : function(win){
        	if(!this.cache) this.cache = [];
        	this.cache.add(win)
        },
        getAll : function(){
        	return this.cache;
        },
        remove : function(win){
        	this.cache.remove(win);
        },
        get : function(id){
        	if(!this.cache) return null;
        	var win = null;
        	for(var i = 0;i<this.cache.length;i++){
    			if(this.cache[i].id == id) {
	        		win = this.cache[i];
    				break;      			
        		}
        	}
        	return ds;
        }
    };
}();

Aurora.Window = Ext.extend(Aurora.Component,{
	constructor: function(config) { 
        this.draggable = true;
        this.closeable = true;
        this.modal = true;
        this.oldcmps = {};
        this.cmps = {};
        Aurora.Window.superclass.constructor.call(this,config);
    },
    initComponent : function(config){
    	Aurora.Window.superclass.initComponent.call(this, config);
    	var sf = this; 
    	Aurora.WindowManager.put(sf);
    	var windowTpl = new Ext.Template(sf.getTemplate());
    	sf.width = sf.width||350;sf.height=sf.height||400;
        sf.wrap = windowTpl.append(document.body, {title:sf.title,width:sf.width,bodywidth:sf.width-2,height:sf.height}, true);
    	sf.title = sf.wrap.child('div[atype=window.title]');
    	sf.head = sf.wrap.child('td[atype=window.head]');
    	sf.body = sf.wrap.child('div[atype=window.body]');
        sf.closeBtn = sf.wrap.child('div[atype=window.close]');
        if(sf.draggable) sf.initDraggable();
        if(!sf.closeable)sf.closeBtn.hide();
        if(sf.modal) Aurora.Mask.mask(sf.wrap);
        if(sf.url){
        	sf.showLoading();       
        	sf.load(sf.url)
        }
        sf.center();
    },
    initEvents : function(){
    	if(this.closeable) this.closeBtn.on("click", this.onClose,  this); 
    	this.wrap.on("click", this.focus, this);
    },
    initDraggable: function(){
    	this.head.addClass('item-draggable');
    	this.head.on('mousedown', this.onMouseDown,this);
    },
    center: function(){
    	var screenWidth = Aurora.getViewportWidth();
    	var screenHeight = Aurora.getViewportHeight();
    	var x = (screenWidth - this.width)/2;
    	var y = (screenHeight - this.height)/2;
        this.wrap.moveTo(x,y);
    },
    getTemplate : function() {
        return [
            '<TABLE unselectable="on" class="window-wrap" style="width:{width}px;height:{height}px;" cellSpacing="0" cellPadding="0" border="0">',
			'<TBODY>',
			'<TR style="height:21px;" >',
				'<TD class="window-caption">',
					'<TABLE cellSpacing="0"  cellPadding="1" width="100%" height="100%" border="0" unselectable="on">',
						'<TBODY>',
						'<TR>',
							'<TD unselectable="on" class="window-caption-label" atype="window.head" width="99%">',
								'<DIV unselectable="on" atype="window.title" unselectable="on">{title}</DIV>',
							'</TD>',
							'<TD unselectable="on" class="window-caption-button" noWrap>',
								'<DIV class="window-close" atype="window.close" unselectable="on"></DIV>',
							'</TD>',
						'</TR>',
						'</TBODY>',
					'</TABLE>',
				'</TD>',
			'</TR>',
			'<TR style="height:99%">',
				'<TD class="window-body" vAlign="top" unselectable="on">',
					'<DIV class="window-content" atype="window.body" style="position:relatvie;width:{bodywidth}px;height:{height}px;" unselectable="on"></DIV>',
				'</TD>',
			'</TR>',
			'</TBODY>',
		'</TABLE>'
        ];
    },
    focus : function(){
    	var wins = Aurora.WindowManager.getAll();
    	for(var i=0;i<wins.length;i++){
    		var zindex = wins[i].wrap.getStyle('z-index');
    		if(zindex == 45)
    		wins[i].wrap.setStyle('z-index', 40);  
    	}
    	this.wrap.setStyle('z-index', 45);
    },
    onMouseDown : function(e){
    	var sf = this; 
    	e.stopEvent();
    	sf.focus();
    	var xy = sf.wrap.getXY();
    	sf.relativeX=xy[0]-e.getPageX();
		sf.relativeY=xy[1]-e.getPageY();
    	Ext.get(document.documentElement).on("mousemove", sf.onMouseMove, sf);
    	Ext.get(document.documentElement).on("mouseup", sf.onMouseUp, sf);
    },
    onMouseUp : function(e){
    	var sf = this; 
    	Ext.get(document.documentElement).un("mousemove", sf.onMouseMove, sf);
    	Ext.get(document.documentElement).un("mouseup", sf.onMouseUp, sf);
    	if(sf.proxy){
    		sf.wrap.moveTo(sf.proxy.getX(),sf.proxy.getY());
	    	sf.proxy.hide();
    	}
    },
    onMouseMove : function(e){
    	e.stopEvent();
    	if(!this.proxy) this.initProxy();
    	this.proxy.show();
    	this.proxy.moveTo(e.getPageX()+this.relativeX,e.getPageY()+this.relativeY);
    },
    showLoading : function(){
    	this.body.update('正在加载...');
    	this.body.setStyle('text-align','center');
    	this.body.setStyle('line-height',5);
    },
    clearLoading : function(){
    	this.body.update('');
    	this.body.setStyle('text-align','');
    	this.body.setStyle('line-height','');
    },
    initProxy : function(){
    	var sf = this; 
    	var p = '<DIV style="border:1px dashed black;Z-INDEX: 10000; LEFT: 0px; WIDTH: 100%; CURSOR: default; POSITION: absolute; TOP: 0px; HEIGHT: 621px;" unselectable="on"></DIV>'
    	sf.proxy = Ext.get(Ext.DomHelper.append(Ext.getBody(),p));
    	var xy = sf.wrap.getXY();
    	sf.proxy.setWidth(sf.wrap.getWidth());
    	sf.proxy.setHeight(sf.wrap.getHeight());
    	sf.proxy.setLocation(xy[0], xy[1]);
    },
    onClose : function(e){
    	Aurora.WindowManager.remove(this);
    	this.destroy();    	
    },
    destroy : function(){
    	for(var key in this.cmps){
    		var cmp = this.cmps[key];
    		if(cmp.destroy){
    			try{
    				cmp.destroy();
    			}catch(e){
    				alert(e)
    			}
    		}
    	}
    	var wrap = this.wrap;
    	if(this.proxy) this.proxy.remove();
    	if(this.modal) Aurora.Mask.unmask(this.wrap);
    	this.wrap.un("click", this.focus, this);
    	this.head.un('mousedown', this.onMouseDown,this);
    	this.closeBtn.un("click", this.onClose,  this);
    	delete this.title;
    	delete this.head;
    	delete this.body;
        delete this.closeBtn;
        delete this.proxy;
    	Aurora.Window.superclass.destroy.call(this);
        wrap.remove();
    },
    load : function(url){
    	var cmps = Aurora.CmpManager.getAll();
    	for(var key in cmps){
    		this.oldcmps[key] = cmps[key];
    	}
    	Ext.Ajax.request({
			url: url,
		   	success: this.onLoad.createDelegate(this)
		});		
    },
    setChildzindex : function(z){
    	for(var key in this.cmps){
    		var c = this.cmps[key];
    		c.setZindex(z)
    	}
    },
    onLoad : function(response, options){
    	this.clearLoading();
    	var html = response.responseText;
    	var sf = this
    	this.body.update(html,true,function(){
	    	var cmps = Aurora.CmpManager.getAll();
	    	for(var key in cmps){
	    		if(sf.oldcmps[key]==null){	    			
	    			sf.cmps[key] = cmps[key];
	    		}
	    	}
    	});
    }
});