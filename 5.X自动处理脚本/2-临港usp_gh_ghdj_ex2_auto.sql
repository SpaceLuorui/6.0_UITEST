create proc usp_gh_ghdj_ex2_auto
	@wkdz varchar(32),
	@jszt smallint,
	@ghbz smallint,
	@ghlb smallint,
	@czksfbz  int,    
	@cfzbz smallint,
	@patid ut_xh12,
	@czyh ut_czyh,
  	@ksdm ut_ksdm,
  	@ysdm ut_czyh,
	@ghksdm ut_ksdm,
  	@sjh ut_sjh = null,
	@lybz smallint = 0,
	@yyxh ut_xh12 = null,
	@zhbz ut_zhbz = null,
	@zddm ut_zddm = null,
	@zxlsh ut_lsh = null,
	@jslsh ut_lsh = null,
	@xmlb ut_dm2 = null,
	@qfdnzhzfje numeric(12,2) = null,
	@qflnzhzfje numeric(12,2) = null,
	@qfxjzfje numeric(12,2) = null,
	@tclnzhzfje numeric(12,2) = null,
	@tcxjzfje numeric(12,2) = null,
	@tczfje numeric(12,2) = null,
	@fjlnzhzfje numeric(12,2) = null,
	@fjxjzfje numeric(12,2) = null,
	@dffjzfje numeric(12,2) = null,
	@dnzhye numeric(12,2) = null,
	@lnzhye numeric(12,2) = null,
	@jsrq ut_rq16 = '',
	@qkbz smallint = 0
	,@ylcardno ut_cardno=''
	,@ylkje ut_money=0
	,@ylkysje ut_money=0
	,@ylksqxh ut_lsh=''
	,@ylkzxlsh ut_lsh=''
	,@ylkyssqxh ut_lsh=''
	,@ylkyszxlsh ut_lsh=''
	,@yslx int=0
	,@cardxh ut_xh12=0			--add by chenwei 2003.12.06
	,@cardje ut_money=0			--同上
	,@zph varchar(32) = ''
	,@zpje numeric(12,2) = 0
	,@yyrq ut_rq16=''
	,@bdyhkje ut_money = 0
	,@bdyhklsh ut_lsh = ''
	,@zlje	ut_money=0
	,@zzdjh varchar(100) = ''
	,@hcsjh ut_sjh =''  --自费转医保缴费用到,指的是红冲记录的sjh
as --集421758 2018-09-14 14:30:37 4.0标准版
/**********
[版本号]4.0.0.0.0
[创建时间]2004.10.25
[作者]王奕
[版权] Copyright ? 2004-2008上海金仕达-卫宁软件股份有限公司[描述]挂号登记
[功能说明]
	挂号登记功能(只用于ghbz=2)
[参数说明]
	@wkdz varchar(32),	网卡地址
	@jszt smallint,		结束状态	1=创建表，2=插入，3=递交
	@ghbz smallint,		挂号标志: 0=预算，1=递交(请求1), 2=正式递交(请求2)
	@ghlb smallint,		挂号类别：0=普通，1=急诊，2=专家，3=点名专家，4=特殊挂号，5=义诊，6=外宾挂号
	@czksfbz  int    充值卡收费标志， 0 :不从充值卡收费  ，1 从充值卡收费 add by szj
	@cfzbz smallint,	初诊标志：0=初诊，1=复诊
	@patid ut_xh12,		病人唯一标识
	@czyh ut_czyh,		操作员号
  	@ksdm ut_ksdm,		科室代码
  	@ysdm ut_czyh,		专家代码
	@ghksdm ut_ksdm,	挂号科室代码
 	@sjh ut_sjh = null,	结算库收据号
	@lybz smallint = 0,	挂号来源0=普通，1=预约 2=自助机
	@yyxh ut_xh12 = null,	预约序号
	@zhbz ut_zhbz = null,	账户标志
	@zddm ut_zddm = null,	诊断代码
	@zxlsh ut_lsh = null,	中心流水号
	@jslsh ut_lsh = null,	计算流水号
	@xmlb ut_dm2 = null,	大病项目
	@qfdnzhzfje numeric(12,2) = null, 	起付段当年账户支付
	@qflnzhzfje numeric(12,2) = null,	起付段历年帐户支付
	@qfxjzfje numeric(12,2) = null,		起付段现金支付
	@tclnzhzfje numeric(12,2) = null,	统筹段历年帐户支付
	@tcxjzfje numeric(12,2) = null,		统筹段现金支付
	@tczfje numeric(12,2) = null,		统筹段统筹支付
	@fjlnzhzfje numeric(12,2) = null,	附加段历年帐户支付
	@fjxjzfje numeric(12,2) = null,		附加段现金支付
	@dffjzfje numeric(12,2) = null,		附加段地方附加支付
	@dnzhye numeric(12,2) = null,		当年账户余额
	@lnzhye numeric(12,2) = null,		历年账户余额
	@jsrq ut_rq16 = ''					结算日期
	@qkbz smallint = 0					欠款标志0：正常，2：欠费
--mit add 2003-05-05 ,,银联卡 
	,@ylcardno ut_cardno=''		银联卡卡号
	,@ylkje ut_money=0		银联卡金额
	,@ylkysje ut_money=0		银联卡预授金额
	,@ylksqxh ut_lsh=’’		银联卡申请序号
	,@ylkzxlsh ut_lsh=’’		银联卡中心流水号
	,@ylkyssqxh ut_lsh=’’		银联卡预授申请序号
	,@ylkyszslsh ut_lsh=’’		银联卡预授中心流水号
	,@yslx int=0			银联卡预授类型0:冻结,1:消费
	,@cardxh ut_xh12=0			--add by chenwei 2003.12.06
	,@cardje ut_money=0			--同上
	,@zph varchar(32) = null	支票号
	,@zpje numeric(12,2) = null	支票金额
	,@zlje	ut_money=0		找零金额

[返回值]
[结果集、排序]
[调用的sp]
[调用实例]
 exec usp_gh_ghdj_ex2 "0013D35E10F4",3,2,2,1,1,2864.0,"00","3207","w859","","20070424000027",0,0,"","","","","",0,0,0,0,0,0,0,0,0,0,0,@zlje=0.00,@zzdjh=""--【Ztsql】
 
[修改记录]
	20030912 tony	医保四期修改：
		1。@ghbz=0时新增传出字段：挂号费，诊疗费，预交金余额
		2。伤残病人挂号费不付
		3。门诊预交金病人从账户里扣钱
		4。外宾挂号挂科室时诊疗费按科室收取
	20031119 mit 增加返回押金余额和冻结金额
	2003.11.8 tony 医保病人使用充值卡时更新在usp_gh_ghdj_ex1中SF_BRJSK欠款标志和欠款金额，本存储过程不再处理
	2003.12.24 cherry 添加代币卡帐户处理
modify by szj	 2004.02.18 充值卡需要提供密
	码才可以收费添加了@czksfbz 参数。控制是否从充值卡上扣钱
	20060524 ozb 修改挂号号序重复的问题，使用脏读 避免事务中的聚合函数引起的问题 使用max替代count避免跳号后引起的号序重复，
		注意不能完全避免,只能降低发生的概率，要完全避免需要做较大的修改。错误重现方法，修改垓存储过程，在commit之前插入delay语句,同时打开两个程序进行挂号就可以重现
	20060622 ozb 增加保存找零金额
	20070206 ozb 预约挂号的号序按预约的先后顺序排
	20070424 wfy 用存储过程usp_gh_getghhx得到当前号序
	20071111 ozb 新发票打印模式下，是否打印发票通过存储过程usp_gh_getfpprintflag获得  
	20071203 ozb 新模式下不在该存储过程中走发票，而是在发票打印时才走发票
    select * from YY_CONFIG where id='1124'
    update YY_CONFIG set config='是' where id='1124'
**********/
set nocount on

