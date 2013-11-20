/**
 * @class Lov
 * 弹出窗口式输入框控件标签
 * <pre><code>
    &lt;a:lov displayField="dfield" valueField="vfield"
	bindTarget="dsId" name="fieldName" prompt="description"
	lovWidth="300" lovHeight="300" lovGridHeight="300"
	lovService="model" title="title" fetchRemote="false"/&gt;
   </code></pre>
 * @extends TextField
 * @author 牛佳庆
 */
 
/**
 * 手工输入后是否自动查询数据
 * @property fetchRemote
 * @type Boolean
 * @default true
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
 * Lov弹出窗口是否自动查询
 * @property lovAutoQuery
 * @type Boolean
 * @default true
 */

/**
 * lov弹出窗口查询条件字段描述的宽度
 * @property lovLabelWidth
 * @type Number
 * @default 75
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
 * 是否可输入
 * @property editable
 * @type Boolean
 * @default true
 */
 
/**
 * 是否通过输入内容自动查询返回给文本框
 * @property fetchremote
 * @type Boolean
 * @default true
 */
 
/**
 * 自动查询结果中有相同结果时，是否弹出选择框
 * @property fetchsingle
 * @type Boolean
 * @default false
 */
 
/**
 * <p>autoComplete的渲染函数</p>
 * <p>函数参数为 function(Lov,record)</p>
 * <p>返回值:html字符串</p>
 * @property autoCompleteRenderer
 * @type Functon
 */