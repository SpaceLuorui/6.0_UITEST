    
alter  proc  usp_win60t_YK_YPCDMLK_yply      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YK_YPCDMLK_yply 15
   */
   
   /**命名规则,此条对比ypdm ,以id结尾
      select * from tab_win60t_MDATA where col='yply' and tab='YK_YPCDMLK'
	  select  MEDICINE_PRIMARY_NO  from MATMDM.MEDICINE where MED_EXT_REF_ID=5345
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
  
    
    --1.取动态表
   declare @MEDICINE nvarchar(255)  
   select  @MEDICINE=dbo.fun_win60test_addsc('MEDICINE')  

   declare @VALUE_SET nvarchar(255)  
   select  @VALUE_SET=dbo.fun_win60test_addsc('VALUE_SET') 

   --表主键
   declare @idm numeric 
   declare @msg nvarchar(255)
   --需要校验的字段
   declare  @yply  nvarchar(255)
   declare  @yply_ys  nvarchar(255)   --映射到60转义后的值
   declare  @yply_60  nvarchar(255)
   

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
    
   declare mycur_yply cursor for (select idm,b.name from YK_YPCDMLK_check a inner join YK_YPLYK_check b on a.yply=b.id where a.No_in60=0)
   ----No_in60=0为在win60存在的数据
   open mycur_yply
   fetch next from mycur_yply into @idm,@yply
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   --select  @idm,@ypdm_60 '取值前'
			   select @sqlcx='select @_yply=b.VALUE_DESC from '+ @MEDICINE+ ' a left join '+@VALUE_SET+' b on a.MEDICINE_SOURCE_CODE=b.VALUE_ID where a.HOSPITAL_SOID=@_hosid and a.MED_EXT_REF_ID=@_idm' 
			   exec sp_executesql @sqlcx,N'@_yply nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_yply=@yply_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
                  --     select @yply,@yply_60
				  --    select @yply_ys=case @yply when 0 then 98360 when 1 then 98361 else 99999 end 
      --               if(@yply_ys= 99999)
						--select @msg='his在win60没有找到映射'
						
							if(@yply <> isnull(@yply_60,'无值'))
								--注意插入的是映射前的数据
								 
								begin
								select @msg='列值不一致'
								insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
									 values ('YK_YPCDMLK','yply',@yply,@idm,'MEDICINE,ORGANIZATION','ORG_NO',isnull(@yply_60,'无值'),@msg)
						   --select @ypdm_60
								end
                
    
     select @sqlcx=''   --清空sql
	 select @msg=''
	 select @yply_60=NULL
	 select @yply_ys=NULL
    fetch next from mycur_yply into @idm,@yply  
     end      
  close mycur_yply          
  DEALLOCATE mycur_yply       
  
  select * from #error
      
  drop table #error      
   