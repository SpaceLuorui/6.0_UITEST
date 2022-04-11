CREATE proc  usp_win60test_autocheck_organization   
   @hisdb   nvarchar(20) ---- his数据库名    
 as  
 begin    
  --错误记录表    
  create table #error(    
  id int identity(1,1),    
  checktype nvarchar(255) not null,  ---对比项    
  checkres  nvarchar(255) not null,  ---对比结果    
  T_5X  nvarchar(255) null,   ---5X中的表名    
  T_60  nvarchar(255) null,   ---60中的表名    
  AP_5X nvarchar(255) null,   --5X中的主键的值 或表数据总数    
  AP_60  nvarchar(255) null,  --60中的主键的值 或表数据总数    
  C_5X nvarchar(255) null,   ---5X中的表的列    
  V_5X nvarchar(255) null,   ---5X中的表的列的值    
  C_60 nvarchar(255) null,   ---60中的表的列    
  V_60 nvarchar(255) null    ---60中的表的列的值    
  )   
 --1比较科室信息    
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题    
  declare @YY_KSBMK  nvarchar(255)       
  select  @YY_KSBMK= @hisdb +'.dbo.YY_KSBMK'    
  declare @copysql nvarchar(255)    
      --物理表性能更好,及时删除即可     
  if exists(select 1 from sys.all_objects where name='YY_KSBMK_check')    
    drop table  YY_KSBMK_check    
  select  @copysql='select * into  YY_KSBMK_check from '+@YY_KSBMK    
  exec(@copysql)       
  
  --1.1比较科室总数    
  declare @count60_ORGANIZATION  int    
  declare @count5X_YY_KSBMK int    
  select @count60_ORGANIZATION=count(*)  from ORGANIZATION where ORG_TYPE_CODE=230267 --从组织表中只查科室的数据  
  declare @sql nvarchar(500)    
  select @sql=N'select @a=count(*) from YY_KSBMK_check'     
  exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_KSBMK OUTPUT    
      
  if(@count5X_YY_KSBMK <> @count60_ORGANIZATION)    
   begin       
 insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)    
 values('科室信息总数对比','科室数量不一致','YY_KSBMK','EMPLOYEE_INFO',@count5X_YY_KSBMK,@count60_ORGANIZATION)    
   end  
  
  --1.2比较科室明细,可以直接在两个表中查询到列对比      
  declare  @id  char(12)  --YY_KSBMK.id  ,主键     
       
  declare  @name  varchar(64)  --YY_KSBMK.name      
  declare  @name_60  varchar(64) --ORGANIZATION.ORG_NAME    
  
  declare  @py  varchar(64)  --YY_KSBMK.py      
  declare  @py_60  varchar(64) --ORGANIZATION.PINYIN     
    
  declare  @wb  varchar(64)  --YY_KSBMK.wb      
  declare  @wb_60  varchar(64) --ORGANIZATION.WUBI  
    
  declare  @jlzt  varchar(64)  --YY_KSBMK.jlzt      
  declare  @jlzt_60  varchar(64) --ORGANIZATION.ORG_STATUS     
  
  declare cur_ks cursor for (select id,name,py,wb,jlzt from YY_KSBMK_check)      
  open cur_ks      
  fetch next from cur_ks into @id,@name,@py,@wb,@jlzt  
  while @@FETCH_STATUS=0      
  begin  
   if exists(select 1 from ORGANIZATION where ORG_NO=@id )      
 begin  
  select @name_60=ORG_NAME,@py_60=PINYIN,@wb_60=WUBI,  
  @jlzt_60=case ORG_STATUS  
  when '98360' THEN '0' --启用  
  when '98361' THEN '1' --停用  
  when '256647' then '256647(60代表新增)' --6.0代表新增  
  when '390031745' then '390031745(60代表发布)' --6.0代表新增  
  --when ''then '空值'  
  else '未找到对应关系'  
  end  
  from ORGANIZATION where ORG_NO=@id  
  
  if(@name <> @name_60)      
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
  values('科室信息明细对比','列值不一致','YY_KSBMK','ORGANIZATION',@id,@id,'name',@name,'ORG_NAME',@name_60)   
      
  if(@py <> @py_60)      
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)    
      
  values('科室信息明细对比','列值不一致','YY_KSBMK','ORGANIZATION',@id,@id,'py',@py,'PINYIN',@py_60)     
  if(@wb <> @wb_60)      
  
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
  values('科室信息明细对比','列值不一致','YY_KSBMK','ORGANIZATION',@id,@id,'wb',@wb,'WUBI',@wb_60)   
  
  if(@jlzt <> @jlzt_60)      
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
  values('科室信息明细对比','列值不一致','YY_KSBMK','ORGANIZATION',@id,@id,'jlzt',@jlzt,'ORG_STATUS',@jlzt_60)  
  
    
    
 end  
 else  --win60没有此数据             
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)      
  values('科室信息明细对比','5X有数据,win60没数据','YY_KSBMK','ORGANIZATION',@id,'','name',@name)        
                   
  fetch next from cur_ks into @id,@name,@py,@wb,@jlzt      
  end       
  close cur_ks          
  DEALLOCATE cur_ks   
  
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',      
  V_5X '5X列值',C_60 '60列名',V_60 '60列值' from #error      
  drop table #error  
     
 end 