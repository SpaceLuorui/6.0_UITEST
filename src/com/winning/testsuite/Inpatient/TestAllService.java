package com.winning.testsuite.Inpatient;
import com.winning.testsuite.workflow.Inpatient.ServiceThread;
import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.WnDecouple;
import com.winning.user.winex.InpatientBrowser;
import com.winning.user.winex.InpatientBrowserManager;
import com.winning.user.winex.InpatientTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkStat;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全医嘱开立专项测试
 *
 * @author Administrator
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAllService extends InpatientTestBase {
    public static String reportTempFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "InpatientService.temp";
    public static String reportFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "住院全医嘱开立报告.html";
    public static Integer last_test_num = 0;
    public static InpatientBrowserManager brsManager = new InpatientBrowserManager();
    public static List<Map<String, String>> EmptyBedList =  new ArrayList<>();
    public static boolean paramSetFlag = true;
    public static String deptId =null;
    public static String wardId =null;
    public static String inpatRoomId =null;
    public static String loginEmployeeId =null;
    public static Map<String, String>  DoctorsAndNurses = new HashMap<String,String>();


    static{
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","Inpatient-25");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01() throws InterruptedException {
        // 开始测试前关闭所有浏览器和驱动
        SdkSystemProcess.stopExeApp("chrome.exe");
        SdkSystemProcess.stopExeApp("chromedriver_chrome.exe");
        // 获取需要测试的服务
        Wn60Db windb60 = new Wn60Db(SdkTools.logger);
        WnDecouple windbhis = new WnDecouple(SdkTools.logger);
        List<Map<String, String>> allServiceList = null;
        allServiceList = windb60.getServiceList(reportTempFile, Data.retryFlag, Data.notRetryFlag);

        deptId = windb60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
        wardId = windb60.getOrgIdByOrgName(Data.inpatient_select_ward,Data.inpatient_select_ward_code);
        inpatRoomId= windb60.getInpatRoomIdByRoomName(Data.InpatRoomName,Data.inpatient_select_ward,Data.inpatient_select_ward_code);
        loginEmployeeId =windb60.getEmployeeIdByEmployeeNo(Data.default_user_login_account);

        //入区所需的医生和护士
        DoctorsAndNurses.put("responsibleDoctor",windb60.getEmployeeIdByEmployeeNo(Data.responsibleDoctor));
        DoctorsAndNurses.put("responsibleNurse",windb60.getEmployeeIdByEmployeeNo(Data.responsibleNurse));
        DoctorsAndNurses.put("attendingDoctor",windb60.getEmployeeIdByEmployeeNo(Data.attendingDoctor));
        DoctorsAndNurses.put("responsibleDoctor",windb60.getEmployeeIdByEmployeeNo(Data.responsibleDoctor));
        DoctorsAndNurses.put("headDoctor",windb60.getEmployeeIdByEmployeeNo(Data.headDoctor));

        windb60.disconnect();

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
                InpatientBrowser browser = brsManager.getFreeBrowser("主线程:" + service.get("NAME") + " :");
                brsManager.getEmptyBed(browser,EmptyBedList);
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