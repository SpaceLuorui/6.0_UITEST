    
create  proc  usp_win60t_SF_BRXXK_hzxm      
   @hospital_soid  numeric,
   @pkval   nvarchar(255)=''   ----传入表的主键值,默认为空
    /*调用： usp_win60t_SF_BRXXK_hzxm 15
	  调用： usp_win60t_SF_BRXXK_hzxm 15,3
   */
   
   /**命名规则,以表名加列名结尾
      select * from tab_win60t_MDATA where col='hzxm' and tab='SF_BRXXK'	  
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
    declare @PERSON nvarchar(255)  
   select  @PERSON=dbo.fun_win60test_addsc('PERSON')    
    
	declare @PERSONAL_IDENTITY nvarchar(255)  
   select  @PERSONAL_IDENTITY=dbo.fun_win60test_addsc('PERSONAL_IDENTITY') 

   --表主键
   declare @patid numeric 

   --需要校验的字段
   declare  @hzxm  nvarchar(255)
   declare  @hzxm_ys  nvarchar(255)
   declare  @hzxm_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
	
   --将win60存在的数据插入临时表，供游标使用
   select patid,hzxm into #curtab from SF_BRXXK_check where  No_in60=0
   --根据入参处理临时表
   if(@pkval<>'')
      delete from #curtab where patid<>@pkval
   --给临时表建索引
	create index ind_patid on #curtab(patid)
   
   --定义游标遍历
   declare mycur_hzxm cursor for (select patid,hzxm from #curtab)
   open mycur_hzxm
   fetch next from mycur_hzxm into @patid,@hzxm
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   --select  @patid,@ypdm_60 '取值前'
			   select @sqlcx='select @_hzxm=a.FULL_NAME from '+ @PERSON+' a inner join '+@PERSONAL_IDENTITY+' b on  a.PERSON_ID=b.PERSON_ID where a.HOSPITAL_SOID=@_soid 
                 and b.IDENTITY_TYPE_CODE=256730 and b.IDENTITY_NO=@_patid' 
			   exec sp_executesql @sqlcx,N'@_hzxm nvarchar(255) out,@_patid nvarchar(12),@_soid numeric',@_hzxm=@hzxm_60 OUTPUT,@_patid=@patid,@_soid=@hospital_soid
              
			   --HIS映射关系(从HIS出发，找win60对应的值)：0表示否，1表示是
			    select @hzxm_ys=@hzxm

			   --注意： 如果返回的XX_60为NULL，说明没有找到对应数据
               if(@hzxm_ys <> isnull(@hzxm_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('SF_BRXXK','hzxm',@hzxm,@patid,'PERSON','FULL_NAME',isnull(@hzxm_60,'无值'),'hzxm不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @hzxm_60=NULL
	select @hzxm_ys=NULL   
    fetch next from mycur_hzxm into @patid,@hzxm  
     end      
  close mycur_hzxm          
  DEALLOCATE mycur_hzxm       
  drop table #curtab

  select * from #error
      
  drop table #error 

  