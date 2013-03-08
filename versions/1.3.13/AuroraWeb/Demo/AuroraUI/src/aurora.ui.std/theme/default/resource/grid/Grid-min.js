$A.Grid=Ext.extend($A.Component,{bgc:"background-color",scor:"#dfeaf5",ocor:"#ffe3a8",cecls:"cell-editor",nbcls:"item-notBlank",constructor:function(a){this.overId=null;this.selectedId=null;this.lockWidth=0;this.autofocus=a.autofocus||true;$A.Grid.superclass.constructor.call(this,a)},initComponent:function(b){$A.Grid.superclass.initComponent.call(this,b);var e=this.wrap;this.wb=Ext.get(this.id+"_wrap");this.fb=this.wb.child("div[atype=grid.fb]");if(this.fb){this.uf=this.fb.child("div[atype=grid.uf]")}this.uc=e.child("div[atype=grid.uc]");this.uh=e.child("div[atype=grid.uh]");this.ub=e.child("div[atype=grid.ub]");this.uht=e.child("table[atype=grid.uht]");this.lc=e.child("div[atype=grid.lc]");this.lh=e.child("div[atype=grid.lh]");this.lb=e.child("div[atype=grid.lb]");this.lht=e.child("table[atype=grid.lht]");this.sp=e.child("div[atype=grid.spliter]");Ext.getBody().insertFirst(this.sp);this.fs=e.child("a[atype=grid.focus]");this.lockColumns=[],this.unlockColumns=[];this.lockWidth=0,this.unlockWidth=0;for(var d=0,a=this.columns.length;d<a;d++){var f=this.columns[d];if(f.lock===true){this.lockColumns.add(f);if(f.hidden!==true){this.lockWidth+=f.width}}else{this.unlockColumns.add(f);if(f.hidden!==true){this.unlockWidth+=f.width}}}this.columns=this.lockColumns.concat(this.unlockColumns);this.initTemplate()},processListener:function(a){$A.Grid.superclass.processListener.call(this,a);this.wrap[a]("mouseover",this.onMouseOver,this);this.wrap[a]("mouseout",this.onMouseOut,this);this.wrap[a]("click",this.focus,this);if(!(this.canwheel===false)){this.wb[a]("mousewheel",this.onMouseWheel,this)}this.fs[a](Ext.isOpera?"keypress":"keydown",this.handleKeyDown,this);this.ub[a]("scroll",this.syncScroll,this);this.ub[a]("click",this.onClick,this);this.ub[a]("dblclick",this.onDblclick,this);this.uht[a]("mousemove",this.onUnLockHeadMove,this);this.uh[a]("mousedown",this.onHeadMouseDown,this);this.uh[a]("click",this.onHeadClick,this);if(this.lb){this.lb[a]("click",this.onClick,this);this.lb[a]("dblclick",this.onDblclick,this)}if(this.lht){this.lht[a]("mousemove",this.onLockHeadMove,this)}if(this.lh){this.lh[a]("mousedown",this.onHeadMouseDown,this)}if(this.lh){this.lh[a]("click",this.onHeadClick,this)}this[a]("cellclick",this.onCellClick,this)},initEvents:function(){$A.Grid.superclass.initEvents.call(this);this.addEvents("render","keydown","dblclick","cellclick","rowclick","editorshow","nexteditorshow")},syncScroll:function(){this.hideEditor();this.uh.dom.scrollLeft=this.ub.dom.scrollLeft;if(this.lb){this.lb.dom.scrollTop=this.ub.dom.scrollTop}if(this.uf){this.uf.dom.scrollLeft=this.ub.dom.scrollLeft}},handleKeyDown:function(m){var q=m.getKey();if(m.ctrlKey&&m.keyCode==86&&this.canpaste){var r=window.clipboardData.getData("text");if(r){var a=this.columns;var t=r.split("\n");for(var h=0,b=t.length;h<b;h++){var s=t[h];var p=s.split("\t");if(p==""){continue}var g={};for(var f=0,o=0,d=this.columns.length;f<d;f++){var n=this.columns[f];if(this.isFunctionCol(n)){continue}if(n.hidden!==true){g[n.name]=p[o];o++}}this.dataset.create(g)}}}else{if(q==38||q==40||q==33||q==34){if(this.dataset.loading==true){return}var s;switch(m.getKey()){case 33:this.dataset.prePage();break;case 34:this.dataset.nextPage();break;case 38:this.dataset.pre();break;case 40:this.dataset.next();break}}}this.fireEvent("keydown",this,m)},processDataSetLiestener:function(a){var b=this.dataset;if(b){b[a]("ajaxfailed",this.onAjaxFailed,this);b[a]("metachange",this.onRefresh,this);b[a]("update",this.onUpdate,this);b[a]("reject",this.onUpdate,this);b[a]("add",this.onAdd,this);b[a]("submit",this.onBeforSubmit,this);b[a]("submitfailed",this.onAfterSuccess,this);b[a]("submitsuccess",this.onAfterSuccess,this);b[a]("query",this.onBeforeLoad,this);b[a]("load",this.onLoad,this);b[a]("loadfailed",this.onAjaxFailed,this);b[a]("valid",this.onValid,this);b[a]("beforeremove",this.onBeforeRemove,this);b[a]("remove",this.onRemove,this);b[a]("clear",this.onLoad,this);b[a]("refresh",this.onRefresh,this);b[a]("fieldchange",this.onFieldChange,this);b[a]("indexchange",this.onIndexChange,this);b[a]("select",this.onSelect,this);b[a]("unselect",this.onUnSelect,this);b[a]("selectall",this.onSelectAll,this);b[a]("unselectall",this.onUnSelectAll,this)}},bind:function(b){if(typeof(b)==="string"){b=$(b);if(!b){return}}var a=this;a.dataset=b;if(b.autopagesize===true){b.pagesize=Math.round(((this.ub.getHeight()||parseFloat(this.ub.dom.style.height))-16)/25)}a.processDataSetLiestener("on");this.onLoad()},initTemplate:function(){this.rowTdTpl=new Ext.Template('<td atype="{atype}" class="grid-rowbox" recordid="{recordid}">');this.rowNumTdTpl=new Ext.Template('<td style="text-align:{align}" class="grid-rownumber" atype="grid-rownumber" recordid="{recordid}">');this.rowNumCellTpl=new Ext.Template('<div style="width:{width}px">{text}</div>');this.tdTpl=new Ext.Template('<td style="visibility:{visibility};text-align:{align}" dataindex="{name}" atype="grid-cell" recordid="{recordid}">');this.cellTpl=new Ext.Template('<div class="grid-cell {cellcls}" style="width:{width}px" id="'+this.id+'_{name}_{recordid}" title="{title}"><span>{text}</span></div>');this.cbTpl=new Ext.Template('<center><div class="{cellcls}" id="'+this.id+'_{name}_{recordid}"></div></center>')},getCheckBoxStatus:function(a,d,c){var g=this.dataset.getField(d);var b=g.getPropertity("checkedvalue");var e=g.getPropertity("uncheckedvalue");var f=a.data[d];return"item-ckb-"+(c?"readonly-":"")+((f&&f==b)?"c":"u")},createTemplateData:function(b,a){return{width:b.width-2,recordid:a.id,visibility:b.hidden===true?"hidden":"visible",name:b.name}},createCell:function(b,g,i){var e=this.createTemplateData(b,g);var m;var d=this.tdTpl;var o="";var c=b.type;var f;var h=this.getEditor(b,g);if(h!=""){var j=$A.CmpManager.get(h);if(j&&(j instanceof $A.CheckBox)){c="cellcheck";o=""}else{o=this.cecls}}else{if(b.name&&Ext.isDefined(g.getField(b.name).get("checkedvalue"))){c="cellcheck";f=true}}if(c=="rowcheck"||c=="rowradio"){if(!this.dataset.execSelectFunction(g)){f="-readonly"}else{f=""}d=this.rowTdTpl;e=Ext.apply(e,{align:"center",atype:c=="rowcheck"?"grid.rowcheck":"grid.rowradio",cellcls:c=="rowcheck"?"grid-ckb item-ckb"+f+"-u":"grid-radio item-radio-img"+f+"-u"});m=this.cbTpl}else{if(c=="cellcheck"){e=Ext.apply(e,{align:"center",cellcls:"grid-ckb "+this.getCheckBoxStatus(g,b.name,f)});m=this.cbTpl}else{var l=g.getMeta().getField(b.name);if(l&&Ext.isEmpty(g.data[b.name])&&g.isNew==true&&l.get("required")==true){o=o+" "+this.nbcls}var a=(o.indexOf(this.cecls)!=-1)?5:2;var n=this.renderText(g,b,g.data[b.name]);e=Ext.apply(e,{align:b.align||"left",cellcls:o,width:e.width-a,text:n,title:String(n).replace(/<[^<>]*>/mg,"")});m=this.cellTpl;if(c=="rownumber"){d=this.rowNumTdTpl;m=this.rowNumCellTpl}}}var k=[];if(i){k.add(d.applyTemplate(e))}k.add(m.applyTemplate(e));if(i){k.add("</td>")}return k.join("")},createRow:function(f,k,h,j){var g=[];var d=this.parseCss(this.renderRow(j,k));g.add('<tr id="'+this.id+"$"+f+"-"+j.id+'" class="'+(k%2==0?"":"row-alt")+d.cls+'"style="'+d.style+'">');for(var b=0,a=h.length;b<a;b++){var e=h[b];g.add(this.createCell(e,j,true))}g.add("</tr>");return g.join("")},parseCss:function(c){var e="",a="";if(Ext.isArray(c)){for(var b=0;b<c.length;b++){var d=this.parseCss(c[b]);e+=";"+d.style;a+=" "+d.cls}}else{if(typeof c=="string"){isStyle=!!c.match(/^([^,:;]+:[^:;]+;)*[^,:;]+:[^:;]+;*$/);a=isStyle?"":c;e=isStyle?c:""}}return{style:e,cls:a}},renderText:function(a,b,e){var d=b.renderer;if(d){var c=$A.getRenderer(d);if(c==null){alert("未找到"+d+"方法!");return e}e=c.call(window,e,a,b.name);return e==null?"":e}return e==null?"":e},renderRow:function(a,e){var d=this.rowrenderer,b=null;if(d){var c=$A.getRenderer(d);if(c==null){alert("未找到"+d+"方法!");return b}b=c.call(window,a,e);return !b?"":b}return b},createTH:function(d){var e=[];e.add('<tr class="grid-hl">');for(var c=0,b=d.length;c<b;c++){var a=d[c].width;if(d[c].hidden===true){a=0}e.add('<th dataindex="'+d[c].name+'" style="height:0px;width:'+a+'px"></th>')}e.add("</tr>");return e.join("")},onBeforeRemove:function(){$A.Masker.mask(this.wb,_lang["grid.mask.remove"])},onBeforeLoad:function(){this.ub.scrollTo("left",0);this.uh.scrollTo("left",0);$A.Masker.mask(this.wb,_lang["grid.mask.loading"])},onBeforSubmit:function(a){$A.Masker.mask(this.wb,_lang["grid.mask.submit"])},onAfterSuccess:function(){$A.Masker.unmask(this.wb)},preLoad:function(){},onLoad:function(){this.wrap.removeClass("grid-select-all");this.isSelectAll=false;this.clearDomRef();this.preLoad();var a=Ext.fly(this.wrap).child("div[atype=grid.headcheck]");if(this.selectable&&this.selectionmodel=="multiple"){this.setCheckBoxStatus(a,false)}if(this.lb){this.renderLockArea()}this.renderUnLockAread();this.drawFootBar();$A.Masker.unmask(this.wb);this.fireEvent("render",this)},clearDomRef:function(){this.selectlockTr=null;this.selectUnlockTr=null},onAjaxFailed:function(b,a){$A.Masker.unmask(this.wb)},onMouseWheel:function(b){b.stopEvent();if(this.editing==true){return}var c=b.getWheelDelta(),a=this;if(c>0){a.dataset.pre()}else{if(c<0){a.dataset.next()}}},focus:function(){this.fs.focus()},renderLockArea:function(){var d=[];var c=this.lockColumns;d.add('<TABLE cellSpacing="0" atype="grid.lbt" cellPadding="0" border="0"  width="'+this.lockWidth+'"><TBODY>');d.add(this.createTH(c));for(var b=0,a=this.dataset.data.length;b<a;b++){d.add(this.createRow("l",b,c,this.dataset.getAt(b)))}d.add("</TBODY></TABLE>");d.add('<DIV style="height:17px"></DIV>');this.lb.update(d.join(""));this.lbt=this.lb.child("table[atype=grid.lbt]")},renderUnLockAread:function(){var d=[];var c=this.unlockColumns;d.add('<TABLE cellSpacing="0" atype="grid.ubt" cellPadding="0" border="0" width="'+this.unlockWidth+'"><TBODY>');d.add(this.createTH(c));for(var b=0,a=this.dataset.data.length;b<a;b++){d.add(this.createRow("u",b,c,this.dataset.getAt(b)))}d.add("</TBODY></TABLE>");this.ub.update(d.join(""));this.ubt=this.ub.child("table[atype=grid.ubt]")},isOverSplitLine:function(a){var d=0;var g=false;this.overColIndex=-1;var f=this.columns;for(var e=0,b=f.length;e<b;e++){var h=f[e];if(h.hidden!==true){d+=h.width}if(a<d+3&&a>d-3&&h.resizable!=false){g=true;this.overColIndex=e;break}}return g},onRefresh:function(){this.onLoad(false);for(var a=0;a<this.dataset.selected.length;a++){this.onSelect(this.dataset,this.dataset.selected[a])}},onIndexChange:function(c,b){var a=this.getDataIndex(b.id);if(a==-1){return}this.selectRow(a,false)},isFunctionCol:function(a){return a.type=="rowcheck"||a.type=="rowradio"},onAdd:function(d,j,m){if(this.lb){var n=[]}var o=[];var q=0;var g=this.columns;var r=this.dataset.data.length-1;var k=this.parseCss(this.renderRow(j,r));if(this.lbt){var b=document.createElement("tr");b.id=this.id+"$l-"+j.id;b.className=(r%2==0?"":"row-alt")+" "+k.cls;Ext.fly(b).set({style:k.style});for(var h=0,f=g.length;h<f;h++){var c=g[h];if(c.lock===true){var e=document.createElement("td");if(c.type=="rowcheck"){Ext.fly(e).set({recordid:j.id,atype:"grid.rowcheck"});e.className="grid-rowbox";if(this.isSelectAll){e.className+=" item-ckb-self"}}else{if(c.type=="rowradio"){Ext.fly(e).set({recordid:j.id,atype:"grid.rowradio"});e.className="grid-rowbox"}else{e.style.visibility=c.hidden===true?"hidden":"visible";e.style.textAlign=c.align||"left";if(!this.isFunctionCol(c)){e.dataindex=c.name}Ext.fly(e).set({dataindex:c.name,recordid:j.id,atype:"grid-cell"});if(c.type=="rownumber"){e.className="grid-rownumber"}}}var p=this.createCell(c,j,false);e.innerHTML=p;b.appendChild(e)}}this.lbt.dom.tBodies[0].appendChild(b)}var a=document.createElement("tr");a.id=this.id+"$u-"+j.id;a.className=(r%2==0?"":"row-alt")+" "+k.cls;Ext.fly(a).set({style:k.style});for(var h=0,f=g.length;h<f;h++){var c=g[h];if(c.lock!==true){var e=document.createElement("td");e.style.visibility=c.hidden===true?"hidden":"visible";e.style.textAlign=c.align||"left";Ext.fly(e).set({dataindex:c.name,recordid:j.id,atype:"grid-cell"});var p=this.createCell(c,j,false);e.innerHTML=p;a.appendChild(e)}}this.ubt.dom.tBodies[0].appendChild(a);this.setSelectStatus(j)},renderEditor:function(f,b,e,d){var a=this.createCell(e,b,false);f.parent("td").update(a)},onUpdate:function(d,g,b,m){this.setSelectStatus(g);var a=Ext.get(this.id+"_"+b+"_"+g.id);if(a){var j=this.findColByName(b);var h=this.getEditor(j,g);if(h!=""&&($(h) instanceof $A.CheckBox)){this.renderEditor(a,g,j,h)}else{var n=this.renderText(g,j,m);a.update(n)}}var o=this.columns;for(var f=0,e=o.length;f<e;f++){var j=o[f];if(j.name!=b){var k=Ext.get(this.id+"_"+j.name+"_"+g.id);if(k){if(j.editorfunction){var h=this.getEditor(j,g);this.renderEditor(k,g,j,h)}if(j.renderer){var n=this.renderText(g,j,g.get(j.name));k.update(n)}}}}this.drawFootBar(b)},onValid:function(e,a,b,d){var g=this.findColByName(b);if(g){var f=Ext.get(this.id+"_"+b+"_"+a.id);if(f){if(d==false){f.addClass("item-invalid")}else{f.removeClass([this.nbcls,"item-invalid"])}}}},onRemove:function(d,a,b){var c=Ext.get(this.id+"$l-"+a.id);if(c){c.remove()}var e=Ext.get(this.id+"$u-"+a.id);if(e){e.remove()}if(Ext.isIE||Ext.isIE9){this.syncScroll()}this.clearDomRef();$A.Masker.unmask(this.wb);this.drawFootBar()},onClear:function(){},onFieldChange:function(d,a,e,b,c){switch(b){case"required":var f=Ext.get(this.id+"_"+e.name+"_"+a.id);if(f){(c==true)?f.addClass(this.nbcls):f.removeClass(this.nbcls)}break}},getDataIndex:function(d){var b=-1;for(var c=0,a=this.dataset.data.length;c<a;c++){var e=this.dataset.getAt(c);if(e.id==d){b=c;break}}return b},onSelect:function(c,b,d){if(!b||d){return}var a=Ext.get(this.id+"__"+b.id);Ext.fly(a.findParent(".grid-rowbox")).addClass("item-ckb-self");if(a){if(this.selectable&&this.selectionmodel=="multiple"){this.setCheckBoxStatus(a,true);this.setSelectStatus(b)}else{this.setRadioStatus(a,true);this.setSelectStatus(b);this.dataset.locate((this.dataset.currentPage-1)*this.dataset.pagesize+this.dataset.indexOf(b)+1)}}},onUnSelect:function(c,b,d){if(!b||d){return}var a=Ext.get(this.id+"__"+b.id);Ext.fly(a.findParent(".grid-rowbox")).addClass("item-ckb-self");if(a){if(this.selectable&&this.selectionmodel=="multiple"){this.setCheckBoxStatus(a,false);this.setSelectStatus(b)}else{this.setRadioStatus(a,false);this.setSelectStatus(b)}}},onSelectAll:function(){this.clearChecked();this.isSelectAll=true;this.isUnSelectAll=false;this.wrap.addClass("grid-select-all")},onUnSelectAll:function(){this.clearChecked();this.isSelectAll=false;this.isUnSelectAll=true;this.wrap.removeClass("grid-select-all")},clearChecked:function(){var d=this.wrap.select(".item-ckb-self");if(d){d.removeClass("item-ckb-self");for(var c=0,b=d.elements,a=b.length;c<a;c++){var e=Ext.fly(b[c]).child(".item-ckb-c");if(e){e.replaceClass("item-ckb-c","item-ckb-u")}}}},onDblclick:function(f){var d=Ext.fly(f.target).findParent("td[atype=grid-cell]");if(d){var c=Ext.fly(d).getAttributeNS("","recordid");var a=this.dataset.findById(c);var g=this.dataset.indexOf(a);var b=Ext.fly(d).getAttributeNS("","dataindex");this.fireEvent("dblclick",this,a,g,b)}},onClick:function(f){var g=Ext.fly(f.target).findParent("td");if(g){var b=Ext.fly(g).getAttributeNS("","atype");var i=Ext.fly(g).getAttributeNS("","recordid");if(b=="grid-cell"){var d=this.dataset.findById(i);var j=this.dataset.indexOf(d);var a=Ext.fly(g).getAttributeNS("","dataindex");this.fireEvent("cellclick",this,j,a,d);this.fireEvent("rowclick",this,j,d)}else{if(b=="grid-rownumber"){var d=this.dataset.findById(i);var j=this.dataset.indexOf(d);if(d.id!=this.selectedId){this.selectRow(j)}}else{if(b=="grid.rowcheck"){var c=Ext.get(this.id+"__"+i);if(c.hasClass("item-ckb-readonly-u")||c.hasClass("item-ckb-readonly-c")){return}if(this.isSelectAll&&!c.parent(".item-ckb-self")){c.replaceClass("item-ckb-u","item-ckb-c")}if(this.isUnselectAll&&!c.parent(".item-ckb-self")){c.replaceClass("item-ckb-c","item-ckb-u")}var h=c.hasClass("item-ckb-c");(h)?this.dataset.unSelect(i):this.dataset.select(i)}else{if(b=="grid.rowradio"){var c=Ext.get(this.id+"__"+i);if(c.hasClass("item-radio-img-readonly-u")||c.hasClass("item-radio-img-readonly-c")){return}this.dataset.select(i)}}}}}},onCellClick:function(c,d,b,a,e){this.adjustColumn(b);this.showEditor(d,b,e)},adjustColumn:function(b){if(!this.findColByName(b).autoadjust){return}var d=this.wrap.select("tr.grid-hl th[dataindex="+b+"]"),a=max=Ext.fly(d.elements[0]).getWidth(),e=12,c=this.width-(this.selectable?23:0)-20;Ext.each(this.wrap.query("td[dataindex="+b+"]"),function(h){var f=Ext.fly(h),g=f.child("span");if(g){if(Ext.isIE||Ext.isIE9){g.parent().setStyle("text-overflow","clip")}max=Math.max(g.getWidth()+e,max);if(Ext.isIE||Ext.isIE9){g.parent().setStyle("text-overflow","")}if(max>c){max=c;return false}}});if(max>a){this.setColumnSize(b,max)}},setColumnPrompt:function(c,a){var e=Ext.DomQuery.select("td.grid-hc",this.wrap.dom);for(var d=0,b=e.length;d<b;d++){var h=e[d];var f=Ext.fly(h).getAttributeNS("","dataindex");if(f==c){var g=Ext.fly(h).child("div");g.update(a);break}}},setEditor:function(b,c){var a=this.findColByName(b);a.editor=c;var d=Ext.get(this.id+"_"+b+"_"+this.selectRecord.id);if(d){if(c==""){d.removeClass(this.cecls)}else{if(!$(c) instanceof $A.CheckBox){d.addClass(this.cecls)}}}},getEditor:function(d,b){var c=d.editor||"";if(d.editorfunction){var a=window[d.editorfunction];if(a==null){alert("未找到"+d.editorfunction+"方法!");return null}c=a.call(window,b,d.name)}return c},showEditor:function(j,a,i){if(j==-1){return}var b=this.findColByName(a);if(!b){return}var c=this.dataset.getAt(j);if(!c){return}if(c.id!=this.selectedId){this.selectRow(j)}this.focusColumn(a);var g=this.getEditor(b,c);this.setEditor(a,g);var f=this;if(f.currentEditor){f.currentEditor.editor.el.un("keydown",f.onEditorKeyDown,f);var h=f.currentEditor.focusCheckBox;if(h){h.setStyle("outline","none");f.currentEditor.focusCheckBox=null}}if(g!=""){var e=$(g);setTimeout(function(){var d=c.get(a);f.currentEditor={record:c,ov:d,name:a,editor:e};var l=Ext.get(f.id+"_"+a+"_"+c.id);var k=l.getXY();e.bind(f.dataset,a);e.render(c);if(e instanceof $A.CheckBox){e.move(-1000,k[1]+5);e.el.on("keydown",f.onEditorKeyDown,f);e.onClick();f.currentEditor.focusCheckBox=l;l.setStyle("outline","1px dotted blue")}else{e.move(k[0],k[1]);e.setHeight(l.parent().getHeight()-5);e.setWidth(l.parent().getWidth()-7);e.isEditor=true;e.isFireEvent=true;e.isHidden=false;e.focus();f.editing=true;e.el.on("keydown",f.onEditorKeyDown,f);e.on("select",f.onEditorSelect,f);Ext.get(document.documentElement).on("mousedown",f.onEditorBlur,f);if(i){i.call(window,e)}f.fireEvent("editorshow",f,e,j,a,c)}},10)}},onEditorSelect:function(){var a=this;setTimeout(function(){a.hideEditor()},1)},onEditorKeyDown:function(c){var b=c.keyCode;if(b==27){if(this.currentEditor&&this.currentEditor.editor){var a=this.currentEditor.editor;a.clearInvalid();a.render(a.binder.ds.getCurrentRecord())}this.hideEditor()}if(b==13){if(!(this.currentEditor&&this.currentEditor.editor&&this.currentEditor.editor instanceof $A.TextArea)){this.showNextEditor()}}if(b==9){c.stopEvent();this.showNextEditor()}},showNextEditor:function(){this.hideEditor();var n=this;if(this.currentEditor&&this.currentEditor.editor){var q=function(d){if(d instanceof Aurora.Lov){d.showLovWindow()}};var k=this.currentEditor.editor,g=k.binder.ds,f=k.binder.name,a=k.record,t=g.data.indexOf(a),b=null;if(t!=-1){var s=this.columns;var c=0;for(var j=0,h=s.length;j<h;j++){if(s[j].name==f){c=j+1}}var m;for(var j=c,h=s.length;j<h;j++){var e=s[j];if(e.hidden!=true){m=this.getEditor(e,a);if(m!=""){b=e.name;break}}}if(n.currentEditor){var o=n.currentEditor.focusCheckBox;if(o){o.setStyle("outline","none");n.currentEditor.focusCheckBox=null}}if(b){var k=$(m);if(k instanceof $A.CheckBox){n.currentEditor={record:a,ov:a.get(b),name:b,editor:k};setTimeout(function(){k.bind(n.dataset,b);k.render(a);var i=Ext.get(n.id+"_"+b+"_"+a.id);var d=i.getXY();k.move(-1000,d[1]);k.focus();k.el.on("keydown",n.onEditorKeyDown,n);n.currentEditor.focusCheckBox=i;i.setStyle("outline","1px dotted blue")},10)}else{this.fireEvent("cellclick",this,t,b,a,q)}}else{var p=g.getAt(t+1);if(p){n.selectRow(t+1);for(var j=0,h=s.length;j<h;j++){var e=s[j];var m=this.getEditor(e,p);if(m!=""){var k=$(m),b=e.name;if(k instanceof $A.CheckBox){n.currentEditor={record:p,ov:p.get(b),name:b,editor:k};setTimeout(function(){k.bind(n.dataset,b);k.render(p);var i=Ext.get(n.id+"_"+b+"_"+p.id);var d=i.getXY();k.move(-1000,d[1]);k.focus();k.el.on("keydown",n.onEditorKeyDown,n);n.currentEditor.focusCheckBox=i;i.setStyle("outline","1px dotted blue")},10)}else{this.fireEvent("cellclick",this,t+1,b,p,q)}break}}}}}this.fireEvent("nexteditorshow",this,t,b)}},focusRow:function(d){var c=25;var b=this.ub.getScroll().top;if(d*c<b){this.ub.scrollTo("top",d*c-1)}if((d+1)*c>(b+this.ub.getHeight())){var a=this.ub.dom.scrollWidth>this.ub.dom.clientWidth?(d+1)*c-this.ub.getHeight()+16:(d+1)*c-this.ub.getHeight();this.ub.scrollTo("top",a)}if(this.autofocus){this.focus()}},focusColumn:function(b){var f=25;var e=this.ub.getScroll().left;var g=lr=lw=tw=0;for(var d=0,a=this.columns.length;d<a;d++){var h=this.columns[d];if(h.name&&h.name==b){tw=h.width}if(h.hidden!==true){if(h.lock===true){lw+=h.width}else{if(tw==0){g+=h.width}}}}lr=g+tw;if(g<e){this.ub.scrollTo("left",g)}if((lr-e)>(this.width-lw)){this.ub.scrollTo("left",lr-this.width+lw)}if(this.autofocus){this.focus()}},hideEditor:function(){if(this.currentEditor&&this.currentEditor.editor&&this.editing){var a=this.currentEditor.editor;var b=true;if(a.canHide){b=a.canHide()}if(b){a.el.un("keydown",this.onEditorKeyDown,this);a.un("select",this.onEditorSelect,this);Ext.get(document.documentElement).un("mousedown",this.onEditorBlur,this);var a=this.currentEditor.editor;a.move(-10000,-10000);a.onBlur();a.isFireEvent=false;a.isHidden=true;this.editing=false}}},onEditorBlur:function(a){if(this.currentEditor&&!this.currentEditor.editor.isEventFromComponent(a.target)){this.hideEditor()}},onLockHeadMove:function(a){this.hmx=a.xy[0]-this.lht.getXY()[0];if(this.isOverSplitLine(this.hmx)){this.lh.setStyle("cursor","w-resize")}else{this.lh.setStyle("cursor","default")}},onUnLockHeadMove:function(b){var a=0;if(this.uht){a=this.uht.getXY()[0]+this.uht.getScroll().left}this.hmx=b.xy[0]-a+this.lockWidth;if(this.isOverSplitLine(this.hmx)){this.uh.setStyle("cursor","w-resize")}else{this.uh.setStyle("cursor","default")}},onHeadMouseDown:function(a){this.dragWidth=-1;if(this.overColIndex==undefined||this.overColIndex==-1){return}this.dragIndex=this.overColIndex;this.dragStart=a.getXY()[0];this.sp.setHeight(this.wrap.getHeight());this.sp.setVisible(true);this.sp.setStyle("top",this.wrap.getXY()[1]+"px");this.sp.setStyle("left",a.xy[0]+"px");Ext.get(document.documentElement).on("mousemove",this.onHeadMouseMove,this);Ext.get(document.documentElement).on("mouseup",this.onHeadMouseUp,this)},onHeadClick:function(i){var j=Ext.fly(i.target).findParent("td");var b;if(j){j=Ext.fly(j);b=j.getAttributeNS("","atype")}if(b=="grid.head"){var h=j.getAttributeNS("","dataindex");var a=this.findColByName(h);if(a&&a.sortable===true){if(this.dataset.isModified()){$A.showInfoMessage("提示","有未保存数据!");return}var k=j.child("div");var m=h;var f="";if(this.currentSortTarget){var g=Ext.fly(this.currentSortTarget);g.removeClass(["grid-asc","grid-desc"])}this.currentSortTarget=k;if(Ext.isEmpty(a.sorttype)){a.sorttype="desc";k.removeClass("grid-asc");k.addClass("grid-desc");f="desc"}else{if(a.sorttype=="desc"){a.sorttype="asc";k.removeClass("grid-desc");k.addClass("grid-asc");f="asc"}else{a.sorttype="";k.removeClass(["grid-desc","grid-asc"])}}this.dataset.sort(m,f)}}else{if(b=="grid.rowcheck"){var c=j.child("div[atype=grid.headcheck]");if(c){var l=c.hasClass("item-ckb-c");this.setCheckBoxStatus(c,!l);if(!l){this.dataset.selectAll()}else{this.dataset.unSelectAll()}}}}},setRadioStatus:function(a,b){if(!b){a.removeClass("item-radio-img-c");a.addClass("item-radio-img-u")}else{a.addClass("item-radio-img-c");a.removeClass("item-radio-img-u")}},setCheckBoxStatus:function(a,b){if(a){if(!b){a.removeClass("item-ckb-c");a.addClass("item-ckb-u")}else{a.addClass("item-ckb-c");a.removeClass("item-ckb-u")}}},setSelectDisable:function(b,a){if(this.selectable&&this.selectionmodel=="multiple"){b.removeClass("item-ckb-c");b.removeClass("item-ckb-u");if(this.dataset.selected.indexOf(a)==-1){b.addClass("item-ckb-readonly-u")}else{b.addClass("item-ckb-readonly-c")}}else{b.removeClass(["item-radio-img-c","item-radio-img-u","item-radio-img-readonly-c","item-radio-img-readonly-u"]);if(this.dataset.selected.indexOf(a)==-1){b.addClass("item-radio-img-readonly-u")}else{b.addClass("item-radio-img-readonly-c")}}},setSelectEnable:function(b,a){if(this.selectable&&this.selectionmodel=="multiple"){b.removeClass(["item-ckb-readonly-u","item-ckb-readonly-c"]);if(this.dataset.selected.indexOf(a)==-1){b.addClass("item-ckb-u")}else{b.addClass("item-ckb-c")}}else{b.removeClass(["item-radio-img-u","item-radio-img-c","item-radio-img-readonly-u","item-radio-img-readonly-c"]);if(this.dataset.selected.indexOf(a)==-1){b.addClass("item-radio-img-u")}else{b.addClass("item-radio-img-c")}}},setSelectStatus:function(b){if(this.dataset.selectfunction){var a=Ext.get(this.id+"__"+b.id);if(!this.dataset.execSelectFunction(b)){this.setSelectDisable(a,b)}else{this.setSelectEnable(a,b)}}},onHeadMouseMove:function(d){d.stopEvent();this.dragEnd=d.getXY()[0];var b=this.dragEnd-this.dragStart;var f=this.columns[this.dragIndex];var a=f.width+b;if(a>30&&a<this.width){this.dragWidth=a;this.sp.setStyle("left",d.xy[0]+"px")}},onHeadMouseUp:function(a){Ext.get(document.documentElement).un("mousemove",this.onHeadMouseMove,this);Ext.get(document.documentElement).un("mouseup",this.onHeadMouseUp,this);this.sp.setVisible(false);if(this.dragWidth!=-1){this.setColumnSize(this.columns[this.dragIndex].name,this.dragWidth)}this.syncScroll()},findColByName:function(d){var b;for(var e=0,a=this.columns.length;e<a;e++){var f=this.columns[e];if(f.name&&f.name.toLowerCase()===d.toLowerCase()){b=f;break}}return b},selectRow:function(d,b){var a=this.dataset.getAt(d);this.selectedId=a.id;if(this.selectlockTr){this.selectlockTr.setStyle(this.bgc,"")}if(this.selectUnlockTr){this.selectUnlockTr.removeClass("row-selected")}this.selectUnlockTr=Ext.get(this.id+"$u-"+a.id);if(this.selectUnlockTr){this.selectUnlockTr.addClass("row-selected")}this.selectlockTr=Ext.get(this.id+"$l-"+a.id);if(this.selectlockTr){this.selectlockTr.setStyle(this.bgc,this.scor)}this.focusRow(d);var c=(this.dataset.currentPage-1)*this.dataset.pagesize+d+1;this.selectRecord=a;if(b!==false&&c!=null){this.dataset.locate.defer(5,this.dataset,[c,false])}},setColumnSize:function(x,m){var a=this.columns;var s,g,b=0,d=0;var f="width",j="px";for(var o=0,n=a.length;o<n;o++){var t=a[o];if(t.name&&t.name===x){if(t.hidden===true){return}t.width=m;if(t.lock!==true){s=this.uh.child("th[dataindex="+x+"]");g=this.ub.child("th[dataindex="+x+"]")}else{if(this.lh){s=this.lh.child("th[dataindex="+x+"]")}if(this.lb){g=this.lb.child("th[dataindex="+x+"]")}}}t.lock!==true?(d+=t.width):(b+=t.width)}var p=Ext.DomQuery.select("td[dataindex="+x+"]",this.wrap.dom);for(var o=0,n=p.length;o<n;o++){var e=p[o];var h=Ext.fly(e).child("DIV.grid-cell");if(h){var q=h.hasClass(this.cecls)?7:4;Ext.fly(h).setStyle(f,Math.max(m-q,0)+j)}}this.unlockWidth=d;this.lockWidth=b;if(s){s.setStyle(f,m+j)}if(g){g.setStyle(f,m+j)}if(this.fb){var r=this.fb.child("th[dataindex="+x+"]");r.setStyle(f,m+j);var v=this.fb.child("table[atype=fb.ubt]");this.uf.setStyle(f,Math.max(this.width-b,0)+j);v.setStyle(f,d+j);var w=this.fb.child("table[atype=fb.lbt]");if(w){var k=this.fb.child("div[atype=grid.lf]");k.setStyle(f,(b-1)+j);w.setStyle(f,b+j)}}if(this.lc){var u=Math.max(b-1,0);this.lc.setStyle(f,u+j);this.lc.setStyle("display",u==0?"none":"")}if(this.lht){this.lht.setStyle(f,b+j)}if(this.lbt){this.lbt.setStyle(f,b+j)}this.uc.setStyle(f,Math.max(this.width-b,0)+j);this.uh.setStyle(f,Math.max(this.width-b,0)+j);this.ub.setStyle(f,Math.max(this.width-b,0)+j);this.uht.setStyle(f,d+j);if(this.ubt){this.ubt.setStyle(f,d+j)}this.syncSize()},drawFootBar:function(b){if(!this.fb){return}b=[].concat((b)?b:this.columns);var a=this;Ext.each(b,function(h){var e=typeof(h)==="string"?a.findColByName(h):h;if(e&&e.footerrenderer){var d=e.name;var g=$A.getRenderer(e.footerrenderer);if(g==null){alert("未找到"+e.footerrenderer+"方法!");return}var c=g.call(window,a.dataset.data,d);var f=a.fb.child("td[dataindex="+d+"]");f.update(c)}})},syncSize:function(){var e=0;for(var b=0,a=this.columns.length;b<a;b++){var f=this.columns[b];if(f.hidden!==true){if(f.lock===true){e+=f.width}}}if(e!=0){var d=this.width-e;this.uc.setWidth(d);this.uh.setWidth(d);this.ub.setWidth(d)}},showColumn:function(c){var b=this.findColByName(c);if(b){if(b.hidden===true){delete b.hidden;this.setColumnSize(c,b.hiddenWidth);delete b.hiddenWidth;var e=Ext.DomQuery.select("td[dataindex="+c+"]",this.wrap.dom);for(var d=0,a=e.length;d<a;d++){var f=e[d];Ext.fly(f).show()}}}},hideColumn:function(c){var b=this.findColByName(c);if(b){if(b.hidden!==true){b.hiddenWidth=b.width;this.setColumnSize(c,0,false);var e=Ext.DomQuery.select("td[dataindex="+c+"]",this.wrap.dom);for(var d=0,a=e.length;d<a;d++){var f=e[d];Ext.fly(f).hide()}b.hidden=true}}},setWidth:function(c){if(this.width==c){return}this.width=c;this.wrap.setWidth(c);var b=$A.CmpManager.get(this.id+"_tb");if(b){b.setWidth(c)}var a=$A.CmpManager.get(this.id+"_navbar");if(a){a.setWidth(c)}if(this.fb){this.fb.setWidth(c)}var d=c-this.lockWidth;this.uc.setWidth(d);this.uh.setWidth(d);this.ub.setWidth(d);if(this.uf){this.uf.setWidth(d)}},setHeight:function(d){if(this.height==d){return}this.height=d;var b=$A.CmpManager.get(this.id+"_tb");if(b){d-=25}var a=$A.CmpManager.get(this.id+"_navbar");if(a){d-=25}if(this.fb){d-=25}this.wrap.setHeight(d);var c=d-25;if(this.lb){this.lb.setHeight(c)}this.ub.setHeight(c)},deleteSelectRows:function(b){var a=[].concat(this.dataset.getSelected());if(a.length>0){this.dataset.remove(a)}b.close()},remove:function(){var a=this.dataset.getSelected();if(a.length>0){$A.showConfirm(_lang["grid.remove.confirm"],_lang["grid.remove.confirmMsg"],this.deleteSelectRows.createDelegate(this))}},clear:function(){var a=this.dataset.getSelected();while(a[0]){this.dataset.removeLocal(a[0])}},_export:function(){this.showExportConfirm()},showExportConfirm:function(){this.initColumnPrompt();var e=this,h=this.id+"_export",f=['<div class="item-export-wrap" style="margin:15px;width:270px" id="'+h+'">','<div class="grid-uh" atype="grid.uh" style="width: 270px; -moz-user-select: none; text-align: left; height: 25px; cursor: default;" onselectstart="return false;" unselectable="on">','<table cellSpacing="0" cellPadding="0" border="0"><tbody><tr height="25px">','<td class="export-hc" style="width:22px;" atype="export.rowcheck"><center><div atype="export.headcheck" class="grid-ckb item-ckb-',"u",'"></div></center></td>','<td class="export-hc" style="width:222px;" atype="grid-type">',_lang["grid.export.column"],"</td>","</tr></tbody></table></div>",'<div style="overflow:auto;height:200px;"><table cellSpacing="0" cellPadding="0" border="0"><tbody>'],d=true;for(var b=0,a=this.columns.length;b<a;b++){var g=this.columns[b];if(!g.type){if(d){d=g.forexport!==false}f.push("<tr",b%2==0?"":' class="row-alt"','><td class="grid-rowbox" style="width:22px;" rowid="',b,'" atype="export.rowcheck"><center><div id="',this.id,"__",b,'" class="grid-ckb item-ckb-',g.forexport===false?"u":"c",'"></div></center></td><td><div class="grid-cell" style="width:220px">',g.prompt,"</div></td></tr>")}}if(d){f[4]="c"}f.push("</tbody></table></div></div>");this.exportwindow=$A.showOkCancelWindow(_lang["grid.export.config"],f.join(""),function(c){$A.showConfirm(_lang["grid.export.confirm"],_lang["grid.export.confirmMsg"],function(i){e.doExport();i.close();c.body.un("click",e.onExportClick,e);c.close()});return false},null,null,300);this.exportwindow.body.on("click",this.onExportClick,this)},initColumnPrompt:function(){if(!this.isPromptInit){for(var b=0,a=this.columns.length;b<a;b++){var d=this.columns[b];if(!d.type){d.prompt=d.name?this.wrap.child("td.grid-hc[dataindex="+d.name+"] div").dom.innerHTML:(d.prompt||this.dataset.getField(d.name).pro.prompt)}}this.isPromptInit=true}},onExportClick:function(h,n){var j=Ext.fly(Ext.fly(n).findParent("td"));if(j){var a=j.getAttributeNS("","atype");var m=j.getAttributeNS("","rowid");if(a=="export.rowcheck"){var c=j.child("div"),k=c.hasClass("item-ckb-c");this.setCheckBoxStatus(c,!k);var g=c.getAttributeNS("","atype");if(g=="export.headcheck"){var f=this.exportwindow.body.query("td[atype=export.rowcheck] div[atype!=export.headcheck]");for(var d=0,b=f.length;d<b;d++){this.setCheckBoxStatus(Ext.fly(f[d]),!k);this.columns[d].forexport=!k}}else{this.columns[m].forexport=!k}}}},doExport:function(){this.initColumnPrompt();var n={parameter:{_column_config_:{}}},b=[],u={},A=this,a=function(o,i){if(!(Ext.isDefined(o.forexport)?o.forexport:true)){return null}var k=Ext.encode(o);var p=u[k];if(!p){p={prompt:o.prompt}}u[k]=p;(p.column=p.column||[]).add(i);if(o._parent){return a(o._parent,p)}return p};for(var x=0;x<A.columns.length;x++){var e=A.columns[x],y=Ext.isDefined(e.forexport)?e.forexport:true;if(!e.type&&y){var z={prompt:e.prompt};if(e.width){z.width=e.width}if(e.name){z.name=e.exportfield||e.name}z.align=e.align||"left";var t=e._parent?a(e._parent,z):z;if(t){b.add(t)}}}n.parameter["_column_config_"]["column"]=b;n._generate_state=true;n._format="xls";var l,m={};if(A.dataset.qds){l=A.dataset.qds.getCurrentRecord()}if(l){Ext.apply(m,l.data)}Ext.apply(m,A.dataset.qpara);for(var w in m){var h=m[w];if(Ext.isEmpty(h,false)){delete m[w]}}Ext.apply(n.parameter,m);var d=document.createElement("form");d.target="_export_window";d.method="post";var f=A.dataset.queryurl;if(f){d.action=f+(f.indexOf("?")==-1?"?":"&")+"r="+Math.random()}var g=Ext.get("_export_window")||new Ext.Template('<iframe id ="_export_window" name="_export_window" style="position:absolute;left:-1000px;top:-1000px;width:1px;height:1px;display:none"></iframe>').insertFirst(document.body,{},true);var j=document.createElement("input");j.id="_request_data";j.type="hidden";j.name="_request_data";j.value=Ext.encode(n);d.appendChild(j);document.body.appendChild(d);d.submit();Ext.fly(d).remove()},destroy:function(){$A.Grid.superclass.destroy.call(this);this.processDataSetLiestener("un");this.sp.remove();delete this.sp}});$A.Grid.revision="$Rev: 5700 $";