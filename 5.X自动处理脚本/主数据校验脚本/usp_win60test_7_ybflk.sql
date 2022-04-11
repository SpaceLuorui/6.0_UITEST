  
ALTER  proc  usp_win60test_autocheck_ybflk       
   @hisdb   nvarchar(255) ---- his数据库名       
   /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60test_autocheck_ybflk 'THIS4_LYLT'           
               不在同一实例下，如链接服务器为WJLS200,则   usp_win60test_autocheck_zgdmk 'WJLS200.THIS4_TEST60'  
   */     
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
  C_60_filter   nvarchar(2500) null,  -- 1对多关系时，用于选出1对1,来对应5.X的字段  
  V_60 nvarchar(255) null    ---60中的表的列的值  
      
  )       
    
 
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题    
   declare @YY_YBFLK  nvarchar(255)       
   select  @YY_YBFLK= @hisdb +'.dbo.YY_YBFLK'    
   declare @copysql nvarchar(255)    
      --物理表性能更好,及时删除即可     
   if exists(select 1 from sys.all_objects where name='YY_YBFLK_check')    
       drop table  YY_YBFLK_check    
   select  @copysql='select * into  YY_YBFLK_check from '+@YY_YBFLK    
   exec(@copysql)       
    
   --1.1比较医疗保险  
   --MED_INSTI_MEDICAL_INSURANCE  医疗保险
   --YY_YBFLK  医保分类库  
   
   declare @count60_MED_INSTI_MEDICAL_INSURANCE  int    
   declare @count5X_YY_YBFLK int  

   declare @MED_INSTI_MEDICAL_INSURANCE nvarchar(255)  
   select  @MED_INSTI_MEDICAL_INSURANCE=dbo.fun_win60test_addsc('MED_INSTI_MEDICAL_INSURANCE')  
   
   declare @sql1   nvarchar(255)   
   select @sql1= 'select @a=count(*)  from '+ @MED_INSTI_MEDICAL_INSURANCE  
   exec sp_executesql  @sql1,N'@a int out',@a=@count60_MED_INSTI_MEDICAL_INSURANCE OUTPUT  
      
   declare @sql nvarchar(500)    
   select @sql=N'select @a=count(*) from YY_YBFLK_check'     
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_YBFLK OUTPUT    
    
   if(@count60_MED_INSTI_MEDICAL_INSURANCE <> @count5X_YY_YBFLK)    
     begin       
	  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)    
	  values('医疗保险信息总数对比','医疗保险数量不一致','YY_YBFLK','MED_INSTI_MEDICAL_INSURANCE',@count5X_YY_YBFLK,@count60_MED_INSTI_MEDICAL_INSURANCE)    
	 end    
       
    
   --1.2比较医疗保险,可以直接在两个表中查询到列对比    
  declare  @ybdm  nvarchar(12)  --YY_YBFLK.ybdm  ,主键  
  declare  @sqlpd nvarchar(2500)=''   ---用于判断是否存在的sql语句  
  declare  @sqlcx nvarchar(2500)='' --用于查询
  declare  @count_pd int --用于保留数据量  
    
  declare  @ybsm  nvarchar(64)  --YY_YBFLK.ybsm    
  declare  @ybsm_60  nvarchar(64) --关联MED_INSTI_MEDICAL_INSURANCE.MED_INSTI_INSUR_NAME  
  
  declare  @py  nvarchar(64)  --YY_YBFLK.py    
  declare  @py_60  nvarchar(64) --关联MED_INSTI_MEDICAL_INSURANCE.PINYIN  
  
  declare  @wb  nvarchar(64)  --YY_YBFLK.wb    
  declare  @wb_60  nvarchar(64) --关联MED_INSTI_MEDICAL_INSURANCE.WUBI  
  
  declare  @ybspbz  nvarchar(256)  --YY_YBFLK.ybspbz 
  declare  @ybspbz_60  nvarchar(256) --MED_INSTI_MEDICAL_INSURANCE.MED_INSUR_APPROVAL_NEEDED_FLAG   

  declare cur_zg cursor for (select ybdm,ybsm,py,wb from YY_YBFLK_check) --ybspbz THIS4_LYLT暂时没有这个字段 不比较
  open cur_zg    
  fetch next from cur_zg into @ybdm,@ybsm,@py,@wb
  while @@FETCH_STATUS=0    
    begin    
     --取win60的数据,有可能在win60没有此数据  
        
      select @sqlpd='select @a1=count(*)  from '+ @MED_INSTI_MEDICAL_INSURANCE +' where MED_INSTI_INSUR_NO=@_ybdm' 
      exec sp_executesql @sqlpd,N'@a1 int out,@_ybdm nvarchar(20)',@a1=@count_pd OUTPUT,@_ybdm=@ybdm    
      if(@count_pd=1 or @count_pd=2)    
      begin  
		select @sqlcx='select @_ybsm=MED_INSTI_INSUR_NAME,@_py=PINYIN,@_wb=WUBI from '+@MED_INSTI_MEDICAL_INSURANCE +' where MED_INSTI_INSUR_NO=@_ybdm'
		exec sp_executesql @sqlcx,N'@_ybdm nvarchar(20),@_ybsm nvarchar(64) out, @_py nvarchar(64) out, @_wb nvarchar(64) out ',@_ybdm=@ybdm,@_ybsm=@ybsm_60 output,@_py=@py_60 OUTPUT,@_wb=@wb_60 OUTPUT
	  

	if(@ybsm<>@ybsm_60)
	insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
	values('医疗保险信息明细对比','列值不一致','YY_YBFLK','MED_INSTI_MEDICAL_INSURANCE',@ybdm,@ybdm,'ybsm',@ybsm,'MED_INSTI_INSUR_NAME',@ybsm_60) 

	if(@py<>@py_60)
	insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
	values('医疗保险信息明细对比','列值不一致','YY_YBFLK','MED_INSTI_MEDICAL_INSURANCE',@ybdm,@ybdm,'py',@py,'PINYIN',@py_60) 

	if(@wb<>@wb_60)
	insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
	values('医疗保险信息明细对比','列值不一致','YY_YBFLK','MED_INSTI_MEDICAL_INSURANCE',@ybdm,@ybdm,'wb',@wb,'WUBI',@wb_60) 
  end    
    
    
  else    
     --win60没有此数据           
     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)    
     values('药房信息明细对比','5X有数据,win60没数据','YF_YFDMK','MED_INSTI_INSUR_NAME',@ybdm,'','ybsm',@ybsm)      
          
    
  select @sqlpd='' --清空判断的sql  
  select @sqlcx='' --清空取值的sql  

  select @count_pd=0 --初始化为0         
  fetch next from cur_zg into @ybdm,@ybsm,@py,@wb  
end    
  close cur_zg        
  DEALLOCATE cur_zg     
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',    
  V_5X '5X列值',C_60 '60列名',V_60 '60列值',C_60_filter '60的关联' from #error    
  drop table #error    
end  
  
  
   
        