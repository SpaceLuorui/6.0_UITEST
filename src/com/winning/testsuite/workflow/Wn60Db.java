package com.winning.testsuite.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.winning.manager.WnSqlManager;

import ui.sdk.config.Data;
import ui.sdk.util.Log;
import ui.sdk.util.SdkDatabaseConn;
import ui.sdk.util.SdkTools;


/**
 * 6.0数据库访问相关接口
 *
 * @author Lenovo
 */
public class Wn60Db {

    public SdkDatabaseConn db = null;
    public Log logger = null;
    public static String userDir = System.getProperty("user.dir");
    public static String fileSeparator = File.separator;

    // 直接使用Data变量做配置
    public Wn60Db(Log l) {
        this.logger = l;
        db = new SdkDatabaseConn(Data.wn60DbType, Data.wn60DbHost, Data.wn60DbInstance, Data.wn60Dbname, Data.wn60DbService, Data.wn60DbUsername, Data.wn60DbPassword, l);
    }

    // 自定义配置
    public Wn60Db(String host, String instanceName, String dbName, String username, String password, Log l) {
        this.logger = l;
        db = new SdkDatabaseConn("sqlserver", host, instanceName, dbName, instanceName, username, password, l);
    }

    public void disconnect() {
        db.disconnect();
    }

