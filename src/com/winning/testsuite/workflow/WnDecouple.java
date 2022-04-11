package com.winning.testsuite.workflow;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.winning.manager.HisSqlManager;
import com.winning.manager.WnSqlManager;

import ui.sdk.config.Data;
import ui.sdk.util.Encounter;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;
import ui.sdk.util.SdkTools;
import ui.sdk.util.SdkTools.IdCardGenerator;
import xunleiHttpTest.HttpTest;
import xunleiHttpTest.HttpTestHeader;
import xunleiHttpTest.HttpTestUrl;


/**
 * 5.x接口相关接口
 *
 * @author Lenovo
 */
public class WnDecouple {

    public SdkDatabaseConn db = null;//his的db对象
    public Wn60Db db60 = null;//60的db对象
    public Log logger = null;
//    public WnOutpatientWorkflow wnwd = null;

    public static String userDir = System.getProperty("user.dir");
    public static String fileSeparator = File.separator;

    // 直接使用Data变量做配置
    public WnDecouple(Log l) {
        this.logger = l;
        db = new SdkDatabaseConn(Data.hisDbType, Data.hisHost, Data.hisInstance, Data.hisDbname, Data.hisDbService, Data.hisUsername, Data.hisPassword, l);
        db60 = new Wn60Db(l);
    }

    // 自定义配置
    public WnDecouple(String host, String instanceName, String dbName, String username, String password, Log l) {
        this.logger = l;
        db = new SdkDatabaseConn("sqlserver", host, instanceName, dbName, instanceName, username, password, l);
        db60 = new Wn60Db(l);
    }


    /**
     * 执行存储过程，获取数据项
     *
     * @param sql  存储过程语句，如exec dbo.GET_MYD_BQBMK
     * @param item 数据项
     * @return
     */
    public ArrayList<String> excuteExec(String sql, String item) {
        return db.query(sql, item);
    }

    /**
     * 山西卫宁挂号同步，通过Fhir进行挂号
     *
     * @param syncHost ip，如172.16.6.99
     * @param port     ias-fhir模块端口号，如50100
     */
    public ArrayList<String> encounterSyncForHisSXWN(String syncHost, String port) {
        Map<String, String> data = db.queryFirstRow("新患者登记挂号", new HisSqlManager().ghdj(null, Data.sxwn_doctorid, null));
        if (data == null || data.size() < 3) {
            throw new Error("挂号失败");
        }
        String patid = data.get("patid").trim();
        String patientName = data.get("hzxm").trim();
        String visitNumber = data.get("ghxh").trim();//#挂号唯一标识患者
        String visitSeqNumber = data.get("ghhx").trim();
        String cardNo = data.get("cardno").trim();
        //cardNo为空
        if (cardNo.equals("")) {
            cardNo = "12345678";
        }
        String ksdm = data.get("ksdm").trim();
        String ksdmExpand = ksdm + "-" + db60.getEmployNoByUsercode(Data.default_user_login_account).get("EMPLOYEE_NO");//山西卫宁挂号科室，需要用这个
        String ksmc = data.get("ksmc").trim();
        String date = data.get("ghrq").trim().substring(0, 10);
        //String ssn = "43018119901011234";
        String sex = data.get("sex").trim();
        if (sex.equals("")) {
            sex = "urn:oid:1.2.156.112604.1.2.5.2";
        }
        else if (sex.equals("男")) {
            sex = "urn:oid:1.2.156.112604.1.2.5.2";
        }
        else if (sex.equals("女")) {
            sex = "urn:oid:1.2.156.112604.1.2.5.3";
        }
        logger.log("patid:" + patid + ",patientName:" + patientName + ",visitNumber:" + visitNumber + ",visitSeqNumber:" + visitSeqNumber +
                ",ksdm:" + ksdm + ",ksmc:" + ksmc + ",date:" + date);

        String url = "http://" + syncHost + port + "/fhir/$process-message";
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
                "        \"eventCoding\": {\n" +
                "          \"system\": \"http://www.winning.com.cn/fhir/message-events\",\n" +
                "          \"code\": \"OutpatientRegistered\",\n" +
                "          \"display\": \"挂号通知\"\n" +
                "        },\n" +
                "        \"destination\": [\n" +
                "          {\n" +
                "            \"name\": \"CIS6.0\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"source\": {\n" +
                "          \"name\": \"HIS\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Slot\",\n" +
                "        \"id\": \"98626b28-0e94-42df-a92b-3d2bb4c72d83\",\n" +
                "        \"extension\": [\n" +
                "          {\n" +
                "            \"url\": \"https://simplifier.net/winningtest/extension-chinese-day-period\",\n" +
                "            \"valueCode\": \"urn:oid:1.2.156.112604.1.2.1157.4\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"start\": \"" + date + "T08:10:48.000+08:00\",\n" +
                "        \"end\": \"" + date + "T08:16:12.000+08:00\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Appointment\",\n" +
                "        \"id\": \"93039f5b-1163-451a-a73e-946c43ab9522\",\n" +
                "        \"identifier\": [\n" +
                "          {\n" +
                "            \"system\": \"urn:oid:1.2.156.112604.1.1.3630\",\n" +
                "            \"value\": \"" + visitSeqNumber + "\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"system\": \"urn:oid:1.2.156.112604.1.1.3765\",\n" +
                "            \"value\": \"" + visitNumber + "\" \n" +
                "          }\n" +
                "        ],\n" +
                "        \"status\": \"urn:oid:1.2.156.112604.1.2.1026.3\",\n" +
                "        \"serviceCategory\": [\n" +
                "          {\n" +
                "            \"coding\": [\n" +
                "              {\n" +
                "                \"code\": \"urn:oid:1.2.156.112604.1.2.999.1\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "        \"appointmentType\": {\n" +
                "          \"coding\": [\n" +
                "            {\n" +
                "              \"code\": \"urn:oid:1.2.156.112604.1.2.1725.1\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"slot\": [\n" +
                "          {\n" +
                "            \"identifier\": {\n" +
                "              \"value\": \"1752318\"\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"reference\": \"98626b28-0e94-42df-a92b-3d2bb4c72d83\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"created\": \"" + date + "T15:08:18+08:00\",\n" +
                "        \"participant\": [\n" +
                "          {\n" +
                "            \"actor\": {\n" +
                "              \"identifier\": {\n" +
                "                \"value\": \"" + ksdmExpand + "\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"actor\": {\n" +
                "              \"identifier\": {\n" +
                "                \"value\": \"0\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Coverage\",\n" +
                "        \"id\": \"ac61558a-0cc2-414f-853f-64596e389252\",\n" +
                "        \"type\": {\n" +
                "          \"coding\": [\n" +
                "            {\n" +
                "              \"code\": \"" + Data.ybdm +  "\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Account\",\n" +
                "        \"id\": \"d6a6e02b-7892-437e-8a4d-3ba610dadfb2\",\n" +
                "        \"coverage\": [\n" +
                "          {\n" +
                "            \"coverage\": {\n" +
                "              \"reference\": \"Coverage/ac61558a-0cc2-414f-853f-64596e389252\"\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Encounter\",\n" +
                "        \"extension\": [\n" +
                "          {\n" +
                "            \"url\": \"https://simplifier.net/winningtest/extension-encounter-visitType\",\n" +
                "            \"valueCodeableConcept\": {\n" +
                "              \"coding\": [\n" +
                "                {\n" +
                "                  \"code\": \"urn:oid:1.2.156.112604.1.2.429.1\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"class\": {\n" +
                "          \"code\": \"urn:oid:1.2.156.112604.1.2.433.1\"\n" +
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
                "              \"identifier\": {\n" +
                "                \"value\": \"5459\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": [\n" +
                "              {\n" +
                "                \"coding\": [\n" +
                "                  {\n" +
                "                    \"system\": \"http://hl7.org/fhir/ValueSet/encounter-participant-type\",\n" +
                "                    \"code\": \"ADM\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"individual\": {\n" +
                "              \"identifier\": {\n" +
                "                \"value\": \"-1\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"account\": [\n" +
                "          {\n" +
                "            \"reference\": \"Account/d6a6e02b-7892-437e-8a4d-3ba610dadfb2\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"serviceProvider\": {\n" +
                "          \"identifier\": {\n" +
                "            \"value\": \"" + ksdm + "\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"resource\": {\n" +
                "        \"resourceType\": \"Patient\",\n" +
                "        \"identifier\": [\n" +
                "          {\n" +
                "            \"system\": \"urn:oid:1.2.156.112604.1.1.271\",\n" +
                "            \"value\": \"" + cardNo + "\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"system\": \"urn:oid:1.2.156.112604.1.1.78\",\n" +
                "            \"value\": \"110101199003070257\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"system\": \"urn:oid:1.2.156.112604.1.1.3215\",\n" +
                "            \"value\": \"" + cardNo + "\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"name\": [\n" +
                "          {\n" +
                "            \"extension\": [\n" +
                "              {\n" +
                "                \"url\": \"https://simplifier.net/winningtest/extension-humanname-inputCode\",\n" +
                "                \"extension\": [\n" +
                "                  {\n" +
                "                    \"url\": \"pinyin\",\n" +
                "                    \"valueString\": \"patientName\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"url\": \"wubi\",\n" +
                "                    \"valueString\": \"TT\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"text\": \"" + patientName + "\",\n" +
                "            \"family\": \"莫\",\n" +
                "            \"given\": [\n" +
                "              \"妮卡\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ],\n" +
                "		\"telecom\": [{" +
                "           \"id\": \"30dd7eab-2ba1-4bd8-869c-1dac4363369f\"," +
                "           \"system\": \"urn:oid:1.2.156.112604.1.2.678.1\"," +
                "           \"value\": \"13902931087\"," +
                "           \"use\": \"home\"" +
                "		}],\n" +
                "        \"gender\": \"" + sex + "\",\n" +
                "        \"birthDate\": \"1990-03-07\",\n" +
                "        \"_birthDate\": {\n" +
                "          \"extension\": [\n" +
                "            {\n" +
                "              \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n" +
                "              \"valueTime\": \"00:00:00\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"address\": [\n" +
                "          {\"text\":\"上海市卫宁健康\"}\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        logger.log(url);
        logger.log("content:" + test.getResponseContent());
        ArrayList<String> syncInfo = new ArrayList<>();
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            syncInfo.add(patientName);//患者姓名，用于挂号检索
            syncInfo.add(visitNumber);//用于收费
            syncInfo.add(patid);
            syncInfo.add(visitSeqNumber);
            logger.log("挂号成功");

        }
        else {
            logger.assertFalse(true, "挂号失败");
        }
        return syncInfo;
    }

