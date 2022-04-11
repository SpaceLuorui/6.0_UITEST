package com.winning.testsuite;

import com.winning.manager.HisSqlManager;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Cookie;
import xunleiHttpTest.HttpTest;
import xunleiHttpTest.HttpTestHeader;
import xunleiHttpTest.HttpTestUrl;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;


import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientTestBase;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDoctorPermission extends OutpatientTestBase {

	public TestDoctorPermission() {
		super();
	}

	// 权限枚举
	public enum PermissonType {
		Drug("256702", "西药开立权限", "您暂无开具\"西药\"的权限"), CPM("256703", "中成药开立权限", "您暂无开具\"中成药\"的权限"),
		Herb("256704", "中草药开立权限", "60无搜索结果"), // 中草药权限关闭时，就搜索不到药品，不会有warning
		SpiritOne("256705", "第一类精神药品开立权限", "您暂无开具\"第一类精神药品\"的权限"),
		SpiritTwo("256706", "精二类精神药品开立权限", "您暂无开具\"第二类精神药品\"的权限"),
		NarcoticDrug("256707", "麻醉药品开立权限", "您暂无开具\"麻醉药品\"的权限"), HighlyToxic("256708", "剧毒药品开立权限", "您暂无开具\"剧毒药品\"的权限"),
		RadioPharmaceuticals("256709", "放射性药品开立权限", "您暂无开具\"放射性药品\"的权限"),
		HighPriced("399217887", "高价药开立权限", "您暂无开具\"高价药\"的权限"), GZ("399283812", "挂账权限", ""),
		Antibacterials_Unrestricted("384040", "非限制级", "抗菌药物非限制使用级别权限"),
		Antibacterials_Restricted("384041", "限制级", "抗菌药物限制使用级别权限"),
		Antibacterials_Special("384042", "特殊级", "抗菌药物特殊使用级别权限"), Secrecy_zero("399057447", "0级", ""),
		Secrecy_one("399057448", "1级", ""), Secrecy_two("399057449", "2级", ""), Secrecy_three("399057450", "3级", ""),
		Secrecy_four("399057451", "4级", ""), Secrecy_five("399057452", "5级", "");

		public final String Code;
		public final String Desc;
		public final String WarnMsg;

		PermissonType(String PermissionCode, String PermissionDesc, String WarnMsg) {
			this.Code = PermissionCode;
			this.Desc = PermissionDesc;
			this.WarnMsg = WarnMsg;
		}

	}

	// 有没有权限的控制
	public enum OwnType {
		Have, // 有权限
		No // 无权限
	}

	static {
		SdkTools.initReport("医生权限专项", "DoctorPermission.html");
//        Data.headless = true;
		Data.checkWarning = true;
		Data.patientage = 1; // 由于接口调用更新保密等级，需要用到出生日期，因此统一更新为1岁
//        Data.ignoreErrors="浏览器模式下，不支持混合框架功能";
		Data.closeBrowser = false;

//        Frmcons.autoReport = false;
//        Public.initReport_tc("医生权限专项", "DoctorPermission.html");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
	}

	private static String wjtest_EmployeeId = "137504213352607744";

	/***
	 * 1.用例标题 2.登录账户 3.开立药品
	 **/

	// 给wjtest账户设置唯一权限,比如设置有权限则设置此权限为有，其它权限设置为无;如果设置为无，则其它设置为有
	public void SetPermission(OutpatientBrowser br, OwnType OType, PermissonType Ptype, String employeeId) {

		String REurl = "http://" + Data.host
				+ "/clinical-mdm/api/v1/app_clinical_mdm/doctor_prescription_permission/sync";
		Cookie cookie = br.wnOutpatientWorkflow.wnwd.getCookieNamed("BEARER_TOKEN");
		String json = "";
		if (Ptype.equals(PermissonType.Drug)) {
			if (OType.equals(OwnType.Have)) // 只有西药权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 全无
				json = "{\"docPrescPermissionInputDTOS\":[],\"employeeIdInputDTOs\":[{\"employeeId\":\"" + employeeId
						+ "\"}]}";
		} else if (Ptype.equals(PermissonType.CPM)) {
			if (OType.equals(OwnType.Have)) // 只有中成药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No))
				json = "{\"docPrescPermissionInputDTOS\":[],\"employeeIdInputDTOs\":[{\"employeeId\":\"" + employeeId
						+ "\"}]}";
		} else if (Ptype.equals(PermissonType.Herb)) {
			if (OType.equals(OwnType.Have)) // 只有中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No))
				json = "{\"docPrescPermissionInputDTOS\":[],\"employeeIdInputDTOs\":[{\"employeeId\":\"" + employeeId
						+ "\"}]}";

		} else if (Ptype.equals(PermissonType.SpiritOne)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药,中草药和精一，精一药品开立的前提是有以上类型的权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"256705\",\"permissionDesc\":\"第一类精神药品开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药,中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.SpiritTwo)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药,中草药和精二
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"256706\",\"permissionDesc\":\"精二类精神药品开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药,中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.NarcoticDrug)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药,中草药和麻醉
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"256707\",\"permissionDesc\":\"麻醉药品开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药,中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.HighlyToxic)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药,中草药和剧毒
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"256708\",\"permissionDesc\":\"剧毒药品开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药,中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.RadioPharmaceuticals)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和放射性药品
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"256709\",\"permissionDesc\":\"放射性药品开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.HighPriced)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和高价药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399217887\",\"permissionDesc\":\"高价药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Antibacterials_Unrestricted)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和非限制级抗菌药权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"384040\",\"permissionDesc\":\"非限制级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Antibacterials_Restricted)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和限制级抗菌药权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"384041\",\"permissionDesc\":\"限制级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Antibacterials_Special)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和特殊级抗菌药权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"384042\",\"permissionDesc\":\"特殊级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_zero)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057447\",\"permissionDesc\":\"0级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_one)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057448\",\"permissionDesc\":\"1级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_two)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057449\",\"permissionDesc\":\"2级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_three)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057450\",\"permissionDesc\":\"3级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_four)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057451\",\"permissionDesc\":\"4级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		} else if (Ptype.equals(PermissonType.Secrecy_five)) {
			if (OType.equals(OwnType.Have)) // 西药，中成药和中草药和保密0级权限
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"},{\"prescPermissionCode\":\"399057452\",\"permissionDesc\":\"5级\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
			else if (OType.equals(OwnType.No)) // 西药，中成药，中草药
				json = "{\"docPrescPermissionInputDTOS\":[{\"prescPermissionCode\":\"256702\",\"permissionDesc\":\"西药开立权限 \"},{\"prescPermissionCode\":\"256703\",\"permissionDesc\":\"中成药开立权限\"},{\"prescPermissionCode\":\"256704\",\"permissionDesc\":\"中草药开立权限\"}],\"employeeIdInputDTOs\":[{\"employeeId\":\""
						+ employeeId + "\"}]}";
		}
		HttpTestUrl httpTestUrl = new HttpTestUrl(REurl);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));
		for (int i = 0; i < 4; i++) {
			test.sendPostRequest(json, null, header);
			test.waitRequestFinish(30000);
			if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
				br.logger.boxLog(1, "成功", "1111");
				break;
			} else {
				br.logger.boxLog(1, "更新权限失败", "第" + String.valueOf(i) + "次更新医生权限出错");
				continue;
			}
		}
		br.driver.navigate().refresh();
	}

	public String GetDeferredPaymentPat(OutpatientBrowser br) {
		String sql = new HisSqlManager().ghdj(Data.newEncounterSubjectCode, null, "男");
		Map<String, String> data = br.decouple.db.queryFirstRow("HIS挂号登记", sql);
		String patid = data.get("patid").trim();
		String patientName = data.get("hzxm").trim();
		String visitNumber = data.get("ghxh").trim();
		String visitSeqNumber = data.get("ghhx").trim();
		String fhirData = br.decouple.getGHFhirData(patid, visitNumber, Data.hospital_soid);
		fhirData = fhirData.replace("\"code\": \"urn:oid:1.2.156.112604.1.2.429.1\"}]}}",
				"\"code\": \"urn:oid:1.2.156.112604.1.2.429.1\"}]}},{ \"url\":\"https://simplifier.net/winningtest/extension-payment-deferred\",\"valueBoolean\":true}");
		String url = "http://" + Data.host + Data.fhir_port + "/fhir/$process-message";// 临港有做转发
		HttpTestUrl httpTestUrl = new HttpTestUrl(url);
		HttpTest test = new HttpTest(httpTestUrl);
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		test.sendPostRequest(fhirData, header);
		test.waitRequestFinish(20000);
		br.logger.log(fhirData);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			br.logger.boxLog(1, "成功", "更新为挂账患者成功");
		} else
			br.logger.assertFalse(true, test.getResponseContent());
		return patientName;
	}

	// 更新患者的保密等级
	public void SetSecricy(OutpatientBrowser br, String EncountId, String Biz_Role_Id, PermissonType pt) {

		Cookie cookie = br.wnOutpatientWorkflow.wnwd.getCookieNamed("BEARER_TOKEN");
		String REurl = "http://" + Data.host + "/outpat-encounter/api/v1/encounter_cis/encounter_record/update";
		String OUTPATIENT_CONTACT_ID = br.decouple.db60.getCONTACTIdbyENCOUNTERId(EncountId);
		// 患者出生日期取前一年的当天日期
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String birthdate = formatter.format(calendar.getTime());
		String json = "{\"encounterId\":\"138819072493099009\",\"addrProvinceCode\":\"139628\",\"addrCityCode\":\"139629\",\"addrCountyCode\":\"139632\",\"addrTownCode\":-1,\"addrDetail\":\"万科金域国际22楼\",\"telephoneNo\":\"13888888888\",\"patientSecretLevelCode\":\"399057447\",\"birthDate\":\"2020-03-25\",\"nationalityCode\":\"101298\",\"nationCode\":\"12828\",\"religionCode\":null,\"occupationCode\":null,\"maritalStatusCode\":\"50605\",\"povertyLevelCode\":null,\"contactList\":[{\"addrProvinceCode\":-1,\"addrCityCode\":-1,\"addrCountyCode\":-1,\"addrTownCode\":-1,\"addrDetail\":null,\"contactName\":null,\"contactTelephoneNo\":null,\"outpatientContactId\":\"contactId\"}],\"personAddresses\":[{\"addressTypeCode\":\"134991\",\"addrProvinceCode\":-1,\"addrCityCode\":-1,\"addrCountyCode\":-1,\"addrTownCode\":-1,\"addrDetail\":\"\"}],\"nativeProvCode\":null,\"nativeCityCode\":null,\"bizRoleId\":\"151011748061599745\",\"savePersonalTags\":[]}";
		json = json.replace("138819072493099009", EncountId).replace("151011748061599745", Biz_Role_Id)
				.replace("contactId", OUTPATIENT_CONTACT_ID).replace("2020-03-25", birthdate)
				.replace("399057447", pt.Code);
		HttpTestUrl httpTestUrl = new HttpTestUrl(REurl);
		HttpTest test = new HttpTest(httpTestUrl);
//        test.setConnectionTimeout();
		HttpTestHeader header = new HttpTestHeader();
		header.addHeader("Version", "1.1");
		header.addHeader("Authorization", URLDecoder.decode(cookie.getValue()));

		test.sendPostRequest(json, null, header);
		test.waitRequestFinish(30000);
		if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
			br.logger.boxLog(1, "成功", "更新患者保密等级成功");
		} else {
			br.logger.boxLog(1, "更新患者保密等级失败", json);
			br.logger.boxLog(1, "cookie.getValue()的值:", cookie.getValue());
			br.logger.boxLog(1, "URLDecoder.decode(cookie.getValue())的值:", URLDecoder.decode(cookie.getValue()));
			br.logger.assertFalse(true, "报错", "请求地址: " + REurl + "\n\n\n请求状态: " + test.getResponseCode()
					+ "\n\n\n返回内容: " + test.getResponseContent());

//                continue;
		}