declare	@now ut_rq16,		--当前时间
		@ybdm ut_ybdm,		--医保代码
		@zfbz smallint,		--比例标志
		@rowcount int,
		@error int,
		@zje ut_money,		--总金额
		@zfyje ut_money,	--自费金额
		@yhje ut_money,		--优惠金额
		@ybje ut_money,		--可用于医保计算的金额
		@pzlx ut_dm2,		--凭证类型
		@sfje ut_money,		--实收金额
		@sfje1 ut_money,	--实收金额(包含自费金额)
		@errmsg varchar(100),
		@srbz char(1),		--舍入标志
		@srje ut_money,		--舍入金额
		@sfje2 ut_money,	--舍入后的实收金额
		@xhtemp ut_xh12,
		@ksmc ut_mc32,		--科室名称
		@ysmc ut_mc64,		--医生姓名
		@xmzfbl float,		--项目自付比例
		@xmce ut_money,		--自付金额和大项自付金额汇总的差额
		@fph bigint,			--发票号
		@fpjxh ut_xh12,		--发票卷序号
		@print smallint,	--是否打印0打印，1不打,2在充值卡的时候已经打印,3新发票模式下打印
		@ghhx int,			--挂号号序
		@ghzdxh ut_xh12,	--挂号账单序号
		@brlx ut_dm2,		--病人类型
		@pzh ut_pzh,		--凭证号
		@qkbz1 smallint,	--欠款标志0：正常，1：记账，2：欠费 3：扣充值卡
		@zhje ut_money,		--账户金额
		@qkje ut_money,		--欠款金额（记账金额）
		@scybdm ut_ybdm,	--伤残病人医保代码
		@yjbz ut_bz,		--是否使用充值卡
		@yjye ut_money,		--预交金余额
		@ybldbz varchar(2),	--医保是否允许联动
		@ghf ut_money,		--挂号费
		@zlf ut_money,		--诊疗费
		@qrbz ut_bz,		--确认标志0无需确认，1未确认，2已确认
		@yjyebz varchar(2),	--充值卡余额不足是否允许继续收费
		@yjdybz varchar(2)	--充值卡挂号是否打印发票
		,@sjyjye ut_money	--实际押金余额
		,@sjdjje ut_money,	--实际冻结金额		--mit , 2oo3-11-19
		@cardbz ut_bz,	    	--是否使用代币卡
		@kdm ut_dm2,		--代币卡分类代码
		@kmc ut_mc16,		--代币卡名称
		@cardno ut_pzh,		--代币卡卡号
		@tcljbz ut_bz,		--统筹累计标志
		@tcljje ut_money,	--统筹累计金额
		@yyjlbz ut_bz       	--膏方预约纪录标志 (0不使用， 1使用 )
		,@qkje2 ut_money
		,@zhdcsx ut_money 	--账户单次上线
		,@gyfpbz ut_bz		--公用发票
		,@gbje ut_money		--干保金额		
		,@ybzfje ut_money   --医保自负金额
		,@gbbz ut_bz   
		,@ghysdm  ut_czyh
		,@jmje	ut_money	--诊查费减免金额
		,@hxfs	ut_bz		--号序方式 非预约病人的挂号号序是否从预约病人数+1开始 1 是 0 否 add by ozb 20070131
		,@maxyys	int	--预约数
		,@tcljybdm varchar(500)  --统筹累计医保集合
		,@nconfigdyms	ut_bz	--0 旧打印模式 1 新打印模式
		,@ntry int	--重试计数器
		,@delaytime datetime
		,@yyxh1  ut_xh12	--预约序号
		,@djje	ut_money	--冻结金额
        ,@ghjzsj varchar(8)
        --,@fsdgh  ut_bz      --分时间段挂号标志1是0否
        ,@pbmxxh ut_xh12
        ,@ghrq   ut_rq16
        ,@hxlx int
        ,@hxfs_new    smallint  	/*号序方式	0 占号方式，即预约了几号挂号就是几号（中医）	
									1 预约号和现场号分离计算，即假设预约号为1 3 5 7 9。。。则预约病人只能挂号预约号上，现场病人只能挂2 4 6 8等号
									2 预约号和现场病人号序混合算,即现场和预约病人都是先来先得
									3 老预约和新预约混用，老预约负责现场号，新预约负责预约号（大华模式）
						*/	
		
declare @jzxh ut_xh12,
		@czym ut_mc64,
		@yydj ut_bz,
        @fpbz ut_bz,		---0打印1不打印
        @config0220 ut_bz	--1按工号和科室代码领发票模式0传统模式
        ,@fpdybz	ut_bz
        ,@jhxh ut_xh12 --加号序号
        ,@fpdm varchar(16)
        ,@config1457 char(2)
        ,@config1524 char(2)
		,@config1582 char(4)
		,@yjxh       varchar(20)
		,@linksvr    varchar(100) --链接服务器
		,@sql        varchar(2000)

select @czym=name from czryk where id=@czyh
select @yydj=c.yydj from YY_KSBMK k(nolock),YY_JBCONFIG c(nolock) 
            where k.id= @ksdm and k.yydm=c.id 
select @yydj = isnull(@yydj,1)

select @hxfs_new = -1
select @hxfs_new = config from YY_CONFIG (nolock) where id = '1235'
if @hxfs_new is null
    select @hxfs_new = 0

declare @iskmgh ut_bz
select @iskmgh= case  when config= '是' then 1 else 0 end  from YY_CONFIG (nolock) where id='1319'   
select @iskmgh=ISNULL(@iskmgh,0)

if (select config from YY_CONFIG (nolock) where id='0220')='是'      
	select @config0220=1      
else      
	select @config0220=0   
if exists (select 1 from YY_CONFIG(nolock) where id='1457' and config='否')
    select @config1457='否'
else
    select @config1457='是'
   
if exists (select 1 from YY_CONFIG(nolock) where id='1524' and config='否')
    select @config1524='否'
else
    select @config1524='是'  

if exists (select 1 from YY_CONFIG(nolock) where id='1582' and config='是')
    select @config1582='是'
else
    select @config1582='否'  
	 
select @now=convert(char(8),getdate(),112)+convert(char(8),getdate(),8),
	@zje=0, @zfyje=0, @yhje=0, @ybje=0,
	@sfje=0, @sfje1=0, @srje=0, @sfje2=0, 
	@xmzfbl=0, @xmce=0, @print=0, @ghhx=0,
	@qkbz1=0, @qkje=0, @yjbz=0, @yjye=0, @ghf=0, @zlf=0, @qrbz=0
	,@sjyjye=0,@sjdjje=0,@cardbz=0,@kdm='',@kmc='',@cardno='',@tcljbz=0, @tcljje=0, @gyfpbz=0, @gbje=0
	,@ybzfje = 0,@gbbz = '0',@jmje=0,@hxfs=0,@maxyys=0,@fpbz =0,@ghjzsj='',@fpdybz=0,@fpdm=''

