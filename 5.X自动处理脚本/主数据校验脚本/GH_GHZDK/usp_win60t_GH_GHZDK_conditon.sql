alter  proc  usp_win60t_GH_GHZDK_conditon  
     @patid nvarchar(255) output   ---返回患者的patid  
    /*调用： 挂号数据的前提条件: 需要一个患者病人  
   
   **/         
 as   
    
   declare @blh varchar(50)      
       exec usp_yy_createblh_wj 'SF_BRXXK','blh',@blh output  --【Ztsql】     
       select @blh=substring(@blh,2,24)  
       select @blh=@blh  
       
     --卡号变量赋值    
             declare @cardno  varchar(50)   
       
             set @cardno='WT'+@blh  
              
       
      
     exec usp_gh_jbxxdj_wj @blh,"挂号前置1","00","1",@cardno,"","","","","                  ","女","20000330",'',"","","","1","","","","","","","","",0,"","","","500243","",0,"","","","","","","",0,"","0714",1,"","","","","无","","0","0","",'0',@jhrxm="",@jhrzjlx='0',@jhrsfzh="",@ay= '',@birthtime = "00:00",@bmdm = "",@ghtxfw = 0 ,@xxmc = "",@qebzkh="",@brlydm='',@jsr='',@lskbz=0,@zjxy="",@qqwxid="",@lxrgx="",@lxrdh="",@lxrdz="",@csd_jd="",@hkd_sid="",@hkd_sname="",@hkd_qid="",@hkd_qname="",@hkd_addr="",@jzd_sid="",@jzd_sname="",@jzd_qid="",@jzd_qname="",@jzd_addr="",@csd_djsid="500200",@csd_djsname="市辖区",@hkd_djsid="",@hkd_djsname="",@jzd_djsid="",@jzd_djsname="",@xwdm="",@brtslb="",@txmszm="",@pkhbz="0",@rqfldm="0004",@dqxxbz="0",@hkd_jd="",@jzd_jd="",@qtbzjh='',@hkd_jdid="",@jzd_jdid="",@csd_jdid="",@ksdm="000",@bmjb="1"  
        
   --返回paitd 
 
   select   @patid=patid  from SF_BRXXK where blh=@blh 
  
   return  
   
   