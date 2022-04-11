package com.winning.testsuite.workflow.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**保存开立详细内容*/
public class PrescribeDetail{
	/**开立剂量(支持: 西成药)*/
	public Integer dose=null;
	
	/**开立数量(支持: 西成药/中草药/治疗)*/
	public Integer num=null;

	/**院内次数，参数CL083为1时有效，仅支持西药***/
	public Integer InHospitalNum=null;

	/***开立的天数**/
	public Integer Days=null;

	/**给药途径(支持: 西成药/中草药)*/
	public String route=null;

	/**频次 (支持: 西成药/中草药)*/
	public String freq=null;

	/**药房 (支持: 西成药/中草药)*/
	public String pharmacy = null;

	/**特殊说明 (支持: 西成药)*/
	public String specialDesc = null;

	/**用法说明 (支持: 西成药)*/
	public String usageDesc = null;

	/**外配药勾选 (支持: 西成药)*/
	public Boolean extProvision = null;

	/**嘱托 (支持: 西成药)*/
	public String entrust = null;

	/**成组中草药(支持: 中草药)*/
	public List<Map<String,String>> groupHerb = null ;
	
	/**成组药品名称(支持: 西药)*/
	public List<String> groupMedicineName =null;

	/**自费勾选框 (支持: 西成药/中草药/检查/检验/病理/治疗)*/
	public Boolean selfPay = null;

	/**慢病勾选框 (支持: 西成药/中草药/检查/检验/病理/治疗)*/
	public Boolean chronicDisease = null;

	/**特殊病勾选框 (支持: 西成药/中草药/检查/检验/病理/治疗)*/
	public Boolean specialDisease = null;

	/**代煎勾选框 (支持: 中草药)*/
	public Boolean decoct = null;

	/**代煎方式 (支持: 中草药)*/
	public String decoctMethod = null;

	/**临床摘要 (支持: 检查/病理)*/
	public String clinicalSummary = "临床摘要";

	/**是否点击新增按钮(支持: 病理)*/
	public Boolean addButton = true;

	/**新增部位序号(支持: 病理)*/
	public Integer positionIndex = 1;

	/**新增标本序号(支持: 病理)*/
	public Integer sampleIndex = 1;

	/**体征(支持: 检查)*/
	public String bodySign = null;

	/**自备药勾选*/
	public Boolean self = null;

}