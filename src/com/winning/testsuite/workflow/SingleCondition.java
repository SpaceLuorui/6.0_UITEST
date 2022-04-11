package com.winning.testsuite.workflow;

import com.google.gson.JsonArray;

import ui.sdk.config.Data;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SingleCondition implements Cloneable {

    Random ran=new Random();
    @Override
    public Object clone(){
        SingleCondition SC=null;
        try {
            SC=(SingleCondition)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return  SC;
    }

    //枚举条件的种类，根据条件种类生成条件对象
    public enum ConditionType {
        Route("970490","给药途径"),
        ORG("970480","业务单元") ,
        Doctor("970478","医生标识")  ,
        Age("970481","年龄分段") ,
        CS("970483","临床服务") ,
        CSType("970482","临床服务分类") ,
        MedType("970491","药理分类") ,
        VisitType("970477","就诊类型") ,
        Tech("970479","专业技术职务") ,
        DecocteMethod("970486","代煎方式") ,
        ExamItem("970494","检查项目标识"),
        Scene("DIY1","开立签署场景"),
        GroupDrug("DIY3","成组开立药品"),
        // 开立药品表示天数，开立项目表示数量
        Count_01("DIY3","第一条的天数或数量"),
//        Count_01_sy("DIY3","第一条的输液次数"),  //CL083开启时使用
        Count_02("DIY3","第二条的天数或数量"),   //可以与1成组的项目
//        Count_02_sy("DIY3","第二条的输液次数"),  //CL083开启时使用
        Count_03("DIY3","第三条的天数或数量"),
//        Count_03_sy("DIY3","第三条的输液次数"),//CL083开启时使用
        Count_04("DIY3","第四条的天数或数量");
//        Count_04_sy("DIY3","第四条的输液次数"),//CL083开启时使用
        // Parameter
//        Parameter_CL083("CL083","输液医嘱启用的参数");
//        SecondTimeFreQuency("DIY5","第二次开立频次");
        public   final String TypeCode;
        public  final String  TypeName;
        ConditionType(String TypeCode,String TypeName){
            this.TypeCode=TypeCode;this.TypeName=TypeName;
        }

    }

    //枚举计算方式
    public enum MatchCode{
        等于("390031993"),不等于("390031994"),包含("390031995"),不包含("390031996");
        public   final String Code;
        MatchCode(String code){
               this.Code=code;
        }
    }


    SdkDatabaseConn DB60_SingleCondition = new SdkDatabaseConn(Data.wn60DbType, Data.wn60DbHost, Data.wn60DbInstance, Data.wn60Dbname, Data.wn60DbService, Data.wn60DbUsername, Data.wn60DbPassword, new Log("SingleCondition"+".html"));

    //条件类型的值
    public String ConType_code;
    public String ConType_name;
    public String ConVal_code;
    public String  ConVal_no;
    public String  ConVal_desc;
    public Boolean Enable;   //是否有效
    public Integer RandomMark;   //取随机数，为非0时，条件处理器会取小于此数的随机值为有效值
    public ArrayList<String> ValueRange=new ArrayList<String>();   //可用范围
    public String ValueRange_sql="";
    public ArrayList<String> EffectiveRange=new ArrayList<String>();    //根据设定范围和可用范围，取出实际的有效范围，用来生成正向用例
    public ArrayList<String> UnEffectiveRange=new ArrayList<String>();    //根据设定范围和可用范围，取出实际的无效范围，用来生成反向用例

    //常规构造
    public SingleCondition(ConditionType CT){
        ConType_code= CT.TypeCode;
        ConType_name=CT.TypeName;
        Enable=true;
        ConVal_code="";
        ConVal_no="";
        ConVal_desc="";
        ValueRange=new ArrayList<String>();
        EffectiveRange=new ArrayList<String>();
        UnEffectiveRange=new ArrayList<String>();
        //给药途径设定可用范围
        if(CT.equals(ConditionType.Route))
        {
            ValueRange_sql="select distinct b.DEFAULT_USAGE_CODE  from WINDBA.MEDICINE a inner join WINDBA.CLINICAL_SERVICE_DRUG b on a.CS_ID=b.CS_ID \n" +
                    "  where a.IS_DEL=0 and b.IS_DEL=0  \n" +
                    "  and a.HOSPITAL_SOID='256181' \n" +
                    " and b.DEFAULT_USAGE_CODE in (select VALUE_ID from WINDBA.VALUE_SET where CODE_SYSTEM_ID=97829 and IS_DEL=0 ) group by b.DEFAULT_USAGE_CODE having count(*)>1 ";
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询给药途径的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                 ValueRange.add(Val.get("DEFAULT_USAGE_CODE"));
            }
        }

        //临床项目设定范围，根据LinkAgeType区分
        if(CT.equals(ConditionType.CS))
        {
            //一般都会重置
            ValueRange_sql="select top 1 * from WINDBA.CLINICAL_SERVICE where  IS_DEL=0";
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询临床服务的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                ValueRange.add(Val.get("CS_ID"));
            }
        }

        if(CT.equals(ConditionType.Age))
        {
            ValueRange_sql="select * from WINDBA.VALUE_SET where CODE_SYSTEM_ID=256812 and IS_DEL=0";
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询临床服务的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                ValueRange.add(Val.get("VALUE_ID"));
            }
        }

        if(CT.equals(ConditionType.DecocteMethod))
        {
            ValueRange_sql="select * from WINDBA.VALUE_SET where CODE_SYSTEM_ID=97872 and IS_DEL=0";
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询代煎方式的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                ValueRange.add(Val.get("VALUE_ID"));
            }
        }

        if(CT.equals(ConditionType.ExamItem))
        {
            ValueRange_sql="select a.* from WINDBA.EXAMINATION_ITEM a inner join WINDBA.CLINICAL_SERVICE b on a.CS_ID=b.CS_ID\n" +
                    "where  a.IS_DEL=0 and b.IS_DEL=0 and b.CS_STATUS=98360";
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询临床服务的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                ValueRange.add(Val.get("EXAM_ITEM_ID"));
            }
        }

