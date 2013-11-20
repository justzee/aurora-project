/**
 * @class Upload
 * 上传组件,可以控制文件类型，文件大小
 * <pre><code>
&lt;a:upload buttonWidth="75" fileType="*.jpg" pkvalue="1" sourcetype="znjq" text="上传图片"/&gt;
   </code></pre> 
 * @author 牛佳庆
 */
 
/**
 * 上传按钮文字
 * @property text
 * @type String
 * @default upload
 */ 
 
/**
 * 上传文件类型
 * @property fileType
 * @type String
 * @default *.*
 */  
 
/**
 * 上传文件大小 单位是KB, 默认是0表示无限制
 * @property filesize
 * @type Integer
 * @default 0
 */  
 
/**
 * 上传按钮大小
 * @property buttonWidth
 * @type Integer
 * @default 50
 */
 
/**
 * 业务来源
 * @property sourcetype
 * @type String
 */
 
/** 
 * 业务主键
 * @property pkvalue
 * @type String
 */
 
 /** 
 * 上传URL
 * @property uploadUrl
 * @type String
 * @default atm_upload.svc
 */ 

 /** 
 * 删除URL
 * @property deleteUrl 
 * @type String
 * @default atm_delete.svc
 */
 
 /** 
 * 下載URL
 * @property downloadUrl 
 * @type String
 * @default atm_download.svc
 */ 