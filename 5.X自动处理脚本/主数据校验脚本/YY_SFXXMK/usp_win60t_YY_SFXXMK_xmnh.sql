    
create  proc  usp_win60t_YY_SFXXMK_xmnh      
			
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_SFXXMK_xmnh 15
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
   declare  @xmnh  nvarchar(255)
   declare  @xmnh_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_xmnh cursor for (select id,xmnh from YY_SFXXMK_check)
   open mycur_xmnh
   fetch next from mycur_xmnh into @id,@xmnh
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_xmnh=ITEM_CONNOTATION from '+ @CHARGING_ITEM+ ' a inner join '+@CHARGING_ITEM_DESCRIPTION+ ' b on a.CHARGING_ITEM_ID=b.CHARGING_ITEM_ID where a.HOSPITAL_SOID=@_hosid and a.CHARGING_ITEM_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_xmnh nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_xmnh=@xmnh_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			   --判断条件： 不存在
               if( (@xmnh_60 <> '不存在' ) and (@xmnh_60<>@xmnh))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_SFXXMK','xmnh',@xmnh,@id,'CHARGING_ITEM_DESCRIPTION','ITEM_CONNOTATION',@xmnh_60,'xmnh不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @xmnh_60='不存在'  
    fetch next from mycur_xmnh into @id,@xmnh  
     end      
  close mycur_xmnh          
  DEALLOCATE mycur_xmnh       
  
  select * from #error
      
  drop table #error      
   