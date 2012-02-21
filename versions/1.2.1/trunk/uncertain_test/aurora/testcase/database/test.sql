		SELECT t.*,to_char(sysdate,'yyyy-mm-dd') CLOSED_DATE FROM (
   select s.exp_requisition_header_id,
       s.exp_requisition_number,
       s.exp_requisition_barcode,
       s.employee_id,
       (select employee_code
          from exp_employees es
         where es.employee_id = s.employee_id) employee_code,
       (select name
          from exp_employees es
         where es.employee_id = s.employee_id) employee_name,
       s.payee_category,
       (  select code_value_name
    from sys_code_values_vl b, sys_codes a
   where b.code_id = a.code_id
     and a.code = 'PAYMENT_OBJECT'
     and code_value=s.payee_category) payee_category_name,
       s.payee_id,
       decode (s.PAYEE_CATEGORY,'EMPLOYEE' ,
       (select name||'-'||employee_code  from  exp_employees ee where ee.employee_id=s.payee_id),
       'CUSTOMER',
       (select vc.DESCRIPTION||'-'||vc.CUSTOMER_CODE from ord_system_customers_vl vc where vc.CUSTOMER_ID=s.payee_id ),
       ( select vv.DESCRIPTION||'-'||vv.VENDER_CODE from pur_system_venders_vl vv where vv.VENDER_ID=s.payee_id ))  payee_name,
       s.currency_code,
       s.exchange_rate_type,
       s.exchange_rate_quotation,
       (select sum(t.requisition_amount)
          from exp_requisition_lines t
         where t.exp_requisition_header_id = s.exp_requisition_header_id) QUARTER_NUM,
       (select sum(t.requisition_functional_amount)
          from exp_requisition_lines t
         where t.exp_requisition_header_id = s.exp_requisition_header_id) QUARTER_NUM_FUN,
       s.period_name,
       s.released_status,
       s.je_creation_status,
       s.gld_interface_flag,
       s.created_by,
       s.creation_date,
       s.last_updated_by,
       s.last_update_date,
       to_char(s.requisition_date, 'YYYY-MM-DD') REQUISITION_DATE,
       s.exp_requisition_type_id,
       (select erpv.DESCRIPTION
          from exp_expense_req_types_vl erpv
         where erpv.EXPENSE_REQUISITION_TYPE_ID = s.exp_requisition_type_id) type_description,
       (select erpv.EXPENSE_REQUISITION_TYPE_CODE
          from exp_expense_req_types_vl erpv
         where erpv.EXPENSE_REQUISITION_TYPE_ID = s.exp_requisition_type_id) TYPE_CODE,
       s.description,
       s.requisition_status requisition_status_value,
       (select b.code_value_name
          from sys_codes_vl a, sys_code_values_vl b
         where a.code_id = b.code_id
           and a.code = 'EXP_EXPENSE_REPORT_STATUS'
           and b.code_value = s.requisition_status) REQUISITION_STATUS
  from exp_requisition_headers s
         where s.company_id = ?
         and (s.reversed_flag is null or s.reversed_flag='N')
         and s.audit_flag = 'Y'
         and exists(select 1
			from exp_requisition_dists t,
						  exp_requisition_lines l
			where t.exp_requisition_line_id = l.exp_requisition_line_id
			and (t.close_flag is null or t.close_flag='N')
			and l.exp_requisition_header_id =s.exp_requisition_header_id   )
		) t
		 WHERE requisition_status_value = to_char(?) ORDER BY exp_requisition_number desc