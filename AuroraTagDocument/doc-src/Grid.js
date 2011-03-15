/**
 * @class Grid
 * <p>图表控件标签</p>
 * <p>列的属性可参阅{@link Grid[Column]}</p>
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
 * 是否具有导航条
 * @property navBar
 * @type Boolean
 * @default false
 */
 
/**
 * 导航条中是否有控制每页显示条数的下拉框
 * @property enablePageSize
 * @type Boolean
 * @default true
 */
 
/**
 * 是否允許使用粘贴功能来增加整行，该功能只有在IE下才有效。
 * @property canPaste
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