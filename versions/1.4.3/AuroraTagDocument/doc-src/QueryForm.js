/**
 * @class QueryForm
 * 单项选择框控件标签
 * <pre><code>
    &lt;a:queryForm bindTarget="SYS1080_query_form_ds" defaultQueryField="transaction_type_code" defaultQueryHint="请输入事务类型代码或名称" queryHook="SYS1080_queryHook" resultTarget="SYS1080_result_ds" style="width:100%;border:none"&gt;
    &lt;a:formBody style="width:100%"&gt;
        &lt;a:hBox labelWidth="100"&gt;
            &lt;a:textField name="transaction_type_code" prompt="事务代码" typeCase="upper"&gt;
                &lt;a:events&gt;
                    &lt;a:event name="enterdown" handler="SYS1080_results_query"/&gt;
                &lt;/a:events&gt;
            &lt;/a:textField&gt;
            &lt;a:textField name="transaction_type_desc" prompt="事务名称"&gt;
                &lt;a:events&gt;
                    &lt;a:event name="enterdown" handler="SYS1080_results_query"/&gt;
                &lt;/a:events&gt;
            &lt;/a:textField&gt;
            &lt;a:textField name="business_type_code" prompt="业务类型代码" typeCase="upper"&gt;
                &lt;a:events&gt;
                    &lt;a:event name="enterdown" handler="SYS1080_results_query"/&gt;
                &lt;/a:events&gt;
            &lt;/a:textField&gt;
            &lt;a:textField name="business_type_desc" prompt="业务类型描述"&gt;
                &lt;a:events&gt;
                    &lt;a:event name="enterdown" handler="SYS1080_results_query"/&gt;
                &lt;/a:events&gt;
            &lt;/a:textField&gt;
        &lt;/a:hBox&gt;
    &lt;/a:formBody&gt;
&lt;/a:queryForm&gt;
   </code></pre>
 * @extends Component
 * @author 牛佳庆
 */

 
/**
 * 是否默认展开
 * @property expand
 * @type Boolean
 * @default false
 */
 
/**
 * queryForm标题
 * @property title
 * @type String
 */ 
 
/**
 * 映射的DataSet
 * @property resulttarget
 * @type String
 */
 
/**
 * 默认的查询Field
 * @property defaultqueryfield
 * @type String
 */ 
 
/**
 * 默认的查询Field的输入提示信息
 * @property defaultqueryhint
 * @type String
 */ 
 
 
/**
 * 默认的查询Field的prompt
 * @property defaultqueryprompt
 * @type String
 */ 
 
/**
 * 查询的hook函数
 * @property queryhook
 * @type String
 */ 
 
 /**
 * 是否创建查询按钮
 * @property createsearchbutton
 * @type Boolean
 * @default true;
 */
 
 /**
  * @property marginHeight
  * @remove
  */


 /**
  * @property marginWidth
  * @remove
  */
 
 