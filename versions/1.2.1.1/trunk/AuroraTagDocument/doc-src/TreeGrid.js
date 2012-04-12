/**
 * @class TreeGrid
 * 树控件标签
 * <pre><code>
&lt;a:treeGrid bindTarget="function_tree_ds" expandField="_expanded" height="400" id="functionTreeGrid" idField="function_id" 
    parentField="parent_function_id" showCheckBox="true" width="570"&gt;
    &lt;a:columns&gt;
        &lt;a:column name="function_name" prompt="功能名称" width="250"/&gt;
        &lt;a:column name="function_code" prompt="功能代码" width="120"/&gt;
        &lt;a:column editorFunction="expandEditorFunction" name="expanded" prompt="是否展开" renderer="expandRenderer" width="80"/&gt;
        &lt;a:column align="right" editor="grid_nf" name="sequence" prompt="序列号" width="100"/&gt;
    &lt;/a:columns&gt;
    &lt;a:editors&gt;
        &lt;a:numberField id="grid_nf"/&gt;
        &lt;a:checkBox id="grid_cb"/&gt;
    &lt;/a:editors&gt;
&lt;/a:treeGrid&gt;
   </code></pre>
 * @extends Grid
 * @author 牛佳庆
 */

/**
 * 是否有选择框
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
 * 显示树节点是否展开的field
 * @property expandField
 * @type String
 * @default expanded
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
 * @property rowRenderer
 * @remove
 */

/**
 * @property name
 * @remove
 */

/**
 * @property navBar
 * @remove
 */
