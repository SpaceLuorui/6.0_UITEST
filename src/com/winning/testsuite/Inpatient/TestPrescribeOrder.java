package com.winning.testsuite.Inpatient;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.InpatientTestBase;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.util.SdkTools;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPrescribeOrder extends InpatientTestBase {
    public TestPrescribeOrder() { super();}

    static {
        SdkTools.initReport("处置开立专项", "TestPrescribeOrder.html");
            Data.closeBrowser = false;

    }

    @Test
    public void test_01_prescribeOrder(){
        init("CASE-01: 搜索开立", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);   //开立西药
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);   //开立中药
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);   //开立检查
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);    //开立检验
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);  //开立治疗
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing);  //开立护理
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_operation);  //开立手术
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_diet);  //开立膳食
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor();
        browser.wnInpatientWorkflow.orderApplication();

    }



    @Test
    public void test_02_requestingOrder(){
        init("CASE-02: 电子申请单开立", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.requestOrder("检查");
        browser.wnInpatientWorkflow.requestOrder("检验");
        browser.wnInpatientWorkflow.requestOrder("治疗");
        browser.wnInpatientWorkflow.requestOrder("病理");
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor();
        browser.wnInpatientWorkflow.orderApplication();


    }

    @Test
    public void test_03_templateOrder(){
        init("CASE-03: 添加医嘱模板、医嘱模板开立、医嘱模板删除", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);   //开立西药
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);   //开立中药
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);   //开立检查
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);    //开立检验
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);  //开立治疗
        browser.wnInpatientWorkflow.signOff(0);
        Data.orderTemplateName = Data.orderTemplateName+System.currentTimeMillis();
        browser.wnInpatientWorkflow.addTemplate( Data.orderTemplateName,Data.DispositionTemplateType,Data.test_disease);
        String patientName1 = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.enterWard(patientName1);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName1);
        browser.wnInpatientWorkflow.quoteTemplate(Data.orderTemplateName,Data.DispositionTemplateType);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.deleteTemplate(Data.orderTemplateName,Data.DispositionTemplateType);
    }

    @Test
    public void test_04_collectOrder(){
        init("CASE-04: 收藏医嘱、收藏开立、取消收藏", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.collectOrder(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.collectOrder(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.collectOrder(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.collectOrder(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.collectOrder(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.searchOrderForUnCollect(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.searchOrderForUnCollect(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.searchOrderForUnCollect(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.searchOrderForUnCollect(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.searchOrderForUnCollect(Data.test_prescribe_treat);

    }

    @Test
    public void test_05_historicalOrder(){
        init("CASE-05: 历史医嘱开立", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.historicalOrder();
        browser.wnInpatientWorkflow.signOff(0);
    }

    @Test
    public void test_06_copyOrder(){
        init("CASE-06: 复制医嘱进行开立", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.copyOrder();
        browser.wnInpatientWorkflow.signOff(0);
    }


    @Test
    public void test_07_copyToClipBoard(){
        init("CASE-07: 复制到剪切板进行开立", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.copyToClipBoard();
        browser.wnInpatientWorkflow.clipBoardToPrescribeOrder();
        browser.wnInpatientWorkflow.signOff(0);
    }

    @Test
    public void test_08_copyAndStopOrder(){
        init("CASE-08: 复制并停止医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.copyAndStopOrder();
        browser.wnInpatientWorkflow.signOff(0);
    }
}