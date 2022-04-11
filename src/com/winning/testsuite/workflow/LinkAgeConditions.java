package com.winning.testsuite.workflow;

import com.winning.manager.WnSqlManager;

import ui.sdk.config.Data;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;

import com.winning.testsuite.workflow.SingleCondition;
import com.winning.testsuite.workflow.SingleCondition.ConditionType;
import java.util.*;


/**条件构造，传入设置场景和正向单条件集，生成用例实现需要的条件集和
 * 1.场景枚举
 * 2.根据场景生成必要的条件集，并附默认值--对应的单条件，要支持场景重载
 *
 * **/
public class LinkAgeConditions {
    //联动类型
    public enum LinkAgeTypeEnum{
        Drug("399010220","Drug"),
        Herb("399010221","Herb"),
        Exam("399010222","Exam");

        public final String TypeCode;
        public final String TypeName;
        LinkAgeTypeEnum(String typecode, String typename){
            this.TypeCode=typecode;
            this.TypeName=typename;
        }
        public String getTypeName() {
            return TypeName;
        }

    }

    //联动显示方式枚举
    public enum LinkAgePromptedFlagEnum{
        不提醒("399283839","不提醒"),只提醒_弹窗("399283840","只提醒_弹窗"),提醒并支持编辑_弹窗("399283841","提醒并支持编辑_弹窗"),提醒并支持编辑_处置界面("399295427","提醒并支持编辑_处置界面");
        public final String ValueId;
        public final String ValueDesc;
        LinkAgePromptedFlagEnum(String ValueId,String Desc){
            this.ValueId=ValueId;
            this.ValueDesc=Desc;
        }

    }

    //开立条件类型代码
    SdkDatabaseConn DB_PrescribeCondition = new SdkDatabaseConn(Data.wn60DbType, Data.wn60DbHost, Data.wn60DbInstance, Data.wn60Dbname, Data.wn60DbService, Data.wn60DbUsername, Data.wn60DbPassword, new Log("PrescribeCondition"+".html"));

    //开立服务的服务类型, Durg,Herb,EXAM
    public LinkAgeTypeEnum LinkAgeType=null;

    //根据过滤条件查找需要开立的服务
    public String CSsql="";
    public static  String getGroupMarkbyRouteCode="select MED_GROUP_ALLOWED_FLAG from WINDBA.DOSAGE_ROUTE where DOSAGE_ROUTE_ID='?1'";
    /**根据CSID查找默认给药途径**/
    public static String getRouteCodebyCSID="select top 1 b.DEFAULT_USAGE_CODE, a.COMMODITY_NAME_CHINESE,a.CS_ID from WINDBA.MEDICINE a inner join WINDBA.CLINICAL_SERVICE_DRUG b on a.CS_ID=b.CS_ID \n" +
            "where a.IS_DEL=0 and b.IS_DEL=0  \n" +
            "and a.HOSPITAL_SOID='256181'  \n" +
            "and a.CS_ID='?1'";
    Random ran=new Random();
    //各个条件，根据需要来初始化必要条件
    public SingleCondition C_Route=null;
    public SingleCondition C_ORG=null;
    public SingleCondition C_Doctor=null;
    public SingleCondition C_Age=null;
    public SingleCondition C_CS=null;
    public SingleCondition C_CSType=null;
    public SingleCondition C_MedType=null;
    public SingleCondition C_VisitType=null;
    public SingleCondition C_Tech=null;
    public SingleCondition C_DecocteMethod=null;
    public SingleCondition C_ExamItem=null;
    public SingleCondition C_Scene=null;
    public SingleCondition C_GroupDrug=null;
    public SingleCondition C_Count_01=null;
//    public SingleCondition C_Count_01_sy=null;    //CL083开启时使用
    public SingleCondition C_Count_02=null;
//    public SingleCondition C_Count_02_sy=null;    //CL083开启时使用
    public SingleCondition C_Count_03=null;
//    public SingleCondition C_Count_03_sy=null;
    public SingleCondition C_Count_04=null;
//    public SingleCondition C_Count_04_sy=null;

