package com.winning.testsuite.workflow.Outpatient;


public class WnOutpatientXpath {

	/**
	 * ############### 门诊工作站XPATH #####################
	 **/

    /**
     * 门诊工作站
     **/
    // 登录页面
    public static String loginUsernameInput = "//input[contains(@placeholder,\"账号\")]";
    public static String loginPasswordInput = "//input[contains(@placeholder,\"密码\")]";
    public static String loginLoginButton = "//button[.=\"登录\"]";
    //多院区标识
    public static String loginHospitalFlag = "//div[@class='login-list org']//div[@class='login-list__input el-cascader']";
    //多院区展示列表
    public static String loginHospitalAreaName = "//div[@class='el-cascader-panel']//div[@class='el-cascader-menu__wrap el-scrollbar__wrap']//ul//li";

    // portal页面
    public static String portalPageFlag = "//span[.='医院业务管理系统']";
    public static String portalOutpatientEntrance = "//main[@class=\"el-main\"]//li/p[.=\"门诊医生站\" or .=\"诊区医生站\"]";

    //就诊科目选择框
    public static String outpatientSubjectChooseBox = "//div[@role='dialog' and starts-with(.,'就诊科目选择')]";
    //科目列表中所有选择框
    public static String outpatientSubjectChooseBoxCheckBox = outpatientSubjectChooseBox + "//label[contains(@class,'el-checkbox el-tooltip item')]";
    //跳过导航按钮
    public static String outpatientSkipButton = "//div[@class='skip skip1']";
    //checkbox中被选中的选项
    public static String outpatientCheckboxIsChecked = outpatientSubjectChooseBox + "//span[@class='el-checkbox__input is-checked']";
    //删除的icon
    public static String outpatientIconError = "el-icon-error";
    //患者信息为空banner
    public static String outpatientEmptyBanner = "//div[@class='patient-info-placeholder']";
    //历史处置 6个月按钮
    public static String quickDateBtnSixMonth = "//div[contains(@class,'quickDateBtn')]//label[contains(.,'6个月')]";
    //患者信息列表
    public static String outpatientPatientList = "//div[@id='header-search']";
    //日期输入框
    public static String outpatientDateInput = "//input[contains(@placeholder,\"选择日期\")]";
    //患者"就诊号序\\姓名\\门诊号"输入框
    //public static String outpatientPatientSearchInput = "//div[@id='header-search']//input[contains(@placeholder,'姓名') and contains(@placeholder,'号序')]";
    public static String outpatientPatientSearchInput ="//div[contains(@class,'input-with-select')]//input[contains(@placeholder,'搜索')]";
    //患者列表-搜索条件-开始日期
    public static String outpatientPatientSearchInputBeggingData = "//input[@placeholder=\"开始日期\"]";
    //患者列表-搜索条件-结束日期
    public static String outpatientPatientSearchInputEndData = "//input[@placeholder=\"结束日期\"]";
    //患者信息弹窗保存并关闭按钮
//	public static String outpatientPatientInfoDialogSaveButton = "//div[@class='make-up-content']//button[.='保存并关闭']";
    public static String outpatientPatientInfoDialogSaveButton = "//div[@class='el-dialog patient-edit-dialog']/div//span[contains(text(),'取消')]";
    //医生站-患者信息展示-患者姓名
    public static String outpatientPatientInfoName = "//div[@class=\"basic\"]/span[1]";
    //医生站-患者信息展示-患者性别
    public static String outpatientPatientInfoSex = "//div[@class=\"basic\"]/span[2]";
    //医生站-患者信息展示-身份证号码
    public static String outpatientPatientIdNum = "//div[@class =\"contact-span\"]/i[contains(@class,\"idcard\")]/../span";
    //医生站-患者信息展示-身份证号码
    public static String outpatientPatientDepartmentChineseName = "//div[contains(@class,\"essName\")]//span";
    //医生站-患者信息展示-挂号号序
    public static String outpatientPatientInfoVisitSeqNumber = "//div[contains(@class,\"triageSeqNo\")]//span";
    //医生站-医保卡核对患者信息弹框
    public static String InsuranceConfirmDialog = "//div[@class='next-patient-confirm-wrapper']";
    //医生站-医保卡核对患者信息弹框关闭按钮
    public static String InsuranceConfirmDialogCloseBtn = "//div[@class='next-patient-confirm-wrapper']//i[contains(@class,'close-next-patient-confirm-wrapper')]";

    //代配药人弹窗    next-patient-confirm-wrapper
    public static String outpatientDispenserInfo = "//div[@class='next-patient-confirm-wrapper' and contains(.,'代配药人信息')]";
    //儿童信息录入弹框
    public static String outpatientChildrenInfoDialog = "//div[@class='children-info-wrapper']";
    //患者搜索按钮
//	public static String outpatientPatientSearchButton = "//span[@class='reader-card-btn' and .='搜索']";
    //知识体系类别
    public static String knowledge_system = "诊疗路径简易版";
    //卫宁医学知识体系知识体系类别
    public static String knowledge_WN = "卫宁医学知识体系";
    //主诉症状类型
    public static String symptom_type = "单症状";
    //诊疗路径推荐检验框
    public static String recommendLabBox_pathWay = "//div[contains(@class,'treatment-labtest-wrap')]";
    //诊疗路径推荐检查框
    public static String recommendExamBox_pathWay = "//div[contains(@class,'treatment-exam-wrap')]";
    //诊疗路径推荐治疗框
    public static String recommendTreatBox_pathWay = "//div[contains(@class,'treatment-drug-wrap')]";
    //主诉日期输入框
    public static String outpatientSymptomDayInput = "//div[@class='disease-days']//input";
    //诊疗路径简易版症状按钮
    public static String outpatientSymptomWarpButton = "//div[@class='symptom-wrap-button']";
    //诊疗路径简易版新增症状按钮
    public static String outpatientChiefComplaintAdd = "//div[@class='chief-complaint-list add']//button";
    //诊疗路径简易版症状名称搜索框
    public static String outpatientAddSymptomSearchInput = "//div[@class='el-autocomplete symptom-value']//input";
    //诊疗路径简易版新增症状确定按钮
    public static String outpatientChiefComplaintAddCommitButton = "//div[@class='el-dialog add-symptom-diaglog']//span[.='确 定']";
    //诊断确定按钮
    public static String outpatientDiagnosisCommitButton = "//div[contains(@class,'disease-dpecies-detail-wrap')]//div[.='确定']";
    //诊断搜索按钮
    public static String outpatientDiagnosisSearchButton = "//div[@class='diagnostic-search-button']";
    //诊断搜索输入栏
    public static String outpatientSearchDiagnosisInput = "//div[@class='diagnostic-search-wrap']//input[@placeholder='请输入内容']";
    //中医诊断症型下拉框第一条value
    public static String outpatientSearchZxFirstSelectvalue = "//input[@placeholder='请搜索证型'][last()]/../../div[@class=\"syndromedata-wrap\"]/span[1]/div[2]";
    //中医诊断治法下拉框第一条value
    public static String outpatientSearchZfFirstSelectvalue = "//input[@placeholder='请搜索治法'][last()]/../../div[@class=\"syndromedata-wrap\"]/div[1]";
    //诊断编辑按钮
    public static String outpatientDiagnosisEditIcon = "//i[@class='icon-edit']";
    //编辑症状输入框
    public static String outpatientDiagnosisEditInput = "//div[@class='diagnostic-item']/div[3]//input";
    //医嘱搜索输入栏
    public static String outpatientSearchOrderInput = "//div[@class='disposal__search']//input[@placeholder='请输入内容']";
    //医嘱搜索结果栏
    public static String outpatientSearchOrderResultBox = "//div[@class='disposal__search--input--res']";
    //医嘱搜索等待动画
    public static String outpatientSearchOrderLoadingBox = "//div[contains(@class,'loading_check')]";
    //加载图标
    public static String outpatientCommonCircle = "//p[contains(@class,'common_circle')]";
    //已开立的医嘱列表中第一条医嘱
    public static String outpatientPreviewListFirstOrder = "//*[@id=\"disposal\"]/div/div[2]/div[1]/div/div";
    //门诊诊断弹出框
    public static String outpatientDiagnosisEditBox = "//div[@id='diagnostic-editor']";
    //门诊诊断弹出框中第一个诊断
    public static String outpatientDiagnosisEditBoxFirstDiag = "//div[@id='diagnostic-editor']//label[@class='el-checkbox']";
    //门诊诊断关联模板弹出选择第一个模板
    public static String outpatientDiagnoseRelationRecordsBoxFirst = "//div[@class='el-dialog__body']/div[2]/div[2]/ul/li[1]/span";
    //门诊诊断弹出框确认按钮
    public static String outpatientDiagnosisEditBoxCommitButton = "//div[@class='diagnostic-search-footer']//button[.='确定']";
    //处置加工厂模板
    public static String outpatientDisposalFactoryTemplateButton = "//span[@class='disposal__search--select--list--item--text' and .='模板']";
    //处置加工厂-(西成药、草药等)用参数：?1代替
    public static String outpatientDisposalFactoryButton = "//span[@class='disposal__search--select--list--item--text' and .='?1']";
    //处置加工厂框
    public static String outpatientDisposalFactory = "//div[@class='disposal__factory']";
    //草药加工厂标识
    public static String outpatientDisposalFactoryHerbFlag = "//div[@class='herbal-bg']";
    //西药加工厂标识
    public static String outpatientDisposalFactoryDrugFlag = "//div[@class='factory_west_wrap']";
    //检验加工厂标识
    public static String outpatientDisposalFactoryLabFlag = "//div[@class='disposal__factory detection_factory']";
    //检查加工厂标识
    public static String outpatientDisposalFactoryExamFlag = "//div[@class='detection_factory inspect_factory']";
    //治疗加工厂标识
    public static String outpatientDisposalFactoryTreatFlag = "//div[@class='disposal__factory treatment']";
    //病理加工厂标识
    public static String outpatientDisposalFactoryPathologyFlag = "//div[@class='pathology_factory']";
    //草药剂量填写框
    public static String outpatientDisposalFactoryHerbNumInput = "//span[.='剂量']/following-sibling::*//input[@role]";
    //草药剂量保存按钮
    public static String outpatientDisposalFactoryHerbNumSave = "//p[@class='herbal-btns']//span[.='保存']";
    //草药代煎方式下拉框
    public static String outpatientDisposalFactoryHerbDecocteMethodList = "//div[contains(@class,'el-form-item') and contains(.,'代煎方式')]//i";
    //草药代煎方式下拉值定位，默认为空，为空时无需设置
    public static String outpatientDisposalFactoryHerbDecocteMethodValue = "";
    //西药剂量输入框
    public static String outpatientDisposalFactoryDrugDoseInput = "//div[@class='disposal__factory']//div[contains(@class,'is-controls-right')]//input";
    //西药数量输入框
    public static String outpatientDisposalFactoryDrugNumInput = "//div[@class='disposal__factory']//div[contains(@class,'factory_drug_input')]//input";
    //西药院内数量输入框(参数CL083开启有效)
    public static String outpatientDisposalFactoryDrugInHospitalNumInput ="//div[@class='disposal__factory']//label[contains(.,'院内次数')]//..//input";
    //西药天数输入框
    public static String outpatientDisposalFactoryDrugDaysInput = "//div[@class='disposal__factory']//div[contains(@class,'itemInputNumber')]//input";
    //治疗次数输入框
    public static String outpatientDisposalFactoryTreatNumInput = "//div[@class='disposal__factory']//li[.='次']//input";
    //检验数量输入框
    public static String outpatientDisposalFactoryLabNumInput = "//div[@class='disposal__factory']//div[contains(text(),'次')]//input";
    //西药加工厂药品名称
    public static String outpatientDisposalFactoryDrugName = "//div[@class='disposal__factory']//span[contains(@class,'medicineName')]";
    //西药加工厂修改频次后弹出的剂量编辑框的完成按钮
    public static String outpatientDisposalFactoryWanChengAfterEditFreq ="//div[@class='el-popover el-popper clinical-disposal-pop']//button[.='完成']";
    //检查临床摘要填写框
    public static String outpatientDisposalFactoryExamSummaryInput = "//li[contains(.,'临床摘要')]//textArea";
    //检验注意事项填写框
    public static String outpatientDisposalFactoryLabTipsInput = "//div[@class='disposal__factory']//p[.='注意事项']//input";
    //处置加工厂的所有复选框 包括人体图
    public static String outpatientDisposalFactoryCheckBoxes = "//div[@class='disposal__factory']//div[contains(@class,'all-exam-classify') or contains(@class,'item-list') or contains(@class,'more-zb')]//label[contains(@class,'el-checkbox') and not(contains(@class,'disabled'))]";
    //所有检查项目选择框
    public static String outpatientDisposalFactoryExamDetail = "//p[@class='exam-item-group']//label[contains(@class,'el-checkbox')]";
    //被选中的检查项目指标明细
    public static String outpatientDisposalFactoryExamDetailIsChecked = "//p[@class='exam-item-group']//label[@class='el-checkbox is-checked']";
    //被选中的检验指标明细
    public static String outpatientDisposalFactoryLabItemIsChecked = "//div[@class='el-checkbox-group']//label[@class='el-checkbox is-checked']";
    //处置加工厂确认按钮
    public static String outpatientDisposalFactoryCommitButton = "//div[contains(@class,'disposal')]//button[.='确认']";
    //处置加工厂取消按钮
    public static String outpatientDisposalFactoryCancelButton = "//div[contains(@class,'disposal')]//button[.='取消']";
    //精麻毒确认按钮
    public static String outpatientDisposalFactorySpectialInfoCommitButton = "el-button el-button--primary el-button--small is-round";
    //精麻毒确认按钮 xpath
    public static String outpatientDisposalFactorySpectialInfoCommitButtonXpath = "//form[contains(@class,'specialInfo')]//button[.='完成']";
    //人体图小项目选中控件（针对CT，MRI等）
    public static String outpatientDisposalFactoryBodyImgColIsChecked = "//label[@class='el-checkbox wrapper-box is-checked']/span[@class='el-checkbox__input is-checked']";
    //人体图控件
    public static String outpatientDisposalFactoryBodyImgBox = "//div[@class='disposal__factory']//div[contains(@class,'body-img-col')]";
    //人体图一级部位
    public static String outpatientDisposalFactoryBodyImgPartButton = "//div[@class='disposal__factory']//div[contains(@class,'body-img-col')]//div[@class='part-list']//li";
    //病理添加明细按钮
    public static String outpatientDisposalFactoryAddDetailBtn = "//p[@class='spec-add cursor_point']";
    //病理添加部位下拉框
    public static String outpatientDisposalAddPartSelect = "(//div[@class='cell']//i[@class='el-select__caret el-input__icon el-icon-arrow-up'])[1]";
    //病理标本下拉框
    public static String outpatientDisposalSpecimenSelect = "(//div[@class='cell']//i[@class='el-select__caret el-input__icon el-icon-arrow-up'])[2]";
    //加工厂医嘱详情按钮
    public static String outpatientDisposalTitleShowRpBpButton = "//p[@class='disposal__title--default']//span[.='费用']" ;
    //点击确定后等待icon
    public static String outpatientDisposalFactoryIconLoading = "//i[@class='el-icon-loading']";
    //医保审批框
    public static String outpatientLabInsuranceTypeDialog = "//div[@role='dialog' and contains(.,'医保控费')]";
    //医保审批自费选项
    public static String outpatientUnselectedBoxes = "//label[@role='radio' and @class='el-radio' and .='自费']";
    //医保审批确认按钮
    public static String outpatientLabInsuranceTypeDialogCommitButton = "//div[@role='dialog']//button[@id='add']";
    //签署按钮
    public static String outpatientOrderSignButton = "//div[@id='disposal']//button[.='签署' and not(@disabled)]";
    //不可点击的签署按钮
    public static String outpatientOrderSignButtonDisabled = "//div[@id='disposal']//button[.='签署' and @disabled]";
    //签署并打印按钮
    public static String outpatientOrderSignAndPrintButton = "//div[@id='disposal']//button[.='签署并打印']";
    //已开立的医嘱列表
    public static String outpatientOrderList = "//div[contains(@class,'previewList_wrap preview-search')]";
    //病例既往第一个病症按钮
    public static String outpatientEmrDiagnoseHistory = "//div[@class='emr']//div[@class='editor-paragraph' and contains(.,'既往史')]//span[@class='editor-fragment']";
    //既往病史选择框 '无' 选项
    public static String outpatientSelectBtnNo = "//div[@wn-domain='emr']//li[.='无']";
    //既往病史选择框 '已婚' 选项
    public static String outpatientSelectBtnMarried = "//div[@wn-domain='emr']//li[.='已婚']";
    //既往病史选择框 '已育' 选项
    public static String outpatientSelectBtnBred = "//div[@wn-domain='emr']//li[.='已育']";
    //病例婚育史婚姻情况按钮
    public static String outpatientEmrDiagnoseMarriageHistory = "//div[@class='emr']//div[@class='editor-paragraph' ]//span[@class='editor-fragment']//span[@class='wn-value'  and contains(.,'婚姻情况')]";
    //婚育史选择框'已婚'选项
    public static String outpatientSelectBtnMarriageHistoryMarried = "//div[@wn-domain='emr']//li[contains(.,'已婚')]";
    //病历签署按钮
    public static String outpatientEmrSignButton = "//div[@class='emr__editor']//button[.='签署' and not(@disabled)]";
    //病历签署按钮 - 禁用
    public static String outpatientEmrSignButtonDisabled = "//div[@class='emr__editor']//button[.='签署' and @disabled]";
    //病历打印按钮
    public static String outpatientPrintButton = "//div[@class='emr__editor']//span[contains(text(),'打印')]";
    //病历签署成功标识
    public static String outpatientEmrSignSucFlag = "//div[contains(@class,'el-message el-message--success')]//p[.='病历签署成功']";
    //病例保存按钮
    public static String outpatientEmrSaveButton = "//div[@class='emr__editor']//button[.='保存']";
    //病例保存成功标识
    public static String outpatientEmrSaveSucFlag = "//div[@class='el-message el-message--success is-closable']//p[.='病历保存成功']";
    //撤销病历按钮
    public static String outpatientEmrRevokeButton = "//div[@class='emr__editor']//button[.='撤销签署' and not(@disabled)]";
    //历史处置按钮
    public static String outpatientHistoricalDisposal = "//div[(contains(@class,'historical-disposal') or contains(@class,'simple-model-tab') and not(contains(@class,'wrap'))) and contains(.,'历史处置')]";
    //历史处置开始日期
    public static String outpatientHistoricalDateStart = "//input[@class='el-range-input' and @placeholder='开始日期']";
    //历史处置结束日期
    public static String outpatientHistoricalDateEnd = "//input[@class='el-range-input' and @placeholder='结束日期']";
    //历史处置清空日期图标
    public static String outpatientHistoricalDateClearIcon = "//div[contains(@class,'el-range-editor')]//i[contains(@class,'el-range__close-icon')]";
    //历史处置本科室选择框
    public static String outpatientHistoricalSelfSubject = "//label[contains(@class,'el-checkbox')]//span[contains(.,'本科室')]";
    //历史处置列表
    public static String outpatientHistoricalList = "//ul[contains(@class,'history-list')]";
    //历史处置明细框
    public static String outpatientHistoricalOrderView = "//ul[contains(@class,'history-detail')]";
    //历史处置明细
    public static String outpatientHistoricalOrderList = "//ul[contains(@class,'history-detail')]//div[contains(@class,'detail-info')]";
    //历史处置检查
    public static String outpatientHistoricalOrderCheck = "//ul[contains(@class,'history-detail')]//li[contains(@class,'flex')]";
    //历史处置引用全部按钮
    public static String outpatientHistoricalQuoteAll = "//li[contains(@class,'isSelected')]//span[contains(@class,'quoteBtnImg')]";
    //历史处置引用医嘱
    public static String outpatienthistoricalQuoteOrder = "//ul[contains(@class,'history-detail')]//span[contains(@class,'quoteBtnImg')]";
    //模板按钮
    public static String outpatientOrderTemplateButton = "//div[@class='collapse-title-wrap treatment-title template' and .='模板']";
    //模板处置
    public static String outpatientTemplateDisposal = "//div[(contains(@class,'historical-disposal') or contains(@class,'simple-model-tab') and not(contains(@class,'wrap'))) and contains(.,'处置模板')]";
    //模板名称输入栏
    public static String outpatientOrderTemplateSearchInput = "//input[@placeholder='请输入模板名称']";
    //第一个模板引用按钮
    public static String outpatientOrderTemplateFirstQuoteBtn = "//i[@class='quoteBtnImg hot-img win-icon-quote']";
    //模板引用成功标识
    public static String outpatientOrderTemplateQuoteSucFlag = "//div[@class='el-message el-message--success is-closable']//p[.='引用成功']";
    //未签署标识
    public static String outpatientUnsignedOrder = "//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap') and contains(.,'开立')]";
    //编辑医嘱标识
    public static String outpatientUpdateOrderIcon = "//span[contains(@class,'win-icon-edit')]";
    //已签署标识
    public static String outpatientSignedOrder = "//div[contains(@class,'previewList_wrap')]//li[.='签署']";
    //撤销医嘱按钮
    public static String outpatientRevokeOrderBtn = "//div[contains(@class,'previewList_wrap')]//span[contains(@class,'win-icon-recall')]";
    //按医嘱名字撤销医嘱
    public static String outpatientRevokeOrderByName = "//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap') and contains(.,'签署') and contains(.,'?1')]//span[contains(@class,'win-icon-recall')]";
    //删除医嘱按钮
    public static String outpatientDeleteOrderBtn = "//div[contains(@class,'previewList_wrap')]//span[contains(@class,'win-icon-delete')]";
    //撤销医嘱加载图标
    public static String outpatientRevokeLoading = "//div[contains(@class,'loading_single') or contains(@class,'loading_check')]";
    //医嘱列表中的医嘱名称
    public static String outpatientOrderName = "//div[contains(@class,'previewList_wrap')]//div[contains(@class,'previewItemWrap')]//li[contains(@class,'content')]//p[contains(@class,'baseline')]/span[contains(@class,'Name')]";
    //治疗方案按钮
    public static String outpatientRecommendTreatBtn = "//div[@class='collapse-title-wrap treatment-title' and contains(.,'治疗方案')]";
    //第一个治疗方案开立按钮
    public static String outpatientRecommendTreatFirstPrescribeBtn = "//div[@class='medication']//div[.='开立']";
    //第一个推荐检验开立按钮
    public static String outpatientRecommendLabFirstPrescribeBtn = "//div[@class='diagnosis-detail-item checkout-wrap']//div[.='开立']";
    //第一个推荐检查开立按钮
    public static String outpatientRecommendExamFirstPrescribeBtn = "//div[@class='diagnosis-detail-item inspect-wrap']//div[.='开立']";
    //用药目的：预防
    public static String outpatientDisposalFactoryDrugPurposeRadioPrevent = "//label[contains(@class,'purposeRadio') and contains(.,'预防')]";
    //用药目的：治疗
    public static String outpatientDisposalFactoryDrugPurposeRadioTreat = "//label[@class='el-radio purposeRadio' and contains(.,'治疗')]";
    //医嘱联动确定按钮
    public static String outpatientLinkageCommitButton = "//div[@aria-label='医嘱联动']//button[.='确定']";
    //单个联动信息所在的div
    public static String outpatientLinkageDiv = "//div[@aria-label='医嘱联动']//div[@class='row']";
    //医保审批：报销
    public static String outpatientMedicalInsuranceApprovaReimbursement = "//div[@class='el-dialog__body']//span[.='报销']";
    //医保审批：自费
    public static String outpatientMedicalInsuranceApprovaOwnExpense = "//div[@class='el-dialog__body']//span[.='自费']";
    //医保审批确定按钮
    public static String outpatientMedicalInsuranceApprovaCommitButton = "//div[@class='el-dialog' and contains(.,'医保审批')]//span[.='确 定']";
    //结束就诊按钮
    public static String outpatientEndOfTreatmentBtn = "//*[@id='outpatient-header__setting']/span[1]";
    //是否需要预约的弹窗
    public static String visitConfirmDialog = "//div[@class='el-message-box prompt-book-next-visit-confirm-class']";
    //取消是否需要预约的弹窗
    public static String visitConfirmDialogCancleBtn = "//div[@class='el-message-box prompt-book-next-visit-confirm-class']//div[3]/button[1]/span[1]";
    //处方列表按钮
    public static String outpatientRecipeListIcon = "//li[@class='disposal__title--showRpBp']";
    //警示标志
    public static String outpatientWarningIcon = "//ul[contains(@class,'history-detail')]//i[@class='el-icon-warning']";
    //历史处置默认本科室
    public static String outpatientCheckboxLabel = "//span[@class='el-checkbox__label' and contains(.,'本科室')]/..//span[@class='el-checkbox__input is-checked']";
    //历史处置时间选择框
    public static String outpatientTimeSelect = "//*[@id=\"disposal\"]/div/div[5]/div/div[1]/div";
    //默认30日选择
    public static String outpatientTimeSelectPicker = "//button[@class='el-picker-panel__shortcut' and contains(.,'1个月')]";
    //历史处置引用标志
    public static String outpatientQuoteBtn = "//p[@class='copyAndQuote dealBtn_quote']";
    //历史处置内容批量处置
    public static String outpatientDealBtnQuote = "//ul[contains(@class,'history-detail')]/li[1]/span[2]";
    //搜索医嘱的所有搜索结果
//    public static String outpatientSearchOrderAllResultList = "//li[contains(@class,'search_item_name')]//div[contains(@class,'search-name')]//span[@title]/..";
    public static String outpatientSearchOrderAllResultList = "//li[contains(@class,'search_item_name')]//div[contains(@class,'search-name')]";
    //医嘱检索设置按钮
    public static String outpatientSearchSettingButton ="//div[@class=\"search-stock\"]/i";
    //库存勾选框(改动后的xpath)
    public static String outpatientSearchStockCheckboxNew ="//label[contains(.,'有库存及权限')]";
    //库存勾选框（未改版前的xpath）
    public static String outpatientSearchStockCheckbox ="//div[contains(@class,'search-stock')]//label[contains(.,'有库存')]";
    //检索设置保存按钮
    public static String outpatientSearchSettingConfirmButton ="//div[contains(@aria-label,\"检索设置\")]//span[contains(text(),\"保存\")]";
    //搜索医嘱的所有有库存的搜索结果
    public static String outpatientSearchOrderAllResultListHasStock = "//li[contains(@class,'search_item_name')]//div[contains(@class,'search-name') and contains(@class,'blackTxt')]//span[@title]";
    //医嘱搜索结果第一条医嘱
    public static String outpatientSearchOrderFirstResult = "//div[@class='drugTypeBlock']//div[@title]";
    //医嘱搜索结果（任意一条）
    public static String outpatientSearchOrderResult = "//li[@class = 'active resizeleft-con search_item_name flex']//div[@class = 'blackTxt overWidthTxt']";
    //医嘱搜索无库存的结果（任意一条）
    public static String outpatientSearchOrderResultNoStock = "//li[@class = 'active resizeleft-con search_item_name flex']//div[@class = 'grayTxt overWidthTxt']";
    //医嘱总价格
    public static String orderTotalPrice = "//div[@class='price']//span[starts-with(.,'￥')]";
    //提示消息框
    public static String alertBox = "//div[@role='alert' and contains(@class,'el-message')]";
    //是否判断错误提示的同时判断警告提示
    public static boolean checkWarning = false;
    //打印确认框
    public static String printConfirmForm = "//div[@class='el-message-box print-confirm']";
    //打印确认按钮
    public static String printConfirmButton = "//div[@class='el-message-box print-confirm']//span[contains(text(),'打印')]";
    //警告文字
    public static String warnText = "//*[contains(@class,'warn-text')]";
    //皮试按钮
    public static String pishiButton = "//div[@role='dialog']//button[contains(.,'皮试')]";
    //联动费用价格
    public static String linkItem = "//div[contains(@class,'rpbp__bp')]//i[contains(.,'联')]/../..//span[contains(.,'￥')]";
    //候诊列表按钮
    public static String waitListBotton = "//i[contains(@class,'win-icon-outpatient-menu')]";
    //患者头像
    public static String patientPhoto = "//div[contains(@class,'avaterParent')]//img";
    //患者详情展示框
    public static String patientDetail = "//div[contains(@class,'patient-details')]";
    //患者详情健康摘要
    public static String patientDetailHealthSummary = "//div[contains(@class,'patient-details')]//li[contains(@class,'item') and contains(.,'健康摘要')]";
    //患者详情个人信息
    public static String patientDetailPersonInfo = "//div[contains(@class,'patient-details')]//li[contains(@class,'item') and contains(.,'个人信息')]";
    //患者详情就诊信息
    public static String patientDetailEncounterInfo = "//div[contains(@class,'patient-details')]//li[contains(@class,'item') and contains(.,'就诊信息')]";
    //皮试记录添加按钮
    public static String addAllergyRecordIcon = "//div[contains(@class,'health-digest-wrap')]//i[contains(@class,'plus')]";
    //过敏源输入框
    public static String AllergySourceInput = "//div[contains(@class,'allergy-detail')]//label[contains(.,'过敏源')]/..//input";
    //过敏结果输入框
    public static String AllergyResultInput = "//div[contains(@class,'allergy-detail')]//label[contains(.,'过敏结果')]/..//input";
    //过敏确诊日期输入框
    public static String AllergyDateInput = "//div[contains(@class,'allergy-detail')]//label[contains(.,'操作时间')]/..//input";
    //过敏信息保存按钮
    public static String AllergySaveButton = "//div[contains(@class,'allergy-detail')]//button[.='保存']";
    //患者详情关闭按钮
    public static String patientDetailCloseBtn = "//button[@aria-label='Close']";
    //禁止开立弹窗确认按钮
    public static String AllergyWarnCommitBtn = "//div[contains(@class,'msgBoxConfirm') and contains(.,'过敏,不能开立该药品!')]//button[contains(.,'确定')]";
    //转诊成功提示
    public static String outpatientSucMsg = "//div[@class='el-message el-message--success is-closable']//p[contains(.,'转诊成功') ]";
    //诊间预约成功提示
    public static String outpatientSucMsgForreserve= "//div[@class='reserve-success-page']//span[contains(.,'预约成功')]";
    
