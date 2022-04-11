      
CREATE  proc  usp_win60test_autocheck_sfxxmk           
   @hisdb   nvarchar(255) ---- his数据库名           
   /*调用举例, 在同一实例下，如数据库名为THIS4_HA, 则 usp_win60test_autocheck_sfxxmk 'THIS4_LYLT'               
               不在同一实例下，如链接服务器为WJLS200,则   usp_win60test_autocheck_sfxxmk 'WJLS200.THIS4_TEST60'      
   */         
 as       
 begin       
   --错误记录表        
  create table #error(        
  id int identity(1,1),        
  checktype nvarchar(255) not null,  ---对比项        
  checkres  nvarchar(255) not null,  ---对比结果        
  T_5X  nvarchar(255) null,   ---5X中的表名        
  T_60  nvarchar(255) null,   ---60中的表名        
  AP_5X nvarchar(255) null,   --5X中的主键的值 或表数据总数        
  AP_60  nvarchar(255) null,  --60中的主键的值 或表数据总数        
  C_5X nvarchar(255) null,   ---5X中的表的列        
  V_5X nvarchar(255) null,   ---5X中的表的列的值        
  C_60 nvarchar(255) null,   ---60中的表的列      
  C_60_filter   nvarchar(2500) null,  -- 1对多关系时，用于选出1对1,来对应5.X的字段      
  V_60 nvarchar(255) null    ---60中的表的列的值      
          
  )           
        
     
   --1.1将表数据拷贝一份到临时表，防止死锁和对比过程引起的性能问题        
   declare @YY_SFXXMK  nvarchar(255)           
   select  @YY_SFXXMK= @hisdb +'.dbo.YY_SFXXMK'        
   declare @copysql nvarchar(255)        
      --物理表性能更好,及时删除即可         
   if exists(select 1 from sys.all_objects where name='YY_SFXXMK_check')        
       drop table  YY_SFXXMK_check        
   select  @copysql='select * into  YY_SFXXMK_check from '+@YY_SFXXMK        
   exec(@copysql)           
        
   --1.1比较收费小项目      
   --CHARGING_ITEM  收费项目    
   --CHARGING_ITEM_DESCRIPTION 收费项目说明  
   --YY_SFXXMK  医保分类库      
       
   declare @count60_CHARGING_ITEM  int        
   declare @count5X_YY_SFXXMK int      
    
   declare @CHARGING_ITEM nvarchar(255)      
   select  @CHARGING_ITEM=dbo.fun_win60test_addsc('CHARGING_ITEM')    
     
   declare @CHARGING_ITEM_DESCRIPTION nvarchar(255)      
   select  @CHARGING_ITEM_DESCRIPTION=dbo.fun_win60test_addsc('CHARGING_ITEM_DESCRIPTION')     
     
   declare @MED_INSTI_CHARGING_ITEM nvarchar(255)      
   select  @MED_INSTI_CHARGING_ITEM=dbo.fun_win60test_addsc('MED_INSTI_CHARGING_ITEM')    
     
   declare @CHARGING_ITEM_CATEGORY nvarchar(255)      
   select  @CHARGING_ITEM_CATEGORY=dbo.fun_win60test_addsc('CHARGING_ITEM_CATEGORY')   
     
   declare @MED_INSTI_CHARGING_ITEM_PRICE nvarchar(255)      
   select  @MED_INSTI_CHARGING_ITEM_PRICE=dbo.fun_win60test_addsc('MED_INSTI_CHARGING_ITEM_PRICE')  
     
   declare @CHARGING_ITEM_PRICE nvarchar(255)      
   select  @CHARGING_ITEM_PRICE=dbo.fun_win60test_addsc('CHARGING_ITEM_PRICE')    
       
   declare @sql1   nvarchar(255)       
   select @sql1= 'select @a=count(*)  from '+ @CHARGING_ITEM      
   exec sp_executesql  @sql1,N'@a int out',@a=@count60_CHARGING_ITEM OUTPUT      
          
   declare @sql nvarchar(500)        
   select @sql=N'select @a=count(*) from YY_SFXXMK_check'         
   exec sp_executesql @sql,N'@a int out',@a=@count5X_YY_SFXXMK OUTPUT        
        
   if(@count60_CHARGING_ITEM <> @count5X_YY_SFXXMK)        
     begin           
   insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60)        
   values('收费小项目信息总数对比','小项目数量不一致','YY_SFXXMK','CHARGING_ITEM',@count5X_YY_SFXXMK,@count60_CHARGING_ITEM)        
  end        
           
        
   --1.2比较收费小项目,可以直接在两个表中查询到列对比        
  declare  @id  nvarchar(12)  --YY_SFXXMK.id  ,主键      
  declare  @sqlpd nvarchar(2500)=''   ---用于判断是否存在的sql语句      
  declare  @sqlcx nvarchar(2500)='' --用于查询    
  
  declare  @sqlcx1 nvarchar(2500)='' --用于查询    
  declare  @sqlcx2 nvarchar(2500)='' --用于查询    
  
  declare  @count_pd int --用于保留数据量      
        
  declare  @name  nvarchar(64)  --YY_SFXXMK.ybsm        
  declare  @name_60  nvarchar(64) --关联CHARGING_ITEM.CHARGING_ITEM_NAME      
      
  declare  @py  nvarchar(64)  --YY_SFXXMK.py        
  declare  @py_60  nvarchar(64) --关联CHARGING_ITEM.PINYIN      
      
  declare  @wb  nvarchar(64)  --YY_SFXXMK.wb        
  declare  @wb_60  nvarchar(64) --关联CHARGING_ITEM.WUBI      
      
  declare  @xmdw  nvarchar(256)  --YY_SFXXMK.xmdw     
  declare  @xmdw_60  nvarchar(256) --CHARGING_ITEM.CHARGE_UNIT  
    
  declare  @memo  nvarchar(256)  --YY_SFXXMK.memo     
  declare  @memo_60  nvarchar(256) --CHARGING_ITEM.MEMO  
    
  declare  @xmsm  nvarchar(256)  --YY_SFXXMK.xmsm     
  declare  @xmsm_60  nvarchar(256) --CHARGING_ITEM_DESCRIPTION.DESCRIPTION    
    
  declare  @cwnr  nvarchar(256)  --YY_SFXXMK.cwnr     
  declare  @cwnr_60  nvarchar(256) --CHARGING_ITEM_DESCRIPTION.EXCLUDED_CONTENT  
    
  declare  @xmnh  nvarchar(256)  --YY_SFXXMK.xmnh    
  declare  @xmnh_60  nvarchar(256) --CHARGING_ITEM_DESCRIPTION.ITEM_CONNOTATION   
    
  declare  @dxmdm  nvarchar(256)  --YY_SFXXMK.dxmdm    
  declare  @dxmdm_60  nvarchar(256) --CHARGING_ITEM_CATEGORY.CHARGING_ITEM_CATEGORY_NO    
    
  declare  @xmdj  nvarchar(256)  --YY_SFXXMK.xmdj    
  declare  @xmdj_60  nvarchar(256) --CHARGING_ITEM_PRICE.UNIT_PRICE    
  
  declare  @sybz  nvarchar(256)  --YY_SFXXMK.xmdj    
  declare  @sybz_60  nvarchar(256) --CHARGING_ITEM_PRICE.UNIT_PRICE   
    
  declare cur_zg cursor for (select id,name,py,wb,xmdw,memo,xmsm,cwnr,xmnh,dxmdm,xmdj,sybz from YY_SFXXMK_check) --ybspbz THIS4_LYLT暂时没有这个字段 不比较    
  open cur_zg        
  fetch next from cur_zg into @id,@name,@py,@wb,@xmdw,@memo,@xmsm,@cwnr,@xmnh,@dxmdm,@xmdj,@sybz  
  while @@FETCH_STATUS=0        
    begin        
     --取win60的数据,有可能在win60没有此数据      
            
      select @sqlpd='select @a1=count(*)  from '+ @CHARGING_ITEM +' where CHARGING_ITEM_NO=@_id'     
      exec sp_executesql @sqlpd,N'@a1 int out,@_id nvarchar(20)',@a1=@count_pd OUTPUT,@_id=@id        
      if(@count_pd>0)        
      begin      
    select @sqlcx='select @_name=CHARGING_ITEM_NAME,@_py=PINYIN,@_wb=WUBI,@_xmdw=CHARGE_UNIT,@_memo=MEMO,@_xmsm=b.DESCRIPTION,@_cwnr=b.EXCLUDED_CONTENT,@_xmnh=b.ITEM_CONNOTATION,@_sybz=case ENABLED_FLAG when 98176 then 0 WHEN 98175 THEN 1 END from '+@CHAR
