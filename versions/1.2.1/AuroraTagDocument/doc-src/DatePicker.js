/**
 * @class DatePicker
 * 日期选择控件标签
 * <pre><code>
    &lt;a:datePicker enableBesideDays="pre" enableMonthBtn="next"
	dayRenderer="renderer" viewSize="3"/&gt;
   </code></pre>
 * @extends TriggerField
 * @author 吴华真
 */
 
/**
 * <p>日期渲染</p> 
 * <p>回调函数function(cell,date,text){return text}</p>
 * <p>当cell.disabled=true时，该日期无法被选择</p>
 * <p>返回值text是html字符串</p>
 * @property dayRenderer
 * @type Function
 */
 
/**
 * 在本月的始末补齐前后月份的日期，可选值：both|pre|next|none
 * @property enableBesideDays
 * @type String
 * @default both
 */
 
/**
 * 月份选择按钮显示方式，可选值：both|pre|next|none
 * @property enableMonthBtn
 * @type String
 * @default both
 */

/**
 * 日历显示个数,4为最大值
 * @property viewSize
 * @type Integer
 * @default 1
 */