Aurora.Window = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {
		var sf = this; 
        sf.draggable = true;
        sf.closeable = true;
        sf.modal = true;
        config = config || {};
        Ext.apply(this, config);
        sf.id = Ext.id();
        Aurora.Window.superclass.constructor.call(sf);
        sf.initComponent();
        sf.initEvents();
    },
    initComponent : function(){
    	var sf = this; 
    	Aurora.winContainers.push(sf);
    	var windowTpl = new Ext.Template(sf.getTemplate());
    	sf.width = sf.width||350;sf.height=sf.height||400;
        sf.wrap = windowTpl.append(document.body, {title:sf.title,width:sf.width,height:sf.height}, true);
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
    	var x = (document.body.clientWidth - this.width)/2;
    	var y = (document.body.clientHeight - this.height)/2;
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
				'<TD class="window-body" vAlign="top" unselectable="on" align="center">',
					'<DIV class="window-content" atype="window.body" style="position:relatvie" unselectable="on"></DIV>',
				'</TD>',
			'</TR>',
			'</TBODY>',
		'</TABLE>'
        ];
    },
    focus : function(){
    	for(var i=0;i<Aurora.winContainers.length;i++){
    		var zindex = Aurora.winContainers[i].wrap.getStyle('z-index');
    		if(zindex == 45)
    		Aurora.winContainers[i].wrap.setStyle('z-index', 40);    		
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
    	this.body.setStyle('line-height',5);
    },
    clearLoading : function(){
    	this.body.update('');
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
    	var sf = this; 
    	e.stopEvent();
    	Aurora.winContainers.remove(sf);
    	if(sf.draggable) sf.head.un('mousedown', sf.onMouseDown, sf);
    	sf.closeBtn.un("click", sf.onClose, sf);
    	if(sf.proxy) sf.proxy.remove();
    	if(sf.modal) Aurora.Mask.unmask(this.wrap);
    	sf.wrap.remove();
    },
    load : function(url){
    	Ext.Ajax.request({
			url: url,
		   	success: this.onLoad.createDelegate(this)
		});		
    },
    onLoad : function(response, options){
    	this.clearLoading();
    	var html = response.responseText;
    	this.body.update(html,true);
//		this.body.dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "").replace(/(?:<link.*?>)((\n|\r|.)*?)/ig, "");
//		var content = Ext.fly(this.body.dom.firstChild);
//		this.wrap.setWidth(content.getWidth()+3);
//		this.wrap.setHeight(content.getHeight())
    }
});