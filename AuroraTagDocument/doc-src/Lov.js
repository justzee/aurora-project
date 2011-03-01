/**
 * @class Lov
 * 弹出窗口式输入框控件标签
 * <pre><code>
    &lt;a:lov displayField="dfield" valueField="vfield"
	bindTarget="dsId" name="fieldName" prompt="description"
	lovWidth="300" lovHeight="300" lovGridHeight="300"
	lovService="model" lovUrl="url" keepError="true"/&gt;
   </code></pre>
 * @extends TextField
 * @author 牛佳庆
 */

/**
 * 选项显示的值
 * @property displayField
 * @type String
 */
 
/**
 * 手工输入后是否自动查询数据
 * @property fetchRemote
 * @type Boolean
 * default true
 */
 
/**
 * Lov窗口中grid的高度
 * @property lovGridHeight
 * @type Integer
 * @default 350
 */
 
/**
 * lov弹出窗口的高度
 * @property lovHeight
 * @type Integer
 * @default 400
 */
 
/**
 * Lov对应的model
 * @property lovService
 * @type String
 */

/**
 * 自定义URL
 * @property lovUrl
 * @type String
 */
 
/**
 * lov弹出窗口的宽度
 * @property lovWidth
 * @type Integer
 * @default 400
 */
 
/**
 * Lov弹出窗口的title
 * @property title
 * @type String
 */
 
/**
 * 选项的值
 * @property valueField
 * @type String
 */