package org.lwap.plugin.webking;
/*
 * 网银循环调用的支付结果*/
import com.kingdee.bos.ebservice.EBException;
import com.kingdee.bos.ebservice.EBHeader;
import com.kingdee.bos.ebservice.PayBody;
import com.kingdee.bos.ebservice.PaymentDetail;
import com.kingdee.bos.ebservice.QueryPayResponse;
import com.kingdee.bos.ebservice.client.demo.junit.KingdeeEBException;
import com.kingdee.bos.ebservice.client.demo.utils.DateUtil;
import com.kingdee.bos.ebservice.client.hand.querypay.ClientQueryPayUtils;
import com.kingdee.bos.ebservice.client.hand.utils.EBHeaderUtils;
import java.util.Date;
import uncertain.composite.CompositeMap;

public class PayQueryUtil {

	public static void payQuery(String batchSeqID, String accNo, String ip,
			int port, CompositeMap cmlist) throws Exception {
		String path = "success";

		String currency = "CNY";

		ClientQueryPayUtils queryPayUtils = new ClientQueryPayUtils(ip, port,
				true);
		EBHeader header = EBHeaderUtils.createHeader("MBTS", "MBTS6.0",
				"request", "pay", "pay", "queryPay", accNo, currency, DateUtil
						.formatDateTime(new Date()));

		QueryPayResponse queryPay = queryPayUtils.callWS(header, batchSeqID);
		EBException ebe = queryPay.getException();

		if (ebe != null) {
			throw new KingdeeEBException(ebe.getMessage());
		}
		PayBody detailBody = queryPay.getBody();
		PaymentDetail[] paydetail = detailBody.getDetails();
		for (int i = 0; i < paydetail.length; i++) {
			CompositeMap record = new CompositeMap("record");
			record.put("BATCHSEQID", batchSeqID);
			record.put("DETAILSEQID", paydetail[i].getDetailSeqID());
			record.put("BIZNO", paydetail[i].getDetailBizNo());
			record.put("PAYEEACCNO", paydetail[i].getPayeeAccNo());
			record.put("PAYEEACCNAME", paydetail[i].getPayeeAccName());
			record.put("PAYEETYPE", paydetail[i].getPayeeType());
			record.put("EBSTATUSMSG", paydetail[i].getBankStatusMsg());

			String bankstatus = paydetail[i].getBankStatus();
			try{
			int begin = bankstatus.indexOf('>', 1);
			int end = bankstatus.indexOf('<', 2);
			bankstatus = bankstatus.substring(begin + 1, end);
			}catch(Exception e){
				
			}
			
			record.put("EBSTATUS", bankstatus);
			record.put("BANKNAME", paydetail[i].getPayeeBankName());
			record.put("MSTATUS", paydetail[i].getEbStatus());
			record.put("MSTATUSMSG", paydetail[i].getBankStatusMsg());
			record.put("ADDR", paydetail[i].getPayeeBankAddr());
			String country = paydetail[i].getPayeeCountry() == null ? ""
					: paydetail[i].getPayeeCountry();
			record.put("COUNTRY", country);
			String province = paydetail[i].getPayeeProvince() == null ? ""
					: paydetail[i].getPayeeProvince();
			record.put("PROVINCE", province);
			String city = paydetail[i].getPayeeCity() == null ? ""
					: paydetail[i].getPayeeCity();
			record.put("CITY", city);
			String areacode = paydetail[i].getPayeeAreaCode() == null ? ""
					: paydetail[i].getPayeeAreaCode();
			record.put("AREACODE", areacode);
			String cnpscode = paydetail[i].getPayeeCnapsCode() == null ? ""
					: paydetail[i].getPayeeCnapsCode();
			record.put("CNPSCODE", cnpscode);
			record.put("AMOUNT", paydetail[i].getAmount());
			cmlist.addChild(record);
		}
	}
}