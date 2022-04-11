package com.winning.manager;


import java.util.List;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;


/**
 * HIS Sql语句管理类
 * 1、所有的接口名字，需要一一对应sql变量名
 * 2、参数名字命名规则，his类型 + "_" + 参数名，多个his公用的参数已his类型1 + "_" + "his类型2" + "_" + "..." + 参数名 ,不需要改参数传null即可
 * 3、所有的sql语句如果需要占位符，统一用？
 *
 * @author luolantao
 */
public class HisSqlManager {

    //His类型
    String dbType = null;

    public HisSqlManager() {
        dbType = Data.hisType.toUpperCase();
    }

    /**
     * 挂号登记sql
     *
     * @param jtwn_zjwn_subjectCode 集团卫宁与浙江卫宁使用，科目编码
     * @param sxwn_doctorId         山西卫宁使用，医生id
     * @return
     */
    public String ghdj(String jtwn_zjwn_subjectCode, String sxwn_doctorId, String sex) {
        if (this.dbType.equals("JTWN")) {
            return new JTWNSql().ghdj.replace("?1", jtwn_zjwn_subjectCode).replace("?2", Data.patientage.toString()).replace("?3", sex).replace("?4",Data.ybdm);
        }
        else if (this.dbType.equals("SXWN")) {
            return new SXWNSql().ghdj.replace("?", sxwn_doctorId);
        }
        else if (this.dbType.equals("ZJWN")) {
            return new ZJWNSql().ghdj.replace("?", jtwn_zjwn_subjectCode);
        }
        else if (this.dbType.equals("TJWN")) {
            return new TJWNSql().ghdj;
        }
        else {
            return null;
        }
    }

    /**
     * 挂号登记sql，使用已存在的patid
     *
     * @param jtwn_subjectCode 集团卫宁使用，科目编码
     * @param jtwn_patid       集团卫宁使用，patid
     * @return
     */
    public String ghdjWithPatid(String jtwn_subjectCode, String jtwn_patid) {
        if (this.dbType.equals("JTWN")) {
            return new JTWNSql().ghdjWithPatid.replace("?1", jtwn_subjectCode).replace("?2", jtwn_patid);
        }
        else if (this.dbType.equals("SXWN")) {
            return new SXWNSql().ghdjWithPatid;
        }
        else if (this.dbType.equals("ZJWN")) {
            return new ZJWNSql().ghdjWithPatid;
        }
        else if (this.dbType.equals("TJWN")) {
            return new TJWNSql().ghdjWithPatid;
        }
        else {
            return null;
        }
    }

    /**
     * 门诊收费
     *
     * @param cisxh 收费序号
     * @return
     */
    public String mzsf(String cisxh) {
        if (this.dbType.equals("JTWN")) {
            return new JTWNSql().mzsf.replace("?", cisxh);
        }
        else if (this.dbType.equals("SXWN")) {
            return new SXWNSql().mzsf.replace("?", cisxh);
        }
        else if (this.dbType.equals("ZJWN")) {
            return new ZJWNSql().mzsf.replace("?", cisxh);
        }
        else if (this.dbType.equals("TJWN")) {
            return new TJWNSql().mzsf;
        }
        else {
            return null;
        }
    }

    /**
     * 从HIS获取挂号登记Fhir报文
     *
     * @param jtwn_patid        患者patid
     * @param jtwn_ghxh         患者挂号序号
     * @param jtwn_hospitalSoid 医院hospital_soid
     * @return
     */
    public String getFhirGhdjData(String jtwn_patid, String jtwn_ghxh, String jtwn_hospitalSoid) {
        if (this.dbType.equals("JTWN")) {
            return new JTWNSql().getFhirGhdjData.replace("?1", jtwn_patid).replace("?2", jtwn_ghxh).replace("?3", jtwn_hospitalSoid);
        }
        else if (this.dbType.equals("SXWN")) {
            return new SXWNSql().getFhirGhdjData;
        }
        else if (this.dbType.equals("ZJWN")) {
            return new ZJWNSql().getFhirGhdjData;
        }
        else if (this.dbType.equals("TJWN")) {
            return new TJWNSql().getFhirGhdjData;
        }
        else {
            return null;
        }
    }

