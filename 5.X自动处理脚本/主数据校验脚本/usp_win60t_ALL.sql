alter  proc  usp_win60t_ALL        
   @hisdb   nvarchar(255), ---- his数据库名
   @tabname  nvarchar(255)='',  ---表名
   @keyvalue  nvarchar(255)='' ---主键值        
    /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60t_ALL 'THIS4_LYLT'           
     场景一：不在同一实例下，比较整个库里需要比较的表   usp_win60t_ALL 'WJLS200.THIS4_TEST60'
	 场景二：不在同一实例下，比较单个表                 usp_win60t_ALL 'WJLS200.THIS4_TEST60','SF_BRXXK' 
	 场景三：不在同一实例下，比较单个表的单条数据       usp_win60t_ALL 'WJLS200.THIS4_TEST60','YK_YPCDMLK','5345'   --2269为主键值
      select * from tab_win60t_MDATA where autotest=1 
   */         
 as       
      
   --错误记录表  
   if exists(select 1 from sys.objects where name='win60t_error' and type='U')   
            drop table  win60t_error    
   create table win60t_error(  
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
   
       
   --1取医院的hospitaloid
   declare @sql_hos nvarchar(255)
   declare @hospitaloid nvarchar(255)    
   declare @YY_JBCONFIG nvarchar(255)    
   select  @YY_JBCONFIG=@hisdb +'.dbo.YY_JBCONFIG'  
      
   select @sql_hos='select @_soid=hospitaloid from '+@YY_JBCONFIG+' where hospitaloid is not null'
   begin try    
   exec sp_executesql  @sql_hos,N'@_soid nvarchar(255) out',@_soid=@hospitaloid OUTPUT
   end try
   begin catch
      select '取soid失败,必须处理，无法继续执行',	
						error_number()as error_number ,
						error_message()as error_message,
						error_state()as error_state,
						error_severity()as error_severity
	  return
   end catch 
   select @hospitaloid=CONVERT(numeric,@hospitaloid)
 
   --根据入参走不同流程
   --1.从全库开始
    if(@hisdb<>'')
	begin
        select  tab,col into #taball from tab_win60t_MDATA where autotest=1  and pk=0  --取所有需要自动化执行的数据,非主键列
		    if(@tabname<>'')   ---如果入参有表，则只保留此表
			     delete from #taball where tab<>@tabname
		select  distinct tab into #tablist from #taball
		   
   --复制所有需要导入数据的表，不能用子存储传回，防止单个失败导致整个库的执行失败，因此采用游标处理
   --1.1复制需要的数据
   declare @tab_copy nvarchar(255)
   declare @sql_copy nvarchar(255)    
   declare  cur_tablist cursor for (select tab from #tablist)
   open cur_tablist
   fetch next from cur_tablist into @tab_copy
   while @@FETCH_STATUS=0
         begin
		    begin try
			 --select @tab_copy
             exec tab_win60t_ALL_INIT  @hisdb,@tab_copy,@keyvalue
			 end try
            begin catch   ---捕获一次异常后,再进行第二次尝试,取总数的一半进行拷贝
			   
                        select 'tab_win60t_ALL_INIT 错误'+@hisdb+','+@tabname+','+@keyvalue,	
						error_number()as error_number ,
						error_message()as error_message,
						error_state()as error_state,
						error_severity()as error_severity
						 /**  查找带?的数据,纠正掉 select  * from SF_BRXXK where hzxm like '%/?%' escape'/'  ***/
						 --如果出错，下面不用执行
							delete from #taball where tab=@tab_copy
							delete from #tablist where tab=@tab_copy
				 
            end catch 
			fetch next from cur_tablist into @tab_copy
	      end
    close cur_tablist
	deallocate cur_tablist
	
   --1.2比较表的总数和数据是否存在
    
	declare @tabcount nvarchar(255)=''  ---用于计算总数和表的对比
	declare @sqlzx_count nvarchar(255)=''  ---用于计算总数和表的对比
	declare @proc_name_count nvarchar(255)  ---用于计算总数和表的对比
    
	--处理表总数和数据
	if(@keyvalue='')   --输入key值不用看表数据对比
	  begin
	      
	  declare mycur_tab cursor for (select distinct tab from #tablist)  
    open mycur_tab  
    fetch next from mycur_tab into @tabcount  
      while(@@FETCH_STATUS=0)  
		  begin  
		  select @proc_name_count='usp_win60t_'+@tabcount
		  select @sqlzx_count='insert into win60t_error  exec '+@proc_name_count+' '+@hospitaloid 
		           begin try
				        exec(@sqlzx_count)
						--select * from win60t_error
				     end  try
					 begin catch
					    select @sqlzx_count as 表总数计算错误,
						error_number()as error_number ,
						error_message()as error_message,
						error_state()as error_state,
						error_severity()as error_severity
					 end catch   
		  fetch next from mycur_tab into @tabcount   
		  end  
	 close mycur_tab            
	  DEALLOCATE mycur_tab 
	 end
 
 
    --1.3处理列

	  declare @proc_name nvarchar(255)  
    declare @sqlzx nvarchar(255)='' 
	declare @tab nvarchar(255),@col nvarchar(255) 
		
    declare mycur cursor for (select tab,col from #taball)  
    open mycur  
    fetch next from mycur into @tab,@col  
      while(@@FETCH_STATUS=0)  
		  begin  
		  select @proc_name='usp_win60t_'+@tab+'_'+@col  
		   if(@keyvalue ='')
		  select @sqlzx='insert into win60t_error  exec '+@proc_name+'  '+@hospitaloid  
		   else
          select @sqlzx='insert into win60t_error  exec '+@proc_name+'  '+@hospitaloid+ ','+@keyvalue
		  --insert into win60t_error  exec  usp_win60t_ypcdmlk_ypdm   @hospitaloid   
		           begin try 
				        exec(@sqlzx)
						--select * from win60t_error
				     end  try
					 begin catch
					    select @sqlzx as 子存储执行错误,
						error_number()as error_number ,
						error_message()as error_message,
						error_state()as error_state,
						error_severity()as error_severity
					 end catch 
					 select @sqlzx=''  
		  fetch next from mycur into @tab,@col   
		  end  
	 close mycur            
	  DEALLOCATE mycur 
  end
  else
     select '没有输入数据库链接'

  select * from win60t_error  order by addtime
         
  drop table win60t_error
 