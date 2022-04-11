alter  proc  usp_win60t_TD         
   @tabname  nvarchar(255)='', ---表名        
   @colname  nvarchar(255)='', ---列名,根据列的不同生成数据        
   @colval   nvarchar(255)='', ---给列具体的值        
   @i    int=0 -- 为0默认返回此次的数据集，如果传入不是0，就返回最新的1条        
    /*调用举例, 在HIS数据库中执行,返回到win60_auto_checkdata中                  
      制造患者数据,如针对SF_BRXXK.hzxm有两条用例，则会制造两条数据： usp_win60t_TD 'GH_GHZDK','jlzt','0',
   */                 
 as               
    --drop table win60_auto_checkdata        
 --select * from SF_BRXXK order by patid desc       
 SET NOCOUNT ON     
 ---存放每个表的主键和值        
 if not exists(select 1 from sys.objects where name='win60_auto_checkdata')         
 create  table win60_auto_checkdata(        
       id   int   identity(1,1) primary key,        
       procv  nvarchar(255) not null,        
    pkv   nvarchar(255) not null,    ---表主键的值        
    colv  nvarchar(255) not null,    ---表对应列的值,应该可以体现出用例的差别
	show nvarchar(255) null,  ---需要展示的除主键外的其它列的值，在tab_win60t_MDATA中可记录        
    addtime  datetime not null default getdate()        
        
 )         
        
 --记录存储开始执行的时间        
    declare @starttime datetime        
 select @starttime=getdate()        
        
        
 select tab,col,col_range into #proclist_td from tab_win60t_MDATA where tab=@tabname and autodata=1        
 if(@colname<>'')      
    begin        
    delete from #proclist_td where col<>@colname       
    end      
 ---防止入参错误      
 if not exists(select 1 from #proclist_td)       
     begin      
   select '检查表和字段是否对应正确'         
   return      
     end      
       
 --检查值域字段的输入      
 if(@colval<>'')      
    begin      
     declare @rangfilter nvarchar(255)      
  select @rangfilter=col_range from #proclist_td      
   if(charindex(@colval,@rangfilter)=0)      
      begin      
       select '输入字段值不在该字段值域范围内'      
    return      
    end      
 end       
      
      
 declare @proc_td_tab nvarchar(255)        
 declare @proc_td_col nvarchar(255)        
 declare @proc_name nvarchar(255)        
 declare @proc_col_range nvarchar(255)        
 declare @sqlzx nvarchar(2500)        
  declare  mycur_proc_td  cursor for (select tab,col,col_range from #proclist_td)        
  open mycur_proc_td        
  fetch next from  mycur_proc_td into @proc_td_tab,@proc_td_col,@proc_col_range        
     while @@FETCH_STATUS=0        
     begin        
           if(@proc_col_range is null)  ---无值域，一般为可输入控件        
              begin        
                  select @proc_name='usp_win60t_'+@proc_td_tab+'_'+@proc_td_col+'_td'        
             
       if(@colval='')  --无值域,输入值为空      
           select @sqlzx='insert into win60_auto_checkdata(procv,pkv,colv)  exec '+@proc_name    ---走默认数据        
        else  ---无值域,输入值不为空      
           select @sqlzx='insert into win60_auto_checkdata(procv,pkv,colv)  exec '+@proc_name+'  '+@colval  ---走指定数据        
      begin try        
      exec(@sqlzx)        
      end  try        
       begin catch        
       select @sqlzx as 子存储执行错误,        
       error_number()as error_number ,        
       error_message()as error_message,        
       error_state()as error_state,        
       error_severity()as error_severity        
       end catch         
       select @sqlzx=''        
             
     end        
     else   ----有值域的处理        
       begin        
            if(@colval='')---有值域,值无输入，遍历值域生成数据        
           begin        
                while(charindex(',',@proc_col_range)<>0)        
              begin        
         --select charindex(',',@ran)        
         --select  substring(@proc_col_range,0,charindex(',',@proc_col_range))        
             select @proc_name='usp_win60t_'+@proc_td_tab+'_'+@proc_td_col+'_td_'+substring(@proc_col_range,0,charindex(',',@proc_col_range))        
                
    --子存储出参必须在执行语句中定义      
    --定义出参      
      select @sqlzx='declare  @procv_sub  nvarchar(255) declare @pkv_sub  nvarchar(255) declare @colv_sub  nvarchar(255) '      
    --执行存储       
      select @sqlzx=@sqlzx+' exec '+@proc_name +' @procv_sub OUTPUT,@pkv_sub OUTPUT,@colv_sub OUTPUT'  ---走默认数据      
    --插入记录表      
        select @sqlzx=@sqlzx+' insert into win60_auto_checkdata(procv,pkv,colv) select @procv_sub,@pkv_sub,@colv_sub'  ---走默认数据      
              begin try        
           exec(@sqlzx)        
end  try        
           begin catch        
           select @sqlzx as 子存储执行错误,        
           error_number()as error_number ,        
           error_message()as error_message,        
           error_state()as error_state,        
    error_severity()as error_severity        
           end catch      
      
     --给rang重新赋值         
         select @proc_col_range=substring(@proc_col_range,charindex(',',@proc_col_range)+1,len(@proc_col_range))      
         
    --清空使用的变量值        
                select @proc_name=''        
               select @sqlzx=''       
                end        
               
       end        
       else ---- 有值域,值有输入,选中值输出      
         begin        
          select @proc_name='usp_win60t_'+@proc_td_tab+'_'+@proc_td_col+'_td_'+@colval   
          --子存储出参必须在执行语句中定义      
    --定义出参      
      select @sqlzx='declare  @procv_sub  nvarchar(255) declare @pkv_sub  nvarchar(255) declare @colv_sub  nvarchar(255) declare @show nvarchar(255) '      
    --执行存储       
      select @sqlzx=@sqlzx+' exec '+@proc_name +' @procv_sub OUTPUT,@pkv_sub OUTPUT,@colv_sub OUTPUT,@show OUTPUT'  ---走默认数据      
    --插入记录表      
            select @sqlzx=@sqlzx+' insert into win60_auto_checkdata(procv,pkv,colv,show) select @procv_sub,@pkv_sub,@colv_sub,@show'  ---走默认数据      
              begin try   
           
           exec(@sqlzx)        
           end  try        
          begin catch        
          select @sqlzx as 子存储执行错误,        
          error_number()as error_number ,        
          error_message()as error_message,        
          error_state()as error_state,        
          error_severity()as error_severity        
          end catch         
              select @proc_name=''        
               select @sqlzx=''        
         end        
       end        
        
       fetch next from  mycur_proc_td into @proc_td_tab,@proc_td_col,@proc_col_range        
     end        
     close mycur_proc_td        
  deallocate mycur_proc_td        
        
  if(@i=0)        
     select  * from win60_auto_checkdata where addtime>@starttime order by addtime desc        
  else        
  select  top 1 * from win60_auto_checkdata where addtime>@starttime order by addtime desc        
  return 