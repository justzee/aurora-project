package org.lwap.plugin.webking;

import java.util.Date;

import org.lwap.plugin.dataimport.ImportSettings;

import com.kingdee.bos.ebservice.Balance;
import com.kingdee.bos.ebservice.BalanceResponse;
import com.kingdee.bos.ebservice.BalanceResponseBody;
import com.kingdee.bos.ebservice.EBException;
import com.kingdee.bos.ebservice.EBHeader;
import com.kingdee.bos.ebservice.client.demo.junit.KingdeeEBException;
import com.kingdee.bos.ebservice.client.hand.balance.ClientBalanceUtils;
import com.kingdee.bos.ebservice.client.hand.utils.DateUtil;
import com.kingdee.bos.ebservice.client.hand.utils.EBHeaderUtils;


import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class BlanceQuery extends AbstractEntry {
	
	 public String accno;
	 
	 private ServiceSettings settings;

	 public BlanceQuery(ServiceSettings settings) {
			this.settings = settings;
			 System.out.println("ImportExcel created");
		}
	
	public String getAccno() {
		return accno;
	}



	public void setAccno(String accno) {
		this.accno = accno;
	}



	public void run(ProcedureRunner runner) throws Exception {
	        
	        CompositeMap context = runner.getContext();
	        System.out.println(context.toXML());
	    	setAccno(context.getObject(accno).toString());
	        int port = this.settings.getServicePORT();
			String ip = this.settings.getServiceIP();
			String path = "success";
//			// 从form通过get()方法取值
			String currency = "CNY";
			ClientBalanceUtils balanceUtils = new ClientBalanceUtils(ip,port,true);
			
			
			
			EBHeader header = EBHeaderUtils.createHeader("MBTS",
					                                     "MBTS6.0",
					                                     "request",
					                                     "balance",
					                                     "today_balance",
					                                     "balance",
					                                     accno,
					                                     currency,
					                                     DateUtil.formatDateTime(new Date()));
			
                System.out.println (header.getAccNo());
				BalanceResponse balance = null;
					balance = balanceUtils.callWS(header);
				
				EBException ebe = balance.getException();
				
				
				if(null != ebe){
						throw new KingdeeEBException(ebe.getMessage());
					
				}else{			
					BalanceResponseBody detailBody = balance.getBody();
					Balance[] balances = detailBody.getBalances();
					CompositeMap cmlist = new CompositeMap("list");
					for (int i=0;i<balances.length;i++){
						CompositeMap cm = new CompositeMap("record");
						cm.put("AVAILABLEBALANCE", balances[i].getAvailableBalance());
						cm.put("ACCNO",accno );
						cm.put("CURRENCY", balances[i].getCurrency());
						cmlist.addChild(cm);
					}
			    	CompositeMap cmmodel = (CompositeMap) context.getObject("model");

	               cmmodel.addChild(cmlist);
				}

				System.out.println(context.toXML());
	    }		
}	