    /**
     * 集团卫宁挂号
     *
     * @param syncHost
     * @param hospital_soid
     * @param patid         (非必填)
     * @param subjectCode   (非必填)
     * @return
     */
//	public ArrayList<String> encounterSyncForHisJTWN(String syncHost, String hospital_soid){
//		return encounterSyncForHisJTWN(syncHost,hospital_soid,null,null);
//	}
    public ArrayList<String> encounterSyncForHisJTWN(String syncHost, String hospital_soid, String patid, String subjectCode, String sex) {
        String sql = "";
        if (sex == null) {
            int r = new Random().nextInt(2);
            sex = "男女".substring(r, r + 1);
        }
        if (subjectCode == null) {
            subjectCode = Data.newEncounterSubjectCode;
        }
        if (patid == null) {
            sql = new HisSqlManager().ghdj(subjectCode, null, sex);
        }
        else {
            sql = new HisSqlManager().ghdjWithPatid(subjectCode, patid);
        }
        Map<String, String> data = db.queryFirstRow("HIS挂号登记", sql);
        if (data == null || !data.containsKey("hzxm") || !data.containsKey("patid") || !data.containsKey("ghxh") || !data.containsKey("ghhx")) {
            logger.assertFalse(true, "执行语句(" + sql + ")异常:" + data);
        }
        patid = data.get("patid").trim();
        String patientName = data.get("hzxm").trim();
        String visitNumber = data.get("ghxh").trim();
        String visitSeqNumber = data.get("ghhx").trim();
        String fhirData = getGHFhirData(patid, visitNumber, hospital_soid);
        String url = "http://" + syncHost + Data.fhir_port + "/fhir/$process-message";//临港有做转发
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Version", "1.1");
        test.sendPostRequest(fhirData, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            ArrayList<String> syncInfo = new ArrayList<>();
            syncInfo.add(patientName);//患者姓名，用于挂号检索
            syncInfo.add(visitNumber);//用于收费
            syncInfo.add(patid);
            syncInfo.add(visitSeqNumber);
            logger.boxLog(1, "挂号成功", "患者姓名: " + patientName + "\n挂号序号: " + visitNumber + "\npatid: " + patid + "\n挂号号序: " + visitSeqNumber);
            return syncInfo;
        }
        else {
            logger.assertFalse(true, "Fhir同步报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
        }
        return null;
    }

    /**
     * 浙江卫宁挂号并同步
     *
     * @param syncHost
     * @param subjectCode
     * @return
     */
    public ArrayList<String> encounterSyncForHisZJWN(String syncHost, String subjectCode) {
        if (subjectCode == null) {
            subjectCode = Data.newEncounterSubjectCode;
        }
        String sql = new HisSqlManager().ghdj(subjectCode, null, null);
        Map<String, String> data = db.queryFirstRow("HIS挂号登记", sql);
        if (data == null || !data.containsKey("hzxm") || !data.containsKey("patid") || !data.containsKey("ghxh") || !data.containsKey("ghhx")) {
            logger.assertFalse(true, "执行语句(" + sql + ")异常:" + data);
        }
        String patid = data.get("patid").trim();
        String patientName = data.get("hzxm").trim();
        String visitNumber = data.get("ghxh").trim();
        String visitSeqNumber = data.get("ghhx").trim();
        String fhirData = "{\"event\": \"OutpatientRegistered\",\"accetp\": \"CIS6.0\",\"hosid \": \"01\",\"transid\": \"A183E703-74DA-41DB-BCF2-BFB5EB910702\",\"datalist\": [{\"datasource\": \"\",\"sourcename\": \"\",\"item\": [{\"keyname\": \"zyh\",\"keyvalue\": \"" + patid + "\",\"context\": \"门诊号\"},{\"keyname\": \"bazdkid\",\"keyvalue\": \"" + visitNumber + "\",\"context\": \"挂号序号\"}]}]}";
        String url = syncHost + "/HisFhirServer/OutPatient/OutpatientRegistered"; //调用his同步服务
        HttpTestUrl httpTestUrl = new HttpTestUrl(url);
        HttpTest test = new HttpTest(httpTestUrl);
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Version", "1.1");
        test.sendPostRequest(fhirData, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"code\":\"1\"")) {
            ArrayList<String> syncInfo = new ArrayList<>();
            syncInfo.add(patientName);//患者姓名，用于挂号检索
            syncInfo.add(visitNumber);//用于收费
            syncInfo.add(patid);
            syncInfo.add(visitSeqNumber);
            logger.boxLog(1, "挂号成功", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n请求参数:" + fhirData + "\n\n\n返回内容: " + test.getResponseContent());
            logger.boxLog(1, "挂号成功", "患者姓名: " + patientName + "\n挂号序号: " + visitNumber + "\npatid: " + patid + "\n挂号号序: " + visitSeqNumber);
            return syncInfo;
        }
        else {
            logger.assertFalse(true, "Fhir同步报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
        }
        return null;
    }

    /**
     * 其他HIS挂号（同济现场）
     */
    public ArrayList<String> encounterSyncForOtherHis(String syncHost, String subjectCode) {
        ArrayList<String> syncInfo = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String patientName = "AUTOTEST" + dateFormat;

        HttpTestUrl httpTestUrl = new HttpTestUrl(syncHost + "/webservice.asmx");
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Content-Type", "text/xml;charset=utf-8");
        String request =  "{\"departmentName\":\""+Data.test_select_subject+"\",\"clinicTimeQuantum\":\"10:30-11:00\",\"tradeNo\":\"" + dateFormat + "\",\"idCard\":\"320981198810252217\",\"departmentCode\":\"" + subjectCode + "\",\"clinicFee\":\"25.00\",\"realName\":\"" + patientName + "\"," +
                "\"doctorName\":\"\"," +
                "\"payType\":\"wechatNo\",\"phone\":\"18914987520\"," +
                "\"doctorCode\":\"\",\"clinicDate\":\"" + formatter + "\",\"patientCard\":\"136401001763069\",\"scheduleId\":\"20210910|上午|10:30-11:00|皮肤科|550|5500102021090911030\"}";
        HttpTest test = new HttpTest(httpTestUrl);
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        if (test.getResponseCode() == 200) {
            logger.boxLog(1, "成功", "挂号成功");
            syncInfo.add(patientName);
            syncInfo.add("-1");
            syncInfo.add("-1");
            syncInfo.add("-1");
            logger.boxLog(1, "挂号成功",(List) syncInfo);
            return syncInfo;
        } else {
            logger.assertFalse(true, "报错", "请求地址: " + httpTestUrl + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
        }return null;
    }

        /**
         * 天津卫宁挂号并同步
         *
         * @param syncHost
         * @param subjectCode
         * @return
         */
    public ArrayList<String> encounterSyncForHisTJWN(String syncHost, String cardNo, String subjectCode, String doctorCode) {
        ArrayList<String> syncInfo = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date endDate = new Date(System.currentTimeMillis());
        String dateString = formatter.format(endDate);
        String patientName = "TEST" + cardNo;

        HttpTestUrl httpTestUrl = new HttpTestUrl(syncHost + "/Service1.asmx");
        HttpTestHeader header = new HttpTestHeader();
        header.addHeader("Content-Type", "text/xml;charset=utf-8");

        //登记患者 - P001
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                "  <soap:Body>\r\n" +
                "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                "      <strMethodName>P001</strMethodName>\r\n" +
                "      <InXml>&lt;Request&gt;\r\n" +
                "&lt;TradeCode&gt;P001&lt;/TradeCode&gt;\r\n" +
                "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                "&lt;CardNo&gt;" + cardNo + "&lt;/CardNo&gt;\r\n" +
                "&lt;CardType&gt;3&lt;/CardType&gt;\r\n" +
                "&lt;PatientName&gt;" + patientName + "&lt;/PatientName&gt;\r\n" +
                "&lt;Birthday&gt;1988-10-22&lt;/Birthday&gt;\r\n" +
                "&lt;Sex&gt;1&lt;/Sex&gt;\r\n" +
                "&lt;Address&gt;天津市和平区&lt;/Address&gt;\r\n" +
                "&lt;OperType&gt;2&lt;/OperType&gt;\r\n" +
                "&lt;/Request&gt;&apos;</InXml>\r\n" +
                "    </CallWebMethod>\r\n" +
                "  </soap:Body>\r\n" +
                "</soap:Envelope>";

        HttpTest test = new HttpTest(httpTestUrl);
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        logger.boxLog(1, "P001 - 登记患者信息",
                "\nheader:\n" + header.map +
                        "\nbody:\n" + request +
                        "\nstateCode:\n" + test.getResponseCode() +
                        "\ncontent:\n" + test.getResponseContent());
        logger.assertFalse(!test.getResponseContent().contains("交易成功"), "登记患者失败(P001)");
        String patId = SdkTools.findMatchList(test.getResponseContent(), "&lt;HISPatientID&gt;(.*?)&lt;/HISPatientID&gt;", 1).get(0);
        cardNo = SdkTools.findMatchList(test.getResponseContent(), "&lt;IDCardNo&gt;(.*?)&lt;/IDCardNo&gt;", 1).get(0);
        patientName = SdkTools.findMatchList(test.getResponseContent(), "&lt;PatientName&gt;(.*?)&lt;/PatientName&gt;", 1).get(0);

        //挂号锁号 - B102
        request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                "  <soap:Body>\r\n" +
                "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                "      <strMethodName>B102</strMethodName>\r\n" +
                "      <InXml>&lt;Request&gt;\r\n" +
                "&lt;TradeCode&gt;B102&lt;/TradeCode&gt;\r\n" +
                "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                "&lt;HISPatientID&gt;" + patId + "&lt;/HISPatientID&gt;\r\n" +
                "&lt;VisitCode&gt;&lt;/VisitCode&gt;\r\n" +
                "&lt;DeptCode&gt;" + subjectCode + "&lt;/DeptCode&gt;\r\n" +
                "&lt;DoctorCode&gt;" + doctorCode + "&lt;/DoctorCode&gt;\r\n" +
                "&lt;APW &gt;Q&lt;/APW&gt;\r\n" +
                "&lt;VisitDate&gt;" + dateString + "&lt;/VisitDate&gt;\r\n" +
                "&lt;BusinessFrom&gt;&lt;/BusinessFrom&gt;\r\n" +
                "&lt;LockType&gt;0&lt;/LockType&gt;\r\n" +
                "&lt;DivisionID&gt;&lt;/DivisionID&gt;\r\n" +
                "&lt;/Request&gt;</InXml>\r\n" +
                "    </CallWebMethod>\r\n" +
                "  </soap:Body>\r\n" +
                "</soap:Envelope>";
        test = new HttpTest(httpTestUrl);
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        logger.boxLog(1, "B102 - 挂号锁号",
                "\nheader:\n" + header.map +
                        "\nbody:\n" + request +
                        "\nstateCode:\n" + test.getResponseCode() +
                        "\ncontent:\n" + test.getResponseContent());
        logger.assertFalse(!test.getResponseContent().contains("交易成功"), "挂号锁号失败(B102)");

        //挂号划价 - B103
        request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                "  <soap:Body>\r\n" +
                "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                "      <strMethodName>B103</strMethodName>\r\n" +
                "      <InXml>&lt;Request&gt;\r\n" +
                "&lt;TradeCode&gt;B103&lt;/TradeCode&gt;\r\n" +
                "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                "&lt;HISPatientID&gt;" + patId + "&lt;/HISPatientID&gt;\r\n" +
                "&lt;MedCardNo&gt;&lt;/MedCardNo&gt;\r\n" +
                "&lt;MedCardPwd&gt;&lt;/MedCardPwd&gt;\r\n" +
                "&lt;IDCardNo&gt;" + cardNo + "&lt;/IDCardNo&gt;\r\n" +
                "&lt;GIDCardNo&gt;&lt;/GIDCardNo&gt;\r\n" +
                "&lt;InsureTypeCode&gt;0&lt;/InsureTypeCode&gt;\r\n" +
                "&lt;MtTypeCode&gt;&lt;/MtTypeCode&gt;\r\n" +
                "&lt;RegFee&gt;0.00&lt;/RegFee&gt;\r\n" +
                "&lt;InspectFee&gt;0.00&lt;/InspectFee&gt;\r\n" +
                "&lt;TotalFee&gt;0.00&lt;/TotalFee&gt;\r\n" +
                "&lt;VisitCode&gt;&lt;/VisitCode&gt;\r\n" +
                "&lt;DeptCode&gt;" + subjectCode + "&lt;/DeptCode&gt;\r\n" +
                "&lt;DoctorCode&gt;" + doctorCode + "&lt;/DoctorCode&gt;\r\n" +
                "&lt;VisitDate&gt;" + dateString + "&lt;/VisitDate&gt;\r\n" +
                "&lt;APW&gt;Q&lt;/APW&gt;\r\n" +
                "&lt;VisitTypeCode&gt;1&lt;/VisitTypeCode&gt;\r\n" +
                "&lt;AppointCode&gt;&lt;/AppointCode&gt;\r\n" +
                "&lt;PatientName&gt;" + patientName + "&lt;/PatientName&gt;\r\n" +
                "&lt;PatientAge&gt;32&lt;/PatientAge&gt;\r\n" +
                "&lt;PatientSex&gt;1&lt;/PatientSex&gt;\r\n" +
                "&lt;BusinessFrom&gt;1&lt;/BusinessFrom&gt;\r\n" +
                "&lt;DivisionID&gt;&lt;/DivisionID&gt;\r\n" +
                "&lt;TerminalCode&gt;&lt;/TerminalCode&gt;\r\n" +
                "&lt;MachineIP&gt;&lt;/MachineIP&gt;\r\n" +
                "&lt;/Request&gt;</InXml>\r\n" +
                "    </CallWebMethod>\r\n" +
                "  </soap:Body>\r\n" +
                "</soap:Envelope>";
        test = new HttpTest(httpTestUrl);
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        logger.boxLog(1, "B103 - 挂号划价",
                "\nheader:\n" + header.map +
                        "\nbody:\n" + request +
                        "\nstateCode:\n" + test.getResponseCode() +
                        "\ncontent:\n" + test.getResponseContent());
        logger.assertFalse(!test.getResponseContent().contains("交易成功"), "挂号划价失败(B103)");
        String CashPay = SdkTools.findMatchList(test.getResponseContent(), "&lt;CashPay&gt;(.*?)&lt;/CashPay&gt;", 1).get(0);


        //挂号确认 - B104
        request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                "  <soap:Body>\r\n" +
                "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                "      <strMethodName>B104</strMethodName>\r\n" +
                "      <InXml>&lt;Request&gt;\r\n" +
                "&lt;TradeCode&gt;B104&lt;/TradeCode&gt;\r\n" +
                "&lt;TransNO&gt;20201104112112044327&lt;/TransNO&gt;\r\n" +
                "&lt;HISPatientID&gt;" + patId + "&lt;/HISPatientID&gt;\r\n" +
                "&lt;MedCardNo&gt;&lt;/MedCardNo&gt;\r\n" +
                "&lt;MedCardPwd&gt;123&lt;/MedCardPwd&gt;\r\n" +
                "&lt;IDCardNo&gt;&lt;/IDCardNo&gt;\r\n" +
                "&lt;GIDCardNo&gt;&lt;/GIDCardNo&gt;\r\n" +
                "&lt;InsureTypeCode&gt;0&lt;/InsureTypeCode&gt;\r\n" +
                "&lt;VisitCode&gt;&lt;/VisitCode&gt;\r\n" +
                "&lt;DivisionID&gt;&lt;/DivisionID&gt;\r\n" +
                "&lt;MtTypeCode&gt;&lt;/MtTypeCode&gt;\r\n" +
                "&lt;DeptCode&gt;" + subjectCode + "&lt;/DeptCode&gt;\r\n" +
                "&lt;DoctorCode&gt;" + doctorCode + "&lt;/DoctorCode&gt;\r\n" +
                "&lt;VisitDate&gt;" + dateString + "&lt;/VisitDate&gt;\r\n" +
                "&lt;APW&gt;Q&lt;/APW&gt;\r\n" +
                "&lt;VisitTypeCode&gt;1&lt;/VisitTypeCode&gt;\r\n" +
                "&lt;BusinessFrom&gt;&lt;/BusinessFrom&gt;\r\n" +
                "&lt;PatientName&gt;" + patientName + "&lt;/PatientName&gt;\r\n" +
                "&lt;PatientSex&gt;1&lt;/PatientSex&gt;\r\n" +
                "&lt;PatientAge&gt;30&lt;/PatientAge&gt;\r\n" +
                "&lt;PhoneNo&gt;&lt;/PhoneNo&gt;\r\n" +
                "&lt;PaymentType&gt;1&lt;/PaymentType&gt;\r\n" +
                "&lt;PaymentBank&gt;&lt;/PaymentBank&gt;\r\n" +
                "&lt;CashPay&gt;" + CashPay + "&lt;/CashPay&gt;\r\n" +
                "&lt;AppointCode&gt;&lt;/AppointCode&gt;\r\n" +
                "&lt;OrderNo&gt;20201104112112044327&lt;/OrderNo&gt;\r\n" +
                "&lt;/Request&gt;</InXml>\r\n" +
                "    </CallWebMethod>\r\n" +
                "  </soap:Body>\r\n" +
                "</soap:Envelope>";
        test = new HttpTest(httpTestUrl);
        test.sendPostRequest(request, header);
        test.waitRequestFinish(30000);
        logger.boxLog(1, "B104 - 挂号确认",
                "\nheader:\n" + header.map +
                        "\nbody:\n" + request +
                        "\nstateCode:\n" + test.getResponseCode() +
                        "\ncontent:\n" + test.getResponseContent());
        logger.assertFalse(!test.getResponseContent().contains("交易成功"), "挂号确认失败(B104)");
        String visitNumber = SdkTools.findMatchList(test.getResponseContent(), "&lt;RegCode&gt;(.*?)&lt;/RegCode&gt;", 1).get(0);
        String visitSeqNumber = SdkTools.findMatchList(test.getResponseContent(), "&lt;QueueNo&gt;(.*?)&lt;/QueueNo&gt;", 1).get(0);
        syncInfo.add(patientName);
        syncInfo.add(visitNumber);
        syncInfo.add(patId);
        syncInfo.add(visitSeqNumber);
        logger.boxLog(1, "挂号成功", (List) syncInfo);
        return syncInfo;
    }

    /**
     * 通过存储过程获取FHIR报文
     *
     * @param patid
     * @param ghxh
     * @param hospital_soid
     * @return
     */
    public String getGHFhirData(String patid, String ghxh, String hospital_soid) {
        Map<String, String> data = db.queryFirstRow("获取同步挂号信息接口入参", new HisSqlManager().getFhirGhdjData(patid, ghxh, hospital_soid));
        String dataString = data.toString().substring(2, data.toString().length() - 1);
        if (dataString.length() < 10) {
            throw new Error(new HisSqlManager().getFhirGhdjData(patid, ghxh, hospital_soid) + "返回值异常:" + dataString);
        }
        return dataString;
    }

    /**
     * 6.0处方签署后，调用该接口进行收费
     *
     * @param patientName 患者姓名
     * @return 返回列表，参数依次是 总金额/药品名称/厂家名称/药品规格/处方天数/idm/药品数量/频次代码/用法id，注意厂家名称是默认的default，不是真实的厂家名称
     */
    public List<String> win60MedicineSF(String patientName) {
        try {
        	List<String> info = null;
            // 等待医嘱落库
            List<String> cisxhList = waitSignOffSync(patientName);
            if(Data.hisType.equals("WINEX")) {
                String sql = "select ENCOUNTER_ID,PERSON_ID from WINDBA.OUTPATIENT_ENCOUNTER where ENCOUNTER_ID=(SELECT top 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' order by CREATED_AT desc) and IS_DEL=0";
                List<Map<String, String>> list = db60.db.queryAllRow("获取患者信息", sql);
                if (list.size() == 0) {
                    throw new Error("60库中查找不到ENCOUNTER_ID:" + sql);
                }
                String encounterId = list.get(0).get("ENCOUNTER_ID");
                String personId = list.get(0).get("PERSON_ID");
            	String hisCost = getCostWINEX(Data.default_user_login_account, Data.default_user_login_pwd, encounterId, personId);
            	info = new ArrayList<String>();
            	info.add(hisCost);
            	return info;
            }

            // 等待正在收费状态为false
            while (Data.inCharging) {
                SdkTools.logger.log(3, "其它线程正在收费,稍后再试");
                logger.log(3, "其它线程正在收费,稍后再试");
                SdkTools.sleep(1000);
            }
            try {
                // 设置为正在收费为true
                Data.inCharging=true;
                // 按cisxh依次收费
                info = chargeByCisxhList(cisxhList);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new Error(e.getMessage());
            }finally {
                // 设置为正在收费为true
                Data.inCharging=false;
            }
            return info;
        }
        catch (Throwable e) {
            throw new Error("His收费失败:" + e.getMessage());
        }
    }

    public List<String> chargeByCisxhList(List<String> cisxhList) {
        try {
            Map<String, String> data = null;
            String procName = "";
            Double totalCost = 0.0;

            // 按cisxh依次收费
            if (Data.hisType.equals("TJWN")) {
                String ghxh = cisxhList.get(0);
                cisxhList.remove(0);
                for (String itemCost : cisxhList) {
                    totalCost += Double.valueOf(itemCost);
                }
                HttpTestUrl httpTestUrl = new HttpTestUrl(Data.hisQyUrl + "/Service1.asmx");
                HttpTestHeader header = new HttpTestHeader();
                header.addHeader("Content-Type", "text/xml;charset=utf-8");
                //收费划价 B202
                String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                        "  <soap:Body>\r\n" +
                        "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                        "      <strMethodName>B202</strMethodName>\r\n" +
                        "      <InXml>&lt;Request&gt;\r\n" +
                        "&lt;TradeCode&gt;B202&lt;/TradeCode&gt;\r\n" +
                        "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                        "&lt;HISPatientID&gt;20201109151013877265&lt;/HISPatientID&gt;\r\n" +
                        "&lt;MedCardNo&gt;&lt;/MedCardNo&gt;\r\n" +
                        "&lt;MedCardPwd&gt;&lt;/MedCardPwd&gt;\r\n" +
                        "&lt;InsurTypeCode&gt;&lt;/InsurTypeCode&gt;\r\n" +
                        "&lt;MtType&gt;&lt;/MtType&gt;\r\n" +
                        "&lt;RegID&gt;" + ghxh + "&lt;/RegID&gt;\r\n" +
                        "&lt;BusinessFrom&gt;1&lt;/BusinessFrom&gt;\r\n" +
                        "&lt;ReceiptPayNo&gt;&lt;/ReceiptPayNo&gt;\r\n" +
                        "&lt;/Request&gt;</InXml>\r\n" +
                        "    </CallWebMethod>\r\n" +
                        "  </soap:Body>\r\n" +
                        "</soap:Envelope>";

                HttpTest test = new HttpTest(httpTestUrl);
                test.sendPostRequest(request, header);
                test.waitRequestFinish(30000);
                logger.boxLog(1, "B202 - 收费划价",
                        "\nheader:\n" + header.map +
                                "\nbody:\n" + request +
                                "\nstateCode:\n" + test.getResponseCode() +
                                "\ncontent:\n" + test.getResponseContent());
                logger.assertFalse(!test.getResponseContent().contains("交易成功"), "B202 - 收费划价失败");

                //收费确认 B203
                request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                        "  <soap:Body>\r\n" +
                        "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                        "      <strMethodName>B203</strMethodName>\r\n" +
                        "      <InXml>&lt;Request&gt;\r\n" +
                        "&lt;TradeCode&gt;B203&lt;/TradeCode&gt;\r\n" +
                        "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                        "&lt;HISPatientID&gt;20201109111332005574&lt;/HISPatientID&gt;\r\n" +
                        "&lt;MedCardNo&gt;&lt;/MedCardNo&gt;\r\n" +
                        "&lt;MedCardPwd&gt;&lt;/MedCardPwd&gt;\r\n" +
                        "&lt;IsureTypeCode&gt;&lt;/IsureTypeCode&gt;\r\n" +
                        "&lt;MtType&gt;&lt;/MtType&gt;\r\n" +
                        "&lt;RegID&gt;" + ghxh + "&lt;/RegID&gt;\r\n" +
                        "&lt;TotalAmount&gt;" + totalCost + "&lt;/TotalAmount&gt;\r\n" +
                        "&lt;ReceiptPayNo&gt;&lt;/ReceiptPayNo&gt;\r\n" +
                        "&lt;BusinessFrom&gt;1&lt;/BusinessFrom&gt;\r\n" +
                        "&lt;OrderNo&gt;&lt;/OrderNo&gt;\r\n" +
                        "&lt;PaymentType&gt;&lt;/PaymentType&gt;\r\n" +
                        "&lt;PaymentBank&gt;&lt;/PaymentBank&gt;\r\n" +
                        "&lt;DivisionID&gt;&lt;/DivisionID&gt;\r\n" +
                        "&lt;/Request&gt;</InXml>\r\n" +
                        "    </CallWebMethod>\r\n" +
                        "  </soap:Body>\r\n" +
                        "</soap:Envelope>";

                test = new HttpTest(httpTestUrl);
                test.sendPostRequest(request, header);
                test.waitRequestFinish(30000);
                logger.boxLog(1, "B203 - 收费确认",
                        "\nheader:\n" + header.map +
                                "\nbody:\n" + request +
                                "\nstateCode:\n" + test.getResponseCode() +
                                "\ncontent:\n" + test.getResponseContent());
                logger.assertFalse(!test.getResponseContent().contains("交易成功"), "B203 - 收费确认");
                ArrayList<String> info = new ArrayList<>();
                info.add("" + totalCost);
                logger.log(1, "His收费完成:" + totalCost);
                return info;
            }

            for (String cisxh : cisxhList) {
                data = null;
                // 尝试收费3次
                for (int i = 0; i < 3; i++) {
                    try {
                        data = db.queryFirstRow("收费_cisxh_" + cisxh, new HisSqlManager().mzsf(cisxh));
                        if (Data.addSaveHisSfList) {
                            Data.SaveHisSfList.add(data);
                        }
                        if (data != null) {
                            break;
                        }
                        SdkTools.sleep(5000);
                    }
                    catch (Throwable e) {
                        throw new Error(e.getMessage());
                    }
                }
                if (data.size() < 3) {
                    throw new Error("执行存储过程报错(" + new HisSqlManager().mzsf(cisxh) + ")" + "(" + data + "");
                }
                totalCost += Double.valueOf(data.get("总金额"));
                //间隔两秒
                SdkTools.sleep(2000);
            }
            ArrayList<String> info = new ArrayList<>();
            info.add("" + totalCost);
            logger.log(1, "His收费完成:" + totalCost);
            return info;
        }
        catch (Throwable e) {
            throw new Error("收费失败:" + e.getMessage());
        }
    }

    public ArrayList<String> waitSignOffSync(String patientName) {
        ArrayList<String> cisxhList = new ArrayList<>();
        Boolean syncFlag = false;
        if (Data.hisType.equals("SXWN")) {
            // 通过患者姓名查询所有 CLI_ORDER_ID
            String get_drug_recipe_list_sql = "SELECT * FROM WINDBA.DRUG_RECIPE WHERE ENCOUNTER_ID=(SELECT TOP 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' ORDER BY CREATED_AT DESC)";
            String get_herb_recipe_list_sql = "SELECT * FROM WINDBA.HERB_RECIPE WHERE ENCOUNTER_ID=(SELECT TOP 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' ORDER BY CREATED_AT DESC)";
            String get_other_recipe_list_sql = "SELECT CO.ENCOUNTER_ID,CO.CLI_ORDER_ID,ITEM.CLI_ORDER_ITEM_CONTENT,ITEM.PRESCRIBED_QTY,REQ.SVC_REQ_NO FROM WINDBA.CLINICAL_ORDER CO LEFT JOIN WINDBA.CLINICAL_ORDER_ITEM ITEM ON CO.CLI_ORDER_ID=ITEM.CLI_ORDER_ID LEFT JOIN WINDBA.SERVICE_REQUISITION REQ ON ITEM.SVC_REQ_ID = REQ.SVC_REQ_ID WHERE CO.IS_DEL=0 AND ITEM.IS_DEL=0 AND REQ.IS_DEL=0 AND CO.ENCOUNTER_ID = (SELECT TOP 1 ENCOUNTER_ID FROM WINDBA.OUTPATIENT_RECORD WHERE FULL_NAME='" + patientName + "' ORDER BY CREATED_AT DESC)";
            ArrayList<String> drug_recipe_list = db60.db.query("查询西药 RECIPE_NO", get_drug_recipe_list_sql, "RECIPE_NO");
            ArrayList<String> herb_recipe_list = db60.db.query("查询中草药 RECIPE_NO", get_herb_recipe_list_sql, "RECIPE_NO");
            ArrayList<String> other_recipe_list = db60.db.query("查询其它类型 SVC_REQ_NO", get_other_recipe_list_sql, "SVC_REQ_NO");
            cisxhList = drug_recipe_list;
            cisxhList.addAll(herb_recipe_list);
            cisxhList.addAll(other_recipe_list);
            if (cisxhList.size() == 0) {
                throw new Error("60库中查找不到 RECIPE_NO或SVC_REQ_NO");
            }
            // 判断2分钟内是否落库
            String cisxhListString = "(";
            for (String cisxh : cisxhList) {
                cisxhListString += "'" + cisxh + "',";
            }
            cisxhListString = cisxhListString.substring(0, cisxhListString.length() - 1) + ")";
            long endTime = System.currentTimeMillis() + 60000;
            while (System.currentTimeMillis() <= endTime) {
                SdkTools.sleep(5000);
                ArrayList<String> hisOrder = db.query("判断是否落库", "SELECT * FROM opdrecipemain WHERE bGetRateFlag=0 AND cRecipeCode in " + cisxhListString, "cRecipeCode");
                ArrayList<String> hisOrder2 = db.query("判断是否落库", "SELECT * FROM HRadvicerun WHERE bGetRateFlag=0 AND cRequestCode in " + cisxhListString, "cRequestCode");
//				List<Map<String, String>> hisOrder = db.queryAllRow("判断是否落库","SELECT * FROM opdrecipemain WHERE bGetRateFlag=0 AND cRecipeCode in "+cisxhListString);
//				List<Map<String, String>> hisOrder2 = db.queryAllRow("判断是否落库","SELECT * FROM HRadvicerun WHERE bGetRateFlag=0 AND cRequestCode in "+cisxhListString);
                if (hisOrder.size() > 0 || hisOrder2.size() > 0) {
                    cisxhList = hisOrder;
                    cisxhList.addAll(hisOrder2);
                    syncFlag = true;
                    break;
                }
            }
        }
        else if (Data.hisType.equals("JTWN")) {
            // 通过患者姓名查找挂号序号
            String sql = "select ENC_REG_SEQ_NO from WINDBA.OUTPATIENT_ENCOUNTER where ENCOUNTER_ID=(SELECT top 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' order by CREATED_AT desc) and IS_DEL=0";
            List<Map<String, String>> ghxhList = db60.db.queryAllRow("获取挂号序号", sql);
            if (ghxhList.size() == 0) {
                throw new Error("60库中查找不到GHXH:" + sql);
            }
            String ghxh = ghxhList.get(0).get("ENC_REG_SEQ_NO");
            // 判断2分钟内是否落库
            long endTime = System.currentTimeMillis() + 120000;
            while (System.currentTimeMillis() <= endTime) {
                SdkTools.sleep(5000);
                sql = "select cisxh+','''+systype+'''' cisxh from dbo.SF_HJCFK where jlzt=0 and ghxh='" + ghxh + "' order by xh desc";
                ArrayList<String> cisxhListTemp = db.query("判断是否落库", sql, "cisxh");
                
                //可能存在把挂号费放到HJCFK的情况，对应cisxh为空,需要排除这部分数据
                for (int index=0; index < cisxhListTemp.size(); index++) {
                	if(cisxhListTemp.get(index) != null && !cisxhListTemp.get(index).toString().equals("")) {
                		cisxhList.add(cisxhListTemp.get(index));
                	}
                }
                
                if (cisxhList != null && cisxhList.size() > 0) {
                    syncFlag = true;
                    break;
                }
            }
        }
        else if (Data.hisType.equals("ZJWN")) {
            // 通过患者姓名查找挂号序号
            String sql = "select ENC_REG_SEQ_NO from WINDBA.OUTPATIENT_ENCOUNTER where ENCOUNTER_ID=(SELECT top 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' order by CREATED_AT desc) and IS_DEL=0";
            List<Map<String, String>> ghxhList = db60.db.queryAllRow("获取挂号序号", sql);
            if (ghxhList.size() == 0) {
                throw new Error("60库中查找不到GHXH:" + sql);
            }
            String ghxh = ghxhList.get(0).get("ENC_REG_SEQ_NO");
            // 判断2分钟内是否落库
            long endTime = System.currentTimeMillis() + 120000;
            while (System.currentTimeMillis() <= endTime) {
                SdkTools.sleep(5000);
                sql = "select fhir_cfxh cisxh,* from ea_mzypmxk where EA_bazdk_id='" + ghxh + "' and EA_sfrq is null";
                String sql2 = "SELECT fhir_RequestID cisxh,EA_sfrq,* FROM ea_dzbl_br_sqd WHERE ea_dyid in (SELECT ea_id FROM ea_mzyskfk WHERE EA_bazdk_id in ('" + ghxh + "')) and EA_sfrq is null";
                ArrayList<String> hisOrder = db.query("判断药品是否落库", sql, "cisxh");
                ArrayList<String> hisOrder2 = db.query("判断项目是否落库", sql2, "cisxh");
                if (hisOrder.size() > 0 || hisOrder2.size() > 0) {
                    cisxhList = hisOrder;
                    cisxhList.addAll(hisOrder2);
                    syncFlag = true;
                    break;
                }
            }
        }
        else if (Data.hisType.equals("TJWN")) {

            // 通过患者姓名查找挂号序号
            String sql = "";
            if (Data.wn60DbType.toUpperCase().equals("SQLSERVER")) {
                sql = "select ENC_REG_SEQ_NO from WINDBA.OUTPATIENT_ENCOUNTER where ENCOUNTER_ID=(SELECT top 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' order by CREATED_AT desc) and IS_DEL=0";
            }
            else {
                sql = "SELECT * FROM (SELECT ENC.ENC_REG_SEQ_NO FROM OUTPATIENT_ENCOUNTER ENC INNER JOIN OUTPATIENT_RECORD REC ON REC.ENCOUNTER_ID=ENC.ENCOUNTER_ID WHERE REC.FULL_NAME = '" + patientName + "' ORDER BY REC.CREATED_AT DESC) WHERE ROWNUM=1";
            }

            List<Map<String, String>> ghxhList = db60.db.queryAllRow("获取挂号序号", sql);
            if (ghxhList.size() == 0) {
                throw new Error("60库中查找不到GHXH:" + sql);
            }
            String ghxh = ghxhList.get(0).get("ENC_REG_SEQ_NO");

            //判断落库
            long endTime = System.currentTimeMillis() + 60000;
            HttpTestUrl httpTestUrl = new HttpTestUrl(Data.hisQyUrl + "/Service1.asmx");
            HttpTestHeader header = new HttpTestHeader();
            header.addHeader("Content-Type", "text/xml;charset=utf-8");
            while (System.currentTimeMillis() <= endTime) {
                //获取收费列表 B201
                String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" +
                        "  <soap:Body>\r\n" +
                        "    <CallWebMethod xmlns=\"http://tempuri.org/\">\r\n" +
                        "      <strMethodName>B201</strMethodName>\r\n" +
                        "      <InXml>&lt;Request&gt;\r\n" +
                        "&lt;TradeCode&gt;B201&lt;/TradeCode&gt;\r\n" +
                        "&lt;TransNO&gt;YK101020120160719145412&lt;/TransNO&gt;\r\n" +
                        "&lt;RegID&gt;" + ghxh + "&lt;/RegID&gt;\r\n" +
                        "&lt;HISPatientID&gt;&lt;/HISPatientID&gt;\r\n" +
                        "&lt;/Request&gt;</InXml>\r\n" +
                        "    </CallWebMethod>\r\n" +
                        "  </soap:Body>\r\n" +
                        "</soap:Envelope>";

                HttpTest test = new HttpTest(httpTestUrl);
                test.sendPostRequest(request, header);
                test.waitRequestFinish(30000);
                logger.boxLog(1, "B201 - 获取待收费列表",
                        "\nheader:\n" + header.map +
                                "\nbody:\n" + request +
                                "\nstateCode:\n" + test.getResponseCode() +
                                "\ncontent:\n" + test.getResponseContent());
                if (test.getResponseContent().contains("交易成功")) {
                    cisxhList = SdkTools.findMatchList(test.getResponseContent(), "&lt;TotalAmount&gt;(.*?)&lt;/TotalAmount&gt;", 1);
                    syncFlag = true;
                    cisxhList.add(0, ghxh);
                    //交易列表有内容后可能只是部分落库,添加五秒间隔
                    SdkTools.sleep(5000);
                    break;
                }
            }
        }
        else if (Data.hisType.equals("WINEX")) {
        	// 通过患者姓名查找BIZ_ROLE_ID
            String sql = "select BIZ_ROLE_ID from WINDBA.OUTPATIENT_ENCOUNTER where ENCOUNTER_ID=(SELECT top 1 ENCOUNTER_ID from WINDBA.OUTPATIENT_RECORD where FULL_NAME='" + patientName + "' order by CREATED_AT desc) and IS_DEL=0";
            List<Map<String, String>> bizRoleIdList = db60.db.queryAllRow("获取BIZ_ROLE_ID", sql);
            if (bizRoleIdList.size() == 0) {
                throw new Error("60库中查找不到BIZ_ROLE_ID:" + sql);
            }
            String bizRoleId = bizRoleIdList.get(0).get("BIZ_ROLE_ID");
            // 判断2分钟内是否落库
            long endTime = System.currentTimeMillis() + 120000;
            while (System.currentTimeMillis() <= endTime) {
                SdkTools.sleep(5000);
                sql = "select PURCH_ORD_CLASS_CODE from WINDBA.SERVICE_PURCH_ORDER WHERE BIZ_ROLE_ID="+ bizRoleId;
                ArrayList<String> purchOrdClassCodeListTemp = db60.db.query("判断是否落库", sql, "PURCH_ORD_CLASS_CODE");
                ArrayList<String> purchOrdClassCodeList = new ArrayList<String>();
                //可能存在把挂号订单放到SERVICE_PURCH_ORDER的情况，对应PURCH_ORD_CLASS_CODE为空,需要排除这部分数据
                for (int index=0; index < purchOrdClassCodeListTemp.size(); index++) {
                	if(purchOrdClassCodeListTemp.get(index) != null && !purchOrdClassCodeListTemp.get(index).toString().equals("")) {
                		purchOrdClassCodeList.add(purchOrdClassCodeListTemp.get(index));
                	}
                }
                
                if (purchOrdClassCodeList != null && purchOrdClassCodeList.size() > 0) {
                    syncFlag = true;
                    break;
                }
            }
        }
        else {
            throw new Error(Data.hisType + " his类型错误.");
        }
        if (!syncFlag) {
            logger.log("签署后2分钟未落库(患者姓名:" + patientName + ")");
            throw new Error("签署后2分钟未落库(患者姓名:" + patientName + ")");
        }
        logger.log(1, "落库成功: " + cisxhList);
        return cisxhList;
    }

    public List<Map<String, String>> getHistoryList(String tempPath, String delString, String notDelString) {
        // 数据库查询所有需要测试的历史处置
        List<Map<String, String>> historyListTemp = db.queryAllRow("SELECT DISTINCT gh.xh 'GHXH',gh.patid 'PATID',gh.hzxm NAME,gh.ghrq DATE FROM VW_GHZDK gh INNER JOIN VW_MZHJCFK cf on gh.xh = cf.ghxh WHERE (cf.cflx in (1,2,3) or isnull(cf.cisxh,'0')<>'0')  and gh.ksdm='" + Data.newEncounterSubjectCode + "'  AND CONVERT(date,SUBSTRING(gh.ghrq,1,8))>='" + Data.historcalStartDate + "' AND CONVERT(date,SUBSTRING(gh.ghrq,1,8))<='" + Data.historcalEndDate + "' AND len(gh.cardno)<>28 AND cf.jlzt not in (2,4) order by gh.xh");
        List<Map<String, String>> historyList = new ArrayList<>();
        for (int historyIndex = 0; historyIndex < historyListTemp.size(); historyIndex++) {
            if (Data.allHistoralTestMaxNo != -1 && Data.allHistoralTestMaxNo <= historyIndex) {
                break;
            }
            else {
                historyList.add(historyListTemp.get(historyIndex));
            }
        }

        Data.serviceSize = historyList.size();
        // 整理已测试结果,取重复,去除带有指定标签的结果
        SdkTools.sortTemp(tempPath, delString, notDelString, "GHXH");
        // 获取整理后的已测试结果中的挂号序号列表
        List<Map<String, String>> skipHistoryList = SdkTools.readServiceListFromTemp(tempPath, "GHXH");
        List<String> skipGHXHList = new ArrayList<String>();
        for (Map<String, String> history : skipHistoryList) {
            skipGHXHList.add(history.get("GHXH"));
        }
        // 从所有历史处置中去掉已测试的
        List<Map<String, String>> newHistoryList = new ArrayList<Map<String, String>>();
        for (Map<String, String> history : historyList) {
            if (!skipGHXHList.contains(history.get("GHXH"))) {
                history.put("DATE", history.get("DATE").substring(0, 4) + "-" + history.get("DATE").substring(4, 6) + "-" + history.get("DATE").substring(6, 8));
                newHistoryList.add(history);
                logger.log("" + history);
            }
        }
        return newHistoryList;
    }

    public boolean checkExecuteSubject(String ghxh, String ghxh_new) {
        Boolean sameFlag = true;
        if (Data.hisType.equals("JTWN")) {
            String sql = "select ghxh,xh,yfdm,ksdm,cflx from VW_MZHJCFK where ksdm<>yfdm and cflx in ('1','2','3') and ghxh='" + ghxh + "'";
            String sql_new = "select ghxh,xh,yfdm,ksdm,cflx from VW_MZHJCFK where ksdm<>yfdm and ghxh='" + ghxh_new + "'";
            logger.log("查询历史处方:");
            List<Map<String, String>> cfList = db.queryAllRow(sql);
            logger.log("查询落库后的处方:");
            List<Map<String, String>> cfList_new = db.queryAllRow(sql_new);
            if (cfList.size() != cfList_new.size()) {
                throw new Error("历史处方条数 和 落库处方条数不一致!(历史处方:" + cfList.size() + "/落库处方:" + cfList_new.size() + ")");
            }
            for (int i = 0; i < cfList.size(); i++) {
                if (cfList.get(i).get("yfdm").equals(cfList_new.get(i).get("yfdm"))) {
                    logger.log("执行科室代码一致:" + cfList.get(i) + "/" + cfList_new.get(i));
                }
                else {
                    logger.log("执行科室代码不一致:" + cfList.get(i) + "/" + cfList_new.get(i));
                    sameFlag = false;
                }
            }
        }
        return sameFlag;
    }

    public void hisRefundByGhxh(String ghxh) {
        try {
            if (Data.hisType.equals("JTWN")) {
                //集团卫宁退费
                List<Map<String, String>> SjhList = db.queryAllRow("select sjh from SF_BRJSK where ghxh=" + ghxh + " and ghsfbz=1 and jlzt=0 and ybjszt=2");
                logger.assertFalse(SjhList == null || SjhList.size() == 0, "没有找到收据号");
                for (Map<String, String> line : SjhList) {
                    hisRefundBySjh(line.get("sjh"));
                }
                db.excute("UPDATE SF_HJCFK SET jlzt=0 WHERE ghxh=" + ghxh);
                logger.log(1, "His退费成功(挂号序号:" + ghxh + ")");
                SdkTools.sleep(10000);
            }
            else {
                //其它卫宁没写
            }

        }
        catch (Throwable e) {
            e.printStackTrace();
            logger.assertFalse(true, "His退费失败(挂号序号:" + ghxh + ")" + e.getMessage());
        }
    }

    public void hisRefundBySjh(String sjh) {
        try {
            if (Data.hisType.equals("JTWN")) {
                // 集团卫宁退费
                List<Map<String, String>> cfmxList = db.queryAllRow("SELECT mx.ypmc,cf.jssjh,cf.xh,mx.xh mxxh,mx.ypsl,mx.cfts from SF_MZCFK cf INNER JOIN SF_CFMXK mx ON cf.xh=mx.cfxh  where cf.jlzt=0 and cf.jssjh='" + sjh + "'");
                logger.assertFalse(!(cfmxList.size() > 0), "获取收费明细失败", "获取不到收据号:" + sjh + " 对应的收费明细,无法退费");
                // 退费步骤1
                String step1_sql = "exec usp_sf_bftf '68071579B77E',1,0,0,1,'" + sjh + "','00'," + cfmxList.get(0).get("xh") + "," + cfmxList.get(0).get("mxxh") + "," + cfmxList.get(0).get("ypsl") + "," + cfmxList.get(0).get("cfts");
                List<List<String>> step1_result = db.queryAllRowReturnList(step1_sql);
                logger.assertFalse(!step1_result.get(0).get(0).equals("T"), "退费第一步报错:" + step1_result);
                // 退费步骤2
                for (Map<String, String> cfmx : cfmxList) {
                    String step2_sql = "exec usp_sf_bftf '68071579B77E',2,0,0,1,'" + sjh + "','00'," + cfmx.get("xh") + "," + cfmx.get("mxxh") + "," + cfmx.get("ypsl") + "," + cfmx.get("cfts");
                    List<List<String>> step2_result = db.queryAllRowReturnList(step2_sql);
                    logger.assertFalse(!step2_result.get(0).get(0).equals("T"), "退费第二步报错:" + step2_result);
                }
                // 退费步骤3
                String step3_sql = "exec usp_sf_bftf '68071579B77E',3,0,0,1,'" + sjh + "','00'," + cfmxList.get(0).get("xh") + "," + cfmxList.get(0).get("mxxh") + "," + cfmxList.get(0).get("ypsl") + "," + cfmxList.get(0).get("cfts") + ",@zffs=0,@tfksdm='2002'";
                List<List<String>> step3_result = db.queryAllRowReturnList(step3_sql);
                logger.assertFalse(!(step3_result.get(0).get(0).equals("T") && step3_result.get(0).size() > 3), "退费第三步报错:" + step3_result);
//				String newSjh = step3_result.get(0).get(2);
                String newSjh = sjh;
                // 退费步骤4
                long startTime = System.currentTimeMillis();
                String step4_sql = "exec usp_sf_bftf_ex2 '68071579B77E',3,2,0,1,'" + sjh + "','00'," + cfmxList.get(0).get("xh") + "," + cfmxList.get(0).get("mxxh") + "," + cfmxList.get(0).get("ypsl") + "," + cfmxList.get(0).get("cfts") + ",'" + newSjh + "', '', '','','','','',0,0,0,0,0,0,0,0,0,0,0,@bdyhkje = 0,@bdyhklsh = '', @IsUseBdk = 0,@sfksdm='2002',@ipdz_gxzsj='10.169.1.190',@zffs=0";
                List<List<String>> step4_result = null;
                while (System.currentTimeMillis() - startTime < 120000) {
                    SdkTools.sleep(5000);
                    step4_result = db.queryAllRowReturnList(step4_sql);
                    if (step4_result.get(0).get(0).equals("T")) {
                        // 退费完成
                        logger.log(1, "退费成功(收据号:" + sjh + ")");
                        return;
                    }
                    else {
                        // 退费第四步失败,重试
                        logger.log(3, "退费第四步失败,重试..." + step4_result);
                    }
                }
                logger.assertFalse(true, "退费第四步报错:" + step4_result);
            }
            else {
                //其它卫宁没写
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            logger.assertFalse(true, "退费失败(收据号:" + sjh + ")" + e.getMessage());
        }
    }

    public List<Map<String, String>> getCfmxList(List<String> cisxhList) {
        List<Map<String, String>> cfmxList = null;
        if (Data.hisType.equals("JTWN")) {
            String sql = new HisSqlManager().getCFMX(cisxhList);
            cfmxList = db.queryAllRow("根据cisxh获取处方明细", sql);
        }
        else {
            logger.assertFalse(true, Data.hisType + " 暂不支持获取处方明细");
        }
        return cfmxList;
    }

    public void disconnect() {
        db.disconnect();
        db60.disconnect();
    }

    // 挂号
    public ArrayList<String> newEncounter() {
        return newEncounter(null, null, null);
    }

    public ArrayList<String> newEncounter(String patid, String subjectCode) {
        return newEncounter(patid, subjectCode, null);
    }

    // 挂号 指定患者 和 科室 和 性别
    public ArrayList<String> newEncounter(String patid, String subjectCode, String sex) {
        ArrayList<String> patInfo = null;
        int count = 0;
        while (patInfo == null && count < 3) {
            count++;
            try {
                if (Data.getEncounterFromFile) {//从文件中读取患者信息
                    return Encounter.getNewEncounter();
                }
                else if (Data.hisType.equals("SXWN")) {
                    if (patid != null || subjectCode != null) {
                        logger.assertFalse(true, Data.hisType + " 暂不支持指定患者挂号");
                    }
                    patInfo = encounterSyncForHisSXWN(Data.host, Data.fhir_port);
                }
                else if (Data.hisType.equals("JTWN")) {
                    patInfo = encounterSyncForHisJTWN(Data.host, Data.hospital_soid, patid, subjectCode, sex);
                }
                else if (Data.hisType.equals("ZJWN")) {
                    if (patid != null || subjectCode != null) {
                        logger.assertFalse(true, Data.hisType + " 暂不支持指定患者挂号");
                    }
                    patInfo = encounterSyncForHisZJWN(Data.hisServiceHost, subjectCode);
                }
                else if (Data.hisType.equals("TJWN")) {
                    if (patid != null || subjectCode != null) {
                        logger.assertFalse(true, Data.hisType + " 暂不支持指定患者挂号");
                    }
                    patInfo = encounterSyncForHisTJWN(Data.hisQyUrl, "" + System.currentTimeMillis(), Data.newEncounterSubjectCode, Data.tjwn_doctorid);
                }
                else if (Data.hisType.equals("QTWN")) {
                    if (patid != null || subjectCode != null) {
                        logger.assertFalse(true, Data.hisType + " 暂不支持指定患者挂号");
                    }
                    patInfo = encounterSyncForOtherHis(Data.hisQyUrl, Data.newEncounterSubjectCode);
                }
                else if(Data.hisType.equals("WINEX")) {
                    if (patid != null) {
                        logger.assertFalse(true, Data.hisType + " 暂不支持指定患者挂号");
                    }
                    patInfo = encounterSyncForWINEX(Data.default_user_login_account, Data.default_user_login_pwd, Data.test_select_subject);
                }
            }
            catch (Throwable e) {
                logger.log(3, "挂号失败 重试"+e.getMessage());
            }
        }
        logger.assertFalse(patInfo == null, "挂号失败");
        return patInfo;
    }

    //挂两次号
    public ArrayList<String> newEncounterTwice() {
        return newEncounterTwice(null, null);
    }

    public ArrayList<String> newEncounterTwice(String patid, String subjectCode) {
        List<String> gh1 = newEncounter(patid, subjectCode);
        List<String> gh2 = newEncounter(gh1.get(2), subjectCode);
        ArrayList<String> info = new ArrayList<>();
        info.add(gh1.get(0));
        info.add(gh1.get(1));
        info.add(gh1.get(3));
        info.add(gh2.get(1));
        info.add(gh2.get(3));
        return info;
    }


    //根据patid获取患者ZDMC 判断诊断是否落库，是否一致
    public void checkExecuteDiagnos(String patid) {

        if (Data.hisType.equals("JTWN")) {
            SdkTools.sleep(10000);
            String sql = new HisSqlManager().getzdmc(patid);
            List<Map<String, String>> item = db.queryAllRow("查询所有诊断", sql);
            System.out.println("size : " + item.size());

            if (item.size() == 0) {
                throw new Error("库中查找不到" + sql);
            }
            else {
                String name = item.get(0).get("zdmc");
                if (name.equals(Data.test_disease)) {
                    logger.log(1, "落库成功，诊断名称为：" + name);
                }
                else {
                    logger.log(2, "诊断库内未找到诊断名称");

                }
            }

        }
    }


    public List<Map<String, String>> getServiceList(String tempPath, String delString, String notDelString) {
        List<Map<String, String>> allServiceList = new ArrayList<Map<String, String>>();
        // 数据库查询所有需要测试的医嘱
        try {
            if (Data.testMedicineFlag) {
                List<Map<String, String>> medicineList = db.queryAllRow("查询所有药品商品", new HisSqlManager().getAllMedicineSql());
                List<Map<String, String>> medicineListValid = new ArrayList<Map<String, String>>();
                for (Map<String, String> medicine : medicineList) {
                    medicineListValid.add(medicine);
                    if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < medicineListValid.size()) {
                        break;
                    }
                    else {
                        allServiceList.add(medicine);
                    }
                }
            }
            if (Data.testExamItemFlag) {
                List<Map<String, String>> examItemList = db.queryAllRow("查询所有检查项目", new HisSqlManager().getAllExamItemSql());
                List<Map<String, String>> examItemListValid = new ArrayList<Map<String, String>>();
                for (Map<String, String> examItem : examItemList) {
                    examItemListValid.add(examItem);
                    if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < examItemListValid.size()) {
                        break;
                    }
                    else {
                        allServiceList.add(examItem);
                    }
                }
            }
            if (Data.testTreatFlag) {
                List<Map<String, String>> treatList = db.queryAllRow("查询所有治疗服务", new HisSqlManager().getAllTreatSql());
                List<Map<String, String>> treatListValid = new ArrayList<Map<String, String>>();
                for (Map<String, String> treat : treatList) {
                    treatListValid.add(treat);
                    if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < treatListValid.size()) {
                        break;
                    }
                    else {
                        allServiceList.add(treat);
                    }

                }
            }
            if (Data.testLabFlag) {
                List<Map<String, String>> labList = db.queryAllRow("查询所有检验服务", new HisSqlManager().getAllLabSql());
                List<Map<String, String>> labListValid = new ArrayList<Map<String, String>>();
                for (Map<String, String> lab : labList) {
                    labListValid.add(lab);
                    if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < labListValid.size()) {
                        break;
                    }
                    else {
                        allServiceList.add(lab);
                    }
                }
            }
//			if (Data.testPathologyFlag) {
//				List<Map<String, String>> pathologyList = db.queryAllRow("查询所有病理服务", new HisSqlManager().getAllPathologySql());
//				List<Map<String, String>> pathologyListValid = new ArrayList<Map<String,String>>();
//				for (Map<String, String> pathology : pathologyList) {
//					pathologyListValid.add(pathology);
//					if(Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < pathologyListValid.size()) {
//						break;
//					}else {
//						allServiceList.add(pathology);
//					}
//				}
//			}
        }
        catch (Throwable e) {
            throw new Error("获取测试数据失败:" + e.getMessage());
        }
        logger.log("总服务个数:" + allServiceList.size());
        Data.serviceSize = allServiceList.size();
        // 解析temp文件, 去掉ID重复行 和 包含指定flag的行
        SdkTools.sortTemp(tempPath, delString, notDelString, "ID");
        // 获取需要跳过的ID列表
        List<String> skipIdList = new ArrayList<String>();
        List<Map<String, String>> skipServiceList = SdkTools.readServiceListFromTemp(tempPath, "ID");
        for (Map<String, String> service : skipServiceList) {
            skipIdList.add(service.get("ID"));
        }
        logger.log("跳过已测试:" + skipIdList.size());
        // 所有医嘱中去除需要跳过的ID得到最终的测试列表
        List<Map<String, String>> newServiceList = new ArrayList<Map<String, String>>();
        for (Map<String, String> service : allServiceList) {
            if (!skipIdList.contains(service.get("ID"))) {
                newServiceList.add(service);
            }
        }
        logger.log("本次测试服务," + newServiceList.size());
        return newServiceList;
    }


