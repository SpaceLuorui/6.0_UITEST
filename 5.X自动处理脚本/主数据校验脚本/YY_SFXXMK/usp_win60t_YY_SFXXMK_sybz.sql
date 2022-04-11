    
create  proc  usp_win60t_YY_SFXXMK_sybz      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YY_SFXXMK_sybz 15
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

   --表主键
   declare @id varchar(50) 

   --需要校验的字段
   declare  @sybz  nvarchar(50)
   declare  @sybz_60  nvarchar(50)
   declare @sybz_ys nvarchar(50)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_sybz cursor for (select id,sybz from YY_SFXXMK_check)
   open mycur_sybz
   fetch next from mycur_sybz into @id,@sybz
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_sybz=ENABLED_FLAG from '+ @CHARGING_ITEM+ ' where HOSPITAL_SOID=@_hosid and CHARGING_ITEM_NO=@_id' 
			   exec sp_executesql @sqlcx,N'@_sybz nvarchar(255) out,@_hosid numeric,@_id nvarchar(12) ',
			                               @_sybz=@sybz_60 OUTPUT,@_hosid=@hospital_soid,@_id=@id 
			   --判断条件： 不存在
			    select @sybz_ys=case @sybz when 0 then 98176 when 1 then 98175 else '无映射' end

               if( @sybz_ys<>ISNULL(@sybz_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YY_SFXXMK','sybz',@sybz,@id,'CHARGING_ITEM','ENABLED_FLAG',ISNULL(@sybz_60,'无值'),'sybz不一致')
             
	     
    select @sqlcx=''   --清空sql
	select @sybz_60=''  
    fetch next from mycur_sybz into @id,@sybz  
     end      
  close mycur_sybz          
  DEALLOCATE mycur_sybz       
  
  select * from #error
      
  drop table #error      
   