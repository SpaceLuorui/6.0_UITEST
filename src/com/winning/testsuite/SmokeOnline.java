package com.winning.testsuite;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SmokeOnline extends OutpatientTestBase {

    public static ArrayList<String> patInfo=null;
    public static Boolean sucFlag = false;
    public SmokeOnline() {
        super();
    }

    static {

        Data.getScreenShot=true;
        SdkTools.initReport("冒烟测试(现场)","UI流程测试报告(现场).html");
        Data.closeBrowser=false;
        Data.checkWarning = true;
    }

    @Test
    public void test_001(){
        init("CASE-01: 冒烟-西医开立-诊断高血压-正式库",true);
        //获取数据
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        Data.getEncounterFromFile = true;//从文件里面获取患者信息，获取 encounter.txt文件里的第一个患者信息
        ArrayList<String> patInfo =browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3),true);
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.clickDignoselistFirstDignose();
        browser.wnOutpatientWorkflow.searchOrderBySeq("西成药","",0);
        browser.wnOutpatientWorkflow.signOff(1000);
        browser.wnOutpatientWorkflow.deleteAllOrder();
        browser.wnOutpatientWorkflow.deleteFirstDignose();
    }
}
