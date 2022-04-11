package com.winning.testsuite;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;



/*
 *  前置条件： 1、反算参数CL066,正算参数CL068
 *            2、维护主数据=物品域-药品,维护药品包装单位应用信息，应用类型设置为门诊，添加不同的包装单位及包装系数（复方丹参滴丸：按批次取整，复方紫草油：按顿取整）
 *            3、CL071设置为1启用
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PositiveReverseCalculationTest extends OutpatientTestBase {

    public PositiveReverseCalculationTest() {
        super();
    }

    static {
        Data.getScreenShot = true;
        SdkTools.initReport("正算反算专项", "正算反算专项.html");
    }
    
    
    /*
     * 步骤一 CL068=0 ，CL066=0  
     * 录入西成药之后，修改剂量、频次、天数后 ,修改剂量、频次、天数后，数量不会变化(需要手动更新数量)，修改数量后，天数也不会变化。
     */
    @Test
    public void case_01() throws InterruptedException {
        init("Case01 - 正算设置为0，反算设置为0 ", true);
        String medicineId = "57395971489261672";//复方丹参滴丸
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL066(Data.host, 0);
        browser.wnOutpatientWorkflow.SetCL068(Data.host, 0);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        ArrayList<String> patInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
        PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.Days = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 1;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.num = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.Days = 1;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
    }

    /*
     * CL068=1,CL066=0 按批次取整
     * 
     */
    @Test
    public void case_02() throws InterruptedException {
        init("Case02 - 正算设置为1，反算设置为0,按批次取整 ", true);
        String medicineId = "57395971489261672";//复方丹参滴丸
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL066(Data.host, 0);
        browser.wnOutpatientWorkflow.SetCL068(Data.host, 1);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        ArrayList<String> patInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
        //1.录入西成药 如：复方丹参滴丸 180粒/盒   规格为：27mg    按批次取整   ，录入剂量：30粒   频次 ：bid    天数是：5
        //A= 30*27  mg 、B=2 、C =5  
        //计算数量= （30*27*2*5）/(27*180)=1.67  （向上取整为2盒 ）
        PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.Days = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 2;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
        
        //调整剂量（或输入框编辑剂量）为60 粒， 数量变为4盒， 计算数量:(60*27*2*5)/(27*180)=3.34 (向上取整为4盒)
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.dose = 60;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 4;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
        
        //调整天数（或者输入输入框编辑天数）为8天 ，数量变为3盒，计算数量：（30*27*2*8）/(27*180)= 2.67 (向上取整为3盒)
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.dose = 30;
        prescribeDetail.Days = 8;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 3;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        //切换频次为：q2h ，数量，变为10盒 ，计算数量：（30*27*12*5）/(27*180)=10
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.freq = "Q2H";
        prescribeDetail.Days = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 10;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
    }

    /*
     * CL068=1,CL066=0 按顿取整
     * 
     */
    @Test
    public void case_03() throws InterruptedException {
        init("Case02 - 正算设置为1，反算设置为0，按顿取整 ", true);
        String medicineId = "57396572784683008";//复方紫草油

        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL066(Data.host, 0);
        browser.wnOutpatientWorkflow.SetCL068(Data.host, 1);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        ArrayList<String> patInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
        //录入西成药 ，如：复方紫草油 30ml/瓶    ，规则为：30ml  按顿取整  ，录入剂量：30ml ,频次 :BID ，天数1  总量是 4瓶
        //A=30   B=2 ,C=1    计算数量：每顿数量是30/30=1瓶   总量： (1*2*1 )  /1=2瓶；
        PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.Days = 1;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 2;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        //调整剂量（或者输入框编辑剂量）为31 ml,计算出数量为4瓶,每顿数量为：31/30=1.034 (向上取整为2)，总量:（2*2*1）/1=4瓶；
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.dose = 31;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 4;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        //调整天数（或者输入框编辑天数）为3天, 计算出数量是：6瓶,每顿数量为：1瓶  ，总量:（1*2*3）/1=6瓶
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.dose = 30;
        prescribeDetail.Days = 3;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 6;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        //切换频次为：q2h ，计算出数量为：12瓶, 每顿数量为：1瓶 ，总量  （1*12*1）/1=12瓶
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.freq = "Q2H";
        prescribeDetail.Days = 1;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.num = 12;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
    }

    
    /*
     * CL068=0,CL066=1 按批次取整
     * 
     */
    @Test
    public void case_04() throws InterruptedException {
        init("Case04- 正算设置为0，反算设置为1，按批次取整 ", true);
        String medicineId = "57395971489261672";//复方丹参滴丸

        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL066(Data.host, 1);
        browser.wnOutpatientWorkflow.SetCL068(Data.host, 0);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        ArrayList<String> patInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
        
        //复方丹参滴丸 180粒/盒  剂量：30粒， 频次：bid  , 5天,正算计算数量为:2盒 
        //修改数量为 4盒, 天数变成:12,天数: 4*180*27 /(30*27*2)=12天
        PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.num = 4;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.Days = 12;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
    }

    /*
     * CL068=0,CL066=1 按顿取整
     * 
     */
    @Test
    public void case_05() throws InterruptedException {
        init("Case05- 正算设置为0，反算设置为1，按顿取整 ", true);
        String medicineId = "57396572784683008";//复方紫草油

        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL066(Data.host, 1);
        browser.wnOutpatientWorkflow.SetCL068(Data.host, 0);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        ArrayList<String> patInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
        //复方紫草油 30ml/瓶   剂量:30ml  ,频次：bid,6瓶
        //天数变为:3天,天数计算:(6*1)\(1*2)=3天
        PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.freq = "BID";
        prescribeDetail.dose = 30;
        prescribeDetail.num = 6;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.Days = 3;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        // 剂量:31ml,频次：bid,2天,计算出数量为:8瓶,每顿数量=31/30=1.034瓶(向上取整为2瓶) 
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.dose = 31;
        prescribeDetail.num = 8;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.Days = 2;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);

        //修改数量为6瓶,天数变为:1天。天数：6/(2*2)=1.5  (向下取整为1天) 
        browser.wnOutpatientWorkflow.deleteOrders();
        prescribeDetail.num = 6;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
        prescribeDetail.Days = 1;
        browser.wnOutpatientWorkflow.checkPrescribeDetail(prescribeDetail);
    }

}