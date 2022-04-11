   
  
alter PROC usp_create_djgh_auto   
   @ksdm  ut_ksdm  ='2028',    --默认挂号的科室代码  
   @ysdm  varchar(50)   ='',        --默认挂号的医生代码,暂时没处理
   @existpatid    ut_xh12='0'
 as 
 
   ---定义使用的变量patid、ghxh、sjh
   --  usp_create_djgh_auto  @existpatid='3153310'
   declare @sjh  varchar(50)   
   declare @patid  ut_xh12  
   declare @ghxh  ut_xh12  
    
   if(@existpatid='0')  
   begin 
   --1.生成病历号  
   declare @blh varchar(50)    
   exec usp_yy_createblh 'SF_BRXXK','blh',@blh output --select substring(@blh,2,49)--【Ztsql】    
   set @blh=substring(@blh,2,49)   
     
   --卡号变量赋值  
   declare @cardno  varchar(50)    
   set @cardno='AUTOTEST'+@blh  
   
     
   --2.基本信息登记（患者姓名直接写死成blh） 
   
   exec usp_gh_jbxxdj_auto  @blh     ,@blh,'00','1',@cardno,'','','','','  ','男','19990821',  '','','','','1','','','','','','','','',0,'','','','','01',0,'','','','','','','',0,'','156',0,'19','其他职业','','',  '无','01','0','0','','0',@jhrxm=''  
       ,@jhrzjlx='0',@jhrsfzh='',@ay= '',@birthtime = '00:00',@bmdm = '',@ghtxfw = 0 ,@xxmc = '' ,@qebzkh='',@brlydm='',@jsr='',@lskbz=0,@zjxy='',@qqwxid='',@lxrgx='',@lxrdh='',@lxrdz='',@csd_jd='',@hkd_sid='',@hkd_sname='', @hkd_qid='',@hkd_qname='',
	   @hkd_addr='',  
    @jzd_sid='',@jzd_sname='',@jzd_qid='',@jzd_qname='',@jzd_addr='',@csd_djsid='',@csd_djsname='', @hkd_djsid='',@hkd_djsname='',@jzd_djsid='',@jzd_djsname='',@xwdm='',@brtslb='',@txmszm='',@pkhbz='0'--【Ztsql】    
    
  --patid变量赋值  
  select  @patid=patid from  SF_BRXXK where  blh=@blh   
    end
	else
	    select @patid=@existpatid
  --3.生成4.0挂号预算信息 （挂号科室）   
      
   exec usp_gh_ghdj_auto '68071579B77E',1,0,0,1,0,@patid,'00',@ksdm,'','2002','SJH',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'',0,'',0,0,'','','','',0,'1','','','',@yyrq='',@zqdm='',@jmjsbz=0,@inghzdxh=0,@ghfdm='',@yhdm='',@sfyf='0',@brlyid='',@wlzxyid=''   
   exec usp_gh_ghdj_auto '68071579B77E',2,0,0,1,0,@patid,'00',@ksdm,'','2002','SJH',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'',0,'',0,0,'','','','',0,'1','','','',  
        @yyrq='',@zqdm='',@jmjsbz=0,@inghzdxh=0,@zymzxh = 0,@pbmxxh=-1,@xmldxx='',@ghfdm='',@kmdm='',@yhdm='',@zbdm='',@brlyid='',@wlzxyid=''--【Ztsql】    
   exec usp_gh_ghdj_auto '68071579B77E',3,0,0,1,0,@patid,'00',@ksdm,'','2002','SJH',0,0,'',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'',0,'',0,0,'','','','',0,'1','','','',  
        @yyrq='',@zqdm='',@jmjsbz=0,@zzdjh = '',@inghzdxh=0,@ghfdm='',@ghfselectbz='',@yhdm='',@sfyf='0',@brlyid='',@wlzxyid=''--【Ztsql】    
   select @sjh= sjh from SF_BRJSK where  patid=@patid    
   --4.生成4.0挂号的结算信息      
   exec usp_gh_ghdj_ex2_auto '68071579B77E',3,2,0,1,0,@patid,'00',@ksdm,'','2002',@sjh,0,0,'','','','','',0,0,0,0,0,0,0,0,0,0,0,'',0,@zlje=0.00,@zzdjh=''--【Ztsql】    
   select @ghxh= xh from GH_GHZDK where  patid=@patid  
   --是补生成需要的记录  
   --insert into YY_PTWSCJL(opertype,patid,xtbz,sjh_syxh_ghxh) values('1',@patid,'0' ,'')     
   --insert into YY_PTWSCJL(opertype,patid,xtbz,sjh_syxh_ghxh) values('103',@patid,'0',@ghxh)    
     
   --出参是patid、blh、hzxm、卡号、身份证（为空）、ghxh、ksdm、sjh、ghhx、ghrq    
   select @patid as patid,b.blh as blh ,b.hzxm as  hzxm ,b.cardno as  cardno,b.sfzh as sfzh,@ghxh as ghxh,@ksdm as ksdm,@sjh as sjh,a.ghhx,a.ghrq ,b.sex,b.birth,a.ksmc  
   from    GH_GHZDK  a,SF_BRXXK  b  where  a.xh=@ghxh  and a.patid=b.patid   ---   
  