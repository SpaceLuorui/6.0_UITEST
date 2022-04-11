create  proc  usp_win60t_YY_ZGBMK_ks_id      
			
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_ZGBMK_ks_id 15
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
   
   declare @ORGANIZATION_X_EMPLOYEE nvarchar(255)
   select  @ORGANIZATION_X_EMPLOYEE=dbo.fun_win60test_addsc('ORGANIZATION_X_EMPLOYEE') 

   declare @ORGANIZATION nvarchar(255)
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION') 

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @ks_id  nvarchar(50)
   declare  @ks_id_60  nvarchar(50)
   

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_ks_id cursor for (select id,ks_id from YY_ZGBMK_check)
   open mycur_ks_id
   fetch next from mycur_ks_id into @id,@ks_id
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   select @sqlcx='select @_ks_id=c.ORG_NO from '+ @EMPLOYEE_INFO+ ' a  inner join '+@ORGANIZATION_X_EMPLOYEE+' b on a.EMPLOYEE_ID=b.EMPLOYEE_ID inner join '+@ORGANIZATION+' c on b.ORG_ID=c.ORG_ID  where a.HOSPITAL_SOID=@_hosid and EMPLOYEE_NO=@_id'
			   exec sp_executesql @sqlcx,N'@_ks_id nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_ks_id=@ks_id_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			  

               if( @ks_id <> ISNULL(@ks_id_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_ZGBMK','ks_id',@ks_id,@id,'ORGANIZATION','ORG_NO',ISNULL(@ks_id_60,'无值'),'ks_id不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @ks_id_60=NULL  
    fetch next from mycur_ks_id into @id,@ks_id  
     end      
  close mycur_ks_id          
  DEALLOCATE mycur_ks_id       
  
  select * from #error
      
  drop table #error      
   