/**
 * @class Grid[Column]
 * 图表控件下的列标签，必须在{@link Grid}标签下被使用。
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
 * @author 牛佳庆
 */


/**
 * 列的编辑器,对应editors中的id
 * @property editor
 * @type String
 */

/**
 * <p>列编辑器函数,可动态改变编辑器</p>
 * <p>回调函数function(record,name){return editorid}</p>
 * <p>返回值editorid是编辑器的id</p>
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
 * 是否是隐藏列
 * @property hidden
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
 * 文字的对齐方式
 * @property align
 * @type String
 * @default left
 */ 

/**
 * 是否自动调整列宽
 * @property autoAdjust
 * @type Boolean
 * @default true
 */ 

/**
 * 列名
 * @property prompt
 * @type String
 */

/**
 * 列ID,主要用于个性化中
 * @property id
 * @type String
 */

/**
 * 列宽自动调整的最大值，最大值不会超过grid的宽度
 * @property maxAdjustWidth
 * @type Integer
 * @default 300
 */
 
/**
 * 是否对该列进行导出
 * @property forExport
 * @type Boolean
 * @default true
 */ 

/**
 * 导出时所代替的field名称
 * @property exportField
 * @type String
 */ 

/**
 * 导出数据格式
 * @property exportDataFormat
 * @type String
 */ 

/**
 * 导出数据类型
 * @property exportDataType
 * @type String
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
