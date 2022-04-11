    
alter proc  usp_win60t_YK_YPCDMLK     
      @hospital_soid  numeric   ----
	  /****
	    usp_win60t_YK_YPCDMLK 15
	  ****/
 as 
	  ---不放在父存储，便于单独调试
		  create table #error(
		  T_5X  nvarchar(255) not null,   ---5X中的表名
		  C_5X nvarchar(255) not null,   ---5X中的表的列
		  PK nvarchar(255) not null,   --5X中的主键的值 或表数据总数
		  V_5X nvarchar(255) not null,   ---5X中的表的列的值
		  V_60 nvarchar(255) not null,    ---60中的表的列的值        
		  T_60  nvarchar(255) not null,   ---60中的表名
		  C_60 nvarchar(255) not null,   ---60中的表的列
		  MSG nvarchar(255) not null,
		  addtime time not null default getdate()
  ) 
   
     --1.1比较药品  
   --MEDICINE_PACK_UNIT_USE_TYPE 应用类型    
   declare @count60_MEDICINE  int        
   declare @count5X_YK_YPCDMLK int   
     
    declare @MEDICINE nvarchar(255)  
   select  @MEDICINE=dbo.fun_win60test_addsc('MEDICINE')    
         
   --1.1比较总数        
   declare @sql1   nvarchar(255)     
   select @sql1= 'select @a=count(*)  from '+ @MEDICINE + ' where HOSPITAL_SOID=@_soid ' 
  
   exec sp_executesql  @sql1,N'@a int out,@_soid numeric',@a=@count60_MEDICINE OUTPUT,@_soid=@hospital_soid    
    
   declare @sql nvarchar(500)      
   select @sql=N'select @a=count(*) from YK_YPCDMLK_check'       
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YK_YPCDMLK OUTPUT      
   
   
  
   if(@count5X_YK_YPCDMLK <> @count60_MEDICINE)      
   begin         
   insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)      
   values('YK_YPCDMLK','总数','MEDICINE','总数','比较总数',@count5X_YK_YPCDMLK,@count60_MEDICINE,'数量不一致') 
   end         
    

	--处理不存在的数据
     declare @sqlpd nvarchar(500)   ---用于判断是否存在
     declare @count_pd int   ---用于判断是否存在
	 declare @idm  nvarchar(255)   ---用于判断是否存在
	 declare @sql2 nvarchar(500)   ---用于执行
	       
      declare cur_pd60 cursor for (select idm from YK_YPCDMLK_check)
	    open cur_pd60
		fetch next from cur_pd60 into @idm
		while @@FETCH_STATUS=0
		  begin
	          select @sqlpd='select @a1=count(*)  from '+ @MEDICINE+' where MED_EXT_REF_ID=@_idm and HOSPITAL_SOID=@_soid' 
			   exec sp_executesql @sqlpd,N'@a1 int out,@_idm nvarchar(12),@_soid numeric',@a1=@count_pd OUTPUT,@_idm=@idm,@_soid=@hospital_soid
              if(@count_pd=0)
			      begin
				  --将不存在的明细插入错误记录表
				  insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)      
                  values('YK_YPCDMLK','总数','MEDICINE','总数',@idm,'','','此条数据在win60不存在')
				  
				  ---将不存在的数据更新字段，为后面做具体字段对比数据源，不用每个规则去判断是否存在
				  select @sql2='update YK_YPCDMLK_check set No_in60=1 where idm='+@idm
				  exec(@sql2)
				  select @sql2=''
				  end
              
			  select @sqlpd=''
			  select @count_pd=0
           fetch next from cur_pd60 into @idm
		 end
	close cur_pd60      
    DEALLOCATE cur_pd60
	
	select * from #error
      
  drop table #error 

  