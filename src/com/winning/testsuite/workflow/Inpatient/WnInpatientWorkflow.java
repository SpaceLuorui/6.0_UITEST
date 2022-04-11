package com.winning.testsuite.workflow.Inpatient;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import com.winning.manager.HisSqlManager;

import org.openqa.selenium.*;

import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;

import ui.sdk.base.SdkWebDriver;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.*;
import ui.sdk.util.SdkTools.IdCardGenerator;
import xunleiHttpTest.HttpTest;
import xunleiHttpTest.HttpTestHeader;
import xunleiHttpTest.HttpTestUrl;

import static com.winning.user.winex.InpatientTestBase.browser;
import static java.awt.SystemColor.info;
import static javax.swing.UIManager.get;

/**
 * ui测试流程
 *
 * @author Administrator
 */
public class WnInpatientWorkflow {
    public SdkDatabaseConn db = null;//his的db对象
    public Wn60Db db60 = null;//60的db对象

    public SdkWebDriver wnwd = null;
    public Log logger = null;
    public static String lcxmBillTestResult = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "lcxm.csv";

    public WnInpatientWorkflow(SdkWebDriver driver) {
        this.wnwd = driver;
        this.logger = wnwd.logger;
        db = new SdkDatabaseConn(Data.hisDbType, Data.hisHost, Data.hisInstance, Data.hisDbname, Data.hisDbService, Data.hisUsername, Data.hisPassword, this.logger);
        db60 = new Wn60Db(this.logger);
    }

