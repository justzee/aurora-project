/**
 * @class DataSet
 * <p>数据集组件</p>
 * <p>数据集下的字段可参阅{@link DataSet[Field]}</p>
 * <p>数据集下的数据项可参阅{@link DataSet[Record]}</p>
 * <pre><code>
&lt;a:dataSets&gt;
    &lt;a:dataSet id="evt_event_query_ds"&gt;
        &lt;a:datas&gt;
            &lt;a:record name="aaa"/&gt;
        &lt;a:record name="bbb"/&gt;
        &lt;/a:datas&gt;
    &lt;/a:dataSet&gt;
    &lt;a:dataSet id="evt_event_result_ds" autoCount="true" autoQuery="true"
		fetchAll="false" model="sys.evt_event" queryDataSet="evt_event_query_ds"
		selectable="true"&gt;
        &lt;a:fields&gt;
            &lt;a:field name="enabled_flag" checkedValue="Y" defaultValue="Y"
				uncheckedValue="N"/&gt;
            &lt;a:field name="event_name" required="true"/&gt;
        &lt;/a:fields&gt;
    &lt;/a:dataSet&gt;
&lt;/a:dataSets&gt;
   </code></pre>
 * @constructor 
 * @author 牛佳庆
 */

/**
 * 是否自动创建一条数据
 * @property autoCreate
 * @type Boolean
 * @default false
 */

/**
 * 是否客户端自动发起ajax查询
 * @property autoQuery
 * @type Boolean
 * @default false
 */

/**
 * 查询是否进行统计
 * @property autoCount
 * @type Boolean
 * @default false
 */

/**
 * 根据Grid的高度，自动填充行数。注：只在grid初次加载时生效，如果Grid设定了marginHeigt，行数不会根据窗口大小的调整而调整。
 * @property autoPageSize
 * @type Boolean
 * @default false
 */

/**
 * 是否可查询
 * @property canQuery
 * @type Boolean
 * @default true
 */

/**
 * 是否可以提交
 * @property canSubmit
 * @type Boolean
 * @default true
 */

/**
 * 是否查询所有数据
 * @property fetchAll
 * @type Boolean
 * @default false
 */

/**
 * DataSet的ID
 * @property id
 * @type String
 */

/**
 * 是否服务端加载数据
 * @property loadData
 * @type Boolean
 * @default false
 */

/**
 * 值列表的CODE
 * @property lookupCode
 * @type String
 */

/**
 * DataSet关联的BM
 * @property model
 * @type String
 */

/**
 * 查询的分页大小
 * @property pageSize
 * @type Integer
 */

/**
 * 查询的DataSet的id
 * @property queryDataSet
 * @type String
 */

/**
 * 查询的URL地址
 * @property queryUrl
 * @type String
 */

/**
 * 是否可选择
 * @property selectable
 * @type Boolean
 * @default false
 */

/**
 * 用回调函数判断行是否可选择
 * <p>回调函数function(record){return true|false}</P>
 * <p>当返回值为false时，该record无法被选择</P>
 * @property selectFunction
 * @type Function
 */

/**
 * 选择模式，可选值：single|multiple
 * @property selectionModel
 * @type String
 */

/**
 * 提交的URL地址
 * @property submitUrl
 * @type String
 */