    /**
     * 开立医嘱
     */
    //选择科目确认按钮
    public static String subject_confirm_button = "el-button el-button--primary el-button--small";
    //西药医嘱开立确认按钮
    public static String westDrugConfirmButton = "el-button disposal-btns btn-commit el-button--primary el-button--small is-round";
    //草药医嘱开立确定按钮
    public static String herbalMedicineConfirmButton = "el-button herbal-commit el-button--primary el-button--small";
    //治疗医嘱开立确认按钮
    public static String treatmentConfirmButton = "el-button el-button--primary el-button--mini is-round";
    //医嘱搜索框
    public static String orderSearchInput = "div.el-input.el-input--small.el-input--prefix>input";
    //医嘱开立加载框
    public static String orderSingoffLoadingFrame = "//*[@id=\"disposal\"]/div/div[6]";
    //医嘱撤销加载框
    public static String orderRevokeLoadingFrame = "//*[@id=\"disposal\"]/div/div[6]/p";
    //签署按钮
    public static String signOffBtn = "el-button el-button--default el-button--small is-round btnConfirmColor";
    //医嘱撤销按钮
//    public static String revokeOrderbtn = "el-tooltip item size cancel";  //css属性已改变
    public static String revokeOrderbtn = "el-tooltip item size win-icon-recall";
    //医嘱删除按钮
    public static String deleteOrderbtn = "//span[@class='el-tooltip item size win-icon-delete']";
    //签署病历按钮
    public static String signOffEmrBookBtn = "el-button highlight el-button--default el-button--small is-round";
    //撤销病历按钮
    public static String revokeEmrBookBtn = "//span[contains(text(),'撤销')]";


