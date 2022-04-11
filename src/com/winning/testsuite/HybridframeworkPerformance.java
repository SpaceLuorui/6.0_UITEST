package com.winning.testsuite;


import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

import org.junit.Test;
import xunleiHttpTest.Util.Log;

import java.awt.AWTException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HybridframeworkPerformance extends OutpatientTestBase {

	public HybridframeworkPerformance() {
		super();
	}

	static {
		SdkTools.initReport("混合框架性能测试", "HybridframeworkPerformance.html");
		Data.headless = false;
		Data.useHybirdApp = true;
	}

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");

	/**
	 * 1.频繁启动 从混合框架启动程序，然后登录到医生站界面，然后关闭程序，循环10次
	 *
	 **/
	@Test
	public void FrequentStartup() {
		Map<String, String> caseMap = new HashMap<String, String>();
		Framework.autoReport = false;
		for (int i = 1; i <= Data.clientNum; i++) {
			// 单条测试结果和错误信息
			boolean testres = true;
			String errmsg = "";
			try {
				String picName = "频繁启动-第" + i + "次打开关闭";
				// 输出报告用到
				caseMap.put("CASE_NAME", picName);
				caseMap.put("START", dateFormat.format(new Date()));
//            Cmd.startExeApp(startHybirdAppCmd);
				browser = new OutpatientBrowser(picName);
//                browser.wnwd.getScreenShot(picName);
				browser.wnwd.sleep(3000);
				SdkSystemProcess.stopExeApp("Win60.exe");
				// 输出报告用到
				caseMap.put("END", dateFormat.format(new Date()));
				browser.wnwd.sleep(3000);
			} catch (Throwable e) {
				testres = false;
				errmsg = e.getMessage();
			} finally {
				caseMap.put("ERRMSG", errmsg);
				if (testres) {
					caseMap.put("RESULT", "通过");
					caseMap.put("CLASS", "TABLEID passCase");
				} else {
					caseMap.put("RESULT", "不通过");
					caseMap.put("CLASS", "TABLEID failCase");
				}
				caseMap.put("ONCLICK", "window.open('capture/" + browser.logger.fileName + "')");
				SdkTools.saveCaseToReport(Framework.savereportFile, caseMap);
			}
		}

	}

	/**
	 * 2.多客户端运行 顺序启动5个客户端进行UI登录，诊断，和开立且不关闭
	 **/
	@Test
	public void ManyClient() {
		Framework.autoReport = false;
		Data.closeBrowser = false;
		Map<String, String> ManyClientcaseMap = new HashMap<String, String>();
		Framework.autoReport = false;
		for (int i = 1; i <= Data.clientNum; i++) {
			boolean testres = true;
			String errmsg = "";
			try {
				String CaseName = "多客户端第" + i + "个";
				// 输出报告用到
				ManyClientcaseMap.put("CASE_NAME", CaseName);
				ManyClientcaseMap.put("START", dateFormat.format(new Date()));
				browser = new OutpatientBrowser(CaseName);
				browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
                browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
                browser.wnOutpatientWorkflow.skip();
                ArrayList<String> encounterInfo = browser.decouple.newEncounter();
                browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
                browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
                browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
                browser.wnOutpatientWorkflow.unselectSearchStock();
                browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Drug);
				ManyClientcaseMap.put("END", dateFormat.format(new Date()));
			} catch (Throwable e) {
				testres = false;
				errmsg = e.getMessage();
			} finally {
				ManyClientcaseMap.put("ERRMSG", errmsg);
				if (testres) {
					ManyClientcaseMap.put("RESULT", "通过");
					ManyClientcaseMap.put("CLASS", "TABLEID passCase");
				} else {
					ManyClientcaseMap.put("RESULT", "不通过");
					ManyClientcaseMap.put("CLASS", "TABLEID failCase");
				}
				ManyClientcaseMap.put("ONCLICK", "window.open('capture/" + browser.logger.fileName + "')");
				SdkTools.saveCaseToReport(Framework.savereportFile, ManyClientcaseMap);
			}
			// 换端口启动
			Data.hybirdAppPort = String.valueOf(9222 + i);

		}

	}

	
	/**
	 * 3.多客户端运行 顺序启动多个客户端后，再次启动一个客户端进行完整流程操作
	 **/
	@Test
	public void ManyClient2() {

		for (int i = 1; i <= Data.clientNum; i++) {
			String CaseName = "多客户端第" + i + "个";
			browser = new OutpatientBrowser(CaseName);
			Data.hybirdAppPort = String.valueOf(9222 + i);
		}
		init("常规开立", true);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		Data.orderTemplateName = Data.orderTemplateName + System.currentTimeMillis();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_Pathology);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,
				new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_drug_pack)));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(patInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60),
				"his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
		browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");

	}
	
	/**
	 * 3.频繁打印 先通用签署并打印完成第一次打印，然后再次点击打印按钮，选择已打印文档进行多次打印
	 * @throws AWTException 
	 **/
	@Test
	public void FrequentPrint() throws IOException, AWTException {
		String filePath = "E:\\drug";
		init("频繁打印", true);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		browser.wnwd.WnPrinter(filePath+".pdf");
		browser.wnOutpatientWorkflow.wnwd.sleep(3000);
		for(int i=1;i<=Data.printTimes;i++) {
			browser.wnOutpatientWorkflow.repeatPrint();
			browser.wnwd.WnPrinter(filePath+i+".pdf");
			browser.wnOutpatientWorkflow.wnwd.sleep(3000);
		}
		
	}

	/***
	 * 4.在高压环境下，运行混合框架客户端查看是否正常使用
	 * 前置条件：通过工具模拟测试机cpu及内存被占用较高的情况下
	 **/
	@Test
	public void Hardwarepressure() {
		init("CASE-04: 高压环境下，执行一遍处置开立流程", true);
        Log.log("等待打开压力客户端");
        browser.wnwd.sleep(20000);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		Data.orderTemplateName = Data.orderTemplateName+System.currentTimeMillis();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_Pathology);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,new ArrayList<>(Arrays.asList(Data.test_prescribe_drug,Data.test_prescribe_drug_pack)));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.addTemplate(Data.orderTemplateName);
		browser.decouple.checkExecuteDiagnos(patInfo.get(2));
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(patInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
	}
}
