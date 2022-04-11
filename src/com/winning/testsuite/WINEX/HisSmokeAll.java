package com.winning.testsuite.WINEX;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;
import xunleiHttpTest.HttpTestHeader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeAll extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeAll() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟", "大his冒烟测试_完整版.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	@Test
	public void test_001(){
		Data.headless = false;
		init("SmokeAll_CASE-01: 患者门诊就诊(患者建档、挂号、查询、退号、预约挂号、预约取消)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		browser.wnWINEXWorkflow.queryEncounterRecord(patientName,Data.test_select_subject);
		browser.wnWINEXWorkflow.cancelEncounter(patientName, Data.test_select_subject);
		ArrayList<String> info = browser.wnWINEXWorkflow.appointmentRegister(patientName);
		if(info.get(0).equals("false")){
			browser.logger.boxLog(0, "暂无可预约班次", "");
			return;
		}
		browser.wnWINEXWorkflow.queryAppointmentRegister(patientName,info.get(1));
		browser.wnWINEXWorkflow.cancelAppointmentRegister(patientName,info.get(1));
	}
		
	@Test
	public void test_002(){
		init("SmokeAll_CASE-02: 患者住院就诊(患者入院预约、登记、缴纳押金、红冲押金、查询、取消)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.admissionRegistration(patientName);
		browser.wnWINEXWorkflow.loginMenuByName("住院预交金");
		browser.wnWINEXWorkflow.searchPatientByName(patientName);
		browser.wnwd.checkElementByXpath("加载患者完成标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		browser.wnWINEXWorkflow.depositPayment("100");
		browser.wnWINEXWorkflow.searchPatientByName(patientName);
		browser.wnwd.checkElementByXpath("加载患者完成标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		browser.wnWINEXWorkflow.reversePayment();
		browser.wnWINEXWorkflow.queryHospitalized(patientName);
		browser.wnWINEXWorkflow.cancelHospitalized(patientName);
	}
	
	@Test
	public void test_003() throws UnsupportedEncodingException{
		init("SmokeAll_CASE-03: 门诊-大his流程——挂号、门诊开立、签署、门诊收费、未发药退费", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		String patientName = patInfo.get(0);
		ArrayList<String> info = browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		
		String visitNumber = info.get(0);  //挂号号序
		String outpatientNumber = info.get(1);  //门诊号
		
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patientName, visitNumber);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.signOff(5000);
		
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
		String cost = browser.wnWINEXWorkflow.outpatientCharges(patientName);
		browser.wnWINEXWorkflow.outpatientChargesBack(outpatientNumber,patientName,cost);
	}

	@Test
	public void test_004() throws UnsupportedEncodingException{
		init("SmokeAll_CASE-04: 门诊-大his流程——挂号、门诊开立、签署、收费、发药、退药、退费", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		String patientName = patInfo.get(0);
		ArrayList<String> info = browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		
		String visitNumber = info.get(0);  //挂号号序
		String outpatientNumber = info.get(1); //门诊号

		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patientName, visitNumber);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);

		browser.wnOutpatientWorkflow.signOff(5000);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
		Map<String, String> pharmacyInfo = browser.wnWINEXWorkflow.getPharmacyInfoByPatientName(patientName);
		String pharmacyNo = pharmacyInfo.get("ORG_NO");
		String pharmacyName = pharmacyInfo.get("ORG_NAME");
		String pharmacyId = browser.decouple.db60.getOrgIdByOrgName(pharmacyName, pharmacyNo);
		browser.wnWINEXWorkflow.updateReviewerStatus(pharmacyId, pharmacyName, pharmacyNo, "98176", "98176");
		String cost = browser.wnWINEXWorkflow.outpatientCharges(patientName);
		browser.wnWINEXWorkflow.queryOutpatientCharges(patientName, cost);
		browser.wnWINEXWorkflow.OutpatientDispensing(Data.windowsName, patientName);
		browser.wnWINEXWorkflow.OutpatientDispensingBack(pharmacyName, patientName);
		browser.wnWINEXWorkflow.outpatientChargesBack(outpatientNumber,patientName,cost);
	}	
	
	@Test
	public void test_005() throws UnsupportedEncodingException{
		Data.host=Data.inpatient_host;
		init("SmokeAll_CASE-05: 住院-大his完整全流程(入院预约、登记、住院入区、开立签署、签收申请执行、HIS发药、住院退药申请、HIS退药)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.admissionRegistration(patientName);
		browser.wnwd.openUrl("http://" + Data.host + "/portal/login");
		browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnInpatientWorkflow.setParamsForTestAllService();
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		try {
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);   //开立西药
			browser.wnInpatientWorkflow.signOff(5000);
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnwd.waitElementByXpathAndClick("点击医嘱管理标签", "//span[contains(text(),'医嘱管理')]", Framework.defaultTimeoutMax);
			browser.wnInpatientWorkflow.signFor();
			browser.wnInpatientWorkflow.orderApply();
			browser.wnInpatientWorkflow.orderApplication();
			Map<String, String> pharmacyInfo = browser.wnWINEXWorkflow.getPharmacyInfoByPatientName(patientName);
			String pharmacyNo = pharmacyInfo.get("ORG_NO");
			String pharmacyName = pharmacyInfo.get("ORG_NAME");
			//大his进行住院发药
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
			String pharmacyId = browser.decouple.db60.getOrgIdByOrgName(pharmacyName, pharmacyNo);
			browser.wnWINEXWorkflow.updateReviewerStatus(pharmacyId, pharmacyName, pharmacyNo, "98176", "98176");
			browser.wnWINEXWorkflow.InpatientDispensing(pharmacyName, patientName);
			//大临床提交退药申请
			browser.wnwd.openUrl("http://" + Data.host + "/portal/login");
			browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//			browser.wnWINEXWorkflow.SetMT0028(Data.host, "0", "允许");
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.dispensingBackApply(patientName);
			//大his进行住院退药
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.inpatientDispensingBack(pharmacyName, patientName);
		} catch (Throwable e) {
//			browser.logger.boxLog(0, "用例执行失败，执行出区操作", "");
			throw new Error(e.getMessage()+"  患者姓名："+patientName);
		}finally {
			//调接口进行出区操作
//			HttpTestHeader header = browser.wnWINEXWorkflow.confusionLoginForWINEX(Data.inpatient_host,Data.default_user_login_account, Data.default_user_login_pwd);
//			String loginEmployeeId =browser.decouple.db60.getEmployeeIdByEmployeeNo(Data.default_user_login_account);
//			browser.wnWINEXWorkflow.outhospitalByInterface(header, loginEmployeeId, patientName);
		}
	}
	
	@Test
	public void test_006() throws UnsupportedEncodingException{
		Data.host=Data.inpatient_host;
		init("SmokeAll_CASE-06: 住院-大his完整全流程(入院预约、登记、住院入区、开立检查、签署、签收申请执行、中途结算、取消结算、出区、出院结算)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.admissionRegistration(patientName);
		browser.wnwd.openUrl("http://" + Data.host + "/portal/login");
		browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnInpatientWorkflow.setParamsForTestAllService();
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		boolean bFlag = false;
		try {
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);   //开立检查
			browser.wnInpatientWorkflow.signOff(1000);
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnwd.waitElementByXpathAndClick("点击医嘱管理标签", "//span[contains(text(),'医嘱管理')]", Framework.defaultTimeoutMax);
			browser.wnInpatientWorkflow.signFor();
			browser.wnInpatientWorkflow.orderApply();
			browser.wnInpatientWorkflow.orderApplication();
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.inpatientCharges(patientName);
			browser.wnWINEXWorkflow.cancelInpatientCharges(patientName);
		} catch (Throwable e) {
			bFlag = true;
			browser.logger.boxLog(0, "用例执行失败，执行出区操作", "");
			throw new Error(e.getMessage()+"  患者姓名："+patientName);
		}finally {
			HttpTestHeader header = browser.wnWINEXWorkflow.confusionLoginForWINEX(Data.inpatient_host,Data.default_user_login_account, Data.default_user_login_pwd);
			String loginEmployeeId =browser.decouple.db60.getEmployeeIdByEmployeeNo(Data.default_user_login_account);
			browser.wnWINEXWorkflow.outhospitalByInterface(header, loginEmployeeId, patientName);
			if(!bFlag) {
				browser.wnWINEXWorkflow.inpatientCharges(patientName);
				browser.wnWINEXWorkflow.cancelInpatientCharges(patientName);
			}
		}
	}
	
	@Test
	public void test_007(){
		init("CASE-07:报损出库，部门出库，职工出库 ", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		
		browser.wnWINEXWorkflow.loginMenuByName("报损出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutLoss(Data.WINEX_stock_drug);
		
		browser.wnWINEXWorkflow.loginMenuByName("部门出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutDepartment(Data.WINEX_stock_drug);
		
		browser.wnWINEXWorkflow.loginMenuByName("职工出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutWorkers(Data.WINEX_stock_drug);
	}
	
	@Test
	public void test_008(){
		init("CASE-08:报溢入库，库存查询变化 ", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("库存查询");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String stockBefore = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug);
		stockBefore = Pattern.compile("[^0-9]").matcher(stockBefore).replaceAll("").trim(); 
		browser.wnWINEXWorkflow.loginMenuByName("报溢入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockInMore(Data.WINEX_stock_drug,"5");
		
		browser.wnWINEXWorkflow.loginMenuByName("库存查询");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String stockAfter = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug);
		stockAfter = Pattern.compile("[^0-9]").matcher(stockAfter).replaceAll("").trim(); 
		browser.logger.assertFalse(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)!=5, "库存更新错误,库存预期增加应该为5，实际增加为"+(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)));
		
		
	}
	@Test
	public void test_009(){
		init("CASE-09: 请调流程(请调申请，请调出库，请调入库)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("请调申请");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String billNo = browser.wnWINEXWorkflow.addByApplicationForTransfer(Data.WINEX_stock_drug,Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.loginMenuByName("请调出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		billNo = browser.wnWINEXWorkflow.dealwithByMedicineStockOutTransfer(billNo);
		browser.wnWINEXWorkflow.loginMenuByName("请调入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.dealwithByMedicineStockInTransfer(billNo);
		
	}
	
	@Test
	public void test_010(){
		init("CASE-10: 请调退库(请调退库，请调退库接收)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("请调退库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String billNo = browser.wnWINEXWorkflow.dealwithByMedicineStockReturnTransfer(Data.WINEX_stock_drug,Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.loginMenuByName("请调退库接收");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.dealwithByMedicineStockReceiveTransfer(billNo);
	}
	
	@Test
	public void test_011(){
		init("CASE-11: 采购入库(采购入库，采购退货)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("采购入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
	    String billNo = browser.wnWINEXWorkflow.addByMedicineStockInPurchase(Data.WINEX_stock_drug);
	    browser.wnWINEXWorkflow.loginMenuByName("采购退货");
	    browser.wnWINEXWorkflow.addByMedicineStockRuturnPurchase(Data.WINEX_stock_drug,billNo);
	}
	
}
