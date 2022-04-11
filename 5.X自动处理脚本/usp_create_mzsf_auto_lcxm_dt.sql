alter proc [dbo].[usp_create_mzsf_auto_lcxm_dt]            
   @ghxh  ut_xh12    ,                             --挂号的序号      
   @i   int                                         --临床项目序号,小于1018(临港库)            
 as              
SET NOCOUNT ON            
 ------            
 --HIS系统对临床项目全部收费，数量默认为1             
 --此存储过程中，科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1,dxmdm=03             
 ---此存储过程中收、配、发窗口默认01            
-- usp_create_mzsf_auto_lcxm_dt  3112508,3       
 -----            
            
               
          
   --2.取患者的基本信息            
   declare @patid  ut_xh12            
   declare @ghsjh  ut_sjh            
   select @patid=patid,  @ghsjh=jssjh from  GH_GHZDK  where  xh=@ghxh order  by  xh desc            
   if @@error<>0 or @@rowcount=0            
   begin            
     select 'F','取患者基本信息出现错误，请检查'            
  return            
   end            
            
            
   ---取到已有执行科室，且包含两个小项目的临床项目作为序号集(因为sfcl第二部的次数和小项目数量有关，因此取1个作为集)       
   declare @lcxmdm_i  varchar(256)            
   declare @zxks_id  varchar(256)       
   declare @allcount ut_xh12     
   
   --将开过的临床放入临时表，只取开过的项目
   create table #lctab(lcxmdm ut_xmdm )
   insert into #lctab 
   select distinct a.lcxmdm from YY_LCSFXMDYK  a inner join VW_MZHJCFMXK b on a.lcxmdm=b.lcxmdm
   
         
   create table #tab1(xh ut_xh12 IDENTITY(1,1) NOT NULL, lcxmdm varchar(256), zxks_id varchar(256))            
   insert into #tab1( lcxmdm,zxks_id)  select distinct lcxmdm,ksdm  from VW_MZJGK_LC a  where lcxmdm<>'0' and ksdm<>'' and lcxmdm in (      
   select lcxmdm from YY_LCSFXMDYK  group by lcxmdm having count(*)=1  ) and  lcxmdm in  (select lcxmdm from #lctab) order  by lcxmdm asc      
    
   select @allcount=max(xh) from  #tab1
   if @i>@allcount  
     begin
	     select "T","临床项目集和已完成遍历"
		 return
	 end  
   --根据i值确定lcxmdm      
   select  @lcxmdm_i=lcxmdm, @zxks_id=zxks_id from #tab1  where  xh=@i      
          
   --取得对应的小项目的信息      
   declare @xmdm  varchar(256)   ---小项目代码      
   declare @dxmdm  varchar(256)   ---大项目代码      
   declare @xmdw  varchar(256)    ---小项目单位      
   declare @xmdj  ut_money       ----项目单价      
   declare @xmmc  ut_mc64        ----项目名称  
   declare @xmsl   ut_sl10       ----项目数量  
   select  @xmdm=xmdm,@dxmdm=dxmdm,@xmsl=xmsl from  YY_LCSFXMDYK where lcxmdm =@lcxmdm_i               
   select @xmdw=xmdw,@xmdj=xmdj,@xmmc=name from YY_SFXXMK where id=@xmdm      
           
   --5.临床项目进行预算处理(科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1 )      
         --hjxh写死为0,cflx写死为4,hjmxxh写死为0   
   --默认ypsl为1,项目数量为1*临床里的项目数量      
  --exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"",   "",  0,  ""  ,1,0,1,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】            
              
    exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,"","",0,"",          1,0,1,0,    @ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""      
    exec usp_sf_sfcl_auto "68071579B77E",2,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,@dxmdm,@xmdm,0,@xmdw,1,@xmsl,1,@xmdj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@xmmc,@lcxmdm=@lcxmdm_i,@hjmxxh="0",@lcxmsl=1,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""      
    exec usp_sf_sfcl_auto "68071579B77E",3,0,1,1,0,@patid,"000","00","2005","00","2002",@zxks_id,"01","","","1",0,0,4,0,0,"","",0,"",          1,0,1,0,    @ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""      
      
    declare @jssjh  ut_sjh   ---预算后的结算收据号      
  select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc             
   --6.药品进行结算处理(收、配、发窗口默认01，)            
 --exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@yfdm,   "01","01","01","1",0,0,3,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=2.50,@isQfbz=0,@jslb=0,@ipdz_gxzsj="172.32.154.71"--【Ztsql】        
          
   exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@zxks_id,"01","01","01","1",0,0,4,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=0.00,@isQfbz=0,@jslb=0,@ipdz_gxzsj="172.32.154.71"      
          
          
           
           
     declare  @zje ut_money ---结算总金额      
        
   ---传出参数jssjh（结算收据号）、zje(总金额)、zfje（支付金额）,cfts(处方付数),yfdm(药房代码),cd_idm(药品idm),ypgg(药品规格)            
  ---cjmc(厂家名称)、mzdj（门诊单价）,ypsl (最小单位的数量)            
  select @jssjh as jssjh,a.zje,a.zfje,lcxmdm,d.name, c.ypmc as 小项目名, yfdm as 执行科室        
   from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c  
   left join YY_LCSFXMK d on c.lcxmdm=d.id            
   where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh 