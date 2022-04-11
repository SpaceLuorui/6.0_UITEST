package com.winning.testsuite.workflow.Inpatient;

public class WnInpatientXpath {
    //portal页面
    public static String portalInpatient = "//span[@class='application-name']";
    public static String portalInpatientNurseStationEntrance = "//main[@class=\"el-main\"]//li/p[.=\"住院护士站\"]";
    public static String portalInpatientResidentStationEntrance = "//main[@class=\"el-main\"]//li/p[.=\"住院医生站\"]";
    //右上角切换科室、退出登录标识
    public static String InpatientUserInfoDropdown = "//div[@class='user-info el-dropdown']//div[@class='win-patient-avatar']";
    public static String InpatientLogout = "//li[@class='el-dropdown-menu__item']//span[text()='退出登录']";
    //病区列表框
    public static String inpatientWardChooseBox = "//div[@class='el-dialog select-subject-class']";
    //住院护士站病区列表中所有选择框
    public static String inpatientNurseWardChooseBoxCheckBox = inpatientWardChooseBox+"//label[contains(@class,'el-radio')]";
    //住院医生站病区列表中所有选择框
    public static String inpatientDoctorWardChooseBoxCheckBox = inpatientWardChooseBox+"//label[contains(@class,'el-checkbox')]";
    //checkbox中被选中的选项
    public static String inpatientCheckboxIsChecked = inpatientWardChooseBox + "//span[@class='el-checkbox__input is-checked']";
    //床位卡打印弹框，取消按钮
    public static String cancelButton = "//div[@class='invice-select config-print']//button//span[.='取消']";
    //住院医生站床位卡页签
    public static String BedCardPage= "//div[@class='nav-item nav-item-type1']//span[text()='床位卡']";
    //住院医生站患者搜索框
    public static String inpatientPatientSearchInput = "//input[@placeholder='床号/住院号/姓名']";
    //住院医生站菜单,?可替换成住院病等菜单名
    public static String inpatientMenu = "//div[@class='child-menu']/span[contains(text(),'?')]";
    //住院护士站大菜单
    public static String NurseMenu="//span[@class='menuName unelli']/span[contains(text(),'?')]";
    //住院护士站子菜单
    public static String NurseChildMenu="//div[@class='child-menu']/span[contains(text(),'?')]";
    //住院护士站待入区
    public static String NurseWaitingEntry="//div[@class='inpat-beadcard-top']//span[@class='plan-btn']//span[contains(text(),'待入区')]";
    //住院医生站医嘱搜索输入栏
    public static String inpatientSearchOrderInput = "//div[@class='disposal__search']//input[@placeholder='请输入处置内容']";
    //医嘱搜索结果栏
    public static String inpatientSearchOrderResultBox = "//div[@class='disposal__search--input--res disposal__search--input--sameLayer']";
    //医嘱搜索等待动画
    public static String inpatientSearchOrderLoadingBox = "//div[contains(@class,'loading_check')]";
    //处置加工厂框
    public static String inpatientDisposalFactory = "//div[@class='factory_wrap']";
    //处置加工厂加载框
    public static String inpatientLoadingDisposalFactory = "//div[@class='loading_wrap']";
    //搜索医嘱的所有搜索结果
    public static String inpatientSearchOrderAllResultList = "//div[contains(@class,'el-table__body-wrapper')]//li[contains(@class,'search_item_name')]//div[contains(@class,'search-name')]";
    //医嘱类型：长期
    public static String inpatientDisposalFactoryOrderTypeRadioLongTerm = "//span[@class='el-radio__label' and contains(.,'长期')]";
    //医嘱类型：临时
    public static String inpatientDisposalFactoryOrderTypeRadioTemporary = "//span[@class='el-radio__label' and contains(.,'临时')]";

    //草药加工厂标识
    public static String inpatientDisposalFactoryHerbFlag = "//div[contains(@class,'herbalDrugFac')]";
    //西药加工厂标识
    public static String inpatientDisposalFactoryDrugFlag = "//div[contains(@class,'clinical-west-drug-card')]";
    //检验加工厂标识
    public static String inpatientDisposalFactoryLabFlag = "//div[@ordertype='256197']";
    //检查加工厂标识
    public static String inpatientDisposalFactoryExamFlag = "//div[@ordertype='256198']";
    //治疗加工厂标识
    public static String inpatientDisposalFactoryTreatFlag = "//div[@ordertype='256199']";
    //用血加工厂标识
    public static String inpatientDisposalFactoryBloodFlag= "//div[@ordertype='384031']";
    //手术加工厂标识
    public static String inpatientDisposalFactoryOperationFlag= "//div[@ordertype='256200']";
    //病理加工厂标识
    public static String inpatientDisposalFactoryPathologyFlag = "//div[@ordertype='384030']";
    //护理加工厂标识
    public static String inpatientDisposalFactoryNursingFlag= "//div[@ordertype='384033']";
    //膳食加工厂标识
    public static String inpatientDisposalFactoryDietFlag= "//div[@ordertype='384032']";
    //嘱托
    public static String inpatientDisposalFactoryEntrustFlag= "//div[@ordertype='384035']";
    //卫材
    public static String inpatientDisposalFactoryMaterialFlag= "//div[@ordertype='384034']";

