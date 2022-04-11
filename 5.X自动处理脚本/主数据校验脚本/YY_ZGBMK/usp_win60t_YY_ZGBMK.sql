      
create  proc  usp_win60t_YY_ZGBMK   
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
   declare @count60_EMPLOYEE_INFO  int          
   declare @count5X_YY_ZGBMK int     
       
   declare @EMPLOYEE_INFO nvarchar(50)    
   select  @EMPLOYEE_INFO=dbo.fun_win60test_addsc('EMPLOYEE_INFO')      
           
   --1.1比较总数          
   declare @sql1   nvarchar(255)       
   select @sql1= 'select @a=count(*)  from '+ @EMPLOYEE_INFO+' where  HOSPITAL_SOID=@_soid'  
   exec sp_executesql  @sql1,N'@a int out,@_soid numeric',@a=@count60_EMPLOYEE_INFO OUTPUT ,@_soid=@hospital_soid    
      
   declare @sql nvarchar(500)        
   select @sql=N'select @a=count(*) from YY_ZGBMK_check'         
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_ZGBMK OUTPUT        
     
     
    
   if(@count5X_YY_ZGBMK <> @count60_EMPLOYEE_INFO)        
   begin           
   insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)        
   values('YY_ZGBMK','总数','EMPLOYEE_INFO','总数','比较总数',@count5X_YY_ZGBMK,@count60_EMPLOYEE_INFO,'数量不一致')   
   end           
      
  
 --处理不存在的数据  
     declare @sqlpd nvarchar(500)   ---用于判断是否存在  
     declare @count_pd int   ---用于判断是否存在  
  declare @id  nvarchar(50)   ---用于判断是否存在  
  declare @sql2 nvarchar(500)   ---用于执行  
          
      declare cur_pd60 cursor for (select id from YY_ZGBMK_check)  
     open cur_pd60  
  fetch next from cur_pd60 into @id  
  while @@FETCH_STATUS=0  
    begin  
           select @sqlpd='select @a1=count(*)  from '+ @EMPLOYEE_INFO+' where EMPLOYEE_NO=@_id and HOSPITAL_SOID=@_soid'   
      exec sp_executesql @sqlpd,N'@a1 int out,@_id nvarchar(12),@_soid numeric ',@a1=@count_pd OUTPUT,@_id=@id ,@_soid=@hospital_soid 
              if(@count_pd=0)  
         begin  
      --将不存在的明细插入错误记录表  
      begin try  
				insert into #error(T_5X,C_5X,T_60,C_60,PK,V_5X,V_60,MSG)        
                  values('YY_ZGBMK','总数','EMPLOYEE_INFO','总数',@id,'','','此条数据在win60不存在')  
      end try  
      begin catch  
		  select '插入记录表失败',error_number()as error_number ,  
		  error_message()as error_message,  
		  error_state()as error_state,  
		  error_severity()as error_severity  
      end catch  
  
  
      ---将不存在的数据更新字段，为后面做具体字段对比数据源，不用每个规则去判断是否存在  
      begin try  
		  select @sql2='update YY_ZGBMK_check set No_in60=1 where id=''@_id'''  
		  exec sp_executesql @sql2,N'@_id nvarchar(50)',@_id=@id  
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
           fetch next from cur_pd60 into @id  
   end  
 close cur_pd60        
    DEALLOCATE cur_pd60  
   
 select * from #error  
        
  drop table #error   
  