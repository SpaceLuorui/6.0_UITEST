package ui.sdk.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.sdk.constant.Framework;


public class Data {
	
	/**
	 * 框架参数，必须包含
	 */
    public static String host = "172.16.7.171";//测试服务器ip
    
    public static String web_url = "http://" + host + "/portal/login";//登录地址

    //混合框架配置
    public static Boolean useHybirdApp = false;//是否使用混合框架，false代表使用浏览器模式，true代表使用混合框架
    public static String hybirdAppPath = Framework.userDir + Framework.fileSeparator + "Win60.exe";//混合框架路径，useHybirdApp为true时有效
    public static String hybirdAppPort = "9222";//混合框架启动调试端口

    //是否读取从文件中读取患者挂号数据
    public static Boolean getEncounterFromFile = false;//默认为false，表示从HIS挂号，设置为true则从文件中读取患者信息

    // 报告中图片在本地获取
    public static String pictureSrc = "";
    
    //是否判断错误提示的同时判断警告提示
    public static boolean checkWarning = false;
    
    // 是否截图
    public static Boolean getScreenShot = true;

    //浏览器的无界面状态
    public static Boolean headless = true;

    // 记录全医嘱条数
    public static Integer serviceSize = 0;
    
    // 记录全医嘱开立是否测试完成
    public static Boolean testOver = false;
    
    // 忽略错误信息
    public static String ignoreErrors = "";
    public static List<String> ignoreList = new ArrayList<String>();
    public static String specialErrorType = "60无搜索结果&&计费策略&&加工厂默认值错误&&签署后2分钟未落库&&His收费失败&&His退费失败";
    public static Boolean closeBrowser = true;
    
    //抓取到的错误消息列表
    public static ArrayList<String> errMsgList = new ArrayList<>();
    
    /*
     * 参数配置的模式:release、develop。默认是release模式
     * release模式是现场模式，参数不符合要求情况下需要手动修改，develop模式是公司测试模式，自动化脚本会自动改配置
     */
    public static String modeConfiguration = "develop";
    
    /**
     * 业务参数，依据业务配置需要存放
     */
    //通用参数
    public static String suiteName = "com.winning.testsuite.smoke";
    public static String fhir_port = "/fhir";//解耦端口
    public static String hospital_soid = "256181";//医院ID
    public static String hisType = "JTWN";//his类型，默认是公司的HIS，即WN;山西卫宁为SXWN
    public static String hisServiceHost = "http://172.16.6.31:8080";//浙江卫宁挂号使用
    public static String hisQyUrl = "http://172.16.0.196:8080";//第三方接口服务地址，提供挂号、收费使用（如天津卫宁挂号收费、上海同济挂号"http://192.168.53.126:8118"）
    public static String Hospital_OgName_name="default";
    //his数据库配置
    public static String hisDbType = "sqlserver";//数据库类型
    public static String hisHost = "172.16.7.50";//ip
    public static String hisInstance = "None";//实例名
    public static String hisDbname = "THIS4_RC";//数据库名
    public static String hisDbService = "ORCL";//服务名，当数据库为oracle时需要
    public static String hisUsername = "THIS4_RC";//用户名
    public static String hisPassword = "Winning@THIS_RC";//密码

    //60数据库配置（7.171）
    public static String wn60DbType = "sqlserver";//数据库类型
    public static String wn60DbHost = "172.16.7.50";
    public static String wn60DbInstance = "None";
    public static String wn60Dbname = "win60_pdb1";
    public static String wn60DbService = "ORCL";//服务名，当数据库为oracle时需要
    public static String wn60DbUsername = "WINDBA";
    public static String wn60DbPassword = "sql2K!^RCAW";

    //xxl-job 地址
    public static String xxljob_host = "172.16.7.40";
    //xxl-job HIS药房库存同步
    public static String drugStorageStockSyn = "46";
    //xxl-job 药品库存同步Redis
    public static String medicineStockSyncRedis = "50";
    //xxl-job 同步库存
    public static String CsMedicineExecStorageSyncHandler = "35";

    //挂号科室代码
    public static String newEncounterSubjectCode = "2011";
    //集团卫宁挂号的患者年龄,默认-1为随机年龄,为0时为当天出生的婴儿患者，不满一岁
    public static Integer patientage = -1;
    //集团卫宁挂号医保代码，默认为1，171环境里的自费医保
    public static String ybdm = "1";
    //挂号医生id(山西卫宁)
    public static String sxwn_doctorid = "3972";
    //挂号医生id(天津卫宁)
    public static String tjwn_doctorid = "1297";
    //医生站帐号
    public static String default_user_login_account = "6618";
    //医生站密码
    public static String default_user_login_pwd = "456";
    