    /**
     * 获取处方明细
     *
     * @param jtwn_cisxhList
     * @return
     */
    public String getCFMX(List<String> jtwn_cisxhList) {
        if (this.dbType.equals("JTWN")) {
            String cisxhString = "";
            for (String cfxh : jtwn_cisxhList) {
                cfxh = SdkTools.findMatchList(cfxh, "(.*?),", 1).get(0);
                cisxhString += "'" + cfxh + "',";
            }
            cisxhString = cisxhString.substring(0, cisxhString.length() - 1);
            return new JTWNSql().getCFMX.replace("?1", cisxhString);
        }
        else if (this.dbType.equals("SXWN")) {
            return new SXWNSql().getCFMX;
        }
        else if (this.dbType.equals("ZJWN")) {
            return new ZJWNSql().getCFMX;
        }
        else if (this.dbType.equals("TJWN")) {
            return new TJWNSql().getCFMX;
        }
        else {
            return null;
        }
    }


    /**
     * 根据获取患者诊断信息
     */
    public String getzdmc(String patid) {
        if (this.dbType.equals("JTWN")) {
            return new JTWNSql().getZDMCWithpatid.replace("?1", patid);
        }
        return null;
    }

    /**
     * 根据患者名字hzxm获取处方明细库自备药标识
     */
    public String getzbybz(String hzxm) {
        if (this.dbType.equals("JTWN")) {
//            return new WnSqlManager.WnSqlserverSql().getEmrTsEmptySubmitFlag.replace("?", MrtName);
            return new JTWNSql().getZBYBZWithHzxm.replace("?1",hzxm);
        }
        return null;
    }
    
    /**
     * 更新库存
     */
    public String updateYFZKC(String stockNum,String ksdm,String drugName) {
        if (this.dbType.equals("JTWN")) {
//            return new WnSqlManager.WnSqlserverSql().getEmrTsEmptySubmitFlag.replace("?", MrtName);
            return new JTWNSql().updateYFZKC.replace("?1",stockNum).replace("?2",ksdm).replace("?3",drugName);
        }
        return null;
    }

    /**
     * 查询需要测试的所有药品sql语句
     */
    public String getAllMedicineSql() {
        return new TJWNSql().getAllMedicineSql;
    }

    /**
     * 查询需要测试的所有检验sql语句
     */
    public String getAllLabSql() {
        return new TJWNSql().getAllLabSql;
    }

    /**
     * 查询需要测试的所有病理sql语句
     */
//	public  String getAllPathologySql() {
//		return new TJWNSql().getAllPathologySql;
//	}

    /**
     * 查询需要测试的所有治疗sql语句
     */
    public String getAllTreatSql() {
        return new TJWNSql().getAllTreatSql;
    }

    /**
     * 查询需要测试的所有检查项目sql语句
     */
    public String getAllExamItemSql() {
        return new TJWNSql().getAllExamItemSql;
    }

    /**
     * 根据科室名称查询科室编码
     */
    public String getGetKSBMByKSMC(String subjectName) {
        return new TJWNSql().getKSBMByKSMC.replace("?",subjectName);
    }

    /**
     * 根据病区名称查询病区编码
     */
    public String getGetBQBMByBQMC(String wardName) {
        return new TJWNSql().getBQBMByBQMC.replace("?",wardName);
    }


    /**
     * 查询床位
     */
    public String getCW(String bedNo) {
        return new TJWNSql().getCW.replace("?",bedNo);
    }


    /**
     * 获取入院登记的最大住院序号、住院号、患者ID
     */
    public String getGetPatientIdWithTDJKZ() {
        return new TJWNSql().getPatientIdWithTDJKZSql;
    }
    /**
     * 获取在院病人费用帐页表最大的登记序号
     */
    public String getTfyzSql() {
        return new TJWNSql().getTfyzSql;
    }

    /**
     * 查询PatientId
     */
    public String getPatientIdSql() {
        return new TJWNSql().getPatientIdSql;
    }

    /**
     * 天津卫宁入院登记的sql
     * @param ZYH  住院号
     * @param XMPY 姓名拼音
     * @param WardCode 病区编码
     * @param SubjectCode 科室编码
     */
    public String setPatientIdWithTDJKZ(String ZYH,String patientName,String XMPY,String sex,String birthday,String age,String IDCode,String currentDate,String WardCode,String SubjectCode,String deposit,String patientId) {
        return new TJWNSql().setPatientIdWithTDJKZSql.replace("?2", ZYH).replace("?3", patientName).replace("?4", XMPY).replace("?5", sex).replace("?6", birthday).replace("?7", age).replace("?8", IDCode).replace("?9", currentDate).replace("?WardCode", WardCode).replace("?SubjectCode", SubjectCode).replace("?deposit", deposit).replace("?patientId", patientId);
    }