    //西药数量输入框
    public static String inpatientDisposalFactoryDrugNumInput = "//div[@class='factory_wrap']//ul[contains(@class,'drug-list')]//div[contains(@class,'dosage-group')]//div[@class='el-input el-input--mini']//input";
    //西药加工厂药品名称
    public static String inpatientDisposalFactoryDrugName = "//div[@class='factory_wrap']//div[@class='overWidthTxt']";
    //西药给药途径下拉框
    public static String inpatientDisposalFactoryRouteInput = "//div[@class='drug-li-rigth']/div[2]//input";
    //西药频次下拉框
    public static String inpatientDisposalFactoryDrugFreqInput = "//div[@class='drug-li-rigth']/div[3]//input";

    //草药剂量填写框
    public static String inpatientDisposalFactoryHerbNumInput = "//div[@class='herbal-drug-edit-box']//form[@class='el-form checked-drug-form el-form--inline']//div[@class='el-input el-input--mini']//input";
    //草药剂量单位下拉框
    public static String inpatientDisposalFactoryHerbDosageUnit ="//div[@class='herbal-drug-edit-box']//form[@class='el-form checked-drug-form el-form--inline']//div[@class='el-select el-select--mini']//input[1]";
    //草药给药途径下拉框
    public static String inpatientDisposalFactoryHerbDecocteMethodDropbox = "//label[contains(text(),'给药途径')]/following-sibling::*//input";
    //草药频次下拉框
    public static String inpatientDisposalFactoryHerbFreqCodeDropbox = "//label[contains(text(),'频次')]/following-sibling::*//input";
    //草药剂量保存按钮
    public static String inpatientDisposalFactoryHerbNumSave = "//div[@class='herbal-drug-edit-box']//button/span[contains(.,'保存')]/parent::*";
    //每次剂量填写框
    public static String inpatientDisposalFactoryOnceDoseInput = "//label[contains(text(),'每次剂量')]/following-sibling::*//input";
    //每剂煎出总量填写框
    public static String inpatientDisposalFactoryProcessedAmountInput ="//label[contains(text(),'每剂煎出总量')]/following-sibling::*//input" ;
    //天数增加按钮
    public static String inpatientDisposalFactoryDaysInput = "//span[text()='天']/preceding-sibling::*//input";
    //剂数增加按钮
    public static String inpatientDisposalFactoryDoseInput = "//span[text()='天']/following-sibling::*//input";


    //下拉框第一个
    public static String  operationFirst = "//div[@class='el-scrollbar']//li";
    //手术等级
    public static String  operationGrade = "//tr[@class='el-table__row']//td[2]";
    //手术部位
    public static String  operationPlace = "//tr[@class='el-table__row active-row']//td[3]//input";
    //手术体位
    public static String  operationPosition = "//tr[@class='el-table__row active-row']//td[4]//input";
    //麻醉方式
    public static String  operationAnaesthesia = "//tr[@class='el-table__row active-row']//td[5]//input";
    //切口等级
    public static String  operationIncision = "//form[@class='el-form el-form--inline']/div[4]//input";
    //手术分类
    public static String  operationClassification= "//form[@class='el-form el-form--inline']/div[5]//input";
    //手术人员
    public static String  operationPersonnel= "//form[@class='el-form el-form--inline']/div[9]//div[@class='diagnosis-show el-popover__reference']";
    //手术人员角色
    public static String  operationRole= "//div[@class='el-input el-input--suffix']//input[@placeholder='请选择角色']";
    //主刀医师
    public static String  operationPhysician= "//div[@class='el-scrollbar']//li//span[contains(text(),'主刀医师')]";
    //手术人员姓名
    public static String  operationName= "//div[@class='el-input el-input--mini el-input--suffix']//input[@placeholder='请选择姓名']";
    //手术室
    public static String  operationRoom= "//form[@class='el-form el-form--inline']/div[10]//input";


    //治疗医嘱数量
    public static String inpatientTreatmentQuantity = "//div[@class='order-card-detail']//ul[@class='cs-name-Qty-unit']//div[contains(@class,'is-controls-right')]//div[@class='el-input el-input--mini']//input";
    //护理医嘱属性
    public static String inpatientRemarks ="//div[@class='el-textarea el-input--mini']//textarea[@placeholder='请输入内容']";
            //"//div[@class='el-input el-input--mini el-input--suffix']//input";
    //检验标本下拉框
    public static String inpatientDisposalFactoryLabSpecimenDropbox = "//label[contains(text(),'标本')]/following-sibling::*//input[@placeholder='请选择']";

