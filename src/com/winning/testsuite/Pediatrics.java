package com.winning.testsuite;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Pediatrics extends OutpatientTestBase {

	public Pediatrics() {
		super();
	}

	static {
		Data.patientage = 5;
		SdkTools.initReport("儿科流程专项", "pediatric.html");
	}

	@Test
	public void case_01() throws InterruptedException {
		init("Case01 - 检查卫宁知识体系推荐诊断 【" + Data.pediatrics_symptom1 + " 推荐 " + Data.pediatrics_recommend_diagnose1 + "】 ",true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("卫宁医学知识体系");
		browser.wnOutpatientWorkflow.chiefComplaint_knowledge(Data.pediatrics_symptom1);
		browser.wnOutpatientWorkflow.checkDiagnose_knowledge(Data.pediatrics_recommend_diagnose1);
	}
	@Test
	public void case_02() throws InterruptedException {
		init("Case02 - 检查并书写 卫宁知识体系推荐查体 【" + Data.pediatrics_diagnose + " 推荐 " + Data.pediatrics_recommend_physicalSign1 + "/" + Data.pediatrics_recommend_physicalSign2 + "】", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("卫宁医学知识体系");
		browser.wnOutpatientWorkflow.diagnosis_knowledge(Data.pediatrics_diagnose);
		browser.wnOutpatientWorkflow.checkPhysicalSign(Data.pediatrics_recommend_physicalSign1);
		browser.wnOutpatientWorkflow.checkPhysicalSign(Data.pediatrics_recommend_physicalSign2);
		browser.wnOutpatientWorkflow.checkRecommendLab(Data.pediatrics_recommend_lab1);
		browser.wnOutpatientWorkflow.checkRecommendExam(Data.pediatrics_recommend_exam2);
		browser.wnOutpatientWorkflow.editAllPhysicalSign();
	}

	@Test
	public void case_03() throws InterruptedException {
		init("Case03 - 诊疗路径,书写各类型主诉 【单症状/体检异常】", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.chiefComplaint_pathWay(Data.pediatrics_symptom1);
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.pediatrics_symptom1);
		String res = browser.wnOutpatientWorkflow.chiefComplaint_pathWay_physicalExam("诊断", "test");
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉", res);
	}

	@Test
	public void case_04() throws InterruptedException {
		init("Case04 - 诊疗路径,添加诊断并检查病历同步", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.diagnosis_pathWay(Data.test_disease);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.test_disease);
		browser.wnOutpatientWorkflow.diagnosis_pathWay(Data.test_disease2);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.test_disease2);
	}

	@Test
	public void case_05() throws InterruptedException {
		Boolean headless_temp = Data.headless;
		Data.headless = false;
		try {
			init("Case05 - 诊疗路径,补充路径内容", true);
			ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//			browser.helper.changeKnowledgeSystem("诊疗路径简易版");
//			browser.helper.diagnosis_pathWay(Data.test_disease);
//			browser.helper.checkEmrFragment("门诊诊断", Data.test_disease);
//			browser.helper.deleteRecommendOrder_pathWay();
			browser.wnOutpatientWorkflow.addRecommendOrder_pathWay("lab", Data.test_prescribe_lab);
			browser.wnOutpatientWorkflow.addRecommendOrder_pathWay("exam", Data.test_prescribe_exam);
			browser.wnOutpatientWorkflow.addRecommendOrder_pathWay("treat", Data.test_prescribe_treat);
			browser.wnOutpatientWorkflow.quoteRecommendLab_pathWay();
			browser.wnOutpatientWorkflow.quoteRecommendExam_pathWay();
			browser.wnOutpatientWorkflow.quoteRecommendTreat_pathWay();
			browser.wnOutpatientWorkflow.signOff(0);
			String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
			String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
			browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
			browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
		} catch (Throwable e) {
			throw new Error(e.getMessage());
		} finally {
			Data.headless = headless_temp;
		}
	}

	@Test
	public void case_06() throws InterruptedException {
		init("Case06 - 急性上呼吸道感染流程 【症状:" + Data.pediatrics_jxshxdgr_symptom + "】【诊断:" + Data.pediatrics_jxshxdgr_diagnose+ "】【药品:" + Data.pediatrics_jxshxdgr_drug1 + "】【检验:" + Data.pediatrics_jxshxdgr_lab1 + "】", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.pediatrics_jxshxdgr_symptom);
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.pediatrics_jxshxdgr_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.pediatrics_jxshxdgr_diagnose);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.pediatrics_jxshxdgr_diagnose);
//		browser.helper.prescribeOrder(Data.pediatrics_jxshxdgr_drug1);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_jxshxdgr_drug2);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_jxshxdgr_drug3);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_jxshxdgr_lab1);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_jxshxdgr_lab2);
		browser.wnOutpatientWorkflow.signOff(0);
//		browser.helper.checkEmrFragment("处置治疗", Data.pediatrics_jxshxdgr_drug1);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_jxshxdgr_drug2);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_jxshxdgr_drug3);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_jxshxdgr_lab1);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_jxshxdgr_lab2);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
		browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
	}

	@Test
	public void case_07() throws InterruptedException {
		init("Case07 - 急性扁桃体炎流程 【症状:" + Data.pediatrics_jxbtty_symptom + "】【诊断:" + Data.pediatrics_jxbtty_diagnose + "】", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.pediatrics_jxbtty_symptom);
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.pediatrics_jxbtty_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.pediatrics_jxbtty_diagnose);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.pediatrics_jxbtty_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_jxbtty_drug1);
		browser.wnOutpatientWorkflow.signOff(0);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_jxbtty_drug1);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
		browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
	}

	@Test
	public void case_08() throws InterruptedException {
		init("Case08 - 黄疸流程 【症状:" + Data.pediatrics_hd_symptom + "】【诊断:" + Data.pediatrics_hd_diagnose + "】", true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.pediatrics_subjCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.pediatrics_subjName);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.pediatrics_hd_symptom);
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.pediatrics_hd_symptom);
		browser.wnOutpatientWorkflow.diagnosis(Data.pediatrics_hd_diagnose);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.pediatrics_hd_diagnose);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.pediatrics_hd_treat1);
		browser.wnOutpatientWorkflow.signOff(0);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.pediatrics_hd_treat1);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
		browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
	}
	
	@Before
    public void setUp() throws Exception {
		if(Data.useHybirdApp) {
			SdkSystemProcess.stopExeApp("chrome.exe");
	        SdkSystemProcess.stopExeApp("Win60.exe");
	        SdkSystemProcess.stopExeApp("Win6.0.exe");
		}
	}

}