    /**
     * 天津卫宁入院登记生成费用帐页表
     * @param DJXH 登记序号
     * @param ZYH 住院号
     * @param BQBM 病区编码
     * @param CYRQ 出院日期
     */
    public String setTfyzSql(String DJXH,String ZYH,String BQBM,String CYRQ) {
        return new TJWNSql().setTfyzSql.replace("?1", DJXH).replace("?2", ZYH).replace("?3", BQBM).replace("?4", CYRQ);
    }

    /**
     * 天津卫宁入院登记生成押金明细
     * @param ZYH 住院号
     * @param RQ 日期
     * @param YJJE 押金金额
     */
    public String setZyyjmxkSql(String ZYH,String RQ,String YJJE) {
        return new TJWNSql().setZyyjmxkSql.replace("?1", ZYH).replace("?2", RQ).replace("?3", YJJE);
    }

    /**
     *根据组织名称获取组织ID
     */
    public String getGetOrgIdByOrgName(String orgName,String OrgNo) {
        return new JTWNSql().getOrgIdByOrgName.replace("?1",orgName).replace("?2",OrgNo);
    }

    /**
     *查询住院的保险信息ID
     */
    public String getMedInstiInsur() {
        return new JTWNSql().getMedInstiInsur;
    }

    /**
     * 根据cisxh获取处方明细
     */
    String getCFMX = null;

    /**
     * 更新当前住院号、当前押金号、当前病案号
     * @param ZYH 住院号
     */
    public String updateTzyhk(String ZYH,String YJH,String BAH) {
        return new TJWNSql().updateTzyhk.replace("?1", ZYH).replace("?2", YJH).replace("?3", BAH);
    }


    //获取his医嘱表tcqyz
    public String getOrderTcqyz(String zyh,String cfh_wn) {
        return new TJWNSql().getOrderTcqyz.replace("?1", zyh).replace("?2",cfh_wn);
    }
    //获取his医嘱表tlsyzz
    public String getOrderTlsyzz(String zyh,String cfh_wn) {
        return new TJWNSql().getOrderTlsyzz.replace("?1", zyh).replace("?2",cfh_wn);
    }
    //获取his医嘱表cqzxmx
    public String getOrderCqzxmx(String zyh,String cfh_wn) {
        return new TJWNSql().getOrderCqzxmx.replace("?1", zyh).replace("?2",cfh_wn);
    }
    //获取his医嘱表kd_mx
    public String getOrderKdmx(String zyh,String cfh_wn) {
        return new TJWNSql().getOrderKdmx.replace("?1", zyh).replace("?2",cfh_wn);
    }
    //获取his医嘱表ssmxk
    public String getOrderSsmxk(String zyh) {
        return new TJWNSql().getOrderSsmxk.replace("?1", zyh);
    }
    //获取his医嘱表yszzyhzjyd
    public String getOrderyszzyhzjyd(String zyh) {
        return new TJWNSql().getOrderyszzyhzjyd.replace("?1", zyh);
    }
    //获取his医嘱表bq_cyff
    public String getOrderbqcyff(String zyh,String cfh_wn) {
        return new TJWNSql().getOrderbqcyff.replace("?1", zyh).replace("?2",cfh_wn);
    }
    //获取his就诊表TDJKZ
    public String getInpatienttdjkz(String zyh) {
        return new TJWNSql().getInpatienttdjkz.replace("?1", zyh);
    }

    
    
    /**
     * 查询指定时间段内需要测试的所有药品sql语句(通过5.x数据库)
     */
    public String getAllMedicineWINEXSql(String startDate,String endDate) {
            return new JTWNSql().getAllMedicineWINEXSql.replace("?1", startDate).replace("?2", endDate);
    }
    
    /**
     * 查询指定时间段内需要测试的所有非药品sql语句，检查检验治疗(通过5.x数据库)
     */
    public String getNonMedicineWINEXSql(String startDate,String endDate) {
            return new JTWNSql().getNonMedicineWINEXSql.replace("?1", startDate).replace("?2", endDate);
    }
    /**
     * 集团卫宁
     */
    public class JTWNSql {

        /**
         * 挂号登记sql
         */
        String ghdj = "wnt_ui_ghdj '?1',@age='?2',@sex='?3',@ybdm='?4'";