select @ghhx=ghhx from GH_GHZDK where jssjh=@sjh
--add by ozb 2007-11-08 打印模式
if exists(select 1 from YY_CONFIG(nolock) where id='1117' and config='是')
	select @nconfigdyms=1
else
	select @nconfigdyms=0
--是否使用代币卡结算
if (select config from YY_CONFIG (nolock) where id='1067')='否'
	select   @cardbz=0
else
 	select   @cardbz=1

if (select config from YY_CONFIG (nolock) where id='1086')='是'
	select @yyjlbz = 1
else
	select @yyjlbz = 0

if (select config from YY_CONFIG (nolock) where id='2135')='是'
	select @gyfpbz = 1
else
	select @gyfpbz = 0

if (select config from YY_CONFIG (nolock) where id='1128')='是'
	select @hxfs = 1
else
	select @hxfs = 0
--if (select config from YY_CONFIG (nolock) where id='1171')='是'
--    select @fsdgh=1
--else
--    select @fsdgh=0


if @qkbz=2 --and (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
begin
	if  exists (select 1 from YY_CONFIG where id='2538' and config='是' )
	begin
		if exists (select 1 from YY_CONFIG where id='1657' and config='是' )
		begin
			if exists (select 1 from SF_BRXXK_FZ where patid=@patid and lstdbz=1 )
			begin
				if not exists (select 1 from SF_BRXXK_FZ where patid=@patid and @now between lstdkssj and lstdjssj)
				begin
					select "F","病人结算时间不在欠费结算有效期内！"
					return
				end
			end	
			else
			begin	
				if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
				begin
					select "F","该患者不允许欠费！"
					return
				end
			end	
		end
		else
		begin
			if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
			begin
				select "F","该患者不允许欠费！"
				return
			end
		end
	end
	else
	if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
	begin
		select "F","该患者不允许欠费！"
		return
	end
end



if @ghbz=2 --正式递交
begin
	select @ybdm=a.ybdm, @qkbz1=qkbz, @qkje=qkje, @zje=zje, @sfje2=zfje, @zfyje=zfyje, @qrbz=qrbz, 
		@tcljbz=tcljbz, @tcljje=zje-zfyje, @pzlx=b.pzlx,@zhdcsx = b.zhdcsx, @gbje=gbje
		,@gbbz = a.gbbz,@ybzfje = (a.zfje - a.srje-a.zfyje),@jmje=jmje,@fpdybz=fpdybz
		from SF_BRJSK a(nolock),YY_YBFLK b(nolock) where sjh=@sjh and a.ybdm=b.ybdm
	if @@rowcount=0
	begin
		select "F","该挂号结算记录不存在！"
		return
	end

	--使用社区接口不打印挂号发票，在收费处中补打处理 zwj 2008-10-13
	if (select config from YY_CONFIG (nolock) where id='1156')='是'
		select @qkbz1=3,@qkje=0,@print=1,@fpdybz=1

	--欠款病人的记账金额要打印出来
	if @qkbz1 = 1
		select @qkje2 = isnull(je,0) from SF_JEMXK(nolock) where jssjh = @sjh and lx = '01' 
	else 
		select @qkje2 = @qkje
	if @qkbz=2 --and (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'   --是否欠款--wudong
	begin
		if exists (select 1 from YY_CONFIG where id='1657' and config='是' )
		begin
			if exists (select 1 from SF_BRXXK_FZ where patid=@patid and lstdbz=1 )
			begin
				if not exists (select 1 from SF_BRXXK_FZ where patid=@patid and @now between lstdkssj and lstdjssj)
				begin
					select "F","病人结算时间不在欠费结算有效期内！"
					return
				end
			end	
		end
		else
		if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
		begin
			select "F","该患者不允许欠费！"
			return
		end
	end

	select @jzxh=case when isnull(zcbz,0)=0 then xh else isnull(zkxh,xh) end ,
           @fpbz =isnull(fpbz,0)
		from YY_JZBRK(nolock) where patid=@patid and jlzt=0
	if @@rowcount=0
    begin
		select @yjye=0
        select @fpbz=0
    end
	else
	begin
		select @yjye=yjye from YY_JZBRK(nolock) where xh=@jzxh and jlzt=0
		if @@rowcount=0
			select @yjye=0
		else
			select @yjbz=1
	end	
    if (select config from YY_CONFIG (nolock) where id='0133')='否' and @fpbz ='1'
		select @print=2
	
	if @qkbz=2
		select @qkbz1=2, @qkje=@sfje2

	select xh, ksdm, ksmc, ysdm, ysmc, ghlb, lybz, yyxh, blh, patid, hzxm, czrq, czyh,ghysdm,ghksdm,pbmxxh,ghrq
		into #ghzd
		from GH_GHZDK where jssjh=@sjh
	if @@rowcount=0
	begin
		select "F","挂号信息不存在！"
		return		
	end

	if (select config from YY_CONFIG (nolock) where id='1006')='否' --挂号单据打印
		select @print=1

	if not exists(select 1 from #ghzd where ghlb not in (5,8)) 
		and (select config from YY_CONFIG (nolock) where id='1031')='否' --义诊是否打印发票
		select @print=1

	--if @qrbz in (1,3)
	if @fpdybz = 1
		select @print=1

   --add by gxf 2007-6-7 
     declare @bdyfplb varchar(128)
	 select @bdyfplb = config from YY_CONFIG where id = '1141'
	 if @sfje2 = 0 
			if charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+@bdyfplb+',')>0 
				select @print=1,@fpdybz=1--@qrbz=1
	--add by gxf 2007-6-7
	
	--add by liuchun 需求185446  20131126 begin
	--declare @config1314 varchar(10)
	--select @config1314 = config from YY_CONFIG where id = '1314'
	--if (select config from YY_CONFIG (nolock) where id='1006')='是' and @print in (0,3) --挂号单据打印
	--begin
	--	if @config1314 = '是'
	--	begin
	--		select @print = 1
	--	end
	--end
	--add by liuchun 需求185446  20131126 end
	--add by yhw for 4117 老打印模式下不打印发票的医保代码  20140916 begin
    declare @bdyfpybdm varchar(128)
	select @bdyfpybdm = config from YY_CONFIG where id = '1362'	
	if @nconfigdyms=0  
	begin
		if charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+@bdyfpybdm+',')>0
			select @print = 1
	end
	--add by yhw for 4117   20140916 end 
	if @config1457='否' and @cardxh<>0 and @cardje<>0			--moved by YC for bug78577
	begin
	    select @print=1
	end 
		
	--add by ozb begin 2007-11-11 根据存储过程返回的打印标志决定是否打印
	if @nconfigdyms=1
	begin
        if exists(select 1 from YY_CONFIG where id='1124' and config='是')
			if @lybz = 2  --2010-11-05 by sdb 自助机才不打发票，窗口应该是打印的
            begin
				select @print=1		--2007-12-03 del by ozb新模式下，不在这里走发票，而在打印发票时走发票
									--2010-07-06 modify by gwh 自助挂号机起动 不打印发票
            end
            else
                select @print = 3
        else
            select @print=3
             
		/*
		exec usp_gh_getfpprintflag @sjh,0,@errmsg output
		if left(@errmsg,1)='F'
		begin 
			select 'F',substring(@errmsg,2,49)
			return
		end	
		else
			select @print= case cast(substring(@errmsg,2,49) as int) when 0 then 1 else 0 end
		*/
	end
	--add by ozb end 2007-11-11 根据存储过程返回的打印标志决定是否打印
	/*if @config1457='否' and @cardxh<>0 and @cardje<>0			--modified by YC for bug78577
	begin
	    select @print=1
	end */
	if @print=0
	begin
		if @gyfpbz=0
		begin
			if @config0220=1
			select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=0 and ksdm=@ghksdm
			else
			select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=0
			if @@rowcount=0
			begin
				select "F","没有可用发票！"
				return
			end
		end
		else
		begin
			select @fph=fpxz, @fpjxh=0,@fpdm=isnull(fpdm,'') from SF_GYFPK(nolock) where czyh=@czyh and xtlb=0
			if @@rowcount=0
			begin
				select "F","没有可用发票！"
				return
			end
		end
	end
	else 
		select @fph=null, @fpjxh=null,@fpdm=''

	begin tran

	if exists(select 1 from #ghzd where lybz=1)
	begin
		update GH_GHYYK set jlzt=1 
		where exists(select 1 from #ghzd a where a.lybz=1 and GH_GHYYK.xh=a.yyxh)

		if @@error<>0
		begin
			select "F","更新挂号预约信息出错！"
			rollback tran
			return
		end
	end
	
	/*计算挂号费用*/
	declare cs_ghzd cursor for 
	select xh, ksdm, ysdm, ghlb, yyxh,pbmxxh,ghrq from #ghzd
	for read only

	open cs_ghzd
	fetch cs_ghzd into @ghzdxh, @ksdm, @ysdm, @ghlb, @yyxh,@pbmxxh,@ghrq
	while @@fetch_status=0
	begin
		--mod by ozb 20060524 begin 减少号序重复的可能性
		--del
		--if @ghlb in (2,3,6,9) --专家类
		--	select @ghhx=isnull(count(ghhx),0) from GH_GHZDK
		--	where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6,9) and isnull(gfbz,0)<>1
		--else if @ghlb<>9
		--	select @ghhx=isnull(count(ghhx),0) from GH_GHZDK
		--	where ghrq like substring(@now,1,8)+'%' and ghksdm=@ksdm and jlzt in (0,1) and ghlb not in (2,3,6,9)
		--add
--modify by wfy 2007-04-24 用存储过程取ghhx
-- 		if @ghlb in (2,3,6,9) --专家类
-- 		begin
-- 			if @hxfs=0
-- 			begin
-- 				select @ghhx=isnull(max(ghhx),0) from GH_GHZDK (nolock)
-- 				where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6,9) and isnull(gfbz,0)<>1
-- 			end
-- 			else
-- 			begin
-- 				if @ghlb=9
-- 				begin
-- 					select @ghhx=count(a.xh) from GH_GHYYK a
-- 					where  a.yyrq=substring(@now,1,8) and a.ysdm=@ysdm and a.xh<@yyxh
-- 				end
-- 				else
-- 				begin
-- 					select @maxyys=count(a.xh) from GH_GHYYK a
-- 						where  a.yyrq=substring(@now,1,8) and a.ysdm=@ysdm
-- 					select @ghhx=isnull(max(ghhx),@maxyys) from GH_GHZDK (nolock)
-- 					where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6) and isnull(gfbz,0)<>1
-- 				end
-- 				
-- 			end
-- 		end
-- 		else if @ghlb<>9
-- 				select @ghhx=isnull(max(ghhx),0) from GH_GHZDK (nolock)
-- 				where ghrq like substring(@now,1,8)+'%' and ghksdm=@ksdm and jlzt in (0,1) and ghlb not in (2,3,6,9)
		
		--mod by ozb 20060524 end 减少号序重复的可能性 
		select @errmsg='F',@ntry=0,@ghjzsj=''
		if @hcsjh=''  --自费转医保缴费时,不重新生成挂号号序
		begin
		  if @ghhx=0
		  begin
			  while left(@errmsg,1)='F' and @ntry<3	
			  begin
				  select @errmsg='',@ntry=@ntry+1
				  if @ntry>1 --ozb 延迟随机时间，最多0.1秒，有可能造成更严重的拥堵，有待实践证明，但是如果不延迟一个随机时间，则两个进程多次重试会毫无意义
				  begin
					  select @delaytime='00:00:00.0'+cast(cast(rand()*100 as int) as varchar(2))
					  waitfor delay @delaytime
					  if @@error<>0	--防止以上程序被修改后时间格式不正确
					  begin
						  rollback tran
						  deallocate cs_ghzd
						  select "F","计算号序时出错！"
						  return		
					  end
				  end        		
				if exists(select 1 from YY_CONFIG(nolock) where id='1373' and config = '是')  
				begin
					exec usp_pb_operateghhx 0,@pbmxxh,0,0,0,@yyxh,@ghhx output ,@errmsg output 
				end
				else
				begin     		
					exec usp_gh_getghhx @ghlb,@ksdm,@ysdm,0,@yyxh,@ghhx output,@errmsg output,@pbmxxh
				end
				  if @@error<>0 
				  begin
					  rollback tran
					  deallocate cs_ghzd
					  select "F","计算号序时出错！"
					  return		
				  end
			  end
			  if left(@errmsg,1)='F'
			  begin
				  rollback tran
				  deallocate cs_ghzd
				  select "F","计算号序时出错！"+substring(@errmsg,2,49)
				  return	
			  end
			  select @errmsg=''
		  end
		end
		--select @ghhx=isnull(@ghhx,0)+1

		if @ghlb = 9 and @yyjlbz = 1
		update GH_GHYYK
		set jlzt = 1
		where jlzt = 0 and xh =@yyxh
		if @@error<>0
		begin
			select "F","保存挂号预约信息出错！"
			rollback tran
			deallocate cs_ghzd
			return		
		end	
		if @pzlx in('10','11')
			select @ghrq=(case when @jsrq='' then @now else @jsrq end)
		--新版预约挂号使用aorigele 20100730
		--利用GH_GHZDK @pbmxxh 来控制
		--自费转医保缴费时,不重新生成挂号号序
		declare @pbkm_temp varchar(20)
		if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '是') and (@pbmxxh > 0) and (@hcsjh='') and (@iskmgh=0) and (@ghhx=0)
		begin 
			--s_yh  移到获取号序之后 for bug 76393
			--if (substring(@ghrq,1,8)<>substring(@now,1,8))   
			--begin    
			--	declare @zxrq_temp varchar(8),@kssj_temp varchar(8)     
			--	select @zxrq_temp=zxrq,@kssj_temp=kssj from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh  s_yh 			
			--	if substring(@now,1,8)<>@zxrq_temp       
			--	begin  
			--		select @ghrq=@zxrq_temp+@kssj_temp+':00'   
			--	end  
			--end	         
			select @hxlx = -1;
			select @pbkm_temp = a.pbkm from GH_YY_PBZB a(nolock),GH_YY_PBZBMX b(nolock) 
				where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh
			begin
				exec usp_gh_getNewghhx @pbmxxh,0,0,@ghhx output ,@errmsg output,@hxfs_new,@ghlb,@ghhx,@yyxh	 --新生成号序
			    
				if isnull(@ghhx,0) <=0
				begin
					select "F","获取挂号号序出错！"
					rollback tran
					deallocate cs_ghzd
					return	
				end; 
				if left(@errmsg,1)='F'
				begin
					select "F",@errmsg
					rollback tran
					deallocate cs_ghzd
					return	
				end
				if @hxfs_new = '1'
				begin
					if @ghlb = 9 
						select @hxlx = 1
					else 
						select @hxlx = 0
					
					select @pbmxxh = a.pbmxid from GH_YY_PBZBMX_HX  a(nolock),GH_YY_PBZBMX b(nolock)  
						where a.pbmxid=b.pbmxid and b.pbkm=@pbkm_temp and b.zxrq = substring(@now,1,8) 
							and hxlx = @hxlx and ghhx = @ghhx and a.jlzt <> 2 --alter by ylj 20110819 
					--select @pbmxxh 
				end
			end;
			--s_yh for bug 76393		
			if (substring(@ghrq,1,8)<>substring(@now,1,8))   
			begin    
				declare @zxrq_temp varchar(8),@kssj_temp varchar(8)     
				--select @zxrq_temp=zxrq,@kssj_temp=kssj from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh  s_yh 
				select @zxrq_temp=zxrq from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh    
				select @kssj_temp=hxsjd from GH_YY_PBZBMX_HX(nolock) where pbmxid=@pbmxxh and ghhx = @ghhx 
				if substring(@now,1,8)<>@zxrq_temp       
				begin  
					select @ghrq=@zxrq_temp+@kssj_temp+':00'   
				end  
			end	   
		end;
		else if (@iskmgh=1) and  (@pbmxxh > 0) and (@hcsjh='') and (@ghhx=0)--yxc
		begin			 
			select @hxlx = -1;
			select @pbkm_temp = a.pbkm from GH_KM_PBZB a(nolock),GH_KM_PBZBMX b(nolock) 
				where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh
			begin
				exec usp_gh_getNewghhx_km @pbmxxh,0,0,@ghhx output ,@errmsg output,@hxfs_new,@ghlb,@ghhx,@yyxh,@updatehx=1	 --新生成号序
			    
				if isnull(@ghhx,0) <=0
				begin
					select "F","获取挂号号序出错！"
					rollback tran
					deallocate cs_ghzd
					return	
				end; 
				if @hxfs_new = '1'
				begin
					if @ghlb = 9 
						select @hxlx = 1
					else 
						select @hxlx = 0
					
					select @pbmxxh = a.pbmxid from GH_KM_PBZBMX_HX  a(nolock),GH_KM_PBZBMX b(nolock)  
						where a.pbmxid=b.pbmxid and b.pbkm=@pbkm_temp and b.zxrq = substring(@now,1,8) 
							and hxlx = @hxlx and ghhx = @ghhx and a.jlzt <> 2 --alter by ylj 20110819 
				end
			end;		
		end
		--add by aorigele
		if exists(select 1 from YY_CONFIG(nolock) where id='1373' and config = '是')  
		begin
			exec usp_pb_operateghhx 1,@pbmxxh,0,0,0,@yyxh,@ghhx output ,@errmsg output
			if substring(@errmsg,1,1)='F'
			begin
				select "F",@errmsg
				rollback tran
				deallocate cs_ghzd
				return
			end
			--更新预约库标志
			--if @ghlb in (9)
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1 where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","更新预约库标志失败，请核对预约信息GH_SH_GHYYK！"
					return
				end;
			end;
		end
		if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '是') and (@pbmxxh > 0) and (@iskmgh=0)
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx=@ghhx,
				czyh=@czyh,
				czrq=@now,
				gfbz = 0,
				--@ghlb smallint,		
				--挂号类别0=普通，1=急诊，2=专家，3=点名专家，4=特殊挂号，5=义诊，6=外宾挂号,7=免挂号费, 8=免费挂号
				ghjzsj=@ghjzsj
				where xh=@ghzdxh 
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","更新挂号账单信息出错！"
				return
			end
			
		    -- add by aorigele 20111025
		    if exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '否')
				and @hxfs_new =2  --场景5，其他场景没有分离出来
		    begin
				--更新规则：当前医生的所有排版中获取好序队列，然后更新指定号序的状态
				--获取当前好序对应的pbmxid					
				declare @pbmxidnew ut_xh12,@pbkm varchar(20),@bmkmlx ut_bz
				 select @pbmxidnew = -1;
				select @pbkm = a.pbkm ,@bmkmlx = a.pbkmlx 
				  from GH_YY_PBZB a(nolock) ,GH_YY_PBZBMX b(nolock)
				 where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh	
				 
				 								
				select top 1 @pbmxidnew = c.pbmxid
				     from GH_YY_PBZB a(nolock) ,GH_YY_PBZBMX b(nolock),GH_YY_PBZBMX_HX c(nolock)
					where a.pbzbid = b.pbzbid and b.pbmxid = c.pbmxid 
					  and a.pbkm = @pbkm
					  and a.pbkmlx = @bmkmlx
					  and b.zxrq = substring(@now,1,8) 
					  and c.ghhx = @ghhx	
				exec usp_gh_setyyghhx @pbmxidnew,@ghhx,-1,2,0,@errmsg output
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;						  				   			      	
		    end
		    else if exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '否')
				and @hxfs_new =4  --场景6，其他场景没有分离出来 长宁妇幼模式
			begin
				if @ghlb in (9) --普通号走公共版，所以不管
				begin
					select @pbmxxh = zjxh from GH_SH_GHYYK b(nolock)where b.xh = @yyxh
					--当前时间段内 标志一个号序 已用就可以
					update GH_YY_PBZBMX_HX 
					   set jlzt = 2
					 where pbmxid = @pbmxxh 
					   and ghhx in
					   ( select MIN(a.ghhx)
						  from GH_YY_PBZBMX_HX  a(nolock) ,GH_SH_GHYYK b(nolock)
						 where a.pbmxid = b.zjxh							   
						   and b.xh = @yyxh
						   --当前时间段内
						   and a.sjd = b.Child_sjd
						   --可用号序
						   and a.jlzt in (0,1)							   
					   )
					if @errmsg <> 'T'
					begin
						rollback tran
						deallocate cs_ghzd
						select "F",@errmsg
						return
					end;
					update GH_GHZDK 
					   set gfbz = (select top 1 a.pbkmlx from GH_YY_PBZB a(nolock),GH_YY_PBZBMX b(nolock) where 
					                   a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh	
					               )						
					 where xh=@ghzdxh 
					if @@error<>0
					begin
						rollback tran
						deallocate cs_ghzd
						select "F","更新挂号账单信息出错！"
						return
					end					   
				end 
			end
		    else if (exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '否')
				and @hxfs_new =3 ) --场景4，大华模式不走 其他模式走
			begin
				if @ghlb in (9) --普通号走公共版
				begin
					--只更新预约号序				
					select @hxlx = -1;
					select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
					--jlzt = 2 挂号已确认
					exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output
					if @errmsg <> 'T'
					begin
						rollback tran
						deallocate cs_ghzd
						select "F",@errmsg
						return
					end;
				end;
			end
			else if (exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '否')
				and @hxfs_new =5 ) --场景5,这种模式下增加挂号序号的传入，在走号时从新分配就诊号序
			begin
				select @hxlx = -1;
				select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
				exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;
			end
			else
		    begin
				select @hxlx = -1;
				select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
				--jlzt = 2 挂号已确认
				exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;
			end;			
			--更新预约库标志
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1
				where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","更新预约库标志失败，请核对预约信息GH_SH_GHYYK！"
					return
				end;
			end;
		end
		else if (@pbmxxh > 0) and (@iskmgh = 1)
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx= @ghhx ,
				czyh=@czyh,
				czrq=@now,			
				ghjzsj=@ghjzsj,
				fzghhx=case when isnull(fzghhx,'')='' then convert(char(4),@ghhx) else fzghhx end
				from GH_GHZDK  
				where @ghzdxh=xh
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","更新挂号账单信息出错！"
				return
			end	
			
			select @hxlx = -1;
			select @hxlx = hxlx from GH_KM_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
			--jlzt = 2 挂号已确认
			exec usp_gh_setyyghhx_km @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
			if @errmsg <> 'T'
			begin
				rollback tran
				deallocate cs_ghzd
				select "F",@errmsg
				return
			end;
			
			select @jhxh=a.xh from GH_KM_JHXXK a(nolock) where a.patid=@patid and a.pbmxid=@pbmxxh and 
			a.jlzt=0 and substring(a.jhrq,1,8)=substring(@now,1,8)
			if @@rowcount = 1
			begin
				update GH_KM_JHXXK set jlzt=1 where xh=@jhxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					select "F","更新GH_KM_JHXXK标志失败，请核对加号信息！"
					return
				end;								
			end
			--更新预约库标志
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1
				where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","更新预约库标志失败，请核对预约信息GH_SH_GHYYK！"
					return
				end;
			end;					
		end
		else --老模式
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx=(case when isnull(b.gfbz,0)=1 then b.yyxh else @ghhx end),
				czyh=@czyh,
				czrq=@now,
				gfbz = isnull(b.gfbz,0),
				ghjzsj=@ghjzsj
				from GH_GHZDK a left join GH_GHYYK b(nolock) on a.yyxh=b.xh
				where a.xh=@ghzdxh  --sql2012 yfq				
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","更新挂号账单信息出错！"
				return
			end
			if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '是') and (@hxfs_new=6) 
			begin
				exec usp_gh_setyyghhx -1,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;			
			end			
		end;
		
		--将fzghhx为空的更新为挂号号序
		update GH_GHZDK set 
			fzghhx=case when isnull(fzghhx,0)=0 then convert(char(4),ghhx) else fzghhx end
		where @ghzdxh=xh
		if @@error<>0
		begin
			rollback tran
			deallocate cs_ghzd
			select "F","更新挂号账单信息出错！"
			return
		end	
			
		--add by sdb 2011-04-25 自助挂号中是否自动判断名老专家存在初诊和复诊挂号费的差异(上海中医：一年内没看过该医生，算初诊) BEGIN
		if (select config from YY_CONFIG (nolock) where id = '1219') = '是'
		begin
			if exists (select 1 from GH_ZYY_ZJGHFSZ where id = @ysdm and jlzt = 0 and isnull(ghf,'') <> '')
			begin--如果是名专，必须把该条挂号信息插到GH_GHZDK_MZ里
				insert into GH_GHZDK_MZ(jssjh,patid,ghrq,ysdm,ysmc) 
				select jssjh,patid,ghrq,ysdm,ysmc from GH_GHZDK a where a.xh=@ghzdxh
						--and a.jssjh not in(select jssjh from GH_GHZDK_MZ)
				if @@error<>0
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","更新挂号账单信息出错！"
					return
				end
			end
		end		
		--add by sdb 2011-04-25 自助挂号中是否自动判断名老专家存在初诊和复诊挂号费的差异 END
		
		if @ghlb=1 
		begin
			insert into SF_JZDJK(ghxh,patid,cardno,sex,birth,ybdm,lxdz,dwbm,dwmc,ksdm)
			select b.xh,a.patid,a.cardno,a.sex,a.birth,a.ybdm,a.lxdz,a.dwbm,a.dwmc,b.ksdm
			from SF_BRXXK a(nolock),GH_GHZDK b(nolock) where a.patid=b.patid and b.xh=@ghzdxh
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","插入急诊登记库失败!"
				return
			end
		end
		fetch cs_ghzd into @ghzdxh, @ksdm, @ysdm, @ghlb, @yyxh,@pbmxxh,@ghrq
	end
	close cs_ghzd
	deallocate cs_ghzd
	/*****
	 @qkbz1=1 :1普通有账户的病人 2民生卡的临时账户概念,核心是没有余额,账户金额
				等于YY_YBFLK 中的zhdcsx 所设置的值
	******/ 
	update SF_BRXXK set zjrq=substring(@now,1,8), zhje=zhje-(case when @qkbz1=1 then @qkje2 else 0 end), ghbz=1,
		ylxm=(case when @pzlx='11' then @xmlb else ylxm end),
		ljje=ljje+(case when @tcljbz=1 then @tcljje else 0 end),
    gxrq=@now --add by yfq @20120531 
		where patid=@patid
	if @@error<>0
	begin
		select "F","更新病人信息出错！"
		rollback tran
		return
	end

	if @yjbz=1 and @qkbz1=3
	begin
		update YY_JZBRK set yjye=yjye-@qkje 
		,@sjyjye=yjye-@qkje,@sjdjje=djje		--mit ,2oo3-11-19 , add
		where xh=@jzxh and jlzt=0
		if @@error<>0
		begin
			select "F","更新记账病人库预交金余额出错！"
			rollback tran
			return
		end
		
		insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh)
		values(0,0,@jzxh,@czyh,@czym,@now,@qkje,0,@sjyjye,1,3,null,0,'',@sjh)
		if @@error<>0
		begin
			select 'F','插入YY_JZBRYJK记录时出错'
			rollback tran
			return
		end
	end

	select @zhje=zhje from SF_BRXXK where patid=@patid
	if @@error<>0
	begin
		select "F","更新账户金额出错！"
		rollback tran
		return
	end


	--代币卡病人处理 add by chenwei 2003.12.06
	if @cardbz = 1 
	begin
		if (@cardxh <> 0) or (@cardje <> 0)
		begin
			select @qkbz1 = '4', @qkje=@cardje
			
			update YY_CARDXXK set yjye=yjye-@cardje,zjrq=(case when @jsrq='' then @now else @jsrq end),
								  @sjyjye=yjye-@qkje,@sjdjje = @qkje
			where kxh=@cardxh and jlzt=0
			if @@error<>0
			begin
				select "F","更新代币卡病人帐户余额出错！"
				rollback tran
				return
			end		

			select @kdm=a.kdm,@kmc=b.kmc,@zhje=yjye,@cardno=cardno 
              		from YY_CARDXXK a(nolock),YY_CARDFLK b(nolock) 
			 where a.kxh=@cardxh and a.kdm=b.kdm
			
			insert into YY_CARDJEK(kxh,jssjh,yjjxh,kdm,czyh,czym,lrrq,zje,zhye,yhje,yhje_zje,yhje_mx,jlzt,xtbz,memo)
			values(@cardxh,@sjh,0,@kdm,@czyh,@czym,(case when @jsrq='' then @now else @jsrq end),@qkje,@zhje,@yhje,0,0,0,0,'')
			if @@error<>0
			begin
				rollback tran
				select "F","更新代币卡金额库出错！"
				return
			end

			select @sfje2=@sfje2+@qkje

		end
	end
	--民生卡处理账户金额问题,因为账户是临时的,不存在余额的概念,所以置为零
	if @ghlb=3  
	begin  
		update SF_BRXXK set zhje =0,gxrq=@now where patid=@patid  --add by yfq @20120531  
		select @zhje=0  
	end
	
	--l__zy add by bug279761 现金支付不合计到zpje中
	if @zph='1' 
	begin
	    select @zph='',@zpje=0
	end
	
	update SF_BRJSK set sfrq=(case when @jsrq='' then @now else @jsrq end),
		ybjszt=2,
		zxlsh=(case isnull(@zxlsh,'') when '' then zxlsh else @zxlsh end),
		fph=@fph,
		fpjxh=@fpjxh,
		pzh=(case when @qkbz1 = 4 then @cardno else pzh end),
		xjje=zfje-@zpje-@qkje-isnull(@ylkje,0)-@gbje-@bdyhkje, --modiby by chenwei 2003.12.22 
		qkbz=@qkbz1,
		qkje=@qkje,
		dnzhye=(case when @qkbz1=1 then @zhje when @qkbz1=4 then @sjyjye else dnzhye end)
		,ylkje=@ylkje
		,ylkysje=@ylkysje
		,ylksqxh=@ylksqxh
		,ylkzxlsh=@ylkzxlsh
		,ylcardno=@ylcardno	--mit ,, 2003-05-05 ,, 银联卡增加字段
		,qrrq=(case when @fpdybz=0 then @now else null end)	--tony 医保四期修改
		,qrczyh=(case when @fpdybz=0 then @czyh else null end)
		,zpje=isnull(@zpje,0)
		,zph=@zph
		,bdyhkje = @bdyhkje
		,bdyhklsh = @bdyhklsh
		,zlje=@zlje
		,lrrq=@now --优化流程报表专用
		,gxrq=@now --add by yfq @20120528
		--,qrbz=case when @qrbz<>qrbz then @qrbz else qrbz end
		,fpdybz=@fpdybz
		where sjh=@sjh
	if @@error<>0 or @@rowcount=0
	begin
		select "F","更新挂号结算信息出错！"
		rollback tran
		return
	end
	IF @nconfigdyms= 0--老发票模式下，发票打印信息更新
	begin
		if exists(select 1 from YY_CONFIG(nolock) where id = '1056' and config = '否')
			and exists(select 1 from SF_BRJSK(nolock) where sjh = @sjh and zfje-@zpje-@qkje-isnull(@ylkje,0)-@gbje-@bdyhkje=0
			and @qkje > 0 and @qkbz1=3)
			select @print=1
		if  @print = 0
		begin
			update SF_BRJSK SET fpdybz = 0,fpdyczyh=@czyh,fpdyrq=@now where sjh = @sjh
			if @@error<>0 or @@rowcount=0
			begin
				select "F","更新挂号发票信息出错！"
				rollback tran
				return
			end
		end
		else
		begin
			update SF_BRJSK SET fpdybz = 1,fpdyczyh=null,fpdyrq=null,fph=null,fpjxh=null,@fph=null,@fpjxh=null where sjh = @sjh
			if @@error<>0 or @@rowcount=0
			begin
				select "F","更新挂号发票信息出错！"
				rollback tran
				return
			end
		end
	end
	if exists (select 1 from sysobjects where name='SF_BRJSK_FZ' and xtype='U')
	BEGIN		
		update  SF_BRJSK_FZ set fph=@fph,fpjxh=@fpjxh,fpdm=@fpdm where sjh=@sjh		 
		if @@error<>0
		begin
			select "F","保存结算账单出错！"
			rollback tran
			return		
		end	
	END	
	--mit ,, 2003-05-05 ,, 如果有预授则插入YY_YLJYJLK
	if @ylkysje>0
	begin
		insert into YY_YLJYJLK(ylcardno,patid,ylkje,jyrq,ylksqxh,ylkzxlsh,jlzt,qxxh,sjh)
		values(@ylcardno,@patid,@ylkysje,@jsrq,@ylkyssqxh,@ylkyszxlsh,@yslx,null,@sjh)
	end
	if @@error<>0
	begin
		select "F","更新银联卡预授信息出错！"
		rollback tran
		return
	end

	if @print=0
	begin
		exec usp_yy_gxzsj 0, @czyh, @errmsg output,@gyfpbz,@fpjxh,@ghksdm
		if @errmsg like 'F%'
		begin
			select "F",substring(@errmsg,2,49)
			rollback tran
			return
		end
	end
	--Wxp20070317 对二三级医院转诊处理，诊查费自费和诊查费优惠50%
	--条件： @zzdjh <> '',是二三级医院，普通挂号费，范围限制：挂号费和诊疗费
	if (@zzdjh <> '' and @yydj > 1 and @ghlb in (0,1)) 
	begin
		update GH_ZZBRJLK set jszt = 2  where jssjh = @sjh 
		if @@error<>0
		begin
			select "F","更新转诊单记录(GH_ZZBRJLK)状态=2时出错！"
			rollback tran
			return		
		end
		update GH_ZZBRDJK set jlzt = 1  where zzdjh = @zzdjh and jlzt = 0 
		if @@error<>0
		begin
			select "F","更新转诊单(GH_ZZBRDJK)状态=1时出错！"
			rollback tran
			return		
		end
		select @jmje = jmghf+jmzlf from GH_ZZBRJLK where jssjh = @sjh
	end	
	select @tcljybdm = config from YY_CONFIG where id = '0115'
	if charindex('"'+rtrim(@ybdm)+'"',@tcljybdm) > 0
	begin
		declare @mzpatid ut_xh12,
				@m_cardno ut_cardno,
				@tcljje2 ut_money,
				@cardtype ut_dm2
		select @m_cardno = cardno,@cardtype = cardtype,
				@tcljje2=zje-(zfje-srje)-yhje-isnull(tsyhje,0)
				from SF_BRJSK nolock where sjh = @sjh
		select @mzpatid=mzpatid from YY_BRLJXXK nolock where cardno = @m_cardno and cardtype = @cardtype
		if @@rowcount <> 0
		begin
			exec usp_zy_tcljjegl @m_cardno,@mzpatid,@tcljje2,0,0,0,0,@czyh
			if @@error <> 0 
			begin
				rollback tran
				select "F","更新YY_BRLJXXK的统筹累计金额出错！"
				return
			end
		end
	end
	IF (SELECT ISNULL(config,"否") FROM YY_CONFIG WHERE id="1137")="是"
	BEGIN
		EXECUTE usp_gh_ghdpjk @sjh, 0
		IF @@error<>0
		BEGIN
			ROLLBACK
			SELECT "F","取挂号数据错误！"
			RETURN
		END
	END

	--zyh 20080229 解冻预约挂号病人帐户 sunyu 20080307 把该段代码从usp_gh_ghdj移到ex2中，表示挂号成功后再解冻
	select @yyxh1=yyxh from #ghzd
	if @yyxh1> 0 
	begin
		select @djje=djje from GH_GHYYK (nolock) where xh=@yyxh1 and djbz=1
		select @djje=isnull(@djje,0)
		if @djje > 0 
		begin
			if exists(select 1 from YY_JZBRK where patid=@patid and djje>@djje)--gxs 20131224 add
			begin
				update YY_JZBRK set djje=djje - @djje where patid=@patid
			end
			else
			begin
				update YY_JZBRK set djje=0 where patid=@patid
			end
			if @@error<>0
			begin
				select "F","预约病人解冻帐户时出错！"
				rollback tran
				return		
			end

			update GH_GHYYK set djje=0, djbz=0 where xh=@yyxh1
			if @@error<>0
			begin
				select "F","预约病人解冻帐户时出错！"
				rollback tran
				return		
			end
		end
	end
  /*-----自费转医保时,更新记录状态 begin-----*/
	if @hcsjh<>''
	begin
		declare @ysjh ut_sjh, --原收费记录sjh
			@ypatid ut_xh12, --原收费记录patid
			@yjzxh ut_xh12, --原收费记录对应的jzxh  
			@yqkje ut_money --原收费记录的qkje qkbz=3
		select @ysjh=tsjh,@ypatid=patid,@yqkje=isnull(case when qkbz=3 then -qkje else 0 end,0) from SF_BRJSK where sjh=@hcsjh
		update SF_BRJSK set ybjszt=2 where sjh=@hcsjh --更新红冲记录的ybjszt
		--更新原收费记录,原GH_GHZDK的jlzt
		update SF_BRJSK set jlzt=1 where sjh=@ysjh
		if @@rowcount=0
		update SF_NBRJSK set jlzt=1 where sjh=@ysjh
		update GH_GHZDK set jlzt=1 where jssjh=@ysjh
		if @@rowcount=0
		update GH_NGHZDK set jlzt=1 where jssjh=@ysjh
		--更新新生成的收费记录的tsjh
		update SF_BRJSK set tsjh=@hcsjh where sjh=@sjh
		--处理原收费记录的扣卡金额
		if @yqkje<>0
		begin
			select @yjzxh=xh from YY_JZBRK (nolock) where patid=@ypatid and jlzt=0 and gsbz=0 
			select @sjyjye=yjye from YY_JZBRK (nolock) where xh=@yjzxh
			insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh,ybdm,dbkje)
			select 0,0,@yjzxh,@czyh,@czym,@now,0,@yqkje,@sjyjye+@yqkje,1,4,null,0,'自费转医保红冲',@hcsjh,NULL,0
			update YY_JZBRK set yjye=@sjyjye+@yqkje where xh=@yjzxh
			update SF_BRXXK set zhje=@sjyjye+@yqkje where patid=@ypatid
		end
	end
  /*-----自费转医保时,更新记录状态 end-----*/

	--增加账户金额及扣卡记录判断 bug110015
	declare @qkjlcount int
	select @qkjlcount = 0
	if @yjbz=1 and @qkbz1=3
	begin
		if exists(select 1 from YY_JZBRK nolock where xh = @jzxh and yjye < 0)
		begin
			select 'F','挂号扣卡错误！'
			rollback tran
			return	
		end
		select @qkjlcount = count(*) from YY_JZBRYJK nolock where jzxh = @jzxh and czlb = 3 and sjh = @sjh
		if @qkjlcount > 1
		begin
			select 'F','当前挂号已扣费，请确认！'
			rollback tran
			return
		end
	end

