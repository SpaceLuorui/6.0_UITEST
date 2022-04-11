package com.winning.testsuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrintingTest extends OutpatientTestBase {

	public PrintingTest() {
		super();
	}

	static {
		Data.useHybirdApp = true;
		SdkTools.initReport("打印流程专项", "打印测试报告.html");
	}
	
	
	
	@Test
	public void printTest_001() throws InterruptedException, IOException {
		init("Case01 - 单西药导诊单、处方单打印测试", true);
		String drugRecipeReport = SdkTools.getIsOpenReportFromPrintConfig("DrugRecipeReport").toLowerCase();
		String guideSheetReport = SdkTools.getIsOpenReportFromPrintConfig("GuideSheetReport").toLowerCase();
		if(drugRecipeReport.equals("false")&&guideSheetReport.equals("false")) {
			browser.logger.assertFalse(true, "当前导诊单及西药处方单都是不打印状态");
		}
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "drugGuide.pdf";
		String drugFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "drug.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		ArrayList<String> drugInfo = new ArrayList<String>();
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getDrugInfo(Data.test_prescribe_drug);
		drugInfo.addAll(info.get("drugInfo"));
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
        if(SdkTools.analyticConfiguration(Data.drugRecipeReportConfig,"检查患者姓名")) {
            drugInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		if(guideSheetReport.equals("true")&&Data.drugGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "西药导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        ArrayList<String> content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
	        browser.logger.assertFalse(content.size()>0, "西药导诊单打印信息不完全一致",content.toString());
		}
		if(drugRecipeReport.equals("true")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(drugFilePath), "西药处方单保存文件失败");
			String drugResponseMsg = browser.wnwd.getPdfContentByOcr(drugFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + drugInfo);
			browser.logger.boxLog(1, "处方单打印文件转化结果为", "" + drugResponseMsg);
	        ArrayList<String> content = SdkTools.checkMsg(drugInfo, drugResponseMsg);
	        browser.logger.assertFalse(content.size()>0, "西药处方单打印信息不完全一致",content.toString());
		}
	}
	
	@Test
	public void printTest_002() throws InterruptedException, IOException {
		init("Case02 - 单中草药导诊单、处方单打印测试", true);
		String herbRecipeReport = SdkTools.getIsOpenReportFromPrintConfig("HerbRecipeReport").toLowerCase();
		String guideSheetReport = SdkTools.getIsOpenReportFromPrintConfig("GuideSheetReport").toLowerCase();
		if(herbRecipeReport.equals("false")&&guideSheetReport.equals("false")) {
			browser.logger.assertFalse(true, "当前导诊单及中草药处方单都是不打印状态");
		}
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "herbGuide.pdf";
		String herbFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "herb.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		ArrayList<String> herbInfo = new ArrayList<String>();
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getHerbInfo(Data.test_prescribe_herb);
		herbInfo.addAll(info.get("herbInfo"));
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
        if(SdkTools.analyticConfiguration(Data.herbRecipeReportConfig,"检查患者姓名")) {
            herbInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		if(guideSheetReport.equals("true")&&Data.herbGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "中草药导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        ArrayList<String> content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
	        browser.logger.assertFalse(content.size()>0, "中草药导诊单打印信息不完全一致",content.toString());
		}
		if(herbRecipeReport.equals("true")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(herbFilePath), "中草药处方单保存文件失败");
			String herbResponseMsg = browser.wnwd.getPdfContentByOcr(herbFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + herbInfo);
			browser.logger.boxLog(1, "处方单打印文件转化结果为", "" + herbResponseMsg);
	        ArrayList<String> content = SdkTools.checkMsg(herbInfo, herbResponseMsg);
	        browser.logger.assertFalse(content.size()>0, "中草药处方单打印信息不完全一致",content.toString());
		}
		
	}
	
	@Test
	public void printTest_003() throws InterruptedException, IOException {
		init("Case03 - 单检查申请单打印测试", true);
		String examOrderReport = SdkTools.getIsOpenReportFromPrintConfig("ExamOrderReport").toLowerCase();
		String guideSheetReport = SdkTools.getIsOpenReportFromPrintConfig("GuideSheetReport").toLowerCase();
		if (examOrderReport.equals("false") && guideSheetReport.equals("false")) {
			browser.logger.assertFalse(true, "当前导诊单及检查申请单都是不打印状态");
		}
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "examGuide.pdf";
		String examFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "exam.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		ArrayList<String> examInfo = new ArrayList<String>();
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getExamInfo(Data.test_prescribe_exam);
		examInfo.addAll(info.get("examInfo"));
		guideInfo.addAll(info.get("guideInfo"));
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查患者姓名")) {
			guideInfo.add(encounterInfo.get(0));
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查患者姓名")) {
			examInfo.add(encounterInfo.get(0));
		}
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		if (guideSheetReport.equals("true")&&Data.examGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "检查导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "检查导诊单打印信息不完全一致", content.toString());
		}
		if (examOrderReport.equals("true")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(examFilePath), "检查申请单保存文件失败");
			String examResponseMsg = browser.wnwd.getPdfContentByOcr(examFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + examInfo);
			browser.logger.boxLog(1, "申请单打印文件转化结果为", "" + examResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(examInfo, examResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "检查申请单打印信息不完全一致", content.toString());
		}
	}
	
	@Test
	public void printTest_004() throws InterruptedException, IOException {
		init("Case04 - 单检验申请单打印测试", true);
		String labTestOrderReport = SdkTools.getIsOpenReportFromPrintConfig("LabTestOrderReport").toLowerCase();
		String guideSheetReport = SdkTools.getIsOpenReportFromPrintConfig("GuideSheetReport").toLowerCase();
		if (labTestOrderReport.equals("false") && guideSheetReport.equals("false")) {
			browser.logger.assertFalse(true, "当前导诊单及检验申请单都是不打印状态");
		}
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "labGuide.pdf";
		String labTestFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "labTest.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		ArrayList<String> labTestInfo = new ArrayList<String>();
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getLabtestInfo(Data.test_prescribe_lab);
		labTestInfo.addAll(info.get("labTestInfo"));
		guideInfo.addAll(info.get("guideInfo"));
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查患者姓名")) {
			guideInfo.add(encounterInfo.get(0));
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查患者姓名")) {
			labTestInfo.add(encounterInfo.get(0));
		}
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		if (guideSheetReport.equals("true")&&Data.labGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "检验导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "检验导诊单打印信息不完全一致", content.toString());
		}
		if (labTestOrderReport.equals("true")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(labTestFilePath), "检验申请单保存文件失败");
			String labTestResponseMsg = browser.wnwd.getPdfContentByOcr(labTestFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + labTestInfo);
			browser.logger.boxLog(1, "申请单打印文件转化结果为", "" + labTestResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(labTestInfo, labTestResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "检验申请单打印信息不完全一致", content.toString());
		}
	}
	
	@Test
	public void printTest_005() throws InterruptedException, IOException {
		init("Case05 - 单治疗申请单打印测试", true);
		String treatmentOrderReport = SdkTools.getIsOpenReportFromPrintConfig("TreatmentOrderReport").toLowerCase();
		String guideSheetReport = SdkTools.getIsOpenReportFromPrintConfig("GuideSheetReport").toLowerCase();
		if (treatmentOrderReport.equals("false") && guideSheetReport.equals("false")) {
			browser.logger.assertFalse(true, "当前导诊单及治疗申请单都是不打印状态");
		}
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "treatGuide.pdf";
		String treatFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "treat.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		ArrayList<String> treatInfo = new ArrayList<String>();
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getTreatInfo(Data.test_prescribe_treat);
		treatInfo.addAll(info.get("treatInfo"));
		guideInfo.addAll(info.get("guideInfo"));
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查患者姓名")) {
			guideInfo.add(encounterInfo.get(0));
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查患者姓名")) {
			treatInfo.add(encounterInfo.get(0));
		}
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		if (guideSheetReport.equals("true")&&Data.treatGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "治疗导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "治疗导诊单打印信息不完全一致", content.toString());
		}
		if (treatmentOrderReport.equals("true")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(treatFilePath), "治疗申请单保存文件失败");
			String treatResponseMsg = browser.wnwd.getPdfContentByOcr(treatFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + treatInfo);
			browser.logger.boxLog(1, "申请单打印文件转化结果为", "" + treatResponseMsg);
			ArrayList<String> content = SdkTools.checkMsg(treatInfo, treatResponseMsg);
			browser.logger.assertFalse(content.size() > 0, "治疗申请单打印信息不完全一致", content.toString());
		}
	}
	
	@Test
	public void printTest_006() throws InterruptedException, IOException {
		init("Case06 - 请假单打印测试", true);
		String sickLeaveReport = SdkTools.getIsOpenReportFromPrintConfig("SickLeaveReport").toLowerCase();
		if (sickLeaveReport.equals("false")) {
			browser.logger.assertFalse(true, "当前请假单是不打印状态");
		}
		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "sickLeave.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		ArrayList<String> sickLeaveInfo = browser.wnOutpatientWorkflow.writeSickLeaveForm();
		
		ArrayList<String> info = new ArrayList<String>();
		info.addAll(sickLeaveInfo);
		browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
		browser.logger.assertTrue(browser.wnwd.WnPrinter(filePath), "保存文件失败");
		String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
		browser.logger.boxLog(1, "请假单打印文件转化结果为", "" + responseMsg);
        ArrayList<String> content = SdkTools.checkMsg(info, responseMsg);
        browser.logger.assertFalse(content.size()>0, "请假单打印信息不完全一致",content.toString());
	}
	
	@Test
	public void printTest_007() throws InterruptedException, IOException {
		init("Case07 - 疾病证明单打印测试", true);
		String diseaseCertificateReport = SdkTools.getIsOpenReportFromPrintConfig("DiseaseCertificateReport").toLowerCase();
		if (diseaseCertificateReport.equals("false")) {
			browser.logger.assertFalse(true, "当前疾病证明单是不打印状态，或者暂未找到疾病证明单状态");
		}
		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "diseaseCertificate.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		ArrayList<String> diseaseCertificateInfo = browser.wnOutpatientWorkflow.writeDiseaseCertificate();
		ArrayList<String> info = new ArrayList<String>();
		info.addAll(diseaseCertificateInfo);
		browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
		Boolean b = browser.wnwd.WnPrinter(filePath);
		browser.logger.assertTrue(b, "保存文件失败");
		String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
		browser.logger.boxLog(1, "疾病证明单打印文件转化结果为", "" +responseMsg);

        ArrayList<String> content = SdkTools.checkMsg(info, responseMsg);
        browser.logger.assertFalse(content.size()>0, "疾病证明单打印信息不完全一致",content.toString());
	}
	
	
	@Test
	public void printTest_008() throws InterruptedException, IOException {
		init("Case08 - 病历测试", true);
		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "emr.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.signOff(10000);
		browser.wnOutpatientWorkflow.emrSignoff();
		
		ArrayList<String> info = new ArrayList<String>();
		ArrayList<String> emrInfo = browser.wnOutpatientWorkflow.getEmrInfo();
		info.addAll(emrInfo);
		if(SdkTools.analyticConfiguration(Data.emrTemplatePrintConfig,"检查临床诊断")) {
			info.add(Data.test_disease);
		}
		if(SdkTools.analyticConfiguration(Data.emrTemplatePrintConfig,"检查科室")) {
			info.add(Data.test_select_subject);
		}
		browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
		browser.wnOutpatientWorkflow.emrPrint();
		browser.logger.assertTrue(browser.wnwd.WnPrinter(filePath), "保存文件失败");
		String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
		browser.logger.boxLog(1, "病历打印文件转化结果为", "" + responseMsg);
		ArrayList<String> content = SdkTools.checkMsg(info, responseMsg);
        browser.logger.assertFalse(content.size()>0, "病历打印信息不完全一致",content.toString());
	}
	
