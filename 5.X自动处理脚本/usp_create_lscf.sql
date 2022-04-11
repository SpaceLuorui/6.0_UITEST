alter proc [dbo].[usp_create_lscf]     
   @ksdm    ut_ksdm,                    
   @datebegin date, ---取多少天前的有效挂号数据作为集和    
   @dateend  date,    
   @i  int   --- 取有历史处置的第几条挂号记录    
 /****       
      [usp_create_lscf]  '2021','2020-2-19','2020-3-19',142   
 ****/    
 as                      
SET NOCOUNT ON                    
      
     --declare   @datebegin int=10 ,@count int=999    
  create table #ghxh(    
     id int identity(1,1),    
  ghxh ut_xh12,    
  ghrq ut_rq16    
  )    
      
    insert into #ghxh    
  select  distinct a.xh,a.ghrq  from GH_GHZDK a inner join SF_MZCFK b on a.xh=b.ghxh    
   inner join SF_BRJSK c on b.jssjh=c.sjh      
  where CONVERT(date,SUBSTRING(ghrq,1,8)) >=@datebegin and  CONVERT(date,SUBSTRING(ghrq,1,8))<=@dateend and a.ksdm=@ksdm  and b.jlzt=0 
  and isnull(b.hjxh,0)<>0   
  order by ghrq desc    
     --select * from #ghxh    
      
  declare @maxid int    
  select @maxid=max(id) from #ghxh    
  declare @ghxh ut_xh12    
  declare @zje_ghxh ut_money    
  if(@i>@maxid)    
    begin    
    select  +@ksdm+'科室在'+CONVERT(nvarchar,@datebegin)+'到'+CONVERT(nvarchar,@dateend)+'间共有'+CONVERT(nvarchar,@maxid)+'个有效挂号记录;无法取到第'+CONVERT(nvarchar,@i)+'个号'    
    end    
   else    
    begin    
             
        select @ghxh=ghxh from #ghxh where id=@i    
     select @zje_ghxh=sum(zje) from SF_BRJSK where ghxh=@ghxh and  ghsfbz=1  and isnull(tsjh,'')=''  
     --select sum(zje) from SF_BRJSK where ghxh='3200058' and jlzt=0 and ghsfbz=1    
    
    
     select  b.ghrq,b.patid,b.ksdm,b.xh as '挂号序号',c.cfxh as 处方序号,c.hjmxxh as 划价明细序号, d.sjh,c.zje as 单明细总金额,c.ypmc,c.ts as '药品贴数',c.ypsl as '药品数量',@zje_ghxh as '当前挂号下的处方总额'  from SF_MZCFK a inner join GH_GHZDK b on a.ghxh=b.xh     
     inner join  SF_CFMXK  c on  a.xh=c.cfxh     
     inner join  SF_BRJSK  d on  a.jssjh=d.sjh     
     where a.ghxh=@ghxh    
     --and  a.jlzt=0 --0:正常,1:退方,2 红冲,3:家床取 消,4：家床取消红冲,9 预算    
     and isnull(d.tsjh,'')=''  ---退费的取原记录，取退费之前的数据
	 and c.hjmxxh<>0     --手工方不要
    
         
    end    
    
    --select * from #ghxh    
    drop table #ghxh    
    return    