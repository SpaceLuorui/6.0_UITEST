    
alter  proc  usp_win60t_YK_YPCDMLK_basicdrug_flag      
   @hospital_soid  numeric,
   @pkval   nvarchar(255)=''   ----传入表的主键值,默认为空
    /*调用： usp_win60t_YK_YPCDMLK_basicdrug_flag 15
	  调用： usp_win60t_YK_YPCDMLK_basicdrug_flag 15,2269
   */
   
   /**命名规则,以表名加列名结尾
      select * from tab_win60t_MDATA where col='basicdrug_flag' and tab='YK_YPCDMLK'	  
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
   declare @MEDICINE nvarchar(255)  
   select  @MEDICINE=dbo.fun_win60test_addsc('MEDICINE')  

   declare @MEDICINE_DETAIL nvarchar(255)  
   select  @MEDICINE_DETAIL=dbo.fun_win60test_addsc('MEDICINE_DETAIL')

   declare @VALUE_SET nvarchar(255)  
   select  @VALUE_SET=dbo.fun_win60test_addsc('VALUE_SET')

   --表主键
   declare @idm numeric 

   --需要校验的字段
   declare  @basicdrug_flag  nvarchar(255)
   declare  @basicdrug_flag_ys  nvarchar(255)
   declare  @basicdrug_flag_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
	
   --将win60存在的数据插入临时表，供游标使用
   select idm,basicdrug_flag into #curtab from YK_YPCDMLK_check where  No_in60=0

   --根据入参处理临时表
   if(@pkval<>'')
      delete from #curtab where idm<>@pkval
   
   --定义游标遍历
   declare mycur_basicdrug_flag cursor for (select idm,basicdrug_flag from #curtab)
   open mycur_basicdrug_flag
   fetch next from mycur_basicdrug_flag into @idm,@basicdrug_flag
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_basicdrug_flag=c.VALUE_DESC from '+ @MEDICINE+ ' a left join '+@MEDICINE_DETAIL+' b on a.MEDICINE_ID=b.MEDICINE_ID 
			                  left join '+@VALUE_SET+' c on b.NEDL_FLAG=c.VALUE_ID 
			          where a.HOSPITAL_SOID=@_hosid and a.MED_EXT_REF_ID=@_idm' 
			   exec sp_executesql @sqlcx,N'@_basicdrug_flag nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_basicdrug_flag=@basicdrug_flag_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
			   --HIS映射关系(从HIS出发，找win60对应的值)：0表示否，1表示是
			    select @basicdrug_flag_ys=case @basicdrug_flag when 0 then CONVERT(nvarchar,'否') when 1 then '是' else '无映射' end

			   --注意： 如果返回的XX_60为NULL，说明没有找到对应数据
               if(@basicdrug_flag_ys <> isnull(@basicdrug_flag_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YK_YPCDMLK','basicdrug_flag',@basicdrug_flag,@idm,'MEDICINE_DETAIL','DDD_UNIT_CODE',isnull(@basicdrug_flag_60,'无值'),'basicdrug_flag不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @basicdrug_flag_60=NULL
	select @basicdrug_flag_ys=NULL   
    fetch next from mycur_basicdrug_flag into @idm,@basicdrug_flag  
     end      
  close mycur_basicdrug_flag          
  DEALLOCATE mycur_basicdrug_flag       
  drop table #curtab

  select * from #error
      
  drop table #error 

  