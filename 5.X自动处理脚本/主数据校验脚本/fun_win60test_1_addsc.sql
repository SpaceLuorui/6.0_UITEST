create function fun_win60test_1_addsc( 
     @tabname varchar(255)   ---原有表名
	 )
	 returns varchar(255)
	as
    /**
    win60不同的表，需要走不同的schema,存储表名需要处理schema
    
    **/
	begin
    select @tabname=b.name+'.'+@tabname from sys.all_objects a inner join sys.schemas b on a.schema_id=b.schema_id  where a.name=@tabname and type='U'
	return(@tabname)
   end