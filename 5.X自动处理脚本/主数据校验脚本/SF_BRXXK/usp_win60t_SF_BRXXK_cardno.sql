    
alter  proc  usp_win60t_SF_BRXXK_cardno      
   @hospital_soid  numeric,
   @pkval   nvarchar(255)=''   ----传入表的主键值,默认为空
    /*调用： usp_win60t_SF_BRXXK_cardno 15
	  调用： usp_win60t_SF_BRXXK_cardno 15,3
   */
   
   /**命名规则,以表名加列名结尾
      select * from tab_win60t_MDATA where col='cardno' and tab='SF_BRXXK'	  
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
   declare  @cardno  nvarchar(255)
   declare  @cardno_ys  nvarchar(255)
   declare  @cardno_60  nvarchar(255)

   --需要校验的字段
   declare  @cardtype  nvarchar(255)
   declare  @cardtype_ys  nvarchar(255)
   declare  @cardtype_60  nvarchar(255)

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
	
   --将win60存在的数据插入临时表，供游标使用
   select patid,cardno,cardtype into #curtab from SF_BRXXK_check where  No_in60=0
   --根据入参处理临时表
   if(@pkval<>'')
      delete from #curtab where patid<>@pkval
   --给临时表建索引
	create index ind_patid on #curtab(patid)
   
   --定义游标遍历
   declare mycur_cardno cursor for (select patid,cardno,cardtype from #curtab)
   open mycur_cardno
   fetch next from mycur_cardno into @patid,@cardno,@cardtype
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   --
			     ---非磁卡暂不做判断
			     if(@cardtype<>1) 
				        continue
				    
			   select @sqlcx='select @_cardno=b.IDENTITY_NO from '+ @PERSON+' a inner join '+@PERSONAL_IDENTITY+' b on  a.PERSON_ID=b.PERSON_ID where a.HOSPITAL_SOID=@_soid 
                 and b.IDENTITY_TYPE_CODE=152690 and b.IDENTITY_NO=@_patid' 
			    

				exec sp_executesql @sqlcx,N'@_cardno nvarchar(255) out,@_patid nvarchar(12),@_soid numeric',@_cardno=@cardno_60 OUTPUT,@_patid=@patid,@_soid=@hospital_soid
             
			   --HIS映射关系(从HIS出发，找win60对应的值)：0表示否，1表示是
			    select @cardno_ys=@cardno

			   --注意： 如果返回的XX_60为NULL，说明没有找到对应数据
               if(@cardno_ys <> isnull(@cardno_60,'无值'))
			        insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
					     values ('SF_BRXXK','cardno',@cardno,@patid,'PERSONAL_IDENTITY','IDENTITY_NO',isnull(@cardno_60,'无值'),'cardno不一致')
               --select @ypdm_60
	     
    select @sqlcx=''   --清空sql
	select @cardno_60=NULL
	select @cardno_ys=NULL  
	    
    fetch next from mycur_cardno into @patid,@cardno,@cardtype  
     end      
  close mycur_cardno          
  DEALLOCATE mycur_cardno       
  drop table #curtab

  select * from #error
      
  drop table #error 

  