    //抗菌药测试账号
    public static String testAntibacterialsUserLoginAccount = "8888";
    //抗菌药测试账号对应密码
    public static String testAntibacterialsUserLoginPwd = "abcd@1234";
    
    //科目
    public static String test_select_subject = "康复医学科";
    //转诊科室
    public static String test_select_subject2 = "肾脏风湿科";
    //症状
    public static String test_chief_complain = "疼痛";
    //诊断
    public static String test_disease = "衣原体性尿道炎";
    //切换诊断
    public static String test_disease2 = "动脉硬化";
    //诊断3
    public static String test_disease3 = "高血压";
    //西药名称
    public static String test_prescribe_drug = "维生素 B2 片";
    //西药规格
    public static String test_prescribe_drug_pack = "片/瓶";
    //中药名称
    public static String test_prescribe_herb = "党参";
    //检验名称
    public static String test_prescribe_lab = "随机血糖测定";
    //检查名称
    public static String test_prescribe_exam = "常规心脏超声检查";
    //治疗名称
    public static String test_prescribe_treat = "雾化吸入";
    //病理名称
    public static String test_prescribe_Pathology = "手术标本检查与诊断";
    //病历模板名称
    public static String emrTemplateName = "测试另存为1";
    //处置专项模板名称
    public static String DispositionTemplateName = "处置专项模板开立";
    //处置模板保存的所属范围
    public static String DispositionTemplateType = "个人";
    public static String DispositionTemplateTypeRadio ="//span[.='科室']/../span[1]";

    /**
     * 历史引用用例常用开立内容
     */
    //西药
    public static String outpatientDisaposalDrug = "氯雷他定片（开瑞坦）";
    //西药规格
    public static String outpatientDisaposalDrugSpecification = "10mg*6粒/盒";
    //西药剂量
    public static String outpatientDisaposalDrugDose = "10mg";
    //西药数量和零售包装单位
    public static String outpatientDisaposalDrugNumberUnit = "1盒";
    //中草药
    public static String outpatientDisaposalHerbal = "[甲]白及";
    //中草药剂量
    public static String outpatientDisaposalHerbalDose = "1克";
    //中草药数量和零售包装单位
    public static String outpatientDisaposalHerbalNumberUnit = "1包";
    //中成药
    public static String outpatientDisaposalCnPatentMedicine = "（乙10%）双黄连口服液";
    //中成药规格
    public static String outpatientDisaposalCnPatentMedicineSpecification = "10ml*12支/盒";
    //中成药剂量
    public static String outpatientDisaposalCnPatentMedicineDose = "10ml";
    //中成药数量和零售包装单位
    public static String outpatientDisaposalCnPatentMedicineNumberUnit = "1盒";
    //电话号码
    public static String telephoneNumber = "13712345678";
    //工作单位
    public static String workplace = "卫宁健康";
    //医生建议
    public static String advise = "注意休息";


    //收费保存医嘱SJH
    public static List<Map<String, String>> SaveHisSfList = new ArrayList<Map<String, String>>();
    //处置开立专项使用配置(默认为false)
    public static boolean addSaveHisSfList = false;
    //无库存默认开立自备药配置(默认为false)
    public static boolean outOfStockFlag = false;
    
    //推荐区域测试时间
    public static Integer recommendTestSeconds = 100;
    //处置区域测试时间
    public static Integer disposalTestSeconds = 100;
    //病例区域测试时间
    public static Integer emrTestSeconds = 100;

    //历史处置开立界面选择科室名称
    public static String historcalSubjectName = "康复医学科";
    //历史处置开立开始日期
    public static String historcalStartDate = "2020-04-05";
    //历史处置开立结束日期
    public static String historcalEndDate = "2020-05-05";
    // 历史处置最大测试数，-1代表测试上述时间范围内所有历史医嘱项
    public static Integer allHistoralTestMaxNo = -1;
    // 是否测试药品
    public static Boolean testMedicineFlag = true;
    // 是否测试检查项目
    public static Boolean testExamItemFlag = true;
    // 是否测试检验
    public static Boolean testLabFlag = true;
    // 是否测试病理
    public static Boolean testPathologyFlag = true;
    // 是否测试治疗
    public static Boolean testTreatFlag = true;
    // 全医嘱中各类医嘱最大测试数，-1代表测试上述时间范围内所有历史医嘱项
    public static Integer allServiceTestMaxNo = -1;
    public static Boolean checkFactory = false;

