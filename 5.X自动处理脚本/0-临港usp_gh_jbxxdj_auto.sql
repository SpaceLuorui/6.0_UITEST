create procedure usp_gh_jbxxdj_auto
    @blh        ut_blh   ,	--病人病历号
    @hzxm       ut_mc64  ,	--姓名
    @czyh       ut_czyh  ,	--操作员
    @cardtype   ut_dm2 	 , 	--卡类别	0:无卡,1:磁卡,2:保障卡,3:ic卡
    @cardno     ut_cardno,	--卡号
    @grybh      ut_mc32	 , 	--个人医保号
    @dwybh      ut_mc32  , 	--单位医保号
    @sbh        ut_cardno,	--社保号
    @qtkh       ut_cardno,	--其它卡号
    @sfzh       ut_sfzh  ,	--身份证号
    @sex        ut_sex   ,	--性别
    @birth      ut_rq8   ,	--出生年月
    @lxdz       varchar(255)  ,	--联系地址
    @lxdh       ut_mc16  ,	--联系电话
    @yzbm       ut_dm12  ,	--邮政编码
    @lxr        ut_mc64  ,	--联系人
    @ybdm       ut_ybdm  ,	--医保代码
    @pzh        ut_pzh   ,	--凭证号
    @dwbm     	ut_ybdwdm,	--单位代码 
    @dwmc     	ut_mc64  ,	--单位名称
    @qxdm       ut_qxdm  ,	--区县代码
    @ksrq       ut_rq8   ,	--开始日期
    @jsrq       ut_rq8   ,	--结束日期
    @ylxm       ut_dm2   ,	--医疗项目
    @zddm       ut_zddm  ,	--诊断代码
    @zhje       ut_money ,	--帐户金额
    @memo       ut_memo =null,	--备注
	@zhbz		ut_zhbz	=null,
	@csd_s      ut_dqdm =null,    --出生地（省市）
	@csd_x      ut_dqdm =null,     --出生地（区县）
    @dyid       ut_dqdm =null,     --病人地域代码(YY_BRDYLYK.id )
    @ljje		ut_money=0,	--统筹累计金额
	@ekfmxm		ut_mc64=null,	--（儿科患者）父母姓名
	@czrq		ut_rq8=null,	--初诊日期
	@gms		ut_mc64=null,	--过敏史
	@xgh		ut_dm12=null,	--X光号
	@cth		ut_dm12=null,	--CT号
	@mrh		ut_dm12=null,	--MR号
	@qth		ut_dm12=null,	--其他检查号
	@zypatid 	ut_xh12 = 0, 	--对应的住院内码patid
	@xlmc		ut_mc16=null,	--学历
	@gjbm		ut_dm4=null,	--国家编码
	@hyzk		ut_bz=0,			--婚姻状况0未婚,1已婚,2离独,3丧偶
	@zydm		ut_dm4=null,	--职业代码
    @brjob      ut_mc256=null,--职业名称
	@spzdm 		varchar(20) = null, --双凭证代码
	@lxsj		ut_mc16 = null,		--手机
    @email      ut_mc16 = null   --email
    ,@mzdm      ut_mc16 = null   --民族代码
    ,@sfzly		ut_bz=''	--身份证录入来源 0-手工录入，1-自动带入（包括医保帐户中读取的和二代身份证中读取的） 需求73863
    ,@maincardtype      ut_dm2=0	 --卡类别	0:无卡,1:磁卡,2:保障卡,3:ic卡
    ,@maincardno        ut_cardno=''     --卡号
    ,@zjlx      ut_bz=0  --证件类型默认为 身份证 add by aorigel for 108959 证件类型维护在 1240中
    ,@jhrxm		ut_mc64 = ''  --监护人姓名
    ,@jhrzjlx   ut_bz = 0  --监护人证件类型默认为 身份证
    ,@jhrsfzh	ut_sfzh = '' --监护人证件号码
    ,@ay		ut_memo = ''  --案由
	,@birthtime  ut_rq8 = ''	--出生时间
	,@bmdm      ut_xmdm = ''	--部门代码
	,@ghtxfw    ut_bz = 0 --挂号提醒服务
	,@xxmc    ut_mc64 = '' --学校名称
	,@qebzkh    ut_mc64 = '' --全额保障卡号
	,@yhdm ut_ybdm=''  
	,@brlydm ut_dm4 ='' --病人来源ID
	,@jsr   ut_mc64 ='' --介绍人
	,@lskbz ut_bz=0
	,@zjxy	varchar(64)=''--宗教信仰
	,@qqwxid	varchar(30)=''--QQ、微信号
	,@lxrgx	ut_dm2=''--联系人关系代码
	,@lxrdh	varchar(13)=''--联系人电话
	,@lxrdz varchar(255)=''--联系地址
	,@csd_jd varchar(64)='' --出生地街道
	,@hkd_sid ut_dqdm=''    --户口地省ID
	,@hkd_sname ut_mc32=''  --户口地省NAME
	,@hkd_qid ut_dqdm=''    --户口地区ID
	,@hkd_qname ut_mc32=''  --户口地区NAME
	,@hkd_addr ut_mc64=''   --户口地详细地址
	,@jzd_sid ut_dqdm=''    --居住地省ID
	,@jzd_sname ut_mc32=''  --居住地省NAME
	,@jzd_qid ut_dqdm=''    --居住地区ID
	,@jzd_qname ut_mc32=''  --居住地区NAME
	,@jzd_addr ut_mc64=''   --居住地详细地址
	,@brtslb ut_dm2 =''     --病人特殊类别 add by mxd for 需求106470 衡水市哈励逊国际和平医院--门诊特殊病人需要标志出来
	,@txmszm varchar(20) =''  --条形码号
	,@tsbrbz ut_bz= null --特殊病人标志 add by mxd for 需求130877 20170123
	,@hmxh ut_mc32='' --化名序号 add by mxd for 需求130877 20170123
	,@hzyjh varchar(32)=''  --急诊系统预检号
	,@yjdjbz ut_bz=0  --预检登记标志 add by sang for 需求158299 20170524
	,@csd_djsid ut_dqdm=''	--出生地地级市ID  	add by djs for 179917 20170726
	,@csd_djsname ut_mc32=''--出生地地级市NAME  add by djs for 179917 20170726
	,@hkd_djsid ut_dqdm=''	--户口地地级市ID  	add by djs for 179917 20170726
	,@hkd_djsname ut_mc32=''	--户口地地级市NAME  add by djs for 179917 20170726
	,@jzd_djsid ut_dqdm=''	--居住地地级市ID  	add by djs for 179917 20170726
	,@jzd_djsname ut_mc32=''	--居住地地级市NAME  add by djs for 179917 20170726
	,@xwdm ut_dm2=''			--学位代码		add by djs for 180241 20170726
	,@pkhbz ut_bz =''         --贫困户标志 0为否，1为是  add z_wm for 257697
