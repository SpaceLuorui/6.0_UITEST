      
create  proc  usp_win60t_YY_YBFLK   
@hospital_soid  numeric    
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
   declare @count60_MED_INSTI_MEDICAL_INSURANCE  int          
   declare @count5X_YY_YBFLK int     
       
   declare @MED_INSTI_MEDICAL_INSURANCE nvarchar(50)    
   select  @MED_INSTI_MEDICAL_INSURANCE=dbo.fun_win60test_addsc('MED_INSTI_MEDICAL_INSURANCE')      
           
   --1.1比较总数          
   declare @sql1   nvarchar(255)       
   select @sql1= 'select @a=count(*)  from '+ @MED_INSTI_MEDICAL_INSURANCE+' where  HOSPITAL_SOID=@_soid'  
   exec sp_executesql  @sql1,N'@a int out,@_soid numeric',@a=@count60_MED_INSTI_MEDICAL_INSURANCE OUTPUT ,@_soid=@hospital_soid    
      
   declare @sql nvarchar(500)        
   select @sql=N'select @a=count(*) from YY_YBFLK_check'         
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_YBFLK OUTPUT        
     
     
    
   if(@count5X_YY_YBFLK <> @count60_MED_INSTI_MEDICAL_INSURANCE)        
   begin           
   insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)        
   values('YY_YBFLK','总数','MED_INSTI_MEDICAL_INSURANCE','总数','比较总数',@count5X_YY_YBFLK,@count60_MED_INSTI_MEDICAL_INSURANCE,'数量不一致')   
   end           
      
  
 --处理不存在的数据  
     declare @sqlpd nvarchar(500)   ---用于判断是否存在  
     declare @count_pd int   ---用于判断是否存在  
  declare @ybdm  nvarchar(50)   ---用于判断是否存在  
  declare @sql2 nvarchar(500)   ---用于执行  
          
      declare cur_pd60 cursor for (select ybdm from YY_YBFLK_check)  
     open cur_pd60  
  fetch next from cur_pd60 into @ybdm  
  while @@FETCH_STATUS=0  
    begin  
           select @sqlpd='select @a1=count(*)  from '+ @MED_INSTI_MEDICAL_INSURANCE+' where MED_INSTI_INSUR_NO=@_ybdm and HOSPITAL_SOID=@_soid'   
      exec sp_executesql @sqlpd,N'@a1 int out,@_ybdm nvarchar(12),@_soid numeric ',@a1=@count_pd OUTPUT,@_ybdm=@ybdm ,@_soid=@hospital_soid 
              if(@count_pd=0)  
         begin  
      --将不存在的明细插入错误记录表  
      begin try  
				insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)        
                  values('YY_YBFLK','总数','EMPLOYEE_INFO','总数',@ybdm,'','','此条数据在win60不存在')  
      end try  
      begin catch  
		  select '插入记录表失败',error_number()as error_number ,  
		  error_message()as error_message,  
		  error_state()as error_state,  
		  error_severity()as error_severity  
      end catch  
  
  
      ---将不存在的数据更新字段，为后面做具体字段对比数据源，不用每个规则去判断是否存在  
      begin try  
		  select @sql2='update YY_YBFLK_check set No_in60=1 where ybdm=''@_ybdm'''  
		  exec sp_executesql @sql2,N'@_ybdm nvarchar(50)',@_ybdm=@ybdm  
		  select @sql2=''  
      end try  
      begin catch  
		  select '更新check表失败',error_number()as error_number ,  
		  error_message()as error_message,  
		  error_state()as error_state,  
		  error_severity()as error_severity  
      end catch  
  
      end  
                
     select @sqlpd=''  
     select @count_pd=0  
           fetch next from cur_pd60 into @ybdm  
   end  
 close cur_pd60        
    DEALLOCATE cur_pd60  
   
 select * from #error  
        
  drop table #error   
  