    // 线程数量
    public static Integer threadNum = 4;

    // 重新测试标识
    public static String retryFlag = "None";
    
    public static String notRetryFlag = "None";

    public static String orderTemplateName = "自动化测试医嘱模板";

    public static int printTimes = 5;//重复打印次数
    
    public static int clientNum = 2;//重复打开客户端数量

    
    
    // 是否测试西药导诊单
    public static Boolean drugGuideFlag = true;
    // 是否测试检查导诊单
    public static Boolean examGuideFlag = true;
    // 是否测试检验导诊单
    public static Boolean labGuideFlag = true;
    // 是否测试中草药导诊单
    public static Boolean herbGuideFlag = true;
    // 是否测试治疗导诊单
    public static Boolean treatGuideFlag = true;

    //导诊单模板打印配置
    public static String guideSheetReportConfig="检查患者姓名=是,检查临床诊断=否,检查科室=否,检查西药名称=是,检查中草药名称=是,检查检查名称=是,检查检验名称=是,检查治疗名称=是,检查剂量=否,检查给药途径=是,检查频次=是,检查费用=是,检查执行科室=是,检查标本=是,检查药房=是";
    //西成药处方单模板打印配置
    public static String drugRecipeReportConfig="检查患者姓名=是,检查临床诊断=否,检查科室=否,检查西药名称=是,检查剂量=否,检查给药途径=是,检查频次=是,检查费用=是,检查药房=否";
    //中草药处方单模板打印配置
    public static String herbRecipeReportConfig="检查患者姓名=是,检查临床诊断=否,检查科室=是,检查中草药名称=是,检查给药途径=是,检查费用=是,检查药房=是";
    //检查申请单模板打印配置
    public static String examOrderReportConfig="检查患者姓名=是,检查临床诊断=是,检查科室=是,检查检查名称=是,检查费用=是,检查执行科室=否";
    //检验申请单模板打印配置
    public static String labTestOrderReportConfig="检查患者姓名=是,检查临床诊断=否,检查科室=是,检查检验名称=是,检查执行科室=是,检查费用=是,检查标本=是";
    //治疗申请单模板打印配置
    public static String treatmentOrderReportConfig="检查患者姓名=是,检查临床诊断=是,检查科室=是,检查执行科室=是,检查治疗名称=是,检查费用=是";
    //请假单模板打印配置
    public static String sickLeaveReportConfig="检查患者姓名=是,检查临床诊断=是,检查电话号码=是,检查工作地点=是,检查建议说明=是,检查开始日期=是";
    //疾病证明单打印配置
    public static String diseaseCertificateReportConfig="检查患者姓名=是,检查电话号码=是,检查工作地点=是,检查建议说明=是";
    //病历打印配置
    public static String emrTemplatePrintConfig="检查患者姓名=是,检查临床诊断=是,检查科室=是";
    
