package com.winning.testsuite.workflow.Inpatient;

import com.winning.testsuite.Inpatient.TestAllService_new;
import com.winning.user.winex.InpatientBrowser;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkTools;

public class ServiceThread_new extends Thread {
    public static Integer threadNum = 0;
    public Map<String, String> service = null;
    public InpatientBrowser br = null;
    public ServiceThread_new(Map<String, String> service, InpatientBrowser br) {
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
            if (br != null && TestAllService_new.brsManager.browsers.contains(br)) {
                TestAllService_new.brsManager.quitBrowser(br);
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
    public void testServiceInThread(Map<String, String> service, InpatientBrowser br, Integer retryCount) throws UnsupportedEncodingException {
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
                + "\n空床编号:" + br.emptyBed.get("BED_NO")
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
                br.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
                br.needLogin = false;
            }
            catch (Throwable e) {
                e.printStackTrace();
                SdkTools.logger.log("登录失败:" + e.getStackTrace());
                service.put("ERRMSG", "登录失败:" + e.getMessage());
                br.logger.boxLog(2, "登录失败", service.get("ERRMSG"));
                br.wnwd.getScreenShot("登录失败");
                //重启浏览器重新测试这一条
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, 0);
                return;
            }
        }
        //设置参数
        if(TestAllService_new.paramSetFlag){
            br.wnInpatientWorkflow.setParamsForTestAllService();
            TestAllService_new.paramSetFlag = false;
        }

        if (br.needReCall) {
            // HIS入院登记，同步到60
            try {
                ArrayList<String> encounterInfo = br.wnInpatientWorkflow.hisAdmissionRegistration();
                br.patientName = encounterInfo.get(0);// 用于挂号检索
                br.encounterId = encounterInfo.get(1);// 用于办理入区
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

            //入区
            try {
                //浏览器未获取到空床位，则先添加加床床位
                if(br.emptyBed.get("BED_NO") == null){
                        String bedNo = br.wnInpatientWorkflow.getEmptyBedNo();
                        br.wnInpatientWorkflow.addBedByInterface(TestAllService_new.wardId,TestAllService_new.inpatRoomId,bedNo);
                        String InpatBedServiceId = br.wnInpatientWorkflow.queryBedInfo(TestAllService_new.wardId,bedNo);
                        br.emptyBed.put("BED_NO",bedNo);
                        br.emptyBed.put("INPAT_BED_SERVICE_ID",InpatBedServiceId);
                        br.emptyBed.put("INPAT_ROOM_ID",TestAllService_new.inpatRoomId);
                        br.emptyBed.put("BED_TYPE_CODE","959852");//床位类型为加床床位

                }
                br.wnInpatientWorkflow.enterWardByInterface(br.encounterId,TestAllService_new.deptId,TestAllService_new.DoctorsAndNurses,br.emptyBed);
            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "入区失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                //重启浏览器重新测试这一条
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, 0);
                return;
            }

            // 叫号
            try {
                br.logger.log(1, br.name + " 开始叫号");
                br.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject,Data.inpatient_select_ward);
                br.wnInpatientWorkflow.callNumberByName(br.patientName);
            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "叫号失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                br.wnwd.getScreenShot("叫号失败");
                //重启浏览器重新测试这一条
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, 0);
                return;
            }

        }



        // 搜索医嘱(如果失败,重启浏览器，不重新入院登记、入区)
        String csType = null;
        try {
            List<String> searchFlag = new ArrayList<String>();
            searchFlag.add(service.get("NAME"));
            if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
                searchFlag.add(service.get("PACKAGE"));
            }
            System.out.println("searchFlag:"+searchFlag.toString());
            br.wnInpatientWorkflow.searchOrder(service.get("NAME"), searchFlag);
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = false;
            service.put("ERRMSG", "搜索医嘱失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("搜索医嘱失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
                newbrowser.needReCall = false;
                newbrowser.patientName = br.patientName;
                testServiceInThread(service, newbrowser, retryCount);
            }
            else {
                //写入测试报告
                System.out.println("1234456768556756756767");
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.inUse = false;
            }
            return;
        }

        // 获取加工厂类型-编辑医嘱开立-签署-收费-对比价格(如果失败,下一条重新叫号)
        try {
            //皮试药品点皮试确认
            if(service.get("TYPE").equals("药品") && service.get("SKINTESTFLAG").equals("98175") ){
                br.wnInpatientWorkflow.skinTestIsNeed();
            }
            csType = br.wnInpatientWorkflow.getDetailType();
            if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
                br.logger.assertFalse(!csType.equals("herb") && !csType.equals("drug"), "加工厂类型不对", "预期类型:" + service.get("TYPE") + "\n加工厂实际类型" + csType);
                service.put("TYPE", csType);
                // 集团卫宁检查加工厂默认值
                if (Data.checkFactory) {
                    br.wnInpatientWorkflow.getDisposalFactoryDefault(service);
                    br.decouple.db60.getInpatDisposalFactoryDefaultFromDb(service);
                    br.wnInpatientWorkflow.CheckDisposalFactoryDefault(csType, service);
                }
            }
            br.wnInpatientWorkflow.editAndCommitOrder(service.get("name"),null,null);
            br.wnInpatientWorkflow.signOff(0);
            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);

        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("开立签署失败");
            if (service.get("ERRMSG").contains("等待诊断选择框不可见") || service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, retryCount);
            }