    //构造时，给类型编码，类型描述，对应值和值描述给空
    public LinkAgeConditions(LinkAgeTypeEnum LinkAgeType){
            this.LinkAgeType=LinkAgeType;
            //初始化查找服务的sql
           if(LinkAgeType.equals(LinkAgeType.Drug)||LinkAgeType.equals(LinkAgeType.Herb))
               CSsql="select  a.COMMODITY_NAME_CHINESE,a.CS_ID  from WINDBA.MEDICINE a left join WINDBA.CLINICAL_SERVICE_DRUG b on a.CS_ID=b.CS_ID\n" +
                       "inner join WINDBA.MEDICINE_STOCK c on a.MEDICINE_ID=c.MEDICINE_ID\n" +
                       "inner join WINDBA.ORGANIZATION d on c.ORG_ID=d.ORG_ID\n" +
                       "inner join WINDBA.CLINICAL_SERVICE e on a.CS_ID=e.CS_ID\n" +
                       "inner join WINDBA.CLINICAL_SERVICE_MEDICATION f on f.CS_ID=a.CS_ID\n" +
                       "inner join WINDBA.CLINICAL_SERVICE g on a.CS_ID=g.CS_ID\n"+
                       "where a.IS_DEL=0 and b.IS_DEL=0 and c.IS_DEL=0 and d.IS_DEL=0 and COMMODITY_ENABLE_FLAG=98360 \n" +
                       "and c.MEDICINE_STOCK_QTY >10\n" +
                       "and a.HOSPITAL_SOID='256181'\n" +
                       "and d.ORG_NAME='门诊药房'";
           else
               CSsql="WINDBA.EXAMINATION_ITEM";

         C_Route=new SingleCondition(ConditionType.Route,LinkAgeType);
         C_ORG=new SingleCondition(ConditionType.ORG,LinkAgeType);
         C_Doctor=new SingleCondition(ConditionType.Doctor,LinkAgeType);
         C_Age=new SingleCondition(ConditionType.Age,LinkAgeType);
         C_CS=new SingleCondition(ConditionType.CS,LinkAgeType);
         C_CSType=new SingleCondition(ConditionType.CSType,LinkAgeType);
         C_MedType=new SingleCondition(ConditionType.MedType,LinkAgeType);
         C_VisitType=new SingleCondition(ConditionType.VisitType,LinkAgeType);
         C_Tech=new SingleCondition(ConditionType.Tech,LinkAgeType);
         C_DecocteMethod=new SingleCondition(ConditionType.DecocteMethod,LinkAgeType);
         C_ExamItem=new SingleCondition(ConditionType.ExamItem,LinkAgeType);
         C_Scene=new SingleCondition(ConditionType.Scene,LinkAgeType);
        C_GroupDrug=new SingleCondition(ConditionType.GroupDrug,LinkAgeType);
        C_Count_01=new SingleCondition(ConditionType.Count_01,LinkAgeType);
        C_Count_02=new SingleCondition(ConditionType.Count_02,LinkAgeType);
        C_Count_03=new SingleCondition(ConditionType.Count_03,LinkAgeType);
        C_Count_04=new SingleCondition(ConditionType.Count_04,LinkAgeType);
//        C_Count_01_sy=new SingleCondition(ConditionType.Count_01_sy,LinkAgeType);
//        C_Count_02_sy=new SingleCondition(ConditionType.Count_02_sy,LinkAgeType);
//        C_Count_03_sy=new SingleCondition(ConditionType.Count_03_sy,LinkAgeType);
//        C_Count_04_sy=new SingleCondition(ConditionType.Count_04_sy,LinkAgeType);
            //更新一下必要默认值
            SetDefaultPrescribeCondition(LinkAgeType);
    }