    //五官科流程变量
    //科室代码
    public static String wuGuan_subjCode = "3205";
    //科室名称
    public static String wuGuan_subjName = "五官科";
    //症状1
    public static String wuGuan_symptom1 = "耳鸣";
    //症状1推荐的两个诊断
    public static String wuGuan_recommend_diagnose1 = "中耳炎";
    public static String wuGuan_recommend_diagnose2 = "耳鸣";
    //症状2
    public static String wuGuan_symptom2 = "鼻塞";
    //症状2推荐的两个
    public static String wuGuan_recommend_diagnose3 = "鼻炎";
    public static String wuGuan_recommend_diagnose4 = "鼻窦炎";
    //诊断
    public static String wuGuan_diagnose = "鼻炎";
    //诊断推荐的查体
    public static String wuGuan_recommend_physicalSign1 = "鼻腔异常分泌物";
    public static String wuGuan_recommend_physicalSign2 = "鼻出血";
    public static String wuGuan_recommend_physicalSign3 = "咽";
    public static String wuGuan_recommend_physicalSign4 = "嗅觉";
    public static String wuGuan_recommend_physicalSign5 = "耳鸣";
    //慢性鼻炎流程
    public static String wuGuan_mxby_symptom = "鼻塞";
    public static String wuGuan_mxby_diagnose = "慢性鼻炎";
    public static String wuGuan_mxby_drug = "（乙10%）盐酸氮卓斯汀鼻喷雾剂（敏奇）";
    public static String wuGuan_mxby_treat = "鼻腔冲洗";
    //外耳道炎流程
    public static String wuGuan_wedy_symptom = "耳痒";
    public static String wuGuan_wedy_diagnose = "外耳道炎";
    public static String wuGuan_wedy_drug1 = "注射用阿莫西林钠克拉维酸钾";
    public static String wuGuan_wedy_drug2 = "地塞米松磷酸钠注射液";
    public static String wuGuan_wedy_drug3 = "硫酸庆大霉素注射液";
    public static String wuGuan_wedy_treat1 = "耳廓假性囊肿穿刺压迫治疗";
    public static String wuGuan_wedy_treat2 = "电耳镜检查";
    //结膜炎流程2
    public static String wuGuan_jmy_symptom = "眼分泌物异常";
    public static String wuGuan_jmy_diagnose = "结膜炎";
    public static String wuGuan_jmy_drug1 = "（甲）阿昔洛韦滴眼液";
    public static String wuGuan_jmy_drug2 = "●（乙10%）复方托吡卡胺滴眼液";
    public static String wuGuan_jmy_treat1 = "角膜厚度检查(裂隙灯法)";


    //妇科流程变量
    //科室代码
    public static String fuKe_subjCode = "2003";
    public static String fuKe_subjName = "妇科";
    public static String chanKe_subjCode = "2000";
    public static String chanKe_subjName = "产科";
    //产科推荐主诉/诊断
    public static String chanKe_symptom = "呕吐";
    public static String chanKe_recommend_diagnose1 = "妊娠状态";
    public static String chanKe_recommend_diagnose2 = "甲状腺疾病";
    //产科推荐查体
    public static String chanKe_diagnose = "阴道炎";
    public static String chanKe_recommend_physicalSign1 = "宫颈糜烂";
    public static String chanKe_recommend_physicalSign2 = "宫颈出血";
    //产科诊疗路径诊断
    public static String chanKe_symptom2 = "呕吐";
    public static String chanKe_diagnose2 = "糖尿病";
    //妇科常规流程
    public static String chanKe_fkcglc_symptom = "异常白带";
    public static String chanKe_fkcglc_diagnose = "急性阴道炎";
    public static String chanKe_fkcglc_drug = "盐酸克林霉素棕榈酸酯分散片";
    public static String chanKe_fkcglc_herb = "当归";
    public static String chanKe_fkcglc_lab = "人乳头瘤病毒(HPV)核酸检测";
    public static String chanKe_fkcglc_pathology = "液基薄层细胞制片术";
    //早孕检查
    public static String chanKe_zyjc_symptom = "孕妇12周,早孕检查";
    public static String chanKe_zyjc_diagnose = "孕12周";
    public static String chanKe_zyjc_drug = "（乙）复合维生素";
    public static String chanKe_zyjc_drug2 = "▲●（乙）阿仑膦酸钠维D3片";
    public static String chanKe_zyjc_lab = "人乳头瘤病毒(HPV)核酸检测";
    public static String chanKe_zyjc_lab2 = "尿液分析";
    //孕前检查流程
    public static String chanKe_yqjc_symptom = "婚后两月，要求孕前检查";
    public static String chanKe_yqjc_diagnose = "避孕问题";
    public static String chanKe_yqjc_exam = "产前检查";
    //辅助生育流程
    public static String chanKe_fzsy_symptom = "婚后3年，未避孕2年";
    public static String chanKe_fzsy_diagnose = "女性不孕症";
    public static String chanKe_fzsy_drug = "来曲唑片";
    public static String chanKe_fzsy_exam = "产前检查";


    //儿科流程变量
    //科室代码
    public static String pediatrics_subjCode = "2002";
    //科室名称
    public static String pediatrics_subjName = "儿科";
    //症状1
    public static String pediatrics_symptom1 = "发热";
    //症状1推荐的两个诊断
    public static String pediatrics_recommend_diagnose1 = "感染性发热";
    //诊断
    public static String pediatrics_diagnose = "感染性发热";
    //诊断推荐的查体
    public static String pediatrics_recommend_physicalSign1 = "浅表淋巴结肿大";
    public static String pediatrics_recommend_physicalSign2 = "呼吸运动";
    //诊断推荐的检验
    public static String pediatrics_recommend_lab1 = "C-反应蛋白（CRP）";
    //诊断推荐的检查
    public static String pediatrics_recommend_exam1 = "胸部正位";
    public static String pediatrics_recommend_exam2 = "CT检查";
    //急性上呼吸道感染流程
    public static String pediatrics_jxshxdgr_symptom = "发热";
    public static String pediatrics_jxshxdgr_diagnose = "急性上呼吸道感染";
    public static String pediatrics_jxshxdgr_drug1 = "抗感颗粒";
    public static String pediatrics_jxshxdgr_drug2 = "小儿复方氨基酸注射液";
    public static String pediatrics_jxshxdgr_drug3 = "布洛芬混悬液";
    public static String pediatrics_jxshxdgr_lab1 = "血常规+CRP";
    public static String pediatrics_jxshxdgr_lab2 = "C反应蛋白";
    //急性扁桃体炎流程
    public static String pediatrics_jxbtty_symptom = "发热";
    public static String pediatrics_jxbtty_diagnose = "急性扁桃体炎";
    public static String pediatrics_jxbtty_drug1 = "布洛芬混悬液";
    public static String pediatrics_jxbtty_drug2 = "阿莫西林克拉维酸钾分散片";
    public static String pediatrics_jxbtty_drug3 = "蓝芩口服液（浓缩型）";
    public static String pediatrics_jxbtty_lab1 = "血常规";
    public static String pediatrics_jxbtty_lab2 = "尿液分析";
    //黄疸流程
    public static String pediatrics_hd_symptom = "黄疸";
    public static String pediatrics_hd_diagnose = "新生儿黄疸";
    ;
    public static String pediatrics_hd_treat1 = "新生儿经皮胆红素测定";


    //中医科流程变量
    //科室代码
    public static String TCM_subjCode = "2030";
    //科室名称
    public static String TCM_subjName = "中医科";
    //症状1
    public static String TCM_symptom1 = "疼痛";
    //诊断
    public static String TCM_diagnose = "腰痛病";

    //针灸流程
    public static String TCM_zj_symptom = "疼痛";
    public static String TCM_zj_diagnose = "肝火旺盛证";
    public static String TCM_zj_treat1 = "雷火灸";
    public static String TCM_zj_treat2 = "肩周炎推拿";
    public static String TCM_zj_treat3 = "拔罐";

    //服务类型术语(CS_TYPE_CODE)
    public static String drugCode = "98095";
    public static String drugCode2 = "98096";
    public static String herbCode = "98097";
    public static String examCode = "98078";
    public static String labCode = "98071";
    public static String treatCode = "98098";
    public static String pathologyCode = "98088";

    //临床域-医生权限设置变量
    public static String NoPermissionAccount = "wjtest";
    public static String Med_Drug = "雷贝拉唑钠肠溶胶囊"; //西药
    public static String Med_Chinesepatentmedicine = "达立通颗粒";   //中成药
    public static String Med_Herb = "白及"; //中草药
    public static String Med_SpiritOneMed = "盐酸二甲双胍片";    //精一药品
    public static String Med_SpiritTwoMed = "地西泮注射液";    //精二药品
    public static String Med_NarcoticDrug = "（甲）磷酸可待因片";    //麻醉药品
    public static String Med_HighlyToxic = "注射用A型肉毒毒素";    //剧毒药品
    public static String Med_RadioPharmaceuticals = "呋塞米片";    //放射性药品
    public static String Med_HighPriced = "复合维生素B片";    //高价药,目前仅(上海五期实时医保)有效
    public static String Med_Antibacterials_Unrestricted = "头孢克洛缓释片";   //非限制级抗菌素
    public static String Med_Antibacterials_Restricted = "硫酸庆大霉素注射液";   //限制级抗菌素
    public static String Med_Antibacterials_Special = "美罗培南";   //特殊级抗菌素
    //记录当前是否在收费中
    public static Boolean inCharging = false;

