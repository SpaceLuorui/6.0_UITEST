alter  proc  usp_win60test_autocheck_cjdmk    
   @hisdb   nvarchar(255) ---- his数据库名     
   /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60test_autocheck_cjdmk 'THIS4_LYLT'         
               不在同一实例下，如链接服务器为WJLS200,则   usp_win60test_autocheck_cjdmk 'WJLS200.THIS4_TEST60'
			   			 
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
  
   --1比较药房信息  
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题  
   declare @YK_CJDMK  nvarchar(255)     
   select  @YK_CJDMK= @hisdb +'.dbo.YK_CJDMK'  
   declare @copysql nvarchar(255)  
      --物理表性能更好,及时删除即可   
   if exists(select 1 from sys.all_objects where name='YK_CJDMK_check')  
       drop table  YK_CJDMK_check  
   select  @copysql='select * into  YK_CJDMK_check from '+@YK_CJDMK 
   exec(@copysql)     
    
    
   --1.1比较药房
   --ORGANIZATION  组织
   --DRUG_STORAGE 库房
   --PHARMACY   药房
   --STOREHOUSE 药库
   declare @count60_ORGANIZATION_CJ  int  
   declare @count5X_YK_CJDMK int    

   declare @ORGANIZATION nvarchar(255)
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION')
   
   
   declare @sql1   nvarchar(255) 
   select @sql1= 'select @a=count(*)  from '+ @ORGANIZATION+ ' where ORG_TYPE_CODE=152643'
   exec sp_executesql  @sql1,N'@a int out',@a=@count60_ORGANIZATION_CJ OUTPUT

   
   declare @sql nvarchar(500)  
   select @sql=N'select @a=count(*) from YK_YKDMK_check'   
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YK_CJDMK OUTPUT  

    
   --select @count5X_YY_ZGBMK  
   if(@count60_ORGANIZATION_CJ <> @count5X_YK_CJDMK)  
     begin     
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)  
  values('制造商信息总数对比','制造商数量不一致','YK_YKDMK','DRUG_STORAGE',@count5X_YK_CJDMK,@count60_ORGANIZATION_CJ)  
  end  
     
  
   --1.2比较药房,可以直接在两个表中查询到列对比  
  declare  @id  nvarchar(12)  --YF_YFDMK.id  ,主键
  declare  @sqlpd nvarchar(2500)=''   ---用于判断是否存在的sql语句
  declare  @count_pd int --用于保留数据量

  declare  @sqlqz nvarchar(2500)='' ---用于取win60的值的sql语句
  declare  @sqlqz_par  nvarchar(2500)=''  --用于给动态sql传参
  
  declare  @name  nvarchar(64)  --YF_YFDMK.name  
  declare  @name_60  nvarchar(64) --关联ORGANIZATION.ORG_NAME

  declare  @py  nvarchar(64)  --YF_YFDMK.py  
  declare  @py_60  nvarchar(64) --关联ORGANIZATION.PINYIN

  declare  @wb  nvarchar(64)  --YF_YFDMK.wb  
  declare  @wb_60  nvarchar(64) --关联ORGANIZATION.WUBI

  declare  @memo  nvarchar(256)  --YF_YFDMK.memo  
  declare  @memo_60  nvarchar(256) --关联ORGANIZATION.ORG_DESC
   
   

  declare cur_zg cursor for (select id,name,py,wb,memo from YK_CJDMK_check)  
  open cur_zg  
  fetch next from cur_zg into @id,@name,@py,@wb,@memo
  while @@FETCH_STATUS=0  
    begin  
			  --取win60的数据,有可能在win60没有此数据
			    begin tran
			   select @sqlpd='select @a1=count(*)  from '+ @ORGANIZATION + ' a  where a.ORG_NO=@id_ORG and ORG_TYPE_CODE=152643' 
			   exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@count_pd OUTPUT,@id_ORG=@id	
			   if(@@ERROR <>0)
			       begin	
			           select @sqlpd
			           select 'F：查找数据错误',@count_pd,@id
					   rollback tran
				     end
			    else
				   commit tran
			   
		if(@count_pd=1)  
		begin
		   begin tran
		    select @sqlqz='select @_name=a.ORG_NAME,@_py=a.PINYIN,@_wb=a.WUBI,@_memo=a.ORG_DESC  from '+ @ORGANIZATION+ ' a where a.ORG_NO=@id_ORG and a.ORG_TYPE_CODE=152643' 			                  
		    select @sqlqz_par=N'@id_ORG nvarchar(12) ,@_name nvarchar(64) out, @_py nvarchar(64) out, @_wb nvarchar(64) out,@_memo nvarchar(256) out'
		    exec sp_executesql @sqlqz,@sqlqz_par,@id_ORG=@id,@_name=@name_60 OUTPUT,@_py=@py_60 OUTPUT,@_wb=@wb_60 OUTPUT,@_memo=@memo_60 OUTPUT
		   if(@@ERROR <> 0)
		       begin
			       rollback tran			   
				   select 'F: 赋值语句错误',@sqlqz , @id
			   end
            else
			    commit tran

		   if(@name <> @name_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('制造商信息明细对比','列值不一致','YK_CJDMK','ORGANIZATION',@id,@id,'name',@name,'ORGANIZATION.ORG_NAME',@name_60) 
           
           if(@py <> @py_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('制造商信息明细对比','列值不一致','YK_CJDMK','ORGANIZATION',@id,@id,'py',@py,'ORGANIZATION.PINYIN',@py_60) 
			
		    if(@wb <> @wb_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('制造商信息明细对比','列值不一致','YK_CJDMK','ORGANIZATION',@id,@id,'wb',@wb,'ORGANIZATION.WUBI',@wb_60) 

             if(@memo <> @memo_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('制造商信息明细对比','列值不一致','YK_CJDMK','ORGANIZATION',@id,@id,'memo',@memo,'ORGANIZATION.ORG_DESC',@memo_60) 

		end  
		
		
		else  
		   --win60没有此数据         
		   insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)  
					values('制造商信息明细对比','5X有数据,win60没数据','YK_CJDMK','ORGANIZATION',@id,'','name',@name)    
        
		
		select @sqlpd='' --清空判断的sql
		select @sqlqz='' --清空取值的sql
		select @sqlqz_par='' --清空取值的sql
		select @count_pd=0 --初始化为0       
		fetch next from cur_zg into @id,@name,@py,@wb,@memo
    end  
  close cur_zg      
  DEALLOCATE cur_zg   
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',  
  V_5X '5X列值',C_60 '60列名',V_60 '60列值',C_60_filter '60的关联' from #error  
  drop table #error  
end


 
      
    



 