    //没有条件处理的给默认值，根据ServiceType不同分开处理
    public void SetDefaultPrescribeCondition(LinkAgeTypeEnum LinkAgeType){
        //给药途径，西药默认静滴，中药默认煎服
        if(C_Route.ConVal_code.equals(""))
        {
            if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
            {
                C_Route.ConVal_code="142433";
                C_Route.ConVal_desc="静滴";
            }
            else if(LinkAgeType.equals(LinkAgeTypeEnum.Herb))
            {
                C_Route.ConVal_code="142438";
                C_Route.ConVal_desc="煎服";
            }
        }

        //业务单元
        if(C_ORG.ConVal_code.equals("")) {
            C_ORG.ConVal_code="57397324403959808";
            C_ORG.ConVal_desc= "康复医学科";
            C_ORG.ConVal_no="2011";
        }

        //医生标识
        if(C_Doctor.ConVal_code.equals("")) {
            C_Doctor.ConVal_code="97425257871919104";
            C_Doctor.ConVal_desc="支持人员";
            C_Doctor.ConVal_no="6618";
        }

        //临床服务
        if(C_CS.ConVal_code.equals("")) {
            if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
            {
                C_CS.ConVal_code="4300215320";
                C_CS.ConVal_desc="（甲）20%甘露醇注射液";}
            else if(LinkAgeType.equals(LinkAgeTypeEnum.Herb)){
                C_CS.ConVal_code="4302686885";
                C_CS.ConVal_desc="[甲]桂枝";
            }
        }

        //检查项目
        if(C_ExamItem.ConVal_code.equals("")) {
            if(LinkAgeType.equals(LinkAgeTypeEnum.Exam))
            {
                C_ExamItem.ConVal_code="1";   //WINDBA.EXAMINATION_ITEM.EXAM_ITEM_ID
                C_ExamItem.ConVal_desc="骨密度测定";
            }
        }

        //就诊类型
        if(C_VisitType.ConVal_code.equals("")) {
            C_VisitType.ConVal_code="138138";
            C_VisitType.ConVal_desc="门诊";

        }

        //第一次开立数和院内数
        if(C_Count_01.ConVal_code.equals("")){
            Integer i=ran.nextInt(10);
            Integer i1=i+1;//防止为0
            Integer i2=ran.nextInt(i1*2);   //输液数量，小于天数*BID
            if(!LinkAgeType.equals(LinkAgeTypeEnum.Exam)){
               C_Count_01.ConVal_code=(i1).toString();
               //只有西药有输液
//               if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
//                  C_Count_01_sy.ConVal_code=(i2).toString();

            }
            else
                C_Count_01.ConVal_code="1";
        }
        //第二次开立数
        if(C_Count_02.ConVal_code.equals("")){
            Integer i=ran.nextInt(10);
            Integer i1=i+1;//防止为0
            Integer i2=ran.nextInt(i1*2);   //输液数量，小于天数
            if(!LinkAgeType.equals(LinkAgeTypeEnum.Exam)){
                C_Count_02.ConVal_code=(i1).toString();
                //只有西药有输液
//                if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
//                C_Count_02_sy.ConVal_code=(i2).toString();
            }
            else
                C_Count_02.ConVal_code="1";
        }
        //第三次开立数
        if(C_Count_03.ConVal_code.equals("")){
            Integer i=ran.nextInt(10);
            Integer i1=i+1;//防止为0
            Integer i2=ran.nextInt(i1*2);   //输液数量，小于天数
            if(!LinkAgeType.equals(LinkAgeTypeEnum.Exam)){
                C_Count_03.ConVal_code=(i1).toString();
//                if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
//                C_Count_03_sy.ConVal_code=(i2).toString();
            }
            else
                C_Count_03.ConVal_code="1";
        }
        //第二次开立天数,10以内随机
        if(C_Count_04.ConVal_code.equals("")){
            Integer i=ran.nextInt(10);
            Integer i1=i+1;//防止为0
            Integer i2=ran.nextInt(i1*2);   //输液数量，小于天数
            if(!LinkAgeType.equals(LinkAgeTypeEnum.Exam)){
                C_Count_04.ConVal_code=(i1).toString();
//                if(LinkAgeType.equals(LinkAgeTypeEnum.Drug))
//                C_Count_04_sy.ConVal_code=(i2).toString();
            }
            else
                C_Count_04.ConVal_code="1";
        }
    }

