    
alter  proc  usp_win60t_YK_YPCDMLK_cjdm      
   @hospital_soid  numeric,
   @pkval   nvarchar(255)=''   ----传入表的主键值,默认为空
    /*调用： usp_win60t_YK_YPCDMLK_cjdm 15
	  调用： usp_win60t_YK_YPCDMLK_cjdm 15,2269
   */
   
   /**命名规则,以表名加列名结尾
      select * from tab_win60t_MDATA where col='cjdm' and tab='YK_YPCDMLK'	  
   **/       
 as 
	  ---不放在父存储，便于单独调试
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

   declare @ORGANIZATION nvarchar(255)  
   select  @ORGANIZATION=dbo.fun_win60test_addsc('ORGANIZATION')

   --declare @VALUE_SET nvarchar(255)  
   --select  @VALUE_SET=dbo.fun_win60test_addsc('VALUE_SET')

   --表主键
   declare @idm numeric 

   --需要校验的字段
   declare  @cjdm  nvarchar(255)
   declare  @cjdm_ys  nvarchar(255)
   declare  @cjdm_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
	
   --将win60存在的数据插入临时表，供游标使用
   select idm,cjdm into #curtab from YK_YPCDMLK_check where  No_in60=0

   --根据入参处理临时表
   if(@pkval<>'')
      delete from #curtab where idm<>@pkval
   
   --定义游标遍历
   declare mycur_cjdm cursor for (select idm,cjdm from #curtab)
   open mycur_cjdm
   fetch next from mycur_cjdm into @idm,@cjdm
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_cjdm=b.ORG_NO from '+ @MEDICINE+ ' a inner join '+@ORGANIZATION+' b on a.MANUFACTURER_ID=b.ORG_ID where a.HOSPITAL_SOID=@_hosid and a.MED_EXT_REF_ID=@_idm' 
			   
			   exec sp_executesql @sqlcx,N'@_cjdm nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_cjdm=@cjdm_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
			   --HIS映射关系(从HIS出发，找win60对应的值)：0表示否，1表示是
			   --无需映射
			    select @cjdm_ys=@cjdm

			   --注意： 如果返回的XX_60为NULL，说明没有找到对应数据
               if(@cjdm_ys <> isnull(@cjdm_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('YK_YPCDMLK','cjdm',@cjdm,@idm,'MEDICINE_DETAIL','ORG_NO',isnull(@cjdm_60,'无值'),'cjdm不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @cjdm_60=NULL
	select @cjdm_ys=NULL   
    fetch next from mycur_cjdm into @idm,@cjdm  
     end      
  close mycur_cjdm          
  DEALLOCATE mycur_cjdm       
  drop table #curtab

  select * from #error
      
  drop table #error 

  