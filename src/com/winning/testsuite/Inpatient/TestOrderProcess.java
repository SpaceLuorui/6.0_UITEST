package com.winning.testsuite.Inpatient;
import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.InpatientTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

import java.util.Arrays;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestOrderProcess extends InpatientTestBase {
    public TestOrderProcess() {
        super();
    }

    static {
        SdkTools.initReport("医嘱流程专项", "TestOrderProcess.html");
        Data.closeBrowser = false;
        try {
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV", "ZQ25");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01() {
        init("CASE-01: 停止医嘱-未签收", true);
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
        //单条停止
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.stopOrder(Data.test_prescribe_drug);
        //批量停止
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.stopOrders();
    }

    @Test
    public void test_02() {
        init("CASE-02: 停止医嘱-已签收", true);
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
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.stopOrder(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.selectAll();
        browser.wnwd.waitElementByXpathAndClick("状态", WnInpatientXpath.orderStatus, Framework.defaultTimeoutMid);
    }

    @Test
    public void test_03() {
        init("CASE-03: 作废医嘱-未签收", true);
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
        //单条作废
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.nullifyOrder(Data.test_prescribe_drug);
        browser.wnwd.waitNotExistByXpath("医嘱消失", WnInpatientXpath.tickOrder, Framework.defaultTimeoutMax);
        //批量作废
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_nursing);
        browser.wnInpatientWorkflow.signOff(15000);
        browser.wnInpatientWorkflow.nullifyOrders();
    }

    @Test
    public void test_04() {
        init("CASE-04: 作废医嘱-已签收", true);
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
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_diet);
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor();
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        //临时医嘱作废
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.nullifyOrder(Data.test_prescribe_diet);
        browser.wnwd.waitNotExistByXpath("提示", WnInpatientXpath.getNullifyOrderFailMsg, Framework.defaultTimeoutMax);
        //长期医嘱作废
        browser.wnInpatientWorkflow.nullifyOrderSign(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_drug);
        browser.wnInpatientWorkflow.selectAll();
        browser.wnwd.waitElementByXpathAndClick("状态", WnInpatientXpath.orderStatus, Framework.defaultTimeoutMid);
    }

    @Test
    public void test_05() {
        init("CASE-05: 撤回医嘱", true);
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
        //撤回未签收医嘱
        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnInpatientWorkflow.recallOrder(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("提示", WnInpatientXpath.recallOrderSucMsg, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug,null, Arrays.asList("1","long"));
        browser.wnInpatientWorkflow.signOff(0);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.signFor(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
        //撤回已签收医嘱
        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.recallOrder(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("提示", WnInpatientXpath.recallOrderFailMsg, Framework.defaultTimeoutMid);
    }
    @Test
    public void test_06() {
        init("CASE-06: 疑问返回", true);
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
        browser.wnInpatientWorkflow.doubtOrder(Data.test_prescribe_drug);
        browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.secondOrder(Data.test_prescribe_drug);
    }
}
