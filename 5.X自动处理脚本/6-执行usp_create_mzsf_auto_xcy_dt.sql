CREATE proc [dbo].[usp_create_mzsf_auto_xcy_dt]  
   @ghxh  ut_xh12    ,                             --�Һŵ����   
   @yfdm  ut_ksdm='3001',                           ---����ҩҩ���Ĵ��룬�ٸ�Ĭ��Ϊ3001  
   @i   int                                         --����ҩ���  
 as    
SET NOCOUNT ON  
 ------  
 --HISϵͳ������ҩҩƷȫ���շѣ�����Ĭ��Ϊ1  
 --�˴洢���̹����У�dxmdm��01��02��д���ģ�  
 --�˴洢�����У����Ҵ���Ĭ��2005,ҽ��Ĭ��00,�շѿ��Ҵ���Ĭ��2002,�շѴ��ڴ���01,ҽ��������1,dxmdm=01   
 ---�˴洢�������ա��䡢������Ĭ��01  
-- usp_create_mzsf_auto_xcy_dt  3108487,'3001',300  
 -----  
 begin    
   --1.�ж�ҩ���Ƿ����ҩƷ,  
   declare @countsl  int   
   select @countsl=  count(*)   from VW_MZJGK_LC a(nolock) left join YY_KSBMK b(nolock) on a.ksdm=b.id left join YY_SFDXMK c(nolock) on a.dxmdm=c.id   
                   where   a.ypbz = 0  and  a.ksdm =@yfdm and syfw  in  (1,0)  and dxmdm in ('01','02')  
   if @countsl=0  
   begin  
     select 'F','���������ҩҩ��û��ҩƷ������ҩ�������Ƿ������'  
  return  
   end   
   if @countsl<@i  
   begin  
     select 'T','����ҩҩƷ��ȫ���������'  
  return  
   end   
  
   --2.ȡ���ߵĻ�����Ϣ  
   declare @patid  ut_xh12  
   declare @ghsjh  ut_sjh  
   select @patid=patid,  @ghsjh=jssjh from  GH_GHZDK  where  xh=@ghxh order  by  xh desc  
   if @@error<>0 or @@rowcount=0  
   begin  
     select 'F','ȡ���߻�����Ϣ���ִ�������'  
  return  
   end  
  
   --3.���������Ҫʹ�õı���  
   declare @idm  int   
   declare @cflx  int   
   declare @mzdw varchar(50)   --���ﵥλ  
   declare @mzxs int           --����ϵ��  
   declare @mzdj ut_money      --���ﵥ��  
   declare @xcypmc varchar(200)  --ҩƷ����  
   declare @jssjh  varchar(50)   --�����վݺ�  
   declare @cjmc varchar(200)  
   declare @dxmdm  varchar(50)   --����Ŀ����  
  
   ---ȡ��ҩƷ��idm��Ϣ  
   create table #tab1(xh ut_xh12 IDENTITY(1,1) NOT NULL, idm int )  
   insert into #tab1( idm)  select idm  from VW_MZJGK_LC a  left join YY_KSBMK b on a.ksdm=b.id left join YY_SFDXMK c  on a.dxmdm=c.id   
   where  a.ypbz = 0  and  a.ksdm=@yfdm and  syfw  in  (1,0)   and dxmdm  in ('01','02') order  by idm asc   
   select  @idm=idm from #tab1  where  xh=@i  
  
   --ȡ��ҩƷ����ϸ��Ϣ  
    select  @mzdw=convert(varchar(4),a.mzdw),@xcypmc=a.ypmc ,@mzxs=a.mzxs,  
  @mzdj=dbo.fun_gettxbldj(a.idm,a.ypdm,str(a.ylsj*a.mzxs/a.ykxs,10,4),'1','��')  
  ,@cflx=case when dxmdm='01' then  1 when  dxmdm='02' then  2 else 1 end,  
  @cjmc=a.cjmc,@dxmdm=dxmdm  
  from VW_MZJGK_LC a  left join YY_KSBMK b on a.ksdm=b.id left join YY_SFDXMK c  on a.dxmdm=c.id   
  where  a.idm=@idm  and    a.ypbz = 0  and  a.ksdm=@yfdm and  syfw  in  (1,0)  and a.dxmdm in ('01','02')  
        
  
          
   --5.ҩƷ����Ԥ�㴦��(���Ҵ���Ĭ��2005,ҽ��Ĭ��00,�շѿ��Ҵ���Ĭ��2002,�շѴ��ڴ���01,ҽ��������1,dxmdm=01 )  
   exec usp_sf_sfcl_auto "68071579B77E",1,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"",   "",  0,  ""  ,1,0,1,0,@ghsjh,@ghxh,0,@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--��Ztsql��  
   exec usp_sf_sfcl_auto  "68071579B77E",2,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,@dxmdm,@idm,@idm,@mzdw,@mzxs,1,1,@mzdj,@ghsjh,@ghxh,0,0,@yhdj=0,@ypmc=@xcypmc,@lcxmdm="0",@hjmxxh="0",@lcxmsl=0,@dfpzhzfje=0,@yhdm="
