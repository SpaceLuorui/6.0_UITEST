alter  proc  usp_win60t_GHZF  
    @xh nvarchar(255)    ----GHZDK.zh
    /*默认调用：usp_win60t_GHZF 1
	  说明：根据挂号xh来作废
   **/         
 as   
 --从前一段取到一条有效挂号记录
 
 
    --挂号作废  
 
 declare @sjh  nvarchar(255)  
      
     select  top 1  @sjh=jssjh from  GH_GHZDK  where xh=@xh  and jlzt=0  
       if(@sjh is null)
	   begin
	        select "此序号无法作废"
            return 
       end
    execute usp_gh_ghzf_wjtest @sjh,"00"  
 --execute usp_gh_ghzf_wjtest @sjh,"00",0,0,"","",0,"","" ,"","",0,0,@zffs=0,@zffs_tf=1,@thyydm="3",@isshowckf= "ZH236",@isshowgbf= "201711103",@m_bIsXztgbf= 1,@m_bIsprintFPforckf= 1  
  
    return    
   
   