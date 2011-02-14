/**
 * @class Tree
 * 树控件标签
 * <pre><code>
    &lt;a:tree bindTarget="ds" checkField="checked" expandField="expanded"
	displayField="function_name" idField="function_id" showCheckBox="true"
	parentField="parent_function_id" sequenceField="seq" renderer="fun"/&gt;
   </code></pre>
 * @extends Component
 * @author 牛佳庆
 */

/**
 * dataset中用来显示树节点前是否有选择框的field
 * @property showCheckBox
 * @type String
 * @default false
 */

/**
 * dataset中用来显示树节点是否被选中的field
 * @property checkField
 * @type String
 * @default checked
 */

/**
 * dataset中用来显示树节点是否展开的field
 * @property expandField
 * @type String
 * @default expanded
 */

/**
 * dataset中用来显示树节点描述的field
 * @property displayField
 * @type String
 * @default name
 */

/**
 * dataset中用来唯一标识树节点的field
 * @property idField
 * @type String
 * @default id
 */

/**
 * dataset中用来标识父节点的field
 * @property parentField
 * @type String
 * @default pid
 */
 
/**
 * dataset中用来排序树节点的field
 * @property sequenceField
 * @type String
 * @default sequence
 */

/**
 * <p>列渲染</p> 
 * <p>回调函数function(value,record){return value}</p>
 * <p>返回值value是html字符串</p>
 * @property renderer
 * @type Function
 */
 
/**
 * @property name
 * @remove
 */