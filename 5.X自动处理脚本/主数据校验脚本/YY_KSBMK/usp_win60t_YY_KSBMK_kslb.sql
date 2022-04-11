    
create  proc  usp_win60t_YY_KSBMK_kslb      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YK_YPCDMLK_memo 15
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
   declare @ORGANIZATION nvarchar(255)  
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION')  

   declare @BUSINESS_UNIT nvarchar(255)  
   select  @BUSINESS_UNIT=dbo.fun_win60test_addsc('BUSINESS_UNIT') 

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @kslb  nvarchar(50)
   declare  @kslb_ys  nvarchar(50)
   declare  @kslb_60  nvarchar(50)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_kslb cursor for (select id,kslb from YY_KSBMK_check)
   open mycur_kslb
   fetch next from mycur_kslb into @id,@kslb
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_kslb=BU_TYPE_CODE from '+ @ORGANIZATION+ ' a inner join '+@BUSINESS_UNIT+' b on a.ORG_ID=b.ORG_ID where a.HOSPITAL_SOID=@_hosid and ORG_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_kslb nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_kslb=@kslb_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 

			  --HIS映射关系(从HIS出发，找win60对应的值)
			    select @kslb_ys=case @kslb when 0 then 256002 when 1 then 256004 when 2 then 256005 when 3 then 256006 when 4 then 256010 else '无映射' end
			   --判断条件： 不存在
               if( @kslb_ys<>ISNULL(@kslb_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_KSBMK','kslb',@kslb,@id,'BUSINESS_UNIT','BU_TYPE_CODE',@kslb_60,'kslb不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @kslb_60='无值'  
    fetch next from mycur_kslb into @id,@kslb  
     end      
  close mycur_kslb          
  DEALLOCATE mycur_kslb       
  
  select * from #error
      
  drop table #error      
   