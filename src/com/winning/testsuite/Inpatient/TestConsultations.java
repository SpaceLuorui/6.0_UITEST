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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestConsultations extends InpatientTestBase {
    public TestConsultations() {
        super();
    }

    static {
        SdkTools.initReport("患者出入区专项", "TestConsultations.html");
        Data.closeBrowser = false;
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","ZQ25");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_05() {
        init("CASE-05: 转科转区", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.transferArea(Data.inpatient_select_newDepartmen,Data.inpatient_select_newWard);
    }

    @Test
    public void test_06() {
        init("CASE-05: 撤销转区", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.transferArea(Data.inpatient_select_newDepartmen,Data.inpatient_select_newWard);
        browser.wnInpatientWorkflow.transferAreaRevoke();
    }

    @Test
    public void test_01() {
        init("CASE-01: 入区", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
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
    }

    @Test
    public void test_03() {
        init("CASE-03: 出区", true);
        String patientName = browser.wnInpatientWorkflow.hisAdmissionRegistration().get(0);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
        String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
        browser.wnInpatientWorkflow.addBed(BedNo);
        browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
        browser.wnInpatientWorkflow.callNumberByName(patientName);
        browser.wnInpatientWorkflow.outHospital();
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
    }
}