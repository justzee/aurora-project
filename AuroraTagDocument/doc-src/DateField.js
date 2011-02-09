/**
 * @class DateField
 * <pre><code>
    &lt;a:dateField enableBesideDays="pre" enableMonthBtn="next" dayRenderer="renderer"
	width="300" height="300"/&gt;
   </code></pre>
 * @extends Component
 * @author 牛佳庆
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
 * <p>日期渲染</p> 
 * <p>回调函数function(cell,date,text){return text}</p>
 * <p>当cell.disabled=true时，该日期无法被选择</p>
 * @property dayRenderer
 * @type Function
 */
 
/**
 * 组件的高度,单位是像素(px)
 * @property height
 * @type Integer
 * @default 150
 */