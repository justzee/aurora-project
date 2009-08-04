Aurora.Window = Ext.extend(Ext.util.Observable,{
	constructor: function(id, config) {	
        config = config || {};
        Ext.apply(this, config);
        window[id] = this;
        this.id = id;
        this.draggable = true;
        Aurora.Field.superclass.constructor.call(this);
        this.initComponent();
        this.initEvents();
    },
    initComponent : function(){
    	var sf = this; 
    	var windowTpl = new Ext.Template(sf.getTemplate());
    	sf.width = sf.width||350;sf.height=sf.height||400;
        sf.wrap = windowTpl.append(document.body, {title:sf.title,width:sf.width,height:sf.height}, true);
    	sf.title = sf.wrap.child('div[atype=window.title]');
    	sf.head = sf.wrap.child('td[atype=window.head]');
        sf.closeBtn = sf.wrap.child('div[atype=window.close]');
        if(sf.draggable) sf.initDraggable();
        sf.center();
    },
    initEvents : function(){
    	this.closeBtn.on("click", this.onClose,  this);    	
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
            '<TABLE id="myWindow" unselectable="on" class="window-wrap" style="width:{width}px;height:{height}px;" cellSpacing="0" cellPadding="0" border="0">',
			'<TBODY>',
			'<TR style="height:21px;" >',
				'<TD class="window-caption">',
					'<TABLE cellSpacing="0" cellPadding="1" width="100%" height="100%" border="0" unselectable="on">',
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
					'<DIV class="window-content" unselectable="on"></DIV>',
				'</TD>',
			'</TR>',
			'</TBODY>',
		'</TABLE>'
        ];
    },
    onMouseDown : function(e){
    	e.stopEvent();
    	var xy = this.wrap.getXY();
    	this.relativeX=xy[0]-e.getPageX();
		this.relativeY=xy[1]-e.getPageY();
    	Ext.get(document.documentElement).on("mousemove", this.onMouseMove, this);
    	Ext.get(document.documentElement).on("mouseup", this.onMouseUp, this);
    },
    onMouseUp : function(e){
    	Ext.get(document.documentElement).un("mousemove", this.onMouseMove, this);
    	Ext.get(document.documentElement).un("mouseup", this.onMouseUp, this);
    	this.wrap.moveTo(this.proxy.getX(),this.proxy.getY());
    	this.proxy.hide();
    },
    onMouseMove : function(e){
    	e.stopEvent();
    	if(!this.proxy) this.initProxy();
    	this.proxy.show();
    	this.proxy.moveTo(e.getPageX()+this.relativeX,e.getPageY()+this.relativeY);
    },
    initProxy : function(){
    	var p = '<DIV style="border:1px dashed black;Z-INDEX: 30; LEFT: 0px; WIDTH: 100%; CURSOR: default; POSITION: absolute; TOP: 0px; HEIGHT: 621px;" unselectable="on"></DIV>'
    	this.proxy = Ext.get(Ext.DomHelper.append(Ext.getBody(),p));
    	this.proxy.setWidth(this.wrap.getWidth());
    	this.proxy.setHeight(this.wrap.getHeight());
    	this.proxy.setX(this.wrap.getX());
    	this.proxy.setY(this.wrap.getY());
    },
    onClose : function(e){
    	e.stopEvent();
    	if(this.draggable) this.head.un('mousedown', this.onMouseDown, this);
    	this.closeBtn.un("click", this.onClose, this);
    	if(this.proxy) this.proxy.remove();
    	this.wrap.remove();
    }
});