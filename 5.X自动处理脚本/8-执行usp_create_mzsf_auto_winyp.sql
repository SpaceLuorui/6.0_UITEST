
CREATE proc [dbo].[usp_create_mzsf_auto_winyp_wj]        
   @cisxh   varchar(20)                                --6.0的医嘱序号         
 as          
SET NOCOUNT ON      
 ------        
 --6.0开立药品信息在5.X收费；      
 --假设每一个医嘱只有一条药品明细，如果多了是联动或者其他产生，需要人工确认一下,此存储过程不对比      
 ---usp_create_mzsf_auto_winyp 68921675183104001       
 -----        
        
 begin          
   --1.判断5.X是否存在有效的医嘱记录       
   if  not exists(select 1 from  SF_HJCFK where jlzt=0 and  cisxh=@cisxh)      
   if  @@ERROR<>0 or  @@ROWCOUNT=0      
   begin      
   select 'F','根据传入的医嘱序号没找到有效的划价记录'        
   return         
   end       
   declare @countsl  int         
   select @countsl=  count(*)   from SF_HJCFMXK where  cfxh=(select xh from    SF_HJCFK where jlzt=0 and  cisxh=@cisxh )      
   if @countsl=0        
   begin        
     select 'F','根据传入的医嘱序号没找到有效的划价明细记录'        
   return        
   end       
   --if @countsl>1      
   --begin        
   --  select 'F','药品处方对应的明细数量大于1，需要人工检查'        
   --return        
   --end       
       
        
   --2.取患者的基本信息        
   declare @patid  ut_xh12        
   declare @ghsjh  ut_sjh        
        
   --3.定义结算需要使用的变量        
   --declare @idm  int         
   declare @cflx  int         
           
   declare @jssjh  varchar(50)   --结算收据号        
   declare @cjmc varchar(200)        
   declare @hjxh ut_xh12     ---hjxh      
   declare @cfts  int            ---处方付数
   --declare @hjmxxh  ut_xh12          ---hjmxxh      
   --declare  @pcdm  varchar(2)  ---频次代码名称      
   declare  @pcmc varchar(10)      
   --declare  @yfid  varchar(10) ---用法代码名称      
   declare  @yfmc varchar(10)      
   declare @yfdm  ut_ksdm    --药房代码      
   declare @dxmdm  varchar(50)   --大项目代码      
   declare @ghxh ut_xh12       
      
     
   select @patid=a.patid,@yfdm=a.yfdm,@hjxh=a.xh ,@cflx=a.cflx,@cfts=a.cfts ,@ghsjh=c.jssjh       
   from SF_HJCFK a      
  -- inner  join SF_HJCFMXK b on a.xh=b.cfxh      
   inner join GH_GHZDK c on a.ghxh=c.xh      
   where a.jlzt=0 and c.jlzt=0  and  a.cisxh=@cisxh      
   if @@error<> 0 or   @@ROWCOUNT=0 or  @@ROWCOUNT>1      
   begin        
     select 'F','取药品处方的基本信息有误'        
   return        
   end      
               
   --5.药品进行预算处理(科室代码默认2005,医生默认00,收费科室代码默认2002,收费窗口代码01,医保代码是1 )        
   exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,@hjxh,@cflx,0,0,"",   "",  0,  ""  ,1,0,@cfts,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】       
 
     
   declare @ypdm ut_xmdm  
   declare @cd_idm ut_xh9  
   declare @ypdw ut_unit  
   declare @ypsl ut_money  
   declare @ypxs ut_dwxs  
   declare @cfts_mx  smallint  
   declare @ylsj ut_money  
   declare @ypmc ut_mc64  
   declare @hjmxxh_mx ut_xh12  
   declare mycur cursor for (select dxmdm,ypdm,cd_idm,ypdw,ypxs,ypsl,cfts,ylsj,ypmc,xh from SF_HJCFMXK where cfxh=@hjxh)  
   open mycur  
   fetch next from mycur into @dxmdm,@ypdm,@cd_idm,@ypdw,@ypxs,@ypsl,@cfts_mx,@ylsj,@ypmc,@hjmxxh_mx  
      while @@FETCH_STATUS=0    
         begin 
		 select @ypsl=@ypsl/@ypxs   
   exec usp_sf_sfcl_auto  "68071579B77E",2,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,@hjxh,@cflx,0,0,@dxmdm,@ypdm,@cd_idm,@ypdw,@ypxs,@ypsl,@cfts_mx,@ylsj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@ypmc,@lcxmdm="0",@hjmxxh=@hjmxxh_mx,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】        
   fetch next from mycur into @dxmdm,@ypdm,@cd_idm,@ypdw,@ypxs,@ypsl,@cfts_mx,@ylsj,@ypmc,@hjmxxh_mx  
         end  
   close mycur  
   DEALLOCATE mycur  
     
   exec usp_sf_sfcl_auto  "68071579B77E",3,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,@hjxh,@cflx,0,0,"","",0,"",1,0,@cfts,0,@ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--【Ztsql】        



    
   select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc         
   --6.药品进行结算处理(收、配、发窗口默认01，)    
         
   exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@yfdm,"01","01","01","1",0,0,@cflx,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=2.50,@isQfbz=0,@jslb=0,@ipdz_gxzsj="172.32.154.71"         
      
          
           
   ---传出参数jssjh（结算收据号）、zje(总金额)、zfje（支付金额）,cfts(处方付数),yfdm(药房代码),cd_idm(药品idm),ypgg(药品规格)        
   ---cjmc(厂家名称)、mzdj（门诊单价）,ypsl (最小单位的数量)       
   ---,pcdm(频次代码)、pcmc（频次名称）,@yfid(用法代码)、@yfmc（用法名称）      
   select @jssjh as jssjh,a.zje '总金额',a.zfje '自负金额',b.cfts,b.yfdm,c.cd_idm,c.ypgg, c.ylsj as '药零售价',c.ypsl as '药品数量',c.ypmc  as  ypmc     
       
   from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c        
   where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh         
end 

