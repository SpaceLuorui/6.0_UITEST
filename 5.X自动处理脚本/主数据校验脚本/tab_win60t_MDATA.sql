 create table tab_win60t_MDATA(
    id      int    identity(1,1) ,
    tab nvarchar(255),    ----HIS对应的表名
	col nvarchar(255),    ----表里的字段名
	col_info nvarchar(max) null, ----字段的描述和功能说明
	tab_60 nvarchar(255) null,
	col_60 nvarchar(255) null,
	col_info_60 nvarchar(max) null, 
	autotest int default 0,    ---0表示未完成自动化,1表示已完成自动化
	pk int default 0, ----0表示非主键，1表示主键
	ttype  int null, ---0表示基础数据表，1表示业务数据表
	zid   int not null default 0 --必须要和其它字段一起找映射关系来测试，存在其它字段在此表的id
	autodata int not null default 0 --是否有测试数据自动化的脚本
	primary key(tab,col)
)

/*****
批量插入列名，表名，先将表结构导入当期执行库
declare @sql nvarchar(255)
declare @name nvarchar(255)
declare  mycur cursor for (select  name   from sys.columns   where    object_id=OBJECT_ID('YK_YPCDMLK_check') and name <> 'idm')
 open mycur
fetch next from mycur into @name
while @@FETCH_STATUS=0
begin
select @sql='insert into tab_win60t_MDATA(tab,col)  values(' +''''+'YK_YPCDMLK'+''''+','+''''+@name+''''+')'
 
  exec(@sql)
 select @sql=''
  fetch next from mycur into @name 
end
close mycur          
  DEALLOCATE mycur 
******/


