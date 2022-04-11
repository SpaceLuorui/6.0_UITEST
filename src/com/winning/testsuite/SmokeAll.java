package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SmokeAll extends OutpatientTestBase {

	public static String insuranceID = "57393491145648129";
	public static String soid = "256181";
	public static String herbOrgName = "草药房";
	public static String drugOrgName = "门诊药房";
	public static String herbCode = "98365";
	public static String drugCode = "98363";
	public static String examCode = "98078";
	public static String labCode = "98071";
	public static String treatCode = "98098";
	public static String pathologyCode = "98088";
	public static String bsSelf = "256162";
	public static ArrayList<String> patInfo=null;
	public static Boolean sucFlag = false;
	@org.junit.Rule
	public TestName name = new TestName();

	public SmokeAll() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("冒烟测试","smoke.html");
		Data.closeBrowser=false;
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
		init("CASE-01: 诊疗路径推荐开立", true);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.checkRecommendLab_pathWay();
		browser.wnOutpatientWorkflow.checkRecommendExam_pathWay();
		browser.wnOutpatientWorkflow.checkRecommendTreat_pathWay();
		browser.wnOutpatientWorkflow.quoteRecommendLab_pathWay();
		browser.wnOutpatientWorkflow.quoteRecommendExam_pathWay();
		browser.wnOutpatientWorkflow.quoteRecommendTreat_pathWay();
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}

	@Test
	public void test_002(){
		init("CASE-02: 卫宁知识体系推荐开立", false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem("卫宁医学知识体系");
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.editAllPhysicalSign();
		browser.wnOutpatientWorkflow.quoteRecommendLab_knowledge();
		browser.wnOutpatientWorkflow.quoteRecommendExam_knowledge();
		browser.wnOutpatientWorkflow.quoteRecommendDrug_knowledge();
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}

	@Test
	public void test_003(){
		init("CASE-03: 搜索开立处置/模板新增", false);
		Data.orderTemplateName = Data.orderTemplateName+System.currentTimeMillis();
		patInfo = browser.decouple.newEncounter();
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
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(patInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}

	@Test
	public void test_004(){
		init("CASE-04: 引用 处置模板", false);
		//重新叫号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0),encounterInfo.get(3));
		//引用模板
		WebElement template = browser.wnOutpatientWorkflow.searchTemplate(Data.orderTemplateName);
		browser.wnOutpatientWorkflow.logger.assertFalse(template==null, "添加医嘱模板失败!");
		browser.wnOutpatientWorkflow.quoteTemplate(template);
		browser.wnOutpatientWorkflow.signOff(0);
		//收费对比
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}


	@Test
	public void test_005(){
		init("CASE-05: 复诊患者引用历史处置", false);
		patInfo = browser.decouple.newEncounter(patInfo.get(2),Data.newEncounterSubjectCode);
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0),patInfo.get(3));
		List<String> diagnosisList = new ArrayList<>(Arrays.asList(Data.test_disease));
		List<String> disposalList = new ArrayList<>(Arrays.asList(Data.test_prescribe_drug,
				Data.test_prescribe_herb,
				Data.test_prescribe_treat,
				Data.test_prescribe_lab,
				Data.test_prescribe_exam,
				Data.test_prescribe_Pathology));
		browser.wnOutpatientWorkflow.quoteHistoryDiagnosisAndDisposal(diagnosisList,disposalList);
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(patInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}


	@Test
	public void test_006(){
		init("CASE-06: 开立/撤销/修改/删除 各类型医嘱  ,检查/修改/保存/签署/撤销病历", false);
		//挂号、叫号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//引用病历
		browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
		browser.wnOutpatientWorkflow.checkEmrParagraph("主诉");
		browser.wnOutpatientWorkflow.checkEmrParagraph("门诊诊断");
		browser.wnOutpatientWorkflow.checkEmrParagraph("处置治疗");
		//添加主诉、诊断
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.checkEmrFragment("主诉",Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.test_disease);
		//开立各类型医嘱
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_Pathology);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,new ArrayList<>(Arrays.asList(Data.test_prescribe_drug,Data.test_prescribe_drug_pack)));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		//签署后检查病历
		browser.wnOutpatientWorkflow.signOff(0);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.checkEmrFragment("处置治疗", Data.test_prescribe_Pathology);
		//修改病历
		browser.wnOutpatientWorkflow.updateEmrFragment("处置治疗", Data.test_prescribe_lab,"修改病历测试;");
		//保存病历
		browser.wnOutpatientWorkflow.saveEmr();
		//签署病历
		browser.wnOutpatientWorkflow.emrSignoff();
		//撤销病历
		browser.wnOutpatientWorkflow.emrRevoke();
		//撤销后再次修改
		browser.wnOutpatientWorkflow.updateEmrFragment("处置治疗", Data.test_prescribe_lab,"撤销后再次修改病历测试;");
		//保存病历
		browser.wnOutpatientWorkflow.saveEmr();
		//签署病历
		browser.wnOutpatientWorkflow.emrSignoff();
		//等待落库完成
		browser.decouple.waitSignOffSync(encounterInfo.get(0));
		//撤销医嘱
		browser.wnOutpatientWorkflow.revokeOrders();
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.waitEmrFragmentNotExist("处置治疗", Data.test_prescribe_Pathology);
		//修改医嘱
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_Pathology);
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_treat);
		//删除所有医嘱
		browser.wnOutpatientWorkflow.deleteOrders();
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getOrderNameList().size()!=0, "删除医嘱失败");
		sucFlag=true;
	}


	@Test
	public void test_007(){
		init("CASE-07: 麻醉药品开立", false);
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getDrugByPsychotropicsCode("152653");
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		Map<String, String> drug_mz = null;
		for (Map<String, String> drug : drugList_mz) {
			drug_mz = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_mz!=null) {
				break;
			}
		}
		browser.logger.log(1, "麻醉药品"+drug_mz);
		browser.logger.assertFalse(drug_mz==null, "没有获取到麻醉药品");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0),encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(drug_mz.get("CS_NO"),new ArrayList<String>(Arrays.asList(drug_mz.get("NAME"),drug_mz.get("PACK"))));
		browser.wnOutpatientWorkflow.signOff(0);
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}

	@Test
	public void test_008(){
		init("CASE-08: 需要医保审批的西药", false);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> drug = browser.decouple.db60.getInsurApprovalMedicine(drugOrgName, drugCode,insuranceID,soid);
		browser.logger.log(1,"需要审批的西药:"+drug);
		browser.logger.assertFalse(drug==null, "当前环境中未取到 需要审批的西药");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		//叫号
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立西药
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug.get("MEDICINE_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> drugInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "MEDICINE_ID", drug.get("MEDICINE_ID"));
		browser.logger.assertFalse(drugInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
		String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
		String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
		browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");
		sucFlag=true;
	}

	@Test
	public void test_009(){
		init("CASE-09: 添加/修改/删除 主诉(诊疗路径)", false);
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
	public void test_010(){
		init("CASE-10: 添加/修改/删除 诊断(诊疗路径)", false);
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
	public void test_011(){
		init("CASE-11: 诊间预约", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.addAppointment();
		sucFlag=true;
	}

	@Test
	public void test_012(){
		init("CASE-12: 申请转诊/接受转诊", false);
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
	public void test_013(){
		init("CASE-13: 取消就诊", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.encCancel();
		sucFlag=true;
	}

	@Test
	public void test_014(){
		init("CASE-14: 修改患者信息/过敏患者重新挂号", false);
		//获取数据
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//测试流程
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.updateBannerPersonInfo("13799999999","澳大利亚");
		browser.wnOutpatientWorkflow.updateBannerHealthSummry("185","70","环境", "空气粉尘","阳性" ,"2019-11-11");
		browser.wnOutpatientWorkflow.updateBannerEncounterInfo("老李","朋友关系","19999999999");
		ArrayList<String> encounterInfo1 = browser.decouple.newEncounter(encounterInfo.get(2),Data.newEncounterSubjectCode);
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo1.get(0), encounterInfo1.get(3));
		browser.logger.log("过敏患者再次挂号叫号成功!");
		sucFlag=true;
	}
}