    /**
     * 病历测试用到的变量
     */
    //病历指定段落
    public static String emrParagraph = "//div[@class='emr']//div[contains(@class,'editor-paragraph') and contains(.,'?1')]";
    //病历指定片段
    public static String emrFragment = "//div[@class='emr']//div[contains(@class,'editor-paragraph') and contains(.,'?1')]//span[(contains(@class,'editor-fragment') or contains(@class,'editor-textbox')) and contains(.,'?2')]";
    //病历指定单元
    public static String emrUnit = "//div[@class='emr']//div[contains(@class,'editor-paragraph') and contains(.,'?1')]//span[(contains(@class,'editor-fragment') or contains(@class,'editor-textbox')) and contains(.,'?2')]//span[contains(@class,'editor-unit') and contains(.,'?3')]";
    //诊断关联的病历模板确定按钮
    public static String dignoseRelateEmrCommitBtn = "//div[@role='dialog' and contains(.,'诊断关联的病历模板')]//button[.='确定']";
    //诊断关联的病历模板取消按钮
    public static String dignoseRelateEmrCancelBtn = "//div[@role='dialog' and contains(.,'诊断关联的病历模板')]//button[.='取消']";
    //诊疗路径诊断复选框
    public static String dignoseCheckBox = "//div[@class='diagnostic-item-wrap']//label//span[contains(@class,'el-checkbox__input')]";
    //诊疗路径'高血压'诊断
    public static String findDignoseCheck = "//div[contains(@class,'diagnosis-content')]//div[contains(@class,'diagnosis-checktext') and contains (.,'?disease')]";
    //左侧诊疗路径诊断列表
    public static String Dignoselist = "//div[contains(@class,'diagnosis-content')]";
    //左侧诊疗路径诊断列表-收藏诊断
    public static String DignoselistFirstDignose = "//div[contains(@class,'diagnosis-content')]/div//div[@class =\"text-over-tooltip-components\"]";
    //知识域诊断
    public static String knowledgeDignose = "//div[@class='diagnostic-item-name']";
    //诊断搜索弹框
    public static String diagnoseSearch ="//div[@class = 'diagnostic-editor']";
    //诊断搜索弹框-分类条件-下拉框-全部value
    public static String diagnoseSearchSelectAll ="//div[@class = \"el-scrollbar\"]//span[contains(text(),'全部')]/..";
    //诊断搜索弹框-分类条件下拉框点击
    public static String diagnoseSearchSelectClick ="//div[@class = 'diagnostic-editor']//input[@readonly]";

