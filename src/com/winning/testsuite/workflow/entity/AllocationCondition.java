package com.winning.testsuite.workflow.entity;

import java.util.List;

import ui.sdk.config.Data;

/**分方规则条件*/
public class AllocationCondition{
	/**条件优先级*/
	public Integer priority = null;
	/**条件类型代码*/
	public Data.ShortName shortName = null;
	/**条件表达式*/
	public Data.OperationCode OperationCode = null;
	/**条件值列表*/
	public List<String> valueList = null; 
	
	/**构造函数*/
	public AllocationCondition(Integer priority,Data.ShortName shortName,Data.OperationCode OperationCode,List<String> valueList) {
		this.priority = priority;
		this.shortName = shortName;
		this.OperationCode = OperationCode;
		this.valueList = valueList;
	}
	
	
}
