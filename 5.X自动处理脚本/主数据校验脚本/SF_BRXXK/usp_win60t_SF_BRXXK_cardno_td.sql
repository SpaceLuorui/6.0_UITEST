alter  proc  usp_win60t_SF_BRXXK_cardno_td
   @cardnoinput nvarchar(255)=''  
    /*默认调用： 从HIS库制造姓名不同的门诊患者
	  传值调用： 生成一个指定姓名的患者 usp_win60t_SF_BRXXK_cardno_td '03311719' 
   **/         
 as   
 --  
    if(@cardnoinput='')
	  begin
   declare @i int  ---用例数据循环产生  
      
   select @i=1  
   while(@i<3)  
   begin  
      
    declare @blh varchar(50)      
       exec usp_yy_createblh_wj 'SF_BRXXK','blh',@blh output  --【Ztsql】     
       select @blh=substring(@blh,2,24)  
       select @blh=@blh  
       
     --卡号变量赋值    
             declare @cardno  varchar(50)   
       
             set @cardno='WT'+@blh  
              
       
   --2.基本信息登记  
   --姓名为常规汉字  
       if(@i=1)  ---cardtype=0,卡号为空  
        exec usp_gh_jbxxdj_wj @blh,"wj卡类型为0","00","0",'',"","","","","                  ","女","20000330",'',"","","","1","","","","","","","","",0,"","","","500243","",0,"","","","","","","",0,"","0714",1,"","","","","无","","0","0","",'0',@jhrxm="",@jhrzjlx='0',@jhrsfzh="",@ay= '',@birthtime = "00:00",@bmdm = "",@ghtxfw = 0 ,@xxmc = "",@qebzkh="",@brlydm='',@jsr='',@lskbz=0,@zjxy="",@qqwxid="",@lxrgx="",@lxrdh="",@lxrdz="",@csd_jd="",@hkd_sid="",@hkd_sname="",@hkd_qid="",@hkd_qname="",@hkd_addr="",@jzd_sid="",@jzd_sname="",@jzd_qid="",@jzd_qname="",@jzd_addr="",@csd_djsid="500200",@csd_djsname="市辖区",@hkd_djsid="",@hkd_djsname="",@jzd_djsid="",@jzd_djsname="",@xwdm="",@brtslb="",@txmszm="",@pkhbz="0",@rqfldm="0004",@dqxxbz="0",@hkd_jd="",@jzd_jd="",@qtbzjh='',@hkd_jdid="",@jzd_jdid="",@csd_jdid="",@ksdm="000",@bmjb="1"  
   
    if(@i=2)   ---卡号为中英文,cardtype为1,磁卡         
      exec usp_gh_jbxxdj_wj @blh,"wj卡类型为1","00","1",@cardno,"","","","","                  ","女","20000330",'',"","","","1","","","","","","","","",0,"","","","500243","",0,"","","","","","","",0,"","0714",1,"","","","","无","","0","0","",'0',@jhrxm="",@jhrzjlx='0',@jhrsfzh="",@ay= '',@birthtime = "00:00",@bmdm = "",@ghtxfw = 0 ,@xxmc = "",@qebzkh="",@brlydm='',@jsr='',@lskbz=0,@zjxy="",@qqwxid="",@lxrgx="",@lxrdh="",@lxrdz="",@csd_jd="",@hkd_sid="",@hkd_sname="",@hkd_qid="",@hkd_qname="",@hkd_addr="",@jzd_sid="",@jzd_sname="",@jzd_qid="",@jzd_qname="",@jzd_addr="",@csd_djsid="500200",@csd_djsname="市辖区",@hkd_djsid="",@hkd_djsname="",@jzd_djsid="",@jzd_djsname="",@xwdm="",@brtslb="",@txmszm="",@pkhbz="0",@rqfldm="0004",@dqxxbz="0",@hkd_jd="",@jzd_jd="",@qtbzjh='',@hkd_jdid="",@jzd_jdid="",@csd_jdid="",@ksdm="000",@bmjb="1"  
      -- if(@i=3)   ---卡号为中英文,cardtype为1,卡  
 
         
    select @i=@i+1  
    end  
  
  ---返回给主存储
  select  top 2 'SF_BRXXK.cardno',patid,cardno from SF_BRXXK where hzxm like 'wj%' order by lrrq desc,patid desc  
  end
  else
     begin
	     declare @blh2 varchar(50)      
       exec usp_yy_createblh_wj 'SF_BRXXK','blh',@blh2 output  --【Ztsql】     
       select @blh=substring(@blh2,2,24)  
       
       
     --卡号变量赋值
			 
			 exec usp_gh_jbxxdj_wj @blh2,"wj卡类型为1","00","1",@cardnoinput,"","","","","                  ","女","20000330",'',"","","","1","","","","","","","","",0,"","","","500243","",0,"","","","","","","",0,"","0714",1,"","","","","无","","0","0","",'0',@jhrxm="",@jhrzjlx='0',@jhrsfzh="",@ay= '',@birthtime = "00:00",@bmdm = "",@ghtxfw = 0 ,@xxmc = "",@qebzkh="",@brlydm='',@jsr='',@lskbz=0,@zjxy="",@qqwxid="",@lxrgx="",@lxrdh="",@lxrdz="",@csd_jd="",@hkd_sid="",@hkd_sname="",@hkd_qid="",@hkd_qname="",@hkd_addr="",@jzd_sid="",@jzd_sname="",@jzd_qid="",@jzd_qname="",@jzd_addr="",@csd_djsid="500200",@csd_djsname="市辖区",@hkd_djsid="",@hkd_djsname="",@jzd_djsid="",@jzd_djsname="",@xwdm="",@brtslb="",@txmszm="",@pkhbz="0",@rqfldm="0004",@dqxxbz="0",@hkd_jd="",@jzd_jd="",@qtbzjh='',@hkd_jdid="",@jzd_jdid="",@csd_jdid="",@ksdm="000",@bmjb="1"  
       
	   select  top 1 'SF_BRXXK.cardno',patid,cardno from SF_BRXXK where cardno=@cardnoinput order by lrrq desc,patid desc

	 end
   
   