//	@Test
//	public void printTest_009() throws InterruptedException, IOException {
//		init("Case09 - 多西药处方单打印测试", true);
//		String filePath = "F:\\drugs.pdf";
//		String drugName2="多酶片";
//		String drugName3="复方紫草油";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
//		browser.wnOutpatientWorkflow.prescribeOrder(drugName2);
//		browser.wnOutpatientWorkflow.prescribeOrder(drugName3);
//		ArrayList<String> info = new ArrayList<String>();
//		info.addAll(browser.wnOutpatientWorkflow.getDrugInfo(Data.test_prescribe_drug));
//		info.addAll(browser.wnOutpatientWorkflow.getDrugInfo(drugName2));
//		info.addAll(browser.wnOutpatientWorkflow.getDrugInfo(drugName3));
//		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
//		WindowsUI.savePrintFile(filePath);
////      File file=new File(filePath);  
//		String responseMsg = WinningOcr.getWSOutput(filePath);
////      String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(info, responseMsg);
//        browser.logger.assertFalse(content.size()>0, "打印信息不完全一致",content.toString());
//	}
//	
//	@Test
//	public void printTest_010() throws InterruptedException, IOException {
//		init("Case10 - 多中草药处方单打印测试", true);
//		String filePath = "F:\\herbs.pdf";
//		String herbName2="陈皮";
//		String herbName3="天葵";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
//		browser.wnOutpatientWorkflow.prescribeOrder(herbName2);
//		browser.wnOutpatientWorkflow.prescribeOrder(herbName3);
//		ArrayList<String> herbInfo = new ArrayList<String>();
//		herbInfo.addAll(browser.wnOutpatientWorkflow.getHerbInfo(Data.test_prescribe_herb));
//		herbInfo.addAll(browser.wnOutpatientWorkflow.getHerbInfo(herbName2));
//		herbInfo.addAll(browser.wnOutpatientWorkflow.getHerbInfo(herbName3));
//		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
//		WindowsUI.savePrintFile(filePath);
////      File file=new File(filePath);  
//		String responseMsg = WinningOcr.getWSOutput(filePath);
////      String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(herbInfo, responseMsg);
//        browser.logger.assertFalse(content.size()>0, "打印信息不完全一致",content.toString());
//	}
//	
//	@Test
//	public void printTest_011() throws InterruptedException, IOException {
//		init("Case11 - 多检查申请单打印测试", true);
//		String filePath = "F:\\exam.pdf";
//		String examName2 = "起搏器介入手术";
//		String examName3 = "计划生育";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
//		browser.wnOutpatientWorkflow.prescribeOrder(examName2);
//		browser.wnOutpatientWorkflow.prescribeOrder(examName3);
//		ArrayList<String> examInfo = new ArrayList<String>();
//		examInfo.addAll(browser.wnOutpatientWorkflow.getExamInfo(Data.test_prescribe_exam));
//		examInfo.addAll(browser.wnOutpatientWorkflow.getExamInfo(examName2));
//		examInfo.addAll(browser.wnOutpatientWorkflow.getExamInfo(examName3));
//				
//		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
//		WindowsUI.savePrintFile(filePath);
////      File file=new File(filePath);  
//		String responseMsg = WinningOcr.getWSOutput(filePath);
////      String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(examInfo, responseMsg);
//        browser.logger.assertFalse(content.size()>0, "打印信息不完全一致",content.toString());
//	}
//	
//	@Test
//	public void printTest_012() throws InterruptedException, IOException {
//		init("Case12 - 多检验申请单打印测试", true);
//		String filePath = "F:\\labtest.pdf";
//		String labName2 ="餐后一小时葡萄糖";
//		String labName3 ="铁蛋白测定";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
//		browser.wnOutpatientWorkflow.prescribeOrder(labName2);
//		browser.wnOutpatientWorkflow.prescribeOrder(labName3);
//		ArrayList<String> labtestInfo = new ArrayList<String>();
//		labtestInfo.addAll(browser.wnOutpatientWorkflow.getLabtestInfo(Data.test_prescribe_lab));
//		labtestInfo.addAll(browser.wnOutpatientWorkflow.getLabtestInfo(labName2));
//		labtestInfo.addAll(browser.wnOutpatientWorkflow.getLabtestInfo(labName3));
//		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
//		WindowsUI.savePrintFile(filePath);
////      File file=new File(filePath);  
//		String responseMsg = WinningOcr.getWSOutput(filePath);
////      String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(labtestInfo, responseMsg);
//        browser.logger.assertFalse(content.size()>0, "打印信息不完全一致",content.toString());
//	}
//	
//	@Test
//	public void printTest_013() throws InterruptedException, IOException {
//		init("Case13 - 多治疗申请单打印测试", true);
//		String filePath = "F:\\treat.pdf";
//		String treatName2 = "大抢救";
//		String treatName3 = "口腔活检术";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
//		browser.wnOutpatientWorkflow.prescribeOrder(treatName2);
//		browser.wnOutpatientWorkflow.prescribeOrder(treatName3);
//		ArrayList<String> treatInfo = new ArrayList<String>();
//		treatInfo.addAll(browser.wnOutpatientWorkflow.getTreatInfo(Data.test_prescribe_treat));
//		treatInfo.addAll(browser.wnOutpatientWorkflow.getTreatInfo(treatName2));
//		treatInfo.addAll(browser.wnOutpatientWorkflow.getTreatInfo(treatName3));
//		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
//		WindowsUI.savePrintFile(filePath);
////      File file=new File(filePath);  
//		String responseMsg = WinningOcr.getWSOutput(filePath);
////      String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(treatInfo, responseMsg);
//        browser.logger.assertFalse(content.size()>0, "打印信息不完全一致",content.toString());
//	}
	
//	@Test
//	public void demo() throws InterruptedException, IOException {
//		init("Case01 - 病历测试", true);
//		String filePath = "F:\\22.pdf";
//		ArrayList<String> herbInfo =browser.wnOutpatientWorkflow.getEmrInfo();
//        File file=new File(filePath);       
//        String responseMsg =browser.wnOutpatientWorkflow.getPDFText(Data.PdfOCRUrl,file);
//        ArrayList<String> content = Public.checkMsg(herbInfo, responseMsg);
//        browser.logger.throwError(content.size()>0, "打印信息不完全一致",content.toString());
//	}

}