as --集3074 2018-11-29 15:38:56 4.0标准版_201810补丁
/**********
[版本号]4.0.0.0.0
[创建时间]2004.10.25
[作者]朱伟杰
[版权] Copyright ? 2004-2008上海金仕达-卫宁软件股份有限公司[描述] 门诊系统--基本信息登记
[功能说明]
	患者基本信息登记
[参数说明]
    @blh        ut_blh 		--病人病历号
    @hzxm       ut_name		--姓名
    @czyh       ut_czyh  	--操作员
    @cardtype   ut_dm2   	--卡类别	0:医院磁卡,1:老医保卡,2:保障卡,3:ic卡
    @cardno     ut_cardno	--卡号
    @grybh      ut_mc32	  	--个人医保号
    @dwybh      ut_mc32   	--单位医保号
    @sbh        ut_cardno	--社保号
    @qtkh       ut_cardno	--其它卡号
    @sfzh       ut_sfzh  	--身份证号
    @sex        ut_sex   	--性别
    @birth      ut_rq8   	--出生年月
    @lxdz       ut_mc64  	--联系地址
    @lxdh       ut_mc16  	--联系电话
    @yzbm       ut_dm12  	--邮政编码
    @lxr        ut_name  	--联系人
    @ybdm       ut_ybdm  	--医保代码
    @pzh        ut_pzh   	--凭证号
    @dwbm     	ut_ybdwdm	--单位代码
    @dwmc     	ut_mc64  	--单位名称
    @qxdm       ut_qxdm  	--区县代码
    @ksrq       ut_rq8   	--开始日期
    @jsrq       ut_rq8   	--结束日期
    @ylxm       ut_dm2   	--医疗项目
    @zddm       ut_zddm  	--诊断代码
    @zhje       ut_money 	--帐户金额
    @memo       ut_memo  	--备注
	@zhbz		ut_zhbz		--帐户标志
	@csd_s      ut_dqdm     --出生地（省市）
	@csd_x      ut_dqdm     --出生地（区县）
    @dyid           ut_dqdm =null      --病人地域代码(YY_BRDYLYK.id )
    @ljje	ut_money=0		--统筹累计金额
	@ekfmxm		ut_mc32=null,	--（儿科患者）父母姓名
	@czrq		ut_rq8=null,	--初诊日期
	@gms		ut_mc64=null,	--过敏史
	@xgh		ut_dm12=null,	--X光号
	@cth		ut_dm12=null,	--CT号
	@mrh		ut_dm12=null,	--MR号
	@qth		ut_dm12=null	--其他检查号
	,@brtslb ut_dm2 =''     --病人特殊类别 add by mxd for 需求106470 衡水市哈励逊国际和平医院--门诊特殊病人需要标志出来
[返回值]

[结果集、排序]
	成功："T",hzxh
	错误："F","错误信息"
[调用的sp]

[调用实例]

[修改历史]
  黄克华  on 2003/04/18 增加传入参数 @csd_s,@csd_x, 插入表SF_BRXXK
  modify by  dengnan  2003.12.19  增加传入参数  @dyid           ut_dqdm =null      --病人地域代码(YY_BRDYLYK.id )
  modify by zwj 2004.02.18	增加传入参数 @ljje	ut_money	--病人统筹累计金额
  2004-09-02  /*@zypatid以往没有使用，先重新起用，当不为空时作为内部调用，不返回结果*/
  modify by mxd 2016-09-24 增加传入参数 @brtslb 插入表SF_BRXXK_FZ 
  @tsbrbz ut_bz='' --特殊病人标志 modify by mxd for 需求130877 20170123
  modify by wjy 2018/06/29 贫困人口标志处理 vsts 315126（参数0384控制）
**********/
set nocount on

declare		@patid	ut_xh12,	--病人内码
			@now 	ut_rq16,	/*当前日期*/
			@pzlx 	ut_dm2,		/*凭证类型*/
	        @pzlb  	ut_bz,      /*凭证类别,0=门诊，1=住院*/
			@errmsg varchar(50),
			@config1459 varchar(32),
		    @dhgxczyh ut_czyh,  --联系电话更新操作员 z_hj
		    @dhgxrq ut_rq16,     --联系电话更新日期   z_hj 
			@config0384 varchar(5)--add by wjy for vsts 315126

-- add kcs 20190627 卡号长度为15位或者18位，卡类别应该是1 >>>>>>>>>>>>>>>>>>>>>>>>>>>>
if len(@cardno) in (15,28) and @cardtype = 2
begin
    select @cardtype = 1
end
-- add kcs 20190627 卡号长度为15位或者18位，卡类别应该是1 <<<<<<<<<<<<<<<<<<<<<<<<<<<<

select @config0384 =LTRIM(RTRIM(config)) from YY_CONFIG(nolock) where id ='0384';

/*验证GH_CARD*/
if @cardtype<>'0' and exists(select 1 from GH_CARD where cardno=@cardno and cardtype=@cardtype)
begin
	select "F","卡号"+@cardno+"已存在！"
	return
end

if @cardtype<>'0' and rtrim(@cardno)=''
begin
	select "F","卡号不允许为空！"
	return
end

select @now=convert(char(8),getdate(),112)+convert(char(8),getdate(),8)

select @config1459=config from YY_CONFIG where id='1459' 
if (@config1459 <> '') and (@birth <> '')
begin
	if cast(substring(@now,1,4) as int ) - cast(substring(@birth,1,4)  as int ) <= @config1459
	begin
		if @ekfmxm=''
		begin
			select "F","父母姓名不能为空！"
			return
		end
	end
end 

if @yjdjbz=0
begin	
	select @pzlx=pzlx,@pzlb=xtbz from YY_YBFLK where ybdm=@ybdm and xtbz=0 and jlzt=0  --得到凭证类
	if @@rowcount=0 or @@error<>0
	begin
		select "F","读取医保分类库时出错！"
		return
	end
end
   
-- add kcs 20190601 紧急处理 默认20岁情况 >>>>>>>>>>>>>>>>>>>>>>>>>>>
if  (substring(@sfzh,7,8) <> @birth) and (@sfzh <> '') and (len(@sfzh) = 18)
begin
    select @birth = substring(@sfzh,7,8)
end
-- add kcs 20190601 紧急处理 默认20岁情况 <<<<<<<<<<<<<<<<<<<<<<<<<<<

declare @py varchar(255),
		@wb varchar(255)

exec usp_yy_getpyzt	@hzxm,0,@py output   --生成单个拼音或五笔

select @py=substring(@py,1,64)

exec usp_yy_getpyzt	@hzxm,1,@wb output  --生成单个拼音或五笔

select @wb=substring(@wb,1,64)

if @pzlx in ('3') --门诊劳保
	select @pzh=substring(@blh,1,10) 

if ((select config from YY_CONFIG where id="1020")='是' or (@blh is null)) and isnull(@zypatid,0) = 0 --是否自动生成病历号
begin
	exec usp_yy_createblh "SF_BRXXK","blh",@errmsg output,0,0,@ybdm,'',0,'',0,'','','',1
	if @errmsg like 'F%'
	begin
		select "F",substring(@errmsg,2,49)
		return
	end
	select @blh=substring(@errmsg,2,19)
end

/*
if @sfzh='' or @sfzh is null  --设置默认身份证号
	select @sfzh='311111111111111111'
*/
if isnull(@maincardno,'')<>''
begin
    if exists(select 1 from SF_BRCARD (nolock) where cardno=@maincardno and cardtype=@maincardtype and cardlb=0 and jlzt=0)
       or exists(select 1 from SF_BRXXK (nolock) where cardno=@maincardno and cardtype=@maincardtype and tybz=0) 
    begin
		select "F","该院内卡号已经存在"
		return
    end
end

if exists(select 1 from YY_CONFIG where id="1310" and config="是")
begin
	if @birthtime = ''
	select @birthtime = '00:00:00';
	if len(rtrim(ltrim(@birthtime))) = 5	 
	select @birthtime = rtrim(ltrim(@birthtime)) + ':00';
end
else
begin
    select @birthtime = '';
end;

--需求：76041
if  exists(select 1 from YY_CONFIG where id="1200" and config="是") 
begin
	if  exists(select 1 from YY_CONFIG where id="1133" and config="否") 
		and
		exists(select 1 from SF_BRXXK (nolock) where UPPER(blh)=UPPER(@blh) and tybz=0) 
	begin
		select "F","病历号["+@blh+"]被重复使用"
		return
	end
end

