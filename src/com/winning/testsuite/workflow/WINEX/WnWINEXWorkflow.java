package com.winning.testsuite.workflow.WINEX;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;

import ui.sdk.base.SdkWebDriver;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;
import ui.sdk.util.SdkTools;
import ui.sdk.util.SdkTools.IdCardGenerator;
import xunleiHttpTest.HttpTest;
import xunleiHttpTest.HttpTestHeader;
import xunleiHttpTest.HttpTestUrl;

public class WnWINEXWorkflow {

	public SdkDatabaseConn db = null;// his的db对象
	public Wn60Db db60 = null;// 60的db对象

	public SdkWebDriver wnwd = null;
	public Log logger = null;

	public WnWINEXWorkflow(SdkWebDriver driver) {
		this.wnwd = driver;
		this.logger = wnwd.logger;
		db = new SdkDatabaseConn(Data.hisDbType, Data.hisHost, Data.hisInstance, Data.hisDbname, Data.hisDbService,
				Data.hisUsername, Data.hisPassword, this.logger);
		db60 = new Wn60Db(this.logger);
	}

	
    // 登录大his
    public void wnlogin(String username, String password) {
        wnwd.waitElementByXpathAndClick("帐号登录方式选择按钮", "//div[.='账号登录']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("用户名输入框", WnOutpatientXpath.loginUsernameInput, username, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("密码输入框", WnOutpatientXpath.loginPasswordInput, password, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("登录按钮", WnOutpatientXpath.loginLoginButton, Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("检查登录是否成功标识", "//span[.='医院基础业务管理']", Framework.defaultTimeoutMax);
    }

	/**
	 * 集团HIS接口方式登录
	 */
	public HttpTestHeader confusionLoginForWINEX(String host,String username, String password) throws UnsupportedEncodingException {
		// 把明文密码倒序再base64加密
		StringBuffer stringBuffer = new StringBuffer(password).reverse();
		password = stringBuffer.toString();
		String base64encodedPassword = Base64.getEncoder().encodeToString(password.getBytes("utf-8"));

		// 登陆HIS
		String url = "http://" + host + "/base/api/v1/base/user/confusion_login";
		String json = "{\"username\":\"" + username + "\",\"password\":\"" + base64encodedPassword
				+ "\",\"orgName\":\"\",\"hospitalSOID\":\"\",\"locationId\":\"\",\"locationName\":\"\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Content-Type", "application/json;charset=UTF-8");
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			// 从登陆接口的返回值中获取access_token
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			String Authorization = "Bearer " + dataObject.get("access_token").getAsString();
			header.addHeader("Authorization", Authorization);
			logger.boxLog(1, "登陆成功", header.toString());
			return header;
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
			return null;
		}
	}
	
    //调接口出院出区
    public void outhospitalByInterface(HttpTestHeader header,String employeeID,String patientName) {
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
    		HttpTest httpTester = SdkTools.post("患者出区", url1, header, json1, null, logger);
            if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "患者出区成功");
            } else {
                logger.assertFalse(true, "患者出区失败", "请求地址: " + url1 + "\n\n\n请求状态: " + httpTester.getResponseCode() + "\n\n\n返回内容: " + httpTester.getResponseContent());
            }
            String url2 = "http://" + Data.host + "/inpatient-encounter/api/v1/app_inpatient_encounter/inpat_bed_charge_job/save";
            String json2 = "{\"jobExeBizTypeCode\":399469083,\"bizRoleId\":\"" + bizRoleId + "\",\"encounterId\":\"" + encounterId + "\",\"inpatBedChargeType\":\"wardDischargedCharge\",\"wardId\":\"" + wardId + "\",\"deptId\":\"" + deptId + "\",\"actualExeDate\":\"" + currentDate + "\"}";
    		httpTester = SdkTools.post("床位释放", url2, header, json2, null, logger);
            if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
                logger.boxLog(1, "成功", "床位释放成功");
            } else {
                logger.assertFalse(true, "床位释放失败", "请求地址: " + url2 + "\n\n\n请求状态: " + httpTester.getResponseCode() + "\n\n\n返回内容: " + httpTester.getResponseContent());
            }
        }catch (Throwable e) {
            logger.log(3,"出区异常");
        }
    }

	/**
	 * 集团大his通过接口方式收费
	 * @param header 
	 * @param encounterId
	 * @param personId
	 * @param employeeId
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String chargingForWINEX(HttpTestHeader header,String encounterId, String personId, String employeeId)
			throws UnsupportedEncodingException {
		// 根据就诊标识列表获取可收费的订单信息
		String url = "http://" + Data.host
				+ "/outp-finance-fee/api/v1/app_finance/service_purch_order/query/by_encounter_ids";
		String param = "{\"encounterIdList\":[{\"encounterId\":\"" + encounterId
				+ "\"}],\"lockedMACAddress\":\"127.0.0.1\",\"lockedIPAddress\":\"127.0.0.1\",\"lockedBy\":\""
				+ employeeId + "\",\"personId\":\"" + personId + "\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";

		HttpTest httpTester = SdkTools.post("根据就诊标识列表获取可收费的订单信息", url, header, param, null, logger);
		String encDeptId = "";
		String bizRoleId = "";
		List<Map<String, Object>> servicePurchOrderIdList = new ArrayList<Map<String, Object>>();
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			encDeptId = dataObject.getAsJsonArray("encounterServicePurchOrderList").get(0)
					.getAsJsonObject().get("encDeptId").getAsString();
			bizRoleId = dataObject.getAsJsonArray("encounterServicePurchOrderList").get(0)
					.getAsJsonObject().get("bizRoleId").getAsString();
			JsonArray dataArray = dataObject.getAsJsonArray("encounterServicePurchOrderList").get(0)
					.getAsJsonObject().getAsJsonArray("servicePurchOrderList");
			for (int i = 0; i < dataArray.size(); i++) {
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("servicePurchOrderId",
						dataArray.get(i).getAsJsonObject().get("servicePurchOrderId").getAsString());
				map1.put("encounterId", encounterId);
				map1.put("medInstiInsurId", "57393491145648129");
				List<Map<String, Object>> servicePurchOrderDetailList = new ArrayList<Map<String, Object>>();
				Map<String, Object> map3 = new HashMap<String, Object>();
				for (int j = 0; j < dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList")
						.size(); j++) {
					map3 = new HashMap<String, Object>();
					map3.put("chargingItemId",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("commodityId").getAsString());
					map3.put("commodityTypeCode",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("commodityTypeCode").getAsString());
					map3.put("commodityTypeCptId",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("commodityTypeCptId").getAsString());
					map3.put("medicineId",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("commodityId").getAsString());

					JsonElement medicinePackUnitId = dataArray.get(i).getAsJsonObject()
							.getAsJsonArray("purchOrdDetailItemList").get(j).getAsJsonObject()
							.getAsJsonArray("servicePurchOrderDetailList").get(0).getAsJsonObject()
							.get("commodityUnitId");
					map3.put("medicinePackUnitId",
							medicinePackUnitId.isJsonNull() ? null : medicinePackUnitId.getAsString());

					map3.put("retailPrice",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("retailPrice").getAsDouble());
					map3.put("retailQty",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("retailQty").getAsDouble());
					map3.put("servicePurchOrderDetailId",
							dataArray.get(i).getAsJsonObject().getAsJsonArray("purchOrdDetailItemList").get(j)
									.getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(0)
									.getAsJsonObject().get("servicePurchOrderDetailId").getAsString());
					servicePurchOrderDetailList.add(map3);
				}
				map1.put("servicePurchOrderDetailList", servicePurchOrderDetailList);
				servicePurchOrderIdList.add(map1);
			}
			logger.boxLog(1, "根据就诊标识列表获取可收费的订单信息", content + "\r" + servicePurchOrderIdList);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 账单生成
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/bill/generate";
		param = "{\"deptId\":\"" + encDeptId + "\",\"identityNo\":1,\"chrgWinId\":\"176855017197166593\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"personalIdentityId\":\"154390834984396803\",\"bizRoleId\":\"" + bizRoleId + "\",\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"billTypeCode\":\"307280\",\"billTypeCptId\":\"399185055\",\"macAddress\":\"\",\"ipAddress\":\"\",\"servicePurchOrderIdList\":"
				+ JSONObject.toJSONString(servicePurchOrderIdList) + ",\"encounterId\":\"" + encounterId
				+ "\",\"settleMedInstiInsurId\":\"57393491145648129\",\"personId\":\"" + personId
				+ "\",\"posCptId\":\"399211914\",\"posConceptId\":\"399211914\"}";
		System.out.println(param);
		httpTester = SdkTools.post("生成订单", url, header, param, null, logger);
		String settlementId = "";
		String billId = "";
		String businessLockRecordId = "";
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "账单生成接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			settlementId = dataObject.get("settlementId").getAsString();
			billId = dataObject.get("billId").getAsString();
			businessLockRecordId = dataObject.get("businessLockRecordId").getAsString();
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 费用计算
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/discount/calculate";
		param = "{\"settlementId\":\"" + settlementId + "\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
		httpTester = SdkTools.post("费用计算", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "费用计算接口返回成功", "费用计算成功");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 根据结算单标识列表查询医保结算单信息
		url = "http://" + Data.host
				+ "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/query/by_settlement_ids";
		param = "{\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"settlementId\":\"" + settlementId
				+ "\",\"businessLockRecordId\":\"" + businessLockRecordId + "\"}";
		httpTester = SdkTools.post("根据结算单标识列表查询医保结算单信息", url, header, param, null, logger);
		String setlInsurReimDetailId = "";
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "获取医保结算单列表接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			setlInsurReimDetailId = dataObject.getAsJsonArray("settlementInsurReimDetailList").get(0).getAsJsonObject()
					.get("setlInsurReimDetailId").getAsString();
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 保存医保预算结果
		url = "http://" + Data.host
				+ "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/update/pre_settled";
		param = "{\"billId\":\"" + billId + "\",\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"setlInsurReimDetailId\":\"" + setlInsurReimDetailId
				+ "\"}";
		httpTester = SdkTools.post("保存医保预算结果", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "保存医保预算结果接口返回成功", content);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 预算完成
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/pre_settled";
		param = "{\"settlementId\":\"" + settlementId + "\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
		httpTester = SdkTools.post("预算完成", url, header, param, null, logger);
		double paymentAmount = 0;
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "预算完成接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			if (dataObject.getAsJsonArray("settlementList").size() == 0) {
				paymentAmount = 0;
			} else {
				for (int i = 0; i < dataObject.getAsJsonArray("settlementList").size(); i++) {
					paymentAmount = paymentAmount + dataObject.getAsJsonArray("settlementList").get(i).getAsJsonObject()
							.get("settlementSelfPayingAmount").getAsDouble();
				}
			}
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 保存结算单待收款信息
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_charge/save";
		param = "{\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"billIdList\":[\"" + billId
				+ "\"],\"settlementIdList\":[\"" + settlementId + "\"],\"chargeDueAmount\":" + paymentAmount
				+ ",\"paymentList\":[{\"paymentAmount\":" + paymentAmount
				+ ",\"medInstiPaymentMethodId\":\"109709972447852545\",\"medInstiInterfaceId\":null,\"medInstiPaymentMethodName\":\"现金\",\"paymentMethodConceptId\":\"399202041\"}],\"chargeSourceCode\":\"991418\",\"chargeSourceCptId\":\"399185053\"}";
		httpTester = SdkTools.post("保存结算单待收款信息", url, header, param, null, logger);
		String chargeId = "";
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "保存结算单待收款信息接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			chargeId = dataObject.get("chargeId").getAsString();
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 根据结算单标识列表查询医保结算单信息
		url = "http://" + Data.host
				+ "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/query/by_settlement_ids";
		param = "{\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"settlementId\":\"" + settlementId
				+ "\",\"businessLockRecordId\":\"" + businessLockRecordId + "\"}";
		httpTester = SdkTools.post("根据结算单标识列表查询医保结算单信息", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "获取医保结算单列表接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			setlInsurReimDetailId = dataObject.getAsJsonArray("settlementInsurReimDetailList").get(0).getAsJsonObject()
					.get("setlInsurReimDetailId").getAsString();
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 保存医保正算结果
		url = "http://" + Data.host
				+ "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/update/settled";
		param = "{\"billId\":\"" + billId + "\",\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"setlInsurReimDetailId\":\"" + setlInsurReimDetailId
				+ "\"}";
		httpTester = SdkTools.post("保存医保正算结果", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "保存医保正算结果接口返回成功", content);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 正算完成
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/settled";
		param = "{\"settlementId\":\"" + settlementId + "\",\"soid\":[\"" + Data.hospital_soid
				+ "\"],\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
		httpTester = SdkTools.post("正算完成", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "正算完成接口返回成功", content);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 结算-根据收款标识获取支付明细信息
		url = "http://" + Data.host + "/finance-common/api/v1/app_finance_common/payment/query/outp_refund_fee";
		param = "{\"chargeId\":\"" + chargeId + "\",\"businessLockRecordId\":\"" + businessLockRecordId
				+ "\",\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"]}";
		httpTester = SdkTools.post("结算-根据收款标识获取支付明细信息", url, header, param, null, logger);
		String paymentId = "";
		String chargeSourceCode = "";
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "根据收款标识获取支付明细信息接口返回成功", content);
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			paymentId = dataObject.getAsJsonArray("paymentList").get(0).getAsJsonObject().get("paymentId")
					.getAsString();
			chargeSourceCode = dataObject.get("chargeSourceCode").getAsString();
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 保存结算单确认支付结果
		url = "http://" + Data.host + "/finance-common/api/v1/app_finance_common/third_party_payment_trace/update/paid";
		param = "{\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"],\"billId\":\"" + billId + "\",\"paymentId\":\""
				+ paymentId + "\",\"chargeSourceCode\":\"" + chargeSourceCode + "\"}";
		httpTester = SdkTools.post("保存结算单确认支付结果", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			logger.boxLog(1, "保存结算单确认支付结果接口返回成功", content);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 结算完成
		url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/finished";
		param = "{\"settlementIdList\":[\"" + settlementId + "\"],\"hospitalSOID\":\"" + Data.hospital_soid
				+ "\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\"" + employeeId
				+ "\",\"operateEmployeeNo\":\"" + Data.default_user_login_account + "\",\"deptId\":\"" + encDeptId
				+ "\",\"soid\":[\"" + Data.hospital_soid + "\"]}";
		httpTester = SdkTools.post("结算完成", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "结算完成接口返回成功", "结算完成");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}

		// 根据业务锁记录标识解锁业务锁
		url = "http://" + Data.host
				+ "/finance-component/api/v1/app_pmts_finance/business_lock_record/update/by_business_lock_record_id";
		param = "{\"unlockedBy\":\"" + employeeId + "\",\"businessLockRecordId\":\"" + businessLockRecordId
				+ "\",\"unlockedMACAddress\":\"\",\"unlockedIPAddress\":\"\",\"unLockActionCode\":\"867790\",\"hospitalSOID\":\""
				+ Data.hospital_soid + "\"}";
		httpTester = SdkTools.post("根据业务锁记录标识解锁业务锁", url, header, param, null, logger);
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "根据业务锁记录标识解锁业务锁接口返回成功", "解锁成功");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}
		return null;
	}

	/**
	 * 大HIS接口方式，获取待收取的费用信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public String getCostWINEX(HttpTestHeader header,String encounterId, String personId)
			throws UnsupportedEncodingException {
		// 根据就诊标识列表获取可收费的订单信息
		String url = "http://" + Data.host
				+ "/outp-finance-fee/api/v1/app_finance/service_purch_order/query/by_encounter_ids";
		String param = "{\"encounterIdList\":[{\"encounterId\":\"" + encounterId
				+ "\"}],\"lockedMACAddress\":\"127.0.0.1\",\"lockedIPAddress\":\"127.0.0.1\",\"personId\":\"" + personId
				+ "\",\"hospitalSOID\":\"" + Data.hospital_soid + "\"}";
		HttpTest httpTester = SdkTools.post("结算完成", url, header, param, null, logger);
		String totalCost = "";
		if (httpTester.getResponseCode() == 200 && httpTester.getResponseContent().contains("\"success\":true")) {
			String content = httpTester.getResponseContent();
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			totalCost = dataObject.getAsJsonObject("chargableServicePurchaseOrderRetAmt").getAsString();
			logger.boxLog(1, "根据就诊标识列表获取可收费的订单信息", "订单待收费用为：" + totalCost + "\r" + content);
			return totalCost;
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + httpTester.getResponseCode()
					+ "\n\n\n返回内容: " + httpTester.getResponseContent());
		}
		return totalCost;
	}
	
	/**
	 * 大HIS接口方式，禁用住院审方，门诊审方
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void updateReviewerStatus(String pharmacyId,String pharmacyName,String pharmacyNo,String outpReviewerStatus,String inpReviewerStatus)
			throws UnsupportedEncodingException {
		// 根据就诊标识列表获取可收费的订单信息
		String url = "http://" + Data.his_host
				+ "/material-mdm/api/v1/app_material_mdm/drug_storage_service_all/update";
		String param = "{\"drugStorageId\":\""+pharmacyId+"\",\"organization\":{\"orgNo\":\""+pharmacyNo+"\",\"orgName\":\""+pharmacyName+"\",\"orgStatus\":\"98360\"},\"drugStorage\":{\"drugStorageTypeCode\":\"307231\",\"drugStorageGradeCode\":\"376377\"},\"pharmacy\":{\"outpPrescReviewEnabledFlag\":\""+outpReviewerStatus+"\",\"inpPresReviewEnabledFlag\":\""+inpReviewerStatus+"\",\"outpDispPrepareEnabledFlag\":\"98176\"}}";
		System.out.println(url);
		System.out.println(param);
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Content-Type", "application/json;charset=UTF-8");
        header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
        test.sendPostRequest(param, null, header);
        test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			String content = test.getResponseContent();
			logger.boxLog(1, "更新审方状态成功", content);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode()
					+ "\n\n\n返回内容: " + test.getResponseContent());
		}
	}
	
	
	/**
	  * 门诊退费禁用退费申请流程
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  根据参数判断门诊退费是否开启退费申请流程，默认值为0；0-否（不启用退费申请），1-是（启用退费申请）
	 */
	public void SetFIN03010(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"175190532704243713\",\"valueConfigs\":[{\"paramValueConfigId\":\"175190532704243714\",\"paramConfigId\":\"175190532704243713\",\"paramValue\":\""+paramValue+"\",\"paramEndValue\":null,\"activatedAt\":\"2021-09-26 09:05:08\",\"valueDesc\":\""+paramDesc+"\"}],\"paramConfigId\":\"175190532704243713\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数FIN03010成功", "参数FIN03010修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}

	/**
	  * 是否允许护士站药品自动签收
	 *
	 * @param syncHost   访问地址
	 * @param paramValue 配置参数0,1（默认为0）
	 * @param paramDesc  参数默认为0；0-允许，1-禁止
	 */
	public void SetMT0028(String syncHost, String paramValue, String paramDesc) {
		String clurl = "http://" + syncHost + "/mdm-base/api/v1/app_base_mdm/parameter_config/save";
		Cookie cookie = wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "{\"paramAppScopeTypeCode\":\"256523\",\"paramId\":\"172933452237551616\",\"valueConfigs\":[{\"paramValueConfigId\":\"172933452237551619\",\"paramConfigId\":\"172933452237551617\",\"paramValue\":\""+paramValue+"\",\"paramEndValue\":null,\"activatedAt\":\"2021-09-28 10:05:43\",\"valueDesc\":\""+paramDesc+"\"}],\"paramConfigId\":\"172933452237551617\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\",\"equipmentName\":\"\"}";
		HttpTestUrl httpTestUrl = new HttpTestUrl(clurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			logger.boxLog(1, "修改主数据参数MT0028成功", "参数MT0028修改为：" + paramValue);
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
	}
	
	/**
	  * 将默认支付方式全部设置为现金
	 */
	public void setDefaultPaymentToCash(String syncHost) {
		String clurl = "http://" + syncHost + "/finance-mdm/api/v1/app_finance_mdm/payment_method/query/pos_payment_method";
		String json = "{\"soid\":[\""+Data.hospital_soid+"\"],\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", wnwd.getCookieValue("BEARER_TOKEN"));
		HttpTest test = SdkTools.post("结算完成", clurl, header, json, null, logger);
		List<List<String>> paramsList = new ArrayList<List<String>>();
		List<String> params = new ArrayList<String>();
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			String content = test.getResponseContent();
			JsonParser parser = new JsonParser();
			JsonObject contentJson = parser.parse(content).getAsJsonObject();
			JsonObject dataObject = contentJson.getAsJsonObject("data");
			
			JsonArray dataArray = dataObject.getAsJsonArray("hospitalPosList").get(0)
					.getAsJsonObject().getAsJsonArray("posList");

			for (int i = 0; i < dataArray.size(); i++) {
				params = new ArrayList<String>();
				JsonArray paymentMethodList =  dataArray.get(i).getAsJsonObject().getAsJsonArray("paymentMethodList");
				params.add(dataArray.get(i).getAsJsonObject().get("posCode").getAsString());
				for (int j = 0; j < paymentMethodList.size(); j++) {
					if(paymentMethodList.get(j).getAsJsonObject().get("medInstiPaymentMethodName").getAsString().equals("现金")) {
						params.add(paymentMethodList.get(j).getAsJsonObject().get("posPaymentMethodId").getAsString());
					}
				}
				paramsList.add(params);
			}
			logger.boxLog(1, "查询医院的收费点信息列表成功", "查询医院的收费点信息列表成功");
		} else {
			logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
					+ test.getResponseContent());
		}
		for(int k=0;k<paramsList.size();k++) {
			clurl = "http://" + syncHost + "/finance-mdm/api/v1/app_finance_mdm/pos_payment_method/enable/by_pos_payment_method_id";
			json = "{\"posPaymentMethodId\":\""+paramsList.get(k).get(1)+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"posCode\":\""+paramsList.get(k).get(0)+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
			test = SdkTools.post("结算完成", clurl, header, json, null, logger);
			if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
				logger.boxLog(1, "设置收费点默认支付方式", "医院的收费点默认支付方式被修改为：现金");
			} else {
				logger.assertFalse(true, "报错", "请求地址: " + clurl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: "
						+ test.getResponseContent());
			}
		}
	}
	
	//获取当前患者开立的最后一个西药的药房信息
	public Map<String,String> getPharmacyInfoByPatientName(String patientName) {
		String encounterId = "";
		if(patientName.substring(0, 2).equals("门诊")) {
			encounterId = db60.getEnCounterIdbyFullName(patientName).get("ENCOUNTER_ID");//门诊
		}else if(patientName.substring(0, 2).equals("住院")){
			encounterId = db60.getEncounterInfoByFullName(patientName).get("ENCOUNTER_ID");//住院
		}
		if(encounterId==null||encounterId=="") {
			logger.assertFalse(true, "未找到患者信息", "");
		}
		String pharmacyId = db60.getPharmacyIdbyEncounterId(encounterId);
		Map<String,String> pharmacyInfo = db60.getOrgById(pharmacyId);
		return pharmacyInfo;
	}
	
	//进入指定模块
	public void loginMenuByName(String menuName) {
		wnwd.waitElementByXpathAndClick("菜单展开按钮", "//span[@class='text']//i", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("菜单搜索框", "//div[@class='search']//input", menuName, Framework.defaultTimeoutMax);
		WebElement el = wnwd.waitElementByXpath(menuName+"入口", "//span[@class='child-menu-name' and contains(text(),'"+menuName+"')]", Framework.defaultTimeoutMax);
		if(el!=null) {
			wnwd.wnDoubleClickElementByMouse(el, "双击"+menuName);
		}else {
			throw new Error("未找到"+menuName+"菜单");
		}
		wnwd.checkElementByXpath(menuName+"已打开", "//div[@class='nav-item']//div[contains(text(),'"+menuName+"')]", Framework.defaultTimeoutMax);
	}
	
	//根据患者姓名查找
	public void searchPatientByName(String patientName) {
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		WebElement el1 = wnwd.waitElementByXpathAndClick("搜索按钮", "//button//span[contains(text(),'搜索')]", Framework.defaultTimeoutMin);
		if(el1==null) {
			wnwd.waitElementByXpathAndClick("更多搜索条件", "//i[@class='win-icon-more']", Framework.defaultTimeoutMin);
			wnwd.waitElementByXpathAndClick("搜索按钮", "//button//span[contains(text(),'搜索')]", Framework.defaultTimeoutMin);
		}
		WebElement el2 = wnwd.waitElementByXpath("搜索框", "//div[@class='win-remote__patient']//input", Framework.defaultTimeoutMax);
		wnwd.wnEnterText(el2,patientName,"患者姓名搜索框");
		wnwd.checkElementByXpath("用户搜索结果展示框", "//ul[@class='el-scrollbar__view el-select-dropdown__list']",Framework.defaultTimeoutMax);
		WebElement patient = wnwd.waitElementByXpath("患者:" + patientName,"//li[contains(@class,'el-select-dropdown__item')]//div[contains(text(),'"+patientName+"')]", Framework.defaultTimeoutMax);
		if(patient!=null) {
			wnwd.wnDoubleClickElementByMouse(patient, "双击患者:" + patientName);
		}else {
			throw new Error("未搜索找到患者："+patient);
		}
		
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
	}
	
	//结算并获取金额
	public String inpatientCharges(String patientName) {
		loginMenuByName("住院结算");
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("搜索患者成功标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMid);
		WebElement el = wnwd.waitElementByXpath("出院结算按钮", "//button//span[contains(text(),'出院结算')  or .='结算']", Framework.defaultTimeoutMin);
		if(el!=null) {
			wnwd.waitElementByXpathAndClick("出院结算按钮", "//button//span[contains(text(),'出院结算')  or .='结算']", Framework.defaultTimeoutMax);
		}else {
			wnwd.waitElementByXpathAndClick("中途结算按钮", "//span[contains(text(),'中途结算')]", Framework.defaultTimeoutMax);
			List<WebElement> checkboxes = wnwd.waitElementListByXpath("//div[@class='signle-fee-detail']//thead//span[@class='el-checkbox__inner']", Framework.defaultTimeoutMax);
			if(checkboxes!=null) {
				for (WebElement checkedBox : checkboxes) {
					wnwd.wnClickElement(checkedBox, "勾选 " + checkedBox.getText(), false, false);
				}
			}else {
				throw new Error("未找到账单");
			}
			wnwd.waitElementByXpathAndClick("结算医保类型选择框", "//input[@placeholder='请选择医疗保险']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("结算医保类型：自费", "//li[contains(text(),'自费')]", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("创建账单按钮", "//span[contains(text(),'创建账单')]", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("结算按钮", "//span[.='结算']", Framework.defaultTimeoutMax);
		}
		WebElement el2 = wnwd.waitElementByXpath("收银框", "//div[@class='el-dialog__title' and contains(.,'收银')]", Framework.defaultTimeoutMax);
        String cost = "";
        if(el2!=null) {
    		cost = wnwd.waitElementByXpath("获取金额", "//div[@class='money-info' and contains(text(),'应收金额')]//span", Framework.defaultTimeoutMax).getText().replace("￥", "");
    		wnwd.waitElementByXpathAndInput("现金金额输入框", "//div[@class='pay-method-item']//div[contains(.,'现金')]/following-sibling::*//input", cost, Framework.defaultTimeoutMax);
        	wnwd.waitElementByXpathAndClick("收银按钮", "//div[@class='el-dialog__body']//button//span[contains(text(),'收银')]", Framework.defaultTimeoutMax);
    		wnwd.checkElementByXpath("结算成功标识","//div[@status='success']//div[contains(text(),'结算成功')]",Framework.defaultTimeoutMax);
        }else {
        	throw new Error("结算未弹出收银框");
        }
		return cost;
	}
	
	//取消结算
	public void cancelInpatientCharges(String patientName) {
		loginMenuByName("取消结算");
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("搜索患者成功标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("取消结算按钮", "//span[contains(text(),'取消结算')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[@class='el-popconfirm']//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待记录消失", "//div[@class='cancel-settle-content-list-table']//tbody/tr", Framework.defaultTimeoutMid);
	}
	
	//门诊收费并获取金额
	public String outpatientCharges(String patientName) {
		loginMenuByName("门诊收费");
		WebElement el = wnwd.waitElementByXpath("药房弹框", "//div[@class='el-dialog__body']", Framework.defaultTimeoutMin);
		if(el==null) {
			wnwd.waitElementByXpathAndClick("用户名", "//span[@class='userName']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("弹出发药窗口", "//div[@class='action-setting-list']//p[contains(text(),'收费窗口')]", Framework.defaultTimeoutMax);
		}
		wnwd.waitElementByXpathAndClick("收费窗口下拉框", "//label[contains(.,'收费窗口')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("第一个收费窗口", "//li[contains(@class,'el-select-dropdown__item')]//span", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("搜索患者成功标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("收银按钮", "//button//span[contains(text(),'收') and contains(text(),'银')]", Framework.defaultTimeoutMax);
        WebElement el2 = wnwd.waitElementByXpath("收银框", "//div[@aria-label='收银']", Framework.defaultTimeoutMax);
        String cost = "";
        if(el2!=null) {
        	cost = wnwd.waitElementByXpath("应收金额", "//span[contains(text(),'应收金额')]/following-sibling::*", Framework.defaultTimeoutMax).getText();
        	wnwd.waitElementByXpathAndInput("现金金额输入框", "//div[@class='pay-method-item']//div[contains(.,'现金')]/following-sibling::*//input", cost, Framework.defaultTimeoutMax);
        	wnwd.waitElementByXpathAndClick("确定按钮", "//div[@aria-label='收银']//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
        }else {
        	throw new Error("结算未弹出收银框");
        }
		return cost;
	}
	
	//根据患者姓名和费用查询门诊费用信息
	public void queryOutpatientCharges(String patientName,String cost) {
		loginMenuByName("门诊费用查询");
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("查询记录", "//div[contains(@class,'el-collapse-item') and contains(.,'"+patientName+"') and contains(.,'"+cost.replaceAll("0+?$", "").replaceAll("[.]$", "")+"')]", Framework.defaultTimeoutMax);
	}
	
	//药房选择(登录某些模块后，选择药房)
	public void pharmacySelection(String pharmacyName) {
		WebElement el = wnwd.waitElementByXpath("药房弹框", "//div[@class='el-dialog__body']", 5000);
		if(el==null) {
			wnwd.waitElementByXpathAndClick("用户名", "//span[@class='userName']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("选择当前库房按钮", "//div[@class='action-setting-list']//p[contains(text(),'当前库房')]", Framework.defaultTimeoutMax);
		}
		wnwd.waitElementByXpathAndClick("当前库房选择下拉框", "//label[contains(.,'当前库房')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("药房："+pharmacyName, "//span[.='"+pharmacyName+"']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
	}
	
	//门诊退费
	public void outpatientChargesBack(String outpatientNumber,String patientName,String price) {
		loginMenuByName("门诊退费");
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.sleep(3000);
		wnwd.waitElementByXpathAndClick("门诊号按钮", "//button//span[contains(text(),'门诊号') or contains(text(),'门诊病历号')]", Framework.defaultTimeoutMax);
		WebElement el = wnwd.waitElementByXpath("搜索框", "//div[@class='win-remote__patient']//input", Framework.defaultTimeoutMax);
		wnwd.wnEnterText(el,outpatientNumber,"搜索框输入门诊号");
		wnwd.sendKeyEvent(Keys.ENTER);
		WebElement el1 = wnwd.waitElementByXpath("用户搜索结果展示框", "//ul[@class='el-scrollbar__view el-select-dropdown__list']",5000);
		if(el1!=null) {
			List<WebElement> searchResults = wnwd.waitElementListByXpath("//li[contains(@class,'el-select-dropdown__item')]", Framework.defaultTimeoutMax);
			// 搜索结果是空 直接报错
			if (searchResults == null || searchResults.size() == 0) {
				logger.assertFalse(true, "大his无搜索结果", "未找到此门诊号用户:" + outpatientNumber);
			}
			WebElement patient = wnwd.waitElementByXpath("患者:" + outpatientNumber,"//li[contains(@class,'el-select-dropdown__item')]//div[contains(text(),'"+price+"')]", Framework.defaultTimeoutMax);
			if(patient!=null) {
				wnwd.wnDoubleClickElementByMouse(patient, "双击患者:" + patientName);
			}else {
				throw new Error("未搜索找到患者："+patient);
			}
		}
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("搜索患者成功标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		WebElement el2 = wnwd.waitElementByXpath("已勾选的全退勾选框", "//span[contains(text(),'全退')]//label[contains(@class,'is-checked')]", Framework.defaultTimeoutMin);
		if(el2==null) {
			wnwd.waitElementByXpathAndClick("全退勾选框", "//span[contains(text(),'全退')]//label//span", Framework.defaultTimeoutMin);
		}
    	wnwd.waitElementByXpathAndClick("退费按钮", "//button//span[contains(text(),'退  费')]", Framework.defaultTimeoutMax);
    	wnwd.checkElementByXpath("退费确认弹窗", "//span[contains(text(),'确认要进行退费吗？')]", Framework.defaultTimeoutMax);
    	wnwd.waitElementByXpathAndClick("确定按钮", "//div[@role='dialog']//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
    	wnwd.checkElementByXpath("退费成功提示", "//p[contains(text(),'退费成功')]", Framework.defaultTimeoutMax);
	}
	
	//住院发药功能
	public void InpatientDispensing(String pharmacyName,String patientName) {
		loginMenuByName("住院发药");
		pharmacySelection(pharmacyName);
		searchPatientByName(patientName);
		wnwd.waitElementByXpathAndClick("需要发药的医嘱", "//div[@class='tree-content']//label/span", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		WebElement el = wnwd.waitElementByXpath("医嘱全选框选中状态", "//span[contains(text(),'全选')]/parent::*[contains(@class,'is-checked')]", Framework.defaultTimeoutMin);
		if(el==null) {
			wnwd.waitElementByXpathAndClick("医嘱全选框", "//span[contains(text(),'全选')]", Framework.defaultTimeoutMin);
		}
		wnwd.waitElementByXpathAndClick("发药确认按钮", "//span[contains(text(),'发药确认')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("发药成功标识", "//div[@class='finish']//span[contains(text(),'发药计费')]/following-sibling::*[contains(text(),'已完成')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("关闭弹框", "//div[@class='el-dialog' and @aria-label='发药中']//i", Framework.defaultTimeoutMax);
	}
	
	//住院退药功能
	public void inpatientDispensingBack(String pharmacyName,String patientName) {
		loginMenuByName("住院退药");
		pharmacySelection(pharmacyName);
		searchPatientByName(patientName);
		wnwd.waitElementByXpathAndClick("需要退药的患者", "//div[contains(@class,'tree-content')]//label/span", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("退药的医嘱全选框", "//div[@class='list-content']//div[contains(@class,'header')]//label", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确认退药按钮", "//span[contains(text(),'退药确认')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("退药中弹框", "//div[@class='el-dialog']//span[contains(text(),'退药中')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("退药成功标识", "//div[@class='finish']//span[contains(text(),'退药计费')]/following-sibling::*[contains(text(),'已完成')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("弹框关闭", "//div[@class='el-dialog' and @aria-label='退药中']//i", Framework.defaultTimeoutMax);
	}
	
	//门诊发药功能
	public void OutpatientDispensing(String pharmacyName,String patientName) {
		loginMenuByName("门诊发药");
		WebElement el = wnwd.waitElementByXpath("药房弹框", "//div[@class='el-dialog__body']", Framework.defaultTimeoutMin);
		if(el==null) {
			wnwd.waitElementByXpathAndClick("用户名", "//span[@class='userName']", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("弹出发药窗口", "//div[@class='action-setting-list']//p[contains(text(),'发药窗口')]", Framework.defaultTimeoutMax);
		}
		wnwd.waitElementByXpathAndClick("发药窗口下拉框", "//label[contains(.,'发药窗口')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("药房："+pharmacyName, "//li[contains(@class,'el-select-dropdown__item')]//span[contains(text(),'"+pharmacyName+"')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("登录状态下拉框", "//label[contains(.,'登录状态')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("在线", "//li[contains(@class,'el-select-dropdown__item')]//span[contains(text(),'在线')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		searchPatientByName(patientName);
		wnwd.waitElementByXpathAndClick("发药确认按钮", "//span[contains(text(),'发药确认')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("发药成功提示", "//p[contains(text(),'发药成功')]", Framework.defaultTimeoutMax);
	}
	
	//门诊退药功能
	public void OutpatientDispensingBack(String pharmacyName,String patientName) {
		loginMenuByName("门诊退药");
		pharmacySelection(pharmacyName);
		searchPatientByName(patientName);
		wnwd.waitElementByXpathAndClick("确认退药按钮", "//span[contains(text(),'确认退药')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("退药成功提示", "//p[contains(text(),'退药成功')]", Framework.defaultTimeoutMax);
	}
	
	//患者信息登记
	public ArrayList<String> outpatientInformationSetUp() {
		loginMenuByName("患者建档");
		ArrayList<String> patInfo = new ArrayList<String>();
		wnwd.waitElementByXpathAndClick("保险信息选择下拉框", "//input[contains(@placeholder,'请选择保险')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("保险信息：自费", "//span[.='自费']",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("证件类型选择下拉框", "//input[contains(@placeholder,'请选择证件')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("证件类型：居民身份证", "//span[contains(text(),'居民身份证')]",Framework.defaultTimeoutMax);
		String IDCode = IdCardGenerator.IDCardCreate();
        wnwd.waitElementByXpathAndInput("身份证号码填写框", "//input[@placeholder='请输入证件号']", IDCode, Framework.defaultTimeoutMax);
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String patientName = "门诊患者" + format.format(date) ;
        int phoneNum = (int) ((Math.random() * 9 + 1) * 10000000);
        wnwd.waitElementByXpathAndInput("姓名填写框", "//input[@placeholder='请输入姓名']", patientName, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("联系电话填写框", "//label[contains(text(),'联系电话')]/following-sibling::*//input", "138"+phoneNum,Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("国籍选择下拉框", "//input[contains(@placeholder,'请选择国籍')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("国籍：中国", "//span[.='中国']",Framework.defaultTimeoutMax);
        WebElement el = wnwd.waitElementByXpath("交易金额框", "//input[@placeholder='请填写充值金额']", Framework.defaultTimeoutMin);
        if(el!=null) {
        	 wnwd.moveToElementByXpath("鼠标移动到清空金额框", "//input[@placeholder='请填写充值金额']", Framework.defaultTimeoutMax);
             wnwd.waitElementByXpathAndClick("清空金额", "//input[@placeholder='请填写充值金额']/following-sibling::*//i", Framework.defaultTimeoutMax);
        }
        wnwd.waitElementByXpathAndClick("保存档案按钮", "//span[contains(text(),'保存档案')]",Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("建档成功提示", "//p[contains(text(),'档案保存成功')]", Framework.defaultTimeoutMax);
        patInfo.add(patientName);
		return patInfo;
	}
	
	//挂号登记
	public ArrayList<String> encounter(String patientName,String subject,String doctorNo) {
		loginMenuByName("挂号登记");
		ArrayList<String> info = new ArrayList<String>();
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("搜索患者成功标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("按行排列", "//div[contains(@class,'regiest-searchBar')]//span[contains(@class,'line-type')]",Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("挂号科目填写框","//div[contains(@class,'regiest-searchBar')]//input[@placeholder='请输入内容']", subject,Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("搜索按钮","//div[contains(@class,'regiest-searchBar')]//i[contains(@class,'search')]",Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待页面加载中元素消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
        WebElement  ele1 = wnwd.waitElementByXpath("//div[@class='regist-list-table']//table//tr[@class='el-table__row' and contains(.,'"+subject+"') ]",Framework.defaultTimeoutMid);
        if(ele1==null) {
        	throw new Error("未找到挂号科目："+subject);
        }
//		wnwd.switchToDefaultContent();
        wnwd.waitElementByXpathAndClick("选择科目", "//div[@class='regist-list-table']//table//tr[@class='el-table__row' and contains(.,'"+subject+"') ]"+"//label",Framework.defaultTimeoutMax);
        WebElement el1 = wnwd.waitElementByXpath("选择医生框", "//div[@aria-label='选择挂号医生']", Framework.defaultTimeoutMid);
        if(el1!=null) {
        	wnwd.waitElementByXpathAndClick("医生："+doctorNo, "//div[contains(@class,'doctor') and contains(text(),'"+doctorNo+"')]",Framework.defaultTimeoutMax);
            wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确定')]",Framework.defaultTimeoutMax);
            wnwd.checkElementByXpath("成功选择医生提示"	, "//p[contains(text(),'已经成功选择')]", Framework.defaultTimeoutMax);
        }
        WebElement  ele = wnwd.waitElementByXpath("//div[@class='regiest-info-submit']//button[contains(.,'挂号')]",Framework.defaultTimeoutMid);
        wnwd.wnDoubleClickElementByMouse(ele, "挂号按钮");
        wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
        WebElement el2 = wnwd.waitElementByXpath("收银框", "//div[@aria-label='收银']", Framework.defaultTimeoutMax);
        if(el2!=null) {
        	String money = wnwd.waitElementByXpath("应收金额", "//span[contains(text(),'应收金额')]/following-sibling::*", Framework.defaultTimeoutMax).getText();
        	wnwd.waitElementByXpathAndInput("现金金额填写框", "//div[@class='pay-method-item']//div[contains(.,'现金')]/following-sibling::*//input", money, Framework.defaultTimeoutMax);
        	wnwd.waitElementByXpathAndClick("确定按钮", "//div[@aria-label='收银']//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
        }
        wnwd.checkElementByXpath("支付成功提示"	, "//p[contains(text(),'支付成功')]", Framework.defaultTimeoutMax);
        long endTime = System.currentTimeMillis() + Framework.defaultTimeoutMax;
        String seqNo = "";
        while (System.currentTimeMillis() <= endTime) {
            SdkTools.sleep(3000);
            seqNo = db60.getSeqNoByPatientName(patientName).get("seqNo");
            if(seqNo!=null) {
            	break;
            }
        }
        
        String omrn = db60.getSeqNoByPatientName(patientName).get("OMRN");
        info.add(seqNo);
        info.add(omrn);
        return info;
	}
	
	//挂号查询
	public void queryEncounterRecord(String patientName,String subject) {
		loginMenuByName("挂号查询");
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("查询记录", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]", Framework.defaultTimeoutMax);
		logger.boxLog(1, "查询成功", "查询到用户:"+patientName);
	}
	
	//退号
	public void cancelEncounter(String patientName,String subject) {
		wnwd.checkElementByXpath("查询记录", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("退号按钮", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]//button//span[contains(.,'退号')]", Framework.defaultTimeoutMax);
    	wnwd.waitElementByXpathAndClick("确定按钮", "//div[@class='el-message-box']//span[contains(.,'确定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("退号成功提示"	, "//p[contains(text(),'退号成功')]", 60000);
    	wnwd.waitNotExistByXpath("查询记录不存在", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]", Framework.defaultTimeoutMax);
	}
	
	//预约挂号
	public ArrayList<String> appointmentRegister(String patientName) {
		ArrayList<String> info = new ArrayList<String>();
		loginMenuByName("门诊预约");
		searchPatientByName(patientName);
        List<WebElement>   eles = wnwd.waitElementListByXpath("//div[contains(@class,'book-calender-item')]",Framework.defaultTimeoutMid);
        if(eles.size()==0) {
        	logger.boxLog(0, "暂无可预约班次", "暂无可预约班次");
        	info.add("false");
        	return info;
        }
        info.add("true");
        String subject = "";
        WebElement el = wnwd.waitElementByXpath("第一个科目","//div[contains(@class,'book-calender-item')]//div[@class='subject-name']", Framework.defaultTimeoutMax);
        if(el!=null) {
        	subject = el.getText();
        }else {
        	throw new Error("未找到可预约挂号的科目");
        }
        info.add(subject);
        wnwd.waitElementByXpathAndClick("第一个科目","//div[contains(@class,'book-calender-item')]//div[@class='subject-name']", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("第一个时间段","//div[contains(@class,'time-item')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("确定预约按钮","//span[contains(.,'确认预约')]", Framework.defaultTimeoutMax);
		WebElement dialog = wnwd.waitElementByXpath("确认预约信息框","//div[@role='dialog' and @aria-label='确认预约信息']", Framework.defaultTimeoutMid);
		if(dialog!=null) {
	        wnwd.waitElementByXpathAndClick("确定预约按钮","//div[@role='dialog' and @aria-label='确认预约信息']//button//span[contains(.,'确认预约')]", Framework.defaultTimeoutMax).getText();
	        wnwd.checkElementByXpath("预约成功提示", "//p[contains(text(),'预约成功')]", Framework.defaultTimeoutMax);
		}
		return info;
	}


	//预约挂号查询
	public void queryAppointmentRegister(String patientName,String subject) {
		loginMenuByName("预约查询");
		wnwd.waitElementByXpathAndClick("开始日期", "//input[@placeholder='开始日期']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("开始日期：今天", "//td[contains(@class,'today')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("结束日期：下个月", "//div[contains(@class,'right')]//td[contains(@class,'prev-month')]", Framework.defaultTimeoutMax);
		searchPatientByName(patientName);
		wnwd.checkElementByXpath("查询记录", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"')]", Framework.defaultTimeoutMax);
		logger.boxLog(1, "查询成功", "查询到用户:"+patientName);
	}
	
	//取消预约
	public void cancelAppointmentRegister(String patientName,String subject) {
		wnwd.checkElementByXpath("查询记录", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("退号按钮", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') ]//button//span[contains(.,'取消预约')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("取消预约原因填写框", "//div[@class='el-message-box']//input","退号原因", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[@class='el-message-box']//span[contains(.,'确定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("取消操作成功提示", "//p[contains(text(),'操作成功')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("查询记录状态变成已取消", "//tr[@class='el-table__row' and contains(.,'"+patientName+"') and contains(.,'"+subject+"') and contains(.,'已取消')]", Framework.defaultTimeoutMax);
		logger.boxLog(1, "查询成功", "查询到用户:"+patientName+"的状态为'已取消'");
	}
	
	//大HIS系统患者入院预约
    public ArrayList<String> admissionAppointment(String subject, String ward) {
    	loginMenuByName("入院预约");
        ArrayList<String> patInfo = new ArrayList<String>();
        wnwd.waitElementByXpathAndClick("保险信息选择下拉框", "//input[contains(@placeholder,'请选择保险')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("保险信息：自费", "//span[.='自费']",Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("证件类型", "//input[contains(@placeholder,'请选择证件')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("证件类型：居民身份证", "//span[contains(text(),'居民身份证')]",Framework.defaultTimeoutMax);
        String IDCode = IdCardGenerator.IDCardCreate();
        wnwd.waitElementByXpathAndInput("身份证号码输入框", "//input[@placeholder='请输入证件号']", IDCode, Framework.defaultTimeoutMax);
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String patientName = "住院患者" + format.format(date) ;
        wnwd.waitElementByXpathAndInput("姓名填写框", "//input[@placeholder='请输入姓名']", patientName, Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("入院途径下拉框", "//label[contains(text(),'入院途径')]/following-sibling::*//input/following-sibling::*/span", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("入院途径：门诊", "//li//span[.='门诊']", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("拟入院科室下拉框", "//label[contains(text(),'拟入院科室')]/following-sibling::*//input/following-sibling::*/span", Framework.defaultTimeoutMax);

        wnwd.waitElementByXpathAndClick("拟入院科室："+subject, "//li//span[contains(text(),'"+subject+"')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("拟入院病区下拉框", "//label[contains(text(),'入院病区')]/following-sibling::*//input/following-sibling::*/span", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("拟入院病区："+ward, "//li//span[contains(text(),'"+ward+"')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("危重级别下拉框", "//label[contains(text(),'危重级别')]/following-sibling::*//input/following-sibling::*/span", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndClick("危重级别：一般", "//li//span[.='一般']", Framework.defaultTimeoutMax);
        WebElement btn = wnwd.waitElementByXpath("确定按钮", "//div[@class='inHosp-info']//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
        wnwd.wnDoubleClickElementByMouse(btn, "确定按钮");
        wnwd.checkElementByXpath("入院预约成功提示", "//p[contains(text(),'入院预约成功')]", Framework.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
        patInfo.add(patientName);
        return patInfo;
    }
    
    //入院登记
    public void admissionRegistration(String patientName) {
    	loginMenuByName("入院登记");
    	searchPatientByName(patientName);
		WebElement diag = wnwd.waitElementByXpath("引用弹框","//div[@class='el-message-box']", Framework.defaultTimeoutMid);
		if(diag!=null) {
	        wnwd.waitElementByXpathAndClick("去引用按钮", "//div[@class='el-message-box']//span[contains(.,'去引用')]", Framework.defaultTimeoutMax);
	        wnwd.waitElementByXpathAndClick("引用按钮", "//div[contains(@aria-label,'入院预约')]//button[contains(.,'引 用')]", Framework.defaultTimeoutMax);
		}
        wnwd.waitElementByXpathAndInput("停药线填写框", "//label[contains(text(),'停药线')]/following-sibling::*//input", "1", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("报警线填写框", "//label[contains(text(),'报警线')]/following-sibling::*//input", "1", Framework.defaultTimeoutMax);
        WebElement btn = wnwd.waitElementByXpath("确定", "//div[@class='inHosp-info']//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
        wnwd.wnDoubleClickElementByMouse(btn, "确定按钮");
        wnwd.checkElementByXpath("入院登记成功提示", "//p[contains(text(),'保存成功')]", Framework.defaultTimeoutMax);

        WebElement ele = wnwd.waitElementByXpath("住院预交金弹框", "//div[@aria-label='住院预交金']//span[contains(text(),'保 存')]", Framework.defaultTimeoutMid);
        if(ele!=null) {
        	wnwd.waitElementByXpathAndClick("直接关闭，不交预交金", "//div[@aria-label='住院预交金']//i[contains(@class,'-close')]", Framework.defaultTimeoutMax);
        }
    }
    
    //入院查询
    public void queryHospitalized(String patientName) {
    	loginMenuByName("入院查询");
    	searchPatientByName(patientName);
		wnwd.checkElementByXpath("列表中查询"+patientName, "//tr[@class='el-table__row' and contains(.,'"+patientName+"')]", Framework.defaultTimeoutMax);
		logger.boxLog(1, "查询成功", "查询到用户:"+patientName);
    }

    //入院取消
    public void cancelHospitalized(String patientName) {
		wnwd.checkElementByXpath("查询记录", "//tr[@class='el-table__row' and contains(.,'"+patientName+"')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("退号按钮", "//tr[@class='el-table__row' and contains(.,'"+patientName+"')]//button//span[contains(.,'入院取消')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[@class='el-message-box']//span[contains(.,'确定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("取消操作成功提示", "//p[contains(text(),'您已取消了【"+patientName+"】的入院登记')]", Framework.defaultTimeoutMax);
    }
    
    //缴纳押金
    public void depositPayment(String cost) {
		wnwd.waitElementByXpathAndClick("缴纳押金选项", "//label[contains(.,'缴纳押金')]", Framework.defaultTimeoutMax);
        wnwd.waitElementByXpathAndInput("缴纳金额填写框", "//label[contains(.,'缴纳金额')]//following-sibling::*//input", cost, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("现金选项", "//label[contains(.,'现金')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("保存按钮", "//button//span[contains(text(),'保 存')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("交易确认弹框", "//div[@class='el-dialog']//span[contains(text(),'交易确认')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[@class='el-dialog']//span[contains(.,'确 定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("缴纳押金成功提示", "//p[contains(text(),'缴纳押金成功')]", Framework.defaultTimeoutMax);
    }
    
    //红冲押金(红冲第一条记录)
    public void reversePayment(){
		wnwd.waitElementByXpathAndClick("红冲押金选项", "//label[contains(.,'红冲押金')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("红冲按钮", "//span[.='红冲']", Framework.defaultTimeoutMax);
		WebElement el = wnwd.waitElementByXpath("业务授权弹框", "//span[@class='el-dialog__title' and .='业务授权']", Framework.defaultTimeoutMin);
		if(el!=null) {
			wnwd.waitElementByXpathAndClick("填写框", "//label[contains(.,'授权人员')]/following-sibling::*//input[contains(@placeholder,'工号')]",Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("授权人员名单：第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndInput("员工密码填写框", "//label[contains(.,'员工密码')]/following-sibling::*//input[@type='text']",Data.default_user_login_pwd,Framework.defaultTimeoutMax);
			wnwd.waitElementByXpathAndClick("确认按钮", "//span[contains(text(),'确 认')]", Framework.defaultTimeoutMax);
		}
        wnwd.checkElementByXpath("红冲押金成功提示", "//p[contains(text(),'红冲押金成功')]", Framework.defaultTimeoutMax);
    }
    
    //添加药品
    public void addMedicine(String medicineName) {
    	wnwd.waitElementByXpathAndClick("添加药品按钮", "//button//span[contains(text(),'添加药品')]", Framework.defaultTimeoutMax);
    	searchMedicineAndBatchNumber(medicineName);
    	wnwd.waitElementByXpathAndInput("数量填写框", "//label[contains(.,'出库数量') or contains(.,'退库数量')]/following-sibling::*//input", "1",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
    }
    
    //搜索药品及生产批号
    public void searchMedicineAndBatchNumber(String medicineName) {
    	searchMedicine(medicineName); 
		wnwd.waitElementByXpathAndClick("生产批号选择下拉框", "//label[contains(.,'生产批号')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("生产批号名单：第一条记录", "//div[contains(@class,'drug-select-table')]//table//tbody//tr", Framework.defaultTimeoutMax);
    }
    
    //添加药品中搜索药品
    public void searchMedicine(String medicineName) {
		wnwd.waitElementByXpathAndClick("药品名称选择下拉框", "//label[contains(.,'药品名称')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("药品名称选择输入框", "//label[contains(.,'药品名称')]/following-sibling::*//input", medicineName, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("药品名称：第一条记录", "//div[contains(@class,'drug-select-table')]//table//tbody//tr", Framework.defaultTimeoutMax);
    }
    
    //保存
    public void save(String msg) {
		wnwd.waitElementByXpathAndClick("保存按钮", "//span[contains(text(),'保存')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("数据新增成功提示", "//p[contains(text(),'"+msg+"')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
    }
    
    //复核
    public void check(String msg) {
		wnwd.waitElementByXpathAndClick("复核按钮", "//span[contains(text(),'复核')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("复核成功提示", "//p[contains(text(),'"+msg+"')]", Framework.defaultTimeoutMax);
        wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
    }
    
    //提交
    public void submit() {
		wnwd.waitElementByXpathAndClick("提交按钮", "//span[contains(text(),'提交')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("数据提交成功提示", "//p[contains(text(),'数据提交成功')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
    }
    
    public String queryStockByMedicineName(String medicineName) {
    	String stock = "";
		wnwd.waitElementByXpathAndInput("药品名称输入框", "//label[contains(.,'药品名称')]/following-sibling::*//input", medicineName, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("搜索按钮", "//button//span[contains(text(),'搜 索')]", Framework.defaultTimeoutMax);
		
		
		WebElement el = wnwd.waitElementByXpath("库存数量", "//tr[contains(.,'"+medicineName+"')]/td[6]//span[@class='blueFont']/span", Framework.defaultTimeoutMax);
    	if(el!=null) {
    		stock = el.getText();
    	}else {
    		throw new Error("未找到"+medicineName+"的库存数量");
    	}
		return stock;
    }
    
    //报溢入库新增
    public void addByMedicineStockInMore(String medicineName,String num) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("添加药品按钮", "//button//span[contains(text(),'添加药品')]", Framework.defaultTimeoutMax);
		searchMedicineAndBatchNumber(medicineName);
		wnwd.waitElementByXpathAndInput("报溢数量填写框", "//label[contains(.,'报溢数量')]/following-sibling::*//input", num,Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("选择下拉框", "//label[contains(.,'报溢原因')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("报溢原因：第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		save("数据新增成功");
		check("复核成功");
    }
    
    //采购入库新增
    public String addByMedicineStockInPurchase(String medicineName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		
		wnwd.waitElementByXpathAndClick("选择下拉框", "//label[contains(.,'供应商名称')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("供应商名称：第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("添加药品按钮", "//button//span[contains(text(),'添加药品')]", Framework.defaultTimeoutMax);
		searchMedicine(medicineName); 
		wnwd.waitElementByXpathAndClick("生产日期选择框", "//label[contains(.,'生产日期')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("生产日期", "//td[contains(@class,'today')]", Framework.defaultTimeoutMax);

		
		wnwd.waitElementByXpathAndClick("失效日期选择框", "//label[contains(.,'失效日期')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("失效日期", "//td[contains(@class,'current')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("生产批号填写框", "//label[contains(.,'生产批号')]/following-sibling::*//input","1", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("入库数量填写框", "//label[contains(.,'入库数量')]/following-sibling::*//input","1", Framework.defaultTimeoutMax);
		int a = ((int) ((Math.random() * 9 + 1) * 10000000));
		wnwd.waitElementByXpathAndInput("发票编号填写框", "//label[contains(.,'发票编号')]/following-sibling::*//input","00"+a, Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		save("数据新增成功");
		wnwd.waitElementByXpathAndClick("提交按钮", "//span[contains(text(),'提交')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@class,'el-message-box')]//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("数据提交成功提示", "//p[contains(text(),'数据提交成功')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("返回按钮", "//span[contains(text(),'返 回')]", Framework.defaultTimeoutMax);
		
		wnwd.waitElementByXpathAndClick("审核按钮", "//td[contains(.,'审核')]//button//span[.='审核']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("通过选项", "//div[contains(@class,'el-dialog')]//span[contains(text(),'通过')]", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@class,'el-dialog')]//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		String billNo= "";
		WebElement el =  wnwd.waitElementByXpath("采购入库第一条记录单据号", "//table//tbody//tr/td[3]/div", Framework.defaultTimeoutMax);
		if(el!=null) {
			billNo = el.getText();
		}else {
			throw new Error("未找到采购记录");
		}
		return billNo;
    }
    
    //采购退货新增
    public void addByMedicineStockRuturnPurchase(String medicineName,String billNo) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("供应商名称选择下拉框", "//label[contains(.,'供应商名称')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("供应商名称:第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("按采购入库单生成按钮", "//button//span[contains(text(),'按采购入库单生成')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("采购入库单号填写框", "//label[contains(.,'采购入库单号')]/following-sibling::*//input", billNo,Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("搜索按钮", "//button//span[contains(text(),'查 询')]", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("采购单据号："+billNo, "//table//tbody//td[contains(.,'"+billNo+"')]/preceding-sibling::*//label", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@aria-label,'采购入库单列表')]//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("编辑按钮", "//tr[contains(.,'"+billNo+"')]//td[contains(.,'编辑')]//button//span[.='编辑']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndInput("退货数量填写框", "//label[contains(.,'退货数量')]/following-sibling::*//input", "1",Framework.defaultTimeoutMax);
		int b = ((int) ((Math.random() * 9 + 1) * 10000000));
		wnwd.waitElementByXpathAndInput("发票编号填写框", "//label[contains(.,'发票编号')]/following-sibling::*//input", "00"+b,Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("发票日期选择框", "//label[contains(.,'发票日期')]/following-sibling::*//i[contains(@class,'date')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("发票日期", "//td[contains(@class,'today')]", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@aria-label,'编辑药品')]//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		save("数据新增成功");
		wnwd.waitElementByXpathAndClick("提交按钮", "//span[contains(text(),'提交')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@class,'el-message-box')]//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
		wnwd.checkElementByXpath("数据提交成功提示", "//p[contains(text(),'数据提交成功')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("返回按钮", "//span[contains(text(),'返 回')]", Framework.defaultTimeoutMax);
		
		wnwd.waitElementByXpathAndClick("审核按钮", "//td[contains(.,'审核')]//button//span[.='审核']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("通过选项", "//div[contains(@class,'el-dialog')]//span[contains(text(),'通过')]", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("确定按钮", "//div[contains(@class,'el-dialog')]//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);

    }
    
    //报损出库新增
    public void addByMedicineStockOutLoss(String medicineName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("添加药品按钮", "//button//span[contains(text(),'添加药品')]", Framework.defaultTimeoutMax);
		searchMedicineAndBatchNumber(medicineName);
		wnwd.waitElementByXpathAndInput("报损数量填写框", "//label[contains(.,'报损数量')]/following-sibling::*//input", "1",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("报损原因选择下拉框", "//label[contains(.,'报损原因')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("报损原因:第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		save("数据新增成功");
		check("复核成功");
    }
    
    //部门出库新增
    public void addByMedicineStockOutDepartment(String medicineName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("部门类型选择下拉框", "//label[contains(.,'部门类型')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("部门类型：第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("领药部门选择下拉框", "//label[contains(.,'领药部门')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("领药部门：第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		addMedicine(medicineName);
		save("数据新增成功");
		check("复核成功");
    }
    
    //职工出库新增
    public void addByMedicineStockOutWorkers(String medicineName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("领药职工选择下拉框", "//label[contains(.,'领药职工')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("领药职工名单:第一条记录", "//div[@class='el-scrollbar']//li", Framework.defaultTimeoutMax);
		addMedicine(medicineName);
		save("数据新增成功");
		check("复核成功");
    }
    
    
    //请调申请新增
    public String addByApplicationForTransfer(String medicineName,String outPharmacyName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("出库库房选择下拉框", "//label[contains(.,'出库库房')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick(outPharmacyName, "//div[@class='el-scrollbar']//li//span[.='"+outPharmacyName+"']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("添加药品按钮", "//button//span[contains(text(),'添加药品')]", Framework.defaultTimeoutMax);
		searchMedicine(medicineName);
		wnwd.waitElementByXpathAndInput("请调数量填写框", "//label[contains(.,'请调数量')]/following-sibling::*//input", "1",Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//span[contains(text(),'确 定')]", Framework.defaultTimeoutMax);
		save("");
		submit();
		String billNo= "";
		WebElement el = wnwd.waitElementByXpath("请调申请记录第一条记录单据号", "//table//tbody//tr/td[2]/div", Framework.defaultTimeoutMid);
		if(el!=null) {
			billNo = el.getText();
		}else {
			throw new Error("未找到请调申请记录");
		}
		return billNo;
    }

    //请调出库处理
    public String dealwithByMedicineStockOutTransfer(String billNo) {
		wnwd.waitElementByXpathAndClick("待处理出库单按钮", "//button//span[contains(text(),'待处理出库单')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		
		wnwd.waitElementByXpathAndClick("单据号："+billNo, "//div[contains(@class,'el-table__body-wrapper')]//table/tbody/tr[contains(.,'"+billNo+"')]", Framework.defaultTimeoutMax);

		wnwd.waitElementByXpathAndClick("生成出库按钮", "//button//span[contains(text(),'生成出库')]", Framework.defaultTimeoutMax);
		save("生成出库单成功");
		check("复核成功");
		
		wnwd.waitElementByXpathAndClick("返回按钮", "//span[contains(text(),'返 回')]", Framework.defaultTimeoutMax);

		WebElement el = wnwd.waitElementByXpath("请调出库第一条记录：单据号", "//table//tbody//tr/td[2]/div", Framework.defaultTimeoutMid);
		if(el!=null) {
			billNo = el.getText();
		}else {
			throw new Error("未找到请调出库记录");
		}
		 
		return billNo;
    }
    
    //请调入库处理
    public void dealwithByMedicineStockInTransfer(String billNo) {
		wnwd.waitElementByXpathAndClick("待处理入库单按钮", "//button//span[contains(text(),'待处理入库单')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("请调日期", "//input[@placeholder='开始日期']", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("开始日期：今天", "//td[contains(@class,'today')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("结束日期：下个月", "//div[contains(@class,'right')]//td[contains(@class,'prev-month')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("查询按钮", "//button//span[contains(text(),'查 询')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("单据号："+billNo, "//div[contains(@class,'el-table__body-wrapper')]//table/tbody/tr[contains(.,'"+billNo+"')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("生成入库按钮", "//button//span[contains(text(),'生成入库')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("复核成功提示", "//p[contains(text(),'复核成功')]", Framework.defaultTimeoutMax);
    }
    
    //请调退库处理
    public String dealwithByMedicineStockReturnTransfer(String medicineName,String returnPharmacyName) {
		wnwd.waitElementByXpathAndClick("新增按钮", "//button//span[contains(text(),'新增')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("退往库房选择下拉框", "//label[contains(.,'退往库房')]/following-sibling::*//input", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick(returnPharmacyName, "//div[@class='el-scrollbar']//li//span[.='"+returnPharmacyName+"']", Framework.defaultTimeoutMax);
		addMedicine(medicineName);
		
		save("数据新增成功");
		check("操作成功");
		String billNo= "";
		
		WebElement el = wnwd.waitElementByXpath("请调退库第一条记录单据号", "//table//tbody//tr/td[2]/div", Framework.defaultTimeoutMax);
		if(el!=null) {
			billNo = el.getText();
		}else {
			throw new Error("未找到请调退库记录");
		}
		return billNo;
    }
    
    //请调退库接收
    public void dealwithByMedicineStockReceiveTransfer(String billNo) {
		wnwd.waitElementByXpathAndClick("待退库单按钮", "//button//span[contains(text(),'待退库单')]", Framework.defaultTimeoutMax);
		wnwd.waitNotExistByXpath("等待加载框消失", WnWINEXXpath.loading, Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("单据号："+billNo, "//table//tbody//div[contains(text(),'"+billNo+"')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("生成退库单按钮", "//button//span[contains(text(),'生成退库单')]", Framework.defaultTimeoutMax);
		wnwd.waitElementByXpathAndClick("确定按钮", "//button//span[contains(text(),'确定')]", Framework.defaultTimeoutMax);
        wnwd.checkElementByXpath("退库成功提示", "//p[contains(text(),'退库成功')]", Framework.defaultTimeoutMax);
    }


}