    //然后过滤不支持的情况，更改Enable标志
    public void UpdatePrescribeCondition(ConditionProcessor.SingleCaseConditionSet SS){

            ArrayList<SingleCondition> ConditionList=SS.ConditionList;
            //如果给药途径改了，那么开立的药品也要修改,变量保存是否修改
            Boolean Bz_RouteChange=false;
            Boolean Bz_MedType=false;
            Boolean Bz_CS=false;
            Boolean Bz_CSType=false;
            Boolean Bz_Doctor=false;
            Boolean Bz_Tech=false;
            Boolean Bz_Exam=false;
            //用来查找服务用到的过滤sql,原始sql在初始化时完成
            String sqlfilter="";
             for(SingleCondition ConditionInfo : ConditionList){
                 //更新给药途径
                 if (ConditionInfo.ConType_code.equals(ConditionType.Route.TypeCode)){
                     Bz_RouteChange=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_Route.ConVal_code=Val;
                     C_Route.ConVal_desc=ValDesc;
                     sqlfilter=sqlfilter+" and b.DEFAULT_USAGE_CODE='"+Val+"' ";
                 }
                 //更新登录科室
                 if (ConditionInfo.ConType_code.equals(ConditionType.ORG.TypeCode)){
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getOrgNameByOrgId(Val), "ORG_NAME").get(0);
                     String ValDesc_NO=DB_PrescribeCondition.query(new WnSqlManager().getOrgNameByOrgId(Val), "ORG_NO").get(0);
                     C_ORG.ConVal_code=Val;
                     C_ORG.ConVal_desc=ValDesc;
                     C_ORG.ConVal_no=ValDesc_NO;
                 }
                 //更新年龄段
                 if (ConditionInfo.ConType_code.equals(ConditionType.Age.TypeCode)){
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_Age.ConVal_code=Val;
                     C_Age.ConVal_desc=ValDesc;
                 }
                 //更新临床服务分类
                 if (ConditionInfo.ConType_code.equals(ConditionType.CSType.TypeCode)){
                     Bz_CSType=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_CSType.ConVal_code=Val;
                     C_CSType.ConVal_desc=ValDesc;
                     sqlfilter=sqlfilter+" and g.CS_TYPE_CODE='"+Val+"' ";
                 }
                 //更新药理分类代码分类
                 if (ConditionInfo.ConType_code.equals(ConditionType.MedType.TypeCode)){
                     Bz_MedType=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_MedType.ConVal_code=Val;
                     C_MedType.ConVal_desc=ValDesc;
                     sqlfilter=sqlfilter+" and f.PHARMACOLOGY_CLASS_CODE='"+Val+"' ";
                 }
                 //更新就诊类型代码
                 if (ConditionInfo.ConType_code.equals(ConditionType.VisitType.TypeCode)){
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_VisitType.ConVal_code=Val;
                     C_VisitType.ConVal_desc=ValDesc;
                 }
                 //更新医生标识
                 if (ConditionInfo.ConType_code.equals(ConditionType.Doctor.TypeCode)){
                     Bz_Doctor=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getEmployeeNameByEmployeeId(Val), "EMPLOYEE_NAME").get(0);
                     String ValDesc_no=DB_PrescribeCondition.query(new WnSqlManager().getEmployeeNameByEmployeeId(Val), "EMPLOYEE_NO").get(0);
                     C_Doctor.ConVal_code=Val;
                     C_Doctor.ConVal_desc=ValDesc;
                     C_Doctor.ConVal_no=ValDesc_no;
                 }
                 //专业技术职务代码
                 if (ConditionInfo.ConType_code.equals(ConditionType.Tech.TypeCode)){
                     Bz_Tech=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_Tech.ConVal_code=Val;
                     C_Tech.ConVal_desc=ValDesc;
                 }
                 //更新临床服务
                 if (ConditionInfo.ConType_code.equals(ConditionType.CS.TypeCode)){
                     Bz_CS=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getCsNameByCsId(Val), "CS_NAME").get(0);
                     C_CS.ConVal_code=Val;
                     C_CS.ConVal_desc=ValDesc;
                 }
                 //更新代煎方式
                 if (ConditionInfo.ConType_code.equals(ConditionType.DecocteMethod.TypeCode)){
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(Val), "VALUE_DESC").get(0);
                     C_DecocteMethod.ConVal_code=Val;
                     C_DecocteMethod.ConVal_desc=ValDesc;
                 }
                 //更新检查项目标识
                 if (ConditionInfo.ConType_code.equals(ConditionType.ExamItem.TypeCode)){
                     Bz_Exam=true;
                     String Val=ConditionInfo.ConVal_code;
                     String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getExamItemNameByExamItemId(Val), "EXAM_ITEM_NAME").get(0);
                     C_ExamItem.ConVal_code=Val;
                     C_ExamItem.ConVal_desc=ValDesc;
                 }
             }

             //如果更新了给药途径且没有设置临床服务,则根据给药途径找临床服务，第一条用于临床服务，第二条用于成组药品;因为成组不能用同样的药品

                //此条件用于找成组医嘱，只有正向用例才考虑成组
            if(Bz_RouteChange&&!Bz_CS&&SS.ConditionSetType.contains("正向")){
                List<Map<String,String>> CSinfo=DB_PrescribeCondition.queryAllRow("更新临床服务",CSsql+sqlfilter);
                if(CSinfo.size()>1){
                String Val_csname= CSinfo.get(1).get("COMMODITY_NAME_CHINESE");
                String Val_cs=CSinfo.get(1).get("CS_ID");
                    C_GroupDrug.ConVal_code=Val_cs;
                    C_GroupDrug.ConVal_desc=Val_csname;
                    //成组开立，第二条就和第一条用一样的数据,输液医嘱也用同样的数据
                    C_Count_02.ConVal_code=C_Count_01.ConVal_code;
                    C_Count_02.ConVal_desc=C_Count_01.ConVal_desc;
//                    C_Count_01_sy.ConVal_code=C_Count_01.ConVal_code;
//                    C_Count_02_sy.ConVal_code=C_Count_01.ConVal_code;
                }
                else
                {
                    C_GroupDrug.Enable=false;
                    C_GroupDrug.ConVal_code="没有根据条件找到可成组的药品";
                    C_GroupDrug.ConVal_desc="没有根据条件找到可成组的药品"+CSsql+sqlfilter;
                }

            }


             //如果CS没有更新，并且更新了其它的范围，则要主动更新CS
             if((Bz_RouteChange|Bz_CSType|Bz_MedType)&!Bz_CS){
                 List<Map<String,String>> CSinfo=DB_PrescribeCondition.queryAllRow("更新临床服务",CSsql+sqlfilter);
                 if(CSinfo.size()>0){
                 String Val_csname= CSinfo.get(0).get("COMMODITY_NAME_CHINESE");
                 String Val_cs=CSinfo.get(0).get("CS_ID");
                     C_CS.ConVal_code=Val_cs;
                     C_CS.ConVal_desc=Val_csname;
                 }
                 else
                 {
                     C_CS.Enable=false;
                     C_CS.ConVal_code="没有根据条件找到可以开立的药品";
                     C_CS.ConVal_desc="没有根据条件找到可以开立的药品"+CSsql+sqlfilter;
                 }

             }

             //如果设置了CS，则给药途径给置空
             if(Bz_CS){
                 getRouteCodebyCSID=getRouteCodebyCSID.replace("?1",C_CS.ConVal_code.toString());
                 Map<String,String> RouteCodebyCSID=DB_PrescribeCondition.queryFirstRow("根据CSID查找默认给药途径",getRouteCodebyCSID);
                 if(!RouteCodebyCSID.isEmpty()){
                 C_Route.ConVal_code=RouteCodebyCSID.get("DEFAULT_USAGE_CODE");
                 String ValDesc=DB_PrescribeCondition.query(new WnSqlManager().getMdmDescByValueId(C_Route.ConVal_code), "VALUE_DESC").get(0);
                 C_Route.ConVal_desc=ValDesc;
                 }
                 else
                 {C_Route.ConVal_code="设置了临床项目，没有找到默认给药途径";
                     C_Route.ConVal_desc="设置了临床项目，没有找到默认给药途径";
                 }

             }
             //如果更新了专业技术职务，且没有更新医生，则要主动更新医生
             if(!Bz_Doctor&Bz_Tech){
                 Map<String,String> DocInfo=new HashMap<>();
                 String sqldoc="select EMPLOYEE_ID,EMPLOYEE_NO,EMPLOYEE_NAME from WINDBA.EMPLOYEE_INFO where IS_DEL=0 and EMPLOYMENT_STATUS=152437 ";
                 sqldoc=sqldoc+" AND EXPERTISE_CODE='"+C_Tech.ConVal_code+"'";
                 //不是6618的职务再更换用例
                 if(!C_Tech.ConVal_code.equals("101739")) {
                     DocInfo = DB_PrescribeCondition.queryFirstRow("根据技术职务查找医生", sqldoc);
                     String Ccode = DocInfo.get("EMPLOYEE_ID");
                     String Cdesc = DocInfo.get("EMPLOYEE_NAME");
                     String Cno = DocInfo.get("EMPLOYEE_NO");
                     if (!(Ccode == null)) {
                         C_Doctor.ConVal_code=Ccode;
                         C_Doctor.ConVal_desc=Cdesc;
                         C_Doctor.ConVal_no=Cno;
                     } else {
                         C_Doctor.Enable=false;
                         C_Doctor.ConVal_desc="根据技术职务" + C_Tech.ConVal_desc+ "没有到找医生(" + sqldoc + ")";
                         C_Doctor.ConVal_no="";
                     }
                 }
             }
             //如果检查类设置了CS，则清除默认的检查项目
             if(Bz_CS&&(LinkAgeType.equals(LinkAgeType.Exam)))
             {
                 if(!Bz_Exam){
                     C_ExamItem.ConVal_code="";
                     C_ExamItem.ConVal_desc="";}
                 else
                 {
                     C_ExamItem.Enable=false;
                     C_ExamItem.ConVal_code="不能同时设置临床服务和检查项目";
                     C_ExamItem.ConVal_desc="不能同时设置临床服务和检查项目";
                 }
             }

             //正向用例，根据给药途径判断是否可以成组,反向条件集无需成组，
             if(SS.ConditionSetType.contains("正向")) {
                  if(this.LinkAgeType.equals(LinkAgeTypeEnum.Drug)){
                 getGroupMarkbyRouteCode = getGroupMarkbyRouteCode.replace("?1", C_Route.ConVal_code);
                 Map<String, String> GroupMark = DB_PrescribeCondition.queryFirstRow("根据给药途径查找成组标志", getGroupMarkbyRouteCode);
                 if (GroupMark != null) {
                     String GroupMarkVal = GroupMark.get("MED_GROUP_ALLOWED_FLAG");
                     if (GroupMarkVal.equals("98175"))   //可以成组
                     {
                         C_Scene.ConVal_code = "正向场景:第一次签署医嘱1,2,3;其中1和2成组;第二次签署医嘱4";
                         C_Scene.ConVal_desc = "正向场景:第一次签署医嘱1,2,3;其中1和2成组;第二次签署医嘱4";
                     }
                 }
                  }
                  else
                  {
                      C_Scene.ConVal_code="正向场景:第一次签署医嘱1,2,3;第二次签署医嘱4";
                      C_Scene.ConVal_desc="正向场景:第一次签署医嘱1,2,3;第二次签署医嘱4";
                  }
             }
             else{
                 C_Scene.ConVal_code = "反向场景:只需开立签署医嘱1";
                 C_Scene.ConVal_desc = "反向场景:只需开立签署医嘱1";
             }
             //过滤可用范围，更新Enable标志,供用例层使用
             FilterPrescribeCondition();
    }

    //过滤当前自动化可以支持的逻辑条件，将不可支持的修改Val_desc
    public void FilterPrescribeCondition(){
        //就诊类型
        if(!C_VisitType.ConVal_code.equals("138138")){
            C_VisitType.Enable=false;
            C_VisitType.ConVal_desc="目前只支持门诊,不支持"+C_VisitType.ConVal_desc;
       }

    }

    //用于格式化输出报告,为空的条件不展示，必要条件都是一定有值的
    public String getRepotFormat(){
        String html="";
        if(C_ORG.ConVal_desc != "")
            html=html+C_ORG.ConType_name+":"+C_ORG.ConVal_desc+"</br>";
        if(C_Doctor.ConVal_desc != "")
            html=html+C_Doctor.ConType_name+":"+C_Doctor.ConVal_no+"</br>";
        if(C_Age.ConVal_desc != "")
            html=html+C_Age.ConType_name+":"+C_Age.ConVal_desc+"</br>";
        if(C_Scene.ConVal_desc != "")
            html=html+C_Scene.ConVal_desc+"</br>";
        if((C_Route.ConVal_desc != "")&&this.LinkAgeType.equals(LinkAgeTypeEnum.Drug))
            html=html+C_Route.ConType_name+":"+C_Route.ConVal_desc+"</br>";
        if(C_CS.ConVal_desc != "")
            html=html+C_CS.ConType_name+":"+C_CS.ConVal_desc+"</br>";
        if(C_GroupDrug.ConVal_desc != "")
            html=html+C_GroupDrug.ConType_name+":"+C_GroupDrug.ConVal_desc+"</br>";
        //第一条正反用例，成组不成组都需要显示
        if(C_Count_01.ConVal_code != ""){
//            if(C_Count_01_sy.ConVal_code!="")
//               html=html+C_Count_01.ConType_name+":"+C_Count_01.ConVal_code+", "+C_Count_01_sy.ConType_name+":"+C_Count_01_sy.ConVal_code+"</br>";
//            else
               html=html+C_Count_01.ConType_name+":"+C_Count_01.ConVal_code+"</br>";}
        //医嘱2的数量和输液数量
        if(C_Count_02.ConVal_code != ""&&C_Scene.ConVal_code.contains("正向场景")){
//            if(C_Count_02_sy.ConVal_code!="")
//                html=html+C_Count_02.ConType_name+":"+C_Count_02.ConVal_code+", "+C_Count_02_sy.ConType_name+":"+C_Count_02_sy.ConVal_code+"</br>";
//            else
                html=html+C_Count_02.ConType_name+":"+C_Count_02.ConVal_code+"</br>";
        }
        //医嘱3的数量和输液数量信息
        if(C_Count_03.ConVal_code != ""&&C_Scene.ConVal_code.contains("正向场景"))
        {
//            if(C_Count_03_sy.ConVal_code!="")
//                html=html+C_Count_03.ConType_name+":"+C_Count_03.ConVal_code+", "+C_Count_03_sy.ConType_name+":"+C_Count_03_sy.ConVal_code+"</br>";
//            else
                html=html+C_Count_03.ConType_name+":"+C_Count_03.ConVal_code+"</br>";
        }
        if(C_Count_04.ConVal_code != ""&&C_Scene.ConVal_code.contains("正向场景"))
        {
//            if(C_Count_04_sy.ConVal_code!="")
//                html=html+C_Count_04.ConType_name+":"+C_Count_04.ConVal_code+", "+C_Count_04_sy.ConType_name+":"+C_Count_04_sy.ConVal_code+"</br>";
//            else
                html=html+C_Count_04.ConType_name+":"+C_Count_04.ConVal_code+"</br>";
        }
        if(this.LinkAgeType.equals(LinkAgeTypeEnum.Drug))
            html=html+"频次均为:BID</br>";
        if(C_CSType.ConVal_desc != "")
            html=html+C_CSType.ConType_name+":"+C_CSType.ConVal_desc+"</br>";
        if(C_MedType.ConVal_desc != "")
            html=html+C_MedType.ConType_name+":"+C_MedType.ConVal_desc+"</br>";
        if(C_VisitType.ConVal_desc != "")
            html=html+C_VisitType.ConType_name+":"+C_VisitType.ConVal_desc+"</br>";
        if(C_Tech.ConVal_desc != "")
            html=html+C_Tech.ConType_name+":"+C_Tech.ConVal_desc+"</br>";
        if(C_DecocteMethod.ConVal_desc != "")
            html=html+C_DecocteMethod.ConType_name+":"+C_DecocteMethod.ConVal_desc+"</br>";
        if(C_ExamItem.ConVal_desc != "")
            html=html+C_ExamItem.ConType_name+":"+C_ExamItem.ConVal_desc+"</br>";
        return html;
    }

}
