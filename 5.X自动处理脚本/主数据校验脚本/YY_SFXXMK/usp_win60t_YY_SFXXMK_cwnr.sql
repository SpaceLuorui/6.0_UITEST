    
create  proc  usp_win60t_YY_SFXXMK_cwnr      
			
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_SFXXMK_cwnr 15
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
   declare @CHARGING_ITEM nvarchar(255)  
   select  @CHARGING_ITEM=dbo.fun_win60test_addsc('CHARGING_ITEM')  

   declare @CHARGING_ITEM_DESCRIPTION nvarchar(255)  
   select  @CHARGING_ITEM_DESCRIPTION=dbo.fun_win60test_addsc('CHARGING_ITEM_DESCRIPTION')  

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @cwnr  nvarchar(255)
   declare  @cwnr_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_cwnr cursor for (select id,cwnr from YY_SFXXMK_check)
   open mycur_cwnr
   fetch next from mycur_cwnr into @id,@cwnr
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_cwnr=EXCLUDED_CONTENT from '+ @CHARGING_ITEM+ ' a inner join '+@CHARGING_ITEM_DESCRIPTION+ ' b on a.CHARGING_ITEM_ID=b.CHARGING_ITEM_ID where a.HOSPITAL_SOID=@_hosid and a.CHARGING_ITEM_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_cwnr nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_cwnr=@cwnr_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			   --判断条件： 不存在
               if( (@cwnr_60 <> '不存在' ) and (@cwnr_60<>@cwnr))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_SFXXMK','cwnr',@cwnr,@id,'CHARGING_ITEM_DESCRIPTION','EXCLUDED_CONTENT',@cwnr_60,'cwnr不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @cwnr_60='不存在'  
    fetch next from mycur_cwnr into @id,@cwnr  
     end      
  close mycur_cwnr          
  DEALLOCATE mycur_cwnr       
  
  select * from #error
      
  drop table #error      
   