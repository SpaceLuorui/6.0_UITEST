    
alter  proc  usp_win60t_YK_YPCDMLK_gwypjb      
   @hospital_soid  numeric
    /*供usp_win60test_ypcdmlk 调用，在之前将ypcdmlk数据导入_check表,
	  调用： usp_win60t_YK_YPCDMLK_gwypjb 15
   */
   
   /**命名规则,此条对比ypdm ,以id结尾
      select * from tab_win60t_MDATA where col='gwypjb' and tab='YK_YPCDMLK'
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
   declare  @gwypjb  nvarchar(255)
   declare  @gwypjb_ys  nvarchar(255)   --映射到60转义后的值
   declare  @gwypjb_60  nvarchar(255)
   

   --给60赋值存在相关变量
   declare @sqlcx nvarchar(max)
    declare @count int=0  --- 计数
	
   --注意游标名称，一定要不同
    
   declare mycur_gwypjb cursor for (select idm,gwypjb from YK_YPCDMLK_check  where No_in60=0)
   ----No_in60=0为在win60存在的数据
   open mycur_gwypjb
   fetch next from mycur_gwypjb into @idm,@gwypjb
   while @@FETCH_STATUS=0
      begin
	        --取win60有的数据,如果没有则不用比较
			   
			   --select  @idm,@ypdm_60 '取值前'
			    select @sqlcx='select @_gwypjb=b.VALUE_DESC from '+ @MEDICINE+ ' a left join '+@VALUE_SET+' b on a.HIGH_RISK_MEDICINE_LEVEL_CODE=b.VALUE_ID where a.HOSPITAL_SOID=@_hosid and a.MED_EXT_REF_ID=@_idm' 
			   exec sp_executesql @sqlcx,N'@_gwypjb nvarchar(255) out,@_hosid numeric,@_idm nvarchar(12) ',
			                               @_gwypjb=@gwypjb_60 OUTPUT,@_hosid=@hospital_soid,@_idm=@idm 
                  --     select @gwypjb,@gwypjb_60
				      select @gwypjb_ys=case @gwypjb when 0 then convert(varchar,'不管理') when 1 then 'A级' when 2 then 'B级'else '在60中无映射' end 
                      
					  --if(@gwypjb_ys= 99999)
						--select @msg='his在win60没有找到映射'
						
							if(@gwypjb_ys <> isnull(@gwypjb_60,'无值'))
								--注意插入的是映射前的数据
								 
								begin
								select @msg='列值不一致'
								insert into #error(T_5X,C_5X,V_5X,PK,T_60,C_60,V_60,MSG)
									 values ('YK_YPCDMLK','gwypjb',@gwypjb,@idm,'MEDICINE','HIGH_RISK_MEDICINE_LEVEL_CODE',isnull(@gwypjb_60,'无值'),@msg)
						   --select @ypdm_60
								end
                
    
     select @sqlcx=''   --清空sql
	 select @msg=''
	 select @gwypjb_60=NULL
	 select @gwypjb_ys=NULL
    fetch next from mycur_gwypjb into @idm,@gwypjb  
     end      
  close mycur_gwypjb          
  DEALLOCATE mycur_gwypjb       
  
  select * from #error
      
  drop table #error      
   