//            else if ((service.get("ERRMSG").contains("His收费失败") && !(service.get("ERRMSG").contains("未落库"))) || service.get("ERRMSG").contains("His退费失败")) {
//                if (retryCount < 5) {
//                    //重新叫号重新测试本条
//                    testServiceInThread(service, br, retryCount);
//                }
//                else {
//                    //写入测试报告
//                    service.put("RESULT", "不通过");
//                    service.put("CLASS", "TABLEID failCase");
//                    SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
//                    br.inUse = false;
//                }
//            }
            else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                br.inUse = false;
                br.emptyBed = null;
                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
            }
            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
            return;
        }

        //皮试用医嘱签收、申请、执行以及皮试管理
        if(service.get("TYPE").equals("药品") && service.get("SKINTESTFLAG").equals("98175") ) {
            try {
                br.logger.log(1, br.name + " 护士站皮试用医嘱签收、申请、执行以及皮试管理开始");
                br.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account, Data.inpatient_select_ward);
                br.wnInpatientWorkflow.callNumberByName(br.patientName);
                br.wnInpatientWorkflow.SignForSkinTestOrder();
                br.wnInpatientWorkflow.orderApply();
                br.wnInpatientWorkflow.orderApplication();
                br.wnInpatientWorkflow.startSkinTest();
                br.wnInpatientWorkflow.finishSkinTest("-");
                br.wnInpatientWorkflow.auditSkinTest();
            } catch (Throwable e) {
                e.printStackTrace();
                br.needReCall = true;
                service.put("ERRMSG", "皮试管理失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                br.wnwd.getScreenShot("皮试管理失败");
                if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                    TestAllService_new.brsManager.quitBrowser(br);
                    InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                    TestAllService_new.brsManager.getEmptyBed(newbrowser, TestAllService_new.EmptyBedList);
                    testServiceInThread(service, newbrowser, retryCount);
                } else {
                    //写入测试报告
                    service.put("RESULT", "不通过");
                    service.put("CLASS", "TABLEID failCase");
                    SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                    br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                    br.inUse = false;
                    br.emptyBed = null;
                    TestAllService_new.brsManager.getEmptyBed(br, TestAllService_new.EmptyBedList);
                }
                br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
                return;
            }
        }

        try {
            br.logger.log(1, br.name + " 护士站签收开始");
            br.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
            br.wnInpatientWorkflow.callNumberByName(br.patientName);
            br.wnInpatientWorkflow.signFor();
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", "签收失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("签收失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, retryCount);
            } else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                br.inUse = false;
                br.emptyBed = null;
                TestAllService_new.brsManager.getEmptyBed(br, TestAllService_new.EmptyBedList);
            }
            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
            return;
        }
        try {
            br.logger.log(1, br.name + " 护士站申请开始");
            br.wnInpatientWorkflow.orderApply();
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", "申请失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("申请失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
//                TestAllService_new.brsManager.getEmptyBedNo(newbrowser, TestAllService_new.EmptyBedNoList);
                TestAllService_new.brsManager.getEmptyBed(newbrowser, TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, retryCount);
            } else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                br.inUse = false;
//                br.emptyBedNo = null;
//                TestAllService_new.brsManager.getEmptyBedNo(br,TestAllService_new.EmptyBedNoList);
                br.emptyBed = null;
                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
            }
            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
            return;
        }

        try {
            br.logger.log(1, br.name + " 护士站执行开始");
            br.wnInpatientWorkflow.orderApplication();
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", "执行失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("执行失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(newbrowser, TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, retryCount);
            } else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
                br.inUse = false;
                br.emptyBed = null;
                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
            }
            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
            return;
        }
        
        try {
        	service.put("PRICE_HIS", br.decouple.getZjeByIdm(service.get("MED_EXT_REF_ID")));
        	br.wnwd.openUrl("http://172.16.7.26/portal/#/login?W-APP=his");
        	br.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
    		Map<String, String> pharmacyInfo = br.wnWINEXWorkflow.getPharmacyInfoByPatientName(br.patientName);
    		String pharmacyNo = pharmacyInfo.get("ORG_NO");
    		String pharmacyName = pharmacyInfo.get("ORG_NAME");
    		String pharmacyId = br.decouple.db60.getOrgIdByOrgName(pharmacyName, pharmacyNo);

        	br.wnWINEXWorkflow.updateReviewerStatus(pharmacyId, pharmacyName, pharmacyNo, "98176", "98176");

            if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("herb") || service.get("TYPE").equals("drug")) {
            	br.wnWINEXWorkflow.InpatientDispensing(pharmacyName,br.patientName);
            	String price_60 = br.wnWINEXWorkflow.inpatientCharges(br.patientName);
            	service.put("PRICE_60", price_60);
            }else {
            	String price_60 = br.wnWINEXWorkflow.inpatientCharges(br.patientName);
            	service.put("PRICE_60", price_60);
            }
            br.wnWINEXWorkflow.outhospitalByInterface(br.wnWINEXWorkflow.confusionLoginForWINEX(Data.inpatient_host,Data.default_user_login_account,Data.default_user_login_pwd),TestAllService_new.loginEmployeeId,br.patientName);
            br.needLogin = true;
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", "大his系统收费失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("大his系统收费失败");
            br.wnWINEXWorkflow.outhospitalByInterface(br.wnWINEXWorkflow.confusionLoginForWINEX(Data.inpatient_host,Data.default_user_login_account,Data.default_user_login_pwd),TestAllService_new.loginEmployeeId,br.patientName);
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
                TestAllService_new.brsManager.quitBrowser(br);
                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
                testServiceInThread(service, newbrowser, retryCount);
            } else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.inUse = false;
                br.emptyBed = null;
                TestAllService_new.brsManager.getEmptyBed(br, TestAllService_new.EmptyBedList);
            }
            br.needLogin = true;
            return;
        }
        
        
        

