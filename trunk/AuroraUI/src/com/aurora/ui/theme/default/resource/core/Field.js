
Aurora.Field = Ext.extend(Ext.util.Observable,{	
	validators: [],
	notBlankCss:'item-notBlank',
	readOnlyCss:'item-readOnly',
	emptyTextCss:'item-emptyText',
	invalidCss:'item-invalid',
	constructor: function(id, config) {	
        config = config || {};
        Ext.apply(this, config);
        window[id] = this;
        this.id = id;
        Aurora.Field.superclass.constructor.call(this);       
        this.wrap = Ext.get(id);
        this.el = this.wrap.child('input[atype=field.input]'); 
        this.addEvents('focus','blur','keydown','change','invalid','valid','keyup','keypress');
        this.initComponent();
        this.initEvents();
    },
    initComponent : function(){
    	this.originalValue = this.getValue();
    	this.applyEmptyText();
    },
    initEvents : function(){
    	this.el.on(Ext.isIE || Ext.isSafari3 ? "keydown" : "keypress", this.fireKey,  this);
    	this.el.on("focus", this.onFocus,  this);
    	this.el.on("blur", this.onBlur,  this);
    	this.el.on("keyup", this.onKeyUp, this);
        this.el.on("keydown", this.onKeyDown, this);
        this.el.on("keypress", this.onKeyPress, this);
        this.el.on("mouseover", this.onMouseOver, this);
        this.el.on("mouseout", this.onMouseOut, this);
    	
    },
    onMouseOver : function(e){
    	Aurora.ToolTip.show(this.id, "测试");
    },
    onMouseOut : function(e){
    	Aurora.ToolTip.hide();
    },
    onKeyUp : function(e){
        this.fireEvent('keyup', this, e);
    },
    onKeyDown : function(e){
        this.fireEvent('keydown', this, e);
    },
    onKeyPress : function(e){
        this.fireEvent('keypress', this, e);
    },
    fireKey : function(e){
      this.fireEvent("keydown", this, e);
    },
    onFocus : function(){
    	if(this.readOnly) return;
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.select.defer(10,this);
            this.fireEvent("focus", this);
            if(this.emptyText){
	            if(this.el.dom.value == this.emptyText){
	                this.setRawValue('');
	            }
	            this.wrap.removeClass(this.emptyTextCss);
	        }
        }
    },
    onBlur : function(e){
        this.hasFocus = false;
        this.validate();
        var v = this.getValue();
        if(String(v) !== String(this.startValue)){
            this.fireEvent('change', this, v, this.startValue);
        }
        this.applyEmptyText();
        this.fireEvent("blur", this);
    },
    
    setValue : function(v){
    	if(this.emptyText && this.el && v !== undefined && v !== null && v !== ''){
            this.wrap.removeClass(this.emptyTextCss);
        }
        this.value = v;
        this.el.dom.value = (v === null || v === undefined ? '' : v);
        this.validate();
        this.applyEmptyText();
    },
    getValue : function(){
        var v = this.el.getValue();
        if(v === this.emptyText || v === undefined){
            v = '';
        }
        return v;
    },
    setNotBlank : function(notBlank){
		this.clearInvalid();    	
    	this.notBlank = notBlank;
    	if(notBlank){
    		this.wrap.addClass(this.notBlankCss);
    	}else{
    		this.wrap.removeClass(this.notBlankCss);
    	}
    },
    setReadOnly : function(readOnly){
    	this.readOnly = readOnly;
    	this.el.dom.readOnly = readOnly;
    	if(readOnly){
    		this.wrap.addClass(this.readOnlyCss);
    	}else{
    		this.wrap.removeClass(this.readOnlyCss);
    	}
    },
    applyEmptyText : function(){
        if(this.emptyText && this.getValue().length < 1){
            this.setRawValue(this.emptyText);
            this.wrap.addClass(this.emptyTextCss);
        }
    },
    validate : function(){
        if(this.readOnly || this.validateValue(this.getValue())){
            this.clearInvalid();
            return true;
        }
        return false;
    },
    clearInvalid : function(){
    	this.invalidMsg = null;
    	this.wrap.removeClass(this.invalidCss);
    	this.fireEvent('valid', this);
    },
    markInvalid : function(msg){
    	this.invalidMsg = msg;
    	this.wrap.addClass(this.invalidCss);
    	this.fireEvent('invalid', this, msg);
    },
    validateValue : function(value){    
    	if(value.length < 1 || value === this.emptyText){ // if it's blank
        	if(!this.notBlank){
                this.clearInvalid();
                return true;
        	}else{
                this.markInvalid('字段费控');//TODO:测试
        		return false;
        	}
        }
    	Ext.each(this.validators.each, function(validator){
    		var vr = validator.validate(value)
    		if(vr !== true){
    			//TODO:
    			return false;
    		}    		
    	})
        return true;
    },
    select : function(start, end){
    	var v = this.getValue();
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
    focus : function(selectText, delay){
    	if(this.readOnly) return;
        this.el.dom.select();
    },
    blur : function(){
    	if(this.readOnly) return;
    	this.el.blur();
    }
})