    //诊断搜索列表 高血压收藏按钮
    public static String ValueDescwrapSvg = "//div[contains(@class,'diagnostic-search-result')]//label[contains(@class,'el-checkbox')]//span[contains(@class,'valueDesc-wrap') and .='?disease']/../../../..//*[name() = 'svg']";
    //诊断搜索结果
    public static String diagnoseResult = "//div[contains(@class,'diagnostic-search-result')]//label[contains(@class,'el-checkbox')]//span[contains(@class,'valueDesc-wrap') and .='?disease']";
    //诊疗路径收藏
    public static String diagnosticCollection = "//div[@class='el-popover el-popper collection-popover' and contains(.,'收藏')]//span[@class='el-checkbox__label' and .='诊疗路径']";
    //诊疗路径收藏确定按钮
    public static String diagnosticCollectionCommitBtm = "//div[@class='el-popover el-popper collection-popover' and contains(.,'收藏类型：')]//button[.='确定']";
    //诊疗路径收藏关闭按钮
    public static String diagnosticCollectionCancelBtm = "//div[@class='el-popover el-popper collection-popover' and contains(.,'收藏类型：')]//button[.='取消']";
    //病历模板输入文本框
    public static String emredittext = "//div[@class='emr']//span[@wn-type='text']//span[@contenteditable='true']";
    //病历模板-体格检查按钮
    public static String humanbtn = "//div[@class='emr']//div[@class='editor-paragraph' and contains(.,'体格检查')]//span[@class='el-tooltip editor-paragraph__title--icon human']";
    //体格检查列表所有可点击btn
    public static String humanbtnIstrue = "//div[@class='emr-diagram']//div[contains(@class,'unit-tree__body')]//button[contains(@draggable,'true')]";
    //体格检查-腰围
    public static String girth = "//div[@class='emr']//div[contains(@class,'editor-paragraph')]//span[contains(.,'腰围')]";
    //体格检查-关闭
    public static String humanclosebtn = "//div[@class='emr-diagram']//i[contains(@class,'el-icon-close')]";
    //病历模板-段落
    public static String recordsParagraphs = "//div[@class='emr']//div[contains(@class,'editor-paragraph')]";
    //病历模板输入选择框
    public static String emreditSelect = "//div[@class='emr']//span[@wn-type='select']//span[@contenteditable='true']/span";
    //病历模板校验门诊诊断
    public static String emrEditorTitle = "//div[@class='emr']//span[@class='editor-paragraph__title'and contains(.,'门诊诊断')]";
    //病历签署主诉段落未填提示框
    public static String emrError = "//div[@role='alert' and contains(@class,'el-notification emr-sign-notification right')]//span[.='主诉']";
    //病历记录
    public static String emrrecord = "//div[@class='emr__editor__menu']//i[@class='record']";
    //病历-历史就诊
    public static String emrrecordhistory = "//div[@class='emr__menu']//div[@class='assisant__record__tab--btn' and contains(.,'历史就诊')]";
    //病历书写助手
    public static String emrwritingAssistant = "//div[@class='emr__editor__menu']//i[@class='assisant']";
    //病历-医嘱
    public static String emrrecordMedicAladvice = "//div[@class='emr__menu']//li[contains(.,'医嘱')]";
    //诊疗路径-主诊断
    public static String diagnosticItemName = "//div[@class='diagnostic-item-wrap']//span[@class='el-checkbox__label']//div[@class='diagnostic-item-name']";
    //病历-门诊诊断
    public static String diagnosisOfRecords = "//div[@class='emr']//div[@class='editor-paragraph' and contains(.,'门诊诊断')]//span[@contenteditable='true']";
    //病历-提示内容关闭
    public static String emrSignClose = "//div[@role='alert' and contains(@class,'el-notification emr-sign-notification right')]//div[@class='el-notification__closeBtn el-icon-close']";
    //病历-书写助手默认第一条历史就诊
    public static String assisantRecordFirst = "//div[@class='assisant__record__history']//div[@class='assisant__record__content__left--item item__active']";
    //病历-书写助手 历史就诊病历段落
    public static String assisantRecordParagraph = "//div[@class='assisant__record__content__right']//div[contains(@class,'editor-paragraph')]";
    //病历-书写助手 历史就诊导入
    public static String historyRecordEditorBtn = "//div[@class='editor__header']//button[.='导入']";
    //病历-书写助手-医嘱类别下拉框
    public static String MedicAladviceForSelect = "//div[@class='assisant-advice__tab__select']";
    //病历-书写助手-医嘱类别下拉列表
    public static String MedicAladviceForSelectList = "//div[@class='el-select-dropdown el-popper advice-select']//li";
    //病历-书写助手-医嘱类别下拉按钮
    public static String MedicAladviceForSelectBtn = "//div[@class='assisant-advice__tab__select']//span[@class='el-input__suffix-inner']";
    //病历-书写助手-历史医嘱列表
    public static String historyMedicalAdvice = "//div[@class='history-list']";
    //病历-书写助手-历史医嘱插入按钮
    public static String historyBtn = "//div[@class='assisant-advice__tab__btn']//button[.='插入']";
    //病历-书写助手-历史医嘱-checkbox中被选中的选项
    public static String historyEmrCheckboxIsChecked = "//div[@class='record-editor']//span[@class='el-checkbox__input is-checked']";
    /**
     * Monkey测试xpath
     */
    //推荐区域基础路径
    public static String recommendRootPath = "//*[@id='recommendation']//*";
    //处置区域基础路径
    public static String disposalRootPath = "//*[@id='disposal']//*";
    //病例区域基础路径
    public static String emrRootPath = "//*[@id='medical_record']//div[@class='emr']/div[@class!='emr-diagram' or not(@class)]//*";

}
