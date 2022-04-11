package com.winning.testsuite;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.winning.manager.WnSqlManager;
import com.winning.testsuite.workflow.*;
import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.testsuite.workflow.entity.SignoffDetail;
import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientTestBase;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;
import ui.sdk.util.SdkTools;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestLinkage extends OutpatientTestBase {

    public static Map<String, String> caseMap = new HashMap<String, String>();

    public TestLinkage() {
        super();
    }

    //是否执行用例，为false不执行只展示
    public static Boolean isrun = true;

    static {
        //控制Case结束后是否自动插入报告
        Framework.autoReport = false;
//        Data.headless = true;
        //Frmcons.savereportFile = Frmcons.savereportFile.replace("report.html", "TestLinkage.html");
        //初始化报告
//            Public.initReport("新版医嘱联动","TestLinkage.html");
        SdkTools.initReport_tc("新版医嘱联动", "TestLinkage.html");
    }

    Log l = new Log("TestLinkage" + ".html");
    SdkDatabaseConn DB_TestLinkage = new SdkDatabaseConn(Data.wn60DbType, Data.wn60DbHost, Data.wn60DbInstance, Data.wn60Dbname, Data.wn60DbService, Data.wn60DbUsername, Data.wn60DbPassword, l);

    //计算数量
    public Map<String, Object> getLinkAgeCalcCount(Map<String, Object> TC) {
        //先取用例的条件数据
        LinkAgeConditions PC = (LinkAgeConditions) TC.get("TC_Condition");

        String Scene = PC.C_Scene.ConVal_code;
        String Count_01 = PC.C_Count_01.ConVal_code;
        String Count_02 = PC.C_Count_02.ConVal_code;
        String Count_03 = PC.C_Count_03.ConVal_code;
        String Count_04 = PC.C_Count_04.ConVal_code;

        //再结合配置信息计算期望数据
        ArrayList<Map<String, String>> TC_Except = (ArrayList<Map<String, String>>) TC.get("TC_Except");
        if (TC_Except == null)
            return TC;
        ArrayList<Map<String, String>> TC_Expect_Calc = new ArrayList<Map<String, String>>();
        for (Map<String, String> TC_Except_detail : TC_Except) {
            //校验CS_ID
            String ExpectLinkAge_CSID = TC_Except_detail.get("ExpectLinkAge_CSID");
            String ExpectLinkAge_CSNAME = TC_Except_detail.get("ExpectLinkAge_CSNAME");
            //联动基数,四舍五入，保留两位小数
            Double ExpectLinkAge_Qty = Double.valueOf(TC_Except_detail.get("ExpectLinkAge_Qty"));
            BigDecimal bg = new BigDecimal(ExpectLinkAge_Qty);
            ExpectLinkAge_Qty=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //计算维度
            String ExpectRes_CalcDimension = TC_Except_detail.get("ExpectRes_CalcDimension");
            //计算规则
            String ExpectRes_CalcRuleCode = TC_Except_detail.get("ExpectRes_CalcRuleCode");
            //西药组外计算规则
            String ExpectRes_OutGroupCalcRuleCode = TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode");
            //计算方式
            String ExpectRes_CalcMethod = TC_Except_detail.get("ExpectRes_CalcMethod");
            //第一条的联动总数
            Double First = 0.00;
            //第二条的联动总数
            Double Second = 0.00;
            Double Third = 0.00;
            Double Fourth = 0.00;
            //最终联动数
            Double ALL = 0.00;
            Double OrderCount = 1.00;
            //先根据规则计算每次开立的数量
            if (ExpectRes_CalcMethod == null || ExpectRes_CalcMethod.equals("固定")) {
                //取固定的时候,默认频次BID
                First = ExpectLinkAge_Qty;
                Second = ExpectLinkAge_Qty;
                Third = ExpectLinkAge_Qty;
                Fourth = ExpectLinkAge_Qty;
            } else if (ExpectRes_CalcMethod.equals("按天*频次")) {
                //默认频次BID
                First = ExpectLinkAge_Qty * Double.valueOf(Count_01) * 2;
                Second = ExpectLinkAge_Qty * Double.valueOf(Count_02) * 2;
                Third = ExpectLinkAge_Qty * Double.valueOf(Count_03) * 2;
                Fourth = ExpectLinkAge_Qty * Double.valueOf(Count_04) * 2;
            } else if (ExpectRes_CalcMethod.equals("按天") || ExpectRes_CalcMethod.equals("按服务数量")) {
                //默认频次BID
                First = ExpectLinkAge_Qty * Double.valueOf(Count_01);
                Second = ExpectLinkAge_Qty * Double.valueOf(Count_02);
                Third = ExpectLinkAge_Qty * Double.valueOf(Count_03);
                Fourth = ExpectLinkAge_Qty * Double.valueOf(Count_04);
            } else if (ExpectRes_CalcMethod.equals("按剂数")) {
                First = ExpectLinkAge_Qty * Double.valueOf(Count_01);
                Second = ExpectLinkAge_Qty * Double.valueOf(Count_02);
                Third = ExpectLinkAge_Qty * Double.valueOf(Count_03);
                Fourth = ExpectLinkAge_Qty * Double.valueOf(Count_04);
            }
            else if (ExpectRes_CalcMethod.equals("按输液次数")) {
                //院内次数默认为天数*频次
                First = ExpectLinkAge_Qty * Double.valueOf(Count_01)*2;
                Second = ExpectLinkAge_Qty * Double.valueOf(Count_02)*2;
                Third = ExpectLinkAge_Qty * Double.valueOf(Count_03)*2;
                Fourth = ExpectLinkAge_Qty * Double.valueOf(Count_04)*2;
            }
            //如果是检查，忽略计算方式，全为1
            if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Exam)) {
                First = ExpectLinkAge_Qty * 1;
                Second = ExpectLinkAge_Qty * 1;
                Third = ExpectLinkAge_Qty * 1;
                Fourth = ExpectLinkAge_Qty * 1;
            }

            Double FirstAndSecond = 0.00;  //12的最终运算结果
            Double Group12 = 0.00;   //12成组时的成组结果，临时保存
            Double OrderCount12 = 0.00;  //12的联动医嘱数
            Double Sign1 = 0.00;    //第一次签署数量
            //西药计算
            if(PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Drug)){
                //先算1和2-成组
                if(Scene.contains("成组")){
                    if (ExpectRes_CalcRuleCode.equals("累计")) {
                        Group12 = First + Second;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_CalcRuleCode.equals("最高")) {
                        if (First > Second)
                            Group12 = First;
                        else
                            Group12 = Second;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_CalcRuleCode.equals("固定")) {
                        Group12 = ExpectLinkAge_Qty;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_CalcRuleCode.equals("不合并计算")) {
                        Group12 = First + Second;
                        OrderCount12 = 2.00;
                    }
                    FirstAndSecond = Group12;
                }
                //先算1和2-不成组
                else{
                    if (ExpectRes_OutGroupCalcRuleCode.equals("累计")) {
                        FirstAndSecond = First + Second;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_OutGroupCalcRuleCode.equals("最高")) {
                        if (First > Second)
                            FirstAndSecond = First;
                        else
                            FirstAndSecond = Second;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_OutGroupCalcRuleCode.equals("固定")) {
                        FirstAndSecond = ExpectLinkAge_Qty;
                        OrderCount12 = 1.00;
                    } else if (ExpectRes_OutGroupCalcRuleCode.equals("不合并计算")) {
                        FirstAndSecond = First + Second;
                        OrderCount12 = 2.00;
                    }

                }
                //再算总数-按签署
                if (ExpectRes_CalcDimension.equals("按签署")){
                    if (ExpectRes_OutGroupCalcRuleCode.equals("累计")) {
                            Sign1 = FirstAndSecond + Third;
                            ALL = Sign1 + Fourth;
                            OrderCount = 2.00;   //联动医嘱数，组外规则不是不合并，那么一次签署就是一条；用例设计为两次签署，那么就是两条
                        }
                    else if (ExpectRes_OutGroupCalcRuleCode.equals("最高")) {
                            if (FirstAndSecond > Third)
                                Sign1 = FirstAndSecond;
                            else Sign1 = Third;
                            //按签署取最高，但是总数上来算还是加起来
                            ALL = Sign1 + Fourth;
                            OrderCount = 2.00;   //联动医嘱数，组外规则不是不合并，那么一次签署就是一条；用例设计为两次签署，那么就是两条
                        }
                    else if (ExpectRes_OutGroupCalcRuleCode.equals("不合并计算")) {
                            Sign1 = FirstAndSecond + Third;
                            ALL = Sign1 + Fourth;
                            OrderCount = OrderCount12 + 2;
                        }
                    else if (ExpectRes_OutGroupCalcRuleCode.equals("固定")) {
                            Sign1 = FirstAndSecond;
                            ALL = Sign1*2;
                            OrderCount = 2.00;   //即使组内不合并，也会合并
                        }

                }
                //再算总数-按就诊
                else if (ExpectRes_CalcDimension.equals("按就诊")){
                    if (ExpectRes_OutGroupCalcRuleCode.equals("累计")) {
                        ALL = FirstAndSecond + Third + Fourth;
                        OrderCount = 2.00;   //即使组内不合并，也会合并
                    }
                    else if (ExpectRes_OutGroupCalcRuleCode.equals("最高")) {
                        if (FirstAndSecond > Third)
                            ALL = FirstAndSecond;
                        else
                            ALL = Third;
                        if (Fourth > ALL)
                            ALL = Fourth;
                        OrderCount = 1.00;   //即使组内不合并，也会合并
                    } else if (ExpectRes_OutGroupCalcRuleCode.equals("不合并计算")) {
                        ALL = FirstAndSecond + Third + Fourth;
                        OrderCount = OrderCount12 + 2;   //即使组内不合并，也会合并
                    } else if (ExpectRes_OutGroupCalcRuleCode.equals("固定")) {
                        ALL = Fourth;
                        OrderCount = 1.00;
                    }
                }
            }
            //中草药和检查计算
            if(!PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Drug)){
                //算总数-按签署
                if (ExpectRes_CalcDimension.equals("按签署")){
                    if (ExpectRes_CalcRuleCode.equals("累计")) {
                        ALL = First+Second + Third+Fourth;
                        OrderCount = 2.00;   //联动医嘱数，组外规则不是不合并，那么一次签署就是一条；用例设计为两次签署，那么就是两条
                    }
                    else if (ExpectRes_CalcRuleCode.equals("最高")) {
                        //先取1和2的最高
                        if(First>Second)
                            FirstAndSecond=First;
                        else
                            FirstAndSecond=Second;
                        //再与3取最高
                        if (FirstAndSecond > Third)
                            Sign1 = FirstAndSecond;
                        else Sign1 = Third;
                        //按签署取最高，但是总数上来算还是加起来
                        ALL = Sign1 + Fourth;
                        OrderCount = 2.00;   //联动医嘱数，组外规则不是不合并，那么一次签署就是一条；用例设计为两次签署，那么就是两条
                    }
                    else if (ExpectRes_CalcRuleCode.equals("不合并计算")) {
                        ALL = First+Second + Third+Fourth;
                        OrderCount = 4.00;
                    }
                    else if (ExpectRes_CalcRuleCode.equals("固定")) {
                        ALL = First*2;
                        OrderCount = 2.00;   //即使组内不合并，也会合并
                    }

                }
                //算总数-按就诊
                else if (ExpectRes_CalcDimension.equals("按就诊")){
                    if (ExpectRes_CalcRuleCode.equals("累计")) {
                        ALL = First+Second + Third + Fourth;
                        OrderCount = 2.00;   //即使组内不合并，也会合并
                    }
                    else if (ExpectRes_CalcRuleCode.equals("最高")) {
                        //先取1和2的最高
                        if(First>Second)
                            FirstAndSecond=First;
                        else
                            FirstAndSecond=Second;
                        //再与3取最高
                        if (FirstAndSecond > Third)
                            Sign1 = FirstAndSecond;
                        else Sign1 = Third;
                        //按就诊取最高，再比一次
                        if(Sign1>Fourth)
                            ALL=Sign1;
                        else
                            ALL=Fourth;
                        OrderCount = 2.00;   //联动医嘱数，组外规则不是不合并，那么一次签署就是一条；用例设计为两次签署，那么就是两条
                    }
                    else if (ExpectRes_CalcRuleCode.equals("不合并计算")) {
                        ALL = First+Second + Third + Fourth;
                        OrderCount = 4.00;   //即使组内不合并，也会合并
                    } else if (ExpectRes_CalcRuleCode.equals("固定")) {
                        ALL = Fourth;
                        OrderCount = 2.00;
                    }
                }
            }
            //取两位小数
            BigDecimal bg_all = new BigDecimal(ALL);
            ALL=bg_all.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //回传给用例
            TC_Except_detail.put("Expect_Calc_Count", ALL.toString());
            TC_Except_detail.put("Expect_Calc_OrderCount", OrderCount.toString());
        }
        return TC;
    }


    public ArrayList<Map<String, Object>> getCaseByDef(OutpatientBrowser browser, String Soid, LinkAgeConditions.LinkAgeTypeEnum LinkAgeType) throws Exception {

        //读取联动里的西药联动
        ArrayList<Map<String, Object>> TClist = new ArrayList<Map<String, Object>>();
        String ServiceType = "";
        if (LinkAgeType.equals(LinkAgeType.Drug))
            ServiceType = LinkAgeType.Drug.TypeName;
        else if (LinkAgeType.equals(LinkAgeType.Herb))
            ServiceType = LinkAgeType.Herb.TypeName;
        else if (LinkAgeType.equals(LinkAgeType.Exam))
            ServiceType = LinkAgeType.Exam.TypeName;

        String LinkAge_Drug_Value = browser.decouple.db60.getLinkAgeByType(LinkAgeType.TypeCode, Data.hospital_soid);
        JsonParser JP = new JsonParser();
        JsonArray LinkAge_Drug_JArray = JP.parse(LinkAge_Drug_Value).getAsJsonArray();
        for (int i = 0; i < LinkAge_Drug_JArray.size(); i++) {
//                  System.out.println(i);
            JsonObject LinkAge_Drug_Detail_JS = LinkAge_Drug_JArray.get(i).getAsJsonObject();
//                  System.out.println(LinkAge_Drug_Detail_JS.toString());
            //每条联动规则的名称
            String LinkAge_Drug_Detail_NAME = LinkAge_Drug_Detail_JS.getAsJsonObject().get("linkageRuleName").getAsString();
            //每条联动规则的状态
            Integer LinkAge_Drug_Detail_ENABLE = LinkAge_Drug_Detail_JS.getAsJsonObject().get("enabledFlag").getAsInt();
            //每条联动规则的删除标志
            Integer LinkAge_Drug_Detail_DEL = LinkAge_Drug_Detail_JS.getAsJsonObject().get("isDel").getAsInt();
            //soid
            Integer LinkAge_Drug_Detail_hospitalSOID = LinkAge_Drug_Detail_JS.getAsJsonObject().get("hospitalSOID").getAsInt();

            //过滤掉删除的和停用数据
            if ((LinkAge_Drug_Detail_ENABLE != 98360) || (LinkAge_Drug_Detail_DEL != 0))
                continue;

            //每条规则的联动项
            ArrayList<Map<String, String>> ExpectRes = new ArrayList<Map<String, String>>();

            //联动的内容
            JsonArray LinkAge_Drug_Detail_CSList = LinkAge_Drug_Detail_JS.get("linkageTargetCsDetailList").getAsJsonArray();
            for (JsonElement LinkAge_Drug_Detail_CS : LinkAge_Drug_Detail_CSList) {
                //联动项明细
                Map<String, String> ExpectRes_Detail = new HashMap<>();

                Integer ExpectRes_Detail_isDel = LinkAge_Drug_Detail_CS.getAsJsonObject().get("isDel").getAsInt();
                if (ExpectRes_Detail_isDel != 0)
                    continue;
                String ExpectRes_Detail_csId = LinkAge_Drug_Detail_CS.getAsJsonObject().get("csId").getAsString();
                String ExpectRes_Detail_csName = this.DB_TestLinkage.query(new WnSqlManager().getCsNameByCsId(ExpectRes_Detail_csId), "CS_NAME").get(0);

                Double ExpectRes_Detail_qty = LinkAge_Drug_Detail_CS.getAsJsonObject().get("calculationCardinal").getAsDouble();
                //计算维度
                Integer ExpectRes_CalcDimension_int = LinkAge_Drug_Detail_CS.getAsJsonObject().get("linkageCalcDimensionCode").getAsInt();
                String ExpectRes_CalcDimension = this.DB_TestLinkage.query(new WnSqlManager().getMdmDescByValueId(ExpectRes_CalcDimension_int.toString()), "VALUE_DESC").get(0);
                //计算规则-组外:linkageCalcRuleCode在西药算组内，在其它算普通，即组内
                Integer ExpectRes_CalcRuleCode_int = LinkAge_Drug_Detail_CS.getAsJsonObject().get("linkageCalcRuleCode").getAsInt();
                String ExpectRes_CalcRuleCode = this.DB_TestLinkage.query(new WnSqlManager().getMdmDescByValueId(ExpectRes_CalcRuleCode_int.toString()), "VALUE_DESC").get(0);
                //计算规则-组外,只有西药有组外计算规则
                String ExpectRes_OutGroupCalcRuleCode = "";
                if (LinkAgeType.equals(LinkAgeType.Drug)) {
                    Integer ExpectRes_OutGroupCalcRuleCode_int = LinkAge_Drug_Detail_CS.getAsJsonObject().get("linkageOrderGroupOuterCalcRuleCode").getAsInt();
                    ExpectRes_OutGroupCalcRuleCode = this.DB_TestLinkage.query(new WnSqlManager().getMdmDescByValueId(ExpectRes_OutGroupCalcRuleCode_int.toString()), "VALUE_DESC").get(0);
                }
                //计算方式,固定规则无需计算
                Integer ExpectRes_CalcMethod_int = null;
                String ExpectRes_CalcMethod = null;
                if (LinkAge_Drug_Detail_CS.getAsJsonObject().get("linkageCalcMethodCode") != null) {
                    ExpectRes_CalcMethod_int = LinkAge_Drug_Detail_CS.getAsJsonObject().get("linkageCalcMethodCode").getAsInt();
                    ExpectRes_CalcMethod = this.DB_TestLinkage.query(new WnSqlManager().getMdmDescByValueId(ExpectRes_CalcMethod_int.toString()), "VALUE_DESC").get(0);
                }

                //是否提醒
                Integer ExpectRes_promptedFlag_int = LinkAge_Drug_Detail_CS.getAsJsonObject().get("promptedFlag").getAsInt();
                String ExpectRes_promptedFlag = this.DB_TestLinkage.query(new WnSqlManager().getMdmDescByValueId(ExpectRes_promptedFlag_int.toString()), "VALUE_DESC").get(0);


                //联动项ID
                ExpectRes_Detail.put("ExpectLinkAge_CSID", ExpectRes_Detail_csId);
                ExpectRes_Detail.put("ExpectLinkAge_CSNAME", ExpectRes_Detail_csName);
                ExpectRes_Detail.put("ExpectLinkAge_Qty", ExpectRes_Detail_qty.toString());
                ExpectRes_Detail.put("ExpectRes_CalcDimension", ExpectRes_CalcDimension);
                ExpectRes_Detail.put("ExpectRes_CalcRuleCode", ExpectRes_CalcRuleCode);
                ExpectRes_Detail.put("ExpectRes_OutGroupCalcRuleCode", ExpectRes_OutGroupCalcRuleCode);
                ExpectRes_Detail.put("ExpectRes_CalcMethod", ExpectRes_CalcMethod);
                ExpectRes_Detail.put("ExpectRes_promptedFlag", ExpectRes_promptedFlag);
                ExpectRes.add(ExpectRes_Detail);

            }


            //用例用ArrayList保存当前规则的条件设置
            ArrayList<SingleCondition> ConditionAll = new ArrayList<SingleCondition>();
            //每条规则的联动条件，目前联动条件设置时是必有的
            JsonArray LinkAge_Drug_Detail_RuleList = LinkAge_Drug_Detail_JS.get("orderLinkageRuleDetailList").getAsJsonArray();
            for (int j = 0; j < LinkAge_Drug_Detail_RuleList.size(); j++) {
                JsonObject LinkAge_Drug_Detail_Rule = LinkAge_Drug_Detail_RuleList.get(j).getAsJsonObject();
                //联动条件-isDel
                Integer LinkAge_Drug_Detail_Rule_isDel = LinkAge_Drug_Detail_Rule.get("isDel").getAsInt();
                //过滤删除掉的数据
                if (LinkAge_Drug_Detail_Rule_isDel != 0)
                    continue;
                //联动条件-条件类型
                Integer linkageRuleConditionCode = LinkAge_Drug_Detail_Rule.get("linkageRuleConditionCode").getAsInt();

                //联动条件-匹配方式
                Integer linkageRuleMatchCode = LinkAge_Drug_Detail_Rule.get("linkageRuleMatchCode").getAsInt();
                //联动条件-匹配结果
                JsonArray linkageRuleMatchResultCodeList = LinkAge_Drug_Detail_Rule.get("linkageRuleMatchResultCodeList").getAsJsonArray();

                //根据解析生成单条件
                SingleCondition SC = null;
                //根据条件值生成对应条件
                for (SingleCondition.ConditionType CT : SingleCondition.ConditionType.values()) {
                    if (linkageRuleConditionCode.toString().equals(CT.TypeCode)) {
                        SC = new SingleCondition(CT, LinkAgeType);
                        break;
                    }
                }
                //根据计算方式计算值域
                SC.ConditionCalc(linkageRuleMatchCode.toString(), linkageRuleMatchResultCodeList);
                ConditionAll.add(SC);
            }

            //用条件处理器处理条件集
            ConditionProcessor AG = new ConditionProcessor();
            ArrayList<ConditionProcessor.SingleCaseConditionSet> Cons = AG.getCasesConditionSet(ConditionAll);

            //根据最大用例数量,和条件集和生成用例
            for (int tcxh = 0; tcxh < Cons.size(); tcxh++) {
                //用例
                Map<String, Object> TC = new HashMap<String, Object>();
                //用例名称
                String TypeName = "";
                if (ServiceType.equals(LinkAgeType.Drug.TypeName))
                    TypeName = "西成药联动";
                else if (ServiceType.equals(LinkAgeType.Herb.TypeName))
                    TypeName = "草药联动";
                else if (ServiceType.equals(LinkAgeType.Exam.TypeName))
                    TypeName = "检查联动";
                String TC_NAME = TypeName + ":" + LinkAge_Drug_Detail_NAME + "_" + String.valueOf(tcxh);

                LinkAgeConditions PC = new LinkAgeConditions(LinkAgeType);
                ConditionProcessor.SingleCaseConditionSet SS = Cons.get(tcxh);
                TC_NAME = TC_NAME + "(" + SS.ConditionSetType + ")";
                TC.put("TC_Name", TC_NAME);
                PC.UpdatePrescribeCondition(SS);
                TC.put("TC_Condition", PC);

                //反向用例的期望结果
                if (!SS.ConditionSetType.contains("正向")) {
                    ExpectRes = null;
                }

                TC.put("TC_Except", ExpectRes);
                TC = getLinkAgeCalcCount(TC);
                TClist.add(TC);
            }

        }
        return TClist;
    }

    /**
     * 关联参数： EX006，CL064**/
    @Test
    public void testLinkage() throws Exception {
        browser = new OutpatientBrowser("TestLinkage");
        //执行语句
        if (isrun) {
            browser.wnwd.openUrl(Data.web_url);
            browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
            browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
            browser.wnOutpatientWorkflow.skip();
            browser.wnOutpatientWorkflow.SetEX006_1(Data.host);//直接改为1
        }
        ArrayList<Map<String, Object>> TCList = new ArrayList<Map<String, Object>>();
        try {
            TCList = getCaseByDef(browser, Data.hospital_soid, LinkAgeConditions.LinkAgeTypeEnum.Drug);   //西药
              TCList.addAll(getCaseByDef(browser,Data.hospital_soid, LinkAgeConditions.LinkAgeTypeEnum.Herb));
              TCList.addAll(getCaseByDef(browser,Data.hospital_soid, LinkAgeConditions.LinkAgeTypeEnum.Exam));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }
        Integer CaseNo = 0;
        for (Map<String, Object> TC : TCList) {
            CaseNo = CaseNo + 1;
            //用例名
            String TC_name = TC.get("TC_Name").toString();
            caseMap.put("CASE_NAME", TC_name);
            //开始时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            caseMap.put("START", dateFormat.format(new Date()));
            //期望结果和实际结果
            String ARES = "";
            //患者年龄
            String Age = "";
            //开立药品名
            String Drug_name = "";
            String Drug_name_CSID = "";
            String Drug_medicineid = "";
            //条件_给药途径
            String Route = "";

            //用例条件解析
            LinkAgeConditions PC = (LinkAgeConditions) TC.get("TC_Condition");
            String ReportInfo = PC.getRepotFormat();


            //如果有不支持的情况直接跳过
            if (!PC.C_ExamItem.Enable || !PC.C_Doctor.Enable || !PC.C_CS.Enable) {
                caseMap.put("END", dateFormat.format(new Date()));
                caseMap.put("RESULT", "Unsupported");
                caseMap.put("STEPS", ReportInfo);
                caseMap.put("ERES", "此条跳过");
                caseMap.put("ARES", "此条跳过");
                caseMap.put("CLASS", "TABLEID errorCase");
                caseMap.put("ERRMSG", "此条跳过");
                caseMap.put("ONCLICK", "window.open('capture/" + browser.logger.fileName + "')");
                //单条用例写入报告
                SdkTools.saveCaseToReport_tc(Framework.savereportFile, caseMap);
                continue;
            }

            //设定挂号科室为指定的业务单元
            Data.newEncounterSubjectCode = PC.C_ORG.ConVal_no;

            //指定登录医生,要求密码为456，因为密码不可逆
            Data.default_user_login_account = PC.C_Doctor.ConVal_no;

            //设定患者年龄
            String AgeRange = PC.C_Age.ConVal_code;
            if (!AgeRange.equals("")) {
                Age = browser.decouple.db60.getBeginAgeBySet(AgeRange, Data.hospital_soid);
                Data.patientage = Integer.valueOf(Age);
            }
            //指定开立项
            if (!PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Exam)) {
                Drug_name = PC.C_CS.ConVal_desc;
                Drug_name_CSID = PC.C_CS.ConVal_code;
                Drug_medicineid = browser.decouple.db60.getMedicineIdbyCSID(Drug_name_CSID, Data.hospital_soid);
            } else {
                Drug_name = PC.C_ExamItem.ConVal_desc;
                Drug_name_CSID = PC.C_ExamItem.ConVal_code;
            }


            //用例执行
            ArrayList<String> encounterInfo = null;
            SignoffDetail Signinfo1 = null;   //用来保存第一次签署的UI信息
            try {
                encounterInfo = browser.decouple.newEncounter();
                if (isrun) {
                    browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
                    browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
                    browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
                    //西药正向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Drug) && TC_name.contains("正向")) {
                        //第一次签署开立，可成组时，1和2一起开
                        if (PC.C_Scene.ConVal_code.contains("成组")) {
                            //成组开第1和第2条医嘱
                            PrescribeDetail PrescribeDetailFirst = new PrescribeDetail();
                            PrescribeDetailFirst.groupMedicineName = new ArrayList<String>(Arrays.asList(PC.C_GroupDrug.ConVal_desc));
                            PrescribeDetailFirst.freq = "BID";
                            PrescribeDetailFirst.Days = Integer.valueOf(PC.C_Count_01.ConVal_code);
//                            PrescribeDetailFirst.InHospitalNum=Integer.valueOf(PC.C_Count_01_sy.ConVal_code); 院内次数只要更改就会从新按照新的方式计算，因此用默认院内次数
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFirst);
                            //单独开第三条医嘱
                            PrescribeDetail PrescribeDetailTHird = new PrescribeDetail();
                            PrescribeDetailTHird.freq = "BID";
                            PrescribeDetailTHird.Days = Integer.valueOf(PC.C_Count_03.ConVal_code);
//                            PrescribeDetailTHird.InHospitalNum=Integer.valueOf(PC.C_Count_03_sy.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailTHird);
                            //第一次签署
                            Signinfo1 = browser.wnOutpatientWorkflow.signOff(0);
                            //开立第四条医嘱
                            PrescribeDetail PrescribeDetailThourth = new PrescribeDetail();
                            PrescribeDetailThourth.freq = "BID";
                            PrescribeDetailThourth.Days = Integer.valueOf(PC.C_Count_04.ConVal_code);
//                            PrescribeDetailThourth.InHospitalNum=Integer.valueOf(PC.C_Count_04_sy.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailThourth);
                            //第二次签署
                            browser.wnOutpatientWorkflow.signOff(0);
                        }
                        //不成组
                        else {
                            PrescribeDetail PrescribeDetailFirst = new PrescribeDetail();
                            PrescribeDetailFirst.freq = "BID";
                            PrescribeDetailFirst.Days = Integer.valueOf(PC.C_Count_01.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFirst);
                            PrescribeDetail PrescribeDetailSecond = new PrescribeDetail();
                            PrescribeDetailSecond.freq = "BID";
                            PrescribeDetailSecond.Days = Integer.valueOf(PC.C_Count_02.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailSecond);
                            PrescribeDetail PrescribeDetailThird = new PrescribeDetail();
                            PrescribeDetailThird.freq = "BID";
                            PrescribeDetailThird.Days = Integer.valueOf(PC.C_Count_03.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailThird);
                            //第一次签署
                            Signinfo1=browser.wnOutpatientWorkflow.signOff(0);
                            PrescribeDetail PrescribeDetailThid = new PrescribeDetail();
                            PrescribeDetailThid.freq = "BID";
                            PrescribeDetailThid.Days = Integer.valueOf(PC.C_Count_03.ConVal_code);
                            browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailThid);
                            //第二次签署
                            browser.wnOutpatientWorkflow.signOff(0);
                        }
                    }
                    //西药反向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Drug) && TC_name.contains("反向")) {
                        PrescribeDetail PrescribeDetailFirst = new PrescribeDetail();
                        PrescribeDetailFirst.freq = "BID";
                        PrescribeDetailFirst.Days = Integer.valueOf(PC.C_Count_01.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFirst);
                        browser.wnOutpatientWorkflow.signOff(0);
                    }
                    //中草药正向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Herb) && TC_name.contains("正向")) {
                        //第一次开立
                        PrescribeDetail PrescribeDetailFirst = new PrescribeDetail();
                        PrescribeDetailFirst.decoct = true;
                        //代煎方式
                        if (!PC.C_DecocteMethod.ConVal_desc.equals(""))
                            PrescribeDetailFirst.decoctMethod = PC.C_DecocteMethod.ConVal_desc;
                        //剂量
                        PrescribeDetailFirst.num = Integer.valueOf(PC.C_Count_01.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFirst);
                        //第二次开立
                        PrescribeDetail PrescribeDetailSecond = new PrescribeDetail();
                        PrescribeDetailSecond.decoct = true;
                        //代煎方式
                        if (!PC.C_DecocteMethod.ConVal_desc.equals(""))
                            PrescribeDetailSecond.decoctMethod = PC.C_DecocteMethod.ConVal_desc;
                        //剂量
                        PrescribeDetailSecond.num = Integer.valueOf(PC.C_Count_02.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailSecond);
                        //第三次开立
                        PrescribeDetail PrescribeDetailThird = new PrescribeDetail();
                        PrescribeDetailThird.decoct = true;
                        //代煎方式
                        if (!PC.C_DecocteMethod.ConVal_desc.equals(""))
                            PrescribeDetailThird.decoctMethod = PC.C_DecocteMethod.ConVal_desc;
                        //剂量
                        PrescribeDetailThird.num = Integer.valueOf(PC.C_Count_03.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailThird);
                        browser.wnOutpatientWorkflow.signOff(0);
                        //第四次开立
                        PrescribeDetail PrescribeDetailFourth = new PrescribeDetail();
                        PrescribeDetailFourth.decoct = true;
                        //代煎方式
                        if (!PC.C_DecocteMethod.ConVal_desc.equals(""))
                            PrescribeDetailFourth.decoctMethod = PC.C_DecocteMethod.ConVal_desc;
                        //剂量
                        PrescribeDetailFourth.num = Integer.valueOf(PC.C_Count_03.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFourth);
                        Signinfo1=browser.wnOutpatientWorkflow.signOff(0);
                    }
                    //中草药反向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Herb) && TC_name.contains("反向")) {
                        //指定代煎方式
                        PrescribeDetail PrescribeDetailFirst = new PrescribeDetail();
                        PrescribeDetailFirst.decoct = true;
                        //代煎方式
                        if (!PC.C_DecocteMethod.ConVal_desc.equals(""))
                            PrescribeDetailFirst.decoctMethod = PC.C_DecocteMethod.ConVal_desc;
                        //剂量
                        PrescribeDetailFirst.num = Integer.valueOf(PC.C_Count_04.ConVal_code);
                        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, Drug_medicineid, PrescribeDetailFirst);
                        browser.wnOutpatientWorkflow.signOff(0);
                    }
                    //检查正向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Exam) && TC_name.contains("正向")) {
                        browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, Drug_name_CSID, new PrescribeDetail());
                        browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, Drug_name_CSID, new PrescribeDetail());
                        browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, Drug_name_CSID, new PrescribeDetail());
                        Signinfo1=browser.wnOutpatientWorkflow.signOff(0);
                        browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, Drug_name_CSID, new PrescribeDetail());
                        browser.wnOutpatientWorkflow.signOff(0);
                    }
                    //检查反向用例
                    if (PC.LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Exam) && TC_name.contains("反向")) {
                        browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, Drug_name_CSID, new PrescribeDetail());
                        browser.wnOutpatientWorkflow.signOff(0);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                ReportInfo = e.getMessage();
            } finally {
                //实际联动项
                List<Map<String, String>> Linkinfo = browser.decouple.db60.getLinkInfoByGhxh(encounterInfo.get(1), Data.hospital_soid);

                //校验期望结果
                ArrayList<Map<String, String>> TC_Except = (ArrayList<Map<String, String>>) TC.get("TC_Except");

                //单用例的测试结果，因为期望结果有若干个，所以用map保存
                Map<String, Boolean> TestRes = new HashMap<String, Boolean>();   //存放单项的测试结果
                Map<String, Boolean> TestRes_errormsg = new HashMap<String, Boolean>();   //存放校验失败的测试结果，用于展示
                ReportInfo = ReportInfo + "</br>";
                String ERES = "";
                if (!(TC_Except == null)){
                    for (Map<String, String> TC_Except_detail : TC_Except) {
                        //校验CS_ID
                        String ExpectLinkAge_CSID = TC_Except_detail.get("ExpectLinkAge_CSID");
                        String ExpectLinkAge_CSNAME = "联动项: " + TC_Except_detail.get("ExpectLinkAge_CSNAME") + "</br>";
                        String ExpectLinkAge_Qty = "联动基数: " + TC_Except_detail.get("ExpectLinkAge_Qty") + "</br>";
                        String ExpectRes_CalcDimension = "计算维度: " + TC_Except_detail.get("ExpectRes_CalcDimension") + "</br>";
                        String ExpectRes_CalcRuleCode = "计算规则: " + TC_Except_detail.get("ExpectRes_CalcRuleCode") + "</br>";
                        String Expect_Calc_Count = TC_Except_detail.get("Expect_Calc_Count");
                        String Expect_Calc_Count_report = "联动总数量: " + Expect_Calc_Count + "</br>";
                        //西药才有组外计算规则
                        String ExpectRes_OutGroupCalcRuleCode = "";
                        if (!TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty()) {
                            ExpectRes_OutGroupCalcRuleCode = "组外计算规则:" + TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode") + "</br>";
                        }

                        String ExpectRes_CalcMethod = "";
                        if (TC_Except_detail.get("ExpectRes_CalcMethod") != null)
                            ExpectRes_CalcMethod = "计算方式" + TC_Except_detail.get("ExpectRes_CalcMethod") + "</br>";

                        //是否提醒
                        String ExpectRes_promptedFlag = TC_Except_detail.get("ExpectRes_promptedFlag");
                        String ExpectRes_promptedFlag_report = "是否提醒：" + ExpectRes_promptedFlag + "</br>";

                        //联动医嘱数
                        String Expect_Calc_OrderCount = TC_Except_detail.get("Expect_Calc_OrderCount");
                        //如果UI展示为处置界面，医嘱数量则要重新计算,还要分按签署和按就诊来处理
                        if(ExpectRes_promptedFlag.equals("提醒并支持编辑（处置界面）")&&TC_Except_detail.get("ExpectRes_CalcDimension").contains("按签署")){
                            //西药组外规则为不合并计算时，医嘱项为3
                            if(!TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="3.0";
                            //西药组外规则为非不合并计算时，医嘱项为2
                            else if(!TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && !TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="3.0";
                            //中草药和检查,计算规则为不合并计算时时为3
                            else if(TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && TC_Except_detail.get("ExpectRes_CalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="3.0";
                            //中草药和检查,计算规则为非不合并计算时时为2
                            else if(TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && !TC_Except_detail.get("ExpectRes_CalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="2.0";
                        }
                        else if(ExpectRes_promptedFlag.equals("提醒并支持编辑（处置界面）")&&TC_Except_detail.get("ExpectRes_CalcDimension").contains("按就诊")){
                            //西药组外规则为不合并计算时，医嘱项为3
                            if(!TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="3.0";
                                //西药组外规则为非不合并计算时，医嘱项为1
                            else if(!TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && !TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="1.0";
                                //中草药和检查,计算规则为不合并计算时时为3
                            else if(TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && TC_Except_detail.get("ExpectRes_CalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="3.0";
                                //中草药和检查,计算规则为非不合并计算时时为1
                            else if(TC_Except_detail.get("ExpectRes_OutGroupCalcRuleCode").isEmpty() && !TC_Except_detail.get("ExpectRes_CalcRuleCode").contains("不合并计算"))
                                Expect_Calc_OrderCount="1.0";
                        }

                        String Expect_Calc_OrderCount_report = "联动医嘱总数量: " + Expect_Calc_OrderCount + "</br>";

                        ERES = ERES +"<pre>"+ ExpectLinkAge_CSNAME + ExpectLinkAge_Qty + ExpectRes_CalcDimension + ExpectRes_CalcRuleCode + ExpectRes_OutGroupCalcRuleCode + ExpectRes_CalcMethod + Expect_Calc_Count_report + Expect_Calc_OrderCount_report + ExpectRes_promptedFlag_report+"</pre>";
                        //默认不通过，找到才通过
                        boolean TestRes_Deail = false;
                        boolean TestRes_Qty = false;
                        boolean TestRes_OrderCount = false;
                        boolean TestRes_UI = false;
                        String ARES_temp = "";
                        Double LinkDetail_AllQTY_groupbyCSID = 0.00;
                        Double LinkDetail_OrderCount_groupbyCSID = 0.00;

                        //通过后台数据校验
                        for (Map<String, String> LinkDetail : Linkinfo) {
                            String Actual_CSID = LinkDetail.get("CS_ID").toString();
                            String Actral_PRESCRIBED_QTY = LinkDetail.get("PRESCRIBED_QTY").toString();
                            String Actual_CSNAME = LinkDetail.get("CLI_ORDER_ITEM_CONTENT");
                            //先校验联动项,并计算该项的总联动数
                            if (LinkDetail.get("CS_ID").toString().equals(ExpectLinkAge_CSID)) {
                                TestRes_Deail = true;
                                if (ARES_temp.equals(""))
                                    ARES_temp = ARES_temp +"<pre>"+ "实际联动项:" + Actual_CSNAME + "</br>";
                                LinkDetail_AllQTY_groupbyCSID = LinkDetail_AllQTY_groupbyCSID + Double.valueOf(Actral_PRESCRIBED_QTY);
                                LinkDetail_OrderCount_groupbyCSID = LinkDetail_OrderCount_groupbyCSID + 1;
                            }
                        }
                        //保留两位小数
                        BigDecimal bg_qty=new BigDecimal(LinkDetail_AllQTY_groupbyCSID);
                        LinkDetail_AllQTY_groupbyCSID=bg_qty.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

                        //UI校验,分为提醒和不提醒
                        if (ExpectRes_promptedFlag.equals("只提醒（弹窗）")||ExpectRes_promptedFlag.equals("提醒并支持编辑（弹窗）") ) {
                            //没有任何联动信息
                            if (Signinfo1==null||Signinfo1.LinkAgeInfo == null) {
                                TestRes_UI = false;
                                ARES_temp = ARES_temp + "实际UI是否提醒: 不提醒" + "</br>";
                            }
                            //有联动信息
                            else {
                                for (Map<String, String> ma : Signinfo1.LinkAgeInfo) {
                                    String LinkAgeName = ma.get("LinkAgeName");
                                    String LinkAgeEdited = ma.get("LinkAgeEdited");
                                    if(LinkAgeEdited.equals("false"))
                                        LinkAgeEdited="只提醒（弹窗）";
                                    else if(LinkAgeEdited.equals("true"))
                                        LinkAgeEdited="提醒并支持编辑（弹窗）";

                                    String LinkAgeCount = ma.get("LinkAgeCount");
                                    if (TC_Except_detail.get("ExpectLinkAge_CSNAME").equals(LinkAgeName)) {
                                        ARES_temp = ARES_temp + "实际UI是否提醒: " + LinkAgeEdited + "</br>";
                                        if (LinkAgeEdited.equals(ExpectRes_promptedFlag))
                                            TestRes_UI = true;
                                    break;   //找到对应联动信息即可，因为UI层面会分开显示，有多条数据，防止联动信息多次显示
                                    }
                                }
                            }
                        }
                        else if(ExpectRes_promptedFlag.equals("不提醒")||ExpectRes_promptedFlag.equals("提醒并支持编辑（处置界面）")){
                            //没有任何联动信息
                            if (Signinfo1==null||Signinfo1.LinkAgeInfo == null) {
                                TestRes_UI = true;
                                ARES_temp = ARES_temp + "实际UI是否提醒: 无联动弹窗" + "</br>";
                            } else {
                                TestRes_UI = true;
                                for (Map<String, String> ma : Signinfo1.LinkAgeInfo) {
                                    String LinkAgeName = ma.get("LinkAgeName");
                                    String LinkAgeEdited = ma.get("LinkAgeEdited");
                                    if(LinkAgeEdited.equals("false"))
                                        LinkAgeEdited="只提醒（弹窗）";
                                    else if(LinkAgeEdited.equals("true"))
                                        LinkAgeEdited="提醒并支持编辑（弹窗）";

                                    String LinkAgeCount = ma.get("LinkAgeCount");
                                    if (TC_Except_detail.get("ExpectLinkAge_CSNAME").equals(LinkAgeName)) {
                                        TestRes_UI = false;
                                        ARES_temp = ARES_temp + "实际UI是否提醒: " + LinkAgeEdited + "</br>";
                                        break;
                                    }
                                }

                                //展示结果，在遍历联动弹框之后判断，如果失败则不用判断
                                if(TestRes_UI)
                                ARES_temp = ARES_temp + "实际UI是否提醒: 无联动弹窗" + "</br>";
                            }
                        }

//                        DecimalFormat dfdouble = new DecimalFormat("0.00");
                        if (LinkDetail_AllQTY_groupbyCSID.toString().equals(Expect_Calc_Count))
                            TestRes_Qty = true;

                        if (LinkDetail_OrderCount_groupbyCSID.toString().equals(Expect_Calc_OrderCount))
                            TestRes_OrderCount = true;
                        //该项的实际联动总数
                        ARES_temp = ARES_temp + "实际联动总数量:" + LinkDetail_AllQTY_groupbyCSID + "</br>";
                        ARES_temp = ARES_temp + "实际联动医嘱总数量:" + LinkDetail_OrderCount_groupbyCSID + "</br>"+ "</br>"+ "</br>"+ "</br>"+ "</br>"+"</pre>";    //加4个换行，与期望结果的高度保持一致
                        ARES = ARES + ARES_temp;
                        ARES_temp = "";
                        //将单条验证放入集和
                        TestRes.put(ExpectLinkAge_CSNAME, TestRes_Deail);
                        TestRes.put(ExpectLinkAge_CSNAME + "_UI提醒", TestRes_UI);
                        TestRes.put(ExpectLinkAge_CSNAME + "_总数", TestRes_Qty);
                        TestRes.put(ExpectLinkAge_CSNAME + "_联动医嘱总数", TestRes_OrderCount);
                    }

                //HIS落库收费
                boolean TestRes_LKHIS=true;   //落库his
                try{
                    if(isrun)
                browser.decouple.win60MedicineSF(encounterInfo.get(0));}
                catch (Throwable e){
                    TestRes_LKHIS=false;
                }

                TestRes.put("落库到HIS并收费", TestRes_LKHIS);
                ARES=ARES+"落库到HIS并收费: "+String.valueOf(TestRes_LKHIS)+"</br>";
                }

                //最终校验
                Boolean TestRes_Assert = true;
                for (Map.Entry<String, Boolean> TR : TestRes.entrySet()) {
                    if (!TR.getValue().booleanValue()){
                        TestRes_Assert = false;
                       TestRes_errormsg.put(TR.getKey(),TR.getValue());}
                }

                caseMap.put("END", dateFormat.format(new Date()));
                caseMap.put("RESULT", TestRes_Assert.toString());
                if (TestRes_Assert)
                    caseMap.put("CLASS", "TABLEID passCase");
                else
                    caseMap.put("CLASS", "TABLEID failCase");

                caseMap.put("STEPS", ReportInfo);
                caseMap.put("ERES", ERES);
                caseMap.put("ARES", ARES);
                caseMap.put("ONCLICK", "window.open('capture/" + browser.logger.fileName + "')");

                if (TestRes_Assert)
                    caseMap.put("ERRMSG", "");
                else
                    caseMap.put("ERRMSG", TestRes_errormsg.toString());
                //单条用例写入报告
                SdkTools.saveCaseToReport_tc(Framework.savereportFile, caseMap);
                browser.logger.resetPath("Case-" + CaseNo.toString() + "TestLinkage.html");
            }
        }

    }

}

