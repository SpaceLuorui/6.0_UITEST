CREATE proc [dbo].[usp_create_mzsf_auto_zcy_dt]    
   @ghxh  ut_xh12    ,                             --挂号的序号     
   @yfdm  ut_ksdm='3007',                           ---草药药房的代码，临港默认为3007    
   @i   int                                         --草药序号    
 as      
SET NOCOUNT ON    
 ------    
 --HIS系统对草药药品全部收费，数量默认为1，处方付数默认为7    
 --此存储过程过程中，dxmdm，03是写死的；    
 --此存储过程中，科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1,dxmdm=03     
 ---此存储过程中收、配、发窗口默认01    
-- usp_create_mzsf_auto_zcy_dt  3108487,'3007',300    
 -----    
    
 begin      
   --1.判断药房是否存在药品,    
   declare @countsl  int     
   select @countsl=  count(*)   from VW_MZJGK_LC a(nolock) left join YY_KSBMK b(nolock) on a.ksdm=b.id left join YY_SFDXMK c(nolock) on a.dxmdm=c.id     
                   where   a.ypbz = 0  and  a.ksdm =@yfdm and syfw  in  (1,0)  and dxmdm in ('03')    
   if @countsl=0    
   begin    
     select 'F','传入的草药药房没有药品，请检查药房代码是否传入错误'    
  return    
   end     
   if @countsl<@i    
   begin    
     select 'T','草药药品已全部开立完成'    
  return    
   end     
    
   --2.取患者的基本信息    
   declare @patid  ut_xh12    
   declare @ghsjh  ut_sjh    
   select @patid=patid,  @ghsjh=jssjh from  GH_GHZDK  where  xh=@ghxh order  by  xh desc    
   if @@error<>0 or @@rowcount=0    
   begin    
     select 'F','取患者基本信息出现错误，请检查'    
  return    
   end    
    
   --3.定义结算需要使用的变量    
   declare @idm  int     
   declare @cflx  int     
   declare @mzdw varchar(50)   --门诊单位    
   declare @mzxs int           --门诊系数    
   declare @mzdj ut_money      --门诊单价    
   declare @xcypmc varchar(200)  --药品名称    
   declare @jssjh  varchar(50)   --结算收据号    
   declare @cjmc varchar(200)    
    
   ---取到药品的idm信息    
   create table #tab1(xh ut_xh12 IDENTITY(1,1) NOT NULL, idm int )    
   insert into #tab1( idm)  select idm  from VW_MZJGK_LC a  left join YY_KSBMK b on a.ksdm=b.id left join YY_SFDXMK c  on a.dxmdm=c.id     
   where  a.ypbz = 0  and  a.ksdm=@yfdm and  syfw  in  (1,0)   and dxmdm  in ('03') order  by idm asc     
   select  @idm=idm from #tab1  where  xh=@i    
    
   --取到药品的明细信息（cflx=3写死）    
    select   @mzdw=convert(varchar(4),a.mzdw),@xcypmc=a.ypmc ,@mzxs=a.mzxs,    
  @mzdj=dbo.fun_gettxbldj(a.idm,a.ypdm,str(a.ylsj*a.mzxs/a.ykxs,10,4),'1','否')    
  ,@cflx=3,    
  @cjmc=a.cjmc    
  from VW_MZJGK_LC a  left join YY_KSBMK b on a.ksdm=b.id left join YY_SFDXMK c  on a.dxmdm=c.id     
  where  a.idm=@idm  and    a.ypbz = 0  and  a.ksdm=@yfdm and  syfw  in  (1,0)  and a.dxmdm in ('03')    
          
    
            
   --5.药品进行预算处理(科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1,dxmdm=03，默认数量是1，付数是7 )    
   exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"",   "",  0,  ""  ,1,0,1,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】    
   exec usp_sf_sfcl_auto  "68071579B77E",2,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"03",@idm,@idm,@mzdw,@mzxs,1,7,@mzdj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@xcypmc,@lcxmdm="0",@hjmxxh="0",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",
  
@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】    
   exec usp_sf_sfcl_auto  "68071579B77E",3,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】    
   select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc     
   --6.药品进行结算处理(收、配、发窗口默认01，)    
   exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@yfdm,"01","01","01","1",0,0,@cflx,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=2.50,@isQfbz=0,@jslb=0,  
   @ipdz_gxzsj="172.32.154.71"--【Ztsql】    
  
      ---7.门诊频次代码  
   declare  @pcdm  varchar(2)  
   declare  @pcmc varchar(10)  
   create table #tab3( rowsl int,pcdm varchar(20) )   --创建临时表      
   insert  into #tab3( rowsl ,pcdm  )  select distinct  count( * )AS count,pcdm FROM  VW_MZHJCFMXK  where   
   pcdm<>'' and  cd_idm=@idm  and  cfxh  in (select  top  1000 xh from  VW_MZHJCFK  order  by  xh desc  )  
   GROUP BY  pcdm ORDER BY count DESC  
   select  top 1 @pcdm=  rtrim(ltrim(pcdm)) from  #tab3  order by rowsl  desc   
   if len(@pcdm)=1  
     select @pcdm='0'+@pcdm     
   select @pcmc=name  from YY_YZPCK  where rtrim(ltrim(pcdm))=@pcdm   
   ---8.门诊用法信息  
   declare  @yfid  varchar(10)  
   declare  @yfmc varchar(10)  
   create table #tab2( rowsl int,yfdm varchar(20) )   --创建临时表      
   insert  into #tab2( rowsl ,yfdm  )  select distinct  count( * )AS count,ypyf   
   FROM  VW_MZHJCFMXK    
   where  ypyf<>'' and  cd_idm=@idm  and  cfxh  in (select  top  1000 xh from  VW_MZHJCFK  order  by  xh desc  )  
   GROUP BY     ypyf ORDER BY count DESC  
   select  top 1 @yfid=  yfdm from  #tab2   order by rowsl  desc   
   select @yfmc=name  from SF_YPYFK  where  id=@yfid    
  
   drop table #tab3  
   drop table #tab2  
       
   ---传出参数jssjh（结算收据号）、zje(总金额)、zfje（支付金额）,cfts(处方付数),yfdm(药房代码),cd_idm(药品idm),ypgg(药品规格)    
   ---cjmc(厂家名称)、mzdj（门诊单价）,ypsl (最小单位的数量)    
   select @jssjh as jssjh,a.zje,a.zfje,b.cfts,b.yfdm,c.cd_idm,c.ypgg,@cjmc as cjmc,@mzdj as mzdj,c.ypsl,@xcypmc  as  ypmc    
      ,@pcdm as  pcdm,@pcmc as  pcmc,@yfid  as yfid ,@yfmc as  yfmc  
   from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c    
   where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh     
end 