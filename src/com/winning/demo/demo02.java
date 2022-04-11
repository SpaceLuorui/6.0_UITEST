package com.winning.demo;
//患者建档
import com.winning.user.winex.WINEXTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class demo02 extends WINEXTestBase {
    public demo02 (){
        super();
    }
    static{
        SdkTools.initReport("HIS", "demo02.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test01(){
        Data.headless = true;
        init("患者建档",true);
        browser.wnwd.openUrl("http://" + Data.his_host + "/portal/#/login?W-APP=his");
        browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account,Data.default_user_login_pwd);
        //患者建档
        loginMenuByName("患者建档");
        ArrayList<String> patInfo = new ArrayList<>();
        browser.wnwd.waitElementByXpathAndClick("保险信息选择下拉框","//input[contains(@placeholder,'请选择保险')]",Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("保险信息：自费","//span[.='自费']",Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("证件类型选择下拉框","//input[contains(@placeholder,'请选择证件')]",Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("居民身份证","//span[contains(text(),'居民身份证')]",Framework.defaultTimeoutMax);
        String IDCode = SdkTools.IdCardGenerator.IDCardCreate();
        browser.wnwd.waitElementByXpathAndInput("身份证号码填写框", "//input[@placeholder='请输入证件号']", IDCode, Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("民族选择下拉框","//input[contains(@placeholder,'请选择民族')]",Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("民族信息：瑶族","//span[.='瑶族']",Framework.defaultTimeoutMax);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //定义随机患者名字
        String patientName = "宇航员" + dateFormat.format(date);
        //定义手机号码
        int phoneNum = (int) ((Math.random() * 9 + 1) * 10000000);
        //填写姓名电话
        browser.wnwd.waitElementByXpathAndInput("填写姓名框","//input[@placeholder='请输入姓名']",patientName,Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndInput("联系电话填写框","//label[contains(text(),'联系电话')]/following-sibling::*//input","188" + phoneNum,Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("国籍选择下拉框","//input[contains(@placeholder,'请选择国籍')]",Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("国籍：中国","//span[.='中国']",Framework.defaultTimeoutMax);
        WebElement webElement = browser.wnwd.waitElementByXpath("交易金额框","//input[@placeholder='请填写充值金额']",Framework.defaultTimeoutMax);
        if (webElement != null){
            browser.wnwd.moveToElementByXpath("鼠标移动到清空金额框","//input[@placeholder='请填写充值金额']",Framework.defaultTimeoutMax);
            browser.wnwd.waitElementByXpathAndClick("清空金额","//input[@placeholder='请填写充值金额']/following-sibling::*//i",Framework.defaultTimeoutMax);
        }
        browser.wnwd.waitElementByXpathAndClick("保存档案按钮","//span[contains(text(),'保存档案')]",Framework.defaultTimeoutMax);
        browser.wnwd.checkElementByXpath("建档成功提示","//p[contains(text(),'档案保存成功')]",Framework.defaultTimeoutMax);
        patInfo.add(patientName);
        System.out.println(patInfo.get(0));

    }
    @Test
    public void test02(){
        Data.headless = false;
        init("患者建档", true);
        //打开系统地址
        browser.wnwd.openUrl("http://"+ Data.his_host +"/portal/#/login?W-APP=his");
        //登录系统
        browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //患者建档
        ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
        //挂号登记
        browser.wnWINEXWorkflow.encounter(patInfo.get(0),Data.test_select_subject,Data.default_user_login_account);
    }

    //进入指定模块
    public void loginMenuByName(String menuName) {
        browser.wnwd.waitElementByXpathAndClick("菜单展开按钮", "//span[@class='text']//i", Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndInput("菜单搜索框", "//div[@class='search']//input", menuName, Framework.defaultTimeoutMax);
        WebElement el = browser.wnwd.waitElementByXpath(menuName+"入口", "//span[@class='child-menu-name' and contains(text(),'"+menuName+"')]", Framework.defaultTimeoutMax);
        if(el!=null) {
            browser.wnwd.wnDoubleClickElementByMouse(el, "双击"+menuName);
        }else {
            throw new Error("未找到"+menuName+"菜单");
        }
        browser.wnwd.checkElementByXpath(menuName+"已打开", "//div[@class='nav-item']//div[contains(text(),'"+menuName+"')]", Framework.defaultTimeoutMax);
    }
}
