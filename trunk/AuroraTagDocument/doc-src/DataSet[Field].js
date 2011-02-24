/**
 * @class DataSet[Field]
 * <p>数据集组件下的字段，必须在{@link DataSet}标签下被使用。</p>
 * <pre><code>
&lt;a:dataSets&gt;
    &lt;a:dataSet id="evt_event_result_ds" autoCount="true" autoQuery="true"
		fetchAll="false" model="sys.evt_event" queryDataSet="evt_event_query_ds"
		selectable="true"&gt;
        &lt;a:fields&gt;
            &lt;a:field name="enabled_flag" checkedValue="Y" defaultValue="Y"
				uncheckedValue="N"/&gt;
            &lt;a:field name="event_name" required="true"/&gt;
	        &lt;a:field name="employee_code" lovGridHeight="300" lovHeight="430" lovWidth="500"&gt;
                lovService="sys.sys_user_employee_lov?ORDER_FIELD=employee_code"
	            &lt;a:mapping&gt;
	                &lt;a:map from="name" to="emp_name"/&gt;
	                &lt;a:map from="employee_code" to="employee_code"/&gt;
	                &lt;a:map from="employee_id" to="employee_id"/&gt;
	            &lt;/a:mapping&gt;
	        &lt;/a:field&gt;
        &lt;/a:fields&gt;
    &lt;/a:dataSet&gt;
&lt;/a:dataSets&gt;
   </code></pre>
 * @constructor 
 * @author 牛佳庆
 */

/**
 * field的name
 * @property name
 * @type String
 */

/**
 * 是否必输
 * @property required
 * @type Boolean
 * @default false
 */

/**
 * 是否只读
 * @property readOnly
 * @type Boolean
 * @default false
 */

/**
 * ComboBox选中值的返回name
 * @property returnField
 * @type String
 */

/**
 * ComboBox的options,对应DataSet的id
 * @property options
 * @type String
 */

/**
 * ComboBox的displayField
 * @property displayField
 * @type String
 */

/**
 * ComboBox的valueField
 * @property valueField
 * @type String
 */

/**
 * field的prompt
 * @property prompt
 * @type String
 */

/**
 * Lov弹出窗口的title
 * @property title
 * @type String
 */

/**
 * Lov对应的model
 * @property lovService
 * @type String
 */

/**
 * lov弹出窗口的宽度
 * @property lovWidth
 * @type Integer
 */

/**
 * lov弹出窗口的高度
 * @property lovHeight
 * @type Integer
 */

/**
 * Lov窗口中grid的高度
 * @property lovGridHeight
 * @type Integer
 */

/**
 * 默认值
 * @property defaultValue
 * @type String
 */

/**
 * checkbox选中的值
 * @property checkedValue
 * @type String
 */

/**
 * checkbox未选中的值
 * @property uncheckedValue
 * @type String
 */

/**
 * 自定义URL
 * @property lovUrl
 * @type String
 */
