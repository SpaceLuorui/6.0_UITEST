package com.winning.manager;


import ui.sdk.config.Data;

/**
 * 60 Sql语句管理类
 * 1、所有的接口名字，需要一一对应sql变量名
 * 2、参数名字命名规则，his类型 + "_" + 参数名，多个his公用的参数已his类型1 + "_" + "his类型2" + "_" + "..." + 参数名 ,不需要改参数传null即可
 * 3、所有的sql语句如果需要占位符，统一用？
 *
 * @author luolantao
 */
public class WnSqlManager {

    //60数据库类型，默认是sqlserver
    String dbType = null;

    public WnSqlManager() {
        dbType = Data.wn60DbType.toUpperCase();
    }

    /**
     * 根据value_id查询主数据对应描述
     *
     * @param VALUE_ID VALUE_SET表中的VALUE_ID
     */
    public String getMdmDescByValueId(String VALUE_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMdmDescByValueId.replace("?", VALUE_ID);
        }
        return new WnOracleSql().getMdmDescByValueId.replace("?", VALUE_ID);
    }

    /****根据ORG_ID查找ORG——name***/
    public String getOrgNameByOrgId(String ORG_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOrgNameByOrgId.replace("?1", ORG_ID);
        }
        return new WnOracleSql().getOrgNameByOrgId.replace("?1", ORG_ID);
    }

    /***根据CSID查找MEDICINEID**/
    public String getMedicineIdbyCSID(String CSID, String HospitalSoid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMedicineIdbyCSID.replace("?1", CSID).replace("?2", HospitalSoid);
        }
        return new WnOracleSql().getMedicineIdbyCSID.replace("?1", CSID).replace("?2", HospitalSoid);
    }

    /****根据EMPLOYEE_ID查找no和name***/
    public String getEmployeeNameByEmployeeId(String Employee_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmployeeNameByEmployeeId.replace("?1", Employee_ID);
        }
        return new WnOracleSql().getEmployeeNameByEmployeeId.replace("?1", Employee_ID);
    }

    /****根据EMPLOYEE_No查找EmployeeId***/
    public String getEmployeeIdByEmployeeNo(String Employee_No) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmployeeIdByEmployeeNo.replace("?1", Employee_No);
        }
        return new WnOracleSql().getEmployeeIdByEmployeeNo.replace("?1", Employee_No);
    }

    /**
     * 根据code_system_id查询value_id列表
     *
     * @param CodeSystemId VALUE_SET表中的CODE_SYSTEM_ID
     */
    public String getMdmValueIdByCodeSystemId(String CodeSystemId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMdmValueIdByCodeSystemId.replace("?", CodeSystemId);
        }
        return new WnOracleSql().getMdmValueIdByCodeSystemId.replace("?", CodeSystemId);
    }

    //根据医保ID获取临床服务(全部类型)
    public String getInsurApprovalService(String ServiceTypeCode, String insuranceID, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInsurApprovalService.replace("?1", ServiceTypeCode).replace("?2", insuranceID).replace("?3", soid);
        }
        return new WnOracleSql().getInsurApprovalService.replace("?1", ServiceTypeCode).replace("?2", insuranceID).replace("?3", soid);
    }

    //根据医保ID获取临床服务(指定类型)
    public String getInsurApprovalServiceNoType(String insuranceID, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInsurApprovalServiceNoType.replace("?2", insuranceID).replace("?3", soid);
        }
        return new WnOracleSql().getInsurApprovalServiceNoType.replace("?2", insuranceID).replace("?3", soid);
    }

    //获取推荐症状(诊疗路径)
    public String getRecommendSymptom(String subjCode, String doctorUserName, String genderCode, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getRecommendSymptom.replace("?1", subjCode).replace("?2", doctorUserName).replace("?3", genderCode).replace("?4", soid);
        }
        return new WnOracleSql().getRecommendSymptom.replace("?1", subjCode).replace("?2", doctorUserName).replace("?3", genderCode).replace("?4", soid);
    }

    //获取推荐诊断(诊疗路径)
    public String getRecommendDiagnose(String subjCode, String doctorUserName, String genderCode, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getRecommendDiagnose.replace("?1", subjCode).replace("?2", doctorUserName).replace("?3", genderCode).replace("?4", soid);
        }
        return new WnOracleSql().getRecommendDiagnose.replace("?1", subjCode).replace("?2", doctorUserName).replace("?3", genderCode).replace("?4", soid);
    }

    /**
     * 获取主数据配置参数信息
     *
     * @param PARAM_NO 参数编号
     */
    public String getMdmParameter(String PARAM_NO) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMdmParameter.replace("?", PARAM_NO);
        }
        return new WnOracleSql().getMdmParameter.replace("?", PARAM_NO);
    }

    //根据患者姓名获取挂号记录
    public String getPatientRecordByName(String name) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getPatientRecordByName.replace("?", name);
        }
        return new WnOracleSql().getPatientRecordByName.replace("?", name);
    }

    //获取医保审批医嘱项列表
    public String getInsurOrderItemsByEncounterid(String encounterid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInsurOrderItemsByEncounterid.replace("?", encounterid);
        }
        return new WnOracleSql().getInsurOrderItemsByEncounterid.replace("?", encounterid);
    }

    //获取医保信息
    public String getInsuranceListByID(String id) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInsuranceListByID.replace("?", id);
        }
        return new WnOracleSql().getInsuranceListByID.replace("?", id);
    }

    //根据药品类型获取药品列表
    public String getDrugByPsychotropicsCode(String psychotropicsCode) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugByPsychotropicsCode.replace("?", psychotropicsCode);
        }
        return new WnOracleSql().getDrugByPsychotropicsCode.replace("?", psychotropicsCode);
    }

    /**
     * 查询需要测试的所有药品sql语句
     */
    public String getAllMedicineSql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllMedicineSql;
        }
        return new WnOracleSql().getAllMedicineSql;
    }

    /**
     * 大临床查询需要测试的所有药品sql语句
     */
    public String getAllMedicineInpatientSql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllMedicineInpatientSql;
        }
        return new WnOracleSql().getAllMedicineInpatientSql;
    }
    /**
     * 查询需要测试的所有检验sql语句
     */
    public String getAllLabSql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllLabSql;
        }
        return new WnOracleSql().getAllLabSql;
    }

    /**
     * 查询需要测试的所有病理sql语句
     */
    public String getAllPathologySql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllPathologySql;
        }
        return new WnOracleSql().getAllPathologySql;
    }

    /**
     * 查询需要测试的所有治疗sql语句
     */
    public String getAllTreatSql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllTreatSql;
        }
        return new WnOracleSql().getAllTreatSql;
    }

    /**
     * 查询需要测试的所有检查项目sql语句
     */
    public String getAllExamItemSql() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllExamItemSql;
        }
        return new WnOracleSql().getAllExamItemSql;
    }

    /**
     * 通过MEDICINE_ID获取CS_ID
     */
    public String getCsidByMedicineId(String MEDICINE_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCsidByMedicineId.replace("?", MEDICINE_ID);
        }
        return new WnOracleSql().getCsidByMedicineId.replace("?", MEDICINE_ID);
    }

    /**
     * 查询西药药品商品推荐剂量
     *
     * @param CS_ID CLINICAL_SERVICE_DRUG表中的CS_ID
     */
    public String getDrugRcmddDosage(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugRcmddDosage.replace("?", CS_ID);
        }
        return new WnOracleSql().getDrugRcmddDosage.replace("?", CS_ID);
    }

    /**
     * 查询西药药品商品推荐剂量单位
     *
     * @param CS_ID RCMDD_DOSAGE_UNIT_CODE表中的CS_ID
     */
    public String getDrugRcmddDosageUnitCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugRcmddDosageUnitCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getDrugRcmddDosageUnitCode.replace("?", CS_ID);
    }

    /**
     * 查询西药药品给药途径
     *
     * @param CS_ID CLINICAL_SERVICE_DRUG表中的CS_ID
     */
    public String getDrugDefaultUsageCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugDefaultUsageCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getDrugDefaultUsageCode.replace("?", CS_ID);
    }

    /**
     * 查询药品推荐给药途径类型代码
     *
     * @param MEDICINE_ID
     */
    public String getMedicineRcmddDosageRouteTypeCode(String MEDICINE_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMedicineRcmddDosageRouteTypeCode.replace("?", MEDICINE_ID);
        }
        return new WnOracleSql().getMedicineRcmddDosageRouteTypeCode.replace("?", MEDICINE_ID);
    }


    /**
     * 查询药品给药途径列表
     *
     * @param CS_ID MEDICATION_DOSAGE_ROUTE表中的CS_ID
     */
    public String getDosageRouteTypeCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDosageRouteTypeCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getDosageRouteTypeCode.replace("?", CS_ID);
    }

    /**
     * 查询药品商品推荐剂量
     *
     * @param CS_ID CLINICAL_SERVICE_DRUG表中的CS_ID
     */
    public String getHerbRcmddDosage(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getHerbRcmddDosage.replace("?", CS_ID);
        }
        return new WnOracleSql().getHerbRcmddDosage.replace("?", CS_ID);
    }

    /**
     * 查询草药药品商品推荐剂量单位
     *
     * @param CS_ID RCMDD_DOSAGE_UNIT_CODE表中的CS_ID
     */
    public String getHerbRcmddDosageUnitCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getHerbRcmddDosageUnitCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getHerbRcmddDosageUnitCode.replace("?", CS_ID);
    }

    /**
     * 查询药品通用名推荐剂量
     *
     * @param CS_ID CLINICAL_SERVICE_MEDICATION表中的CS_ID
     */
    public String getMedicationDosageConvFactor(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMedicationDosageConvFactor.replace("?", CS_ID);
        }
        return new WnOracleSql().getMedicationDosageConvFactor.replace("?", CS_ID);
    }

    /**
     * 查询药品通用名推荐剂量单位
     *
     * @param CS_ID CLINICAL_SERVICE_MEDICATION表中的CS_ID
     */
    public String getMedicationDosageUnitCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMedicationDosageUnitCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getMedicationDosageUnitCode.replace("?", CS_ID);
    }

    /**
     * 查询给药途径术语列表
     *
     * @param CS_ID
     * @return
     */
    public String getMedicationDosageRouteCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getMedicationDosageRouteCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getMedicationDosageRouteCode.replace("?", CS_ID);
    }


    /**
     * 查询西药药品配置的默认频次
     *
     * @param CS_ID
     * @return
     */
    public String getDrugDefaultFreqCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugDefaultFreqCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getDrugDefaultFreqCode.replace("?", CS_ID);
    }

    /**
     * 查询药品可用频次列表
     *
     * @param CS_ID
     * @return
     */
    public String getFreqCode(String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getFreqCode.replace("?", CS_ID);
        }
        return new WnOracleSql().getFreqCode.replace("?", CS_ID);
    }


    /**
     * 查询默认门诊包装单位
     *
     * @param MEDICINE_ID
     */
    public String getDefaultPackUnitCode(String MEDICINE_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDefaultPackUnitCode.replace("?", MEDICINE_ID);
        }
        return new WnOracleSql().getDefaultPackUnitCode.replace("?", MEDICINE_ID);
    }

    /**
     * 获取非皮试药品且有库存的药品
     */
    public String getNormalMedicialList(String orgName, String medicineType) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getNormalMedicialList.replace("?1", orgName).replace("?2", medicineType);
        }
        return new WnOracleSql().getNormalMedicialList.replace("?1", orgName).replace("?2", medicineType);
    }

    /**
     * 获取无库存非皮试药品
     */
    public String getOutOfStocklMedicineList(String orgName, String medicineType) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOutOfStocklMedicineList.replace("?1", orgName).replace("?2", medicineType);
        }
        return new WnOracleSql().getOutOfStocklMedicineList.replace("?1", orgName).replace("?2", medicineType);
    }


    /**
     * 获取所有需要医保审批的启用药品
     *
     * @param MED_INSTI_INSUR_ID 医保ID
     * @param soid               soid
     */
    public String getInsurApprovalMedicine(String MED_INSTI_INSUR_ID, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInsurApprovalMedicine.replace("?1", MED_INSTI_INSUR_ID).replace("?2", soid);
        }
        return new WnOracleSql().getInsurApprovalMedicine.replace("?1", MED_INSTI_INSUR_ID).replace("?2", soid);
    }


    /**
     * 获取皮试药品
     *
     * @param orgName            指定药房有库存
     * @param skinTestTypeString 皮试类型字符串，如(类型1，类型2,...)
     */
    public String getSkinTestMedicine(String orgName, String skinTestTypeString) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getSkinTestMedicine.replace("?1", orgName).replace("?2", skinTestTypeString);
        }
        return new WnOracleSql().getSkinTestMedicine.replace("?1", orgName).replace("?2", skinTestTypeString);
    }

    /**
     * 根据模板名字获取诊断段落是否可以为空提交
     */
    public String getEmrTsEmptySubmitFlag(String MrtName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmrTsEmptySubmitFlag.replace("?", MrtName);
        }
        return new WnOracleSql().getEmrTsEmptySubmitFlag.replace("?", MrtName);
    }

    /**
     * 成员计费_检查服务项目按个数区间计费_累计计费策略-csId获取价格列表
     *
     * @param soid
     * @param CS_ID
     * @return
     */
    public String getBsExamItemRangeSumByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsExamItemRangeSumByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsExamItemRangeSumByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检查服务项目按个数区间计费_不累计计费策略-csId获取价格列表
     */
    public String getBsExamItemRangeNoneSumByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsExamItemRangeNoneSumByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsExamItemRangeNoneSumByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检查服务项目按个数加收计费策略-根据csId获取价格列表
     */
    public String getBsExamItemRangeExtraFeeByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsExamItemRangeExtraFeeByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsExamItemRangeExtraFeeByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检查按项目明细合计计费策略-csId获取价格列表
     */
    public String getBsExamItemByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsExamItemByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsExamItemByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检验按指标个数区间_累计计费策略-csId获取价格列表
     */
    public String getBsLabtestIndexRangeSumByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsLabtestIndexRangeSumByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsLabtestIndexRangeSumByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检验按指标个数区间_不累计计费策略-csId获取价格列表
     */
    public String getBsLabtestIdxRanNoneSumByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsLabtestIdxRanNoneSumByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsLabtestIdxRanNoneSumByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
     */
    public String getBsLabtestIdxRangeExFeeByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsLabtestIdxRangeExFeeByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsLabtestIdxRangeExFeeByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
     */
    public String getBsLabtestIndexCostByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsLabtestIndexCostByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsLabtestIndexCostByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 组合计费-根据csId获取价格列表
     */
    public String getBsCompositeCostByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsCompositeCostByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsCompositeCostByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 本服务计费-根据csId获取价格
     */
    public String getBsSelfCostByCsid(String soid, String CS_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBsSelfCostByCsid.replace("?1", soid).replace("?2", CS_ID);
        }
        return new WnOracleSql().getBsSelfCostByCsid.replace("?1", soid).replace("?2", CS_ID);
    }

    /**
     * 根据计费策略获取临床服务
     */
    public String getServiceListByStrategy(String soid, String StrategyClassCode) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getServiceListByStrategy.replace("?1", soid).replace("?2", StrategyClassCode);
        }
        return new WnOracleSql().getServiceListByStrategy.replace("?1", soid).replace("?2", StrategyClassCode);
    }


    /**
     * 根据计费策略获取临床服务,ServiceTypeCode不为空
     */
    public String getServiceListByStrategyAndServiceTypeCode(String soid, String StrategyClassCode, String ServiceTypeCode) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getServiceListByStrategyAndServiceTypeCode.replace("?1", soid).replace("?2", StrategyClassCode).replace("?3", ServiceTypeCode);
        }
        return new WnOracleSql().getServiceListByStrategyAndServiceTypeCode.replace("?1", soid).replace("?2", StrategyClassCode).replace("?3", ServiceTypeCode);
    }

    /**
     * 根据临床服务类型代码和临床服务状态获取临床服务
     */
    public String getServiceListByCsTypeCodeAndCsStatus(String CsTypeCode, String CsStatus) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getServiceListByCsStatusAndCsTypeCode.replace("?1", CsStatus).replace("?2", CsTypeCode);
        }
        return new WnOracleSql().getServiceListByCsStatusAndCsTypeCode.replace("?1", CsStatus).replace("?2", CsTypeCode);
    }

    /**
     * 根据CSID更新临床服务状态
     */
    public String updateClinicalServiceByCsName(String CsStatus, String CsName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().updateClinicalServiceByCsName.replace("?1", CsStatus).replace("?2", "'" + CsName + "'");
        }
        return new WnOracleSql().updateClinicalServiceByCsName.replace("?1", CsStatus).replace("?2", "'" + CsName + "'");
    }

    /**
     * 获取收藏列表所有西药的MEDICINE_ID
     */
    public String getAllCommonMedId() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getAllCommonMedId;
        }
        return new WnOracleSql().getAllCommonMedId;
    }

    /***根据类型和soid获取设置的联动规则的值，为json格式**/
    public String getLinkAgeByType(String LinkType, String SOID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getLinkAgeByType.replace("?1", LinkType).replace("?2", SOID);
        }
        return new WnOracleSql().getLinkAgeByType.replace("?1", LinkType).replace("?2", SOID);
    }

    /**
     * 根据默认给药途径查找药房可开立的药品
     ***/
    public String getDrugByDefaultRoute(String SOID, String ORG_NAME, String DEFAULT_USAGE_CODE) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getDrugByDefaultRoute.replace("?1", SOID).replace("?2", ORG_NAME).replace("?3", DEFAULT_USAGE_CODE);
        }
        return new WnOracleSql().getDrugByDefaultRoute.replace("?1", SOID).replace("?2", ORG_NAME).replace("?3", DEFAULT_USAGE_CODE);

    }

    /**
     * 根据CSID查找CSNAME
     ***/
    public String getCsNameByCsId(String CsId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCsNameByCsId.replace("?1", CsId);
        }
        return new WnOracleSql().getCsNameByCsId.replace("?1", CsId);

    }

    /***根据his挂号序号查找联动的项目***/
    public String getLinkInfoByGhxh(String ghxh, String soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getLinkInfoByGhxh.replace("?1", ghxh).replace("?2", soid);
        }
        return new WnOracleSql().getLinkInfoByGhxh.replace("?1", ghxh).replace("?2", soid);


    }

    /***根据EXAM_ITEM_ID在EXAMINATION_ITEM中查找EXAM_ITEM_NAME***/
    public String getExamItemNameByExamItemId(String ExamItemId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getExamItemNameByExamItemId.replace("?1", ExamItemId);
        }
        return new WnOracleSql().getExamItemNameByExamItemId.replace("?1", ExamItemId);
    }

    //临床域867684方案中根据年龄段查找最小年龄
    public String getBeginAgeBySet(String AGE_SEGMENT_CODE, String Soid) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBeginAgeBySet.replace("?1", AGE_SEGMENT_CODE).replace("?2", Soid);
        }
        return new WnOracleSql().getBeginAgeBySet.replace("?1", AGE_SEGMENT_CODE).replace("?2", Soid);
    }

    /***根据患者姓名修改保密等级***/
    public String getEnCounterIdbyFullName(String Full_name) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEnCounterIdbyFullName.replace("?1", Full_name);
        }
        return new WnOracleSql().getEnCounterIdbyFullName.replace("?1", Full_name);
    }

    /***根据ENCOUNTER_ID查询OUTPATIENT_CONTACT_ID***/
    public String getCONTACTIdbyENCOUNTERId(String ENCOUNTER_ID) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCONTACTIdbyENCOUNTERId.replace("?1", ENCOUNTER_ID);
        }
        return new WnOracleSql().getCONTACTIdbyENCOUNTERId.replace("?1", ENCOUNTER_ID);
    }

    /***根据ENCOUNTER_ID查询OUTPATIENT_CONTACT_ID***/
    public String getEmployNoByUsercode(String user_code) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmployNoByUsercode.replace("?1", user_code);
        }
        return new WnOracleSql().getEmployNoByUsercode.replace("?1", user_code);
    }
    
    /***根据user_code查询employid***/
    public String getEmployIdByUsercode(String user_code) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmployIdByUsercode.replace("?1", user_code);
        }
        return new WnOracleSql().getEmployNoByUsercode.replace("?1", user_code);
    }

    /*** 根据住院病历模板查找病历目录分类  ***/
    public String getInpEmrClassName(String InpMrtName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpEmrClassName.replace("?1", InpMrtName);
        }
        return new WnOracleSql().getInpEmrClassName.replace("?1", InpMrtName);
    }

    /*** 根据住院病历阅改等级代码查询住院病历审阅流程标识ID ***/
    public String getInpEmrReviewProcessId(String InpEmrReviewLevelCode) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpEmrReviewProcessId.replace("?1", InpEmrReviewLevelCode);
        }
        return new WnOracleSql().getInpEmrReviewProcessId.replace("?1", InpEmrReviewLevelCode);
    }

    /*** 根据住院病历模板名称获取模板的监控类型编码 ***/
    public String getInpMrtMonitorNo(String inpMrtName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpMrtMonitorNo.replace("?1", inpMrtName);
        }
        return new WnOracleSql().getInpMrtMonitorNo.replace("?1", inpMrtName);
    }


    /*** 查询住院病历三级阅改流程与病历监控类型关系是否存在  ***/
    public String getInpEmrReviewMonitoring(String InpMrtMonitoringNo) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpEmrReviewMonitoring.replace("?1", InpMrtMonitoringNo);
        }
        return new WnOracleSql().getInpEmrReviewMonitoring.replace("?1", InpMrtMonitoringNo);
    }

    /*** 根据概念域id查找护理文书下拉框的选项值  ***/
    public String getChartDropdownValue(String chartConceptDomainId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getChartDropdownValue.replace("?", chartConceptDomainId);
        }
        return new WnOracleSql().getChartDropdownValue.replace("?", chartConceptDomainId);
    }

    /*** 根据CHART_TEMPLATE_NAME查找CHART_TEMPLATE_ID、CHART_TEMPLATE_CLASS_CODE、CHART_TEMPLATE_TYPE_CODE ***/
    public String getChartTemplate(String chartTemplateName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getChartTemplate.replace("?1", chartTemplateName);
        }
        return new WnOracleSql().getChartTemplate.replace("?1", chartTemplateName);
    }

    /*** 根据患者姓名获取EnCounterId、BizRoleID、CURRENT_DEPT_ID、CURRENT_WARD_ID ***/
    public String getEncounterInfoByFullName(String fullName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEncounterInfoByFullName.replace("?", fullName);
        }
        return new WnOracleSql().getEncounterInfoByFullName.replace("?", fullName);
    }

    /**
     * 根据ParamNo查找ParamConfigId
     ***/
    public String getParamConfigIdByParamNo(String paramNo) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getParamConfigIdByParamNo.replace("?1", paramNo);
        }
        return new WnOracleSql().getParamConfigIdByParamNo.replace("?1", paramNo);
    }

    /*** 根据护理文书字段项ID获取字段的最大最小值 ***/
    public String getChartItemLimit(String chartItemId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getChartItemLimit.replace("?1", chartItemId);
        }
        return new WnOracleSql().getChartItemLimit.replace("?1", chartItemId);
    }

    /*** 查询临床配置参数值 ***/
    public String getCliSettingValue(String cliSettingNo) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCliSettingValue.replace("?1", cliSettingNo);
        }
        return new WnOracleSql().getCliSettingValue.replace("?1", cliSettingNo);
    }

    /*** 查询临床配置参数值 ***/
    public String updateCliSettingValueByCliSettingNo(String CliSettingNo, String value) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().updateCliSettingValueByCliSettingNo.replace("?1", CliSettingNo).replace("?2", value);
        }
        return new WnOracleSql().updateCliSettingValueByCliSettingNo.replace("?1", CliSettingNo).replace("?2", value);
    }

    /*** 查询住院病区的空床位 ***/
    public String getEmptyBedNoByOrgName() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmptyBedNoByOrgName;
        }
        return new WnOracleSql().getEmptyBedNoByOrgName;
    }

    /*** 查询最大床位号 ***/
    public String getBedNo() {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBedNo;
        }
        return new WnOracleSql().getBedNo;
    }

    //查询住院就诊登记流水号zyh
    public String getInpatEncRegSeqNo(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatEncRegSeqNo.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatEncRegSeqNo.replace("?1", EncounterId);
    }

    //查询开始日期ksrq，停止日期jsrq，开立日期时间lrrq/rq/ysqrsj，医嘱天数ts
    public String getTime(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getTime.replace("?1", EncounterId);
        }
        return new WnOracleSql().getTime.replace("?1", EncounterId);
    }

    //查询临床服务编码/药品编码bm，临床服务名称yzmc
    public String getCs(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCs.replace("?1", EncounterId);
        }
        return new WnOracleSql().getCs.replace("?1", EncounterId);
    }

    //查询开立数量cs，医嘱项内容kdmc，医嘱项标识cfh_wn，嘱托yszt
    public String getCliOrderItemId(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getCliOrderItemId.replace("?1", EncounterId);
        }
        return new WnOracleSql().getCliOrderItemId.replace("?1", EncounterId);
    }

    //查询药品 开立单位dw,商品名pm
    public String getBasicPackUnitCode(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getBasicPackUnitCode.replace("?1", EncounterId);
        }
        return new WnOracleSql().getBasicPackUnitCode.replace("?1", EncounterId);
    }

    //查询开立医生ysbm/lrrbm
    public String getEmployeeNo(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getEmployeeNo.replace("?1", EncounterId);
        }
        return new WnOracleSql().getEmployeeNo.replace("?1", EncounterId);
    }

    //查询开立病区bq/bqks
    public String getOrgNo(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOrgNo.replace("?1", EncounterId);
        }
        return new WnOracleSql().getOrgNo.replace("?1", EncounterId);
    }

    //查询开立科室kdks
    public String getOrgNoKs(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOrgNoKs.replace("?1", EncounterId);
        }
        return new WnOracleSql().getOrgNoKs.replace("?1", EncounterId);
    }
    //查询执行科室ks
    public String getOrgNoZx(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOrgNoZx.replace("?1", EncounterId);
        }
        return new WnOracleSql().getOrgNoZx.replace("?1", EncounterId);
    }
    //查询频次代码
    public String geValueDesc(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().geValueDesc.replace("?1", EncounterId);
        }
        return new WnOracleSql().geValueDesc.replace("?1", EncounterId);
    }

    //查询单位dw
    public String geValueDescDw(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().geValueDescDw.replace("?1", EncounterId);
        }
        return new WnOracleSql().geValueDescDw.replace("?1", EncounterId);
    }

    //查询医嘱类型lx
    public String geValueNo(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().geValueNo.replace("?1", EncounterId);
        }
        return new WnOracleSql().geValueNo.replace("?1", EncounterId);
    }

    //查询包装规格 hldw,yfyl
    public String getPackSpec(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getPackSpec.replace("?1", EncounterId);
        }
        return new WnOracleSql().getPackSpec.replace("?1", EncounterId);
    }

    //查询药品 零售价jg1
    public String getPetailPrice(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getPetailPrice.replace("?1", EncounterId);
        }
        return new WnOracleSql().getPetailPrice.replace("?1", EncounterId);
    }
    //查询收费项目 零售价jg1，名称，编码
    public String getPetailPriceXm(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getPetailPriceXm.replace("?1", EncounterId);
        }
        return new WnOracleSql().getPetailPriceXm.replace("?1", EncounterId);
    }

    //查询姓名xm
    public String getFullName(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getFullName.replace("?1", EncounterId);
        }
        return new WnOracleSql().getFullName.replace("?1", EncounterId);
    }

    //查询草药 给药途径yf，制出总量zl，剂量(每顿剂量)cl，服药周期付数jr
    public String getHerbOrder(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getHerbOrder.replace("?1", EncounterId);
        }
        return new WnOracleSql().getHerbOrder.replace("?1", EncounterId);
    }

    //查询护士签收人编码qrhs，时间hsqrsj
    public String getHandledBy(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getHandledBy.replace("?1", EncounterId);
        }
        return new WnOracleSql().getHandledBy.replace("?1", EncounterId);
    }

    //查询住院发药申请标识
    public String getInpmeddispreqid(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpmeddispreqid.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpmeddispreqid.replace("?1", EncounterId);
    }

    //查询住院发药计划标识，住院发药剂量，住院发药数量
    public String getInpmeddispplanid(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpmeddispplanid.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpmeddispplanid.replace("?1", EncounterId);
    }

    //查询申请人
    public String getOperatedBy(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOperatedBy.replace("?1", EncounterId);
        }
        return new WnOracleSql().getOperatedBy.replace("?1", EncounterId);
    }

    //查询国籍
    public String getInpatientInfo(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientInfo.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientInfo.replace("?1", EncounterId);
    }
    //查询性别
    public String getInpatientGender(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientGender.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientGender.replace("?1", EncounterId);
    }
    //查询名族
    public String getInpatientNation(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientNation.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientNation.replace("?1", EncounterId);
    }
    //查询入区途径
    public String getInpatientAdmitted(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientAdmitted.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientAdmitted.replace("?1", EncounterId);
    }
    //查询身份证号码，病案号，就诊登记流水号
    public String getInpatientRecord(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientRecord.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientRecord.replace("?1", EncounterId);
    }

    //查询床位
    public String getInpatientBed(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientBed.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientBed.replace("?1", EncounterId);
    }

    //查询住院科室
    public String getInpatientRoom(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientRoom.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientRoom.replace("?1", EncounterId);
    }

    //查询住院病区
    public String getInpatientWard(String EncounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatientWard.replace("?1", EncounterId);
        }
        return new WnOracleSql().getInpatientWard.replace("?1", EncounterId);
    }

    //根据组织名称和组织code获取组织ID
    public String getOrgIdByOrgName(String orgName, String orgNo) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getOrgIdByOrgName.replace("?1", orgName).replace("?2", orgNo);
        }
        return new WnOracleSql().getOrgIdByOrgName.replace("?1", orgName).replace("?2", orgNo);
    }

    //根据病房编码和病区名查询病房ID
    public String getInpatRoomIdByRoomName(String InpatRoomName, String orgName, String orgNo) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getInpatRoomIdByRoomName.replace("?1", InpatRoomName).replace("?2", orgName).replace("?3", orgNo);
        }
        return new WnOracleSql().getInpatRoomIdByRoomName.replace("?1", InpatRoomName).replace("?2", orgName).replace("?3", orgNo);
    }

    /***根据ENCOUNTER_ID查询PHARMACY_ID***/
    public String getPharmacyIdbyEncounterId(String encounterId) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getPharmacyIdbyEncounterId.replace("?1", encounterId);
        }
        return new WnOracleSql().getPharmacyIdbyEncounterId.replace("?1", encounterId);
    }

    
    /****根据PatientName查找SeqNo,OMRN***/
    public String getSeqNoByPatientName(String patientName) {
        if (this.dbType.equals("SQLSERVER")) {
            return new WnSqlserverSql().getSeqNoByPatientName.replace("?1", patientName);
        }
        return new WnOracleSql().getSeqNoByPatientName.replace("?1", patientName);
    }
    public class WnSqlserverSql {

        //临床域867684方案中根据年龄段查找最小年龄
        String getBeginAgeBySet = "SELECT AGE_RANGE_BEGIN_VALUE FROM WINDBA.AGE_SEGMENT_SETTINGS WHERE AGE_SEGMENT_CODE = '?1' AND IS_DEL = 0 "
                + "AND AGE_SEGMENT_CONFIG_CODE = '867684' AND HOSPITAL_SOID = '?2'";
        //根据患者姓名获取挂号记录
        String getPatientRecordByName = "SELECT * FROM WINDBA.OUTPATIENT_RECORD WHERE FULL_NAME = '?' ORDER BY CREATED_AT DESC";

        //根据CSID获取medicineid
        String getMedicineIdbyCSID = "SELECT TOP 1 * FROM WINDBA.MEDICINE WHERE CS_ID = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '?2'";

        //获取医保审批医嘱项列表
        String getInsurOrderItemsByEncounterid = "SELECT INSUR.* FROM "
                + "WINDBA.CLINICAL_ORDER O "
                + "INNER JOIN WINDBA.CLINICAL_ORDER_ITEM ITEM ON O.CLI_ORDER_ID = ITEM.CLI_ORDER_ID "
                + "INNER JOIN WINDBA.PURCH_ORDER_MED_INSUR_APPROVAL INSUR ON ITEM.CLI_ORDER_ITEM_ID = INSUR.CLI_ORDER_ITEM_ID "
                + "WHERE O.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND INSUR.IS_DEL = 0 AND O.ENCOUNTER_ID = '?'";


        //获取医保信息
        String getInsuranceListByID = "SELECT * FROM WINDBA.MED_INSTI_MEDICAL_INSURANCE WHERE MED_INSTI_INSUR_ID = ?";

        //根据药品类型获取药品列表
        String getDrugByPsychotropicsCode = "SELECT * FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND PSYCHOTROPICS_CODE = ? AND CS_ID IN (SELECT CS_ID FROM WINDBA.MEDICINE WHERE IS_DEL = 0)";

        //根据医保ID获取临床服务(指定类型)
        String getInsurApprovalServiceNoType =
                "SELECT "
                        + "CS.CS_ID, "
                        + "CS.CS_NO, "
                        + "(SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE, "
                        + "CS.CS_NAME, INSUR.MED_INSTI_INSUR_ID,"
                        + "ITEM.CHARGING_ITEM_ID, "
                        + "ITEM.CHARGING_ITEM_NO, "
                        + "ITEM.CHARGING_ITEM_NAME "
                        + "FROM "
                        + "WINDBA.MED_INSUR_APPROVAL INSUR "
                        + "INNER JOIN WINDBA.MED_INSTI_CHARGING_ITEM X ON X.MED_INSTI_CHARGING_ITEM_ID = INSUR.MED_INSTI_CHARGING_ITEM_ID "
                        + "INNER JOIN WINDBA.CHARGING_ITEM ITEM ON X.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID "
                        + "INNER JOIN WINDBA.BS_SELF BS ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID "
                        + "INNER JOIN WINDBA.CLINICAL_SERVICE CS ON CS.CS_ID = BS.CS_ID "
                        + "WHERE ITEM.IS_DEL = 0 "
                        + "AND INSUR.IS_DEL = 0 "
                        + "AND X.IS_DEL = 0 "
                        + "AND BS.IS_DEL = 0 "
                        + "AND CS.IS_DEL = 0 "
                        + "AND ITEM.HOSPITAL_SOID = ?3 "
                        + "AND INSUR.HOSPITAL_SOID = ?3 "
                        + "AND X.HOSPITAL_SOID = ?3 "
                        + "AND BS.HOSPITAL_SOID = ?3 "
                        + "AND CS.HOSPITAL_SOID = ?3 "
                        + "AND X.ENABLED_FLAG = 98175 "
                        + "AND ITEM.ENABLED_FLAG = 98175 "
                        + "AND INSUR.MED_INSTI_INSUR_ID = '?2' "
                        + "AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() "
                        + "AND BS.ENABLED_FLAG = 98175 "
                        + "AND BS.ENCOUNTER_TYPE_CODE = 138138 "
                        + "AND CS.CS_STATUS = 98360 "
                        + "AND COALESCE (CS_SET_FLAG,98176)<>98175";


        //根据医保ID获取临床服务(全部类型)
        String getInsurApprovalService = getInsurApprovalServiceNoType + " AND CS.CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = ?1 OR PARENT_VALUE_ID = ?1)";


        // 根据科室和医生获取推荐主诉(诊疗路径)
        String getRecommendSymptom = "SELECT * FROM WINDBA.TREATMENT_PATHWAY_SYMPTOM " + "WHERE "
                + "IS_DEL = 0 "
                + "AND ENABLED_FLAG = 98360 "
                + "AND DEPT_ID = (SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR_ID = (SELECT EMPLOYEE_ID FROM WINDBA.EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?2' AND IS_DEL = 0 AND HOSPITAL_SOID = ?4) "
                + "AND (GENDER_CODE = ?3 OR GENDER_CODE IS NULL) "
                + "AND HOSPITAL_SOID = ?4 "
                + "ORDER BY SEQ_NO ";

        // 获取推荐诊断(诊疗路径)
        String getRecommendDiagnose = "SELECT DIAGNOSIS.DIAGNOSIS_CODE,(SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = DIAGNOSIS.DIAGNOSIS_CODE) DIAGNOSE_NAME,DIAGNOSIS.GENDER_CODE,DOCTOR.SEQ_NO "
                + "FROM WINDBA.TREATMENT_PATHWAY_DIAGNOSIS DIAGNOSIS "
                + "INNER JOIN WINDBA.DOCTOR_DIAGNOSIS DOCTOR ON DIAGNOSIS.TREAT_PATH_DIAGNOSIS_ID = DOCTOR.TREAT_PATH_DIAGNOSIS_ID "
                + "WHERE "
                + "DIAGNOSIS.IS_DEL = 0 "
                + "AND DOCTOR.IS_DEL = 0 "
                + "AND DIAGNOSIS.HOSPITAL_SOID = ?4 "
                + "AND DOCTOR.HOSPITAL_SOID = ?4 "
                + "AND DIAGNOSIS.ENABLED_FLAG = 98360 "
                + "AND DIAGNOSIS.DEPT_ID = (SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR.DEPT_ID = (SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR.DOCTOR_ID = (SELECT EMPLOYEE_ID FROM WINDBA.EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?2' AND IS_DEL = 0 AND HOSPITAL_SOID = ?4) "
                + "AND (DIAGNOSIS.GENDER_CODE = ?3 OR DIAGNOSIS.GENDER_CODE IS NULL) "
                + "ORDER BY DOCTOR.SEQ_NO";


        /**
         * 根据value_id查询主数据对应描述
         */
        String getMdmDescByValueId = "SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = ?";

        /***根据org_id查找org_name**/
        String getOrgNameByOrgId = "SELECT ORG_NAME,ORG_NO FROM WINDBA.ORGANIZATION WHERE ORG_ID = '?1'";

        /**
         * 查找员工信息
         **/
        String getEmployeeNameByEmployeeId = "SELECT EMPLOYEE_NAME,EMPLOYEE_NO FROM WINDBA.EMPLOYEE_INFO WHERE EMPLOYEE_ID = '?1'";

        /**
         * 查找员工信息
         **/
        String getEmployeeIdByEmployeeNo = "SELECT EMPLOYEE_ID FROM WINDBA.EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?1' AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据code_system_id查询value_id列表
         */
        String getMdmValueIdByCodeSystemId = "SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE CODE_SYSTEM_ID = ? AND VALUE_STATUS = 98360";


        /**
         * 获取主数据配置参数信息
         */
        String getMdmParameter = "SELECT P.PARAM_NO,P.PARAM_DESC,PCV.PARAM_VALUE FROM WINDBA.PARAMETER P INNER JOIN WINDBA.PARAMETER_CONFIGURATION PC ON P.PARAM_ID = PC.PARAM_ID INNER JOIN WINDBA.PARAMETER_VALUE_CONFIGURATION PCV ON PC.PARAM_CONFIG_ID = PCV.PARAM_CONFIG_ID WHERE P.IS_DEL = 0 AND PC.IS_DEL = 0 AND PCV.IS_DEL = 0 AND PC.PARAM_APP_SCOPE_TYPE_CODE = 256523 AND P.PARAM_NO = '?'";

        /**
         * 大临床查询需要测试的所有药品sql语句
         */
        String getAllMedicineInpatientSql = "SELECT '药品' TYPE,A.MEDICINE_ID ID,A.CS_ID CS_ID,A.COMMODITY_NAME_CHINESE NAME,A.PACK_SPEC PACKAGE,B.AST_REQIRED_FLAG SKINTESTFLAG,A.MED_EXT_REF_ID FROM WINDBA.MEDICINE A LEFT JOIN WINDBA.MEDICINE_DETAIL B ON A.MEDICINE_ID=B.MEDICINE_ID\n" +
                "WHERE A.IS_DEL = 0 AND A.COMMODITY_ENABLE_FLAG = 98360 AND B.IS_DEL=0 AND A.HOSPITAL_SOID='" + Data.hospital_soid + "'";


        /**
         * 查询需要测试的所有药品sql语句
         */
        String getAllMedicineSql = "SELECT '药品' TYPE,MEDICINE_ID ID,CS_ID CS_ID,COMMODITY_NAME_CHINESE NAME,PACK_SPEC PACKAGE FROM WINDBA.MEDICINE WHERE IS_DEL = 0 AND COMMODITY_ENABLE_FLAG = 98360 AND HOSPITAL_SOID='" + Data.hospital_soid + "'";

        /**
         * 查询需要测试的所有检验sql语句
         */
        String getAllLabSql = "SELECT '检验' TYPE,CS_ID ID,CS_NAME NAME FROM WINDBA.CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = 98071 OR PARENT_VALUE_ID = 98071) AND HOSPITAL_SOID='" + Data.hospital_soid + "'";

        /**
         * 查询需要测试的所有病理sql语句
         */
        String getAllPathologySql = "SELECT '病理' TYPE,CS_ID ID,CS_NAME NAME FROM WINDBA.CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = 98088 OR PARENT_VALUE_ID = 98088) AND HOSPITAL_SOID='" + Data.hospital_soid + "'";

        /**
         * 查询需要测试的所有治疗sql语句
         */
        String getAllTreatSql = "SELECT '治疗' TYPE,CS_ID ID,CS_NAME NAME FROM WINDBA.CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = 98098 OR PARENT_VALUE_ID = 98098) AND HOSPITAL_SOID='" + Data.hospital_soid + "'";

        /**
         * 查询需要测试的所有检查项目sql语句
         */
        String getAllExamItemSql = "SELECT '检查项目' TYPE,EXAM_ITEM_ID ID,EXAM_ITEM_NAME NAME FROM WINDBA.EXAMINATION_ITEM WHERE IS_DEL = 0 AND HOSPITAL_SOID='" + Data.hospital_soid + "'";

        /**
         * 查询西药药品商品推荐剂量
         */
        String getDrugRcmddDosage = "SELECT RCMDD_DOSAGE FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE CS_ID = ?";

        /**
         * 查询西药药品商品推荐剂量单位
         */
        String getDrugRcmddDosageUnitCode = "SELECT RCMDD_DOSAGE_UNIT_CODE FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE CS_ID = ?";

        /**
         * 查询药品给药途径
         */
        String getDrugDefaultUsageCode = "SELECT DEFAULT_USAGE_CODE FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品推荐给药途径类型代码
         */
        String getMedicineRcmddDosageRouteTypeCode = "SELECT RCMDD_DOSAGE_ROUTE_TYPE_CODE FROM WINDBA.MEDICINE_RECOMMENDED_DOSAGE WHERE IS_DEL = 0 AND MEDICINE_ID = ?";

        /**
         * 查询西药药品配置的默认频次
         */
        String getDrugDefaultFreqCode = "SELECT DEFAULT_FREQ_CODE FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品可用频次列表
         */
        String getFreqCode = "SELECT FREQ_CODE FROM WINDBA.MEDICATION_FREQUENCY WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品给药途径列表
         */
        String getDosageRouteTypeCode = "SELECT DOSAGE_ROUTE_TYPE_CODE FROM WINDBA.MEDICATION_DOSAGE_ROUTE WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询草药药品商品推荐剂量
         */
        String getHerbRcmddDosage = "SELECT RCMDD_DOSAGE FROM WINDBA.CLINICAL_SERVICE_HERB WHERE CS_ID = ?";

        /**
         * 查询草药药品商品推荐剂量单位
         */
        String getHerbRcmddDosageUnitCode = "SELECT RCMDD_DOSAGE_UNIT_CODE FROM WINDBA.CLINICAL_SERVICE_HERB WHERE CS_ID = ?";

        /**
         * 查询药品通用名推荐剂量
         */
        String getMedicationDosageConvFactor = "SELECT DOSAGE_CONV_FACTOR FROM WINDBA.CLINICAL_SERVICE_MEDICATION WHERE CS_ID = ?";

        /**
         * 查询药品通用名推荐剂量单位
         */
        String getMedicationDosageUnitCode = "SELECT DOSAGE_UNIT_CODE FROM WINDBA.CLINICAL_SERVICE_MEDICATION WHERE CS_ID = ?";

        /**
         * 查询给药途径术语列表
         */
        String getMedicationDosageRouteCode = "SELECT D.DOSAGE_ROUTE_CODE FROM WINDBA.CLINICAL_SERVICE_MEDICATION M LEFT JOIN WINDBA.DOSAGE_FORM_DOSAGE_ROUTE D ON M.MEDICINE_DOSAGE_FORM_CODE = D.DOSAGE_FORM_CODE WHERE M.IS_DEL = 0 AND D.IS_DEL = 0 AND M.CS_ID = ?";

        /**
         * 查询默认门诊包装单位
         */
        String getDefaultPackUnitCode = "SELECT T.PACK_UNIT_CODE FROM WINDBA.MEDICINE_PACKING_UNIT T LEFT JOIN WINDBA.MEDICINE_PACK_UNIT_USE_TYPE T1 ON T.MEDICINE_PACK_UNIT_ID = T1.MEDICINE_PACK_UNIT_ID WHERE T1.PACK_UNIT_USE_TYPE_CODE = 256147 AND MEDICINE_ID = ?";

        /**
         * 获取非皮试非自费药品且有库存的药品
         */
        String getNormalMedicialList = "SELECT MED.CS_ID,ORG.ORG_NO ORG_NO,MED.MEDICINE_PRIMARY_NO IDM,PRI.RETAIL_PRICE,MED.RETAIL_PACK_CONV_FACTOR,ORG.ORG_NAME ORG,MED.MEDICINE_ID,MED.COMMODITY_NAME_CHINESE NAME,MEDICATION.CADN_CHINESE CS_NAME,STOCK.MEDICINE_STOCK_QTY QTY,MED.PACK_SPEC PACK,"
                + "(SELECT TOP 1 CS.CS_NO FROM WINDBA.CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID) CS_NO FROM WINDBA.MEDICINE_PRICE PRI,WINDBA.ORGANIZATION ORG,WINDBA.MEDICINE_STOCK STOCK,WINDBA.MEDICINE MED,WINDBA.CLINICAL_SERVICE_MEDICATION MEDICATION "
                + "WHERE ORG.ORG_ID = STOCK.ORG_ID AND PRI.MEDICINE_ID = MED.MEDICINE_ID AND STOCK.MEDICINE_ID = MED.MEDICINE_ID AND MED.CS_ID = MEDICATION.CS_ID AND PRI.IS_DEL = 0 AND ORG.IS_DEL = 0 AND STOCK.IS_DEL = 0 AND MED.IS_DEL = 0 AND MEDICATION.IS_DEL = 0 "
                + "AND STOCK.MEDICINE_STOCK_QTY > 10 AND ORG.ORG_NAME = '?1' AND MEDICATION.MEDICINE_TYPE_CODE = ?2 AND MEDICATION.CS_ID NOT IN (SELECT CS_ID FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE AST_REQIRED_FLAG = 98175) AND MED.COMMODITY_ENABLE_FLAG = 98360 AND PRI.RETAIL_PRICE>0 AND MED.MEDICINE_ID NOT IN ( SELECT MEDICINE_ID FROM WINDBA.MEDICINE_HOSPITAL_SETTINGS WHERE OUTP_SELF_PAYMENT_FLAG = 98175 )";


        /**
         * 获取非皮试无库存药品
         */
        String getOutOfStocklMedicineList = "SELECT MED.CS_ID,ORG.ORG_NO ORG_NO,MED.MEDICINE_PRIMARY_NO IDM,PRI.RETAIL_PRICE,ORG.ORG_NAME ORG,MED.MEDICINE_ID,MED.COMMODITY_NAME_CHINESE NAME,STOCK.MEDICINE_STOCK_QTY QTY,MED.PACK_SPEC PACK,"
                + "(SELECT TOP 1 CS.CS_NO FROM WINDBA.CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID) CS_NO FROM WINDBA.MEDICINE_PRICE PRI,WINDBA.ORGANIZATION ORG,WINDBA.MEDICINE_STOCK STOCK,WINDBA.MEDICINE MED,WINDBA.CLINICAL_SERVICE_MEDICATION MEDICATION "
                + "WHERE ORG.ORG_ID = STOCK.ORG_ID AND PRI.MEDICINE_ID = MED.MEDICINE_ID AND STOCK.MEDICINE_ID = MED.MEDICINE_ID AND MED.CS_ID = MEDICATION.CS_ID AND PRI.IS_DEL = 0 AND ORG.IS_DEL = 0 AND STOCK.IS_DEL = 0 AND MED.IS_DEL = 0 AND MEDICATION.IS_DEL = 0 "
                + "AND STOCK.MEDICINE_STOCK_QTY = 0 AND ORG.ORG_NAME = '?1' AND MEDICATION.MEDICINE_TYPE_CODE = ?2 AND MEDICATION.CS_ID NOT IN (SELECT CS_ID FROM WINDBA.CLINICAL_SERVICE_DRUG WHERE AST_REQIRED_FLAG = 98175) AND MED.COMMODITY_ENABLE_FLAG = 98360 AND PRI.RETAIL_PRICE>0 AND MED.MEDICINE_ID NOT IN ( SELECT MEDICINE_ID FROM WINDBA.MEDICINE_HOSPITAL_SETTINGS WHERE OUTP_SELF_PAYMENT_FLAG = 98175 )";


        /**
         * 获取需要医保审批的启用药品
         */
        String getInsurApprovalMedicine = "SELECT MED.*,INS.MED_INSTI_INSUR_ID,INS.APPROVAL_PROMPT_MESSAGE FROM WINDBA.MED_INSUR_APPROVAL INS INNER JOIN WINDBA.MEDICINE MED ON MED.MEDICINE_ID = INS.MED_INSTI_COMMODITY_ID WHERE INS.MED_INSTI_INSUR_ID = '?1' AND INS.IS_DEL = 0 AND INS.HOSPITAL_SOID = ?2 AND MED.IS_DEL = 0 AND MED.HOSPITAL_SOID = ?2 AND MED.COMMODITY_ENABLE_FLAG = 98360";


        /**
         * 获取皮试药品
         */
        String getSkinTestMedicine = "SELECT MED.MEDICINE_ID MEDICINE_ID, MED.COMMODITY_NAME_CHINESE NAME, MED.PACK_SPEC PACK, MED.COMMODITY_NAME_CHINESE SKIN_TEST_NAME, DRUG.AST_TYPE_CODE SKIN_TEST_TYPE, STOCK.MEDICINE_STOCK_QTY QTY,(SELECT TOP 1 CS.CS_NO FROM WINDBA.CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID) CS_NO,(SELECT TOP 1 VALUE_DESC FROM WINDBA.VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = (SELECT TOP 1 ALLERGY.MEDICINE_ALLERGY_CODE FROM WINDBA.MEDICINE_ALLERGY_RULE ALLERGY WHERE ALLERGY.MEDICINE_ID = MED.MEDICINE_ID AND ALLERGY.IS_DEL = 0)) ALLERGY,DET.AST_FREE_FLAG AST_FREE_FLAG,DET.AST_VALID_PERIOD PERIOD,DET.AST_VALID_PERIOD_UNIT_CODE FROM WINDBA.CLINICAL_SERVICE_CORRELATION PS LEFT JOIN WINDBA.MEDICINE MED ON PS.SOURCE_CS_ID = MED.CS_ID LEFT JOIN WINDBA.CLINICAL_SERVICE CS ON PS.TARGET_CS_ID = CS.CS_ID LEFT JOIN WINDBA.CLINICAL_SERVICE_DRUG DRUG ON PS.SOURCE_CS_ID = DRUG.CS_ID LEFT JOIN WINDBA.MEDICINE_STOCK STOCK ON MED.MEDICINE_ID = STOCK.MEDICINE_ID LEFT JOIN WINDBA.ORGANIZATION ORG ON STOCK.ORG_ID = ORG.ORG_ID LEFT JOIN WINDBA.MEDICINE_DETAIL DET ON MED.MEDICINE_ID = DET.MEDICINE_ID WHERE STOCK.IS_DEL = 0 AND DET.IS_DEL = 0 AND PS.IS_DEL = 0 AND MED.IS_DEL = 0 AND CS.IS_DEL = 0 AND DRUG.IS_DEL = 0 AND DRUG.AST_REQIRED_FLAG = 98175 AND MED.COMMODITY_ENABLE_FLAG = 98360 AND ORG.ORG_NAME = '?1' AND STOCK.MEDICINE_STOCK_QTY>10 AND DET.AST_REQIRED_FLAG = 98175 AND DRUG.AST_TYPE_CODE IN ?2";

        /**
         * 根据临床服务状态和临床服务类型代码获取临床服务
         */
        String getServiceListByCsStatusAndCsTypeCode = "SELECT CLI.CS_ID ID,CLI.CS_NAME NAME FROM WINDBA.CLINICAL_SERVICE CLI WHERE CLI.CS_STATUS = ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = ?2 OR PARENT_VALUE_ID = ?2)";


        /**
         * 根据模板名字获取诊断段落是否可以为空提交
         */
        String getEmrTsEmptySubmitFlag = "SELECT TS_EMPTY_SUBMIT_FLAG,a.*,b.* FROM WINDBA.MEDICAL_RECORD_TEMPLATE a LEFT JOIN WINDBA.TEMPLATE_SECTION b on a.MRT_ID = b.MRT_ID WHERE a.MRT_NAME = '?' AND b.CS_CONCEPT_ID = 390000213";


        /**
         * 成员计费_检查服务项目按个数区间计费_累计计费策略-csId获取价格列表
         */
        String getBsExamItemRangeSumByCsid = "SELECT BS.CS_ID,BS_RANGE.EXAM_ITEM_LOWER_LIMIT,BS_RANGE.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_EXAM_ITEM_RANGE_SUM BS LEFT JOIN WINDBA.EXAM_ITEM_RANGE_SUM BS_RANGE ON BS.BS_EXAM_ITEM_RNG_SUM_ID = BS_RANGE.BS_EXAM_ITEM_RNG_SUM_ID LEFT JOIN WINDBA.EXAM_ITEM_RANGE_SUM_ITEM BS_ITEM ON BS_RANGE.EXAM_ITEM_RANGE_SUM_ID = BS_ITEM.EXAM_ITEM_RANGE_SUM_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.EXAM_ITEM_UPPER_LIMIT";

        /**
         * 成员计费_检查服务项目按个数区间计费_不累计计费策略-csId获取价格列表
         */
        String getBsExamItemRangeNoneSumByCsid = "SELECT BS.CS_ID,BS_RANGE.EXAM_ITEM_LOWER_LIMIT,BS_RANGE.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_EXAM_ITEM_RANGE_NONE_SUM BS LEFT JOIN WINDBA.EXAM_ITEM_RANGE_NONE_SUM BS_RANGE ON BS.BS_EXAM_ITEM_RNG_NONE_SUM_ID = BS_RANGE.BS_EXAM_ITEM_RNG_NONE_SUM_ID LEFT JOIN WINDBA.EXAM_ITEM_RANGE_NONE_SUM_ITEM BS_ITEM ON BS_RANGE.EXAM_ITEM_RANGE_NONE_SUM_ID = BS_ITEM.EXAM_ITEM_RANGE_NONE_SUM_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.EXAM_ITEM_UPPER_LIMIT";

        /**
         * 成员计费_检查服务项目按个数加收计费策略-根据csId获取价格列表
         */
        String getBsExamItemRangeExtraFeeByCsid = "SELECT ITEM.CHARGING_ITEM_NAME,BS_ITEM.EXAM_ITEM_LOWER_LIMIT,BS_ITEM.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_EXAM_ITEM_RANGE_EXTRA_FEE BS LEFT JOIN WINDBA.EXAM_ITEM_RNG_EXTRA_FEE_ITEM BS_ITEM ON BS.BS_EXAM_ITEM_RNG_EXTRA_FEE_ID = BS_ITEM.BS_EXAM_ITEM_RNG_EXTRA_FEE_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_ITEM.EXAM_ITEM_LOWER_LIMIT ASC";

        /**
         * 成员计费_检查按项目明细合计计费策略-csId获取价格列表
         */
        String getBsExamItemByCsid = "SELECT EXAM.CS_ID, EXAM.EXAM_ITEM_NO, EXAM.EXAM_ITEM_NAME, ITEM.CHARGING_ITEM_NAME, BS_ITEM.CHARGING_ITEM_QTY, P1.UNIT_PRICE FROM WINDBA.BS_EXAM_ITEM BS LEFT JOIN WINDBA.EXAMINATION_ITEM EXAM ON BS.EXAM_ITEM_ID = EXAM.EXAM_ITEM_ID LEFT JOIN WINDBA.BS_EXAM_ITEM_CHARGING_ITEM BS_ITEM ON BS.BS_EXAM_ITEM_ID = BS_ITEM.BS_EXAM_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND EXAM.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND EXAM.CS_ID = ?2";

        /**
         * 成员计费_检验按指标个数区间_累计计费策略-csId获取价格列表
         */
        String getBsLabtestIndexRangeSumByCsid = "SELECT BS.CS_ID,BS_RANGE.LABTEST_INDEX_UPPER_LIMIT,BS_RANGE.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_LABTEST_INDEX_RANGE_SUM BS LEFT JOIN WINDBA.LABTEST_INDEX_RNG_SUM BS_RANGE ON BS.BS_LABTEST_INDEX_RANGE_SUM_ID = BS_RANGE.BS_LABTEST_INDEX_RANGE_SUM_ID LEFT JOIN WINDBA.LABTEST_INDEX_RNG_SUM_ITEM BS_ITEM ON BS_RANGE.LABTEST_INDEX_RNG_SUM_ID = BS_ITEM.LABTEST_INDEX_RNG_SUM_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.LABTEST_INDEX_UPPER_LIMIT";

        /**
         * 成员计费_检验按指标个数区间_不累计计费策略-csId获取价格列表
         */
        String getBsLabtestIdxRanNoneSumByCsid = "SELECT BS.CS_ID,BS_RANGE.LABTEST_INDEX_UPPER_LIMIT,BS_RANGE.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_LABTEST_IDX_RAN_NONE_SUM BS LEFT JOIN WINDBA.LABTEST_INDEX_RNG_NONE_SUM BS_RANGE ON BS.BS_LABTEST_IDX_RAN_NONE_SUM_ID = BS_RANGE.BS_LABTEST_IDX_RAN_NONE_SUM_ID LEFT JOIN WINDBA.LABTEST_IDX_RNG_NONE_SUM_ITEM BS_ITEM ON BS_RANGE.LABTEST_INDEX_RNG_NONE_SUM_ID = BS_ITEM.LABTEST_INDEX_RNG_NONE_SUM_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.LABTEST_INDEX_UPPER_LIMIT";

        /**
         * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
         */
        String getBsLabtestIdxRangeExFeeByCsid = "SELECT ITEM.CHARGING_ITEM_NAME,BS_ITEM.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.LABTEST_INDEX_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_LABTEST_IDX_RANGE_EX_FEE BS LEFT JOIN WINDBA.LABTEST_INDEX_RNG_EX_FEE_ITEM BS_ITEM ON BS.BS_LABTEST_IDX_RANGE_EX_FEE_ID = BS_ITEM.BS_LABTEST_IDX_RANGE_EX_FEE_ID LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2";

        /**
         * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
         */
        String getBsLabtestIndexCostByCsid = "SELECT(SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE VALUE_ID = BS.LABTEST_INDEX_CODE) INDEX_NAME,ITEM.CHARGING_ITEM_NAME,P1.UNIT_PRICE FROM WINDBA.LABTEST_INDEX IND LEFT JOIN WINDBA.BS_LABTEST_INDEX BS ON IND.LABTEST_INDEX_CODE = BS.LABTEST_INDEX_CODE LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE IND.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND BS.IS_DEL = 0 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND BS.ENABLED_FLAG = 98175 AND ITEM.ENABLED_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND IND.HOSPITAL_SOID = ?1 AND IND.CS_ID = ?2";

        /**
         * 组合计费-根据csId获取价格列表
         */
        String getBsCompositeCostByCsid = "SELECT ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM WINDBA.BS_COMPOSITE BS LEFT JOIN WINDBA.COMPOSITE_CHARGING_ITEM ITEM ON BS.BS_COMPOSITE_ID = ITEM.BS_COMPOSITE_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE	BS.IS_DEL = 0 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND BS.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2";

        /**
         * 本服务计费-根据csId获取价格
         */
        String getBsSelfCostByCsid = "SELECT P1.UNIT_PRICE FROM WINDBA.BS_SELF BS LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND BS.START_AT < GETDATE() AND BS.END_AT > GETDATE() AND P2.START_AT < GETDATE() AND P2.END_AT > GETDATE() AND BS.ENABLED_FLAG = 98175 AND ITEM.ENABLED_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND BS.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.CS_ID = ?2";

        /**
         * 根据计费策略获取临床服务
         */
        String getServiceListByStrategy = "SELECT CS.CS_ID,CS.CS_NO,(SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE,CS.CS_NAME,BS.STRATEGY_CLASS_CODE,BS.STRATEGY_CLASS_NAME FROM WINDBA.CLINICAL_SERVICE CS LEFT JOIN WINDBA.CS_X_BILLING_STRATEGY X ON CS.CS_ID = X.CS_ID LEFT JOIN WINDBA.MED_INSTI_BS_CLASS BS ON X.STRATEGY_CLASS_CODE = BS.STRATEGY_CLASS_CODE WHERE CS.CS_ID IS NOT NULL AND X.CS_ID IS NOT NULL AND CS.CS_NO IS NOT NULL AND BS.STRATEGY_CLASS_CODE IS NOT NULL AND CS.IS_DEL = 0 AND X.IS_DEL = 0 AND BS.IS_DEL = 0 AND CS.HOSPITAL_SOID = ?1 AND X.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND X.ENCOUNTER_TYPE_CODE = 138138 AND CS.CS_STATUS = 98360 AND BS.ENABLED_FLAG = 98175 AND BS.STRATEGY_CLASS_CODE = ?2 AND COALESCE(CS.CS_SET_FLAG,98176) <> 98175";

        /**
         * 根据计费策略获取临床服务,ServiceTypeCode不为空
         */
        String getServiceListByStrategyAndServiceTypeCode = "SELECT CS.CS_ID,CS.CS_NO,(SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE,CS.CS_NAME,BS.STRATEGY_CLASS_CODE,BS.STRATEGY_CLASS_NAME FROM WINDBA.CLINICAL_SERVICE CS LEFT JOIN WINDBA.CS_X_BILLING_STRATEGY X ON CS.CS_ID = X.CS_ID LEFT JOIN WINDBA.MED_INSTI_BS_CLASS BS ON X.STRATEGY_CLASS_CODE = BS.STRATEGY_CLASS_CODE WHERE CS.CS_ID IS NOT NULL AND X.CS_ID IS NOT NULL AND CS.CS_NO IS NOT NULL AND BS.STRATEGY_CLASS_CODE IS NOT NULL AND CS.IS_DEL = 0 AND X.IS_DEL = 0 AND BS.IS_DEL = 0 AND CS.HOSPITAL_SOID = ?1 AND X.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND X.ENCOUNTER_TYPE_CODE = 138138 AND CS.CS_STATUS = 98360 AND BS.ENABLED_FLAG = 98175 AND BS.STRATEGY_CLASS_CODE = ?2 AND CS.CS_TYPE_CODE IN (SELECT VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID = ?3 OR PARENT_VALUE_ID = ?3) AND COALESCE(CS.CS_SET_FLAG,98176) <> 98175";

        /**
         * 根据MEDICINE_ID获取CS_ID
         */
        String getCsidByMedicineId = "SELECT CS_ID FROM WINDBA.MEDICINE WHERE MEDICINE_ID = ?";

        /**
         * 获取DOCTOR_ID = 57393667239307269（登录6618）收藏列表所有西药的MEDICINE_ID和MEDICINE_NAME
         */
        String getAllCommonMedId = "SELECT B.MEDICINE_ID, * FROM  WINDBA.CLINICAL_SERVICE_MEDICATION A INNER JOIN WINDBA.MEDICINE B on A.CS_ID = B.CS_ID INNER JOIN WINDBA.COMMON_USED_SERVICE C on B.MEDICINE_ID = C.MEDICINE_ID AND C.CS_TYPE_CODE = 98095 AND DOCTOR_ID = 57393667239307269 AND C.IS_DEL = 0 order by C.CREATED_AT DESC";

        /**
         * 根据csName更新clinicalService表的csstatus状态
         */
        String updateClinicalServiceByCsName = "UPDATE WINDBA.CLINICAL_SERVICE SET CS_STATUS = ?1 WHERE CS_NAME = ?2";

        /**
         * 根据类型和soid获取设置的联动规则的值，为json格式*
         */
        String getLinkAgeByType = "SELECT EXEC_RULE_VALUE FROM WINDBA.EXEC_RULE WHERE IS_DEL = 0 AND EXEC_RULE_TYPE_CODE = ?1 AND ENABLED_FLAG = 98360 AND HOSPITAL_SOID = ?2";

        /**
         * 根据默认给药途径查找药房可开立的药品
         */
        String getDrugByDefaultRoute = "SELECT TOP 1 a.COMMODITY_NAME_CHINESE,a.CS_ID FROM WINDBA.MEDICINE a INNER JOIN WINDBA.CLINICAL_SERVICE_DRUG b on a.CS_ID = b.CS_ID "
                + "INNER JOIN WINDBA.MEDICINE_STOCK c on a.MEDICINE_ID = c.MEDICINE_ID "
                + "INNER JOIN WINDBA.ORGANIZATION d on c.ORG_ID = d.ORG_ID "
                + "WHERE a.IS_DEL = 0 AND b.IS_DEL = 0 AND c.IS_DEL = 0 AND d.IS_DEL = 0 "
                + "AND c.MEDICINE_STOCK_QTY >10 "
                + "AND a.HOSPITAL_SOID = '?1' "
                + "AND d.ORG_NAME = '?2' "
                + "AND b.DEFAULT_USAGE_CODE = '?3'";

        /***根据CS_ID在CLINICAL_SERVICE中查找CS_NAME***/
        String getCsNameByCsId = "SELECT CS_NAME FROM WINDBA.CLINICAL_SERVICE WHERE CS_ID = ?1";

        /***** 根据挂号序号查找联动信息 ****/
        String getLinkInfoByGhxh = "SELECT c.CS_ID,c.CLI_ORDER_ITEM_CONTENT,c.PRESCRIBED_QTY,c.LINKAGE_FLAG FROM WINDBA.CLINICAL_ORDER a "
                + "INNER JOIN WINDBA.OUTPATIENT_ENCOUNTER b on a.ENCOUNTER_ID = b.ENCOUNTER_ID "
                + "INNER JOIN WINDBA.CLINICAL_ORDER_ITEM c on a.CLI_ORDER_ID = c.CLI_ORDER_ID "
                + "WHERE a.CLI_ORDER_SOURCE_CODE = 383979 AND b.ENC_REG_SEQ_NO = '?1' "
                + "AND a.IS_DEL = 0 AND b.IS_DEL = 0 "
                + "AND a.HOSPITAL_SOID = '?2'";

        /***根据EXAM_ITEM_ID在EXAMINATION_ITEM中查找EXAM_ITEM_NAME***/
        String getExamItemNameByExamItemId = "SELECT * FROM WINDBA.EXAMINATION_ITEM WHERE IS_DEL = 0 AND EXAM_ITEM_ID = ?1";

        /***根据患者姓名获取EnCounterId**/
        String getEnCounterIdbyFullName = "SELECT b.BIZ_ROLE_ID,c.ENCOUNTER_ID FROM WINDBA.PERSON a INNER JOIN WINDBA.ROLE b on a.PERSON_ID = b.PERSON_ID "
                + "INNER JOIN WINDBA.OUTPATIENT_RECORD c on c.FULL_NAME = a.FULL_NAME "
                + "WHERE a.IS_DEL = 0 AND b.IS_DEL = 0 AND c.IS_DEL = 0 "
                + "AND a.FULL_NAME = '?1' ORDER BY c.CREATED_AT DESC";

        /***根据ENCOUNTER_ID查询OUTPATIENT_CONTACT_ID***/
        String getCONTACTIdbyENCOUNTERId = "SELECT OUTPATIENT_CONTACT_ID FROM WINDBA.OUTPATIENT_CONTACT WHERE ENCOUNTER_ID = '?1' ";

        /**
         * 根据user_code查询employee_no
         **/
        String getEmployNoByUsercode = "SELECT EMPLOYEE_NO FROM WINDBA.EMPLOYEE_INFO WHERE USER_ID = (SELECT USER_ID FROM WINDBA.USER_INFO WHERE USER_CODE = '?1')";

        /**
         * 根据user_code查询employee_no
         **/
        String getEmployIdByUsercode = "SELECT EMPLOYEE_ID FROM WINDBA.USER_INFO WHERE USER_CODE = '?1'";

        /**
         * 根据病历模板查找病历目录分类
         */
        String getInpEmrClassName = "SELECT INP_EMR_CLASS_NAME FROM WINDBA.INPATIENT_EMR_CLASS WHERE INP_EMR_CLASS_ID in (SELECT a.INP_EMR_CLASS_ID FROM WINDBA.INP_EMR_CLASS_MRT_CLASS A, WINDBA.INPATIENT_EMR_TEMPLATE B,WINDBA.INP_EMR_TEMPLATE_CLASS C WHERE A.INP_MRT_CLASS_ID = B.INP_MRT_CLASS_ID AND A.INP_MRT_CLASS_ID = C.INP_MRT_CLASS_ID AND B.INP_MRT_NAME LIKE '?1' AND A.IS_DEL = 0 AND A.HOSPITAL_SOID = '" + Data.hospital_soid + "' AND B.IS_DEL = 0 AND B.HOSPITAL_SOID = '" + Data.hospital_soid + "' AND C.HOSPITAL_SOID = 10)";

        /**
         * 根据paramNo查找ParamConfigId
         */
        String getParamConfigIdByParamNo = "SELECT C.PARAM_CONFIG_ID FROM WINDBA.PARAMETER_CONFIGURATION C INNER JOIN WINDBA.PARAMETER P ON P.PARAM_ID = C.PARAM_ID WHERE P.PARAM_NO = '?1' AND P.IS_DEL = 0 AND C.IS_DEL = 0 AND C.HOSPITAL_SOID = '" + Data.hospital_soid + "'";
        /**
         * 根据住院病历阅改等级代码查询住院病历审阅流程标识ID
         */
        String getInpEmrReviewProcessId = "SELECT INP_EMR_REVIEW_PROCESS_ID FROM WINDBA.INP_EMR_REVIEW_PROCESS WHERE INP_EMR_REVIEW_LEVEL_CODE = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据住院病历模板名称获取模板的监控类型编码
         */
        String getInpMrtMonitorNo = "select MONITORING.INP_MRT_MONITOR_NO from  WINDBA.INPATIENT_EMR_TEMPLATE TEMPLATE ,WINDBA.INP_EMR_TEMPLATE_MONITORING MONITORING ,WINDBA.INP_EMR_CLASS_MRT_CLASS CLASS where TEMPLATE.INP_MRT_NAME = '?1' and TEMPLATE.INP_MRT_MONITOR_ID=MONITORING.INP_MRT_MONITOR_ID AND TEMPLATE.INP_MRT_CLASS_ID =  CLASS.INP_MRT_CLASS_ID and TEMPLATE.IS_DEL=0 and MONITORING.IS_DEL=0 and TEMPLATE.HOSPITAL_SOID ="+ Data.hospital_soid +" AND MONITORING.HOSPITAL_SOID ="+ Data.hospital_soid +"  and CLASS.IS_DEL=0  AND CLASS.HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 查询住院病历三级阅改流程与病历监控类型关系是否存在
         */
        String getInpEmrReviewMonitoring = "SELECT INP_EMR_REVIEW_MONITORING_ID FROM WINDBA.INP_EMR_REVIEW_MONITORING A WHERE A.INP_MRT_MONITORING_NO = '?1' AND A.INP_EMR_REVIEW_PROCESS_ID in (SELECT INP_EMR_REVIEW_PROCESS_ID FROM WINDBA.INP_EMR_REVIEW_PROCESS B WHERE B.INP_EMR_REVIEW_LEVEL_CODE = '399469360' AND B.IS_DEL = 0 AND B.HOSPITAL_SOID = '" + Data.hospital_soid + "') AND A.IS_DEL = 0";

        /**
         * 查找护理文书下拉框的选项值
         */
        String getChartDropdownValue = "SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE CODE_SYSTEM_ID IN (SELECT DEFAULT_CODE_SYSTEM_ID FROM WINDBA.CHART_CONCEPT_DOMAIN WHERE CHART_CONCEPT_DOMAIN_ID = ?) AND IS_DEL = 0 ORDER BY VALUE_ID";

        /**
         * 查找护理文书模板ID、模板大类、模板小类
         */
        String getChartTemplate = "SELECT CHART_TEMPLATE_ID,CHART_TEMPLATE_CLASS_CODE,CHART_TEMPLATE_TYPE_CODE FROM WINDBA.CHART_TEMPLATE WHERE CHART_TEMPLATE_NAME = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "' AND CHART_TEMPLATE_STATUS = 399284018";

        /**
         * 根据患者姓名获取EnCounterId、BizRoleID、CURRENT_DEPT_ID、CURRENT_WARD_ID
         */
        String getEncounterInfoByFullName = "SELECT A.ENCOUNTER_ID,A.BIZ_ROLE_ID,A.CURRENT_DEPT_ID,A.CURRENT_WARD_ID FROM WINDBA.INPATIENT_ENCOUNTER A,WINDBA.ROLE B, WINDBA.PERSON C WHERE C.FULL_NAME = '?' AND C.PERSON_ID = B.PERSON_ID AND B.BIZ_ROLE_ID = A.BIZ_ROLE_ID AND A.IS_DEL = 0 AND B.IS_DEL = 0 AND C.IS_DEL = 0 ORDER BY A.CREATED_AT DESC";

        /**
         * 根据护理文书字段项ID获取字段的最大最小值
         */
        String getChartItemLimit = "SELECT CHART_ITEM_NUMBER_MIN_VAL, CHART_ITEM_NUMBER_MAX_VAL FROM WINDBA.CHART_ITEM WHERE CHART_ITEM_ID = ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 查询临床配置参数值
         */
        String getCliSettingValue = "select CLI_SETTING_VALUE from WINDBA.CLINICAL_SETTING_VALUE A where CLI_SETTING_ID in (SELECT CLI_SETTING_ID from  WINDBA.CLINICAL_SETTING  B where CLI_SETTING_NO ='?1' AND B.IS_DEL=0 and B.HOSPITAL_SOID= " + Data.hospital_soid + " AND B.ENABLED_FLAG='98360') AND A.HOSPITAL_SOID=" + Data.hospital_soid +" AND A.IS_DEL=0";

        /**
         * 更新住院临床配置参数值
         */
        String updateCliSettingValueByCliSettingNo = "update WINDBA.CLINICAL_SETTING_VALUE  set  CLI_SETTING_VALUE ='?2'  where HOSPITAL_SOID=" + Data.hospital_soid + "AND IS_DEL=0 AND CLI_SETTING_ID in (SELECT CLI_SETTING_ID from  WINDBA.CLINICAL_SETTING  B where CLI_SETTING_NO ='?1' AND B.IS_DEL=0 and B.HOSPITAL_SOID=" + Data.hospital_soid + " AND B.ENABLED_FLAG='98360')";

        /**
         * 查询住院空床号
         */
        String getEmptyBedNoByOrgName = "select a.INPAT_BED_SERVICE_ID,a.BED_NO,a.INPAT_ROOM_ID,a.BED_TYPE_CODE from WINDBA.INPATIENT_BED_SERVICE  a,INPATIENT_ROOM b,ORGANIZATION c where c.ORG_NAME='" + Data.inpatient_select_ward + "' and org_no='" + Data.inpatient_select_ward_code + "' and c.ORG_ID=b.WARD_ID and a.INPAT_ROOM_ID =b.inpat_room_id and a.ENABLED_FLAG=98175 and    a.is_del=0 and b.IS_DEL=0 and c.IS_DEL=0 and a.INPAT_BED_SERVICE_ID  not in\n" +
                "(select BED_SERVICE_ID from  WINDBA.INPAT_BED_IN_USE where IS_DEL=0)  and a.INPAT_BED_SERVICE_ID  not in  (select BED_SERVICE_ID from WINDBA.INPAT_BED_RESERVATION where is_Del=0)";

        /**
         * 查询最大床位号
         */
        String getBedNo = "select max(SUBSTRING(BED_NO,3,2)) BED_NO from  WINDBA.INPATIENT_BED_SERVICE where BED_NO like '" + Data.bedNoPrefix + "%'";


        //查询住院就诊登记流水号zyh
        String getInpatEncRegSeqNo = "select INPAT_ENC_REG_SEQ_NO from WINDBA.INPATIENT_ENCOUNTER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开始日期ksrq，停止日期jsrq，开立日期时间lrrq/rq/ysqrsj，医嘱天数ts
        String getTime = "select START_AT,TERMINATED_AT,PRESCRIBED_AT,DAYS_OF_USE from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品编码bm，临床服务名称yzmc
        String getCs = "select CS_NO,CS_NAME from WINDBA.CLINICAL_SERVICE where CS_ID in " +
                "( select CS_ID from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立数量cs，医嘱项内容kdmc，医嘱项标识cfh_wn，嘱托yszt
        String getCliOrderItemId = "select PRESCRIBED_QTY,CLI_ORDER_ITEM_CONTENT,CLI_ORDER_ITEM_ID,ADVICE from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品--开立单位dw,商品名pm
        String getBasicPackUnitCode = "select BASIC_PACK_UNIT_CODE,COMMODITY_NAME_CHINESE from WINDBA.MEDICINE where CS_ID in " +
                "(select CS_ID from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立医生ysbm
        String getEmployeeNo = "select EMPLOYEE_NO from WINDBA.EMPLOYEE_INFO where EMPLOYEE_ID in " +
                "(select PRESCRIBING_DOCTOR_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立病区bq
        String getOrgNo = "select ORG_NO from WINDBA.Organization where org_id in (select PRESCRIBING_WARD_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立科室kdks
        String getOrgNoKs = "select ORG_NO from WINDBA.Organization where org_id in (select PRESCRIBING_DEPT_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询执行科室ks
        String getOrgNoZx = "select ORG_NO from WINDBA.Organization where org_id in (select EXEC_DEPT_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询频次代码
        String geValueDesc = "select VALUE_DESC from WINDBA.VALUE_SET where VALUE_ID in (select FREQ_CODE from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0 ";
        //查询单位dw
        String geValueDescDw = "select VALUE_DESC from WINDBA.VALUE_SET where VALUE_ID in (select PRESCRIBED_UNIT_CODE from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in (select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0 ";
        //查询医嘱类型lx
        String geValueNo = "select VALUE_NO from WINDBA.VALUE_SET where VALUE_ID in (select CLI_ORDER_TYPE_CODE from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0 ";
        // 查询包装规格
        String getPackSpec = "select PACK_SPEC from WINDBA.MEDICINE where CS_ID in " +
                "(select CS_ID from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品 零售价jg1
        String getPetailPrice = "select RETAIL_PRICE from WINDBA.MEDICINE_PRICE where MEDICINE_ID in " +
                "(select  MEDICINE_ID from WINDBA.MEDICINE where CS_ID in " +
                "(select CS_ID from WINDBA.CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+") AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+")AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+"";
        //查询收费项目 零售价
        String getPetailPriceXm = "SELECT P1.UNIT_PRICE,ITEM.CHARGING_ITEM_NO,ITEM.CHARGING_ITEM_NAME FROM WINDBA.BS_SELF BS  " +
                "LEFT JOIN WINDBA.CHARGING_ITEM ITEM ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID " +
                "LEFT JOIN WINDBA.CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID " +
                "LEFT JOIN WINDBA.MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID " +
                "LEFT JOIN WINDBA.CLINICAL_ORDER_ITEM IT ON BS.CS_ID=IT.CS_ID " +
                "LEFT JOIN WINDBA.CLINICAL_ORDER ER ON IT.CLI_ORDER_ID=ER.CLI_ORDER_ID " +
                "WHERE ER.ENCOUNTER_ID='?1' AND BS.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND IT.IS_DEL = 0 AND ER.IS_DEL = 0 " +
                "AND BS.HOSPITAL_SOID = "+Data.hospital_soid+" AND ITEM.HOSPITAL_SOID = "+Data.hospital_soid+" AND P1.HOSPITAL_SOID = "+Data.hospital_soid+" AND P2.HOSPITAL_SOID = "+Data.hospital_soid+" AND IT.HOSPITAL_SOID = "+Data.hospital_soid+" AND ER.HOSPITAL_SOID = "+Data.hospital_soid+" ";
        //查询姓名xm
        String getFullName = "select FULL_NAME from WINDBA.INPATIENT_RECORD where ENCOUNTER_ID= ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询草药--给药途径yf，制出总量zl，剂量(每顿剂量)cl，服药周期付数jr
        String getHerbOrder = "select DOSAGE_ROUTE_CODE,PROCESSED_AMOUNT,DOSE,DRUG_USAGE_FREQ_QUANTITY from WINDBA.HERB_ORDER where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询护士签收人编码qrhs，时间hsqrsj
        String getHandledBy = "select HANDLED_BY,HANDLED_AT from WINDBA.CLI_ORDER_CHANGE_REQUEST where CLI_ORDER_ID in" +
                " (select CLI_ORDER_ID from WINDBA.CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询住院发药申请标识fysqbs_wn
        String getInpmeddispreqid = "select INP_MED_DISP_REQ_ID from WINDBA.INP_MED_DISPENSE_REQUEST where ENCOUNTER_ID= ?1  AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询住院发药计划标识fyjhbs_wn，住院发药剂量HL，住院发药数量SL
        String getInpmeddispplanid = "select INP_MED_DISP_PLAN_ID,INP_MED_DISP_DOSE,INP_MED_DISP_QTY from WINDBA.INP_MED_DISPENSE_PLAN where INP_MED_DISP_PLAN_ID in" +
                " (select INP_MED_DISP_PLAN_ID from WINDBA.INP_MED_DISPENSE_REQUEST where ENCOUNTER_ID=?1  AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询申请人zxrbm
        String getOperatedBy = "select EMPLOYEE_NO from WINDBA.EMPLOYEE_INFO where EMPLOYEE_ID in " +
                "(select OPERATED_BY from WINDBA.EXEC_ACTION_RECORD where EXEC_ACTION_RECORD_ID in " +
                "(select EXEC_ACTION_RECORD_ID from WINDBA.EXEC_ORDER_ACTION_RECORD where EXEC_ORDER_STATUS in " +
                "(select EXEC_ORDER_STATUS from WINDBA.EXEC_ORDER WHERE ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询国籍
        String getInpatientInfo = "select VALUE_NO from WINDBA.VALUE_SET where VALUE_ID in (select NATIONALITY_CODE from WINDBA.INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") AND IS_DEL = 0 " ;
        //查询性别
        String getInpatientGender = "select VALUE_NO from WINDBA.VALUE_SET where VALUE_ID in (select GENDER_CODE from WINDBA.INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + ")  AND IS_DEL = 0 ";
        //查询名族
        String getInpatientNation = "select VALUE_NO from WINDBA.VALUE_SET where VALUE_ID in (select NATION_CODE from WINDBA.INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") AND IS_DEL = 0 ";
        //查询入区途径
        String getInpatientAdmitted = "select VALUE_NO from WINDBA.VALUE_SET where VALUE_ID in (SELECT ADMITTED_TO_WARD_ROUTE_CODE FROM WINDBA.INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID =" + Data.hospital_soid + ")  AND IS_DEL = 0 ";
        //查询身份证号码，病案号，就诊登记流水号
        String getInpatientRecord = "select IDCARD_NO,CASE_NO,IMRN from WINDBA.INPATIENT_RECORD where ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询床位
        String getInpatientBed = "select BED_NO from WINDBA.INPATIENT_BED_SERVICE a LEFT JOIN WINDBA.INPAT_BED_IN_USE b on a.INPAT_BED_SERVICE_ID = b.BED_SERVICE_ID where b.ENCOUNTER_ID='?1' and a.IS_DEL=0 and a.HOSPITAL_SOID =" + Data.hospital_soid + "";
        //查询住院科室
        String getInpatientRoom = "select ORG_NO from WINDBA.Organization where org_id in (SELECT CURRENT_DEPT_ID FROM WINDBA.INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID='?1' and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + " ";
        //查询住院病区
        String getInpatientWard = "select ORG_NO from WINDBA.Organization where org_id in (SELECT CURRENT_WARD_ID FROM WINDBA.INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID='?1' and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + " ";

        /**
         * 根据组织名称获取组织ID
         */
        String getOrgIdByOrgName = "SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE IS_DEL=0 AND ORG_TYPE_CODE= 230267 AND ORG_STATUS=98360 AND ORG_NAME ='?1' AND ORG_NO ='?2' AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据病房名称和病区名查询病房ID
         */
        String getInpatRoomIdByRoomName = "select INPAT_ROOM_ID  from WINDBA.INPATIENT_ROOM  where ROOM_NAME like '?1' and WARD_ID in(SELECT ORG_ID FROM WINDBA.ORGANIZATION WHERE IS_DEL=0 AND ORG_TYPE_CODE= 230267 AND ORG_STATUS=98360 AND ORG_NAME ='?2' AND ORG_NO ='?3' AND HOSPITAL_SOID = '" + Data.hospital_soid + "') And IS_DEL=0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";
     
     /**
         *根据ENCOUNTER_ID查询PHARMACY_ID
       */
        String getPharmacyIdbyEncounterId = "SELECT D.PHARMACY_ID from WINDBA.CLINICAL_ORDER O LEFT JOIN WINDBA.CLINICAL_ORDER_ITEM  I ON O.CLI_ORDER_ID = I.CLI_ORDER_ID LEFT JOIN  WINDBA.DRUG_ORDER_ITEM D ON D.CLI_ORDER_ITEM_ID=I.CLI_ORDER_ITEM_ID WHERE O.ENCOUNTER_ID= '?1' AND D.PHARMACY_ID IS NOT NULL  ORDER BY D.CREATED_AT DESC";

        /**
         * 通过患者姓名查找seqNo,OMRN
         */
        String getSeqNoByPatientName = "select T.TRIAGE_SEQ_NO seqNo,O.OMRN from WINDBA.TRIAGE T left join WINDBA.OUTPATIENT_RECORD O ON O.ENCOUNTER_ID = T.ENCOUNTER_ID  WHERE O.FULL_NAME ='?1' ORDER BY O.CREATED_AT DESC";

    }


    /**
     * Win60 Oracle语句管理类 TODO
     */
    public class WnOracleSql {
        // 根据科室和医生获取推荐主诉(诊疗路径)
        String getRecommendSymptom = "SELECT * FROM TREATMENT_PATHWAY_SYMPTOM "
                + "WHERE "
                + "IS_DEL = 0 "
                + "AND ENABLED_FLAG = 98360 "
                + "AND DEPT_ID = (SELECT ORG_ID FROM ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR_ID = (SELECT EMPLOYEE_ID FROM EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?2' AND IS_DEL = 0 AND HOSPITAL_SOID = ?4) "
                + "AND (GENDER_CODE = ?3 OR GENDER_CODE IS NULL) "
                + "AND HOSPITAL_SOID = ?4 "
                + "ORDER BY SEQ_NO ";

        // 获取推荐诊断(诊疗路径)
        String getRecommendDiagnose = "SELECT DIAGNOSIS.DIAGNOSIS_CODE,(SELECT VALUE_DESC FROM VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = DIAGNOSIS.DIAGNOSIS_CODE) DIAGNOSE_NAME,DIAGNOSIS.GENDER_CODE,DOCTOR.SEQ_NO "
                + "FROM TREATMENT_PATHWAY_DIAGNOSIS DIAGNOSIS "
                + "INNER JOIN DOCTOR_DIAGNOSIS DOCTOR ON DIAGNOSIS.TREAT_PATH_DIAGNOSIS_ID = DOCTOR.TREAT_PATH_DIAGNOSIS_ID "
                + "WHERE "
                + "DIAGNOSIS.IS_DEL = 0 "
                + "AND DOCTOR.IS_DEL = 0 "
                + "AND DIAGNOSIS.HOSPITAL_SOID = ?4 " + "AND DOCTOR.HOSPITAL_SOID = ?4 "
                + "AND DIAGNOSIS.ENABLED_FLAG = 98360 "
                + "AND DIAGNOSIS.DEPT_ID = (SELECT ORG_ID FROM ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR.DEPT_ID = (SELECT ORG_ID FROM ORGANIZATION WHERE ORG_STATUS = 98360 AND IS_DEL = 0 AND HOSPITAL_SOID = ?4 AND ORG_NO = '?1') "
                + "AND DOCTOR.DOCTOR_ID = (SELECT EMPLOYEE_ID FROM EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?2' AND IS_DEL = 0 AND HOSPITAL_SOID = ?4) "
                + "AND (DIAGNOSIS.GENDER_CODE = ?3 OR DIAGNOSIS.GENDER_CODE IS NULL) "
                + "ORDER BY DOCTOR.SEQ_NO";

        /**
         * 根据value_id查询主数据对应描述
         */
        String getMdmDescByValueId = "SELECT VALUE_DESC FROM VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = ?";

        /***根据org_id查找org_name**/
        String getOrgNameByOrgId = "SELECT ORG_NAME,ORG_NO FROM ORGANIZATION WHERE ORG_ID = '?1'";

        /**
         * 根据code_system_id查询value_id列表
         */
        String getMdmValueIdByCodeSystemId = "SELECT VALUE_ID FROM VALUE_SET WHERE CODE_SYSTEM_ID = ? AND VALUE_STATUS = 98360";


        /**
         * 根据CSID获取medicineid
         */
        String getMedicineIdbyCSID = "SELECT * FROM MEDICINE WHERE CS_ID = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '?2' AND ROWNUM = 1";

        /**
         * 获取主数据配置参数信息
         */
        String getMdmParameter = "SELECT P.PARAM_NO,P.PARAM_DESC,PCV.PARAM_VALUE FROM PARAMETER P INNER JOIN PARAMETER_CONFIGURATION PC ON P.PARAM_ID = PC.PARAM_ID INNER JOIN PARAMETER_VALUE_CONFIGURATION PCV ON PC.PARAM_CONFIG_ID = PCV.PARAM_CONFIG_ID WHERE P.IS_DEL = 0 AND PC.IS_DEL = 0 AND PCV.IS_DEL = 0 AND PC.PARAM_APP_SCOPE_TYPE_CODE = 256523 AND P.PARAM_NO = '?'";

        /**
         * 根据患者姓名获取挂号记录
         */
        String getPatientRecordByName = "SELECT * FROM OUTPATIENT_RECORD WHERE FULL_NAME = '?' ORDER BY CREATED_AT DESC";

        /**
         * 获取医保审批医嘱项列表
         */
        String getInsurOrderItemsByEncounterid = "SELECT INSUR.* FROM "
                + "CLINICAL_ORDER O "
                + "INNER JOIN CLINICAL_ORDER_ITEM ITEM ON O.CLI_ORDER_ID = ITEM.CLI_ORDER_ID "
                + "INNER JOIN PURCH_ORDER_MED_INSUR_APPROVAL INSUR ON ITEM.CLI_ORDER_ITEM_ID = INSUR.CLI_ORDER_ITEM_ID "
                + "WHERE O.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND INSUR.IS_DEL = 0 AND O.ENCOUNTER_ID = '?'";


        /**
         * 获取医保信息
         */
        String getInsuranceListByID = "SELECT * FROM MED_INSTI_MEDICAL_INSURANCE WHERE MED_INSTI_INSUR_ID = ?";

        /**
         * 根据药品类型获取药品列表
         */
        String getDrugByPsychotropicsCode = "SELECT * FROM CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND PSYCHOTROPICS_CODE = ? AND CS_ID IN (SELECT CS_ID FROM MEDICINE WHERE IS_DEL = 0)";

        //根据医保ID获取临床服务(指定类型)
        String getInsurApprovalServiceNoType =
                "SELECT "
                        + "CS.CS_ID, "
                        + "CS.CS_NO, "
                        + "(SELECT VALUE_DESC FROM VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE, "
                        + "CS.CS_NAME, INSUR.MED_INSTI_INSUR_ID,"
                        + "ITEM.CHARGING_ITEM_ID, "
                        + "ITEM.CHARGING_ITEM_NO, "
                        + "ITEM.CHARGING_ITEM_NAME "
                        + "FROM "
                        + "MED_INSUR_APPROVAL INSUR "
                        + "INNER JOIN MED_INSTI_CHARGING_ITEM X ON X.MED_INSTI_CHARGING_ITEM_ID = INSUR.MED_INSTI_CHARGING_ITEM_ID "
                        + "INNER JOIN CHARGING_ITEM ITEM ON X.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID "
                        + "INNER JOIN BS_SELF BS ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID "
                        + "INNER JOIN CLINICAL_SERVICE CS ON CS.CS_ID = BS.CS_ID "
                        + "WHERE ITEM.IS_DEL = 0 "
                        + "AND INSUR.IS_DEL = 0 "
                        + "AND X.IS_DEL = 0 "
                        + "AND BS.IS_DEL = 0 "
                        + "AND CS.IS_DEL = 0 "
                        + "AND ITEM.HOSPITAL_SOID = ?3 "
                        + "AND INSUR.HOSPITAL_SOID = ?3 "
                        + "AND X.HOSPITAL_SOID = ?3 "
                        + "AND BS.HOSPITAL_SOID = ?3 "
                        + "AND CS.HOSPITAL_SOID = ?3 "
                        + "AND X.ENABLED_FLAG = 98175 "
                        + "AND ITEM.ENABLED_FLAG = 98175 "
                        + "AND INSUR.MED_INSTI_INSUR_ID = '?2' "
                        + "AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE "
                        + "AND BS.ENABLED_FLAG = 98175 "
                        + "AND BS.ENCOUNTER_TYPE_CODE = 138138 "
                        + "AND CS.CS_STATUS = 98360 "
                        + "AND COALESCE (CS_SET_FLAG,98176)<>98175";


        //根据医保ID获取临床服务(全部类型)
        String getInsurApprovalService = getInsurApprovalServiceNoType + " AND CS.CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = ?1 OR PARENT_VALUE_ID = ?1)";

        /**
         * 查询需要测试的所有药品sql语句
         */
        String getAllMedicineSql = "SELECT '药品' TYPE,MEDICINE_ID ID,CS_ID CS_ID,COMMODITY_NAME_CHINESE NAME,PACK_SPEC PACKAGE FROM MEDICINE WHERE IS_DEL = 0 AND COMMODITY_ENABLE_FLAG = 98360";

        /**
         * 大临床查询需要测试的所有药品sql语句
         */
        String getAllMedicineInpatientSql = "SELECT '药品' TYPE,A.MEDICINE_ID ID,A.CS_ID CS_ID,A.COMMODITY_NAME_CHINESE NAME,A.PACK_SPEC PACKAGE,B.AST_REQIRED_FLAG SKINTESTFLAG FROM MEDICINE A LEFT JOIN MEDICINE_DETAIL B ON A.MEDICINE_ID=B.MEDICINE_ID WHERE A.IS_DEL = 0 AND A.COMMODITY_ENABLE_FLAG = 98360 AND B.IS_DEL=0";

        /**
         * 查询需要测试的所有检验sql语句
         */
        String getAllLabSql = "SELECT '检验' TYPE,CS_ID ID,CS_NAME NAME FROM CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = 98071 OR PARENT_VALUE_ID = 98071)";

        /**
         * 查询需要测试的所有病理sql语句
         */
        String getAllPathologySql = "SELECT '病理' TYPE,CS_ID ID,CS_NAME NAME FROM CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = 98088 OR PARENT_VALUE_ID = 98088)";

        /**
         * 查询需要测试的所有治疗sql语句
         */
        String getAllTreatSql = "SELECT '治疗' TYPE,CS_ID ID,CS_NAME NAME FROM CLINICAL_SERVICE WHERE IS_DEL = 0 AND CS_STATUS = 98360 AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = 98098 OR PARENT_VALUE_ID = 98098)";

        /**
         * 查询需要测试的所有检查项目sql语句
         */
        String getAllExamItemSql = "SELECT '检查项目' TYPE,EXAM_ITEM_ID ID,EXAM_ITEM_NAME NAME FROM EXAMINATION_ITEM WHERE IS_DEL = 0";

        /**
         * 获取一条门诊药房有库存的西药信息
         */
        String getOneDrugOrderValid = null;

        /**
         * 获取一条草药房有库存的中草药信息
         */
        String getOneHerbOrderValid = null;


        /**
         * 根据MEDICINE_ID获取CS_ID
         */
        String getCsidByMedicineId = "SELECT CS_ID FROM MEDICINE WHERE MEDICINE_ID = ?";
        /**
         * 查询西药药品商品推荐剂量
         */
        String getDrugRcmddDosage = "SELECT RCMDD_DOSAGE FROM CLINICAL_SERVICE_DRUG WHERE CS_ID = ?";

        /**
         * 查询西药药品商品推荐剂量单位
         */
        String getDrugRcmddDosageUnitCode = "SELECT RCMDD_DOSAGE_UNIT_CODE FROM CLINICAL_SERVICE_DRUG WHERE CS_ID = ?";

        /**
         * 查询药品给药途径
         */
        String getDrugDefaultUsageCode = "SELECT DEFAULT_USAGE_CODE FROM CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品推荐给药途径类型代码
         */
        String getMedicineRcmddDosageRouteTypeCode = "SELECT RCMDD_DOSAGE_ROUTE_TYPE_CODE FROM MEDICINE_RECOMMENDED_DOSAGE WHERE IS_DEL = 0 AND MEDICINE_ID = ?";

        /**
         * 查询西药药品配置的默认频次
         */
        String getDrugDefaultFreqCode = "SELECT DEFAULT_FREQ_CODE FROM CLINICAL_SERVICE_DRUG WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品可用频次列表
         */
        String getFreqCode = "SELECT FREQ_CODE FROM MEDICATION_FREQUENCY WHERE IS_DEL = 0 AND CS_ID = ?";

        /**
         * 查询药品给药途径列表
         */
        String getDosageRouteTypeCode = "SELECT DOSAGE_ROUTE_TYPE_CODE FROM MEDICATION_DOSAGE_ROUTE WHERE IS_DEL = 0 AND CS_ID = ?";


        /**
         * 查询草药药品商品推荐剂量
         */
        String getHerbRcmddDosage = "SELECT RCMDD_DOSAGE FROM CLINICAL_SERVICE_HERB WHERE CS_ID = ?";

        /**
         * 查询药品通用名推荐剂量
         */
        String getMedicationDosageConvFactor = "SELECT DOSAGE_CONV_FACTOR FROM CLINICAL_SERVICE_MEDICATION WHERE CS_ID = ?";

        /**
         * 查询草药药品商品推荐剂量单位
         */
        String getHerbRcmddDosageUnitCode = "SELECT RCMDD_DOSAGE_UNIT_CODE FROM CLINICAL_SERVICE_HERB WHERE CS_ID = ?";

        /**
         * 查询药品通用名推荐剂量单位
         */
        String getMedicationDosageUnitCode = "SELECT DOSAGE_UNIT_CODE FROM CLINICAL_SERVICE_MEDICATION WHERE CS_ID = ?";

        /**
         * 查询给药途径术语列表
         */
        String getMedicationDosageRouteCode = "SELECT D.DOSAGE_ROUTE_CODE FROM CLINICAL_SERVICE_MEDICATION M LEFT JOIN DOSAGE_FORM_DOSAGE_ROUTE D ON M.MEDICINE_DOSAGE_FORM_CODE = D.DOSAGE_FORM_CODE WHERE M.IS_DEL = 0 AND D.IS_DEL = 0 AND M.CS_ID = ?";

        /**
         * 查询默认门诊包装单位
         */
        String getDefaultPackUnitCode = "SELECT T.PACK_UNIT_CODE FROM MEDICINE_PACKING_UNIT T LEFT JOIN MEDICINE_PACK_UNIT_USE_TYPE T1 ON T.MEDICINE_PACK_UNIT_ID = T1.MEDICINE_PACK_UNIT_ID WHERE T1.PACK_UNIT_USE_TYPE_CODE = 256147 AND MEDICINE_ID = ?";


        /**
         * 获取非皮试药品且有库存的药品
         */
        String getNormalMedicialList = "SELECT MED.CS_ID,ORG.ORG_NO ORG_NO,MED.MEDICINE_PRIMARY_NO IDM,PRI.RETAIL_PRICE,ORG.ORG_NAME ORG,MED.MEDICINE_ID,MED.COMMODITY_NAME_CHINESE NAME,STOCK.MEDICINE_STOCK_QTY QTY,MED.PACK_SPEC PACK,"
                + "(SELECT CS.CS_NO FROM CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID AND ROWNUM = 1) CS_NO FROM MEDICINE_PRICE PRI,ORGANIZATION ORG,MEDICINE_STOCK STOCK,MEDICINE MED,CLINICAL_SERVICE_MEDICATION MEDICATION "
                + "WHERE ORG.ORG_ID = STOCK.ORG_ID AND PRI.MEDICINE_ID = MED.MEDICINE_ID AND STOCK.MEDICINE_ID = MED.MEDICINE_ID AND MED.CS_ID = MEDICATION.CS_ID AND PRI.IS_DEL = 0 AND ORG.IS_DEL = 0 AND STOCK.IS_DEL = 0 AND MED.IS_DEL = 0 AND MEDICATION.IS_DEL = 0 "
                + "AND STOCK.MEDICINE_STOCK_QTY > 10 AND ORG.ORG_NAME = '?1' AND MEDICATION.MEDICINE_TYPE_CODE = ?2 AND MEDICATION.CS_ID NOT IN (SELECT CS_ID FROM CLINICAL_SERVICE_DRUG WHERE AST_REQIRED_FLAG = 98175) AND MED.COMMODITY_ENABLE_FLAG = 98360 AND PRI.RETAIL_PRICE>0 AND MED.MEDICINE_ID NOT IN ( SELECT MEDICINE_ID FROM MEDICINE_HOSPITAL_SETTINGS WHERE OUTP_SELF_PAYMENT_FLAG = 98175 )";

        /**
         * 获取需要医保审批的启用药品
         */
        String getInsurApprovalMedicine = "SELECT MED.*,INS.MED_INSTI_INSUR_ID,INS.APPROVAL_PROMPT_MESSAGE FROM MED_INSUR_APPROVAL INS INNER JOIN MEDICINE MED ON MED.MEDICINE_ID = INS.MED_INSTI_COMMODITY_ID WHERE INS.MED_INSTI_INSUR_ID = '?1' AND INS.IS_DEL = 0 AND INS.HOSPITAL_SOID = ?2 AND MED.IS_DEL = 0 AND MED.HOSPITAL_SOID = ?2 AND MED.COMMODITY_ENABLE_FLAG = 98360";


        /**
         * 获取非皮试非自费药品且有库存的药品
         */
        String getOutOfStocklMedicineList = "SELECT MED.CS_ID,ORG.ORG_NO ORG_NO,MED.MEDICINE_PRIMARY_NO IDM,PRI.RETAIL_PRICE,ORG.ORG_NAME ORG,MED.MEDICINE_ID,MED.COMMODITY_NAME_CHINESE NAME,STOCK.MEDICINE_STOCK_QTY QTY,MED.PACK_SPEC PACK,"
                + "(SELECT CS.CS_NO FROM CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID AND ROWNUM = 1) CS_NO FROM MEDICINE_PRICE PRI,ORGANIZATION ORG,MEDICINE_STOCK STOCK,MEDICINE MED,CLINICAL_SERVICE_MEDICATION MEDICATION "
                + "WHERE ORG.ORG_ID = STOCK.ORG_ID AND PRI.MEDICINE_ID = MED.MEDICINE_ID AND STOCK.MEDICINE_ID = MED.MEDICINE_ID AND MED.CS_ID = MEDICATION.CS_ID AND PRI.IS_DEL = 0 AND ORG.IS_DEL = 0 AND STOCK.IS_DEL = 0 AND MED.IS_DEL = 0 AND MEDICATION.IS_DEL = 0 "
                + "AND STOCK.MEDICINE_STOCK_QTY = 0 AND ORG.ORG_NAME = '?1' AND MEDICATION.MEDICINE_TYPE_CODE = ?2 AND MEDICATION.CS_ID NOT IN (SELECT CS_ID FROM CLINICAL_SERVICE_DRUG WHERE AST_REQIRED_FLAG = 98175) AND MED.COMMODITY_ENABLE_FLAG = 98360 AND PRI.RETAIL_PRICE>0 AND MED.MEDICINE_ID NOT IN ( SELECT MEDICINE_ID FROM MEDICINE_HOSPITAL_SETTINGS WHERE OUTP_SELF_PAYMENT_FLAG = 98175 )";

        /**
         * 获取皮试药品
         */
        String getSkinTestMedicine = "SELECT MED.MEDICINE_ID MEDICINE_ID, MED.COMMODITY_NAME_CHINESE NAME, MED.PACK_SPEC PACK, MED.COMMODITY_NAME_CHINESE SKIN_TEST_NAME, DRUG.AST_TYPE_CODE SKIN_TEST_TYPE, STOCK.MEDICINE_STOCK_QTY QTY,(SELECT TOP 1 CS.CS_NO FROM WINDBA.CLINICAL_SERVICE CS WHERE CS.CS_ID = MED.CS_ID) CS_NO,(SELECT TOP 1 VALUE_DESC FROM WINDBA.VALUE_SET WHERE IS_DEL = 0 AND VALUE_ID = (SELECT TOP 1 ALLERGY.MEDICINE_ALLERGY_CODE FROM WINDBA.MEDICINE_ALLERGY_RULE ALLERGY WHERE ALLERGY.MEDICINE_ID = MED.MEDICINE_ID AND ALLERGY.IS_DEL = 0)) ALLERGY,DET.AST_FREE_FLAG AST_FREE_FLAG,DET.AST_VALID_PERIOD PERIOD,DET.AST_VALID_PERIOD_UNIT_CODE FROM WINDBA.CLINICAL_SERVICE_CORRELATION PS LEFT JOIN WINDBA.MEDICINE MED ON PS.SOURCE_CS_ID = MED.CS_ID LEFT JOIN WINDBA.CLINICAL_SERVICE CS ON PS.TARGET_CS_ID = CS.CS_ID LEFT JOIN WINDBA.CLINICAL_SERVICE_DRUG DRUG ON PS.SOURCE_CS_ID = DRUG.CS_ID LEFT JOIN WINDBA.MEDICINE_STOCK STOCK ON MED.MEDICINE_ID = STOCK.MEDICINE_ID LEFT JOIN WINDBA.ORGANIZATION ORG ON STOCK.ORG_ID = ORG.ORG_ID LEFT JOIN WINDBA.MEDICINE_DETAIL DET ON MED.MEDICINE_ID = DET.MEDICINE_ID WHERE STOCK.IS_DEL = 0 AND DET.IS_DEL = 0 AND PS.IS_DEL = 0 AND MED.IS_DEL = 0 AND CS.IS_DEL = 0 AND DRUG.IS_DEL = 0 AND DRUG.AST_REQIRED_FLAG = 98175 AND MED.COMMODITY_ENABLE_FLAG = 98360 AND ORG.ORG_NAME = '?1' AND STOCK.MEDICINE_STOCK_QTY>10 AND DET.AST_REQIRED_FLAG = 98175 AND DRUG.AST_TYPE_CODE IN ?2";

        /**
         * 根据模板名字获取诊断段落是否可以为空提交
         */
        String getEmrTsEmptySubmitFlag = "SELECT TS_EMPTY_SUBMIT_FLAG,a.*,b.* FROM MEDICAL_RECORD_TEMPLATE a LEFT JOIN TEMPLATE_SECTION b on a.MRT_ID = b.MRT_ID WHERE a.MRT_NAME = '?' AND b.CS_CONCEPT_ID = 390000213";

        /**
         * 成员计费_检查服务项目按个数区间计费_累计计费策略-csId获取价格列表
         */
        String getBsExamItemRangeSumByCsid = "SELECT BS.CS_ID,BS_RANGE.EXAM_ITEM_LOWER_LIMIT,BS_RANGE.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_EXAM_ITEM_RANGE_SUM BS LEFT JOIN EXAM_ITEM_RANGE_SUM BS_RANGE ON BS.BS_EXAM_ITEM_RNG_SUM_ID = BS_RANGE.BS_EXAM_ITEM_RNG_SUM_ID LEFT JOIN EXAM_ITEM_RANGE_SUM_ITEM BS_ITEM ON BS_RANGE.EXAM_ITEM_RANGE_SUM_ID = BS_ITEM.EXAM_ITEM_RANGE_SUM_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.EXAM_ITEM_UPPER_LIMIT";

        /**
         * 成员计费_检查服务项目按个数区间计费_不累计计费策略-csId获取价格列表
         */
        String getBsExamItemRangeNoneSumByCsid = "SELECT BS.CS_ID,BS_RANGE.EXAM_ITEM_LOWER_LIMIT,BS_RANGE.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_EXAM_ITEM_RANGE_NONE_SUM BS LEFT JOIN EXAM_ITEM_RANGE_NONE_SUM BS_RANGE ON BS.BS_EXAM_ITEM_RNG_NONE_SUM_ID = BS_RANGE.BS_EXAM_ITEM_RNG_NONE_SUM_ID LEFT JOIN EXAM_ITEM_RANGE_NONE_SUM_ITEM BS_ITEM ON BS_RANGE.EXAM_ITEM_RANGE_NONE_SUM_ID = BS_ITEM.EXAM_ITEM_RANGE_NONE_SUM_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.EXAM_ITEM_UPPER_LIMIT";

        /**
         * 成员计费_检查服务项目按个数加收计费策略-根据csId获取价格列表
         */
        String getBsExamItemRangeExtraFeeByCsid = "SELECT ITEM.CHARGING_ITEM_NAME,BS_ITEM.EXAM_ITEM_LOWER_LIMIT,BS_ITEM.EXAM_ITEM_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_EXAM_ITEM_RANGE_EXTRA_FEE BS LEFT JOIN EXAM_ITEM_RNG_EXTRA_FEE_ITEM BS_ITEM ON BS.BS_EXAM_ITEM_RNG_EXTRA_FEE_ID = BS_ITEM.BS_EXAM_ITEM_RNG_EXTRA_FEE_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2";

        /**
         * 成员计费_检查按项目明细合计计费策略-csId获取价格列表
         */
        String getBsExamItemByCsid = "SELECT EXAM.CS_ID, EXAM.EXAM_ITEM_NO, EXAM.EXAM_ITEM_NAME, ITEM.CHARGING_ITEM_NAME, BS_ITEM.CHARGING_ITEM_QTY, P1.UNIT_PRICE FROM BS_EXAM_ITEM BS LEFT JOIN EXAMINATION_ITEM EXAM ON BS.EXAM_ITEM_ID = EXAM.EXAM_ITEM_ID LEFT JOIN BS_EXAM_ITEM_CHARGING_ITEM BS_ITEM ON BS.BS_EXAM_ITEM_ID = BS_ITEM.BS_EXAM_ITEM_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND EXAM.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND EXAM.CS_ID = ?2";

        /**
         * 成员计费_检验按指标个数区间_累计计费策略-csId获取价格列表
         */
        String getBsLabtestIndexRangeSumByCsid = "SELECT BS.CS_ID,BS_RANGE.LABTEST_INDEX_UPPER_LIMIT,BS_RANGE.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_LABTEST_INDEX_RANGE_SUM BS LEFT JOIN LABTEST_INDEX_RNG_SUM BS_RANGE ON BS.BS_LABTEST_INDEX_RANGE_SUM_ID = BS_RANGE.BS_LABTEST_INDEX_RANGE_SUM_ID LEFT JOIN LABTEST_INDEX_RNG_SUM_ITEM BS_ITEM ON BS_RANGE.LABTEST_INDEX_RNG_SUM_ID = BS_ITEM.LABTEST_INDEX_RNG_SUM_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.LABTEST_INDEX_UPPER_LIMIT";

        /**
         * 成员计费_检验按指标个数区间_不累计计费策略-csId获取价格列表
         */
        String getBsLabtestIdxRanNoneSumByCsid = "SELECT BS.CS_ID,BS_RANGE.LABTEST_INDEX_UPPER_LIMIT,BS_RANGE.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_LABTEST_IDX_RAN_NONE_SUM BS LEFT JOIN LABTEST_INDEX_RNG_NONE_SUM BS_RANGE ON BS.BS_LABTEST_IDX_RAN_NONE_SUM_ID = BS_RANGE.BS_LABTEST_IDX_RAN_NONE_SUM_ID LEFT JOIN LABTEST_IDX_RNG_NONE_SUM_ITEM BS_ITEM ON BS_RANGE.LABTEST_INDEX_RNG_NONE_SUM_ID = BS_ITEM.LABTEST_INDEX_RNG_NONE_SUM_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_RANGE.IS_DEL = 0 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_RANGE.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2 ORDER BY BS_RANGE.LABTEST_INDEX_UPPER_LIMIT";

        /**
         * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
         */
        String getBsLabtestIdxRangeExFeeByCsid = "SELECT ITEM.CHARGING_ITEM_NAME,BS_ITEM.LABTEST_INDEX_LOWER_LIMIT,BS_ITEM.LABTEST_INDEX_UPPER_LIMIT,BS_ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_LABTEST_IDX_RANGE_EX_FEE BS LEFT JOIN LABTEST_INDEX_RNG_EX_FEE_ITEM BS_ITEM ON BS.BS_LABTEST_IDX_RANGE_EX_FEE_ID = BS_ITEM.BS_LABTEST_IDX_RANGE_EX_FEE_ID LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS_ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.ENABLED_FLAG = 98175 AND BS_ITEM.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND ITEM.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND BS_ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2";

        /**
         * 成员计费_检验按指标明细合计计费策略-根据csId获取价格列表
         */
        String getBsLabtestIndexCostByCsid = "SELECT(SELECT VALUE_DESC FROM VALUE_SET WHERE VALUE_ID = BS.LABTEST_INDEX_CODE) INDEX_NAME,ITEM.CHARGING_ITEM_NAME,P1.UNIT_PRICE FROM LABTEST_INDEX IND LEFT JOIN BS_LABTEST_INDEX BS ON IND.LABTEST_INDEX_CODE = BS.LABTEST_INDEX_CODE LEFT JOIN CHARGING_ITEM ITEM ON ITEM.CHARGING_ITEM_ID = BS.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE IND.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND BS.IS_DEL = 0 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND BS.ENABLED_FLAG = 98175 AND ITEM.ENABLED_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND IND.HOSPITAL_SOID = ?1 AND IND.CS_ID = ?2";

        /**
         * 组合计费-根据csId获取价格列表
         */
        String getBsCompositeCostByCsid = "SELECT ITEM.CHARGING_ITEM_QTY,P1.UNIT_PRICE FROM BS_COMPOSITE BS LEFT JOIN COMPOSITE_CHARGING_ITEM ITEM ON BS.BS_COMPOSITE_ID = ITEM.BS_COMPOSITE_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE	BS.IS_DEL = 0 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND BS.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.CS_ID = ?2";

        /**
         * 本服务计费-根据csId获取价格
         */
        String getBsSelfCostByCsid = "SELECT P1.UNIT_PRICE FROM BS_SELF BS LEFT JOIN CHARGING_ITEM ITEM ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID WHERE BS.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND BS.START_AT < SYSDATE AND BS.END_AT > SYSDATE AND P2.START_AT < SYSDATE AND P2.END_AT > SYSDATE AND BS.ENABLED_FLAG = 98175 AND ITEM.ENABLED_FLAG = 98175 AND P2.ENABLED_FLAG = 98175 AND P2.PREFERENCE_FLAG = 98175 AND BS.HOSPITAL_SOID = ?1 AND ITEM.HOSPITAL_SOID = ?1 AND P1.HOSPITAL_SOID = ?1 AND P2.HOSPITAL_SOID = ?1 AND BS.ENCOUNTER_TYPE_CODE = 138138 AND BS.CS_ID = ?2";

        /**
         * 根据计费策略获取临床服务
         */
        String getServiceListByStrategy = "SELECT CS.CS_ID,CS.CS_NO,(SELECT VALUE_DESC FROM VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE,CS.CS_NAME,BS.STRATEGY_CLASS_CODE,BS.STRATEGY_CLASS_NAME FROM CLINICAL_SERVICE CS LEFT JOIN CS_X_BILLING_STRATEGY X ON CS.CS_ID = X.CS_ID LEFT JOIN MED_INSTI_BS_CLASS BS ON X.STRATEGY_CLASS_CODE = BS.STRATEGY_CLASS_CODE WHERE CS.CS_ID IS NOT NULL AND X.CS_ID IS NOT NULL AND CS.CS_NO IS NOT NULL AND BS.STRATEGY_CLASS_CODE IS NOT NULL AND CS.IS_DEL = 0 AND X.IS_DEL = 0 AND BS.IS_DEL = 0 AND CS.HOSPITAL_SOID = ?1 AND X.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND X.ENCOUNTER_TYPE_CODE = 138138 AND CS.CS_STATUS = 98360 AND BS.ENABLED_FLAG = 98175 AND BS.STRATEGY_CLASS_CODE = ?2 AND COALESCE(CS.CS_SET_FLAG,98176) <> 98175";

        /**
         * 根据计费策略获取临床服务,ServiceTypeCode不为空
         */
        String getServiceListByStrategyAndServiceTypeCode = "SELECT CS.CS_ID,CS.CS_NO,(SELECT VALUE_DESC FROM VALUE_SET WHERE VALUE_ID = CS_TYPE_CODE) TYPE,CS.CS_NAME,BS.STRATEGY_CLASS_CODE,BS.STRATEGY_CLASS_NAME FROM CLINICAL_SERVICE CS LEFT JOIN CS_X_BILLING_STRATEGY X ON CS.CS_ID = X.CS_ID LEFT JOIN MED_INSTI_BS_CLASS BS ON X.STRATEGY_CLASS_CODE = BS.STRATEGY_CLASS_CODE WHERE CS.CS_ID IS NOT NULL AND X.CS_ID IS NOT NULL AND CS.CS_NO IS NOT NULL AND BS.STRATEGY_CLASS_CODE IS NOT NULL AND CS.IS_DEL = 0 AND X.IS_DEL = 0 AND BS.IS_DEL = 0 AND CS.HOSPITAL_SOID = ?1 AND X.HOSPITAL_SOID = ?1 AND BS.HOSPITAL_SOID = ?1 AND X.ENCOUNTER_TYPE_CODE = 138138 AND CS.CS_STATUS = 98360 AND BS.ENABLED_FLAG = 98175 AND BS.STRATEGY_CLASS_CODE = ?2 AND CS.CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = ?3 OR PARENT_VALUE_ID = ?3) AND COALESCE(CS.CS_SET_FLAG,98176) <> 98175";

        /**
         * 获取收藏列表所有西药的MEDICINE_ID
         */
        String getAllCommonMedId = "SELECT * FROM MEDICINE A INNER JOIN COMMON_USED_SERVICE B ON A.MEDICINE_ID = B.MEDICINE_ID AND B.CS_TYPE_CODE = 98095 AND DOCTOR_ID = 57393667239307269 AND B.IS_DEL = 0 order by B.CREATED_AT DESC";

        /**
         * 根据临床服务状态和临床服务类型代码获取临床服务
         */
        String getServiceListByCsStatusAndCsTypeCode = "SELECT CLI.CS_ID ID,CLI.CS_NAME NAME FROM CLINICAL_SERVICE CLI WHERE CLI.CS_STATUS = ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " AND CS_TYPE_CODE IN (SELECT VALUE_ID FROM VALUE_SET WHERE VALUE_ID = ?2 OR PARENT_VALUE_ID = ?2)";

        /**
         * 根据csid更新clinicalService表的csstatus状态
         */
        String updateClinicalServiceByCsName = "UPDATE CLINICAL_SERVICE SET CS_STATUS = ?1 WHERE CS_NAME = ?2";

        /**
         * 根据类型和soid获取设置的联动规则的值，为json格式
         */
        String getLinkAgeByType = "SELECT EXEC_RULE_VALUE FROM EXEC_RULE WHERE IS_DEL = 0 AND EXEC_RULE_TYPE_CODE = ?1 AND ENABLED_FLAG = 98360 AND HOSPITAL_SOID = ?2";

        /**
         * 根据默认给药途径查找药房可开立的药品
         ***/
        String getDrugByDefaultRoute = "SELECT a.COMMODITY_NAME_CHINESE,a.CS_ID FROM MEDICINE a INNER JOIN CLINICAL_SERVICE_DRUG b on a.CS_ID = b.CS_ID "
                + "INNER JOIN MEDICINE_STOCK c on a.MEDICINE_ID = c.MEDICINE_ID "
                + "INNER JOIN ORGANIZATION d on c.ORG_ID = d.ORG_ID "
                + "WHERE a.IS_DEL = 0 AND b.IS_DEL = 0 AND c.IS_DEL = 0 AND d.IS_DEL = 0 "
                + "AND c.MEDICINE_STOCK_QTY >10 "
                + "AND a.HOSPITAL_SOID = '?1' " + "AND d.ORG_NAME = '?2' " + "AND b.DEFAULT_USAGE_CODE = '?3' "
                + "AND rownum = 1";

        /***根据CS_ID在CLINICAL_SERVICE中查找CS_NAME***/
        String getCsNameByCsId = "SELECT CS_NAME FROM CLINICAL_SERVICE WHERE CS_ID = ?1";

        /**
         * 根据ghxh查找联动项信息
         */
        String getLinkInfoByGhxh = "SELECT c.CS_ID,c.CLI_ORDER_ITEM_CONTENT,c.PRESCRIBED_QTY,c.LINKAGE_FLAG FROM CLINICAL_ORDER a "
                + "INNER JOIN OUTPATIENT_ENCOUNTER b on a.ENCOUNTER_ID = b.ENCOUNTER_ID "
                + "INNER JOIN CLINICAL_ORDER_ITEM c on a.CLI_ORDER_ID = c.CLI_ORDER_ID "
                + "WHERE a.CLI_ORDER_SOURCE_CODE = 383979 AND b.ENC_REG_SEQ_NO = '?1' " + "AND a.IS_DEL = 0 AND b.IS_DEL = 0 "
                + "AND a.HOSPITAL_SOID = '?2'";

        /***根据EXAM_ITEM_ID在EXAMINATION_ITEM中查找EXAM_ITEM_NAME***/
        String getExamItemNameByExamItemId = "SELECT * FROM EXAMINATION_ITEM WHERE IS_DEL = 0 AND EXAM_ITEM_ID = ?1";

        /**
         * 查找员工信息
         */
        String getEmployeeNameByEmployeeId = "SELECT EMPLOYEE_NAME,EMPLOYEE_NO FROM EMPLOYEE_INFO WHERE EMPLOYEE_ID = '?1'";

        /**
         * 查找员工信息
         **/
        String getEmployeeIdByEmployeeNo = "SELECT EMPLOYEE_ID FROM EMPLOYEE_INFO WHERE EMPLOYEE_NO = '?1' AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据年龄段查找最小年龄
         **/
        String getBeginAgeBySet = "SELECT AGE_RANGE_BEGIN_VALUE FROM AGE_SEGMENT_SETTINGS WHERE AGE_SEGMENT_CODE = '?1' AND IS_DEL = 0 "
                + "AND AGE_SEGMENT_CONFIG_CODE = '867684' AND HOSPITAL_SOID = '?2'";

        /**
         * 根据患者姓名获取EncounterId
         **/
        String getEnCounterIdbyFullName = "SELECT b.BIZ_ROLE_ID,c.ENCOUNTER_ID FROM PERSON a INNER JOIN ROLE b on a.PERSON_ID = b.PERSON_ID "
                + "INNER JOIN OUTPATIENT_RECORD c on c.FULL_NAME = a.FULL_NAME "
                + "WHERE a.IS_DEL = 0 AND b.IS_DEL = 0 AND c.IS_DEL = 0 "
                + "AND a.FULL_NAME = '?1' ORDER BY c.CREATED_AT DESC";

        String getCONTACTIdbyENCOUNTERId = "SELECT OUTPATIENT_CONTACT_ID FROM OUTPATIENT_CONTACT WHERE ENCOUNTER_ID = '?1' ";

        String getEmployNoByUsercode = "SELECT EMPLOYEE_NO FROM EMPLOYEE_INFO WHERE USER_ID = (SELECT USER_ID FROM USER_INFO WHERE USER_CODE = '?1')";

        String getEmployIdByUsercode = "SELECT EMPLOYEE_ID FROM USER_INFO WHERE USER_CODE = '?1')";
        /**
         * 根据病历模板查找病历目录分类
         */
        String getInpEmrClassName = "SELECT INP_EMR_CLASS_NAME FROM INPATIENT_EMR_CLASS WHERE INP_EMR_CLASS_ID in (SELECT a.INP_EMR_CLASS_ID FROM INP_EMR_CLASS_MRT_CLASS A, INPATIENT_EMR_TEMPLATE B,INP_EMR_TEMPLATE_CLASS C WHERE A.INP_MRT_CLASS_ID = B.INP_MRT_CLASS_ID AND A.INP_MRT_CLASS_ID = C.INP_MRT_CLASS_ID AND B.INP_MRT_NAME LIKE '?1' AND A.IS_DEL = 0 AND A.HOSPITAL_SOID = '" + Data.hospital_soid + "' AND B.IS_DEL = 0 AND B.HOSPITAL_SOID = '" + Data.hospital_soid + "' AND C.HOSPITAL_SOID = 10)";

        /**
         * 根据paramNo查找ParamConfigId
         */
        String getParamConfigIdByParamNo = "SELECT C.PARAM_CONFIG_ID FROM PARAMETER_CONFIGURATION C INNER JOIN PARAMETER P ON P.PARAM_ID = C.PARAM_ID WHERE P.PARAM_NO = '?1' AND P.IS_DEL = 0 AND C.IS_DEL = 0 AND C.HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据住院病历阅改等级代码查询住院病历审阅流程标识ID
         */
        String getInpEmrReviewProcessId = "SELECT INP_EMR_REVIEW_PROCESS_ID FROM INP_EMR_REVIEW_PROCESS WHERE INP_EMR_REVIEW_LEVEL_CODE = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据住院病历模板名称获取模板的监控类型编码
         */
        String getInpMrtMonitorNo = "select MONITORING.INP_MRT_MONITOR_NO from  INPATIENT_EMR_TEMPLATE TEMPLATE ,INP_EMR_TEMPLATE_MONITORING MONITORING ,INP_EMR_CLASS_MRT_CLASS CLASS where TEMPLATE.INP_MRT_NAME = '?1' and TEMPLATE.INP_MRT_MONITOR_ID=MONITORING.INP_MRT_MONITOR_ID AND TEMPLATE.INP_MRT_CLASS_ID =  CLASS.INP_MRT_CLASS_ID and TEMPLATE.IS_DEL=0 and MONITORING.IS_DEL=0 and TEMPLATE.HOSPITAL_SOID ="+ Data.hospital_soid +" AND MONITORING.HOSPITAL_SOID ="+ Data.hospital_soid +"  and CLASS.IS_DEL=0  AND CLASS.HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 查询住院病历三级阅改流程与病历监控类型关系是否存在
         */
        String getInpEmrReviewMonitoring = "SELECT INP_EMR_REVIEW_MONITORING_ID FROM INP_EMR_REVIEW_MONITORING A WHERE A.INP_MRT_MONITORING_NO = '?1' AND A.INP_EMR_REVIEW_PROCESS_ID in (SELECT INP_EMR_REVIEW_PROCESS_ID FROM INP_EMR_REVIEW_PROCESS B WHERE B.INP_EMR_REVIEW_LEVEL_CODE = '399469360' AND B.IS_DEL = 0 AND B.HOSPITAL_SOID = '" + Data.hospital_soid + "') AND A.IS_DEL = 0";

        /**
         * 查找护理文书下拉框的选项值
         */
        String getChartDropdownValue = "SELECT VALUE_DESC FROM VALUE_SET WHERE CODE_SYSTEM_ID IN (SELECT DEFAULT_CODE_SYSTEM_ID FROM CHART_CONCEPT_DOMAIN WHERE CHART_CONCEPT_DOMAIN_ID = ?) AND IS_DEL = 0 ORDER BY VALUE_ID";

        /**
         * 查找护理文书模板ID、模板大类
         */
        String getChartTemplate = "SELECT CHART_TEMPLATE_ID,CHART_TEMPLATE_CLASS_CODE,CHART_TEMPLATE_TYPE_CODE FROM CHART_TEMPLATE WHERE CHART_TEMPLATE_NAME = '?1' AND IS_DEL = 0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "' AND CHART_TEMPLATE_STATUS = 399284018";

        /**
         * 根据患者姓名获取EnCounterId、BizRoleID、CURRENT_DEPT_ID、CURRENT_WARD_ID
         */
        String getEncounterInfoByFullName = "SELECT A.ENCOUNTER_ID,A.BIZ_ROLE_ID,A.CURRENT_DEPT_ID,A.CURRENT_WARD_ID FROM INPATIENT_ENCOUNTER A,ROLE B, PERSON C WHERE C.FULL_NAME = '?' AND C.PERSON_ID = B.PERSON_ID AND B.BIZ_ROLE_ID = A.BIZ_ROLE_ID AND A.IS_DEL = 0 AND B.IS_DEL = 0 AND C.IS_DEL = 0 ORDER BY A.CREATED_AT DESC";

        /**
         * 根据护理文书字段项ID获取字段的最大最小值
         */
        String getChartItemLimit = "SELECT CHART_ITEM_NUMBER_MIN_VAL, CHART_ITEM_NUMBER_MAX_VAL FROM CHART_ITEM WHERE CHART_ITEM_ID = ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";

        /**
         * 查询住院临床配置参数值
         */
        String getCliSettingValue = "select CLI_SETTING_VALUE from CLINICAL_SETTING_VALUE A where CLI_SETTING_ID in (SELECT CLI_SETTING_ID from  CLINICAL_SETTING  B where CLI_SETTING_NO ='?1' AND B.IS_DEL=0 and B.HOSPITAL_SOID= " + Data.hospital_soid + " AND B.ENABLED_FLAG='98360') AND A.HOSPITAL_SOID="+ Data.hospital_soid +" AND A.IS_DEL=0";

        /**
         * 更新住院临床配置参数值
         */
        String updateCliSettingValueByCliSettingNo = "update CLINICAL_SETTING_VALUE  set  CLI_SETTING_VALUE ='?2'  where CLI_SETTING_ID in (SELECT CLI_SETTING_ID from  CLINICAL_SETTING  B where CLI_SETTING_NO ='?1' AND B.IS_DEL=0 and B.HOSPITAL_SOID=" + Data.hospital_soid + " AND B.ENABLED_FLAG='98360') AND A.HOSPITAL_SOID=" + Data.hospital_soid +" AND A.IS_DEL=0";

        /**
         * 查询住院空床号
         */
        String getEmptyBedNoByOrgName = "select a.INPAT_BED_SERVICE_ID,a.BED_NO,a.INPAT_ROOM_ID,a.BED_TYPE_CODE from  INPATIENT_BED_SERVICE  a,INPATIENT_ROOM b,ORGANIZATION c where c.ORG_NAME='" + Data.inpatient_select_ward + "' and org_no='" + Data.inpatient_select_ward_code + "' and c.ORG_ID=b.WARD_ID and a.INPAT_ROOM_ID =b.inpat_room_id and a.ENABLED_FLAG=98175 and a.is_del=0 and b.IS_DEL=0 and c.IS_DEL=0 and a.INPAT_BED_SERVICE_ID  not in\n" +
                "(select BED_SERVICE_ID from  INPAT_BED_IN_USE where IS_DEL=0)  and a.INPAT_BED_SERVICE_ID  not in  (select BED_SERVICE_ID from INPAT_BED_RESERVATION where is_Del=0)";
        /**
         * 查询最大床位号
         */
        String getBedNo = "select max(SUBSTR(BED_NO,3)) BED_NO from  INPATIENT_BED_SERVICE where BED_NO like '" + Data.bedNoPrefix + "%'";


        //查询住院就诊登记流水号zyh
        String getInpatEncRegSeqNo = "select INPAT_ENC_REG_SEQ_NO from INPATIENT_ENCOUNTER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开始日期ksrq，停止日期jsrq，开立日期时间lrrq/rq/ysqrsj，医嘱天数ts
        String getTime = "select START_AT,TERMINATED_AT,PRESCRIBED_AT,DAYS_OF_USE from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品编码bm，临床服务名称yzmc
        String getCs = "select CS_NO,CS_NAME from CLINICAL_SERVICE where CS_ID in " +
                "( select CS_ID from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立数量cs，医嘱项内容kdmc，医嘱项标识cfh_wn，嘱托yszt
        String getCliOrderItemId = "select PRESCRIBED_QTY,CLI_ORDER_ITEM_CONTENT,CLI_ORDER_ITEM_ID,ADVICE from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品--开立单位dw,商品名pm
        String getBasicPackUnitCode = "select BASIC_PACK_UNIT_CODE,COMMODITY_NAME_CHINESE from MEDICINE where CS_ID in " +
                "(select CS_ID from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立医生ysbm
        String getEmployeeNo = "select EMPLOYEE_NO from EMPLOYEE_INFO where EMPLOYEE_ID in " +
                "(select PRESCRIBING_DOCTOR_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立病区bq
        String getOrgNo = "select ORG_NO from Organization where org_id in (select PRESCRIBING_WARD_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询开立科室kdks
        String getOrgNoKs = "select ORG_NO from Organization where org_id in (select PRESCRIBING_DEPT_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询执行科室ks
        String getOrgNoZx = "select ORG_NO from Organization where org_id in (select EXEC_DEPT_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询频次代码
        String geValueDesc = "select VALUE_DESC from VALUE_SET where VALUE_ID in (select FREQ_CODE from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0";
        //查询单位dw
        String geValueDescDw = "select VALUE_DESC from VALUE_SET where VALUE_ID in (select PRESCRIBED_UNIT_CODE from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in (select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0 ";
        //查询医嘱类型lx
        String geValueNo = "select VALUE_NO from VALUE_SET where VALUE_ID in (select CLI_ORDER_TYPE_CODE from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") and IS_DEL=0 ";
        // 查询包装规格
        String getPackSpec = "select PACK_SPEC from MEDICINE where CS_ID in " +
                "(select CS_ID from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询药品 零售价jg1
        String getPetailPrice = "select RETAIL_PRICE from MEDICINE_PRICE where MEDICINE_ID in " +
                "(select  MEDICINE_ID from MEDICINE where CS_ID in " +
                "(select CS_ID from CLINICAL_ORDER_ITEM where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+") AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+")AND IS_DEL = 0 AND HOSPITAL_SOID = "+Data.hospital_soid+"";
        //查询收费项目 零售价
        String getPetailPriceXm = "SELECT P1.UNIT_PRICE,ITEM.CHARGING_ITEM_NO,ITEM.CHARGING_ITEM_NAME FROM BS_SELF BS  " +
                "LEFT JOIN CHARGING_ITEM ITEM ON BS.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID " +
                "LEFT JOIN CHARGING_ITEM_PRICE P1 ON P1.CHARGING_ITEM_ID = ITEM.CHARGING_ITEM_ID " +
                "LEFT JOIN MED_INSTI_CHARGING_ITEM_PRICE P2 ON P1.PRICE_ID = P2.PRICE_ID " +
                "LEFT JOIN CLINICAL_ORDER_ITEM IT ON BS.CS_ID=IT.CS_ID " +
                "LEFT JOIN CLINICAL_ORDER ER ON IT.CLI_ORDER_ID=ER.CLI_ORDER_ID " +
                "WHERE ER.ENCOUNTER_ID='?1' AND BS.IS_DEL = 0 AND ITEM.IS_DEL = 0 AND P1.IS_DEL = 0 AND P2.IS_DEL = 0 AND IT.IS_DEL = 0 AND ER.IS_DEL = 0 " +
                "AND BS.HOSPITAL_SOID = "+Data.hospital_soid+" AND ITEM.HOSPITAL_SOID = "+Data.hospital_soid+" AND P1.HOSPITAL_SOID = "+Data.hospital_soid+" AND P2.HOSPITAL_SOID = "+Data.hospital_soid+" AND IT.HOSPITAL_SOID = "+Data.hospital_soid+" AND ER.HOSPITAL_SOID = "+Data.hospital_soid+" ";
        //查询姓名xm
        String getFullName = "select FULL_NAME from INPATIENT_RECORD where ENCOUNTER_ID= ?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询草药--给药途径yf，制出总量zl，剂量(每顿剂量)cl，服药周期付数jr
        String getHerbOrder = "select DOSAGE_ROUTE_CODE,PROCESSED_AMOUNT,DOSE,DRUG_USAGE_FREQ_QUANTITY from HERB_ORDER where CLI_ORDER_ID in " +
                "(select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询护士签收人编码qrhs，时间hsqrsj
        String getHandledBy = "select HANDLED_BY,HANDLED_AT from CLI_ORDER_CHANGE_REQUEST where CLI_ORDER_ID in" +
                " (select CLI_ORDER_ID from CLINICAL_ORDER where ENCOUNTER_ID='?1' AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询住院发药申请标识fysqbs_wn
        String getInpmeddispreqid = "select INP_MED_DISP_REQ_ID from INP_MED_DISPENSE_REQUEST where ENCOUNTER_ID= ?1  AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询住院发药计划标识fyjhbs_wn，住院发药剂量HL，住院发药数量SL
        String getInpmeddispplanid = "select INP_MED_DISP_PLAN_ID,INP_MED_DISP_DOSE,INP_MED_DISP_QTY from INP_MED_DISPENSE_PLAN where INP_MED_DISP_PLAN_ID in" +
                " (select INP_MED_DISP_PLAN_ID from INP_MED_DISPENSE_REQUEST where ENCOUNTER_ID=?1  AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + " ) " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询申请人zxrbm
        String getOperatedBy = "select EMPLOYEE_NO from EMPLOYEE_INFO where EMPLOYEE_ID in " +
                "(select OPERATED_BY from EXEC_ACTION_RECORD where EXEC_ACTION_RECORD_ID in " +
                "(select EXEC_ACTION_RECORD_ID from EXEC_ORDER_ACTION_RECORD where EXEC_ORDER_STATUS in " +
                "(select EXEC_ORDER_STATUS from EXEC_ORDER WHERE ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") " +
                "AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + ") AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询国籍
        String getInpatientInfo = "select VALUE_NO from VALUE_SET where VALUE_ID in (select NATIONALITY_CODE from INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 " ;
        //查询性别
        String getInpatientGender = "select VALUE_NO from VALUE_SET where VALUE_ID in (select GENDER_CODE from INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + ")  and IS_DEL=0 ";
        //查询名族
        String getInpatientNation = "select VALUE_NO from VALUE_SET where VALUE_ID in (select NATION_CODE from INPATIENT_RECORD where ENCOUNTER_ID=?1 and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 ";
        //查询入区途径
        String getInpatientAdmitted = "select VALUE_NO from VALUE_SET where VALUE_ID in (SELECT ADMITTED_TO_WARD_ROUTE_CODE FROM INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID =" + Data.hospital_soid + ")  and IS_DEL=0 ";
        //查询身份证号码，病案号，就诊登记流水号
        String getInpatientRecord = "select IDCARD_NO,CASE_NO,IMRN from INPATIENT_RECORD where ENCOUNTER_ID=?1 AND IS_DEL = 0 AND HOSPITAL_SOID = " + Data.hospital_soid + "";
        //查询床位
        String getInpatientBed = "select BED_NO from INPATIENT_BED_SERVICE a LEFT JOIN INPAT_BED_IN_USE b on a.INPAT_BED_SERVICE_ID = b.BED_SERVICE_ID where b.ENCOUNTER_ID='?1' and a.IS_DEL=0 and a.HOSPITAL_SOID =" + Data.hospital_soid + "";
        //查询住院科室
        String getInpatientRoom = "select ORG_NO from Organization where org_id in (SELECT CURRENT_DEPT_ID FROM INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID='?1' and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + " ";
        //查询住院病区
        String getInpatientWard = "select ORG_NO from Organization where org_id in (SELECT CURRENT_WARD_ID FROM INPATIENT_ENCOUNTER WHERE ENCOUNTER_ID='?1' and IS_DEL=0 and HOSPITAL_SOID =" + Data.hospital_soid + ") and IS_DEL=0 and HOSPITAL_SOID = " + Data.hospital_soid + " ";


        /**
         * 根据组织名称获取组织ID
         */
        String getOrgIdByOrgName = "SELECT ORG_ID FROM ORGANIZATION WHERE IS_DEL=0 AND ORG_TYPE_CODE= 230267 AND ORG_STATUS=98360 AND ORG_NAME ='?1' AND ORG_NO ='?2' AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

        /**
         * 根据病房名称和病区名查询病房ID
         */
        String getInpatRoomIdByRoomName = "select INPAT_ROOM_ID  from INPATIENT_ROOM  where ROOM_Name like '?1' and WARD_ID in(SELECT ORG_ID FROM ORGANIZATION WHERE IS_DEL=0 AND ORG_TYPE_CODE= 230267 AND ORG_STATUS=98360 AND ORG_NAME ='?2' AND ORG_NO ='?3' AND HOSPITAL_SOID = '" + Data.hospital_soid + "') And IS_DEL=0 AND HOSPITAL_SOID = '" + Data.hospital_soid + "'";

      /**
       * 根据ENCOUNTER_ID查询PHARMACY_ID
       */
        String getPharmacyIdbyEncounterId = "SELECT D.PHARMACY_ID from CLINICAL_ORDER O LEFT JOIN CLINICAL_ORDER_ITEM  I ON O.CLI_ORDER_ID = I.CLI_ORDER_ID LEFT JOIN DRUG_ORDER_ITEM D ON D.CLI_ORDER_ITEM_ID=I.CLI_ORDER_ITEM_ID WHERE O.ENCOUNTER_ID= '?1' AND D.PHARMACY_ID IS NOT NULL  ORDER BY D.CREATED_AT DESC";        

        /**
         * 通过患者姓名查找seqNo,OMRN
         */
        String getSeqNoByPatientName = "select T.TRIAGE_SEQ_NO seqNo,O.OMRN from TRIAGE T left join OUTPATIENT_RECORD O ON O.ENCOUNTER_ID = T.ENCOUNTER_ID  WHERE O.FULL_NAME ='?1' ORDER BY O.CREATED_AT DESC";
    }
}

