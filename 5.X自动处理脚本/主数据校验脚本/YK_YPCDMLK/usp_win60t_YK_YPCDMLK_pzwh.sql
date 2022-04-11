    
alter  proc  usp_win60t_YK_YPCDMLK_pzwh      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YK_YPCDMLK_pzwh 15
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
  ) 
  
    
    --1.比较ypdm
   declare @MEDICINE nvarchar(255)  
   select  @MEDICINE=dbo.fun_win60test_addsc('MEDICINE')  

   declare @MEDICINE_DETAIL nvarchar(255)  
   select  @MEDICINE_DETAIL=dbo.fun_win60test_addsc('MEDICINE_DETAIL')

   --表主键
   declare @idm numeric 

   --需要校验的字段
   declare  @pzwh  nvarchar(255)
   declare  @pzwh_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
   declare mycur_pzwh cursor for (select idm,pzwh from YK_YPCDMLK_check where  No_in60=0)
   open mycur_pzwh
   fetch next from mycur_pzwh into @idm,@pzwh
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_pzwh=b.APPROVAL_NO from '+ @MEDICINE+ ' a left join '+@MEDICINE_DETAIL+' b on a.MEDICINE_ID=b.MEDICINE_ID where a.HOSPITAL_SOID=@_hosid and a.MED_EXT_REF_ID=@_idm' 
			   exec sp_executesql @sqlcx,N'@_pzwh nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_pzwh=@pzwh_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
			   --判断条件： 不存在
               if(@pzwh <> isnull(@pzwh_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YK_YPCDMLK','pzwh',@pzwh,@idm,'MEDICINE','PINYIN',isnull(@pzwh_60,'无值'),'pzwh不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @pzwh_60=NULL  
    fetch next from mycur_pzwh into @idm,@pzwh  
     end      
  close mycur_pzwh          
  DEALLOCATE mycur_pzwh       
  
  select * from #error
      
  drop table #error      
   