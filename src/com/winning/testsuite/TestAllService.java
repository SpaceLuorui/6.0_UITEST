package com.winning.testsuite;

import java.util.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Outpatient.ServiceThread;
import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientBrowserManager;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkStat;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

/**
 * 专项测试，西药全量测试
 *
 * @author Administrator
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAllService extends OutpatientTestBase {
    public static String reportTempFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "service.temp";
    public static String reportFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "全医嘱开立报告.html";
    public static List<String> sucIdmList = null;
    public static Integer last_test_num = 0;
    public static OutpatientBrowserManager brsManager = new OutpatientBrowserManager();


    @Test
    public void test_01() throws InterruptedException {
        // 开始测试前关闭所有浏览器和驱动
        SdkSystemProcess.stopExeApp("chrome.exe");
        SdkSystemProcess.stopExeApp("chromedriver_chrome.exe");
        // 获取需要测试的服务
        Wn60Db windb60 = new Wn60Db(SdkTools.logger);
        WnDecouple windbhis = new WnDecouple(SdkTools.logger);
//        WnOutpatientWorkflow.checkParamsForTestAllService();
        List<Map<String, String>> allServiceList = null;

        if (Data.hisType == "TJWN") {
            allServiceList = windbhis.getServiceList(reportTempFile, Data.retryFlag, Data.notRetryFlag);
            windbhis.disconnect();
        } else {
            allServiceList = windb60.getServiceList(reportTempFile, Data.retryFlag, Data.notRetryFlag);
            windb60.disconnect();
        }
        // 启动线程监控浏览器数量
        brsManager.initBrowsers(Data.threadNum);
        //循环测试取到的service
        for (int i = last_test_num; i < allServiceList.size(); i++) {
            // 记录当前测试进度,retry时有用
            last_test_num = i;
            try {
                // 获取下一个测试项目
                Map<String, String> service = allServiceList.get(i);
                SdkTools.logger.log("(" + last_test_num + "/" + allServiceList.size() + "): " + service);
                OutpatientBrowser browser = brsManager.getFreeBrowser("主线程:" + service.get("NAME") + " :");
                new ServiceThread(service, browser).start();
                SdkStat.StatBuild("正在执行第 " + i + "/" + allServiceList.size() + " 条医嘱测试，名称:" + service.get("NAME"));
                if (i % 5 == 0) {
                    SdkTools.makeReport(reportTempFile, reportFile);
                }
                SdkTools.sleep(1000);
            } catch (Throwable e) {
                SdkTools.logger.log(i + "主线程报错：" + e.getMessage());
                i--;
                continue;
            }
        }
        while (ServiceThread.threadNum != 0) {
            SdkTools.logger.log("threadNum:" + ServiceThread.threadNum);
            SdkTools.sleep(3000);
        }
        Data.testOver = true;
        SdkTools.logger.log("整理开始" + System.currentTimeMillis());
        SdkTools.makeReport(reportTempFile, reportFile);
        SdkTools.logger.log("整理完成" + System.currentTimeMillis());
        SdkTools.logger.log("主线程结束");
    }
}