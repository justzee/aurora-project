package org.lwap.plugin.webking;

import java.util.Date;

import uncertain.composite.CompositeMap;

import com.kingdee.bos.ebservice.EBException;
import com.kingdee.bos.ebservice.EBHeader;
import com.kingdee.bos.ebservice.PayBody;
import com.kingdee.bos.ebservice.PaymentDetail;
import com.kingdee.bos.ebservice.QueryPayResponse;
import com.kingdee.bos.ebservice.client.demo.junit.KingdeeEBException;
import com.kingdee.bos.ebservice.client.demo.utils.DateUtil;
import com.kingdee.bos.ebservice.client.hand.querypay.ClientQueryPayUtils;
import com.kingdee.bos.ebservice.client.hand.utils.EBHeaderUtils;

public class PayQueryUtil {

	public static void payQuery(String batchSeqID, String accNo, String ip, int port,CompositeMap cmlist) throws Exception {
		String path = "success";
		// // 从form通过get()方法取值

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
			for (int i = 0; i < paydetail.length; i++) {
				CompositeMap record = new CompositeMap("record");
				record.put("DETAILSEQID", paydetail[i].getDetailSeqID());
				record.put("BIZNO", batchSeqID);
				record.put("PAYEEACCNO", paydetail[i].getPayeeAccNo()
						.toString());
				record.put("PAYEEACCNAME", paydetail[i].getPayeeAccName()
						.toString());
				record.put("PAYEETYPE", paydetail[i].getPayeeType().toString());
				record.put("EBSTATUSMSG", paydetail[i].getEbStatusMsg());
				record.put("EBSTATUS",paydetail[i].getEbStatus());
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
				System.out.println(paydetail[i].getBankBatchSeqID()+"-"+paydetail[i].getBankBatchSeqID()+"-"+paydetail[i].getDetailBizNo()+
						"-"+paydetail[i].getDetailSeqID()+"-"+paydetail[i].getEbSeqID()+"-"+paydetail[i].getFlowSerialNo()+"-"+
						paydetail[i].getForceManual()+"-"+paydetail[i].getRqstSerialNo()+"-"+paydetail[i].getRspSerialNo());
			}
		}
	}
}
