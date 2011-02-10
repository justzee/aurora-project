/**
 * @class Menu
 * <pre><code>
    &lt;a:menuBar bindTarget="ds" iconField="icon"
	displayField="function_name" idField="function_id"
	parentField="parent_function_id" urlField="command_line"
	urlTarget="main" sequenceField="seq" renderer="fun"/&gt;
   </code></pre>
 * @extends Component
 * @author 吴华真
 */

/**
 * dataset中用来显示菜单的图标的field
 * @property iconField
 * @type String
 * @default icon
 */

/**
 * dataset中用来显示菜单描述的field
 * @property displayField
 * @type String
 * @default name
 */

/**
 * dataset中用来唯一标识菜单的field
 * @property idField
 * @type String
 * @default id
 */

/**
 * dataset中用来标识菜单的父菜单的field
 * @property parentField
 * @type String
 * @default pid
 */
 
/**
 * dataset中用来排序菜单的field
 * @property sequenceField
 * @type String
 * @default sequence
 */

/**
 * dataset中用来指定菜单点击后打开页面的url的field
 * @property urlField
 * @type String
 * @default url
 */

/**
 * <p>菜单点击后打开页面的对象</p>
 * <p>当urlTarget被指定时</p>
 * <p>如果找到name为该值的iframe时，新页面将在iframe中被打开。</p>
 * <p>如果未找到name为该值的iframe，新页面将在Aurora.Window中被打开。</p>
 * <p>当urlTarget未被指定时，新页面将在新窗口中被打开</p>
 * @property urlTarget
 * @type String
 */

/**
 * <p>列渲染</p> 
 * <p>回调函数function(value,record){return value}</p>
 * <p>返回值value是html字符串</p>
 * @property renderer
 * @type Function
 */
 
/**
 * 组件的高度,单位是像素(px)
 * @property height
 * @type Integer
 */

/**
 * 组件的宽度,单位是像素(px)
 * @property width
 * @type Integer
 */

/**
 * @property name
 * @remove
 */