/**
 * @class Grid
 * <pre><code>
&lt;a:grid bindTarget="ds" rowRenderer="fun" navBar="true" width="300" autoFocus="false"&gt;
	&lt;a:toolBar&gt;
    	&lt;a:button click="fun" icon="x.gif" text="..."/&gt;
    	&lt;a:button type="add"/&gt;
    	&lt;a:button type="delete"/&gt;
    	&lt;a:button type="save"/&gt;
    &lt;/a:toolBar&gt;
	&lt;a:columns&gt;
		&lt;a:column editor="tf" name="fieldName"
		renderer="fun" footerRenderer="fun"
		width="30" align="center" lock="true"
		sortable="true" resizable="false"
		editorFunction="fun"/&gt;
	&lt;/a:columns&gt;
	&lt;a:editors&gt;
		&lt;a:textField id="tf"/&gt;
	&lt;/a:editors&gt;
&lt;/a:grid&gt;
   </code></pre>
 * @extends Component
 * @author 牛佳庆
 */

/**
 * 是否自动聚焦
 * @property autoFocus
 * @type Boolean
 * @default true
 */

/**
 * 列的编辑器,对应editors中的id
 * @property editor
 * @type String
 */

/**
 * 列编辑器函数,可动态改变编辑器
 * @property editorFunction
 * @type Function
 */

/**
 * 是否可按照次字段排序
 * @property sortable
 * @type Boolean
 * @default false
 */

/**
 * 是否锁定
 * @property lock
 * @type Boolean
 * @default false
 */

/**
 * 是否可调整宽度
 * @property resizable
 * @type Boolean
 * @default true
 */
 
/**
 * 是否具有导航条
 * @property navBar
 * @type Boolean
 * @default false
 */
 

/**
 * <p>通过回调函数返回的样式表渲染指定行</p> 
 * <p>回调函数function(record,rowIndex){return css}</p>
 * <p>返回值css值可以是class或者style字符串,也可以是class和style字符串数组</p>
 * @property rowRenderer
 * @type Function
 */
 
/**
 * <p>列渲染</p> 
 * <p>回调函数function(value,record,name){return value}</p>
 * <p>返回值value是html字符串</p>
 * @property renderer
 * @type Function
 */

/**
 * <p>列脚注渲染</p> 
 * <p>回调函数function(data,name){return value}</p>
 * <p>参数data是所绑定的dataset中的数据,返回值value是html字符串</p>
 * @property footerRenderer
 * @type Function
 */
