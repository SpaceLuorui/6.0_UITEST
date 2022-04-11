    
create  proc  usp_win60t_YY_YBFLK_ybsm      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_YBFLK_ybsm 15
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
   declare @MED_INSTI_MEDICAL_INSURANCE nvarchar(255)  
   select  @MED_INSTI_MEDICAL_INSURANCE=dbo.fun_win60test_addsc('MED_INSTI_MEDICAL_INSURANCE')  

   --表主键
   declare @ybdm varchar(50) 

   --需要校验的字段
   declare  @ybsm  nvarchar(255)
   declare  @ybsm_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_ybsm cursor for (select ybdm,ybsm from YY_YBFLK_check)
   open mycur_ybsm
   fetch next from mycur_ybsm into @ybdm,@ybsm
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @ybdmm,@ypdm_60 '取值前'
			   select @sqlcx='select @_ybsm=MED_INSTI_INSUR_NAME from '+ @MED_INSTI_MEDICAL_INSURANCE+ ' where HOSPITAL_SOID=@_hosid and MED_INSTI_INSUR_NO=@_ybdm' 
			   exec sp_executesql @sqlcx,N'@_ybsm nvarchar(255) out,@_hosid numeric,@_ybdm nvarchar(12) ',
			                               @_ybsm=@ybsm_60 OUTPUT,@_hosid=@hospital_soid,@_ybdm=@ybdm 
			   --判断条件： 不存在
               if( (@ybsm_60 <> '不存在' ) and (@ybsm_60<>@ybsm))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_YBFLK','ybsm',@ybsm,@ybdm,'MED_INSTI_MEDICAL_INSURANCE','MED_INSTI_INSUR_NAME',@ybsm_60,'ybsm不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @ybsm_60='不存在'  
    fetch next from mycur_ybsm into @ybdm,@ybsm  
     end      
  close mycur_ybsm          
  DEALLOCATE mycur_ybsm       
  
  select * from #error
      
  drop table #error      
   