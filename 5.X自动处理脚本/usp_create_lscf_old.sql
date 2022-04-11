alter proc [dbo].[usp_create_lscf_old]               
   @lb   ut_kmdm,
   @ksdm   ut_ksdm 
 /****01.随机取只有一个西药, 
      02.随机取只有一个中药,
	  03.随机取只有一个中草药, 
	  04.随机取只有一个小项目 ,
	  05.随机取只有一个临床项目,
	  06,取最大组合(cflx种类最多,数量最少的情况 )        
 
 ****/as                
SET NOCOUNT ON              
 ------              
    ---usp_create_lscf '06','2021'
     declare @maxcfxh ut_xh12
	 
   -- 只有一条处方一条药品明细(根据最大处方号取)
     if (@lb='01' or @lb='02' or @lb='03')
	   begin  
	   create table #tab_lb1( id int identity(1,1),cfxh ut_xh12 )
	 insert into  #tab_lb1(cfxh)    
	 select   a.cfxh from SF_HJCFMXK a inner join SF_HJCFK b on a.cfxh=b.xh where a.dxmdm=@lb and a.cd_idm<> '0' 
	 and b.ghxh in (select a.ghxh from SF_HJCFK a inner join GH_GHZDK b on a.ghxh=b.xh and b.ksdm=@ksdm group by ghxh having count(*)=1) 
	 group by cfxh  having count(*)=1 order by cfxh desc
	 declare @zs1 int
	 select @zs1=MAX(id) from #tab_lb1
	 select  @maxcfxh=cfxh from  #tab_lb1   where id=cast( floor(rand()*@zs1) as int)   
     select d.patid as patid,d.blh as blh,d.hzxm as '患者姓名',d.sex as '性别',d.cardno as '患者卡号',a.ghxh as '挂号序号',c.ghhx as '挂号号序', c.ksdm as '挂号科室',c.ghrq as '挂号日期' ,a.xh as '划价处方序号',b.ypdm as 药品或项目代码,b.ypmc as 药品或项目名 from SF_HJCFK a inner join SF_HJCFMXK b on a.xh=b.cfxh 
	 and b.dxmdm=@lb and b.cd_idm<> '0'
	 inner join GH_GHZDK c on a.ghxh=c.xh
	 inner join SF_BRXXK d on a.patid=d.patid   where a.xh=@maxcfxh 
	   return      
       end
   ---只有一条小项目明细(非临床项目)      
     else if @lb='04'
	    begin
		create table #tab_lb4( id int identity(1,1),cfxh ut_xh12 )
	 insert into  #tab_lb4(cfxh)
     select   a.cfxh from SF_HJCFMXK a inner join SF_HJCFK b on a.cfxh=b.xh where  a.cd_idm= '0' and lcxmdm='0'
	 and b.ghxh in (select a.ghxh from SF_HJCFK a inner join GH_GHZDK b on a.ghxh=b.xh and b.ksdm=@ksdm group by ghxh having count(*)=1) 
	 group by cfxh  having count(*)=1 order by cfxh desc
	 declare @zs4 int
	 select @zs4=MAX(id) from #tab_lb4
	 select  @maxcfxh=cfxh from  #tab_lb4  where id=cast( floor(rand()*@zs4) as int)   
     select d.patid as patid,d.blh as blh,d.hzxm as '患者姓名',d.sex as '性别',d.cardno as '患者卡号',a.ghxh as '挂号序号',c.ghhx as '挂号号序', c.ksdm as '挂号科室',c.ghrq as '挂号日期' ,a.xh as '划价处方序号',b.ypdm as 药品或项目代码,b.ypmc as 药品或项目名 from SF_HJCFK a inner join SF_HJCFMXK b on a.xh=b.cfxh 
	 and b.cd_idm= '0' and lcxmdm='0'
	 inner join GH_GHZDK c on a.ghxh=c.xh
	 inner join SF_BRXXK d on a.patid=d.patid   where a.xh=@maxcfxh
	   return      
       end
   --只有一条临床项目明细()       
   else if @lb='05'
	    begin
		create table #tab_lb5( id int identity(1,1),cfxh ut_xh12 )
	 insert into  #tab_lb5(cfxh)
     select   a.cfxh from SF_HJCFMXK a inner join SF_HJCFK b on a.cfxh=b.xh where  a.cd_idm= '0' and lcxmdm<>'0'
	 and b.ghxh in (select a.ghxh from SF_HJCFK a inner join GH_GHZDK b on a.ghxh=b.xh and b.ksdm=@ksdm group by ghxh having count(*)=1) 
	 group by cfxh  having count(*)=1 order by cfxh desc
	 declare @zs5 int
	 select @zs5=MAX(id) from #tab_lb5
	 select  @maxcfxh=cfxh from  #tab_lb5  where id=cast( floor(rand()*@zs5) as int)   
     select d.patid as patid,d.blh as blh,d.hzxm as '患者姓名',d.sex as '性别',d.cardno as '患者卡号',a.ghxh as '挂号序号',c.ghhx as '挂号号序', c.ksdm as '挂号科室',c.ghrq as '挂号日期' ,a.xh as '划价处方序号',b.ypdm as 药品或项目代码,b.ypmc as 药品或项目名 from SF_HJCFK a inner join SF_HJCFMXK b on a.xh=b.cfxh 
	 and b.cd_idm= '0' and lcxmdm<>'0'
	 inner join GH_GHZDK c on a.ghxh=c.xh
	 inner join SF_BRXXK d on a.patid=d.patid   where a.xh=@maxcfxh
	   return      
       end
   --取最大可能性,同一挂号序号下cflx种类最多,数量最少的情况 
   else if @lb='06' 
      begin
	  declare @ghxhmax ut_xh12
	  select  top 1 @ghxhmax=ghxh from SF_HJCFK a inner join GH_GHZDK b on a.ghxh=b.xh group by ghxh  order by count(distinct cflx) desc, count(*) asc
	  --select @ghxhmax	   
	 
	 select d.patid as patid,d.blh as blh,d.hzxm as '患者姓名',d.cardno as '患者卡号',d.sex as '性别',a.ghxh as '挂号序号',c.ghhx as '挂号号序', a.cflx as 处方类型,c.ksdm as '挂号科室',c.ghrq as '挂号日期' ,a.xh as '划价处方序号',b.ypdm as 药品或项目代码,b.ypmc as 药品或项目名,b.lcxmdm as 临床项目代码, e.name as 临床项目名称  from SF_HJCFK a 
	  inner join SF_HJCFMXK b on a.xh=b.cfxh 
	 inner join GH_GHZDK c on a.ghxh=c.xh
	 inner join SF_BRXXK d on a.patid=d.patid  
	 left join YY_LCSFXMK e on b.lcxmdm=e.id where c.xh=@ghxhmax
	   return      
       end     
              

