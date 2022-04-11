package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Smoke extends OutpatientTestBase {

	public static ArrayList<String> patInfo=null;
	public static Boolean sucFlag = false;

	public Smoke() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("冒烟测试","UI流程测试报告.html");
		Data.closeBrowser=false;
		Data.knowledge_system = "诊疗路径简易版";
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
		init("CASE-01: 开立处置/模板新增", true);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		Data.orderTemplateName = Data.orderTemplateName+System.currentTimeMillis();
		patInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);

		if(Data.testLabFlag) {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		}

		if(Data.testExamItemFlag) {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		}

		if(Data.testPathologyFlag) {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_Pathology);
		}

		if(Data.testMedicineFlag) {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,new ArrayList<>(Arrays.asList(Data.test_prescribe_drug,Data.test_prescribe_drug_pack)));
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		}
		
		if(Data.testTreatFlag) {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		}

		browser.wnOutpatientWorkflow.addTemplate(Data.orderTemplateName);
		browser.decouple.checkExecuteDiagnos(patInfo.get(2));
		browser.wnOutpatientWorkflow.signOff(0);
		if(!Data.hisType.equals("WINEX")) {
			String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
			String cost_his = browser.decouple.win60MedicineSF(patInfo.get(0)).get(0);
			browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
			browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		}
		sucFlag=true;
	}
	@Test
	public void test_002(){
		init("CASE-02: 引用 处置模板", false);
		//重新叫号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0),encounterInfo.get(3));
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		//引用模板
		WebElement template = browser.wnOutpatientWorkflow.searchTemplate(Data.orderTemplateName);
		browser.wnOutpatientWorkflow.logger.assertFalse(template==null, "添加医嘱模板失败!");
		browser.wnOutpatientWorkflow.quoteTemplate(template);
		browser.wnOutpatientWorkflow.signOff(0);
		//收费对比
		if(!Data.hisType.equals("WINEX")) {
			String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
			String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
			browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
			browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		}
		sucFlag=true;
	}

	@Test
	public void test_003(){
		init("CASE-03: 添加/修改/删除 主诉(诊疗路径)", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.updateChiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.deleteChiefComplaint_pathWay(Data.test_chief_complain);
		sucFlag=true;
	}

	@Test
	public void test_004(){
		init("CASE-04: 添加/修改/删除 诊断(诊疗路径)", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.diagnosis_pathWay(Data.test_disease);
		browser.wnOutpatientWorkflow.updateDiagnosis_pathWay(Data.test_disease);
		browser.wnOutpatientWorkflow.deleteDiagnosis_pathWay(Data.test_disease);
		browser.wnOutpatientWorkflow.diagnosis_pathWay(Data.test_disease2);
		sucFlag=true;
	}
	@Test
	public void test_005(){
		init("CASE-05: 诊间预约", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.addAppointment();
		sucFlag=true;
	}

	@Test
	public void test_006(){
		init("CASE-06: 申请转诊/接受转诊", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		//切换科室
		browser.wnOutpatientWorkflow.changeSubject(new ArrayList<String>(Arrays.asList(Data.test_select_subject,Data.test_select_subject2)));
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.addReferral(Data.test_select_subject2,encounterInfo.get(0));
		browser.wnOutpatientWorkflow.acceptReferral(Data.test_select_subject2,encounterInfo.get(0));
		sucFlag=true;
	}

	@Test
	public void test_007(){
		init("CASE-07: 取消就诊", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.encCancel();
		sucFlag=true;
	}

}