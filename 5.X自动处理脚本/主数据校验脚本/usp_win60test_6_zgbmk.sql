
alter  proc  usp_win60test_autocheck_zgbmk
     
   @hisdb   nvarchar(255) ---- his数据库名     
   /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60test_autocheck_zgbmk 'THIS4_LYLT'         
               不在同一实例下，如链接服务器为WJLS200,则   usp_win60test_autocheck_zgbmk 'WJLS200.THIS4_TEST60'
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
  
   --1比较职工信息  
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题  
   declare @YY_ZGBMK  nvarchar(255)     
   select  @YY_ZGBMK= @hisdb +'.dbo.YY_ZGBMK'  
   declare @copysql nvarchar(255)  
      --物理表性能更好,及时删除即可   
   if exists(select 1 from sys.all_objects where name='YY_ZGBMK_check')  
       drop table  YY_ZGBMK_check  
   select  @copysql='select * into  YY_ZGBMK_check from '+@YY_ZGBMK  
   exec(@copysql)     
    --drop table YY_ZGBMK_check
  
   --1.1比较职工总数  
   declare @count60_EMPLOYEE_INFO  int  
   declare @count5X_YY_ZGBMK int  
   
   declare @sql_c60 nvarchar(500) 
   declare @EMPLOYEE_INFO nvarchar(255)
   select  @EMPLOYEE_INFO=dbo.fun_win60test_addsc('EMPLOYEE_INFO')

   declare @EMPLOYEE_TELECOM nvarchar(255)
   select  @EMPLOYEE_TELECOM=dbo.fun_win60test_addsc('EMPLOYEE_TELECOM')


   declare @EMPLOYEE_IDCARD nvarchar(255)
   select  @EMPLOYEE_IDCARD=dbo.fun_win60test_addsc('EMPLOYEE_IDCARD')

   declare @ORGANIZATION_X_EMPLOYEE nvarchar(255)
   select  @ORGANIZATION_X_EMPLOYEE=dbo.fun_win60test_addsc('ORGANIZATION_X_EMPLOYEE')
   
   declare @ORGANIZATION nvarchar(255)
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION')
    
   select @sql_c60='select @a=count(*)  from '+ @EMPLOYEE_INFO
   exec sp_executesql @sql_c60,N'@a int out',@a=@count60_EMPLOYEE_INFO OUTPUT

   declare @sql nvarchar(500)  
   select @sql=N'select @a=count(*) from YY_ZGBMK_check'   
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_ZGBMK OUTPUT  
   --select @count5X_YY_ZGBMK  
   if(@count5X_YY_ZGBMK <> @count60_EMPLOYEE_INFO)  
     begin     
  insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)  
  values('职工信息总数对比','职工数量不一致','YY_ZGBMK','EMPLOYEE_INFO',@count5X_YY_ZGBMK,@count60_EMPLOYEE_INFO)  
  end  
     
  
   --1.2比较职工明细,可以直接在两个表中查询到列对比
  declare  @sqlpd nvarchar(2500)=''   ---用于判断是否存在的sql语句
  declare  @count_pd int --用于保留数据量

  declare  @sqlqz nvarchar(2500)='' ---用于取win60的值的sql语句
  declare  @sqlqz_par  nvarchar(2500)=''  --用于给动态sql传参

  declare  @sqlqz_ydhm nvarchar(2500)='' ---用于取win60的值的sql语句
  declare  @sqlqz_par_ydhm  nvarchar(2500)=''  --用于给动态sql传参

  declare  @sqlqz_zjlx nvarchar(2500)='' ---用于取win60的值的sql语句
  declare  @sqlqz_par_zjlx  nvarchar(2500)=''  --用于给动态sql传参


  declare  @sqlqz_ksid nvarchar(2500)='' ---用于取win60的值的sql语句
  declare  @sqlqz_par_ksid  nvarchar(2500)=''  --用于给动态sql传参

  declare  @id  char(12)  --YY_ZGBMK.id  ,主键 
   
  declare  @name  varchar(64)  --YY_ZGBMK.name  
  declare  @name_60  varchar(64) --EMPLOYEE_INFO.EMPLOYEE_NAME

  declare  @py  varchar(64)  --YY_ZGBMK.py  
  declare  @py_60  varchar(64) --EMPLOYEE_INFO.PINYIN 

  declare  @wb  varchar(64)  --YY_ZGBMK.wb  
  declare  @wb_60  varchar(64) --EMPLOYEE_INFO.WUBI 

  declare  @sex  varchar(64)  --YY_ZGBMK.sex  
  declare  @sex_60  varchar(64) --EMPLOYEE_INFO.GENDER_CODE

  declare  @birth  varchar(64)  --YY_ZGBMK.birth  
  declare  @birth_60  varchar(64) --EMPLOYEE_INFO.BIRTH_DATE
    
  declare  @jlzt  varchar(64)  --YY_ZGBMK.jlzt  
  declare  @jlzt_60  varchar(64) --EMPLOYEE_INFO.EMPLOYMENT_STATUS
   
  declare  @zglb  nvarchar(64)  --YY_ZGBMK.zglb  
  declare  @zglb_60  nvarchar(64) --EMPLOYEE_INFO.EMPLOYMENT_TYPE_CODE
  
  declare @ydhm  varchar(64)
  declare  @ydhm_60  varchar(64)
   
   declare @zjlx  varchar(64)
   declare @zjlx_60  varchar(64)

      declare @sfzh  varchar(64)
   declare @sfzh_60  varchar(64)

   declare @ks_id varchar(64)
   declare @ks_id_60 varchar(64)

  declare cur_zg cursor for (select id,name,py,wb,sex,birth,jlzt,zglb,ydhm,zjlx,sfzh,ks_id from YY_ZGBMK_check)  
  open cur_zg  
  fetch next from cur_zg into @id,@name,@py,@wb,@sex,@birth,@jlzt,@zglb,@ydhm,@zjlx,@sfzh,@ks_id
  while @@FETCH_STATUS=0  
    begin  
			  --取win60的数据,有可能在win60没有此数据
			    begin tran
			   select @sqlpd='select @a1=count(*)  from '+ @EMPLOYEE_INFO + ' a  where a.EMPLOYEE_NO=@id_e' 
			   exec sp_executesql @sqlpd,N'@a1 int out,@id_e nvarchar(12) ',@a1=@count_pd OUTPUT,@id_e=@id	
			   if(@@ERROR <>0)
			       begin	
			           select @sqlpd
			           select 'F：查找数据错误',@count_pd,@id
					   rollback tran
				     end
			    else
				   commit tran  
		if(@count_pd=1 )  
		begin
		     begin tran
		    select @sqlqz='select @_name=a.EMPLOYEE_NAME,@_py=a.PINYIN,@_sex=a.GENDER_CODE,@_birth=a.BIRTH_DATE,@_jlzt=a.EMPLOYMENT_STATUS,@_zglb=a.EMPLOYMENT_TYPE_CODE  from '+ @EMPLOYEE_INFO+ ' a  where a.EMPLOYEE_NO=@id_e' 			                  
		    select @sqlqz_par=N'@id_e nvarchar(12) ,@_name nvarchar(64) out, @_py nvarchar(64) out, @_wb nvarchar(64) out,@_sex nvarchar(256) out,@_birth nvarchar(256) out,@_jlzt  nvarchar(256) out,@_zglb nvarchar(256) out'
		    exec sp_executesql @sqlqz,@sqlqz_par,@id_e=@id,@_name=@name_60 OUTPUT,@_py=@py_60 OUTPUT,@_wb=@wb_60 OUTPUT,@_sex=@sex_60 OUTPUT,@_birth=@birth_60 OUTPUT,@_jlzt=@jlzt_60 OUTPUT,@_zglb=@zglb_60 OUTPUT
		   if(@@ERROR <> 0)
		       begin
			       rollback tran			   
				   select 'F: 赋值语句错误',@sqlqz , @id
			   end
            else
			    commit tran	
		
			select @sex_60=case @sex_60 
			when 50601 then '未知' --6.0中代表未知的性别 转换为 5.x中的未知
			when 50602 then '男'   --6.0中代表男性 转换为 5.x中的男
			when 50603 then '女'   --6.0中代表女性 转换为 5.x中的女
			when 50604 then ''     --6.0中代表未说明的性别 转换为 5.x中的空值
			else '未找到映射关系'
			end

			select @birth_60=CONVERT(char(8), @birth_60, 112)

			select @jlzt_60=case @jlzt_60 
			when 152437 then '0' --6.0中代表在职 转换为 5.x中的0
			--when 152438 then '' --5.x同步到6.0只有离职和在职，暂时停职不判断
			when 152439 then '1' --6.0中代表离职 转换为 5.x中的1
			end

			 
			select @zglb_60=case @zglb_60 
			when 253530 then 0 --普通医生
			when 253531 then 1 --专家医生
			when 253532 then 5 --进修医师
			when 253534 then 7 --研究生实习医师
			when 253535 then 2 --护士
			when 253536 then 3 --麻醉师
			when 253537 then 4 --其他
			when 253545 then 6 --规培医师
			--when 253533 then '' --实习医师 6.0多出三个职工类别，不判断
			--when 263894 then '' --智能设备
			--when 152436 then '' --医生
			else 999
			end		
			 
			 
			if(@name <> @name_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'name',@name,'EMPLOYEE_NAME',@name_60) 
			if(@py <> @py_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'py',@py,'PINYIN',@py_60) 
			if(@wb <> @wb_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'wb',@wb,'WUBI',@wb_60)
			if(@sex <> @sex_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'sex',@sex,'GENDER_CODE',@sex_60)
			if(@birth <> @birth_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'birth',@birth,'BIRTH_DATE',@birth_60)
			if(@jlzt <> @jlzt_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'jlzt',@jlzt,'EMPLOYMENT_STATUS',@jlzt_60) 
			if(@zglb <> @zglb_60)  
				insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)  
				values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_INFO',@id,@id,'zglb',@zglb,'EMPLOYMENT_TYPE_CODE',@zglb_60)

			--由于EMPLOYEE_TELECOM与EMPLOYEE_INFO可能存在1对多的关系，因此单独取值,加上EMPLOYEE_TELECOM固定值的排选,
			--ydhm
			 begin tran
		    select @sqlqz_ydhm='select @_ydhm=b.CONTACT_NO from '+ @EMPLOYEE_INFO+ ' a  inner join '+@EMPLOYEE_TELECOM+' b on a.EMPLOYEE_ID=b.EMPLOYEE_ID where a.EMPLOYEE_NO=@id_e and b.CONTACT_CODE=152790 and b.CONTACT_USAGE_CODE=145389' 			                  
		    select @sqlqz_par_ydhm=N'@id_e nvarchar(12) ,@_ydhm nvarchar(64) out'
		    exec sp_executesql @sqlqz_ydhm,@sqlqz_par_ydhm,@id_e=@id,@_ydhm=@ydhm_60 OUTPUT
		   if(@@ERROR <> 0)
		       begin
			       rollback tran			   
				   select 'F: ydhm赋值语句错误',@sqlqz_ydhm , @id
			   end
            else
			    commit tran 

              if(@ydhm <> @ydhm_60)
			     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60,C_60_filter)  
				 values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_TELECOM',@id,@id,'ydhm',@ydhm,'CONTACT_NO',@zglb_60,'CONTACT_CODE=152790,CONTACT_USAGE_CODE=145389')
			 
			 --zjlx,sfzh
			 begin tran
			 select @sqlqz_zjlx='select @_zjlx=b.IDCARD_TYPE_CODE, @_sfzh=b.IDCARD_NO from '+ @EMPLOYEE_INFO+ ' a  inner join '+@EMPLOYEE_IDCARD+' b on a.EMPLOYEE_ID=b.EMPLOYEE_ID where a.EMPLOYEE_NO=@id_e and b.IDCARD_USAGE_CODE=145453' 			                  
		    select @sqlqz_par_zjlx=N'@id_e nvarchar(12) ,@_zjlx nvarchar(64) out,@_sfzh nvarchar(64) out'
		    exec sp_executesql @sqlqz_zjlx,@sqlqz_par_zjlx,@id_e=@id,@_zjlx=@zjlx_60 OUTPUT,@_sfzh=@sfzh_60 OUTPUT
		   if(@@ERROR <> 0)
		       begin
			       rollback tran			   
				   select 'F: zjlx赋值语句错误',@sqlqz_ydhm, @id
			   end
            else
			    commit tran 

              if(@zjlx <> @zjlx_60)
			     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60,C_60_filter)  
				 values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_IDCARD',@id,@id,'zjlx',@zjlx,'IDCARD_TYPE_CODE',@zjlx_60,'DCARD_USAGE_CODE=145453')
			 
			  if(@sfzh <> @sfzh_60)
			     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60,C_60_filter)  
				 values('职工信息明细对比','列值不一致','YY_ZGBMK','EMPLOYEE_IDCARD',@id,@id,'sfzh',@sfzh,'IDCARD_NO',@sfzh_60,'DCARD_USAGE_CODE=145453')
			 
			 --ks_id
			 begin tran
			 select @sqlqz_ksid='select @_ksid=c.ORG_NO  from '+ @EMPLOYEE_INFO+ ' a  inner join '+@ORGANIZATION_X_EMPLOYEE+' b on a.EMPLOYEE_ID=b.EMPLOYEE_ID inner join '+@ORGANIZATION+' c on b.ORG_ID=c.ORG_ID  where a.EMPLOYEE_NO=@id_e' 			                  
		    select @sqlqz_par_ksid=N'@id_e nvarchar(12) ,@_ksid nvarchar(64) out'
		    exec sp_executesql @sqlqz_ksid,@sqlqz_par_ksid,@id_e=@id,@_ksid=@ks_id_60 OUTPUT
		   if(@@ERROR <> 0)
		       begin
			       rollback tran			   
				   select 'F: @sqlqz_ksid赋值语句错误',@sqlqz_ydhm, @id
			   end
            else
			    commit tran
 
				  
			 if(@ks_id <> @ks_id_60)
			     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60,C_60_filter)  
				 values('职工信息明细对比','列值不一致','YY_ZGBMK','ORGANIZATION',@id,@id,'ks_id',@ks_id,'ORG_NO',@ks_id_60,'')
			 	   

		end  
		else  --win60没有此数据         
		   insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)  
					values('职工信息明细对比','5X有数据,win60没数据','YY_ZGBMK','EMPLOYEE_INFO',@id,'','name',@name)    
               
		fetch next from cur_zg into @id,@name,@py,@wb,@sex,@birth,@jlzt,@zglb,@ydhm,@zjlx,@sfzh,@ks_id

		select @sqlpd='' --清空判断的sql
		 select @sqlqz='' --清空取值的sql
		 select @sqlqz_par='' --清空取值的sql
		 select @sqlqz_ydhm='' --清空取值的sql
		 select @sqlqz_par_ydhm='' --清空取值的sql
		 select @sqlqz_zjlx='' --清空取值的sql
		 select @sqlqz_par_zjlx='' --清空取值的sql
		 select @sqlqz_ksid='' --清空取值的sql
		 select @sqlqz_par_ksid='' --清空取值的sql
		select @count_pd=0 --初始化为0
    end  
  close cur_zg      
  DEALLOCATE cur_zg   
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',  
  V_5X '5X列值',C_60 '60列名',V_60 '60列值',C_60_filter '60的关联' from #error  
  drop table #error  
end


 
      
    