begin tran 
	/*验证病历号是否重复*/
	if exists(select 1 from YY_CONFIG where id="1133" and config="否")
	begin
		if exists(select 1 from SF_BLHXXK where upper(blh)=upper(@blh) and xtflag=0) 
		begin
			select "F","病历号["+@blh+"]被重复使用!"
			rollback transaction 
			return  
		end
		else
		begin
			insert into SF_BLHXXK(blh,xtflag)
			values(@blh,0)
			if @@error <> 0 or @@rowcount = 0
			begin
				select "F","插入病历号信息库时失败!"
				rollback transaction 
				return 
			end
		end
	end
	insert SF_BRXXK(blh,hzxm,wb,py,cardno,sbh,qtkh,sfzh,sex,birth,csd_s,csd_x,
			lxdz,lxdh,yzbm,lxr,ybdm,pzh,dwbm,dwmc,qxdm,zhje,ljje,zhszrq,zjrq,
			czyh,lrrq,tybz,cardtype,zhbz,memo,dyid,ekfmxm,czrq,gms,xgh,cth,mrh,qth,zypatid,
			xlmc,gjbm,hyzk,zydm,brjob,spzlx,lxsj,email,mzdm,sfzly,zjlx,jhrxm,jhrzjlx,jhrsfzh,ay,birthtime,bmdm,txfwbz,xxmc,qebzkh
			,yhdm--add wuwei 20150315
			,lxrdh,lxrgx--add liuchun 20150723
			,lxrdz--add zlw 20160203
			,tsbrbz
			,pkhbz,ylxm)
	values(@blh,@hzxm,@wb,@py,isnull(@cardno,''),@sbh,@qtkh,@sfzh,@sex,@birth,@csd_s,@csd_x,
			@lxdz,@lxdh,@yzbm,@lxr,@ybdm,@pzh,@dwbm,@dwmc,@qxdm,@zhje,@ljje,null,substring(@now,1,8),
			@czyh,substring(@now,1,8),0,@cardtype,@zhbz,@memo,@dyid,@ekfmxm,@czrq,@gms,@xgh,@cth,@mrh,@qth,@zypatid,
			@xlmc,@gjbm,@hyzk,@zydm,@brjob,@spzdm,@lxsj,@email,@mzdm,@sfzly,@zjlx,@jhrxm,@jhrzjlx,@jhrsfzh,@ay,@birthtime,@bmdm,@ghtxfw,@xxmc,@qebzkh
			,@yhdm
			,@lxrdh,@lxrgx
			,@lxrdz
			,@tsbrbz
			,@pkhbz,@ylxm)

  	if @@error<>0
   	begin
    	select "F","插入病人信息库时出错！"
        rollback transaction 
        return
   	end

	select @patid=@@identity
	
	--add by mxd for 需求130877 20170123
	if @hmxh <> '' and exists(select 1 from YY_CONFIG (nolock) where id = '1539' and config = '是') 
	begin
	    update YY_BRHMK set mzpatid = @patid where id = @hmxh 
	    if @@ERROR <> 0
	    begin
	        select "F","启用化名流程下更新病人化名库出错！"
            rollback transaction 
            return
	    end
	end
	
	--add by z_hj 当练习电话填写了才记录@dhgxczyh和@dhgxrq，否则为空
	if @lxdh<>''
	begin
		select @dhgxczyh=@czyh
		select @dhgxrq=substring(@now,1,8)
	end 
	
	insert into SF_BRXXK_FZ(patid,lyid,wtys,jsr,lskbz,zjxy,qqwxid,csd_jd,
	    hkd_sid,hkd_sname,hkd_xid,hkd_xname,hkd_addr,jzd_sid,jzd_sname,jzd_xid,jzd_xname,jzd_addr,brtslb,txmszm,dhgxrq,dhgxczyh,hzyjh,
	    yjdjbz,csd_djsid,csd_djsname,hkd_djsid,hkd_djsname,jzd_djsid,jzd_djsname,xwdm)
	values(@patid,@brlydm,'',@jsr,@lskbz,@zjxy,@qqwxid,@csd_jd,
	    @hkd_sid,@hkd_sname,@hkd_qid,@hkd_qname,@hkd_addr,@jzd_sid,@jzd_sname,@jzd_qid,@jzd_qname,@jzd_addr,@brtslb,@txmszm,@dhgxrq,@dhgxczyh,@hzyjh,
	    @yjdjbz,@csd_djsid,@csd_djsname,@hkd_djsid,@hkd_djsname,@jzd_djsid,@jzd_djsname,@xwdm)
  	if @@error<>0
   	begin
    	select "F","插入病人信息扩展库时出错！"
        rollback transaction 
        return
   	end	
    --多卡绑定处理begin xxl 200810
    if (isnull(@cardno,'')<>'') and (isnull(@maincardno,'')<>'')  
    begin  
        if @maincardtype=1 and @cardtype<>1 
        begin
			insert SF_BRCARD(patid,cardno,cardtype,cardlb,jlzt)  
				   values(@patid,isnull(@cardno,''),@cardtype,1,0)  
				  if @@error<>0  
					 begin  
					   select "F","插入卡记录时出错！"  
					rollback transaction   
					return  
					 end  
						insert SF_BRCARD(patid,cardno,cardtype,cardlb,jlzt)  
						values(@patid,isnull(@maincardno,''),@maincardtype,0,0)  
				  if @@error<>0  
			 begin  
			   select "F","插入主卡记录时出错！"  
				  rollback transaction   
				  return  
			 end  
       end
       else if @maincardtype<>1 and @cardtype=1 
       begin
            insert SF_BRCARD(patid,cardno,cardtype,cardlb,jlzt)  
				   values(@patid,isnull(@maincardno,''),@maincardtype,1,0)  
				  if @@error<>0  
					 begin  
					   select "F","插入卡记录时出错！"  
					rollback transaction   
					return  
					 end  
						insert SF_BRCARD(patid,cardno,cardtype,cardlb,jlzt)  
						values(@patid,isnull(@cardno,''),@cardtype,0,0)  
				  if @@error<>0  
			 begin  
			   select "F","插入主卡记录时出错！"  
				  rollback transaction   
				  return  
			 end  
       end   
    end  
    --多卡绑定处理end xxl 200810
	--增加慈善医疗这样的账户纪录 开始
	if @zhje <> 0 
	begin
		insert into GH_ZHBRCZJL(patid,ybdm,zhfsje,zhye,czyh,lrrq)
		select @patid,@ybdm,@zhje,@zhje,@czyh,@now
	  	if @@error<>0
	   	begin
	    	select "F","插入账户病人冲值纪录时出错！"
	        rollback transaction 
	        return
	   	end
	end
	--增加慈善医疗这样的账户纪录 结束
   	if (@pzlx>0 and not @pzlx in (10,11,13,15,18)) or 
		exists(select 1 from YY_CONFIG (nolock) where charindex('"'+rtrim(@ybdm)+'"',config)>0 and id='0053')