    public void updatejlzt(String ghxh, String ypmc) {
        if (Data.hisType.equals("JTWN")) {
//			List<Map<String, String>> list = db.queryAllRow("update SF_HJCFK set jlzt=0 from dbo.SF_HJCFK,dbo.SF_HJCFMXK where ghxh ='?1'  AND SF_HJCFK.xh = SF_HJCFMXK.cfxh and  SF_HJCFMXK.ypmc like '?2'".replace("?1",ghxh).replace("?2",ypmc));
//			if (list.size() == 0) {
//				throw new Error("更新jlzt失败" + list);
//			}logger.log(1, "更新状态成功：" + list.get(1).get("jlzt"));
            db.excute("update SF_HJCFK set jlzt=0 from dbo.SF_HJCFK,dbo.SF_HJCFMXK where ghxh ='?1'  AND SF_HJCFK.xh = SF_HJCFMXK.cfxh and  SF_HJCFMXK.ypmc like '?2'".replace("?1", ghxh).replace("?2", ypmc));
            logger.log(1, "执行成功 (挂号序号:" + ghxh + ")");
        }

    }


    //判断自备药落库标识
    public Boolean getzbybz(String hzxm) {
        Boolean flag = null;
        String sql = new HisSqlManager().getzbybz(hzxm);
        ArrayList<String> items = db.query("自备药标识", sql, "zbybz");
        if (items.size() != 1) {
            throw new Error("HIS库中查找不到zbybz:" + sql);
        }
        if (items.get(0).equals("1")) {
            flag = true;
            logger.log(1,"自备药标识为‘1’，自备药HIS落库成功！");
        }
        else {
            flag = false;
        }
        return flag;
    }