GING_ITEM +' a inner join '+@CHARGING_ITEM_DESCRIPTION+' b on a.CHARGING_ITEM_ID=b.CHARGING_ITEM_ID where a.CHARGING_ITEM_NO=@_id'    
    exec sp_executesql @sqlcx,N'@_id nvarchar(20),@_name nvarchar(64) out, @_py nvarchar(64) out, @_wb nvarchar(64) out, @_xmdw nvarchar(64) out,@_memo nvarchar(64) out,@_xmsm nvarchar(64) out, @_cwnr nvarchar(64) out,@_xmnh nvarchar(64) out, @_sybz nvarc
har(64) out',  
        @_id=@id,@_name=@name_60 OUTPUT,@_py=@py_60 OUTPUT,@_wb=@wb_60 OUTPUT,@_xmdw=@xmdw_60 OUTPUT,@_memo=@memo_60 OUTPUT,@_xmsm=@xmsm_60 OUTPUT,@_cwnr=@cwnr_60 output, @_xmnh=@xmnh_60 output, @_sybz=@sybz_60  
  
    if(@name<>@name_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'name',@name,'CHARGING_ITEM_NAME',@name_60)     
    
    if(@py<>@py_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'py',@py,'PINYIN',@py_60)     
    
    if(@wb<>@wb_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'wb',@wb,'WUBI',@wb_60)   
      
    if(@xmdw<>@xmdw_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'xmdw',@xmdw,'CHARGE_UNIT',@xmdw_60)   
  
   if(@memo<>@memo_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'memo',@memo,'MEMO',@memo_60)   
      
    if(@xmsm<>@xmsm_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'xmsm',@xmsm,'DESCRIPTION',@xmsm_60)   
  
    if(@cwnr<>@cwnr_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'cwnr',@cwnr,'EXCLUDED_CONTENT',@cwnr_60)   
      
    if(@xmnh<>@xmnh_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'xmnh',@xmnh,'ITEM_CONNOTATION',@xmnh_60)   
  
    if(@sybz<>@sybz_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'sybz',@sybz,'ENABLED_FLAG',@sybz_60)   
      
    select @sqlcx='select  @_dxmdm=C.CHARGING_ITEM_CATEGORY_NO  From '+ @CHARGING_ITEM +' A  INNER JOIN '+ @MED_INSTI_CHARGING_ITEM +' B   
      ON A.CHARGING_ITEM_ID=B.CHARGING_ITEM_ID INNER   JOIN '+ @CHARGING_ITEM_CATEGORY +' C ON C.CHARGING_ITEM_CATEGORY_ID=B.CHARGING_ITEM_CATEGORY_ID where A.CHARGING_ITEM_NO=@_id'  
    exec sp_executesql  @sqlcx,N'@_id nvarchar(20),@_dxmdm nvarchar(64) out ', @_id=@id,@_dxmdm=@dxmdm_60 OUTPUT  
  
    if(@dxmdm<>@dxmdm_60)    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'dxmdm',@dxmdm,'CHARGING_ITEM_CATEGORY_NO',@dxmdm_60)   
  
    select @sqlcx2='select @_xmdj=D.UNIT_PRICE From '+ @CHARGING_ITEM +' A  INNER JOIN '+@MED_INSTI_CHARGING_ITEM+' B   
    ON A.CHARGING_ITEM_ID=B.CHARGING_ITEM_ID  INNER JOIN '+@MED_INSTI_CHARGING_ITEM_PRICE +' C   
    ON C.MED_INSTI_CHARGING_ITEM_ID=B.MED_INSTI_CHARGING_ITEM_ID INNER JOIN '+ @CHARGING_ITEM_PRICE +' D  
    ON D.PRICE_ID=C.PRICE_ID where A.CHARGING_ITEM_NO=@_id'  
    exec sp_executesql  @sqlcx2,N'@_id nvarchar(20),@_xmdj nvarchar(64) out ', @_id=@id,@_xmdj=@xmdj_60 OUTPUT  
  
    if(@xmdj<>Convert(decimal(18,2),@xmdj_60))    
    insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X,C_60,V_60)      
    values('收费小项目信息明细对比','列值不一致','YY_SFXXMK','CHARGING_ITEM',@id,@id,'xmdj',@xmdj,'UNIT_PRICE',@xmdj_60)   
  end         
     else        
     --win60没有此数据               
     insert into #error(checktype,checkres,T_5X,T_60,AP_5X,AP_60,C_5X,V_5X)        
     values('收费小项目信息明细对比','5X有数据,win60没数据','YF_YFDMK','MED_INSTI_INSUR_NAME',@id,'','name',@name)          
              
  select @sqlpd='' --清空判断的sql      
  select @sqlcx='' --清空取值的sql   
  select @sqlcx1='' --清空取值的sql  
  select @sqlcx2='' --清空取值的sql     
    
  select @count_pd=0 --初始化为0             
  fetch next from cur_zg into @id,@name,@py,@wb,@xmdw,@memo,@xmsm,@cwnr,@xmnh,@dxmdm,@xmdj,@sybz      
  end        
  close cur_zg            
  DEALLOCATE cur_zg         
  select id,checktype,checkres,T_5X as '5X的表' ,T_60 as '60的表',AP_5X as '5X的表总数或主键值',AP_60 'win60表总数或主键值',C_5X '5X列名',        
  V_5X '5X列值',C_60 '60列名',V_60 '60列值',C_60_filter '60的关联' from #error        
  drop table #error        
end      
      
      