//        }
		br.driver.navigate().refresh();
	}

	@Test
	public void Case_01_Drug_Y() {
		boolean RES = true;
		init("Case_01_有权限的医生开立西药", true);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.NoPermissionAccount, Data.default_user_login_pwd);
		SetPermission(browser, OwnType.Have, PermissonType.Drug, wjtest_EmployeeId); // 设置权限
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
		browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
		browser.wnOutpatientWorkflow.unselectSearchStock();
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Drug);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有西药权限的医生不可以开立", RES);

	}

	@Test
	public void Case_02_Drug_N() {
//        Boolean res =prescribeOrder_t("Case_02_没有西药权限的医生开立西药", OwnType.No, Data.Med_Drug,PermissonType.Drug);
//        browser.logger.throwError(!res, "没有权限的医生不可以开立");
		init("Case_02_没有西药权限的医生开立西药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Drug, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Drug);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.Drug.WarnMsg))
				RES = true;
		}
//        browser.logger.throwError(!RES, "没有西药权限的医生开立西药没有对应控制");
		Assert.assertTrue("没有权限的医生可以开立", RES);
	}

	@Test
	public void Case_03_CPM_Y() {
//        Boolean res =prescribeOrder_t("Case_03_有权限的医生开立中成药", OwnType.Have, Data.Med_Chinesepatentmedicine,PermissonType.CPM);
//        browser.logger.throwError(!res, "有权限的医生不可以开立中成药");
		init("Case_03_有中成药权限的医生开立中成药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.CPM, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有中成药权限的医生不能开立中成药", RES);
	}

	@Test
	public void Case_04_CPM_N() {
//        Boolean res =prescribeOrder_t("Case_04_没有权限的医生开立中成药", OwnType.No, Data.Med_Chinesepatentmedicine,PermissonType.CPM);
//        browser.logger.throwError(!res, "没有权限的医生可以开立中成药");
		init("Case_04_没有权限的医生开立中成药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.CPM, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.CPM.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有中成药权限的医生可以开立中成药", RES);
	}

	@Test
	public void Case_05_Herb_Y() {
		init("Case_05_有权限的医生开立中草药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.Herb, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Herb);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有中草药权限的医生不能开立中草药", RES);
	}

	//
	@Test
	public void Case_06_Herb_N() {
		init("Case_06_没有权限的医生开立中草药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Herb, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
		} catch (Throwable e) {
//            if(e.getMessage().contains(PermissonType.Herb.WarnMsg))  --没有权限开立中草药没有提示，直接隐藏
			RES = true;
		}
		Assert.assertTrue("没有中草药权限的医生可以开立中草药", RES);
	}

	//
	@Test
	public void Case_07_SpiritOne_Y() {
		init("Case_07_有权限的医生开立精一药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.SpiritOne, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_SpiritOneMed);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立精一药", RES);
	}

	@Test
	public void Case_08_SpiritOne_N() {
		init("Case_08_没有权限的医生开立精一药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.SpiritOne, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_SpiritOneMed);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.SpiritOne.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有精一权限的医生可以开立精一药", RES);
	}

	//
	@Test
	public void Case_09_SpiritTwo_Y() {
		init("Case_09_有权限的医生开立精二药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.SpiritTwo, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_SpiritTwoMed);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立精二药", RES);
	}

	//
	@Test
	public void Case_10_SpiritTwo_N() {
		init("Case_10_没有权限的医生开立精二药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.SpiritTwo, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_SpiritTwoMed);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.SpiritTwo.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有精二权限的医生可以开立精二药", RES);
	}

	//
	@Test
	public void Case_11_NarcoticDrug_Y() {
		init("Case_11_有权限的医生开立麻醉药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.NarcoticDrug, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_NarcoticDrug);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立麻醉药", RES);
	}

	//
	@Test
	public void Case_12_NarcoticDrug_N() {
		init("Case_12_没有权限的医生开立麻醉药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.NarcoticDrug, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_NarcoticDrug);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.NarcoticDrug.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立麻醉药", RES);
	}

	//
	@Test
	public void Case_13_HighlyToxic_Y() {
		init("Case_13_有权限的医生开立剧毒药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.HighlyToxic, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_HighlyToxic);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立剧毒药", RES);
	}

	//
	@Test
	public void Case_14_HighlyToxic_N() {
		init("Case_14_没有权限的医生开立剧毒药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.HighlyToxic, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_HighlyToxic);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.HighlyToxic.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立剧毒药", RES);
	}

	@Test
	public void Case_15_RadioPharmaceuticals_Y() {
		init("Case_15_有权限的医生开立放射性药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.RadioPharmaceuticals, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_RadioPharmaceuticals);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立放射性药", RES);
	}

	//
	@Test
	public void Case_16_RadioPharmaceuticals_N() {
		init("Case_16_没有权限的医生开立放射性药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.RadioPharmaceuticals, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_RadioPharmaceuticals);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.RadioPharmaceuticals.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立放射性药", RES);
	}

	@Test
	public void Case_17_Antibacterials_Unrestricted_Y() {
		init("Case_17_有权限的医生开立非限制级抗菌药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.Antibacterials_Unrestricted, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Unrestricted);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立非限制级抗菌药", RES);
	}

	@Test
	public void Case_18_Antibacterials_Unrestricted_N() {
		init("Case_18_没有权限的医生开立非限制级抗菌药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Antibacterials_Unrestricted, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Unrestricted);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.Antibacterials_Unrestricted.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立非限制级抗菌药", RES);
	}

	@Test
	public void Case_19_Antibacterials_Restricted_Y() {
		init("Case_19_有权限的医生开立限制级抗菌药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.Antibacterials_Restricted, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Restricted);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立限制级抗菌药", RES);
	}

	@Test
	public void Case_20_Antibacterials_Restricted_N() {
		init("Case_20_没有权限的医生开立限制级抗菌药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Antibacterials_Restricted, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Restricted);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.Antibacterials_Restricted.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立限制级抗菌药", RES);
	}

	@Test
	public void Case_21_Antibacterials_Special_Y() {
		init("Case_21_有权限的医生开立特殊级抗菌药", false);
		boolean RES = true;
		SetPermission(browser, OwnType.Have, PermissonType.Antibacterials_Special, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Special);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立特殊级抗菌药", RES);
	}

	@Test
	public void Case_22_Antibacterials_Special_N() {
		init("Case_22_没有权限的医生开立特殊级抗菌药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Antibacterials_Special, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Antibacterials_Special);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.Antibacterials_Special.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立特殊级抗菌药", RES);
	}

	@Test
	public void Case_27_HighPriced_Y() {
		init("Case_27_有权限的医生开立高价药", false);

		boolean RES = true;
		Data.ybdm = "2000";
		SetPermission(browser, OwnType.Have, PermissonType.HighPriced, wjtest_EmployeeId);
		try {
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
			browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_HighPriced);
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有权限的医生不能开立高价药", RES);
	}

	@Test
	public void Case_28_HighPriced_N() {
		Data.ybdm = "2000";
		init("Case_28_没有权限的医生开立高价药", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.HighPriced, wjtest_EmployeeId);
		try {
			browser.wnOutpatientWorkflow.unselectSearchStock();
			browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_HighPriced);
		} catch (Throwable e) {
			if (e.getMessage().contains(PermissonType.HighPriced.WarnMsg))
				RES = true;
		}
		Assert.assertTrue("没有权限的医生可以开立高价药", RES);
	}

	@Test
	public void Case_29_SecricyZero_Y() {
//        br.quit();
		init("Case_29_有保密0级的医生接诊保密0级患者", false);
		boolean RES = true;
//        br.driver.navigate().refresh();
//        br=browser;
//        br.wnwd.openUrl(Data.web_url);
//        br.helper.wnlogin(Data.NoPermissionAccount, Data.default_user_login_pwd);
//        br.helper.loginOutPatientNew(Data.test_select_subject);
//        br.helper.skip();
//        cookie = br.helper.driver.manage().getCookieNamed("BEARER_TOKEN");
		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_zero, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_zero); // 更新患者保密等级,需要先叫号再调用
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密0级的医生无法接诊保密0级患者", RES);
	}

	@Test
	public void Case_30_SecricyZero_N() {
//        br.quit();
		init("Case_30_没有保密0级的医生接诊保密0级患者", false);
		boolean RES = false;
//        br=browser;
//        br.wnwd.openUrl(Data.web_url);
//        br.helper.wnlogin(Data.NoPermissionAccount, Data.default_user_login_pwd);
//        br.helper.loginOutPatientNew(Data.test_select_subject);
//        br.helper.skip();
//        cookie = br.helper.driver.manage().getCookieNamed("BEARER_TOKEN");
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_zero, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_zero); // 更新患者保密等级
		try {

			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密0级的医生接诊保密0级患者", RES);
	}

	@Test
	public void Case_31_SecricyOne_Y() {
//        br.quit();
		init("Case_31_有保密1级的医生接诊保密1级患者", false);
		boolean RES = true;
//        br.driver.navigate().refresh();
//        br=browser;
//        br.wnwd.openUrl(Data.web_url);
//        br.helper.wnlogin(Data.NoPermissionAccount, Data.default_user_login_pwd);
//        br.helper.loginOutPatientNew(Data.test_select_subject);
//        br.helper.skip();
//        cookie = br.helper.driver.manage().getCookieNamed("BEARER_TOKEN");
		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_one, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_one); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密1级的医生无法接诊保密1级患者", RES);
	}

	@Test
	public void Case_32_SecricyOne_N() {

		init("Case_32_没有保密1级的医生接诊保密1级患者", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_one, wjtest_EmployeeId); // 设置权限
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_one); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密0级的医生接诊保密0级患者", RES);
	}

	@Test
	public void Case_33_SecricyTwo_Y() {
//        br.quit();
		init("Case_33_有保密2级的医生接诊保密2级患者", false);
		boolean RES = true;
//        br.driver.navigate().refresh();
//        br=browser;
//        br.wnwd.openUrl(Data.web_url);
//        br.helper.wnlogin(Data.NoPermissionAccount, Data.default_user_login_pwd);
//        br.helper.loginOutPatientNew(Data.test_select_subject);
//        br.helper.skip();
//        cookie = br.helper.driver.manage().getCookieNamed("BEARER_TOKEN");
		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_two, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_two); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密2级的医生无法接诊保密2级患者", RES);
	}

	@Test
	public void Case_34_SecricyTwo_N() {

		init("Case_34_没有保密2级的医生接诊保密2级患者", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_two, wjtest_EmployeeId); // 设置权限
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_two); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密0级的医生接诊保密0级患者", RES);
	}

	@Test
	public void Case_35_SecricyThree_Y() {

		init("Case_35_有保密3级的医生接诊保密3级患者", false);
		boolean RES = true;

		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_three, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_three); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密3级的医生无法接诊保密3级患者", RES);
	}

	@Test
	public void Case_36_SecricyThree_N() {

		init("Case_36_没有保密3级的医生接诊保密3级患者", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_three, wjtest_EmployeeId); // 设置权限
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_three); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密3级的医生接诊保密3级患者", RES);
	}

	@Test
	public void Case_37_SecricyFour_Y() {

		init("Case_37_有保密4级的医生接诊保密4级患者", false);
		boolean RES = true;

		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_four, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_four); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密4级的医生无法接诊保密4级患者", RES);
	}

	@Test
	public void Case_38_SecricyFour_N() {

		init("Case_37_没有保密4级的医生接诊保密4级患者", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_four, wjtest_EmployeeId); // 设置权限
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_four); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密4级的医生接诊保密4级患者", RES);
	}

	@Test
	public void Case_39_SecricyFive_Y() {

		init("Case_39_有保密5级的医生接诊保密5级患者", false);
		boolean RES = true;

		SetPermission(browser, OwnType.Have, PermissonType.Secrecy_five, wjtest_EmployeeId); // 设置权限

		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_five); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = false;
		}
		Assert.assertTrue("有保密4级的医生无法接诊保密4级患者", RES);
	}

	@Test
	public void Case_40_SecricyFive_N() {

		init("Case_40_没有保密5级的医生接诊保密5级患者", false);
		boolean RES = false;
		SetPermission(browser, OwnType.No, PermissonType.Secrecy_five, wjtest_EmployeeId); // 设置权限
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		String full_name = encounterInfo.get(0);
		Map<String, String> Info = browser.decouple.db60.getEnCounterIdbyFullName(full_name);
		String EncounterID = Info.get("ENCOUNTER_ID");
		String BIZ_ROLE_ID = Info.get("BIZ_ROLE_ID");
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		SetSecricy(browser, EncounterID, BIZ_ROLE_ID, PermissonType.Secrecy_five); // 更新患者保密等级
		try {
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		} catch (Throwable e) {
			RES = true;
		}
		Assert.assertTrue("没有保密4级的医生接诊保密4级患者", RES);
	}
}