    //更新YFZKC中药品库存数量
    public void updateYFZKC(String pharmacyName, String drugName, String stockNum) {
        String sql1= "select id from YY_KSBMK where name ='"+pharmacyName+"'";
        String ksdm =db.queryFirstRow(sql1).get("id").trim();
        String sql = new HisSqlManager().updateYFZKC(stockNum,ksdm,drugName);
        db.excute(sql);
    }
    
    /**
     * 修改库存
     * @param pharmacyName 药房名称
     * @param drugName 药品名称
     * @param stockNum 库存数量
     */
    public void updateStock(String pharmacyName, String drugName, String stockNum) {
        updateYFZKC(pharmacyName,drugName,stockNum) ;
        SdkTools.execXXLJOB(Data.xxljob_host, Data.drugStorageStockSyn, Data.hospital_soid);
        SdkTools.execXXLJOB(Data.xxljob_host, Data.medicineStockSyncRedis, Data.hospital_soid);
        SdkTools.execXXLJOB(Data.xxljob_host, Data.CsMedicineExecStorageSyncHandler, Data.hospital_soid);
    }
    
    //医保分类库中根据医保代码ybbm获取医保说明ybsm
    public String getYbsmFromYBFLKByYbdm(String ybdm) {
    	String ybsm = db.queryFirstRow("select ybsm from YY_YBFLK where ybdm ="+ybdm).get("ybsm").trim();
    	return ybsm;
    }
    