        /**
         * 挂号登记sql，使用已存在的patid
         */
        String ghdjWithPatid = "wnt_ui_ghdj '?1',@existpatid=?2";

        /**
         * HIS同步到60的挂号报文
         */
        String getFhirGhdjData = "usp_gh_jo_interface ?1, 3, ?2, ?3";

        /**
         * 门诊收费
         */
        String mzsf = "wnt_MZSF ?";

        /**
         * 根据多个cisxh获取处方明细,参数为 xh1,xh2,xh3...
         */
        String getCFMX = "SELECT cf.xh cfxh,cf.shbz5,cf.sycfbz,mx.xh mxxh,mx.ypmc FROM SF_HJCFK cf INNER JOIN SF_HJCFMXK mx ON cf.xh=mx.cfxh WHERE cf.cisxh in (?1)";

        /**
         * 根据患者patid获取诊断名字
         */
        String getZDMCWithpatid = "select zdmc from SF_YS_MZBLZDK sf inner join  GH_GHZDK gh on gh.xh = sf.ghxh where gh.patid='?1'";

        /**
         * 根据患者姓名hzxm获取自备药标识
         */
        String getZBYBZWithHzxm = "select zbybz,* from SF_HJCFMXK mxk inner join SF_HJCFK cfk on cfk.xh = mxk.cfxh where cfk.hzxm='?1'";

        /**
         * 根据组织名称获取组织ID
         */
        String getOrgIdByOrgName ="SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE IS_DEL=0 AND ORG_TYPE_CODE= 230267 AND ORG_STATUS=98360 AND ORG_NAME ='?1' AND ORG_NO ='?2'";

        /**
         * 查询住院的保险信息ID
         */
        String getMedInstiInsur ="SELECT  a.MED_INSTI_INSUR_ID FROM WINDBA.MED_INSTI_MEDICAL_INSURANCE  a,WINDBA.MED_INSUR_USE_MODE b where a.is_del=0  and b.IS_DEL=0 and b.MED_INSUR_USE_MODE_code=959939 and a.MED_INSTI_INSUR_ID=b.MED_INSTI_INSUR_ID and a.enabled_flag=98175";
        
        /**
         * 更新药房总库存
         */
        String updateYFZKC = "update YF_YFZKC set kcsl3 ='?1' where ksdm='?2' and ypmc like '%?3%'";
        
        /**
         * 查询指定时间段内需要测试的所有药品sql语句（通过5.x数据库）
         */
        String getAllMedicineWINEXSql ="select  distinct  a.idm,a.ypmc,a.ypgg,a.zje,a.ypsl as '最小单位数量',CAST(a.ypsl/c.zyxs  as numeric(10,2)) as '住院数量' from ZY_BRFYMXK a,ZY_BRJSK b,YK_YPCDMLK  c where a.jsxh=b.xh and b.jlzt=0 and b.ybjszt=2 and a.idm=c.idm and a.dxmdm  in ('01','02','03')  and ypsl>0 and CAST(a.ypsl/c.zyxs  as numeric(10,2)) =1 and a.qqrq>'?1' and a.qqrq<'?2' ";
        
        /**
         * 查询指定时间段内需要测试的所有药品sql语句（通过5.x数据库）
         */
        String getNonMedicineWINEXSql ="select  distinct  a.idm,a.ypmc,a.ypgg,a.zje,a.ypsl as '最小单位数量',a.ypsl  as '住院数量',a.lcxmdm from ZY_BRFYMXK a,ZY_BRJSK b,YY_SFXXMK  c where a.jsxh=b.xh and b.jlzt=0 and b.ybjszt=2 and a.ypdm=c.id and a.dxmdm not in ('01','02','03') and ypsl>0 and ypsl=1 and a.qqrq>'?1' and a.qqrq<'?2' ";

    }

    /**
     * 山西卫宁
     */
    public class SXWNSql {
        /**
         * 挂号登记sql
         */
        String ghdj = "wnt_sx_ghdj ?";

        /**
         * 挂号登记sql，使用已存在的patid
         */
        String ghdjWithPatid = null;

        /**
         * HIS同步到60的挂号报文
         */
        String getFhirGhdjData = null;

        /**
         * 门诊收费
         */
        String mzsf = "wnt_sx_MZSF ?";

        /**
         * 根据cisxh获取处方明细
         */
        String getCFMX = null;


    }

