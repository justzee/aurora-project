package org.lwap.plugin.webking;
/*
 *  网银同步程序
 * */
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.lwap.database.TransactionFactory;
import org.lwap.plugin.quartz.SchedulerConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;

public class PayDetailSynchronizer implements Job {
	public static Integer running = new Integer(0);

	public PayDetailSynchronizer() {

	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		synchronized (running) {
			if (running.intValue() == 1) {
				return;
			}
			running = new Integer(1);
		}

		UncertainEngine engine = SchedulerConfig.getUncertainEngine(context
				.getJobDetail().getJobDataMap());
		IObjectRegistry os = engine.getObjectRegistry();
		TransactionFactory t_fact = (TransactionFactory) os
				.getInstanceOfType(TransactionFactory.class);
		Logger logger = engine.getLogger();
		try {
			/*
			 * synlist.xml 查询处理的数据来源
			 * */
			CompositeMap resultmap = new CompositeMap("resultmap");
			resultmap = t_fact.query("synlist.xml", new CompositeMap(
					"parameter"));

			Iterator it = ((CompositeMap) resultmap.getObject("/model/synlist"))
					.getChildIterator();
			CompositeMap resultlist = new CompositeMap("resultlist");

			while (it.hasNext()) {
				CompositeMap record = (CompositeMap) it.next();
				PayQueryUtil.payQuery(record.getString("BATCH_ID"), record
						.getString("ACCNO"), record.getString("IP"), record
						.getInt("PORT"), resultlist);
			}
			/*
			 * synresult.xml 查询结果用这个package处理
			 * */
			t_fact.execute("synresult.xml", resultlist);
		} catch (Exception ex) {
			logger.info(ex.getMessage());
		} finally {
			running = new Integer(0);
		}
	}

}
