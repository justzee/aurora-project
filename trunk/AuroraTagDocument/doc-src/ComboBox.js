/**
 * @class ComboBox
 * 下拉菜单控件标签
 * <pre><code>
    &lt;a:comboBox displayField="name" valueField="code" options="dsId"
	bindTarget="dsId" name="fieldName" prompt="description"/&gt;
   </code></pre>
 * @extends TriggerField
 * @author 牛佳庆
 */

/**
 * 选项集的dataset中用来显示选项描述的field
 * @property displayField
 * @type String
 * @default name
 */

/**
 * 选项集的dataset的id
 * @property options
 * @type String
 */
 
/**
 * 选项集的dataset中用来定义选项被选中后的值的field
 * @property valueField
 * @type String
 * @default code
 */