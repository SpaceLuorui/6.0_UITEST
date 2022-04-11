package com.winning.testsuite.workflow.Outpatient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64.Decoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.openqa.selenium.*;

import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.entity.AllocationCondition;
import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.testsuite.workflow.entity.SignoffDetail;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ui.sdk.base.SdkWebDriver;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Log;
import ui.sdk.util.SdkTools;
import xunleiHttpTest.HttpTest;
import xunleiHttpTest.HttpTestHeader;
import xunleiHttpTest.HttpTestUrl;

/**
 * ui测试流程
 *
 * @author Administrator
 */
public class WnOutpatientWorkflow {
	public WebDriver driver = null;
	public SdkWebDriver wnwd = null;
	public Log logger = null;
	public static String lcxmBillTestResult = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator
			+ "lcxm.csv";

	public WnOutpatientWorkflow(SdkWebDriver driver) {
		this.wnwd = driver;
		this.logger = this.wnwd.logger;
	}

	// 登录后并选择肾脏风湿科进入主页面
	public void wnloginToMain() {
		wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		loginOutPatient();
		choiceSubject("肾脏风湿科");
		skip();
	}

	// 登录portal
	public void wnlogin(String username, String password) {
		try {
			wnwd.waitElementByXpathAndClick("帐号登录方式选择按钮", "//div[.='账号登录']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("用户名输入框", WnOutpatientXpath.loginUsernameInput, username,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("密码输入框", WnOutpatientXpath.loginPasswordInput, password,
					Framework.defaultTimeoutMax);
			//wnwd.waitElementByXpathAndClick("登录按钮", WnOu diagnosis_pathWay()tpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
			WebElement HospitalArea = wnwd.waitElementByXpath("多院区标识", WnOutpatientXpath.loginHospitalFlag, Framework.defaultTimeoutMin);
			if (HospitalArea != null) {
				if (Data.Hospital_OgName_name.equals("default")) {
					wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
					wnwd.checkElementByXpath("Portal页面标识", WnOutpatientXpath.portalPageFlag, Framework.defaultTimeoutMax);
				}else {
					wnwd.wnClickElement(HospitalArea, "点击多院区标识");
					List<WebElement> eleArelist = wnwd.waitElementListByXpath(WnOutpatientXpath.loginHospitalAreaName,Framework.defaultTimeoutMax);
					WebElement title = wnwd.waitElementByXpath(".//span[.='"+Data.Hospital_OgName_name+"']",Framework.defaultTimeoutMax);
					wnwd.wnClickElement(title, "点击 :" + title.getText());
					wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
					wnwd.checkElementByXpath("Portal页面标识", WnOutpatientXpath.portalPageFlag, Framework.defaultTimeoutMax);
				}
			}else {
				wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
				wnwd.sleep(Framework.defaultTimeoutMin);
			}
		} catch (Throwable e) {
			throw new Error("登录失败:" + e.getMessage());
		}
	}

	// 门诊医生站登出系统
	public void wnlogout() {
		wnwd.waitElementByXpathAndClick("点击图标", "//div[@class='win-patient-avatar']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击注销", "//span[contains(text(),'注销')]", Framework.defaultTimeoutMax);
	}

	// 进入门诊医生站
	public void loginOutPatient() {
		wnwd.waitElementByXpathAndClick("医生站入口", WnOutpatientXpath.portalOutpatientEntrance, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("就诊科目选择框", WnOutpatientXpath.outpatientSubjectChooseBox, Framework.defaultTimeoutMax);
	}

	// 进入门诊医生站并执行科目选择,科目选择框可能不存在
	public void loginOutPatientNew(String subject) {
		loginOutPatientNew(new ArrayList<>(Arrays.asList(subject)));
	}

	// 进入门诊医生站并选择多个科室
	public void loginOutPatientNew(List<String> subjectList) {
		wnwd.waitElementByXpathAndClick("医生站入口", WnOutpatientXpath.portalOutpatientEntrance, Framework.defaultTimeoutMax);
		WebElement element = wnwd.waitElementByXpath("就诊科目选择框", WnOutpatientXpath.outpatientSubjectChooseBox,
				Framework.defaultTimeoutMax);
		if (element != null) {
			choiceSubject(subjectList);
		}
	}

	// 从门诊医生站进入主数据管理页面
	public void loginMDMFromOutpatient() {
		wnwd.waitElementByXpathAndClick("进入主菜单界面", "//span[contains(.,'门诊医生站')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("主数据管理入口", "//main[@class=\"el-main\"]//li/p[.=\"主数据管理\"]",
				Framework.defaultTimeoutMax);
	}

	// 从portal进入主数据管理页面
	public void loginMDMFromPortal() {
		wnwd.waitElementByXpathAndClick("主数据管理入口", "//main[@class=\"el-main\"]//li/p[.=\"主数据管理\"]",
				Framework.defaultTimeoutMax);
	}

	// 进入portal页面
	public void loginPortalFromMDM() {
		wnwd.waitElementByXpathAndClick("进入主菜单界面", "//img[@class='logo-img']", Framework.defaultTimeoutMax);
	}

	// 更新临床服务状态，csStatus 可以为98360,98361
	public void updateClinicalServiceCsStatus(String csName, String csStatus) {
		wnwd.moveToElementByXpath("鼠标移动到主数据临床域", "//span[contains (.,'临床域')]/parent::*", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("打开主数据临床域", "//span[contains (.,'临床域')]/parent::*", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("打开临床服务设置", "//span[contains (.,'临床服务设置')]/parent::*",
				Framework.defaultTimeoutMax);
		wnwd.sleep(2000);
		WebElement frameElement = wnwd.waitElementByXpath("切换frame", "//iframe", Framework.defaultTimeoutMax);
		wnwd.switchToFrame(frameElement);
		wnwd.waitElementByXpathAndInput("服务名称搜索框", "(//label[contains (.,'服务名称')]/following-sibling::*//input)[1]",
				csName, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击启用状态下拉框", "//label[contains (.,'启用状态')]/following-sibling::*//i",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击全部", "//span[contains (.,'全部')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击查询", "//span[contains (.,'查询')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击编辑",
				"//tr[@class='el-table__row']//td[.='" + csName + "']/..//span[contains (.,'编辑')]",
				Framework.defaultTimeoutMax);
		WebElement csStatusEnableButton = wnwd.waitElementByXpath(
				"//span[contains (.,'启用状态')]/following-sibling::*[@class='el-switch is-checked']",
				Framework.defaultTimeoutMax);
		if (csStatusEnableButton != null && csStatus == "98361") {
			wnwd.waitElementByXpathAndClick("停用服务",
					"//span[contains (.,'启用状态')]/following-sibling::*[@class='el-switch is-checked']",
					Framework.defaultTimeoutMax);
		} else if (csStatusEnableButton == null && csStatus == "98360") {
			wnwd.waitElementByXpathAndClick("启用服务",
					"//span[contains (.,'启用状态')]/following-sibling::*[@class='el-switch']", Framework.defaultTimeoutMax);
		}
		wnwd.waitElementByXpathAndClick("点击保存", "//span[contains (.,'保存')]", Framework.defaultTimeoutMax);
		wnwd.switchToDefaultContent();
	}

	// 更新药品状态，csStatus 可以为98360,98361
	public void updateMedicineCsStatusByMedicineId(String medicineId, String csStatus) {
		String url = "";
		if(csStatus.equals("98360")) {
			url = "http://" + Data.host + "/material-mdm/api/v1/app_material_mdm/medicine/enable";
		}else if(csStatus.equals("98361")) {
			url = "http://" + Data.host + "/material-mdm/api/v1/app_material_mdm/medicine/disable";
		}else {
			logger.assertFalse(true, "修改药品启用状态接口参数错误");
		}
		String param = "{\"medicineId\":\""+medicineId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String desc = "修改药品启用状态:" +  "  -->  "+ (csStatus=="98360" ? "启用" : "停用") ;
		HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
		if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
			logger.assertFalse(true, "修改启用状态失败");
		}
	}

	// 跳过导航
	public void skip() {
		try {
			WebElement skipButton = wnwd.getElementByXpath(WnOutpatientXpath.outpatientSkipButton, false);
			if (skipButton != null) {
				wnwd.wnClickElement(skipButton, "跳过按钮");
			}
			wnwd.sleep(1000);
		} catch (Throwable e) {
			throw new Error("跳过导航失败:" + e.getMessage());
		}
	}

	// 设置病历模板推荐
	public void emrTemplateRecommend() {
//    	WebElement skipButton = wnwd.getElementByXpath("//div[@class='el-popover el-popper emr-menu__more--popover']//span[@class='el-checkbox__input is-checked']");
//    	if (skipButton==null) {
//			wnwd.waitElementByXpathAndClick("展开模板推荐按钮", "//i[@class='el-popover__reference']", Frmcons.defaultTimeoutMax);
//			//wnwd.waitElementByXpathAndClick("选择模板推荐按钮", "//div[@class='el-popover el-popper emr-menu__more--popover']//span[@class='el-checkbox__input is-checked']", Frmcons.defaultTimeoutMax)
//		}
	}

	// 选择科目
	public void choiceSubject(String subject) {
		choiceSubject(new ArrayList<>(Arrays.asList(subject)));
	}

	public void choiceSubject(List<String> subjectList) {
		// 首先将所有科目选中状态去掉
		List<WebElement> search_result = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientCheckboxIsChecked,
				Framework.defaultTimeoutMid);
		for (WebElement checkedBox : search_result) {
			wnwd.wnClickElement(checkedBox, "取消勾选 " + checkedBox.getText(), false, false);
		}
		// 找到指定科室选中
		List<WebElement> checkboxlist = wnwd.waitElementListByXpath(
				WnOutpatientXpath.outpatientSubjectChooseBoxCheckBox, Framework.defaultTimeoutMax);
		for (WebElement webElement : checkboxlist) {
			Boolean chooseFlag = false;
			for (String subject : subjectList) {
				if (webElement.getText().contains(subject)) {
					chooseFlag = true;
					break;
				}
			}
			if (chooseFlag) {
				wnwd.wnClickElement(webElement, "勾选科目 " + webElement.getText());
			}
		}
		WebElement el = wnwd.getElementByXpath("//div[contains(.,'启用分诊')]/div[@role='switch' and contains(@class,'is-checked')]");
		if(el!=null) {
			wnwd.wnClickElement(el, "禁用分诊");
		}
		wnwd.waitElementByXpathAndClick("确认科目按钮", WnOutpatientXpath.outpatientSubjectChooseBox + "//button[.='确定']",
				Framework.defaultTimeoutMax);
//        WebElement subject_confirm_button = wnwd.waitElementByCssSelectorAndText(Data.subject_confirm_button, "确定", Frmcons.defaultTimeoutMax);
//        wnwd.wnClickElement(subject_confirm_button, "确认科目按钮");
		wnwd.waitNotExistByXpath("等待科目选择框不可见", WnOutpatientXpath.outpatientSubjectChooseBox, Framework.defaultTimeoutMax);
	}

	// 重新选择科室
	public void changeSubject(List<String> subjectList) {
		wnwd.waitElementByXpathAndClick("右上角图标", "//div[@class='win-patient-avatar']", Framework.defaultTimeoutMax);
		wnwd.moveToElementByXpath("开诊科目信息",
				"//div[@class='subjectsInfo']//span[contains(@class,'subjectsInfo-popover-modeSwitch el-popover__reference')]",
				Framework.defaultTimeoutMid);
		wnwd.waitElementByXpathAndClick("切换科目", "//p[@class='switch-subject']", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("就诊科目选择框", WnOutpatientXpath.outpatientSubjectChooseBox, Framework.defaultTimeoutMax);
		choiceSubject(subjectList);
	}

	// 根据名称叫号
	public void callNumberByName(String patientName) {
		try {
			wnwd.waitElementByXpathAndClick("候诊按钮", WnOutpatientXpath.waitListBotton, Framework.defaultTimeoutMax);
			cancelCheckboxInPatientList();
			wnwd.waitElementByXpathAndInput("患者搜索框", WnOutpatientXpath.outpatientPatientSearchInput, patientName,
					Framework.defaultTimeoutMax);
			WebElement Patient = wnwd.waitElementByXpath("患者" + patientName,
					"//div[@class='cell']//span[.='" + patientName + "']", Framework.defaultTimeoutMax);
			wnwd.wnDoubleClickElementByMouse(Patient, "双击患者:" + patientName);
			wnwd.waitNotExistByXpath("等待就诊列表消失", "//div[@id='header-search']", Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("叫号完成标识",
					"//span[contains(.,'" + patientName
							+ "') and (contains(@class,'basic-full-name') or contains(@class,'spe-username'))]",
					Framework.defaultTimeoutMax);
			bloodPressureConfirmIfNeed(true);
		} catch (Throwable e) {
			throw new Error("叫号患者失败:" + e.getMessage());
		}
	}

	// 针对两次挂号，按照序号叫号

	/**
	 * @param patientName    患者名称
	 * @param visitSeqNumber 就诊序号
	 * @describtion 根据名称搜索患者列表，根据序号叫号
	 */
	public void callNumberByNo(String patientName, String visitSeqNumber) {
		callNumberByNo(patientName, visitSeqNumber, false);
	}

	public void callNumberByNo(String patientName, String visitSeqNumber, Boolean dataConditionFlag) {
		try {
			wnwd.waitElementByXpathAndClick("点击候诊按钮", WnOutpatientXpath.waitListBotton, Framework.defaultTimeoutMax);
			cancelCheckboxInPatientList();
			if (dataConditionFlag) {
				WebElement begDataInput = wnwd.waitElementByXpath(
						WnOutpatientXpath.outpatientPatientSearchInputBeggingData, Framework.defaultTimeoutMid);
				begDataInput.clear();
				begDataInput.sendKeys("2021-01-01");
				wnwd.sendKeyEvent(Keys.ENTER);
			}
			wnwd.waitElementByXpathAndInput("患者搜索框", WnOutpatientXpath.outpatientPatientSearchInput, patientName,
					Framework.defaultTimeoutMax);
			wnwd.sleep(2000);
			String vn = visitSeqNumber.trim();
			vn = Integer.valueOf(vn).toString();
			WebElement Patient = wnwd.checkElementByXpath("患者号序" + vn,
					"//div[@class='cell el-tooltip']//span[.='" + vn + "']", Framework.defaultTimeoutMax);
			if (Patient == null && Data.getEncounterFromFile) {
				logger.boxLog(2, "搜索结果为空", "请确认配置的患者信息是否已过叫号有效期，重新挂号并更新encounter.txt文件里的患者信息");
			}
			wnwd.wnDoubleClickElementByMouse(Patient, "双击患者");
			wnwd.waitNotExistByXpath("等待就诊列表消失", "//div[@id='header-search']", Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("叫号完成标识",
					"//span[contains(.,'" + patientName
							+ "') and (contains(@class,'basic-full-name') or contains(@class,'spe-username'))]",
					Framework.defaultTimeoutMax);
			wnwd.sleep(Framework.defaultTimeoutMin);
			changeToNormalVisitIfNeed();
			commitPatientInfoIfNeed();
			closeInsuranceConfirmDialogIfNeed();
			commitDispenserInfoIfNeed(true);
			bloodPressureConfirmIfNeed(true);
		} catch (Throwable e) {
			throw new Error("叫号患者失败:" + e.getMessage());
		}
	}

	// 叫号完成后，检查页面信息是否和挂号信息一致
	public Map<String, String> getEncounterDetail() {
		WebElement sexEl = wnwd.waitElementByXpath("性别", "(//div[@class='spe-info']//span)[2]", Framework.defaultTimeoutMin);
		String sex = "";
		if (sexEl != null) {
			sex = sexEl.getText();
		} else {
			logger.assertFalse(true, "未找到页面性别信息");
		}

		WebElement ageEl = wnwd.waitElementByXpath("年龄", "(//div[@class='spe-info']//span)[3]", Framework.defaultTimeoutMin);
		String age = "";
		if (ageEl != null) {
			age = ageEl.getText();
		} else {
			logger.boxLog(2, "未找到页面科目信息", "未找到页面性别信息,请检查个人偏好中是否设置科目信息");
//			logger.assertFalse(true, "未找到页面年龄信息");
		}

		WebElement ybEl = wnwd.waitElementByXpath("保险信息", "//span[@class='insurance-overflow']",
				Framework.defaultTimeoutMin);
		String ybsm = "";
		if (ybEl != null) {
			ybsm = ybEl.getText();
		} else {
			logger.boxLog(2, "未找到页面保险信息", "未找到页面保险信息,请检查个人偏好中是否设置保险信息");
//			logger.assertFalse(true, "未找到页面保险信息");
		}

		WebElement subjectEl = wnwd.waitElementByXpath("科目信息", "//span[@class='register-overflow']",
				Framework.defaultTimeoutMin);
		String subject = "";
		if (subjectEl != null) {
			subject = subjectEl.getText();
		} else {
			logger.assertFalse(true, "未找到页面科目信息");
		}

		Map<String, String> encounterDetail = new HashMap<String, String>();
		encounterDetail.put("sex", sex);
		encounterDetail.put("age", age);
		encounterDetail.put("ybsm", ybsm);
		encounterDetail.put("subject", subject);
		return encounterDetail;

	}

	// 填写患者个人信息
	public void commitPatientInfoIfNeed() {
		WebElement patientInfoDialogSaveButton = wnwd.waitElementByXpath("患者信息弹窗",
				WnOutpatientXpath.outpatientPatientInfoDialogSaveButton, Framework.defaultTimeoutMin);
		if (patientInfoDialogSaveButton != null) {
			wnwd.wnClickElement(patientInfoDialogSaveButton, "保存并关闭按钮");
			wnwd.waitNotExistByXpath("等待患者信息窗口消失", WnOutpatientXpath.outpatientPatientInfoDialogSaveButton,
					Framework.defaultTimeoutMax);
		}
	}

	// 医保卡核对患者信息
	public void closeInsuranceConfirmDialogIfNeed() {
		WebElement InsuranceConfirmDialog = wnwd.waitElementByXpath("医保卡核对信息弹窗",
				WnOutpatientXpath.InsuranceConfirmDialog, Framework.defaultTimeoutMid);
		if (InsuranceConfirmDialog != null) {
			WebElement InsuranceConfirmDialogCloseBtn = wnwd.waitElementByXpath("医保卡核对信息弹窗关闭按钮",
					WnOutpatientXpath.InsuranceConfirmDialogCloseBtn, Framework.defaultTimeoutMin);
			wnwd.wnClickElement(InsuranceConfirmDialogCloseBtn, "关闭按钮");
			wnwd.waitNotExistByXpath("等待医保卡核对信息窗口消失", WnOutpatientXpath.InsuranceConfirmDialog,
					Framework.defaultTimeoutMid);
		}
	}

	// 填写待配药人信息
	public void commitDispenserInfoIfNeed(Boolean callpopupFlag) {
		if (callpopupFlag) {
			WebElement DispenserInfoDialog = wnwd.waitElementByXpath("代配药人窗口",
					WnOutpatientXpath.outpatientDispenserInfo, Framework.defaultTimeoutMin);
			if (DispenserInfoDialog != null) {
				wnwd.waitElementByXpathAndInput("代配药人姓名", "//div[contains(@class,'el-form-item') and .='代配人']//input",
						"张全蛋", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人关系下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'患者关系')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人关系下拉框 - 第一个选项", "//div[@x-placement]//ul/li",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("代配药人联系方式",
						"//div[contains(@class,'el-form-item') and .='联系方式']//input", "13838383838",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人联系地址 - 下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'联系地址')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人联系地址 - 省 - 第一个选项",
						"//div[@class='el-cascader-panel']/div[1]//ul/li/span", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人联系地址 - 市 - 第一个选项",
						"//div[@class='el-cascader-panel']/div[2]//ul/li/span", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人联系地址 - 区 - 第一个选项",
						"//div[@class='el-cascader-panel']/div[3]//ul/li/span", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人联系地址 - 街道 - 第一个选项",
						"//div[@class='el-cascader-panel']/div[4]//ul/li/span", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("代配药人联系地址输入框",
						"//div[contains(@class,'el-form-item') and .='联系地址']//div[contains(@class,'address-input')]//input",
						"一栋一单元一号", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人证件信息 - 下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'证件信息')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人证件信息 - 第一个选项", "//div[@x-placement]//ul/li",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("代配药人联系地址输入框",
						"//div[contains(@class,'el-form-item') and .='证件信息']//div[contains(@class,'address-input')]//input",
						"123456789088886666", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("代配药人确认按钮",
						"//div[@class='next-patient-confirm-wrapper' and contains(.,'代配药人信息')]//button[.='确认']",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpath("等待代配药人窗口消失", WnOutpatientXpath.outpatientDispenserInfo,
						Framework.defaultTimeoutMin);
			}
		} else {
			SdkTools.logger.log("无需填写代配药人相关信息");
		}

	}

//    //高血压管理信息
//    public void bloodPressureConfirmIfNeed() {
//        WebElement bloodPressureConfirm = wnwd.waitElementByXpath("//div[@class='blood-pressure-confirm']", Frmcons.defaultTimeoutMin);
//        if (bloodPressureConfirm != null) {
//            wnwd.waitElementByXpathAndClick("取消输入高血压信息", "//div[@class='blood-pressure-confirm']//span[contains (.,'取 消')]", Frmcons.defaultTimeoutMin);
//        }
//    }
//高血压管理信息
	public void bloodPressureConfirmIfNeed(boolean callpopupFlag) {
		if (callpopupFlag) {
			WebElement bloodPressureConfirm = wnwd.waitElementByXpath("//div[@class='blood-pressure-confirm']",
					Framework.defaultTimeoutMin);
			if (bloodPressureConfirm != null) {
				wnwd.waitElementByXpathAndClick("取消输入高血压信息",
						"//div[@class='blood-pressure-confirm']//span[contains (.,'取 消')]", Framework.defaultTimeoutMin);
			}
		} else {
			SdkTools.logger.log("无需填写相关高血压信息");
		}

	}

	// 取消勾选午别和本医生
	public void cancelCheckboxInPatientList() {
		List<WebElement> chechedItems = wnwd
				.getElementListByXpath("//div[@id='header-search']//label[contains(@class,'el-checkbox is-checked')]");
		for (WebElement item : chechedItems) {
			wnwd.wnClickElement(item, "取消勾选 " + item.getText());
		}
	}

	// 医保审批窗口
	public void own_expense() {
		own_expense(null);
	}

	// 医保审批窗口
	public void own_expense(Boolean findDialog) {
		try {
			wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", WnOutpatientXpath.outpatientDisposalFactory,
					Framework.defaultTimeoutMax);
			WebElement dialog = null;
			WebElement loadingBox = null;
			long endTime = System.currentTimeMillis() + Framework.defaultTimeoutMax;
			while (System.currentTimeMillis() < endTime) {
				dialog = wnwd.getElementByXpath(WnOutpatientXpath.outpatientLabInsuranceTypeDialog);
				if (dialog != null) {
					break;
				}
				loadingBox = wnwd.getElementByXpath(WnOutpatientXpath.outpatientRevokeLoading);
				if (loadingBox == null) {
					break;
				}
			}
			if (loadingBox != null && dialog == null) {
				if (Data.getScreenShot) {
					wnwd.getScreenShot("开立医嘱后加载超时");
				}
				throw new Error("开立医嘱后加载超时");
			}
			if (findDialog != null) {
				logger.assertFalse((dialog == null) == findDialog, findDialog == true ? "未检查到 付费类型弹窗" : "不应弹出 付费类型弹窗");
			}
			if (dialog != null) {
				List<WebElement> unSelectBoxes = dialog
						.findElements(By.xpath(WnOutpatientXpath.outpatientUnselectedBoxes));
				for (int i = 0; i < unSelectBoxes.size(); i++) {
					wnwd.wnClickElement(unSelectBoxes.get(i), "选择自费");
				}
				wnwd.waitElementByXpathAndClick("付费类型弹窗 - 确定按钮",
						WnOutpatientXpath.outpatientLabInsuranceTypeDialogCommitButton, Framework.defaultTimeoutMin);
				wnwd.waitNotExistByXpath("等待 付费类型弹窗 消失", WnOutpatientXpath.outpatientLabInsuranceTypeDialog,
						Framework.defaultTimeoutMax);
			}
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			throw new Error("选择付费类型失败:" + e.getMessage());
		}
	}

	// 获取当前体系
	public String getKnowledgeSystem() {
		String knowledgeSystem = null;
		WebElement element = wnwd.waitElementByXpaths(new ArrayList<String>(
				Arrays.asList("//button[@class='el-button change-symptom-type el-button--default el-button--small']",
						"//div[@class='diagnostic-search-button']")),
				Framework.defaultTimeoutMax);
		if (element != null && element.getAttribute("class").contains("change-symptom-type")) {
			knowledgeSystem = "卫宁医学知识体系";
		} else if (element != null && element.getAttribute("class").contains("diagnostic-search-button")) {
			knowledgeSystem = "诊疗路径简易版";
		} else {
			logger.assertFalse(true, "获取知识体系失败");
		}
		logger.log(1, "当前体系:" + knowledgeSystem);
		return knowledgeSystem;
	}

	// 选择一个主诉
	public void chiefComplaint(String symptomName) {
		String knowledgeSystem = getKnowledgeSystem();
		if (knowledgeSystem.equals("卫宁医学知识体系")) {
			chiefComplaint_knowledge(symptomName);
		} else if (knowledgeSystem.equals("诊疗路径简易版")) {
			chiefComplaint_pathWay(symptomName);
		} else {
			logger.assertFalse(true, "不支持的知识体系:" + knowledgeSystem);
		}
	}

	// 选择主诉(知识体系)
	public void chiefComplaint_knowledge(String symptomName) {
		try {
			changeSymptomType("单症状");
			SdkTools.sleep(1000);
			wnwd.waitElementByXpathAndClick("展开更多主诉 ",
					"//div[@class='select-chief-complaint-wrap']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("知识体系 - 更多主诉 ",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'更多')]", Framework.defaultTimeoutMax);
			SdkTools.sleep(1000);
			wnwd.waitElementByXpathAndInput("主诉搜索框",
					"//div[contains(@class,'more-symptom-wrap')]//input[@placeholder='请输入内容']", symptomName,
					Framework.defaultTimeoutMax);
			WebElement symptom = wnwd.checkElementByXpath("主诉搜索结果",
					"//div[contains(@class,'search-symptom-wrap')]//div[contains(@class,'search-symptom-item')]/label[contains(.,'"
							+ symptomName + "')]",
					Framework.defaultTimeoutMax);
			if (symptom.getAttribute("class").contains("is-checked")) {
				throw new Error("主诉已选择");
			} else {
				wnwd.wnClickElement(symptom, "主诉: " + symptomName);
			}
			WebElement dayInput = wnwd.waitElementByXpathAndInput("症状时间输入框",
					WnOutpatientXpath.outpatientSymptomDayInput, "3", Framework.defaultTimeoutMax);
			dayInput.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName
							+ "') and contains(.,'3')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("关闭", "//div[contains(@class,'more-symptom-wrap')]//button[.='关闭']",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加主诉失败:" + e.getMessage());
		}
	}

	// 选择主诉(诊疗路径)
	public void chiefComplaint_pathWay(String symptomName) {
		try {
			wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("诊疗路径简易版 - 症状按钮", WnOutpatientXpath.outpatientSymptomWarpButton,
					Framework.defaultTimeoutMax);
			changeSymptomType("单症状");
			WebElement symptomElement = wnwd.waitElementByXpath("主诉",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMax);
			if (symptomElement == null) {
				wnwd.waitElementByXpathAndClick("新增症状按钮", WnOutpatientXpath.outpatientChiefComplaintAdd,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("症状搜索框", WnOutpatientXpath.outpatientAddSymptomSearchInput, symptomName,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定按钮", WnOutpatientXpath.outpatientChiefComplaintAddCommitButton,
						Framework.defaultTimeoutMax);
			}
			wnwd.waitElementByXpathAndClick("主诉",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMax);
			WebElement dayInput = wnwd.waitElementByXpathAndInput("输入症状时间", WnOutpatientXpath.outpatientSymptomDayInput,
					"3", Framework.defaultTimeoutMax);
			dayInput.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName
							+ "') and contains(.,'3')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉框关闭按钮",
					"//div[contains(@class,'chief-complaint-header')]//i[contains(@class,'el-icon-close')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加主诉失败:" + e.getMessage());
		}
	}

	// 选择主诉(产科模式)
	public void chiefComplaint_pregnant(String symptomName) {
		try {
			changeSymptomType("单症状");
			WebElement symptomElement = wnwd.waitElementByXpath("主诉",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMid);
			if (symptomElement == null) {
				wnwd.waitElementByXpathAndClick("新增症状按钮", WnOutpatientXpath.outpatientChiefComplaintAdd,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("症状搜索框", WnOutpatientXpath.outpatientAddSymptomSearchInput, symptomName,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定按钮", WnOutpatientXpath.outpatientChiefComplaintAddCommitButton,
						Framework.defaultTimeoutMax);
			}
			wnwd.waitElementByXpathAndClick("主诉",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMax);
			WebElement dayInput = wnwd.waitElementByXpathAndInput("输入症状时间", WnOutpatientXpath.outpatientSymptomDayInput,
					"3", Framework.defaultTimeoutMax);
			dayInput.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName
							+ "') and contains(.,'3')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加主诉失败:" + e.getMessage());
		}
	}

	// 选择主诉(诊疗路径 - 体检异常)
	public String chiefComplaint_pathWay_physicalExam(String type, String desc) {
		try {
			wnwd.waitElementByXpathAndClick("诊疗路径简易版 - 症状按钮", WnOutpatientXpath.outpatientSymptomWarpButton,
					Framework.defaultTimeoutMax);
			changeSymptomType("体检异常");
			wnwd.waitElementByXpathAndClick(type + " 类型主诉",
					"//div[contains(@class,'chief-complaint-name') and contains(.,'" + type + "')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick(type + " 下拉框",
					"//div[contains(@class,'chief-complaint-item')]//div[@title='" + type + "']",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("", "//div[@class='physical-list-popover']//input", "高血压",
					Framework.defaultTimeoutMax);
			wnwd.sleep(500);
			WebElement element = wnwd.waitElementByXpathAndClick("第一个搜索结果",
					"//div[contains(@class,'physical-list')]//div[@title and not(contains(@title,'*'))]",
					Framework.defaultTimeoutMax);
			String text = element.getText();
			wnwd.waitElementByXpathAndInput("症状描述输入框",
					"//div[contains(@class,'chief-complaint-item')]//div[contains(@class,'physical-result-input')]//input",
					desc, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("体检发现",
					"//div[contains(@class,'physical-find-title') and contains(.,'体检发现')]", Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("描述:" + desc,
					"//div[contains(@class,'chief-complaint-item') and contains(.,'" + desc + "')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉框关闭按钮",
					"//div[contains(@class,'chief-complaint-header')]//i[contains(@class,'el-icon-close')]",
					Framework.defaultTimeoutMax);
			return text;
		} catch (Throwable e) {
			logger.assertFalse(true, "添加主诉失败:" + e.getMessage());
		}
		return null;
	}

	// 修改主诉
	public void updateChiefComplaint(String symptomName) {
		try {
			wnwd.waitElementByXpathAndClick("诊疗路径简易版 - 症状按钮", WnOutpatientXpath.outpatientSymptomWarpButton,
					Framework.defaultTimeoutMax);
			WebElement targetSymptom = wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMax);
			wnwd.wnClickElement(targetSymptom, "主诉:" + symptomName);
			WebElement dayInput = wnwd.waitElementByXpathAndInput("症状时间输入框",
					WnOutpatientXpath.outpatientSymptomDayInput, "5", Framework.defaultTimeoutMax);
			dayInput.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("修改后的主诉:" + symptomName + " 5天",
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName
							+ "') and contains(.,'5')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉框关闭按钮",
					"//div[contains(@class,'chief-complaint-header')]//i[contains(@class,'el-icon-close')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "修改主诉失败:" + e.getMessage());
		}
	}

	// 删除主诉(诊疗路径)
	public void deleteChiefComplaint_pathWay(String symptomName) {
		try {
			wnwd.waitElementByXpathAndClick("诊疗路径简易版 - 症状按钮", WnOutpatientXpath.outpatientSymptomWarpButton,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName
							+ "')]/..//i[contains(@class,'el-icon-error')]",
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待主诉删除成功",
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'" + symptomName + "')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉框关闭按钮",
					"//div[contains(@class,'chief-complaint-header')]//i[contains(@class,'el-icon-close')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "删除主诉失败:" + e.getMessage());
		}
	}

	// 删除主诉(知识体系)
	public void deleteChiefComplaint_knowledge(String symptomName) {
		try {
			wnwd.waitElementByXpathAndClick("删除主诉按钮",
					"//div[contains(@class,'chief-complaint-item') and contains(.,'?1')]//i[contains(@class,'el-icon-error')]"
							.replace("?1", symptomName),
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待主诉删除成功",
					"//div[contains(@class,'chief-complaint-item') and contains(.,'?1')]".replace("?1", symptomName),
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "删除主诉失败:" + e.getMessage());
		}
	}

	// 选择一个诊断
	public void diagnosis(String disease) {
		String knowledgeSystem = getKnowledgeSystem();
		if (knowledgeSystem.equals("卫宁医学知识体系")) {
			diagnosis_knowledge(disease);
		} else if (knowledgeSystem.equals("诊疗路径简易版")) {
			diagnosis_pathWay(disease);
		} else {
			logger.assertFalse(true, "不支持的知识体系:" + knowledgeSystem);
		}
	}

	// 检查知识推荐包含诊断
	public void checkDiagnose_knowledge(String disease) {
		try {
			wnwd.checkElementByXpath("知识域推荐诊断:" + disease,
					"//div[contains(@class,'diagnosis-checktext') and .='" + disease + "']", Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "知识域没有推荐诊断" + disease);
		}
	}

	// 检查药品用量控制提示框
	public void checkDrugDosageControlMsg(String warningMsg) {
		WebElement ele = wnwd.waitElementByXpath("药品用量控制提醒", "//span[.='" + warningMsg + "数量提示：']",
				Framework.defaultTimeoutMax);
		if (ele == null) {
			logger.assertFalse(true, "药品用量控制的" + warningMsg + "提醒数量提示未出现");
		}else {
			wnwd.getScreenShot("找到药品用量控制提醒");
		}
	}

	// 检查医嘱签署状态
	public void checkOrderStatus(boolean signoff) {
		if (signoff == true) {
			WebElement ele = wnwd.waitElementByXpath("医嘱已签署状态标识", WnOutpatientXpath.outpatientSignedOrder,
					Framework.defaultTimeoutMax);
			if (ele == null) {
				logger.assertFalse(true, "医嘱状态错误，不是已签署状态");
			} else {
				wnwd.getScreenShot("医嘱状态正确，是已签署状态");
			}
		} else {
			WebElement ele = wnwd.waitElementByXpath("医嘱未签署状态标识", WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMax);
			if (ele == null) {
				logger.assertFalse(true, "医嘱状态错误，不是未签署状态");
			} else {
				wnwd.getScreenShot("医嘱状态正确，是未签署状态");
			}
		}
	}

	// 执行药品用量控制签署
	public void executeDrugDosageControlSingoff(boolean value) {
		if (value == true) {
			wnwd.waitElementByXpathAndClick("签署", "//div[@class='dosage-control']//span[.='签署']",
					Framework.defaultTimeoutMid);
		} else {
			wnwd.waitElementByXpathAndClick("中断签署", "//span[.='中断签署']", Framework.defaultTimeoutMid);
		}

	}

	// 症型治法选择
	private void selectZxAndZf() {
		WebElement zxSelect = wnwd.waitElementByXpath("症型下拉框", WnOutpatientXpath.outpatientSearchZxFirstSelectvalue,
				Framework.defaultTimeoutMax); // 症型选择下拉框
		if (zxSelect != null) {
			wnwd.waitElementByXpathAndClick("点击症型下拉框第一条value", WnOutpatientXpath.outpatientSearchZxFirstSelectvalue,
					Framework.defaultTimeoutMax);
			SdkTools.sleep(1000);
			wnwd.waitElementByXpathAndClick("点击治法下拉框第一条value", WnOutpatientXpath.outpatientSearchZfFirstSelectvalue,
					Framework.defaultTimeoutMax);
			SdkTools.sleep(1000);
		}
	}

	// 点击诊疗路径第一条诊断
	public void clickDignoselistFirstDignose() {
		// 诊断知识域不存在该诊断，再进行诊断开立
		List<WebElement> checkKnowledgeList = wnwd.waitElementListByXpath(WnOutpatientXpath.knowledgeDignose,
				Framework.defaultTimeoutMid);
		if (checkKnowledgeList.size() > 0) {
			for (int i = 0; i < checkKnowledgeList.size(); i++) {
				deleteFirstDignose();
			}
			logger.boxLog(1, "存在诊断", "知识诊断域已经存在，删除已存在诊断");
		}

		wnwd.waitElementByXpathAndClick("点击诊疗路径第一个诊断", WnOutpatientXpath.DignoselistFirstDignose,
				Framework.defaultTimeoutMid);
		WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
				Framework.defaultTimeoutMin);
		if (cancelBtn != null) {
			wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
		}
	}

	// 删除知识域第一个诊断
	public void deleteFirstDignose() {
		WebElement firstDignose = wnwd.waitElementByXpath(WnOutpatientXpath.knowledgeDignose,
				Framework.defaultTimeoutMid);
		String diseaseName = firstDignose.getText();
		deleteDiagnosis_pathWay(diseaseName);
	}

	// 选择一个诊断(卫宁知识体系)
	public void diagnosis_knowledge(String disease) {
		try {
			wnwd.waitElementByXpathAndClick("诊断搜索图标", WnOutpatientXpath.outpatientDiagnosisSearchButton,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput, disease,
					Framework.defaultTimeoutMax);
			SdkTools.sleep(1000);
			wnwd.waitElementByXpathAndClick("诊断:" + disease,
					WnOutpatientXpath.diagnoseResult.replace("?disease", disease), Framework.defaultTimeoutMid);
			SdkTools.sleep(1000);
			selectZxAndZf();
			wnwd.waitElementByXpathAndClick("确定诊断按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待诊断选择框不可见", WnOutpatientXpath.outpatientDiagnosisEditBox,
					Framework.defaultTimeoutMax);
			WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMin);
			if (cancelBtn != null) {
				wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
			}
			wnwd.waitElementByXpathAndClick("检查知识域诊断",
					"//div[contains(@class,'diagnostic-item-name') and contains(.,'" + disease + "')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加诊断(卫宁知识体系)失败:" + e.getMessage());
		}
	}

	// 选择一个诊断(诊疗路径)
	public void diagnosis_pathWay(String disease) {
		try {
			wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
			WebElement findlist = wnwd.waitElementByXpath("收藏诊断:" + disease,
					WnOutpatientXpath.findDignoseCheck.replace("?disease", disease), Framework.defaultTimeoutMid);
			if (findlist == null) {
				wnwd.waitElementByXpathAndClick("诊断搜索图标", WnOutpatientXpath.outpatientDiagnosisSearchButton,
						Framework.defaultTimeoutMax);
				
				wnwd.waitElementByXpathAndClick("诊断搜索图标", "//div[@class='diagnostic-search-header']//i[contains(@class,'el-icon-arrow-up')]",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("诊断搜索图标", "//div[@class='el-scrollbar']//span[contains(text(),'全部')]",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput, disease,
						Framework.defaultTimeoutMax);
				SdkTools.sleep(1000);
				WebElement ele = wnwd.waitElementByXpath("诊断收藏",
						WnOutpatientXpath.ValueDescwrapSvg.replace("?disease", disease), Framework.defaultTimeoutMid);
				ele.click();
				wnwd.waitElementByXpathAndClick("诊疗路径收藏按钮", WnOutpatientXpath.diagnosticCollection,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("收藏确定", WnOutpatientXpath.diagnosticCollectionCommitBtm,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("诊断确定按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
						Framework.defaultTimeoutMax);
			}

			wnwd.waitElementByXpathAndClick("诊疗路径tab", "//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("收藏诊断:" + disease, WnOutpatientXpath.findDignoseCheck.replace("?disease", disease), Framework.defaultTimeoutMax);

			WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMin);
			if (cancelBtn != null) {
				wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
			}
			wnwd.checkElementByXpath("检查知识域诊断",
					"//div[contains(@class,'diagnostic-item-name') and contains(.,'" + disease + "')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加诊断(诊疗路径)失败:" + e.getMessage());
		}
	}

	// 诊疗路径 添加多个诊断
	public void Multiplediagnosis_pathWay(String disease1, String disease2) {
		try {
			WebElement findlist1 = wnwd.waitElementByXpath("收藏诊断:" + disease1,
					WnOutpatientXpath.findDignoseCheck.replace("?disease", disease1), Framework.defaultTimeoutMid);
			if (findlist1 == null) {
				wnwd.waitElementByXpathAndClick("诊断搜索图标", WnOutpatientXpath.outpatientDiagnosisSearchButton,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput, disease1,
						Framework.defaultTimeoutMax);
				SdkTools.sleep(1000);
				WebElement ele = wnwd.waitElementByXpath("诊断收藏",
						WnOutpatientXpath.ValueDescwrapSvg.replace("?disease", disease1), Framework.defaultTimeoutMid);
				ele.click();
				wnwd.waitElementByXpathAndClick("诊疗路径收藏按钮", WnOutpatientXpath.diagnosticCollection,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("收藏确定", WnOutpatientXpath.diagnosticCollectionCommitBtm,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("诊断确定按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
						Framework.defaultTimeoutMax);
			}
			wnwd.waitElementByXpathAndClick("收藏诊断:" + disease1,
					WnOutpatientXpath.findDignoseCheck.replace("?disease", disease1), Framework.defaultTimeoutMax);
			WebElement cancelBtn1 = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMid);
			if (cancelBtn1 != null) {
				wnwd.wnClickElement(cancelBtn1, "诊断关联病历模板弹窗-取消按钮");
			}
			wnwd.checkElementByXpath("检查知识域诊断",
					"//div[contains(@class,'diagnostic-item-name') and contains(.,'" + disease1 + "')]",
					Framework.defaultTimeoutMax);

			WebElement findlist2 = wnwd.waitElementByXpath("收藏诊断:" + disease2,
					WnOutpatientXpath.findDignoseCheck.replace("?disease", disease2), Framework.defaultTimeoutMid);
			if (findlist2 == null) {
				wnwd.waitElementByXpathAndClick("诊断搜索图标", WnOutpatientXpath.outpatientDiagnosisSearchButton,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndInput("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput, disease2,
						Framework.defaultTimeoutMax);
				SdkTools.sleep(1000);
				WebElement ele = wnwd.waitElementByXpath("诊断收藏",
						WnOutpatientXpath.ValueDescwrapSvg.replace("?disease", disease2), Framework.defaultTimeoutMid);
				ele.click();
				wnwd.waitElementByXpathAndClick("诊疗路径收藏按钮", WnOutpatientXpath.diagnosticCollection,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("收藏确定", WnOutpatientXpath.diagnosticCollectionCommitBtm,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("诊断确定按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
						Framework.defaultTimeoutMax);
			}
			wnwd.waitElementByXpathAndClick("收藏诊断:" + disease1,
					WnOutpatientXpath.findDignoseCheck.replace("?disease", disease2), Framework.defaultTimeoutMax);
			WebElement cancelBtn2 = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMid);
			if (cancelBtn2 != null) {
				wnwd.wnClickElement(cancelBtn1, "诊断关联病历模板弹窗-取消按钮");
			}
			wnwd.checkElementByXpath("检查知识域诊断",
					"//div[contains(@class,'diagnostic-item-name') and contains(.,'" + disease2 + "')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加诊断(诊疗路径)失败:" + e.getMessage());
		}
	}

	// 编辑诊断
	public void updateDiagnosis_pathWay(String disease) {
		try {
			WebElement targetDiagnose = wnwd.checkElementByXpath("诊断:" + disease,
					"//div[@class='diagnostic-item-name' and contains(.,'" + disease + "')]",
					Framework.defaultTimeoutMax);
			wnwd.moveToElement(targetDiagnose, "诊断:" + disease);
			WebElement editIcon = wnwd.checkElementByXpath("诊断编辑图标",
					"//*[name() = 'svg' and contains(@class,'svg-icon icon-edit')]", Framework.defaultTimeoutMax);
			wnwd.wnClickElementByMouse(editIcon, "诊断编辑图标");
			wnwd.checkElementByXpath("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput,
					Framework.defaultTimeoutMax);
			List<WebElement> inputList = wnwd.waitElementListByXpath(
					"//div[contains(@class,'diagnostic-editor')]//div[@class='diagnostic-item' and contains(.,'"+disease+"')]//input",
					Framework.defaultTimeoutMax);
			logger.assertFalse(inputList.size() < 2, "找不到诊断编辑输入框");
			wnwd.wnEnterText(inputList.get(0), "前缀", "诊断前缀输入框");
			wnwd.wnEnterText(inputList.get(1), "后缀", "诊断后缀输入框");
			wnwd.waitElementByXpathAndClick("诊断确定按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("诊断: 前缀 + " + disease + " +后缀",
					"//div[@class='diagnostic-item-name' and contains(.,'" + disease
							+ "') and contains(.,'前缀') and contains(.,'后缀')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "编辑诊断失败:" + e.getMessage());
		}
	}

	// 删除诊断
	public void deleteDiagnosis_pathWay(String disease) {
		try {
			WebElement targetDiagnose = wnwd.checkElementByXpath("诊断:" + disease,
					"//div[@class='diagnostic-item-name' and contains(.,'" + disease + "')]",
					Framework.defaultTimeoutMax);
			wnwd.moveToElement(targetDiagnose, "诊断:" + disease);
			WebElement editIcon = wnwd.checkElementByXpath("诊断删除图标",
					"//div[contains(@class,'diagnostic-item complete') and contains(.,'" + disease
							+ "')]//i[contains(@class,'el-icon-error')]",
					Framework.defaultTimeoutMax);
			wnwd.wnClickElementByMouse(editIcon, "诊断编辑图标");
			diagnosisIfneed();
			wnwd.waitNotExistByXpath("等待诊断删除成功",
					"//div[@class='diagnostic-item-name' and contains(.,'" + disease + "')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "删除诊断失败:" + e.getMessage());
		}
	}

	// 删除诊断弹出确认
	public void diagnosisIfneed() {
		WebElement deldiagnosisButton = wnwd.waitElementByXpath("医嘱关联诊断删除确认",
				"//div[@class='el-message-box__btns']//button[contains(.,'确定')]", 2000);
		if (deldiagnosisButton != null) {
			wnwd.wnClickElement(deldiagnosisButton, "诊断删除确认");
		}
	}

	// 诊疗路径删除诊断的所有推荐
	public void deleteRecommendOrder_pathWay() {
		WebElement checkBox = wnwd.checkElementByXpath("全选",
				"//div[@id='recommendation']//div[contains(@class,'treatment-title-wrap')]//label[contains(@class,'el-checkbox') and .='全选']",
				Framework.defaultTimeoutMax);
		if (checkBox.getAttribute("class").contains("is-checked")) {
			wnwd.wnClickElement(checkBox, "取消全选");
		}
		wnwd.wnClickElement(checkBox, "全选");
		wnwd.waitElementByXpathAndClick("删除推荐按钮",
				"//div[@id='recommendation']//div[contains(@class,'treatment-title-wrap')]//span[.='批量删除']",
				Framework.defaultTimeoutMax);
		WebElement commitBtn = wnwd.waitElementByXpath("确认删除按钮",
				"//div[contains(@class,'el-message-box')]//button[contains(.,'确定')]", Framework.defaultTimeoutMin);
		if (commitBtn != null) {
			wnwd.wnClickElement(commitBtn, "确认删除按钮");
		}
		wnwd.waitNotExistByXpath("等待推荐医嘱删除完成",
				"//div[contains(@class,'treatment-labtest-wrap') or contains(@class,'treatment-exam-wrap') or contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']",
				Framework.defaultTimeoutMax);
	}

	// 诊疗路径拖入诊断的推荐
	public void addRecommendOrder_pathWay(String type, String orderName) {
		try {
			String boxXpath = "";
			if (type.toLowerCase().equals("lab")) {
				boxXpath = WnOutpatientXpath.recommendLabBox_pathWay;
			} else if (type.toLowerCase().equals("exam")) {
				boxXpath = WnOutpatientXpath.recommendExamBox_pathWay;
			} else if (type.toLowerCase().equals("treat")) {
				boxXpath = WnOutpatientXpath.recommendTreatBox_pathWay;
			} else {
				throw new Error("添加类型 type错误:" + type);
			}
			prescribeOrder(orderName);
			WebElement order = wnwd.checkElementByXpath("医嘱",
					"//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap') and contains(.,'开立')]//span[contains(@class,'win-icon-delete')]",
					Framework.defaultTimeoutMax);
			WebElement labBox = wnwd.checkElementByXpath("检查框", boxXpath, Framework.defaultTimeoutMax);
			wnwd.drag("拖动检查到收藏", order, labBox);
			if (type.toLowerCase().equals("lab")) {
				wnwd.moveToElementByXpath("鼠标移动到检验",
						"//div[contains(@class,'treatment-labtest-wrap')]//span[@class='el-checkbox__label']",
						Framework.defaultTimeoutMax);

			} else if (type.toLowerCase().equals("exam")) {
				wnwd.moveToElementByXpath("鼠标移动到检验",
						"//div[contains(@class,'treatment-exam-wrap')]//span[@class='el-checkbox__label']",
						Framework.defaultTimeoutMax);

			} else if (type.toLowerCase().equals("treat")) {
				wnwd.moveToElementByXpath("鼠标移动到治疗",
						"//div[contains(@class,'treatment-drug-wrap')]//table[@class='el-table__body']",
						Framework.defaultTimeoutMax);

			}
			wnwd.checkElementByXpath("推荐检查", boxXpath + "//*[name() = 'svg']", Framework.defaultTimeoutMax);
			deleteOrders();
		} catch (Throwable e) {
			logger.assertFalse(true, "诊疗路径添加推荐失败:" + e.getMessage());
		}
	}

	public void checkRecommendLab_pathWay() {
		WebElement firstRecommendLab = wnwd.waitElementByXpath("推荐检验",
				"//div[contains(@class,'treatment-labtest-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMin);
		if (firstRecommendLab == null) {
			addRecommendOrder_pathWay("lab", Data.test_prescribe_lab);
		}
	}

	public void checkRecommendExam_pathWay() {
		WebElement firstRecommendLab = wnwd.waitElementByXpath("推荐检查",
				"//div[contains(@class,'treatment-exam-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMin);
		if (firstRecommendLab == null) {
			addRecommendOrder_pathWay("exam", Data.test_prescribe_exam);
		}
	}

	public void checkRecommendTreat_pathWay() {
		WebElement firstRecommendLab = wnwd.waitElementByXpath("推荐治疗",
				"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMin);
		if (firstRecommendLab == null) {
			addRecommendOrder_pathWay("treat", Data.test_prescribe_treat);
		}
	}

	// 诊疗路径引用推荐检验
	public void quoteRecommendLab_pathWay() {
		wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
		WebElement dignoseCheckBox = wnwd.waitElementByXpath("诊断勾选框",
				WnOutpatientXpath.dignoseCheckBox, Framework.defaultTimeoutMid);
		if (!dignoseCheckBox.getAttribute("class").contains("is-checked")) {
			wnwd.waitElementByXpathAndClick("点击勾选诊断","//div[@class='diagnostic-item-wrap']//label[@class='el-checkbox diagnostic-item-checkbox']//span[@class='el-checkbox__input']",Framework.defaultTimeoutMax);
		}
		wnwd.moveToElementByXpath("鼠标移动到检验",
				"//div[contains(@class,'treatment-labtest-wrap')]//span[@class='el-checkbox__label']",
				Framework.defaultTimeoutMax);
		WebElement el = wnwd.checkElementByXpath("推荐检验",
				"//div[contains(@class,'treatment-labtest-wrap')]//*[name() = 'svg' and contains(@class,'quote')]",
				Framework.defaultTimeoutMax);
		wnwd.wnClickElementByMouse(el, "推荐检验");
		wnwd.sleep(5000);
		own_expense();
		wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 诊疗路径引用推荐检查
	public void quoteRecommendExam_pathWay() {
		wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
		WebElement dignoseCheckBox = wnwd.waitElementByXpath("诊断勾选框",
				WnOutpatientXpath.dignoseCheckBox, Framework.defaultTimeoutMid);
		if (!dignoseCheckBox.getAttribute("class").contains("is-checked")) {
			wnwd.waitElementByXpathAndClick("点击勾选诊断","//div[@class='diagnostic-item-wrap']//label[@class='el-checkbox diagnostic-item-checkbox']//span[@class='el-checkbox__input']",Framework.defaultTimeoutMax);
		}
		wnwd.moveToElementByXpath("鼠标移动到检查",
				"//div[contains(@class,'treatment-exam-wrap')]//span[@class='el-checkbox__label']",
				Framework.defaultTimeoutMax);
		WebElement el = wnwd.checkElementByXpath("推荐检查",
				"//div[contains(@class,'treatment-exam-wrap')]//*[name() = 'svg' and contains(@class,'quote')]",
				Framework.defaultTimeoutMax);
		wnwd.wnClickElementByMouse(el, "推荐检查");
		wnwd.sleep(5000);
		own_expense();
		wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 诊疗路径引用推荐治疗
	public void quoteRecommendTreat_pathWay() {
		wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击勾选诊断","//div[@class='diagnostic-item-wrap']//label[@class='el-checkbox diagnostic-item-checkbox']//span[@class='el-checkbox__input']",Framework.defaultTimeoutMax);
		wnwd.moveToElementByXpath("鼠标移动到治疗",
				"//div[contains(@class,'treatment-drug-wrap')]//table[@class='el-table__body']",
				Framework.defaultTimeoutMax);
		WebElement el = wnwd.checkElementByXpath("推荐治疗",
				"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg' and contains(@class,'quote')]",
				Framework.defaultTimeoutMax);
		wnwd.wnClickElementByMouse(el, "推荐治疗");
		own_expense();
		wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 卫宁知识体系引用检验
	public void quoteRecommendLab_knowledge() {
		List<WebElement> allLabs = wnwd.waitElementListByXpath(
				"//div[@class='diagnosis-detail-item checkout-wrap']//div[contains(@class,'inspect-name')]",
				Framework.defaultTimeoutMax);
		wnwd.assertTrue("卫宁知识体系 推荐检验为空", allLabs.size() > 0);
		WebElement targetLab = allLabs.get(0);
		wnwd.moveToElement(targetLab, "鼠标移动到检验:" + targetLab.getText());
		wnwd.waitElementByXpathAndClick("点击开立", WnOutpatientXpath.outpatientRecommendLabFirstPrescribeBtn,
				Framework.defaultTimeoutMax);
		String orderType = getDetailType();
		wnwd.assertTrue("当前加工厂类型不是检验", orderType.equals("lab"));
		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		// 输入医保审批
		own_expense(null);
		wnwd.checkElementByXpath("检查开立结果", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 卫宁知识体系引用检查
	public void quoteRecommendExam_knowledge() {
		List<WebElement> allExams = wnwd.waitElementListByXpath(
				"//div[@class='diagnosis-detail-item inspect-wrap']//div[contains(@class,'inspect-name')]",
				Framework.defaultTimeoutMax);
		wnwd.assertTrue("卫宁知识体系 推荐检查为空", allExams.size() > 0);
		WebElement targetExam = allExams.get(0);
		wnwd.moveToElement(targetExam, "鼠标移动到检查:" + targetExam.getText());
		wnwd.waitElementByXpathAndClick("点击开立", WnOutpatientXpath.outpatientRecommendExamFirstPrescribeBtn,
				Framework.defaultTimeoutMax);
		String orderType = getDetailType();
		wnwd.assertTrue("当前加工厂类型不是检查", orderType.equals("exam"));
//        editExam(new ArrayList<String>(Arrays.asList("ALL")));
		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		// 输入医保审批
		own_expense(null);
		wnwd.checkElementByXpath("检查开立结果", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 卫宁知识体系引用药品
	public void quoteRecommendDrug_knowledge() {
		try {
			wnwd.waitElementByXpathAndClick("治疗方案按钮", WnOutpatientXpath.outpatientRecommendTreatBtn,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("诊断推荐按钮", "//div[contains(@class,'diagnostic-title')]",
					Framework.defaultTimeoutMax);
			WebElement quoteMedicineBtn = wnwd.waitElementByXpath("引用药品按钮",
					"//div[@class='medication']//tr[@class='el-table__row']//td[.='开立']//div[contains(@class,'opening') and .='开立']",
					Framework.defaultTimeoutMax);
			wnwd.assertTrue("卫宁知识体系 推荐药品为空", quoteMedicineBtn != null);
			wnwd.wnClickElement(quoteMedicineBtn, "引用药品按钮");
			editAndCommitOrder(null);
			// 输入医保审批
			own_expense(null);
			wnwd.checkElementByXpath("检查开立结果", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "开立推荐药品失败:" + e.getMessage());
		}

	}

	// 检查知识域推荐查体
	public void checkPhysicalSign(String physicalSignName) {
		try {
			wnwd.checkElementByXpath("知识域推荐查体:" + physicalSignName,
					"//div[contains(@class,'diagnosis-detail-item physical-wrap')]//div[contains(@class,'nesting-physical-check')]//span[.='?1']"
							.replace("?1", physicalSignName),
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "知识域没有推荐查体" + physicalSignName);
		}
	}

	// 检查知识域推荐检验
	public void checkRecommendLab(String recommendLabName) {
		try {
			wnwd.checkElementByXpath("知识域推荐检验:" + recommendLabName,
					"//div[contains(@class,'diagnosis-detail-item checkout-wrap')]//div[.='?1']".replace("?1",
							recommendLabName),
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "知识域没有推荐检验" + recommendLabName);
		}
	}

	// 检查知识域推荐检查
	public void checkRecommendExam(String recommendExamName) {
		try {
			wnwd.checkElementByXpath("知识域推荐检查:" + recommendExamName,
					"//div[contains(@class,'diagnosis-detail-item inspect-wrap')]//div[contains(text(),'?1')]"
							.replace("?1", recommendExamName),
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "知识域没有推荐检查" + recommendExamName);
		}
	}

	// 编辑所有知识域推荐查体
	public void editAllPhysicalSign() {
		List<WebElement> physicalSignList = wnwd.waitElementListByXpath(
				"//div[contains(@class,'diagnosis-detail-item physical-wrap')]//div[contains(@class,'nesting-physical-check')]",
				Framework.defaultTimeoutMax);
		if (physicalSignList.size() == 0) {
			logger.log(2, "知识域没有推荐查体");
		}
		Integer i = 0;
		for (WebElement targetSign : physicalSignList) {
			i++;
			if (i > 5) {
				break;
			}
			WebElement signNameElement = targetSign.findElement(By.xpath(".//span[@class='physical-name']"));
			String signName = signNameElement.getText();
			WebElement selectIcon = null;

			try {
				selectIcon = targetSign.findElement(By.xpath(".//i"));
			} catch (Throwable e) {
				logger.log(3, signName + "不是选择类型查体");
			}

			// 选择型查体
			if (selectIcon != null) {
				wnwd.wnClickElement(selectIcon, "展开查体下拉框:" + signName);
				wnwd.waitElementByXpathAndClick("下拉框第二项", "//ul[contains(@class,'physical-check-menu')]//li[2]",
						Framework.defaultTimeoutMax);
				continue;
			}
			// 输入型查体
			try {
				WebElement input = targetSign.findElement(By.xpath(".//input"));
				wnwd.wnEnterText(input, "37", "查体输入37:" + signName);
				wnwd.wnClickElement(signNameElement, "查体名称");
			} catch (Throwable e) {
				logger.assertFalse(true, "查体输入框编辑失败:" + signName);
			}
		}
	}

	//引用病历模板
	public void quoteEmrTemplate(String templateName) {
		wnwd.waitElementByXpathAndClick("病历模板按钮",
				"//div[@id='medical_record']//div[contains(@class,'emr__editor__menu--list')]//span[.='病历模板']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("病历模板搜索框",
				"//div[contains(@class,'menu-book__template')]//div[contains(@class,'menu-book__template__menu__search')]//input",
				templateName, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("模板搜索结果",
				"//div[contains(@class,'menu-book__template__menu__tree__list')]//span[contains(@class,'list-name') and .='"
						+ templateName + "']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("引用病历模板按钮", "//div[contains(@class,'menu-book__template')]//button[.='引用']",
				Framework.defaultTimeoutMax);
		SdkTools.sleep(Framework.defaultTimeoutMin);
	}

	// 检查病历种存在指定段落和指定片段
	public void checkEmrParagraph(String paragraph) {
		wnwd.checkElementByXpath("段落:" + paragraph, WnOutpatientXpath.emrParagraph.replace("?1", paragraph),
				Framework.defaultTimeoutMax);
	}

	// 检查病历种存在指定段落和指定片段
	public void checkEmrFragment(String paragraph, String fragment) {
		wnwd.checkElementByXpath("段落:" + paragraph + "-片段:" + fragment,
				WnOutpatientXpath.emrFragment.replace("?1", paragraph).replace("?2", fragment),
				Framework.defaultTimeoutMax);
	}

	//等待病历指定段落消失
	public void waitEmrFragmentNotExist(String paragraph, String fragment) {
		wnwd.waitNotExistByXpath("等待段落:" + paragraph + "-片段:" + fragment + " 消失",
				WnOutpatientXpath.emrFragment.replace("?1", paragraph).replace("?2", fragment),
				Framework.defaultTimeoutMax);
	}

	// 修改病历种存在指定段落和指定片段的内容
	public void updateEmrFragment(String paragraph, String fragment, String updateString) {
		WebElement element = wnwd.checkElementByXpath("段落:" + paragraph + "-片段:" + fragment,
				WnOutpatientXpath.emrFragment.replace("?1", paragraph).replace("?2", fragment),
				Framework.defaultTimeoutMax);
		wnwd.addElementTextContent(element, updateString);
		if (Data.getScreenShot) {
			wnwd.getScreenShot("修改病历完成");
		}
	}

	// 引用历史诊断+处置
	public void quoteHistoryDiagnosisAndDisposal(List<String> diagnosisList, List<String> disposalList) {
		wnwd.waitElementByXpathAndClick("进入历史处置页面", WnOutpatientXpath.outpatientHistoricalDisposal,
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("历史处置内容:", "//li[contains(@class,'flex__alignItems--center historyItem other')]",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("勾选所有处置:", "//span[@class='detail-checkbox']//span[@class='el-checkbox__inner']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击引用处置加诊断:" + diagnosisList.get(0), "//p[@class='copyAndQuote dealBtn_quote w110']//span[contains(@class,'quoteBtnImg')]",
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("检查引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
				Framework.defaultTimeoutMax);
		own_expense();
		for (int i = 0; i < diagnosisList.size(); i++) {
			wnwd.checkElementByXpath("知识域诊断:" + diagnosisList.get(i),
					"//div[contains(@class,'diagnostic-item-name') and contains(.,'" + diagnosisList.get(i) + "')]",
					Framework.defaultTimeoutMax);
		}
		for (int i = 0; i < disposalList.size(); i++) {
			wnwd.checkElementByXpath("开立结果:" + disposalList.get(i),
					"//div[contains(@class,'previewList_wrap')]//li[contains(.,'" + disposalList.get(i)
							+ "')]/..//li[.='开立']",
					Framework.defaultTimeoutMax);
		}
	}

	// 进入历史处置页面
	public void enterHistory() {
		try {
			wnwd.waitElementByXpathAndClick("进入历史处置页面", WnOutpatientXpath.outpatientHistoricalDisposal,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("检查本科室勾选框", WnOutpatientXpath.outpatientHistoricalSelfSubject,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("6个月按钮", WnOutpatientXpath.quickDateBtnSixMonth, Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载框消失", "//div[@class='loading_loc']", Framework.defaultTimeoutMax);
			wnwd.getScreenShot("进入历史处置页面成功");
		} catch (Throwable e) {
			logger.log(1, "errMsg:入历史处置页面失败: " + e.getMessage());
			wnwd.assertFalse("入历史处置页面失败:" + e.getMessage(), true);
		}

	}

	// 根据日期选则历史处置
	public void selectHistoryByDate(String date) {
		try {
			String sortDate = date.substring(5, 7) + "." + date.substring(8, 10);
			wnwd.waitElementByXpathAndClick("进入历史处置详情",
					"//ul[contains(@class,'history-list')]//li[contains(@class,'flex__alignItems--center')]//p[contains(@class,'month') and .='"
							+ sortDate + "']",
					Framework.defaultTimeoutMax);
			wnwd.sleep(Framework.defaultTimeoutMin);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientCommonCircle, Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("检查进入历史处置详情", WnOutpatientXpath.outpatientHistoricalOrderView,
					Framework.defaultTimeoutMax);
			wnwd.getScreenShot("根据日期进入历史处置详情成功");
		} catch (Throwable e) {
			logger.log(1, "errMsg:根据日期选则历史处置失败 : " + e.getMessage());
			wnwd.assertFalse("指定日期无历史处置:" + date + "(" + e.getMessage() + ")", true);
		}

	}

	// 历史处置页面输入查询条件
	public void searchHistory(String startDate, String endDate, boolean selfSubject) {
		// endDate 转为明天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Date date = null;
		Date tomorrow = null;
		try {
			date = format.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		tomorrow = c.getTime();
		endDate = format.format(tomorrow);

		WebElement startInput = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientHistoricalDateStart,
				Framework.defaultTimeoutMax);
		WebElement endInput = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientHistoricalDateEnd,
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(startInput, "日期选择框");
		wnwd.waitElementByXpathAndClick("清空已输入日期按钮", WnOutpatientXpath.outpatientHistoricalDateClearIcon,
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(startInput, startDate, "开始日期输入框" + startDate);
		wnwd.wnEnterText(endInput, endDate, "结束日期输入框" + endDate);
		endInput.sendKeys(Keys.ENTER);
		if (selfSubject != true) {
			wnwd.waitElementByXpathAndClick("取消勾选本科室", WnOutpatientXpath.outpatientHistoricalSelfSubject,
					Framework.defaultTimeoutMax);
		}
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientCommonCircle, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("历史处置搜索结果", WnOutpatientXpath.outpatientHistoricalList, Framework.defaultTimeoutMax);
	}

	// 获取历史处置明细
	public List<String> getHistoryOrderNames() {
		List<String> orderNames = new ArrayList<>();
		try {
			List<WebElement> orderList = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientHistoricalOrderList,
					Framework.defaultTimeoutMax);
			for (WebElement order : orderList) {
				orderNames.add(order.getText());

			}
		} catch (Throwable e) {
			logger.log(1, "errMsg:获取历史医嘱列表失败: " + e.getMessage());
			throw new Error("获取历史医嘱列表失败:" + e.getMessage());
		}
		for (String orderName : orderNames) {
			logger.log(1, "找到历史处置:" + orderName);
			wnwd.assertFalse("历史处置包含null[" + orderName + "]", orderName.contains("null"));
		}

		if (orderNames.size() == 0) {
			throw new Error("历史处置内容为空");
		}
		logger.log(1, "历史处置条数:" + orderNames.size());
		return orderNames;
	}

	// 检查历史处置是否有异常
	public void checkHistoryOrders() {
		List<String> errOrderNames = new ArrayList<>();
		try {
			List<WebElement> orderList = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientHistoricalOrderCheck,
					Framework.defaultTimeoutMax);
			for (WebElement order : orderList) {
				if (order.findElements(By.xpath(".//i[contains(@class,'fail')]")).size() > 0) {
					errOrderNames.add(order.getText().replace("\n", "-").replace("\r", "-"));
				}
			}
		} catch (Throwable e) {
			throw new Error("检查历史处置列表失败:" + e.getMessage());
		}
		if (errOrderNames.size() > 0) {
			wnwd.getScreenShot("历史处置异常");
			throw new Error("历史处置异常:" + errOrderNames);
		}
	}

	// 引用处置加诊断
	public void quoteAllHistory() {
//    	String alertText = null;
		List<WebElement> unSignOrders = null;
		List<String> errOrders = new ArrayList<>();
		try {
			WebElement el = wnwd.waitElementByXpath("//ul[contains(@class,'history-detail')]//span[contains(@class,'is-indeterminate')]", Framework.defaultTimeoutMin);
			if(el!=null) {
				el.click();
			}
			wnwd.waitElementByXpathAndClick("引用处置加诊断按钮", WnOutpatientXpath.outpatienthistoricalQuoteOrder,
					Framework.defaultTimeoutMax);
			addDiagnoseIfNeed();
			own_expense(null);
			unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMid);
			if (unSignOrders == null || unSignOrders.size() == 0) {
				throw new Error("没有找到未签署医嘱!");
			}
			for (WebElement order : unSignOrders) {
				if (order.findElements(By.xpath(".//i[contains(@class,'fail') or contains(@class,'warning')]"))
						.size() > 0) {
					errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
				}
			}
			if (errOrders.size() > 0) {
				wnwd.getScreenShot("医嘱列表异常");
				throw new Error("发现异常医嘱:" + errOrders);
			}

		} catch (Throwable e) {
			logger.log(1, "errMsg:引用处置加诊断失败: " + e.getMessage());
			wnwd.assertFalse("引用失败:" + e.getMessage(), true);
		}
	}

	// 搜索并选择医嘱
	// name:输入的名称
	// searchFlag 搜索结果中必须包含 searchFlag 的每一个元素
	public void searchOrderForCommon(String name) {
		wnwd.sleep(1000);
		WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
				WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
		wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
		wnwd.checkElementByXpath("医嘱搜索结果", WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
		wnwd.sleep(Framework.defaultTimeoutMin);
		WebElement ele = wnwd.waitElementByXpath("//li[@class='absolute collectIcon']//i", Framework.defaultTimeoutMin);
		try {
			if (ele.getAttribute("class").equals("win-icon-collected")) {
				logger.log(1, "该药品当前已收藏");
				ele.click();
				wnwd.checkElementByXpath("取消收藏提示",
						"(//div[@class='el-message el-message--success is-closable']//p[.='取消收藏成功'])",
						Framework.defaultTimeoutMid);
			} else if (ele.getAttribute("class").equals("win-icon-uncollected")) {
				logger.log(1, "该药品当前未收藏");
				ele.click();
				wnwd.checkElementByXpath("收藏提示",
						"(//div[@class='el-message el-message--success is-closable']//p[.='收藏成功'])",
						Framework.defaultTimeoutMid);
			}
		} catch (Throwable e) {
			throw new Error("没找到收藏相关Xpath:" + e.getMessage());
		}

	}

	//搜索医嘱项
	public void searchOrder(String name) {
		searchOrder(name, null);
	}

	public void searchOrder(List<String> searchFlag) {
		searchOrder(searchFlag.get(0), searchFlag);
	}

	public void searchOrder(String name, List<String> searchFlag) {
		
//		if(ifSearchOrdEmpty(Data.host, name)) {
//			logger.log(2, "60通过接口未搜索到医嘱:" + name);
//			logger.assertFalse(true, "搜索医嘱失败", "未找到此医嘱项:" + name); 
//		}
		
		wnwd.waitElementByXpathAndClick("全部医嘱",
				WnOutpatientXpath.outpatientDisposalFactoryTemplateButton.replace("模板", "全部"),
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
				WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
		wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
		wnwd.checkElementByXpath("医嘱搜索结果", WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
		WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);

		// 查找所有搜索结果
		List<WebElement> searchResults = resultBox
				.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
//        logger.log(2,"搜索结果数量:"+searchResults.size());
		// 搜索结果是空 直接报错
		if (searchResults == null || searchResults.size() == 0) {
			logger.assertFalse(true, "60无搜索结果", "未找到此医嘱项:" + name);
		}
		// searchFlag是空 直接选择第一个搜索结果
		if (searchFlag == null || searchFlag.size() == 0) {
			wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
			return;
		}
		// searchFlag不是空 选择第一个包含List中所有元素的结果
		WebElement resultLine = null;
		for (WebElement line : searchResults) {
//	    	logger.log(2,"line:"+line.getText());
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
			if (searchFlag.size() == 1 && line.getText().trim().equals(searchFlag.get(0).trim())) {
				logger.log(1, "完全一致");
				break;
			}
		}
		if (resultLine != null) {
			// 无库存时报错
//	    	logger.log(1, resultLine.getText());
			logger.assertFalse(!resultLine.getAttribute("class").contains("blackTxt"), "60无库存");
			wnwd.wnClickElement(resultLine, "医嘱项:" + resultLine.getText());
		} else {
			// 搜索不到结果时报错
			logger.assertFalse(true, "60无搜索结果", "未找到此医嘱项:" + name + "\n结果包含:" + searchFlag);
		}
	}

	public void searchOrderDD(String name, List<String> searchFlag) {
		wnwd.sleep(1000);
		WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
				WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
		wnwd.wnDoubleClickElementByMouse(inputSearchOrderElement, "医嘱搜索框");
		wnwd.checkElementByXpath("医嘱搜索结果", WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
		WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);

		// 查找所有搜索结果
		List<WebElement> searchResults = resultBox
				.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
//        logger.log(2,"搜索结果数量:"+searchResults.size());
		// 搜索结果是空 直接报错
		if (searchResults == null || searchResults.size() == 0) {
			logger.assertFalse(true, "60无搜索结果", "未找到此医嘱项:" + name);
		}
		// searchFlag是空 直接选择第一个搜索结果
		if (searchFlag == null || searchFlag.size() == 0) {
			wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
			return;
		}
		// searchFlag不是空 选择第一个包含List中所有元素的结果
		WebElement resultLine = null;
		for (WebElement line : searchResults) {
//	    	logger.log(2,"line:"+line.getText());
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
			if (searchFlag.size() == 1 && line.getText().trim().equals(searchFlag.get(0).trim())) {
				logger.log(1, "完全一致");
				break;
			}
		}
		if (resultLine != null) {
			// 无库存时报错
//	    	logger.log(1, resultLine.getText());
			logger.assertFalse(!resultLine.getAttribute("class").contains("blackTxt"), "60无库存");
			wnwd.wnClickElement(resultLine, "医嘱项:" + resultLine.getText());
		} else {
			// 搜索不到结果时报错
			logger.assertFalse(true, "60无搜索结果", "未找到此医嘱项:" + name + "\n结果包含:" + searchFlag);
		}
	}

	//检查医嘱加工厂是否打开
	public WebElement checkDisposalFactory() {
		return wnwd.checkElementByXpath("医嘱加工厂", WnOutpatientXpath.outpatientDisposalFactory,
				Framework.defaultTimeoutMax);
	}

	// 判断当前在哪个药品加工厂
	// 返回String: herb 草药; drug 西药; lab 检验; exam 检查; treatment 治疗; pathology 病理;
	// unknow 未知;
	public String getDetailType() {
		String detailType = "";
		try {
			// 等待开立详情界面
			WebElement disposalFactory = checkDisposalFactory();
			if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryHerbFlag))
					.size() != 0) {
				detailType = "herb";
			} else if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryDrugFlag))
					.size() != 0) {
				detailType = "drug";
			} else if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryLabFlag))
					.size() != 0) {
				detailType = "lab";
			} else if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryExamFlag))
					.size() != 0) {
				detailType = "exam";
			} else if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryTreatFlag))
					.size() != 0) {
				detailType = "treatment";
			} else if (disposalFactory.findElements(By.xpath(WnOutpatientXpath.outpatientDisposalFactoryPathologyFlag))
					.size() != 0) {
				detailType = "pathology";
			} else {
				throw new Error("未知的加工厂类型");
			}
			wnwd.waitNotExistByXpath("等待加工厂加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
			// 精麻毒点击确认信息
			spacialMedicineCommitIfNeed();
			// 输入用药目的
			purposeCommitIfNeed();
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

	// 开立任意医嘱
	public void prescribeOrder(String name) {
		if (name.toLowerCase().equals("none")) {
			return;
		}
		if (name.contains(",")) {
			List<String> orderList = SdkTools.strToList(name, ",");
			for (String order : orderList) {
				prescribeOrder(order, null, null);
			}
		} else {
			prescribeOrder(name, null, null);
		}
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
		addDiagnoseIfNeed();
		childrenInfoIfNeed();
/*		if(pishiIfNeed()){
			editAndCommitOrder(orderDetail);
		}*/
		pishiIfNeed();
		editAndCommitOrder(orderDetail);
		own_expense();
	}

	public void checkOrderForSkin() {
		try {
			List<WebElement> unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMax);
			if (unSignOrders == null || unSignOrders.size() == 0) {
				throw new Error("没有找到未签署医嘱!");
			}
			List<String> errOrders = new ArrayList<>();
			for (WebElement order : unSignOrders) {
				if (order.findElements(By.xpath("//div[@class='doint_bg preview-col' and contains(.,'开立')]"))
						.size() > 0) {
					errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
				}
			}
			if (errOrders.size() > 0) {
				wnwd.getScreenShot("医嘱列表异常");
				throw new Error("发现异常医嘱:" + errOrders);
			}
			wnwd.waitElementByXpathAndClick("医嘱编辑按钮",
					WnOutpatientXpath.outpatientUnsignedOrder + "//button[.='编辑']", Framework.defaultTimeoutMax);
			purposeCommitIfNeed();
			editAndCommitOrder(null);
		} catch (Throwable e) {
			logger.assertFalse(true, "签署前检查医嘱列表不通过:" + e.getMessage());
		}
	}
	
	// 修改加工厂并确定
	public void updateOrder(String orderName) {
		wnwd.waitElementByXpathAndClick("医嘱" + orderName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + orderName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		try {
			String detailType = getDetailType();
			if (detailType == "herb") {
				editHerb(new ArrayList<>(Arrays.asList("3")));
			} else if (detailType == "drug") {
				editDrug(new ArrayList<>(Arrays.asList("3")));
			} else if (detailType == "exam") {
				editExam(new ArrayList<>(Arrays.asList("UPDATE")));
			} else if (detailType == "lab") {
				editLab(new ArrayList<>(Arrays.asList("UPDATE")));
			} else if (detailType == "treatment") {
				editTreat(new ArrayList<>(Arrays.asList("3")));
			} else if (detailType == "pathology") {
				editPathology(new ArrayList<>(Arrays.asList("修改临床摘要", "修改检查目的")));
			} else {
				wnwd.assertTrue("当前未处于开立详情界面", false);
			}
			wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", WnOutpatientXpath.outpatientDisposalFactory,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
					"contains(.,'开立') and contains(.,'" + orderName + "')"), Framework.defaultTimeoutMax);
			own_expense();
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑医嘱失败", e.getMessage());
		}
	}

	// 编辑加工厂并点击确定
	public void editAndCommitOrder(List<String> orderDetail) {
		try {
			String detailType = getDetailType();
			// 根据当前医嘱类型 进行不通操作
			if (detailType == "herb") {
				editHerb(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
			} else if (detailType == "drug") {
				editDrug(orderDetail == null ? new ArrayList<>(Arrays.asList("1")) : orderDetail);
			} else if (detailType == "exam") {
				editExam(orderDetail);
			} else if (detailType == "lab") {
				editLab(orderDetail);
			} else if (detailType == "treatment") {
				editTreat(orderDetail);
			} else if (detailType == "pathology") {
				editPathology(orderDetail);
			} else {
				wnwd.assertTrue("当前未处于开立详情界面", false);
			}
			wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", WnOutpatientXpath.outpatientDisposalFactory,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "开立医嘱失败", e.getMessage());
		}
	}

	// 编辑西药医嘱详情
	public void editDrug(List<String> orderDetail) {
		logger.log(1, "editDrug:" + orderDetail);
		WebElement purposeRadio = wnwd
				.getElementByXpath(WnOutpatientXpath.outpatientDisposalFactoryDrugPurposeRadioPrevent);
		if (purposeRadio != null) {
			wnwd.wnClickElement(purposeRadio, "选择用药目的-预防");
		}
		wnwd.waitElementByXpathAndInput("西药数量输入框", WnOutpatientXpath.outpatientDisposalFactoryDrugNumInput,
				orderDetail.get(0), Framework.defaultTimeoutMax);
		WebElement ele = wnwd.waitElementByXpath("药品名称", WnOutpatientXpath.outpatientDisposalFactoryDrugName,
				Framework.defaultTimeoutMax);
		wnwd.wnClickElementByMouse(ele, "药品名称");
	}

	// 编辑中草药医嘱详情
	public void editHerb(List<String> orderDetail) {
		logger.log(1, "editHerb:" + orderDetail);
		// 中草药编辑
		WebElement herbName = wnwd
				.getElementByXpath("//div[@class='disposal__factory']//li//span[@class='herbal-name']");
		if (herbName != null) {
			wnwd.wnClickElement(herbName, "药品名称");
		}
		wnwd.waitElementByXpathAndInput("中草药剂量输入框", WnOutpatientXpath.outpatientDisposalFactoryHerbNumInput,
				orderDetail.get(0), Framework.defaultTimeoutMax);
		// 代煎方式设置处理
		if (!WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue.equals("")) {
			wnwd.waitElementByXpathAndClick("代煎方式下拉框", WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodList,
					Framework.defaultTimeoutMax);
//			WebElement element=wnwd.moveToElementByXpath("指定的代煎方式",WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue,Frmcons.defaultTimeoutMax);
//			wnwd.wnClickElementByMouse(element,"点击指定的代煎方式");
			wnwd.waitElementByXpathAndClick("点击需要的代煎方式",
					WnOutpatientXpath.outpatientDisposalFactoryHerbDecocteMethodValue, Framework.defaultTimeoutMax);

		}
		wnwd.waitElementByXpathAndClick("中草药保存按钮", WnOutpatientXpath.outpatientDisposalFactoryHerbNumSave,
				Framework.defaultTimeoutMax);
	}

	// 编辑检验医嘱详情
	public void editLab(List<String> orderDetail) {
		logger.log(1, "editLab:" + orderDetail);
		if (orderDetail != null) {
			if (orderDetail.get(0).toUpperCase().contentEquals("UPDATE")) {
				wnwd.waitElementByXpathAndInput("注意事项输入框", WnOutpatientXpath.outpatientDisposalFactoryLabTipsInput,
						"修改注意事项", Framework.defaultTimeoutMax);
				return;
			}
			List<String> itemIndexes = orderDetail;
			// 找到所有复选框
			List<WebElement> examItems = wnwd.waitElementListByXpath(
					WnOutpatientXpath.outpatientDisposalFactoryCheckBoxes, Framework.defaultTimeoutMax);
			// 全部取消勾选
			for (WebElement examItem : examItems) {
				if (examItem.getAttribute("class").contains("is-checked")) {
					wnwd.wnClickElement(examItem, "取消勾选:" + examItem.getText());
				}
			}
			if (orderDetail.get(0).toUpperCase().contentEquals("ALL")) {
				// 勾选全部
				for (WebElement examItem : examItems) {
					wnwd.wnClickElement(examItem, "勾选:" + examItem.getText());
				}
			} else {
				// 勾选指定项目
				for (String itemIndex : itemIndexes) {
					int itemIndexInt = Integer.parseInt(itemIndex);
					wnwd.wnClickElement(examItems.get(itemIndexInt), "勾选:" + examItems.get(itemIndexInt).getText());
				}
			}

		}
	}

	// 编辑检查医嘱详情
	public void editExam(List<String> orderDetail) {
		logger.log(1, "editExam:" + orderDetail);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("摘要输入框", WnOutpatientXpath.outpatientDisposalFactoryExamSummaryInput, "test",
				Framework.defaultTimeoutMax);
		WebElement tizhengElement = wnwd
				.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'体征')]//input");
		if (tizhengElement != null) {
			wnwd.wnEnterText(tizhengElement, "体征", "体征输入框");
		}
		if (orderDetail != null) {
			if (orderDetail.get(0).toUpperCase().contentEquals("UPDATE")) {
				wnwd.waitElementByXpathAndInput("摘要输入框", WnOutpatientXpath.outpatientDisposalFactoryExamSummaryInput,
						"修改临床摘要", Framework.defaultTimeoutMax);
				return;
			}
			List<String> itemIndexes = orderDetail;
			// 找到所有检查项目
			List<WebElement> examItems = wnwd.waitElementListByXpath(
					WnOutpatientXpath.outpatientDisposalFactoryCheckBoxes, Framework.defaultTimeoutMax);
			// 全部取消勾选
			for (WebElement examItem : examItems) {
				if (examItem.getAttribute("class").contains("is-checked")) {
					wnwd.wnClickElement(examItem, "取消勾选:" + examItem.getText());
				}
			}
			if (orderDetail.get(0).toUpperCase().contentEquals("ALL")) {
				// 勾选全部
				for (WebElement examItem : examItems) {
					wnwd.wnClickElement(examItem, "勾选:" + examItem.getText());
				}
			} else {
				// 勾选指定项目
				for (String itemIndex : itemIndexes) {
					int itemIndexInt = Integer.parseInt(itemIndex);
					wnwd.wnClickElement(examItems.get(itemIndexInt), "勾选:" + examItems.get(itemIndexInt).getText());
					wnwd.waitNotExistByXpath("等待加载框消失", "//div[@class='el-loading-mask']", Framework.defaultTimeoutMid);
				}
			}
			WebElement bloodPressureInputBox = wnwd.waitElementByXpath("血压输入框", "//label[.='血压']/parent::*//input",
					Framework.defaultTimeoutMin);
			if (bloodPressureInputBox != null) {
				wnwd.waitElementByXpathAndInput("血压输入框", "//label[.='血压']/parent::*//input", "80",
						Framework.defaultTimeoutMin);
			}

			WebElement heartRateInputBox = wnwd.waitElementByXpath("心律输入框", "//label[.='心律']/parent::*//input",
					Framework.defaultTimeoutMin);
			if (heartRateInputBox != null) {
				wnwd.waitElementByXpathAndInput("心律输入框", "//label[.='心律']/parent::*//input", "80",
						Framework.defaultTimeoutMin);
			}

			WebElement pulseInputBox = wnwd.waitElementByXpath("脉搏输入框", "//label[.='脉搏']/parent::*//input",
					Framework.defaultTimeoutMin);
			if (pulseInputBox != null) {
				wnwd.waitElementByXpathAndInput("脉搏输入框", "//label[.='脉搏']/parent::*//input", "80",
						Framework.defaultTimeoutMin);
			}
		}
	}

	// 编辑治疗医嘱详情
	public void editTreat(List<String> orderDetail) {
		logger.log(1, "editTreat:" + orderDetail);
		if (orderDetail != null) {
			wnwd.waitElementByXpathAndInput("治疗数量输入框", WnOutpatientXpath.outpatientDisposalFactoryTreatNumInput,
					orderDetail.get(0), Framework.defaultTimeoutMax);
		}
	}

	// 编辑病理医嘱详情
	public void editPathology(List<String> orderDetail) {
		try {
			if (orderDetail == null) {
				orderDetail = new ArrayList<>(Arrays.asList("测试", "测试"));
			}
			logger.log(1, "editPathology:" + orderDetail);
			wnwd.waitElementByXpathAndInput("输入临床摘要", "//label[contains(.,'临床摘要')]/..//textarea", orderDetail.get(0),
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("输入检查目的", "//label[contains(.,'检查目的')]/..//textarea", orderDetail.get(1),
					Framework.defaultTimeoutMax);
			WebElement position = wnwd.getElementByXpath("//div[@class='disposal__factory']//tr/td[2]//input");
			if (position == null) {
				wnwd.waitElementByXpathAndClick("点击增加标本", "//p[@class='spec-add cursor_point']",
						Framework.defaultTimeoutMax);
				position = wnwd.checkElementByXpath("选择部位", "//div[@class='disposal__factory']//tr/td[2]//input",
						Framework.defaultTimeoutMax);
			}
			wnwd.wnClickElement(position, "部位选择框");
			wnwd.waitElementByXpathAndClick("选择第一个部位",
					"//ul[@class='el-scrollbar__view el-select-dropdown__list']/li[1]//span",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("选择标本", "//div[@class='disposal__factory']//tr/td[3]//input",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("选择第一个标本",
					"//ul[@class='el-scrollbar__view el-select-dropdown__list']/li[1]//span",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			throw new Error("编辑病理详情失败:" + e.getMessage());
		}
	}

	// 医嘱列表中的检查 如果存在没有临床摘要的情况 自动添加
	public void addClinicalSummaryIfNeed() {
		try {
			wnwd.checkElementByXpath("医嘱签署按钮(可点击状态)", WnOutpatientXpath.outpatientOrderSignButton,
					Framework.defaultTimeoutMax);
			while (true) {
				WebElement warnIcon = wnwd.getElementByXpath(
						WnOutpatientXpath.outpatientUnsignedOrder + "//i[contains(@class,'warning')]");
				if (warnIcon == null) {
					break;
				}
				wnwd.moveToElement(warnIcon, "警告图标");
				WebElement warnText = wnwd.checkElementByXpath("警告内容", "//div[@class='warnTxt']",
						Framework.defaultTimeoutMax);
				logger.assertFalse(!warnText.getText().contains("临床摘要不可为空") && !warnText.getText().contains("用药目的不可为空"),
						"异常医嘱:" + warnText.getText());
				if (warnText.getText().contains("是否需要皮试")) {
					wnwd.waitElementByXpathAndClick("皮试编辑按钮",
							WnOutpatientXpath.outpatientUnsignedOrder + "//button[.='皮试']", Framework.defaultTimeoutMid);
				}
				wnwd.waitElementByXpathAndClick("医嘱编辑按钮",
						WnOutpatientXpath.outpatientUnsignedOrder + "//button[.='编辑']", Framework.defaultTimeoutMax);
				editAndCommitOrder(null);
				own_expense();
			}
		} catch (Throwable e) {
			logger.assertFalse(true, "异常医嘱编辑失败:" + e.getMessage());
		}
	}

	// 签署医嘱
	public SignoffDetail signOff(int sleepTime) {
		// 传出签署信息
		SignoffDetail Signoffinfo = new SignoffDetail();
		try {
			addClinicalSummaryIfNeed();
			checkOrderState();
			wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
					Framework.defaultTimeoutMax);
			WebElement patientTypeDialog = wnwd.waitElementByXpath("患者类型选择框",
					"//div[@role='dialog' and contains(.,'请选择患者类型')]", 2000);
			if (patientTypeDialog != null) {
				wnwd.waitElementByXpathAndClick("第一个类型",
						"//div[@role='dialog' and contains(.,'请选择患者类型')]//label[@role='radio']",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定", "//div[@role='dialog' and contains(.,'请选择患者类型')]//button[.='确定']",
						Framework.defaultTimeoutMax);
			}

			long endTime = System.currentTimeMillis() + Framework.defaultTimeoutMax;
			WebElement SignButtonDisabled = null;
			while (System.currentTimeMillis() < endTime) {
				SdkTools.sleep(500);
				SignButtonDisabled = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientOrderSignButtonDisabled,
						Framework.defaultTimeoutMin);
				if (SignButtonDisabled != null) {
					break;
				}
				WebElement LinkageCommitButton = wnwd
						.waitElementByXpath(WnOutpatientXpath.outpatientLinkageCommitButton, Framework.defaultTimeoutMin);
				// 取单个联动项的信息
				List<WebElement> LinkAgeUIList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientLinkageDiv);
				for (WebElement el : LinkAgeUIList) {
					Map<String, String> LinkAgeDetail = new HashMap<String, String>();
					WebElement welName = null;
					WebElement welCount = null;
					welName = el.findElement(By.xpath(".//div[@class='col']"));
					welCount = el.findElement(By.xpath(".//div[@class='col last-child']//input"));
					String LinkAgeName = "";
					LinkAgeName = welName.getText();
					String LinkAgeEdited = "";
					String LinkAgeCount = "";
					LinkAgeCount = welCount.getAttribute("aria-valuenow");
					LinkAgeEdited = welCount.getAttribute("disabled");
					if (LinkAgeEdited == null)
						LinkAgeEdited = "true";
					else
						LinkAgeEdited = "false";

					LinkAgeDetail.put("LinkAgeName", LinkAgeName);
					LinkAgeDetail.put("LinkAgeCount", LinkAgeCount);
					LinkAgeDetail.put("LinkAgeEdited", LinkAgeEdited);
					Signoffinfo.LinkAgeInfo.add(LinkAgeDetail);
				}

				if (LinkageCommitButton != null) {
					wnwd.wnClickElement(LinkageCommitButton, "联动确认按钮");
				}
				own_expense();
			}
			// 防止debug超时，没有重新获取控件导致抛出异常阻碍debug
			WebElement dosageControl = wnwd.waitElementByXpath("//div[@id='tab-dosageControl']",
					Framework.defaultTimeoutMin);
			if (dosageControl == null) {
				SignButtonDisabled = wnwd.getElementByXpath(WnOutpatientXpath.outpatientOrderSignButtonDisabled);
				if (SignButtonDisabled == null) {
					throw new Error("请查看截图");
				}
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
		return Signoffinfo;
	}

	// 签署并打印医嘱
	public void signOffAndPrint(int sleepTime) {
		try {
			addClinicalSummaryIfNeed();
			checkOrderState();
			wnwd.waitElementByXpathAndClick("医嘱签署并打印按钮", WnOutpatientXpath.outpatientOrderSignAndPrintButton,
					Framework.defaultTimeoutMax);
			bindingDiagnosis();
			WebElement patientTypeDialog = wnwd.waitElementByXpath("患者类型选择框",
					"//div[@role='dialog' and contains(.,'请选择患者类型')]", 2000);
			if (patientTypeDialog != null) {
				wnwd.waitElementByXpathAndClick("第一个类型",
						"//div[@role='dialog' and contains(.,'请选择患者类型')]//label[@role='radio']",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定", "//div[@role='dialog' and contains(.,'请选择患者类型')]//button[.='确定']",
						Framework.defaultTimeoutMax);
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

	// 引用医嘱模板
	/**
	 * 1.进入医嘱模板界面 2.搜索模板名称 3.引用搜索结果第一个模板 4.检查医嘱列表中有未签署医嘱
	 */
	public void quoteTemplate(String name) {
		wnwd.waitElementByXpathAndClick("进入模板界面", WnOutpatientXpath.outpatientOrderTemplateButton,
				Framework.defaultTimeoutMax);
		WebElement OrderTemplateSearchInput = wnwd.waitElementByXpathAndInput("输入模板名称",
				WnOutpatientXpath.outpatientOrderTemplateSearchInput, name, Framework.defaultTimeoutMax);
		OrderTemplateSearchInput.sendKeys(Keys.ENTER);
		wnwd.waitElementByXpathAndClick("点击引用模板", WnOutpatientXpath.outpatientOrderTemplateFirstQuoteBtn,
				Framework.defaultTimeoutMax);
		try {
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.log(1, "找不到成功提示,判断是否有编辑框");
			wnwd.checkElementByXpath("等待诊断编辑框", WnOutpatientXpath.outpatientDiagnosisEditBox, 3000);
			wnwd.waitElementByXpathAndClick("选择第一个诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxFirstDiag,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("确定诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);
		}
		wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	/**
	 * @QuoteWay 引用方式，为0时从开立界面，输入模板名称，然后引用
	 * @TemplateType 模板类型,仅限个人与科室
	 * @TemplateName 模板名称
	 **/
	public void quoteOrderTemplate(Integer QuoteWay, String TemplateType, String TemplateName) {

		// 从开立界面
		if (QuoteWay == 0) {
			wnwd.waitElementByXpathAndClick("进入模板界面", WnOutpatientXpath.outpatientDisposalFactoryTemplateButton,
					Framework.defaultTimeoutMax);
			WebElement OrderTemplateSearchInput = wnwd.waitElementByXpathAndInput("输入模板名称",
					WnOutpatientXpath.outpatientSearchOrderInput, TemplateName, Framework.defaultTimeoutMax);
			WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框",
					WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
			// 查找所有搜索结果
			List<WebElement> searchResults = null;
			if (TemplateType.equals("个人"))
				searchResults = resultBox
						.findElements(By.xpath("//ul[contains(@class,'list resizelist')]//li[.='个人']"));
			if (TemplateType.equals("科室"))
				searchResults = resultBox
						.findElements(By.xpath("//ul[contains(@class,'list resizelist')]//li[.='业务单元']"));
			// 搜索结果是空 直接报错
			if (searchResults == null || searchResults.size() == 0) {
				logger.assertFalse(true, "60无搜索结果", "未找到此模板:" + TemplateName + "\n结果包含:");
			}
			// searchFlag是空 直接选择第一个搜索结果
			else {
				wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
			}
			wnwd.waitElementByXpathAndClick("点击引用模板", WnOutpatientXpath.outpatientOrderTemplateFirstQuoteBtn,
					Framework.defaultTimeoutMax);
			try {
				wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
						Framework.defaultTimeoutMax);
				List<WebElement> unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
						Framework.defaultTimeoutMax);
				addDiagnoseIfNeed();
				own_expense(null);
				List<String> errOrders = new ArrayList<>();
				if (unSignOrders == null || unSignOrders.size() == 0) {
					throw new Error("医嘱模板未引用成功,没有找到未签署医嘱!");
				}
				for (WebElement order : unSignOrders) {
					if (order.findElements(By.xpath(".//i[contains(@class,'fail') or contains(@class,'warning')]"))
							.size() > 0) {
						errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
					}
				}
				if (errOrders.size() > 0) {
					wnwd.getScreenShot("医嘱列表异常");
					throw new Error("发现异常医嘱:" + errOrders);
				}
			} catch (Throwable e) {
				logger.log(1, "找不到成功提示,判断是否有编辑框");
				wnwd.checkElementByXpath("等待诊断编辑框", WnOutpatientXpath.outpatientDiagnosisEditBox, 3000);
				wnwd.waitElementByXpathAndClick("选择第一个诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxFirstDiag,
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
						Framework.defaultTimeoutMax);
				wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
						Framework.defaultTimeoutMax);
			}
			wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);

		}
		if (QuoteWay == 1) {
		}

	}

	// 推荐治疗方案中开立第一个药品
	public void prescribeFromRecommendTreat() {
		wnwd.waitElementByXpathAndClick("进入治疗方案", WnOutpatientXpath.outpatientRecommendTreatBtn,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击第一个开立", WnOutpatientXpath.outpatientRecommendTreatFirstPrescribeBtn,
				Framework.defaultTimeoutMax);
		getDetailType();
		WebElement purposeRadio = wnwd
				.getElementByXpath(WnOutpatientXpath.outpatientDisposalFactoryDrugPurposeRadioPrevent);
		if (purposeRadio != null) {
			wnwd.wnClickElement(purposeRadio, "选择用药目的:预防");
		}
		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 签署病历
	public void emrSignoff() {
		wnwd.waitElementByXpathAndClick("签署病历按钮", WnOutpatientXpath.outpatientEmrSignButton, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("签署成功提示", WnOutpatientXpath.outpatientEmrSignSucFlag, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("签署病历按钮(禁用)", WnOutpatientXpath.outpatientEmrSignButtonDisabled,
				Framework.defaultTimeoutMax);
	}

	// 直接打印病历
	public void emrPrint() {
		wnwd.waitElementByXpathAndClick("点击打印病历箭头", "//i[@class='el-icon-arrow-up']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击直接打印病历", "//li[contains(text(),'直接打印')]", Framework.defaultTimeoutMax);
		printConfirm();
	}

	// 病例预览
	public void emrPreview() {
		wnwd.waitElementByXpathAndClick("点击打印按钮右侧箭头", "//div[@id='medical_record']//button[.='打印']//i",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击预览", "//ul//li[.='预览']", Framework.defaultTimeoutMax);
		WebElement emrPreview = wnwd.checkElementByXpath("检查预览窗口", "//div[contains(@class,'emr-print-preview')]",
				Framework.defaultTimeoutMax);
		wnwd.getElementShot(emrPreview, "emrPreview");
	}

	// 保存病例
	public void saveEmr() {
		wnwd.waitElementByXpathAndClick("点击保存病例", WnOutpatientXpath.outpatientEmrSaveButton, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("等待保存成功提示", WnOutpatientXpath.outpatientEmrSaveSucFlag, Framework.defaultTimeoutMax);
	}

	// 撤销病例
	public void emrRevoke() {
		wnwd.waitElementByXpathAndClick("点击撤销签署按钮", WnOutpatientXpath.outpatientEmrRevokeButton,
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("签署病例按钮", WnOutpatientXpath.outpatientEmrSignButton, Framework.defaultTimeoutMax);
	}

	// 根据名称撤销医嘱项
	public void revoke(String name) {
		wnwd.IsNotExistByXpath(WnOutpatientXpath.orderSingoffLoadingFrame);
		WebElement el = wnwd.waitElementByCssSelectorAndPartText("overWidthTxt", name, Framework.defaultTimeoutMax);
		wnwd.moveToElement(el, name);
		wnwd.sleep(1000);
		WebElement revokeOrderbtn = wnwd.waitElementByCssSelector(WnOutpatientXpath.revokeOrderbtn,
				Framework.defaultTimeoutMax);
		wnwd.wnClickElement(revokeOrderbtn, "撤销医嘱按钮");
	}

	// 撤销第一条医嘱项
	public void revoke() {
		wnwd.sleep(1000);
		wnwd.IsNotExistByXpath(WnOutpatientXpath.orderSingoffLoadingFrame);
		WebElement el = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientPreviewListFirstOrder,
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(el, "第一条医嘱项");
		wnwd.sleep(1000);
		WebElement revokeOrderbtn = wnwd.waitElementByCssSelector(WnOutpatientXpath.revokeOrderbtn,
				Framework.defaultTimeoutMax);
		wnwd.wnClickElement(revokeOrderbtn, "撤销医嘱按钮");
	}

	// 根据名称删除医嘱项
	public void delete(String name) {
		wnwd.sleep(1000);
		wnwd.IsNotExistByXpath(WnOutpatientXpath.orderRevokeLoadingFrame);
		wnwd.waitElementByXpathAndClick("删除医嘱按钮", WnOutpatientXpath.deleteOrderbtn, Framework.defaultTimeoutMax);

	}

	// 删除第一条医嘱项
	public void delete() {

		wnwd.IsNotExistByXpath(WnOutpatientXpath.orderRevokeLoadingFrame);
		WebElement el = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientPreviewListFirstOrder,
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(el, "第一条医嘱项");
		wnwd.sleep(1000);
		wnwd.waitElementByXpathAndClick("删除医嘱按钮", WnOutpatientXpath.deleteOrderbtn, Framework.defaultTimeoutMax);
	}

	// 撤销并删除所有医嘱
	public void deleteAllOrder() {
		List<WebElement> result = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientSignedOrder,
				Framework.defaultTimeoutMax);
		for (int r = 0; r < result.size(); r++) {
			revoke();
			delete();
		}
	}
	
	// 删除所有未签署医嘱(仅限医嘱都未签署时)
	public void deleteAllUnsignedOrder() {
		List<WebElement> result = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
				Framework.defaultTimeoutMax);
		for (int r = 0; r < result.size(); r++) {
			delete();
		}
	}

	// 删除所有主诉和诊断
	public void deleteChiefAndDiag() {
		List<WebElement> delete_icon = wnwd.waitElementListByClass(WnOutpatientXpath.outpatientIconError,
				Framework.defaultTimeoutMax);
		for (int r = 0; r < delete_icon.size(); r++) {
			logger.log(1, "" + delete_icon.get(r).isDisplayed());
			if (delete_icon.get(r).isDisplayed() == true) {
				wnwd.wnClickElement(delete_icon.get(r), "点击删除主诉和症状按钮");
			}
		}
	}

	/**
	 * 判断所搜结果列表是否存在无库存项
	 *
	 * @return 返回true代表存在无库存项，返回false代表不存在无库存项目
	 */
	public Boolean existNoStockItem() {
		WebElement orderItemNoStock = wnwd.waitElementByXpath(WnOutpatientXpath.outpatientSearchOrderResultNoStock,
				Framework.defaultTimeoutMin);
		if (orderItemNoStock != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 搜索药品
	 *
	 * @param medicineName 药品名称
	 * @param medicineGG   药品规格
	 */
	public WebElement medicineSearch(String medicineName, String medicineGG) {
		try {
			if (medicineName == null || medicineGG == null) {
				throw new Error("药品名称或规格为空");
			}
			if (medicineName.contains("'") || medicineName.contains("\"") || medicineGG.contains("'")
					|| medicineGG.contains("\"")) {
				logger.log(1, "药品名称或规格中包含引号,无法搜索,跳过");
				throw new Error("药品名称或规格中包含引号,无法搜索,跳过");
			}
			wnwd.waitElementByXpathAndClick("医嘱搜索框", WnOutpatientXpath.outpatientSearchOrderInput,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("医嘱搜索框", WnOutpatientXpath.outpatientSearchOrderInput, medicineName,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			WebElement medicineSearchResult = wnwd.waitElementByXpath("正确搜索结果",
					"//li[@class = 'active resizeleft-con search_item_name flex']//span[@title='" + medicineName + " "
							+ medicineGG + "']/..",
					Framework.defaultTimeoutMax);
			if (Data.getScreenShot) {
				wnwd.getScreenShot("搜索完成");
			}
			return medicineSearchResult;
		} catch (Throwable e) {
			throw new Error("搜索医嘱失败:" + e.getMessage());
		}
	}

	// 搜索检查项目
	public WebElement examItemSearch(String examItemName) {
		try {
			wnwd.waitElementByXpathAndClick("医嘱搜索框", WnOutpatientXpath.outpatientSearchOrderInput,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("搜索检查项目:" + examItemName, WnOutpatientXpath.outpatientSearchOrderInput,
					examItemName, Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			WebElement examItemResult = wnwd.waitElementByXpath("完全匹配搜索结果",
					"//li[@class = 'active resizeleft-con search_item_name flex']//span[@title='" + examItemName
							+ "']/..",
					Framework.defaultTimeoutMid);
			if (Data.getScreenShot) {
				wnwd.getScreenShot("搜索完成");
			}
			if (examItemResult == null) {
				examItemResult = wnwd.getElementByXpath(
						"//li[@class = 'active resizeleft-con search_item_name flex']//span[starts-with(@title,'"
								+ examItemName + "')]/..");
			}
			return examItemResult;
		} catch (Throwable e) {
			throw new Error("搜索医嘱失败:" + e.getMessage());
		}
	}

	// 搜索检查项目
	public WebElement equalItemSearch(String ItemName) {
		try {
			wnwd.waitElementByXpathAndClick("医嘱搜索框", WnOutpatientXpath.outpatientSearchOrderInput,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("搜索医嘱:" + ItemName, WnOutpatientXpath.outpatientSearchOrderInput, ItemName,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			WebElement examItemResult = wnwd.waitElementByXpath("完全匹配搜索结果",
					"//li[@class = 'active resizeleft-con search_item_name flex']//span[@title='" + ItemName + "']/..",
					Framework.defaultTimeoutMax);
			if (Data.getScreenShot) {
				wnwd.getScreenShot("搜索完成");
			}
			return examItemResult;
		} catch (Throwable e) {
			throw new Error("搜索医嘱失败:" + e.getMessage());
		}
	}

	//获取加工厂勾选的临床服务个数
	public Integer getClinicalItemCount() {
		try {
			wnwd.waitElementByXpathAndClick("打开加工厂", "//span[@class='el-tooltip item size win-icon-look']",
					Framework.defaultTimeoutMax);
			//获取加工厂勾选的临床服务
			List<WebElement> clinicalItems = wnwd.waitElementListByXpath("//div[@class='all-exam-classify']//h4[@class='prescribed-qty']",
					Framework.defaultTimeoutMid);
			Integer totalCount = 0;
			if (null != clinicalItems) {
				totalCount = clinicalItems.size();
			}
			if (Data.getScreenShot) {
				wnwd.getScreenShot("获取加工厂勾选的临床服务个数_" + totalCount);
			}
			return totalCount;
		} catch (Throwable e) {
			if (Data.getScreenShot) {
				wnwd.getScreenShot("获取加工厂勾选的临床服务个数");
			}
			throw new Error("获取60加工厂勾选的临床服务个数:" + e.getMessage());
		}
	}

	// 获取收费合计
	public String getTotalCost() {
		try {
			WebElement rpbp = wnwd.waitElementByXpath("费用信息框", "//div[@class='rpbp rpbpShow']",
					Framework.defaultTimeoutMin);
			if (rpbp == null) {
				wnwd.waitElementByXpathAndClick("展开收费列表按钮", WnOutpatientXpath.outpatientDisposalTitleShowRpBpButton,
						Framework.defaultTimeoutMin);
			}
			WebElement totalCountSpan = wnwd.waitElementByXpath("合计收费", WnOutpatientXpath.orderTotalPrice,
					Framework.defaultTimeoutMin);
			wnwd.assertElementExist(totalCountSpan, "合计收费");
			String text = totalCountSpan.getAttribute("innerText");
			String totalCount = text.substring(1, text.length());
			for(int i=0;i<=5;i++) {
				if(totalCount.trim().equals("0")) {
					wnwd.sleep(500);
					totalCount = text.substring(1, text.length());
				}else {
					break;
				}
			}
			if (Data.getScreenShot) {
				wnwd.getScreenShot("获取价格完成_" + totalCount);
			}
			logger.assertFalse(totalCount.isEmpty(), "获取到60价格为空");
			return totalCount;
		} catch (Throwable e) {
			if (Data.getScreenShot) {
				wnwd.getScreenShot("获取价格失败");
			}
			throw new Error("获取60价格失败:" + e.getMessage());
		}
	}

	//获取联动费用
	public Double getLinkCost() {
		Double linkCost = 0.0;
		List<WebElement> linkItems = wnwd.getElementListByXpath(WnOutpatientXpath.linkItem);
		for (WebElement item : linkItems) {
			String text = item.getText();
			logger.log(1, "联动:" + text);
			linkCost += Double.valueOf(text.replace("￥", ""));
		}
		return linkCost;
	}

	// 获取检验医嘱费用
	public String getLabtestBill(String csName) {
		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		// 签署
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		signOff(0);
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		return getTotalCost();
	}

	// 获取检查医嘱费用
	public String getExamBill(String csName) {
		wnwd.waitElementByXpathAndInput("输入摘要", WnOutpatientXpath.outpatientDisposalFactoryExamSummaryInput, csName,
				Framework.defaultTimeoutMax);

		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);

		// 针对CT,MRI等包含人体图的检查项目
		List<WebElement> search_result1 = wnwd.waitElementListByXpath(
				WnOutpatientXpath.outpatientDisposalFactoryBodyImgColIsChecked, Framework.defaultTimeoutMin);
		if (search_result1.size() > 0) {
			wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
					Framework.defaultTimeoutMax);

		} else {

			// 检查项目指标明细选中状态，如果为0则选择第一个，如果为1则直接点击确认，如果大于1个，则只选择第一个
			List<WebElement> elements = wnwd.waitElementListByXpath(
					WnOutpatientXpath.outpatientDisposalFactoryExamDetailIsChecked, Framework.defaultTimeoutMax);

			if (elements.size() == 1) {
				String attributevalue = elements.get(0).getAttribute("title");
				if (!attributevalue.contains(csName)) {
					logger.log(1, attributevalue);
					wnwd.assertFalse("获取到的" + attributevalue + "与" + csName + "不一致", true);
				}
			}
			wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
					Framework.defaultTimeoutMax);

		}

		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);

		// 签署
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		signOff(0);
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		return getTotalCost();
	}

	// 获取治疗医嘱费用
	public String getTreatmentill(String csName) {
		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		// 签署
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		signOff(0);
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		return getTotalCost();
	}

	// 获取病理医嘱费用
	public String getPathologyBill(String csName) {
		wnwd.waitElementByXpathAndInput("输入检查目的", "//label[.='检查目的']/..//input", csName, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("输入临床摘要", "//label[.='临床摘要']/..//input", csName, Framework.defaultTimeoutMax);

		// 添加明细
		wnwd.waitElementByXpathAndClick("点击添加明细", WnOutpatientXpath.outpatientDisposalFactoryAddDetailBtn,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击添加部位下拉框", WnOutpatientXpath.outpatientDisposalAddPartSelect,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("选择部位", "//span[.='胃肠镜检查']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击标本下拉框", WnOutpatientXpath.outpatientDisposalSpecimenSelect,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("选择标本", "//span[.='内镜粘膜活检']", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("点击确认", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);
		// 签署
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		signOff(0);
		wnwd.waitNotExistByXpath("等待处理完成", WnOutpatientXpath.outpatientDisposalFactoryIconLoading,
				Framework.defaultTimeoutMax);
		return getTotalCost();
	}

	// 进入历史处置
	public void historicalDisposal() {
		wnwd.sleep(1000);
		wnwd.waitElementByXpathAndClick("历史处置标签", WnOutpatientXpath.outpatientHistoricalDisposal,
				Framework.defaultTimeoutMax);
	}

	// 点击历史处置后，判断是否默认选中本科室以及判断是否显示 ‘当前无处置信息’
	public void isChoosedUndergraduateCourseRoom() {
		wnwd.sleep(1000);
		wnwd.checkElementByXpath("检查默认选中本科室", WnOutpatientXpath.outpatientCheckboxLabel, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("检查无处置信息标签", "//span[.='当前无处置信息']", Framework.defaultTimeoutMax);
	}

	// 是否默认选中本科室和时间日期是否30日
	public void checkHistoryBaseInfo() {
		wnwd.checkElementByXpath("检查默认选中本科室", WnOutpatientXpath.outpatientCheckboxLabel, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击时间选择框", WnOutpatientXpath.outpatientTimeSelect, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击30日", WnOutpatientXpath.outpatientTimeSelectPicker,
				Framework.defaultTimeoutMax);
	}

	/**
	 * @description 点击结束诊断按钮
	 */
	public void endDiagnosis() {
		wnwd.waitElementByXpathAndClick("点击结束诊断按钮", WnOutpatientXpath.outpatientEndOfTreatmentBtn,
				Framework.defaultTimeoutMax);
		// wnwd.waitNotExistByXpath("等待结束诊断按钮不可见",WnOutpatientXpath.outpatientEndOfTreatmentBtn,Frmcons.defaultTimeoutMax);
//        wnwd.sleep(5000);
	}

	/**
	 * @description 点击历史处置后 1、判断诊断内容 2、判断就诊科室 3、判断就诊医生 4、判断引用标志
	 */
	public void checkHistoryInfo() {
		wnwd.sleep(1000);
		checkHistoryBaseInfo();
		// 判断诊断内容
		wnwd.checkElementByXpath("获取就诊内容",
				"//p[@class='diagnosis overWidthTxt w110 el-popover__reference' and contains(.,'尿路感染')]",
				Framework.defaultTimeoutMax);
		// 判断就诊科室
		wnwd.checkElementByXpath("获取就诊科室", "//span[@class='overWidthTxt' and contains(.,'肾脏风湿科')]",
				Framework.defaultTimeoutMax);
		// 判断就诊医生曲忠森
		wnwd.checkElementByXpath("获取就诊医生",
				"//p[@class='flex flex__justifyContent--spaceBetween single-dept']/span[.='曲忠森']",
				Framework.defaultTimeoutMax);
		// 判断引用标志
		wnwd.checkElementByXpath("获取引用标志", WnOutpatientXpath.outpatientQuoteBtn, Framework.defaultTimeoutMax);
	}

	/**
	 * @description 检查本科室——检查30日——检查引用标志——判断处置内容——数量包装单位
	 */
	public void checkHistoryContentInfo() {
		wnwd.sleep(1000);
		checkHistoryBaseInfo();
		// 判断引用标志
		wnwd.checkElementByXpath("判断引用标志", WnOutpatientXpath.outpatientDealBtnQuote, Framework.defaultTimeoutMax);
		// 判断处置内容
		WebElement element = wnwd.checkElementByXpath("获取处置内容",
				"//ul[@class='history-detail']//p[@class='el-tooltip blackTxt detail-info overWidthTxt']",
				Framework.defaultTimeoutMax);
		// 判断处置内容是否存在
		if (element != null) {
			String text = element.getText().trim();
			logger.log(1, text);
			wnwd.assertTrue("处置内容包含不存在，获取到的处置内容如下: " + text, "" != text && null != text);
			// 判断处置内容是否存在对应信息
			String[] texts = text.split(" ");
			wnwd.assertTrue("处置内容包含信息存在异常，获取到的处置内容如下: " + text, texts.length == 4);
			// 药品名称
			String drug_name = text.split(" ")[0];
			logger.log(1, drug_name);
			wnwd.assertTrue("药品名称不对应: " + drug_name, drug_name.contains(Data.outpatientDisaposalDrug));
			// 规格
			String drug_specification = text.split(" ")[1];
			logger.log(1, drug_specification);
			wnwd.assertTrue("药品规格不对应: " + drug_specification,
					drug_specification.equals(Data.outpatientDisaposalDrugSpecification));
			// 规格剂量
			String drug_dose = text.split(" ")[2];
			logger.log(1, drug_dose);
			wnwd.assertTrue("药品剂量不对应: " + drug_dose, drug_dose.equals(Data.outpatientDisaposalDrugDose));
			// 药品数量&零售包装单位
			String drug_number_unit = text.split(" ")[3];
			logger.log(1, drug_number_unit);
			wnwd.assertTrue("药品数量&零售包装单位不对应: " + drug_number_unit,
					drug_number_unit.equals(Data.outpatientDisaposalDrugNumberUnit));
			// 数字正则表达式
			String reg = "[^0-9]";
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(drug_number_unit);
			// 药品数量
			String drug_number = m.replaceAll("").trim();
			// 零售包装单位
			String drug_unit = drug_number_unit.replace(drug_number, "");
			logger.log(1, "药品名称：" + drug_name + "； 规格：" + drug_specification + "； 规格剂量：" + drug_dose + "； 药品数量："
					+ drug_number + "； 零售包装单位：" + drug_unit);

		}

	}

	/**
	 * @description 检查本科室——检查30日——检查引用标志——判断处置内容——中草药数量包装单位
	 */
	public void checkHistoryHerbContentInfo() {
		wnwd.sleep(1000);
		checkHistoryBaseInfo();
		// 判断引用标志
		wnwd.checkElementByXpath("判断引用标志", WnOutpatientXpath.outpatientDealBtnQuote, Framework.defaultTimeoutMax);
		WebElement el = wnwd.waitElementByXpath("//p[@class='el-tooltip blackTxt detail-info overWidthTxt']",
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(el, "处置内容");
		// 判断处置内容
		WebElement element = wnwd.checkElementByXpath("获取处置内容",
				"//div[@class='el-tooltip__popper is-dark histroyHerbal-popper']//span", Framework.defaultTimeoutMax);
		// 判断处置内容是否存在
		if (element != null) {
			String text = element.getText().trim();
			logger.log(1, text);
			wnwd.assertTrue("处置内容包含不存在，获取到的处置内容如下: " + text, "" != text && null != text);
			// 判断处置内容是否存在对应信息
			String[] texts = text.split(" ");
			wnwd.assertTrue("处置内容包含信息存在异常，获取到的处置内容如下: " + text, texts.length == 4);
			// 药品名称
			String herb_name = text.split(" ")[0];
			logger.log(1, herb_name);
			wnwd.assertTrue("药品名称不对应: " + herb_name, herb_name.equals(Data.outpatientDisaposalHerbal));
			// 规格剂量
			String herb_dose = text.split(" ")[2];
			logger.log(1, herb_dose);
			wnwd.assertTrue("药品剂量不对应: " + herb_dose, herb_dose.equals(Data.outpatientDisaposalHerbalDose));
			// 药品数量&零售包装单位
			String herb_number_unit = text.split(" ")[3];
			logger.log(1, herb_number_unit);
			wnwd.assertTrue("药品数量&零售包装单位不对应: " + herb_number_unit,
					herb_number_unit.equals(Data.outpatientDisaposalHerbalNumberUnit));
			// 数字正则表达式
			String reg = "[^0-9]";
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(herb_number_unit);
			// 药品数量
			String herb_number = m.replaceAll("").trim();
			// 零售包装单位
			String herb_unit = herb_number_unit.replace(herb_number, "");
			logger.log(1,
					"药品名称：" + herb_name + "；规格剂量：" + herb_dose + "； 药品数量：" + herb_number + "； 零售包装单位：" + herb_unit);

		}
	}

	/**
	 * @description 检查本科室——检查30日——检查引用标志——判断处置内容——中成药数量包装单位
	 */
	public void checkHistoryCnPatentMedicineContentInfo() {
		wnwd.sleep(1000);
		checkHistoryBaseInfo();
		// 判断引用标志
		wnwd.checkElementByXpath("判断引用标志", WnOutpatientXpath.outpatientDealBtnQuote, Framework.defaultTimeoutMax);
		// 判断处置内容
		WebElement element = wnwd.checkElementByXpath("获取处置内容",
				"//ul[@class='history-detail']//p[@class='el-tooltip blackTxt detail-info overWidthTxt']",
				Framework.defaultTimeoutMax);
		// 判断处置内容是否存在
		if (element != null) {
			String text = element.getText().trim();
			logger.log(1, text);
			wnwd.assertTrue("处置内容包含不存在，获取到的处置内容如下: " + text, "" != text && null != text);
			// 判断处置内容是否存在对应信息
			String[] texts = text.split(" ");
			wnwd.assertTrue("处置内容包含信息存在异常，获取到的处置内容如下: " + text, texts.length == 4);
			// 药品名称
			String CnPatentMedicine_name = text.split(" ")[0];
			logger.log(1, CnPatentMedicine_name);
			wnwd.assertTrue("药品名称不对应: " + CnPatentMedicine_name,
					CnPatentMedicine_name.equals(Data.outpatientDisaposalCnPatentMedicine));
			// 规格
			String CnPatentMedicine_specification = text.split(" ")[1];
			logger.log(1, CnPatentMedicine_specification);
			wnwd.assertTrue("药品规格不对应: " + CnPatentMedicine_specification,
					CnPatentMedicine_specification.equals(Data.outpatientDisaposalCnPatentMedicineSpecification));
			// 规格剂量
			String CnPatentMedicine_dose = text.split(" ")[2];
			logger.log(1, CnPatentMedicine_dose);
			wnwd.assertTrue("药品剂量不对应: " + CnPatentMedicine_dose,
					CnPatentMedicine_dose.equals(Data.outpatientDisaposalCnPatentMedicineDose));
			// 药品数量&零售包装单位
			String CnPatentMedicine_number_unit = text.split(" ")[3];
			logger.log(1, CnPatentMedicine_number_unit);
			wnwd.assertTrue("药品数量&零售包装单位不对应: " + CnPatentMedicine_number_unit,
					CnPatentMedicine_number_unit.equals(Data.outpatientDisaposalCnPatentMedicineNumberUnit));
			// 数字正则表达式
			String reg = "[^0-9]";
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(CnPatentMedicine_number_unit);
			// 药品数量
			String CnPatentMedicine_number = m.replaceAll("").trim();
			// 零售包装单位
			String CnPatentMedicine_unit = CnPatentMedicine_number_unit.replace(CnPatentMedicine_number, "");
			logger.log(1,
					"药品名称：" + CnPatentMedicine_name + "； 规格：" + CnPatentMedicine_specification + "； 规格剂量："
							+ CnPatentMedicine_dose + "； 药品数量：" + CnPatentMedicine_number + "； 零售包装单位："
							+ CnPatentMedicine_unit);

		}
	}

	/**
	 * @description 检查本科室——检查30日——检查引用标志——判断服务名称——判断警示标志——判断tips显示服务名称——判断tips显示服务名称
	 */
	public void checkHistoryTreatmentInfo(String Tname) {
		wnwd.sleep(1000);
		checkHistoryBaseInfo();
		// 判断引用标志
		wnwd.IsNotExistByXpath("//ul[@class='history-detail']/li[1]/span[@class='quoteBtnImg copyAndQuote']");
		wnwd.IsNotExistByXpath("//ul[@class='history-detail']/li[2]/span[@class='quoteBtnImg copyAndQuote']");
		// 判断服务名称

		WebElement name = wnwd.checkElementByXpath("检查服务名称", "//ul[@class='history-detail']//p[.='" + Tname + "']",
				Framework.defaultTimeoutMax);
		// 判断警示标志
		WebElement icon = wnwd.checkElementByXpath("检查警示标志", WnOutpatientXpath.outpatientWarningIcon,
				Framework.defaultTimeoutMax);
		// 判断tips显示服务名称
		wnwd.moveToElement(name, "tips显示服务名称");
		wnwd.sleep(1000);
		wnwd.checkElementByXpath("检查tips显示服务名称",
				"//div[@class='el-tooltip__popper is-dark' and @aria-hidden='false']/div[.='" + Tname + "']",
				Framework.defaultTimeoutMax);

		// 判断tips显示服务名称
		wnwd.moveToElement(icon, "tips显示警示提示");
		wnwd.sleep(1000);
		wnwd.checkElementByXpath("检查tips显示警示提示",
				"//div[@class='el-popover el-popper' and @aria-hidden='false']/div[.='该医嘱的状态为只读']",
				Framework.defaultTimeoutMax);
	}

	/**
	 * @param diseaseSpecies 主诊断名称
	 * @param disease        子诊断名称
	 */
	public void checkQuoteHistoryInfo(String diseaseSpecies, String disease) {
		wnwd.waitElementByXpathAndClick("点击诊断名称",
				"//div[@class='info-single']//p[@class='diagnosis overWidthTxt w110 el-popover__reference' and .='尿路感染']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击引用",
				"//div[contains(@class,'diagnosis_Wrap_select')]//span[@class='quoteBtnImg']",
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("检查记录域诊断",
				"//span[@wn-desc='请输入门诊诊断' and contains(.,'" + diseaseSpecies + "') and contains(.,'" + disease + "')]",
				Framework.defaultTimeoutMax);
	}

	// 处置内容药品引用
	public void checkQuoteDrugHistoryInfo() {
		// 获取处置内容药物行的元素
		wnwd.waitElementByXpathAndClick("点击诊断名称",
				"//div[@class='info-single']//p[@class='diagnosis overWidthTxt w110 el-popover__reference' and .='尿路感染']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击引用",
				"//div[contains(@class,'diagnosis_Wrap_select')]//span[@class='quoteBtnImg']",
				Framework.defaultTimeoutMax);

		WebElement drugLine = wnwd.checkElementByXpath("药物信息行", "//ul[@class='history-detail']/li[2]",
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(drugLine, "内容药物行的元素");
		wnwd.waitElementByXpathAndClick("点击引用", "//ul[@class='history-detail']/li[2]//span[@class='quoteBtnImg']",
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("等待引用成功提示", "//div[@class='el-message el-message--success']//p[.='引用成功']",
				Framework.defaultTimeoutMax);
		// 判断是否自费
		own_expense();
		// 判断是否未签署
		wnwd.checkElementByXpath("判断是否未签署", "//div[@class='preview']//li[2]/span[.='未签署']", Framework.defaultTimeoutMax);
		/*---判断数量是否一致开始----*/
		// 判断处置内容的数量
		WebElement historyInfo = wnwd.checkElementByXpath("获取处置内容",
				"//ul[@class='history-detail']//p[@class='el-tooltip blackTxt detail-info overWidthTxt']",
				Framework.defaultTimeoutMax);
		String historyInfoText = historyInfo.getText().trim();
		String historyInfoNumber = historyInfoText.split(" ")[3];
		// 获取引用内容的数量
		// *[@id="disposal"]/div/div[2]/div[1]/div/div/div/div/ul/li[3]/p/span[7]
		WebElement quoteInfo = wnwd.checkElementByXpath("获取引用内容的药品数量",
				"//ul[@class='doint_bg preview__list flex__alignItems--center herbal']//li[@class='content textAlign__left']//span[@class='num overWidthTxt']",
				Framework.defaultTimeoutMax);
		String quoteInfoNumber = quoteInfo.getText().trim().replace(" ", "");
		// 对比
		wnwd.assertTrue("处置内容和引用内容药品数量是否一致", historyInfoNumber.equals(quoteInfoNumber));
		/*---判断数量是否一致结束----*/
	}

	// 处置内容多个药品引用
	public void checkQuoteMultipleDrugHistoryInfo(List<String> drugList) {
		// 获取处置内容药物行的元素
		wnwd.waitElementByXpathAndClick("点击诊断名称",
				"//div[@class='info-single']//p[@class='diagnosis overWidthTxt w110 el-popover__reference' and .='尿路感染']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击引用",
				"//div[contains(@class,'diagnosis_Wrap_select')]//span[@class='quoteBtnImg']",
				Framework.defaultTimeoutMax);
		// 获取处置内容div行数
		List<WebElement> liline = wnwd.getElementListByXpath("//ul[@class='history-detail']/li");
		wnwd.assertTrue("判断处置内容行数是否符合填写医嘱的行数", liline.size() == drugList.size() + 1);
		for (int d = 0; d < drugList.size(); d++) {
			wnwd.checkElementByXpath("检查药物信息行:" + drugList.get(d),
					"//ul[@class='history-detail']/li[contains(.,'" + drugList.get(d) + "')]/..//li",
					Framework.defaultTimeoutMax);
			WebElement drugLine = wnwd.checkElementByXpath("药物信息行", "//ul[@class='history-detail']/li[" + (d + 2) + "]",
					Framework.defaultTimeoutMax);
			wnwd.moveToElement(drugLine, "内容药物行的元素");
			wnwd.waitElementByXpathAndClick("点击引用",
					"//ul[@class='history-detail']/li[" + (d + 2) + "]//span[@class='quoteBtnImg']",
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("等待引用成功提示", "//div[@class='el-message el-message--success']//p[.='引用成功']",
					Framework.defaultTimeoutMax);
			// 判断是否自费
			own_expense();
			// 判断是否未签署
			wnwd.checkElementByXpath("判断是否未签署", "//div[@class='previewItemWrap candraggable'][" + (d + 1)
					+ "]//div[@class='preview']//ul[1]//li[2]/span[.='未签署']", Framework.defaultTimeoutMax);
			/*---判断数量是否一致开始----*/
			// 判断处置内容的数量
			WebElement historyInfo = wnwd.checkElementByXpath("获取处置内容", "//ul[@class='history-detail']/li[" + (d + 2)
					+ "]//p[@class='el-tooltip blackTxt detail-info overWidthTxt']", Framework.defaultTimeoutMax);
			String historyInfoText = historyInfo.getText().trim();
			String historyInfoNumber = historyInfoText.split(" ")[3];
			// 获取引用内容的数量
			// *[@id="disposal"]/div/div[2]/div[1]/div/div/div/div/ul/li[3]/p/span[7]
			WebElement quoteInfo = wnwd.checkElementByXpath("获取引用内容的药品数量",
					"//div[@class='previewItemWrap candraggable'][" + (d + 1)
							+ "]//div[@class='preview']//ul[1]//li[@class='content textAlign__left']//span[@class='num overWidthTxt']",
					Framework.defaultTimeoutMax);
			String quoteInfoNumber = quoteInfo.getText().trim().replace(" ", "");
			// 对比
			wnwd.assertTrue("处置内容和引用内容药品数量是否一致", historyInfoNumber.equals(quoteInfoNumber));
			/*---判断数量是否一致结束----*/
		}

	}

	// 上线历史处置引用单个药品
	public void QuoteDrug() {
		wnwd.checkElementByXpath("检查默认选中本科室", WnOutpatientXpath.outpatientCheckboxLabel, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击时间选择框", WnOutpatientXpath.outpatientTimeSelect, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击6个月", "//button[@class='el-picker-panel__shortcut' and contains(.,'6个月')]",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击诊断名称", "//ul[@class='history-list']/li[1]//span[.='诊+处']",
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("检查引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
				Framework.defaultTimeoutMax);
		WebElement drugLine = wnwd.checkElementByXpath("药物信息行", "//ul[@class='history-detail']/li[2]",
				Framework.defaultTimeoutMax);
		wnwd.moveToElement(drugLine, "内容药物行的元素");
		// 判断是否自费
		own_expense();
		// 判断是否未签署
		wnwd.checkElementByXpath("判断是否未签署", "//div[@class='preview']//li[2]/span[.='未签署']", Framework.defaultTimeoutMax);
		signOff(0);
		emrSignoff();
		endDiagnosis();
	}

	// 上线历史处置引用多个药品
	public void QuoteMultipleDrug() {
		wnwd.checkElementByXpath("检查默认选中本科室", WnOutpatientXpath.outpatientCheckboxLabel, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击时间选择框", WnOutpatientXpath.outpatientTimeSelect, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("点击6个月", "//button[@class='el-picker-panel__shortcut' and contains(.,'6个月')]",
				50000);
		wnwd.waitElementByXpathAndClick("点击诊断名称", "//ul[@class='history-list']/li[1]//span[.='诊+处']",
				Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("检查引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
				Framework.defaultTimeoutMax);

		// 获取处置内容div行数
		List<WebElement> liline = wnwd.getElementListByXpath("//ul[@class='history-detail']/li");
		List<WebElement> drugdline = wnwd
				.getElementListByXpath("//div[@class='previewList_wrap preview-search relative max398']");
		wnwd.assertTrue("判断处置内容行数是否符合填写医嘱的行数", liline.size() == drugdline.size());
		for (int d = 0; d < drugdline.size(); d++) {
			wnwd.checkElementByXpath("检查药物信息行:" + drugdline.get(d),
					"//ul[@class='history-detail']/li[contains(.,'" + drugdline.get(d) + "')]/..//li",
					Framework.defaultTimeoutMax);
			WebElement drugLine = wnwd.checkElementByXpath("药物信息行", "//ul[@class='history-detail']/li[" + (d + 2) + "]",
					Framework.defaultTimeoutMax);
			wnwd.moveToElement(drugLine, "内容药物行的元素");
			// 判断是否自费
			own_expense();
			signOff(0);

		}
		emrSignoff();
		endDiagnosis();
	}

	//获取当前处置开立区中医嘱数量
	public int getClinicalOrderCount() {
		int signedOrderCount = 0;
		List<WebElement> elements = wnwd
				.waitElementListByXpath(WnOutpatientXpath.outpatientOrderName, Framework.defaultTimeoutMid);
		if(elements!=null) {
			signedOrderCount = elements.size();
			logger.boxLog(1, "处置开立区医嘱数量："+elements.size(), "");
			return signedOrderCount;
		}
		return signedOrderCount;
	}

	// 如果弹出诊断框,选择第一个
	public void addDiagnoseIfNeed() {
		WebElement ele = wnwd.waitElementByXpaths(new ArrayList<String>(Arrays
				.asList(WnOutpatientXpath.outpatientDisposalFactory, WnOutpatientXpath.outpatientDiagnosisEditBox)),
				Framework.defaultTimeoutMax);
		if (ele != null && ele.getAttribute("id").contains("diagnostic")) {
			wnwd.waitElementByXpathAndClick("第一个诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxFirstDiag,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("确定诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待诊断选择框不可见", WnOutpatientXpath.outpatientDiagnosisEditBox,
					Framework.defaultTimeoutMax);
			WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMid);
			if (cancelBtn != null) {
				wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
			}
		}
	}

	// 如果弹出皮试框,点击皮试
	public boolean pishiIfNeed() {
		Wn60Db windb60 = new Wn60Db(SdkTools.logger);
		Map<String, String> param;
		param = windb60.getParam("CL040");
		if (param != null && param.containsKey("PARAM_VALUE") && param.get("PARAM_VALUE").equals("2")) {
			WebElement pishiButton = wnwd.waitElementByXpath("皮试确认", WnOutpatientXpath.pishiButton,
					Framework.defaultTimeoutMin);
			if (pishiButton != null) {
				wnwd.wnClickElement(pishiButton, "皮试确认");
			}
			return true;
		}
		if (param != null && param.containsKey("PARAM_VALUE") && param.get("PARAM_VALUE").equals("1")) {
			WebElement pishiButton = wnwd.waitElementByXpath("皮试确认", WnOutpatientXpath.pishiButton,
					Framework.defaultTimeoutMin);
			if (pishiButton != null) {
				wnwd.wnClickElement(pishiButton, "皮试确认");
				checkOrderForSkin();
			}
			return false;

		}
		return false;
	}

	// 如果弹出儿童信息框，输入信息
	public void childrenInfoIfNeed() {
		WebElement childrenInfoDialog = wnwd.waitElementByXpath("儿童信息录入框",
				WnOutpatientXpath.outpatientChildrenInfoDialog, Framework.defaultTimeoutMin);
		if (childrenInfoDialog != null) {
			wnwd.waitElementByXpathAndInput("输入身高", "//label[.='身高']//following-sibling::*//input", "120",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("输入体重", "//label[.='体重']//following-sibling::*//input", "35",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("儿童信息录入确定按钮", "//div[@class='children-info-wrapper']//button",
					Framework.defaultTimeoutMax);
		}
	}

	// 如果弹出皮试框,点击确认
	public void checkSkinTestDialog(Boolean skinTestDialog, List<String> checkButtonList, String clickButton) {
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
		WebElement skinTest = wnwd.waitElementByXpath("皮试窗口", WnOutpatientXpath.pishiButton, Framework.defaultTimeoutMid);
		if (skinTestDialog) {
			logger.assertFalse(skinTest == null, "未找到皮试窗口");
			if (checkButtonList != null) {
				// 检查按钮
				List<WebElement> buttonList = wnwd
						.getElementListByXpath(WnOutpatientXpath.pishiButton.replace("皮试", ""));
				logger.assertFalse(buttonList.size() != checkButtonList.size(), "按钮数量与预期不一致",
						"预期:" + checkButtonList.size() + " 个\n实际:" + buttonList.size() + "个");
				for (String buttonText : checkButtonList) {
					Boolean findFlag = false;
					for (WebElement button : buttonList) {
						if (button.getText().equals(buttonText)) {
							findFlag = true;
						}
					}
					logger.assertFalse(!findFlag, "未找到按钮:" + buttonText);
					logger.log(1, "检查按钮通过: " + buttonText);
				}
			}
			wnwd.waitElementByXpathAndClick(clickButton + "按钮",
					WnOutpatientXpath.pishiButton.replace("皮试", clickButton), Framework.defaultTimeoutMax);
		} else {
			logger.assertFalse(skinTest != null, "不应弹出皮试窗口");
		}
	}

	// 精麻毒药品确认
	public void spacialMedicineCommitIfNeed() {
		WebElement finish = wnwd
				.getElementByXpath(WnOutpatientXpath.outpatientDisposalFactorySpectialInfoCommitButtonXpath);
		if (finish != null) {
			wnwd.wnClickElement(finish, "完成");
		}
	}

	// 选择用药目的
	public void purposeCommitIfNeed() {
		WebElement purposeRadio = wnwd
				.getElementByXpath(WnOutpatientXpath.outpatientDisposalFactoryDrugPurposeRadioPrevent);
		if (purposeRadio != null) {
			wnwd.wnClickElement(purposeRadio, "选择用药目的:预防");
		}
	}

	// 选择医嘱联动
	public void linkageCommitIfNeed() {
//    	WebElement linkageDialog = wnwd.getElementByXpath(WnOutpatientXpath.outpatientLinkageCommitButton);
		WebElement linkageDialog = wnwd.waitElementByXpath("医嘱联动对话框", WnOutpatientXpath.outpatientLinkageCommitButton,
				Framework.defaultTimeoutMax);
		if (linkageDialog != null) {
			wnwd.wnClickElement(linkageDialog, "医嘱联动确认");
		}
	}

	// 切换主诉类型
	public void changeSymptomType(String symptom_type) {
		try {
			wnwd.waitElementByXpathAndClick("切换主诉类型图标", "//button[contains(@class,'change-symptom-type')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick(symptom_type,
					"//div[@class='symptom-type-wrap']//div[contains(.,'?1')]".replace("?1", symptom_type),
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "切换主诉类型失败:" + e.getMessage());
		}
	}

	// 知识体系切换
	public void changeKnowledgeSystem(String knowledge_system) {
		try {
			wnwd.waitElementByXpathAndClick("右上角图标", "//div[@class='win-patient-avatar']", Framework.defaultTimeoutMax);
			wnwd.moveToElementByXpath("鼠标移动到知识体系切换", "//span[contains(text(),'知识体系切换')]", Framework.defaultTimeoutMax);
			WebElement element = wnwd.waitElementByXpath(knowledge_system,
					"//li[contains(text(),'" + knowledge_system + "')]", Framework.defaultTimeoutMax);
			wnwd.wnClickElementByMouse(element, knowledge_system);
			SdkTools.sleep(Framework.defaultTimeoutMin);
		} catch (Throwable e) {
			logger.assertFalse(true, "切换" + knowledge_system + "失败:" + e.getMessage());
		}
	}

	public void unselectSearchStock() {
		try {
			WebElement serchStockCheck = wnwd.waitElementByXpath("检索设置按钮",
					WnOutpatientXpath.outpatientSearchSettingButton, Framework.defaultTimeoutMax);// 检索设置按钮
			if (serchStockCheck != null) {
				wnwd.wnClickElement(serchStockCheck, "点击检索设置按钮");
				WebElement serchStockCheckBox = wnwd.waitElementByXpath("库存勾选框",
						WnOutpatientXpath.outpatientSearchStockCheckboxNew, Framework.defaultTimeoutMid);
				if (serchStockCheckBox.getAttribute("class").contains("is-checked")) {
					wnwd.wnClickElement(serchStockCheckBox, "取消勾选有库存");
					wnwd.waitElementByXpathAndClick("点击检索确认按钮", WnOutpatientXpath.outpatientSearchSettingConfirmButton,
							Framework.defaultTimeoutMid);
				} else {// 当勾选框未被勾选时关闭检索设置弹框
					wnwd.waitElementByXpathAndClick("点击检索确认按钮", WnOutpatientXpath.outpatientSearchSettingConfirmButton,
							Framework.defaultTimeoutMid);
				}
				wnwd.waitNotExistByXpath("等待库存勾选框消失", WnOutpatientXpath.outpatientSearchStockCheckboxNew,
						Framework.defaultTimeoutMid);
			} else {
				WebElement serchStockCheckBox = wnwd.waitElementByXpath("库存勾选框",
						WnOutpatientXpath.outpatientSearchStockCheckbox, Framework.defaultTimeoutMax);
				if (serchStockCheckBox.getAttribute("class").contains("is-checked")) {
					wnwd.wnClickElement(serchStockCheckBox, "取消勾选有库存");
				}
			}
		} catch (Throwable e) {
			throw new Error("弹框加载错误:检索设置弹框未消失:" + e.getMessage());
		}
	}

	public void selectSearchStock() {
		try {
			WebElement serchStockCheck = wnwd.waitElementByXpath("检索设置按钮",
					WnOutpatientXpath.outpatientSearchSettingButton, Framework.defaultTimeoutMax);// 检索设置按钮
			if (serchStockCheck != null) {
				wnwd.wnClickElement(serchStockCheck, "点击检索设置按钮");
				WebElement serchStockCheckBox = wnwd.waitElementByXpath("库存勾选框",
						WnOutpatientXpath.outpatientSearchStockCheckboxNew, Framework.defaultTimeoutMid);
				if (serchStockCheckBox.getAttribute("class").contains("is-checked")) {
					wnwd.waitElementByXpathAndClick("点击检索确认按钮", WnOutpatientXpath.outpatientSearchSettingConfirmButton,
							Framework.defaultTimeoutMid);
				} else {// 当勾选框未被勾选时关闭检索设置弹框
					wnwd.wnClickElement(serchStockCheckBox, "取消勾选有库存");
					wnwd.waitElementByXpathAndClick("点击检索设置保存按钮",
							WnOutpatientXpath.outpatientSearchSettingConfirmButton, Framework.defaultTimeoutMid);
				}
				wnwd.waitNotExistByXpath("等待库存勾选框消失", WnOutpatientXpath.outpatientSearchStockCheckboxNew,
						Framework.defaultTimeoutMid);
			} else {
				WebElement serchStockCheckBox = wnwd.waitElementByXpath("库存勾选框",
						WnOutpatientXpath.outpatientSearchStockCheckbox, Framework.defaultTimeoutMax);
				if (serchStockCheckBox.getAttribute("class").contains("is-checked")) {
					wnwd.wnClickElement(serchStockCheckBox, "取消勾选有库存");
				}
			}
		} catch (Throwable e) {
			throw new Error("弹框加载错误:检索设置弹框未消失:" + e.getMessage());
		}
	}

	// 获取加工厂默认值
	public void getDisposalFactoryDefault(Map<String, String> service) {
		try {
			if (service.get("TYPE").equals("drug")) {
				List<WebElement> inputs = wnwd.waitElementListByXpath(
						"//div[@class='disposal__factory']//ul[contains(@class,'disposal__factory--list')]//input",
						Framework.defaultTimeoutMin);
				service.put("UI_DOSAGE", inputs.get(0).getAttribute("value"));
				service.put("UI_DOSAGE_UNIT", inputs.get(1).getAttribute("value"));
				service.put("UI_DOSAGE_ROUTE", inputs.get(2).getAttribute("value"));
				service.put("UI_FREQ", inputs.get(3).getAttribute("value"));
				service.put("UI_PACKAGE_UNIT", inputs.get(6).getAttribute("value"));
			} else if (service.get("TYPE").equals("herb")) {
				List<WebElement> inputs = wnwd.waitElementListByXpath(
						"//div[@class='disposal__factory']//span[.='剂量']/..//input", Framework.defaultTimeoutMin);
				service.put("UI_DOSAGE", inputs.get(0).getAttribute("value"));
				service.put("UI_DOSAGE_UNIT", inputs.get(1).getAttribute("value"));
				service.put("UI_DOSAGE_ROUTE",
						wnwd.getElementByXpath("//div[@class='disposal__factory']//label[.='给药途径']/..//input")
								.getAttribute("value"));
				service.put("UI_FREQ",
						wnwd.getElementByXpath("//div[@class='disposal__factory']//label[.='频次']/..//input")
								.getAttribute("value"));
			}
		} catch (Throwable e) {
			throw new Error("加工厂默认值错误:获取药品加工厂默认参数失败:" + e.getMessage());
		}
	}

	public void CheckDisposalFactoryDefault(String csType, Map<String, String> service) {
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
			if (service.get("UI_DOSAGE_ROUTE").equals(service.get("DB_DOSAGE_ROUTE"))) {
				logger.log(1,
						"默认给药途径正确: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
			} else {
				throw new Error(
						"默认给药途径错误: 界面: " + service.get("UI_DOSAGE_ROUTE") + "/数据库: " + service.get("DB_DOSAGE_ROUTE"));
			}
			if (service.get("UI_FREQ").toUpperCase().equals(service.get("DB_FREQ").toUpperCase())) {
				logger.log(1, "默认频次正确: 界面: " + service.get("UI_FREQ") + "/数据库: " + service.get("DB_FREQ"));
			} else {
//                throw new Error("默认频次错误: 界面: " + service.get("UI_FREQ") + "/数据库: " + service.get("DB_FREQ"));
			}
			if (csType.equals("drug")) {
				if (service.get("UI_PACKAGE_UNIT").equals(service.get("DB_PACKAGE_UNIT"))) {
					logger.log(1, "默认门诊单位正确: 界面: " + service.get("UI_PACKAGE_UNIT") + "/数据库: "
							+ service.get("DB_PACKAGE_UNIT"));
				} else {
					throw new Error("默认门诊单位错误: 界面: " + service.get("UI_PACKAGE_UNIT") + "/数据库: "
							+ service.get("DB_PACKAGE_UNIT"));
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.log("" + e.getStackTrace());
			throw new Error("加工厂默认值错误:" + e.getMessage());
		}
	}

	// 撤销所有已签署医嘱
	public void revokeOrders() {
		while (wnwd.waitElementByXpath("已签署医嘱", WnOutpatientXpath.outpatientSignedOrder,
				Framework.defaultTimeoutMin) != null) {
			revokeOrder();
		}
		logger.log(1, "撤销所有已签署医嘱完成");
	}

	// 删除所有未签署医嘱
	public void deleteOrders() {
		while (wnwd.waitElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder,
				Framework.defaultTimeoutMin) != null) {
			deleteOrder();
		}
		logger.log(1, "删除所有未签署医嘱完成");
	}

	// 撤销第一条已签署
	public void revokeOrder() {
		try {
			List<WebElement> unsignOrderList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder);
			Integer unsignOrderCount = unsignOrderList == null ? 0 : unsignOrderList.size();
			wnwd.waitElementByXpathAndClick("撤销医嘱按钮", WnOutpatientXpath.outpatientRevokeOrderBtn,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
			unsignOrderList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder);
			Integer unsignOrderCount2 = unsignOrderList == null ? 0 : unsignOrderList.size();
			if (unsignOrderCount >= unsignOrderCount2) {
				throw new Error("撤销后未签署医嘱数量没有增加!");
			} else {
				wnwd.checkError(1000);
				logger.log(1, "撤销医嘱成功(未签署医嘱数):" + unsignOrderCount + "--" + unsignOrderCount2);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "撤销医嘱失败", e.getMessage());
		}
	}

	// 删除第一条未签署医嘱
	public void deleteOrder() {
		try {
			List<WebElement> unsignOrderList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder);
			Integer unsignOrderCount = unsignOrderList == null ? 0 : unsignOrderList.size();
			wnwd.waitElementByXpathAndClick("删除医嘱按钮", WnOutpatientXpath.outpatientDeleteOrderBtn,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
					Framework.defaultTimeoutMax);
			wnwd.sleep(Framework.defaultTimeoutMax);
			unsignOrderList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder);
			Integer unsignOrderCount2 = unsignOrderList == null ? 0 : unsignOrderList.size();
			if (unsignOrderCount <= unsignOrderCount2) {
				throw new Error("删除后未签署医嘱数量没有减少!");
			} else {
				wnwd.checkError(1000);
				logger.log(1, "删除医嘱成功(未签署医嘱数):" + unsignOrderCount + "--" + unsignOrderCount2);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "删除医嘱失败", e.getMessage());
		}
	}

	// 结束就诊
	public void endOutpatient() {
		wnwd.waitElementByXpathAndClick("点击结束就诊", "//span[contains(text(),'结束就诊')]", Framework.defaultTimeoutMax);
//		wnwd.checkElementByXpath("检查结束就诊按钮是否禁灰", "//button[@class='el-button end-treatment-btn el-button--default el-button--small is-disabled disabled']", Frmcons.defaultTimeoutMax);
		visitConfirmIfNeed();
	}

	//结束就诊后，是否需要预约
	public void visitConfirmIfNeed() {
		WebElement visitConfirmDialog = wnwd.waitElementByXpath("是否需要预约弹窗",
				WnOutpatientXpath.visitConfirmDialog, Framework.defaultTimeoutMin);
		WebElement visitConfirmDialogCancleBtn = wnwd.waitElementByXpath("是否需要预约弹窗取消按钮",
				WnOutpatientXpath.visitConfirmDialogCancleBtn, Framework.defaultTimeoutMin);
		if (visitConfirmDialog != null) {
			wnwd.wnClickElement(visitConfirmDialogCancleBtn, "关闭按钮");
			wnwd.waitNotExistByXpath("等待是否需要预约窗口消失", WnOutpatientXpath.visitConfirmDialog,
					Framework.defaultTimeoutMid);
		}
	}


	public List<String> getOrderNameList() {
		List<WebElement> orderList = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientOrderName);
		List<String> orderNameList = new ArrayList<String>();
		for (WebElement order : orderList) {
			orderNameList.add(order.getText());
		}
		return orderNameList;
	}

	// 给患者添加皮试记录
	// allergyClass 过敏源分类
	// allergyItem 过敏源
	// skinTestResult 过敏结果(阴性/阳性)
	// skinTestDate 确诊时间
	public void addSkinTestRecord(String allergyClass, String allergyItem, String skinTestResult, String skinTestDate) {
		try {
			wnwd.waitElementByXpathAndClick("患者头像", WnOutpatientXpath.patientPhoto, Framework.defaultTimeoutMax);
			wnwd.sleep(1000);
			wnwd.waitElementByXpathAndClick("健康摘要", WnOutpatientXpath.patientDetailHealthSummary,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("有过敏勾选框", "//div[contains(@class,'patient-details')]//label[.='有过敏']",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("添加皮试记录按钮", WnOutpatientXpath.addAllergyRecordIcon,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("过敏源下拉框", WnOutpatientXpath.AllergySourceInput, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("过敏源类型 - " + allergyClass, "//ul//li[.='" + allergyClass + "']",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("过敏源类型 - " + allergyItem, "//ul//li[.='" + allergyItem + "']",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("过敏结果下拉框", WnOutpatientXpath.AllergyResultInput, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("过敏源类型 - " + skinTestResult, "//ul//li[.='" + skinTestResult + "']",
					Framework.defaultTimeoutMax);
			WebElement dateInput = wnwd.waitElementByXpathAndInput("确诊时间下拉框", WnOutpatientXpath.AllergyDateInput,
					skinTestDate, Framework.defaultTimeoutMax);
			dateInput.sendKeys(Keys.ENTER);
			wnwd.waitElementByXpathAndClick("保存按钮", WnOutpatientXpath.AllergySaveButton, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "添加皮试记录失败", e.getMessage());
		}
	}

	public void closePatientDetail() {
		try {
			wnwd.waitElementByXpathAndClick("患者详情关闭按钮", WnOutpatientXpath.patientDetailCloseBtn,
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "关闭患者详情失败", e.getMessage());
		}
	}

	public void checkAllergyWarnBox() {
		try {
			wnwd.waitElementByXpathAndClick("禁止开立弹窗确认按钮", WnOutpatientXpath.AllergyWarnCommitBtn,
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "没有找到禁止开立弹窗", e.getMessage());
		}
	}
	
	//检查异常医嘱及警告提示信息
	public void checkAbnormalOrdersAndWarningTxt(String txt) {
		WebElement abnormalOrder = wnwd.waitElementByXpath("查找异常状态医嘱项", "//span[contains(@class,'warnTxt')]", Framework.defaultTimeoutMax);
		if(abnormalOrder!=null) {
			wnwd.moveToElementByXpath("鼠标移动到警告提示感叹号上", "//i[contains(@class,'win-icon-warning')]", Framework.defaultTimeoutMax);
			WebElement warningTxt = wnwd.waitElementByXpath("警告提示信息", "//div[contains(@class,'warnTxt')]", Framework.defaultTimeoutMax);
			if(!warningTxt.getText().contains(txt)) {
				throw new Error("未找到指定的提示信息!"+txt);
			}
		}else {
			throw new Error("未找到指定的提示信息!"+txt);
		}
	}

	public void diagnoseRelationRecords() {
		WebElement CommitBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCommitBtn,
				Framework.defaultTimeoutMid);
		if (CommitBtn != null) {
			wnwd.waitElementByXpathAndClick("第一个模板", WnOutpatientXpath.outpatientDiagnoseRelationRecordsBoxFirst,
					Framework.defaultTimeoutMax);
			wnwd.wnClickElement(CommitBtn, "诊断关联病历模板弹窗-确定按钮");
		} else {
			WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMid);
			if (cancelBtn != null) {
				wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
			}
		}
	}

	// 诊疗路径默认‘高血压’诊断并开立，如果没有,添加收藏至诊疗路径并开立
	public void findDignoseCheck(String disease) {
		wnwd.waitElementByXpathAndClick("点击诊疗路径","//div[@class='simple-diagnosis-treatment-model-wrap']//div[(contains(@class,'simple-model-tab')) and contains(.,'诊疗路径')]//div[1]",Framework.defaultTimeoutMax);
		// WebElement findlist = wnwd.waitElementByXpath("诊断列表", Data.Dignoselist,
		// Frmcons.defaultTimeoutMax);
		WebElement findlist = wnwd.waitElementByXpath("诊断列表找高血压诊断",
				WnOutpatientXpath.findDignoseCheck.replace("?disease", disease), Framework.defaultTimeoutMax);
		if (findlist != null) {
			wnwd.waitElementByXpathAndClick("点击诊断", WnOutpatientXpath.findDignoseCheck.replace("?disease", disease),
					Framework.defaultTimeoutMax);

		} else {
			wnwd.waitElementByXpathAndClick("诊断搜索图标", WnOutpatientXpath.outpatientDiagnosisSearchButton,
					Framework.defaultTimeoutMax);
//                WebElement inputSearchDiagnosisElement = wnwd.waitElementByXpath("诊断搜索框:", WnOutpatientXpath.outpatientSearchDiagnosisInput, Frmcons.defaultTimeoutMax);
//                wnwd.wnEnterText(inputSearchDiagnosisElement, disease, "搜索诊断:" + disease);
			wnwd.waitElementByXpathAndInput("诊断搜索框", WnOutpatientXpath.outpatientSearchDiagnosisInput, disease,
					Framework.defaultTimeoutMax);
			// span[@class='valueDesc-wrap' and contains (.,'高血压')]
			SdkTools.sleep(1000);
//                wnwd.waitElementByXpathAndClick("诊断收藏",Data.ValueDescwrapSvg,Frmcons.defaultTimeoutMid);
			WebElement ele = wnwd.waitElementByXpath("诊断收藏",
					WnOutpatientXpath.ValueDescwrapSvg.replace("?disease", disease), Framework.defaultTimeoutMid);
			ele.click();
			wnwd.waitElementByXpathAndClick("点击选择诊疗路径收藏", WnOutpatientXpath.diagnosticCollection,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("收藏确定", WnOutpatientXpath.diagnosticCollectionCommitBtm,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("点击选中诊断",
					"//span[@class='el-tooltip valueDesc-wrap' and contains (.,'" + disease + "')]", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("点击确定按钮", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			SdkTools.sleep(1000);
		}

	}

	// 选择所有类型控件并赋值(text=文本类型；select=下拉框)
	public void AllTypeSet() {
		List<WebElement> elelist = wnwd.getElementListByXpath(WnOutpatientXpath.emredittext);
		for (int i = 1; i < elelist.size(); i++) {
			wnwd.setElementAttribute1(elelist.get(i), "test2222");
		}
		List<WebElement> selectElelist = wnwd.getElementListByXpath(WnOutpatientXpath.emreditSelect);
		WebElement ele2 = wnwd.checkElementByXpath("门诊号", "//span[.='门诊号：']", Framework.defaultTimeoutMax);
		for (WebElement ele : selectElelist) {
			wnwd.wnDoubleClickElementByMouse(ele, "hahaha");
			logger.log(3, ele.getText());
			try {
				wnwd.waitElementByXpathAndClick("列表第一个", "//div[@class='emr']//div[@class='wn-editor-select']//li",
						Framework.defaultTimeoutMid);
			} catch (Throwable e) {
				logger.log(1, "点击失败,跳过:" + ele.getText());
			}
			wnwd.wnClickElementByMouse(ele2, "门诊号");

		}
	}

	// 体格检查
	public void physicalExaminationSet() {
		wnwd.waitElementByXpathAndClick("体格检查", WnOutpatientXpath.humanbtn, Framework.defaultTimeoutMid);
		wnwd.sleep(2000);
		List<WebElement> humanBtmlist = wnwd.getElementListByXpath(WnOutpatientXpath.humanbtnIstrue);
		logger.log(1, "体格检查button数量:" + humanBtmlist.size());
		for (WebElement element : humanBtmlist) {
			wnwd.wnClickElement(element, "体格检查button");
			logger.log(1, "名字:" + element.getText());
			if (element.getText().equals("腰围")) {
				wnwd.wnClickElement(element, "取消点击腰围");
			}
		}
		wnwd.waitElementByXpathAndClick("体格检查关闭", WnOutpatientXpath.humanclosebtn, Framework.defaultTimeoutMid);
	}

	// 病历临床段落，根据元素类型逐个赋值
	public void EmrTypeSet() {
		List<WebElement> elelist = wnwd.getElementListByXpath(WnOutpatientXpath.recordsParagraphs);
		logger.log(1, "段落数量:" + elelist.size());

		for (int i = 1; i < elelist.size(); i++) {
			try {
				logger.log(1, "段落:" + elelist.get(i).getAttribute("lastfragmentid"));
				List<WebElement> elelist1 = elelist.get(i)
						.findElements(By.xpath(".//*[@wn-type='text' or @wn-type='select' or @wn-type='number']"));
				logger.log(1, "片段数量:" + elelist1.size());

				for (WebElement ele1 : elelist1) {
					logger.log(1, "片段:" + ele1.getText());
					logger.log(1, "类型:" + ele1.getAttribute("wn-type"));
					if (ele1.getAttribute("wn-type").equals("text")) {
						// 赋值
						WebElement webElement = ele1.findElement(By.xpath(".//span[@class='wn-richtext-value']"));
						wnwd.setElementAttribute1(webElement, "病历测试");
						logger.log(1, "text类型赋值成功");
					} else if (ele1.getAttribute("wn-type").equals("number")) {
						WebElement ele2 = ele1.findElement(By.xpath(".//span[@contenteditable='true']"));
						wnwd.setElementAttribute1(ele2, "1");
						logger.log(1, "number类似赋值成功");
					} else if (ele1.getAttribute("wn-type").equals("select")) {
						WebElement select = null;
						try {
							select = ele1.findElement(By.xpath(".//span[@wn-desc='列举']"));
						} catch (Throwable e) {

						}
						if (select != null) {
							logger.log(1, "跳过列举");
							continue;
						}
						wnwd.wnDoubleClickElementByMouse(ele1, "hahaha");
						wnwd.waitElementByXpathAndClick("列表第一个",
								"//div[@class='emr']//div[@class='wn-editor-select']//li", Framework.defaultTimeoutMid);
						logger.log(1, "select类型选择成功");
					} else {
						// 报错
						logger.log(2, "点击失败,跳过:" + ele1.getText());
					}

				}
			} catch (Throwable e) {

			}
			logger.log(3, "未找到下拉选择数据联动后的Xpath");

		}
		physicalExaminationSet();
	}

	// 签署病例并检验
	public void emrSignoffAndcheck(Boolean ifCheck) {
		if (ifCheck) {
			String element = wnwd.getElementByXpath(WnOutpatientXpath.diagnosticItemName).getText();
			System.out.println("" + element);

			String elemen = wnwd.getElementByXpath(WnOutpatientXpath.diagnosisOfRecords).getText();
			System.out.println("" + elemen);

			logger.log(1, "诊断内容" + element);
			logger.log(1, "病历诊断内容" + elemen);
			logger.assertFalse(!element.equals(elemen), "病历诊断内容不一致", element + "\n" + elemen);
		}
		wnwd.waitElementByXpathAndClick("点击签署病历", WnOutpatientXpath.outpatientEmrSignButton, Framework.defaultTimeoutMax);

		WebElement findele = wnwd.waitElementByXpath("提示内容", WnOutpatientXpath.emrError.replace("chief", "主诉"),
				Framework.defaultTimeoutMin);
		if (findele != null) {
			wnwd.waitElementByXpathAndClick("提示内容关闭", WnOutpatientXpath.emrSignClose, Framework.defaultTimeoutMax);
			List<WebElement> emrfirstele = wnwd.getElementListByXpath(WnOutpatientXpath.emredittext);
			wnwd.setElementAttribute1(emrfirstele.get(0), "复查配药");
		}
//		saveEmr();
		emrSignoff();
	}

	// 根据诊断选择病历模板后，判断模板段落是否可为空提交（门诊诊断）
	public String diagnoseRelationRecords1() {
		String MrtName = "";
		WebElement CommitBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCommitBtn,
				Framework.defaultTimeoutMid);
		if (CommitBtn != null) {
			WebElement eleFirst = wnwd.waitElementByXpathAndClick("第一个模板",
					WnOutpatientXpath.outpatientDiagnoseRelationRecordsBoxFirst, Framework.defaultTimeoutMax);
			MrtName = eleFirst.getText();
			System.out.println("" + MrtName);

			logger.log(1, "模板名字" + MrtName);
			wnwd.wnClickElement(CommitBtn, "诊断关联病历模板弹窗-确定按钮");
		} else {
			WebElement cancelBtn = wnwd.waitElementByXpath("诊断关联病历模板弹窗", WnOutpatientXpath.dignoseRelateEmrCancelBtn,
					Framework.defaultTimeoutMid);
			if (cancelBtn != null) {
				wnwd.wnClickElement(cancelBtn, "诊断关联病历模板弹窗-取消按钮");
			}
		}
		return MrtName;
	}

	// 登录portal时，根据用户名密码获取当前就诊医生名字
	public void wnlogin1(String username, String password) {
		wnwd.waitElementByXpathAndInput("用户名输入框", WnOutpatientXpath.loginUsernameInput, username,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("密码输入框", WnOutpatientXpath.loginPasswordInput, password,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("Portal页面标识", WnOutpatientXpath.portalPageFlag, Framework.defaultTimeoutMax);

		// 数据库判断当前登录医生 相关权限
	}

	// 病历-历史病历（默认选择本科室一个月第一天历史就诊病历）
	public void historyEmrRecord() {
		try {

			wnwd.waitElementByXpathAndClick("病历记录", WnOutpatientXpath.emrrecord, Framework.defaultTimeoutMid);
			wnwd.waitElementByXpathAndClick("历史就诊", WnOutpatientXpath.emrrecordhistory, Framework.defaultTimeoutMid);
			wnwd.sleep(2000);
			WebElement historyele = wnwd.getElementByXpath(WnOutpatientXpath.assisantRecordFirst);
			if (historyele != null) {
				List<WebElement> search_result = wnwd.waitElementListByXpath(
						WnOutpatientXpath.historyEmrCheckboxIsChecked, Framework.defaultTimeoutMax);
				for (WebElement checkedBox : search_result) {
					wnwd.wnClickElement(checkedBox, "取消勾选 " + checkedBox.getText());
				}

				List<WebElement> elelist = wnwd.getElementListByXpath(WnOutpatientXpath.assisantRecordParagraph);
				logger.log(1, "历史病历段落数量:" + elelist.size());
				wnwd.sleep(Framework.defaultTimeoutMin);
				for (int i = 0; i < elelist.size(); i++) {
					WebElement title = elelist.get(i)
							.findElement(By.xpath(".//span[@class='editor-paragraph__title']/.."));
					System.out.println(title.getText());
					logger.log(1, "历史病历段落名字:" + title.getText());
					if (title.getText().equals("现病史") || title.getText().equals("既往史")
							|| title.getText().equals("个人史")) {
						logger.log(2, "找到了");
						WebElement selectBox = title.findElement(By.xpath(".//label[contains(@class,'el-checkbox')]"));
						wnwd.wnClickElement(selectBox, "勾选 :" + title.getText());
						String texthistory = elelist.get(i)
								.findElement(By.xpath(".//p[@class='editor-paragraph__content']")).getText();
						System.out.println("" + texthistory);
						wnwd.sleep(Framework.defaultTimeoutMin);
					}
				}
				wnwd.waitElementByXpathAndClick("历史病历段落导入按钮", WnOutpatientXpath.historyRecordEditorBtn,
						Framework.defaultTimeoutMid);
				SdkTools.sleep(Framework.defaultTimeoutMin);
				historyEmrRecordEquals();
			}
		} catch (Throwable e) {
			throw new Error("找不到xpath:" + e.getMessage());
		}
	}

	// 病历导入后内容对比判断（当前只对比现代史、既往史、个人史）

	public void historyEmrRecordEquals() {
		List<WebElement> elelist = wnwd.getElementListByXpath(WnOutpatientXpath.assisantRecordParagraph);
		List<WebElement> emrlist = wnwd.getElementListByXpath(WnOutpatientXpath.recordsParagraphs);
		Map<String, String> eleMap = new HashMap<>();
		Map<String, String> emrMap = new HashMap<>();

		for (int i = 0; i < elelist.size(); i++) {
			WebElement title = elelist.get(i).findElement(By.xpath(".//span[@class='editor-paragraph__title']/.."));
			if (title.getText().equals("现病史") || title.getText().equals("既往史") || title.getText().equals("个人史")) {
				String texthistory = elelist.get(i).findElement(By.xpath(".//p[@class='editor-paragraph__content']"))
						.getText();
				System.out.println("历史病历内容：" + texthistory);
				eleMap.put(title.getText(), texthistory);
			}

		}
		for (int i = 0; i < emrlist.size(); i++) {
			WebElement editortitle = emrlist.get(i)
					.findElement(By.xpath(".//span[@class='editor-paragraph__title']/.."));
			if (editortitle.getText().equals("现病史") || editortitle.getText().equals("既往史")
					|| editortitle.getText().equals("个人史")) {
				String textemr = emrlist.get(i).findElement(By.xpath(".//p[@class='editor-paragraph__content']"))
						.getText();
				System.out.println("当前病历内容：" + textemr);
				emrMap.put(editortitle.getText(), textemr);
			}
		}

		for (Map.Entry<String, String> entry : eleMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (emrMap.containsKey(key) && (value != null && value.equals(emrMap.get(key)))) {
				System.out.println(key + " 一致");
			} else {
				System.out.println(key + " 不一致");
			}
			emrMap.remove(key);
		}
		System.out.println(emrMap);
		logger.log(1, "病历内容对比一致");
		wnwd.waitElementByXpathAndClick("关闭书写助手页面", "//div[@class='emr__menu__title']//i", Framework.defaultTimeoutMid);
	}

	// 获取病历处置治疗段落内容
	private String readEmr() {
		List<WebElement> emrlist = wnwd.getElementListByXpath(WnOutpatientXpath.recordsParagraphs);
		for (int i = 0; i < emrlist.size(); i++) {
			WebElement editortitle = emrlist.get(i)
					.findElement(By.xpath(".//span[@class='editor-paragraph__title']/.."));
			if (editortitle.getText().equals("处置治疗")) {
				return emrlist.get(i).findElement(By.xpath(".//p[@class='editor-paragraph__content']")).getText();
			}
		}
		return "";
	}

	// 病历-书写助手医嘱插入
	public void writingAssistantForMedicAladvice() {
		// 书写助手-医嘱
		wnwd.waitElementByXpathAndClick("书写助手", WnOutpatientXpath.emrwritingAssistant, Framework.defaultTimeoutMid);
		wnwd.waitElementByXpathAndClick("医嘱", WnOutpatientXpath.emrrecordMedicAladvice, Framework.defaultTimeoutMid);
		SdkTools.sleep(Framework.defaultTimeoutMin);
		WebElement MedicAladviceSelect = wnwd.getElementByXpath(WnOutpatientXpath.MedicAladviceForSelect);
		logger.log(1, "找到了");

		if (MedicAladviceSelect != null) {
			List<WebElement> selectElelist = wnwd
					.getselectElementListByXpath(WnOutpatientXpath.MedicAladviceForSelectList);
			logger.log(1, "医嘱类别数量:" + selectElelist.size());
			wnwd.sleep(2000);
			// 每个类型医嘱选择并插入
			WebElement selectBtnEle = MedicAladviceSelect
					.findElement(By.xpath(WnOutpatientXpath.MedicAladviceForSelectBtn));
			for (int i = 0; i < selectElelist.size(); i++) {
				wnwd.wnClickElement(selectBtnEle, "点击下拉框");
				wnwd.sleep(2000);
				wnwd.wnClickElement(selectElelist.get(i), "点击医嘱类别");
				System.out.println(selectElelist.get(i).getText());
				WebElement history = wnwd.getElementByXpath(WnOutpatientXpath.historyMedicalAdvice);
				if (history != null) {
					history.click();
					String text = history.getText();
					if (text != null) {
						text = text.replace("全选\n", "");
					}
					// List<WebElement> all =
					// history.findElements(By.xpath("//div[@class='history-list']//span[@class='el-checkbox__label']"));
					String fristEmr = readEmr();
					System.out.println(text);
					boolean f = fristEmr.contains(text);
					System.out.println(f);
					WebElement insertBtn = wnwd.getElementByXpath(WnOutpatientXpath.historyBtn);
					wnwd.wnClickElement(insertBtn, "点击插入按钮");
					String senced = readEmr();
					boolean s = senced.contains(text);
					System.out.println(s);
					if ((!f) && s) {
						System.out.println("医嘱插入成功");
						logger.log(1, "医嘱插入成功");
						logger.log(1, text);
					}

				}
				wnwd.sleep(2000);
			}

		}
	}

	// 获取pdf文件内容
	public String getPDFText(String url, File file) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
				.readTimeout(60000, TimeUnit.MILLISECONDS).build();
		MediaType mediaType = MediaType.parse("image/gif");
		RequestBody body = RequestBody.create(mediaType, file);
		Request request = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "image/gif")
				.build();
		Response response = client.newCall(request).execute();
		String responseMsg = response.body().string();
		System.out.println("---查看返回：" + responseMsg);
		JsonParser parser = new JsonParser();
		JsonArray contentJson = parser.parse(responseMsg).getAsJsonArray();
		String data = "";
		for (JsonElement json : contentJson) {
			System.out.println(json.getAsJsonObject());
			if (json.getAsJsonObject().has("content")) {

				data += json.getAsJsonObject().get("content").getAsString();
			} else if (json.getAsJsonObject().has("words_result")) {
				JsonArray words = json.getAsJsonObject().get("words_result").getAsJsonArray();
				for (JsonElement word : words) {
					data += word.getAsJsonObject().get("words").getAsString();
				}
			} else {
				throw new Error("返回json格式不对");
			}

		}
		data = data.replace("（", "").replace("）", "").replace("(", "").replace(")", "").replace(" ", "")
				.replace("\r", "").replace("\n", "").replace(";", "");
		return data;
	}

	// 如果弹出打印确认框,点击打印按钮
	public void printConfirm() {
		WebElement printSetUpForm = wnwd.waitElementByXpath("打印设置框", "//div[@class='el-dialog__body']",
				Framework.defaultTimeoutMid);
		if (printSetUpForm != null) {
			wnwd.waitElementByXpathAndClick("打印设置确认按钮",
					"//div[@class='el-dialog__body']//span[contains(.,'确 定') or contains(.,'打印')]",
					Framework.defaultTimeoutMax);
		}
		WebElement printConfirmForm = wnwd.waitElementByXpath("打印确认框", WnOutpatientXpath.printConfirmForm,
				Framework.defaultTimeoutMid);
		if (printConfirmForm != null) {
			wnwd.waitElementByXpathAndClick("打印确认按钮", WnOutpatientXpath.printConfirmButton, Framework.defaultTimeoutMax);
		}
	}

	// 搜索处置模板
	public WebElement searchTemplate(String templateName) {
		wnwd.waitElementByXpathAndClick("模板搜索按钮",
				"//div[contains(@class,'disposal__search--select--list--item') and .='模板']", Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		wnwd.checkElementByXpath("医嘱搜索结果", WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		wnwd.waitElementByXpathAndInput("医嘱搜索框", WnOutpatientXpath.outpatientSearchOrderInput, templateName,
				Framework.defaultTimeoutMax);
		WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		// 查找所有搜索结果
		List<WebElement> searchResults = resultBox
				.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
		// 没有搜索结果返回null
		if (searchResults == null || searchResults.size() == 0) {
			return null;
		}
		// 有搜索结果返回第一个
		return searchResults.get(0);
	}

	// 添加处置模板
	public void addTemplate(String templateName) {
		wnwd.waitElementByXpathAndClick("存为模板按钮", "//div[@id='disposal']//button[.='存为模板']", Framework.defaultTimeoutMax);
		if (!Data.DispositionTemplateType.equals("个人"))
			wnwd.waitElementByXpathAndClick("模板的所属范围单选框", Data.DispositionTemplateTypeRadio, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("模板名称输入框", "//div[@aria-label='另存为模板']//input", templateName,
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("模板所属范围(个人)","//div[@class='el-radio-group']//label[contains(@class,'el-radio')and .='个人']//span",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("存为模板按钮", "//div[@aria-label='另存为模板']//button[.='确 定']",
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待保存模板窗口消失", "//div[@aria-label='另存为模板']", Framework.defaultTimeoutMax);
	}

	// 引用处置模板
	public void quoteTemplate(WebElement template) {
		try {
			String templateName = template.getText();
			wnwd.wnClickElement(template, "医嘱模板:" + templateName);
			wnwd.waitElementByXpathAndClick("本人模板","//div[@class='template-filter']//label[@class='el-radio']/..//span[contains(.,'本人')]",Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("模板引用按钮",
					"//ul[contains(@class,'template-detail')]//span[contains(@class,'dealBtn_quote')]//i",
					Framework.defaultTimeoutMax);
			addDiagnoseIfNeed();
			own_expense(null);
		} catch (Throwable e) {
			logger.assertFalse(true, "引用处置模板失败:" + e.getMessage());
		}
	}

	// 签署前检查医嘱(没有未签署医嘱或有异常医嘱报错)
	public void checkOrderState() {
		try {
			List<WebElement> unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMax);
			if (unSignOrders == null || unSignOrders.size() == 0) {
				throw new Error("引用后没有找到未签署医嘱!");
			}
			List<String> errOrders = new ArrayList<>();
			for (WebElement order : unSignOrders) {
				if (order.findElements(By.xpath(".//i[contains(@class,'fail') or contains(@class,'warning')]"))
						.size() > 0) {
					errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
				}
			}
			if (errOrders.size() > 0) {
				wnwd.getScreenShot("医嘱列表异常");
				throw new Error("发现异常医嘱:" + errOrders);
			}
		} catch (Throwable e) {
			logger.assertFalse(true, "签署前检查医嘱列表不通过:" + e.getMessage());
		}
	}

	// 添加诊间预约
	public void addAppointment() {
		try {
			wnwd.waitElementByXpathAndClick("诊间预约", "//aside[contains(@class,'outpatient-aside')]//li[.='诊间预约']",
					Framework.defaultTimeoutMax);
			WebElement element = wnwd.checkElementByXpath("剩余数量",
					"//div[contains(@class,'clinic_appointment')]//div[contains(@class,'source-list')]//div[contains(@class,'el-table__body-wrapper')]//table//td[4]",
					60000);
			Integer remainNum = null;
			try {
				remainNum = Integer.valueOf(element.getText());
			} catch (Throwable e) {
				throw new Error("获取诊间预约剩余数量失败:" + element.getText() + "\n" + e.getMessage());
			}
			WebElement printCheckBox = wnwd.checkElementByXpath("打印预约单勾选框",
					"//div[contains(@class,'clinic_appointment')]//label[contains(@class,'el-checkbox') and .='打印预约单']",
					Framework.defaultTimeoutMax);
			if (printCheckBox.getAttribute("class").contains("is-checked")) {
				wnwd.wnClickElement(printCheckBox, "取消勾选");
				SdkTools.sleep(500);
			}
			logger.assertFalse(printCheckBox.getAttribute("class").contains("is-checked"), "取消勾选打印失败");
			wnwd.waitElementByXpathAndClick("确认预约",
					"//div[contains(@class,'clinic_appointment')]//button[contains(.,'确认预约')]",
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("预约成功提示", WnOutpatientXpath.outpatientSucMsgForreserve, Framework.defaultTimeoutMax);
//            wnwd.waitNotExistByXpath("等待诊间预约敞口消失", "//div[contains(@class,'clinic_appointment')]", Frmcons.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("诊间预约", "//aside[contains(@class,'outpatient-aside')]//li[.='诊间预约']",
					Framework.defaultTimeoutMax);
			element = wnwd.checkElementByXpath("剩余数量",
					"//div[contains(@class,'clinic_appointment')]//div[contains(@class,'source-list')]//div[contains(@class,'el-table__body-wrapper')]//table//td[4]",
					60000);
			Integer remainNum2 = null;
			try {
				remainNum2 = Integer.valueOf(element.getText());
			} catch (Throwable e) {
				throw new Error("获取诊间预约剩余数量失败:" + element.getText() + "\n" + e.getMessage());
			}
			logger.assertFalse(!(remainNum - remainNum2 == 1),
					"诊间预约剩余数量变化不对(预约前:" + remainNum + "/预约后:" + remainNum2 + ")");
		} catch (Throwable e) {
			logger.assertFalse(true, "诊间预约失败:" + e.getMessage());
		}
	}

	// 申请转诊
	public void addReferral(String subjectName, String patientName) {
		try {
			wnwd.waitElementByXpathAndClick("转诊菜单", "//aside[contains(@class,'outpatient-aside')]//li[.='转诊']",
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("转诊窗口患者姓名",
					"//div[@id='clinic_appointment']//div[contains(@class,'encounter__msg')]//span[contains(.,'"
							+ patientName + "')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("转诊科室输入框",
					"//div[@id='clinic_appointment']//input[@placeholder='请选择科室' and not(@disabled)]", subjectName,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("转诊科室选择框", "//ul//li[.='" + subjectName + "']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("转诊科室输入框",
					"//div[@id='clinic_appointment']//input[@placeholder='请选择科目' and not(@disabled)]", subjectName,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("转诊科室选择框", "//ul//li[.='" + subjectName + "']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("转诊理由输入框",
					"//div[@id='clinic_appointment']//div[contains(@class,'select-subject') and .='转诊理由']//textArea",
					"UI自动化转诊测试", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("转诊提交", "//div[@id='clinic_appointment']//button[contains(.,'提交并打印')]",
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("转诊申请成功提示", WnOutpatientXpath.outpatientSucMsg.replace("?1", "转诊成功"),
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("转诊列表",
					"//div[@id='clinic_appointment']//tr[contains(.,'转诊中') and contains(.,'" + subjectName
							+ "') and contains(.,'UI自动化转诊测试') and contains(.,'" + patientName + "')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("关闭转诊窗口图标",
					"//div[@id='clinic_appointment']//i[contains(@class,'win-icon-close')]", Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("未就诊状态", "//div[contains(@class,'patient-info-placeholder')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "转诊申请失败:" + e.getMessage());
		}
	}

	// 接受转诊
	public void acceptReferral(String subjectName, String patientName) {
		try {
			wnwd.checkElementByXpath("转诊消息框",
					"//div[contains(@class,'referralList-tips-dialog') and contains(.,'转诊提醒') and contains(.,'"
							+ patientName + "')]",
					80000);
			wnwd.waitElementByXpathAndClick("转诊确认签收",
					"//div[contains(@class,'referralList-tips-dialog') and contains(.,'转诊提醒') and contains(.,'"
							+ patientName + "')]//button[.='确认签收']",
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("转诊签收成功提示", WnOutpatientXpath.outpatientSucMsg.replace("?1", "签收转诊成功"),
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("点击候诊按钮", WnOutpatientXpath.waitListBotton, Framework.defaultTimeoutMax);
			cancelCheckboxInPatientList();
			wnwd.waitElementByXpathAndInput("患者搜索框", WnOutpatientXpath.outpatientPatientSearchInput, patientName,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("患者搜索结果", "//div[@id='header-search']//tr[contains(.,'" + patientName
					+ "') and contains(.,'" + subjectName + "') and contains(.,'未就诊')]", Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("患者转诊标识", "//div[@id='header-search']//tr[contains(.,'" + patientName
					+ "') and contains(.,'" + subjectName + "') and contains(.,'未就诊')]//div[@class='cell']//span[contains(@class,'win-userTag list')and contains(.,'转诊')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "转诊接受失败:" + e.getMessage());
		}
	}

	// 取消就诊
	public void encCancel() {
		try {
			wnwd.waitElementByXpathAndClick("取消就诊",
					"//div[@id='patient_banner']//span[contains(@class,'setting-item-span') and .='取消就诊']",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("取消就诊确定","//div[@class='el-message-box']//div[@class='el-message-box__btns']//button[@class='el-button el-button--default el-button--small el-button--primary ']",Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("未就诊状态", "//div[contains(@class,'patient-info-placeholder')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "取消就诊失败:" + e.getMessage());
		}
	}

	// 修改个人信息
	public void updateBannerPersonInfo(String phoneNum, String country) {
		try {
			wnwd.waitElementByXpathAndClick("患者头像", WnOutpatientXpath.patientPhoto, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("个人信息", WnOutpatientXpath.patientDetailPersonInfo,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("手机号输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and .='联系方式']//input",
					phoneNum, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("国籍输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'国籍')]//input",
					country, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("选择国籍", "//ul//li[.='" + country + "']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("个人信息确定", "//div[contains(@class,'patient-details')]//button[.='确 定']",
					Framework.defaultTimeoutMax);
//			wnwd.checkElementByXpath("国籍:"+country, "//div[@id='patient_banner']//div[contains(@class,'nationalityDesc') and contains(.,'国籍')]//div[.='中国']", Frmcons.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待患者信息框关闭", WnOutpatientXpath.patientDetail, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "修改个人信息失败:" + e.getMessage());
		}
	}

	// 修改健康摘要
	public void updateBannerHealthSummry(String height, String weight, String allergyClass, String allergyItem,
			String skinTestResult, String skinTestDate) {
		try {
			addSkinTestRecord(allergyClass, allergyItem, skinTestResult, skinTestDate);
			wnwd.waitElementByXpathAndInput("身高输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'身高')]//input",
					height, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("体重输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'体重')]//input",
					weight, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("个人信息确定", "//div[contains(@class,'patient-details')]//button[.='确 定']",
					Framework.defaultTimeoutMax);
//			wnwd.checkElementByXpath("保存成功提示", WnOutpatientXpath.outpatientSucMsg.replace("?1", "保存成功"), Frmcons.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待患者信息框关闭", WnOutpatientXpath.patientDetail, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "修改健康摘要失败:" + e.getMessage());
		}
	}

	// 修改就诊信息
	public void updateBannerEncounterInfo(String contactName, String relationship, String phoneNum) {
		try {
			wnwd.waitElementByXpathAndClick("患者头像", WnOutpatientXpath.patientPhoto, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("就诊信息", WnOutpatientXpath.patientDetailEncounterInfo,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("代配人输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'代配人')]//input",
					contactName, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("患者关系输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'患者关系')]//input",
					relationship, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("选择护着关系", "//ul//li[.='" + relationship + "']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("联系方式输入框",
					"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'联系方式')]//input",
					phoneNum, Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("个人信息确定", "//div[contains(@class,'patient-details')]//button[.='确 定']",
					Framework.defaultTimeoutMax);
//			wnwd.checkElementByXpath("保存成功提示", WnOutpatientXpath.outpatientSucMsg.replace("?1", "保存成功"), Frmcons.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待患者信息框关闭", WnOutpatientXpath.patientDetail, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "修改就诊信息失败:" + e.getMessage());
		}
	}

	// 获取西药信息 (基本信息、药品名称、剂量、给药途径、频次、费用、药房)
	public Map<String, ArrayList<String>> getDrugInfo(String drugName) {
		wnwd.waitElementByXpathAndClick("医嘱" + drugName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + drugName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		String dose = wnwd
				.waitElementByXpath("剂量",
						WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
								"contains(.,'开立') and contains(.,'" + drugName + "')")
								+ "//span[@class='dose textAlign__right overWidthTxt']",
						Framework.defaultTimeoutMax)
				.getText();
		String method = wnwd.waitElementByXpath("给药途径",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + drugName + "')") + "//span[@class='method overWidthTxt']",
				Framework.defaultTimeoutMax).getText();
		String freqCode = wnwd.waitElementByXpath("频次",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + drugName + "')") + "//span[@class='freq overWidthTxt']",
				Framework.defaultTimeoutMax).getText();
		String freq = "";
		try {
			freq = Data.freq.get(freqCode);
		} catch (Throwable e) {
			throw new Error("找不到" + freqCode + "对应的频次信息");
		}

		String totalPrice = getTotalCost();
		String pharmacy = wnwd.waitElementByXpath("药房", "//span[@class='phaemac-style']", Framework.defaultTimeoutMax)
				.getText();
		pharmacy = pharmacy.substring(3, pharmacy.length());
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);

		ArrayList<String> drugInfo = new ArrayList<>();
		ArrayList<String> guideInfo = new ArrayList<>();
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查西药名称")) {
			guideInfo.add(Data.test_prescribe_drug);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查西药名称")) {
			drugInfo.add(Data.test_prescribe_drug);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查临床诊断")) {
			guideInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查临床诊断")) {
			drugInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查科室")) {
			guideInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查科室")) {
			drugInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查剂量")) {
			guideInfo.add(dose);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查剂量")) {
			drugInfo.add(dose);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查给药途径")) {
			guideInfo.add(method);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查给药途径")) {
			drugInfo.add(method);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查频次")) {
			guideInfo.add(freq);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查频次")) {
			drugInfo.add(freq);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查费用")) {
			guideInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查费用")) {
			drugInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查药房")) {
			guideInfo.add(pharmacy);
		}
		if (SdkTools.analyticConfiguration(Data.drugRecipeReportConfig, "检查药房")) {
			drugInfo.add(pharmacy);
		}
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("guideInfo", guideInfo);
		map.put("drugInfo", drugInfo);
		return map;

	}

	// 获取中药信息（姓名、临床诊断、药品名称、剂量、单位、给药途径、药房、费用）
	public Map<String, ArrayList<String>> getHerbInfo(String herbName) {

		wnwd.waitElementByXpathAndClick("医嘱" + herbName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + herbName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		String method = wnwd.waitElementByXpath("给药途径",
				"//div[@class='el-form-item is-required el-form-item--mini']//input[1]", Framework.defaultTimeoutMax)
				.getAttribute("value");
		String totalPrice = getTotalCost();
		String pharmacy = wnwd.waitElementByXpath("中药药房", "//span[@class='phaemac-style']", Framework.defaultTimeoutMax)
				.getText();
		pharmacy = pharmacy.substring(3, pharmacy.length());
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);

		ArrayList<String> herbInfo = new ArrayList<>();
		ArrayList<String> guideInfo = new ArrayList<>();
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查中草药名称")) {
			guideInfo.add(Data.test_prescribe_herb);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查中草药名称")) {
			herbInfo.add(Data.test_prescribe_herb);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查临床诊断")) {
			guideInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查临床诊断")) {
			herbInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查科室")) {
			guideInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查科室")) {
			herbInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查给药途径")) {
			guideInfo.add(method);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查给药途径")) {
			herbInfo.add(method);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查费用")) {
			guideInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查费用")) {
			herbInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查药房")) {
			guideInfo.add(pharmacy);
		}
		if (SdkTools.analyticConfiguration(Data.herbRecipeReportConfig, "检查药房")) {
			herbInfo.add(pharmacy);
		}

		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("guideInfo", guideInfo);
		map.put("herbInfo", herbInfo);
		return map;
	}

	// 获取检查信息 (门诊号、姓名、性别、年龄、临床诊断、执行科室、费用)
	public Map<String, ArrayList<String>> getExamInfo(String examName) {
		wnwd.waitElementByXpathAndClick("医嘱" + examName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + examName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		String executiveDepartment = wnwd.waitElementByXpath("执行科室",
				"//span[contains(text(),'执行科室')]/following-sibling::*//input", Framework.defaultTimeoutMax)
				.getAttribute("value");
		String totalPrice = getTotalCost();
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);

		ArrayList<String> guideInfo = new ArrayList<>();
		ArrayList<String> examInfo = new ArrayList<>();

		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查检查名称")) {
			guideInfo.add(Data.test_prescribe_exam);
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查检查名称")) {
			examInfo.add(Data.test_prescribe_exam);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查临床诊断")) {
			guideInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查临床诊断")) {
			examInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查科室")) {
			guideInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查科室")) {
			examInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查执行科室")) {
			guideInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查执行科室")) {
			examInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查费用")) {
			guideInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.examOrderReportConfig, "检查费用")) {
			examInfo.add(totalPrice);
		}
		System.out.println(examInfo);

		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("guideInfo", guideInfo);
		map.put("examInfo", examInfo);
		return map;
	}

	// 获取检验信息 (门诊号、姓名、性别、年龄、临床诊断、执行科室、费用)
	public Map<String, ArrayList<String>> getLabtestInfo(String labtestName) {
		wnwd.waitElementByXpathAndClick("医嘱" + labtestName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + labtestName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		String executiveDepartment = wnwd.waitElementByXpath("执行科室",
				"//span[contains(text(),'执行科室')]/following-sibling::*//input", Framework.defaultTimeoutMax)
				.getAttribute("value");
		String specimen = wnwd.waitElementByXpath("标本",
				"//div[@class='cell']//div[@class='el-input el-input--small el-input--suffix']//input",
				Framework.defaultTimeoutMax).getAttribute("value");
		String totalPrice = getTotalCost();
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);

		ArrayList<String> guideInfo = new ArrayList<>();
		ArrayList<String> labtestInfo = new ArrayList<>();

		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查检验名称")) {
			guideInfo.add(Data.test_prescribe_lab);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查检验名称")) {
			labtestInfo.add(Data.test_prescribe_lab);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查临床诊断")) {
			guideInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查临床诊断")) {
			labtestInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查科室")) {
			guideInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查科室")) {
			labtestInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查执行科室")) {
			guideInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查执行科室")) {
			labtestInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查标本")) {
			guideInfo.add(specimen);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查标本")) {
			labtestInfo.add(specimen);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查费用")) {
			guideInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.labTestOrderReportConfig, "检查费用")) {
			labtestInfo.add(totalPrice);
		}
		System.out.println(labtestInfo);
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("guideInfo", guideInfo);
		map.put("labTestInfo", labtestInfo);
		return map;
	}

	// 获取治疗信息 (门诊号、姓名、性别、年龄、临床诊断、执行科室、费用)
	public Map<String, ArrayList<String>> getTreatInfo(String treatName) {
		wnwd.waitElementByXpathAndClick("医嘱" + treatName + "编辑按钮",
				WnOutpatientXpath.outpatientUnsignedOrder.replace("contains(.,'开立')",
						"contains(.,'开立') and contains(.,'" + treatName + "')")
						+ WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMax);
		String executiveDepartment = wnwd.waitElementByXpath("执行科室",
				"//label[contains(text(),'执行科室')]/following-sibling::*//input", Framework.defaultTimeoutMax)
				.getAttribute("value");
		String totalPrice = getTotalCost();
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
				Framework.defaultTimeoutMax);

		ArrayList<String> guideInfo = new ArrayList<>();
		ArrayList<String> treatInfo = new ArrayList<>();
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查治疗名称")) {
			guideInfo.add(Data.test_prescribe_treat);
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查治疗名称")) {
			treatInfo.add(Data.test_prescribe_treat);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查临床诊断")) {
			guideInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查临床诊断")) {
			treatInfo.add(Data.test_disease);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查科室")) {
			guideInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查科室")) {
			treatInfo.add(Data.test_select_subject);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查执行科室")) {
			guideInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查执行科室")) {
			treatInfo.add(executiveDepartment);
		}
		if (SdkTools.analyticConfiguration(Data.guideSheetReportConfig, "检查费用")) {
			guideInfo.add(totalPrice);
		}
		if (SdkTools.analyticConfiguration(Data.treatmentOrderReportConfig, "检查费用")) {
			treatInfo.add(totalPrice);
		}
		System.out.println(treatInfo);

		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("guideInfo", guideInfo);
		map.put("treatInfo", treatInfo);
		return map;
	}

	//打印病假单并获取相关信息
	public ArrayList<String> writeSickLeaveForm() {
		ArrayList<String> sickLeaveInfo = new ArrayList<>();
		wnwd.waitElementByXpathAndClick("点击单据", "//span[contains(text(),'单据')]", Framework.defaultTimeoutMax).getText();
		wnwd.waitElementByXpathAndClick("点击病假单", "//span[contains(text(),'病假单')]", Framework.defaultTimeoutMax).getText();

		WebElement inputTelephoneNumberElement = wnwd.waitElementByXpath("电话号码输入框",
				"//div[@class='sickLeave-bill']//label[contains(text(),'联系方式')]/following-sibling::div//input",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputTelephoneNumberElement, Data.telephoneNumber, "电话号码输入框");
		if (SdkTools.analyticConfiguration(Data.sickLeaveReportConfig, "检查电话号码")) {
			sickLeaveInfo.add(Data.telephoneNumber);
		}
		WebElement inputWorkplaceElement = wnwd.waitElementByXpath("工作单位输入框",
				"//div[@class='sickLeave-bill']//label[contains(text(),'工作单位')]/following-sibling::div//input",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputWorkplaceElement, Data.workplace, "工作单位输入框");
		if (SdkTools.analyticConfiguration(Data.sickLeaveReportConfig, "检查工作地点")) {
			sickLeaveInfo.add(Data.workplace);
		}
		WebElement inputAdviseElement = wnwd.waitElementByXpath("建议输入框",
				"//div[@class='sickLeave-bill']//label[contains(text(),'建议')]/following-sibling::div//textarea",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputAdviseElement, Data.advise, "建议输入框");
		if (SdkTools.analyticConfiguration(Data.sickLeaveReportConfig, "检查建议说明")) {
			sickLeaveInfo.add(Data.advise);
		}
		WebElement dayElement = wnwd.waitElementByXpath("天数输入框",
				"//div[contains(text(),'休假')]/parent::*/parent::*/following-sibling::*//div[@class='el-input el-input--small']//input",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(dayElement, "5", "天数输入框");
		wnwd.wnDoubleClickElementByMouse(dayElement, "点击病假开始时间");
		String startDate = wnwd.waitElementByXpath("病假开始时间", "//input[@placeholder='开始日期']", Framework.defaultTimeoutMax)
				.getAttribute("value");
		sickLeaveInfo.add(startDate);
		if (SdkTools.analyticConfiguration(Data.sickLeaveReportConfig, "检查开始日期")) {
			sickLeaveInfo.add(startDate);
		}
		System.out.println(sickLeaveInfo);
		wnwd.waitElementByXpathAndClick("提交并打印按钮", "//span[contains(text(),'提交并打印')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("打印按钮", "//div[@class='history-detail']//span[contains(text(),'打印')]", Framework.defaultTimeoutMax);
		printConfirm();
		return sickLeaveInfo;
	}

	// 开立疾病证明单并获取信息
	public ArrayList<String> writeDiseaseCertificate() {
		ArrayList<String> diseaseCertificateInfo = new ArrayList<>();
		wnwd.waitElementByXpathAndClick("点击单据", "//span[contains(text(),'单据')]", Framework.defaultTimeoutMax).getText();
		wnwd.waitElementByXpathAndClick("点击疾病证明单", "//span[contains(text(),'疾病证明单')]", Framework.defaultTimeoutMax)
				.getText();
		WebElement inputTelephoneNumberElement = wnwd.waitElementByXpath("电话号码输入框",
				"//div[@class='desease-bill']//label[contains(text(),'联系方式')]/following-sibling::div//input",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputTelephoneNumberElement, Data.telephoneNumber, "电话号码输入框");
		if (SdkTools.analyticConfiguration(Data.diseaseCertificateReportConfig, "检查电话号码")) {
			diseaseCertificateInfo.add(Data.telephoneNumber);
		}
		WebElement inputWorkplaceElement = wnwd.waitElementByXpath("工作单位输入框",
				"//div[@class='desease-bill']//label[contains(text(),'工作单位')]/following-sibling::div//input",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputWorkplaceElement, Data.workplace, "工作单位输入框");
		if (SdkTools.analyticConfiguration(Data.diseaseCertificateReportConfig, "检查工作地点")) {
			diseaseCertificateInfo.add(Data.workplace);
		}
		WebElement inputAdviseElement = wnwd.waitElementByXpath("说明输入框",
				"//div[@class='desease-bill']//label[contains(text(),'说明')]/following-sibling::div//textarea",
				Framework.defaultTimeoutMax);
		wnwd.wnEnterText(inputAdviseElement, Data.advise, "说明输入框");
		if (SdkTools.analyticConfiguration(Data.diseaseCertificateReportConfig, "检查建议说明")) {
			diseaseCertificateInfo.add(Data.advise);
		}
		System.out.println(diseaseCertificateInfo);
		wnwd.waitElementByXpathAndClick("提交并打印按钮", "//span[contains(text(),'提交并打印')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("打印按钮", "//div[@class='history-detail']//span[contains(text(),'打印')]", Framework.defaultTimeoutMax);
		printConfirm();
		return diseaseCertificateInfo;
	}

	// 获取病历信息
	public ArrayList<String> getEmrInfo() {
		ArrayList<String> emrInfo = new ArrayList<>();
//        String Info = wnwd.getElementListByXpath("//div[@class='emr']//div[contains(@class,'editor-paragraph') and contains(.,'处置治疗')]//span[(contains(@class,'editor-fragment') or contains(@class,'editor-textbox')) and contains(.,'" + Data.test_prescribe_drug + "')]").get(0).getText().replace("（", "").replace("）", "").replace("(", "").replace(")", "").replace(" ", "").replace("\r", "").replace("\n", "").replace(";", "");

//        emrInfo.add(Data.test_disease);
//        emrInfo.add(Info);
		return emrInfo;
	}

	public void CommonHerb(String DrugName) {
		try {
			wnwd.waitElementByXpathAndClick("处置加工厂西成药",
					WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "西成药"), Framework.defaultTimeoutMax);
			SdkTools.sleep(Framework.defaultTimeoutMin);

			List<WebElement> elelist = wnwd.getElementListByXpath("//div[@class='serves']//span[@title]");
			logger.log(2, "find:" + elelist.size());
			for (WebElement webElement : elelist) {
				String Text = webElement.getText();
				logger.log(2, "find:" + Text);
				if (Text.equals(DrugName)) {
					System.out.println(Text);
					System.out.println(DrugName);
					wnwd.wnClickElement(webElement, "点击输液开立");
					wnwd.waitElementByXpathAndClick("开立医嘱确认", "//div[@class='flex main']//button[.='确认']",
							Framework.defaultTimeoutMax);
				}
			}
		} catch (Throwable e) {
			throw new Error("找不到xpath:" + e.getMessage());
		}
	}

	public void CommonAll() {
		List<WebElement> elementList = wnwd.getElementListByXpath("//div[@class='disposal__search--select']//li")
				.subList(1, 7);
		System.out.println("这个list长度： " + elementList.size());

//		for (int i = 0; i < elementList.size(); i++) {
//			System.out.println("这个list名字： " + elementList.get(i).getText());
		for (WebElement ele1 : elementList) {
			switch (ele1.getText()) {
			case "西成药":
				// 点击
				wnwd.checkElementByXpath("点击加工厂西药",
						WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "西成药"),
						Framework.defaultTimeoutMid).click();
				logger.log(1, "点击成功");
				searchOrderForCommon(Data.test_prescribe_drug);
				prescribeOrder(Data.test_prescribe_drug,
						new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_drug_pack)));
				break;
			case "草药":
				// 点击
				wnwd.checkElementByXpath("点击加工厂草药",
						WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "草药"),
						Framework.defaultTimeoutMid).click();
				logger.log(1, "点击成功");
				searchOrderForCommon(Data.test_prescribe_herb);
				prescribeOrder(Data.test_prescribe_herb);
				break;
			case "检验":
				// 点击
				wnwd.checkElementByXpath("点击加工厂检验",
						WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "检验"),
						Framework.defaultTimeoutMid).click();
				logger.log(1, "点击成功");
				searchOrderForCommon(Data.test_prescribe_lab);
				prescribeOrder(Data.test_prescribe_lab);
				break;
			case "检查":
				// 点击
				wnwd.checkElementByXpath("点击加工厂检查",
						WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "检查"),
						Framework.defaultTimeoutMid).click();
				logger.log(1, "点击成功");
				searchOrderForCommon(Data.test_prescribe_exam);
				prescribeOrder(Data.test_prescribe_exam);
				break;
			case "治疗":
				// 点击
				wnwd.checkElementByXpath("点击加工厂治疗",
						WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "治疗"),
						Framework.defaultTimeoutMid).click();
				logger.log(1, "点击成功");
				searchOrderForCommon(Data.test_prescribe_treat);
				prescribeOrder(Data.test_prescribe_treat);
				break;
			}
		}

	}

	/*
	 * 管制药品模板引用
	 */
	public void quoteTemplateForControlledSubstance(String name) {
		wnwd.waitElementByXpathAndClick("进入模板界面", WnOutpatientXpath.outpatientDisposalFactoryTemplateButton,
				Framework.defaultTimeoutMax);
		WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		// 查找所有搜索结果
		List<WebElement> searchResults = resultBox
				.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
		// 搜索结果是空 直接报错
		if (searchResults == null || searchResults.size() == 0) {
			logger.assertFalse(true, "60无搜索结果", "输入:" + name + "\n结果包含:");
		}
		// searchFlag是空 直接选择第一个搜索结果
		else {
			wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
		}

		SdkTools.sleep(2000);
		wnwd.waitElementByXpathAndClick("点击引用模板", WnOutpatientXpath.outpatientOrderTemplateFirstQuoteBtn,
				Framework.defaultTimeoutMax);
		try {
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);

			List<WebElement> unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMax);
			List<WebElement> lilist = wnwd.waitElementListByXpath(
					"//ul[contains(@class,'template-detail')]/div//li[contains(@class,'flex__alignItems--center')]",
					Framework.defaultTimeoutMid);//// div[@class='template-detail-column']//span[contains(.,'雷贝')]"
			if (unSignOrders.size() == lilist.size()) {
				logger.log(1, "引用成功,医嘱条数：" + lilist.size());
			} else {
				logger.log(3, "引用医嘱失败: " + lilist.get(0).getText());
			}

			addDiagnoseIfNeed();
			own_expense(null);
			List<String> errOrders = new ArrayList<>();
			if (unSignOrders == null || unSignOrders.size() == 0) {
				throw new Error("没有找到未签署医嘱!");
			}
			for (WebElement order : unSignOrders) {
				if (order.findElements(By.xpath(".//i[contains(@class,'fail') or contains(@class,'warning')]"))
						.size() > 0) {
					errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
					addClinicalSummaryIfNeed();
				}
			}
			if (errOrders.size() > 0) {
				wnwd.getScreenShot("医嘱列表异常");
				throw new Error("发现异常医嘱:" + errOrders);
			}

		} catch (Throwable e) {
			logger.log(1, "找不到成功提示,判断是否有编辑框");
		}
		wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);

	}

	/*
	 * 管制药品收藏开立
	 */
	public void CommonForControlledSubstance(String name) {
		wnwd.checkElementByXpath("点击加工厂西药", WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "西成药"),
				Framework.defaultTimeoutMid).click();
		List<WebElement> elelist = wnwd.waitElementListByXpath("//div[@class='serves']//p[@class='serves_examItem']",
				Framework.defaultTimeoutMid);
		List<WebElement> warnIconlist = wnwd.waitElementListByXpath(
				"//div[@class='serves']//p[@class='serves_examItem']//i[contains(@class,'el-icon-warning resultTipsStop')]",
				Framework.defaultTimeoutMid);
		for (int i = 0; i < elelist.size(); i++) {
			if (warnIconlist.size() > i) {
				WebElement warnIcon = warnIconlist.get(i);
				wnwd.moveToElement(warnIcon, "警告图标");
				WebElement warnText = wnwd.checkElementByXpath("警告内容", "//div[@class='el-popover el-popper']",
						Framework.defaultTimeoutMax);
				if (warnText.getText().contains("抗菌药物") && name.equals("白细胞疾患")) {
					logger.log(2, warnText.getText());
				} else if (warnText.getText().contains("您暂无开具") && name.equals("苯丙酮尿症")) {
					logger.log(2, warnText.getText());
				}
			} else {
				wnwd.waitElementByXpathAndClick("收藏开立医嘱",
						"//div[@class='serves']//span[contains(@class,'overWidthTxt examItem_content')and contains(.,'雷贝')]",
						Framework.defaultTimeoutMid);
				wnwd.waitElementByXpathAndClick("开立医嘱确认", "//div[@class='flex main']//button[.='确认']",
						Framework.defaultTimeoutMax);
			}

		}
	}

	// 医嘱加工厂引用医嘱模板开立
	public void quoteTemplateForFactory(String name) {
		wnwd.waitElementByXpathAndClick("进入模板界面", WnOutpatientXpath.outpatientDisposalFactoryTemplateButton,
				Framework.defaultTimeoutMax);
		WebElement OrderTemplateSearchInput = wnwd.waitElementByXpathAndInput("输入模板名称",
				WnOutpatientXpath.outpatientSearchOrderInput, name, Framework.defaultTimeoutMax);
		WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		// 查找所有搜索结果
		List<WebElement> searchResults = resultBox
				.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
		// 搜索结果是空 直接报错
		if (searchResults == null || searchResults.size() == 0) {
			logger.assertFalse(true, "60无搜索结果", "输入:" + name + "\n结果包含:");
		}
		// searchFlag是空 直接选择第一个搜索结果
		else {
			wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
		}

		SdkTools.sleep(5000);
//		OrderTemplateSearchInput.sendKeys(Keys.ENTER);
//		WebElement OrderTemplateSearchText = wnwd.waitElementByXpath("//div[@class='template-list-column']//div[@class='el-tooltip ellipsis']//span[.='输液专项模板引用']",Framework.defaultTimeoutMax);
		WebElement OrderTemplateSearchText = wnwd.waitElementByXpath("//div[@class='template-list-column']//div[@class='el-tooltip ellipsis']//span",Framework.defaultTimeoutMax);
		if (OrderTemplateSearchText.getText().equals(name)){
			wnwd.waitElementByXpathAndClick("点击引用模板", WnOutpatientXpath.outpatientOrderTemplateFirstQuoteBtn,
					Framework.defaultTimeoutMax);

		try {
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);
			SdkTools.sleep(4000);
			own_expense(null);
			List<WebElement> unSignOrders = wnwd.waitElementListByXpath(WnOutpatientXpath.outpatientUnsignedOrder,
					Framework.defaultTimeoutMax);
			addDiagnoseIfNeed();
			List<String> errOrders = new ArrayList<>();
			if (unSignOrders == null || unSignOrders.size() == 0) {
				throw new Error("没有找到未签署医嘱!");
			}
			for (WebElement order : unSignOrders) {
				if (order.findElements(By.xpath(".//i[contains(@class,'fail') or contains(@class,'warning')]"))
						.size() > 0) {
					errOrders.add(order.getText().replace("\n", "-").replace("\r", "-"));
				}
			}
			if (errOrders.size() > 0) {
				wnwd.getScreenShot("医嘱列表异常");
				throw new Error("发现异常医嘱:" + errOrders);
			}
		} catch (Throwable e) {
			logger.log(1, "找不到成功提示,判断是否有编辑框");
//			wnwd.checkElementByXpath("等待诊断编辑框", WnOutpatientXpath.outpatientDiagnosisEditBox, 3000);
//			wnwd.waitElementByXpathAndClick("选择第一个诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxFirstDiag,
//					Framework.defaultTimeoutMax);
//			wnwd.waitElementByXpathAndClick("确定诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
//					Framework.defaultTimeoutMax);
//			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
//					Framework.defaultTimeoutMax);
		}
		wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
		}
	}

	// 诊疗路径输液专项引用医嘱模板开立

	/**
	 * 1.进入医嘱模板界面 2.搜索模板名称 3.引用搜索结果第一个模板 4.检查医嘱列表中有未签署医嘱
	 */
	public void quoteTemplateForTreatmentPaths(String name) {
		wnwd.waitElementByXpathAndClick("进入模板界面", WnOutpatientXpath.outpatientTemplateDisposal,
				Framework.defaultTimeoutMax);
		SdkTools.sleep(2000);
		WebElement OrderTemplateSearchInput = wnwd.waitElementByXpathAndInput("输入模板名称",
				WnOutpatientXpath.outpatientOrderTemplateSearchInput, name, Framework.defaultTimeoutMax);
		OrderTemplateSearchInput.sendKeys(Keys.ENTER);
		SdkTools.sleep(2000);
		wnwd.waitElementByXpathAndClick("点击引用模板", WnOutpatientXpath.outpatientOrderTemplateFirstQuoteBtn,
				Framework.defaultTimeoutMax);
		try {
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.log(1, "找不到成功提示,判断是否有编辑框");
			wnwd.checkElementByXpath("等待诊断编辑框", WnOutpatientXpath.outpatientDiagnosisEditBox, 3000);
			wnwd.waitElementByXpathAndClick("选择第一个诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxFirstDiag,
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("确定诊断", WnOutpatientXpath.outpatientDiagnosisEditBoxCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("等待引用成功提示", WnOutpatientXpath.outpatientOrderTemplateQuoteSucFlag,
					Framework.defaultTimeoutMax);
		}
		own_expense();
		wnwd.checkElementByXpath("找到未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
	}

	// 诊疗路径拖动添加输液医嘱
	public void checkRecommendTreat_pathWay(String DrugName) {
		WebElement firstRecommendLab = wnwd.waitElementByXpath("推荐治疗",
				"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMax);
		if (firstRecommendLab == null) {
			prescribeOrder(DrugName);
			WebElement order = wnwd.checkElementByXpath("医嘱",
					"//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap') and contains(.,'开立')]//span[contains(@class,'win-icon-delete')]",
					Framework.defaultTimeoutMax);
			WebElement labBox = wnwd.checkElementByXpath("治疗框", "//div[contains(@class,'treatment-drug-wrap')]",
					Framework.defaultTimeoutMax);
			wnwd.drag("拖动到收藏", order, labBox);

			firstRecommendLab = wnwd.checkElementByXpath("推荐医嘱",
					"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMax);

			wnwd.wnClickElement(order, "删除医嘱");
			wnwd.wnClickElement(firstRecommendLab, "点击推荐医嘱引用");
		}
	}

	public void checkRecommendTreat_pathWay1(String DrugName) {
		WebElement firstRecommendLab = wnwd.waitElementByXpath("推荐治疗",
				"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMax);
		if (firstRecommendLab == null) {
			prescribeOrder(DrugName);
			WebElement order = wnwd.checkElementByXpath("医嘱",
					"//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap') and contains(.,'开立')]//span[contains(@class,'win-icon-delete')]",
					Framework.defaultTimeoutMax);
			WebElement labBox = wnwd.checkElementByXpath("检验框", "//div[contains(@class,'treatment-drug-wrap')]",
					Framework.defaultTimeoutMax);
			wnwd.drag("拖动检验到收藏", order, labBox);
			firstRecommendLab = wnwd.checkElementByXpath("推荐检验",
					"//div[contains(@class,'treatment-drug-wrap')]//*[name() = 'svg']", Framework.defaultTimeoutMax);
			wnwd.wnClickElement(order, "删除医嘱");
			wnwd.wnClickElement(firstRecommendLab, "fdsfdsafds f");
		}
	}
	
	/**
	 * 通过接口判断前端医嘱搜索是否为空
	 * @param syncHost
	 * @param cs_name 医嘱名称
	 */
	public Boolean ifSearchOrdEmpty(String syncHost, String cs_name) {
		String url = "http://" + syncHost + "/outpat/api/v4/cis/clinical_service/search";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		
		Wn60Db db60 =  new Wn60Db(logger);
		String dockerId = db60.getEmployIdByUsercode(Data.default_user_login_account).get("EMPLOYEE_ID");
		db60.disconnect();
		
		String json = "{\"keyword\":\"" + cs_name + "\",\"type\":\"0\",\"pageNo\":0,\"pageSize\":20,\"doctorId\":\"" + dockerId + "\",\"pharmacyId\":\"\",\"isAllowCancel\":true,\"axiosModel\":\"clinicalOrder_search_loadSearchList\",\"filterType\":\"1\",\"matchName\":\"1\",\"matchCode\":\"1\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			String content = test.getResponseContent();
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonArray dataArray = contentJson.getAsJsonArray("data");
			if(dataArray.size() > 0) {
				logger.boxLog(1, "成功", "接口检索医嘱不为空");
				return false;
			}else {
				logger.boxLog(3, "提醒", "接口检索医嘱为空");
				return true;
			}

		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
			return true;
		}

	}

	// 从search接口获取成组开立医嘱 数据
	public void DisposalOfDispositionSetsDateForSearch(String syncHost) {
		String Drug = null;
		String MedSpec = null;
		String url = "http://" + syncHost + "/outpat/api/v3/cis/clinical_service/search";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"keyword\":\"\",\"type\":\"8\",\"pageNo\":0,\"pageSize\":10000,\"doctorId\":\"57393667239307269\",\"pharmacyId\":\"\",\"isAllowCancel\":true,\"axiosModel\":\"clinicalOrder_search_loadSearchList\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			String content = test.getResponseContent();
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonArray dataArray = contentJson.getAsJsonArray("data");
			JsonElement data = null;
			for (int i = 0; i < dataArray.size(); i++) {
				if (dataArray.get(i).getAsJsonObject().get("tipsType").getAsString().equals("0")) {
					data = dataArray.get(i);
					break;
				}
			}
			logger.boxLog(1, "成功", "1111");
			assert data != null;
			Drug = (data.getAsJsonObject().get("commodityNameChinese").toString());
			MedSpec = (data.getAsJsonObject().get("medicineSpec").toString());
			System.out.println(
					"药品名字规格: " + Drug.substring(1, Drug.length() - 1) + MedSpec.substring(1, MedSpec.length() - 1));
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		searchOrderDD(Drug.substring(1, Drug.length() - 1),
				new ArrayList<>(Arrays.asList(MedSpec.substring(1, MedSpec.length() - 1))));

	}

	// 接诊患者时，患者基本信息设置弹框，98175为是，98176为否
	public void SetPatientInforFlag(String syncHost, String value) {
		String clurl = "http://" + syncHost
				+ "/encounter-mdm/api/v1/app_encounter_mdm/clinic_call_preference_ext_hospital/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"clinicBaseInfoTips\":{\"clinicCallPreferenceCode\":\"399283634\",\"clinicCallPreferenceValue\":{\"isShowBasicInfoPopup\":\""
				+ value + "\",\"isShowPopupEveryTime\":\"98176\",\"basicCodeList\":[]}}}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// 接诊患者时，代配药人信息弹框设置，98175为是，98176为否
	public void SetDispenserFlag(String syncHost, String value) {
		String clurl = "http://" + syncHost
				+ "/encounter-mdm/api/v1/app_encounter_mdm/clinic_call_preference_ext_hospital/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"clinicInsteadOfInfo\":{\"clinicCallPreferenceCode\":\"399283633\",\"clinicCallPreferenceValue\":{\"typeOpenFlag\":\""
				+ value + "\",\"restrictInsuranceType\":null,\"medInstiInsurIds\":[]}}}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// KNW006是否启用药品用量医保控制（启动1,不启动0,默认不启动0）
	public void SetKNW006(String syncHost, int value) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"122508985703184385\",\"valueConfigs\":[{\"paramValueConfigId\":\"122508985703184385\",\"paramConfigId\":\"122508985703184385\",\"paramValue\":\""
				+ value
				+ "\",\"paramEndValue\":\"\",\"activatedAt\":\"2021-03-19 10:37:57\",\"valueDesc\":\"启用\"}],\"paramConfigId\":\"122508985703184385\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// 药品医保用量提醒是否干预流程（0：不干预，默认为0；1：干预）http://172.17.1.235/knowledge-mdm/api/v1/app_knowledge_mdm/knowledge_param_config/save
	public void SetCLI_VALI_MAX_DOSE_INTERVENTION_FLAG(String syncHost, int value) {
		String clurl = "http://" + syncHost + "/knowledge-mdm/api/v1/app_knowledge_mdm/knowledge_param_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"saveConfigParamsDTOS\":[{\"configurationId\":\"109326706734465031\",\"doctorId\":null,\"enabledFlag\":\"98175\",\"isDel\":\"0\",\"hospitalSOID\":\""
				+ Data.hospital_soid + "\",\"paramContent\":\"" + value
				+ "\",\"paramDesc\":\"药品医保用量提醒是否干预流程（0：不干预，默认为0；1：干预）\",\"paramName\":\"CLI_VALI_MAX_DOSE_INTERVENTION_FLAG\",\"parameterTypeCode\":\"390031904\"}]}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// 根据paramNo查出当前主数据参数的值
	public String getParamValueByParamNo(String paramNo) {
		String url = "http://" + Data.host
				+ "/mdm-base/api/v1/app_base_mdm/parameter/query/parameter_value_by_app_menu_id";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"pageNo\":0,\"pageSize\":10,\"pageType\":\"P\",\"keyword\":\"" + paramNo + "\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		JsonElement paramId = null;
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			System.out.println("成功");
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
			JsonArray paramConfigs = null;
			JsonArray dataArray = resJson.getAsJsonArray("data");
			for (JsonElement data : dataArray) {
				paramConfigs = data.getAsJsonObject().get("paramConfigs").getAsJsonArray();
				for (JsonElement paramConfig : paramConfigs) {
					paramId = paramConfig.getAsJsonObject().get("paramId");
				}
			}
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		url = "http://" + Data.host + "/mdm-base/api/v1/app_base_mdm/parameter/get/by_id";
		json = "{\"paramId\":" + paramId.toString() + ",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
		httpTestUrl = new HttpTestUrl(url);
		test = new HttpTest(httpTestUrl);
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		String paramValue = null;
		List<String> list = new ArrayList<String>();
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
			JsonArray dataArray = resJson.get("data").getAsJsonObject().getAsJsonArray("paramValueList");
			for (JsonElement data : dataArray) {
				paramValue = data.getAsJsonObject().get("paramValue").toString();
				paramValue = paramValue.replace("\"", "");
				list.add(paramValue);
			}
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		String value = list.toString().replace("[", "").replace("]", "");
		return value;
	}

	// 根据keyword查出当前知识个性化参数的值
	public String getParamContentByKeyword(String keyword) {
		String paramContent = "";
		String url = "http://" + Data.host + "/knowledge-mdm/api/v1/app_knowledge_mdm/config_param/query/by_example";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");

		String json = "{\"keyword\":\"" + keyword + "\",\"pageNo\":0,\"pageSize\":10,\"pageType\":\"P\"}";
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
			JsonArray dataArray = resJson.getAsJsonArray("data");
			for (JsonElement data : dataArray) {
				if (data.getAsJsonObject().get("paramName").toString().contains(keyword)) {
					paramContent = data.getAsJsonObject().get("paramContent").toString().replace("\"", "");
				}
			}
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		return paramContent;
	}

	// CL023自备药管理模式（0：仅允许医院在用商品作为自备药，1：允许医院所有商品作为自备药；）
	public void SetCL023(String syncHost, String value) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String jsonvalue = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4300231659\",\"valueConfigs\":[{\"paramValueConfigId\":\"4300231787\",\"paramConfigId\":\"4300231756\",\"paramValue\":\"?value\",\"paramEndValue\":null,\"activatedAt\":\"2021-05-11 14:14:48\",\"valueDesc\":\"仅允许医院在用商品作为自备药\"}],\"paramConfigId\":\"4300231756\"}";
		jsonvalue = jsonvalue.replace("?value", value);
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(jsonvalue, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL023成功", "参数CL023修改为：" + value);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// CL066开立药品是否根据数量反算天数（启动1,不启动0,默认不启动0）
	public void SetCL066(String syncHost, int value) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304926615\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304926620\",\"paramConfigId\":\"4304926619\",\"paramValue\":\""
				+ value
				+ "\",\"paramEndValue\":null,\"activatedAt\":\"2021-03-01 13:56:43\",\"valueDesc\":\"启用反算\"}],\"paramConfigId\":\"4304926619\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL066成功", "参数CL066修改为为：" + value);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// CL068药品开立正算模式是否启用（启动1,不启动0,默认启用1）
	public void SetCL068(String syncHost, int value) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304926783\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304926788\",\"paramConfigId\":\"4304926787\",\"paramValue\":\""
				+ value
				+ "\",\"paramEndValue\":null,\"activatedAt\":\"2021-02-01 17:46:18\",\"valueDesc\":\"启用\"}],\"paramConfigId\":\"4304926787\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// RE010中医诊断证型默认选择设置(2：证型非必选)
	public void SetRE010(String syncHost) {
		String REurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304926855\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304926867\",\"paramConfigId\":\"4304926865\",\"paramValue\":\"2\",\"paramEndValue\":\"\",\"activatedAt\":\"2020-12-08 13:17:03\",\"valueDesc\":\"证型非必选\"}],\"paramConfigId\":\"4304926865\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(REurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + REurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	/**
	 * RE022门诊病历编辑器类型
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  参数默认值为0；0-知识编辑器 ；1-自研编辑器
	 */
	public void SetRE022(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"152374502515924992\",\"valueConfigs\":[{\"paramValueConfigId\":\"165349272312285185\",\"paramConfigId\":\"165349272312285184\",\"paramValue\":\""
				+ paramValue +"\",\"paramEndValue\":\"\",\"activatedAt\":\"2021-06-30 17:49:38\",\"valueDesc\":\""
				+ paramDesc
				+ "\"}],\"paramConfigId\":\"165349272312285184\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数RE022成功", "参数RE022修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// RE010中医诊断证型默认选择设置(0：证型必选且支持一个)
	public void SetRE010toN(String syncHost, String value) {
		String REurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304926855\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304926867\",\"paramConfigId\":\"4304926865\",\"paramValue\":?value,\"paramEndValue\":\"\",\"activatedAt\":\"2020-12-08 13:17:03\",\"valueDesc\":\"证型非必选\"}],\"paramConfigId\":\"4304926865\"}";
		json.replace("?value", value);
		HttpTestUrl httpTestUrl = new HttpTestUrl(REurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "调用接口成功", "1111");
		} else {
			logger.assertFalse(true, "调用接口报错", "请求地址: " + REurl + "\n\n\n请求状态: " + test.getResponseCode()
					+ "\n\n\n返回内容: " + test.getResponseContent());
		}
	}

	// EX006设置为1，启用新的执行域的联动设置
	public void SetEX006_1(String syncHost) {
		String REurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"128422335346765824\",\"valueConfigs\":[{\"paramValueConfigId\":\"128422335346765824\",\"paramConfigId\":\"128422335346765824\",\"paramValue\":\"1\",\"paramEndValue\":\"\",\"activatedAt\":\"2020-12-30 14:55:39\",\"valueDesc\":\"执行新模式\"}],\"paramConfigId\":\"128422335346765824\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(REurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + REurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// 个性化配置草药、中成药开立校验中医诊断、中医证型（0：二者均非必填，默认为0）
	public void SetTCM_MEDICINE_DIAG_VERIFY(String syncHost) {
		String url = "http://" + syncHost + "/knowledge-mdm/api/v1/app_knowledge_mdm/knowledge_param_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"saveConfigParamsDTOS\":[{\"configurationId\":\"109326706734465035\",\"doctorId\":null,\"enabledFlag\":\"98175\",\"isDel\":\"0\",\"hospitalSOID\":\""
				+ Data.hospital_soid
				+ "\",\"paramContent\":\"0\",\"paramDesc\":\"草药、中成药开立校验中医诊断、中医证型（0：二者均非必填，默认为0；1：二者均必填；2：诊断必填；3：证型必填；4：二选一必填）\",\"paramName\":\"TCM_MEDICINE_DIAG_VERIFY\",\"parameterTypeCode\":\"390031904\"}]}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		System.out.println(cookie.toString());
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

	// 医嘱录入时校验是否有西医诊断(默认为0：不校验，不需有西医诊断;1：校验，需要有西医诊断
	public void SetVM_MEDICINE_DIAG_VERIFY(String value) {
		String url = "http://" + Data.host + "/knowledge-mdm/api/v1/app_knowledge_mdm/knowledge_param_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"saveConfigParamsDTOS\":[{\"configurationId\":\"109326706734465036\",\"doctorId\":null,\"enabledFlag\":\"98175\",\"isDel\":\"0\",\"hospitalSOID\":\""
				+ Data.hospital_soid + "\",\"paramContent\":\"" + value
				+ "\",\"paramDesc\":\"医嘱录入时校验是否有西医诊断(默认为0：不校验，不需有西医诊断;1：校验，需要有西医诊断)\",\"paramName\":\"VM_MEDICINE_DIAG_VERIFY\",\"parameterTypeCode\":\"390031904\"}]}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		System.out.println(cookie.toString());
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));

		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改知识个性化设置参数VM_MEDICINE_DIAG_VERIFY成功", "参数VM_MEDICINE_DIAG_VERIFY修改为：" + value);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	// 主数据参数初始化
	public void InitMdsParameterConfig(String syncHost) {
//        SetCL069(Data.host);
		SetRE010(Data.host);

//            SetTCM_MEDICINE_DIAG_VERIFY(Data.host);

	}

	// 全医嘱开立前置参数检查
	public void checkParamsForTestAllService(Map<String, String> parmalist) {
		Wn60Db windb60 = new Wn60Db(SdkTools.logger);
		Map<String, String> param;
		boolean sucflag = true;
		try {
			for (String s : parmalist.keySet()) {
				param = windb60.getParam(s);
				if (param != null && param.get("PARAM_NO").equals(s) && param.containsKey("PARAM_VALUE")) {
					System.out.println("param:"+param);
					if (!parmalist.get(s).equals(getParamValueByParamNo(s))) {
						logger.boxLog(2,s+"参数值不符合自动化要求，当前为:"+param.get("PARAM_VALUE")+",请先将" + s + "参数:"+param.get("PARAM_DESC")+",设置成" + parmalist.get(s),"");
						sucflag = false;
					}else {
						logger.boxLog(1,s+"参数值符合自动化要求，为：" + parmalist.get(s)+":","");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("前置参数错误");
		}
		if(sucflag==false) {
			throw new Error("主数据参数错误，请查看详细报告，并手工完成设置");
		}
	}

	// 全医嘱开立前置参数检查
	public void setParamsForTestAllService() {
		if(Data.modeConfiguration.equals("develop")) {
			String paramValue = getParamValueByParamNo("CL021");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "CL021参数值不符合要求，当前值为：" + paramValue, "当前CL021参数不正确，自动化会将CL021参数设置成0");
				SetCL021(Data.host, "0", "不控制");
			} else {
				logger.boxLog(1, "CL021参数符合要求", "CL021参数符合要求，为：0 不控制");
			}

			paramValue = getParamValueByParamNo("CL059");
			if (!paramValue.equals("1")) {
				logger.boxLog(2, "CL059参数值不符合要求，当前值为：" + paramValue, "当前CL059参数不正确，自动化会将CL059参数设置成1");
				SetCL059(Data.host, "1", "常显示");
			} else {
				logger.boxLog(1, "CL059参数符合要求", "CL059参数符合要求，为：1 常显示");
			}

			paramValue = getParamValueByParamNo("CL069");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "CL069参数值不符合要求，当前值为：" + paramValue, "当前CL069参数不正确，自动化会将CL069参数设置成0");
				SetCL069(Data.host, "0", "不启动");
			} else {
				logger.boxLog(1, "CL069参数符合要求", "CL069参数符合要求，为：0 不启动");
			}

			paramValue = getParamValueByParamNo("CL070");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "CL070参数值不符合要求，当前值为：" + paramValue, "当前CL070参数不正确，自动化会将CL070参数设置成：0 不控制");
				SetCL070(Data.host, "0", "不控制");
			} else {
				logger.boxLog(1, "CL070参数符合要求", "CL070参数符合要求，为：0 不控制");
			}

			paramValue = getParamValueByParamNo("CL074");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "CL074参数值不符合要求，当前值为：" + paramValue, "当前CL074参数不正确，自动化会将CL074参数设置成：0 文本拼接模式");
				SetCL074(Data.host, "0", "文本拼接模式");
			} else {
				logger.boxLog(1, "CL074参数符合要求", "CL074参数符合要求，为：0 不控制");
			}

			paramValue = getParamContentByKeyword("VM_MEDICINE_DIAG_VERIFY");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "VM_MEDICINE_DIAG_VERIFY参数值不符合要求，当前值为：" + paramValue,
						"当前VM_MEDICINE_DIAG_VERIFY参数不正确，自动化会将参数设置成： 0 不控制");
				SetVM_MEDICINE_DIAG_VERIFY("0");
			} else {
				logger.boxLog(1, "VM_MEDICINE_DIAG_VERIFY参数符合要求", "VM_MEDICINE_DIAG_VERIFY参数（医嘱录入时校验是否有西医诊断）符合要求，为：0 不校验");
			}
		}else if(Data.modeConfiguration.equals("release")) {
			Map<String, String> parmalist = ImmutableMap.of("CL021", "0","CL059", "1","CL069", "0","CL070","0","CL074","0");
			checkParamsForTestAllService(parmalist);
			
			String paramValue = getParamContentByKeyword("VM_MEDICINE_DIAG_VERIFY");
			if (!paramValue.equals("0")) {
				logger.boxLog(2, "知识域-知识个性化设置参数VM_MEDICINE_DIAG_VERIFY值不符合要求，当前值为:" + paramValue+"，请手动修改为0。","");
				throw new Error("主数据参数错误，请查看详细报告，并手工完成设置");
			}
		}

	}

	/**
	 * 用于药品成组搜索开立
	 *
	 * @param name
	 */
    public void prescribeOrderForDD(String name) {
        prescribeOrderForDD(name, null);
    }
	
	public void prescribeOrderForDD(String name, List<String> searchFlag) {
		prescribeOrder1(name, searchFlag, null);
	}

	public void prescribeOrder1(String name, List<String> searchFlag, List<String> orderDetail) {
		if (name.toUpperCase().equals("NONE")) {
			return;
		}
		searchOrderDD(name, searchFlag);

		addDiagnoseIfNeed();
		childrenInfoIfNeed();
		pishiIfNeed();
		DisposalOfDispositionSetsDateForSearch(Data.host);
		editAndCommitOrder(orderDetail);
		own_expense();

	}

	/**
	 * 用于中药批量搜索开立
	 *
	 * @param names 多个中药
	 * @param
	 */
	public void prescribeHerbOrders(List<String> names) {
		prescribeHerbOrders(names, null);
	}

	public void prescribeHerbOrders(List<String> names, List<String> searchFlag) {
		prescribeHerbOrders(names.get(0), searchFlag, null);
	}

	public void prescribeHerbOrders(String name, List<String> searchFlag, PrescribeDetail detail) {
		if (name.equals("NONE")) {
			return;
		}
		searchOrder(name, searchFlag);

		addDiagnoseIfNeed();
		childrenInfoIfNeed();
		pishiIfNeed();
		editHerbFactory(detail);
		commitFactory();
		afterFactory();
	}

	// 编辑怀孕信息
	public void editPregnantInfo() {
		String timtString = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
				.format(new Date(System.currentTimeMillis() - 3600000l * 24 * 30));
		String receptionRomfirmDilogXpath = "//div[@role='dialog' and starts-with(.,'接诊确认')]";
		wnwd.checkElementByXpath("接诊确认框", receptionRomfirmDilogXpath, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("立即填写", receptionRomfirmDilogXpath + "//button[.='立即填写']",
				Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("末次月经",
				"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'末次月经')]//input",
				timtString, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("孕次",
				"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'孕次')]//input",
				"1", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("产次",
				"//div[contains(@class,'patient-details')]//div[contains(@class,'el-form-item') and contains(.,'产次')]//input",
				"0", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("个人信息确定", "//div[contains(@class,'patient-details')]//button[.='确 定']",
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待个人信息框消失", "//div[contains(@class,'patient-details')]", Framework.defaultTimeoutMax);
	}

	// 检查产科就诊模式标识
	public void checkObstetricalMode() {
		wnwd.checkElementByXpath("产科就诊模式标识",
				"//div[contains(@class,'pregnancy-postpartum-duration') and contains(.,'妊娠期')]",
				Framework.defaultTimeoutMax);
	}

	// 编辑怀孕信息
	public void changeToNormalVisitIfNeed() {
		String receptionRomfirmDilogXpath = "//div[@role='dialog' and starts-with(.,'接诊确认')]";
		WebElement dialog = wnwd.waitElementByXpath("产科接诊确认框", receptionRomfirmDilogXpath, Framework.defaultTimeoutMin);
		if (dialog != null) {
			wnwd.waitElementByXpathAndClick("普通接诊", receptionRomfirmDilogXpath + "//button[.='普通接诊']",
					Framework.defaultTimeoutMax);
			SdkTools.sleep(Framework.defaultTimeoutMin);
		}
	}

	// 书写一个主诉
	public void writeSymptom(String symptomName) {
		String knowledgeSystem = getKnowledgeSystem();
		if (knowledgeSystem.equals("卫宁医学知识体系")) {
			writeSymptom_knowledge(symptomName);
		} else if (knowledgeSystem.equals("诊疗路径简易版")) {
			writeSymptom_pathWay(symptomName);
		} else {
			logger.assertFalse(true, "不支持的知识体系:" + knowledgeSystem);
		}
	}

	// 书写一个主诉(知识体系)
	public void writeSymptom_knowledge(String symptomName) {
		try {
			WebElement inputElement = wnwd.waitElementByXpathAndInput("主诉书写框",
					"//div[contains(@class,'chief-complaint-item')]//div[contains(@class,'chief-complaint-text-input')]//input",
					symptomName, Framework.defaultTimeoutMax);
			inputElement.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'「" + symptomName + "」')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "知识体系 书写主诉失败:" + e.getMessage());
		}
	}

	// 书写一个主诉(诊疗路径)
	public void writeSymptom_pathWay(String symptomName) {
		try {
			wnwd.waitElementByXpathAndClick("诊疗路径简易版 - 症状按钮", WnOutpatientXpath.outpatientSymptomWarpButton,
					Framework.defaultTimeoutMax);
			WebElement inputElement = wnwd.waitElementByXpathAndInput("主诉书写框",
					"//div[contains(@class,'chief-complaint-item')]//div[contains(@class,'chief-complaint-text-input')]//input",
					symptomName, Framework.defaultTimeoutMax);
			inputElement.sendKeys(Keys.ENTER);
			wnwd.checkElementByXpath("主诉:" + symptomName,
					"//div[contains(@class,'chief-complaint-item-name') and contains(.,'「" + symptomName + "」')]",
					Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("主诉框关闭按钮",
					"//div[contains(@class,'chief-complaint-header')]//i[contains(@class,'el-icon-close')]",
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			logger.assertFalse(true, "诊疗路径 书写主诉失败:" + e.getMessage());
		}
	}

	// 从浏览器缓存获取登录信息
	// userInfo.get("userId").getAsString());
	// userInfo.get("employeeNo").getAsString());
	// userInfo.get("hospitalSOID").getAsString());
	// userInfo.get("orgName").getAsString());
	// +userInfo.get("employeeName").getAsString());
	public JsonObject getLoginInfoFromCookies() {
		String userInfoString = wnwd.getCookieValue("userInfo");
		JsonParser jp = new JsonParser();
		JsonObject userInfo = jp.parse(userInfoString).getAsJsonObject();
		return userInfo;
	}

	/**
	 * 通过接口获取搜索结果排在第几个
	 *
	 * @param searchName 输入的内容
	 * @param searchType 搜索的类型 ( 草药:9 , 西成药:5 , 检验:3 , 检查:4 , 病理:11 ,治疗:2 ,模板: 10)
	 * @param itemName   预期的 字段名
	 * @param ItemValue  预期的 字段内容
	 * @return int seq 药品搜索结果中排在第几个(从0开始) example:
	 *         getMedicineSeqByApi("葡萄糖","5","medicineId","12345") 返回 0 , 表示医生站搜索
	 *         西成药:葡萄糖 返回结果中 medicineId为12345的结果排在第1个 example:
	 *         getMedicineSeqByApi("CT检查","4","csId","12345") 返回 1 , 表示医生站搜索 检查:CT检查
	 *         返回结果中 csId为12345的结果排在第2个
	 */
	public Integer getMedicineSeqByApi(String searchName, String searchType, String itemName, String ItemValue) {
		
		SdkTools.logger.log(1, "搜索临床服务:" + searchName + ",预期字段名称:" + itemName + ",预期字段内容:" + ItemValue);
		
		// 从浏览器Cookie中获取
		String doctorId = getLoginInfoFromCookies().get("userId").getAsString();
		// 调用搜索医嘱搜索接口
		String url = "http://" + Data.host + "/outpat/api/v4/cis/clinical_service/search";
		String param = "{\"keyword\":\"" + searchName + "\",\"type\":\"" + searchType
				+ "\",\"pageNo\":0,\"doctorId\":\"" + doctorId
				+ "\",\"pharmacyId\":\"\",\"isAllowCancel\":true,\"axiosModel\":\"clinicalOrder_search_loadSearchList\",\"filterType\":\"1\",\"matchName\":\"1\",\"matchCode\":\"1\"}";

		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		SdkTools.logger.log(1, "搜索临床服务入参ulr:" + url);
		SdkTools.logger.log(1, "搜索临床服务入参header:" + header.toString());
		SdkTools.logger.log(1, "搜索临床服务入参param:" + param.toString());
		HttpTest httpTester = SdkTools.post("搜索临床医嘱", url, header, param, null, logger);
		// 解析请求结果
		JsonElement target = null;
		int seq = 0;
		
		SdkTools.logger.log(1, "搜索临床服务状态码:" + httpTester.getResponseCode());
		SdkTools.logger.log(1, "搜索临床服务响应值:" + httpTester.getResponseContent().toString());
		
		
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray dataArray = resJson.getAsJsonArray("data");
			logger.assertFalse(dataArray.size() < 1, "搜索无结果 CS_NAME:" + searchName);
			SdkTools.logger.log(1, "搜索临床服务返回数据数量:" + dataArray.size());
			for (JsonElement data : dataArray) {
				try {
					if (data.getAsJsonObject().get(itemName).getAsString().equals(ItemValue)) {
						target = data;
						break;
					}
				} catch (Throwable e) {
					SdkTools.logger.log(1, "搜索临床服务返回:" + data);
					SdkTools.logger.log(1, "返回服务中没有字段:" + itemName);
				}
				seq++;
			}
		} else {
			logger.assertFalse(true, "请求医嘱列表失败");
		}
		logger.assertFalse(target == null, "搜索无结果  " + itemName + ":" + ItemValue);
		if (Data.outOfStockFlag = false) {
			logger.assertFalse(!target.getAsJsonObject().get("tipsType").getAsString().equals("0"),
					"搜索到结果,但无法正常开立: " + target.getAsJsonObject().get("tipsContent"));
		}
		logger.boxLog(1, searchName + " 排在第" + (seq + 1) + "个:", "" + target);
		return seq;
	}
	
	/**
	 * 通过接口获取搜索结果排在第几个
	 *
	 * @param searchName 输入的内容
	 * @param searchType 搜索的类型 ( 草药:9 , 西成药:5 , 检验:3 , 检查:4 , 病理:11 ,治疗:2 ,模板: 10)
	 * @param itemName   预期的 字段名
	 * @param ItemValue  预期的 字段内容
	 * @param itemName2       预期的 字段名2
	 * @param ItemValue2    预期的 字段内容2
	 * @return int seq 药品搜索结果中排在第几个(从0开始) example:
	 *         getMedicineSeqByApi("葡萄糖","5","medicineId","12345") 返回 0 , 表示医生站搜索
	 *         西成药:葡萄糖 返回结果中 medicineId为12345的结果排在第1个 example:
	 *         getMedicineSeqByApi("CT检查","4","csId","12345") 返回 1 , 表示医生站搜索 检查:CT检查
	 *         返回结果中 csId为12345的结果排在第2个
	 */
	public Integer getMedicineSeqByApi(String searchName, String searchType, String itemName, String ItemValue, String itemName2, String ItemValue2) {
		
		SdkTools.logger.log(1, "搜索临床服务:" + searchName + ",预期字段名称:" + itemName + ",预期字段内容:" + ItemValue);
		
		// 从浏览器Cookie中获取
		String doctorId = getLoginInfoFromCookies().get("userId").getAsString();
		// 调用搜索医嘱搜索接口
		String url = "http://" + Data.host + "/outpat/api/v4/cis/clinical_service/search";
		String param = "{\"keyword\":\"" + searchName + "\",\"type\":\"" + searchType
				+ "\",\"pageNo\":0,\"doctorId\":\"" + doctorId
				+ "\",\"pharmacyId\":\"\",\"isAllowCancel\":true,\"axiosModel\":\"clinicalOrder_search_loadSearchList\",\"filterType\":\"1\",\"matchName\":\"1\",\"matchCode\":\"1\",\"matchPy\":\"1\"}";

		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		SdkTools.logger.log(1, "搜索临床服务入参ulr:" + url);
		SdkTools.logger.log(1, "搜索临床服务入参header:" + header.toString());
		SdkTools.logger.log(1, "搜索临床服务入参param:" + param.toString());
		HttpTest httpTester = SdkTools.post("搜索临床医嘱", url, header, param, null, logger);
		// 解析请求结果
		JsonElement target = null;
		int seq = 0;
		
		SdkTools.logger.log(1, "搜索临床服务状态码:" + httpTester.getResponseCode());
		SdkTools.logger.log(1, "搜索临床服务响应值:" + httpTester.getResponseContent().toString());
		
		
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray dataArray = resJson.getAsJsonArray("data");
			logger.assertFalse(dataArray.size() < 1, "搜索无结果 CS_NAME:" + searchName);
			SdkTools.logger.log(1, "搜索临床服务返回数据数量:" + dataArray.size());
			for(int index=0; index<dataArray.size(); index++) {
				try {
					if(dataArray.get(index).getAsJsonObject().get(itemName).getAsString().equals(ItemValue) && dataArray.get(index).getAsJsonObject().get(itemName2).getAsString().equals(ItemValue2)) {
						target = dataArray.get(index);
						SdkTools.logger.log(1, "index:" + index + "," + dataArray.get(index).getAsJsonObject().get(itemName) + "," + dataArray.get(index).getAsJsonObject().get(itemName2));
						break;
					}
				}catch (Throwable e) {
					SdkTools.logger.log(1, "搜索临床服务返回:" + dataArray.get(index));
					SdkTools.logger.log(1, "返回服务中没有字段:" + itemName);
				}
				seq++;
			}
		} else {
			logger.assertFalse(true, "请求医嘱列表失败");
		}
		logger.assertFalse(target == null, "搜索无结果  " + itemName + ":" + ItemValue);
		if (Data.outOfStockFlag = false) {
			logger.assertFalse(!target.getAsJsonObject().get("tipsType").getAsString().equals("0"),
					"搜索到结果,但无法正常开立: " + target.getAsJsonObject().get("tipsContent"));
		}
		logger.boxLog(1, searchName + " 排在第" + (seq + 1) + "个:", "" + target);
		return seq;
	}

	/**
	 * CL021诊断控制医嘱录入模式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1,2,3,4,5（默认为0）
	 * @param paramDesc  ，0：不控制；1：无诊断不允许录入药品医嘱，2：无诊断不允许录入检验医嘱；3：无诊断不允许录入检查医嘱；4：无诊断不录入治疗医嘱；5：无诊断不录入病理医嘱，默认为0
	 */
	public void SetCL021(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4300231657\",\"valueConfigs\":[{\"paramValue\":\""
				+ paramValue
				+ "\",\"paramEndValue\":null}],\"paramConfigId\":\"4300231754\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		
		String value = getParamValueByParamNo("CL021");
		if(!value.equals("0")) {
			Wn60Db db60 = new Wn60Db(logger);
			String paramConfigId = db60.getParamConfigIdByParamNo("CL021");
			String clurl1 = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
			Cookie cookie1 = wnwd.getCookieNamed("BEARER_TOKEN");
			String json1 = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4300231657\",\"valueConfigs\":[{\"paramValue\":\""
					+ paramValue
					+ "\",\"paramEndValue\":null}],\"paramConfigId\":\""
					+ paramConfigId
					+ "\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
			HttpTestUrl httpTestUrl1 = new HttpTestUrl(clurl1);
			HttpTest test1 = new HttpTest(httpTestUrl1);
			HttpTestHeader header1 = new HttpTestHeader();
			header1.addHeader("Version", "1.1");
			header1.addHeader("Authorization", URLDecoder.decode(cookie1.getValue()));
			test1.sendPostRequest(json1, null, header1);
			test1.waitRequestFinish(30000);
			if (test1.getResponseCode() == 200 && test1.getResponseContent().contains("\"success\":true")) {
				logger.boxLog(1, "修改主数据参数CL021成功", "参数CL021修改为：" + paramValue);
			} else {
				logger.assertFalse(true, "报错，请手动修改CL021参数为0", "请求地址: " + clurl1 + "\n\n\n请求状态: " + test1.getResponseCode() + "\n\n\n返回内容: "
						+ test1.getResponseContent());
			}
		}else {
			logger.boxLog(1, "修改主数据参数CL021成功", "参数CL021修改为：" + paramValue);
		}
	}

	/**
	 * 处置展示区操作按钮常显方式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  0 不显示鼠标划过显示 1 常显示。
	 */
	public void SetCL059(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304875917\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304875922\","
				+ "\"paramConfigId\":\"4304875921\",\"paramValue\":\"" + paramValue
				+ "\",\"paramEndValue\":null,\"activatedAt\":\"2021-03-19 16:59:56\"," + "\"valueDesc\":\"" + paramDesc
				+ "\"}],\"paramConfigId\":\"4304875921\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL059成功", "参数CL059修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	/**
	 * CL069是否启用处方绑定诊断模式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1,2（默认为0）
	 * @param paramDesc  0为不启动；1为处方不默认加载诊断模式；2为处方默认加载诊断模式。
	 */
	public void SetCL069(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304926836\",\"valueConfigs\":[{\"paramValueConfigId\":\"151479841513474049\","
				+ "\"paramConfigId\":\"151479841513474049\",\"paramValue\":\"" + paramValue
				+ "\",\"paramEndValue\":null,\"activatedAt\":\"2021-03-19 16:59:56\"," + "\"valueDesc\":\"" + paramDesc
				+ "\"}],\"paramConfigId\":\"151479841513474049\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL069成功", "参数CL069修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	/**
	 * CL070检验标本控制必填模式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  0：不控制 1：控制 默认值：0
	 */
	public void SetCL070(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304953470\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304953480\",\"paramConfigId\":\"4304953478\",\"paramValue\":\""
				+ paramValue + "\",\"paramEndValue\":\"\",\"activatedAt\":\"2021-06-08 10:27:36\",\"valueDesc\":\""
				+ paramDesc
				+ "\"}],\"paramConfigId\":\"4304953478\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL070成功", "参数CL070修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	/**
	 * CL074医嘱检索列表模式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  0 文本拼接模式 1表格模式 参数默认0
	 */
	public void SetCL074(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"4304953809\",\"valueConfigs\":[{\"paramValueConfigId\":\"4304953814\",\"paramConfigId\":\"4304953813\",\"paramValue\":\""
				+ paramValue +"\",\"paramEndValue\":\"\",\"activatedAt\":\"2021-06-30 17:49:38\",\"valueDesc\":\""
				+ paramDesc 
				+ "\"}],\"paramConfigId\":\"4304953813\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL074成功", "参数CL074修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}
	
	/**
	 * 诊间接诊相关弹出信息
	 *
	 * @param encounterId 患者encounterId
	 * @param bizRoleId   bizRoleId 返回一个boolean
	 */
	// 诊间接诊弹框信息
	public boolean getClinicCallPopUp(String encounterId, String bizRoleId) {
		boolean callpopupFlag = false;
		String url = "http://" + Data.host
				+ "/outpat-encounter/api/v1/encounter_cis/clinic_call_pop_up/query/by_example";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"encounterId\":\"" + encounterId + "\",\"bizRoleId\":\"" + bizRoleId
				+ "\",\"codes\":[\"399283633\",\"399283634\",\"399283808\",\"399295398\"],\"employeeId\":\"57393667239307269\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "成功", "1111");
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(test.getResponseContent()).getAsJsonObject();
			JsonElement popupFlag = null;
			JsonElement codeDesc = null;
			JsonArray dataArray = resJson.getAsJsonArray("data");
			for (JsonElement data : dataArray) {
				codeDesc = data.getAsJsonObject().get("codeDesc");
				popupFlag = data.getAsJsonObject().get("popupFlag");
				if (popupFlag.getAsInt() == 98175) {
					System.out.println("启用" + codeDesc);
					callpopupFlag = true;
				}
				if (popupFlag.getAsInt() == 98176) {
					System.out.println("不启用" + codeDesc);
					callpopupFlag = false;
				}
			}
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		return callpopupFlag;
	}

	/**
	 * 搜索医嘱并选择指定结果
	 *
	 * @param type 类型名称 西成药/草药/检查/检验/病理/治疗/模板
	 * @param name 输入框输入内容
	 * @param seq  选择第几个结果(从0开始) example: searchOrderBySeq("西成药", "葡萄糖", 0) 医生站界面点击
	 *             西成药类型 输入葡萄糖 选择第1个结果
	 */
	public void searchOrderBySeq(String type, String name, Integer seq) {
		searchOrderBySeq(type, name, seq, true);
		// 新增开立医嘱之后的操作，处理可能出现的场景
//		addDiagnoseIfNeed();
//		childrenInfoIfNeed();
//		pishiIfNeed();
//		editAndCommitOrder(null);
//		own_expense();
//		bloodPressureConfirmIfNeed(true);
	}

	public void searchOrderBySeq(String type, String name, Integer seq, Boolean checkResText) {
		name = name.trim();
		// 点击搜索框
		wnwd.waitElementByXpathAndClick(type + "按钮",
				WnOutpatientXpath.outpatientDisposalFactoryTemplateButton.replace("模板", type),
				Framework.defaultTimeoutMax);
		// 取消有库存勾选框
		if (type.contains("药")) {
			unselectSearchStock();
		}
		// 点击搜索框
		wnwd.waitElementByXpathAndClick(type + "按钮",
				WnOutpatientXpath.outpatientDisposalFactoryTemplateButton.replace("模板", type),
				Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		WebElement inputSearchOrderElement = wnwd.waitElementByXpathAndClick("医嘱搜索框",
				WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("医嘱搜索结果框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientRevokeLoading, Framework.defaultTimeoutMax);
		wnwd.sleep(1000);
		// 输入药品名
		wnwd.wnEnterText(inputSearchOrderElement, name, "医嘱搜索框");
		wnwd.checkElementByXpath("医嘱搜索结果框", WnOutpatientXpath.outpatientSearchOrderResultBox,
				Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
				Framework.defaultTimeoutMax);
		// 查找所有搜索结果
		List<WebElement> searchResults = wnwd.getElementListByXpath(WnOutpatientXpath.outpatientSearchOrderResultBox
				+ "//ul[contains(@class,'list')]//li[contains(@class,'search_item_name')]");
		// 搜索结果是空 直接报错
		if (searchResults.size() < seq + 1) {
			logger.assertFalse(true, "搜索结果条数少于" + (seq + 1) + "条!");
		}
		WebElement resultLine = searchResults.get(seq);
		logger.log(1, "找到医嘱项: " + resultLine.getText());
		logger.assertFalse((checkResText && !resultLine.getText().contains(name)) && (checkResText && !resultLine.getText().replace("丫", "吖").contains(name)) && (checkResText && !resultLine.getText().replace("吖", "丫").contains(name)), "搜索医嘱异常,搜索结果中不包含医嘱名称:" + name);
		wnwd.wnClickElement(resultLine, "医嘱搜索结果");
	}

	// 选择医嘱后,医嘱加工厂打开前,需要做的通用操作
	public void beforeFactory() {
		addDiagnoseIfNeed();
		childrenInfoIfNeed();
		pishiIfNeed();
	}

	// 确认开立后,检查到开立成功前,需要做的操作
	public void afterFactory() {
		afterFactory(null);
	}

	public void afterFactory(Boolean check_own_expense) {
		own_expense(check_own_expense);
	}

	// 点击加工厂确认按钮并等待加工厂关闭
	public void commitFactory() {
		try {

			wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton,
					Framework.defaultTimeoutMax);
			wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", WnOutpatientXpath.outpatientDisposalFactory,
					Framework.defaultTimeoutMax);
			wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder, Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "确认开立失败", e.getMessage());
		}
	}

	// 检查西药加工厂内容
	public void checkDrugFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String factoryType = getDetailType();
			logger.assertFalse(!factoryType.equals("drug"), "西药加工厂未打开");
			// 获取加厂默认值
			// 默认药房
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'药房')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 数量输入框
			if (detail.num != null) {
				WebElement ele = wnwd.waitElementByXpath("西药数量输入框",
						WnOutpatientXpath.outpatientDisposalFactoryDrugNumInput, Framework.defaultTimeoutMax);
				String num = ele.getAttribute("value");
				logger.log(num);
				if (!detail.num.toString().equals(num)) {
					logger.assertFalse(true, "数量结果不对", "数量结果预期为:"+detail.num+"，实际为:" + num);
				}
			}

			// 天数输入框
			if (detail.Days != null) {
				WebElement ele = wnwd.waitElementByXpath("西药天数输入框",
						WnOutpatientXpath.outpatientDisposalFactoryDrugDaysInput, Framework.defaultTimeoutMax);
				String days = ele.getAttribute("value");
				logger.log(days);
				if (!detail.Days.toString().equals(days)) {
					logger.assertFalse(true, "天数结果不对", "天数结果预期为:"+detail.Days+"，实际为:" + days);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "检查西药加工厂失败", e.getMessage());
		}
	}

	// 检查草药加工厂
	public void checkHerbFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("herb"), "草药加工厂未打开");
			// 获取加工厂默认值
			// 默认药房
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'药房')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 中草药编辑
			WebElement herbName = wnwd
					.getElementByXpath("//div[@class='disposal__factory']//li//span[@class='herbal-name']");
			if (herbName != null) {
				wnwd.wnClickElement(herbName, "药品名称");
			}
			wnwd.waitElementByXpathAndClick("中草药保存按钮", WnOutpatientXpath.outpatientDisposalFactoryHerbNumSave,
					Framework.defaultTimeoutMax);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑草药加工厂失败", e.getMessage());
		}
	}

	// 检查检验加工厂内容
	public void checkLabFactory(PrescribeDetail detail) {
		try {
			// 判断加工厂是否打开
			String factoryType = getDetailType();
			logger.assertFalse(!factoryType.equals("lab"), "检验加工厂未打开");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 数量输入框
			if (detail.num != null) {
				WebElement ele = wnwd.waitElementByXpath("检验数量输入框",
						WnOutpatientXpath.outpatientDisposalFactoryLabNumInput, Framework.defaultTimeoutMax);
				String num = ele.getAttribute("value");
				logger.log(num);
				if (!detail.num.toString().equals(num)) {
					logger.assertFalse(true, "数量结果不对", "数量结果为:" + num);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "检查检验加工厂失败", e.getMessage());
		}
	}
	
	// 编辑西药加工厂
	public PrescribeDetail editDrugFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String factoryType = getDetailType();
			logger.assertFalse(!factoryType.equals("drug"), "西药加工厂未打开");
			// 获取加厂默认值
			// 默认药房
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'药房')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}

			// 剂量输入框
			if (detail.dose != null) {
				wnwd.waitElementByXpathAndInput("西药剂量输入框", WnOutpatientXpath.outpatientDisposalFactoryDrugDoseInput,
						detail.dose.toString(), Framework.defaultTimeoutMax);
				WebElement ele = wnwd.waitElementByXpath("药品名称", WnOutpatientXpath.outpatientDisposalFactoryDrugName,
						Framework.defaultTimeoutMax);
				wnwd.wnClickElementByMouse(ele, "药品名称");
			}

			// 给药途径下拉框
			if (detail.route != null) {
				wnwd.waitElementByXpathAndClick("西成药给药途径下拉框",
						"//div[@class='disposal__factory']//ul[contains(@class,'selected_drug')]//li[contains(@class,'item')][3]//i",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.route,
						"//ul/li[.='" + detail.route + "' or starts-with(.,'" + detail.route + "')]",
						Framework.defaultTimeoutMax);
			}
			// 频次下拉框
			if (detail.freq != null) {
				wnwd.waitElementByXpathAndClick("西成药频次下拉框",
						"//div[@class='disposal__factory']//ul[contains(@class,'selected_drug')]//li[contains(@class,'item')][4]//i",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.freq,
						"//ul/li[.='" + detail.freq + "' or starts-with(.,'" + detail.freq + "')]",
						Framework.defaultTimeoutMax);
				// 有可能弹出剂量编辑的弹框，弹框很快，3秒
				WebElement WanChengAfterEditFreq = wnwd
						.waitElementByXpath(WnOutpatientXpath.outpatientDisposalFactoryWanChengAfterEditFreq, 5000);
				if (WanChengAfterEditFreq != null)
					wnwd.wnClickElement(WanChengAfterEditFreq, "编辑西药频次后的剂量弹框的完成按钮");
			}

			// 天数输入框
			if (detail.Days != null) {
				wnwd.waitElementByXpathAndInput("西药天数输入框", WnOutpatientXpath.outpatientDisposalFactoryDrugDaysInput,
						detail.Days.toString(), Framework.defaultTimeoutMax);
				WebElement ele = wnwd.waitElementByXpath("药品名称", WnOutpatientXpath.outpatientDisposalFactoryDrugName,
						Framework.defaultTimeoutMax);
				wnwd.wnClickElementByMouse(ele, "药品名称");
			}

			// 数量输入框
			if (detail.num != null) {
				wnwd.waitElementByXpathAndInput("西药数量输入框", WnOutpatientXpath.outpatientDisposalFactoryDrugNumInput,
						detail.num.toString(), Framework.defaultTimeoutMax);
				WebElement ele = wnwd.waitElementByXpath("药品名称", WnOutpatientXpath.outpatientDisposalFactoryDrugName,
						Framework.defaultTimeoutMax);
				wnwd.wnClickElementByMouse(ele, "药品名称");
			}

			// 成组药品名称
			if (detail.groupMedicineName != null) {
				for (String medicineName : detail.groupMedicineName) {
					WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
							WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
					inputSearchOrderElement.clear();
//					wnwd.waitElementByXpathAndClick("医嘱搜索框",
//							WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
//					wnwd.checkElementByXpath("医嘱搜索结果", WnOutpatientXpath.outpatientSearchOrderResultBox,
//							Framework.defaultTimeoutMax);
//					wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
//							Framework.defaultTimeoutMax);
					wnwd.sleep(1000);
					wnwd.wnEnterText(inputSearchOrderElement, medicineName, "医嘱搜索框");
					WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框",
							WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
					wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
							Framework.defaultTimeoutMax);
					// 查找所有搜索结果
					List<WebElement> searchResults = resultBox
							.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
					// 搜索结果是空 直接报错
					if (searchResults == null || searchResults.size() == 0) {
						logger.assertFalse(true, "60无搜索结果", "未找到此西药:" + medicineName);
					}
					wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
					wnwd.checkElementByXpath("已开立药品名称:" + medicineName,
							"//div[@class='factory_west_wrap']//ul[contains(@class,'disposal__factory--list') and contains(.,'"
									+ medicineName + "')]",
							Framework.defaultTimeoutMax);
				}
			}

			// 特殊说明下拉框
			if (detail.specialDesc != null) {
				wnwd.waitElementByXpathAndClick("特殊说明下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'特殊说明')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.specialDesc,
						"//ul/li[.='" + detail.specialDesc + "' or starts-with(.,'" + detail.specialDesc + "')]",
						Framework.defaultTimeoutMax);
			}

			// 院内数量输入框,要放在数量输入框和成组的操作的后面
			if (detail.InHospitalNum != null) {
				wnwd.waitElementByXpathAndInput("西药院内数量输入框",
						WnOutpatientXpath.outpatientDisposalFactoryDrugInHospitalNumInput,
						detail.InHospitalNum.toString(), Framework.defaultTimeoutMax);
				WebElement ele = wnwd.waitElementByXpath("药品名称", WnOutpatientXpath.outpatientDisposalFactoryDrugName,
						Framework.defaultTimeoutMax);
				wnwd.wnClickElementByMouse(ele, "药品名称");
			}

			// 用法说明下拉框
			if (detail.usageDesc != null) {
				wnwd.waitElementByXpathAndClick("药房下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'用法说明')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.usageDesc,
						"//ul/li[.='" + detail.usageDesc + "' or starts-with(.,'" + detail.usageDesc + "')]",
						Framework.defaultTimeoutMax);
			}

			// 药房下拉框
			if (detail.pharmacy != null) {
				wnwd.waitElementByXpathAndClick("药房下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'药房')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.pharmacy,
						"//ul/li[.='" + detail.pharmacy + "' or starts-with(.,'" + detail.pharmacy + "')]",
						Framework.defaultTimeoutMax);
			}

			// 嘱托输入框
			if (detail.entrust != null) {
				wnwd.waitElementByXpathAndInput("嘱托输入框",
						"//div[contains(@class,'el-form-item') and contains(.,'嘱托')]//input", detail.entrust,
						Framework.defaultTimeoutMax);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='特殊病']",
						detail.specialDisease);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='自费']",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='慢病']",
						detail.chronicDisease);
			}
			// 外配勾选框
			if (detail.extProvision != null) {
				selectCheckBox("外配勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='外配']",
						detail.extProvision);
			}
			// 自备药勾选框
			if (detail.self != null) {
				selectCheckBox("自备药勾选框", "//div[contains(@class,'selfProvide')]//label", detail.self);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑西药加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	// 编辑草药加工厂
	public PrescribeDetail editHerbFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("herb"), "草药加工厂未打开");
			// 获取加工厂默认值
			// 默认药房
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'药房')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 中草药编辑
			WebElement herbName = wnwd
					.getElementByXpath("//div[@class='disposal__factory']//li//span[@class='herbal-name']");
			if (herbName != null) {
				wnwd.wnClickElement(herbName, "药品名称");
			}
			// 数量输入框
			if (detail.num != null) {
				wnwd.waitElementByXpathAndInput("中草药剂量输入框", WnOutpatientXpath.outpatientDisposalFactoryHerbNumInput,
						detail.num.toString(), Framework.defaultTimeoutMax);
			}
			// 给药途径下拉框
			if (detail.route != null) {
				wnwd.waitElementByXpathAndClick("中草药给药途径下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'给药途径')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.route,
						"//ul/li[.='" + detail.route + "' or starts-with(.,'" + detail.route + "')]",
						Framework.defaultTimeoutMax);
			}
			// 频次下拉框
			if (detail.freq != null) {
				wnwd.waitElementByXpathAndClick("中草药频次下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'频次')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.freq,
						"//ul/li[.='" + detail.freq + "' or starts-with(.,'" + detail.freq + "')]",
						Framework.defaultTimeoutMax);
			}
			// 药房下拉框
			if (detail.pharmacy != null) {
				wnwd.waitElementByXpathAndClick("药房下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'药房')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.pharmacy,
						"//ul/li[.='" + detail.pharmacy + "' or starts-with(.,'" + detail.pharmacy + "')]",
						Framework.defaultTimeoutMax);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='自费']",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='慢病']",
						detail.chronicDisease);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='特殊病']",
						detail.specialDisease);
			}
			// 代煎勾选框
			if (detail.decoct != null) {
				selectCheckBox("代煎勾选框",
						"//div[@class='disposal__factory']//div[contains(.,'代煎')]/label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]",
						detail.decoct);
			}
			// 代煎方式下拉框
			if (detail.decoctMethod != null) {
				wnwd.waitElementByXpathAndClick("代煎方式下拉框",
						"//div[contains(@class,'el-form-item') and contains(.,'代煎方式')]//i", Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick(detail.decoctMethod,
						"//ul/li[.='" + detail.decoctMethod + "' or starts-with(.,'" + detail.decoctMethod + "')]",
						Framework.defaultTimeoutMax);
			}
			wnwd.waitElementByXpathAndClick("中草药保存按钮", WnOutpatientXpath.outpatientDisposalFactoryHerbNumSave,
					Framework.defaultTimeoutMax);
			
			// 批量中草药开立
			if (detail.groupHerb != null) {
				for (Map<String,String> herb : detail.groupHerb) {
					WebElement inputSearchOrderElement = wnwd.waitElementByXpath("医嘱搜索框",
							WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
					inputSearchOrderElement.clear();
					wnwd.sleep(1000);
					wnwd.wnEnterText(inputSearchOrderElement, herb.get("name"), "医嘱搜索框");
					wnwd.waitElementByXpathAndClick("医嘱搜索框",
							WnOutpatientXpath.outpatientSearchOrderInput, Framework.defaultTimeoutMax);
					WebElement resultBox = wnwd.checkElementByXpath("医嘱搜索结果展示框",
							WnOutpatientXpath.outpatientSearchOrderResultBox, Framework.defaultTimeoutMax);
					wnwd.waitNotExistByXpath("等待加载完成", WnOutpatientXpath.outpatientSearchOrderLoadingBox,
							Framework.defaultTimeoutMax);
					// 查找所有搜索结果
					List<WebElement> searchResults = resultBox
							.findElements(By.xpath(WnOutpatientXpath.outpatientSearchOrderAllResultList));
					// 搜索结果是空 直接报错
					if (searchResults == null || searchResults.size() == 0) {
						logger.assertFalse(true, "60无搜索结果", "未找到此中草药:" + herb.get("name"));
					}
					wnwd.wnClickElement(searchResults.get(0), "搜索结果第一个");
					wnwd.waitElementByXpathAndInput("中草药剂量输入框", WnOutpatientXpath.outpatientDisposalFactoryHerbNumInput,
							herb.get("dose")==null? "1" : herb.get("dose"), Framework.defaultTimeoutMax);
			         wnwd.waitElementByXpathAndClick("中草药保存按钮", WnOutpatientXpath.outpatientDisposalFactoryHerbNumSave,
					Framework.defaultTimeoutMax);
					wnwd.checkElementByXpath("已开立药品名称:" + herb.get("name"),
							"//ul[contains(@class,'has-checked-herbal-main')]//span[@class='herbal-name'  and contains(.,'"+herb.get("name")+"')]",
							Framework.defaultTimeoutMax);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑草药加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	// 编辑检验加工厂
	public PrescribeDetail editLabFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("lab"), "检验加工厂未打开");
			// 获取加工厂默认值
			// 默认执行科室
			default_detail.pharmacy = wnwd
					.getElementByXpath("//p[contains(@class,'exe-style') and contains(.,'执行科室')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 数量输入框
			if (detail.num != null) {
				wnwd.waitElementByXpathAndInput("检验数量输入框", WnOutpatientXpath.outpatientDisposalFactoryLabNumInput,
						detail.num.toString(), Framework.defaultTimeoutMax);
				wnwd.sendKeyEvent(Keys.ENTER);
				wnwd.sleep(1000);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='自费']",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='慢病']",
						detail.chronicDisease);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='特殊病']",
						detail.specialDisease);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑检验加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	// 编辑治疗加工厂
	public PrescribeDetail editTreatFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("treatment"), "治疗加工厂未打开");
			// 获取加工厂默认值
			// 默认执行科室
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'执行科室')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 数量输入框
			if (detail.num != null) {
				wnwd.waitElementByXpathAndInput("治疗数量输入框", WnOutpatientXpath.outpatientDisposalFactoryTreatNumInput,
						detail.num.toString(), Framework.defaultTimeoutMax);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='自费']",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='慢病']",
						detail.chronicDisease);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='特殊病']",
						detail.specialDisease);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑治疗加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	// 编辑病理加工厂
	public PrescribeDetail editPathologyFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("pathology"), "病理加工厂未打开");
			// 获取加工厂默认值
			// 默认执行科室
			default_detail.pharmacy = wnwd
					.getElementByXpath("//div[contains(@class,'pathology-form') and contains(.,'执行科室')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 临床摘要输入框
			if (detail.clinicalSummary != null) {
				wnwd.waitElementByXpathAndInput("输入临床摘要", "//label[.='临床摘要']/..//textarea", detail.clinicalSummary,
						Framework.defaultTimeoutMax);
			}
			// 新增按钮
			if (detail.addButton != null && detail.addButton) {
				wnwd.waitElementByXpathAndClick("增加标本", "//p[@class='spec-add cursor_point']",
						Framework.defaultTimeoutMax);
			}
			// 部位下拉框
			if (detail.positionIndex != null) {
				wnwd.waitElementByXpathAndClick("部位下拉框", "//div[@class='disposal__factory']//tr/td[2]//input",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("第一个部位",
						"//ul[@class='el-scrollbar__view el-select-dropdown__list']/li[" + detail.positionIndex
								+ "]//span",
						Framework.defaultTimeoutMax);
			}
			// 标本下拉框
			if (detail.sampleIndex != null) {
				wnwd.waitElementByXpathAndClick("标本下拉框", "//div[@class='disposal__factory']//tr/td[3]//input",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("第一个标本",
						"//ul[@class='el-scrollbar__view el-select-dropdown__list']/li[" + detail.sampleIndex
								+ "]//span",
						Framework.defaultTimeoutMax);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//p[contains(.,'自费')]/label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//p[contains(.,'慢病')]/label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]",
						detail.chronicDisease);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//p[contains(.,'特殊病')]/label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]",
						detail.specialDisease);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑病理加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	// 编辑检查加工厂
	public PrescribeDetail editExamFactory(PrescribeDetail detail) {
		PrescribeDetail default_detail = new PrescribeDetail();
		try {
			// 判断加工厂是否打开
			String detailType = getDetailType();
			logger.assertFalse(!detailType.equals("exam"), "检查加工厂未打开");
			// 获取加工厂默认值
			// 默认执行科室
			default_detail.pharmacy = wnwd
					.getElementByXpath("//p[contains(@class,'inspect-exe') and contains(.,'执行科室')]//input")
					.getAttribute("value");

			// detail为空时不进行任何操作
			if (detail == null) {
				detail = new PrescribeDetail();
			}
			// 体征输入框
			if (detail.bodySign != null) {
				wnwd.waitElementByXpathAndInput("体征输入框",
						"//div[contains(@class,'el-form-item') and contains(.,'体征')]//input", detail.bodySign,
						Framework.defaultTimeoutMax);
			} else {
				WebElement tizhengElement = wnwd
						.getElementByXpath("//div[contains(@class,'el-form-item') and contains(.,'体征')]//input");
				if (tizhengElement != null) {
					wnwd.wnEnterText(tizhengElement, "默认体征", "体征输入框");
				}
			}
			// 临床摘要输入框
			if (detail.clinicalSummary != null) {
				wnwd.waitElementByXpathAndInput("摘要输入框", WnOutpatientXpath.outpatientDisposalFactoryExamSummaryInput,
						detail.clinicalSummary, Framework.defaultTimeoutMax);
			}
			// 自费勾选框
			if (detail.selfPay != null) {
				selectCheckBox("自费勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='自费']",
						detail.selfPay);
			}
			// 慢病勾选框
			if (detail.chronicDisease != null) {
				selectCheckBox("慢病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='慢病']",
						detail.chronicDisease);
			}
			// 特殊病勾选框
			if (detail.specialDisease != null) {
				selectCheckBox("特殊病勾选框",
						"//div[@class='disposal__factory']//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled')) and .='特殊病']",
						detail.specialDisease);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.assertFalse(true, "编辑检查加工厂失败", e.getMessage());
		}
		return default_detail;
	}

	/**
	 * 选择勾选框或取消勾选
	 *
	 * @param desc     选择框描述
	 * @param xpath    选择框xpath
	 * @param ifSelect 是否要勾选 example: selectCheckBox("自费",xpath,false)
	 *                 1.根据xpath找到选择框 2.如果是已勾选状态,取消勾选 3.如果是未勾选状态,不操作
	 */
	public void selectCheckBox(String desc, String xpath, Boolean ifSelect) {
		WebElement item = wnwd.checkElementByXpath(desc, xpath, Framework.defaultTimeoutMax, false);
		if (ifSelect && !item.getAttribute("class").contains("is-checked")) {
			// 要勾选而默认没勾选
			wnwd.wnClickElement(item, "勾选" + desc);
		}
		if (!ifSelect && item.getAttribute("class").contains("is-checked")) {
			// 不要勾选而默认已勾选
			wnwd.wnClickElement(item, "取消勾选" + desc);
		}
	}

	// 传入csid 判断病理是否可以开立 可以开立返回null 不能开立返回原因(String)
	public String checkPathologyAvailable(String id) {
		try {
			String url = "http://" + Data.host
					+ "/clinical-mdm/api/v1/app_clinical_mdm/clinical_service_pathology/query/all_by_cs_id";
			String param = "{\"csIdList\":[\"" + id + "\"]}";
			HttpTestHeader header = new HttpTestHeader();
			header.addHeader("Version", "1.1");
			header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
			HttpTest httpTester = SdkTools.post("查询临床医嘱详情(主数据)", url, header, param, null, logger);
			if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
				JsonParser parser = new JsonParser();
				JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
				JsonElement data = resJson.getAsJsonArray("data").get(0);
				;
				if (!data.getAsJsonObject().get("csStatus").getAsString().equals("98360")) {
					throw new Error("服务未启用");
				}
				if (!data.getAsJsonObject().get("orderFlag").getAsString().equals("98175")) {
					throw new Error("服务医嘱标识未勾选");
				}
				if (data.getAsJsonObject().get("encounterTypeCodeList").toString().equals("null")) {
//    				logger.log("encounterTypeCodeList是null,默认是门诊");
//                	logger.log(""+(data.getAsJsonObject().get("123")==null));
				} else {
					Boolean outpEncflag = false;
					JsonArray encounterTypeCodeList = data.getAsJsonObject().get("encounterTypeCodeList")
							.getAsJsonArray();
					for (JsonElement encounterTypeCode : encounterTypeCodeList) {
//    					logger.log(""+encounterTypeCode);
						if (encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc") != null
								&& encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc").getAsString()
										.equals("门诊")) {
							outpEncflag = true;
						}
					}
					if (!outpEncflag) {
						throw new Error("服务就诊类型中不包含门诊");
					}
				}
			} else {
				throw new Error("主数据接口查询临床医嘱详情失败!");
			}
		} catch (Throwable e) {
			String msgString = "csId(?id)不可开立: ?msg".replace("?id", id).replace("?msg", e.getMessage());
			logger.log(3, msgString);
			return msgString;
		}
		return null;
	}

	// 传入csid 判断检验是否可以开立 可以开立返回null 不能开立返回原因(String)
	public String checkLabAvailable(String id) {
		try {
			String url = "http://" + Data.host + "/clinical-mdm/api/v1/app_clinical_mdm/cs_labtest/query/all_by_cs_id";
			String param = "{\"csIdList\":[\"" + id + "\"]}";
			HttpTestHeader header = new HttpTestHeader();
			header.addHeader("Version", "1.1");
			header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
			HttpTest httpTester = SdkTools.post("查询临床医嘱详情(主数据)", url, header, param, null, logger);
			if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
				JsonParser parser = new JsonParser();
				JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
				JsonElement data = resJson.getAsJsonArray("data").get(0);
				;
				if (!data.getAsJsonObject().get("csStatus").getAsString().equals("98360")) {
					throw new Error("服务未启用");
				}
				if (!data.getAsJsonObject().get("orderFlag").getAsString().equals("98175")) {
					throw new Error("服务医嘱标识未勾选");
				}
				if (!data.getAsJsonObject().get("encounterTypeCodeList").toString().equals("null")) {
					Boolean outpEncflag = false;
					JsonArray encounterTypeCodeList = data.getAsJsonObject().get("encounterTypeCodeList")
							.getAsJsonArray();
					for (JsonElement encounterTypeCode : encounterTypeCodeList) {
						if (encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc") != null
								&& encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc").getAsString()
										.equals("门诊")) {
							outpEncflag = true;
						}
					}
					if (!outpEncflag) {
						throw new Error("服务就诊类型中不包含门诊");
					}
				}
			} else {
				throw new Error("主数据接口查询临床医嘱详情失败!");
			}
		} catch (Throwable e) {
			String msgString = "csId(?id)不可开立: ?msg".replace("?id", id).replace("?msg", e.getMessage());
			logger.log(3, msgString);
			return msgString;
		}
		return null;
	}

	// 传入csid 判断治疗是否可以开立 可以开立返回null 不能开立返回原因(String)
	public String checkTreatAvailable(String id) {
		try {
			String url = "http://" + Data.host
					+ "/clinical-mdm/api/v1/app_clinical_mdm/cs_treatment/query/all_by_cs_id";
			String param = "{\"csIdList\":[\"" + id + "\"]}";
			HttpTestHeader header = new HttpTestHeader();
			header.addHeader("Version", "1.1");
			header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
			HttpTest httpTester = SdkTools.post("查询临床医嘱详情(主数据)", url, header, param, null, logger);
			if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
				JsonParser parser = new JsonParser();
				JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
				JsonElement data = resJson.getAsJsonArray("data").get(0);
				if (!data.getAsJsonObject().get("csStatus").getAsString().equals("98360")) {
					throw new Error("服务未启用");
				}
				if (!data.getAsJsonObject().get("orderFlag").getAsString().equals("98175")) {
					throw new Error("服务医嘱标识未勾选");
				}
				if (!data.getAsJsonObject().get("encounterTypeCodeList").toString().equals("null")) {
					Boolean outpEncflag = false;
					JsonArray encounterTypeCodeList = data.getAsJsonObject().get("encounterTypeCodeList")
							.getAsJsonArray();
					for (JsonElement encounterTypeCode : encounterTypeCodeList) {
						if (encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc") != null
								&& encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc").getAsString()
										.equals("门诊")) {
							outpEncflag = true;
						}
					}
					if (!outpEncflag) {
						throw new Error("服务就诊类型中不包含门诊");
					}
				}
			} else {
				throw new Error("主数据接口查询临床医嘱详情失败!");
			}
		} catch (Throwable e) {
			String msgString = "csId(?id)不可开立: ?msg".replace("?id", id).replace("?msg", e.getMessage());
			logger.log(3, msgString);
			return msgString;
		}
		return null;
	}

	// 传入csid 判断检查是否可以开立 可以开立返回null 不能开立返回原因(String)
	public String checkExamAvailable(String id) {
		try {
			String url = "http://" + Data.host + "/clinical-mdm/api/v1/app_clinical_mdm/cs_exam/query/all_by_cs_id";
			String param = "{\"csIdList\":[\"" + id + "\"]}";
			HttpTestHeader header = new HttpTestHeader();
			header.addHeader("Version", "1.1");
			header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
			HttpTest httpTester = SdkTools.post("查询临床医嘱详情(主数据)", url, header, param, null, logger);
			if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
				JsonParser parser = new JsonParser();
				JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
				JsonElement data = resJson.getAsJsonArray("data").get(0);
				;
				if (!data.getAsJsonObject().get("csStatus").getAsString().equals("98360")) {
					throw new Error("服务未启用");
				}
				if (!data.getAsJsonObject().get("orderFlag").getAsString().equals("98175")) {
					throw new Error("服务医嘱标识未勾选");
				}
				if (!data.getAsJsonObject().get("encounterTypeCodeList").toString().equals("null")) {
					Boolean outpEncflag = false;
					JsonArray encounterTypeCodeList = data.getAsJsonObject().get("encounterTypeCodeList")
							.getAsJsonArray();
					for (JsonElement encounterTypeCode : encounterTypeCodeList) {
						if (encounterTypeCode.getAsJsonObject().get("encounterTypeCodeDesc") != null
								&& encounterTypeCode.getAsJsonObject().get("encounterTypeCode").getAsString()
										.equals("138138")) {
							outpEncflag = true;
						}
					}
					if (!outpEncflag) {
						throw new Error("服务就诊类型中不包含门诊");
					}
				}
			} else {
				throw new Error("主数据接口查询临床医嘱详情失败!");
			}
		} catch (Throwable e) {
			String msgString = "csId(?id)不可开立: ?msg".replace("?id", id).replace("?msg", e.getMessage());
			logger.log(3, msgString);
			return msgString;
		}
		return null;
	}

	// 获取分方规则列表
	public JsonArray getRecipeAllocationRuleList() {
		String url = "http://" + Data.host
				+ "/clinical-mdm/api/v1/app_clinical_mdm/recipe_allocation_list/query/by_example";
		String param = "{}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("查询分方规则列表(主数据)", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray ruleList = resJson.getAsJsonArray("data");
			return ruleList;
		} else {
			logger.assertFalse(true, "查询分方规则列表(主数据)失败!");
		}
		return null;
	}

	// 根据序号获取分方规则
	public JsonObject getRecipeAllocationRuleByPriority(Integer priority) {
		JsonObject target_rule = null;
		JsonArray ruleList = getRecipeAllocationRuleList();
		for (JsonElement rule : ruleList) {
			if (rule.getAsJsonObject().get("priority").getAsInt() == priority) {
				target_rule = rule.getAsJsonObject();
			}
		}
		return target_rule;
	}

	// 禁用全部分方规则
	public void allocationRule_saveAll(Boolean enabledFlag, Boolean cliOrderGroupFirstFlag) {
		JsonArray ruleList = getRecipeAllocationRuleList();
		for (JsonElement rule : ruleList) {
			rule.getAsJsonObject().addProperty("enabledFlag", enabledFlag ? "98175" : "98176");
			rule.getAsJsonObject().addProperty("cliOrderGroupFirstFlag", cliOrderGroupFirstFlag ? "98175" : "98176");
			String url = "http://" + Data.host + "/clinical-mdm/api/v1/app_clinical_mdm/recipe_allocation_rule/save";
			String param = rule.getAsJsonObject().toString();
			HttpTestHeader header = new HttpTestHeader();
			header.addHeader("Version", "1.1");
			header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
			String desc = "修改分方规则:" + rule.getAsJsonObject().get("recipeAllocRuleCodeDesc") + "  -->  "
					+ (enabledFlag ? "启用" : "停用") + "/" + (cliOrderGroupFirstFlag ? "勾选成组优先" : "不勾选成组优先");
			HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
			if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
				logger.assertFalse(true, "修改分方规则失败");
			}
		}
	}

	// 修改单条分方规则
	public void allocationRule_saveByPriority(Integer priority, Boolean enabledFlag, Boolean cliOrderGroupFirstFlag) {
		JsonObject rule = getRecipeAllocationRuleByPriority(priority);
		rule.getAsJsonObject().addProperty("enabledFlag", enabledFlag ? "98175" : "98176");
		rule.getAsJsonObject().addProperty("cliOrderGroupFirstFlag", cliOrderGroupFirstFlag ? "98175" : "98176");
		String url = "http://" + Data.host + "/clinical-mdm/api/v1/app_clinical_mdm/recipe_allocation_rule/save";
		String param = rule.getAsJsonObject().toString();
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String desc = "修改分方规则:" + rule.getAsJsonObject().get("recipeAllocRuleCodeDesc") + "  -->  "
				+ (enabledFlag ? "启用" : "停用") + "/" + (cliOrderGroupFirstFlag ? "勾选成组优先" : "不勾选成组优先");
		HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
		if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
			logger.assertFalse(true, "修改分方规则失败");
		}
	}

	// 修改单条分方规则详情
	public void allocationRuleAll_saveByPriority(Integer priority, Boolean enabledFlag, Boolean cliOrderGroupFirstFlag,
			List<AllocationCondition> conditionList) {
		if (conditionList == null) {
			conditionList = Arrays.asList();
		}
		JsonObject rule = getRecipeAllocationRuleByPriority(priority);
		String ruleId = rule.getAsJsonObject().get("recipeAllocRuleId").getAsString();
		rule.getAsJsonObject().addProperty("enabledFlag", enabledFlag ? "98175" : "98176");
		rule.getAsJsonObject().addProperty("cliOrderGroupFirstFlag", cliOrderGroupFirstFlag ? "98175" : "98176");
		// 添加规则字段
		JsonParser parser = new JsonParser();
		JsonArray conditions = parser.parse("[]").getAsJsonArray();
		for (AllocationCondition condition : conditionList) {
			JsonObject c = new JsonObject();
			c.addProperty("recipeAllocRuleId", ruleId);
			c.addProperty("priority", condition.priority);
			c.addProperty("recipeAllocConditShortName", condition.shortName.code);
			c.addProperty("regularOperationCode", condition.OperationCode.code);
			JsonArray values = parser.parse("[]").getAsJsonArray();
			for (String valueCode : condition.valueList) {
				JsonObject value = parser.parse("{recipeAllocConditId: \"\", recipeAllocMatchValue: \"" + valueCode
						+ "\", recipeAllocMatchValueId: \"\"}").getAsJsonObject();
				values.add(value);
			}
			c.add("allocConditionValueList", values);
			conditions.add(c);
		}
		rule.add("allocationConditionList", conditions);
		String url = "http://" + Data.host + "/clinical-mdm/api/v1/app_clinical_mdm/recipe_allocation_rule_all/save";
		String param = rule.getAsJsonObject().toString();
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String desc = "修改分方规则:" + rule.getAsJsonObject().get("recipeAllocRuleCodeDesc") + "  -->  "
				+ (enabledFlag ? "启用" : "停用") + "/" + (cliOrderGroupFirstFlag ? "勾选成组优先" : "不勾选成组优先");
		HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
		if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
			logger.assertFalse(true, "修改分方规则失败");
		}
	}

	// 获取处方列表,返回处方条数/每条处方明细数量/每条处方包含的药品名称
	public List<Map<String, String>> getRecipeList() {
		List<Map<String, String>> recipeList = new ArrayList<Map<String, String>>();
		WebElement rpbp = wnwd.waitElementByXpath("费用信息框", "//div[@class='rpbp rpbpShow']", Framework.defaultTimeoutMin);
		if (rpbp == null) {
			wnwd.waitElementByXpathAndClick("展开收费列表按钮", WnOutpatientXpath.outpatientDisposalTitleShowRpBpButton,
					Framework.defaultTimeoutMax);
		}
		List<WebElement> recipes = wnwd.waitElementListByXpath(
				"//div[@class='rpbp rpbpShow']//ul[@class='chufang-list']//li", Framework.defaultTimeoutMax);
		for (WebElement recipe : recipes) {
			String diagnoseName = recipe.findElements(By.xpath(".//span[@class='rpbp-diagosis']")).get(0).getText();
			String title = recipe.findElements(By.xpath(".//p[@class='rpbp-list-title']")).get(0).getText();
			List<WebElement> medList = recipe.findElements(By.xpath(".//span[@class='rpbp-detail-list']"));
			String count = "" + medList.size();
			String medName = "\n";
			for (WebElement med : medList) {
				medName += med.getText() + "\n";
			}
			Map<String, String> recipeMap = new HashMap<String, String>();
			recipeMap.put("title", title);
			recipeMap.put("count", count);
			recipeMap.put("medNames", medName);
			recipeMap.put("diagnoses", diagnoseName);
			recipeList.add(recipeMap);
		}
		for (int i = 0; i < recipeList.size(); i++) {
			logger.log(1, "第" + (i + 1) + "条处方:\n" + recipeList.get(i));
		}
		return recipeList;
	}

	// 获取注射药品列表
	public JsonArray getInjectMedinces() {
		// 从浏览器Cookie中获取
		String doctorId = getLoginInfoFromCookies().get("userId").getAsString();
		// 调用搜索医嘱搜索接口
		String url = "http://" + Data.host + "/outpat/api/v4/cis/clinical_service/search";
//		String param = "{\"keyword\":\"\",\"type\":\"8\",\"pageNo\":0,\"pageSize\":100,\"doctorId\":\"" + doctorId
//				+ "\",\"pharmacyId\":\"\",\"isAllowCancel\":true,\"axiosModel\":\"clinicalOrder_search_loadSearchList\"}";
		String param = "{\"keyword\":\"\",\"type\":\"8\",\"pageNo\":0,\"doctorId\":\"" + doctorId + "\",\"pharmacyId\":\"\",\"externalProcessingFlag\":\"\",\"isAllowCancel\":true," +
				"\"axiosModel\":\"clinicalOrder_search_loadSearchList\",\"pageSize\":20,\"filterType\":1,\"matchPy\":1,\"matchWb\":null,\"matchCode\":1,\"matchName\":1,\"afterEsId\":null}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("搜索注射药品", url, header, param, null, logger);
		// 解析请求结果
		JsonArray datas = new JsonArray();
		int seq = 0;
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray dataArray = resJson.getAsJsonArray("data");
			logger.assertFalse(dataArray.size() < 1, "搜索无结果,不到注射药品");
			for (JsonElement data : dataArray) {
				try {
					if (data.getAsJsonObject().get("tipsType").getAsString().equals("0")) {
						datas.add(data);
					}
				} catch (Throwable e) {
					SdkTools.logger.log(1, "搜索临床服务返回:" + data);
					SdkTools.logger.log(1, "返回服务中没有字段:tipsType");
				}
				seq++;
			}
		} else {
			logger.assertFalse(true, "请求医嘱列表失败");
		}
		return datas;
	}

	// 主数据医生开立医嘱权限接口 --设置 精麻毒发药物权限
	public void setDoctorPrescriptionPermissionForSync(List<Data.PermissonType> pList) {
		// 从浏览器Cookie中获取
		String employeeId = getLoginInfoFromCookies().get("employeeId").getAsString();
		// 调用搜索医嘱搜索接口
		String url = "http://" + Data.host
				+ "/clinical-mdm/api/v1/app_clinical_mdm/doctor_prescription_permission/sync";

		String docPrescPermissionInputDTOS = "\"docPrescPermissionInputDTOS\":[";

		for (Data.PermissonType p : pList) {
			docPrescPermissionInputDTOS += "{" + "\"prescPermissionCode\":" + p.Code + ",\"permissionDesc\":\"" + p.Desc
					+ "\"},";
		}
		docPrescPermissionInputDTOS += "]";
		String param = "{" + docPrescPermissionInputDTOS + ",\"employeeIdInputDTOs\":[{\"employeeId\":\"" + employeeId
				+ "\"}]}";
		param = param.replace("},],", "}],");
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		logger.log(param);
		HttpTest httpTester = SdkTools.post("医嘱开立权限", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
		}
	}

	public boolean getWarningMes() {
		WebElement getWarningMes = wnwd.checkElementByXpath("等待提示告警",
				"//div[@class='el-message el-message--warning is-closable']//p[contains(.,'权限')]",
				Framework.defaultTimeoutMax);
		if (getWarningMes != null) {
			return true;
		} else {
			return false;
		}
	}

	// 获取所有执行流向类型
	public JsonArray getFlowRuleTypeList() {
		JsonParser parser = new JsonParser();
		String soid = getLoginInfoFromCookies().get("hospitalSOID").getAsString();
		String url = "http://" + Data.host
				+ "/execute-mdm/api/v1/app_execute_mdm/clinical_service_category/query/by_example";
		String param = "{\"enabledFlag\":\"98175\",\"keyword\":\"\",\"hospitalSOID\":\"" + soid + "\"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("获取流向规则集合ID", url, header, param, null, logger);
		logger.assertFalse(
				httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true"),
				"获取流向规则集合ID失败");
		JsonArray data = parser.parse(httpTester.getResponseContent()).getAsJsonObject().get("data").getAsJsonArray();
		return data;
	}

	// 根据名称获取执行流向类型ID
	public String getFlowRuleTypeCodeByName(String typeName) {
		JsonArray flowRuleTypeList = getFlowRuleTypeList();
		String typeCode = null;
		for (JsonElement type : flowRuleTypeList) {
			if (type.getAsJsonObject().get("csCategoryName").getAsString().equals(typeName)) {
				typeCode = type.getAsJsonObject().get("csTypeCode").getAsString();
				break;
			}
		}
		logger.assertFalse(typeCode == null, "未知的类型名称");
		return typeCode;
	}

	// 根据执行流向类型 获取流向规则集合ID
	public String getFlowRuleSetId(String flowRuleTypeCode) {
		JsonParser parser = new JsonParser();
		String soid = getLoginInfoFromCookies().get("hospitalSOID").getAsString();
		String url = "http://" + Data.host
				+ "/execute-mdm/api/v1/app_execute_mdm/clinical_order_flow_rule_set/query/by_cs_type_code";
		String param = "{\"pageType\":\"A\",\"csTypeCode\":\"" + flowRuleTypeCode + "\",\"hospitalSOID\":\"" + soid
				+ "\",\"enableFlag\":\"98175\",\"encounterTypeCode\":\"138138\"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("获取流向规则集合ID", url, header, param, null, logger);
		logger.assertFalse(
				httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true"),
				"获取流向规则集合ID失败");
		JsonArray data = parser.parse(httpTester.getResponseContent()).getAsJsonObject().get("data").getAsJsonArray();
		String flowRuleSetId = data.get(0).getAsJsonObject().get("cliOrderFlowRuleSetId").getAsString();
		return flowRuleSetId;
	}

	// 根据流向规则集合ID获取规则列表
	public JsonArray getFlowRuleSet(String id) {
		JsonParser parser = new JsonParser();
		String soid = getLoginInfoFromCookies().get("hospitalSOID").getAsString();
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String url = "http://" + Data.host
				+ "/execute-mdm/api/v1/app_execute_mdm/clinical_order_flow_rule/query/by_set_ids";
		String param = "{\"cliOrderFlowRuleSetIds\":[\"" + id
				+ "\"],\"encounterTypeCode\":\"138138\",\"hospitalSOID\":\"" + soid
				+ "\",\"keyWord\":\"\",\"pageNo\":0,\"pageSize\":30,\"pageType\":\"P\"}";
		HttpTest httpTester = SdkTools.post("获取流向规则列表详情", url, header, param, null, logger);
		logger.assertFalse(
				httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true"),
				"获取流向规则列表失败");
		JsonArray rules = parser.parse(httpTester.getResponseContent()).getAsJsonObject().get("data").getAsJsonArray();
		return rules;
	}

	// 判断 服务 是否符合 流向规则
	public Boolean ifMatchFlowRlue(JsonObject rule, String CS_ID, String CS_CATEGORY_ID, String CS_TYPE_CODE) {
		Wn60Db db60 = new Wn60Db(logger);
		JsonArray conditionList = rule.get("clinicalOrderFlowCondOutputDTOS").getAsJsonArray();
		logger.assertFalse(conditionList.isJsonNull() || conditionList.size() == 0,
				"医嘱流向规则没有设置具体条件:" + rule.get("cliOrderFlowRuleName").getAsString());
		for (JsonElement condition : conditionList) {
			String conditionDesc = condition.getAsJsonObject().get("conditionDeShortDesc").getAsString();
			JsonArray values = condition.getAsJsonObject().get("clinicalOrderFlowCondValueList").getAsJsonArray();
			List<String> parentTypeCodeList = new ArrayList<String>();
			for (Data.flowRuleType type : Data.flowRuleType.values()) {
				parentTypeCodeList.add(type.code);
			}
			List<String> valueList = new ArrayList<String>();
			for (JsonElement value : values) {
				String conditionValue = value.getAsJsonObject().get("conditionValue").getAsString();
				valueList.add(conditionValue);
				if (conditionDesc.equals("临床服务分类") || conditionDesc.equals("临床服务类型代码")) {
					if (parentTypeCodeList.contains(conditionValue)) {
						List<Map<String, String>> childValueList = db60.getValueSetByParentValueId(conditionValue);
						for (Map<String, String> childValue : childValueList) {
							valueList.add(childValue.get("VALUE_ID"));
						}
					}
				}
			}
			db60.disconnect();

			logger.log(3, "条件名称:" + conditionDesc + " 条件值:" + valueList);
			logger.assertFalse(valueList.size() == 0,
					"医嘱流向规则:" + rule.get("cliOrderFlowRuleName").getAsString() + "中,条件:" + conditionDesc + "没有设置具体值");
			if (conditionDesc.equals("临床服务")) {
				if (!valueList.contains(CS_ID)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
			if (conditionDesc.equals("临床服务分类")) {
				if (!valueList.contains(CS_TYPE_CODE)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
			if (conditionDesc.equals("临床服务类型代码")) {
				if (!valueList.contains(CS_CATEGORY_ID)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
//			logger.log(1,"符合条件:"+condition.getAsJsonObject().toString());
		}
		logger.log(1, "匹配规则:" + rule);
		return true;
	}

	// 判断 服务 是否符合 流向规则
	public Boolean ifMatchFlowRlueNew(JsonObject rule, String CS_ID, String CS_CATEGORY_ID, String CS_TYPE_CODE, String orgID) {
		if(rule.get("enableFlag").getAsString().equals("98361")){
			return false;
		}
		Wn60Db db60 = new Wn60Db(logger);
		JsonArray conditionList = rule.get("clinicalOrderFlowCondOutputDTOS").getAsJsonArray();
		logger.assertFalse(conditionList.isJsonNull() || conditionList.size() == 0,
				"医嘱流向规则没有设置具体条件:" + rule.get("cliOrderFlowRuleName").getAsString());
		for (JsonElement condition : conditionList) {
			String conditionDesc = condition.getAsJsonObject().get("conditionDeShortDesc").getAsString();
			JsonArray values = condition.getAsJsonObject().get("clinicalOrderFlowCondValueList").getAsJsonArray();
			List<String> parentTypeCodeList = new ArrayList<String>();
			for (Data.flowRuleType type : Data.flowRuleType.values()) {
				parentTypeCodeList.add(type.code);
			}
			List<String> valueList = new ArrayList<String>();
			for (JsonElement value : values) {
				String conditionValue = value.getAsJsonObject().get("conditionValue").getAsString();
				valueList.add(conditionValue);
				if (parentTypeCodeList.contains(conditionValue)) {
					List<Map<String, String>> childValueList = db60.getValueSetByParentValueId(conditionValue);
					for (Map<String, String> childValue : childValueList) {
						valueList.add(childValue.get("VALUE_ID"));
					}
				}

			}
			db60.disconnect();

			logger.log(3, "条件名称:" + conditionDesc + " 条件值:" + valueList);
			logger.assertFalse(valueList.size() == 0,
					"医嘱流向规则:" + rule.get("cliOrderFlowRuleName").getAsString() + "中,条件:" + conditionDesc + "没有设置具体值");
			if (conditionDesc.equals("临床服务")) {
				if (!valueList.contains(CS_ID)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
			if (conditionDesc.equals("临床服务分类")) {
				if (!valueList.contains(CS_TYPE_CODE)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
			if (conditionDesc.equals("临床服务类型代码")) {
				if (!valueList.contains(CS_CATEGORY_ID)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
			if (conditionDesc.equals("开立业务单元")) {
				if (!valueList.contains(orgID)) {
					logger.log(2, "不符合条件");
					return false;
				}
			}
//			logger.log(1,"符合条件:"+condition.getAsJsonObject().toString());
		}
		logger.log(1, "匹配规则:" + rule);
		return true;
	}


	// 根据传入的流向规则和开立时间,获取应流向的业务单元名称
	public String getBizUnitNameByRule(JsonObject rule) {
		String format = "HH:mm:ss";
		String nowTime = SdkTools.stampToDate(format, System.currentTimeMillis());
		long now = SdkTools.dateToStamp(format, nowTime);
		JsonArray flowBizUnitList = rule.getAsJsonObject().get("clinicalOrderFlowBizUnitList").getAsJsonArray();
		logger.assertFalse(flowBizUnitList.isJsonNull() || flowBizUnitList.size() == 0,
				"医嘱流向规则没有设置具体流向科室:" + rule.get("cliOrderFlowRuleName").getAsString());
		JsonObject matchBizUnit = null;
		for (JsonElement bizUnit : flowBizUnitList) {
			String startTime = bizUnit.getAsJsonObject().get("serviceStartStr").isJsonNull() ? "00:00:00"
					: bizUnit.getAsJsonObject().get("serviceStartStr").getAsString();
			String endTime = bizUnit.getAsJsonObject().get("serviceEndStr").isJsonNull() ? "23:59:59"
					: bizUnit.getAsJsonObject().get("serviceEndStr").getAsString();
			if (startTime.equals(endTime)) {
				matchBizUnit = bizUnit.getAsJsonObject();
				break;
			}
			long start = SdkTools.dateToStamp(format, startTime);
			long end = SdkTools.dateToStamp(format, endTime);
			if (start <= now && now <= end) {
				matchBizUnit = bizUnit.getAsJsonObject();
				break;
			}
		}
		logger.assertFalse(matchBizUnit == null,
				"医嘱流向规则没有覆盖当前时间:" + rule.get("cliOrderFlowRuleName").getAsString() + "(" + nowTime + ")");
		if (matchBizUnit.get("buName").getAsString().contains("@")) {
			return matchBizUnit.get("buName").getAsString();
		}
		String ORG_ID = matchBizUnit.get("buId").getAsString();
		Wn60Db db60 = new Wn60Db(logger);
		Map<String, String> org = db60.getOrgById(ORG_ID);
		db60.disconnect();
		return org.get("ORG_NAME");
	}

	// 根据传入服务详情 获取 应流向的科室名称
	public String getBizUnitNameByCsId(String typeName, String CS_ID) {
		Wn60Db db60 = new Wn60Db(logger);
		Map<String, String> service = db60.getServiceByCsId(CS_ID);
		db60.disconnect();
		String ruleTypeCode = getFlowRuleTypeCodeByName(typeName);
		String ruleSetId = getFlowRuleSetId(ruleTypeCode);
		JsonArray ruleList = getFlowRuleSet(ruleSetId);
		JsonObject matchRule = null;
		for (JsonElement rule : ruleList) {
			if (ifMatchFlowRlue(rule.getAsJsonObject(), CS_ID, service.get("CS_CATEGORY_ID"),
					service.get("CS_TYPE_CODE"))) {
				matchRule = rule.getAsJsonObject();
				break;
			}
		}
		logger.assertFalse(matchRule == null, "没有找到匹配的医嘱流向规则:" + CS_ID);
		logger.log(1, "匹配的流向规则: " + matchRule.get("cliOrderFlowRuleName").getAsString());
		String bizUnitName = getBizUnitNameByRule(matchRule);
		return bizUnitName;
	}

	// 获取流向科室名称
	public String getBizUnitName(String typeName, String CS_ID, String orgID) {
		Wn60Db db60 = new Wn60Db(logger);
		Map<String, String> service = db60.getServiceByCsId(CS_ID);
		db60.disconnect();
		String ruleTypeCode = getFlowRuleTypeCodeByName(typeName);
		String ruleSetId = getFlowRuleSetId(ruleTypeCode);
		JsonArray ruleList = getFlowRuleSet(ruleSetId);
		JsonObject matchRule = null;
		for (JsonElement rule : ruleList) {
			if (ifMatchFlowRlueNew(rule.getAsJsonObject(), CS_ID, service.get("CS_CATEGORY_ID"),
					service.get("CS_TYPE_CODE"), orgID)) {
				matchRule = rule.getAsJsonObject();
				break;
			}
		}
		logger.assertFalse(matchRule == null, "没有找到匹配的医嘱流向规则:" + CS_ID);
		logger.log(1, "匹配的流向规则: " + matchRule.get("cliOrderFlowRuleName").getAsString());
		String bizUnitName = getBizUnitNameByRule(matchRule);
		return bizUnitName;
	}

	// 签署医嘱
	public void signOffFro(int sleepTime) {
		try {
			addClinicalSummaryIfNeed();
			checkOrderState();
			wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
					Framework.defaultTimeoutMax);
			bindingDiagnosis();
			WebElement patientTypeDialog = wnwd.waitElementByXpath("患者类型选择框",
					"//div[@role='dialog' and contains(.,'请选择患者类型')]", 2000);
			if (patientTypeDialog != null) {
				wnwd.waitElementByXpathAndClick("第一个类型",
						"//div[@role='dialog' and contains(.,'请选择患者类型')]//label[@role='radio']",
						Framework.defaultTimeoutMax);
				wnwd.waitElementByXpathAndClick("确定", "//div[@role='dialog' and contains(.,'请选择患者类型')]//button[.='确定']",
						Framework.defaultTimeoutMax);
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


	//绑定诊断
	public void bindingDiagnosis() {
		Wn60Db windb60 = new Wn60Db(SdkTools.logger);
		Map<String, String> param;
		param = windb60.getParam("CL069");
		if (param != null && param.containsKey("PARAM_VALUE") && !param.get("PARAM_VALUE").equals("0")) {
			wnwd.waitElementByXpathAndClick("医嘱签署按钮", WnOutpatientXpath.outpatientOrderSignButton,
					Framework.defaultTimeoutMax);
			WebElement eleElement = wnwd.waitElementByXpath("//div[@class='el-message el-message--error']",
					Framework.defaultTimeoutMid);
			String msg = null;
			if (eleElement != null) {
				msg = eleElement.getText();
				logger.log(0, "发现报错:" + eleElement.getText());
				System.out.println(msg);
			}
		}
	}

	public void repeatPrint() {
		wnwd.waitElementByXpathAndClick("医嘱签署并打印按钮", WnOutpatientXpath.outpatientOrderSignAndPrintButton,
				Framework.defaultTimeoutMax);
		WebElement printDialog = wnwd.waitElementByXpath("打印框", "//div[@class='el-dialog print-dialog']",
				Framework.defaultTimeoutMin);
		if (printDialog != null) {

			wnwd.waitElementByXpathAndClick("选中已打印的申请单", "//div[@class='wrap-image']//span", Framework.defaultTimeoutMin);

			wnwd.waitElementByXpathAndClick("点击打印按钮", "//div[@class='print-content']//span[.='打印']",
					Framework.defaultTimeoutMin);

		}
	}
	
	/**
	 * 根据medicineId开立药品
	 *
	 * @param medicineId 根据medicineId查找药品/通用名,检查药品/通用名是否可用
	 * @param detail     根据detail编辑加工厂内容
	 */
	public void prescribeByMedicineId(Wn60Db db, String medicineId, PrescribeDetail detail) {
		prescribeByMedicineId(db, medicineId, detail, null);
	}

	/**
	 * 检查药品加工厂内容
	 */
	public void checkPrescribeDetail(PrescribeDetail detail) {
		wnwd.waitElementByXpathAndClick("编辑医嘱", WnOutpatientXpath.outpatientUpdateOrderIcon,
				Framework.defaultTimeoutMin);
		String detailType = getDetailType();
		if (detailType.equals("herb")) {
			checkHerbFactory(detail);
		} else if (detailType.equals("drug")) {
			checkDrugFactory(detail);
		} else {
			logger.assertFalse(true, "不是药品加工厂");
		}
		wnwd.waitElementByXpathAndClick("加工厂确认按钮", WnOutpatientXpath.outpatientDisposalFactoryCommitButton, Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待医嘱加工厂关闭", WnOutpatientXpath.outpatientDisposalFactory, Framework.defaultTimeoutMax);
	}

	public void prescribeByMedicineId(Wn60Db db, String medicineId, PrescribeDetail detail, Boolean check_own_expense) {
		String searchType = "";
		String searchTypeDesc = "";
		List<String> expectTypeCodeList = new ArrayList<String>(Arrays.asList("98095", "98096", "98097"));
		// 校验药品
		Map<String, String> medicine = db.getMedicineByMedicineId(medicineId);
		logger.assertFalse(!medicine.get("COMMODITY_ENABLE_FLAG").equals("98360"), "药品已停用");
		logger.assertFalse(medicine.get("CS_ID") == null, "该药品没有CS_ID");
		// 校验药品通用名
		Map<String, String> service = db.getServiceByCsId(medicine.get("CS_ID"));
		logger.assertFalse(!service.get("CS_STATUS").equals("98360"), "服务已停用");
		logger.assertFalse(!expectTypeCodeList.contains(service.get("CS_TYPE_CODE")),
				"类型不是西药或中草药 cs_id:" + medicine.get("CS_ID"));
		if (service.get("CS_TYPE_CODE").equals("98097")) {
			// 中草药
			searchType = "9";
			searchTypeDesc = "中草药";
		} else {
			// 西成药
			searchType = "5";
			searchTypeDesc = "西成药";
		}
		// 获取药品排序
		int medicineSeq = getMedicineSeqByApi(medicine.get("COMMODITY_NAME_CHINESE"), searchType, "medicineId", medicineId, "medicineSpec", medicine.get("PACK_SPEC"));
		// 搜索药品并选择
		searchOrderBySeq(searchTypeDesc, medicine.get("COMMODITY_NAME_CHINESE"), medicineSeq);
		beforeFactory();
		if (service.get("CS_TYPE_CODE").equals("98097")) {
			editHerbFactory(detail);
		} else {
			editDrugFactory(detail);
		}
		commitFactory();
		afterFactory(check_own_expense);
	}

	/**
	 * 根据CS_ID开立检验
	 *
	 * @param CS_ID  根据CS_ID查找检验服务 并 检查服务类型是否正确,是否可用
	 * @param detail 根据detail编辑加工厂内容
	 */
	public void prescribeLabByCsid(Wn60Db db, String CS_ID, PrescribeDetail detail) {
		prescribeLabByCsid(db, CS_ID, detail, null);
	}

	public void prescribeLabByCsid(Wn60Db db, String CS_ID, PrescribeDetail detail, Boolean check_own_expense) {
		String searchType = "3";
		String searchTypeDesc = "检验";
		// 校验临床服务数据
		Map<String, String> service = db.getServiceByCsId(CS_ID);
		logger.assertFalse(!service.get("CS_STATUS").equals("98360"), "服务已停用");
		logger.assertFalse(service.get("CS_TYPE_CODE") == null, "没有服务类型");
		if (!service.get("CS_TYPE_CODE").equals(Data.labCode)) {
			String parentTypeCode = db.getValueSetByValueId(service.get("CS_TYPE_CODE"))
					.get("PARENT_VALUE_ID");
			logger.assertFalse(parentTypeCode == null || !parentTypeCode.equals(Data.labCode), "该服务类型不是检验:" + CS_ID);
		}
		String errMsg = checkLabAvailable(CS_ID);
		logger.assertFalse(!(errMsg == null), errMsg);
		// 获取药品排序
		int seq = getMedicineSeqByApi(service.get("CS_NAME"), searchType, "csId",
				service.get("CS_ID"));
		// 搜索药品并选择
		searchOrderBySeq(searchTypeDesc, service.get("CS_NAME"), seq);
		beforeFactory();
		editLabFactory(detail);
		commitFactory();
		afterFactory(check_own_expense);
	}

	/**
	 * 根据CS_ID开立治疗
	 *
	 * @param CS_ID  根据CS_ID查找治疗服务 并 检查服务类型是否正确,是否可用
	 * @param detail 根据detail编辑加工厂内容
	 */
	public void prescribeTreatByCsid(Wn60Db db, String CS_ID, PrescribeDetail detail) {
		prescribeTreatByCsid(db, CS_ID, detail, null);
	}

	public void prescribeTreatByCsid(Wn60Db db,String CS_ID, PrescribeDetail detail, Boolean check_own_expense) {
		String searchType = "2";
		String searchTypeDesc = "治疗";
		// 校验临床服务数据
		Map<String, String> service = db.getServiceByCsId(CS_ID);
		logger.assertFalse(!service.get("CS_STATUS").equals("98360"), "服务已停用");
		logger.assertFalse(service.get("CS_TYPE_CODE") == null, "没有服务类型");
		if (!service.get("CS_TYPE_CODE").equals(Data.treatCode)) {
			String parentTypeCode = db.getValueSetByValueId(service.get("CS_TYPE_CODE"))
					.get("PARENT_VALUE_ID");
			logger.assertFalse(parentTypeCode == null || !parentTypeCode.equals(Data.treatCode), "该服务类型不是治疗:" + CS_ID);
		}
		String errMsg = checkTreatAvailable(CS_ID);
		logger.assertFalse(!(errMsg == null), errMsg);
		// 获取药品排序
		int seq = getMedicineSeqByApi(service.get("CS_NAME"), searchType, "csId",
				service.get("CS_ID"));
		// 搜索药品并选择
		searchOrderBySeq(searchTypeDesc, service.get("CS_NAME"), seq);
		beforeFactory();
		editTreatFactory(detail);
		commitFactory();
		afterFactory(check_own_expense);
	}

	/**
	 * 根据CS_ID开立病理
	 *
	 * @param CS_ID  根据CS_ID查找病理服务 并 检查服务类型是否正确,是否可用
	 * @param detail 根据detail编辑加工厂内容
	 */
	public void prescribePathologyByCsid(Wn60Db db,String CS_ID, PrescribeDetail detail) {
		prescribePathologyByCsid(db, CS_ID, detail, null);
	}

	public void prescribePathologyByCsid(Wn60Db db, String CS_ID, PrescribeDetail detail, Boolean check_own_expense) {
		String searchType = "11";
		String searchTypeDesc = "病理";
		// 校验临床服务数据
		Map<String, String> service = db.getServiceByCsId(CS_ID);
		logger.assertFalse(!service.get("CS_STATUS").equals("98360"), "服务已停用");
		logger.assertFalse(service.get("CS_TYPE_CODE") == null, "没有服务类型");
		if (!service.get("CS_TYPE_CODE").equals(Data.pathologyCode)) {
			String parentTypeCode = db.getValueSetByValueId(service.get("CS_TYPE_CODE"))
					.get("PARENT_VALUE_ID");
			logger.assertFalse(parentTypeCode == null || !parentTypeCode.equals(Data.pathologyCode),
					"该服务类型不是病理:" + CS_ID);
		}
		String errMsg = checkPathologyAvailable(CS_ID);
		logger.assertFalse(!(errMsg == null), errMsg);
		// 获取药品排序
		int seq = getMedicineSeqByApi(service.get("CS_NAME"), searchType, "csId",
				service.get("CS_ID"));
		// 搜索药品并选择
		searchOrderBySeq(searchTypeDesc, service.get("CS_NAME"), seq);
		beforeFactory();
		editPathologyFactory(detail);
		commitFactory();
		afterFactory(check_own_expense);
	}

	/**
	 * 根据ExamItemId开立检查
	 *
	 * @param ExamItemId 根据ExamItemId查找检查项目和检查服务, 检查服务类型是否正确,是否可用
	 * @param detail     根据detail编辑加工厂内容
	 */
	public void prescribeExamByExamItemId(Wn60Db db, String ExamItemId, PrescribeDetail detail) {
		prescribeExamByExamItemId(db, ExamItemId, detail, null);
	}

	public void prescribeExamByExamItemId(Wn60Db db, String ExamItemId, PrescribeDetail detail, Boolean check_own_expense) {
		String searchType = "4";
		String searchTypeDesc = "检查";
		// 校验检查项目
		Map<String, String> examItem = db.getExamItemByExamItemId(ExamItemId);
		String CS_ID = examItem.get("CS_ID");
		// 校验临床服务数据
		Map<String, String> service = db.getServiceByCsId(CS_ID);
		logger.assertFalse(!service.get("CS_STATUS").equals("98360"), "服务已停用");
		logger.assertFalse(service.get("CS_TYPE_CODE") == null, "没有服务类型");
		if (!service.get("CS_TYPE_CODE").equals(Data.examCode)) {
			String parentTypeCode = db.getValueSetByValueId(service.get("CS_TYPE_CODE"))
					.get("PARENT_VALUE_ID");
			logger.assertFalse(parentTypeCode == null || !parentTypeCode.equals(Data.examCode), "该服务类型不是检查:" + CS_ID);
		}
		String errMsg = checkExamAvailable(CS_ID);
		logger.assertFalse(!(errMsg == null), errMsg);
		// 获取药品排序
		int seq = getMedicineSeqByApi(examItem.get("EXAM_ITEM_NAME"), searchType, "examItemId",
				examItem.get("EXAM_ITEM_ID"));
		// 搜索药品并选择
        searchOrderBySeq(searchTypeDesc, examItem.get("EXAM_ITEM_NAME"), seq);
		beforeFactory();
		editExamFactory(detail);
		commitFactory();
		afterFactory(check_own_expense);
	}


	/**
	 * CL091医嘱规则启用模式
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  参数默认值为0；0-旧模式；1-新模式（V3.0版本）；2-半切新模式（用量、费用继续用旧模式）
	 */
	public void SetCL091(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"148064470617899008\",\"valueConfigs\":[{\"paramValueConfigId\":\"148064470617899011\",\"paramConfigId\":\"148064470617899008\",\"paramValue\":\""
				+ paramValue + "\",\"paramEndValue\":null,\"activatedAt\":\"2021-07-21 10:59:20\",\"valueDesc\":\""
				+ paramDesc
				+ "\"}],\"paramConfigId\":\"148064470617899008\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数CL070成功", "参数CL070修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}
	
	// 获取医嘱规则列表
	public JsonArray getMedicalOrderRuleList() {
		String url = "http://" + Data.host + "/clinical-mdm/api/v2/app_clinical_mdm/clinical_order_rules_list/query/by_example";
		String param = "{}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("查询医嘱规则列表(主数据)", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray ruleList = resJson.getAsJsonArray("data");
			return ruleList;
		} else {
			logger.assertFalse(true, "查询医嘱规则列表(主数据)失败!");
		}
		return null;
	}

	// 禁用/启用全部医嘱规则
	public void medicalOrderRule_saveAll(Boolean enabledFlag) {
		setParamsForTestAllService();
		JsonArray ruleList = getMedicalOrderRuleList();
		String status = enabledFlag ? "98361" : "98360";
		for (JsonElement rule : ruleList) {
			if(rule.getAsJsonObject().get("enabledFlag").toString().trim().replace("\"", "").equals(status)) {
				String cliOrderRuleId = rule.getAsJsonObject().get("cliOrderRuleId").toString();
				String url = "http://" + Data.host + "/clinical-mdm/api/v2/app_clinical_mdm/clinical_order_rules_status/update";
				String param = "{\"cliOrderRuleIds\":["+cliOrderRuleId+"],\"isEnableRule\":"+enabledFlag+"}";
				HttpTestHeader header = new HttpTestHeader();
				header.addHeader("Version", "1.1");
				header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
				String desc = "修改医嘱规则:" + rule.getAsJsonObject().get("cliOrderRulesTypeCodeDesc") + "_"+rule.getAsJsonObject().get("cliOrderRuleName")+ "  -->  "
						+ (enabledFlag ? "启用" : "停用");
				HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
				if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
					logger.assertFalse(true, "修改医嘱规则失败");
				}
			}
		}
	}
	
	// 根据名称获取医嘱规则
	public JsonObject getMedicalOrderRuleByName(String name) {
		JsonObject target_rule = null;
		JsonArray ruleList = getMedicalOrderRuleList();
		for (JsonElement rule : ruleList) {
			if (rule.getAsJsonObject().get("cliOrderRuleName").toString().replace("\"", "").equals(name)) {
				target_rule = rule.getAsJsonObject();
				return target_rule;
			}
		}
		if(target_rule==null) {
			logger.assertFalse(true, "未找到指定医嘱规则："+name);
		}
		return target_rule;
	}
	
	// 修改单条医嘱规则
	public void medicalOrderRule_saveByName(String name, Boolean enabledFlag) {
		JsonObject rule = getMedicalOrderRuleByName(name);
		String url = "http://" + Data.host + "/clinical-mdm/api/v2/app_clinical_mdm/clinical_order_rules_status/update";
		String cliOrderRuleId = rule.getAsJsonObject().get("cliOrderRuleId").toString();
		System.out.println("------"+cliOrderRuleId);
		String param = "{\"cliOrderRuleIds\":["+cliOrderRuleId+"],\"isEnableRule\":"+enabledFlag+"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String desc = "修改医嘱规则:" + rule.getAsJsonObject().get("cliOrderRulesTypeCodeDesc") + "_"+rule.getAsJsonObject().get("cliOrderRuleName") + "  -->  "
				+ (enabledFlag ? "启用" : "停用") ;
		HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
		if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
			logger.assertFalse(true, "修改医嘱规则失败");
		}
	}
	
	// 获取打印单据管理列表
	public JsonArray getPrintTemplateList() {
		String url = "http://" + Data.host
				+ "/mdm-cooperation/api/v1/app_coo_config/printer/print_template/query";
		String param = "{\"formName\":\"\",\"formTypeName\":\"\",\"pageType\":\"P\",\"pageNo\":0,\"pageSize\":100}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest httpTester = SdkTools.post("查询打印单据管理列表(主数据)", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			JsonParser parser = new JsonParser();
			JsonObject resJson = parser.parse(httpTester.getResponseContent()).getAsJsonObject();
			JsonArray templateList = resJson.getAsJsonArray("data");
			return templateList;
		} else {
			logger.assertFalse(true, "查询打印单据管理列表(主数据)失败!");
		}
		return null;
	}
	
	// 根据打印模板名称获取打印模板信息
	public JsonObject getPrintTemplateByPrintTemplateName(String printTemplateName) {
		JsonObject printTemplate = null;
		JsonArray printTemplateList = getPrintTemplateList();
		for (JsonElement template : printTemplateList) {
			if (template.getAsJsonObject().get("printTemplateName").toString().replace("\"", "").equals(printTemplateName)) {
				printTemplate = template.getAsJsonObject();
				return printTemplate;
			}
		}
		return printTemplate;
	}
	
	// 根据模板名称获取打印模板启用状态
	public String getPrintTemplateStatusByName(String printTemplateName) {
		JsonObject printTemplate = getPrintTemplateByPrintTemplateName(printTemplateName);
		String status = "98361";
		if(printTemplate!=null) {
			status = printTemplate.getAsJsonObject().get("enableFlag").getAsString();
			return status;
		}
		return status;
	}
		
    // 禁用/启用全部打印单据
	public void printTemplate_saveAll(String enableFlag) {
		setParamsForTestAllService();
		switchPrintTemplateVersion(Data.host,"1");
		JsonArray printTemplateList = getPrintTemplateList();
		for (JsonElement template : printTemplateList) {
			if (!template.getAsJsonObject().get("enableFlag").toString().trim().replace("\"", "").equals(enableFlag)) {
				String printTemplateId = template.getAsJsonObject().get("printTemplateId").toString().replace("\"", "");
				String printTemplateName = template.getAsJsonObject().get("printTemplateName").toString();
				String url = "http://" + Data.host + "/mdm-cooperation/api/v1/app_coo_config/printer/print_template/enable";
				String param = "{\"printTemplateId\":\"" + printTemplateId + "\",\"enabledFlag\":" + enableFlag + "}";
				HttpTestHeader header = new HttpTestHeader();
				header.addHeader("Version", "1.1");
				header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
				String desc = "修改单据启用状态:" + template.getAsJsonObject().get("formName") + "  -->  "
						+ (enableFlag == "98360" ? "启用" : "停用");
				HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
				if (httpTester.getResponseCode() != 200
						|| !httpTester.getResponseContent().contains("\"success\":true")) {
					logger.assertFalse(true, "全量修改单据失败，修改：" + printTemplateName + "启用状态失败");
				}
			}
		}
	}
		
	// 根据名称修改单条打印单据状态
	public void printTemplate_saveByPrintTemplateName(String printTemplateName, String enabledFlag) {
		JsonObject template = getPrintTemplateByPrintTemplateName(printTemplateName);
		String url = "http://" + Data.host + "/mdm-cooperation/api/v1/app_coo_config/printer/print_template/enable";
		String printTemplateId = template.getAsJsonObject().get("printTemplateId").toString().replace("\"", "");
		String param = "{\"printTemplateId\":\""+printTemplateId+"\",\"enabledFlag\":"+enabledFlag+"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		String desc = "修改单据启用状态:" + template.getAsJsonObject().get("cliOrderRulesTypeCodeDesc") + "  -->  "
				+ (enabledFlag=="98360" ? "启用" : "停用") ;
		HttpTest httpTester = SdkTools.post(desc, url, header, param, null, logger);
		if (httpTester.getResponseCode() != 200 || !httpTester.getResponseContent().contains("\"success\":true")) {
			logger.assertFalse(true, "修改单据"+printTemplateName+"启用状态失败");
		}
	}
		
	// 切换处方打印版本(0: V1版本，1:V2版本)
	public void switchPrintTemplateVersion(String syncHost, String paramValue) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/param_items_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramConfigGroup\":\"161231682862764032\",\"paramItemValueConfigList\":[{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"161231511064072193\",\"value\":[{\"paramEndValue\":null,\"paramValue\":\""+paramValue+"\"}]}]}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改处方打印版本成功", "参数处方打印版本修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}
	
	//生成图片
	public boolean generateImage(WebElement canvas, String path) {
        Object base64 = ((JavascriptExecutor) driver)
    			.executeScript("return arguments[0].toDataURL('image/png').substring(21);", canvas);
    	String base64Str = base64.toString().replaceAll(" ", "").replace("\r\n", "");
		if (base64Str == null)
			return false;
		// 解密
		try {
			Decoder decoder = java.util.Base64.getMimeDecoder();
			byte[] b = decoder.decode(base64Str.replace("\r\n", "").replace(" ", ""));
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			FileOutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}




}