",@tbzddm="",@tbzdmc="",@yhyy=""--��Ztsql��  
   exec usp_sf_sfcl_auto  "68071579B77E",3,0,1,1,0,@patid,"000","00","2005","00","2002",@yfdm,"01","","","1",0,0,@cflx,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0,"",@lcxmsl=0,@dfpzhzfje=0,@yhdm="",@tbzddm="",@tbzdmc="",@yhyy=""--��Ztsql��  
   select top 1  @jssjh= sjh from SF_BRJSK where  patid=@patid  order  by  sfrq  desc   
   --6.ҩƷ���н��㴦��(�ա��䡢������Ĭ��01��)  
   exec usp_sf_sfcl_ex2_auto "68071579B77E",3,2,1,1,0,@patid,@jssjh,"00","2005","00","2002",@yfdm,"01","01","01","1",0,0,@cflx,0,0,"","",0,"",1,0,1,0,@ghsjh,@ghxh,0,0,"",0.00,"","","","","",0,0,0,0,0,0,0,0,0,0,0,0,"",@zlje=2.50,@isQfbz=0,@jslb=0,@ipdz_gxz
sj="172.32.154.71"--��Ztsql��  
   ---7.����Ƶ�δ���  
   declare  @pcdm  varchar(2)  
   declare  @pcmc varchar(10)  
   create table #tab3( rowsl int,pcdm varchar(20) )   --������ʱ��      
   insert  into #tab3( rowsl ,pcdm  )  select distinct  count( * )AS count,pcdm FROM  VW_MZHJCFMXK  where   
   pcdm<>'' and  cd_idm=@idm  and  cfxh  in (select  top  1000 xh from  VW_MZHJCFK  order  by  xh desc  )  
   GROUP BY  pcdm ORDER BY count DESC  
   select  top 1 @pcdm=  rtrim(ltrim(pcdm)) from  #tab3  order by rowsl  desc   
   if len(@pcdm)=1  
     select @pcdm='0'+@pcdm     
   select @pcmc=name  from YY_YZPCK  where rtrim(ltrim(pcdm))=@pcdm   
   ---8.�����÷���Ϣ  
   declare  @yfid  varchar(10)  
   declare  @yfmc varchar(10)  
   create table #tab2( rowsl int,yfdm varchar(20) )   --������ʱ��      
   insert  into #tab2( rowsl ,yfdm  )  select distinct  count( * )AS count,ypyf   
   FROM  VW_MZHJCFMXK    
   where  ypyf<>'' and  cd_idm=@idm  and  cfxh  in (select  top  1000 xh from  VW_MZHJCFK  order  by  xh desc  )  
   GROUP BY     ypyf ORDER BY count DESC  
   select  top 1 @yfid=  yfdm from  #tab2   order by rowsl  desc   
   select @yfmc=name  from SF_YPYFK  where  id=@yfid    
  
   drop table #tab3  
   drop table #tab2  
  
   ---��������jssjh�������վݺţ���zje(�ܽ��)��zfje��֧����,cfts(��������),yfdm(ҩ������),cd_idm(ҩƷidm),ypgg(ҩƷ���)  
   ---cjmc(��������)��mzdj�����ﵥ�ۣ�,ypsl (��С��λ������),pcdm(Ƶ�δ���)��pcmc��Ƶ�����ƣ�,@yfid(�÷�����)��@yfmc���÷����ƣ�  
   select @jssjh as jssjh,a.zje,a.zfje,b.cfts,b.yfdm,c.cd_idm,c.ypgg,@cjmc as cjmc,@mzdj as mzdj,c.ypsl,@xcypmc  as  ypmc  
   ,@pcdm as  pcdm,@pcmc as  pcmc,@yfid  as yfid ,@yfmc as  yfmc  
   from  SF_BRJSK  a,SF_MZCFK  b,SF_CFMXK  c  
   where a.sjh=@jssjh  and  a.ybjszt=2 and b.jlzt=0 and a.sjh=b.jssjh and b.xh=c.cfxh   
end    