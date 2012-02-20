/**
 * @class Table[Column]
 * 自适应宽度和高度的图表下的列标签，必须在{@link Table}标签下被使用。
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
 * @author 吴华真
 */

/**
 * 列的编辑器,对应editors中的id
 * @property editor
 * @type String
 */

/**
 * 列的百分比宽度,单位是(%)
 * @property percentWidth
 * @type Integer
 */


/**
 * 列的宽度,单位是像素(px)
 * @property width
 * @type Integer
 */

/**
 * 文字的对齐方式
 * @property align
 * @type String
 * @default left
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