    //科室编码库中，通过ID获取name
    public String getNameFromKSBMKById(String id) {
    	String name = db.queryFirstRow("select name from YY_KSBMK where id ='"+id+"'").get("name").trim();
    	return name;
    }
    
	  /**
	   * 大HIS接口方式登录，获取待收取的费用信息
	 * @throws UnsupportedEncodingException
	 */
	public String getCostWINEX(String username, String password ,String encounterId ,String personId) throws UnsupportedEncodingException {
	    //把明文密码倒序再base64加密
	    StringBuffer stringBuffer = new StringBuffer(password).reverse();
	    password = stringBuffer.toString();
	    String base64encodedPassword = Base64.getEncoder().encodeToString(password.getBytes("utf-8"));
	
	    //登陆大HIS系统
	    String url = "http://" + Data.host + "/base/api/v1/base/user/confusion_login";
	    String json = "{\"username\":\"" + username + "\",\"password\":\"" + base64encodedPassword + "\",\"orgName\":\"\",\"hospitalSOID\":\"\",\"locationId\":\"\",\"locationName\":\"\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\"}";
	    HttpTestUrl httpTestUrl = new HttpTestUrl(url);
	    HttpTest test = new HttpTest(httpTestUrl);
	    HttpTestHeader header = new HttpTestHeader();
	    header.addHeader("Content-Type", "application/json;charset=UTF-8");
	    test.sendPostRequest(json, null, header);
	    test.waitRequestFinish(30000);
	    if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
	        logger.boxLog(1, "登陆接口返回成功", "登陆成功");
	        //从登陆接口的返回值中获取access_token
	        JsonParser parser = new JsonParser();
	        JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
	        JsonObject dataObject = contentJson.getAsJsonObject("data");
	        String Authorization = "Bearer " + dataObject.get("access_token").getAsString();
	        String cookie = "W-FLOW=canary; W-SEQ=6666; W-APP=his; X-DEBUG=hybrid; BEARER_TOKEN=Bearer%20" + dataObject.get("access_token").getAsString();
	        header.addHeader("Cookie", cookie);
	        header.addHeader("Authorization", Authorization);
	    } else {
	        logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
	    }
	    
