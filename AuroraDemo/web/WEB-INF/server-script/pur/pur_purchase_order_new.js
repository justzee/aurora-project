//--put employee id and name into session
$ctx.session.employee_id = '1';
$ctx.session.employee_name = '莫言';
//--get sys_parameter
var config = $config();
var nf1=CompositeUtil.findChild(config,'numberField','id','e_nf_float1');
var nf2=CompositeUtil.findChild(config,'numberField','id','e_nf_float2');
$ctx.put('/sys_parameter/@BUSINESS_ACCURACY',2);
nf2.put('decimalPrecision',2);
$ctx.put('/sys_parameter/@QUANTITY_ACCURACY',2);
nf1.put('decimalPrecision',2);
$ctx.put('/sys_parameter/@TAX_RATE',17);
