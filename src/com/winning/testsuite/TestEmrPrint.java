package com.winning.testsuite;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEmrPrint extends OutpatientTestBase {

    public TestEmrPrint() {
        super();
    }

    static {
        SdkTools.initReport("打印测试", "print.html");
        Data.useHybirdApp = true;
    }


    @Test
    public void test_01_emr_print() throws InterruptedException {
        init("签署并打印", true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByName(encounterInfo.get(0));
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
        browser.wnOutpatientWorkflow.signOffAndPrint(0);
        browser.wnOutpatientWorkflow.wnwd.checkError(10000);
    }
}