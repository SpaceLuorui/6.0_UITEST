package com.winning.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TraditionalChineseMedicine extends OutpatientTestBase {

    public TraditionalChineseMedicine() {
        super();
    }

    static {
        Data.getScreenShot=true;
        SdkTools.initReport("中医科流程专项", "中医科专项.html");
        Data.headless = false;
    }

    @Test
    public void case_01() throws InterruptedException {
        init("Case01 - 代煎代配流程，一次开立多种中草药 " , true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.TCM_subjCode);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.SetRE022(Data.host, "0", "知识编辑器");
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.TCM_subjName);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.TCM_symptom1);
        browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.TCM_symptom1);
        browser.wnOutpatientWorkflow.diagnosis(Data.TCM_diagnose);
        browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.TCM_diagnose);
        List<Map<String,String>> herbs = new ArrayList<Map<String,String>>();
        PrescribeDetail detail = new PrescribeDetail();
        Map<String,String> herb1 = new HashMap<String,String>();
        herb1.put("name", "徐长卿");
        herb1.put("dose", "30");
        Map<String,String> herb2 = new HashMap<String,String>();
        herb2.put("name", "天葵子");
//        herb2.put("dose", "5");
        
        herbs.add(herb1);
        herbs.add(herb2);
        
        detail.groupHerb = herbs;
        
        browser.wnOutpatientWorkflow.prescribeHerbOrders("陈皮",null,detail);
        browser.wnOutpatientWorkflow.signOff(0);
    }

    @Test
    public void case_02() throws InterruptedException {
        init("Case02 - 针灸科流程 " , true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter(null, Data.TCM_subjCode);
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.SetRE022(Data.host, "0", "知识编辑器");
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.TCM_subjName);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.TCM_zj_symptom);
        browser.wnOutpatientWorkflow.checkEmrFragment("主诉", Data.TCM_zj_symptom);
        browser.wnOutpatientWorkflow.diagnosis(Data.TCM_zj_diagnose);
        browser.wnOutpatientWorkflow.checkEmrFragment("门诊诊断", Data.TCM_zj_diagnose);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.TCM_zj_treat1);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.TCM_zj_treat2);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.TCM_zj_treat3);
        browser.wnOutpatientWorkflow.signOff(0);
        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:"+cost_his+"/win60:"+cost_60+")");
        browser.logger.log(1, "收费对比通过:(his:"+cost_his+"/win60:"+cost_60+")");

    }
    
    @Before
    public void setUp() throws Exception {
        if(Data.useHybirdApp) {
            SdkSystemProcess.stopExeApp("chrome.exe");
            SdkSystemProcess.stopExeApp("Win60.exe");
            SdkSystemProcess.stopExeApp("Win6.0.exe");
        }
    }

}
