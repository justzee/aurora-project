/**
 * @class DataSet[Field[Map]]
 * <p>lov或者combobox和当前dataSet的映射关系，必须在{@link DataSet[Field]}标签下被使用。</p>
 * <pre><code>
&lt;a:dataSets&gt;
    &lt;a:dataSet id="evt_event_result_ds" autoCount="true" autoQuery="true"
	fetchAll="false" model="sys.evt_event" queryDataSet="evt_event_query_ds"
	selectable="true"&gt;
        &lt;a:fields&gt;
            &lt;a:field name="employee_code" lovGridHeight="300" lovHeight="430" lovWidth="500"
                lovService="sys.sys_user_employee_lov?ORDER_FIELD=employee_code"&gt;
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
 * 映射关系从,一般对应lov或者combobox的dataset的field
 * @property from
 * @type String
 */

/**
 * 映射关系到,对应当前dataset的field
 * @property to
 * @type String
 */