-- add kcs 20190523 防止欠款患者欠费标志<>2问题导致漏费 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	if exists(select 1 from SF_BRJSK a(nolock) inner join YY_YBFLK b(nolock) on a.ybdm = b.ybdm where b.zhbz=2 and a.qkbz <>2 and a.sjh = @sjh)
	begin
		select 'F','欠费患者必须欠款结算'
		return
	end
-- add kcs 20190523 防止欠款患者欠费标志<>2问题导致漏费 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	commit tran
--  20190722 保存干保信息
   exec usp_sf_savegbxx @sjh --保存干保信息  &&
--  20190722 保存干保信息


	--消息推送
	if @config1524='是'
	begin
	    exec usp_app_xxts '01',@patid,@ghzdxh,'','','','','',@errmsg output
	end	
	if @config1582='是'
	begin
	    select @yjxh= isnull(yjxh,'0') from GH_GHZDK (nolock) where xh=@ghzdxh
		if  (@yjxh<>'') and (@yjxh<>'0')
		begin
			select @linksvr=config from YY_CONFIG where id='1566' -- @linksvr='testlinksvr.NIS.dbo.'
			select @linksvr=ltrim(rtrim(ISNULL(@linksvr,''))) 
			select @sql='declare @rettype      varchar(200),@errmsg1 varchar(200)  exec '+@linksvr+'usp_his5_jzhs_writebackghbj '+rtrim(convert(varchar(20),@yjxh))+','+@ksdm+',@rettype output,@errmsg1 output' 
			exec(@sql)
		end
	end
	/*
	if @errmsg like "F%"
	begin
		select "F",substring(@errmsg,2,49)
		return
	end
	*/
	
	--if @gbbz = '1' 
	--begin
	--	select 	@ybzfje = sum(je) from SF_JEMXK nolock where lx in ('20','22') and jssjh = @sjh
	--end
	--if (select config from YY_CONFIG (nolock) where id='1025')='是' --打印是否用发票大项
	--begin	
	--	select " T", @zje, @zfyje, convert(varchar(20),@fph), @print, @sfje2-@qkje-@gbje-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,-- 0-6
	--		@tczfje, @dffjzfje, '', @qkbz1, @qkje2  -- 7-11
	--		,@sjyjye ,@sjdjje, @kmc, @cardno, @gbje,@ybzfje,@jmje,@fpdm  --12-17
	--	union all
	--	select fpxmmc, sum(xmje), 0, 0, 0, 0, 0, 0, 0, fpxmdm, 0, 0 ,0,0, '', '', 0,0,0,''
	--		from SF_BRJSMXK where jssjh=@sjh group by fpxmdm, fpxmmc
	--end
	--else 
	--begin
	--	select " T", @zje, @zfyje, convert(varchar(20),@fph), @print, @sfje2-@qkje-@gbje-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,
	--		@tczfje, @dffjzfje, '', @qkbz1, @qkje2
	--		,@sjyjye ,@sjdjje, @kmc, @cardno, @gbje,@ybzfje,@jmje,@fpdm
	--	union all
	--	select dxmmc, xmje, 0, 0, 0, 0, 0, 0, 0, dxmdm, 0, 0 ,0,0, '', '', 0,0,0,''
	--		from SF_BRJSMXK where jssjh=@sjh
	--end		
end

return


