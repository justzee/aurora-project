Aurora.version='3.0';

Aurora.onReady = Ext.onReady;

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
				if(window[el]){
					if(window[el].wrap){
						ele = window[el].wrap;
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
			this.tip.hide();
		}
	}
	return q
}();