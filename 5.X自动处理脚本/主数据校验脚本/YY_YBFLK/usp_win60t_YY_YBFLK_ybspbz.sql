    
CREATE  proc  usp_win60t_YY_YBFLK_ybspbz      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_YBFLK_ybspbz 15
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
   declare  @ybspbz  nvarchar(50)
   declare  @ybspbz_60  nvarchar(50)
   declare @ybspbz_ys nvarchar(50)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_ybspbz cursor for (select ybdm,ybspbz from YY_YBFLK_check)
   open mycur_ybspbz
   fetch next from mycur_ybspbz into @ybdm,@ybspbz
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @ybdmm,@ypdm_60 '取值前'
			   select @sqlcx='select @_ybspbz=MED_INSUR_APPROVAL_NEEDED_FLAG from '+ @MED_INSTI_MEDICAL_INSURANCE+ ' where HOSPITAL_SOID=@_hosid and MED_INSTI_INSUR_NO=@_ybdm' 
			   exec sp_executesql @sqlcx,N'@_ybspbz nvarchar(255) out,@_hosid numeric,@_ybdm nvarchar(12) ',
			                               @_ybspbz=@ybspbz_60 OUTPUT,@_hosid=@hospital_soid,@_ybdm=@ybdm 

				select @ybspbz_ys= case isnull(@ybspbz,0) when 0 then 98176 else 98175 end
			   --判断条件： 不存在
               if(@ybspbz_ys<>ISNULL(@ybspbz_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_YBFLK','ybspbz',@ybspbz,@ybdm,'MED_INSTI_MEDICAL_INSURANCE','MED_INSUR_APPROVAL_NEEDED_FLAG',ISNULL(@ybspbz_60,'无值'),'ybspbz不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @ybspbz_60=NULL  
    fetch next from mycur_ybspbz into @ybdm,@ybspbz  
     end      
  close mycur_ybspbz          
  DEALLOCATE mycur_ybspbz       
  
  select * from #error
      
  drop table #error      
   