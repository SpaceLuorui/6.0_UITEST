package com.winning.testsuite.Inpatient;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.InpatientTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFhir extends InpatientTestBase {
    public TestFhir() {
        super();
    }

    static {
        SdkTools.initReport("Fhir对接专项", "TestFhir.html");
        Data.closeBrowser = false;
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","ZQ25");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01() {
        init("CASE-01: 入区登记", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.compareOrder(patientName,"enter");
    }

    @Test
    public void test_02() {
        init("CASE-02: 入区取消", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.enterWardRevoke();
        browser.wnInpatientWorkflow.comparePatient(patientName,"cancel");
    }

    @Test
    public void test_03() {
        init("CASE-03: 出区登记", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.outHospital();
        browser.wnInpatientWorkflow.comparePatient(patientName,"out");
    }

    @Test
    public void test_04() {
        init("CASE-04: 出区召回", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.outHospital();
        browser.wnInpatientWorkflow.outHospitalRecall(patientName);
        browser.wnInpatientWorkflow.comparePatient(patientName,"recall");
    }

    @Test
    public void test_05() {
        init("CASE-05: 西成药-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        //医嘱签收
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOccyp");
        //医嘱申请
        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyDrugOcc");
        //医嘱撤销申请
        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_06() {
        init("CASE-06: 西成药-长期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signLongyp");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_drug,"long");
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyDrugLong");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyLong");
    }

    @Test
    public void test_07() {
        init("CASE-07: 草药-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOccyp");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signHerb");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyDrugOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_08() {
        init("CASE-08: 检验-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signInspection");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signJy");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_lab);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_09() {
        init("CASE-09: 检查-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signInspection");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_exam);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_10() {
        init("CASE-10: 治疗-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_11() {
        init("CASE-11: 治疗-长期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_treat,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signLong");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_treat,"long");
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyLong");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_treat);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyLong");
    }

    @Test
    public void test_12() {
        init("CASE-12: 手术-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_operation);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_operation);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signInspection");
        browser.wnInpatientWorkflow.compareOrder(patientName,"signSs");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_operation);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_operation);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_13() {
        init("CASE-13: 膳食-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_14() {
        init("CASE-14: 膳食-长期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_diet,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signLong");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_diet,"long");
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyLong");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyLong");
    }

    @Test
    public void test_15() {
        init("CASE-15: 护理-临期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signOcc");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyOcc");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyOcc");
    }

    @Test
    public void test_16() {
        init("CASE-16: 护理-长期医嘱", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.compareOrder(patientName,"signLong");

        browser.wnInpatientWorkflow.orderApply(Data.test_prescribe_nursing,"long");
        browser.wnInpatientWorkflow.compareOrder(patientName,"applyLong");

        browser.wnInpatientWorkflow.orderAppliyCancel(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.compareOrder(patientName,"revokeApplyLong");
    }
}