    /**
     * 浙江卫宁
     */
    public class ZJWNSql {
        /**
         * 挂号登记sql
         */
        String ghdj = "wnt_zj_ghdj ?";

        /**
         * 挂号登记sql，使用已存在的patid
         */
        String ghdjWithPatid = null;

        /**
         * HIS同步到60的挂号报文
         */
        String getFhirGhdjData = null;

        /**
         * 门诊收费
         */
        String mzsf = "wnt_zj_MZSF ?";

        /**
         * 根据cisxh获取处方明细
         */
        String getCFMX = null;
    }

    /**
     * 天津卫宁
     */
    public class TJWNSql {

        /**
         * 挂号登记sql
         */
        String ghdj = null;

        /**
         * 挂号登记sql，使用已存在的patid
         */
        String ghdjWithPatid = null;

        /**
         * HIS同步到60的挂号报文
         */
        String getFhirGhdjData = null;

        /**
         * 门诊收费
         */
        String mzsf = null;

        /**
         * 根据cisxh获取处方明细
         */
        String getCFMX = null;

        /**
         * 查询需要测试的所有药品sql语句
         */
        String getAllMedicineSql = "select '药品' TYPE,MEDEXTREFID ID,MEDEXTREFID MEDEXTREFID,commodityNameChinese NAME,pkgSpec PACKAGE FROM FHIR_MEDICINE";

        /**
         * 查询需要测试的所有检验sql语句
         */
        String getAllLabSql = "SELECT '检验' TYPE,csNo ID,csName NAME FROM FHIR_SERVICE_LABTEST";

        /**
         * 查询需要测试的所有病理sql语句
         */
        String getAllPathologySql = "SELECT '病理' TYPE,CS_ID ID,CS_NAME NAME FROM CLINICAL_SERVICE WHERE IS_DEL=0 AND CS_STATUS=98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID=98088 OR PARENT_VALUE_ID=98088)";

        /**
         * 查询需要测试的所有治疗sql语句
         */
        String getAllTreatSql = "SELECT '治疗' TYPE,csno ID,csName NAME FROM FHIR_CLINICAL_TREATMENT";

        /**
         * 查询需要测试的所有检查项目sql语句
         */
        String getAllExamItemSql = "SELECT '检查项目' TYPE,csno ID,csName NAME FROM fhir_CLINICAL_SERVICE_EXAMI";

        /**
         * 根据科室名称获取科室编码
         */
        String getKSBMByKSMC = "select KSBM from  Mz_ksbm where KSMC LIKE '?'";

        /**
         * 根据病区名称获取病区编码
         */
        String getBQBMByBQMC= "select BQBM from Bqbmk where BQMC like '?'";

        /**
         *查询床位
         */
        String getCW= "select BEDNO from FHIR_CW where BEDNO = '?'";

        /**
         * 查询入院登记的当前住院号
         */
        String getPatientIdWithTDJKZSql = "SELECT DQZYH,DQYJH,DQBAH FROM tzyhk";

        /**
         * 查询费用帐页表最大的登记序号
         */
        String getTfyzSql = "SELECT max(DJXH) as DJXH FROM tfyz";

        /**
         * 查询PatientId
         */
        String getPatientIdSql = "select mz_card_seq.nextval patientid from dual";