	    //根据就诊标识列表获取可收费的订单信息
	    url = "http://" + Data.host + "/outp-finance-fee/api/v1/app_finance/service_purch_order/query/by_encounter_ids";
	    json="{\"encounterIdList\":[{\"encounterId\":\""+encounterId+"\"}],\"lockedMACAddress\":\"127.0.0.1\",\"lockedIPAddress\":\"127.0.0.1\",\"personId\":\""+personId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
	    httpTestUrl = new HttpTestUrl(url);
	    test = new HttpTest(httpTestUrl);
	    System.out.println(json);
	    test.sendPostRequest(json, null, header);
	    test.waitRequestFinish(30000);
	    String totalCost ="";
	    if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
	        String content = test.getResponseContent();
	        JsonParser parser = new JsonParser();
	        JsonObject contentJson = parser.parse(content).getAsJsonObject();
	        JsonObject dataObject = contentJson.getAsJsonObject("data");
	        totalCost = dataObject.get("chargableServicePurchaseOrderRetAmt").getAsString();
	        logger.boxLog(1, "根据就诊标识列表获取可收费的订单信息", "订单待收费用为："+totalCost+"\r"+content);
	        return totalCost;
	    } else {
	        logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
	    }
	    return totalCost;
}
	
	  /**
	   * 大HIS接口方式登录，患者信息登记，并挂号
	 * @throws UnsupportedEncodingException
   */
  public ArrayList<String> encounterSyncForWINEX(String username, String password, String subject) {
      try {
    	//把明文密码倒序再base64加密
          StringBuffer stringBuffer = new StringBuffer(password).reverse();
          password = stringBuffer.toString();
          String base64encodedPassword = Base64.getEncoder().encodeToString(password.getBytes("utf-8"));

          //登陆大HIS系统
          String url = "http://" + Data.host + "/base/api/v1/base/user/confusion_login";
          String json = "{\"username\":\"" + username + "\",\"password\":\"" + base64encodedPassword + "\",\"orgName\":\"\",\"hospitalSOID\":\"\",\"locationId\":\"\",\"locationName\":\"\",\"ipAddress\":\"127.0.0.1\",\"macAddress\":\"26:93:4d:fa:5d:c1\"}";
          HttpTestUrl httpTestUrl = new HttpTestUrl(url);
          HttpTest test = new HttpTest(httpTestUrl);
          HttpTestHeader header = new HttpTestHeader();
          header.addHeader("Content-Type", "application/json;charset=UTF-8");
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String token = "" ;
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              logger.boxLog(1, "登陆接口返回成功", "登陆成功");
              //从登陆接口的返回值中获取access_token
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(test.getResponseContent()).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              token = dataObject.get("access_token").getAsString();
              String Authorization = "Bearer " + dataObject.get("access_token").getAsString();
              String cookie = "W-FLOW=canary; W-SEQ=6666; W-APP=his; X-DEBUG=hybrid; BEARER_TOKEN=Bearer%20" + dataObject.get("access_token").getAsString();
              header.addHeader("Cookie", cookie);
              header.addHeader("Authorization", Authorization);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //获取用户信息
          url = "http://" + Data.host + "/base/api/v1/base/user/get_information";
          json = "{\"token\":\""+token+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String employeeId ;
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              employeeId = dataObject.get("employeeId").getAsString();
              logger.boxLog(1, "用户信息获取接口返回成功", content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
              return null;
          }


          //根据科目和日期从号源池中查询指定班次
    		String noonTypeCode = "";
    		int hour = Integer.valueOf(SdkTools.getCurrentTime().substring(11, 13));
    		if(hour<12) {
    			noonTypeCode = "375566";
    		}else if(hour>=18) {
    			noonTypeCode ="376487";
    		}else {
    			noonTypeCode ="376486";
    		}
//    		noonTypeCode="376487";
          url = "http://" + Data.host + "/schedule-outpatient/api/v1/app_encounter_schedule/enc_num_pool/query/by_example";
          json = "{\"pageType\":\"P\",\"pageSize\":30,\"pageNo\":0,\"encDeptId\":\"\",\"noonTypeCode\":\""+noonTypeCode+"\",\"essTypeCode\":\"\",\"essCategoryId\":\"\",\"autoSelectorFlag\":\"98175\",\"scheduledDate\":\""+SdkTools.getCurrentDate()+"\",\"seqProperty\":\"1\",\"seqType\":\"desc\",\"serviceChannelTypeCode\":\"253604\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String essName= "" ;
          String registrationfee= "";
          String resourceQtyNotused= "";
          String bizResourcePoolId= "";
          String essChannelId= "";
          String essId= "";
          String essCategoryId= "";
          String essTypeCode= "";
          String encChargeServiceId = "";
          String encDeptId= "";
          String resourceQtyInChildes= "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
              logger.boxLog(1, "根据科目和日期从号源池中查询指定班次接口返回成功",content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
    			JsonArray dataArray = contentJson.getAsJsonArray("data");
//              if(dataArray.size()==0) {
//                  logger.assertFalse(true, "报错", "今日"+SdkTools.getCurrentDate()+"当前午别没有可挂号数据");
//              }
              boolean sucFlag = true;
              for(int i=0;i<dataArray.size();i++) {
              	essName = dataArray.get(i).getAsJsonObject().get("essName").getAsString().replace("\"", "");
              	if(essName.equals(subject)) {
              		registrationfee = dataArray.get(i).getAsJsonObject().get("registrationfee").getAsString();
              		if(registrationfee==null) {
              			logger.assertFalse(true, "报错", "此科目："+subject+"挂号费用异常");
              		}
                      resourceQtyNotused= dataArray.get(i).getAsJsonObject().get("resourceQtyNotused").getAsString();
                      resourceQtyInChildes = dataArray.get(i).getAsJsonObject().get("resourceQtyInChildes").getAsString();
//                      if(resourceQtyNotused.equals("0")) {
//              			logger.assertFalse(true, "报错", "此科目："+subject+"剩余号数为0");
//              		}
                      bizResourcePoolId= dataArray.get(i).getAsJsonObject().get("bizResourcePoolId").getAsString();
                      essChannelId= dataArray.get(i).getAsJsonObject().get("essChannelId").getAsString();
                      essId= dataArray.get(i).getAsJsonObject().get("essId").getAsString();
                      essCategoryId= dataArray.get(i).getAsJsonObject().get("essCategoryId").getAsString();
                      essTypeCode= dataArray.get(i).getAsJsonObject().get("essTypeCode").getAsString();
                      encChargeServiceId= dataArray.get(i).getAsJsonObject().get("encChargeServiceId").getAsString();
                      encDeptId= dataArray.get(i).getAsJsonObject().get("encDeptId").getAsString();
                      sucFlag = false;
                      break;
              	}
              }
              logger.assertFalse(sucFlag, "报错", "可挂号科目列表里未找到当前科目："+subject);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //查询当前资源池下医生信息
          url = "http://" + Data.host + "/schedule-outpatient/api/v1/app_encounter_schedule/doctor_ess_number/query/by_example";
          json = "{\"bizResourcePoolIds\":[\""+bizResourcePoolId+"\"],\"operateSwitch\":\"EN105\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          int scheduleNum = 0;
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              String content = test.getResponseContent();
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
    			JsonArray dataArray = contentJson.getAsJsonArray("data");
    			if(dataArray.get(0).getAsJsonObject().getAsJsonArray("provideEssDoctors").size()==0) {
    				scheduleNum = Integer.valueOf(resourceQtyInChildes)-Integer.valueOf(resourceQtyNotused);
    			}else {
    				for(int i=0;i<dataArray.get(0).getAsJsonObject().getAsJsonArray("provideEssDoctors").size();i++) {
    					scheduleNum = scheduleNum+dataArray.get(0).getAsJsonObject().getAsJsonArray("provideEssDoctors").get(i).getAsJsonObject().get("scheduleNum").getAsInt();
    				}
    			}
    			
    			logger.boxLog(1, "查询当前资源池下医生信息接口返回成功", "查询当前资源池下医生信息成功，此科目当前午别下共计挂号人数为："+scheduleNum+"\r"+content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }
          
          //门诊患者信息建档，获取接口返回的personId、bizRoleId，做为后续下个接口的传参
          String patientName = "自动化测试患者" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ((int) ((Math.random() * 9 + 1) * 100000)) + "";
          String IDCode = IdCardGenerator.IDCardCreate();
          String birthday = IdCardGenerator.getBirthdayAgeSex(IDCode).get("birthday");
          url = "http://" + Data.host + "/encounter-patient/api/v1/person_patient/personal_file/save";
          json="{\"fullName\":\"" + patientName + "\",\"genderCptId\":\"253816\",\"hashContinue\":false,\"bizRoleInsurance\":[{\"defaultFlag\":98175,\"medInstiInsurId\":\"57393491145648129\"}],\"cardMediaFlag\":\"98176\",\"anonym\":null,\"birthDate\":\""+birthday+"\",\"birthTime\":\"00:00\",\"hashRealNameAuthByManMade\":false,\"idcardNo\":\""+IDCode+"\",\"idcardTypeCptId\":\"391004456\",\"nationCptId\":\"391004355\",\"nationalityCptId\":\"399205791\",\"realNameAuthLevelCode\":\"255799\",\"handleType\":\"save\",\"medInstiInsurId\":\"57393491145648129\",\"contactAddrCodeList\":{},\"contactRechargeType\":\"109709972447852545\",\"patientTag\":[],\"nativePlace\":{},\"greenChannelFlag\":\"98176\",\"personGreenChannel\":null,\"addrCountryCode\":\"399205791\",\"medInsurUsableRangeCode\":\"959938\",\"hospitalSOID\":\""+ Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String personId = "";
          String bizRoleId = "";
          String omrn = "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              //从接口返回值中获取personId、bizRoleId
              String content = test.getResponseContent();
              logger.boxLog(1, "门诊患者信息建档接口返回成功，患者信息点击见详情", "患者姓名："+patientName+" 身份证号码："+IDCode+"\r"+content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              personId = dataObject.get("personId").getAsString();
              bizRoleId = dataObject.get("bizRoleId").getAsString();
              omrn= dataObject.get("omrn").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //挂号联动规则校验
          url = "http://" + Data.host + "/schedule-outpatient/api/v1/app_encounter_schedule/registration_linkage_validate/query";
          json="{\"ruleAppScenesCode\":\"956446\",\"detailList\":[{\"bizResourcePoolId\":\""+bizResourcePoolId+"\",\"encDeptId\":\""+encDeptId+"\",\"essChannelId\":\""+essChannelId+"\",\"essId\":\""+essId+"\",\"essCategoryId\":\""+essCategoryId+"\",\"essTypeCode\":\""+essTypeCode+"\",\"initVisitFlag\":\"138126\",\"bizRoleId\":\""+bizRoleId+"\"}],\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = SdkTools.post("挂号联动规则校验", url, header, json, null, logger);
          List<Map<String, String>> encounterServiceList = new ArrayList<Map<String, String>>();
          Map<String, String> map = new HashMap<String, String>();
          map.put("esId",encChargeServiceId);
    		map.put("calculationCardinal", "1");
    		encounterServiceList.add(map);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "挂号联动规则校验接口请求成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
    			JsonArray dataArray = contentJson.getAsJsonArray("data");
    			if(dataArray.size()==0) {
    				logger.boxLog(0, "没有联动信息", "挂号联动规则校验接口未查询到联动信息");
    			}else {
    				for(int i=0;i<dataArray.size();i++) {
    					map = new HashMap<String, String>();
    					map.put("esId",dataArray.get(i).getAsJsonObject().get("encChargeServiceId").getAsString());
    					map.put("calculationCardinal", dataArray.get(i).getAsJsonObject().get("quantity").getAsString());
    					encounterServiceList.add(map);
    				}
    			}
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //挂号预处理
          url = "http://" + Data.host + "/schedule-outpatient/api/v1/app_encounter_schedule/register_outp_number/lock";
          json="{\"bizRoleId\":\""+bizRoleId+"\",\"initVisitFlag\":\"138126\",\"takeUpNumberList\":[{\"bizResourcePoolId\":\""+bizResourcePoolId+"\",\"essChannelId\":\""+essChannelId+"\",\"essId\":\""+essId+"\",\"essCategoryId\":\""+essCategoryId+"\",\"scheduledDoctorId\":\""+employeeId+"\",\"secondConfirmationFlag\":false}],\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String bizResourceId ="";
          String encounterId ="";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              String content = test.getResponseContent();
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
    			JsonArray dataArray = contentJson.getAsJsonArray("data");
    			bizResourceId = dataArray.get(0).getAsJsonObject().get("bizResourceId").getAsString().replace("\"", "");
    			encounterId = dataArray.get(0).getAsJsonObject().get("encounterId").getAsString().replace("\"", "");
    			logger.boxLog(1, "挂号预处理接口返回成功", content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //就诊服务计费
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/outpatient/encounter_service/bill";
          json="{\"bizRoleId\":\""+bizRoleId+"\",\"createdBy\":\""+employeeId+"\",\"encounterId\":\""+encounterId+"\",\"encounterServiceList\":"+JSONObject.toJSONString(encounterServiceList)+",\"personId\":\""+personId+"\",\"prescribingDeptId\":\""+encDeptId+"\",\"requestedAt\":\""+SdkTools.getCurrentTime()+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          List<Map<String,Object>> servicePurchOrderIdList = new ArrayList<Map<String,Object>>();
          List<Map<String,Object>> registerChargeList = new ArrayList<Map<String,Object>>();
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
            	String content = test.getResponseContent();
              	logger.boxLog(1, "就诊服务计费接口返回成功",content);
              	JsonParser parser = new JsonParser();
    	        JsonObject contentJson = parser.parse(content).getAsJsonObject();  
    			JsonArray dataArray = contentJson.getAsJsonArray("data");
    			if(dataArray.size()==0) {
    				logger.boxLog(0, "没有就诊服务详细信息", "");
    			}else {
    				for(int i=0;i<dataArray.size();i++) {
    					Map<String, Object> map1 = new HashMap<String, Object>();
    					Map<String, Object> map2 = new HashMap<String, Object>();
    					map1.put("servicePurchOrderId", dataArray.get(i).getAsJsonObject().get("servicePurchOrderId").getAsString());
    					map1.put("encounterId", encounterId);
    					map1.put("medInstiInsurId", "57393491145648129");
    					List<Map<String,Object>> servicePurchOrderDetailList = new ArrayList<Map<String,Object>>();
    					Map<String, Object> map3 = new HashMap<String, Object>();
    					for(int j=0;j<dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").size();j++) {
    						map3 = new HashMap<String, Object>();
    						map3.put("chargingItemId",dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(j).getAsJsonObject().get("chargingItemId").getAsString());
    						map3.put("commodityTypeCode","255656");
    						map3.put("commodityTypeCptId","399202502");
    						map3.put("retailPrice",dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(j).getAsJsonObject().get("retailPrice").getAsDouble());
    						map3.put("retailQty",dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(j).getAsJsonObject().get("retailQty").getAsDouble());
    						map3.put("servicePurchOrderDetailId",dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").get(j).getAsJsonObject().get("servicePurchOrderDetailId").getAsString());
    						servicePurchOrderDetailList.add(map3);
    					}
    					map1.put("servicePurchOrderDetailList", servicePurchOrderDetailList);
    					servicePurchOrderIdList.add(map1);
    					map2.put("servicePurchOrderId", dataArray.get(i).getAsJsonObject().get("servicePurchOrderId").getAsString());
    					map2.put("encChargeServiceId",dataArray.get(i).getAsJsonObject().get("esId").getAsString());
    					map2.put("servicePurchaseOrderRetAmt",dataArray.get(i).getAsJsonObject().get("servicePurchaseOrderRetAmt").getAsString());
    					map2.put("encChargeServiceQty",dataArray.get(i).getAsJsonObject().getAsJsonArray("servicePurchOrderDetailList").size()+"");
    					registerChargeList.add(map2);
    				}
    			}

          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //账单生成
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/bill/generate";
          json="{\"deptId\":\""+encDeptId+"\",\"identityNo\":1,\"chrgWinId\":\"1\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"personalIdentityId\":\"154390834984396803\",\"bizRoleId\":\""+bizRoleId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\",\"billTypeCode\":\"307279\",\"billTypeCptId\":\"399185053\",\"macAddress\":\"\",\"ipAddress\":\"\",\"servicePurchOrderIdList\":"+JSONObject.toJSONString(servicePurchOrderIdList)+",\"encounterId\":\""+encounterId+"\",\"settleMedInstiInsurId\":\"57393491145648129\",\"personId\":\""+personId+"\",\"posCptId\":\"399211916\",\"posConceptId\":\"399211916\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String settlementId = "";
          String billId = "";
          String businessLockRecordId = "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "账单生成接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              settlementId = dataObject.get("settlementId").getAsString();
              billId = dataObject.get("billId").getAsString();
              businessLockRecordId = dataObject.get("businessLockRecordId").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //费用计算
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/discount/calculate";
          json="{\"settlementId\":\""+settlementId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              logger.boxLog(1, "费用计算接口返回成功", "费用计算成功");
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //根据结算单标识列表查询医保结算单信息
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/query/by_settlement_ids";
          json="{\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"settlementId\":\""+settlementId+"\",\"businessLockRecordId\":\""+businessLockRecordId+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String setlInsurReimDetailId = "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
        	  String content = test.getResponseContent();
              logger.boxLog(1, "获取医保结算单列表接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              setlInsurReimDetailId = dataObject.getAsJsonArray("settlementInsurReimDetailList").get(0).getAsJsonObject().get("setlInsurReimDetailId").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //保存医保预算结果
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/update/pre_settled";
          json="{\"billId\":\""+billId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"setlInsurReimDetailId\":\""+setlInsurReimDetailId+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "保存医保预算结果接口返回成功", content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //预算完成
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/pre_settled";
          json="{\"settlementId\":\""+settlementId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          double paymentAmount= 0;
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "预算完成接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              if(dataObject.getAsJsonArray("settlementList").size()==0) {
              	paymentAmount= 0;
              }else {
              	for(int i=0;i<dataObject.getAsJsonArray("settlementList").size();i++) {
              		paymentAmount = paymentAmount+dataObject.getAsJsonArray("settlementList").get(i).getAsJsonObject().get("settlementSelfPayingAmount").getAsDouble();
              	}
              }
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //保存结算单待收款信息
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_charge/save";
          json="{\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"billIdList\":[\""+billId+"\"],\"settlementIdList\":[\""+settlementId+"\"],\"chargeDueAmount\":"+paymentAmount+",\"paymentList\":[{\"paymentAmount\":"+paymentAmount+",\"medInstiPaymentMethodId\":\"109709972447852545\",\"medInstiInterfaceId\":null,\"medInstiPaymentMethodName\":\"现金\",\"paymentMethodConceptId\":\"399202041\"}],\"chargeSourceCode\":\"991418\",\"chargeSourceCptId\":\"399185053\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String chargeId = "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "保存结算单待收款信息接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              chargeId = dataObject.get("chargeId").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

        //根据结算单标识列表查询医保结算单信息
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/query/by_settlement_ids";
          json="{\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"settlementId\":\""+settlementId+"\",\"businessLockRecordId\":\""+businessLockRecordId+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
          	logger.boxLog(1, "获取医保结算单列表接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              setlInsurReimDetailId = dataObject.getAsJsonArray("settlementInsurReimDetailList").get(0).getAsJsonObject().get("setlInsurReimDetailId").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //保存医保正算结果
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement_insur_reim_detail/update/settled";
          json="{\"billId\":\""+billId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"setlInsurReimDetailId\":\""+setlInsurReimDetailId+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
              logger.boxLog(1, "保存医保正算结果接口返回成功",content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //正算完成
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/settled";
          json="{\"settlementId\":\""+settlementId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
              logger.boxLog(1, "正算完成接口返回成功", content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }
          

          //结算-根据收款标识获取支付明细信息
          url = "http://" + Data.host + "/finance-common/api/v1/app_finance_common/payment/query/outp_refund_fee";
          json="{\"chargeId\":\""+chargeId+"\",\"businessLockRecordId\":\""+businessLockRecordId+"\",\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"]}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          String paymentId = "";
          String chargeSourceCode = "";
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
              logger.boxLog(1, "根据收款标识获取支付明细信息接口返回成功", content);
              JsonParser parser = new JsonParser();
              JsonObject contentJson = parser.parse(content).getAsJsonObject();
              JsonObject dataObject = contentJson.getAsJsonObject("data");
              paymentId = dataObject.getAsJsonArray("paymentList").get(0).getAsJsonObject().get("paymentId").getAsString();
              chargeSourceCode = dataObject.get("chargeSourceCode").getAsString();
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //保存结算单确认支付结果
          url = "http://" + Data.host + "/finance-common/api/v1/app_finance_common/third_party_payment_trace/update/paid";
          json="{\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"],\"billId\":\""+billId+"\",\"paymentId\":\""+paymentId+"\",\"chargeSourceCode\":\""+chargeSourceCode+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
          	String content = test.getResponseContent();
              logger.boxLog(1, "保存结算单确认支付结果接口返回成功", content);
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //结算完成
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/settlement/update/finished";
          json="{\"settlementIdList\":[\""+settlementId+"\"],\"hospitalSOID\":\""+Data.hospital_soid+"\",\"ipAddress\":\"\",\"macAddress\":\"\",\"operatedBy\":\""+employeeId+"\",\"operateEmployeeNo\":\""+Data.default_user_login_account+"\",\"deptId\":\""+encDeptId+"\",\"soid\":[\""+Data.hospital_soid+"\"]}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              logger.boxLog(1, "结算完成接口返回成功", "结算完成");
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }


          //根据业务锁记录标识解锁业务锁
          url = "http://" + Data.host + "/finance-component/api/v1/app_pmts_finance/business_lock_record/update/by_business_lock_record_id";
          json="{\"unlockedBy\":\""+employeeId+"\",\"businessLockRecordId\":\""+businessLockRecordId+"\",\"unlockedMACAddress\":\"\",\"unlockedIPAddress\":\"\",\"unLockActionCode\":\"867790\",\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              logger.boxLog(1, "根据业务锁记录标识解锁业务锁接口返回成功", "解锁成功");
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
          }

          //register，挂号确认
          url = "http://" + Data.host + "/schedule-outpatient/api/v1/app_encounter_schedule/outp_number/register";
          json="{\"bizRoleId\":\""+bizRoleId+"\",\"checkNumberList\":[{\"bizResourceId\":\""+bizResourceId+"\",\"encounterId\":\""+encounterId+"\",\"checkId\":\""+encounterId+"\",\"identityTypeCode\":\"\",\"identityNo\":\"\",\"registerChargeList\":"+JSONObject.toJSONString(registerChargeList)+"}],\"hospitalSOID\":\""+Data.hospital_soid+"\"}";
          httpTestUrl = new HttpTestUrl(url);
          test = new HttpTest(httpTestUrl);
          System.out.println(json);
          test.sendPostRequest(json, null, header);
          test.waitRequestFinish(30000);
          ArrayList<String> syncInfo = new ArrayList<>();
          if (test.getResponseCode() == 200 && test.getResponseContent().contains("\"success\":true")) {
              int visitSeqNumber = scheduleNum+1;
              syncInfo.add(patientName);//患者姓名，用于挂号检索
              syncInfo.add(IDCode);
              syncInfo.add(omrn);//挂号序号，门诊病历号
              syncInfo.add(visitSeqNumber+"");//号序，用于叫号
              syncInfo.add(encounterId);
              syncInfo.add(employeeId);
              syncInfo.add(personId);
              logger.boxLog(1, "挂号确认接口返回成功", "挂号成功。患者姓名为："+ patientName+",挂号号序为："+visitSeqNumber+"\r"+syncInfo.toString());

              return syncInfo;
          } else {
              logger.assertFalse(true, "报错", "请求地址: " + url + "\n\n\n请求状态: " + test.getResponseCode() + "\n\n\n返回内容: " + test.getResponseContent());
              return null;
          }
	} catch (Exception e) {
        e.printStackTrace();
        logger.assertFalse(true, "大his系统挂号失败" + e.getMessage());
	}
	return null;
  }
	  
	  

    //天津his表tlsyzz
    public Map<String, String> getOrderTlsyzz(String zyh, String cfh_wn) {
        String sql = new HisSqlManager().getOrderTlsyzz(zyh,cfh_wn);
        Map<String, String> orderTlsyzz = db.queryFirstRow("his表：查询表tlsyzz中的数据",sql);
        return orderTlsyzz;
    }

    //天津his表tcqyz
    public Map<String, String> getOrderTcqyz(String zyh, String cfh_wn) {
        String sql = new HisSqlManager().getOrderTcqyz(zyh,cfh_wn);
        Map<String, String> orderTcqyz = db.queryFirstRow("his表：查询表tcqyz中的数据",sql);
        return orderTcqyz;
    }
    //天津his表cqzxmx
    public Map<String, String> getOrderCqzxmx(String zyh, String cfh_wn) {
        String sql = new HisSqlManager().getOrderCqzxmx(zyh,cfh_wn);
        Map<String, String> orderCqzxmx = db.queryFirstRow("his表：查询表cqzxmx中的数据",sql);
        return orderCqzxmx;
    }
    //天津his表kdmx
    public Map<String, String> getOrderKdmx(String zyh, String cfh_wn) {
        String sql = new HisSqlManager().getOrderKdmx(zyh,cfh_wn);
        Map<String, String> orderKdmx = db.queryFirstRow("his表：查询表kdmx中的数据",sql);
        return orderKdmx;
    }
    //天津his表ssmxk
    public Map<String, String> getOrderSsmxk(String zyh) {
        String sql = new HisSqlManager().getOrderSsmxk(zyh);
        Map<String, String> orderKdmx = db.queryFirstRow("his表：查询表ssmxk中的数据",sql);
        return orderKdmx;
    }
    //天津his表ssmxk
    public Map<String, String> getOrderyszzyhzjyd(String zyh) {
        String sql = new HisSqlManager().getOrderyszzyhzjyd(zyh);
        Map<String, String> orderyszzyhzjyd = db.queryFirstRow("his表：查询表yszzyhzjyd中的数据",sql);
        return orderyszzyhzjyd;
    }
    //天津his表bqcyff
    public Map<String, String> getOrderbqcyff(String zyh, String cfh_wn) {
        String sql = new HisSqlManager().getOrderbqcyff(zyh,cfh_wn);
        Map<String, String> orderbqcyff = db.queryFirstRow("his表：查询表bqcyff中的数据",sql);
        return orderbqcyff;
    }
    //天津his表tdjkz
    public Map<String, String> getInpatienttdjkz(String zyh) {
        String sql = new HisSqlManager().getInpatienttdjkz(zyh);
        Map<String, String> tdjkz = db.queryFirstRow("his表：查询表tdjkz中的数据",sql);
        return tdjkz;
    }
    
    
    /**
     * 5.x通过存储过程进行收费预结算
     * @return
     */
    //步骤一、5.X取出可以结算的患者
    public List<Map<String, String>> getNotSettlementPatient() {
    	List<Map<String, String>> res= new ArrayList<Map<String, String>>();
    	String sql = "select TOP 5 a.* from ZY_BRSYK a left join ZY_BRSYK_FZ b on a.syxh=b.syxh and a.patid = b.patid  left join YY_YBFLK c on a.ybdm=c.ybdm left join YY_KSBMK d on a.ksdm=d.id  where  a.bqdm in (select id from ZY_BQDMK a where jlzt = 0) and a.brzt in (1,5,6,7) and isnull(a.gzbz,0)=0";
    	res= db.queryAllRow("取出可以结算的患者", sql);
    	if(res.size()==0) {
    		logger.assertFalse(true, "未找到可以结算的患者");
    	}
    	return res;
    }
    //步骤二、5.X取出患者的预交金序号yjjxh
    public String getPatientAdvancePayment(String syxh) {
    	ArrayList<String> xhs = new ArrayList<>();
    	String sql ="select xh from ZYB_BRYJK where syxh="+syxh+" and jlzt=0 and czlb=0 ";
    	xhs= db.query("取出患者的预交金序号", sql, "xh");
    	if(xhs.size()==0) {
    		logger.assertFalse(true, "未找到预交金序号");
    	}
    	String yjjxh = "";
    	yjjxh = "'("+String.join(",",xhs)+")'";
    	return yjjxh;
    }
    
    //步骤三、5.X系统进行预结算
    public List<Map<String,Double>> preSettlement(String syxh,String yjjxh) {
    	List<List<String>>  list1 = new ArrayList<List<String>> ();
    	List<List<String>>  list2 = new ArrayList<List<String>> ();
    	String sql = "exec usp_zyb_brjs "+syxh+",2,0,'00',0,'1',0,'2','', @rqbz = 0,@jsrq = '',@mxxh='', @yjjxh = "+yjjxh;
    	list1=db.queryAllRowReturnList(sql);
    	if(list1.size()==0) {
    		logger.assertFalse(true, "5.X系统进行预结算失败");
    	}   
//    	取出返回的第一行数据，
    	List<String> info = list1.get(0);
//    	第二列=结账界面的总金额；
    	String zje = info.get(1);
//    	第五列=结账界面的预交金合计；
    	String yjjhj = info.get(4);//预交金合计
//    	ZY_BRJSK.srje=结账界面的舍入金额；
//    	第十三列=[结算序号]
    	String jsxh = info.get(12);//结算序号
    	
    	String sql2 = "exec usp_zyb_brjs_depzcl "+syxh+","+jsxh+",2,0,'' ";
    	System.out.println("======="+sql2);
//    	出参的第二列=结账界面的其中自付
    	String qzzf = "";//其中自付
    	list2 = db.queryAllRowReturnList(sql2);
    	
    	qzzf = list2.get(0).get(1);
    	double ytxj = 0; //应退现金
    	double ysje = 0; //应收金额
    	if(Double.valueOf(qzzf)-Double.valueOf(yjjhj)>=0) {
    		ytxj = 0;
    		ysje = Double.valueOf(qzzf)-Double.valueOf(yjjhj);
    	}else {
    		ytxj = Double.valueOf(yjjhj)-Double.valueOf(qzzf);
    		ysje = 0;
    	}
    
    	List<Map<String,Double>> costInfo = new ArrayList<Map<String,Double>>();
    	Map<String,Double> cost = new HashMap<String,Double>();
    	cost.put("ytxj", ytxj);//应退现金
    	cost.put("ysje", ysje);//应收金额
    	cost.put("qzzf", Double.valueOf(qzzf));
    	
    	costInfo.add(cost);
    	return costInfo;
    }
    
    //获取idm
	public String getIdm(String startDate,String endDate) {
		ArrayList<String> res = new ArrayList<String>();
		String sql = new HisSqlManager().getAllMedicineWINEXSql(startDate, endDate);
		res = db.query("查询西药 idm", sql, "idm");
		String[] str = new String[res.size()];
		res.toArray(str);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length-1; i++) {
			sb.append("'").append(str[i]).append("'").append(",");
		}
		sb.append("'").append(str[str.length-1]).append("'");
		System.out.println(sb.toString());
		return "("+sb.toString()+")";
	}
    
	//获取临床项目代码lcxmdm
	public String getLcxmdm(String startDate,String endDate) {
		ArrayList<String> res = new ArrayList<String>();
		String sql = new HisSqlManager().getNonMedicineWINEXSql(startDate, endDate);;
		res = db.query("查询非药品的lcxmdm", sql, "lcxmdm");
		String[] str = new String[res.size()];
		res.toArray(str);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length-1; i++) {
			sb.append("'").append(str[i]).append("'").append(",");
		}
		sb.append("'").append(str[str.length-1]).append("'");
		return "("+sb.toString()+")";
	}
	
	//获取测试内容
	public List<Map<String, String>> getServiceList_new(String tempPath, String delString, String notDelString) {
        List<Map<String, String>> allServiceList = new ArrayList<Map<String, String>>();
        // 数据库查询所有需要测试的医嘱
        try {
            if(Data.hisType.equals("WINEX")) {
            	String sql = new WnSqlManager().getAllMedicineInpatientSql();
            	sql = sql +" AND A.MED_EXT_REF_ID IN "+ getIdm(Data.clinicalSettlementStartDate,Data.clinicalSettlementEndDate);
                List<Map<String, String>> medicineList = db60.db.queryAllRow("查询所有药品商品", sql);
                System.out.println("sql"+sql);
//                List<Map<String, String>> medicineListValid = new ArrayList<Map<String, String>>();
                for (Map<String, String> medicine : medicineList) {
//                    medicineListValid.add(medicine);
//                    if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < medicineListValid.size()) {
//                        break;
//                    }
//                    else {
                        allServiceList.add(medicine);
//                    }
                }
            }

        }
        catch (Throwable e) {
            throw new Error("获取测试数据失败:" + e.getMessage());
        }
        logger.log("总服务个数:" + allServiceList.size());
        Data.serviceSize = allServiceList.size();
        // 解析temp文件, 去掉ID重复行 和 包含指定flag的行
        SdkTools.sortTemp(tempPath, delString, notDelString, "ID");
        // 获取需要跳过的ID列表
        List<String> skipIdList = new ArrayList<String>();
        List<Map<String, String>> skipServiceList = SdkTools.readServiceListFromTemp(tempPath, "ID");
        for (Map<String, String> service : skipServiceList) {
            skipIdList.add(service.get("ID"));
        }
        logger.log("跳过已测试:" + skipIdList.size());
        // 所有医嘱中去除需要跳过的ID得到最终的测试列表
        List<Map<String, String>> newServiceList = new ArrayList<Map<String, String>>();
        for (Map<String, String> service : allServiceList) {
            if (!skipIdList.contains(service.get("ID"))) {
                newServiceList.add(service);
            }
        }
        logger.log("本次测试服务," + newServiceList.size());
        return newServiceList;
    }
	
	//通过idm获取总金额zje
	public String getZjeByIdm(String idm) {
		String zje = "";
		String sql = "";
		sql = new HisSqlManager().getAllMedicineWINEXSql(Data.clinicalSettlementStartDate, Data.clinicalSettlementEndDate)+ " and a.idm="+idm;
		Map<String, String> data = db.queryFirstRow("", sql);
        if (data == null || !data.containsKey("zje")) {
            throw new Error("执行语句(" + sql + ")异常:" + data);
        }
        zje = data.get("zje");
		return zje;
	}
}

