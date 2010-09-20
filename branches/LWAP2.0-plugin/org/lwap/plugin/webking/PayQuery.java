package org.lwap.plugin.webking;

import java.util.Date;

import com.kingdee.bos.ebservice.EBException;
import com.kingdee.bos.ebservice.EBHeader;
import com.kingdee.bos.ebservice.PayBody;
import com.kingdee.bos.ebservice.PaymentDetail;
import com.kingdee.bos.ebservice.QueryPayResponse;
import com.kingdee.bos.ebservice.client.demo.junit.KingdeeEBException;
import com.kingdee.bos.ebservice.client.demo.utils.DateUtil;
import com.kingdee.bos.ebservice.client.hand.querypay.ClientQueryPayUtils;
import com.kingdee.bos.ebservice.client.hand.utils.EBHeaderUtils;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class PayQuery extends AbstractEntry {

	public String accno;
	public String batchid;
	private ServiceSettings settings;

	public PayQuery(ServiceSettings settings) {
		this.settings = settings;
		System.out.println("ImportExcel created");
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		int port = this.settings.getServicePORT();
		String ip = this.settings.getServiceIP();
		String path = "success";
		// // 从form通过get()方法取值
		String batchSeqID = context.getObject(batchid).toString();
		String accNo = context.getObject(accno).toString();

		String currency = "CNY";

		ClientQueryPayUtils queryPayUtils = new ClientQueryPayUtils(ip, port,
				true);
		EBHeader header = EBHeaderUtils.createHeader("MBTS", "MBTS6.0",
				"request", "pay", "pay", "queryPay", accNo, currency, DateUtil
						.formatDateTime(new Date()));

		QueryPayResponse queryPay = queryPayUtils.callWS(header, batchSeqID);
		EBException ebe = queryPay.getException();

		if (null != ebe) {
			throw new KingdeeEBException(ebe.getMessage());
		} else {
			PayBody detailBody = queryPay.getBody();
			PaymentDetail[] paydetail = detailBody.getDetails();
			CompositeMap cmlist = new CompositeMap("list");
			for (int i = 0; i < paydetail.length; i++) {
				CompositeMap record = new CompositeMap("record");
				record.put("DETAILSEQID", paydetail[i].getDetailSeqID()
						.toString());
				record.put("BIZNO", paydetail[i].getDetailBizNo().toString());
				record.put("PAYEEACCNO", paydetail[i].getPayeeAccNo()
						.toString());
				record.put("PAYEEACCNAME", paydetail[i].getPayeeAccName()
						.toString());
				record.put("PAYEETYPE", paydetail[i].getPayeeType().toString());
				record.put("EBSTATUSMSG", paydetail[i].getEbStatusMsg());
				record.put("BANKNAME", paydetail[i].getPayeeBankName()
						.toString());
				record.put("ADDR", paydetail[i].getPayeeBankAddr().toString());
				String country = paydetail[i].getPayeeCountry() == null ? ""
						: paydetail[i].getPayeeCountry().toString();
				record.put("COUNTRY", country);
				String province = paydetail[i].getPayeeProvince() == null ? ""
						: paydetail[i].getPayeeProvince().toString();
				record.put("PROVINCE", province);
				String city = paydetail[i].getPayeeCity() == null ? ""
						: paydetail[i].getPayeeCity().toString();
				record.put("CITY", city);
				String areacode = paydetail[i].getPayeeAreaCode() == null ? ""
						: paydetail[i].getPayeeAreaCode().toString();
				record.put("AREACODE", areacode);
				String cnpscode = paydetail[i].getPayeeCnapsCode() == null ? ""
						: paydetail[i].getPayeeCnapsCode().toString();
				record.put("CNPSCODE", cnpscode);
				record.put("AMOUNT", paydetail[i].getAmount().toString());
				cmlist.addChild(record);
			}
			CompositeMap cmmodel = (CompositeMap) context.getObject("model");

			cmmodel.addChild(cmlist);
		}

	}

}
