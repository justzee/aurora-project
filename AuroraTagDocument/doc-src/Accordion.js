/**
 * @class Accordion
 * 可折叠布局标签
 * <pre><code>
    &lt;a:accordionPanel width="300" height="250" singleMode="false"&gt;
	&lt;a:accordions&gt;
		&lt;a:accordion ref="xxx.screen"/&gt;
		&lt;a:accordion prompt="title" selected="true"&gt;
			...
		&lt;/a:accordion&gt;
	&lt;/a:accordions&gt;
&lt;/a:accordionPanel&gt;
   </code></pre>
 * @extends Component
 * @author 吴华真
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
 * 单选模式
 * @property singleMode
 * @type Boolean
 * @default true
 */
 
/**
 * @property name
 * @remove
 */