    // 登录portal
    public void wnlogin(String username, String password) {
        wnwd.waitElementByXpathAndClick("帐号登录方式选择按钮", "//div[.='账号登录']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("用户名输入框", WnOutpatientXpath.loginUsernameInput, username, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("密码输入框", WnOutpatientXpath.loginPasswordInput, password, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("Portal页面标识", WnOutpatientXpath.portalPageFlag, Framework.defaultTimeoutMax);
    }

    // 从portal进入主数据管理页面
    public void loginMDMFromPortal() {
        wnwd.waitElementByXpathAndClick("主数据管理入口", "//main[@class=\"el-main\"]//li/p[.=\"主数据管理\"]",
                Framework.defaultTimeoutMax);
    }

    // 从主数据进入portal页面
    public void loginPortalFromMDM() {
        wnwd.waitElementByXpathAndClick("进入主菜单界面", "//img[@class='logo-img']", Framework.defaultTimeoutMax);
    }

    //大HIS系统患者入院登记
    public ArrayList<String> admissionRegistration(String ward, String subject) {
        ArrayList<String> patInfo = new ArrayList<String>();
        wnwd.moveToElementByXpath("鼠标移动到出入院管理", "//div[.='出入院管理']", Framework.defaultTimeoutMax);
        wnwd.moveToElementByXpath("鼠标移动到日常工作", "//div[contains(text(),'日常工作')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击入院登记", "//li[.='入院登记']", Framework.defaultTimeoutMax);

        String IDCode = IdCardGenerator.IDCardCreate();
        wnwd.waitElementByXpathAndInput("输入身份证号码", "//input[@placeholder='请输入证件号']", IDCode, Framework.defaultTimeoutMax);

        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String patientName = "测试患者" + format.format(date) + ((int) ((Math.random() * 9 + 1) * 100000));
        wnwd.waitElementByXpathAndInput("输入姓名", "//input[@placeholder='请输入姓名']", patientName, Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("点击入院途径", "//label[contains(text(),'入院途径')]/following-sibling::*//input", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择入院途径", "//li//span[.='门诊']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击入院科室", "//label[contains(text(),'入院科室')]/following-sibling::*//input", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择入院科室", "//li//span[.='" + Data.test_select_subject + "']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击入院病区", "//label[contains(text(),'入院病区')]/following-sibling::*//input", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择入院病区", "//li//span[.='" + Data.inpatient_select_ward + "']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击危重级别", "//label[contains(text(),'危重级别')]/following-sibling::*//input", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择危重级别", "//li//span[.='一般']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("输入停药线", "//label[contains(text(),'停药线')]/following-sibling::*//input", "1", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("输入报警线", "//label[contains(text(),'报警线')]/following-sibling::*//input", "1", Framework.defaultTimeoutMax);
        WebElement btn = wnwd.waitElementByXpath("点击确定", "//div[@class='inHosp-info']//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
        wnwd.wnDoubleClickElementByMouse(btn, "点击确定按钮");
//        wnwd.waitElementByXpathAndClick("点击确定", "//div[@class='inHosp-info']//button//span[contains(text(),'确定')]", Frmcons.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载框消失", "//div[@class='el-loading-mask']", Framework.defaultTimeoutMax);
        patInfo.add(patientName);
        return patInfo;
    }

    //进入住院护士站并执行科目选择,科目选择框可能不存在
    public void loginInpatientNurseStation(String loginAccount, String ward) {
        loginInpatientNurseStation(loginAccount, new ArrayList<>(Arrays.asList(ward)));
    }

    //进入住院护士站，并选择病区
    public void loginInpatientNurseStation(String loginAccount, List<String> wardList) {
        wnwd.waitElementByXpathAndClick("住院护士站入口", WnInpatientXpath.portalInpatientNurseStationEntrance, Framework.defaultTimeoutMax);
        String applicationName = "住院护士站";
        int num = queryWardsOfNurse(loginAccount);
        if (num == 0) {
            logger.assertFalse(true, "登录账号无护理病区");
        } else if (num > 1) {
            choiceWard(applicationName, wardList);
        } else {
            logger.log(1, "登录账号只有一个护理病区，无需选择病区");
            wnwd.waitNotExistByXpath("等待病区选择框不可见", WnInpatientXpath.inpatientWardChooseBox, Framework.defaultTimeoutMax);
        }
    }

    // 选择科目
    public void choiceWard(String applicationName, String ward) {
        choiceWard(applicationName, new ArrayList<>(Arrays.asList(ward)));
    }

    public void choiceWard(String applicationName, List<String> wardList) {
        // 首先将所有科目选中状态去掉
        if (applicationName.equals("住院医生站")) {
            List<WebElement> search_result = wnwd.waitElementListByXpath(WnInpatientXpath.inpatientCheckboxIsChecked, Framework.defaultTimeoutMax);
            for (WebElement checkedBox : search_result) {
                wnwd.wnClickElement(checkedBox, "取消勾选 " + checkedBox.getText(), false, false);
            }
        }

        // 找到指定科室选中
        List<WebElement> checkboxlist = null;
        if (applicationName.equals("住院护士站")) {
            checkboxlist = wnwd.waitElementListByXpath(WnInpatientXpath.inpatientNurseWardChooseBoxCheckBox, Framework.defaultTimeoutMax);
        } else if (applicationName.equals("住院医生站")) {
            checkboxlist = wnwd.waitElementListByXpath(WnInpatientXpath.inpatientDoctorWardChooseBoxCheckBox, Framework.defaultTimeoutMax);

        }
        for (WebElement webElement : checkboxlist) {
            Boolean chooseFlag = false;
            for (String ward : wardList) {
                if (webElement.getText().contains(ward)) {
                    chooseFlag = true;
                    break;
                }
            }
            if (chooseFlag) {
                wnwd.wnClickElement(webElement, "勾选科目 " + webElement.getText());
            }
        }
        System.out.println("checkboxlist.size():"+checkboxlist.size());
        //护士站病区只有一个，则不用点击确认按钮
        if ((applicationName.equals("住院护士站") && checkboxlist.size() > 1) || applicationName.equals("住院医生站")) {
            wnwd.waitElementByXpathAndClick("确认按钮", WnInpatientXpath.inpatientWardChooseBox + "//span[.='确 定']//button", Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待病区选择框不可见", WnInpatientXpath.inpatientWardChooseBox, Framework.defaultTimeoutMax);
        }
    }

    public void enterWard(String patientName) {
        enterWard(patientName, null);
    }

    //患者入区
    public void enterWard(String patientName, String bedNo) {
        wnwd.waitElementByXpathAndClick("点首页", "//div[@class='nav-scroll']//span[text()='首页']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击筛选按钮", "//div[contains(@class,'search-confition-btn')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击有人按钮", "//span[contains(@class,'query-item-cell') and contains(text(),'有人')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击包床按钮", "//span[contains(@class,'query-item-cell') and contains(text(),'包床')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击查询按钮", "//div[contains(@class,'home-query-setting')]//span[.='查询']", Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpathAndClick("点击待入区", WnInpatientXpath.WaitToEnterWardButton,Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpathAndClick("点击空床", "//li[@type='plain']//span[@title='空床']", Framework.defaultTimeoutMax);
        if (bedNo != null) {
            wnwd.waitElementByXpathAndInput("输入框输入床位号", WnInpatientXpath.inpatientPatientSearchInput, bedNo, Framework.defaultTimeoutMin);
        }
        wnwd.waitElementByXpathAndClick("点击第一个办理入区按钮", "//div[contains(@class,'bed-card-item')]//div[contains(text(),'办理入区')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpath("等待入区框出现", "//span[contains(@class,'el-dialog__title') and .='入区登记']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("输入患者姓名", "//input[@placeholder='住院号/姓名']", patientName, Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("选中待入区患者", "//div[contains(@class,'patient-item')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("请选择责任护士", "//input[@placeholder='请选择责任护士']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择责任护士", "//li[contains(text(),'" + Data.responsibleNurse + "')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("请选择责任医生", "//input[@placeholder='请选择责任医生']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择责任医生", "//li[contains(text(),'" + Data.responsibleDoctor + "')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("请选择住院医生", "//input[@placeholder='请选择住院医生']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择住院医生", "//li[contains(text(),'" + Data.residentDoctor + "')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("请选择主治医生", "//input[@placeholder='请选择主治医生']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择主治医生", "//li[contains(text(),'" + Data.attendingDoctor + "')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("请选择主任医生", "//input[@placeholder='请选择主任医生']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择主任医生", "//li[contains(text(),'" + Data.headDoctor + "')]", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("点击确定按钮", "//div[@class='inpat-content']//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
//        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("检查入区成功提示", "//p[.='入区录入成功']", Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpathAndClick("床位卡打印弹框，点击取消按钮", WnInpatientXpath.cancelButton, Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpathAndClick("腕带打印弹框，点击取消按钮", WnInpatientXpath.cancelButton, Framework.defaultTimeoutMax);
    }

    //进入住院医生站，并选择病区
    public void loginInpatientResidentStation(String subject, String ward) {
        loginInpatientResidentStation(subject, new ArrayList<>(Arrays.asList(ward)));
    }

    public void loginInpatientResidentStation(String subject, List<String> wardList) {
        wnwd.waitElementByXpathAndClick("住院医生站入口", WnInpatientXpath.portalInpatientResidentStationEntrance, Framework.defaultTimeoutMax);
        String applicationName = "住院医生站";
        WebElement element = wnwd.waitElementByXpath("病区选择框", WnInpatientXpath.inpatientWardChooseBox, Framework.defaultTimeoutMax);
        if (element != null) {
            wnwd.waitElementByXpathAndClick("点击请选择科室", "//input[@placeholder='请选择科室']", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择科室", "//span[.='" + subject + "']", Framework.defaultTimeoutMax);
            choiceWard(applicationName, wardList);
        }
    }

    //开立任意医嘱
    public void prescribeOrder(String name) {
        prescribeOrder(name, null, null);
    }

    public void prescribeOrder(String name, List<String> searchFlag) {
        prescribeOrder(name, searchFlag, null);
    }

    public void prescribeOrder(List<String> searchFlag, List<String> orderDetail) {
        prescribeOrder(searchFlag.get(0), searchFlag, orderDetail);
    }

    public void prescribeOrder(String name, List<String> searchFlag, List<String> orderDetail) {
        if (name.toUpperCase().equals("NONE")) {
            return;
        }
        searchOrder(name, searchFlag);
        editAndCommitOrder(name, orderDetail);
    }


    //搜索医嘱
    public void searchOrder(String name) {
        searchOrder(name, null);
    }

    public void searchOrder(List<String> searchFlag) {
        searchOrder(searchFlag.get(0), searchFlag);
    }

    public void searchOrder(String name, List<String> searchFlag) {
        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        WebElement inputSearchOrder = wnwd.waitElementByXpath("请选择", "//div[@class='el-input el-input--suffix']//input[contains(@placeholder,'请选择')]", Framework.defaultTimeoutMax);
        wnwd.wnClickElementByMouse(inputSearchOrder,"请选择");
//        wnwd.sleep(Framework.defaultTimeoutMin);
//        WebElement ele = wnwd.waitElementByXpath( "//div[contains(@class,'disposal__search--select--arrow')]//i", Framework.defaultTimeoutMax);
//        if(ele==null){
//            wnwd.wnDoubleClickElementByMouse(inputSearchOrder,"请选择");
//            wnwd.sleep(Framework.defaultTimeoutMin);
//        }
//        wnwd.wnClickElement(ele,"医嘱类型下拉框");
        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点下拉图标", "//div[contains(@class,'disposal__search--select--arrow')]//i", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("医嘱类型选择全部", "//div[contains(@class,'disposal__search') ]//div[contains(text(),'全部')]", Framework.defaultTimeoutMax);
        WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框", WnInpatientXpath.inpatientSearchOrderInput, Framework.defaultTimeoutMax);
        wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
//        wnwd.checkElementByXpath("医嘱搜索结果", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
//        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        wnwd.sleep(1000);
        wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框输入信息");
        WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);

        //查找所有搜索结果
        List<WebElement> searchResults = resultBox.findElements(By.xpath(WnInpatientXpath.inpatientSearchOrderAllResultList));
//        logger.log(2,"搜索结果数量:"+searchResults.size());
        // 搜索结果是空 直接报错
        if (searchResults == null || searchResults.size() == 0) {
            logger.assertFalse(true, "60无搜索结果", "输入:" + name);
        }
        // searchFlag是空 直接选择第一个搜索结果
        if (searchFlag == null || searchFlag.size() == 0) {
            logger.assertFalse(!searchResults.get(0).getAttribute("class").contains("blackTxt"), "60无库存");
            wnwd.wnClickElement(searchResults.get(0), "医嘱搜索结果第一个");
            return;
        }
        // searchFlag不是空 选择第一个包含List中所有元素的结果
        WebElement resultLine = null;
        for (WebElement line : searchResults) {
            System.out.println("line.getText()的值：" + line.getText());
            Boolean findFlag = true;
            for (String searchString : searchFlag) {
                if (!line.getText().contains(searchString.trim())) {
                    logger.log(3, line.getText() + " 不包含 " + searchString);
                    findFlag = false;
                    break;
                }
            }
            if (!findFlag) {
                continue;
            }
            resultLine = line;
            logger.log(1, "找到医嘱:" + line.getText().trim());
            String sFlag = SdkTools.getListValueAndSplice((ArrayList<String>) searchFlag).replace(";", " ").trim();
            System.out.println("searchFlag:" + sFlag);
            if (line.getText().trim().equals(sFlag)) {
                logger.log(1, "完全一致");
                break;
            }
        }
        if (resultLine != null) {
            // 无库存时报错
            System.out.println(resultLine.getAttribute("class"));
            logger.assertFalse(!resultLine.getAttribute("class").contains("blackTxt"), "60无库存");
            wnwd.wnClickElement(resultLine, "医嘱项:" + resultLine.getText());
        } else {
            // 搜索不到结果时报错
            logger.assertFalse(true, "60无搜索结果", "输入:" + name + "\n结果包含:" + searchFlag);
        }
    }

    //编辑加工厂并点击确定
    public void editAndCommitOrder(String name, String detailType) {
        editAndCommitOrder(name, null, detailType);
    }

    public void editAndCommitOrder(String name, List<String> orderDetail) {
        editAndCommitOrder(name, orderDetail, null);
    }

    public void editAndCommitOrder(String name, List<String> orderDetail, String detailType) {
        try {
            if (detailType == null) {
                detailType = getDetailType();
            }
            // 根据当前医嘱类型 进行不同操作
            if (detailType == "herb") {
                editHerb(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "drug") {
                editDrug(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "exam") {
                editExam(name, orderDetail);
            } else if (detailType == "lab") {
                editLab(name, orderDetail);
            } else if (detailType == "treatment") {
                editTreat(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "blood") {
                editBlood(orderDetail);
            } else if (detailType == "operation") {
                editOperation(orderDetail);
            } else if (detailType == "pathology") {
                editPathology(orderDetail);
            } else if (detailType == "nursing" ) {
                editNursing(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "diet") {
                editDiet(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "entrust") {
                editEntrust(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            } else if (detailType == "material") {
                editMaterial(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
            }
            else {
                wnwd.assertTrue("当前未处于开立详情界面", false);
            }


            wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnInpatientXpath.inpatientDisposalFactoryCommitButton, Framework.defaultTimeoutMax);

            wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", "//div[@class='default-pic']", Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载动画结束", "//div[@class='win-loading']", Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.assertFalse(true, "开立医嘱失败", e.getMessage());
        }
    }

    public WebElement checkDisposalFactory() {
        return wnwd.checkElementByXpath("医嘱加工厂", WnInpatientXpath.inpatientDisposalFactory, Framework.defaultTimeoutMin);
    }

    //判断当前在哪个药品加工厂
    //返回String: herb 草药;   drug 西药;  lab 检验;  exam 检查; treatment 治疗;  pathology 病理;  unknow 未知;
    public String getDetailType() {
        String detailType = "";
        try {
            // 等待开立详情界面
            WebElement disposalFactory = checkDisposalFactory();
            if (disposalFactory.findElements(By.xpath(WnInpatientXpath.inpatientDisposalFactoryHerbFlag)).size() != 0) {
                detailType = "herb";
            } else if (disposalFactory.findElements(By.xpath(WnInpatientXpath.inpatientDisposalFactoryDrugFlag)).size() != 0) {
                detailType = "drug";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryLabFlag)!=null) {
                detailType = "lab";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryExamFlag)!=null) {
                detailType = "exam";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryTreatFlag)!=null) {
                detailType = "treatment";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryBloodFlag)!=null) {
                detailType = "blood";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOperationFlag)!=null) {
                detailType = "operation";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryPathologyFlag)!=null) {
                detailType = "pathology";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryNursingFlag)!=null) {
                detailType = "nursing";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryDietFlag)!=null) {
                detailType = "diet";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryEntrustFlag)!=null) {
                detailType = "entrust";
            } else if (wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryMaterialFlag)!=null) {
                detailType = "material";
            } else {
                throw new Error("未知的加工厂类型");
            }
            logger.log("当前医嘱类型为：" + detailType);
            wnwd.waitNotExistByXpath("等待加工厂加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
            if (Data.getScreenShot) {
                wnwd.getScreenShot("打开加工厂成功");
            }
        } catch (Throwable e) {
            if (Data.getScreenShot) {
                wnwd.getScreenShot("打开加工厂失败");
            }
            throw new Error("打开加工厂失败:" + e.getMessage());
        }
        logger.log(1, "当前加工厂类型:" + detailType);
        return detailType;
    }


    //编辑西药医嘱详情
    public void editDrug(List<String> orderDetail) {
        logger.log(1, "editDrug:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            wnwd.waitElementByXpathAndInput("西药数量输入框", WnInpatientXpath.inpatientDisposalFactoryDrugNumInput, orderDetail.get(0), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击给药途径下拉框", WnInpatientXpath.inpatientDisposalFactoryRouteInput, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击给药途径下拉框第一个", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击频次下拉框", WnInpatientXpath.inpatientDisposalFactoryDrugFreqInput, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击频次ST", "//div[@class='el-scrollbar']//li//span[contains(text(),'ST')]", Framework.defaultTimeoutMax);
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
        } else {
            wnwd.waitElementByXpathAndInput("西药数量输入框", WnInpatientXpath.inpatientDisposalFactoryDrugNumInput, orderDetail.get(0), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击给药途径下拉框", WnInpatientXpath.inpatientDisposalFactoryRouteInput, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击给药途径下拉框第一个", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击频次下拉框", WnInpatientXpath.inpatientDisposalFactoryDrugFreqInput, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击频次QD", "//div[@class='el-scrollbar']//li//span[contains(text(),'QD')]", Framework.defaultTimeoutMax);
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
        }
    }

    // 编辑中草药医嘱详情
    public void editHerb(List<String> orderDetail) {
        logger.log(1, "editHerb:" + orderDetail);
        // 中草药编辑
        wnwd.waitElementByXpathAndInput("中草药剂量输入框", WnInpatientXpath.inpatientDisposalFactoryHerbNumInput,
                orderDetail.get(0), Framework.defaultTimeoutMax);

        // 代煎方式设置处理
//        if (!WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue.equals("")) {
//            wnwd.waitElementByXpathAndClick("代煎方式下拉框", WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodList, Frmcons.defaultTimeoutMax);
//			WebElement element=wnwd.moveToElementByXpath("指定的代煎方式",WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue,Frmcons.defaultTimeoutMax);
//			wnwd.wnClickElementByMouse(element,"点击指定的代煎方式");
//            wnwd.waitElementByXpathAndClick("点击需要的代煎方式", WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue, Frmcons.defaultTimeoutMax);
//
//        }
        // 频次
        WebElement element = wnwd.waitElementByXpath(WnInpatientXpath.inpatientDisposalFactoryHerbFreqCodeDropbox, Framework.defaultTimeoutMax);
        if (element.isEnabled()) {
            wnwd.waitElementByXpathAndClick("频次下拉框", WnInpatientXpath.inpatientDisposalFactoryHerbFreqCodeDropbox, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择第一个频次选项", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
        }
        // 给药途径
        wnwd.waitElementByXpathAndClick("给药途径下拉框", WnInpatientXpath.inpatientDisposalFactoryHerbDecocteMethodDropbox, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择第一个给药途径选项", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
        WebElement ele1 = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOnceDoseInput);
        if (ele1 != null) {
            wnwd.wnEnterText(ele1, orderDetail.get(0), "输入每次剂量");
            wnwd.waitElementByXpathAndInput("输入每剂煎出总量", WnInpatientXpath.inpatientDisposalFactoryProcessedAmountInput, orderDetail.get(0), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入天数", WnInpatientXpath.inpatientDisposalFactoryDaysInput, orderDetail.get(0), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入剂数", WnInpatientXpath.inpatientDisposalFactoryDoseInput, orderDetail.get(0), Framework.defaultTimeoutMax);
            wnwd.sendKeyEvent(Keys.ENTER);
        }
        wnwd.waitElementByXpathAndClick("中草药保存按钮", WnInpatientXpath.inpatientDisposalFactoryHerbNumSave, Framework.defaultTimeoutMax);

    }

    //编辑检验医嘱详情
    public void editLab(String name, List<String> orderDetail) {
        logger.log(1, "editLab:" + orderDetail);
        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        //找到所有检验项目
//        List<WebElement> examItems = wnwd.waitElementListByXpath(WnInpatientXpath.inpatientDisposalFactoryCheckBoxes, Frmcons.defaultTimeoutMax);
//        //全部取消勾选
//        for (WebElement examItem : examItems) {
//            if (examItem.getAttribute("class").contains("is-checked")) {
//                wnwd.wnClickElement(examItem, "取消勾选:" + examItem.getText());
//            }
//        }
//        //勾选指定项目
//        for (WebElement examItem : examItems) {
//        	int i = 0;
//        	if(examItem.getText().contains(name)) {
//                wnwd.wnClickElement(examItem, "勾选:" + examItem.getText());
//                i++;
//        	}
//        	System.out.println(i);
//        	if(i==0) {
//                wnwd.wnClickElement(examItems.get(0), "勾选:" + examItem.getText());
//        	}
//        }
        //标本
        wnwd.waitElementByXpathAndClick("标本下拉框", WnInpatientXpath.inpatientDisposalFactoryLabSpecimenDropbox, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("选择第一个标本", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);

    }

    //编辑检查医嘱详情
    public void editExam(String name, List<String> orderDetail) {
        logger.log(1, "editExam:" + orderDetail);
        wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("摘要输入框", WnInpatientXpath.inpatientDisposalFactoryExamSummaryInput, "test", Framework.defaultTimeoutMax);
        List<WebElement>  ele = wnwd.getElementListByXpath("//aside//li");
        if(ele.size()>0){
            wnwd.wnClickElement(ele.get(0),"选择检查类别");
        }
        //找到所有检查项目
        List<WebElement> examItems = wnwd.waitElementListByXpath(WnInpatientXpath.inpatientDisposalFactoryCheckBoxes, Framework.defaultTimeoutMax);
        //全部取消勾选
        for (WebElement examItem : examItems) {
            if (examItem.getAttribute("class").contains("is-checked")) {
                wnwd.wnClickElement(examItem, "取消勾选:" + examItem.getText());
            }
        }
        //勾选指定项目
        int i = 0;
        for (WebElement examItem : examItems) {
            if (examItem.getText().contains(name)) {
                System.out.println(examItem);
                wnwd.wnClickElement(examItem, "勾选:" + examItem.getText());
                i++;
            }
        }
        if (i == 0) {
            wnwd.wnClickElement(examItems.get(0), "勾选:" + examItems.get(0).getText());
        }
    }

    //编辑治疗医嘱详情
    public void editTreat(List<String> orderDetail) {
        logger.log(1, "editTreat:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
        } else {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
        }
        WebElement ele1 = wnwd.getElementByXpath(WnInpatientXpath.inpatientTreatmentQuantity);
        if (ele1 != null) {
            wnwd.waitElementByXpathAndInput("数量输入框", WnInpatientXpath.inpatientTreatmentQuantity, orderDetail.get(0), Framework.defaultTimeoutMax);
        }
    }

    //编辑用血医嘱详情
    public void editBlood(List<String> orderDetail) {
        logger.log(1, "editBlood:" + orderDetail);
        wnwd.waitElementByXpathAndInput("数量输入框", WnInpatientXpath.inpatientTreatmentQuantity, orderDetail.get(0), Framework.defaultTimeoutMax);
    }

    //编辑手术医嘱详情
    public void editOperation(List<String> orderDetail) {
        logger.log(1, "editOperation:" + orderDetail);
        try {
            wnwd.waitElementByXpathAndClick("点击等级", WnInpatientXpath.operationGrade, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击部位", WnInpatientXpath.operationPlace, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击体位", WnInpatientXpath.operationPosition, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击麻醉方式", WnInpatientXpath.operationAnaesthesia, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击切口等级", WnInpatientXpath.operationIncision, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击手术分类", WnInpatientXpath.operationClassification, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击手术室", WnInpatientXpath.operationRoom, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击手术人员", WnInpatientXpath.operationPersonnel, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击姓名", WnInpatientXpath.operationName, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择下拉框第一个", WnInpatientXpath.operationFirst, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
            logger.assertFalse(true, "编辑手术医嘱失败");
        }
    }

    //编辑病理医嘱详情
    public void editPathology(List<String> orderDetail) {
        logger.log(1, "editPathology:" + orderDetail);
    }

    //编辑护理医嘱详情
    public void editNursing(List<String> orderDetail) {
        logger.log(1, "editNursing:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        } else {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        }
    }

    //编辑膳食医嘱详情
    public void editDiet(List<String> orderDetail) {
       logger.log(1, "editDiet:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        } else {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        }
    }

    //嘱托
    public void editEntrust(List<String> orderDetail) {
        logger.log(1, "editEntrust:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        } else {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        }
    }

    //卫材
    public void editMaterial(List<String> orderDetail) {
        logger.log(1, "editMaterial:" + orderDetail);
        WebElement purposeRadio = null;
        if (orderDetail == null || orderDetail.size() <= 1) {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioTemporary);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-临时");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        } else {
            purposeRadio = wnwd.getElementByXpath(WnInpatientXpath.inpatientDisposalFactoryOrderTypeRadioLongTerm);
            if (purposeRadio != null) {
                wnwd.wnClickElement(purposeRadio, "选择医嘱类型-长期");
            }
            wnwd.waitElementByXpathAndInput("医嘱备注", WnInpatientXpath.inpatientRemarks, orderDetail.get(0), Framework.defaultTimeoutMax);
        }
    }

    //搜索患者
    public void callNumberByName(String name) {
        String applicationName = wnwd.waitElementByXpath("//span[@class='application-name']", Framework.defaultTimeoutMin).getText();
        if (applicationName.contains("住院医生站")) {
            wnwd.waitElementByXpathAndClick("点击床位卡", WnInpatientXpath.BedCardPage, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击选中的病区", "//div[@class='ward-tabs']//span[contains(text(),'" + Data.inpatient_select_ward + "')]", Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载完成", "//div[@class='el-loading-spinner']", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击在区标签栏", "//div[@class='plan-btn-content__qname']//span[contains(text(),'在区')]", Framework.defaultTimeoutMax);
            wnwd.waitElementListByXpath("//div[contains(@class,'bed-card-item')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("搜索框输入患者姓名", WnInpatientXpath.inpatientPatientSearchInput, name, Framework.defaultTimeoutMin);
            WebElement patientCard = wnwd.waitElementByXpath("患者" + name + "的床位卡", "//div[contains(@class,'bed-card-item') and contains (.,'" + name + "')]", Framework.defaultTimeoutMin);
            wnwd.wnDoubleClickElementByMouse(patientCard, "双击床位卡");
        } else if (applicationName.contains("住院护士站")) {
	        wnwd.waitElementByXpathAndClick("点击在区标签栏", "//div[@class='plan-btn-content__qname']//span[contains(text(),'在区')]", Framework.defaultTimeoutMax);
            wnwd.waitElementListByXpath("//div[contains(@class,'bed-card-item')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("搜索框输入患者姓名", WnInpatientXpath.inpatientPatientSearchInput, name, Framework.defaultTimeoutMin);
            WebElement patientCard = wnwd.waitElementByXpath("患者" + name + "的床位卡", "//div[contains(@class,'bed-card-item') and contains (.,'" + name + "')]", Framework.defaultTimeoutMin);
            wnwd.wnDoubleClickElementByMouse(patientCard, "双击床位卡");
        }

    }


    //签署医嘱
    public void signOff(int sleepTime) {
        try {
            wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnInpatientXpath.inpatientOrderSignButton, Framework.defaultTimeoutMax);

            long endTime = System.currentTimeMillis() + Framework.defaultTimeoutMax;
            WebElement SignButtonDisabled = null;
            while (System.currentTimeMillis() < endTime) {
                SdkTools.sleep(500);
                SignButtonDisabled = wnwd.getElementByXpath(WnInpatientXpath.inpatientOrderSignButtonDisabled);
                if (SignButtonDisabled != null) {
                    break;
                }
            }
            if (SignButtonDisabled == null) {
                throw new Error("请查看截图");
            }
            wnwd.sleep(sleepTime);
        } catch (Throwable e) {
            try {
                wnwd.getScreenShot("签署失败");
            } catch (Throwable e2) {
                logger.log(1, e.getMessage());
            }
            wnwd.assertFalse("签署医嘱失败:" + e.getMessage(), true);
        }

    }

    //停止医嘱（单条）.
    public void stopOrder(String name) {
        try {
            WebElement StopOrderIcon =wnwd.waitElementByXpath("停止医嘱标签", WnInpatientXpath.stopOrderIcon.replace("?",name), Framework.defaultTimeoutMid);
            if (StopOrderIcon !=null) {
                wnwd.waitElementByXpathAndClick("点击停止医嘱:"+name, WnInpatientXpath.stopOrderIcon.replace("?",name), Framework.defaultTimeoutMax);
                WebElement StopOrderSelectDate=wnwd.checkElementByXpath("选择日期时间", WnInpatientXpath.stopOrderSelectDate, Framework.defaultTimeoutMax);
                wnwd.wnClickElementByMouse( StopOrderSelectDate, "双击");
                wnwd.waitElementByXpathAndClick("此刻", WnInpatientXpath.stopOrderSelectNow, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.stopOrderConfirm, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("停止医嘱成功", WnInpatientXpath.stopOrderSucMsg, Framework.defaultTimeoutMax);
            } else{
                logger.log(2, "不存在可停止的长期医嘱" );
            }
        }catch (Throwable e){
            logger.assertFalse(true, "停止医嘱失败:" + e.getMessage());
        }
    }

    //停止医嘱（批量）.
    public void stopOrders() {
        try {
            wnwd.waitElementByXpathAndClick("批量停止医嘱按钮", WnInpatientXpath.batchStopOrderButton, Framework.defaultTimeoutMax);
            WebElement BatchStopOrderWarning =wnwd.waitElementByXpath("没有可以停止的医嘱弹框", WnInpatientXpath.batchStopOrderWarning, Framework.defaultTimeoutMid);
            if (BatchStopOrderWarning ==null) {
                wnwd.waitElementByXpathAndClick("点击确定", WnInpatientXpath.batchStopOrderConfirm, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("弹出框确认", WnInpatientXpath.batchStopOrderFrameConfirm, Framework.defaultTimeoutMax);
                wnwd.waitNotExistByXpath("停止医嘱标签", WnInpatientXpath.stopOrderIcon, Framework.defaultTimeoutMax);
            }else{
                logger.log(2, "没有可以停止的医嘱" );
            }
        }catch (Throwable e){
            logger.assertFalse(true, "批量停止医嘱失败:" + e.getMessage());
        }
    }

    //作废医嘱（单条）.
    public void nullifyOrder(String name) {
        try {
            wnwd.waitElementByXpathAndClick("勾选医嘱", WnInpatientXpath.tickOrder.replace("?", name), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("作废", WnInpatientXpath.cancelOrder, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.cancelOrderConfirm, Framework.defaultTimeoutMax);
        }catch (Throwable e){
            logger.assertFalse(true, "作废医嘱失败:" + e.getMessage());
        }
    }

    //作废医嘱（批量）.
    public void nullifyOrders() {
        try {
            wnwd.waitElementByXpathAndClick("选择全部", WnInpatientXpath.tickOrderBatch, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("作废", WnInpatientXpath.cancelOrder, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.cancelOrderConfirm, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("医嘱消失", WnInpatientXpath.tickOrder, Framework.defaultTimeoutMax);
        }catch (Throwable e){
            logger.assertFalse(true, "批量作废医嘱失败:" + e.getMessage());
        }
    }

    //作废长期医嘱（已签收）.
    public void nullifyOrderSign(String name){
        try {
            wnwd.waitElementByXpathAndClick("勾选医嘱", WnInpatientXpath.tickOrder.replace("?", name), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("作废", WnInpatientXpath.cancelOrder, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.cancelOrderConfirm, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("停止医嘱确定", WnInpatientXpath.batchStopOrderConfirm, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("二次确定", WnInpatientXpath.cancelOrderConfirm, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("停止医嘱成功", WnInpatientXpath.stopOrderSucMsg, Framework.defaultTimeoutMax);
        }catch (Throwable e){
            logger.assertFalse(true, "批量作废医嘱失败:" + e.getMessage());
        }
    }

    //撤回医嘱.
    public void recallOrder(String name) {
        try {
            wnwd.waitElementByXpathAndClick("撤回医嘱", WnInpatientXpath.recallOrder.replace("?", name), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("二次确认", WnInpatientXpath.cancelOrderConfirm.replace("?", name), Framework.defaultTimeoutMax);
            }catch (Throwable e){
            logger.assertFalse(true, "撤回医嘱失败:" + e.getMessage());
        }
    }

    //医嘱二次确认.
    public void secondOrder(String name) {
       try {
           wnwd.waitElementByXpathAndClick("二次确认", WnInpatientXpath.reconfirmOrder.replace("?", name), Framework.defaultTimeoutMax);
           wnwd.waitElementByXpathAndInput("二次确认原因", WnInpatientXpath.reconfirmOrderDescribe, "二次确认原因",Framework.defaultTimeoutMax);
           wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.reconfirmOrderConfirm, Framework.defaultTimeoutMax);
           wnwd.waitElementByXpathAndClick("提示信息", WnInpatientXpath.reconfirmOrderSucMsg, Framework.defaultTimeoutMax);
    }catch (Throwable e){
        logger.assertFalse(true, "医嘱二次确认失败:" + e.getMessage());
    }
}

    //护士站-医嘱单-搜索全部医嘱.
    public void selectAll() {
        wnwd.waitElementByXpathAndClick("点击全部医嘱", WnInpatientXpath.selectAll, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击医嘱状态下拉框", WnInpatientXpath.orderStatusDropDown, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击医嘱状态下拉框-全部", WnInpatientXpath.orderStatusDropDownSelectAll, Framework.defaultTimeoutMax);
    }

    //医嘱疑问.
    public void doubtOrder(String name) {
        try {
            wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("疑问", WnInpatientXpath.doubtOrder.replace("?", name), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("疑问原因", WnInpatientXpath.doubtOrderReason, "疑问原因",Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.doubtOrderConfirm, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("提示信息", WnInpatientXpath.doubtOrderSucMsg, Framework.defaultTimeoutMax);
        }
        catch (Throwable e){
            logger.assertFalse(true, "医嘱疑问失败:" + e.getMessage());
        }
    }

    //签收医嘱
    public void signFor(String name) {
        try{
        wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("进入未签收界面", "//div[@class='signStateBtn signBoxActive']//div[contains(text(),'未签收')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击刷新按钮", "//button[contains(@class,'el-button--small is-plain')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("签收:" + name, "//tr[contains(@class,'el-table__row') and contains(.,'" + name + "')]//span[contains(text(),'签 收')]", Framework.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载框消失", "//div[@class='el-loading-mask']", Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "医嘱申请失败");
        }
    }

    //医嘱批量签收
    public void signFor() {
        wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击未签收按钮", "//div[contains(text(),'未签收')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("签收:", WnInpatientXpath.orderSignButton, Framework.defaultTimeoutMax);
//        wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
//        wnwd.checkElementByXpath("等待签收成功提示", WnInpatientXpath.signSucMsg, Framework.defaultTimeoutMax);
    }

    //一天后
    public static String getWeekLater() {
        Date today = new Date(System.currentTimeMillis() + 604800000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String WeekLater = simpleDateFormat.format(today);
        return WeekLater;
    }

    //医嘱申请--临时.
    public void orderApply(String name) {
        try {
            wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击今日执行", "//div[@class='nurse-task-header-conditions-task']//span[contains(text(),'今日执行')]", Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("申请按钮:", "//div[contains(@class,'el-table__fixed-right')]//tr[contains(@class,'el-table__row') and contains(.,'" + name + "')]//span[contains(text(),'申 请')]", Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("等待申请成功提示", WnInpatientXpath.OperateSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "医嘱申请失败");
        }
    }

    //医嘱申请--长期.
    public void orderApply(String name,String type) {
        try {
            wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击今日执行", "//div[@class='nurse-task-header-conditions-task']//span[contains(text(),'今日执行')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("搜索", "//button[contains(@class,'el-button--small el-popover__reference')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("时间范围", "//div[contains(@class,'el-date-editor--datetimerange')]//i[contains(@class,'el-icon-time')]", Framework.defaultTimeoutMax);
            WebElement endDataInput = wnwd.waitElementByXpath("//div[contains(@class,'el-date-range-picker__editor')]//input[@placeholder='结束日期']", Framework.defaultTimeoutMid);
            endDataInput.clear();
            endDataInput.sendKeys(getWeekLater());
            wnwd.waitElementByXpathAndClick("确定", "//button[contains(@class,'el-button--mini is-plain')]//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定", "//div[@class='el-popover el-popper']//button[contains(@class,'el-button--primary')]//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("申请按钮:", "//div[contains(@class,'el-table__fixed-right')]//tr[contains(@class,'el-table__row') and contains(.,'" + name + "')]//span[contains(text(),'申 请')]", Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("等待申请成功提示", WnInpatientXpath.OperateSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "医嘱申请失败");
        }
    }

    //医嘱撤销申请.
    public void orderAppliyCancel(String name) {
        try {
            wnwd.waitElementByXpathAndClick("点击医嘱执行标签", "//span[contains(text(),'医嘱执行')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("待执行", WnInpatientXpath.orderExecuteStatus, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击刷新按钮", "//button[contains(@class,'el-button--small is-plain')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击撤销申请:" + name, "//tr[contains(@class,'el-table__row') and contains(.,'" + name + "')]//span[contains(text(),'撤销申请')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入撤销申请原因", WnInpatientXpath.orderRevokeReason, "撤销申请原因", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击确定", WnInpatientXpath.orderRevokeReasonConfirm, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("等待操作成功提示", WnInpatientXpath.OperateSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "医嘱撤销申请失败");
        }
    }

    //医嘱执行
    public void orderApplication(String name) {
        try {
            wnwd.waitElementByXpathAndClick("点击医嘱执行标签", "//span[contains(text(),'医嘱执行')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("待执行", WnInpatientXpath.orderExecuteStatus, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("执行:" + name, "//div[contains(@class,'el-table__fixed-right')]//tr[contains(@class,'el-table__row') and contains(.,'" + name + "')]//span[contains(text(),'执行')]", Framework.defaultTimeoutMax);
            WebElement element = wnwd.getElementByXpath(WnInpatientXpath.nurseCheckDialog);
            if(element != null) {
                wnwd.waitElementByXpath("等待护士执行弹框", WnInpatientXpath.nurseCheckDialog, Framework.defaultTimeoutMid);
                wnwd.waitElementByXpathAndInput("输入账号", WnInpatientXpath.nurseCheckAccount, Data.default_user_login_account, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndInput("输入密码", WnInpatientXpath.nurseCheckPwd, Data.default_user_login_pwd, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定按钮", WnInpatientXpath.nurseCheckConfirmButton, Framework.defaultTimeoutMax);
            }
        } catch (Throwable e) {
            logger.assertFalse(true, "医嘱执行失败");
        }
    }

    //医嘱批量申请
    public void orderApply() {
        wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("点击今日执行", "//div[@class='nurse-task-header-conditions-task']//span[contains(text(),'今日执行')]", Framework.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("申请:", "//div[@class='nurse-task-header-conditions-task']//button[@class='el-button el-button--primary']//span[contains(text(),'申 请')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("等待申请成功提示", WnInpatientXpath.OperateSucMsg, Framework.defaultTimeoutMax);
    }
        //医嘱批量执行
        public void orderApplication () {
            wnwd.waitElementByXpathAndClick("点击医嘱执行标签", "//span[contains(text(),'医嘱执行')]", Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("待执行", WnInpatientXpath.orderExecuteStatus, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点执行按钮:", WnInpatientXpath.orderExecuteButton, Framework.defaultTimeoutMax);
            WebElement element = wnwd.getElementByXpath(WnInpatientXpath.nurseCheckDialog);
            if(element != null){
                wnwd.waitElementByXpathAndInput("输入账号", WnInpatientXpath.nurseCheckAccount, Data.default_user_login_account, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndInput("输入密码", WnInpatientXpath.nurseCheckPwd, Data.default_user_login_pwd, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定按钮", WnInpatientXpath.nurseCheckConfirmButton, Framework.defaultTimeoutMax);
            }
//            wnwd.checkElementByXpath("等待执行成功提示", WnInpatientXpath.executeSucMsg, Framework.defaultTimeoutMax);
        }
      
        //提交退药申请
        public void dispensingBackApply(String patientName) {
            wnwd.waitElementByXpathAndClick("点击首页", WnInpatientXpath.homePage, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击物资管理标签", "//span[contains(text(),'物资管理')]", Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndClick("点击退药申请标签", "//span[contains(text(),'退药申请')]", Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndInput("搜索患者", "//div[contains(@class,'search-input')]//input", patientName, Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndClick("选中患者", "//li[contains(.,'"+patientName+"')]//span[@class='checkbox-icon']", Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndClick("全选需要退药的医嘱", "//div[@class='title-main']//label", Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndInput("输入退药数量", "//td[contains(@class,'inputReturnNum')]//input", "1", Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndClick("点击提交申请按钮", "//span[contains(text(),'提交申请')]", Framework.defaultTimeoutMid);
            wnwd.checkElementByXpath("退药预览弹框", "//div[@class='el-dialog']//span[contains(text(),'退药预览')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击发送申请按钮", "//span[contains(text(),'发送申请')]", Framework.defaultTimeoutMid);
    		wnwd.checkElementByXpath("等待退药申请提交成功标识", "//p[contains(text(),'退药申请提交成功')]", Framework.defaultTimeoutMax);
        }

        /**处置内容另存为模板
         * @param  templateName 模板名称
         * @param templateType 模板的所属范围：科室，个人
         * @param disease 诊断
         */
        public void addTemplate (String templateName, String templateType, String disease){
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("医嘱全选", WnInpatientXpath.orderCheckBox, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点另存为模板", WnInpatientXpath.addTemplateButton.replace("?", "另存为模板"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpath("弹出另存为模板对话框", WnInpatientXpath.dialogSaveAsTemplate, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndInput("输入模板名称", WnInpatientXpath.dialogSaveAsTemplateInput, templateName, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("模板的所属范围", WnInpatientXpath.templateAppScopeType.replace("?", templateType), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("添加诊断", WnInpatientXpath.addDiagnostic, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击诊断类型", WnInpatientXpath.DiagnosticTypeSelect, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("选择西医诊断", WnInpatientXpath.DiagnosticType, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndInput("输入诊断进行搜索", WnInpatientXpath.DiagnosticSearchInput, disease, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("选择第一个搜索结果", WnInpatientXpath.DiagnosticCheckBox, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("确定", WnInpatientXpath.dialogSaveAsTemplateConfirm, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpath(WnInpatientXpath.saveSucMsg, Framework.defaultTimeoutMax);
        }
        //搜索医嘱模板
        public void searchTemplate (String templateName, String templateType){
            wnwd.waitElementByXpathAndClick("点医嘱模板按钮", WnInpatientXpath.orderButton.replace("?", "医嘱模板"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpath("等待医嘱模板选择页面出现", WnInpatientXpath.orderTemplatePage, Framework.defaultTimeoutMax);
            if (templateType.equals("科室")) {
                wnwd.waitElementByXpathAndClick("模板的所属范围：本科室", WnInpatientXpath.orderTemplateTypeRadio.replace("?", "本科室"), Framework.defaultTimeoutMin);
            } else {
                wnwd.waitElementByXpathAndClick("模板的所属范围：本人", WnInpatientXpath.orderTemplateTypeRadio.replace("?", "本人"), Framework.defaultTimeoutMin);
            }
            wnwd.waitElementByXpathAndInput("输入模板名称", WnInpatientXpath.orderTemplateSearchInput, templateName, Framework.defaultTimeoutMax);
            WebElement template = wnwd.checkElementByXpath("医嘱模板搜索结果", WnInpatientXpath.orderTemplate, Framework.defaultTimeoutMax);
            logger.assertFalse(template == null, "医嘱模板不存在!");
            wnwd.wnClickElement(template, "选择模板");
        }
        //医嘱模板开立
        public void quoteTemplate (String templateName, String templateType){
            searchTemplate(templateName, templateType);
            wnwd.waitElementByXpathAndClick("点开立按钮", WnInpatientXpath.orderQuoteButton, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载动画结束", "//div[@class='win-loading']", Framework.defaultTimeoutMax);
        }
        //删除医嘱模板
        public void deleteTemplate (String templateName, String templateType){
            wnwd.waitNotExistByXpath("等待加载动画结束", "//div[@class='win-loading']", Framework.defaultTimeoutMax);
            searchTemplate(templateName, templateType);
            wnwd.waitElementByXpathAndClick("更多操作", WnInpatientXpath.orderTemplateIconMore.replace("?", templateName), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("删除模板", WnInpatientXpath.orderTemplateDeleteButton, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpath("等待弹出提示框", WnInpatientXpath.orderTemplateDeleteWarn, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("更多操作", WnInpatientXpath.orderTemplateConfirmButton, Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载动画结束", "//div[@class='win-loading']", Framework.defaultTimeoutMax);
        }
        // 收藏医嘱,并返回医嘱所在加工厂类型
        // name:输入的名称
        public String searchOrderForCollect (String name){
            wnwd.sleep(1000);
            wnwd.waitElementByXpathAndClick("点击下拉选择", WnInpatientXpath.orderSearchIcon, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击下拉选择", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "全部"), Framework.defaultTimeoutMin);
            WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
                    WnInpatientXpath.inpatientSearchOrderInput, Framework.defaultTimeoutMax);
            wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
            wnwd.checkElementByXpath("医嘱搜索结果", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox,
                    Framework.defaultTimeoutMax);
            wnwd.sleep(1000);
            wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
            wnwd.sleep(Framework.defaultTimeoutMin);
            WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
            //查找所有搜索结果
            List<WebElement> searchResults = resultBox.findElements(By.xpath(WnInpatientXpath.inpatientSearchOrderAllResultList));
            // 搜索结果是空 直接报错
            if (searchResults == null || searchResults.size() == 0) {
                logger.assertFalse(true, "60无搜索结果", "输入:" + name);
            }
            List<WebElement> ele = wnwd.waitElementListByXpath(WnInpatientXpath.orderCollectIcon, Framework.defaultTimeoutMin);
            try {
                if (ele.get(0).getAttribute("class").contains("win-icon-collected")) {
                    logger.log(1, "该药品当前已收藏");
                } else if (ele.get(0).getAttribute("class").contains("win-icon-uncollected")) {
                    logger.log(1, "该药品当前未收藏");
                    wnwd.wnClickElement(ele.get(0), "点收藏图标");
                    wnwd.checkElementByXpath("收藏提示", WnInpatientXpath.sucMsg.replace("?", "收藏成功"), Framework.defaultTimeoutMid);
                    wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
                }
            } catch (Throwable e) {
                throw new Error("没找到收藏相关Xpath:" + e.getMessage());
            }
            wnwd.wnClickElement(searchResults.get(0), "医嘱搜索结果第一个");
            String detailType = getDetailType();
            wnwd.waitElementByXpathAndClick("点击取消", WnInpatientXpath.inpatientDisposalFactoryCancelButton, Framework.defaultTimeoutMax);
            return detailType;
        }
        // 通过搜索取消收藏医嘱
        // name:输入的名称
        public void searchOrderForUnCollect (String name){
            wnwd.sleep(1000);
            wnwd.waitElementByXpathAndClick("点击下拉选择", WnInpatientXpath.orderSearchIcon, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击下拉选择", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "全部"), Framework.defaultTimeoutMin);
            WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
                    WnInpatientXpath.inpatientSearchOrderInput, Framework.defaultTimeoutMax);
            wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
            wnwd.checkElementByXpath("医嘱搜索结果", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox,
                    Framework.defaultTimeoutMax);
            wnwd.sleep(1000);
            wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
            wnwd.sleep(Framework.defaultTimeoutMin);
            WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnInpatientXpath.inpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
            //查找所有搜索结果
            List<WebElement> searchResults = resultBox.findElements(By.xpath(WnInpatientXpath.inpatientSearchOrderAllResultList));
            // 搜索结果是空 直接报错
            if (searchResults == null || searchResults.size() == 0) {
                logger.assertFalse(true, "60无搜索结果", "输入:" + name);
            }
            List<WebElement> ele = wnwd.waitElementListByXpath(WnInpatientXpath.orderCollectIcon, Framework.defaultTimeoutMin);
            try {
                if (ele.get(0).getAttribute("class").contains("win-icon-collected")) {
                    ele.get(0).click();
                    wnwd.checkElementByXpath("取消收藏提示", WnInpatientXpath.sucMsg.replace("?", "取消收藏成功"), Framework.defaultTimeoutMid);
                    wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
                    logger.log(1, "该药品已取消收藏");
                } else if (ele.get(0).getAttribute("class").contains("win-icon-uncollected")) {
                    logger.log(1, "该药品当前未收藏");
                }
            } catch (Throwable e) {
                throw new Error("没找到医嘱:" + e.getMessage());
            }
        }
        //收藏开立
        public void collectOrder (String name){
            wnwd.sleep(1000);
            String detailType = searchOrderForCollect(name);
            System.out.println("detailType:" + detailType);
            wnwd.waitElementByXpathAndClick("点击下拉选择", WnInpatientXpath.orderSearchIcon, Framework.defaultTimeoutMin);
            if (detailType.equals("drug")) {
                wnwd.checkElementByXpath("点击加工厂西药", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "西成药"), Framework.defaultTimeoutMid).click();
            } else if (detailType.equals("herb")) {
                wnwd.checkElementByXpath("点击加工厂草药", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "草药"), Framework.defaultTimeoutMid).click();
            } else if (detailType.equals("lab")) {
                wnwd.checkElementByXpath("点击加工厂检验", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "检验"), Framework.defaultTimeoutMid).click();
            } else if (detailType.equals("exam")) {
                wnwd.checkElementByXpath("点击加工厂检查", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "检查"), Framework.defaultTimeoutMid).click();
            } else if (detailType.equals("treatment")) {
                wnwd.checkElementByXpath("点击加工厂治疗", WnInpatientXpath.inpatientDisposalFactoryButton.replace("?", "治疗"), Framework.defaultTimeoutMid).click();
            } else {
                logger.assertFalse(true, "该类型医嘱暂不支持收藏开立！");
            }
            WebElement ele = wnwd.checkElementByXpath("医嘱是否已添加到收藏夹", WnInpatientXpath.collectedOrder.replace("?", name), Framework.defaultTimeoutMax);
            wnwd.wnClickElement(ele, "点击收藏医嘱");
            wnwd.waitElementByXpathAndClick("点确认按钮", WnInpatientXpath.inpatientOrderConfirmButton, Framework.defaultTimeoutMin);
        }
        //历史医嘱开立
        public void historicalOrder () {
            try {
                wnwd.waitElementByXpathAndClick("点历史医嘱按钮", WnInpatientXpath.orderButton.replace("?", "历史医嘱"), Framework.defaultTimeoutMin);
                wnwd.waitElementByXpath("等待页面出现", WnInpatientXpath.historicalOrderPage, Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("点历史医嘱", WnInpatientXpath.historicalOrderInpatientButton, Framework.defaultTimeoutMin);
                List<WebElement> eleList = wnwd.waitElementListByXpath(WnInpatientXpath.historicalOrderRecord, Framework.defaultTimeoutMax);
                if (eleList == null || eleList.size() == 0) {
                    logger.assertFalse(true, "无历史医嘱");
                }
                //勾选第一条历史医嘱
                wnwd.waitElementByXpathAndClick("勾选第一条记录的复选框", WnInpatientXpath.historicalOrderRecordCheckBox, Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("点开立按钮", WnInpatientXpath.historicalOrderPrescribeButton, Framework.defaultTimeoutMin);
                wnwd.waitNotExistByXpath("等待加载动画结束", "//div[@class='win-loading']", Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "选择历史医嘱失败");
            }
        }
        //复制医嘱
        public void copyOrder () {
            try {
                wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("医嘱全选", WnInpatientXpath.orderCheckBox, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点复制", WnInpatientXpath.addTemplateButton.replace("?", "复制"), Framework.defaultTimeoutMin);
            } catch (Throwable e) {
                logger.assertFalse(true, "医嘱复制失败");
            }
        }
        //复制到剪切板
        public void copyToClipBoard () {
            try {
                wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("医嘱全选", WnInpatientXpath.orderCheckBox, Framework.defaultTimeoutMax);
                WebElement ele = wnwd.waitElementByXpath(WnInpatientXpath.copyOrderDropdown, Framework.defaultTimeoutMin);
                wnwd.moveToElement(ele, "鼠标移动到下拉框");
                wnwd.waitElementByXpathAndClick("点复制到剪切板", WnInpatientXpath.copyOrderDropdownItem.replace("?", "复制到剪切板"), Framework.defaultTimeoutMin);
            } catch (Throwable e) {
                logger.assertFalse(true, "医嘱复制到剪切板失败");
            }
        }
        //剪切板开立
        public void clipBoardToPrescribeOrder () {
            try {
                wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点剪切板", WnInpatientXpath.orderButton.replace("?", "剪切板"), Framework.defaultTimeoutMin);
                wnwd.waitElementByXpath("打开剪切板页面", WnInpatientXpath.clinicalClipBoard, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpath("全选", WnInpatientXpath.clinicalClipBoardCheckbox, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpath("点开立按钮", WnInpatientXpath.clinicalClipBoardPrescribeButton, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "剪切板开立失败");
            }
        }
        //复制并停止医嘱
        public void copyAndStopOrder () {
            try {
                wnwd.waitNotExistByXpath("等待加载框消失", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("医嘱全选", WnInpatientXpath.orderCheckBox, Framework.defaultTimeoutMax);
                WebElement ele = wnwd.waitElementByXpath(WnInpatientXpath.copyOrderDropdown, Framework.defaultTimeoutMin);
                wnwd.moveToElement(ele, "鼠标移动到下拉框");
                wnwd.waitElementByXpathAndClick("点复制并停止", WnInpatientXpath.copyOrderDropdownItem.replace("?", "复制并停止"), Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定", WnInpatientXpath.confirmButton, Framework.defaultTimeoutMin);
                wnwd.checkElementByXpath("停止医嘱成功提示", WnInpatientXpath.sucMsg.replace("?", "停止医嘱成功"), Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "复制并停止医嘱失败");
            }
        }

    // 住院病历审签流程查询与设置，返回是否可执行审签操作标志
    public Boolean setParamsForEmrAudit (List<String>EmrMonitorNameList) {
           Boolean Flag = false;
        if(Data.modeConfiguration.equals("develop")) {
            Boolean auditFlag = queryEmrReviewMonitoring(EmrMonitorNameList);
            if (auditFlag) {
                logger.boxLog(1, "病历已配置审签流程", "");
            } else {
                logger.boxLog(2, "存在病历未配置审签流程", "系统将自动配置住院病历的二级审签流程");
                setEmrReviewMonitoring(EmrMonitorNameList);
            }
            Flag = true;
        }else if(Data.modeConfiguration.equals("release")) {
            Boolean auditFlag = queryEmrReviewMonitoring(EmrMonitorNameList);
            if (auditFlag) {
                logger.boxLog(1, "病历已配置审签流程", "");
                Flag = true;
            } else {
                logger.boxLog(2, "病历审核操作不执行,若要验证病历审核,请手动配置住院病历的二级或三级审签流程", "");
            }
        }
        return Flag;
    }


    /** 查询住院病历是否配置了二级审签、三级审签
     * @param inpMrtNameList 住院病历模板名称list
     */
    public Boolean queryEmrReviewMonitoring (List<String>inpMrtNameList){
        Boolean auditFlag = false;
        List<String> EmrMonitorNoList = new ArrayList<>();
        for (String inpMrtName : inpMrtNameList) {
            EmrMonitorNoList.add(db60.getInpMrtMonitorNo(inpMrtName));
        }
        System.out.println("EmrMonitorNoList:"+EmrMonitorNoList.toString());
        String url = "http://" + Data.host + "/emr-mdm/api/v2/app_record_mdm/inpatient_emr/emr_review_setting/query";
        Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
        String json = "{\"mrtEditorTypeCode\":\"399461576\",\"mrtEditorTypeName\":\"都昌\"}";
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Version", "1.1");
        header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
        test.sendPostRequest(json, null, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            JsonParser parser = new JsonParser();
            JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
            JsonObject dataObject = resJson.getAsJsonObject("data");
            JsonArray monitors = dataObject.getAsJsonArray("monitors");
            if(monitors!= null){
                for(JsonElement info:monitors){
                    if(info.getAsJsonObject().get("inpEmrReviewLevel").getAsString().equals("399555151")){
                        JsonArray jsonList = info.getAsJsonObject().get("inpMrtMonitorNos").getAsJsonArray();
                        Gson gson1=new Gson();
                        List<String> monitorsList= gson1.fromJson(jsonList, new TypeToken<List<String>>() {}.getType());
                       auditFlag = monitorsList.containsAll(EmrMonitorNoList);
                       if(auditFlag){
                           logger.log(1,"病历已配置二级审签");
                           break;
                       }else{
                           logger.log(2,"存在病历未配置二级审签");
                       }
                    }else if(info.getAsJsonObject().get("inpEmrReviewLevel").getAsString().equals("399555150")){
                        JsonArray jsonList = info.getAsJsonObject().get("inpMrtMonitorNos").getAsJsonArray();
                        Gson gson1=new Gson();
                        List<String> monitorsList= gson1.fromJson(jsonList, new TypeToken<List<String>>() {}.getType());
                        auditFlag = monitorsList.containsAll(EmrMonitorNoList);
                        if(auditFlag){
                            logger.log(1,"病历已配置三级审签");
                            break;
                        }else{
                            logger.log(2,"存在病历未配置三级审签");
                        }
                    }
                }
            }else{
                logger.log(2,"系统未配置住院病历审签流程");
            }
        } else {
            logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
                    + test.getResponseContent());
        }
        return  auditFlag;
    }




    /** 设置住院病历二级审签
         * @param inpMrtNameList 住院病历模板名称list
         */
        public void setEmrReviewMonitoring (List < String > inpMrtNameList){
            List<String> EmrMonitorNoList = new ArrayList<>();
            for (String inpMrtName : inpMrtNameList) {
                EmrMonitorNoList.add(db60.getInpMrtMonitorNo(inpMrtName));
            }
            String InpEmrReviewProcessId1 = db60.getInpEmrReviewProcessId("399555152"); //上级审签流程ID
            String InpEmrReviewProcessId2 = db60.getInpEmrReviewProcessId("399555151"); //二级审签流程ID
            String InpEmrReviewProcessId3 = db60.getInpEmrReviewProcessId("399555150"); //三级审签流程ID
            if ((InpEmrReviewProcessId3 == "") || (InpEmrReviewProcessId2 == "") || (InpEmrReviewProcessId1 == "")) {
                logger.assertFalse(true, "住院病历审阅流程不存在或配置有问题");
            }
            String EmrMonitorNos = new Gson().toJson(EmrMonitorNoList);
            String url = "http://" + Data.host + "/emr-mdm/api/v2/app_record_mdm/inpatient_emr/emr_review_setting/save";
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            String json = "{\"enabledFlag\":\"98360\",\"monitors\":[{\"inpEmrReviewSettingId\":\""+ InpEmrReviewProcessId1 +"\",\"sameLevelReviewEnabledFlag\":\"98360\",\"inpEmrReviewLevel\":\"399555152\",\"inpMrtMonitorNos\":[]},{\"inpEmrReviewSettingId\":\""+ InpEmrReviewProcessId2 +"\",\"sameLevelReviewEnabledFlag\":\"98360\",\"inpEmrReviewLevel\":\"399555151\",\"inpMrtMonitorNos\":"+ EmrMonitorNos +"},{\"inpEmrReviewSettingId\":\""+ InpEmrReviewProcessId3 +"\",\"sameLevelReviewEnabledFlag\":\"98360\",\"inpEmrReviewLevel\":\"399555150\",\"inpMrtMonitorNos\":[]}],\"mrtEditorTypeCode\":\"399461576\",\"mrtEditorTypeName\":\"都昌\"}";
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Version", "1.1");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "1111");
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
                        + test.getResponseContent());
            }
        }


        //住院病历创建、编辑、提交
        public void inpatientsEmr (String EMR){
            if (EMR.toUpperCase().equals("NONE")) {
                return;
            }
            emrCreate(EMR);
            emrEdit();
            emrSubmit();
        }


        //创建病历
        public void emrCreate (String EMR){
            wnwd.waitElementByXpathAndClick("点击住院病历标签", WnInpatientXpath.inpatientMenu.replace("?", "住院病历"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击待书写文书list",WnInpatientXpath.inpatientEmrList, Framework.defaultTimeoutMax);
            WebElement ele = wnwd.getElementByXpath(WnInpatientXpath.inpatientEmrAddButton);
            if(ele!= null){
                wnwd.waitElementByXpathAndClick("点击新增按钮", WnInpatientXpath.inpatientEmrAddButton, Framework.defaultTimeoutMax);
            }else{
                wnwd.waitElementByXpathAndClick("点击新增按钮", WnInpatientXpath.inpatientEmrCreateButton, Framework.defaultTimeoutMax);
            }
            WebElement ele1 = wnwd.waitElementByXpath(WnInpatientXpath.inpatientEmrBatchCreateDialog, Framework.defaultTimeoutMax);
            if(ele1!= null){
                wnwd.waitElementByXpathAndClick("点否", WnInpatientXpath.inpatientEmrNoButton, Framework.defaultTimeoutMax);
            }
            wnwd.waitElementByXpath("等待模板选择框出现", WnInpatientXpath.inpatientEmrTemplateSelectPage, 5000);
            //根据住院病历模板查找病历目录分类
            String EmrClassName = db60.getInpEmrClassName(EMR);
            if (EmrClassName == "") {
                logger.assertFalse(true, "病历模板不存在或模板配置有问题");
            }
            wnwd.waitElementByXpathAndClick("点击病历目录分类", WnInpatientXpath.inpatientEmrClassName.replace("?", EmrClassName), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击住院页签", WnInpatientXpath.inpatientEmrTemplateSelectSheet, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入病历模板名称", WnInpatientXpath.inpatientEmrTemplateSearchInput, EMR, Framework.defaultTimeoutMax);
            wnwd.sendKeyEvent(Keys.ENTER);
            wnwd.waitElementByXpathAndClick("选择病历模板", WnInpatientXpath.inpatientEmrTemplate.replace("?", EMR), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击确定按钮", WnInpatientXpath.inpatientConfirmButton, Framework.defaultTimeoutMax);
        }

        //编辑住院病历
        public void emrEdit(){
            try {
                WebElement frameElement = wnwd.waitElementByXpath("切换frame", "//iframe", Framework.defaultTimeoutMax);
                wnwd.switchToFrame(frameElement);
                wnwd.sleep(Framework.defaultTimeoutMin);
                //获取所有必填文本框，并赋值
                List<WebElement> elelist1 = wnwd.getElementListByXpath(WnInpatientXpath.requiredInput);
                logger.log(1, "文本框数量:" + elelist1.size());
                if (elelist1.size() > 0) {
                    for (WebElement ele1 : elelist1) {
                        wnwd.setElementAttribute1(ele1, "住院病历测试");
                        logger.log(1, ele1.getAttribute("name") + "赋值成功");
                    }

                }
                //获取所有必填数值框，并赋值
                List<WebElement> elelist2 = wnwd.getElementListByXpath(WnInpatientXpath.requiredNumeric);
                logger.log(1, "数值输入框数量:" + elelist2.size());
                if (elelist2.size() > 0) {
                    for (WebElement ele2 : elelist2) {
                        wnwd.setElementAttribute1(ele2, "1");
                        logger.log(1, ele2.getAttribute("name") + "赋值成功");
                    }
                }
                //获取所有必填的下拉框，并赋值
                List<WebElement> elelist3 = wnwd.getElementListByXpath(WnInpatientXpath.requiredDropdownList);
                logger.log(1, "下拉框数量:" + elelist3.size());
                if (elelist3.size() > 0) {
                    for (WebElement ele3 : elelist3) {
                        String Attribute = ele3.getAttribute("lt0");
                        wnwd.setElementAttribute1(ele3, Attribute);
                        logger.log(1, ele3.getAttribute("name") + "赋值成功");
                    }
                }
            } catch (Throwable e) {
                logger.assertFalse(true, "编辑住院病历失败");
            }
            wnwd.switchToParentFrame();
        }


        //住院病历提交
        public void emrSubmit () {
            WebElement patientInfoDialogSaveButton = wnwd.waitElementByXpath("提交按钮", WnInpatientXpath.inpatientSubmitButton, Framework.defaultTimeoutMax);
            patientInfoDialogSaveButton.sendKeys(Keys.ENTER);
            wnwd.waitElementByXpathAndClick("点击签名按钮", WnInpatientXpath.inpatientSignButton, Framework.defaultTimeoutMax);
            wnwd.sleep(Framework.defaultTimeoutMin);
            wnwd.checkElementByXpath("显示撤销提交按钮", WnInpatientXpath.inpatientCancelSubmitButton, Framework.defaultTimeoutMax);
        }

        //住院病历审签通过
        public void emrAuditPass (String EMR, String patientName){
            wnwd.waitElementByXpathAndClick("点击住院病历标签", WnInpatientXpath.inpatientMenu.replace("?", "住院病历"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击住院病历", WnInpatientXpath.inpatientEmrSheet.replace("?", EMR), Framework.defaultTimeoutMax);
            WebElement emrAuditButton = wnwd.waitElementByXpath("审签通过按钮", WnInpatientXpath.inpatientPassButton, Framework.defaultTimeoutMax);
            emrAuditButton.sendKeys(Keys.ENTER);
            wnwd.waitElementByXpathAndClick("点击签名按钮", WnInpatientXpath.inpatientSignButton, Framework.defaultTimeoutMax);
            wnwd.sleep(Framework.defaultTimeoutMin);
            wnwd.checkElementByXpath("审签通过后显示撤销审签按钮", WnInpatientXpath.inpatientCancelAuditButton, Framework.defaultTimeoutMax);
        }

        //住院病历审签退回
        public void emrAuditReject (String EMR, String patientName){
            wnwd.waitElementByXpathAndClick("点击住院病历标签", WnInpatientXpath.inpatientMenu.replace("?", "住院病历"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击住院病历", WnInpatientXpath.inpatientEmrSheet.replace("?", EMR), Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpath("等待病历编辑页面加载完成", WnInpatientXpath.DocumentHeaderPatitenName.replace("?", patientName), Frmcons.defaultTimeoutMax);
            WebElement emrAuditButton = wnwd.waitElementByXpath("审签退回按钮", WnInpatientXpath.inpatientRejectButton, Framework.defaultTimeoutMax);
            emrAuditButton.sendKeys(Keys.ENTER);
            wnwd.sleep(Framework.defaultTimeoutMin);
            wnwd.checkElementByXpath("显示置灰的提交按钮", WnInpatientXpath.inpatientSubmitButton, Framework.defaultTimeoutMax);
        }

        //住院病历撤销审签
        public void emrCancelAudit (String EMR, String patientName){
            wnwd.waitElementByXpathAndClick("点击住院病历标签", WnInpatientXpath.inpatientMenu.replace("?", "住院病历"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击住院病历", WnInpatientXpath.inpatientEmrSheet.replace("?", EMR), Framework.defaultTimeoutMax);
//        wnwd.waitElementByXpath("等待病历编辑页面加载完成", WnInpatientXpath.DocumentHeaderPatitenName.replace("?", patientName), Frmcons.defaultTimeoutMax);
            WebElement emrAuditButton = wnwd.waitElementByXpath("撤销审签按钮", WnInpatientXpath.inpatientCancelAuditButton, Framework.defaultTimeoutMax);
            emrAuditButton.sendKeys(Keys.ENTER);
            wnwd.sleep(Framework.defaultTimeoutMin);
            wnwd.checkElementByXpath("审签退回后显示审签通过按钮", WnInpatientXpath.inpatientPassButton, Framework.defaultTimeoutMax);
        }


        //退出系统
        public void WnlogOut () {
            wnwd.waitElementByXpathAndClick("点击图标", WnInpatientXpath.InpatientUserInfoDropdown, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击退出登录", WnInpatientXpath.InpatientLogout, Framework.defaultTimeoutMid);
            wnwd.waitElementByXpathAndClick("点击确定", WnInpatientXpath.confirmButton, Framework.defaultTimeoutMin);
        }


        // 入院登记
        public ArrayList<String> hisAdmissionRegistration () {
            return hisAdmissionRegistration(null, null);
        }

        // 入院登记   指定  病区 和 科室 和
        public ArrayList<String> hisAdmissionRegistration (String ward, String subjectCode){
            ArrayList<String> patInfo = null;
            int count = 0;
            while (patInfo == null && count < 3) {
                count++;
                try {
                    if (Data.hisType.equals("WINEX")) {
                        patInfo = admissionRegistrationForHisJTWN(Data.default_user_login_account, Data.default_user_login_pwd, Data.test_select_subject, Data.newEncounterSubjectCode, Data.inpatient_select_ward, Data.inpatient_select_ward_code);
                    } else if (Data.hisType.equals("TJWN")) {
                        patInfo = admissionRegistrationForHisTJWN(Data.inpatient_select_ward, Data.test_select_subject);
                    }
                } catch (Throwable e) {
                    logger.log(3, "入院登记失败 重试");
                }
            }
            logger.assertFalse(patInfo == null, "入院登记失败");
            return patInfo;
        }


        /**
         * 天津卫宁入院登记
         *
         * @param wardName
         * @param subjectName
         * @return
         */
        public ArrayList<String> admissionRegistrationForHisTJWN (String wardName, String subjectName){
            while (Data.inAdmissionRegistering) {
                SdkTools.logger.log(3, "其它线程正在入院登记,稍后再试");
                logger.log(3, "其它线程正在入院登记,稍后再试");
                SdkTools.sleep(5000);
            }

            try {
                // 设置为正在入院登记为true
                Data.inAdmissionRegistering = true;
                String url = "http://" + Data.host + ":" + Data.fhir_port + "/fhir/$process-message";
                String sql1 = new HisSqlManager().getGetPatientIdWithTDJKZ();
                Map<String, String> info = db.queryFirstRow(sql1);
                Map<String, String> value = db.queryFirstRow(new HisSqlManager().getPatientIdSql());
//        String ZYXH = info.get("ZYXH").trim();
                String ZYH = info.get("DQZYH").trim();  //当前住院号
                String BAH = info.get("DQBAH").trim();  //当前病案号
                String YJH = info.get("DQYJH").trim();  //当前病案号
                String PATIENTID = value.get("PATIENTID").trim(); //当前patientid
                String IDCode = IdCardGenerator.IDCardCreate();
                String patientName = "测试患者" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ((int) ((Math.random() * 9 + 1) * 100000)) + "";
                String XMPY = SdkTools.ChineseCharToEnUtil.getAllFirstLetter(patientName);
                if (XMPY.length() > 10) {
                    XMPY = XMPY.substring(0, 10);
                }

//        ZYXH = String.format("%0" + ZYXH.length() + "d", Integer.parseInt(ZYXH) + 1);
                ZYH = String.format("%08d", Integer.parseInt(ZYH) + 1);
//        PATIENTID = String.format("%0" + PATIENTID.length() + "d", Integer.parseInt(PATIENTID) + 1);
                YJH = Integer.toString(Integer.parseInt(YJH) + 1);
                BAH = Integer.toString(Integer.parseInt(BAH) + 1);

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = dateFormat.format(date);
                String sex = IdCardGenerator.getBirthdayAgeSex(IDCode).get("sex");
                String birthday = IdCardGenerator.getBirthdayAgeSex(IDCode).get("birthday");
                String age = IdCardGenerator.getBirthdayAgeSex(IDCode).get("age");

                DateFormat valueDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                String valueDateTime = valueDate.format(date);
                String gender = Integer.parseInt(sex) % 2 == 0 ? "1.2.156.112604.1.2.5.2" : "1.2.156.112604.1.2.5.3";

                //根据病区名称、科室名称获取病区编码、科室编码
                String sql2 = new HisSqlManager().getGetBQBMByBQMC(wardName);
                Map<String, String> info2 = db.queryFirstRow(sql2);
                String wardCode = info2.get("BQBM").trim();
                System.out.println(wardCode);
                String sql3 = new HisSqlManager().getGetKSBMByKSMC(subjectName);
                System.out.println(wardCode);

                Map<String, String> info3 = db.queryFirstRow(sql3);
                System.out.println(wardCode);

                String subjectCode = info3.get("KSBM").trim();
                System.out.println(subjectCode);
                System.out.println(wardCode.getClass());
                String insertSQL = new HisSqlManager().setPatientIdWithTDJKZ(ZYH, patientName, XMPY, sex, birthday, age, IDCode, currentDate, wardCode, subjectCode, Data.inpatient_deposit,PATIENTID);
                System.out.println(insertSQL);
                db.excute(insertSQL);

                //HIS数据库插入在院病人费用帐页tfyz表记录
                String sql4 = new HisSqlManager().getTfyzSql();
                Map<String, String> info4 = db.queryFirstRow(sql4);
                String DJXH = info4.get("DJXH").trim();
                String insertSetTfyzSql = new HisSqlManager().setTfyzSql(DJXH, ZYH, wardCode, currentDate);
                db.excute(insertSetTfyzSql);

                //HIS数据库插入住院押金明细记录
                String insertZyyjmxkSql = new HisSqlManager().setZyyjmxkSql(ZYH, currentDate, Data.inpatient_deposit);
                db.excute(insertZyyjmxkSql);

                //更新tzyhk表当前住院号、当前押金号、当前病案号
                String newZYH = ZYH.replaceAll("^(0+)", "");
                System.out.println("newZHY:" + newZYH);
                String updateTzyhk = new HisSqlManager().updateTzyhk(newZYH, YJH, BAH);
                db.excute(updateTzyhk);

                HttpTestUrl httpTestUrl = new HttpTestUrl(url);
                HttpTest test = new HttpTest(httpTestUrl);
                HttpTestHeader header = new HttpTestHeader();
                header.addHeader("Version", "1.1");

                String request = "{\n" +
                        "  \"resourceType\": \"Bundle\",\n" +
                        "  \"meta\": {\n" +
                        "    \"extension\": [\n" +
                        "      {\n" +
                        "        \"url\": \"https://simplifier.net/winningtest/extension-tenantid\",\n" +
                        "        \"valueId\": \"" + Data.hospital_soid + "\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  \"type\": \"message\",\n" +
                        "  \"entry\": [\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"MessageHeader\",\n" +
                        "        \"contained\": [\n" +
                        "          {\n" +
                        "            \"resourceType\": \"PractitionerRole\",\n" +
                        "            \"id\": \"e3718482-ce6f-4e3d-8730-a478549ad45b\",\n" +
                        "            \"practitioner\": {\n" +
                        "              \"identifier\": {\n" +
                        "                \"system\": \"urn:oid:1.2.156.112604.1.1.2472\",\n" +
                        "                \"value\": \"0000\"\n" +
                        "              }\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"extension\": [\n" +
                        "          {\n" +
                        "            \"url\": \"https://simplifier.net/winningtest/extension-event-time\",\n" +
                        "            \"valueDateTime\": \"" + valueDateTime + "\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"eventCoding\": {\n" +
                        "          \"code\": \"InpatientAdmitted\"\n" +
                        "        },\n" +
                        "        \"destination\": [\n" +
                        "          {\n" +
                        "            \"name\": \"CIS6.0\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"enterer\": {\n" +
                        "          \"reference\": \"#e3718482-ce6f-4e3d-8730-a478549ad45b\"\n" +
                        "        },\n" +
                        "        \"source\": {\n" +
                        "          \"name\": \"HIS\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Patient\",\n" +
                        "        \"id\": \"5161107e-2a40-4030-a624-4159b98a3dd1\",\n" +
                        "        \"meta\": {\n" +
                        "          \"profile\": [\n" +
                        "            \"http://www.winning.com.cn/fhir/StructureDefinition/winning-patient\"\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"extension\": [\n" +
                        "          {\n" +
                        "            \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-nationality\",\n" +
                        "            \"extension\": [\n" +
                        "              {\n" +
                        "                \"url\": \"code\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"urn:oid:1.2.156.112604.1.2.8.44\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"url\": \"https://simplifier.net/winningtest/extension-place-of-origin\",\n" +
                        "            \"valueCodeableConcept\": {\n" +
                        "              \"coding\": [\n" +
                        "                {\n" +
                        "                  \"code\": \"天津@天津市\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"url\": \"https://simplifier.net/winningtest/extension-patient-education\",\n" +
                        "            \"valueCodeableConcept\": {\n" +
                        "              \"coding\": [\n" +
                        "                {\n" +
                        "                  \"code\": \"urn:oid:1.2.156.112604.1.2.531.12\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"url\": \"https://simplifier.net/winningtest/extension-patient-ethnicGroup\",\n" +
                        "            \"valueCodeableConcept\": {\n" +
                        "              \"coding\": [\n" +
                        "                {\n" +
                        "                  \"code\": \"urn:oid:1.2.156.112604.1.2.2258.1\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"identifier\": [\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.2.663.15\",\n" +
                        "            \"value\": \"" + PATIENTID + "\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.271\",\n" +
                        "            \"value\": \"" + ZYH + "\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.255\",\n" +
                        "            \"value\": \"" + BAH + "\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.78\",\n" +
                        "            \"value\": \"" + IDCode + "\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.220\",\n" +
                        "            \"value\": \"" + BAH + "\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.318\",\n" +
                        "            \"value\": \"" + ZYH + "\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"active\": true,\n" +
                        "        \"name\": [\n" +
                        "          {\n" +
                        "            \"extension\": [\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-humanname-inputCode\",\n" +
                        "                \"extension\": [\n" +
                        "                  {\n" +
                        "                    \"url\": \"pinyin\",\n" +
                        "                    \"valueString\": \"ceshihuanzhe\"\n" +
                        "                  },\n" +
                        "                  {\n" +
                        "                    \"url\": \"wubi\",\n" +
                        "                    \"valueString\": \"XJ\"\n" +
                        "                  }\n" +
                        "                ]\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"text\": \"" + patientName + "\",\n" +
                        "            \"family\": \"测\",\n" +
                        "            \"given\": [\n" +
                        "              \"试患者\"\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"telecom\": [\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.2.678.1\",\n" +
                        "            \"value\": \"28351396\",\n" +
                        "            \"use\": \"urn:oid:1.2.156.112604.1.2.521.1\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"gender\": \"urn:oid:" + gender + "\",\n" +
                        "        \"birthDate\": \"" + birthday + "\",\n" +
                        "        \"_birthDate\": {\n" +
                        "          \"extension\": [\n" +
                        "            {\n" +
                        "              \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
                        "              \"valueTime\": \"00:00:00\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"address\": [\n" +
                        "          {\n" +
                        "            \"id\": \"a4d0cc39-a5ed-4a33-8232-a36067a4e515\",\n" +
                        "            \"extension\": [\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-city\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"阳泉市\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-county\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"市辖区\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"type\": \"urn:oid:1.2.156.112604.1.2.24.3\",\n" +
                        "            \"postalCode\": \"30000\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"id\": \"3631e594-80f2-47d3-8a1e-81b8ba4fd178\",\n" +
                        "            \"extension\": [\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-province\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"山西省\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-city\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"阳泉市\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-county\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"市辖区\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"type\": \"urn:oid:1.2.156.112604.1.2.24.1\",\n" +
                        "            \"postalCode\": \"3000  \"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"id\": \"e8840ca8-5f9b-4ad0-a1ca-40c87b106c83\",\n" +
                        "            \"extension\": [\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-province\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"山西省\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-city\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"阳泉市\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"url\": \"https://simplifier.net/winningtest/extension-address-county\",\n" +
                        "                \"valueCodeableConcept\": {\n" +
                        "                  \"coding\": [\n" +
                        "                    {\n" +
                        "                      \"code\": \"市辖区\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"type\": \"urn:oid:1.2.156.112604.1.2.24.9\",\n" +
                        "            \"postalCode\": \"30000\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"maritalStatus\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"code\": \"urn:oid:1.2.156.112604.1.2.6.1\"\n" +
                        "            }\n" +
                        "          ],\n" +
                        "          \"text\": \"1\"\n" +
                        "        },\n" +
                        "        \"contact\": [\n" +
                        "          {\n" +
                        "            \"relationship\": [\n" +
                        "              {\n" +
                        "                \"coding\": [\n" +
                        "                  {\n" +
                        "                    \"code\": \"urn:oid:1.2.156.112604.1.7.28.2.50765\"\n" +
                        "                  }\n" +
                        "                ]\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"name\": {\n" +
                        "              \"extension\": [\n" +
                        "                {\n" +
                        "                  \"url\": \"https://simplifier.net/winningtest/extension-humanname-inputCode\",\n" +
                        "                  \"extension\": [\n" +
                        "                    {\n" +
                        "                      \"url\": \"pinyin\"\n" +
                        "                    },\n" +
                        "                    {\n" +
                        "                      \"url\": \"wubi\"\n" +
                        "                    }\n" +
                        "                  ]\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            },\n" +
                        "            \"telecom\": [\n" +
                        "              {\n" +
                        "                \"id\": \"5403bb95-dc29-47ce-9bdb-48814a5bbdfb\",\n" +
                        "                \"system\": \"urn:oid:1.2.156.112604.1.2.678.1\"\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"id\": \"6311ce8b-f995-451b-934f-0e331722a387\",\n" +
                        "            \"relationship\": [\n" +
                        "              {\n" +
                        "                \"coding\": [\n" +
                        "                  {\n" +
                        "                    \"system\": \"http://hl7.org/fhir/valueset-patient-contactrelationship.html\",\n" +
                        "                    \"code\": \"E\"\n" +
                        "                  }\n" +
                        "                ]\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Coverage\",\n" +
                        "        \"id\": \"f6f5064f-1c82-4434-9026-2e1efb733976\",\n" +
                        "        \"type\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"code\": \"ZF\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Account\",\n" +
                        "        \"id\": \"e0b79e99-7aca-42e1-a08e-062e986ce55e\",\n" +
                        "        \"coverage\": [\n" +
                        "          {\n" +
                        "            \"coverage\": {\n" +
                        "              \"reference\": \"Coverage/f6f5064f-1c82-4434-9026-2e1efb733976\"\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Appointment\",\n" +
                        "        \"id\": \"9df00537-afb4-40a9-93c6-5bfa3915b403\",\n" +
                        "        \"meta\": {\n" +
                        "          \"profile\": [\n" +
                        "            \"https://simplifier.net/winningtest/inpatient-appointment-profile\"\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"contained\": [\n" +
                        "          {\n" +
                        "            \"resourceType\": \"Location\",\n" +
                        "            \"id\": \"1\",\n" +
                        "            \"meta\": {\n" +
                        "              \"profile\": [\n" +
                        "                \"https://simplifier.net/winningtest/location-ward-profile\"\n" +
                        "              ]\n" +
                        "            },\n" +
                        "            \"identifier\": [\n" +
                        "              {\n" +
                        "                \"system\": \"urn:oid:1.2.156.112604.1.1.233\",\n" +
                        "                \"value\": \"" + wardCode + "\"\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"resourceType\": \"Location\",\n" +
                        "            \"id\": \"2\",\n" +
                        "            \"meta\": {\n" +
                        "              \"profile\": [\n" +
                        "                \"https://simplifier.net/winningtest/location-bed-profile\"\n" +
                        "              ]\n" +
                        "            },\n" +
                        "            \"identifier\": [\n" +
                        "              {\n" +
                        "                \"system\": \"urn:oid:1.2.156.112604.1.1.228\"\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"identifier\": [\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.3765\",\n" +
                        "            \"value\": \"" + ZYH + "\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"status\": \"booked\",\n" +
                        "        \"participant\": [\n" +
                        "          {\n" +
                        "            \"actor\": {\n" +
                        "              \"reference\": \"Organization/c8bf3a12bd4a47a39cc74f09d2491741\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"actor\": {\n" +
                        "              \"reference\": \"#1\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"actor\": {\n" +
                        "              \"reference\": \"#2\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"actor\": {\n" +
                        "              \"reference\": \"Location/aa0a8a26a12e4960bd3a43571f1b908a\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"actor\": {\n" +
                        "              \"reference\": \"Patient/5161107e-2a40-4030-a624-4159b98a3dd1\"\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Encounter\",\n" +
                        "        \"id\": \"e2ca88ba-d0b9-4d39-9d3f-5a377f25023a\",\n" +
                        "        \"meta\": {\n" +
                        "          \"profile\": [\n" +
                        "            \"https://simplifier.net/winningtest/inpatient-encounter-profile\"\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"identifier\": [\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.2575\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"status\": \"arrived\",\n" +
                        "        \"class\": {\n" +
                        "          \"code\": \"urn:oid:1.2.156.112604.1.2.433.5\",\n" +
                        "          \"display\": \"住院\"\n" +
                        "        },\n" +
                        "        \"priority\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"code\": \"urn:oid:1.2.156.112604.1.2.1073.1\",\n" +
                        "              \"display\": \"1级\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"subject\": {\n" +
                        "          \"reference\": \"Patient/5161107e-2a40-4030-a624-4159b98a3dd1\"\n" +
                        "        },\n" +
                        "        \"participant\": [\n" +
                        "          {\n" +
                        "            \"type\": [\n" +
                        "              {\n" +
                        "                \"coding\": [\n" +
                        "                  {\n" +
                        "                    \"system\": \"http://hl7.org/fhir/ValueSet/encounter-participant-type\",\n" +
                        "                    \"code\": \"PPRF\"\n" +
                        "                  }\n" +
                        "                ]\n" +
                        "              }\n" +
                        "            ],\n" +
                        "            \"individual\": {\n" +
                        "              \"reference\": \"PractitionerRole/e3718482-ce6f-4e3d-8730-a478549ad45b\"\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"appointment\": [\n" +
                        "          {\n" +
                        "            \"reference\": \"Appointment/9df00537-afb4-40a9-93c6-5bfa3915b403\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"period\": {\n" +
                        "          \"start\": \"" + valueDateTime + "\"\n" +
                        "        },\n" +
                        "        \"hospitalization\": {\n" +
                        "          \"extension\": [\n" +
                        "            {\n" +
                        "              \"url\": \"https://simplifier.net/winningtest/extension-hospitalized-times\",\n" +
                        "              \"valueInteger\": 1\n" +
                        "            }\n" +
                        "          ],\n" +
                        "          \"admitSource\": {\n" +
                        "            \"coding\": [\n" +
                        "              {\n" +
                        "                \"code\": \"urn:oid:1.2.156.112604.1.2.284.1\",\n" +
                        "                \"display\": \"门诊\"\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        },\n" +
                        "        \"location\": [\n" +
                        "          {\n" +
                        "            \"location\": {\n" +
                        "              \"reference\": \"Location/location 1\"\n" +
                        "            },\n" +
                        "            \"status\": \"completed\",\n" +
                        "            \"physicalType\": {\n" +
                        "              \"coding\": [\n" +
                        "                {\n" +
                        "                  \"system\": \"http://terminology.hl7.org/CodeSystem/location-physical-type\",\n" +
                        "                  \"code\": \"bu\",\n" +
                        "                  \"display\": \"Building\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"serviceProvider\": {\n" +
                        "          \"reference\": \"https://simplifier.net/winningtest/department-profile\",\n" +
                        "          \"type\": \"Organization\",\n" +
                        "          \"identifier\": {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.2361\",\n" +
                        "            \"value\": \"" + subjectCode + "\"\n" +
                        "          },\n" +
                        "          \"display\": \"" + Data.test_select_subject + "\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"resource\": {\n" +
                        "        \"resourceType\": \"Condition\",\n" +
                        "        \"id\": \"234c4171-3a8b-4f83-aaf6-4c5fdb6b8e6e\",\n" +
                        "        \"meta\": {\n" +
                        "          \"profile\": [\n" +
                        "            \"https://simplifier.net/winningtest/diagnosis-profile\"\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"identifier\": [\n" +
                        "          {\n" +
                        "            \"system\": \"urn:oid:1.2.156.112604.1.1.2295\",\n" +
                        "            \"value\": \"诊断标识\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"clinicalStatus\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"system\": \"http://terminology.hl7.org/CodeSystem/condition-clinical\",\n" +
                        "              \"code\": \"active\",\n" +
                        "              \"display\": \"Active\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"verificationStatus\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"system\": \"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\n" +
                        "              \"code\": \"confirmed\",\n" +
                        "              \"display\": \"Confirmed\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        },\n" +
                        "        \"category\": [\n" +
                        "          {\n" +
                        "            \"coding\": [\n" +
                        "              {\n" +
                        "                \"code\": \"urn:oid:1.2.156.112604.1.2.1329.2\",\n" +
                        "                \"display\": \"门诊诊断\"\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"code\": {\n" +
                        "          \"coding\": [\n" +
                        "            {\n" +
                        "              \"code\": \"A18.815+K87.0*\",\n" +
                        "              \"display\": \"胆管结核\"\n" +
                        "            }\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                test.sendPostRequest(request, header);
                test.waitRequestFinish(30000);
                System.out.println(test.getResponseContent());
                ArrayList<String> syncInfo = new ArrayList<>();
                if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                    syncInfo.add(patientName);
                    JsonParser parser = new JsonParser();
                    JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
                    String encounterId = contentJson.get("data").getAsString();
                    syncInfo.add(encounterId);
                    System.out.println("encounterId:" + encounterId);
                    logger.log("入院登记成功");
                    return syncInfo;
                } else {
                    logger.assertFalse(true, "Fhir同步报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());

                }

                return syncInfo;
            } catch (Throwable e) {
                e.printStackTrace();
                throw new Error(e.getMessage());
            } finally {
                // 设置为正在入院登记为false
                Data.inAdmissionRegistering = false;
            }
        }


        /**
         * 集团HIS接口方式登录，入院登记
         */
        public ArrayList<String> admissionRegistrationForHisJTWN (String username, String password, String
        deptName, String deptOrgNo, String wardName, String wardOrgNo) throws UnsupportedEncodingException {
            //把明文密码倒序再base64加密
            StringBuffer stringBuffer = new StringBuffer(password).reverse();
            password = stringBuffer.toString();
            String base64encodedPassword = Base64.getEncoder().encodeToString(password.getBytes("utf-8"));

            //登陆HIS
            String url = "http://" + Data.host + "/base/api/v1/base/user/confusion_login";
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + base64encodedPassword + "\",\"orgName\":\"\",\"hospitalSOID\":\"" + Data.hospital_soid +  "\",\"locationId\":\"\",\"locationName\":\"\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\"}";
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);

            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "登陆成功");
                //从登陆接口的返回值中获取access_token
                JsonParser parser = new JsonParser();
                JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
                JsonObject dataObject = contentJson.getAsJsonObject("data");
                String Authorization = "Bearer " + dataObject.get("access_token").getAsString();
                header.addHeader("Authorization", Authorization);
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
                return null;
            }


            //保存person信息，获取接口返回的personId、bizRoleId，做为入院登记下个接口的传参
            String patientName = "测试患者" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ((int) ((Math.random() * 9 + 1) * 100000)) + "";
            String IDCode = IdCardGenerator.IDCardCreate();
            String birthday = IdCardGenerator.getBirthdayAgeSex(IDCode).get("birthday");
            String genderCode = IdCardGenerator.getBirthdayAgeSex(IDCode).get("sex");
            if (genderCode.equals("1")) {
                genderCode = "50602";
            } else {
                genderCode = "50603";
            }

            String sql = new HisSqlManager().getMedInstiInsur();
            Map<String, String> value = db60.db.queryFirstRow(sql);
            String medInstiInsurId = value.get("MED_INSTI_INSUR_ID").trim();

            url = "http://" + Data.host + "/encounter-patient/api/v1/person_patient/personal_file/save";
            json = "{\"fullName\":\"" + patientName + "\",\"genderCode\":\"" + genderCode + "\",\"hashContinue\":false,\"bizRoleInsurance\":[{\"defaultFlag\":98175,\"medInstiInsurId\":\""+ medInstiInsurId +"\"}],\"cardMediaFlag\":\"98176\",\"anonym\":null,\"birthDate\":\"" + birthday + "\",\"birthTime\":\"00:00\",\"hashRealNameAuthByManMade\":false,\"idcardNo\":\"" + IDCode + "\",\"idcardTypeCode\":\"152626\",\"maritalStatusCode\":\"50605\",\"nationCode\":\"951807\",\"nationalityCode\":\"101298\",\"realNameAuthLevelCode\":\"255799\",\"handleType\":\"save\",\"medInstiInsurId\":\"136217280056981504\",\"medInsurUsableRangeCode\":\"959939\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}\"";
            httpTestUrl = new HttpTestUrl(url);
            test = new HttpTest(httpTestUrl);
            System.out.println(json);
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            String personId;
            String bizRoleId;
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "保存个人信息成功");
                //从接口返回值中获取personId、bizRoleId
                String content = test.getResponseContent();
                JsonParser parser = new JsonParser();
                JsonObject contentJson1 = parser.parse(content).getAsJsonObject();
                JsonObject dataObject1 = contentJson1.getAsJsonObject("data");
                personId = dataObject1.get("personId").getAsString();
                bizRoleId = dataObject1.get("bizRoleId").getAsString();
                ;
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
                return null;
            }


            //入院登记保存encounter信息
            String sql1 = new HisSqlManager().getGetOrgIdByOrgName(deptName, deptOrgNo);
            Map<String, String> info = db60.db.queryFirstRow(sql1);
            String admDeptId = info.get("ORG_ID").trim();
            String sql2 = new HisSqlManager().getGetOrgIdByOrgName(wardName, wardOrgNo);
            info = db60.db.queryFirstRow(sql2);
            String admWardId = info.get("ORG_ID").trim();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String admittedAtDate = dateFormat.format(date);
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            String admittedAtTime = dateFormat.format(date);
            url = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/encounter/save";
            json = "{\"bizRoleId\":\"" + bizRoleId + "\",\"admittedAtDate\":\"" + admittedAtDate + "\",\"admittedAtTime\":\"" + admittedAtTime + "\",\"admissionRouteCode\":\"65997\",\"admWardId\":\"" + admWardId + "\",\"admDeptId\":\"" + admDeptId + "\",\"medInstiInsurId\":\"136217280056981504\",\"hospitalizationTimes\":1,\"severityLevelCode\":\"138483\",\"personId\":\"" + personId + "\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
            httpTestUrl = new HttpTestUrl(url);
            test = new HttpTest(httpTestUrl);
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            String encounterId;
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "入院登记成功");
                //从接口返回值中获取encounterId
                String resContent = test.getResponseContent();
                JsonParser parser = new JsonParser();
                JsonObject contentJson2 = parser.parse(resContent).getAsJsonObject();
                JsonObject dataObject2 = contentJson2.getAsJsonObject("data");
                encounterId = dataObject2.get("encounterId").getAsString();
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
                return null;
            }


            //入院登记保存停药线、报警线
            url = "http://" + Data.host + "/finance-fee-inp/api/v1/app_finance_fee_inp/inp_enc_amount_limit/save";
            json = "{\"encounterId\":\"" + encounterId + "\",\"operateBy\":\"57393746696202243\",\"hospitalSOID\":\"" + Data.hospital_soid + "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"inpEncAmountLimitList\":[{\"inpEncAmtLimitTypeCptId\":\"399467836\",\"inpEncAmountLimitTypeDesc\":\"停药线\",\"limitAmount\":1000},{\"inpEncAmtLimitTypeCptId\":\"399467837\",\"inpEncAmountLimitTypeDesc\":\"报警线\",\"limitAmount\":1000}]}";
            httpTestUrl = new HttpTestUrl(url);
            test = new HttpTest(httpTestUrl);
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "入院登记保存停药线、报警线成功");
                ArrayList<String> patientInfoList = new ArrayList<>();
                patientInfoList.add(patientName);
                patientInfoList.add(encounterId);
                return patientInfoList;
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
                return null;
            }

        }

        /**
         * 护理文书
         */
        //新建护理文书
        public void newChart (String chart){
            newChart(new ArrayList<>(Arrays.asList(chart)));
        }

        public void newChart (List < String > chartList) {
            wnwd.waitElementByXpathAndClick("点击护理工作菜单", WnInpatientXpath.NurseMenu.replace("?", "护理工作"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击护理文书菜单", WnInpatientXpath.NurseChildMenu.replace("?", "护理文书"), Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击新建文书按钮", WnInpatientXpath.chartAddButton, Framework.defaultTimeoutMin);
            List<WebElement> checkBoxList = wnwd.waitElementListByXpath(WnInpatientXpath.chartCheckBox, Framework.defaultTimeoutMin);
            for (WebElement webElement : checkBoxList) {
                Boolean chooseFlag = false;
                for (String chart : chartList) {
                    if (webElement.getText().equals(chart)) {
                        chooseFlag = true;
                        break;
                    }
                }
                if (chooseFlag) {
                    wnwd.wnClickElement(webElement, "勾选文书 " + webElement.getText());
                }
            }
            wnwd.waitElementByXpathAndClick("点击确定按钮", WnInpatientXpath.chartConfirmButton, Framework.defaultTimeoutMin);

        }

        //编辑并提交护理文书
        public void editChart (String chart, String patientName){
//            wnwd.waitElementByXpathAndClick("点击护理工作菜单", WnInpatientXpath.NurseMenu.replace("?", "护理工作"), Framework.defaultTimeoutMin);
//            wnwd.waitElementByXpathAndClick("点击护理文书菜单", WnInpatientXpath.NurseChildMenu.replace("?", "护理文书"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("等待加载完成并点击页签", WnInpatientXpath.chartTitle, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击填写", WnInpatientXpath.charEditButton.replace("?", chart), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpath("等待填写页面加载完成", WnInpatientXpath.chartEditTitle.replace("?", chart), Framework.defaultTimeoutMax);
            String chartTemplateClassCode = db60.getChartTemplate(chart).get("CHART_TEMPLATE_CLASS_CODE");
            String chartTemplateId = db60.getChartTemplate(chart).get("CHART_TEMPLATE_ID");
            //模板大类为体征单或记录单,走editRecord方法
            if (chartTemplateClassCode.equals("399282924") || chartTemplateClassCode.equals("399282927")) {
                editRecord(chartTemplateId, patientName);
                submitRecord();
            }
            //模板大类为入院评估单，走editInpatientAssess方法
            if (chartTemplateClassCode.equals("399282925")) {
                editInpatientAssess(chart, chartTemplateId);
                submitRecord();
            }
            //模板大类为评估单，走editAssess方法
            if (chartTemplateClassCode.equals("399282926")) {
                String chartTemplateTypeCode = db60.getChartTemplate(chart).get("CHART_TEMPLATE_TYPE_CODE");
                editAssess(chartTemplateTypeCode);
            }

        }

        //编辑体征单或记录单
        public void editRecord (String chartTemplateId, String patientName){
            WebElement ele = wnwd.getElementByXpath(WnInpatientXpath.charAddButton);
            if (ele != null) {
                wnwd.wnClickElement(ele, "点击新建按钮");
            }
            List<WebElement> search_result = wnwd.getElementListByXpath(WnInpatientXpath.charEditIcon);
            if(search_result!=null &&search_result.size()>0){
                for (WebElement icon : search_result) {
                    wnwd.wnClickElement(icon, "点击展开页面图标", true, false);
                }
            }

            List<WebElement> timeInputList = wnwd.getElementListByXpath(WnInpatientXpath.charEditTimeInput);
            if(timeInputList!=null &&timeInputList.size()>0){
                for (WebElement timeInput : timeInputList) {
                    timeInput.sendKeys(Keys.ENTER);
                }
            }

            Map<String, String> encounterInfo = db60.getEncounterInfoByFullName(patientName);
            String encounterId = encounterInfo.get("ENCOUNTER_ID");
            String currentDeptId = encounterInfo.get("CURRENT_DEPT_ID");
            String currentWardId = encounterInfo.get("CURRENT_WARD_ID");
            String bizRoleId = encounterInfo.get("BIZ_ROLE_ID");
            //调接口查询护理文书模板,获取模板的所有输入框、下拉框
            String url = "http://" + Data.host + "/inpatient-charting/api/v1/chart_app/record_sheet_template/query/by_ids";
            String json = "{\"chartTemplateIds\":[\"" + chartTemplateId + "\"],\"currentDeptId\":\"" + currentDeptId + "\",\"bizRoleId\":\"" + bizRoleId + "\",\"currentWardId\":\"" + currentWardId + "\",\"encounterId\":\"" + encounterId + "\"}";
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            System.out.println(test.getResponseContent());
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                JsonParser parser = new JsonParser();
                JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
                JsonArray dataArray = contentJson.getAsJsonArray("data");
                JsonObject dataObject = dataArray.get(0).getAsJsonObject();
                JsonArray recordSheetSectionArray = dataObject.getAsJsonArray("recordSheetSection");
                for (int i = 0; i < recordSheetSectionArray.size(); i++) {
                    JsonObject recordSheetSection = (JsonObject) recordSheetSectionArray.get(i);
                    JsonArray recordSheetItemArray = recordSheetSection.getAsJsonArray("recordSheetItem");
                    getRecordSheetItem(recordSheetItemArray);
                }
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }

        }

        public void getRecordSheetItem (JsonArray recordSheetItemArray){
            if (recordSheetItemArray == null || recordSheetItemArray.size() == 0) {
                return;
            }
            for (int i = 0; i < recordSheetItemArray.size(); i++) {
                JsonObject recordSheetItemObject = (JsonObject) recordSheetItemArray.get(i);
                if (!recordSheetItemObject.get("recordSheetItem").isJsonNull()) {
                    //递归查询recordSheetItem
                    getRecordSheetItem(recordSheetItemObject.getAsJsonArray("recordSheetItem"));
                }
                String chartItemShowName = recordSheetItemObject.get("chartItemShowName").isJsonNull() ? recordSheetItemObject.get("chartItemName").getAsString() : recordSheetItemObject.get("chartItemShowName").getAsString();
                String chartItemControlTypeCode = recordSheetItemObject.get("chartItemControlTypeCode").getAsString();
                String EnableEditFlag = recordSheetItemObject.get("chartItemEnableEditFlag").isJsonNull() ? "98176" : recordSheetItemObject.get("chartItemEnableEditFlag").getAsString();
                if (EnableEditFlag.equals("98175")) {
                    // 数字和整型数字输入框
                    if (chartItemControlTypeCode.equals("399282093") || chartItemControlTypeCode.equals("399282094")) {
                        String chartItemNumberMaxVal = recordSheetItemObject.get("chartItemNumberMaxVal").isJsonNull() ? "999999" : recordSheetItemObject.get("chartItemNumberMaxVal").getAsString();
                        String chartItemNumberMinVal = recordSheetItemObject.get("chartItemNumberMinVal").isJsonNull() ? "1" : recordSheetItemObject.get("chartItemNumberMinVal").getAsString();
                        String value = chartItemNumberMinVal;
                        for (Data.signs signs : Data.signs.values()) {
                            if (chartItemShowName.equals(signs.getDesc())) {
                                //枚举值在模板设置的最大最小值之间，取枚举值，否则取最小值
                                if ((Double.parseDouble(signs.getValue()) < Double.parseDouble(chartItemNumberMaxVal)) && (Double.parseDouble(signs.getValue()) >= Double.parseDouble(chartItemNumberMinVal))) {
                                    value = signs.getValue();
                                    break;
                                }
                            }
                        }

                        System.out.println("chartItemName" + chartItemShowName + ";chartItemControlTypeCode:" + chartItemControlTypeCode + "值：" + value);
                        wnwd.waitElementByXpathAndInput(chartItemShowName + ":数值型文本框赋值", WnInpatientXpath.chartNumInput.replace("?", chartItemShowName), value, Framework.defaultTimeoutMin);
                    }

                    //文本输入框
                    if (chartItemControlTypeCode.equals("399282092")) {
                        String value = "护理文书测试";
                        for (Data.signs signs : Data.signs.values()) {
                            if (chartItemShowName.equals(signs.getDesc())) {
                                value = signs.getValue();
                                break;
                            }
                        }
                        wnwd.waitElementByXpathAndInput(chartItemShowName + ":文本框赋值", WnInpatientXpath.chartInput.replace("?", chartItemShowName), value, Framework.defaultTimeoutMin);
                    }
                    //下拉单选框
                    if (chartItemControlTypeCode.equals("399282098")) {
                        //通过下拉框的概念域id获取下拉框选项值
                        String chartConceptDomainId = recordSheetItemObject.get("chartConceptDomainId").getAsString();
                        String ChartDropdownValue = db60.getChartDropdownValue(chartConceptDomainId);
                        System.out.print("设置下拉框的值:" + ChartDropdownValue);
                        wnwd.waitElementByXpathAndClick(":点击下拉框", WnInpatientXpath.chartDropdownList.replace("?", chartItemShowName), Framework.defaultTimeoutMin);
                        wnwd.waitElementByXpathAndClick(":选择下拉框选项值", WnInpatientXpath.chartDropdownListValue.replace("?", ChartDropdownValue), Framework.defaultTimeoutMin);

                    }
                }
            }

        }
        public void submitRecord () {
            wnwd.waitElementByXpathAndClick("点击提交按钮", WnInpatientXpath.chartSubmitButton, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击签名按钮", WnInpatientXpath.chartSignButton, Framework.defaultTimeoutMin);
        }


        //编辑入院评估单
        public void editInpatientAssess (String chart, String chartTemplateId){
            String url = "http://" + Data.host + "/inpatient-charting/api/v1/chart_app/assess_template/query/by_ids";
            String json = "{\"chartTemplateIds\":[\"" + chartTemplateId + "\"]}";
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            System.out.println(test.getResponseContent());
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                JsonParser parser = new JsonParser();
                JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
                JsonArray dataArray = contentJson.getAsJsonArray("data");
                JsonObject dataObject = dataArray.get(0).getAsJsonObject();
                JsonArray chartAssessSectionArray = dataObject.getAsJsonArray("chartAssessSection");
                for (int i = 0; i < chartAssessSectionArray.size(); i++) {
                    JsonObject recordSheetSection = (JsonObject) chartAssessSectionArray.get(i);
                    String chartSectionContent = recordSheetSection.get("chartSectionContent").getAsString();
                    JsonElement chartElement = parser.parse(chartSectionContent);
                    JsonArray chartSectionContentArray = chartElement.getAsJsonArray();
                    System.out.println("chartSectionContent：" + chartSectionContentArray.toString());
                    getNameAndType(chart, chartTemplateId, chartSectionContentArray, null);
                }
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }

        }


        public void getNameAndType (String chart, String chartTemplateId, JsonArray chartSectionContentArray, String
        scriptCode){
            for (int i = 0; i < chartSectionContentArray.size(); i++) {
                JsonObject chartSectionContent = (JsonObject) chartSectionContentArray.get(i);
                String type = chartSectionContent.get("type").getAsString();
                String name = chartSectionContent.has("name") ? chartSectionContent.get("name").getAsString() : chartSectionContent.get("label").getAsString(); //表格类型的没有name，只有label
                Boolean hidden = chartSectionContent.has("hidden") ? chartSectionContent.get("hidden").getAsBoolean() : Boolean.FALSE;
                JsonObject configObject = chartSectionContent.getAsJsonObject("config");
                //code是组合控件中的主控件，选择了主控件，隐藏的控件才显示
                String code = scriptCode;
                System.out.println("name：" + name + ";type:" + type);
                System.out.println("hidden：" + hidden);
                System.out.println("code：" + code);
                //hidden =false表示页面显示,没有hidden字段则默认为显示
                if (!hidden) {
                    //组合控件，script记录了主控件和隐藏控件
                    if (type.equals("group")) {
                        JsonArray childrenArray = chartSectionContent.getAsJsonArray("children");
                        String script = chartSectionContent.get("script").getAsString();
                        System.out.println("script:" + script);
                        JsonObject scriptObject = new JsonParser().parse(script).getAsJsonObject();
                        code = scriptObject.keySet().iterator().next();
                        getNameAndType(chart, chartTemplateId, childrenArray, code);

                    }
                    //复选框
                    if (type.equals("checkbox")) {
                        //通过名字去定位下面的复选框，单击，第一个和最后一个选上
                        if(name!=""){
                            wnwd.waitElementByXpathAndClick(":点击复选框框第一个选项："+name, WnInpatientXpath.chartFirstCheck.replace("?", name), Framework.defaultTimeoutMin);
                            wnwd.waitElementByXpathAndClick(":点击复选框框最后一个选项："+name, WnInpatientXpath.chartLastCheck.replace("?", name), Framework.defaultTimeoutMin);
                        }
                    }
                    //单选框
                    if (type.equals("radio")) {
                        //通过名字去定位下面的单选框，单击，选择组合控件的主控件或者第一个
                        JsonArray optionsArray = configObject.getAsJsonArray("options");
                        String radioName = ((JsonObject) optionsArray.get(0)).get("name").getAsString();
                        for (int j = 0; j < optionsArray.size(); j++) {
                            JsonObject options = (JsonObject) optionsArray.get(j);
                            if (options.get("code").getAsString().equals(code)) {
                                radioName = options.get("name").getAsString();
                                break;
                            }
                        }
                        wnwd.waitElementByXpathAndClick(":点击单选框", WnInpatientXpath.chartRadio.replace("?1", name).replace("?2", radioName), Framework.defaultTimeoutMin);

                    }
                    //文本框
                    if (type.equals("input")) {
                        String model = chartSectionContent.get("model").getAsString();
                        String value = "护理文书测试";
                        for (Data.signs signs : Data.signs.values()) {
                            if (name.equals(signs.getDesc())) {
                                value = signs.getValue();
                                break;
                            }
                        }
                        WebElement ele3 = wnwd.waitElementByXpath(WnInpatientXpath.charInput.replace("?", model), Framework.defaultTimeoutMin);
                        if (ele3 != null) {
                            wnwd.waitElementByXpathAndInput(name + ":文本框赋值", WnInpatientXpath.charInput.replace("?", model), value, Framework.defaultTimeoutMin);
                        }

                    }
                    //下拉单选
                    if (type.equals("select")) {
                        String chartConceptDomainId = chartSectionContent.get("chartConceptDomainId").getAsString();
                        String ChartDropdownValue = db60.getChartDropdownValue(chartConceptDomainId);
                        wnwd.waitElementByXpathAndClick(":点击下拉框", WnInpatientXpath.chartSelect.replace("?", name), Framework.defaultTimeoutMin);
                        wnwd.waitElementByXpathAndClick(":选择下拉框选项值", WnInpatientXpath.chartSelectItem.replace("?", ChartDropdownValue), Framework.defaultTimeoutMin);
                    }
                    //表格，暂时未实现编辑
//                if(type.equals("table")){
//                    wnwd.waitElementByXpathAndClick( ":点击编辑按钮",WnInpatientXpath.chartEditButton,Frmcons.defaultTimeoutMin);
//                    JsonObject config = chartSectionContent.getAsJsonObject("config");
//                    JsonArray cols= config.getAsJsonArray("cols");
//                    getNameAndType(chartTemplateId,cols,code);
//                }
                    if (type.equals("pointNumber") || type.equals("intNumber")) {
                        //json中没有返回最大最小值，需要拿model（相当于CHART_ITEM_ID）去CHART_ITEM表查询
                        String chartItemId = chartSectionContent.get("model").getAsString();
                        Map<String, String> getChartItemLimit = db60.getChartItemLimit(chartItemId);
                        String chartItemNumberMinVal = "1";
                        String chartItemNumberMaxVal = "999999";
                        if (getChartItemLimit.get("CHART_ITEM_NUMBER_MIN_VAL") != null) {
                            chartItemNumberMinVal = getChartItemLimit.get("CHART_ITEM_NUMBER_MIN_VAL");
                        }
                        if (getChartItemLimit.get("CHART_ITEM_NUMBER_MIN_VAL") != null) {
                            chartItemNumberMaxVal = getChartItemLimit.get("CHART_ITEM_NUMBER_MAX_VAL");
                        }
                        String value = chartItemNumberMinVal;
                        for (Data.signs signs : Data.signs.values()) {
                            if (name.equals(signs.getDesc())) {
                                //枚举值在模板设置的最大最小值之间，取枚举值，否则取最小值
                                if ((Double.parseDouble(signs.getValue()) < Double.parseDouble(chartItemNumberMaxVal)) && (Double.parseDouble(signs.getValue()) >= Double.parseDouble(chartItemNumberMinVal))) {
                                    value = signs.getValue();
                                    break;
                                }
                            }
                        }
                        System.out.println("name" + name + ";type:" + type + "值：" + value);
                        WebElement ele = wnwd.getElementByXpath( WnInpatientXpath.chartSelect.replace("?", name));
                        if(ele!= null){
                            wnwd.waitElementByXpathAndInput(name + ":数值型文本框赋值", WnInpatientXpath.chartSelect.replace("?", name), value, Framework.defaultTimeoutMin);
                        }
                    }
                    //评估组件，点按钮弹出评估单编辑页面，页面都是单选或多选项
                    if (type.equals("third")) {
                        String chartTemplateTypeCode = chartSectionContent.get("chartTemplateTypeCode").getAsString();
                        wnwd.waitElementByXpath("等待填写页面加载完成", WnInpatientXpath.chartEditTitle.replace("?", chart), Framework.defaultTimeoutMax);
                        wnwd.waitElementByXpathAndClick(":点击评估按钮", WnInpatientXpath.charThirdButton.replace("?", name), Framework.defaultTimeoutMin);
                        editAssess(chartTemplateTypeCode);
                    }
                }
            }
        }

        //编辑评估单
        public void editAssess (String chartTemplateTypeCode){
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            //疼痛评估单
            if (chartTemplateTypeCode.equals("399282943")) {
                wnwd.waitElementByXpathAndClick("单击添加评估方法按钮", WnInpatientXpath.EvaluationButton, Framework.defaultTimeoutMax);
                // 首先将选中的评估方法去掉
                List<WebElement> search_result = wnwd.waitElementListByXpath(WnInpatientXpath.painEvaluationIsChecked, Framework.defaultTimeoutMin);
                for (WebElement checkedBox : search_result) {
                    wnwd.wnClickElement(checkedBox, "取消勾选 " + checkedBox.getText(), false, false);
                }
                wnwd.waitElementByXpathAndClick("单击添加评估方法按钮", WnInpatientXpath.EvaluationButton, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("选中面部表情评估法", WnInpatientXpath.painEvaluationItem, Framework.defaultTimeoutMax);//把评估法的选择框都赋值给list，然后点面部评估法
                //面部表情评估法
                WebElement ele2 = wnwd.waitElementByXpath(WnInpatientXpath.painEvaluationForm, Framework.defaultTimeoutMin);
                if (ele2 != null) {
                    wnwd.waitElementByXpathAndClick("选择第一个选项", WnInpatientXpath.painEvaluationFormItem, Framework.defaultTimeoutMin);
                }
            }
            //非疼痛评估单
            else {
                //获取所有单选或复选框的第一个选项值
                List<WebElement> elelist2 = wnwd.getElementListByXpath(WnInpatientXpath.chartAssessRadio);
                logger.log(1, "单选或多选框数量:" + elelist2.size());
                if (elelist2.size() > 0) {
                    for (WebElement ele2 : elelist2) {
                        wnwd.wnClickElement(ele2, "单击第一个选项");
                    }

                }
            }
            wnwd.waitElementByXpathAndClick("点击提交按钮", WnInpatientXpath.chartAssessSubmit, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击签名按钮", WnInpatientXpath.chartSignButton, Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
        }

        //电子检查单开立
        public void requestOrder (String requestOrderName){
            wnwd.waitElementByXpathAndClick("点击申请单按钮", WnInpatientXpath.orderButton.replace("?", "申请单"), Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击检查", WnInpatientXpath.applyFormButton.replace("?", requestOrderName), Framework.defaultTimeoutMin);
            WebElement element = wnwd.waitElementByXpath(WnInpatientXpath.applyFormFirstItem, Framework.defaultTimeoutMin);
            if (element == null) {
                logger.assertFalse(true, "申请单模板不存在，请先配置申请单模板");
            } else {
                wnwd.wnClickElement(element, "选择申请单模板");
                wnwd.waitElementByXpathAndClick("选择第一个检查项目", WnInpatientXpath.examItemCheckBox, Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("点确定按钮", WnInpatientXpath.orderConfirmButton, Framework.defaultTimeoutMin);
                wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            }
        }

        //出院出区
        public void outHospital () {
            try {
                wnwd.waitElementByXpathAndClick("点击就诊管理菜单", WnInpatientXpath.NurseMenu.replace("?", "就诊管理"), Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("点击患者出区标签", WnInpatientXpath.NurseChildMenu.replace("?", "患者出区"), Framework.defaultTimeoutMax);
                wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击下一步", WnInpatientXpath.nextButton, Framework.defaultTimeoutMax);
                WebElement ele = wnwd.waitElementByXpath("等待出现费用页面", WnInpatientXpath.feePage, Framework.defaultTimeoutMin);
//                WebElement ele2 = wnwd.getShadowRoot(ele);
//                WebElement ele3 = ele2.findElement(By.id("costcheck"));
//                WebElement ele4 = ele3.findElement(By.xpath(".//button"));
//                ele4.click();
//                wnwd.waitElementByXpath("核对成功提示", "//p[@class='el-message__content' and .='核对成功']", Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击费用核对",WnInpatientXpath.costCheckButton, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击下一步", WnInpatientXpath.nextButton, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击病情转归下拉框", WnInpatientXpath.outAreaSelect, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点下拉框第一个选项值", WnInpatientXpath.dropdownValue, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击办理出区", WnInpatientXpath.outAreaButton, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定返回首页", WnInpatientXpath.outAreaConfirmButton, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "出院出区失败");
            }
        }

        //出区召回.
        public void outHospitalRecall (String patientName){
            try {
                wnwd.waitElementByXpathAndClick("点击首页", WnInpatientXpath.homePage, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击已出区", WnInpatientXpath.out, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndInput("搜索框输入患者姓名", WnInpatientXpath.inpatientPatientSearchInput, patientName, Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("点击更多菜单", "//div[@class='el-table__fixed-body-wrapper' and contains(.,'" + patientName + "')]//td//span[contains(text(),'更多菜单')]", Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击出区召回", WnInpatientXpath.outHospitalRecall, Framework.defaultTimeoutMax);
                List<Map<String, String>> EmptyBedNoList = db60.getEmptyBedNoByOrgName();
                if (EmptyBedNoList.size() == 0) {
                    System.out.println("没有空床位，无法办理出区召回");
                }
//                wnwd.waitElementByXpathAndClick("选择床位按钮", WnInpatientXpath.selectBed, Framework.defaultTimeoutMax);
//                wnwd.waitElementByXpathAndClick("点击床位", WnInpatientXpath.selectBedButton, Framework.defaultTimeoutMax);
//                wnwd.waitElementByXpathAndClick("选中床位-确定", WnInpatientXpath.selectBedConfirm, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("办理召回", WnInpatientXpath.recall, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("办理召回-确定", WnInpatientXpath.recallConfirm, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "出区召回失败");
            }
        }

        //转科转区.
        public void transferArea (String newDepartmen, String newWard){
            try {
                wnwd.waitElementByXpathAndClick("点击就诊管理菜单", WnInpatientXpath.encounterMgmt, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击转区转科菜单", WnInpatientXpath.transferAre, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("下一步", WnInpatientXpath.transferAreNext, Framework.defaultTimeoutMax);
                //点击费用核对
                WebElement ele = wnwd.waitElementByXpath("等待出现费用页面", WnInpatientXpath.feePage, Framework.defaultTimeoutMax);
//                WebElement ele2 = wnwd.getShadowRoot(ele);
//                WebElement ele3 = ele2.findElement(By.id("costcheck"));
//                WebElement ele4 = ele3.findElement(By.xpath(".//button"));
//               ele4.click();
                wnwd.waitElementByXpathAndClick("下一步", WnInpatientXpath.transferAreNext, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("科室", WnInpatientXpath.Departmen, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("新科室", WnInpatientXpath.newDepartmen.replace("?", newDepartmen), Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("病区", WnInpatientXpath.Ward, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("新病区", WnInpatientXpath.newWard.replace("?", newWard), Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("确定转区", WnInpatientXpath.transferConfirm, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("转区成功提示", WnInpatientXpath.transferConfirmSucMsg, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "转科转区失败");
            }
        }

        //撤销转区.
        public void transferAreaRevoke () {
            try {
                wnwd.waitElementByXpathAndClick("首页", "//div[@class='nav-scroll']//span[text()='首页']", Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("病区事务", "//span[text()='病区事务']", Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("撤销转区", "//span[text()='撤销转区']", Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("撤销转区", WnInpatientXpath.transferRevoke, Framework.defaultTimeoutMid);
                wnwd.waitElementByXpathAndClick("撤销转区成功提示", WnInpatientXpath.transferRevokeSucMsg, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "撤销转区失败");
            }
        }

        //入区取消.
        public void enterWardRevoke () {
            try {
                wnwd.waitElementByXpathAndClick("点击就诊管理菜单", WnInpatientXpath.encounterMgmt, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点击入区取消菜单", WnInpatientXpath.enterWardRevoke, Framework.defaultTimeoutMax);
                //点击费用核对
                WebElement ele = wnwd.waitElementByXpath("等待出现费用页面", WnInpatientXpath.feePage, Framework.defaultTimeoutMax);
//                WebElement ele2 = wnwd.getShadowRoot(ele);
//                WebElement ele3 = ele2.findElement(By.id("costcheck"));
//                WebElement ele4 = ele3.findElement(By.xpath(".//button"));
//                ele4.click();
                wnwd.waitElementByXpathAndClick("入区取消-下一步", WnInpatientXpath.enterWardRevokeNext, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("取消入区", WnInpatientXpath.revokeEnterWard, Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("取消入区-确定", WnInpatientXpath.revokeEnterConfirm, Framework.defaultTimeoutMax);
                wnwd.checkElementByXpath("入区取消成功提示", WnInpatientXpath.revokeEnterWardSucMsg, Framework.defaultTimeoutMax);
            } catch (Throwable e) {
                logger.assertFalse(true, "入区取消失败");
            }
        }

        // 全医嘱开立前置参数检查,并将设置
        public void setParamsForTestAllService () {
            if(Data.modeConfiguration.equals("develop")) {
                String paramValue = db60.getCliSettingValue("CLI_ORDER_OPT_004");
                if (!paramValue.equals("1")) {
                    logger.boxLog(2, "CLI_ORDER_OPT_004参数值不符合要求，当前值为：" + paramValue, "当前CLI_ORDER_OPT_004参数不正确，自动化会将CLI_ORDER_OPT_011参数设置成1");
                    db60.updateCliSettingValueByCliSettingNo("CLI_ORDER_OPT_004", "1");
                } else {
                    logger.boxLog(1, "CLI_ORDER_OPT_004参数符合要求", "CLI_ORDER_OPT_004参数符合要求，医嘱检索列表模式为表格模式");
                }
                paramValue = db60.getCliSettingValue("CLI_ORDER_OPT_011");
                if (!paramValue.equals("0")) {
                    logger.boxLog(2, "CLI_ORDER_OPT_011参数值不符合要求，当前值为：" + paramValue, "当前CLI_ORDER_OPT_011参数不正确，自动化会将CLI_ORDER_OPT_011参数设置成0");
                    db60.updateCliSettingValueByCliSettingNo("CLI_ORDER_OPT_011", "0");
                } else {
                    logger.boxLog(1, "CLI_ORDER_OPT_011参数符合要求", "CLI_ORDER_OPT_011参数符合要求，草药开立默认医嘱类型为临时医嘱，频次为ST");
                }
                paramValue = getEncounterGeneralConfig("399544418","ORDER_CHECK_EXIT_WARD");
                if (!paramValue.equals("98176")) {
                    logger.boxLog(2, "ORDER_CHECK_EXIT_WARD参数值不符合要求，当前值为：" + paramValue, "当前ORDER_CHECK_EXIT_WARD参数不正确，自动化会将ORDER_CHECK_EXIT_WARD参数设置成98176");
                    setEncounterGeneralConfig("399544418","ORDER_CHECK_EXIT_WARD","98176");
                } else {
                    logger.boxLog(1, "ORDER_CHECK_EXIT_WARD参数符合要求", "ORDER_CHECK_EXIT_WARD参数符合要求，进行出院出区时，是否必须有出院医嘱为否");
                }

            }else if(Data.modeConfiguration.equals("release")) {
                String paramValue = db60.getCliSettingValue("CLI_ORDER_OPT_004");
                if (!paramValue.equals("1")) {
                    logger.boxLog(2, "CLI_ORDER_OPT_004参数值不符合要求，当前值为："+ paramValue+"，请手动修改为1。","");
                    throw new Error("主数据参数错误，请查看详细报告，并手工完成设置");
                }
                paramValue = db60.getCliSettingValue("CLI_ORDER_OPT_011");
                if (!paramValue.equals("0")) {
                    logger.boxLog(2, "CLI_ORDER_OPT_011参数值不符合要求，当前值为："+ paramValue+"，请手动修改为0。","");
                    throw new Error("主数据参数错误，请查看详细报告，并手工完成设置");
                }
                paramValue = getEncounterGeneralConfig("399544418","ORDER_CHECK_EXIT_WARD");
                if (!paramValue.equals("98176")) {
                    logger.boxLog(2, "ORDER_CHECK_EXIT_WARD参数值不符合要求，当前值为："+ paramValue+"，请手动修改为98176。","");
                    throw new Error("主数据参数错误，请查看详细报告，并手工完成设置");
                }
            }
        }

        // 弹出皮试框,点击皮试
        public void skinTestIsNeed () {
            WebElement skinTestButton = wnwd.waitElementByXpath("皮试弹框",WnInpatientXpath.skinTestButton,Framework.defaultTimeoutMid);
            if (skinTestButton != null) {
                wnwd.wnClickElement(skinTestButton, "皮试确认");
            }
        }

        // 获取加工厂默认值
        public void getDisposalFactoryDefault (Map < String, String > service){
            try {
                if (service.get("TYPE").equals("drug")) {
                    List<WebElement> inputs = wnwd.waitElementListByXpath(
                            "//div[contains(@class,'westDrugFac')]//li[contains(@class,'doseBlock')]//input",
                            Framework.defaultTimeoutMin);
                    service.put("UI_DOSAGE", inputs.get(0).getAttribute("value"));
                    service.put("UI_DOSAGE_UNIT", inputs.get(1).getAttribute("value"));
                    WebElement drugRout = wnwd.waitElementByXpath("西药给药途径", WnInpatientXpath.inpatientDisposalFactoryRouteInput, Framework.defaultTimeoutMin);
                    service.put("UI_DOSAGE_ROUTE", drugRout.getAttribute("value"));
                    WebElement drugFreq = wnwd.waitElementByXpath("西药频次", WnInpatientXpath.inpatientDisposalFactoryDrugFreqInput, Framework.defaultTimeoutMin);
                    service.put("UI_FREQ", drugFreq.getAttribute("value"));

                } else if (service.get("TYPE").equals("herb")) {
                    WebElement herbDosage = wnwd.waitElementByXpath("草药剂量", WnInpatientXpath.inpatientDisposalFactoryHerbNumInput, Framework.defaultTimeoutMin);
                    service.put("UI_DOSAGE", herbDosage.getAttribute("value"));
                    WebElement herbDosageUnit = wnwd.waitElementByXpath("草药剂量单位", WnInpatientXpath.inpatientDisposalFactoryHerbDosageUnit, Framework.defaultTimeoutMin);
                    service.put("UI_DOSAGE_UNIT", herbDosageUnit.getAttribute("value"));
//                WebElement herbRout= wnwd.waitElementByXpath("草药给药途径",WnInpatientXpath.inpatientDisposalFactoryHerbDecocteMethodDropbox,Framework.defaultTimeoutMin);
//                service.put("UI_DOSAGE_ROUTE", herbRout.getAttribute("value"));
                    WebElement herbFreq = wnwd.waitElementByXpath("草药频次", WnInpatientXpath.inpatientDisposalFactoryHerbFreqCodeDropbox, Framework.defaultTimeoutMin);
                    service.put("UI_FREQ", herbFreq.getAttribute("value"));
                }
            } catch (Throwable e) {
                throw new Error("加工厂默认值错误:获取药品加工厂默认参数失败:" + e.getMessage());
            }
        }

        public void CheckDisposalFactoryDefault (String csType, Map < String, String > service){
            try {
                if (Math.abs(
                        Float.valueOf(service.get("UI_DOSAGE")) - Float.valueOf(service.get("DB_DOSAGE"))) < 0.000001) {
                    logger.log(1, "默认剂量正确: 界面: " + service.get("UI_DOSAGE") + "/数据库: " + service.get("DB_DOSAGE"));
                } else {
                    throw new Error("默认剂量错误: 界面: " + service.get("UI_DOSAGE") + "/数据库: " + service.get("DB_DOSAGE"));
                }
                if (service.get("UI_DOSAGE_UNIT").equals(service.get("DB_DOSAGE_UNIT"))) {
                    logger.log(1,
                            "默认剂量单位正确: 界面: " + service.get("UI_DOSAGE_UNIT") + "/数据库: " + service.get("DB_DOSAGE_UNIT"));
                } else {
                    throw new Error(
                            "默认剂量单位错误: 界面: " + service.get("UI_DOSAGE_UNIT") + "/数据库: " + service.get("DB_DOSAGE_UNIT"));
                }
//            if (service.get("UI_DOSAGE_ROUTE").equals(service.get("DB_DOSAGE_ROUTE"))) {
//                logger.log(1,
//                        "默认给药途径正确: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
//            } else {
//                throw new Error(
//                        "默认给药途径错误: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
//            }
                if (service.get("UI_FREQ").toUpperCase().equals(service.get("DB_FREQ").toUpperCase())) {
                    logger.log(1, "默认频次正确: 界面: " + service.get("UI_FREQ") + "/数据库: " + service.get("DB_FREQ"));
                } else {
                    throw new Error("默认频次错误: 界面: " + service.get("UI_FREQ") + "/数据库: " + service.get("DB_FREQ"));
                }
                //草药没有默认给药途径，只比较西药
                if (csType.equals("drug")) {
                    if (service.get("UI_DOSAGE_ROUTE").equals(service.get("DB_DOSAGE_ROUTE"))) {
                        logger.log(1,
                                "默认给药途径正确: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
                    } else {
                        throw new Error(
                                "默认给药途径错误: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
                    }
                }

            } catch (Throwable e) {
                e.printStackTrace();
                logger.log("" + e.getStackTrace());
                throw new Error("加工厂默认值错误:" + e.getMessage());
            }
        }

        //护士站添加加床床位
        public void addBed (String bedNo){
            wnwd.waitElementByXpathAndClick("点击病区事务标签", "//span[contains(text(),'病区事务')]", Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击床位管理标签", "//span[contains(text(),'床位管理') or contains(text(),'加床床位')]", Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击添加床位按钮", WnInpatientXpath.addBedButton, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击病房下拉框", WnInpatientXpath.inpatRoomIdInput, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击第一个选项值", WnInpatientXpath.inpatRoomItem, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndInput("输入床位编号", WnInpatientXpath.bedNoInput, bedNo, Framework.defaultTimeoutMin);
            WebElement element = wnwd.waitElementByXpath(WnInpatientXpath.bedTypeSelect, Framework.defaultTimeoutMax);
            if (element.isEnabled()) {
                wnwd.waitElementByXpathAndClick("点击床位类型下拉框", WnInpatientXpath.bedTypeSelect, Framework.defaultTimeoutMin);
                wnwd.waitElementByXpathAndClick("选择第一个选项值", WnInpatientXpath.bedTypeItem, Framework.defaultTimeoutMin);
            }
            wnwd.waitElementByXpathAndClick("点保存", WnInpatientXpath.saveButton, Framework.defaultTimeoutMin);
            wnwd.checkElementByXpath("保存成功提示",WnInpatientXpath.saveSucMsg,Framework.defaultTimeoutMax);
            WebElement ele = wnwd.getElementByXpath(WnInpatientXpath.addBedDialog);
            if(ele!=null){
                wnwd.waitElementByXpathAndClick("点床位服务绑定按钮",WnInpatientXpath.addServiceButton,Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点床位收费服务名称",WnInpatientXpath.FeeServiceNameInput,Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("选第一个选项",WnInpatientXpath.FeeServiceNameSelect,Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定",WnInpatientXpath.dialogConfirmButton,Framework.defaultTimeoutMax);
                wnwd.waitElementByXpathAndClick("点确定",WnInpatientXpath.confirmButton,Framework.defaultTimeoutMax);
            }
            logger.log(1, "添加床位成功，空床编号：" + bedNo);
        }

        //调接口完成护士站添加加床床位
        public void addBedByInterface (String wardId, String inpatRoomId, String bedNo){
            String url = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/bas_inpatient_bed/save";
            String json = "[{\"wardId\":\"" + wardId + "\",\"inpatRoomId\":\"" + inpatRoomId + "\",\"bedNo\":\"" + bedNo + "\",\"bedTypeCode\":\"959852\",\"currentNurseId\":\"\",\"currentDoctorId\":\"\",\"nurseGroupId\":\"\",\"medicalGroupId\":\"\",\"bedAgeTypeCode\":\"\",\"bedSexTypeCode\":\"\",\"inpatBedParticipantList\":[]}]";
            System.out.println(json);
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "添加加床床位成功");
            } else {
                logger.assertFalse(true, "添加加床床位失败", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }
        }

        //查询床位信息
        public String queryBedInfo (String wardId, String bedNo){
            String InpatBedServiceId = null;
            String url = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/bas_inpatient_bed/query/by_example";
            String json = "{\"pageNo\":0,\"pageSize\":20,\"pageType\":\"P\",\"wardId\":\"" + wardId + "\",\"bedTypeCode\":\"959852\",\"roomName\":\"\",\"bedNo\":\"" + bedNo + "\"}";
            System.out.println(json);
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                String content = test.getResponseContent();
                JsonParser parser = new JsonParser();
                JsonObject contentJson = parser.parse(content).getAsJsonObject();
                JsonArray dataObject = contentJson.getAsJsonArray("data");
                InpatBedServiceId = dataObject.get(0).getAsJsonObject().get("inpatBedServiceId").getAsString();
                ;
                System.out.println("InpatBedServiceId:" + InpatBedServiceId);
                logger.boxLog(1, "成功", "查询加床床位成功");
            } else {
                logger.assertFalse(true, "查询加床床位失败", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }
            return InpatBedServiceId;
        }


        //主数据添加床位
        public void addBedFromMDM (String wardName, String bedNo){
            wnwd.moveToElementByXpath("鼠标移动到主数据就诊域", "//span[contains (.,'就诊域')]/parent::*", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("打开主数据临床域", "//span[contains (.,'就诊域')]/parent::*", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("打开住院就诊基本设置",
                    "//span[contains (.,'就诊域')]/parent::*//following-sibling::*//span[.='住院就诊基础设置']", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("打开病房与床位设置",
                    "//li[@role='menuitem']//span[.='病房与床位设置']", Framework.defaultTimeoutMax);
            WebElement frameElement = wnwd.waitElementByXpath("切换frame", "//iframe[2]", Framework.defaultTimeoutMax);
            wnwd.switchToFrame(frameElement);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击床位设置", WnInpatientXpath.bedSettingPage, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击添加床位按钮", WnInpatientXpath.addBedButton, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndInput("点击病区并输入", WnInpatientXpath.bedSettingPageWardInput, wardName, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击病区选项值", WnInpatientXpath.bedSettingPageWardSelectItem.replace("?", wardName), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击病房输入框", WnInpatientXpath.inpatRoomIdInput, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击病房第一个选项值", WnInpatientXpath.inpatRoomItem, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndInput("输入床位编号", WnInpatientXpath.bedNoInput, bedNo, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击床位类型下拉框", WnInpatientXpath.bedTypeSelect, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击编制床位", WnInpatientXpath.bedTypeItem, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点保存", WnInpatientXpath.saveButton, Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.inpatientSearchOrderLoadingBox, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpath(WnInpatientXpath.sucMsg.replace("?", "保存成功"), Framework.defaultTimeoutMax);
            logger.log(1, "添加床位成功，空床编号：" + bedNo);
        }

        //皮试用医嘱签收
        public void SignForSkinTestOrder () {
            wnwd.waitElementByXpathAndClick("点击医嘱单标签", "//span[contains(text(),'医嘱单')]", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpath("等待页面加载完成", "//div[@class='nurse-task-single-box']", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("签收皮试用的医嘱:", "//div[@class='el-table__fixed-right']//tr[contains(@class,'el-table__row') and contains(.,'皮试用')]//td[last()]//span[contains(text(),'签 收')]", Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("等待签收成功提示", WnInpatientXpath.signSucMsg, Framework.defaultTimeoutMax);
        }

        /**接口方式办理入区
         * DoctorsAndNurses中必须包含responsibleDoctor、responsibleNurse、attendingDoctor、residentDoctor、headDoctor
         * EmptyBed中必须包含INPAT_ROOM_ID、INPAT_BED_SERVICE_ID、BED_TYPE_CDE
         */
        public void enterWardByInterface (String encounterId, String
        admDeptId, Map < String, String > DoctorsAndNurses, Map < String, String > EmptyBed){
            String responsibleDoctor = DoctorsAndNurses.get("responsibleDoctor");
            String responsibleNurse = DoctorsAndNurses.get("responsibleNurse");
            String attendingDoctor = DoctorsAndNurses.get("attendingDoctor");
            String residentDoctor = DoctorsAndNurses.get("responsibleDoctor");
            String headDoctor = DoctorsAndNurses.get("headDoctor");
            String admittedRoomId = EmptyBed.get("INPAT_ROOM_ID");
            String inpatBedServiceId = EmptyBed.get("INPAT_BED_SERVICE_ID");
            String bedTypeCode = EmptyBed.get("BED_TYPE_CODE");

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(date);

            String url = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/enter_ward/confirm";
            String json = "{\n" +
                    "    \"bmiIndex\":\"\",\n" +
                    "    \"bmiIndexClassCode\":\"\",\n" +
                    "    \"currentDeptId\":\"" + admDeptId + "\",\n" +
                    "    \"currentDoctorId\":\"" + responsibleDoctor + "\",\n" +
                    "    \"participants\":[\n" +
                    "        {\n" +
                    "            \"employeeId\":\"" + responsibleDoctor + "\",\n" +
                    "            \"inpatParticipantTypeCode\":\"1000098\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"employeeId\":\"" + responsibleNurse + "\",\n" +
                    "            \"inpatParticipantTypeCode\":\"1000099\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"employeeId\":\"" + attendingDoctor + "\",\n" +
                    "            \"inpatParticipantTypeCode\":\"1000096\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"employeeId\":\"" + residentDoctor + "\",\n" +
                    "            \"inpatParticipantTypeCode\":\"1000095\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"employeeId\":\"" + headDoctor + "\",\n" +
                    "            \"inpatParticipantTypeCode\":\"1000097\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"currentMedicalGroupId\":null,\n" +
                    "    \"currentNurseGroupId\":null,\n" +
                    "    \"currentNurseId\":\"" + responsibleNurse + "\",\n" +
                    "    \"encounterId\":\"" + encounterId + "\",\n" +
                    "    \"heartRate\":\"\",\n" +
                    "    \"firstAdmittedToWardAt\":\"" + currentDate + "\",\n" +
                    "    \"admittedToWardAt\":\"" + currentDate + "\",\n" +
                    "    \"pathologicalStatus\":\"\",\n" +
                    "    \"phyExamDbp\":\"\",\n" +
                    "    \"phyExamHgt\":\"\",\n" +
                    "    \"phyExamSbp\":\"\",\n" +
                    "    \"phyExamTemp\":\"\",\n" +
                    "    \"phyExamWt\":\"\",\n" +
                    "    \"severityLevelCode\":\"\",\n" +
                    "    \"physiologicalStatus\":null,\n" +
                    "    \"sphygmusTimesMin\":\"\",\n" +
                    "    \"admittedRoomId\":\"" + admittedRoomId + "\",\n" +
                    "    \"bedTypeCode\":\"" + bedTypeCode + "\",\n" +
                    "    \"inpatBedServiceId\":\"" + inpatBedServiceId + "\",\n" +
                    "    \"inpatHealthConditionExtList\":[\n" +
                    "        {\n" +
                    "            \"inpatHealthAssessItemCode\":\"399471503\",\n" +
                    "            \"inpatHealthAssessItemValue\":\"\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"inpatHealthAssessItemCode\":\"399471504\",\n" +
                    "            \"inpatHealthAssessItemValue\":\"\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"inpatHealthAssessItemCode\":\"399471505\",\n" +
                    "            \"inpatHealthAssessItemValue\":\"\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            System.out.println(json);
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "入区成功");
            } else {
                logger.assertFalse(true, "入区失败", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }
        }


        public String applyMethodOcc (String patientName){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            String inpatEncRegSeqNo = db60.getInpatEncRegSeqNo(EncounterId);
            Map<String, String> info = browser.decouple.getOrderTlsyzz(inpatEncRegSeqNo, db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID"));
            return info.get("ZXRBM");
        }

        public String applyMethodLong (String patientName){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            String inpatEncRegSeqNo = db60.getInpatEncRegSeqNo(EncounterId);
            Map<String, String> info = browser.decouple.getOrderTcqyz(inpatEncRegSeqNo, db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID"));
            return info.get("ZXRQ");
        }
        //数据对比
        public void fieldComparison (HashMap < String, String > win, HashMap < String, String > his, String
        logMsg, String assertMsg){
            StringBuilder stringBuilder = new StringBuilder();
            boolean compares = win.equals(his);
            if (win.size() != his.size()) {
                logger.assertFalse(true, "60与his对比的数据size不一致");
            }
            if (compares) {
                logger.log(1, logMsg);
            } else {
                for (String s : his.keySet()) {
                    if (his.get(s) != null && win.get(s) != null) {
                        if (!his.get(s).equals(win.get(s))) {
                            stringBuilder.append(s + "==60值为：" + win.get(s) + "，his值为：" + his.get(s) + "\n");
                        }
                    }
                    if (his.get(s) == null && win.get(s) != null) {
                        stringBuilder.append(s + "==60值为：" + win.get(s) + "，his值为：" + his.get(s) + "\n");
                    }
                    if (his.get(s) != null && win.get(s) == null) {
                        stringBuilder.append(s + "==60值为：" + win.get(s) + "，his值为：" + his.get(s) + "\n");
                    }
                }
                logger.assertFalse(true, assertMsg + String.valueOf(stringBuilder));
            }
        }

        //查询大临床数据
        public HashMap<String, String> fhirwin (String patientName, String type){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            String inpatEncRegSeqNo = db60.getInpatEncRegSeqNo(EncounterId);
            if (EncounterId == null || inpatEncRegSeqNo == null) {
                logger.assertFalse(true, "60库无数据，无法进行fhir对比。患者姓名：" + patientName);
            }
            //药品医嘱签收
            if (type.equals("orderyp")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("开立病区", db60.getOrgNo(EncounterId));
                winSql.put("开立科室", db60.getOrgNoKs(EncounterId));
                winSql.put("开立医生", db60.getEmployeeNo(EncounterId));
                winSql.put("频次代码", db60.geValueDesc(EncounterId));
                winSql.put("嘱托", db60.getCliOrderItemId(EncounterId).get("ADVICE"));
                winSql.put("医嘱项标识", db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID"));
                winSql.put("临床服务名称", db60.getCs(EncounterId).get("CS_NAME"));
                winSql.put("开立数量", db60.getCliOrderItemId(EncounterId).get("PRESCRIBED_QTY"));
                winSql.put("零售价", db60.getPetailPrice(EncounterId));
                winSql.put("用药天数", db60.getTime(EncounterId).get("DAYS_OF_USE"));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //非药品医嘱签收
            if (type.equals("order")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("开立病区", db60.getOrgNo(EncounterId));
                winSql.put("开立科室", db60.getOrgNoKs(EncounterId));
                winSql.put("开立医生", db60.getEmployeeNo(EncounterId));
                winSql.put("频次代码", db60.geValueDesc(EncounterId));
                winSql.put("嘱托", db60.getCliOrderItemId(EncounterId).get("ADVICE"));
                winSql.put("医嘱项标识", db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID"));
                winSql.put("临床服务名称", db60.getCs(EncounterId).get("CS_NAME"));
                winSql.put("开立数量", db60.getCliOrderItemId(EncounterId).get("PRESCRIBED_QTY"));
                winSql.put("零售价", db60.getPetailPriceXm(EncounterId).get("UNIT_PRICE"));
                winSql.put("收费项目名称",db60.getPetailPriceXm(EncounterId).get("CHARGING_ITEM_NAME") );
                winSql.put("收费项目编码",db60.getPetailPriceXm(EncounterId).get("CHARGING_ITEM_NO") );
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //草药
            if (type.equals("orderHerb")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("给药途径代码", db60.getHerbOrder(EncounterId).get("DOSAGE_ROUTE_CODE"));
                winSql.put("制出总量", db60.getHerbOrder(EncounterId).get("PROCESSED_AMOUNT"));
                winSql.put("剂量", db60.getHerbOrder(EncounterId).get("DOSE"));
                winSql.put("服药周期付数", db60.getHerbOrder(EncounterId).get("DRUG_USAGE_FREQ_QUANTITY"));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //检验检查
            if (type.equals("orderInspection")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("开立科室", db60.getOrgNoKs(EncounterId));
                winSql.put("开立医生", db60.getEmployeeNo(EncounterId));
                winSql.put("医嘱项标识", db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID"));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //检验
            if (type.equals("orderYszzyhzjyd")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("执行科室",db60.getOrgNoZx(EncounterId));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //手术
            if (type.equals("orderSsmxk")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院号", inpatEncRegSeqNo);
                winSql.put("当前病区",db60.getInpatientWard(EncounterId));
                winSql.put("当前科室",db60.getInpatientRoom(EncounterId));
                winSql.put("开立医生", db60.getEmployeeNo(EncounterId));
                winSql.put("服务编码", db60.getCs(EncounterId).get("CS_NO"));
                winSql.put("服务名称", db60.getCs(EncounterId).get("CS_NAME"));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //药品医嘱申请
            if (type.equals("orderDrug")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("住院发药申请", db60.getInpmeddispreqid(EncounterId));
                winSql.put("住院发药计划", db60.getInpmeddispplanid(EncounterId).get("INP_MED_DISP_PLAN_ID"));
                winSql.put("执行人", db60.getOperatedBy(EncounterId));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            //患者就诊
            if (type.equals("inpatient")) {
                HashMap<String, String> winSql = new HashMap<>();
                winSql.put("身份证号码", db60.getInpatientRecord(EncounterId).get("IDCARD_NO"));
                winSql.put("病案号", db60.getInpatientRecord(EncounterId).get("CASE_NO"));
                winSql.put("国籍", db60.getInpatientInfo(EncounterId));
                winSql.put("性别", db60.getInpatientGender(EncounterId));
                winSql.put("入区途径", db60.getInpatientAdmitted(EncounterId));
                winSql.put("床位号", db60.getInpatientBed(EncounterId));
                winSql.put("科室", db60.getInpatientRoom(EncounterId));
                winSql.put("病区", db60.getInpatientWard(EncounterId));
                for (Object i : winSql.keySet()) {
                    logger.log(1, "60数据: " + i + " -- " + winSql.get(i));
                }
                return winSql;
            }
            return null;
        }

        //查询天津his数据
        public HashMap<String, String> fhirhis (String patientName, String type){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            String inpatEncRegSeqNo = db60.getInpatEncRegSeqNo(EncounterId);
            //药品 临时医嘱
            if (type.equals("tlsyzzyp")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderTlsyzz(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表tlsyzz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("开立病区", info.get("BQ"));
                hisSql.put("开立科室", info.get("KSBM"));
                hisSql.put("开立医生", info.get("YSBM"));
                hisSql.put("频次代码", info.get("YFYL"));
                hisSql.put("嘱托", info.get("YSZT"));
                hisSql.put("医嘱项标识", info.get("CFH_WN"));
                hisSql.put("临床服务名称", info.get("YZMC"));
                hisSql.put("开立数量", info.get("CS"));
                hisSql.put("零售价", info.get("JG1"));
                hisSql.put("用药天数", info.get("TS"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //药品 长期医嘱
            if (type.equals("tcqyzyp")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderTcqyz(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表tcqyz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("开立病区", info.get("BQ"));
                hisSql.put("开立科室", info.get("KB"));
                hisSql.put("开立医生", info.get("YSBM"));
                hisSql.put("频次代码", info.get("YFYL"));
                hisSql.put("嘱托", info.get("YSZT"));
                hisSql.put("医嘱项标识", info.get("CFH_WN"));
                hisSql.put("临床服务名称", info.get("YZMC"));
                hisSql.put("开立数量", info.get("CS"));
                hisSql.put("零售价", info.get("JG1"));
                hisSql.put("用药天数", info.get("TS"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //临时医嘱
            if (type.equals("tlsyzz")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderTlsyzz(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表tlsyzz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("开立病区", info.get("BQ"));
                hisSql.put("开立科室", info.get("KSBM"));
                hisSql.put("开立医生", info.get("YSBM"));
                hisSql.put("频次代码", info.get("YFYL"));
                hisSql.put("嘱托", info.get("YSZT"));
                hisSql.put("医嘱项标识", info.get("CFH_WN"));
                hisSql.put("临床服务名称", info.get("YZMC"));
                hisSql.put("开立数量", info.get("CS"));
                hisSql.put("零售价", info.get("JG1"));
                hisSql.put("收费项目名称", info.get("PM"));
                hisSql.put("收费项目编码", info.get("BM"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //长期医嘱
            if (type.equals("tcqyz")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderTcqyz(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表tcqyz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("开立病区", info.get("BQ"));
                hisSql.put("开立科室", info.get("KB"));
                hisSql.put("开立医生", info.get("YSBM"));
                hisSql.put("频次代码", info.get("YFYL"));
                hisSql.put("嘱托", info.get("YSZT"));
                hisSql.put("医嘱项标识", info.get("CFH_WN"));
                hisSql.put("临床服务名称", info.get("YZMC"));
                hisSql.put("开立数量", info.get("CS"));
                hisSql.put("零售价", info.get("JG1"));
                hisSql.put("收费项目名称", info.get("PM"));
                hisSql.put("收费项目编码", info.get("BM"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //草药
            if (type.equals("bqcyff")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderbqcyff(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表bqcyff的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("给药途径代码", info.get("YF"));
                hisSql.put("制出总量", info.get("ZL"));
                hisSql.put("剂量", info.get("CL"));
                hisSql.put("服药周期付数", info.get("JR"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " -- " + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " : " + hisSql.get(i));
                }
                return hisSql;
            }
            //检验检查医嘱
            if (type.equals("kdmx")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderKdmx(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表kdmx的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("开立科室", info.get("KDKS"));
                hisSql.put("开立医生", info.get("KDYS"));
                hisSql.put("医嘱项标识", info.get("CFH_WN"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //检验
            if (type.equals("yszzyhzjyd")) {
                Map<String, String> info = browser.decouple.getOrderyszzyhzjyd(inpatEncRegSeqNo);
                logger.assertFalse(info.size() < 1, "查询不到表yszzyhzjyd的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("执行科室", info.get("JYKS"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            //手术
            if (type.equals("ssmxk")) {
                Map<String, String> info = browser.decouple.getOrderSsmxk(inpatEncRegSeqNo);
                logger.assertFalse(info.size() < 1, "查询不到表ssmxk的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院号", info.get("ZYH"));
                hisSql.put("当前病区", info.get("BQBM"));
                hisSql.put("当前科室", info.get("BQKS"));
                hisSql.put("开立医生", info.get("KDRY"));
                hisSql.put("服务编码", info.get("SSBM"));
                hisSql.put("服务名称", info.get("SSMC"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " ，" + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }

            //临时药品申请
            if (type.equals("tlsyzzDrug")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderTlsyzz(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表tlsyzz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院发药申请", info.get("FYSQBS_WN"));
                hisSql.put("住院发药计划", info.get("FYJHBS_WN"));
                hisSql.put("执行人", info.get("ZXRBM"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " -- " + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " : " + hisSql.get(i));
                }
                return hisSql;
            }
            //长期药品申请
            if (type.equals("cqzxmxDrug")) {
                String cliOrderItem = db60.getCliOrderItemId(EncounterId).get("CLI_ORDER_ITEM_ID");
                Map<String, String> info = browser.decouple.getOrderCqzxmx(inpatEncRegSeqNo, cliOrderItem);
                logger.assertFalse(info.size() < 1, "查询不到表cqzxmx的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("住院发药申请", info.get("FYSQBS_WN"));
                hisSql.put("住院发药计划", info.get("FYJHBS_WN"));
                hisSql.put("执行人", info.get("ZXRBM"));
                for (Object i : hisSql.keySet()) {
                    System.out.println("his数据: " + i + " -- " + hisSql.get(i));
                    logger.log(1, "his数据: " + i + " : " + hisSql.get(i));
                }
                return hisSql;
            }
            //患者就诊
            if (type.equals("tdjkz")) {
                Map<String, String> info = browser.decouple.getInpatienttdjkz(inpatEncRegSeqNo);
                logger.assertFalse(info.size() < 1, "查询不到表tdjkz的信息，落库失败");
                HashMap<String, String> hisSql = new HashMap<>();
                hisSql.put("身份证号码", info.get("SFZH"));
                hisSql.put("病案号", info.get("BAH"));
                hisSql.put("国籍", info.get("GJDM"));
                hisSql.put("性别", info.get("XB"));
                hisSql.put("入区途径", info.get("RYFS"));
                hisSql.put("床位号", info.get("CH"));
                hisSql.put("科室", info.get("KSBM"));
                hisSql.put("病区", info.get("BQBM"));
                for (Object i : hisSql.keySet()) {
                    logger.log(1, "his数据: " + i + " -- " + hisSql.get(i));
                }
                return hisSql;
            }
            return null;
        }

        //Fhir--大临床与天津his数据对比.
        public void compareOrder (String patientName, String type){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            logger.log(1, "患者名称:" + patientName + "，住院就诊登记流水号：" + db60.getInpatEncRegSeqNo(EncounterId));

            if (type.equals("enter")) {
                HashMap<String, String> win = fhirwin(patientName, "inpatient");
                HashMap<String, String> his = fhirhis(patientName, "tdjkz");
                fieldComparison(win, his, "患者入区 落库成功", "患者入区 对比数据不一致，");
            }

            if (type.equals("signOccyp")) {
                HashMap<String, String> win = fhirwin(patientName, "orderyp");
                HashMap<String, String> his = fhirhis(patientName, "tlsyzzyp");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signLongyp")) {
                HashMap<String, String> win = fhirwin(patientName, "orderyp");
                HashMap<String, String> his = fhirhis(patientName, "tcqyzyp");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signOcc")) {
                HashMap<String, String> win = fhirwin(patientName, "order");
                HashMap<String, String> his = fhirhis(patientName, "tlsyzz");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signLong")) {
                HashMap<String, String> win = fhirwin(patientName, "order");
                HashMap<String, String> his = fhirhis(patientName, "tcqyz");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signHerb")) {
                HashMap<String, String> win = fhirwin(patientName, "orderHerb");
                HashMap<String, String> his = fhirhis(patientName, "bqcyff");
                fieldComparison(win, his, "草药医嘱签收落库成功", "草药医嘱签收对比数据不一致，");
            }

            if (type.equals("signInspection")) {
                HashMap<String, String> win = fhirwin(patientName, "orderInspection");
                HashMap<String, String> his = fhirhis(patientName, "kdmx");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signJy")) {
                HashMap<String, String> win = fhirwin(patientName, "orderYszzyhzjyd");
                HashMap<String, String> his = fhirhis(patientName, "yszzyhzjyd");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("signSs")) {
                HashMap<String, String> win = fhirwin(patientName, "orderSsmxk");
                HashMap<String, String> his = fhirhis(patientName, "ssmxk");
                fieldComparison(win, his, "医嘱签收落库成功", "医嘱签收对比数据不一致，");
            }

            if (type.equals("applyDrugOcc")) {
                HashMap<String, String> win = fhirwin(patientName, "orderDrug");
                HashMap<String, String> his = fhirhis(patientName, "tlsyzzDrug");
                fieldComparison(win, his, "药品医嘱申请落库成功", "药品医嘱申请对比数据不一致，");
            }

            if (type.equals("applyDrugLong")) {
                HashMap<String, String> win = fhirwin(patientName, "orderDrug");
                HashMap<String, String> his = fhirhis(patientName, "cqzxmxDrug");
                fieldComparison(win, his, "药品医嘱申请落库成功", "药品医嘱申请对比数据不一致，");
            }

            if (type.equals("applyOcc")) {
                logger.log(1, "申请人--60数据:" + db60.getOperatedBy(EncounterId) + "，申请人--his数据" + applyMethodOcc(patientName));
                if ((applyMethodOcc(patientName) != null && applyMethodOcc(patientName).equals(db60.getOperatedBy(EncounterId)))) {
                    logger.log(1, "医嘱申请 落库成功");
                } else {
                    logger.assertFalse(true, "医嘱申请 落库失败");
                }
            }

            if (type.equals("applyLong")) {
                logger.log(1, "申请人--60数据:" + db60.getOperatedBy(EncounterId) + "，申请人--his数据" + applyMethodLong(patientName));
                if ((applyMethodLong(patientName) != null)) {
                    logger.log(1, "医嘱申请 落库成功");
                } else {
                    logger.assertFalse(true, "医嘱申请 落库失败");
                }
            }

            if (type.equals("revokeApplyLong")) {
                if (applyMethodLong(patientName) == null) {
                    logger.log(1, "医嘱撤销申请 落库成功");
                } else {
                    logger.assertFalse(true, "医嘱撤销申请 落库失败");
                }
            }

            if (type.equals("revokeApplyOcc")) {
                if (applyMethodOcc(patientName) == null) {
                    logger.log(1, "医嘱撤销申请 落库成功");
                } else {
                    logger.assertFalse(true, "医嘱撤销申请 落库失败");
                }
            }
        }
        //Fhir--大临床与天津his数据对比.
        public void comparePatient (String patientName, String type){
            String EncounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");
            Map<String, String> info = browser.decouple.getInpatienttdjkz(db60.getInpatEncRegSeqNo(EncounterId));
            logger.log(1, "患者名称:" + patientName + "，住院就诊登记流水号：" +  db60.getInpatEncRegSeqNo(EncounterId));
            if (type.equals("cancel")) {
                if (info.get("CH") == null) {
                    logger.log(1, "入区取消 落库成功");
                } else {
                    logger.assertFalse(true, "床位号不为空，入区取消 落库不成功");
                }
            }

            if (type.equals("out")) {
                if (info.get("CYRQ") != null) {
                    logger.log(1, "患者出区 落库成功");
                } else {
                    logger.assertFalse(true, "出区时间为空，患者出区 落库不成功");
                }
            }

            if (type.equals("recall")) {
                if (info.get("H_UPDATETIME") != null && info.get("CYRQ") == null && info.get("CH") != null) {
                    logger.log(1, "出区召回 落库成功");
                } else {
                    logger.assertFalse(true, "出区召回时间为空，患者出区召回 落库不成功");
                }
            }

        }





    //开始皮试
    public void startSkinTest() {
        try {
            wnwd.waitElementByXpathAndClick("点击就诊管理", WnInpatientXpath.NurseMenu.replace("?", "就诊管理"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点击皮试管理", WnInpatientXpath.NurseChildMenu.replace("?", "皮试管理"), Framework.defaultTimeoutMin);
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
//            wnwd.waitElementByXpathAndClick("勾选待皮试记录", WnInpatientXpath.skinTestManagementCheckBox.replace("?", medicinalName), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点开始皮试", WnInpatientXpath.skinTestManagementSkinTestButton.replace("?", "开始皮试"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入药品批号", WnInpatientXpath.skinTestManagementDrugBatch, "1", Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点保存按钮", WnInpatientXpath.skinTestManagementSaveButton, Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("检查保存成功提示", WnInpatientXpath.saveSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "开始皮试操作失败");
        }
    }

    //结束皮试
    public void finishSkinTest(String skinTestResult) {
        try {
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击状态单选框", WnInpatientXpath.skinTestStatusInput, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("选择皮试中", WnInpatientXpath.skinTestStatusItem.replace("?", "皮试中"), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点结束皮试", WnInpatientXpath.skinTestManagementSkinTestButton.replace("?", "结束皮试"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点皮试结果单选框", WnInpatientXpath.skinTestResultSelect, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("选择皮试结果选项", WnInpatientXpath.skinTestResultItem.replace("?", skinTestResult), Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("点保存按钮", WnInpatientXpath.skinTestManagementSaveButton, Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("检查保存成功提示", WnInpatientXpath.saveSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "结束皮试操作失败");
        }
    }

    //皮试结果审核
    public void auditSkinTest() {
        try {
            wnwd.waitNotExistByXpath("等待加载完成", WnInpatientXpath.loading, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点击状态单选框", WnInpatientXpath.skinTestStatusInput, Framework.defaultTimeoutMin);
            wnwd.waitElementByXpathAndClick("选择皮试结束", WnInpatientXpath.skinTestStatusItem.replace("?", "皮试结束"), Framework.defaultTimeoutMin);
//            wnwd.waitElementByXpathAndClick("勾选皮试结束的记录", WnInpatientXpath.skinTestManagementCheckBox.replace("?", medicinalName), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点审核", WnInpatientXpath.skinTestManagementSkinTestButton.replace("?", "审核"), Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndInput("输入密码", WnInpatientXpath.skinTestResultAuditPasswordInput,Data.default_user_login_pwd, Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("点保存按钮", WnInpatientXpath.skinTestManagementSaveButton, Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("检查保存成功提示", WnInpatientXpath.saveSucMsg, Framework.defaultTimeoutMax);
        } catch (Throwable e) {
            logger.assertFalse(true, "皮试审核操作失败");
        }
    }


    //调接口查询护士的病区
    public int queryWardsOfNurse(String employeeNo) {
        int num = 0;
        String employeeId = db60.getEmployeeIdByEmployeeNo(employeeNo);
        String url = "http://" + Data.host + "/encounter-component/api/v1/encounter_component/inpat_org_target/query/by_example";
        String json = "{\"buTypeCode\":\"256003\",\"employeeId\":\"" + employeeId + "\"}";
        Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Content-Type", "application/json;charset=UTF-8");
        header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
        test.sendPostRequest(json, null, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            String content = test.getResponseContent();
            JsonParser parser = new JsonParser();
            JsonObject contentJson = parser.parse(content).getAsJsonObject();
            JsonArray dataObject = contentJson.getAsJsonArray("data");
            num = dataObject == null ? 0 : dataObject.size();
            System.out.println("num:"+num);
            logger.boxLog(1, "成功", "查询护士所属病区成功"+content);
        } else {
            logger.assertFalse(true, "查询护士所属病区失败", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
        }
        return num;
    }
    //生成床位编号
    public String  getEmptyBedNo(){
            String bedNo = null;
            if (Data.bedNoPrefix == null){
                Data.bedNoPrefix = getRandomString(2);
            }
            System.out.println("随机生成床位前缀："+ Data.bedNoPrefix);
            if(Data.maxBedNo == -1){
                Data.maxBedNo = Integer.parseInt(db60.getBedNo().trim());
                System.out.println("当前最大床位号:"+Data.maxBedNo);
            }
            if(Data.maxBedNo<99) {
                Data.maxBedNo = Data.maxBedNo + 1;
                bedNo = Data.bedNoPrefix + String.format("%02d", Data.maxBedNo);
                if(Data.hisType.equals("TJWN")){
                    String sql = new HisSqlManager().getCW(bedNo);
                    Map<String, String> info = db.queryFirstRow(sql);
                    if(info.get("BEDNO") != null){
                        Data.bedNoPrefix = null;
                        Data.maxBedNo = -1;
                        bedNo =getEmptyBedNo();
                    }
                }
                System.out.println("生成的床位编号:"+bedNo);
            }else{
                System.out.println("床位号已满");
                Data.bedNoPrefix = null;
                Data.maxBedNo = -1;
                bedNo =getEmptyBedNo();
            }
            return bedNo;
    }

    //随机获取字符串
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(52);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    //调接口出院出区
    public void outhospitalByInterface(String employeeID,String patientName) {
        try {
            Map<String, String> info = db60.getEncounterInfoByFullName(patientName);
            String encounterId = info.get("ENCOUNTER_ID");
            String bizRoleId = info.get("BIZ_ROLE_ID");
            String wardId = info.get("CURRENT_WARD_ID");
            String deptId = info.get("CURRENT_DEPT_ID");
            String url1 = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/leave_ward/confirm";
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(date);
            String json1 = "{\"dischargeOutcomeCode\":\"64592\",\"dischargedFromWardAt\":\"" + currentDate + "\",\"operByUser\":\"" + employeeID + "\",\"encounterId\":\"" + encounterId + "\",\"oweFeeReason\":\"\",\"secondConfirmationFlag\":false}";
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            HttpTestUrl httpTestUrl = new HttpTestUrl(url1);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "application/json;charset=UTF-8");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json1, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "患者出区成功");

            } else {
                logger.assertFalse(true, "患者出区失败", "请求地址: " + url1 + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }

            String url2 = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/inpat_bed_charge_job/save";
            String json2 = "{\"jobExeBizTypeCode\":399469083,\"bizRoleId\":\"" + bizRoleId + "\",\"encounterId\":\"" + encounterId + "\",\"inpatBedChargeType\":\"wardDischargedCharge\",\"wardId\":\"" + wardId + "\",\"deptId\":\"" + deptId + "\",\"actualExeDate\":\"" + currentDate + "\"}";
            httpTestUrl = new HttpTestUrl(url2);
            test = new HttpTest(httpTestUrl);
            test.sendPostRequest(json2, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "床位释放成功");

            } else {
                logger.assertFalse(true, "床位释放失败", "请求地址: " + url2 + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
            }
        }catch (Throwable e) {
            logger.log(3,"出区异常");
        }
    }

    // 通过ENCOUNTER_GENERAL_CONFIG_CODE查询主数据-住院就诊配置
    public JsonArray getEncounterGeneralConfig(String encounterGeneralConfigCode) {
        JsonArray configValueArray = null;
        String url = "http://" + Data.host + "/encounter-mdm/api/v1/app_encounter_mdm/encounter_general_config/get/by_code";
        Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
        String json = "{\"encounterGeneralConfigCode\":\""+ encounterGeneralConfigCode+ "\"}";
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Version", "1.1");
        header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
        test.sendPostRequest(json, null, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            JsonParser parser = new JsonParser();
            JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
            JsonObject dataObject = resJson.getAsJsonObject("data");
            String encounterGeneralConfigValue = dataObject.get("encounterGeneralConfigValue").getAsString();
            Gson gson =new Gson();
            configValueArray = gson.fromJson(encounterGeneralConfigValue, JsonArray.class);
        } else {
            logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
                    + test.getResponseContent());
        }
        return configValueArray;
    }

    // 通过ENCOUNTER_GENERAL_CONFIG_CODE和ruleCode查询主数据-住院就诊配置页面的参数值
    public String getEncounterGeneralConfig(String encounterGeneralConfigCode,String ruleCode) {
            String ruleValue = "";
            String url = "http://" + Data.host + "/encounter-mdm/api/v1/app_encounter_mdm/encounter_general_config/get/by_code";
            Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
            String json = "{\"encounterGeneralConfigCode\":\""+ encounterGeneralConfigCode+ "\"}";
            System.out.println(json);
            HttpTestUrl httpTestUrl = new HttpTestUrl(url);
            HttpTest test = new HttpTest(httpTestUrl);
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Version", "1.1");
            header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
            test.sendPostRequest(json, null, header);
            test.waitRequestFinish(30000);
            if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
                JsonParser parser = new JsonParser();
                JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
                JsonObject dataObject = resJson.getAsJsonObject("data");
                String encounterGeneralConfigValue = dataObject.get("encounterGeneralConfigValue").getAsString();
                Gson gson =new Gson();
                JsonArray configValueArray = gson.fromJson(encounterGeneralConfigValue, JsonArray.class);
                for (JsonElement data : configValueArray) {
                    if (data.getAsJsonObject().get("ruleCode").getAsString().contains(ruleCode)) {
                        ruleValue = data.getAsJsonObject().get("ruleValue").getAsString();
                    }
                }
            } else {
                logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
                        + test.getResponseContent());
            }
            return ruleValue;
        }

    // 设置主数据-住院就诊配置页面的参数值
    public void setEncounterGeneralConfig(String encounterGeneralConfigCode,String ruleCode,String ruleValue) {
        JsonArray configValueArray = getEncounterGeneralConfig(encounterGeneralConfigCode);
        if(configValueArray != null){
            for(JsonElement data : configValueArray){
                if (data.getAsJsonObject().get("ruleCode").getAsString().contains(ruleCode)) {
                   data.getAsJsonObject().addProperty("ruleValue",ruleValue);
                }
            }
        }
        String encounterGeneralConfigValue = configValueArray.toString().replace("\"","\\\"");
        System.out.println(encounterGeneralConfigValue);
        String url = "http://" + Data.host + "/encounter-mdm/api/v1/app_encounter_mdm/encounter_general_config/save";
        Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
        String json = "{\"encounterGeneralConfigCode\":\""+ encounterGeneralConfigCode +"\",\"encounterGeneralConfigValue\":\""+ encounterGeneralConfigValue +"\"}";
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Version", "1.1");
        header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
        test.sendPostRequest(json, null, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            logger.boxLog(1, "成功", "参数设置成功");
        } else {
            logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
                    + test.getResponseContent());
        }
    }

}