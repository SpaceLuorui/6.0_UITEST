package com.winning.demo;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.user.winex.WINEXTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//打开浏览器、登录系统
public class demo01 extends WINEXTestBase {
    public demo01(){
        super();
    }
    static{
        SdkTools.initReport("HIS", "demo01.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test01(){
         /*
        Headerless Browser（无头的浏览器）是浏览器的无界面状态，可以在不打开浏览器GUI的情况下，使用浏览器支持的性能。
        Chrome Headless相比于其他的浏览器，可以更便捷的运行web自动化，编写爬虫、截图等。通常是由编程或者命令行来控制的。
        好处：
        可以加快UI自动化测试的执行时间，对于UI自动化测试，少了真实浏览器加载css，js以及渲染页面的工作。无头测试要比真实浏览器快的多。
        可以在无界面的服务器或CI上运行测试，减少了外界的干扰，使自动化测试更稳定。
         */
        Data.headless = false;
        init("登录HIS系统",true);
        browser.wnwd.openUrl("http://" + Data.his_host + "/portal/#/login?W-APP=his");
        //Xpath
        browser.wnwd.waitElementByXpathAndClick("帐号登录方式选择按钮", "//div[.='账号登录']", Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndInput("用户名输入框", WnOutpatientXpath.loginUsernameInput, Data.default_user_login_account, Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndInput("密码输入框", WnOutpatientXpath.loginPasswordInput, Data.default_user_login_pwd, Framework.defaultTimeoutMax);
        browser.wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
        browser.wnwd.checkElementByXpath("检查登录是否成功标识", "//span[.='医院基础业务管理']", Framework.defaultTimeoutMax);
    }
    @Test
    public void test02(){
        Data.headless = false;
        init("登录HIS系统", true);
        browser.wnwd.openUrl("http://"+ Data.his_host+"/portal/#/login?W-APP=his");
        browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
    }
}
