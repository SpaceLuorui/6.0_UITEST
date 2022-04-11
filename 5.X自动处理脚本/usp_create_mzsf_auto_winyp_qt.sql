CREATE proc [dbo].[usp_create_mzsf_auto_winyp_qt]        
   @cisxh   varchar(20)                                --6.0的医嘱序号         
 as          
SET NOCOUNT ON      
 ------        
 --6.0开立项目信息在5.X收费；不支持药品与项目混合      
 --      
 ---usp_create_mzsf_auto_winyp_qt 1       
 -----        
        
             
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
          
      
       
   --1.预算第一步  
     declare  @yfdm  ut_ksdm   ---HJ库保存的执行科室代码  
  declare  @ksdm_hj  ut_ksdm  ---开方医生所在科室  
  declare  @ysdm   ut_czyh    ----开方医生  
  declare  @patid  ut_xh12  ---患者patid  
  declare  @hjxh   ut_xh12  ----划价序号  
  declare  @cflx   ut_bz    ----处方类型  
  declare  @cfts   smallint  ----处方贴数  
  declare  @ghsjh  ut_sjh    ----挂号收据号  
  declare  @ghxh   ut_xh12   ----挂号序号  
  declare  @sycfbz_hj  ut_bz  ----输液处方标志  
  select @yfdm=a.yfdm,@ksdm_hj=a.ksdm,@ysdm=a.ysdm,@patid=a.patid,@hjxh=a.xh,@cflx=a.cflx,@cfts=a.cfts,@ghsjh=b.jssjh,@sycfbz_hj=sycfbz,@ghxh=b.xh  from SF_HJCFK a inner join GH_GHZDK b on  a.ghxh=b.xh where   a.cisxh=@cisxh  and a.jlzt=0   
     --sjh写死000,czyh写死00,操作员登录科室写死2002  
  --收费窗口窗口写死为01,医保代码写死为1,处方序号写死为0,      
  exec usp_sf_sfcl_auto  "54E1AD566349",1,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,"","",0,"",1,0,@cfts,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
       
   --2.预算第二步(需要拆分明细,判断是否为临床项目,如果为临床项目，需要再拆一次)  
   declare @lcxmdm_hjmx  ut_xmdm   -----明细的临床项目代码  
   declare @dxmdm  ut_kmdm    -----大项目代码  
   declare @xmdm  ut_xmdm     -----小项目代码  
   declare @xmdw  ut_unit     -----项目单位  
   declare @ylsj ut_money     -----零售价格  
   declare @ypmc_xxm  ut_mc64      -----项目名称  
   declare @cfts_hjmx smallint     ----处方贴数  
   declare @ypsl ut_sl10      -----项目数量  
   declare @mxxh ut_xh12      -----明细序号  
      
   declare Cur_mx cursor for (select lcxmdm,dxmdm,ypdm,ypdw,ylsj,ypmc,cfts,ypsl,xh from SF_HJCFMXK where cfxh=@hjxh )  
   open Cur_mx;  
   fetch next from Cur_mx into @lcxmdm_hjmx,@dxmdm,@xmdm,@xmdw,@ylsj,@ypmc_xxm,@cfts_hjmx,@ypsl,@mxxh  
         
   while @@FETCH_STATUS=0  
        begin  
        
       if(@lcxmdm_hjmx ='0' or @lcxmdm_hjmx ='')   
       begin   
          
          ---收据号写死传入为000,操作员号写死00,                    
       ---小项目的执行科室可以更改,写死为2002,收费科室写死2002  
    ----exec usp_sf_sfcl_auto  "54E1AD566349",1,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,"","",0,"",1,0,@cfts,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
                  --  select @lcxmdm_hjmx,@dxmdm,@xmdm,@xmdw,@ylsj,@ypmc_xxm,@cfts_hjmx,@ypsl,@mxxh  
             
        exec usp_sf_sfcl_auto  "54E1AD566349",2,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,@dxmdm,@xmdm,0,@xmdw,1,@ypsl,@cfts_hjmx,@ylsj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@ypmc_xxm,@lcxmdm="0",@hjmxxh=@mxxh,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""   
             
    -- exec usp_sf_sfcl_auto  "54E1AD566349",2,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,@dxmdm,@xmdm,0,@xmdw,1,@ypsl,@cfts_hjmx,@ylsj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@ypmc_xxm,@lcxmdm="0",@hjmxxh=@mxxh,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""   
              
    end  
    else  
       begin  
         declare  @lc_xxmdm  ut_xmdm  --- 临床对应小项目代码  
      declare  @lc_xxm_mc  ut_mc64 --- 临床对应小项目名称  
      declare  @lc_xxm_sl  ut_sl10 --- 临床对应小项目数量  
      declare  @mx_xxmzs   ut_sl10 --- 临床明细对应的小项目的总数，就是ypsl*@lc_xxm_sl  
         
         declare Cur_lcmx cursor for (select a.xmdm,b.name,a.xmsl from YY_LCSFXMDYK a inner join  YY_SFXXMK b on a.xmdm=b.id where a.lcxmdm=@lcxmdm_hjmx)  
         open Cur_lcmx  
      fetch next from Cur_lcmx into @lc_xxmdm,@lc_xxm_mc,@lc_xxm_sl  
      while @@FETCH_STATUS=0  
         begin  
     -- select @mx_xxmzs=@lc_xxm_sl * @ypsl  
     -- select @lc_xxmdm,@lc_xxm_mc,@lc_xxm_sl,@mx_xxmzs  
      select @ylsj,@ghsjh,@ghxh        
     -- exec usp_sf_sfcl_auto  "54E1AD566349",2,0,1,1,0,'3150888',"000","00",'2004','L11046',"2002",'4324',"01","","","1",0,'314165',4,0,0,"11","310701021",0,"小时",1,2,1,'6.00','2020010966010006','3113436',0,0,@yhdj=0,@ypmc="动态血压监测",@lcxmdm="JC6345",@hjmxxh='429442',@lcxmsl=2.00,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""   
                --exec usp_sf_sfcl "00155D90EAD8",2,0,1,1,0,3150888,"000","6601","2004","L11046","2005","4324","02","","","1",0,314165,4,0,0,"11","310701021",0,"小时",1,2,1,6,"2020010966010006",3113436,0,0,@yhdj=0,@ypmc="动态血压监测",@lcxmdm="JC6345",@hjmxxh="429442",@lcxmsl=2,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""  
  
        exec usp_sf_sfcl_auto  "54E1AD566349",2,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,@dxmdm,@lc_xxmdm,0,@xmdw,1,@mx_xxmzs,@cfts,@ylsj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@lc_xxm_mc,@lcxmdm=@lcxmdm_hjmx,@hjmxxh=@mxxh,@lcxmsl=@ypsl,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""   
                 
       fetch next from Cur_lcmx into @lc_xxmdm,@lc_xxm_mc,@lc_xxm_sl        
      end  
                     close Cur_lcmx  
      DEALLOCATE Cur_lcmx  
        
       end  
  
          fetch next from Cur_mx into @lcxmdm_hjmx,@dxmdm,@xmdm,@xmdw,@ylsj,@ypmc_xxm,@cfts_hjmx,@ypsl,@mxxh  
  end  
   close  Cur_mx  
   DEALLOCATE   Cur_mx  
      
   ---预算第三部  
    exec usp_sf_sfcl_auto      "54E1AD566349",3,0,1,1,0,@patid,"000","00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,"","",0,"",1,0,@cfts,0,@ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy="" 
 
    declare @jssjh  ut_sjh   ---预算后的结算收据号    
    select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc   
   --结算  
      
    exec usp_sf_sfcl_ex2_auto  "54E1AD566349",3,2,1,1,0,@patid,@jssjh,"00",@ksdm_hj,@ysdm,"2002",@yfdm,"01","","","1",0,@hjxh,@cflx,@sycfbz_hj,0,"","",0,"",1,0,@cfts,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=0.00,@isQfbz=0,@jslb=0,@ipdz_gxzsj="192.168.173.193"  
  
   --传出  
     select @jssjh as jssjh,a.zje,a.zfje,yfdm as 执行科室,b.xh as 处方号,c.xh as 明细序号    
     from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c          
     where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh  
      
    return  
        
                
           
      
          