    //住院医生站检查临床摘要填写框
    public static String inpatientDisposalFactoryExamSummaryInput = "//label[contains(.,'临床摘要')]/following-sibling::*//textarea";
    //处置加工厂的所有复选框 包括人体图
    public static String inpatientDisposalFactoryCheckBoxes = "//div[@class='factory_wrap']//div[contains(@class,'all-exam-classify') or contains(@class,'item-list') or contains(@class,'more-zb')]//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]";
    //处置加工厂确认按钮
    public static String inpatientDisposalFactoryCommitButton = "//button[@class='el-button el-button--primary el-button--small']//span[contains(text(),'确认')]";
    //处置加工厂取消按钮
    public static String inpatientDisposalFactoryCancelButton = "//button[contains(@class,'el-button btnCancelColor')]//span[contains(text(),'取消')]";
    //签署按钮
    public static String inpatientOrderSignButton = "//div[contains(@class,'disposal')]//span[contains(text(),'签署')]";
    //不可点击的签署按钮
    public static String inpatientOrderSignButtonDisabled = "//div[contains(@class,'disposal')]//button[@disabled]//span[contains(text(),'签署')]";

    //页面加载中标识
    public static String loading ="//div[@class='el-loading-mask']";
    //电子申请单开立页面的按钮，?可替换成：检查、检验、治疗、病理、用血
    public static String applyFormButton = "//div[@class='apply-form-wrap__title__tabs']//span[contains(text(),'?')]";
    //申请单搜索结果第一个选项
    public static String applyFormFirstItem = "//li[contains(@class,'el-menu-item')][1]";
    //项目第一个选项框
    public static String examItemCheckBox = "//div[contains(@class,'table-wrap el-row')]//td//span[contains(@class,'el-checkbox__input')][1]";
    //确定按钮
    public static String orderConfirmButton = "//div[contains(@class,'apply-form-details-wrap')]//span[contains(text(),'确定')]";
    //医嘱页面全选框
    public static String orderCheckBox ="//div[@class='order-list-content']//div[@class='el-table__header-wrapper']//span[contains(@class,'el-checkbox__input')]";
    //医嘱页面按钮：复制、另存为模板、停止、作废、打印、刷新
    public static String addTemplateButton ="//div[@class='order-advice-wrapper']//button/span[contains(text(),'?')]" ;
    //另存为模板对话框
    public static String dialogSaveAsTemplate ="//div[@class='el-dialog dialog-saveAs-template']";
    //处置模板名称输入框
    public static String dialogSaveAsTemplateInput ="//div[@class='el-dialog__body']//input[@placeholder='请输入新建的模板名称']" ;
    //处置模板保存的所属范围
    public static String templateAppScopeType= "//label[@for='templateAppScopeTypeCode']/..//span[contains(text(),'?')]";
    //添加诊断
    public static String addDiagnostic = "//div[@class='tagWapper']//button";
    //诊断类型选项框
    public static String DiagnosticTypeSelect ="//div[@class='addWrapper is-Adding']//input[@placeholder='请选择诊断类型']" ;
    //诊断类型
    public static String DiagnosticType ="//li/span[.='西医诊断']";
    //诊断搜索框
    public static String DiagnosticSearchInput ="//div[@class='addWrapper is-Adding']//input[@placeholder='搜索可选诊断']";
    //诊断复选框
    public static String DiagnosticCheckBox = "//div[@class='addWrapper is-Adding']//span[@class='el-checkbox__input']";
    //模板另存确认按钮
    public static String dialogSaveAsTemplateConfirm= "//div[@class='el-dialog__footer']//span[contains(.,'确定')]";
    //保存成功提示
    public static String saveSucMsg ="//p[text()='保存成功']";
    //医嘱开立页面按钮，?可替换成：医嘱模板、申请单、剪切板、历史医嘱、临床路径、出院带药
    public static String orderButton = "//div[@class='disposal-btns']//span[contains(.,'?')]";
    //医嘱模板页面标识
    public static String orderTemplatePage= "//div[@class='order-template-warp']";
    //医嘱模板搜索框
    public static String orderTemplateSearchInput = "//div[@class='order-template-warp']//input[@placeholder='请输入模板名称']";
    //医嘱模板类型单选控件
    public static String orderTemplateTypeRadio ="//span[@class='el-radio__label' and contains(text(),'?')]";
    //医嘱模板搜索结果
    public static String orderTemplate= "//ul[@class='template-list']//li";
    //医嘱模板页面开立按钮
    public static String orderQuoteButton = "//p[@class='order-quote']/button";
    //医嘱模板更多操作
    public static String orderTemplateIconMore = "//ul[@class='template-list']//li[@title='?']//i[contains(@class,'win-icon-more more')]";
    //医嘱模板更多操作
    public static String orderTemplateDeleteButton = "//div[@aria-hidden='false']//i[@class='win-icon-delete delete']";
    //医嘱模板删除提示框
    public static String orderTemplateDeleteWarn = "//div[@class='el-message-box orderSearch-factory-messagebox warn']";
    //医嘱模板删除确定按钮
    public static String orderTemplateConfirmButton = "//div[@class='el-message-box orderSearch-factory-messagebox warn']//span[contains(.,'确定')]";
    //医嘱收藏图标
    public static String orderCollectIcon ="//div[contains(@class,'searchListWrapper')]//div[@class='el-table__fixed-right']//td[4]//i" ;
    //医嘱搜索下拉图标
    public static String orderSearchIcon = "//div[@class='disposal__search--select--arrow']";
    //医嘱搜索结果
    public static String orderSearchResult ="//div[@title='?']";
    //处置加工厂-(西成药、草药等)用参数：?代替
    public static String inpatientDisposalFactoryButton = "//div[@class='disposal__search--select--list--item--text'  and contains(.,'?')]";
    //医嘱收藏夹中的医嘱
    public static String collectedOrder = "//ul[@class='serve_type_list']//span[contains(text(),'?')]";
    //确认按钮
    public static String inpatientOrderConfirmButton = "//div[@class='collectBtn']//span[contains(text(),'确认')]";
    //操作成功提示
    public static String sucMsg= "//div[@class='el-message el-message--success']//p[.='?']";

