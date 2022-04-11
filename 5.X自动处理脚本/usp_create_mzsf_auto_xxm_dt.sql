alter proc [dbo].[usp_create_mzsf_auto_xxm_dt]        
   @ghxh  ut_xh12    ,                             --挂号的序号  
   @i   int                                         --临床项目序号,最大763       
 as          
SET NOCOUNT ON        
 ------        
 --HIS系统对临床项目全部收费，数量默认为1         
 --此存储过程中，科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1,        
 ---此存储过程中收、配、发窗口默认01        
-- usp_create_mzsf_auto_xxm_dt  3112508,762    
 -----        
        
           
   --1.项目不用判断库存       
   --2.取患者的基本信息        
   declare @patid  ut_xh12        
   declare @ghsjh  ut_sjh        
   select @patid=patid,  @ghsjh=jssjh from  GH_GHZDK  where  xh=@ghxh order  by  xh desc        
   if @@error<>0 or @@rowcount=0        
   begin        
     select 'F','取患者基本信息出现错误，请检查'        
  return        
   end        
        
        
   ---取到已有执行科室的收费小项目  
   declare @xmdm_i  varchar(256)        
   declare @zxks_id  varchar(256)   
   
   --开过历史处方的小项目集和
   create table #tab (ypdm ut_xmdm)
    insert into #tab
	select distinct ypdm from VW_MZHJCFMXK where  cd_idm='0'
   
   --取现阶段符合条件的小项目集和       
   create table #tab1(xh ut_xh12 IDENTITY(1,1) NOT NULL, xmdm varchar(256), zxks_id varchar(256))        
   insert into #tab1( xmdm,zxks_id)  select id,zxks_id from YY_SFXXMK where sybz=1 and mzbz=1 and zxks_id<>''
   and id in (select ypdm from #tab)
    order  by id asc   
   
   --select max(xh) from #tab1
   --return
   --遍历完成就直接返回 
   declare @allcount ut_xh12
   select @allcount=max(xh) from  #tab1
   if @i>@allcount  
     begin
	     select "T","小项目集和已完成遍历"
		 return
	 end  
   --根据i值确定xmdm  
   select  @xmdm_i=xmdm, @zxks_id=zxks_id from #tab1  where  xh=@i  
      
   --取得对应的小项目的信息     
   declare @dxmdm  varchar(256)   ---大项目代码  
   declare @xmdw  varchar(256)    ---小项目单位  
   declare @xmdj  ut_money       ----项目单价  
   declare @xmmc  ut_mc64        ----项目名称            
   select @xmdw=xmdw,@xmdj=xmdj,@xmmc=name,@dxmdm=dxmdm from YY_SFXXMK where id=@xmdm_i  
        
   --5.小项目项目进行预算处理(科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1,默认数量是1 )  
         --hjxh写死为0,cflx写死为4,hjmxxh写死为0   
       
   exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,"","",0,"",          1,0,1,0,    @ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
      exec usp_sf_sfcl_auto "68071579B77E",2,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,@dxmdm,@xmdm_i,0,@xmdw,1,1,1,@xmdj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@xmmc,@lcxmdm="0",@hjmxxh="0",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
      exec usp_sf_sfcl_auto "68071579B77E",3,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,"","",0,"",          1,0,1,0,    @ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
  
    declare @jssjh  ut_sjh   ---预算后的结算收据号  
  select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc         
   --6.药品进行结算处理(收、配、发窗口默认01，)   
 --exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@zxks_id,"01","01","01","1",0,0,4,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=0.00,@isQfbz=0,@jslb=0,@ipdz_gxzsj="172.32.154.71"  
   exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@zxks_id,"01","01","01","1",0,0,4,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=0.00,@isQfbz=0,@jslb=0,@ipdz_gxzsj="172.32.154.71"  
    
     
   ---传出参数jssjh（结算收据号）、zje(总金额)、zfje（支付金额）,cfts(处方付数),yfdm(药房代码),cd_idm(药品idm),ypgg(药品规格)        
   ---cjmc(厂家名称)、mzdj（门诊单价）,ypsl (最小单位的数量)        
   select @jssjh as jssjh,a.zje,a.zfje,yfdm as 执行科室,b.xh as 处方号,c.xh as 明细序号,c.ypmc,c.ypdm  
   from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c        
   where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh 