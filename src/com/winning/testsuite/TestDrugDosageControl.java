package com.winning.testsuite;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDrugDosageControl extends OutpatientTestBase {

	public TestDrugDosageControl() {
		super();
	}

	static {
		Data.getScreenShot = true;
		SdkTools.initReport("药品用量控制专项", "药品用量控制专项.html");
	}
/*
 * 前置条件： 1、进入主数据系统-知识域-知识域配置-药品知识库配置
 *           2、配置(甲)氯雷他定片（开瑞坦）的药品累计用量
 *           3、设置（提醒10盒，控制12盒）
 * 
 */
	
	@Test
	public void case_01() throws InterruptedException {
		init("Case01 -不启用，不干预",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,0);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,0);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 11;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.signOffFro(0);
		browser.wnOutpatientWorkflow.checkOrderStatus(true);

	}
	
	@Test
	public void case_02() throws InterruptedException {
		init("Case02 -不启用，干预",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,0);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,1);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 11;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.signOffFro(0);
		browser.wnOutpatientWorkflow.checkOrderStatus(true);
	}
	@Test
	public void case_03() throws InterruptedException {
		init("Case03 -启用，不干预，提醒",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,1);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,0);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 11;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
				Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("提醒");
		browser.wnOutpatientWorkflow.checkOrderStatus(true);
	}
	
	@Test
	public void case_04() throws InterruptedException {
		init("Case04 -启用，不干预，控制",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,1);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,0);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 13;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
				Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("提醒");
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("控制");
		browser.wnOutpatientWorkflow.checkOrderStatus(true);
	}
	
	@Test
	public void case_05() throws InterruptedException {
		init("Case05 -启用，干预，提醒",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,1);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,1);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 11;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
				Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("提醒");
		browser.wnOutpatientWorkflow.executeDrugDosageControlSingoff(false);
		browser.wnOutpatientWorkflow.checkOrderStatus(false);
		browser.wnOutpatientWorkflow.wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
				Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.executeDrugDosageControlSingoff(true);
		browser.wnOutpatientWorkflow.checkOrderStatus(true);
		
	}
	
	@Test
	public void case_06() throws InterruptedException {
		init("Case06 -启用，干预，控制",true);
		String medicineId ="57396033766287361";//(甲)氯雷他定片（开瑞坦）
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetKNW006(Data.host,1);
		browser.wnOutpatientWorkflow.SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(Data.host,1);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
		prescribeDetail.num = 13;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicineId, prescribeDetail);
		browser.wnOutpatientWorkflow.wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
				Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("提醒");
		browser.wnOutpatientWorkflow.checkDrugDosageControlMsg("控制");
		browser.wnOutpatientWorkflow.checkOrderStatus(false);

	}
}