    /**
     * 停止医嘱
     */
    //停止医嘱标签（红点）
    public static String stopOrderIcon  ="//tr[contains(@class,'el-table__row') and contains(.,'?')]//i[contains(@class,'win-icon-stop')]";
    //停止医嘱-选择日期
    public static String stopOrderSelectDate = "//div[@class='el-dialog stop-order-col']//input[@placeholder='选择日期时间']";
    //停止医嘱-选择日期-此刻
    public static String stopOrderSelectNow = "//div[contains(@class,'has-time')]//button//span[contains(text(),'此刻')]";
    //停止医嘱-选择日期-确定
    public static String stopOrderConfirm = "//div[@class='el-dialog stop-order-col']//button//span[contains(text(),'确 定')]";
    //医嘱批量停止按钮
    public static String batchStopOrderButton  = "//div[@class='handle-part']//button//span[contains(text(),'停止')]";
    //没有可以停止的医嘱弹框
    public static String batchStopOrderWarning  = "//div[contains(@class,'el-message--warning')]//p[contains(text(),'没有可以停止的医嘱')]";
    //医嘱批量停止-确认按钮
    public static String batchStopOrderConfirm  = "//div[@class='el-dialog dialog-stop-order']//button//span[contains(text(),'确 定')]";
    //医嘱批量停止-弹出框确认按钮
    public static String batchStopOrderFrameConfirm  = "//div[@class='el-message-box__wrapper']//button//span[contains(text(),'确定')  ]";

    /**
     * 作废医嘱
     */
    //勾选医嘱
    public static String tickOrder  = "//tr[contains(@class,'el-table__row')  and contains(.,'?')]//span[@class='el-checkbox__inner']";
    //勾选全部
    public static String tickOrderBatch  ="//div[@class='el-table__header-wrapper']//span[@class='el-checkbox__inner']";
    //作废
    public static String cancelOrder  = "//button[contains(@class,'el-button')]//span[contains(text(),'作废')]";
    //确定
    public static String cancelOrderConfirm  = "//div[@class='el-message-box__wrapper']//button[contains(@class,'el-button')]//span[contains(text(),'确定')]";
    //作废失败-确定
    public static String cancelOrderFailConfirm  = "//div[@class='el-dialog dialog-stop-order']//span[contains(text(),'确 定')]";
    //作废医嘱提示信息
    public static String getNullifyOrderFailMsg ="//p[text()='医嘱已审核，不允许作废医嘱']";
    //提示信息
    public static String stopOrderSucMsg ="//p[text()='停止医嘱成功']";
    //作废失败
    public static String nullifyOrderFail ="//p[text()='作废医嘱失败']";

    /**
     * 医嘱疑问
     */
    //疑问
    public static String doubtOrder ="//tr[contains(@class,'el-table__row')  and contains(.,'?')]//span[contains(text(),'疑 问')]";
    //疑问原因
    public static String doubtOrderReason ="//div[@class='el-dialog__wrapper']//textarea[@placeholder='请输入其他原因']";
    //确定
    public static String doubtOrderConfirm ="//div[@class='el-dialog__wrapper']//span[contains(text(),'确 定')]";
    //提示信息
    public static String doubtOrderSucMsg ="//p[text()='拒签成功']";

    /**
     * 医嘱二次确认
     */
    //二次确认按钮
    public static String reconfirmOrder ="//tr[contains(@class,'el-table__row') and contains(.,'?')]//i[@class='win-icon-erciqueren ']";
    //二次确认描述
    public static String reconfirmOrderDescribe ="//div[@class='el-dialog__wrapper']//textarea[@placeholder='请输入内容']";
    //确认
    public static String reconfirmOrderConfirm ="//div[@class='el-dialog__wrapper']//span[contains(text(),'确定')]";
    //提示信息
    public static String reconfirmOrderSucMsg ="//p[text()='保存成功']";

    //撤回医嘱
    public static String recallOrder ="//tr[contains(@class,'el-table__row') and contains(.,'?')]//i[@class='el-tooltip win-icon-recall']";
    //提示信息
    public static String recallOrderSucMsg ="//p[text()='撤销医嘱成功']";
    //提示信息
    public static String recallOrderFailMsg ="//p[text()='医嘱护士已签收，不允许撤销，请作废/停止医嘱。']";

    /**
     * 住院护士站-医嘱单
     */
    //全部按钮
    public static String selectAll  = "//button//span[contains(text(),'全部医嘱')]";
    //医嘱状态下拉框
    public static String orderStatusDropDown  = "//div[contains(@class,'orderSelect')]//input[@placeholder='请选择']";
    //医嘱状态下拉框-全部
    public static String orderStatusDropDownSelectAll  = "//div[contains(@class,'el-select-dropdown')]//li//span[contains(text(),'全部')]";
    //医嘱状态
    public static String orderStatus  = "//tr[contains(@class,'el-table__row')]//span[contains(text(),'已完成')]";

    //全选按钮
    public static String allSelectedButton="//div[@class='nurse-task-header-conditions__btns']//span[contains(text(),'全选')]";
    //批量签收按钮
    public static String orderSignButton="//div[@class='nurse-task-header-conditions__btns']//span[contains(text(),'签收')]";
    //批量申请按钮
    public static String orderRequestButton="//div[@class='nurse-task-header-conditions__btns']//span[contains(text(),'执行申请')]";
    //批量执行按钮
    public static String orderExecuteButton="//div[@class='nurse-task-header-conditions__btns']//span[contains(text(),'执行')]";
    //医嘱执行页面，执行状态：已申请
    public static String orderExecuteStatus = "//div[@class='el-form-item el-form-item--small']//span[text()='待执行']";
    //医嘱撤销申请
    public static String orderRevoke="//tr[contains(@class,'el-table__row') and contains(.,'\"+name+\"')]//span[contains(text(),'撤销申请')]";
    //医嘱撤销原因
    public static String orderRevokeReason="//div[@class='el-dialog']//div[@class='el-textarea el-input--small']//textarea[contains(@placeholder,'请输入撤销原因')]";
    //医嘱撤销原因-确定
    public static String orderRevokeReasonConfirm="//div[@class='el-dialog']//button//span[contains(text(),'确 定')]";
    //医嘱签收成功提示
    public static String signSucMsg ="//p[text()='医嘱核对成功！']";
    //医嘱操作成功提示
    public static String OperateSucMsg ="//p[contains(text(),'成功')]";
    //医嘱执行页面已申请按钮
    public static String appliedRadio = "//div[@class='el-radio-group']//span[text()='已申请']";
    //护士核对弹框
    public static String nurseCheckDialog = "//div[@aria-label='核对护士']";
    //护士核对账号输入框
    public static String nurseCheckAccount = "//div[@aria-label='核对护士']//input[@placeholder='请输入账号']";
    //护士核对密码输入框
    public static String nurseCheckPwd = "//div[@aria-label='核对护士']//input[@placeholder='请输入密码']";
    //护士核对确定按钮
    public static String nurseCheckConfirmButton = "//div[@aria-label='核对护士']//button//span[text()='确 定']";

    //历史医嘱页面标识
    public static String historicalOrderPage= "//div[@class='clinical-historicalOrders-warp']";
    //历史医嘱页面住院医嘱按钮
    public static String historicalOrderInpatientButton = "//div[@class='clinical-historicalOrders-warp']//p[contains(.,'住院医嘱')]";
    //历史医嘱记录
    public static String historicalOrderRecord= "//div[@class='clinical-historicalOrders-warp']//div[contains(@class,'el-table__body-wrapper')]//tr";
    //历史医嘱的复选框
    public static String historicalOrderRecordCheckBox ="//div[@class='clinical-historicalOrders-warp']//div[contains(@class,'el-table__body-wrapper')]//tr[1]//span[@class='el-checkbox__input']" ;
    //历史医嘱开立按钮
    public static String historicalOrderPrescribeButton = "//div[@class='historical-orders-wrap__content_right']//span[contains(.,'开立')]";
    //医嘱开立页面复制按钮下拉框
    public static String copyOrderDropdown = "//div[@class='func-part-wrapper']//div[@class='copy-arrow el-dropdown']/i";
    //复制按钮选项，?可替换成：复制、复制到剪切板、复制并停止
    public static String copyOrderDropdownItem = "//li[@class='el-dropdown-menu__item' and contains(.,'?')]";
    //剪切板页面
    public static String clinicalClipBoard ="//div[@class='clinical-clipBoard']";
    //剪切板全选框
    public static String clinicalClipBoardCheckbox ="//div[@class='clinical-clipBoard']//li[@class='order-list-header']//span[contains(@class,'el-checkbox__input')]";
    //剪切板页面开立按钮
    public static String clinicalClipBoardPrescribeButton = "//div[@class='clinical-clipBoard']//button//span[contains(.,'开立')]";
    //提示框的确认按钮
    public static String confirmButton ="//div[@class='el-message-box']//span[contains(.,'确定')]";
    //皮试按钮
    public static String skinTestButton = "//div[@role='dialog']//button[contains(.,'皮试')]";

