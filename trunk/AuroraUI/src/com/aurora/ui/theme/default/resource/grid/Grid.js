Aurora.Grid = Ext.extend(Aurora.Component,{
	bgc:'background-color',
	constructor: function(config){
		this.overIndex = -1;
		this.selectedIndex = -1;
		this.lockWidth = 0;
		Aurora.Grid.superclass.constructor.call(this,config);
	},
	initComponent:function(config){
		Aurora.Grid.superclass.initComponent.call(this, config);
		
		this.uc = this.wrap.child('div[atype=grid.uc]'); 
		this.uh = this.wrap.child('div[atype=grid.uh]'); 
    	this.ub = this.wrap.child('div[atype=grid.ub]'); 
		this.uht = this.wrap.child('table[atype=grid.uht]'); 
		
		this.lc = this.wrap.child('div[atype=grid.lc]'); 
		this.lh = this.wrap.child('div[atype=grid.lh]');
		this.lb = this.wrap.child('div[atype=grid.lb]');
		this.lht = this.wrap.child('table[atype=grid.lht]'); 

		this.sp = this.wrap.child('div[atype=grid.spliter]');
		
		var lock =[],unlock = [],columns=[];
		for(var i=0,l=this.columns.length;i<l;i++){
			var c = this.columns[i];
			if(c.lock == true){
				lock.add(c);
			}else{
				unlock.add(c);
			}
		}
		this.columns = lock.concat(unlock);
    	this.initTemplate();
    	//this.onLoad();//test
	},
	initEvents:function(){
		Aurora.Grid.superclass.initEvents.call(this);   
		this.addEvents('dblclick','select');
		this.ub.on('scroll',this.syncScroll, this);
		this.ub.on('click',this.onClick, this);
		this.ub.on('dblclick',this.onDblclick, this);
		this.ub.on('mouseover',this.onMouseOver, this);
		this.uht.on('mousemove',this.onUnLockHeadMove, this);
		this.uh.on('mousedown', this.onHeadMouseDown,this);

		if(this.lb){
			this.lb.on('mouseover',this.onMouseOver, this);
			this.lb.on('click',this.onClick, this);
		}
		if(this.lht) this.lht.on('mousemove',this.onLockHeadMove, this);
		if(this.lh) this.lh.on('mousedown', this.onHeadMouseDown,this);
		
		
	},
	syncScroll : function(){
		this.hideEditor();
		this.uh.dom.scrollLeft = this.ub.dom.scrollLeft;
		if(this.lb) this.lb.dom.scrollTop = this.ub.dom.scrollTop;
	},
	bind : function(ds){
		if(typeof(ds)==='string'){
			ds = $(ds);
			if(!ds) return;
		}
		this.dataset = ds;
		ds.on('metachange', this.onRefresh, this);
		ds.on('update', this.onUpdate, this);
    	ds.on('create', this.onCreate, this);
    	ds.on('load', this.onLoad, this);
    	ds.on('valid', this.onValid, this);
    	ds.on('remove', this.onRemove, this);
    	ds.on('clear', this.onClear, this);
    	ds.on('fieldchange', this.onFieldChange, this);
    	ds.on('indexchange', this.onIndexChange, this);
		this.onLoad();
	},
	initTemplate : function(){
		this.cellTpl = new Ext.Template('<TD style="visibility:{visibility};text-align:{align}" dataindex="{dataindex}"><div class="grid-cell" id="'+this.id+'_{dataindex}_{recordid}" dataindex="{dataindex}" recordid="{recordid}">{text}</div></TD>');		
	},
	createRow : function(type, row, cols, item){
		var sb = [];
		sb.add('<TR id="'+this.id+'$'+type+'-'+row+'" class="'+(row % 2==0 ? '' : 'row-alt')+'">');
		for(var i=0,l=cols.length;i<l;i++){
			var c = cols[i];
			var data = {
				width:c.width,
				text:this.renderText(item,c.dataindex,item.data[c.dataindex]||''),
				recordid:item.id,
				visibility: c.hidden == true ? 'hidden' : 'visible',
				align:c.align||'left',
				dataindex:c.dataindex
			}
			sb.add(this.cellTpl.applyTemplate(data));
		}
		sb.add('</TR>');
		return sb.join('');
	},
	renderText : function(record,name,value){
		var field = this.dataset.getField(name);
		var col = this.getColByDataIndex(name);
		if(col.renderer){
			value = window[col.renderer].call(window,record,name, value);
			return value;
		}
		if(field){
			var options = field.getOptions();
			if(options){
				var val = field.getPropertity('valuefield');
				var dis = field.getPropertity('displayfield');
				var r = $(options).find(val,value);
				value = r ? r.get(dis) : value;
			}
		}
		return value;
	},
	createTH : function(cols){
		var sb = [];
		sb.add('<TR class="grid-hl">');
		for(var i=0,l=cols.length;i<l;i++){
			var w = cols[i].width;
			if(cols[i].hidden == true) w = 0;
			sb.add('<TH dataindex="'+cols[i].dataindex+'" style="height:0px;width:'+w+'px"></TH>');
		}
		sb.add('</TR>');
		return sb.join('');
	},
	onLoad : function(){
		if(this.lb)
		this.renderLockArea();
		this.renderUnLockAread();
	},
	renderLockArea : function(){
		var sb = [];var cols = [];
		var v = 0;
		var columns = this.columns;
		for(var i=0,l=columns.length;i<l;i++){
			if(columns[i].lock === true){
				cols.add(columns[i]);
				if(columns[i].hidden !== true) v += columns[i].width;
			}
		}
		this.lockWidth = v;
		sb.add('<TABLE cellSpacing="0" atype="grid.lbt" cellPadding="0" border="0"  width="'+v+'"><TBODY>');
		sb.add(this.createTH(cols));
		for(var i=0,l=this.dataset.data.length;i<l;i++){
			sb.add(this.createRow('l', i, cols, this.dataset.getAt(i)));
		}
		sb.add('</TBODY></TABLE>');
		sb.add('<DIV style="height:17px"></DIV>');
		this.lb.update(sb.join(''));
		this.lbt = this.lb.child('table[atype=grid.lbt]'); 
	},
	renderUnLockAread : function(){
		var sb = [];var cols = [];
		var v = 0;
		var columns = this.columns;
		for(var i=0,l=columns.length;i<l;i++){
			if(columns[i].lock !== true){
				cols.add(columns[i]);
				if(columns[i].hidden !== true) v += columns[i].width;
			}
		}
		sb.add('<TABLE cellSpacing="0" atype="grid.ubt" cellPadding="0" border="0" width="'+v+'"><TBODY>');
		sb.add(this.createTH(cols));
		for(var i=0,l=this.dataset.data.length;i<l;i++){
			sb.add(this.createRow('u', i, cols, this.dataset.getAt(i)));
		}
		sb.add('</TBODY></TABLE>');
		this.ub.update(sb.join(''));
		this.ubt = this.ub.child('table[atype=grid.ubt]'); 
	},
    isOverSplitLine : function(x){
		var v = 0;		
		var isOver = false;
		this.overColIndex = -1;
		var columns = this.columns;
		for(var i=0,l=columns.length;i<l;i++){
			var c = columns[i];
			if(c.hidden !== true) v += c.width;
			if(x < v+3 && x > v-3){
				isOver = true;
				this.overColIndex = i;
				break;
			}
		}
		return isOver;
	},
	onRefresh : function(){
		
	},
	onIndexChange:function(ds, r){
		var index = this.getDataIndex(r.id);
		if(index == -1)return;
//		alert(r.id + " " + this.selectRecord.id)
		if(r != this.selectRecord)
		this.selectRow(index, false);
	},
	onCreate : function(){
		
	},
	onUpdate : function(ds,record, name,value){
		var div = document.getElementById(this.id+'_'+name+'_'+record.id)
		if(div){ 
			var text = this.renderText(record,name, value);
			Ext.fly(div).update(text);//TODO:要考虑 renderer
		}
	},
	onValid : function(){
		
	},
	onRemove : function(){
		
	},
	onClear : function(){
		
	},
	onFieldChange : function(){
		
	},
	onMouseOver : function(e){
		if(Ext.fly(e.target).hasClass('grid-cell')){
			var rid = Ext.fly(e.target).getAttributeNS("","recordid");
			var row = this.getDataIndex(rid);
			if(row == -1)return;
			if(row != this.overIndex)
			if(this.overlockTr) this.overlockTr.setStyle(this.bgc, this.selectedIndex ==this.overIndex ? '#ffe3a8' : '');
				if(this.overUnlockTr)  this.overUnlockTr.setStyle(this.bgc,this.selectedIndex ==this.overIndex ? '#ffe3a8' : '');
				this.overIndex = row;
				this.overlockTr = Ext.get(document.getElementById(this.id+'$l-'+row));
				if(this.overlockTr)this.overlockTr.setStyle(this.bgc,'#d9e7ed');
				this.overUnlockTr = Ext.get(document.getElementById(this.id+'$u-'+row));
				this.overUnlockTr.setStyle(this.bgc,'#d9e7ed');
			}
	},
	getDataIndex : function(rid){
		var index = -1;
		for(var i=0,l=this.dataset.data.length;i<l;i++){
			var item = this.dataset.getAt(i);
			if(item.id == rid){
				index = i;
				break;
			}
		}
		return index;
	},
	onDblclick : function(e){
		if(Ext.fly(e.target).hasClass('grid-cell')){
			var dom = e.target;
			var rid = Ext.fly(dom).getAttributeNS("","recordid");
			var record = this.dataset.findById(rid);
			var row = this.dataset.indexOf(record);
			var dataindex = Ext.fly(dom).getAttributeNS("","dataindex");
			this.fireEvent('dblclick', this, record, row, dataindex)
		}
	},
	onClick : function(e) {
		if(Ext.fly(e.target).hasClass('grid-cell')){
			var dom = e.target;
			var rid = Ext.fly(dom).getAttributeNS("","recordid");
			var record = this.dataset.findById(rid);
			var row = this.dataset.indexOf(record);
			var dataindex = Ext.fly(dom).getAttributeNS("","dataindex");
			this.showEditor(row,dataindex);
			
			
//			if(row != this.selectedIndex);
//			this.selectRow(row);
//			
//			var col = this.getColByDataIndex(dataindex);
//			this.focusRow(row)
//			
//			var xy = Ext.fly(dom).getXY();
//			var editor = col.editor;			
//			if(col.editorfunction) {
//				editor = window[col.editorfunction].call(window,record)
//			}
//			if($(editor)){
//				var sf = this;
//				setTimeout(function(){
//					sf.showEditor(dom,editor,xy[0],xy[1],record,dataindex);				
//				},1)
//			}
		}
	},
	showEditor : function(row, dataindex){
		Ext.get(document.documentElement).un("mousedown", this.onEditorBlur, this);
		if(row == -1)return;
		var col = this.getColByDataIndex(dataindex);
		if(!col)return;
		var record = this.dataset.getAt(row);
		if(!record)return;
		if(row != this.selectedIndex);
		this.selectRow(row);
		this.focusRow(row)
		
		var editor = col.editor;			
		if(col.editorfunction) {
			editor = window[col.editorfunction].call(window,record)
		}
		if($(editor)){
			var dom = document.getElementById(this.id+'_'+dataindex+'_'+record.id);
			var xy = Ext.fly(dom).getXY();
			var sf = this;
			setTimeout(function(){
				var v = record.get(dataindex)
				sf.currentEditor = {
					record:record,
					ov:v,
					dataindex:dataindex,
					editor:$(editor)
				};
				var ed = sf.currentEditor.editor;
				ed.setHeight(Ext.fly(dom.parentNode).getHeight()-3)
				ed.setWidth(Ext.fly(dom.parentNode).getWidth()-6);
				ed.bind(sf.dataset, dataindex);
				ed.setValue(v,true);
				if(!ed.wrap.isVisible())ed.setVisible(true);
				
				ed.move(xy[0],xy[1])
				ed.focus();				
				Ext.get(document.documentElement).on("mousedown", sf.onEditorBlur, sf);				
			},1)
		}
		
		
		
	},
	focusRow : function(row){
		var stop = this.ub.getScroll().top;
		if(row*24<stop){
			this.ub.scrollTo('top',row*24-1)
		}
		if((row+1)*24>(stop+this.ub.getHeight())){
			this.ub.scrollTo('top',(row+1)*24-this.ub.getHeight())
		}
	},
	hideEditor : function(){
		Ext.get(document.documentElement).un("mousedown", this.onEditorBlur, this);
		if(this.currentEditor && this.currentEditor.editor){
			var ed = this.currentEditor.editor;
			ed.blur();
			ed.move(-10000,-10000)
		}
	},
	onEditorBlur : function(e){
		if(this.currentEditor && !this.currentEditor.editor.wrap.contains(e.target)) {			
			this.hideEditor();
		}
	},
	onLockHeadMove : function(e){
		this.hmx = e.xy[0] - this.lht.getXY()[0];
		if(this.isOverSplitLine(this.hmx)){
			this.lh.setStyle('cursor',"w-resize");			
		}else{
			this.lh.setStyle('cursor',"default");			
		}
		//Ext.get('console').update(this.hmx)
	},
	onUnLockHeadMove : function(e){
		var lw = 0;
		if(this.uht){
			lw = this.uht.getXY()[0] + this.uht.getScroll().left;
		}
		this.hmx = e.xy[0] - lw + this.lockWidth;
		if(this.isOverSplitLine(this.hmx)){
			this.uh.setStyle('cursor',"w-resize");			
		}else{
			this.uh.setStyle('cursor',"default");
		}		
	},
	onHeadMouseDown : function(e){
		this.dragWidth = -1;
		if(this.overColIndex == -1) return;
		this.dragIndex = this.overColIndex;
		this.dragStart = e.getXY()[0];
		this.sp.setHeight(this.height);
		this.sp.setVisible(true);
		this.sp.setStyle("left", (e.xy[0] - this.wrap.getXY()[0]-1)+"px")
		Ext.get(document.documentElement).on("mousemove", this.onHeadMouseMove, this);
    	Ext.get(document.documentElement).on("mouseup", this.onHeadMouseUp, this);
	},
	onHeadMouseMove: function(e){
		e.stopEvent();
		this.dragEnd = e.getXY()[0];
		var move = this.dragEnd - this.dragStart;
		var c = this.columns[this.dragIndex];
		
		var w = c.width + move;
		
		if(w > 30 && w < this.width) {
			this.dragWidth = w;
			//Ext.get('console').update(w)
			this.sp.setStyle("left", (e.xy[0] - this.wrap.getXY()[0])+"px")
		}
	},
	onHeadMouseUp: function(e){
		Ext.get(document.documentElement).un("mousemove", this.onHeadMouseMove, this);
    	Ext.get(document.documentElement).un("mouseup", this.onHeadMouseUp, this);
		
		
		this.sp.setVisible(false);
		if(this.dragWidth != -1)
		this.setColumnSize(this.columns[this.dragIndex].dataindex, this.dragWidth);
		
	},
	getColByDataIndex : function(dataindex){
		var col;
		for(var i=0,l=this.columns.length;i<l;i++){
			var c = this.columns[i];
			if(c.dataindex === dataindex){
				col = c;
				break;
			}
		}
		return col;
	},
	/** API ���� **/
	selectRow : function(row, locate){
		this.selectedIndex = row;
		if(this.selectlockTr) this.selectlockTr.setStyle(this.bgc,'');
		if(this.selectUnlockTr) this.selectUnlockTr.setStyle(this.bgc,'');
		var s = new Date();
		this.selectUnlockTr = Ext.get(document.getElementById(this.id+'$u-'+row));
		if(this.selectUnlockTr)this.selectUnlockTr.setStyle(this.bgc,'#ffe3a8');
		
		this.selectlockTr = Ext.get(document.getElementById(this.id+'$l-'+row));
		if(this.selectlockTr)this.selectlockTr.setStyle(this.bgc,'#ffe3a8');
		var r = (this.dataset.currentPage-1)*this.dataset.pageSize + row+1;
		this.selectRecord = this.dataset.getAt(row) 
		if(locate!==false && r != null) this.dataset.locate(r);
//		this.fireEvent('select', this)
	},
	setColumnSize : function(dataindex, size){
		var columns = this.columns;
		var hth,bth,lw=0,uw=0;
		for(var i=0,l=columns.length;i<l;i++){
			var c = columns[i];
			if(c.dataindex === dataindex){
				if(c.hidden == true) return;
				c.width = size;
				if(c.lock !== true){					
					hth = this.uh.child('TH[dataindex='+dataindex+']');
					bth = this.ub.child('TH[dataindex='+dataindex+']');
					
				}else{							
					if(this.lh) hth = this.lh.child('TH[dataindex='+dataindex+']');
					if(this.lb) bth = this.lb.child('TH[dataindex='+dataindex+']');
					
				}
			}
			c.lock !== true ? uw += c.width : lw += c.width;
		}
		this.lockWidth = lw;
		if(hth) hth.setStyle("width", size+"px");
		if(bth) bth.setStyle("width", size+"px");
		if(this.lc)this.lc.setStyle("width",(lw-1)+"px");
		if(this.lht)this.lht.setStyle("width",lw+"px");
		if(this.lbt)this.lbt.setStyle("width",lw+"px");
		this.uc.setStyle("width", (this.width - lw)+"px");
		this.uht.setStyle("width",uw+"px");
		this.ubt.setStyle("width",uw+"px");
	},
	showColumn : function(dataindex){
		var col = this.getColByDataIndex(dataindex);
		if(col){
			if(col.hidden === true){
				delete col.hidden;
				this.setColumnSize(dataindex, col.hiddenWidth);
				delete col.hiddenWidth;
				if(!Ext.isIE){
					var tds = Ext.DomQuery.select('TD[dataindex='+dataindex+']',this.wrap.dom);
					for(var i=0,l=tds.length;i<l;i++){
						var td = tds[i];
						Ext.fly(td).show();
					}
				}
			}
		}
	},
	hideColumn : function(dataindex){
		var col = this.getColByDataIndex(dataindex);
		if(col){
			if(col.hidden !== true){
				col.hiddenWidth = col.width;
				this.setColumnSize(dataindex, 0, false);
				if(!Ext.isIE){
					var tds = Ext.DomQuery.select('TD[dataindex='+dataindex+']',this.wrap.dom);
					for(var i=0,l=tds.length;i<l;i++){
						var td = tds[i];
						Ext.fly(td).hide();
					}
				}
				col.hidden = true;
			}
			
		}
	}	
	
});