        /**
         * 入院登记的sql
         */
        String setPatientIdWithTDJKZSql = "INSERT INTO TDJKZ(ZYH, YLZH, CH, CXH, BFFH, XM, XMPY, XB, CSNY, NL, HF, JG, GJDM, MZ, SFZH, GZDW, DH, RYFS, RYCS, LXR, JTZZ, ZCCS, MZZD, MZYS, JZHS, YLLX, BXBS, GLDJE, GLDYE, YJYE, JSFS, JSCS, ZYRQ, RYSJ, KZRBM, ZZYSBM, ZYYSBM, SXYSBM, DJRQ, CYRQ, BQBM, BQMC, CYQK, CSBS, YJYJ, LXRGX, ZY, GZ_YZBM, HKDZ, HK_YZBM, LXR_DZ, LXR_DH, ICNO, KSBM, BAH, FYLJ, RYZD, RYZDYS, RYZDRQ, QZZD, QZZDYS, QZZDRQ, CYZD, CYZDYS, CYZDRQ, RYLXBM, RYLX, RYQK, FB, CYBS, TPBS, BABS, XTDM, DWDM, XGH, CTH, BCH, XGHRQ, CTHRQ, BCHRQ, PZRBM, XXBM, GMS, TZBS, BFBS, CY_SJH, JFBS, GCBS, JCBS, ZYCBM, NZYCS, BXXH, ZHBS, JSRQ, XYJE, WCBS, BGBS, BGJE, ZRYS, ZRHS, QFBS, ZCNY, MZZDMC, RYBQBM, RYKSBM, ZKKSBM, DWDH, DWLXR, JTLXR, JZCS, GRZMC, ZJMC, ZAMC, TJSX, BLZT, BLDHSJ, YCH, HLZCBS, NLDW, TPSJ, BXZZBS, QTYLBXKH, HKDH, WYZZ, SCBXBS, ZLZBM, YSYJ, SSSJ, PATIENT_ID, CASE_ORDER_NUMBER, SG, TZ, JYCD, FM, MM, CYM, GMDM, BSLQK, HEIGHT, WEIGHT, YBLX, BKJS, GS_QYXH, GSBS, GSXH, HSFYJ, HSFYJYE, CYBZ, ZJLX, CYBZSJ, FBXSCBGBS, DFXH, EMRSTATUS, EMRGDSJ, CRBBS, YNGRBS, CSD_X, DJRY, BKJSYY, GLBJ, CZY, DJSJ, SFCJGS, MEMO, XSERCSTZ, XSERRYTZ, CSD_S, JG_SS, JG_S, XZZ_SS, XZZ_S, XZZ_X, XZZ_DH, XZZ_YB, HK_SS, HK_S, HK_X, YLFKFS, GSBZ, JKKH, EMR_FLAG, HICIS_FLAG, HICIS_FLAG2, BZYY, JKDA_UP, FLAG_MPI, HZBQTZLX, JWS, TDJRY, SF90TCYJS, MNW_FLAG, UP_DATE, UP_ZT, NHXH, FYSHBS, FYSHRYQ, FYSHRQ, HLFZBM, H_CREATETIME, H_UPDATE, H_UPDATETIME, YBKH, ABBS, ZCRYSJ, YBZYLSH, SFSZBS, BKJSRY, MZH, HZYSBS, HZBQTZLXBM, BZXYZ, SFZHSK, QTYBZF, YBLX2014, SFZHSK_CZY, JSBZ, SBBS, HZBXLX, GRBS, BLHSDYBZ, TDJYY, YBLB, KSS_JSBS, SYBXBS, DFBS, SQSXH, BLHSBZ, BAHSRQ, ZCZYYY_GS, YGBS, NLTS, XNHXH, VNURSINGGRADE, VILLNESSSTATE, DTOPERATIONTIME, VMEDICARETYPE, RJSSBS, SFXJ, RYFS2, SFJYL, VIPBS, ZZDJBS, JHRSFZH, LXR_DZ_SS, LXR_DZ_S, LXR_DZ_X, ZRYLJGMC, BCCH, SXBXFLAG, BFRYBZ, NIS_NO, NIS_ZYCS, JTYS_SCBS_JC, YZKBS, SFXGFY, BXSZD,PATIENTID) VALUES ('?2', NULL, NULL, NULL, NULL, '?3', '?4', '?5', TO_DATE('?6 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'), '?7', ' ', '12', '001', '01', '?8', ' ', ' ', '2', NULL, ' ', ' ', NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, '?deposit', '2', NULL, NULL, NULL, NULL, NULL, NULL, NULL, TO_DATE('?9', 'SYYYY-MM-DD HH24:MI:SS'), NULL, '?WardCode', '?WardCode', NULL, NULL, '?deposit', ' ', ' ', NULL, NULL, NULL, ' ', ' ', NULL, '?SubjectCode', '117393', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '3', NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, ' ', NULL, NULL, NULL, NULL, '0', NULL, '01', NULL, NULL, NULL, NULL, '0', NULL, NULL, '0', NULL, NULL, NULL, NULL, ' ', '?WardCode', '?SubjectCode', NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '岁', NULL, NULL, NULL, NULL, NULL, '0', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '身份证', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, TO_DATE('?9', 'SYYYY-MM-DD HH24:MI:SS'), NULL, TO_DATE('?9', 'SYYYY-MM-DD HH24:MI:SS'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,'?patientId')";
        /**
         * 入院登记生成费用帐页表
         */
        String setTfyzSql = "INSERT INTO tfyz(DJXH, ZYH, BQBM, CYRQ, ZYTS, FY01, FY02, FY03, FY04, FY05, FY06, FY07, FY08, FY09, FY10, FY11, FY12, FY13, FY14, FY15, FY16, FY17, FY18, FY19, FY20, FY21, FY22, FY23, FY24, FY25, FY26, FY27, FY28, FY29, FY30, FY31, FY32, FY33, FY34, FY35, FY36, FY37, FY38, FY39, FY40, HJJE, BXFYLJ, ZHZF, TCZF, XJZF, TYFY, TJFY, TZFY, ZFJE, CZYDM) VALUES ('?1', '?2', '?3', TO_DATE('?4', 'SYYYY-MM-DD HH24:MI:SS'), '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0','0000')";

