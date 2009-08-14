Aurora.Button = Ext.extend(Ext.util.Observable,{
	constructor: function(config) {	
        config = config || {};
        Ext.apply(this, config);
        window[this.id] = this;
        Aurora.Button.superclass.constructor.call(this);       
        this.el = Ext.get(this.id);
        this.addEvents('click','mouseout','mouseover');
        this.initComponent();
        this.initEvents();
    },
    initComponent : function(){
    	
    },
    initEvents : function(){
    	this.el.on("click", this.onClick,  this);
    	this.el.on("mouseover", this.onMouseOver,  this);
    	this.el.on("mouseout", this.onMouseOut,  this);
    },
    disable: function(){
    	this.el.dom.disabled = true;
    },
    enable: function(){
    	this.el.dom.disabled = false;
    },
    onClick: function(e){
    	this.fireEvent("click", this);
    },
    onMouseOver: function(e){
    	this.fireEvent("mouseover", this);
    },
    onMouseOut: function(e){
    	this.fireEvent("mouseout", this);
    }
});