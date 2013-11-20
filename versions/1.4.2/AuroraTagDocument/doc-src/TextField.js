/**
 * @class TextField
 * 文本输入框,可限制大小写
 * <pre><code>
&lt;a:textField bindTarget="login_dataset" id="user_name_tf" name="user_name" prompt="HAP_USERNAME" width="150"&gt;
    &lt;a:events&gt;
        &lt;a:event handler="login" name="enterdown"/&gt;
    &lt;/a:events&gt;
&lt;/a:textField&gt;
</code></pre>
 * @extends InputField
 * @author 牛佳庆
 */
 
 
/**
 * 输入框类型
 * @property inputType
 * @type String
 * @default input
 */

/**
 * 输入大小写限制
 * @property typeCase
 * @type String
 * 
 */
 
/**
 * 通过正则表达式限制输入内容
 * @property restrict
 * @type String
 * 
 */ 

 /**
  * 输入限制提示信息
  * @property restrictinfo
  * @type String
  */