    /**
     * 住院病历相关
     */
    //住院病历list图标
    public static String inpatientEmrList = "//div[@role='radiogroup']/label[contains(@class,'tab-action-list')]";
    //新增按钮
    public static String inpatientEmrAddButton = "//div[contains(text(),'新增')]";
    public static String inpatientEmrCreateButton = "//span[contains(text(),'新增')]";
    //批量新建病历弹框
    public static String inpatientEmrBatchCreateDialog= "//div[@aria-label='批量新建病历']";
    //否
    public static String inpatientEmrNoButton = "//span[contains(text(),'否')]";
    //模板选择页面
    public static String inpatientEmrTemplateSelectPage ="//span[contains(@class,'el-dialog__title') and .='模板选择']";
    //模板选择页面的病历目录分类
    public static String inpatientEmrClassName= "//div[contains(@class,'clinicalnote-template-class-table')]//div[contains(text(),'?')]";
    //模板选择页面全院页签
    public static String inpatientEmrTemplateSelectSheet = "//div[contains(@class,'el-tabs__item') and .='全院']";
    //模板选择页面搜索输入框
    public static String inpatientEmrTemplateSearchInput = "//input[contains(@placeholder,'请输入搜索内容')]";
    //根据搜索条件查询到的模板
    public static String inpatientEmrTemplate = "//span[text()=' ? ' and @class='node-name']";
    //模板选择页面确认按钮
    public static String inpatientConfirmButton="//div[contains(@aria-label,'模板选择')]//span[text()='确 定']";
    //住院病历的编辑页面病历标题
    public static String inpatientTitle = "//table[@id='dctable_AllContent']//span[text()='?']";
    //提交按钮
    public static String inpatientSubmitButton ="//button//span[text()='提 交']/..";
    //撤销提交按钮
    public static String inpatientCancelSubmitButton = "//button//span[text()='撤销提交']";
    //暂存按钮
    public static String inpatientSaveButton ="//button[@class='el-button']//span[text()='暂 存']/..";
    //签名按钮
    public static String inpatientSignButton = "//div[@aria-hidden='false']//button[.='签名']";
    //病历编辑页面第一个患者姓名
    public static String DocumentHeaderPatitenName = "//div[@id='divXTextDocumentHeaderElement']//span[@dc_innervalue='?']";
    //病历编辑页面文本框
    public static String requiredInput =  "//div[@id=\"divAllContainer\"]//span[@dc_contentreadonly=\"False\" and not(@dc_innereditstyle)]";
    //病历编辑页面数值型输入框
    public static String requiredNumeric = "//div[@id=\"divAllContainer\"]//span[@dc_contentreadonly=\"False\" and (@dc_innereditstyle=\"Numeric\")]";
    //病历编辑页面下拉单选框
    public static String requiredDropdownList = "//div[@id=\"divAllContainer\"]//span[@dc_contentreadonly=\"False\" and (@dc_innereditstyle=\"DropdownList\")]";
    //住院病历引用短语
    public static String quotePhrase = "//div[@class='widget-item dc-editor-widget-context-menu-wrap']//li[contains(text(),'引用短语')]";
    //住院病历审签通过按钮
    public static String inpatientPassButton = "//span[text()='审签通过']/..";
    //住院病历审签退回按钮
    public static String inpatientRejectButton = "//span[text()='审签退回']/..";
    //住院病历列表的病历
    public static String inpatientEmrSheet = "//div[@class=\"inpatient-emr-navigate-panel-body\"]//span[text()=' ? ' ]";
    //住院病历撤销审签按钮
    public static String inpatientCancelAuditButton = "//span[text()='撤销审签']/..";

