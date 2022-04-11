package com.winning.testsuite.workflow.Outpatient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.winning.testsuite.TestAllService;
import com.winning.user.winex.OutpatientBrowser;

import ui.sdk.base.Browser;
import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

public class ServiceThread extends Thread {
    public static Integer threadNum = 0;
    public Map<String, String> service = null;
    public OutpatientBrowser br = null;

    public ServiceThread(Map<String, String> service, OutpatientBrowser br) {
        super();
        br.inUse = true;
        this.service = service;
        this.br = br;
    }

    @Override
    public void run() {
        threadNum++;
        try {
            testServiceInThread(service, br, 0);
        }
        catch (Throwable e) {
            e.printStackTrace();
            SdkTools.logger.log("" + e.getStackTrace());
            String finalMsg = br.name + "线程异常:" + service + "  msg:" + e.getMessage();
            SdkTools.logger.log(finalMsg);
            if (br != null && TestAllService.brsManager.browsers.contains(br)) {
                TestAllService.brsManager.quitBrowser(br);
            }
        }
        finally {
            if (br != null) {
                br.inUse = false;
            }
            threadNum--;
        }
    }


    // 全医嘱开立
    public void testServiceInThread(Map<String, String> service, OutpatientBrowser br, Integer retryCount) {
//    	retryCount=10;
        service.put("PRICE_60", "");
        service.put("PRICE_HIS", "");
        service.put("RESULT", "");
        service.put("DETAIL", "");
        service.put("START", "" + System.currentTimeMillis());
        if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
            service.put("TITLE", service.get("NAME") + "[规格:" + service.get("PACKAGE") + "]");
        }
        else {
            service.put("TITLE", service.get("NAME"));
        }
        br.inUse = true;
        br.logger.resetPath(service.get("ID") + service.get("NAME") + ".html");
        service.put("FILENAME", br.logger.fileName);
        br.logger.log(1, "测试服务: " + service);
        br.logger.log(1, br.name
                + "\n开始时间" + new SimpleDateFormat("_yyyyMMdd_HH_mm_ss_SSS", Locale.getDefault()).format(new Date(Long.valueOf(service.get("START"))))
                + "\n重新登录:" + br.needLogin
                + "\n重新叫号:" + br.needReCall
                + "\n患者姓名:" + br.patientName
                + "\n重试次数:" + retryCount
                + "\nID:" + service.get("ID")
                + "\n类型:" + service.get("TYPE")
                + "\n名称:" + service.get("TITLE")
        );
        retryCount++;
        // 打开浏览器并登录
        if (br.needLogin) {
            try {
                br.logger.log(1, "开始登录");
                br.wnwd.openUrl(Data.web_url);
                br.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
                br.wnOutpatientWorkflow.setParamsForTestAllService();

                br.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
                br.wnOutpatientWorkflow.skip();
                br.needLogin = false;
            }
            catch (Throwable e) {
                e.printStackTrace();
                SdkTools.logger.log("登录失败:" + e.getStackTrace());
                service.put("ERRMSG", "登录失败:" + e.getMessage());
                br.logger.boxLog(2, "登录失败", service.get("ERRMSG"));
                br.wnwd.getScreenShot("登录失败");
                //重启浏览器重新测试这一条
                TestAllService.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, 0);
                return;
            }
        }

        if (br.needReCall) {
            // 挂号
            try {
                ArrayList<String> encounterInfo = br.decouple.newEncounter();
                br.patientName = encounterInfo.get(0);// 用于挂号检索
                br.visitNumber = encounterInfo.get(1);// 用于收费
                br.ghhx = encounterInfo.get(3);
            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "挂号失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                //两分钟后使用同一个浏览器重新测试本条
                br.wnwd.sleep(120000);
                testServiceInThread(service, br, 0);
                br.inUse = false;
                return;
            }
            // 叫号
            try {
                br.logger.log(1, br.name + " 开始叫号");
                if(Data.hisType.equals("QTWN")){
                    br.wnOutpatientWorkflow.callNumberByName(br.patientName);
                }else{
                    br.wnOutpatientWorkflow.callNumberByNo(br.patientName, br.ghhx);
                }

            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "叫号失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                br.wnwd.getScreenShot("叫号失败");
                //重启浏览器重新测试这一条
                TestAllService.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, 0);
                return;
            }
        }

        // 搜索医嘱(如果失败,下一条不重新叫号)
        String csType = null;
        try {
            br.wnOutpatientWorkflow.unselectSearchStock();
            List<String> searchFlag = new ArrayList<String>();
            searchFlag.add(service.get("NAME"));
            if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
                searchFlag.add(service.get("PACKAGE"));
            }
            br.wnOutpatientWorkflow.searchOrder(service.get("NAME"), searchFlag);
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = false;
            service.put("ERRMSG", "搜索医嘱失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("搜索医嘱失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                TestAllService.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, retryCount);
            }
            else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService.reportTempFile, service);
                br.inUse = false;
            }
            return;
        }

        // 获取加工厂类型-编辑医嘱开立-签署-收费-对比价格(如果失败,下一条重新叫号)
        try {
//			// 选择医嘱
//			br.wnwd.wnClickElement(searchItem, "选择医嘱:"+searchItem.getText());		
            // 判断加工厂类型
            br.wnOutpatientWorkflow.addDiagnoseIfNeed();
            br.wnOutpatientWorkflow.pishiIfNeed();
            csType = br.wnOutpatientWorkflow.getDetailType();
            if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
                br.logger.assertFalse(!csType.equals("herb") && !csType.equals("drug"), "加工厂类型不对", "预期类型:" + service.get("TYPE") + "\n加工厂实际类型" + csType);
                service.put("TYPE", csType);
                // 集团卫宁检查加工厂默认值
                if (Data.checkFactory) {
                    br.wnOutpatientWorkflow.getDisposalFactoryDefault(service);
                    br.decouple.db60.getDisposalFactoryDefaultFromDb(service);
                    br.wnOutpatientWorkflow.CheckDisposalFactoryDefault(csType, service);
                }
            }
            br.wnOutpatientWorkflow.editAndCommitOrder(null);
            br.wnOutpatientWorkflow.own_expense();
            br.wnOutpatientWorkflow.signOff(0);


            if (Data.getEncounterFromFile) {
                //TODO 暂时未实现收费
                service.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
                service.put("PRICE_HIS", "0");
            }
            if(Data.hisType.equals("WINEX")||Data.hisType.equals("QTWN")) {
            	service.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
                service.put("PRICE_HIS", "0");
            	br.logger.boxLog(0, "暂不执行收费比对", "");
            }
            else {
                service.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
                service.put("PRICE_HIS", br.decouple.win60MedicineSF(br.patientName).get(0));
                // 价格对比不通过
                if (!SdkTools.compareMoney(service.get("PRICE_60"), service.get("PRICE_HIS"))) {
                    throw new Error("价格对比不通过");
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("开立签署或对比价格失败");
            if (service.get("ERRMSG").contains("等待诊断选择框不可见") || service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                TestAllService.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, retryCount);
            }
            else if ((service.get("ERRMSG").contains("His收费失败") && !(service.get("ERRMSG").contains("未落库"))) || service.get("ERRMSG").contains("His退费失败")) {
                if (retryCount < 5) {
                    //重新叫号重新测试本条
                    testServiceInThread(service, br, retryCount);
                }
                else {
                    //写入测试报告
                    service.put("RESULT", "不通过");
                    service.put("CLASS", "TABLEID failCase");
                    SdkTools.saveServiceToTempFile(TestAllService.reportTempFile, service);
                    br.inUse = false;
                }
            }
            else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService.reportTempFile, service);
                br.inUse = false;
            }
            br.inUse = false;
            return;
        }

        // 测试通过
        SdkTools.logger.log(1, "测试通过!");
        br.logger.log(1, "测试通过!");
        br.wnwd.getScreenShot("测试通过");
        service.put("RESULT", "通过");
        service.put("CLASS", "TABLEID passCase");
        service.put("ERRMSG", "");
        SdkTools.saveServiceToTempFile(TestAllService.reportTempFile, service);
        br.needReCall = true;
        br.inUse = false;
    }
}