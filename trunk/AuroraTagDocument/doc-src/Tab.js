/**
 * @class Tab
 * 标签式布局标签
 * <pre><code>
    &lt;a:tabPanel width="300" height="250"&gt;
	&lt;a:tabs&gt;
		&lt;a:tab ref="xxx.screen" width="30"
			tabClassName="cls" tabStyle="s"/&gt;
		&lt;a:tab prompt="title" width="30" selected="true"
			bodyClassName="cls" bodyStyle="s"&gt;
			...
		&lt;/a:tab&gt;
	&lt;/a:tabs&gt;
&lt;/a:tabPanel&gt;
   </code></pre>
 * @extends Component
 * @author 牛佳庆
 */

/**
 * 引用页面地址
 * @property ref
 * @type String
 */
 
/**
 * 是否被选中
 * @property selected
 * @type Boolean
 * @default false
 */

/**
 * 标签显示文字
 * @property prompt
 * @type String
 */
 
/**
 * 标签的样式表
 * @property tabClassName
 * @type String
 */
 
/**
 * 标签的样式
 * @property tabStyle
 * @type String
 */
 
/**
 * 容器的样式表
 * @property bodyClassName
 * @type String
 */
 
/**
 * 容器的样式
 * @property bodyStyle
 * @type String
 */
 
/**
 * @property name
 * @remove
 */