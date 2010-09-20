package org.lwap.plugin.webking;

import java.util.Date;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import com.kingdee.bos.ebservice.Balance;
import com.kingdee.bos.ebservice.BalanceResponse;
import com.kingdee.bos.ebservice.BalanceResponseBody;
import com.kingdee.bos.ebservice.Detail;
import com.kingdee.bos.ebservice.DetailResponse;
import com.kingdee.bos.ebservice.DetailResponseBody;
import com.kingdee.bos.ebservice.EBException;
import com.kingdee.bos.ebservice.EBHeader;
import com.kingdee.bos.ebservice.client.demo.junit.KingdeeEBException;
import com.kingdee.bos.ebservice.client.demo.utils.DateUtil;
import com.kingdee.bos.ebservice.client.hand.balance.ClientBalanceUtils;
import com.kingdee.bos.ebservice.client.hand.detail.ClientDetailUtils;
import com.kingdee.bos.ebservice.client.hand.utils.EBHeaderUtils;

public class DetailAction extends AbstractEntry {

	public String accno;
	public String startdate;
	public String enddate;
	private ServiceSettings settings;

	public DetailAction(ServiceSettings settings) {
		this.settings = settings;
		System.out.println("ImportExcel created");
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		int port = this.settings.getServicePORT();
		String ip = this.settings.getServiceIP();
		String path = "success";
		// // 从form通过get()方法取值
		String accNo = context.getObject(accno).toString();
		String startDate = context.getObject(startdate).toString().replaceAll(
				"-", "");
		String endDate = context.getObject(enddate).toString().replaceAll("-",
				"");

		String currency = "CNY";
		ClientDetailUtils detailUtils = new ClientDetailUtils(ip, port, true);

		EBHeader header = EBHeaderUtils.createHeader("MBTS", "MBTS6.0",
				"request", "detail", "normal_detail", "detail", accNo,
				currency, DateUtil.formatDateTime(new Date()));

		DetailResponse balance = detailUtils.callWS(header, startDate, endDate);
		EBException ebe = balance.getException();

		if (null != ebe) {
			throw new KingdeeEBException(ebe.getMessage());
		} else {
			DetailResponseBody detailBody = balance.getBody();
			Detail[] details = detailBody.getDetails();
			CompositeMap cmlist = new CompositeMap("list");
			for (int i = 0; i < details.length; i++) {
				CompositeMap cm = new CompositeMap("record");
				cm.put("DETAILID", details[i].getDetailID());
				cm.put("ACCNO", details[i].getAccNo());
				cm.put("OPPACCNO", details[i].getOppAccNo());
				cm.put("OPPACCNAME", details[i].getOppAccName());
				cm.put("OPPACCBANK", details[i].getOppAccBank());
				cm.put("DEBITAMOUNT", details[i].getDebitAmount());
				cm.put("CREDITAMOUNT", details[i].getCreditAmount());
				cm.put("CURRENCY", details[i].getCurrency());
				cm.put("BLANCE", details[i].getBalance());
				cm.put("DETAILDATETIME", details[i].getDetailDateTime());
				cm.put("EXPLANANATION", details[i].getExplanation());
				cmlist.addChild(cm);
			}

			CompositeMap cmmodel = (CompositeMap) context.getObject("model");

			cmmodel.addChild(cmlist);

		}

	}

}
