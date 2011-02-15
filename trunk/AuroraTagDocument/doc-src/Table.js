/**
 * @class Table
 * <p>自适应宽度和高度的图表控件标签</p>
 * <p>列的属性可参阅{@link Table[Column]}</p>
 * <pre><code>
    &lt;a:table bindTarget="ds" rowRenderer="fun"
	width="300" percentWidth="90" title="tt"&gt;
	&lt;a:columns&gt;
		&lt;a:column editor="tf" name="fieldName"
		renderer="fun" footerRenderer="fun"
		width="30" percentWidth="10" align="center"/&gt;
	&lt;/a:columns&gt;
	&lt;a:editors&gt;
		&lt;a:textField id="tf"/&gt;
	&lt;/a:editors&gt;
&lt;/a:table&gt;
   </code></pre>
 * @extends Component
 * @author 吴华真
 */

/**
 * 组件的百分比宽度,单位是(%)
 * @property percenWidth
 * @type Integer
 */

/**
 * 表的标题
 * @property title
 * @type String
 */

/**
 * <p>通过回调函数返回的样式表渲染指定行</p> 
 * <p>回调函数function(record,rowIndex){return css}</p>
 * <p>返回值css值可以是class或者style字符串,也可以是class和style字符串数组</p>
 * @property rowRenderer
 * @type Function
 */
 
/**
 * 组件的宽度,单位是像素(px)
 * @property width
 * @type Integer
 */

/**
 * @property height
 * @remove
 */
 
 /**
 * @property marginHeight
 * @remove
 */
 
  /**
 * @property marginWidth
 * @remove
 */