package com.winning.testsuite.Inpatient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.InpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InpatientSmoke extends InpatientTestBase{

	public static ArrayList<String> patInfo=null;
	public static Boolean sucFlag = true;

	public InpatientSmoke() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("大临床冒烟测试","大临床UI冒烟测试报告.html");
		Data.closeBrowser=false;
		try{
			Config.loadOnlineDefaultConfig("DEV");
			Config.loadOnlineExtraConfig("DEV","Inpatient-25");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void init(String description, Boolean openBrowser){
		super.init(description,openBrowser);
		SdkTools.logger.assertFalse(browser==null, "启动浏览器没有成功,后续用例不执行");
		if (!currentCase.get("CASE_NAME").startsWith("CASE-01:")) {
			browser.logger.assertFalse(!sucFlag, "前置用例失败,后续用例不执行");
		}
		sucFlag=false;
	}

	@Test
	public void test_001(){
		init("CASE-01: 大临床冒烟基础流程", true);
		String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnInpatientWorkflow.setParamsForTestAllService();
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
		browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
		browser.wnInpatientWorkflow.callNumberByName(patientName);
		browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);   //开立西药
		browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);   //开立中药
		if(Data.testExamItemFlag) {
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);   //开立检查
		}
		if(Data.testLabFlag) {
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);    //开立检验
		}
		if(Data.testTreatFlag) {
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);  //开立治疗
		}
		browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing);  //开立护理
		browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_operation);  //开立手术
		browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_diet);  //开立膳食
		browser.wnInpatientWorkflow.signOff(0);
		browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		browser.wnInpatientWorkflow.callNumberByName(patientName);
		browser.wnInpatientWorkflow.signFor();
		browser.wnInpatientWorkflow.orderApply();
		browser.wnInpatientWorkflow.orderApplication();
		browser.wnInpatientWorkflow.outHospital();
		sucFlag=true;

	}

    @Test
	public void test_002(){
		init("CASE-02: 新增住院病历、提交并审核", false);
		String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnInpatientWorkflow.wnlogin(Data.inpatient_submitEMR_login_account, Data.inpatient_submitEMR_login_pwd);
		List<String> emrList =new ArrayList<>(Arrays.asList(Data.inpatient_EMR1,Data.inpatient_EMR2,Data.inpatient_EMR3));
		Boolean auditFlag = browser.wnInpatientWorkflow.setParamsForEmrAudit(emrList);
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.inpatient_submitEMR_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
		//住院医生登录
		browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
		browser.wnInpatientWorkflow.callNumberByName(patientName);
		//创建病历
		browser.wnInpatientWorkflow.inpatientsEmr(Data.inpatient_EMR1);
		browser.wnInpatientWorkflow.inpatientsEmr(Data.inpatient_EMR2);
		browser.wnInpatientWorkflow.inpatientsEmr(Data.inpatient_EMR3);
		if(auditFlag){
			browser.wnInpatientWorkflow.WnlogOut();
			//住院病历二级审核医生登录
			browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			//审签通过
			browser.wnInpatientWorkflow.emrAuditPass(Data.inpatient_EMR1,patientName);
			//审签拒绝
			browser.wnInpatientWorkflow.emrAuditReject(Data.inpatient_EMR2,patientName);
			//审签通过后撤销审签
			browser.wnInpatientWorkflow.emrAuditPass(Data.inpatient_EMR3,patientName);
			browser.wnInpatientWorkflow.emrCancelAudit(Data.inpatient_EMR3,patientName);
		}
		sucFlag=true;
	}

	@Test
	public void test_003(){
		init("CASE-03: 新增护理文书", false);
		String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		browser.wnInpatientWorkflow.callNumberByName(patientName);
		List<String> chartList =new ArrayList<>(Arrays.asList(Data.chart_Record,Data.chart_InpatientAssess,Data.chart_assess1,Data.chart_assess2));
        browser.wnInpatientWorkflow.newChart(chartList);
		browser.wnInpatientWorkflow.editChart(Data.chart_Record,patientName);
		browser.wnInpatientWorkflow.editChart(Data.chart_InpatientAssess,patientName);
		browser.wnInpatientWorkflow.editChart(Data.chart_assess1,patientName);
		browser.wnInpatientWorkflow.editChart(Data.chart_assess2,patientName);
		sucFlag=true;
	}

}