//        // 出院出区
//        try {
//            br.logger.log(1, "开始出院出区");
//            br.wnInpatientWorkflow.outHospital();
//        }
//        catch (Throwable e){
//            e.printStackTrace();
//            br.needReCall = true;
//            service.put("ERRMSG", "出院出区失败:" + e.getMessage());
//            SdkTools.logger.log(2, service.get("ERRMSG"));
//            br.logger.log(2, service.get("ERRMSG"));
//            br.wnwd.getScreenShot("出院出区失败");
//            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
//                TestAllService_new.brsManager.quitBrowser(br);
//                InpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
//                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
//                TestAllService_new.brsManager.getEmptyBed(newbrowser,TestAllService_new.EmptyBedList);
//                testServiceInThread(service, newbrowser, retryCount);
//            }
//            else {
//                //写入测试报告
//                service.put("RESULT", "不通过");
//                service.put("CLASS", "TABLEID failCase");
//                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
//                br.wnInpatientWorkflow.outhospitalByInterface(TestAllService_new.loginEmployeeId,br.patientName);
//                br.inUse = false;
//                br.emptyBed = null;
//                TestAllService_new.brsManager.getEmptyBed(br,TestAllService_new.EmptyBedList);
//            }
//            br.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
//            return;
//        }
        


        // 测试通过
        SdkTools.logger.log(1, "测试通过!");
        br.logger.log(1, "测试通过!");
        br.wnwd.getScreenShot("测试通过");
        service.put("RESULT", "通过");
        service.put("CLASS", "TABLEID passCase");
        service.put("ERRMSG", "");
        SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
        br.needReCall = true;
        br.inUse = false;
        TestAllService_new.EmptyBedList.add(br.emptyBed);
        br.emptyBed = null;


    }
}