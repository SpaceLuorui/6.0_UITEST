
alter  proc  usp_win60test_autocheck_yfdmk     
   @hisdb   nvarchar(255) ---- his数据库名     
   /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60test_autocheck_yfdmk 'THIS4_LYLT'         
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
  
   --1比较药房信息  
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题  
   declare @YF_YFDMK  nvarchar(255)     
   select  @YF_YFDMK= @hisdb +'.dbo.YF_YFDMK'  
   declare @copysql nvarchar(255)  
      --物理表性能更好,及时删除即可   
   if exists(select 1 from sys.all_objects where name='YF_YFDMK_check')  
       drop table  YF_YFDMK_check  
   select  @copysql='select * into  YF_YFDMK_check from '+@YF_YFDMK  
   exec(@copysql)     
    
    
   --1.1比较药房
   --ORGANIZATION  组织
   --DRUG_STORAGE 库房
   --PHARMACY   药房
   --STOREHOUSE 药库
   declare @count60_PHARMACY  int  
   declare @count5X_YF_YFDMK int
   declare @PHARMACY nvarchar(255)
   select  @PHARMACY=dbo.fun_win60test_addsc('PHARMACY')

   declare @ORGANIZATION nvarchar(255)
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION')

   declare @DRUG_STORAGE nvarchar(255)
   select  @DRUG_STORAGE=dbo.fun_win60test_addsc('DRUG_STORAGE')

   declare @DRUG_STORAGE_LEDGER_TYPE nvarchar(255)
   select  @DRUG_STORAGE_LEDGER_TYPE=dbo.fun_win60test_addsc('DRUG_STORAGE_LEDGER_TYPE')

   declare @ORGANIZATION_X_ENCOUNTER_TYPE nvarchar(255)
   select  @ORGANIZATION_X_ENCOUNTER_TYPE=dbo.fun_win60test_addsc('ORGANIZATION_X_ENCOUNTER_TYPE')
 
   declare @sql1   nvarchar(255) 
   select @sql1= 'select @a=count(*)  from '+ @PHARMACY
   exec sp_executesql  @sql1,N'@a int out',@a=@count60_PHARMACY OUTPUT

   
   declare @sql nvarchar(500)  
   select @sql=N'select @a=count(*) from YF_YFDMK_check'   
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YF_YFDMK OUTPUT  

    
   --select @count5X_YY_ZGBMK  
   if(@count5X_YF_YFDMK <> @count60_PHARMACY)  
     begin     
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)  
  values('药房信息总数对比','职工数量不一致','YF_YFDMK','PHARMACY',@count5X_YF_YFDMK,@count60_PHARMACY)  
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

  declare  @yfbz  nvarchar(256)  --YF_YFDMK.yfbz  0:二级药房 1：三级药房 
  declare  @yfbz_60  nvarchar(256) --库房分级代码：DRUG_STORAGE.DRUG_STORAGE_GRADE_CODE, 

  declare  @pybz  nvarchar(256)  --YF_YFDMK.pybz
  declare  @pybz_60  nvarchar(256) --关联ORGANIZATION.PHARMACY_SERVICE_FLAG

  declare  @zmlb  nvarchar(256)  --YF_YFDMK.pybz
  declare  @zmlb_60_xy  int=0    --西药
  declare  @zmlb_60_xy_sql  nvarchar(256)    --西药判断的sql
 
  declare  @zmlb_60_zcy  int=0   --中成药
  declare  @zmlb_60_zcy_sql  nvarchar(256)    --西药判断的sql

  declare  @zmlb_60_cy  int=0   --中草药
  declare  @zmlb_60_cy_sql  nvarchar(256)    --西药判断的sql
  declare  @zmlb_60_zj  int=0--制剂
  declare  @zmlb_60_zj_sql  nvarchar(256)    --西药判断的sql

  --xtbz
  declare  @xtbz  nvarchar(256)  --YF_YFDMK.xtbz
  declare  @xtbz_60  int=0    --

  declare cur_zg cursor for (select id,name,py,wb,memo,yfbz,pybz,zmlb,xtbz from YF_YFDMK_check)  
  open cur_zg  
  fetch next from cur_zg into @id,@name,@py,@wb,@memo,@yfbz,@pybz,@zmlb,@xtbz
  while @@FETCH_STATUS=0  
    begin  
			  --取win60的数据,有可能在win60没有此数据
			   
			   select @sqlpd='select @a1=count(*)  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION + ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join '+@ORGANIZATION_X_ENCOUNTER_TYPE+ ' d on b.ORG_ID=d.ORG_ID'+' where b.ORG_NO=@id_ORG' 
			   exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@count_pd OUTPUT,@id_ORG=@id		
			   
		if(@count_pd=1)  
		begin
		    select @sqlqz='select @_name=b.ORG_NAME,@_py=b.PINYIN,@_wb=b.WUBI,@_memo=b.ORG_DESC,@_yfbz=c.DRUG_STORAGE_GRADE_CODE,@_pybz=a.PHARMACY_SERVICE_FLAG,@_xtbz=d.ENCOUNTER_TYPE_CODE  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION +  ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join '+@ORGANIZATION_X_ENCOUNTER_TYPE+' d on b.ORG_ID=d.ORG_ID'+' where b.ORG_NO=@id_ORG' 			                  
		    select @sqlqz_par=N'@id_ORG nvarchar(12) ,@_name nvarchar(64) out, @_py nvarchar(64) out, @_wb nvarchar(64) out,@_memo nvarchar(256) out,@_yfbz nvarchar(256) out,@_pybz  nvarchar(256) out,@_xtbz  nvarchar(256) out'
			exec sp_executesql @sqlqz,@sqlqz_par,@id_ORG=@id,@_name=@name_60 OUTPUT,@_py=@py_60 OUTPUT,@_wb=@wb_60 OUTPUT,@_memo=@memo_60 OUTPUT,@_yfbz=@yfbz_60 OUTPUT,@_pybz=@pybz_60 OUTPUT,@_xtbz=@xtbz_60 OUTPUT
		 
		    select @yfbz_60=case  @yfbz_60 when '376378' then '1'  when '376377' then '0' else '非药房' end
			        ---376375 药库属于一级库存点, 对应HIS应该是YK_YKDMK
				    --376377  药房属于二级库存点, 对应HIS应该是YF_YFDMK.yfbz=0 ，在HIS中是二级药房
				    --376378 科室或病区如果作为库存点属于三级库存点,
           select @pybz_60=case @pybz_60 when '98175' then '1' when  '98176' then '0' else '配药属性错误' end
		   --select @pybz,@pybz_60

		   --select @xtbz_60
		    select @xtbz_60=case @xtbz_60 when '138138' then '0' when '145235' then '1' else '非门诊和住院' end
		       --138138,门诊，HIS为0
			   --145235，住院,HIS为1
            
		   if(@name <> @name_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'name',@name,'ORGANIZATION.ORG_NAME',@name_60) 

           if(@py <> @py_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'py',@py,'ORGANIZATION.PINYIN',@py_60) 
			
		    if(@memo <> @memo_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'memo',@memo,'ORGANIZATION.ORG_DESC',@memo_60) 

            if(@yfbz <> @yfbz_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'yfbz',@yfbz,'DRUG_STORAGE.DRUG_STORAGE_GRADE_CODE',@yfbz_60) 
            
			if(@pybz <> @pybz_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'pybz',@pybz,'PHARMACY_SERVICE_FLAG',@pybz_60)
		
		    if(@xtbz <> @xtbz_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('药房信息明细对比','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'xtbz',@xtbz,'ENCOUNTER_TYPE_CODE',@xtbz_60)
		


		   ---zmlb的对比
		     if((charindex('01',@zmlb) != 0 )) ---包含西药,LEDGER_TYPE_CODE=142183
			    begin
		         select @zmlb_60_xy_sql='select @a1=count(*)  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION + ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join'+@DRUG_STORAGE_LEDGER_TYPE+' d on c.DRUG_STORAGE_ID=d.DRUG_STORAGE_ID'+' where b.ORG_NO=@id_ORG and d.LEDGER_TYPE_CODE=142183' 
			     exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@zmlb_60_xy OUTPUT,@id_ORG=@id				  
				    if(@zmlb_60_xy <> 1)  
						insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
						values('药房账目西药','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'zmlb',@zmlb,'DRUG_STORAGE_LEDGER_TYPE','没有142183的数据')
			   
			   end 

			   if((charindex('02',@zmlb) != 0 )) ---包含中成药,LEDGER_TYPE_CODE=142184
			    begin
		         select @zmlb_60_zcy_sql='select @a1=count(*)  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION + ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join'+@DRUG_STORAGE_LEDGER_TYPE+' d on c.DRUG_STORAGE_ID=d.DRUG_STORAGE_ID'+' where b.ORG_NO=@id_ORG and d.LEDGER_TYPE_CODE=142184' 
			     exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@zmlb_60_zcy OUTPUT,@id_ORG=@id				  
				    if(@zmlb_60_zcy <> 1)  
						insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
						values('药房账目中成药','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'zmlb',@zmlb,'DRUG_STORAGE_LEDGER_TYPE','没有142184的数据')
			   end    
			   
			   if((charindex('03',@zmlb) != 0 )) ---包含草药,LEDGER_TYPE_CODE=142185
			    begin
		         select @zmlb_60_cy_sql='select @a1=count(*)  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION + ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join'+@DRUG_STORAGE_LEDGER_TYPE+' d on c.DRUG_STORAGE_ID=d.DRUG_STORAGE_ID'+' where b.ORG_NO=@id_ORG and d.LEDGER_TYPE_CODE=142185' 
			     exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@zmlb_60_cy OUTPUT,@id_ORG=@id				  
				    if(@zmlb_60_cy <> 1)  
						insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
						values('药房账目草药','列值不一致','YF_YFDMK','PHARMACY',@id,@id,'zmlb',@zmlb,'DRUG_STORAGE_LEDGER_TYPE','没有142185的数据')
			   end    
			   
			   if((charindex('04',@zmlb) != 0 )) ---包含制剂,LEDGER_TYPE_CODE=142186
			    begin
		         select @zmlb_60_zj_sql='select @a1=count(*)  from '+ @PHARMACY+' a  inner join '+ @ORGANIZATION + ' b on a.DRUG_STORAGE_ID=b.ORG_ID inner join '+@DRUG_STORAGE+ ' c on  a.DRUG_STORAGE_ID=c.DRUG_STORAGE_ID inner join'+@DRUG_STORAGE_LEDGER_TYPE+' d on c.DRUG_STORAGE_ID=d.DRUG_STORAGE_ID'+' where b.ORG_NO=@id_ORG and d.LEDGER_TYPE_CODE=142186' 
			     exec sp_executesql @sqlpd,N'@a1 int out,@id_ORG nvarchar(12) ',@a1=@zmlb_60_cy OUTPUT,@id_ORG=@id				  
				    if(@zmlb_60_zj <> 1)  
						insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
						values('药房账目制剂','列值不一致','YF_YFDMK','ORGANIZATION_X_ENCOUNTER_TYPE',@id,@id,'zmlb',@zmlb,'DRUG_STORAGE_LEDGER_TYPE','没有142186的数据')
			   end      
		
		
		end  
		
		
		else  
		   --win60没有此数据         
		   insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)  
					values('药房信息明细对比','5X有数据,win60没数据','YF_YFDMK','PHARMACY',@id,'','name',@name)    
        
		
		select @sqlpd='' --清空判断的sql
		select @sqlqz='' --清空取值的sql
		select @sqlqz_par='' --清空取值的sql
		select @count_pd=0 --初始化为0       
		fetch next from cur_zg into @id,@name,@py,@wb,@memo,@yfbz,@pybz,@zmlb,@xtbz
    end  
  close cur_zg      
  DEALLOCATE cur_zg   
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',  
  V_5X '5X列值',C_60 '60列名',V_60 '60列值',C_60_filter '60的关联' from #error  
  drop table #error  
end


 
      
    