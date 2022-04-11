alter proc tab_win60t_ALL_INIT      
     @hisdb   nvarchar(255), ---hisdb    
     @tab  nvarchar(255),  ---表名必须传入,根据表名来逐个copy数据，防止一起copy出现错误无法继续，捕获异常放在游标里展示，不在子存储处理    
     @keyvalue  nvarchar(255)='', ---主键值 
	 @keyvalue2  nvarchar(255)='' ---第二主键值   
 as      
    /**      
    初始化主数据的表数据,从his表中导入到win60中    
 tab_win60t_ALL_INIT 'WJLS200.THIS4_TEST60','YK_YPCDMLK','2269' 
 tab_win60t_ALL_INIT 'WJLS200.THIS4_TEST60','YF_YFZKC','1','3202'   
 select * from YF_YFZKC_check    
    **/      
       
   --处理基础表数据，拷贝至win60,主键列不用放入    
   declare @col nvarchar(255)      
   declare @tab_check nvarchar(255)      
   declare @tabfullname nvarchar(255)      
   declare @dropsql nvarchar(255)      
   declare @copysql nvarchar(255)         
        
    select @tab_check=@tab+'_check'      
       select  @tabfullname= @hisdb +'.dbo.'+@tab      
           --物理表性能更好,及时删除即可    
     if exists(select 1 from sys.all_objects where name=@tab_check)              
       begin    
    select @dropsql='drop table '+@tab_check      
       exec(@dropsql)      
    end    
     
	if(@keyvalue2='')   ---单主键的表处理
	begin
    select @col=col from tab_win60t_MDATA where tab=@tab and pk=1    --取主键列,主键倒序排列    
    if(@col is null)    
    begin    
    select @tab+'主键未维护，必须维护用来排序取数据'    
    return    
    end        
   if(@keyvalue='')   --初始化sql    
    select  @copysql='select * into '+@tab_check+'  from '+@tabfullname +' order by '+@col+' desc  alter table '+ @tab_check+' add  No_in60 int not null default 0  '   --0为存在，1为不存在      
   else    
       begin    
             
         select  @copysql='select * into '+@tab_check+'  from '+@tabfullname +' where '+@col+'='+@keyvalue+' alter table '+ @tab_check+' add  No_in60 int not null default 0'   --0为存在，1为不存在      
   end    
      exec(@copysql)    
    
   --给主键加主键约束    
   declare @sql_addpk nvarchar(255)       
   declare @pkname nvarchar(255)       
   select @pkname='PK_'+@tab_check    
   select @sql_addpk='alter table '+@tab_check+' add constraint '+@pkname+ ' primary key('+@col+')'    
   exec(@sql_addpk)    
    
   --给列No_in60加索引    
   declare @sql_addindex nvarchar(255)    
   select @sql_addindex='create index index_in60 on '+@tab_check+'(NO_in60)'    
   exec(@sql_addindex)    
     end
	 else   ----双主键情况
	  begin
	    declare @col2 nvarchar(255) ---第二主键
	 
	    select @col=col from tab_win60t_MDATA where tab=@tab and pk=1    --取主键列,主键倒序排列  
		select @col2=col from tab_win60t_MDATA where tab=@tab and pk=2    --取主键列,主键倒序排列  
			if((@col is null)or(@col2 is null))  
			begin    
			select @tab+'双主键未维护，必须维护用来排序取数据'    
			return    
			end
			
		--值先加双引号后再拼接
		    select  @keyvalue2=''''+@keyvalue2+''''
		    select  @keyvalue=''''+@keyvalue+''''

     select  @copysql='select * into '+@tab_check+'  from '+@tabfullname +' where '+@col+'='+@keyvalue+' and '+@col2+'='+@keyvalue2+' alter table '+ @tab_check+' add  No_in60 int not null default 0'   --0为存在，1为不存在      
      --select @copysql
      exec(@copysql)    
    
   --给主键加主键约束    
   declare @sql_addpk2 nvarchar(255)       
   declare @pkname2 nvarchar(255)       
   select @pkname='PK_'+@tab_check    
   select @sql_addpk='alter table '+@tab_check+' add constraint '+@pkname+ ' primary key('+@col+','+@col2+')'    
   exec(@sql_addpk2)    
    
   --给列No_in60加索引    
   declare @sql_addindex2 nvarchar(255)    
   select @sql_addindex2='create index index_in60 on '+@tab_check+'(NO_in60)'    
   exec(@sql_addindex2)


	 end
   --create index in60index on  SF_BRXXK_check(NO_in60)    
   --alter table SF_BRXXK_check add constraint PK_patid primary key(patid)    
         
         
        
      
          
      
      
          
       
      