        /**
         * 入院登记生成住院押金明细
         */
        String setZyyjmxkSql = "INSERT INTO zyyjmxk(ZYH, RQ, YJJE, PZH, CZYMC, JS_LX,  ZYCBM, CZYDM, YHKJYXH, YSPJH) VALUES ('?1', TO_DATE('?2', 'SYYYY-MM-DD HH24:MI:SS'), '?3', '10469345','首佳调试', '1', '01','0000','0', '0')";

        /**
         * 更新DQZYH
         */
        String updateDQZYH = "update tzyhk set DQZYH=?";


        //获取天津his表tlsyzz--流水号，开立病区，开立科室，开立医生，用药天数，开立数量，开立单位，频次代码，给药途径1，给药途径2，包装规格1，包装规格2，基本包装单位，商品名，临床服务名称，零售价，嘱托，医嘱类型，医嘱项标识，编码，执行人编码，住院发药申请，住院发药计划，发药数量，发药剂量
        String getOrderTlsyzz  = "select zyh,bq,ksbm,ysbm,ts,cs,dw,yfyl,fsbm,zxfs,hldw,ypyl,dw,pm,yzmc,jg1,yszt,lx,cfh_wn,bm,zxrbm,fysqbs_wn,fyjhbs_wn,SL,HL from tlsyzz where zyh = '?1' and cfh_wn = '?2'";
        //获取天津his表tcqyz--流水号，开立病区，开立科室，开立医生，开立数量，开立单位代码，频次代码，给药途径1，给药途径2，基本包装单位，商品名，临床服务名称，零售价，嘱托，医嘱项标识，药品编码
        String getOrderTcqyz= "select zyh,bq,kb,ysbm,ts,cs,dw,yfyl,fsbm,zxfs,dw,pm,yzmc,jg1,yszt,cfh_wn,bm,zxrq from tcqyz where zyh = '?1' and cfh_wn = '?2'";
       //获取天津his表cqzxmx
        String getOrderCqzxmx=" select fyjhbs_wn,fysqbs_wn,zxrbm,zxrq from cqzxmx where yzxh in (select yzxh from tcqyz where zyh = '?1' and cfh_wn = '?2')";
        //获取天津his表kd_mx
        String getOrderKdmx = "select zyh,kdks,kdys,jymd,jydbh,cfh_wn from kd_mx where zyh = '?1' and cfh_wn = '?2'";
        //获取天津his表ssmxk
        String getOrderSsmxk = "select zyh,bqks,bqbm,kdry,ssbm,ssmc from ssmxk where zyh = '?1'";
        //获取天津his表YSZ_ZYHZJYD
        String getOrderyszzyhzjyd = "select zyh,jyks from YSZ_ZYHZJYD where zyh = '?1'";
        //获取天津his表bq_cyff--流水号，开立医生，姓名，给药途径，制出总量，剂量(每顿剂量)，服药周期付数，嘱托
        String getOrderbqcyff = "select zyh,ysbm,xm,yf,zl,cl,jr,yszt from bq_cyff where zyh = '?1' and cfh_wn = '?2'";
        //获取天津his表TDJKZ--身份证号码，病案号，就诊登记流水号,国籍，性别，名族,床位,出区时间,出区召回时间
        String getInpatienttdjkz = "select SFZH,BAH,ZYH,GJDM,XB,MZ,RYFS,CH,KSBM,BQBM,CYRQ,H_UPDATETIME from TDJKZ where zyh='?1'";

        String updateTzyhk = "update tzyhk set DQZYH=?1,DQYJH=?2,DQBAH=?3";
    }


}
