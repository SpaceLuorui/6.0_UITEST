    
alter proc  usp_win60t_usp_win60t_SF_BRXXK     
      @hospital_soid  numeric   ----
	  /****
	    usp_win60t_SF_BRXXK 15
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
   --PERSON_PACK_UNIT_USE_TYPE 应用类型    
   declare @count60_PERSON  int        
   declare @count5X_SF_BRXXK int   
     
    declare @PERSON nvarchar(255)  
   select  @PERSON=dbo.fun_win60test_addsc('PERSON')    
    
	declare @PERSONAL_IDENTITY nvarchar(255)  
   select  @PERSONAL_IDENTITY=dbo.fun_win60test_addsc('PERSONAL_IDENTITY') 
        
   --1.1比较总数        
   declare @sql1   nvarchar(255)     
   select @sql1= 'select @a=count(*)  from '+ @PERSON + ' a inner join '+@PERSONAL_IDENTITY+' b on  a.PERSON_ID=b.PERSON_ID where a.HOSPITAL_SOID=@_soid 
                 and b.IDENTITY_TYPE_CODE=256730' 
  
   exec sp_executesql  @sql1,N'@a int out,@_soid numeric',@a=@count60_PERSON OUTPUT,@_soid=@hospital_soid    
    
   declare @sql nvarchar(500)      
   select @sql=N'select @a=count(*) from SF_BRXXK_check'       
   exec sp_executesql @sql,N'@a int out',@a=@count5X_SF_BRXXK OUTPUT      
   
   
  
   if(@count5X_SF_BRXXK <> @count60_PERSON)      
   begin         
   insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)      
   values('SF_BRXXK','总数','PERSON','总数','比较总数',@count5X_SF_BRXXK,@count60_PERSON,'数量不一致') 
   end   
   
    

	--处理不存在的数据
     declare @sqlpd nvarchar(500)   ---用于判断是否存在
     declare @count_pd int   ---用于判断是否存在
	 declare @sql2 nvarchar(500)   ---用于执行
	 
	  declare @patidmax int
      declare @patidmin int
	  declare @patid_str nvarchar(255)
      select @patidmax=max(patid),@patidmin=min(patid) from SF_BRXXK_check  
	       
      
		while(@patidmin<=@patidmax)  
		  begin
		   
		      if not exists(select patid from SF_BRXXK_check(nolock) where patid=@patidmin)
			      begin
				       select @patidmin= @patidmin+1
			           continue

				  end
	          select @sqlpd='select @a1=count(b.IDENTITY_NO)  from '+ @PERSON+' a inner join '+@PERSONAL_IDENTITY+' b on  a.PERSON_ID=b.PERSON_ID where a.HOSPITAL_SOID=@_soid 
                 and b.IDENTITY_TYPE_CODE=256730 and b.IDENTITY_NO=@_patid' 
			   exec sp_executesql @sqlpd,N'@a1 int out,@_patid nvarchar(12),@_soid numeric',@a1=@count_pd OUTPUT,@_patid=@patidmin,@_soid=@hospital_soid
              if(@count_pd=0)
			      begin
				  --将不存在的明细插入错误记录表
				  insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)      
                  values('SF_BRXXK','总数','PERSON','总数',@patidmin,'','','此条数据在win60不存在')
				  
				  ---将不存在的数据更新字段，为后面做具体字段对比数据源，不用每个规则去判断是否存在
				  select @patid_str=convert(nvarchar,@patidmin)
				  select @sql2='update SF_BRXXK_check set No_in60=1 where patid='+@patid_str
				 -- select @sql2
				   exec(@sql2)
				  select @sql2=''
				  end
              
			  select @sqlpd=''
			  select @count_pd=0
			  select @patidmin= @patidmin+1
		 end
	 
	
	select * from #error
      
  drop table #error 

  