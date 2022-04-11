create  proc  usp_win60t_YY_ZGBMK_zjlx      
			
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_ZGBMK_zjlx 15
   */
   
   /**命名规则,此条对比ypdm ,以id结尾
      select * from tab_win60t_MDATA where col='ypdm' and tab='YK_YPCDMLK'
	  select  MEDICINE_PRIMARY_NO  from MATMDM.MEDICINE where MED_EXT_REF_ID=3304
   **/       
 as 
     
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
  
    
    --1.比较ypdm
   declare @EMPLOYEE_INFO nvarchar(255)  
   select  @EMPLOYEE_INFO=dbo.fun_win60test_addsc('EMPLOYEE_INFO')
   
   declare @EMPLOYEE_IDCARD nvarchar(255)
   select  @EMPLOYEE_IDCARD=dbo.fun_win60test_addsc('EMPLOYEE_IDCARD') 

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @zjlx  nvarchar(50)
   declare  @zjlx_60  nvarchar(50)
   declare  @zjlx_ys  nvarchar(50)
   

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_zjlx cursor for (select id,zjlx from YY_ZGBMK_check)
   open mycur_zjlx
   fetch next from mycur_zjlx into @id,@zjlx
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_zjlx=b.IDCARD_TYPE_CODE from '+ @EMPLOYEE_INFO+ ' a inner join '+@EMPLOYEE_IDCARD+' b on a.EMPLOYEE_ID=b.EMPLOYEE_ID where a.HOSPITAL_SOID=@_hosid and b.IDCARD_USAGE_CODE=145453 and EMPLOYEE_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_zjlx nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_zjlx=@zjlx_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			   select @zjlx_ys=case @zjlx  --根据不同医院，映射可能不同，需自行调整
							  when '01' then '152626' --居民身份证
							  when '02' then '152627' --居民户口簿
							  when '03' then '152628' --护照
							  when '04' then '152629' --军官证
							  when '05' then '152630' --驾驶证
							  when '06' then '152631' --港澳居民来往内地通行证
							  when '07' then '152632' --台湾居民来往内地通行证
							  when '99' then '152634' --其他法定有效证件
							  else '无映射关系'
							  end

               if( @zjlx_ys <> ISNULL(@zjlx_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_ZGBMK','zjlx',@zjlx_ys,@id,'EMPLOYEE_TELECOM','EMPLOYEE_IDCARD',ISNULL(@zjlx_60,'无值'),'zjlx不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @zjlx_60=NULL  
    fetch next from mycur_zjlx into @id,@zjlx  
     end      
  close mycur_zjlx          
  DEALLOCATE mycur_zjlx       
  
  select * from #error
      
  drop table #error      
   