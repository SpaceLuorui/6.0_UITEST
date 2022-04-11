create  proc  usp_win60t_YY_ZGBMK_zglb      
			
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_ZGBMK_zglb 15
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

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @zglb  nvarchar(50)
   declare  @zglb_60  nvarchar(50)
   declare  @zglb_ys  nvarchar(50)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_zglb cursor for (select id,zglb from YY_ZGBMK_check)
   open mycur_zglb
   fetch next from mycur_zglb into @id,@zglb
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_zglb=EMPLOYMENT_TYPE_CODE from '+ @EMPLOYEE_INFO+ ' where HOSPITAL_SOID=@_hosid and EMPLOYEE_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_zglb nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_zglb=@zglb_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			


			   --判断条件： 不存在
			   select @zglb_ys=case @zglb when 0 then 253530 
										  when 1 then 253531
										  when 2 then 253535 
										  when 3 then 253536 
										  when 4 then 253537 
										  when 5 then 253532 
										  when 6 then 253545 
										  when 7 then 253534
										  else '无映射'  
										  end

               if( @zglb_ys <> ISNULL(@zglb_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_ZGBMK','zglb',@zglb,@id,'EMPLOYEE_INFO','EMPLOYMENT_TYPE_CODE',ISNULL(@zglb_60,'无值'),'zglb不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @zglb_60=NULL  
    fetch next from mycur_zglb into @id,@zglb  
     end      
  close mycur_zglb          
  DEALLOCATE mycur_zglb       
  
  select * from #error
      
  drop table #error      
   