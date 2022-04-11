package com.winning.testsuite.workflow.Outpatient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.winning.testsuite.TestHistory;
import com.winning.testsuite.workflow.WnDecouple;
import com.winning.user.winex.OutpatientBrowser;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;


public class HistoryThread extends Thread {
    public static Integer threadNum = 0;
    public Map<String, String> history = null;
    public OutpatientBrowser br = null;

    public HistoryThread(Map<String, String> history, OutpatientBrowser br) {
        super();
        br.inUse = true;
        this.history = history;
        this.br = br;
    }

    @Override
    public void run() {
        threadNum++;
        try {
            testHistoryInThread(history, br, 0);
        }
        catch (Throwable e) {
            String finalMsg = br.name + "线程异常:" + history + "  msg:" + e.getMessage();
            SdkTools.logger.log(finalMsg);
            if (br != null && TestHistory.brsManager.browsers.contains(br)) {
                TestHistory.brsManager.quitBrowser(br);
            }
        }
        finally {
            br.inUse = false;
            threadNum--;
        }
    }


    // 历史处置开立
    public void testHistoryInThread(Map<String, String> history, OutpatientBrowser br, Integer retryCount) {
        retryCount++;
        WnDecouple decouple = new WnDecouple(br.logger);
        history.put("PRICE_60", "");
        history.put("PRICE_HIS", "");
        history.put("RESULT", "不通过");
//    	history.put("ERRMSG", "");
        history.put("DETAIL", "");
        history.put("CLASS", "Case");
        history.put("START", "" + System.currentTimeMillis());


        br.logger.resetPath(history.get("NAME") + "_" + history.get("GHXH") + ".html");
        br.logger.log(1, br.name + "\n开始测试" + history + "\n重新登录" + br.needLogin);
        history.put("FILENAME", br.logger.fileName);


        // 获取一个带历史处置的患者并挂号到60
        try {
            ArrayList<String> encounterInfo = br.decouple.newEncounter(history.get("PATID"), Data.newEncounterSubjectCode);
            history.put("NAME", encounterInfo.get(0));
            history.put("GHXH_NEW", encounterInfo.get(1));
            history.put("GHHX", encounterInfo.get(3));
        }
        catch (Throwable e) {
            history.put("ERRMSG", "挂号失败:" + e.getMessage());
            SdkTools.logger.log(2, history.get("ERRMSG"));
            br.logger.log(2, history.get("ERRMSG"));
            testHistoryInThread(history, br, retryCount);
//    		if (retryCount<5 && !history.get("ERRMSG").contains("usp_gh_jo_interface")) {
//    			// 重试
//    			testHistoryInThread(history, br, retryCount);
//    		}else {
//    			//重试超过3次,写入报告
//    			history.put("RESULT", "挂号异常");
//    			history.put("CLASS", "TABLEID failCase");
//    			Public.saveServiceToTempFile(TestHistory.historyTemp,history);
//    		}
            SdkTools.sleep(1000);
            return;
        }


        // 打开医生站
        if (br.needLogin) {
            try {
                br.wnwd.openUrl(Data.web_url);
                br.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
                br.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
                br.wnOutpatientWorkflow.skip();
                br.needLogin = false;
            }
            catch (Throwable e) {
                history.put("ERRMSG", "登录失败:" + e.getMessage());
                SdkTools.logger.log(2, history.get("ERRMSG"));
                br.logger.log(2, history.get("ERRMSG"));
                //重启浏览器重新测试这一条
                TestHistory.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestHistory.brsManager.getFreeBrowser(history.get("NAME"));
                testHistoryInThread(history, newbrowser, 0);
                return;
            }
        }


        // 叫号
        try {
            br.logger.log(1, br.name + " 开始叫号");
            br.wnOutpatientWorkflow.callNumberByNo(history.get("NAME"), history.get("GHHX"));
        }
        catch (Throwable e) {
            history.put("ERRMSG", "叫号失败:" + e.getMessage());
            SdkTools.logger.log(2, history.get("ERRMSG"));
            br.logger.log(2, history.get("ERRMSG"));
            TestHistory.brsManager.quitBrowser(br);
            OutpatientBrowser newbrowser = TestHistory.brsManager.getFreeBrowser(history.get("NAME"));
            testHistoryInThread(history, newbrowser, retryCount);
//			if (history.get("ERRMSG").contains("element is not attached") || history.get("ERRMSG").contains("WebDriver")) {
//				//重启浏览器重新测试这一条			
//				TestHistory.brsManager.quitBrowser(br);
//				Browser newbrowser = TestHistory.brsManager.getFreeBrowser(history.get("NAME"));
//				testHistoryInThread(history,newbrowser,retryCount);
//			}else {
//				//重试超过3次,写入报告
//				history.put("RESULT", "叫号异常");
//				history.put("CLASS", "TABLEID failCase");
//				Public.saveServiceToTempFile(TestHistory.historyTemp,history);
//				//退出浏览器
//				TestHistory.brsManager.quitBrowser(br);
//			}
            return;
        }

        List<String> orderList = null;
        Integer clinicalOrderCount = null;
        try {
            //进入历史处置
            br.wnOutpatientWorkflow.enterHistory();
            //根据起止日期和是否本科室搜索历史处置
//			br.helper.searchHistory("2020-01-01",encounterInfo.get(3),true);
            //进入指定日期历史处置
            br.wnOutpatientWorkflow.selectHistoryByDate(history.get("DATE"));
            //判断医嘱数量
            orderList = br.wnOutpatientWorkflow.getHistoryOrderNames();
            //检查异常历史处置医嘱
            br.wnOutpatientWorkflow.checkHistoryOrders();
            //引用-检查异常医嘱
            br.wnOutpatientWorkflow.quoteAllHistory();
            //签署
            br.wnOutpatientWorkflow.signOff(0);
            //获取已签署数量
            clinicalOrderCount = br.wnOutpatientWorkflow.getClinicalOrderCount();
            // 获取60价格
            history.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
            // 获取his收费
            history.put("PRICE_HIS", decouple.win60MedicineSF(history.get("NAME")).get(0));
            //价格对比不通过
            if (!SdkTools.compareMoney(history.get("PRICE_60"), history.get("PRICE_HIS"))) {
                throw new Error("价格对比不通过:60显示价格与His收费(" + history.get("PRICE_60") + "/" + history.get("PRICE_HIS") + ")");
            }
            if (clinicalOrderCount != orderList.size()) {
                throw new Error("历史处置条数和引用成功条数不一致(历史处置:" + orderList.size() + " 条/引用成功:" + clinicalOrderCount + " 条)!");
            }
            if (!decouple.checkExecuteSubject(history.get("GHXH"), history.get("GHXH_NEW"))) {
                throw new Error("执行科室不一致!");
            }
        }
        catch (Throwable e) {
            history.put("ERRMSG", e.getMessage());
            SdkTools.logger.log(2, history.get("ERRMSG"));
            br.logger.log(2, history.get("ERRMSG"));
            if (history.get("ERRMSG").contains("element is not attached") || history.get("ERRMSG").contains("WebDriver")) {
                //重启浏览器重新测试这一条
                TestHistory.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestHistory.brsManager.getFreeBrowser(history.get("NAME"));
                testHistoryInThread(history, newbrowser, retryCount);
            }
            else {
                history.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestHistory.reportFile, history);
            }
            return;
        }


        // 测试通过
        br.logger.log(1, "测试通过!");
        history.put("ERRMSG", "");
        history.put("RESULT", "通过");
        history.put("CLASS", "TABLEID passCase");
        SdkTools.saveServiceToTempFile(TestHistory.reportFile, history);
        br.inUse = false;
    }
}