    /**
     * 护理文书相关
     */
    //护理文书菜单
    public static String chartSpan = "//div[@class='child-menu']/span[contains(text(),'护理文书')]";
    //护理文书页面标识
    public static String chartTitle = "//div[@id='charting']//div[text()='护理文书']";
    //新建文书按钮
    public static String chartAddButton = "//button/span[contains(text(),'新建文书')]";
    //文书编辑页面标识
    public static String chartEditTitle = "//div[contains(text(),'?')]";
    //文书复选框
    public static String chartCheckBox = "//label[@class='w-checkbox']";
    //确定按钮
    public static String chartConfirmButton = "//div[@class='nurse-doc__buttons']//span[contains(text(),'确定')]";
    //填写按钮
    public static String charEditButton = "//li[@class=\"doc-list__item\"]//span[text()='?']/..//button//span[text()='填写']";
    //体温单页面编辑按钮
    public static String charAddButton = "//div[@class='title flex']//button//span[contains(.,'新建 +')]";
    //体温单页面展开页面图标
    public static String charEditIcon = "//div[@class='w-table__fixed']//div[@class='cell' and contains(.,'记录时间')]//i[@class='w-icon-arrow-right']";
    //体温单页面时间输入框
    public static String charEditTimeInput = "//div[contains(@class,'w-table__body-wrapper')]//div[@class='tableTimeItem']//input";
    //体温单编辑页面数值输入框
    public static String chartNumInput = "//span[contains(text(),'?')]/../../../td[2]//div[@class='el-input el-input--small']//input";
    //体温单编辑页面文本框
    public static String chartInput = "//span[contains(text(),'?')]/../../../td[2]//div[@class='inputWidth flex1 el-textarea']//textarea";
    //体温单编辑页面下拉框
    public static String chartDropdownList = "//span[contains(text(),'?')]/../../../td[2]//div[@class='el-select el-select--small']";
    //体温单编辑页面下拉框选项
    public static String chartDropdownListValue = "//li//span[text()='?']/../..";
    //体温单编辑页面提交按钮
    public static String chartSubmitButton = "//button//span[contains(.,'提交')]";
    //体温单编辑页面签名按钮
    public static String chartSignButton = "//span[contains(text(),'签 名')]";
    //入院评估单编辑页面单选框
    public static String chartRadio="//label[text()='?1']/..//label[@title='?2']";
    //入院评估单编辑页面复选框第一个选项
    public static String chartFirstCheck="//label[text()='?']/..//div[@class='w-checkbox-group is-discrete']/label[1]";
    //入院评估单编辑页面复选框最后一个选项
    public static String chartLastCheck="//label[text()='?']/..//div[@class='w-checkbox-group is-discrete']/label[last()]";
    //入院评估单编辑页面文本输入框
    public static String charInput ="//label[@for='?']/..//input";
    //入院评估单编辑页面下拉框
    public static String chartSelect ="//label[contains(text(),'?')]/..//input";
    //入院评估单编辑页面下拉框选项值
    public static String chartSelectItem ="//li//span[contains(text(),'?')]";
    //入院评估单编辑页面表格编辑按钮
    public static String chartEditButton ="//tr[@class='w-table__row']//span[contains(text(),'编辑')][1]";
    //入院评估单编辑页面评估按钮
    public static String charThirdButton ="//label[text()='?']/..//button";
    //评估单页面单选框或多选框的第一个选项值
    public static String chartAssessRadio = "//div[@class='el-radio-item']/div[1]";
    //添加评估方法按钮
    public static String EvaluationButton = "//span[contains(text(),'添加评估方法')]";
    //疼痛评估页面被选中的方法
    public static String painEvaluationIsChecked ="//label[@class='pain-evaluation-checks-item w-checkbox is-checked']//span[@class='w-checkbox__input']";
    //面部评估法选择框
    public static String painEvaluationItem ="//div[@class='pain-evaluation-checks w-checkbox-group']//span[contains(text(),'面部表情评估法')]";
    //面部评估框标识
    public static String painEvaluationForm = "//div[@class='nursing-doc__pain-evaluation-content-item']";
    //疼痛评估单的面部评估法第一个选项值
    public static String painEvaluationFormItem = "//div[@class='pain-evaluation-form-item'][1]";
    //评估单页面提交按钮
    public static String chartAssessSubmit ="//button//span[@data-label='提交']";

    /**
     * 出院出区相关
     */
    //出院出区页面下一步按钮
    public static String nextButton= "//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'下一步')]";
    //出院出区费用页面标识
    public static String feePage = "//div[@data-name='nurse-task-report']";
    //出院出区费用核对按钮
    public static String costCheckButton = "//button//span[contains(text(),'费用核对')]";
    //出院出区页面办理出区按钮
    public static String outAreaButton= "//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'办理出区')]";
    //病情转归下拉框
    public static String outAreaSelect= "//label[@for='dischargeOutcomeCode']/..//input";
    //第一个下拉框选项值
    public static String dropdownValue="//div[contains(@class,'el-select-dropdown__wrap')]//li[1]" ;
    //操作完成提示框确定按钮
    public static String outAreaConfirmButton= "//div[@class='el-message-box__wrapper']//div[@class='el-message-box']//button";

    //就诊管理
    public static String encounterMgmt="//span[@class='menuName unelli']/span[contains(text(),'就诊管理')]";
    //入区取消
    public static String enterWardRevoke="//div[@class='child-menu']/span[contains(text(),'入区取消')]";
    //入区取消-下一步
    public static String enterWardRevokeNext="//article[@class='cancel-area inpat-discharge-common']//button/span[contains(text(),'下一步')]";
    //取消入区
    public static String revokeEnterWard="//article[@class='cancel-area inpat-discharge-common']//button/span[contains(text(),'取消入区')]";
    //取消入区-确定
    public static String revokeEnterConfirm="//div[@class='el-message-box__wrapper']//button/span[contains(text(),'确定')]";
    //入区取消成功提示
    public static String revokeEnterWardSucMsg ="//p[contains(text(),'成功')]";

    //首页
    public static String homePage="//div[@class='nav-container']//div//span[contains(text(),'首页')]";
    //已出区
    public static String out="//div[@class='inpat-beadcard-top']//span[@class='plan-btn']//span[contains(text(),'已出区')]";
    //更多菜单
    public static String moreMenus="//td[@class='el-table_1_column_12  ']//span[contains(text(),'更多菜单')]";
    //出区召回
    public static String outHospitalRecall="//div[@class='bed-sub-menu-list-content']//div[contains(text(),'出区召回')]";
    //选择床位
    public static String selectBed="//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'选择床位')]";
    //选择床位-选中床位
    public static String selectBedButton="//div[@class='el-dialog']//div[@class='bedItem']";
    //选择床位-选中床位-确定
    public static String selectBedConfirm="//div[@class='el-dialog']//span[contains(text(),'确 定')]";
    //办理召回
    public static String recall="//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'办理召回')]";
    //办理召回-确定
    public static String recallConfirm="//div[@class='el-message-box__wrapper']//button/span[contains(text(),'确定')]";
    //出区召回成功提示
    public static String recallSucMsg ="//p[contains(text(),'成功')]";

