package com.winning.testsuite;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DisabledOrderTest extends OutpatientTestBase {

	public DisabledOrderTest() {
		super();
		System.out.println("DisableOrderTest");
	}

	static {
		Data.test_prescribe_drug="多酶片";
		Data.test_prescribe_exam ="冠脉介入手术";
		Data.test_prescribe_lab="尿三杯";
		Data.test_prescribe_treat="特大换药";

		SdkTools.initReport("临床服务停用专项", "disabledOrder.html");
	}



	@Test//选择一个西药，启用状态下可以开立，停用状态下无法开立
	public void disabledOrderTest_001() throws InterruptedException, IOException {
		init("Case01 - 药品停用测试", true);
		String drugName = "利拉鲁肽注射液";
		String medicineId="57396549162362907";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.updateMedicineCsStatusByMedicineId(medicineId, "98360");
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(drugName);
		browser.wnOutpatientWorkflow.delete(drugName);
		browser.wnOutpatientWorkflow.updateMedicineCsStatusByMedicineId(medicineId, "98361");
		try {
			browser.wnOutpatientWorkflow.searchOrder(drugName);
			browser.logger.assertFalse(true, "搜索到了"+drugName+"西药  报错!");
		} catch (Throwable e) {
			browser.logger.assertFalse(!e.getMessage().contains("60无搜索结果"), "报错信息中没有包含  60无搜索结果   报错!");
		}
		browser.decouple.db60.updateClinicalServiceByCsName("98360", drugName);
	}


	@Test
	public void disabledOrderTest_002() throws InterruptedException, IOException {
		init("Case02 - 检查停用测试", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginMDMFromPortal();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_exam,"98360");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.delete(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.loginMDMFromOutpatient();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_exam,"98361");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			browser.wnOutpatientWorkflow.searchOrder(Data.test_prescribe_exam);
			browser.logger.assertFalse(true, "搜索到了"+Data.test_prescribe_exam+"  报错!");
		} catch (Throwable e) {
			browser.logger.assertFalse(!e.getMessage().contains("60无搜索结果"), "报错信息中没有包含  60无搜索结果   报错!");
		}
		browser.decouple.db60.updateClinicalServiceByCsName("98360", Data.test_prescribe_exam);
	}

	@Test
	public void disabledOrderTest_003() throws InterruptedException, IOException {
		init("Case03 - 检验停用测试", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginMDMFromPortal();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_lab,"98360");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.delete(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.loginMDMFromOutpatient();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_lab,"98361");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			browser.wnOutpatientWorkflow.searchOrder(Data.test_prescribe_lab);
			browser.logger.assertFalse(true, "搜索到了"+Data.test_prescribe_lab+"  报错!");
		} catch (Throwable e) {
			browser.logger.assertFalse(!e.getMessage().contains("60无搜索结果"), "报错信息中没有包含  60无搜索结果   报错!");
		}
		browser.decouple.db60.updateClinicalServiceByCsName("98360", Data.test_prescribe_lab);
	}

	@Test//选择一个治疗医嘱，启用状态下可以开立，停用状态下无法开立
	public void disabledOrderTest_004() throws InterruptedException, IOException {
		init("Case04 - 治疗停用测试", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginMDMFromPortal();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_treat,"98360");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.delete(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.loginMDMFromOutpatient();
		browser.wnOutpatientWorkflow.updateClinicalServiceCsStatus(Data.test_prescribe_treat,"98361");
		browser.wnOutpatientWorkflow.loginPortalFromMDM();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			browser.wnOutpatientWorkflow.searchOrder(Data.test_prescribe_treat);
			browser.logger.assertFalse(true, "搜索到了"+Data.test_prescribe_treat+"治疗  报错!");
		} catch (Throwable e) {
			browser.logger.assertFalse(!e.getMessage().contains("60无搜索结果"), "报错信息中没有包含  60无搜索结果   报错!");
		}
		browser.decouple.db60.updateClinicalServiceByCsName("98360", Data.test_prescribe_treat);
	}
}