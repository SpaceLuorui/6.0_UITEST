package com.winning.testsuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrintingTest_new extends OutpatientTestBase {

	public PrintingTest_new() {
		super();
	}

	static {
		SdkTools.initReport("打印流程专项", "打印测试报告.html");
		Config.load("print_config");
		Data.useHybirdApp = true;

	}

	@Test
	public void printTest_001() throws InterruptedException, IOException {
		init("Case01 - 单西药处方单打印测试", true);

		String drugFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "drug.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("drug.frx", "98360");
		String drugRecipeReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("drug.frx").trim();
		if(drugRecipeReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前西药处方单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		ArrayList<String> drugInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getDrugInfo(Data.test_prescribe_drug);
		drugInfo.addAll(info.get("drugInfo"));
        if(SdkTools.analyticConfiguration(Data.drugRecipeReportConfig,"检查患者姓名")) {
            drugInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(drugRecipeReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(drugFilePath), "西药处方单保存文件失败");
			String drugResponseMsg = browser.wnwd.getPdfContentByOcr(drugFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + drugInfo);
			browser.logger.boxLog(1, "处方单打印文件转化结果为", "" + drugResponseMsg);
			content = SdkTools.checkMsg(drugInfo, drugResponseMsg);
		}
        if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "西成药处方单打印信息不完全一致","西成药处方单中错误信息："+content.toString());
		}
	}
	
	@Test
	public void printTest_002() throws InterruptedException, IOException {
		init("Case02 - 单中草药处方单打印测试", true);

		String herbFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "herb.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("herb.frx", "98360");
		String herbRecipeReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("herb.frx").trim();
		if(herbRecipeReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前中草药处方单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		ArrayList<String> herbInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getHerbInfo(Data.test_prescribe_herb);
		herbInfo.addAll(info.get("herbInfo"));
        if(SdkTools.analyticConfiguration(Data.herbRecipeReportConfig,"检查患者姓名")) {
            herbInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(herbRecipeReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(herbFilePath), "中草药处方单保存文件失败");
			String herbResponseMsg = browser.wnwd.getPdfContentByOcr(herbFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + herbInfo);
			browser.logger.boxLog(1, "中草药处方单打印文件转化结果为", "" + herbResponseMsg);
			content = SdkTools.checkMsg(herbInfo, herbResponseMsg);
		}
        if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "中草药处方单打印信息不完全一致","处方单中错误信息："+content.toString());
		}    
	}
	
	@Test
	public void printTest_003() throws InterruptedException, IOException {
		init("Case03 - 单检查申请单打印测试", true);
		String examFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "exam.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("exam.frx", "98360");
		String examRecipeReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("exam.frx").trim();
		if(examRecipeReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前导诊单及检查申请单都是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		ArrayList<String> examInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getExamInfo(Data.test_prescribe_exam);
		examInfo.addAll(info.get("examInfo"));
        if(SdkTools.analyticConfiguration(Data.examOrderReportConfig,"检查患者姓名")) {
            examInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(examRecipeReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(examFilePath), "检查申请单保存文件失败");
			String examResponseMsg = browser.wnwd.getPdfContentByOcr(examFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + examInfo);
			browser.logger.boxLog(1, "检查申请单打印文件转化结果为", "" + examResponseMsg);
			content = SdkTools.checkMsg(examInfo, examResponseMsg);
		}
        if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "检查申请单打印信息不完全一致","检查申请单中错误信息："+content.toString());
		}
	}
	
	@Test
	public void printTest_004() throws InterruptedException, IOException {
		init("Case04 - 单检验申请单打印测试", true);

		String labFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "lab.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("test.frx", "98360");
		String labRecipeReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("test.frx").trim();
		if(labRecipeReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前导诊单及检验申请单都是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		ArrayList<String> labInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getLabtestInfo(Data.test_prescribe_lab);
		labInfo.addAll(info.get("labTestInfo"));
        if(SdkTools.analyticConfiguration(Data.labTestOrderReportConfig,"检查患者姓名")) {
            labInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(labRecipeReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(labFilePath), "检验申请单保存文件失败");
			String labResponseMsg = browser.wnwd.getPdfContentByOcr(labFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + labInfo);
			browser.logger.boxLog(1, "检验申请单打印文件转化结果为", "" + labResponseMsg);
			content = SdkTools.checkMsg(labInfo, labResponseMsg);
		}
        if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "检验申请单打印信息不完全一致","检验申请单中错误信息："+content.toString());
		}   
	}
	
	
	@Test
	public void printTest_005() throws InterruptedException, IOException {
		init("Case05 - 单治疗申请单打印测试", true);

		String treatFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "treat.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("treatment.frx", "98360");
		String treatRecipeReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("treatment.frx").trim();
		if(treatRecipeReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前治疗申请单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		ArrayList<String> treatInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getTreatInfo(Data.test_prescribe_treat);
		treatInfo.addAll(info.get("treatInfo"));
        if(SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig,"检查患者姓名")) {
            treatInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(treatRecipeReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(treatFilePath), "治疗申请单保存文件失败");
			String treatResponseMsg = browser.wnwd.getPdfContentByOcr(treatFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + treatInfo);
			browser.logger.boxLog(1, "治疗申请单打印文件转化结果为", "" + treatResponseMsg);
			content = SdkTools.checkMsg(treatInfo, treatResponseMsg);
		}
        if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "治疗申请单打印信息不完全一致","治疗申请单中错误信息："+content.toString());
		}
	}
	
	
	@Test
	public void printTest_006() throws InterruptedException, IOException {
		init("Case06 - 单西药导诊单打印测试", true);
		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "drugGuide.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("guide.frx", "98360");
		String guideSheetReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("guide.frx").trim();
		if(guideSheetReport.equals("98361")&&Data.drugGuideFlag) {
			browser.logger.assertFalse(true, "当前导诊单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getDrugInfo(Data.test_prescribe_drug);
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(guideSheetReport.equals("98360")&&Data.drugGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "导诊单打印信息不完全一致","导诊单中错误信息："+content.toString());
		}
	}
	
	@Test
	public void printTest_007() throws InterruptedException, IOException {
		init("Case07 - 单中草药导诊单打印测试", true);

		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "herbGuide.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("guide.frx", "98360");
		String guideSheetReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("guide.frx").trim();
		if(guideSheetReport.equals("98361")&&Data.herbGuideFlag) {
			browser.logger.assertFalse(true, "当前导诊单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getHerbInfo(Data.test_prescribe_herb);
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(35000);
		List<String> content = new ArrayList<String>();
		if(guideSheetReport.equals("98360")&&Data.herbGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "导诊单打印信息不完全一致","导诊单中错误信息："+content.toString());
		}
	}
	
	@Test
	public void printTest_008() throws InterruptedException, IOException {
		init("Case08 - 单检查导诊单打印测试", true);

		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "examGuide.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("guide.frx", "98360");
		String guideSheetReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("guide.frx").trim();
		if(guideSheetReport.equals("98361")&&Data.examGuideFlag) {
			browser.logger.assertFalse(true, "当前导诊单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getExamInfo(Data.test_prescribe_exam);
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(guideSheetReport.equals("98360")&&Data.examGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "导诊单打印信息不完全一致","导诊单中错误信息："+content.toString());
		}
	}
	
	@Test
	public void printTest_009() throws InterruptedException, IOException {
		init("Case09 - 单检验导诊单打印测试", true);

		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "labGuide.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("guide.frx", "98360");
		String guideSheetReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("guide.frx").trim();
		if(guideSheetReport.equals("98361")&&Data.labGuideFlag) {
			browser.logger.assertFalse(true, "当前导诊单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getLabtestInfo(Data.test_prescribe_lab);
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(guideSheetReport.equals("98360")&&Data.labGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "导诊单打印信息不完全一致","导诊单中错误信息："+content.toString());
		}
	}
	
	
	@Test
	public void printTest_010() throws InterruptedException, IOException {
		init("Case10 - 单治疗导诊单打印测试", true);

		String guideFilePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "treatGuide.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("guide.frx", "98360");
		String guideSheetReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("guide.frx").trim();
		if(guideSheetReport.equals("98361")&&Data.treatGuideFlag) {
			browser.logger.assertFalse(true, "当前导诊单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		ArrayList<String> guideInfo = new ArrayList<String>();
		Map<String, ArrayList<String>> info = browser.wnOutpatientWorkflow.getTreatInfo(Data.test_prescribe_treat);
		guideInfo.addAll(info.get("guideInfo"));
        if(SdkTools.analyticConfiguration(Data.guideSheetReportConfig,"检查患者姓名")) {
            guideInfo.add(encounterInfo.get(0));
        }
		browser.wnOutpatientWorkflow.signOffAndPrint(10000);
		List<String> content = new ArrayList<String>();
		if(guideSheetReport.equals("98360")&&Data.treatGuideFlag) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(guideFilePath), "导诊单保存文件失败");
			String guideResponseMsg = browser.wnwd.getPdfContentByOcr(guideFilePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + guideInfo);
			browser.logger.boxLog(1, "导诊单打印文件转化结果为", "" + guideResponseMsg);
	        content = SdkTools.checkMsg(guideInfo, guideResponseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "导诊单打印信息不完全一致","导诊单中错误信息："+content.toString());
		}
	}
	

	
//	@Test
//	public void printTest_011() throws InterruptedException, IOException {
//		init("Case11 - 请假单打印测试", true);
//
//		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "sickLeave.pdf";
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
//		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("888.frx", "98360");
//		String sickLeaveReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("888.frx").trim();
//		if (sickLeaveReport.equals("98361")) {
//			browser.logger.assertFalse(true, "当前请假单是不打印状态");
//		}
//		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
//		browser.wnOutpatientWorkflow.skip();
//		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
////		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
////		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
//		
//		String[] printCheckMsg_sickLeave = Data.printCheckMsg_sickLeave.split(",");
//		ArrayList<String> info = new ArrayList<String>(Arrays.asList(printCheckMsg_sickLeave));
//		ArrayList<String> sickLeaveInfo = browser.wnOutpatientWorkflow.writeSickLeaveForm();
//		info.addAll(sickLeaveInfo);
//		
//		
//		if(WnUtils.analyticConfiguration(Data.sickLeaveReportConfig,"检查患者姓名")) {
//			info.add(encounterInfo.get(0));
//        }
//		List<String> content = new ArrayList<String>();
//		if(sickLeaveReport.equals("98360")) {
//			browser.logger.assertTrue(browser.wnwd.WnPrinter(filePath), "请假单保存文件失败");
//			String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
//			browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
//			browser.logger.boxLog(1, "请假单打印文件转化结果为", "" + responseMsg);
//	        content = WnUtils.checkMsg(info, responseMsg);
//		}
//		if(content!= null&&content.size()!=0) {
//			browser.logger.assertFalse(content.size()>0, "请假单打印信息不完全一致","请假单中错误信息："+content.toString());
//		}
//	}
	
	@Test
	public void printTest_012() throws InterruptedException, IOException {
		init("Case07 - 疾病证明单打印测试", true);
		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "diseaseCertificate.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("jbzmd.frx", "98360");
		String diseaseCertificateReport = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("jbzmd.frx").trim();
		if (diseaseCertificateReport.equals("98361")) {
			browser.logger.assertFalse(true, "当前疾病证明单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		
		ArrayList<String> info = new ArrayList<String>();
		ArrayList<String> diseaseCertificateInfo = browser.wnOutpatientWorkflow.writeDiseaseCertificate();
		info.addAll(diseaseCertificateInfo);
		if(SdkTools.analyticConfiguration(Data.sickLeaveReportConfig,"检查患者姓名")) {
			info.add(encounterInfo.get(0));
        }
		List<String> content = new ArrayList<String>();
		if(diseaseCertificateReport.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(filePath), "疾病证明单保存文件失败");
			String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
			browser.logger.boxLog(1, "疾病证明单打印文件转化结果为", "" + responseMsg);
	        content = SdkTools.checkMsg(info, responseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "疾病证明单打印信息不完全一致","疾病证明单中错误信息："+content.toString());
		}
	}
	
	
	@Test
	public void printTest_013() throws InterruptedException, IOException {
		init("Case08 - 病历测试", true);
		String filePath = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "emr.pdf";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.printTemplate_saveAll("98361");
		browser.wnOutpatientWorkflow.printTemplate_saveByPrintTemplateName("duide.frx", "98360");
		String emrStatus = browser.wnOutpatientWorkflow.getPrintTemplateStatusByName("duide.frx").trim();
		if (emrStatus.equals("98361")) {
			browser.logger.assertFalse(true, "当前病历单是不打印状态");
		}
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		browser.wnOutpatientWorkflow.signOff(10000);
		browser.wnOutpatientWorkflow.emrSignoff();
		browser.wnOutpatientWorkflow.emrPrint();
		ArrayList<String> info = new ArrayList<String>();
		ArrayList<String> emrInfo = browser.wnOutpatientWorkflow.getEmrInfo();
		info.addAll(emrInfo);
		if(SdkTools.analyticConfiguration(Data.emrTemplatePrintConfig,"检查临床诊断")) {
			info.add(Data.test_disease);
		}
		if(SdkTools.analyticConfiguration(Data.emrTemplatePrintConfig,"检查科室")) {
			info.add(Data.test_select_subject);
		}
		
		List<String> content = new ArrayList<String>();
		if(emrStatus.equals("98360")) {
			browser.logger.assertTrue(browser.wnwd.WnPrinter(filePath), "病历单保存文件失败");
			String responseMsg = browser.wnwd.getPdfContentByOcr(filePath);
			browser.logger.boxLog(1, "需要校验的信息为：", "" + info);
			browser.logger.boxLog(1, "病历单打印文件转化结果为", "" + responseMsg);
	        content = SdkTools.checkMsg(info, responseMsg);
		}
		if(content!= null&&content.size()!=0) {
			browser.logger.assertFalse(content.size()>0, "病历单打印信息不完全一致","病历单中错误信息："+content.toString());
		}
	}
}