//        if(CT.equals(ConditionType.Parameter_CL083))
//        {
//            ValueRange.add("0");
//            ValueRange.add("1");
//        }
    }

    //联动构造
    public SingleCondition(ConditionType CT, LinkAgeConditions.LinkAgeTypeEnum LinkAgeType){
        this(CT);
        //临床项目设定范围，根据LinkAgeType区分
        if(CT.equals(ConditionType.CS))
        {
            ValueRange.clear();
            if(LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Drug)) {
                ValueRange_sql = "select  a.* from WINDBA.MEDICINE  a inner join WINDBA.CLINICAL_SERVICE_MEDICATION b on a.MEDICINE_PRIMARY_NO=b.CADN_NO\n" +
                        "where a.IS_DEL=0 and b.IS_DEL=0 and a.COMMODITY_ENABLE_FLAG='98360'  \n" +
                        "and b.MEDICINE_TYPE_CODE in ('98363','98364') ";
            }
            else if(LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Herb)){
                ValueRange_sql = "select  a.* from WINDBA.MEDICINE  a inner join WINDBA.CLINICAL_SERVICE_MEDICATION b on a.MEDICINE_PRIMARY_NO=b.CADN_NO\n" +
                        "where a.IS_DEL=0 and b.IS_DEL=0 and a.COMMODITY_ENABLE_FLAG='98360'  \n" +
                        "and b.MEDICINE_TYPE_CODE='98365' ";
            }else if(LinkAgeType.equals(LinkAgeConditions.LinkAgeTypeEnum.Exam)){
                ValueRange_sql ="select * from WINDBA.EXAMINATION_ITEM WHERE IS_DEL=0";
            }
            List<Map<String, String>> ValueRange_sql_RES= DB60_SingleCondition.queryAllRow("查询临床服务的系统可用范围",ValueRange_sql);
            for(Map<String, String> Val:ValueRange_sql_RES)
            {
                ValueRange.add(Val.get("CS_ID"));
            }
        }
    }

    //根据传入的运算符和值集，设定有效值集和无效值集
    public void ConditionCalc(String MatchCode,ArrayList<String> SetRange){
        //没有设置则不处理各种范围
        if(SetRange.size() == 0)
            return;

           //等于和包含，生成
           if(MatchCode.equals(SingleCondition.MatchCode.等于.Code)||MatchCode.equals(SingleCondition.MatchCode.包含.Code))
           {
               UnEffectiveRange=ValueRange;
               for(String SetValue:SetRange){
                   EffectiveRange.add(SetValue);
                   if(UnEffectiveRange.contains(SetValue))
                        UnEffectiveRange.remove(SetValue);
//                   else
//                       Public.logger.throwError(true,"设定值不在系统范围内,请检查");
               }
           }
        if(MatchCode.equals(SingleCondition.MatchCode.不等于.Code)||MatchCode.equals(SingleCondition.MatchCode.不包含.Code))
        {
            EffectiveRange=ValueRange;
            for(String SetValue:SetRange){
                UnEffectiveRange.add(SetValue);
                if(EffectiveRange.contains(SetValue))
                    EffectiveRange.remove(SetValue);
//                else
//                    Public.logger.throwError(true,"设定值不在系统范围内,请检查");
            }
        }

    }

    //重载支持jsonarray
    public void ConditionCalc(String MatchCode, JsonArray SetRange_JA){
        ArrayList<String> SetRange=new ArrayList<String>();
        for (int i = 0; i < SetRange_JA.size(); i++) {
            SetRange.add(SetRange_JA.get(i).toString());
        }
        ConditionCalc(MatchCode,SetRange);
    }
}
