package com.winning.testsuite;

import java.util.ArrayList;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FuChanKe extends OutpatientTestBase {	
	public static ArrayList<String> patInfo=null;
	public static Boolean sucFlag = true;
	
	public FuChanKe() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("妇产科流程专项", "fuChanKe.html");
		Data.closeBrowser=false;
	}
	
	@Override
	public void init(String description, Boolean openBrowser){
		super.init(description,openBrowser);
		SdkTools.logger.assertFalse(browser==null, "启动浏览器没有成功,后续用例不执行");
		browser.logger.assertFalse(!sucFlag, "前置用例失败,后续用例不执行");
		sucFlag=false;
	}
	
	@Test
	public void case_01(){
		init("Case01 - 产科知识体系 检查推荐主诉、诊断【主诉:"+Data.chanKe_symptom+" 诊断:"+Data.chanKe_recommend_diagnose1+"/"+Data.chanKe_recommend_diagnose2+"】", true);
		Data.patientage = 20;
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.chanKe_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("卫宁医学知识体系");
		browser.wnOutpatientWorkflow.chiefComplaint_knowledge(Data.chanKe_symptom);
		browser.wnOutpatientWorkflow.checkDiagnose_knowledge(Data.chanKe_recommend_diagnose1);
		browser.wnOutpatientWorkflow.checkDiagnose_knowledge(Data.chanKe_recommend_diagnose2);
		sucFlag = true;
	}
	
	@Test
	public void case_02(){
		init("Case02 - 产科知识体系 检查诊断推荐查体"+"【诊断:"+Data.chanKe_diagnose+" 推荐查体:"+Data.chanKe_recommend_physicalSign1+"/"+Data.chanKe_recommend_physicalSign2+"】", false);
		browser.wnOutpatientWorkflow.diagnosis_knowledge(Data.chanKe_diagnose);
		browser.wnOutpatientWorkflow.checkPhysicalSign(Data.chanKe_recommend_physicalSign1);
		browser.wnOutpatientWorkflow.checkPhysicalSign(Data.chanKe_recommend_physicalSign2);
		browser.wnOutpatientWorkflow.editAllPhysicalSign();
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.signOff(0);
		sucFlag = true;
	}
	
	@Test
	public void case_03(){
		init("Case03 - 产科诊疗路径 开立推荐医嘱", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.chiefComplaint_pathWay(Data.chanKe_symptom2);
		browser.wnOutpatientWorkflow.diagnosis_pathWay(Data.chanKe_diagnose2);
		browser.wnOutpatientWorkflow.quoteRecommendLab_pathWay();
		browser.wnOutpatientWorkflow.quoteRecommendExam_pathWay();
		browser.wnOutpatientWorkflow.quoteRecommendTreat_pathWay();
		sucFlag = true;
	}
	
	@Test
	public void case_04(){
		init("Case04 - 产科常规流程", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.writeSymptom(Data.chanKe_fkcglc_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.chanKe_fkcglc_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fkcglc_drug);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fkcglc_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fkcglc_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fkcglc_pathology);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag = true;
	}
	
	@Test
	public void case_05(){
		init("Case05 - 早孕检查", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.writeSymptom(Data.chanKe_zyjc_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.chanKe_zyjc_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_zyjc_drug);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_zyjc_drug2);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_zyjc_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_zyjc_lab2);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag = true;
	}
	
	@Test
	public void case_06(){
		init("Case06 - 孕前检查", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.writeSymptom(Data.chanKe_yqjc_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.chanKe_yqjc_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_yqjc_exam);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag = true;
	}
	
	@Test
	public void case_07(){
		init("Case07 - 辅助生育流程", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null,Data.chanKe_subjCode,"女");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.writeSymptom(Data.chanKe_fzsy_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.chanKe_fzsy_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fzsy_drug);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.chanKe_fzsy_exam);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag = true;
	}
}