--		(@pzlx=0 and @ybdm='55') --非急诊和大病医保
   	begin
		if ltrim(rtrim(@pzh))='' --alter by sqf 2009.1.8
		begin
            select "F","凭证号不能为空！"
            rollback transaction
            return
		end

       	insert into SF_YBPZK
      		(pzh,patid,ybdm,hzxm,blh,ksrq,jsrq,dwbm,ylxm,
       		zddm,czyh,lrrq,pzlx,zhje,ljje,zhszrq,jlzt,memo)
       	values
       		(@pzh,@patid,@ybdm,@hzxm,@blh,@ksrq,@jsrq,@dwbm,@ylxm,
        	@zddm,@czyh,@now,@pzlx,@zhje,@ljje,null,0,null)
       	if @@error<>0
       	begin
            select "F","插入凭证信息时出错！"
            rollback transaction
            return
       	end
	end
	
	if @cardtype<>'0'
	begin
		insert GH_CARD(cardno,cardtype,patid,hzxm,grybh,dwybh,
						lrrq,czyh,cbrq,bgrq,jlzt,memo)
		values(@cardno,@cardtype,@patid,@hzxm,@grybh,@dwybh,
						@now,@czyh,@ksrq,null,0,null)
       	if @@error<>0
       	begin
            select "F","插入卡号库时出错！"
            rollback transaction
            return
       	end
	end

	--add by wjy for vsts 315126
	if @config0384='是' and isnull(@sfzh,'')<>''
	begin
		if exists (select 1 from  YY_PKRKXXB where sfzh=@sfzh and jlzt =1 and shbz =1)
		begin
			update SF_BRXXK set pkhbz=1 where patid =@patid;
			if @@error<>0
   			begin
    			select "F","更新贫困户标志出错！"
				rollback transaction 
				return
   			end	
		end	
		else
		begin
			update SF_BRXXK set pkhbz=0 where patid =@patid;
			if @@error<>0
   			begin
    			select "F","更新贫困户标志出错！"
				rollback transaction 
				return
   			end	
		end;	
	end
commit tran
/*@zypatid以往没有使用，先重新起用，当不为空时作为内部调用，不返回结果*/
--if isnull(@zypatid,0) = 0 
--	select "T",@patid
return



