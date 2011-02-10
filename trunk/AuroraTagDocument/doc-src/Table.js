/**
 * @class Table
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
 * 列的编辑器,对应editors中的id
 * @property editor
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