    //转科转区
    public static String transferAre="//div[@class='child-menu']/span[contains(text(),'转科转区')]";
    //转科转区-下一步
    public static String transferAreNext="//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'下一步')]";
    //科室
    public static String Departmen="//div[contains(@class,'el-input')]//input[@placeholder='请输入科室名称/拼音']";
    //新科室
    public static String newDepartmen="//div[@class='el-scrollbar']//li/span[contains(text(),'?')]";
    //病区
    public static String Ward="//div[contains(@class,'el-input')]//input[@placeholder='请输入病区名称/拼音']";
    //新病区
    public static String newWard="//div[@class='el-scrollbar']//li/span[contains(text(),'?')]";
    //确定转区
    public static String transferConfirm="//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'确定转区')]";
    //撤销转区
    public static String transferRevoke="//article[@class='out-area inpat-discharge-common']//button/span[contains(text(),'撤销转区')]";
    //转区成功提示
    public static String transferConfirmSucMsg ="//p[text()='转区成功']";
    //撤销转区成功提示
    public static String transferRevokeSucMsg ="//p[text()='撤销转区成功']";

    /**
     *病房与床位
     */
    //主数据左侧搜索输入框
    public static String mdmSearchInput= "//div[@class='left-menu is-search']//input" ;
    //搜索结果
    public static String mdmSearchResult = "//div[@class='left-menu is-search']//ul[@class='content-list']//li";
    //病房与床位设置页面--床位设置
    public static String bedSettingPage = "//div[@class='tabs-list']//li[.='床位设置']";
    //添加床位按钮
    public static String addBedButton = "//button/span[contains(text(),'添加床位')]";
    //添加床位页面病区输入框
    public static String bedSettingPageWardInput= "//label[@for='wardId']/..//input";
    //床位设置页面，病区下拉框选项值
    public static String bedSettingPageWardSelectItem= "//body/div[@class='el-select-dropdown el-popper'][1]//li[contains(@class,'el-select-dropdown__item')]/span[.='?']";
    //病房下拉框
    public static String inpatRoomIdInput = "//label[@for='inpatRoomId']/..//input";
    //病房下拉框选项值
    public static String inpatRoomItem = "//div[@class='el-select-dropdown el-popper select-popper-inpatRoom']//li[1]";
    //病床编号输入框
    public static String bedNoInput = "//td[1]//input";
    //床位类型
    public static String bedTypeSelect = "//td[2]//input";
    //床位类型：编制床位
    public static String bedTypeItem ="//li[contains(@class,'el-select-dropdown__item') and contains(.,'编制床位')]";
    //保存按钮
    public static String saveButton = "//button/span[.='保存']" ;
    //床位收费设置页面
    public static String addBedDialog = "//div[@aria-label='床位收费设置']";
    //床位服务绑定按钮
    public static String addServiceButton = "//button//span[contains(text(),'床位服务绑定')]";
    //床位收费服务名称选择框
    public static String FeeServiceNameInput = "//div[@aria-label='床位服务绑定']//input[contains(@placeholder,'请选择')]";
    //床位收费服务名称第一个选项
    public static String FeeServiceNameSelect = "//div[@class='el-select-dropdown el-popper']//li[contains(@class,'el-select-dropdown__item')]/span[1]";
    //确认按钮
    public static String dialogConfirmButton= "//div[@class='el-dialog__footer']//span[contains(.,'确定')]";

    //皮试管理
    //待皮试记录的复选框
    public static String skinTestManagementCheckBox ="//div[@class='el-table__fixed']//div[contains(@class,'el-table__fixed-body-wrapper') and contains(.,'?')]//span[@class='el-checkbox__inner']";
    //皮试操作按钮
    public static String skinTestManagementSkinTestButton = "//div[@class='el-table__fixed-right']//div[contains(@class,'el-table__fixed-body-wrapper')]//span[text()='?']";
    //药品批号输入框
    public static String skinTestManagementDrugBatch = "//div[@role='dialog' and @aria-label='开始皮试']//div[contains(@class,'el-form-item') and contains(.,'药品批号')]//input";
    //保存按钮
    public static String skinTestManagementSaveButton = "//div[@role='dialog']//button//span[text()='保存']";
    //状态选择框
    public static String skinTestStatusInput =  "//div[@class='title-wrap']//div[@class='el-form-item' and contains(.,'状态')]//input";
    //皮试状态选项值
    public static String skinTestStatusItem = "//li[contains(@class,'el-select-dropdown__item')]//span[text()='?']";
    //结束皮试页面皮试结果下拉框
    public static String skinTestResultSelect = "//div[@role='dialog' and @aria-label='结束皮试']//div[contains(@class,'el-form-item') and contains(.,'皮试结果')]//input";
    //皮试结果选项值
    public static String skinTestResultItem = "//li[contains(@class,'el-select-dropdown__item')]//span[text()='?']";
    //皮试审核密码输入框
    public static String skinTestResultAuditPasswordInput = "//div[@aria-label='审核']//input[@type='password']";

}
