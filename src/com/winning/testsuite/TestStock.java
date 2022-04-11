package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestStock extends OutpatientTestBase {	
	public static ArrayList<String> patInfo=null;
	
	public TestStock() {
		super();
	}

	static {
		SdkTools.initReport("库存专项", "库存专项.html");
		Data.test_prescribe_drug = "利拉鲁肽注射液";
		Data.test_prescribe_herb = "益母草";
	}
	
	@Test
	public void case_01(){
		init("Case01 - 西药库存校验,库存充足时正常开立", true);
		browser.decouple.updateStock("门诊药房",Data.test_prescribe_drug,"10");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("门诊药房",Data.test_prescribe_drug,"10");
		
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null,Arrays.asList("9"));
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
				WebElement stockWarning = browser.wnwd.waitElementByXpath("库存提醒信息", "//div[@class='checkDescWrap stop']",Framework.defaultTimeoutMin);
				if (stockWarning != null && stockWarning.getText().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
				}else {
					browser.logger.assertFalse(true, "未找到库存信息提示");
				}
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含开立失败报错!");
			}
		}
		
	}
	
	@Test
	public void case_02(){
		init("Case02 - 西药库存校验,库存不足时无法开立", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("门诊药房",Data.test_prescribe_drug,"10");
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null,Arrays.asList("11"));
			browser.logger.assertFalse(true, "未抓取药品库存报错，失败!!");
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
				WebElement stockWarning = browser.wnwd.waitElementByXpath("库存提醒信息", "//div[@class='checkDescWrap stop']",Framework.defaultTimeoutMin);
				if (stockWarning != null && stockWarning.getText().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
				}else {
					browser.logger.assertFalse(true, "未找到库存信息提示");
				}
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含开立失败报错!");
			}
		}
	}
	
	@Test
	public void case_03(){
		init("Case03 - 西药库存校验,库存不足时无法开立，增加库存后可以开立", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("门诊药房",Data.test_prescribe_drug,"10");
		
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null,Arrays.asList("11"));
			browser.logger.assertFalse(true, "未抓取药品库存报错，失败!");
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
				WebElement stockWarning = browser.wnwd.waitElementByXpath("库存提醒信息", "//div[@class='checkDescWrap stop']",Framework.defaultTimeoutMin);
				if (stockWarning != null && stockWarning.getText().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
				}else {
					browser.logger.assertFalse(true, "未找到库存信息提示");
				}
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含开立失败报错!");
			}
		}
		WebElement outpatientDisposalFactory = browser.wnwd.waitElementByXpath("医嘱加工厂", WnOutpatientXpath.outpatientDisposalFactory, Framework.defaultTimeoutMin);
		if(outpatientDisposalFactory!=null) {
			browser.wnwd.waitElementByXpathAndClick("点击取消", WnOutpatientXpath.outpatientDisposalFactoryCancelButton, Framework.defaultTimeoutMin);
		}
		browser.decouple.updateStock("门诊药房",Data.test_prescribe_drug,"100");
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null,Arrays.asList("11"));
	}
	
	@Test
	public void case_04(){
		init("Case04 - 中草药库存校验,库存充足时正常开立", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"10");
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb,Arrays.asList("1000"),Arrays.asList("1"));
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
				if (e.toString() != null && e.toString().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
				}else {
					browser.logger.assertFalse(true, "未找到库存信息提示");
				}
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含开立失败报错!");
			}
		}
	}
	
	@Test
	public void case_05(){
		init("Case05 - 中草药库存校验,库存不足时无法开立", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"10");
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb,Arrays.asList("1000"),Arrays.asList("11"));
			browser.logger.assertFalse(true, "未抓取药品库存报错信息，失败!");
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
				if (e.toString() != null && e.toString().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
				}else {
					browser.logger.assertFalse(true, "未找到库存信息提示");
				}
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含开立失败报错!");
			}
		}
	}
	
	@Test
	public void case_06(){
		init("Case06 - 中草药库存校验,库存不足时无法开立，增加库存后可以开立", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"10");
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb,Arrays.asList("1000"),Arrays.asList("11"));
			browser.logger.assertFalse(true, "未抓取药品库存报错信息，  失败!");
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("库存不足")) {
					browser.logger.boxLog(0, "库存不足", "当前开立数量大于库存数量，无法开立");
			}else {
				browser.logger.assertFalse(true, "报错信息中没有包含库存不足报错!");
			}
		}
		WebElement outpatientDisposalFactory = browser.wnwd.waitElementByXpath("医嘱加工厂", WnOutpatientXpath.outpatientDisposalFactory, Framework.defaultTimeoutMin);
		if(outpatientDisposalFactory!=null) {
			browser.wnwd.waitElementByXpathAndClick("点击取消", WnOutpatientXpath.outpatientDisposalFactoryCancelButton, Framework.defaultTimeoutMin);
		}
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"100");
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb,Arrays.asList("1000"),Arrays.asList("11"));
	}
	
	/*
	 * 20210804新增场景（tfs115411）：已经正常签署的中草药医嘱，当中草药无库存时，撤销报错
	 */
	@Test
	public void case_07(){
		init("Case07 - 中草药库存校验,无库存时，撤销医嘱正常", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"10");
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb,Arrays.asList("1000"),Arrays.asList("10"));
		browser.wnOutpatientWorkflow.signOff(5000);
		browser.decouple.updateStock("草药房",Data.test_prescribe_herb,"0");
		browser.wnOutpatientWorkflow.deleteAllOrder();
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()!=0, "删除失败，  报错!");
	}
}