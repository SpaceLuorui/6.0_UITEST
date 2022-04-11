    
create  proc  usp_win60t_GH_GHZDK_ksdm      
   @hospital_soid  numeric,
   @pkval   nvarchar(255)=''   ----传入表的主键值,默认为空
    /*调用： usp_win60t_GH_GHZDK_ksdm 15
	  调用： usp_win60t_GH_GHZDK_ksdm 15,8
   */
   
   /**命名规则,以表名加列名结尾
      select * from tab_win60t_MDATA where col='ksdm' and tab='GH_GHZDK'	  
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
    
	declare @OUTPATIENT_ENCOUNTER nvarchar(255)  
   select  @OUTPATIENT_ENCOUNTER=dbo.fun_win60test_addsc('OUTPATIENT_ENCOUNTER') 

   --表主键
   declare @xh numeric 

   --需要校验的字段
   declare  @ksdm  nvarchar(255)
   declare  @ksdm_ys  nvarchar(255)
   declare  @ksdm_60  nvarchar(255)
    
   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
	
   --将win60存在的数据插入临时表，供游标使用
   select xh,ksdm into #curtab from GH_GHZDK_check where  No_in60=0
   --根据入参处理临时表
   if(@pkval<>'')
      delete from #curtab where xh<>@pkval
   --给临时表建索引
	create index ind_patid on #curtab(xh)
   
   --定义游标遍历
   declare mycur_ksdm cursor for (select xh,ksdm from #curtab)
   open mycur_ksdm
   fetch next from mycur_ksdm into @xh,@ksdm
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
				    
			   select @sqlcx='select @_ksdm=b.ORG_NO from '+ @OUTPATIENT_ENCOUNTER+' a inner join '+@ORGANIZATION+' b on  a.ENC_DEPT_ID=b.ORG_ID where a.HOSPITAL_SOID=@_soid 
                  and a.ENC_REG_SEQ_NO=@_xh'
				exec sp_executesql @sqlcx,N'@_ksdm nvarchar(255) out,@_xh nvarchar(12),@_soid numeric',@_ksdm=@ksdm_60 OUTPUT,@_xh=@xh,@_soid=@hospital_soid
             
			   --HIS映射关系(从HIS出发，找win60对应的值)：0表示否，1表示是
			    select @ksdm_ys=@ksdm

			   --注意： 如果返回的XX_60为NULL，说明没有找到对应数据
               if(@ksdm_ys <> isnull(@ksdm_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('GH_GHZDK','ksdm',@ksdm,@xh,'ORGANIZATION','ORG_NO',isnull(@ksdm_60,'无值'),'ksdm不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @ksdm_60=NULL
	select @ksdm_ys=NULL  
	    
    fetch next from mycur_ksdm into @xh,@ksdm
     end      
  close mycur_ksdm          
  DEALLOCATE mycur_ksdm       
  drop table #curtab

  select * from #error
      
  drop table #error 

  