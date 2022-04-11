    
create  proc  usp_win60t_ypcdmlk_ypdm_5      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_ypcdmlk_ypdm_5 15
   */
   
   /**命名规则,此条对比ypdm ,以id结尾
      select id from tab_win60t_MDATA where col='ypdm' and tab='YK_YPCDMLK'
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
		  PNAME nvarchar(255) not null,---子存储的序号   
  ) 
  
    
    --1.比较ypdm
   declare @MEDICINE nvarchar(255)  
   select  @MEDICINE=dbo.fun_win60test_addsc('MEDICINE')  

   --表主键
   declare @idm numeric 

   --需要校验的字段
   declare  @ypdm  nvarchar(255)
   declare  @ypdm_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   declare mycur cursor for (select idm,ypdm from YK_YPCDMLK_check)
   open mycur
   fetch next from mycur into @idm,@ypdm
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_ypdm=MEDICINE_PRIMARY_NO from '+ @MEDICINE+ ' where HOSPITAL_SOID=@_hosid and MED_EXT_REF_ID=@_idm' 
			   exec sp_executesql @sqlcx,N'@_ypdm nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_ypdm=@ypdm_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
			   --判断条件： 不存在
               if( (@ypdm_60 <> '不存在' ) and (@ypdm_60<>@ypdm))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG,PNAME)
					     values ('YK_YPCDMLK','ypdm',@ypdm,@idm,'MEDICINE','MEDICINE_PRIMARY_NO',@ypdm_60,'ypdm不一致','usp_win60test_ypcdmlk_5')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @ypdm_60='不存在'  
    fetch next from mycur into @idm,@ypdm  
     end      
  close mycur          
  DEALLOCATE mycur       
  
  select * from #error
      
  drop table #error      
   