    //根据csid查找cs_name
    public String getCsNameByCsId(String CSID) {
        String sql = new WnSqlManager().getCsNameByCsId(CSID);
        String CSName = db.queryFirstRow("根据CSID查找CSNAME", sql).get(0);
        return CSName;
    }
    public List<Map<String, String>> getServiceList(String tempPath, String delString, String notDelString) {
        List<Map<String, String>> allServiceList = new ArrayList<Map<String, String>>();
        // 数据库查询所有需要测试的医嘱
        try {
            if(Data.hisType.equals("WINEX")) {
            	
            }else {
            	if (Data.testMedicineFlag) {
                    List<Map<String, String>> medicineList = db.queryAllRow("查询所有药品商品", new WnSqlManager().getAllMedicineInpatientSql());
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
                    List<Map<String, String>> examItemList = db.queryAllRow("查询所有检查项目", new WnSqlManager().getAllExamItemSql());
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
                    List<Map<String, String>> treatList = db.queryAllRow("查询所有治疗服务", new WnSqlManager().getAllTreatSql());
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
                    List<Map<String, String>> labList = db.queryAllRow("查询所有检验服务", new WnSqlManager().getAllLabSql());
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
                if (Data.testPathologyFlag) {
                    List<Map<String, String>> pathologyList = db.queryAllRow("查询所有病理服务", new WnSqlManager().getAllPathologySql());
                    List<Map<String, String>> pathologyListValid = new ArrayList<Map<String, String>>();
                    for (Map<String, String> pathology : pathologyList) {
                        pathologyListValid.add(pathology);
                        if (Data.allServiceTestMaxNo != -1 && Data.allServiceTestMaxNo < pathologyListValid.size()) {
                            break;
                        }
                        else {
                            allServiceList.add(pathology);
                        }
                    }
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

    // 获取药品默认剂量
    public void getMedicineDosage(Map<String, String> medicine) {
        try {
            String dosage = null;
            if (medicine.get("TYPE").equals("drug")) {
                dosage = db.query("查询药品商品推荐剂量", new WnSqlManager().getDrugRcmddDosage(medicine.get("CS_ID")), "RCMDD_DOSAGE").get(0);
                if (dosage == null) {
                    dosage = db.query("查询药品通用名推荐剂量", new WnSqlManager().getMedicationDosageConvFactor(medicine.get("CS_ID")), "DOSAGE_CONV_FACTOR").get(0);
                }
            }
            else if (medicine.get("TYPE").equals("herb")) {
                dosage = db.query("查询药品商品推荐剂量", new WnSqlManager().getHerbRcmddDosage(medicine.get("CS_ID")), "RCMDD_DOSAGE").get(0);
                if (dosage == null) {
                    dosage = db.query("查询药品通用名推荐剂量", new WnSqlManager().getMedicationDosageConvFactor(medicine.get("CS_ID")), "DOSAGE_CONV_FACTOR").get(0);
                }
            }
            else {
                dosage = "药品类型不正确:" + medicine.get("CS_TYPE");
            }
            medicine.put("DB_DOSAGE", dosage);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认剂量失败:" + e.getMessage());
        }

    }

    // 获取药品默认剂量单位
    public void getMedicineDosageUnit(Map<String, String> medicine) {
        try {
            String dosageUnitDesc = null;
            String dosageUnitCode = null;
            if (medicine.get("TYPE").equals("drug")) {
                dosageUnitCode = db.query("查询药品商品推荐剂量单位", new WnSqlManager().getDrugRcmddDosageUnitCode(medicine.get("CS_ID")), "RCMDD_DOSAGE_UNIT_CODE").get(0);
                if (dosageUnitCode == null) {
                    dosageUnitCode = db.query("查询药品通用名推荐剂量单位", new WnSqlManager().getMedicationDosageUnitCode(medicine.get("CS_ID")), "DOSAGE_UNIT_CODE").get(0);
                }
                dosageUnitDesc = db.query("查询推荐剂量单位描述", new WnSqlManager().getMdmDescByValueId(dosageUnitCode), "VALUE_DESC").get(0);
            }
            else if (medicine.get("TYPE").equals("herb")) {
                dosageUnitCode = db.query("查询药品商品推荐剂量单位", new WnSqlManager().getHerbRcmddDosageUnitCode(medicine.get("CS_ID")), "RCMDD_DOSAGE_UNIT_CODE").get(0);
                if (dosageUnitCode == null) {
                    dosageUnitCode = db.query("查询药品通用名推荐剂量单位", new WnSqlManager().getMedicationDosageUnitCode(medicine.get("CS_ID")), "DOSAGE_UNIT_CODE").get(0);
                }
                dosageUnitDesc = db.query("查询推荐剂量单位描述", new WnSqlManager().getMdmDescByValueId(dosageUnitCode), "VALUE_DESC").get(0);
            }
            else {
                dosageUnitDesc = "药品类型不正确:" + medicine.get("CS_TYPE");
            }
            medicine.put("DB_DOSAGE_UNIT", dosageUnitDesc);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认剂量单位失败:" + e.getMessage());
        }
    }

    // 获取药品默认给药途径
    public void getMedicineRoute(Map<String, String> medicine) {
        try {
            String routeDesc = null;
            String RouteCode = null;
            List<String> RouteList = null;
            if (medicine.get("TYPE").equals("drug")) {

            	ArrayList<String> RCMDD_DOSAGE_ROUTE_TYPE_CODE_LIST = db.query("查询药品推荐给药途径代码", new WnSqlManager().getMedicineRcmddDosageRouteTypeCode(medicine.get("MEDICINE_ID")), "RCMDD_DOSAGE_ROUTE_TYPE_CODE");
                if(RCMDD_DOSAGE_ROUTE_TYPE_CODE_LIST != null && RCMDD_DOSAGE_ROUTE_TYPE_CODE_LIST.size() > 0) {
                	RouteCode = RCMDD_DOSAGE_ROUTE_TYPE_CODE_LIST.get(0);
                }

            	if(RouteCode == null) {
            		RouteCode = db.query("查询药品给药途径", new WnSqlManager().getDrugDefaultUsageCode(medicine.get("CS_ID")), "DEFAULT_USAGE_CODE").get(0);
            	}

                RouteList = db.query("查询药品给药途径列表", new WnSqlManager().getDosageRouteTypeCode(medicine.get("CS_ID")), "DOSAGE_ROUTE_TYPE_CODE");
                if (RouteList.size() == 0) {
                    RouteList = db.query("药品给药途径列表为空,查询给药途径术语列表", new WnSqlManager().getMedicationDosageRouteCode(medicine.get("CS_ID")), "DOSAGE_ROUTE_CODE");
                }
                if (RouteList.size() == 0) {
                    RouteList = db.query("查询药品给药途径描述", new WnSqlManager().getMdmValueIdByCodeSystemId("97829"), "VALUE_ID");
                }
                if (RouteCode == null || !RouteList.contains(RouteCode)) {
                    RouteCode = RouteList.get(0);
                }
                routeDesc = db.query(new WnSqlManager().getMdmDescByValueId(RouteCode), "VALUE_DESC").get(0);
            }
            else if (medicine.get("TYPE").equals("herb")) {
                routeDesc = "煎服";
            }
            else {
                routeDesc = "药品类型不正确:" + medicine.get("CS_TYPE");
            }
            medicine.put("DB_DOSAGE_ROUTE", routeDesc);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认给药途径失败:" + e.getMessage());
        }
    }

    // 获取药品默认频次
    public void getMedicineFreq(Map<String, String> medicine) {
        try {
            String freqDesc = null;
            String freqCode = null;
            List<String> FreqCodeList = null;
            if (medicine.get("TYPE").equals("drug")) {
                freqCode = db.query("查询药品配置的默认频次", new WnSqlManager().getDrugDefaultFreqCode(medicine.get("CS_ID")), "DEFAULT_FREQ_CODE").get(0);
                FreqCodeList = db.query("查询药品可用频次列表", new WnSqlManager().getFreqCode(medicine.get("CS_ID")), "FREQ_CODE");
                if (FreqCodeList.size() == 0) {
                    FreqCodeList = db.query("药品频次列表为空,查询频次术语列表", new WnSqlManager().getMdmValueIdByCodeSystemId("97828"), "VALUE_ID");
                }
                if (freqCode == null || !FreqCodeList.contains(freqCode)) {
                    freqCode = FreqCodeList.get(0);
                }
                freqDesc = db.query("查询默认频次描述", new WnSqlManager().getMdmDescByValueId(freqCode), "VALUE_DESC").get(0);
            }
            else if (medicine.get("TYPE").equals("herb")) {
                freqDesc = "BID";
            }
            else {
                freqDesc = "药品类型不正确:" + medicine.get("CS_TYPE");
            }
            medicine.put("DB_FREQ", freqDesc);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认频次失败:" + e.getMessage());
        }
    }

    public void getMedicinePackUnit(Map<String, String> medicine) {
        try {
            String sql = new WnSqlManager().getDefaultPackUnitCode(medicine.get("ID"));
            String packageUnitCode = db.query("查询默认门诊包装单位", sql, "PACK_UNIT_CODE").get(0);
            String packageUnitDesc = db.query("查询默认门诊包装单位描述", new WnSqlManager().getMdmDescByValueId(packageUnitCode), "VALUE_DESC").get(0);
            medicine.put("DB_PACKAGE_UNIT", packageUnitDesc);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认门诊包装单位失败:" + e.getMessage());
        }
    }

    public void getDisposalFactoryDefaultFromDb(Map<String, String> service) {
        getMedicineDosage(service);
        getMedicineDosageUnit(service);
        getMedicineRoute(service);
        getMedicineFreq(service);
        if (service.get("TYPE").equals("drug")) {
            getMedicinePackUnit(service);
        }
    }

    public void getInpatDisposalFactoryDefaultFromDb(Map<String, String> service) {
        getMedicineDosage(service);
        getMedicineDosageUnit(service);
        getMedicineRoute(service);   //草药的给药途径没有默认值，不比
        getMedicineFreq(service);
        if(service.get("TYPE").equals("herb")){
            //草药医嘱类型设置为临时医嘱，频次默认为ST
            service.put("DB_FREQ","ST");
        }
    }

    //  有库存的非皮试药品信息
    //	98363 -- 西成药
    //  98364 -- 中成药
    //	98365 -- 中草药
    public List<Map<String, String>> getNomalMedicineList(String orgName, String medicineType) {
        String sql = new WnSqlManager().getNormalMedicialList(orgName, medicineType);
        List<Map<String, String>> medicineList = db.queryAllRow("获取有库存的非皮试药品", sql);
        if (medicineList.size() == 0) {
            logger.log("获取有库存的非皮试药品");
            throw new Error("获取有库存的非皮试药品");
        }
        return medicineList;
    }

    //获取没有库存的非皮试药品信息
    public List<Map<String, String>> getOutOfStocklMedicineList(String orgName, String medicineType) {
        String sql = new WnSqlManager().getOutOfStocklMedicineList(orgName, medicineType);
        List<Map<String, String>> medicineList = db.queryAllRow("获取无库存的非皮试药品", sql);
        if (medicineList.size() == 0) {
            logger.log("获取无库存的非皮试药品");
            throw new Error("获取无库存的非皮试药品");
        }
        return medicineList;
    }

    //获取所有医保药品
    public List<Map<String, String>> getInsurApprovalMedicineList(String MED_INSTI_INSUR_ID, String soid) {
        String sql = new WnSqlManager().getInsurApprovalMedicine(MED_INSTI_INSUR_ID, soid);
        List<Map<String, String>> medicineList = db.queryAllRow("获取需要审批的药品", sql);
        if (medicineList.size() == 0) {
            logger.log("获取需要审批的药品失败");
            throw new Error("获取需要审批的药品失败");
        }
        return medicineList;
    }

    //获取指定类型并且有库存的医保审批药品
    public Map<String, String> getInsurApprovalMedicine(String orgName, String medicineType, String MED_INSTI_INSUR_ID, String soid) {
        //获取所有医保药品
        List<Map<String, String>> medList_ins = getInsurApprovalMedicineList(MED_INSTI_INSUR_ID, soid);
        //获取所有有库存药品
        List<Map<String, String>> medList_stock = getNomalMedicineList(orgName, medicineType);
        //取交集
        Map<String, String> medicine_stock_ins = null;
        for (Map<String, String> med_ins : medList_ins) {
            if (SdkTools.getMapByValue(medList_stock, "MEDICINE_ID", med_ins.get("MEDICINE_ID")) != null) {
                medicine_stock_ins = med_ins;
                break;
            }
        }
        logger.assertFalse(medicine_stock_ins == null, "获取不到医保审批且有库存的药品");
        return medicine_stock_ins;
    }

    //获取指定类型并且有库存的医保审批药品
    public Map<String, String> getNotInsurApprovalMedicine(String orgName, String medicineType, String MED_INSTI_INSUR_ID, String soid) {
        //获取所有医保药品
        List<Map<String, String>> medList_ins = getInsurApprovalMedicineList(MED_INSTI_INSUR_ID, soid);
        //获取所有有库存药品
        List<Map<String, String>> medList_stock = getNomalMedicineList(orgName, medicineType);
        //取交集
        Map<String, String> medicine_stock_not_ins = null;
        for (Map<String, String> med_stock : medList_stock) {
            if (SdkTools.getMapByValue(medList_ins, "MEDICINE_ID", med_stock.get("MEDICINE_ID")) == null) {
                medicine_stock_not_ins = med_stock;
                break;
            }
        }
        logger.assertFalse(medicine_stock_not_ins == null, "获取不到 不需要医保审批且有库存的药品");
        return medicine_stock_not_ins;
    }


    public Map<String, String> getNomalMedicine(String orgName, String medicineType) {
        return getNomalMedicineList(orgName, medicineType).get(0);
    }

    public List<Map<String, String>> getSkinTestMedicineList(String orgName, List<String> skinTestTypeList, Boolean astFreeFlag, Boolean hasPeriod) {
        // 拼接查询条件: 皮试类型
        String skinTestTypeString = "(";
        for (String skinTestType : skinTestTypeList) {
            skinTestTypeString += skinTestType + ",";
        }
        skinTestTypeString = skinTestTypeString.substring(0, skinTestTypeString.length() - 1) + ")";
        String sql = new WnSqlManager().getSkinTestMedicine(orgName, skinTestTypeString);
        // 拼接查询条件: 是否有免试标识
        if (astFreeFlag == null) {
            // 等于null时查所有
        }
        else if (astFreeFlag) {
            sql += " AND DET.AST_FREE_FLAG=98175";
        }
        else {
            sql += " AND (DET.AST_FREE_FLAG=98176 OR DET.AST_FREE_FLAG IS NULL)";
        }
        // 拼接查询条件: 是否有有效期
        if (hasPeriod == null) {
            // 等于null时查所有
        }
        else if (hasPeriod) {
            sql += " AND DET.AST_VALID_PERIOD_UNIT_CODE=230490 AND DET.AST_VALID_PERIOD IS NOT NULL AND DET.AST_VALID_PERIOD<>0";
        }
        else {
            sql += " AND DET.AST_VALID_PERIOD IS NULL";
        }
        List<Map<String, String>> res = db.queryAllRow("获取皮试药品", sql);
        if (res.size() == 0) {
            logger.log("获取皮试药品失败");
            throw new Error("获取皮试药品失败");
        }
//		Map<String, String> medicine=res.get(0);
        return res;
    }

    //  返回一个有库存的皮试西成药信息
    //  skinTestTypeList -- 皮试类型数组(249942--皮试液皮试, 249943--原液皮试)
    //  orgName -- 指定药房有库存
    //  astFreeFlag(选填) -- 是否勾选免试标识: true:勾选   false:未勾选  null:随便
    //  hasPeriod(选填) -- 是否有皮试有效期:  true:有   false:没有  null:随便
    public Map<String, String> getSkinTestMedicine(String orgName, List<String> skinTestTypeList, Boolean astFreeFlag, Boolean hasPeriod) {
        // 拼接查询条件: 皮试类型
        String skinTestTypeString = "(";
        for (String skinTestType : skinTestTypeList) {
            skinTestTypeString += skinTestType + ",";
        }
        skinTestTypeString = skinTestTypeString.substring(0, skinTestTypeString.length() - 1) + ")";
        String sql = new WnSqlManager().getSkinTestMedicine(orgName, skinTestTypeString);
        // 拼接查询条件: 是否有免试标识
        if (astFreeFlag == null) {
            // 等于null时查所有
        }
        else if (astFreeFlag) {
            sql += " AND DET.AST_FREE_FLAG=98175";
        }
        else {
            sql += " AND (DET.AST_FREE_FLAG=98176 OR DET.AST_FREE_FLAG IS NULL)";
        }
        // 拼接查询条件: 是否有有效期
        if (hasPeriod == null) {
            // 等于null时查所有
        }
        else if (hasPeriod) {
            sql += " AND DET.AST_VALID_PERIOD_UNIT_CODE=230490 AND DET.AST_VALID_PERIOD IS NOT NULL AND DET.AST_VALID_PERIOD<>0";
        }
        else {
            sql += " AND DET.AST_VALID_PERIOD IS NULL";
        }

        List<Map<String, String>> res = db.queryAllRow("获取皮试药品", sql);
        if (res.size() == 0) {
            logger.log("获取皮试药品失败");
            throw new Error("获取皮试药品失败");
        }
        Map<String, String> medicine = res.get(0);
        return medicine;
    }

    public Map<String, String> getSkinTestMedicine(String orgName, List<String> skinTestTypeList, Boolean astFreeFlag) {
        return getSkinTestMedicine(orgName, skinTestTypeList, astFreeFlag, null);
    }

    public Map<String, String> getSkinTestMedicine(String orgName, List<String> skinTestTypeList) {
        return getSkinTestMedicine(orgName, skinTestTypeList, null, null);
    }


    // ServiceTypeCode:检验 - 98071, 检查 - 98078, 病理 - 98088 ,治疗 - 98098
    //	256162	本服务计费策略
    //	256163	不计费策略
    //	256164	组合计费策略
    //	256167	成员计费_检验按指标明细合计计费策略
    //	256168	成员计费_检验按指标个数区间_个数加收计费策略
    //	256169	成员计费_检验按指标个数区间_不累计计费策略
    //	256170	成员计费_检验按指标个数区间_累计计费策略
    //	256172	成员计费_检查服务项目按个数加收计费策略
    //	256175	成员计费_检查服务项目按个数区间计费_不累计计费策略
    //	256176	成员计费_检查服务项目按个数区间计费_累计计费策略
    //	376726	成员计费_检查按项目明细合计计费策略
    //	256166	成员计费_检验按指标检测方法计费策略
    public List<Map<String, String>> getServiceListByStrategy(String ServiceTypeCode, String StrategyClassCode, String soid) {
        String sql = "";
        if (ServiceTypeCode != null) {
            sql = new WnSqlManager().getServiceListByStrategyAndServiceTypeCode(soid, StrategyClassCode, ServiceTypeCode);
        }
        else {
            sql = new WnSqlManager().getServiceListByStrategy(soid, StrategyClassCode);
        }
        List<Map<String, String>> serviceList = db.queryAllRow("根据计费策略获取临床服务", sql);
        if (serviceList.size() == 0) {
            logger.log("根据计费策略获取临床服务失败");
            throw new Error("根据计费策略获取临床服务失败");
        }
        return serviceList;

    }

    //本服务计费策略
    public String getBsSelfCostByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsSelfCostByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("本服务计费-根据csId获取价格", sql);
        if (priceList.size() != 1) {
            logger.log("本服务计费-根据csId获取价格失败");
            return null;
        }
        return priceList.get(0).get("UNIT_PRICE");
    }

    //组合计费策略
    public String getBsCompositeCostByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsCompositeCostByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("组合计费-根据csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("组合计费-根据csId获取价格列表失败");
            return null;
        }
        Double finalPrice = 0.0;
        for (Map<String, String> price : priceList) {
            finalPrice += Double.valueOf(price.get("UNIT_PRICE")) * Double.valueOf(price.get("CHARGING_ITEM_QTY"));
        }
        return "" + finalPrice;
    }

    //成员计费_检验按指标明细合计计费策略
    public List<Map<String, String>> getBsLabtestIndexCostByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsLabtestIndexCostByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检验按指标明细合计计费策略-根据csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检验按指标明细合计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }

    //成员计费_检验按指标个数区间_个数加收计费策略
    public List<Map<String, String>> getBsLabtestIdxRangeExFeeByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsLabtestIdxRangeExFeeByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检验按指标明细合计计费策略-根据csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检验按指标明细合计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }

    //成员计费_检验按指标个数区间_不累计计费策略
    public List<Map<String, String>> getBsLabtestIdxRanNoneSumByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsLabtestIdxRanNoneSumByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检验按指标个数区间_不累计计费策略-csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检验按指标个数区间_不累计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }

    //成员计费_检验按指标个数区间_累计计费策略
    public List<Map<String, String>> getBsLabtestIndexRangeSumByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsLabtestIndexRangeSumByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检验按指标个数区间_累计计费策略-csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检验按指标个数区间_累计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }

    //成员计费_检查按项目明细合计计费策略
    public List<Map<String, String>> getBsExamItemByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsExamItemByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检查按项目明细合计计费策略-csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检查按项目明细合计计费策略 - 根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }


    //成员计费_检查服务项目按个数加收计费策略
    public List<Map<String, String>> getBsExamItemRangeExtraFeeByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsExamItemRangeExtraFeeByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检查服务项目按个数加收计费策略-根据csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检查服务项目按个数加收计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }


    //成员计费_检查服务项目按个数区间计费_不累计计费策略
    public List<Map<String, String>> getBsExamItemRangeNoneSumByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsExamItemRangeNoneSumByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检查服务项目按个数区间计费_不累计计费策略-csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检查服务项目按个数区间计费_不累计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }


    //成员计费_检查服务项目按个数区间计费_累计计费策略
    public List<Map<String, String>> getBsExamItemRangeSumByCsid(String csId, String soid) {
        String sql = new WnSqlManager().getBsExamItemRangeSumByCsid(soid, csId);
        List<Map<String, String>> priceList = db.queryAllRow("成员计费_检查服务项目按个数区间计费_累计计费策略-csId获取价格列表", sql);
        if (priceList.size() == 0) {
            logger.log("成员计费_检查服务项目按个数区间计费_累计计费策略-根据csId获取价格列表失败");
            return null;
        }
        return priceList;
    }

    // 获取主数据默认参数
    public Map<String, String> getParam(String paramNo) {
        String sql = new WnSqlManager().getMdmParameter(paramNo);
        List<Map<String, String>> paramList = db.queryAllRow("获取参数:" + paramNo, sql);
        if (paramList.size() == 0) {
            throw new Error("获取参数失败");
        }
//        if (paramList.size() != 1) {
//            throw new Error("主数据参数存在多条记录，请确认");
//        }
        return paramList.get(0);
    }

    //根据当前病历模板名称获取门诊诊断是否为空
    public Boolean getFlagByEmrMrtId(String MrtName) {
        Boolean flag = null;
        String sql = new WnSqlManager().getEmrTsEmptySubmitFlag(MrtName);
        ArrayList<String> items = db.query("根据模板名字获取诊断段落是否可以为空提交FLAG", sql, "TS_EMPTY_SUBMIT_FLAG");
        if (items.size() != 1) {
            throw new Error("60库中查找不到TS_EMPTY_SUBMIT_FLAG:" + sql);
        }
        if (items.get(0).equals("98176")) {
            flag = true;
        }
        else {
            flag = false;
        }
        return flag;
    }

    //根据患者姓名获取挂号记录
    public List<Map<String, String>> getPatientRecordByName(String name) {
        String sql = new WnSqlManager().getPatientRecordByName(name);
        List<Map<String, String>> recordList = db.queryAllRow("获取患者挂号信息:" + name, sql);
        if (recordList.size() == 0) {
            logger.log(2, "根据患者姓名查询不到挂号记录:" + name);
            throw new Error("根据患者姓名查询不到挂号记录:" + name);
        }
        return recordList;
    }

    //获取医保审批医嘱项列表
    public List<Map<String, String>> getInsurOrderItemsByEncounterid(String encounterid) {
        String sql = new WnSqlManager().getInsurOrderItemsByEncounterid(encounterid);
        List<Map<String, String>> insurOrderItems = db.queryAllRow("获取医保审批医嘱项列表", sql);
        return insurOrderItems;
    }

    //获取医保信息
    public Map<String, String> getInsuranceListByID(String insuranceID) {
        String sql = new WnSqlManager().getInsuranceListByID(insuranceID);
        List<Map<String, String>> insurOrderItems = db.queryAllRow("获取医保信息", sql);
        logger.assertFalse(insurOrderItems.size() != 1, "没有找到医保信息");
        return insurOrderItems.get(0);
    }

    //根据医保类型获取临床服务
    public List<Map<String, String>> getInsurApprovalService(String ServiceTypeCode, String insuranceID, String soid) {
        String sql = "";
        if (ServiceTypeCode != null) {
            sql = new WnSqlManager().getInsurApprovalService(ServiceTypeCode, insuranceID, soid);
        }
        else {
            sql = new WnSqlManager().getInsurApprovalServiceNoType(insuranceID, soid);
        }
        List<Map<String, String>> serviceList = db.queryAllRow("根据医保类型获取临床服务", sql);
        if (serviceList.size() == 0) {
            logger.log("根据医保类型获取临床服务失败");
            throw new Error("根据医保类型获取临床服务失败");
        }
        return serviceList;
    }


    //返回收藏的所有西药MEDICINE_ID
    public List<Map<String, String>> getAllCommonMedId() {
        String sql = new WnSqlManager().getAllCommonMedId();
        List<Map<String, String>> Medlist = db.queryAllRow("获取MEDICINE_ID", sql);
        if (Medlist == null) {
            throw new Error("执行sql失败.");
        }
        return Medlist;
    }


    //根据MEDICINE_ID获取CS_ID
    public List<Map<String, String>> getCsidByMedicineId(String MEDICINE_ID) {
        String sql = new WnSqlManager().getCsidByMedicineId(MEDICINE_ID);
        List<Map<String, String>> cslist = db.queryAllRow("获取CS_ID", sql);
        if (cslist.size() == 0) {
            logger.log("没找到对应的CS_ID");
            return null;
        }
        return cslist;
    }

    // 获取输液药品默认给药途径
    public void getInfusionMedicineRoute() {
        try {
            String routeDesc = null;
            String RouteCode = null;
            List<String> RouteList = null;

//			for (){
//
//			}
//			if (medicine.get("TYPE").equals("drug")) {
//				RouteCode = db.query("查询药品给药途径", new WnSqlManager().getDrugDefaultUsageCode(medicine.get("CS_ID")), "DEFAULT_USAGE_CODE").get(0);
//				RouteList = db.query("查询药品给药途径列表", new WnSqlManager().getDosageRouteTypeCode(medicine.get("CS_ID")), "DOSAGE_ROUTE_TYPE_CODE");
//				if (RouteList.size()==0) {
//					RouteList=db.query("药品给药途径列表为空,查询给药途径术语列表", new WnSqlManager().getMedicationDosageRouteCode(medicine.get("CS_ID")), "DOSAGE_ROUTE_CODE");
//				}
//				if (RouteList.size()==0) {
//					RouteList=db.query("查询药品给药途径描述",new WnSqlManager().getMdmValueIdByCodeSystemId("97829"), "VALUE_ID");
//				}
//				if (RouteCode == null || !RouteList.contains(RouteCode)) {
//					RouteCode = RouteList.get(0);
//				}
//				routeDesc = db.query(new WnSqlManager().getMdmDescByValueId(RouteCode), "VALUE_DESC").get(0);
//			}else if (medicine.get("TYPE").equals("herb")) {
//				routeDesc = "煎服";
//			}else{
//				routeDesc = "药品类型不正确:"+medicine.get("CS_TYPE");
//			}
//			medicine.put("DB_DOSAGE_ROUTE", routeDesc);
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new Error("加工厂默认值错误:数据库查询默认给药途径失败:" + e.getMessage());
        }
    }

    public String getRouteByMedId(String medicine_id) {
        String Route = null;
        String sql = new WnSqlManager().getCsidByMedicineId(medicine_id);
        List<Map<String, String>> cslist = db.queryAllRow("获取CS_ID", sql);
        if (cslist.size() == 0) {
            logger.log("没找到对应的CS_ID");
            return null;
        }
        String cs_id = cslist.get(0).get("CS_ID");
        String RouteCode = db.query("查询药品给药途径", new WnSqlManager().getDrugDefaultUsageCode(cs_id), "DEFAULT_USAGE_CODE").get(0);
        ArrayList<String> RouteList = db.query("查询药品给药途径列表", new WnSqlManager().getDosageRouteTypeCode(cs_id), "DOSAGE_ROUTE_TYPE_CODE");
        if (RouteList.size() == 0) {
            RouteList = db.query("药品给药途径列表为空,查询给药途径术语列表", new WnSqlManager().getMedicationDosageRouteCode(cs_id), "DOSAGE_ROUTE_CODE");
        }
        if (RouteList.size() == 0) {
            RouteList = db.query("查询药品给药途径描述", new WnSqlManager().getMdmValueIdByCodeSystemId("97829"), "VALUE_ID");
        }
        if (RouteCode == null || !RouteList.contains(RouteCode)) {
            RouteCode = RouteList.get(0);
        }
        Route = db.query(new WnSqlManager().getMdmDescByValueId(RouteCode), "VALUE_DESC").get(0);
        return Route;
    }

    //根据科室和医生获取诊疗路径推荐主诉
    public List<Map<String, String>> getRecommendSymptom(String subjCode, String doctorUserName, String genderCode, String soid) {
        String sql = new WnSqlManager().getRecommendSymptom(subjCode, doctorUserName, genderCode, soid);
        List<Map<String, String>> symptomList = db.queryAllRow("根据科室和医生获取诊疗路径推荐主诉", sql);
        logger.assertFalse(symptomList.size() == 0, "根据科室(" + subjCode + ")和医生(" + doctorUserName + ")获取不到 诊疗路径推荐主诉");
        return symptomList;
    }

    //根据科室和医生获取诊疗路径推荐诊断
    public List<Map<String, String>> getRecommendDiagnose(String subjCode, String doctorUserName, String genderCode, String soid) {
        String sql = new WnSqlManager().getRecommendDiagnose(subjCode, doctorUserName, genderCode, soid);
        List<Map<String, String>> symptomList = db.queryAllRow("根据科室和医生获取诊疗路径推荐诊断", sql);
        logger.assertFalse(symptomList.size() == 0, "根据科室(" + subjCode + ")和医生(" + doctorUserName + ")获取不到 诊疗路径推荐诊断");
        return symptomList;
    }

    //根据药品类型获取药品列表
    //	152650	普通药品
    //	152651	一类精神药品
    //	152652	二类精神药品
    //	152653	麻醉药品
    //	152654	毒性药品
    //	152655	放射性药品
    public List<Map<String, String>> getDrugByPsychotropicsCode(String PsychotropicsCode) {
        String sql = new WnSqlManager().getDrugByPsychotropicsCode(PsychotropicsCode);
        List<Map<String, String>> symptomList = db.queryAllRow("根据精麻毒放类型,获取药品", sql);
        logger.assertFalse(symptomList.size() == 0, "根据精麻毒放类型:" + PsychotropicsCode + " 获取不到药品");
        return symptomList;
    }

    //获取临床服务
    public List<Map<String, String>> getServiceListByCsTypeCodeAndCsStatus(String csTypeCode, String csStatus) {
        String sql = new WnSqlManager().getServiceListByCsTypeCodeAndCsStatus(csTypeCode, csStatus);
        List<Map<String, String>> serviceList = db.queryAllRow("获取需要的临床服务", sql);
        if (serviceList.size() == 0) {
            logger.log("获取需要的临床服务失败");
            throw new Error("获取需要的临床服务失败");
        }
        return serviceList;
    }

    //更新临床服务停用状态
    public void updateClinicalServiceByCsName(String CsStatus, String CsName) {
        System.out.println("-----" + CsStatus);
        System.out.println(CsName);
        String sql = new WnSqlManager().updateClinicalServiceByCsName(CsStatus, CsName);
        db.excute(sql);
    }

    /***根据类型和soid获取设置的联动规则的值，为json格式
     * 399010220 西药联动；399010221 草药联动；399010222 检查联动
     * **/
    public String getLinkAgeByType(String LinkType, String Soid) {
        String sql = new WnSqlManager().getLinkAgeByType(LinkType, Soid);
        Map<String, String> LinkValue = db.queryFirstRow("根据类型和soid获取设置的联动规则的值", sql);
        if (!LinkValue.isEmpty()) {
            return LinkValue.get("EXEC_RULE_VALUE");
        }
        else {
            return "";
        }
    }

    /**
     * 根据默认给药途径查找药房可开立的药品
     ***/
    public List<Map<String, String>> getDrugByDefaultRoute(String SOID, String ORG_NAME, String DEFAULT_USAGE_CODE) {
        String sql = new WnSqlManager().getDrugByDefaultRoute(SOID, ORG_NAME, DEFAULT_USAGE_CODE);
        List<Map<String, String>> DrugInfo = db.queryAllRow("根据默认给药途径查找药房可开立的药品", sql);
        logger.assertFalse(DrugInfo == null, "根据药房(" + ORG_NAME + ")和默认给药途径(" + DEFAULT_USAGE_CODE + ")获取不到 西药");

        return DrugInfo;

    }

    /***根据挂号序号查找联动信息**/
    public List<Map<String, String>> getLinkInfoByGhxh(String ghxh, String soid) {
        String sql = new WnSqlManager().getLinkInfoByGhxh(ghxh, soid);
        List<Map<String, String>> LinkAgeInfo = db.queryAllRow("根据挂号序号查找联动信息", sql);
        return LinkAgeInfo;
    }

    /***根据EXAM_ITEM_ID在EXAMINATION_ITEM中查找EXAM_ITEM_NAME***/
    public String getExamItemNameByExamItemId(String ExamItemId) {
        String sql = new WnSqlManager().getExamItemNameByExamItemId(ExamItemId);
        Map<String, String> LinkValue = db.queryFirstRow("根据EXAM_ITEM_ID在EXAMINATION_ITEM中查找EXAM_ITEM_NAME", sql);
        if (!LinkValue.isEmpty()) {
            return LinkValue.get("EXAM_ITEM_NAME");
        }
        else {
            return "";
        }
    }


    /***根据EXAM_ITEM_ID查找检查项目***/
    public Map<String, String> getExamItemByExamItemId(String ExamItemId) {
        String sql = new WnSqlManager().getExamItemNameByExamItemId(ExamItemId);
        List<Map<String, String>> itemList = db.queryAllRow("根据EXAM_ITEM_ID查找检查项目", sql);
        logger.assertFalse(itemList.size() < 1, "查询不到 EXAM_ITEM_ID:" + ExamItemId);
        logger.assertFalse(itemList.size() > 1, "查询到多条 EXAM_ITEM_ID:" + ExamItemId);
        return itemList.get(0);
    }

    //根据medicineId获取药品
    public Map<String, String> getMedicineByMedicineId(String medicineId) {
        List<Map<String, String>> drugList = db.queryAllRow("查询药品表", "SELECT * FROM WINDBA.MEDICINE WHERE MEDICINE_ID=?1 AND IS_DEL=0".replace("?1", medicineId));
        logger.assertFalse(drugList.size() < 1, "查询不到 medicine_id:" + medicineId);
        logger.assertFalse(drugList.size() > 1, "查询到多条 medicine_id:" + medicineId);
        return drugList.get(0);
    }

    //根据CSID获取medicineid
    public String getMedicineIdbyCSID(String CSID,String HospitalSoid){
        String sql=new WnSqlManager().getMedicineIdbyCSID(CSID,HospitalSoid);
        Map<String, String>  Medinfo=db.queryFirstRow("根据CSID查找MedicineID,只取第一条",sql);
        String Medid=Medinfo.get("MEDICINE_ID");
        logger.assertFalse(Medinfo.isEmpty(), "根据CSID:"+CSID+",没有查找到MedicineID");
        return Medid;
    }

    //根据csId获取临床服务
    public Map<String, String> getServiceByCsId(String csId) {
        List<Map<String, String>> serviceList = db.queryAllRow("查询临床服务表", "SELECT * FROM WINDBA.CLINICAL_SERVICE WHERE CS_ID=?1 AND IS_DEL=0".replace("?1", csId));
        logger.assertFalse(serviceList.size() < 1, "查询不到临床服务 cs_id:" + csId);
        logger.assertFalse(serviceList.size() > 1, "查询到多条临床服务 cs_id:" + csId);
        return serviceList.get(0);
    }

    ////根据valueId获取valueset
    public Map<String, String> getValueSetByValueId(String valueId) {
        List<Map<String, String>> serviceList = db.queryAllRow("查询术语列表", "SELECT * FROM WINDBA.VALUE_SET WHERE VALUE_ID=?1 AND IS_DEL=0".replace("?1", valueId));
        logger.assertFalse(serviceList.size() < 1, "查询不到术语 valueId:" + valueId);
        logger.assertFalse(serviceList.size() > 1, "查询到多条术语 valueId:" + valueId);
        return serviceList.get(0);
    }

    //根据valueId获取valueset
    public List<Map<String, String>> getValueSetByParentValueId(String PARENT_VALUE_ID) {
        List<Map<String, String>> valueList = db.queryAllRow("查询术语列表", "SELECT * FROM WINDBA.VALUE_SET WHERE PARENT_VALUE_ID=?1 AND IS_DEL=0".replace("?1", PARENT_VALUE_ID));
        return valueList;
    }

    //根据valueId获取parentValueId
    public Map<String, String> getParentValueIdByValueId(String VALUE_ID) {
        //ArrayList<String> parentValueId = db.query("查询术语列表", "SELECT PARENT_VALUE_ID FROM WINDBA.VALUE_SET WHERE VALUE_ID=?1 AND IS_DEL=0".replace("?1", VALUE_ID));
        Map<String, String> parentValueId = null;
        List<Map<String, String>> parentValueIdList =db.queryAllRow("查询术语列表", "SELECT * FROM WINDBA.VALUE_SET WHERE VALUE_ID=?1 AND IS_DEL=0".replace("?1", VALUE_ID));
        if(null!=parentValueIdList && parentValueIdList.size()>0) {
            parentValueId = parentValueIdList.get(0);
        }
        return parentValueId;

    }

    //临床域方案中根据年龄段查找最小值
    public String getBeginAgeBySet(String AGE_SEGMENT_CODE, String Soid) {
        String sql = new WnSqlManager().getBeginAgeBySet(AGE_SEGMENT_CODE, Soid);
        Map<String, String> AgeInfo = db.queryFirstRow("临床域方案中根据年龄段查找最小年龄", sql);
        if (!AgeInfo.isEmpty()) {
            return AgeInfo.get("AGE_RANGE_BEGIN_VALUE");
        }
        else {
            return "";
        }
    }

    //根据CSID获取检查项目列表
    public List<Map<String, String>> getExamItemListByCsid(String CS_ID) {
        List<Map<String, String>> exam_item_list = db.queryAllRow("SELECT * FROM WINDBA.EXAMINATION_ITEM WHERE CS_ID=?1 AND IS_DEL=0".replace("?1", CS_ID));
        return exam_item_list;
    }

    //根据患者姓名查找EnCounterId
    public Map<String, String> getEnCounterIdbyFullName(String Full_name) {
        String sql = new WnSqlManager().getEnCounterIdbyFullName(Full_name);
        Map<String, String> RES = db.queryFirstRow("根据患者姓名查找BizRoleId和EncounterID", sql);
        return RES;
    }

    //根据medicine_id获取药品门诊开立基本单位
    public Map<String, String> getOutpUnitByMedicineId(String MEDICINE_ID) {
        String sql = "SELECT * FROM WINDBA.MEDICINE_PACKING_UNIT UNIT INNER JOIN WINDBA.MEDICINE_PACK_UNIT_USE_TYPE TYPE ON UNIT.MEDICINE_PACK_UNIT_ID=TYPE.MEDICINE_PACK_UNIT_ID WHERE UNIT.IS_DEL=0 AND TYPE.IS_DEL=0 AND TYPE.PACK_UNIT_USE_TYPE_CODE=256147 AND UNIT.MEDICINE_ID = " + MEDICINE_ID;
        List<Map<String, String>> drugOutpUnitList = db.queryAllRow("查询药品门诊包装单位", sql);
        logger.assertFalse(drugOutpUnitList.size() < 1, "查询不到药品门诊包装单位 MEDICINE_ID:" + MEDICINE_ID);
        logger.assertFalse(drugOutpUnitList.size() > 1, "查询到多个药品门诊包装单位 MEDICINE_ID:" + MEDICINE_ID);
        return drugOutpUnitList.get(0);
    }


    //获取外配药列表
    public List<Map<String, String>> getExtProvisionMedicineList() {
        String sql = "SELECT * FROM WINDBA.MEDICINE_HOSPITAL_SETTINGS WHERE ALLOW_EXT_PROVISION_FLAG = 98175 AND IS_DEL=0";
        List<Map<String, String>> extProvisionListMedicineList = db.queryAllRow("查询外配药列表", sql);
        return extProvisionListMedicineList;
    }

    //获取自备药药品列表
    public List<Map<String, String>> getSelfProvisionMedicineList() {
        String sql = "select * from WINDBA.MEDICINE M left join WINDBA.MEDICINE_HOSPITAL_SETTINGS H ON M.MEDICINE_ID=H.MEDICINE_ID WHERE H.ALLOW_SELF_PROVIDED_FLAG=98175 AND h.IS_DEL=0";
        List<Map<String, String>> selfProvisionMedicineList = db.queryAllRow("查询自备药药品列表", sql);
        return selfProvisionMedicineList;
    }

    //根据orgid获取组织详情
    public Map<String, String> getOrgById(String org_id){
        String sql = "SELECT * FROM WINDBA.ORGANIZATION WHERE IS_DEL=0 AND ORG_ID="+org_id;
        List<Map<String, String>> orgList = db.queryAllRow("查询组织详情", sql);
        logger.assertFalse(orgList.size() < 1, "查询不到组织详情 org_id:" + org_id);
        logger.assertFalse(orgList.size() > 1, "查询到多条组织详情 org_id:" + org_id);
        return orgList.get(0);
    }

    //根据分类代码获取服务
    public List<Map<String, String>> getCsByCsCategoryId(String CS_CATEGORY_ID){
        String sql = "SELECT (SELECT VALUE_DESC FROM WINDBA.VALUE_SET WHERE VALUE_ID=?1) TYPE,CS_ID,CS_NAME FROM WINDBA.CLINICAL_SERVICE WHERE IS_DEL=0 AND CS_STATUS=98360 AND CS_CATEGORY_ID =?1".replace("?1", CS_CATEGORY_ID);
        List<Map<String, String>> csList = db.queryAllRow("查询临床服务列表", sql);
        return csList;
    }

    //根据ENCOUNTERId查找CONTACTId
    public String getCONTACTIdbyENCOUNTERId(String ENCOUNTERId){
        String sql = new WnSqlManager().getCONTACTIdbyENCOUNTERId(ENCOUNTERId);
        Map<String, String>  CONTACTInfo = db.queryFirstRow("根据ENCOUNTER_ID查找OUTPATIENT_CONTACT_ID", sql);
        if (!CONTACTInfo.isEmpty()) {
            return CONTACTInfo.get("OUTPATIENT_CONTACT_ID");
        }
        else {
            return "";
        }
    }

    //根据ENCOUNTERId查找PHARMACYID
    public String getPharmacyIdbyEncounterId(String encounterId){
        String sql = new WnSqlManager().getPharmacyIdbyEncounterId(encounterId);
        Map<String, String>  info = db.queryFirstRow("根据ENCOUNTERId查找PHARMACYID", sql);
        if (!info.isEmpty()) {
            return info.get("PHARMACY_ID");
        }
        else {
            return "";
        }
    }
 
    //通过患者姓名查找seqNo
    public Map<String,String> getSeqNoByPatientName(String patientName) {
        String sql = new WnSqlManager().getSeqNoByPatientName(patientName);
        Map<String,String> info = db.queryFirstRow("通过患者姓名查找seqNo", sql);
        return info;
    }
    
    //根据user_code查询employeeNo
    public Map<String, String> getEmployNoByUsercode(String user_code){
        String sql = new WnSqlManager().getEmployNoByUsercode(user_code);
        List<Map<String, String>> list = db.queryAllRow("查询员工编码", sql);
        logger.assertFalse(list.size() < 1, "查询不到员工编码:" + user_code);
        return list.get(0);
    }
    
    //根据user_code查询employeeId
    public Map<String, String> getEmployIdByUsercode(String user_code){
        String sql = new WnSqlManager().getEmployIdByUsercode(user_code);
        List<Map<String, String>> list = db.queryAllRow("查询员工编码", sql);
        logger.assertFalse(list.size() < 1, "查询不到员工编码:" + user_code);
        return list.get(0);
    }

    //根据住院病历模板查找病历目录分类
    public String getInpEmrClassName(String InpMrtName){
        String sql = new WnSqlManager().getInpEmrClassName(InpMrtName);
        Map<String, String>  InpMrtClassName = db.queryFirstRow("根据INP_MRT_NAME查找INP_EMR_CLASS_NAME", sql);
        if (!InpMrtClassName.isEmpty()) {
            return InpMrtClassName.get("INP_EMR_CLASS_NAME");
        }
        else {
            return "";
        }
    }

    //根据 paramNo查找ParamConfigId
    public String getParamConfigIdByParamNo(String paramNo) {
        String sql = new WnSqlManager().getParamConfigIdByParamNo(paramNo);
        String ParamConfigId = db.queryFirstRow("根据 paramNo查找ParamConfigId", sql).get("PARAM_CONFIG_ID");
        System.out.println(ParamConfigId);
        return ParamConfigId;
    }

    //根据住院病历阅改等级代码查询住院病历审阅流程标识ID
    public String getInpEmrReviewProcessId(String InpEmrReviewLevelCode){
        String sql = new WnSqlManager().getInpEmrReviewProcessId(InpEmrReviewLevelCode);
        Map<String, String>  InpEmrReviewProcessId = db.queryFirstRow("根据INP_EMR_REVIEW_LEVEL_CODE查找INP_EMR_REVIEW_PROCESS_ID", sql);
        if (!InpEmrReviewProcessId.isEmpty()) {
            return InpEmrReviewProcessId.get("INP_EMR_REVIEW_PROCESS_ID");
        }
        else {

            return "";
        }
    }

    //根据住院病历模板名称获取模板的监控类型编码
    public String getInpMrtMonitorNo(String inpMrtName){
        String sql = new WnSqlManager().getInpMrtMonitorNo(inpMrtName);
        Map<String, String>  value = db.queryFirstRow("根据INP_MRT_NAME查找INP_MRT_MONITOR_NO", sql);
        if (!value.isEmpty()) {
            return value.get("INP_MRT_MONITOR_NO");
        }
        else {
            logger.assertFalse(true,"病历模板不存在或模板配置有问题，请检查");
            return "";
        }
    }

    //查询住院病历三级阅改流程与病历监控类型关系是否存在
    public Boolean getInpEmrReviewMonitoring(String InpMrtMonitoringNo){
        String sql = new WnSqlManager().getInpEmrReviewMonitoring(InpMrtMonitoringNo);
        Map<String, String>  info = db.queryFirstRow("查询住院病历三级阅改流程与病历监控类型关系是否存在", sql);
        if (!info.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }


    //根据概念域id查找护理文书下拉框的选项值
    public String getChartDropdownValue(String chartConceptDomainId){
        String sql = new WnSqlManager().getChartDropdownValue(chartConceptDomainId);
        Map<String, String>  Value = db.queryFirstRow("根据Concept_Domain_Id查找VALUE_DESC", sql);
        if (!Value.isEmpty()) {
            return Value.get("VALUE_DESC");
        }
        else {
            return "";
        }
    }

    //根据护理文书模板名称获取护理文书模板ID、模板大类、模板小类
    public  Map<String, String> getChartTemplate(String chartTemplateName){
        String sql = new WnSqlManager().getChartTemplate(chartTemplateName);
        Map<String, String>  chartTemplateInfo = db.queryFirstRow("根据CHART_TEMPLATE_NAME查找CHART_TEMPLATE_ID、CHART_TEMPLATE_CLASS_CODE", sql);
        logger.assertFalse(chartTemplateInfo.size() < 1, "查询不到护理文书模板 :" + chartTemplateName);
        return chartTemplateInfo;
        }

    //根据患者姓名获取EnCounterId、BizRoleID、CURRENT_DEPT_ID、CURRENT_WARD_ID
    public Map<String, String> getEncounterInfoByFullName(String fullName){
        String sql = new WnSqlManager().getEncounterInfoByFullName(fullName);
        Map<String, String>  encounterInfo = db.queryFirstRow("根据CHART_TEMPLATE_NAME查找ENCOUNTER_ID、BIZ_ROLE_ID、CURRENT_DEPT_ID、CURRENT_WARD_ID", sql);
        logger.assertFalse(encounterInfo.size() < 1, "查询不到患者信息 :" + fullName);
        return encounterInfo;
    }

    //根据护理文书字段项ID获取字段的最大最小值
    public Map<String, String> getChartItemLimit(String chartItemId){
        String sql = new WnSqlManager().getChartItemLimit(chartItemId);
        Map<String, String>  ChartItemLimit = db.queryFirstRow("根据CHART_ITEM_ID查找CHART_ITEM_NUMBER_MIN_VAL、CHART_ITEM_NUMBER_MAX_VAL", sql);
        return ChartItemLimit;
    }

    //获取住院临床配置参数值
    public String getCliSettingValue(String cliSettingNo){
        String sql = new WnSqlManager().getCliSettingValue(cliSettingNo);
        Map<String, String>  CliSettingValue = db.queryFirstRow("根据CLI_SETTING_NO查询CLI_SETTING_VALUE", sql);
        if (CliSettingValue.get("CLI_SETTING_VALUE") != null) {
            return CliSettingValue.get("CLI_SETTING_VALUE");
        }
        else {
            return "";
        }
    }

    //更新住院临床配置参数值
    public void updateCliSettingValueByCliSettingNo(String CliSettingNo,String value) {
        System.out.println("-----" + CliSettingNo);
        String sql = new WnSqlManager().updateCliSettingValueByCliSettingNo(CliSettingNo,value);
        db.excute(sql);
    }


    //获取病区的空床位信息
    public List<Map<String,String>> getEmptyBedNoByOrgName() {
        List<Map<String, String>> bedNoList = new ArrayList<Map<String, String>>();
        String sql = new WnSqlManager().getEmptyBedNoByOrgName();
        bedNoList = db.queryAllRow(sql);
        return bedNoList;
    }

    public String getBedNo(){
        String sql = new WnSqlManager().getBedNo();
        Map<String, String> value = db.queryFirstRow("查询最大的床位号", sql);
        if (value.get("BED_NO") != null) {
            return value.get("BED_NO");
        } else {
            return "0";
        }
    }




    //查询住院就诊登记流水号zyh
    public String getInpatEncRegSeqNo(String EncounterId) {
        String sql = new WnSqlManager().getInpatEncRegSeqNo(EncounterId);
        Map<String, String> InpatEncRegSeqNo = db.queryFirstRow("60表：查找住院就诊登记流水号INPAT_ENC_REG_SEQ_NO", sql);
        if (!InpatEncRegSeqNo.isEmpty()) {
            return InpatEncRegSeqNo.get("INPAT_ENC_REG_SEQ_NO");
        } else {
            return "";
        }
    }
    //查询开始日期ksrq，停止日期jsrq，开立日期时间lrrq/rq/ysqrsj，医嘱天数ts
    public  Map<String, String> getTime(String EncounterId){
        String sql = new WnSqlManager().getTime(EncounterId);
        Map<String, String>  Time= db.queryFirstRow("60表：查找开始日期ksrq，停止日期jsrq，开立日期时间lrrq/rq/ysqrsj，医嘱天数START_AT,TERMINATED_AT,PRESCRIBED_AT,DAYS_OF_USE", sql);
        return Time;
    }
    //查询临床服务编码/药品编码bm，临床服务名称yzmc
    public  Map<String, String> getCs(String EncounterId){
        String sql = new WnSqlManager().getCs(EncounterId);
        Map<String, String>  Cs= db.queryFirstRow("60表：查找临床服务编码bm，临床服务名称CS_NO,CS_NAME", sql);
        return Cs;
    }
    //查询开立数量cs，医嘱项内容kdmc，医嘱项标识cfh_wn，嘱托yszt
    public  Map<String, String> getCliOrderItemId(String EncounterId){
        String sql = new WnSqlManager().getCliOrderItemId(EncounterId);
        Map<String, String>  CliOrderItemId= db.queryFirstRow("60表：查找开立数量cs，医嘱项内容kdmc，医嘱项标识cfh_wn，嘱托PRESCRIBED_QTY,CLI_ORDER_ITEM_CONTENT,CLI_ORDER_ITEM_ID,ADVICE", sql);
        return CliOrderItemId;
    }
    //查询药品 开立单位dw,商品名pm
    public  Map<String, String> getBasicPackUnitCode(String EncounterId){
        String sql = new WnSqlManager().getBasicPackUnitCode(EncounterId);
        Map<String, String>  BasicPackUnitCode= db.queryFirstRow("60表：查找开立单位dw,商品名BASIC_PACK_UNIT_CODE,COMMODITY_NAME_CHINESE", sql);
        return BasicPackUnitCode;
    }
    //查询开立医生ysbm/lrrbm
    public String getEmployeeNo(String EncounterId) {
        String sql = new WnSqlManager().getEmployeeNo(EncounterId);
        Map<String, String> EmployeeNo= db.queryFirstRow("60表：查找开立医生EMPLOYEE_NO", sql);
        if (!EmployeeNo.isEmpty()) {
            return EmployeeNo.get("EMPLOYEE_NO");
        } else {
            return "";
        }
    }
    //查询开立病区bq/bqks
    public String getOrgNo(String EncounterId) {
        String sql = new WnSqlManager().getOrgNo(EncounterId);
        Map<String, String> orgNo= db.queryFirstRow("60表：查找病区PRESCRIBING_WARD_ID", sql);
        if (!orgNo.isEmpty()) {
            return orgNo.get("ORG_NO");
        } else {
            return "";
        }
    }
    //查询开立科室
    public String getOrgNoKs(String EncounterId) {
        String sql = new WnSqlManager().getOrgNoKs(EncounterId);
        Map<String, String> orgNoKs= db.queryFirstRow("60表：查找科室PRESCRIBING_DEPT_ID", sql);
        if (!orgNoKs.isEmpty()) {
            return orgNoKs.get("ORG_NO");
        } else {
            return "";
        }
    }
    //查询执行科室
    public String getOrgNoZx(String EncounterId) {
        String sql = new WnSqlManager().getOrgNoZx(EncounterId);
        Map<String, String> orgNoKs= db.queryFirstRow("60表：查找科室EXEC_DEPT_ID", sql);
        if (!orgNoKs.isEmpty()) {
            return orgNoKs.get("ORG_NO");
        } else {
            return "";
        }
    }
    //查询频次代码/用法用量yfyl
    public String geValueDesc(String EncounterId) {
        String sql = new WnSqlManager().geValueDesc(EncounterId);
        Map<String, String> ValueDesc= db.queryFirstRow("60表：查找频次代码VALUE_DESC", sql);
        if (!ValueDesc.isEmpty()) {
            return ValueDesc.get("VALUE_DESC");
        } else {
            return "";
        }
    }
    //查询单位dw
    public String geValueDescDw(String EncounterId) {
        String sql = new WnSqlManager().geValueDescDw(EncounterId);
        Map<String, String> ValueDescDw= db.queryFirstRow("60表：查找医嘱类型VALUE_DESC", sql);
        if (!ValueDescDw.isEmpty()) {
            return ValueDescDw.get("VALUE_DESC");
        } else {
            return "";
        }
    }
    //查询医嘱类型lx
    public String geValueNo(String EncounterId) {
        String sql = new WnSqlManager().geValueNo(EncounterId);
        Map<String, String> valueNo= db.queryFirstRow("60表：查找医嘱类型VALUE_NO", sql);
        if (!valueNo.isEmpty()) {
            return valueNo.get("VALUE_NO");
        } else {
            return "";
        }
    }

    //查询包装规格 hldw,yfyl
    public String getPackSpec(String EncounterId) {
        String sql = new WnSqlManager().getPackSpec(EncounterId);
        Map<String, String> PackSpec= db.queryFirstRow("60表：查找包装规格PACK_SPEC", sql);
        if (!PackSpec.isEmpty()) {
            return PackSpec.get("PACK_SPEC");
        } else {
            return "";
        }
    }
    //查询药品 零售价jg1
    public String getPetailPrice(String EncounterId) {
        String sql = new WnSqlManager().getPetailPrice(EncounterId);
        Map<String, String> PetailPrice= db.queryFirstRow("60表：查找药品零售价RETAIL_PRICE", sql);
        if (!PetailPrice.isEmpty()) {
            return PetailPrice.get("RETAIL_PRICE");
        } else {
            return "";
        }
    }
    //查询收费项目 零售价jg1，名称，编码
    public Map<String, String> getPetailPriceXm(String EncounterId) {
        String sql = new WnSqlManager().getPetailPriceXm(EncounterId);
        Map<String, String> PetailPriceXm= db.queryFirstRow("60表：查找收费项目售价UNIT_PRICE，名称ITEM.CHARGING_ITEM_NAME，编码CHARGING_ITEM_NO", sql);
        return PetailPriceXm;
    }
    //查询姓名xm
    public String getFullName(String EncounterId) {
        String sql = new WnSqlManager().getFullName(EncounterId);
        Map<String, String> FullName= db.queryFirstRow("60表：查找姓名FULL_NAME", sql);
        if (!FullName.isEmpty()) {
            return FullName.get("FULL_NAME");
        } else {
            return "";
        }
    }
    //查询草药 给药途径yf，制出总量zl，剂量(每顿剂量)cl，服药周期付数jr
    public  Map<String, String> getHerbOrder(String EncounterId){
        String sql = new WnSqlManager().getHerbOrder(EncounterId);
        Map<String, String>  HerbOrder= db.queryFirstRow("60表：查找给药途径yf，制出总量zl，剂量(每顿剂量)cl，服药周期付数DOSAGE_ROUTE_CODE,PROCESSED_AMOUNT,DOSE,DRUG_USAGE_FREQ_QUANTITY", sql);
        return HerbOrder;
    }
    //查询护士签收人编码qrhs，时间hsqrsj
    public  Map<String, String> getHandledBy(String EncounterId){
        String sql = new WnSqlManager().getHandledBy(EncounterId);
        Map<String, String>  HandledBy= db.queryFirstRow("60表：查找签收人编码qrhs，时间HANDLED_BY,HANDLED_AT", sql);
        return HandledBy;
    }
    //查询住院发药申请标识fysqbs_wn
    public String getInpmeddispreqid(String EncounterId) {
        String sql = new WnSqlManager().getInpmeddispreqid(EncounterId);
        Map<String, String> Inpmeddispreqid= db.queryFirstRow("60表：查找住院发药申请标识INP_MED_DISP_REQ_ID", sql);
        if (!Inpmeddispreqid.isEmpty()) {
            return Inpmeddispreqid.get("INP_MED_DISP_REQ_ID");
        } else {
            return "";
        }
    }
    //查询住院发药计划标识fyjhbs_wn，住院发药剂量HL，住院发药数量SL
    public  Map<String, String> getInpmeddispplanid(String EncounterId){
        String sql = new WnSqlManager().getInpmeddispplanid(EncounterId);
        Map<String, String>  Inpmeddispplanid= db.queryFirstRow("60表：查找住院发药计划标识fyjhbs_wn，住院发药剂量HL，住院发药数量INP_MED_DISP_PLAN_ID,INP_MED_DISP_DOSE,INP_MED_DISP_QTY", sql);
        return Inpmeddispplanid;
    }
    //查询申请人zxrbm
    public String getOperatedBy(String EncounterId) {
        String sql = new WnSqlManager().getOperatedBy(EncounterId);
        Map<String, String> operatedBy= db.queryFirstRow("60表：查找申请人OPERATED_BY", sql);
        if (!operatedBy.isEmpty()) {
            return operatedBy.get("EMPLOYEE_NO");
        } else {
            return "";
        }
    }
    //查询国籍
    public String getInpatientInfo(String EncounterId) {
        String sql = new WnSqlManager().getInpatientInfo(EncounterId);
        Map<String, String> inpatientInfo= db.queryFirstRow("60表：查找国籍", sql);
        if (!inpatientInfo.isEmpty()) {
            return inpatientInfo.get("VALUE_NO");
        } else {
            return "";
        }
    }
    //查询性别
    public String getInpatientGender(String EncounterId) {
        String sql = new WnSqlManager().getInpatientGender(EncounterId);
        Map<String, String> inpatientGender= db.queryFirstRow("60表：查找性别", sql);
        if (!inpatientGender.isEmpty()) {
            return inpatientGender.get("VALUE_NO");
        } else {
            return "";
        }
    }
    //查询名族
    public String getInpatientNation(String EncounterId) {
        String sql = new WnSqlManager().getInpatientNation(EncounterId);
        Map<String, String> inpatientNation= db.queryFirstRow("60表：查找名族", sql);
        if (!inpatientNation.isEmpty()) {
            return inpatientNation.get("VALUE_NO");
        } else {
            return "";
        }
    }
    //查询入区途径
    public String getInpatientAdmitted(String EncounterId) {
        String sql = new WnSqlManager().getInpatientAdmitted(EncounterId);
        Map<String, String> inpatientAdmitted= db.queryFirstRow("60表：查找入区途径", sql);
        if (!inpatientAdmitted.isEmpty()) {
            return inpatientAdmitted.get("VALUE_NO");
        } else {
            return "";
        }
    }
   //查询身份证号码，病案号，就诊登记流水号
    public  Map<String, String> getInpatientRecord(String EncounterId){
        String sql = new WnSqlManager().getInpatientRecord(EncounterId);
        Map<String, String>  InpatientRecord= db.queryFirstRow("60表：查找身份证号码，病案号，就诊登记流水号IDCARD_NO,CASE_NO,IMRN", sql);
        return InpatientRecord;
    }
    //查询床位
    public String getInpatientBed(String EncounterId) {
        String sql = new WnSqlManager().getInpatientBed(EncounterId);
        Map<String, String> inpatientBed= db.queryFirstRow("60表：查找床位BED_NO", sql);
        if (!inpatientBed.isEmpty()) {
            return inpatientBed.get("BED_NO");
        } else {
            return "";
        }
    }
    //查询住院科室
    public String getInpatientRoom(String EncounterId) {
        String sql = new WnSqlManager().getInpatientRoom(EncounterId);
        Map<String, String> InpatientRoom= db.queryFirstRow("60表：查找住院科室CURRENT_DEPT_ID", sql);
        if (!InpatientRoom.isEmpty()) {
            return InpatientRoom.get("ORG_NO");
        } else {
            return "";
        }
    }
    //查询住院病区
    public String getInpatientWard(String EncounterId) {
        String sql = new WnSqlManager().getInpatientWard(EncounterId);
        Map<String, String> InpatientWard= db.queryFirstRow("60表：查找住院病区CURRENT_WARD_ID", sql);
        if (!InpatientWard.isEmpty()) {
            return InpatientWard.get("ORG_NO");
        } else {
            return "";
        }
    }

    /**
     *根据EmployeeNo查询EmployeeID
     */
    public String getEmployeeIdByEmployeeNo(String Employee_No) {
        String sql = new WnSqlManager().getEmployeeIdByEmployeeNo(Employee_No);
        Map<String, String> value= db.queryFirstRow("根据EmployeeNo查询EmployeeID", sql);
        if (!value.isEmpty()) {
            return value.get("EMPLOYEE_ID");
        } else {
            return "";
        }
    }

    /**
     *根据组织名称和组织code获取组织ID
     */
    public String getOrgIdByOrgName(String orgName,String orgNo) {
        String sql = new WnSqlManager().getOrgIdByOrgName(orgName,orgNo);
        Map<String, String> value= db.queryFirstRow("根据orgName和OrgNo获取orgId", sql);
        if (!value.isEmpty()) {
            return value.get("ORG_ID");
        } else {
            return "";
        }
    }

    /**
     *根据病房编码和病区名查询病房ID
     */
    public String getInpatRoomIdByRoomName(String InpatRoomNo,String orgName,String orgNo){
        String sql = new WnSqlManager().getInpatRoomIdByRoomName(InpatRoomNo,orgName,orgNo);
        Map<String, String> value= db.queryFirstRow("根据InpatRoomNo、orgName、orgNo获取inpatRoomId", sql);
        if (!value.isEmpty()) {
            return value.get("INPAT_ROOM_ID");
        } else {
            return "";
        }

    }

}