    //权限枚举
    public static enum PermissonType{
        Drug("256702","西药开立权限"),
        CPM("256703","中成药开立权限"),
        Herb("256704","中草药开立权限"),
        SpiritOne("256705","第一类精神药品开立权限"),
        SpiritTwo("256706","精二类精神药品开立权限"),
        NarcoticDrug("256707","麻醉药品开立权限"),
        HighlyToxic("256708","剧毒药品开立权限"),
        RadioPharmaceuticals("256709","放射性药品开立权限"),
        HighPriced("399217887","高价药开立权限"),
        GZ("399283812","挂账权限"),
        Antibacterials_Unrestricted("384040","非限制级"),
        Antibacterials_Restricted("384041","限制级"),
        Antibacterials_Special("384042","特殊级");
        public final String Code;
        public final String Desc;
        PermissonType(String PermissionCode,String PermissionDesc){
            this.Code=PermissionCode;
            this.Desc=PermissionDesc;
        }

    }


    //分方规则条件类型代码
    public static enum ShortName {
        route("65434", "给药途径代码"),
        specialType("98149", "精麻毒放代码"),
        medicine("98094", "药品");
        public String code;
        public String desc;

        ShortName(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    //表达式类型代码
    public enum OperationCode {
        equal("390031993", "等于"),
        notEqual("390031994", "不等于");
        public String code;
        public String desc;

        OperationCode(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    //药品类型
    public enum medicineType {
        pt("152650", "普通药品"),
        js1("152651", "一类精神药品"),
        js2("152652", "二类精神药品"),
        mz("152653", "麻醉药品"),
        dx("152654", "毒性药品"),
        fsx("152655", "放射性药品");
        public String code;
        public String desc;

        medicineType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    //给药途径
    public enum route {
        jingDi("142433", "静滴"),
        jingTui("142434", "静推"),
        kouFu("142435", "口服"),
        sheXiaYongYao("142436", "舌下用药"),
        yanFen("142437", "研粉"),
        jianFu("142438", "煎服"),
        piXiaZhuShe("142440", "皮下注射"),
        yingYang("142441", "营养"),
        juMa("142442", "局麻"),
        piNeiZhuShe("142443", "皮内注射"),
        gongDiZhuShe("142444", "宫底注射"),
        qiuZhouZhuShe("142445", "球周注射"),
        penWu("142446", "喷雾"),
        biSi("142447", "鼻饲"),
        waiFu("142448", "外敷"),
        waiTu("142449", "外涂"),
        diYan("142450", "滴眼"),
        tuYan("142451", "涂眼"),
        yuanLiaoYao("142452", "原料药"),
        gangNei("142453", "肛内"),
        yinDaoYongYao("142454", "阴道用药"),
        shuKou("142455", "漱口"),
        diBi("142456", "滴鼻"),
        diEr("142457", "滴耳"),
        qingXi("142458", "清洗"),
        zaoYing("142459", "造影"),
        weiZhu("142460", "胃注"),
        zuoYu("142461", "坐浴"),
        tuiNa("142462", "推拿"),
        qiTa("142463", "其他"),
        jieYao("142464", "借药"),
        wuHuaXiRu("142465", "雾化吸入"),
        waiXi("142466", "外洗"),
        paoZu("142467", "泡足"),
        pangGuangChongXi("142468", "膀胱冲洗"),
        shiHuaQiDao("142469", "湿化气道"),
        waiYong("142470", "外用"),
        fengGuan("142471", "封管"),
        GFNYY("142472", "椎管内用药"),
        FJQNYY("142473", "关节腔内用药"),
        XMQNYY("142474", "胸膜腔内用药"),
        FQNYY("142475", "腹腔内用药"),
        QGNYY("142476", "气管内用药"),
        penHou("142477", "喷喉"),
        hanHua("142478", "含化"),
        QRJBYYTJ("142479", "其他局部用药途径"),
        QTYYTJ("142480", "其他用药途径"),
        chongFu("951865", "冲服"),
        jiaoFu("951866", "嚼服"),
        baHenZhuShe("951867", "疤痕注射"),
        diGuanZhuShe("951868", "骶管注射"),
        QFNZS("951869", "前房内注射"),
        tongDianZhuShe("951870", "痛点注射"),
        XGJZS("951871", "小关节注射"),
        xueWeiZhuShe("951872", "穴位注射"),
        guanSi("951873", "管饲"),
        waiTie("951874", "外贴"),
        penBi("951875", "喷鼻"),
        guanChang("951876", "灌肠"),
        chongGuan("951877", "冲管"),
        bengRu("951878", "泵入"),
        kouQiangXiRu("951879", "口腔吸入"),
        biQiangXiRu("951880", "鼻腔冲洗"),
        hanFu("951881", "含服"),
        juBuFengBi("973406", "局部封闭"),
        weiGuanZhuRu("973462", "胃管注入"),
        XZQNZS("973463", "血肿腔内注射"),
        qiGuanDiRu("973464", "气管滴入"),
        touXiYong("973465", "透析用"),
        jingZhu("973466", "静注"),
        pangGuangGuanZhu("973467", "膀胱灌注"),
        chongXi("973468", "冲洗"),
        zhuShe("973469", "注射"),
        xunXi("973470", "熏洗"),
        fuQiangZhuShe("399010062", "腹腔注射"),
        WTXHYY("399010063", "体外循环用药"),
        shuZhongYongYao("399010064", "术中用药"),
        guanZhu("399010065", "灌注"),
        yanDiZhuShe("399010066", "眼底注射"),
        HNNZS("399010067", "滑囊内注射"),
        daiChaYin("399011399", "代茶饮"),
        xiRu("399016416", "吸入"),
        TQNZS("399201990", "体腔内注射"),
        tunFu("399201991", "吞服"),
        hanShu("399201992", "含漱"),
        chuiBi("399201993", "吹鼻"),
        ruHu("399282198", "入壶"),
        ruYe("399282199", "入液"),
        xuYe("399282200", "续液"),
        piShi("399200978", "皮试"),
        sanJi("399201994", "散剂");
        public String code;
        public String desc;

        route(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    //流向规则类型
    public enum flowRuleType {
        JY("98071","检验"),
        JC("98078","检查"),
        BL("98088","病理"),
        ZYBZ("98092","中医辩证"),
        YP("98094","药品"),
        YBZL("98098","一般治疗"),
        shouShu("98103","手术"),
        YX("98107","用血"),
        shanShi("98110","膳食"),
        ZLKM("98128","诊疗科目"),
        HL("98129","护理"),
        WC("98133","卫材"),
        ZT("98134","嘱托"),
        HZ("230498","会诊"),
        HZLZL("991420","患者流转类");
        public String code;
        public String desc;
        flowRuleType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
    public static Map<String,String> freq = new HashMap<String,String>();  
    static {  
        freq.put("立即执行", "立即执行"); 
        freq.put("BID", "每日两次"); 
        freq.put("Q12H", "每12小时一次"); 
        freq.put("Q6H", "每6小一次"); 
        freq.put("Q8H", "每8小时一次"); 
        freq.put("QD", "每日一次"); 
        freq.put("每日一次", "每日一次"); 
        freq.put("QID", "每日四次"); 
        freq.put("QN", "每晚一次"); 
        freq.put("TID", "每日三次"); 
        freq.put("Q2H", "每2小时一次"); 
        freq.put("PRN", "需要时服用（长期）"); 
        freq.put("BIW", "每周两次"); 
        freq.put("TIW", "每周三次"); 
        freq.put("QW", "每周一次"); 
        freq.put("QOD", "隔日一次"); 
        freq.put("QH", "每1小时一次"); 
        freq.put("Q3H", "每3小时一次"); 
        freq.put("Q4H", "每4小时一次"); 
        freq.put("Q5H", "每5小时一次"); 
        freq.put("Hs", "临睡前"); 
        freq.put("other", "其他"); 
        freq.put("ONCE", "临时一次"); 
        freq.put("ALWAYS", "持续"); 
        freq.put("Q1/2H", "30分钟一次"); 
        freq.put("qow", "隔周一次"); 
        freq.put("q2w", "每两周一次"); 
        freq.put("q3w", "每三周一次"); 
        freq.put("q4w", "每四周一次"); 
        freq.put("sos", "必要时一次"); 
        freq.put("Q3D", "每3日一次"); 
        freq.put("Q5D", "每5日一次"); 
        freq.put("Q72H", "每72小时一次"); 
        freq.put("QM", "每天早上一次"); 
    }
    
    

    public enum csTypeCode {
        JY("98071","检验"),
        SHCG("98072","生化常规"),
        LJCG("98073","临检常规"),
        TYCG("98074","体液常规"),
        MYCG("98075","免疫常规"),
        WSWCG("98076","微生物常规"),
        QTJY("98077","其它检验"),
        BL("98088","病理"),
        CGBL("98089","常规病理"),
        JRBL("98090","介入病理"),
        SSBL("98091","手术病理"),
        YBZL("98098","一般治疗"),
        PTZL("98099","普通治疗"),
        FSZL("98100","放射治疗"),
        JC("98078","检查"),
        CS("98079","超声"),
        PF("98080","普放"),
        CT("98081","CT"),
        MRI("98082","MRI"),
        DSL("98083","电生理"),
        NJ("98084","内镜");
        public String code;
        public String desc;
        csTypeCode(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
    
    /**
     * 知识体系
     */
    //知识体系类别
    public static String knowledge_system = "诊疗路径简易版";
    //卫宁医学知识体系知识体系类别
    public static String knowledge_WN = "卫宁医学知识体系";
    //主诉症状类型
    public static String symptom_type = "单症状";
    
    /**
     * 大临床测试配置
     */
    //病区
    public static String inpatient_select_ward = "骨科病区";
    public static String inpatient_select_ward_code = "2905";
    //转科-新科室
    public static String inpatient_select_newDepartmen = "结核科";
    //转科-新病区
    public static String inpatient_select_newWard = "结核一病区";
    //住院病历二级审签流程中提交病历的医生账号
    public static String inpatient_submitEMR_login_account = "1007";
    //住院病历二级审签流程中提交病历的医生密码
    public static String inpatient_submitEMR_login_pwd = "abcd@1234";
    //护理医嘱
    public static String test_prescribe_nursing = "头晕护理常规";
    //手术医嘱
    public static String test_prescribe_operation = "肢体缩短手术";
    //用血医嘱
    public static String test_prescribe_blood = "浓缩血小板";
    //膳食医嘱
    public static String test_prescribe_diet = "软食";

    public static String responsibleNurse = "2077";  //责任护士账号
    public static String responsibleDoctor = "1303";//责任医生账号
    public static String residentDoctor= "1303";//住院医生账号
    public static String attendingDoctor = "1303";//主治医生账号
    public static String headDoctor = "1310";//主任医生账号

    public static String inpatient_EMR1 = "入院记录";  //住院病历
    public static String inpatient_EMR2 = "首次病程记录";  //住院病历
    public static String inpatient_EMR3= "日常病程记录";  //住院病历
    public static String inpatient_deposit = "5000" ;  //住院押金

    //身体体征正常值
    public enum signs{
        TEMP1("36.5","体温"),
        TEMP2("36.5","T"),
        SBP1("120", "收缩压"),
        SBP2("120", "BP"),
        DBP1("80", "舒张压"),
        DBP2("80", "/"),
        R1("16", "呼吸"),
        R2("16", "R"),
        P1("80", "脉搏"),
        P2("80", "P"),
        SaO2("98", "血氧"),
        Ht("170","身高"),
        Wt("60","体重"),
        defecate ("1", "大便"),
        urinate ("5", "小便");
        public String value;
        public String desc;

        signs(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }
        public String getDesc() {
            return desc;
        }
    }

    public static String chart_Record = "体温单202106";  //体温单
    public static String chart_InpatientAssess = "入院评估单202106";  //入院评估单
    public static String chart_assess1= "疼痛评估单202106";  //评估单
    public static String chart_assess2 = "跌倒/坠床风险评估202106" ;  //评估单
    public static String bedNoPrefix = "ST"; //床位编号前缀
    public static Integer maxBedNo = -1; //最大床位编号
    //记录当前是否在入院登记中
    public static Boolean inAdmissionRegistering = false;
    //入院登记的病房名称
    public static String InpatRoomName= "1501";
    
    
    
    /**
     * 大his配置
     */
    //大his地址（临时）
    public static String his_host = "172.16.7.26";
    //大临床地址（临时）
    public static String inpatient_host = "172.16.7.93";
    //窗口名称
    public static String windowsName = "发药窗口";
    //临床结算查询开始日期
    public static String clinicalSettlementStartDate = "20180405";
    //临床结算查询结束日期
    public static String clinicalSettlementEndDate = "20210505";
    //进销存药品
    public static String WINEX_stock_drug = "百令胶囊";
    //库存测试专用药
    public static String WINEX_stock_drug2 = "阿司匹林肠溶片";
    //入库药房
    public static String WINEX_pharmacy_name1 = "中心药房";
    //出库药房
    public static String WINEX_pharmacy_name2 = "西药库";

    
    


}