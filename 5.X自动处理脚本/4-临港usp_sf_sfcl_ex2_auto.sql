CREATE proc usp_sf_sfcl_ex2_auto
	@wkdz varchar(32),
	@jszt smallint,
	@sfbz smallint,
	@sflb smallint,
	@czksfbz int,
	@qrbz ut_bz,
	@patid ut_xh12,
	@sjh ut_sjh,
	@czyh ut_czyh,
	@ksdm ut_ksdm,
	@ysdm ut_czyh, 
	@sfksdm ut_ksdm,
	@yfdm ut_ksdm,
	@sfckdm ut_dm2,
	@pyckdm ut_dm2, 
	@fyckdm ut_dm2,
	@ybdm ut_ybdm,
	@cfxh int,
	@hjxh ut_xh12,
	@cflx ut_bz,
	@sycfbz ut_bz,
	@tscfbz ut_bz,
	@dxmdm ut_kmdm,
	@xxmdm ut_xmdm,
	@idm ut_xh9,
	@ypdw ut_unit,
	@dwxs ut_dwxs,
	@fysl ut_sl10,
	@cfts integer,
	@ypdj ut_money,
	@ghsjh ut_sjh = null,
	@ghxh ut_xh12 = null,
	@tcljje numeric(12,2) = 0,
	@shbz	ut_bz = 0,
	@zph varchar(32) = null,
	@zpje numeric(12,2) = null,
	@zhbz ut_zhbz = null,
	@zddm ut_zddm = null,
	@zxlsh ut_lsh = null,
	@jslsh ut_lsh = null,
	@xmlb ut_dm2 = null,
	@qfdnzhzfje numeric(12,2) = null,
	@qflnzhzfje numeric(12,2) = null,
	@qfxjzfje numeric(12,2) = null,
	@tclnzhzfje numeric(12,2) = null,
	@tcxjzfje numeric(12,2) = null,
	@tczfje numeric(12,2) = null,
	@fjlnzhzfje numeric(12,2) = null,
	@fjxjzfje numeric(12,2) = null,
	@dffjzfje numeric(12,2) = null,
	@dnzhye numeric(12,2) = null,
	@lnzhye numeric(12,2) = null,
	@qkbz smallint = 0,
	@jsrq ut_rq16 = ''
	,@ylcardno ut_cardno=''
	,@ylkje ut_money=0
	,@ylkhcysje ut_money=0
	,@ylksqxh ut_lsh=''
	,@ylkzxlsh ut_lsh=''
	,@ylkhcyssqxh ut_lsh=''
	,@ylkhcyszxlsh ut_lsh=''
	,@cardxh ut_xh12=0			--add by chenwei 2003.12.06
	,@cardje ut_money=0			--ͬ��
	,@bdyhkje ut_money = 0
	,@bdyhklsh ut_lsh = ''
	,@zlje	ut_money=0			--add by ozb 20060622 ������
	,@isQfbz ut_bz  = 0   --add by will
	,@hcsjh ut_sjh=''  --�Է�תҽ��ʱ�õ�,ָ����¼��sjh
	,@jslb	smallint = 0	--�������0����һ�ν���1�ַ�Ʊ��ν���
	,@ipdz_gxzsj VARCHAR(30)=''
as
/**********
[�汾��]4.0.0.0.0
[����ʱ��]2004.10.25
[����]����
[��Ȩ] Copyright ? 2004-2008�Ϻ����˴�-��������ɷ����޹�˾[����]�շѴ���
[����˵��]
	�շѴ�����(ֻ����sfbz=2)
[����˵��]
	@wkdz varchar(32),	������ַ
	@jszt smallint,		����״̬	1=������2=���룬3=�ݽ�
	@sfbz smallint,		�շѱ�־0=Ԥ�㣬1=�ݽ�(����1), 2=��ʽ�ݽ�(����2)
	@sflb smallint,		�շ����1=��ͨ��2=����
	@czksfbz int,		��ֵ���շѱ�־�� 0 :���ӳ�ֵ���շ�  ��1 �ӳ�ֵ���շ� add by szj
	@qrbz ut_bz,		ȷ�ϱ�־0=��ͨ��1=����(ҽ���շ���)
	@patid ut_xh12,		����Ψһ��ʶ
  	@sjh ut_sjh,		�վݺ�
	@czyh ut_czyh,		����Ա��
  	@ksdm ut_ksdm,		���Ҵ���
  	@ysdm ut_czyh,		ҽ������
	@sfksdm ut_ksdm,	�շѿ��Ҵ���
	@yfdm ut_ksdm,		ҩ������
	@sfckdm ut_dm2,		�շѴ��ڴ���
	@pyckdm ut_dm2,		��ҩ���ڴ���
	@fyckdm ut_dm2,		��ҩ���ڴ���
	@ybdm ut_ybdm,		ҽ������
	@cfxh int,			�������
	@hjxh ut_xh12,		�������
	@cflx ut_bz,		�������1:��ҩ����,2:��ҩ����,3:��ҩ����,4:���ƴ���
	@sycfbz ut_bz,		��Һ������־0:��ͨ������1:��Һ����
	@tscfbz ut_bz,		���⴦����־0:��ͨ������1:��֢����
	@dxmdm ut_kmdm,		����Ŀ����
	@xxmdm ut_xmdm,		С��Ŀ���루ҩƷ���룩
	@idm ut_xh9,		����idm
	@ypdw ut_unit,		ҩƷ��λ
	@dwxs ut_dwxs,		��λϵ��
	@fysl ut_sl10,		��ҩ����
	@cfts integer,		��������
	@ypdj ut_money,		ҩƷ����
	@ghsjh ut_sjh = null,		�Һ��վݺ�
	@ghxh ut_xh12 = null,		�Һ����
	@tcljje numeric(12,2) = 0,	ͳ���ۼƽ��
	@shbz	ut_bz = 0,		��˱�־ 0 �������,1 ���,2 ��˲�ͨ��
	@zph varchar(32) = null,	֧Ʊ��
	@zpje numeric(12,2) = null,	֧Ʊ���
	@zhbz ut_zhbz = null,		�˻���־	
	@zddm ut_zddm = null,		��ϴ���
	@zxlsh ut_lsh = null,		������ˮ��
	@jslsh ut_lsh = null,		������ˮ��
	@xmlb ut_dm2 = null,		����Ŀ
	@qfdnzhzfje numeric(12,2) = null, 	�𸶶ε����˻�֧��
	@qflnzhzfje numeric(12,2) = null,	�𸶶������ʻ�֧��
	@qfxjzfje numeric(12,2) = null,		�𸶶��ֽ�֧��
	@tclnzhzfje numeric(12,2) = null,	ͳ��������ʻ�֧��
	@tcxjzfje numeric(12,2) = null,		ͳ����ֽ�֧��
	@tczfje numeric(12,2) = null,		ͳ���ͳ��֧��
	@fjlnzhzfje numeric(12,2) = null,	���Ӷ������ʻ�֧��
	@fjxjzfje numeric(12,2) = null,		���Ӷ�����֧��
	@dffjzfje numeric(12,2) = null		���Ӷεط�����֧��
	@dnzhye numeric(12,2) = null,		�����˻����
	@lnzhye numeric(12,2) = null,		�����˻����
	@qkbz smallint = 0					Ƿ���־0��������2��Ƿ��
	@jsrq ut_rq16 = ''					��������
--mit add 2003-05-05 ,,������ 
	,@ylcardno ut_cardno=''		����������
	,@ylkje ut_money=0		���������
	,@ylkhcysje ut_money=0	������Ԥ�ڽ��(��)
	,@ylksqxh ut_lsh=����		�������������
	,@ylkzxlsh ut_lsh=����		������������ˮ��
	,@ylkhcyssqxh ut_lsh=����		������Ԥ���������(��)
	,@ylkhcyszslsh ut_lsh=����		������Ԥ��������ˮ��(��)

	,@cardxh ut_xh12=0,					���ҿ����
	,@cardje ut_money=0					���ҿ����
	,@bdyhkje ut_money = 0      �����п����
	,@bdyhklsh ut_lsh = ''      �����п���ˮ��
	,@zlje	ut_money=0		������ 
[����ֵ]
[�����������]
[���õ�sp]
[����ʵ��]

[�޸�˵��]
Modify by qxh  2003.2.27  
	���Ӱ�������ӡ��Ʊ�Ĵ��� 
add by chenwei 2003.12.06
	���ҿ����˴��� 
modify by szj	 2004.02.18 ��ֵ����Ҫ�ṩ��
	��ſ����շ������@czksfbz �����������Ƿ�ӳ�ֵ���Ͽ�Ǯ
add by gzy at 20050520 ����0081���ݲ�ͬҪ�����ɲ�ͬ��ҩ����ˮ��
mod by ozb 20060622
	���ε��߷�Ʊ������ �ڴ�ӡ��Ʊǰ���� usp_sf_zfp���д�����Ϊ�շѺ�ŷַ�Ʊ
	���ӱ���������
ozb 20060705 ���Ӽ����ԣ�������ǰ�ķ�Ʊ��ӡģʽ
wfy 20070315 �޸�yflsh�����ִ��룬ԭ���벻��ʵ�����Ŀ��
wfy 20070316 �����ء�0081����Ϊ2ʱ��ҽ��������ɵ���ˮ�ű��浽SF_MZCFK�е�fyckxh,�ۺ����õ���yflsh�ֶ�
**********/
set nocount on

declare	@now ut_rq16,		--��ǰʱ��
		@zfbz smallint,		--������־
		@rowcount int,
		@error int,
		@zje ut_money,		--ҩ���ܽ��
		@zje1 ut_money,		--��ҩ���ܽ��
		@zfyje ut_money,	--�Է�ҩ�ѽ��
		@zfyje1 ut_money,	--�Էѷ�ҩ�ѽ��
		@yhje ut_money,		--�Ż�ҩ�ѽ��
		@yhje1 ut_money,	--�Żݷ�ҩ�ѽ��
		@ybje ut_money,		--������ҽ�������ҩ�ѽ��
		@ybje1 ut_money,	--������ҽ������ķ�ҩ�ѽ��
		@pzlx ut_dm2,		--ƾ֤����
		@sfje ut_money,		--ʵ�ս��(ҩƷ)
		@sfje1 ut_money,	--ʵ�ս��(��ҩƷ)
		@sfje_all ut_money,	--ʵ�ս��(�����Էѽ��)
		@errmsg varchar(50),
		@srbz char(1),		--�����־
		@srje ut_money,		--������
		@sfje2 ut_money,	--������ʵ�ս��
		@xhtemp ut_xh12,
		@ksmc ut_mc32,		--��������
		@ysmc ut_mc64,		--ҽ������
--		@xmzfbl float,		--ҩƷ�Ը�����
--		@xmzfbl1 float,		--��ҩƷ�Ը�����
		@xmzfbl numeric(12,4),		
		@xmzfbl1 numeric(12,4),	--mit ,, 2oo3-o7-28 ,,float�Ļ���������������	
		@xmce ut_money,		--�Ը����ʹ����Ը������ܵĲ��
		@fplx smallint,		--��Ʊ����
		@fph bigint,			--��Ʊ��
		@fpjxh ut_xh12,		--��Ʊ�����
		@print smallint,	--�Ƿ��ӡ0��ӡ��1����
		@pzh ut_pzh,		--ƾ֤��
		@brlx ut_dm2,		--��������
		@qkbz1 smallint,	--Ƿ���־0��������1�����ˣ�2��Ƿ�� 3���۳�ֵ��
		@zhje ut_money,		--�˻����
		@qkje ut_money,		--Ƿ������˽�
		@acfdfp ut_bz,	        --�Ƿ񰴴�����Ʊ  
		@yjbz ut_bz,		--�Ƿ�ʹ�ó�ֵ��
		@yjye ut_money,		--Ԥ�������
		@flzfje ut_money	--�����Ը����
		,@djje ut_money		--������
		,@sjyjye ut_money	--ʵ��Ѻ�����
		,@sjdjje ut_money,	--ʵ�ʶ�����		--mit , 2oo3-11-19
		@cardbz ut_bz,	    --�Ƿ�ʹ�ô��ҿ�
		@kdm ut_dm2,		--���ҿ��������
		@kmc ut_mc16,		--���ҿ�����
		@cardno ut_pzh,		--���ҿ�����
		@tcljbz ut_bz,		--ͳ���ۼƱ�־
		@tcljje1 ut_money	--ͳ���ۼƽ��򱣡��½��ػ�ʹ�ã� 
		,@qkje2 ut_money
		,@yflsh	integer		--ҩ����ˮ��
		,@gyfpbz ut_bz		--���÷�Ʊ��־0:˽��1:����
		,@fyckdm1 ut_dm2	--��ҩ���ڴ���1	--add by gzy at 20050518
		,@fyckxh INTEGER		--��ҩ�������	--add by gzy at 20050518
		,@tsyhje ut_money  ---�����Żݽ�� 
		,@spzlx varchar(10)  --˫ƾ֤����
		,@gbje ut_money
		,@ysybzfje  ut_money --ԭʼҽ���Ը����,2005-11-14�ɱ�Ҫ���ӡʵ�ʸɱ��Ը����
		,@gbje2 ut_money    --2005-11-14�ɱ�Ҫ���ӡ�Էѽ��2
		,@gbbz ut_bz	--�ɱ���־
		,@configdyms	ut_bz	--��ӡģʽ 0 �ɵĴ�ӡģʽ 1 �µĴ�ӡģʽ
		,@tjmfbz ut_bz  --�����ѱ�־
		,@sctjrq ut_rq8 --�ϴ��������
    	,@bdyfplb varchar(255)
		,@tcljybdm varchar(500)  --ͳ���ۼ�ҽ������
		,@lczje ut_money		--���ҩ���ܽ��	
		,@lcyhje ut_money       --����Żݽ��
		,@yydjje ut_money
		,@now8 ut_rq16
declare @jzxh ut_xh12,
		@czym ut_mc64,
		@pycfxh	ut_xh12,
		@pyhjxh	ut_xh12,
        @fpbz ut_bz ---0��ӡ1����ӡ
        ,@dbkye ut_money 
        ,@dbkzf ut_money
        ,@config0220 ut_bz	--1�����źͿ��Ҵ����췢Ʊģʽ0��ͳģʽ
        ,@configA234 ut_bz  -- ADD KCS �Ƿ�����HRPϵͳ
        ,@fpdm varchar(16)
		,@ysxjje ut_money	--Ԥ���ֽ��� 
        ,@ysyjye ut_money --���������Ѻ�����       
		,@zsxjje ut_money  --�������ֽ��� 
		,@zsyjye ut_money --���������Ѻ����� 
		,@config2391	smallint	--�ж�Ѻ������Ƿ�Ϊ��ֵ,���һع�����
        ,@config1524 char(2)
        ,@config0203 char(2)
        ,@config2559 char(2)		
select @now8 = convert(char(8),getdate(),112),@yflsh = 0

select @now=convert(char(8),getdate(),112)+convert(char(8),getdate(),8),
	@zje=0, @lczje=0, @zfyje=0, @yhje=0, @ybje=0,
	@zje1=0, @zfyje1=0, @yhje1=0, @ybje1=0,
	@sfje=0, @sfje1=0, @sfje_all=0, @srje=0, @sfje2=0, 
	@xmzfbl=0, @xmzfbl1=0, @xmce=0, @print=0, @fplx=0,
	@qkbz1=0, @qkje=0, @yjbz=0, @yjye=0, @flzfje=0
	,@sjyjye=0,@sjdjje=0,@cardbz=0,@kdm='',@kmc='',@cardno='',@tcljbz=0,@tcljje1=0,@yflsh=0, @gyfpbz=0
	,@tsyhje = 0, @gbje=0, @ysybzfje = 0,@gbje2 = 0,@gbbz = '0'
	 ,@lcyhje = 0,@lczje = 0,@yydjje = 0,@fpbz =0,@fpdm=''
	,@ysxjje=0	--Ԥ���ֽ��� 
    ,@ysyjye=0 --���������Ѻ�����       
	,@zsxjje=0  --�������ֽ��� 
	,@zsyjye=0 --���������Ѻ����� 
	,@config2391=0
	,@config2559='��'
	
select @spzlx = spzlx from SF_BRXXK (nolock)where patid = @patid
 
select @czym=name from czryk where id=@czyh

if (select config from YY_CONFIG (nolock) where id='2391')='��'
	select   @config2391=0
else
 	select   @config2391=1
 
if (select config from YY_CONFIG (nolock) where id='2044')='��'
	select   @acfdfp=0
else
 	select   @acfdfp=1

--�Ƿ�ʹ�ô��ҿ�����
if (select config from YY_CONFIG (nolock) where id='2083')='��'
	select   @cardbz=0
else
 	select   @cardbz=1

if (select config from YY_CONFIG (nolock) where id='2135')='��'
	select @gyfpbz = 1
else
	select @gyfpbz = 0
	
if exists (select 1 from YY_CONFIG(nolock) where id='1524' and config='��')
    select @config1524='��'
else
    select @config1524='��'   	
   
if exists (select 1 from YY_CONFIG(nolock) where id='0203' and config='��')
    select @config0203='��'
else
    select @config0203='��'   	    
      
--add by ozb �շ��Ƿ�ʹ���µĴ�ӡģʽ
if exists(select 1 from YY_CONFIG(nolock) where id='2154' and config='��')
	select @configdyms=1 
else 
	select @configdyms=0
 
if (select config from YY_CONFIG (nolock) where id='0220')='��'      
	select @config0220=1      
else      
	select @config0220=0   
	
if (select config from YY_CONFIG (nolock) where id='A234')='��'      
	select @configA234=1      
else      
	select @configA234=0 
if exists (select 1 from YY_CONFIG(nolock) where id='2559' and config='��')
    select @config2559='��'
else
    select @config2559='��'
if @config0203='��' and @zph='S' 
begin
	exec usp_pay_judgesettlement 1,@patid,@sjh,0,0,0,0,0,@errmsg output   
  	if substring(@errmsg,1,1)='F'   
  	begin  
   		select "F",@errmsg  
   		return  
  	end    
end	
--add wuwei 2011-06-10
declare @srfs varchar(1)  --0����ȷ���֣�1����ȷ����
select @srfs = config from YY_CONFIG (nolock) where id='2235'
if @@error<>0 or @@rowcount=0
	select @srfs='0'

if @qkbz=2 --and (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
begin
	if  exists (select 1 from YY_CONFIG where id='2538' and config='��' )
	begin
		if exists (select 1 from YY_CONFIG where id='1657' and config='��' )
		begin
			if exists (select 1 from SF_BRXXK_FZ where patid=@patid and lstdbz=1 )
			begin
				if not exists (select 1 from SF_BRXXK_FZ where patid=@patid and @now between lstdkssj and lstdjssj)
				begin
					select "F","���˽���ʱ�䲻��Ƿ�ѽ�����Ч���ڣ�"
					return
				end
			end	
			else
			begin	
				if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
				begin
					select "F","�û��߲�����Ƿ�ѣ�"
					return
				end
			end	
		end
		else
		begin
			if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
			begin
				select "F","�û��߲�����Ƿ�ѣ�"
				return
			end
		end
	end
	else
	if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
	begin
		select "F","�û��߲�����Ƿ�ѣ�"
		return
	end
end


if @sfbz=2
begin
	select @ybdm=ybdm, @qkbz1=qkbz, @qkje=qkje, @zje=zje, @lczje=zje-lcyhje, @sfje2=zfje, @zfyje=zfyje, @qrbz=qrbz, @flzfje=flzfje,
			@yhje=yhje, @tcljbz=tcljbz, @tcljje1=zje-zfyje,@tsyhje = tsyhje, @gbje=isnull(gbje,0)
			,@gbbz = gbbz, @tjmfbz =isnull(tjmfbz,0),@lcyhje = lcyhje,
			@ysxjje=xjje	--Ԥ���ֽ��� 
			,@ysyjye=qkje --���������Ѻ����� 
		from SF_BRJSK where sjh=@sjh
	if @@rowcount=0
	begin
		select "F","���շѽ����¼�����ڣ�"
		return
	end
	--Ƿ��˵ļ��˽��Ҫ��ӡ������ͬʱ�˻����-@qkje2�����@qkbz1 = 3�������԰���ԭ����ֵ
	if @qkbz1 = 1
		select @qkje2 = isnull((select je from SF_JEMXK where jssjh = @sjh and lx = '01'),0) 
	else 
		select @qkje2 = @qkje

	select @pzlx=pzlx from YY_YBFLK (nolock) where ybdm=@ybdm
	if @@rowcount=0 or @@error<>0
	begin
		select "F","���߷��������ȷ��"
		return
	end

	select @jzxh=case when isnull(zcbz,0)=0 then xh else isnull(zkxh,xh) end ,
        @fpbz =isnull(fpbz,0)
		from YY_JZBRK where patid=@patid and jlzt=0
	if @@rowcount=0
    begin
		select @yjye=0
        select @fpbz=0
    end
	else
	begin
		select @yydjje=sum(isnull(djje,0)) from GH_GHYYK(nolock) where patid=@patid and djbz=1 and jlzt = 0 

		select @yjye=isnull(yjye,0)-isnull(@yydjje,0) from YY_JZBRK where xh=@jzxh and jlzt=0
		if @@rowcount=0
			select @yjye=0
		else
			select @yjbz=1
	end	
	
	--���Ƴ�ֵ�� ��ֵ������ͬʱʹ��
	if @yjbz=1
	begin
	    if @config0203='��'
	    begin
	        if exists(select 1 From YY_PAYTYPEK where id=3 and jlzt=0)
	        begin
				select "F","��ֵ���ʹ�ֵ�����ܲ���ͬʱ���ã�"
				return
	        end	    
	    end	
	end	
-- ��Ԥ��ʱ��qkje���������Ѿ������꣬�����ٴμ���
--	if @czksfbz = 1 --�ӳ�ֵ���շ�
--	begin
--		if @yjye>0
--		begin
--			if (@sfje2-@gbje)<=@yjye
--				select @qkje=(@sfje2-@gbje)
--			else
--			begin	
--				if @srbz='5'
--					select @qkje=round(@yjye, 1)
--				else if @srbz='6'
--					exec usp_yy_wslr @yjye,1,@qkje output 
--				else if @srbz>='1' and @srbz<='9'
--					exec usp_yy_wslr @yjye,1,@qkje output,@srbz
--				else
--					select @qkje=@yjye	
--			end
--		end
--	end
--
--	---add wuwei 2011-06-13
--	if @srfs = '1'---1����ȷ������������20110426sqf
--	begin
--		select @qkje=round(@qkje, 1,1) ---ȥ��С��λ
--	end
    if (select config from YY_CONFIG (nolock) where id='0133')='��' and @fpbz ='1'
		select @print=2

	if @qkbz=2 --and (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
	begin
	    if exists (select 1 from YY_CONFIG where id='1657' and config='��' )
		begin
			if exists (select 1 from SF_BRXXK_FZ where patid=@patid and lstdbz=1 )
			begin
				if not exists (select 1 from SF_BRXXK_FZ where patid=@patid and @now between lstdkssj and lstdjssj)
				begin
					select "F","���˽���ʱ�䲻��Ƿ�ѽ�����Ч���ڣ�"
					return
				end
			end	
		end
		else
		if (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'
		begin
			select "F","�û��߲�����Ƿ�ѣ�"
			return
		end
	end

	if @qkbz=2
		select @qkbz1=2, @qkje=@sfje2

	select hjxh	into #sfcfk from SF_MZCFK(nolock) where jssjh=@sjh
	if @@rowcount=0
	begin
		select "F","�շ���Ϣ�����ڣ�"
		return		
	end 

	
	if (@config2559='��') and exists(select 1 from SF_HJCFK(nolock) where jlzt=1 and isnull(ybtscf,0)<>1 and xh in (select hjxh from #sfcfk) and patid=@patid)
	begin
		select "F","��ǰ�����Ѿ������㣬�޷��ٴν��н��㣡"
		return	
	end
	
	if @qkbz1=2 and @qkje>0
		select @print=1
	if exists(select 1 from YY_CONFIG (NOLOCK)WHERE id='2275' and config='��') and 	@qkbz1=2 --yxc 20121207
	BEGIN
		select @print=0	
	
	END	
	-- by cjt 20130726 ȱ�ѱ�־Ϊ1ʱ��ȷ�ϱ�־����Ϊ1,�ڽ��µ�����л�������,�˴���Ҫ���ڷ�Ʊ��ӡ�ж�
	if @isQfbz=1 
	begin
		select @qrbz=1
	end
	if @qrbz=1
		select @print=1
	
	 --add by gxf 2007-6-7 
	 select @bdyfplb = config from YY_CONFIG where id = '2161'
	 if @sfje2 = 0 
			if charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+@bdyfplb+',')>0 
				select @print=1
	--add by gxf 2007-6-7

	if (select config from YY_CONFIG (nolock) where id='2238')='��'
	begin 
		if @sfje2 = 0 
		    select @print=1 
	end
	
	--add wuwei ���ҿ�֧������ӡ��Ʊ
	if (select config from YY_CONFIG where id ='2243' ) ='��'
	begin
		if (@cardxh <>0) and (@cardje<>0)
			select @print=1
	end
	--�Ϸ�Ʊģʽ�����ӶԲ���2273�ܿ�
	if exists(select 1 from YY_CONFIG where id = '2273' and charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+config+',')>0) 
		select @print=1

	begin tran
	
	-- add kcs by 11439 �������ʿ��
	if (@configA234 = 1) and (exists (select 1 from SF_BRJSK a
					inner join SF_MZCFK b on a.sjh = b.jssjh
					inner join SF_CFMXK c on b.xh = c.cfxh
					inner join SF_HJCFMXK d on c.hjmxxh = d.xh
					where a.sjh = @sjh and ISNULL(d.wzkfdm,'') <> '' and isnull(d.wzdm,'')<>''))   -- ����Ҫ��ͨ��SF_HJCFMXK.wzkfdm �Ƿ���ֵ�ж��Ƿ�۷ѿۿ��
	begin
	    declare 
		   @kc_ifid               ut_xh12,                --�ӿ�ȫ�ֱ�ʶ
		   @kc_ifid_t             ut_xh12,                --�����
		   @kc_kfdm               varchar(12),            --�����ҿⷿ
		   @kc_wzdm               ut_mc32,                --���ʴ���
		   @kc_xmmc               ut_mc64,                --��Ŀ����
		   @kc_pcxh               varchar(120),    --��ʱ,���벻��Ϊ��
		   @kc_wzsl               ut_sl10 ,            --����Ϊ��
		   @kc_xhfs               ut_zt,                  --���ķ�ʽ:0: ���ϼ���; 1: ���ϲ�����; 3. ҽ��ִ��; 4.ҽ���շ�; 5.��������,6.������������¼��; 9.����
		   @kc_ksdm               ut_ksdm,         --������  �۷ѿ��Ҵ���
		   @kc_ksmc               ut_mc32,         --�۷ѿ������
		   @kc_bqdm               ut_ksdm,
		   @kc_bqmc               ut_mc32,
		   @kc_pKsdmBr            ut_ksdm,         --���˿��Ҵ���,��Ƶ�סԺ����
		   @kc_pKsmcBr            ut_mc32,         --���˿�������
		   @kc_pDrName            ut_mc64,         --ҽ������������ǩ��
		   @kc_pNurseName         ut_mc64,         --��ʿ����������ǩ��
		   @kc_syxh               ut_syxh,
		   @kc_blh                ut_blh,
		   @kc_ch                 ut_mc16 ,         --����
		   @kc_patid              ut_syxh ,
		   @kc_patname            ut_mc64 ,
		   @kc_zxczy              ut_czyh ,
		   @kc_zxczyxm            ut_mc64 ,
		   @kc_lrczy              ut_czyh ,         --¼�����Ա
		   @kc_lrczyxm            ut_mc64 ,         --¼�����Ա����
		   @kc_fymxh              ut_xh12 ,         --������ϸ��
		   @kc_pHjmxh             ut_xh12 ,           --������ϸ��
		   @kc_pTxmMaster         ut_mc64 ,         --��������
		   @kc_pTxmSlave          ut_mc64 ,         --��������
		   @kc_pNeedNewBill       ut_zt ,            -- ��¼�����
		   @kc_memo               ut_memo,         -- ��ע
		   @outpcxh            ut_mc64,
		   @outpcsl            ut_mc64,
		   @hrperrmsg             ut_mc64,
		   @config6845            VARCHAR(20),
		   @sql                   nVARCHAR(3000),
		   @ParmDefinition        nvarchar(500)
		select @kc_wzsl= 1,@kc_pHjmxh= -1,@kc_pNeedNewBill= '0'
		select @config6845 = config from YY_CONFIG where id = '6845'
		
		declare lscm_kc cursor for select c.xh from SF_BRJSK a
					inner join SF_MZCFK b on a.sjh = b.jssjh
					inner join SF_CFMXK c on b.xh = c.cfxh
					inner join SF_HJCFMXK d on c.hjmxxh = d.xh
					where a.sjh = @sjh and ISNULL(d.wzkfdm,'') <> '' and ISNULL(d.wzdm,'') <> '' 
	    open lscm_kc
	    fetch lscm_kc into @kc_ifid
		while @@fetch_status=0 
	    begin        
	        select @kc_kfdm = b.wzkfdm,@kc_wzdm = case isnull(b.wzdm,'') when '' then '"0"' else b.wzdm end,@kc_xmmc = a.ypmc,@kc_pcxh = b.wzpcxh,@kc_wzsl = a.ypsl,
	               @kc_ksdm = c.sfksdm,@kc_ksmc = d.name,@kc_fymxh = a.xh,@kc_pHjmxh = b.xh,@kc_pTxmMaster = b.tm,
	               @kc_pTxmSlave = b.ctxm,@kc_pKsdmBr = e.ksdm,@kc_pKsmcBr = f.name,@kc_pDrName = g.name,@kc_blh = h.blh,
	               @kc_patid = @patid,@kc_patname = h.hzxm,@kc_zxczy = e.qrczyh,@kc_zxczyxm = i.name  
	                 from SF_CFMXK a
	                 inner join SF_HJCFMXK b on a.hjmxxh = b.xh
	                 inner join SF_BRJSK c on c.sjh = @sjh
	                 inner join YY_KSBMK d on c.sfksdm = d.id
	                 left join SF_MZCFK e on a.cfxh = e.xh
	                 left join YY_KSBMK f on e.ksdm = f.id
	                 left join YY_ZGBMK g on e.ysdm = g.id
	                 left join SF_BRXXK h on e.patid = h.patid
	                 left join czryk i on e.qrczyh = i.id
					 where a.xh = @kc_ifid					 		 	 
					 
			select @sql = N'exec ' + @config6845 + 'dbo.USP_LSCM_IF4THIS_KC @ifid = ' + convert(varchar(12),isnull(@kc_ifid,0))
			            +', @kfdm = "' + convert(varchar(12),isnull(@kc_kfdm,''))
			            +'", @wzdm = ' + convert(varchar(12),isnull(@kc_wzdm,0))
			            +', @xmmc = "'+ convert(varchar(64),isnull(@kc_xmmc,''))
			            +'",@pcxh=' + convert(varchar(12),isnull(@kc_pcxh,0))
			            +',@wzsl=' + convert(varchar(12),isnull(@kc_wzsl,''))
			            +',@xhfs=7,'
			            +'@ksdm="' + convert(varchar(12),isnull(@kc_ksdm,''))
			            +'",@ksmc="'+ convert(varchar(64),isnull(@kc_ksmc,''))
			            +'",@pHjmxh='+ convert(varchar(12),isnull(@kc_pHjmxh,''))
			            +',@pTxmMaster="'+convert(varchar(64),isnull(@kc_pTxmMaster,''))
			            +'",@pTxmSlave="'+convert(varchar(64),isnull(@kc_pTxmSlave,''))
			            +'",@pKsdmBr="'+convert(varchar(12),isnull(@kc_pKsdmBr,''))
			            +'",@pKsmcBr="'+convert(varchar(12),isnull(@kc_pKsmcBr,''))
			            +'",@pDrName="'+convert(varchar(64),isnull(@kc_pDrName,''))
			            +'",@blh="'+convert(varchar(12),isnull(@kc_blh,''))
			            +'",@patid="'+convert(varchar(12),isnull(@kc_patid,''))
			            +'",@patname="'+convert(varchar(64),isnull(@kc_patname,''))
			            +'",@zxczy="'+convert(varchar(12),isnull(@kc_zxczy,''))
			            +'",@zxczyxm="'+convert(varchar(64),isnull(@kc_zxczyxm,''))
			            +'"'
			 
			select  @sql = @sql + N',@outpcxh = @outpcxh OUTPUT ,@outpcsl = @outpcsl output,@errmsg = @hrperrmsg output '
			set  @ParmDefinition = N' @outpcxh varchar(100) output,@outpcsl  varchar(100) output,  @hrperrmsg varchar(64) output '
			            
			exec sp_executesql @sql,@ParmDefinition,@outpcxh = @outpcxh OUTPUT,@outpcsl = @outpcsl OUTPUT ,@hrperrmsg=@hrperrmsg OUTPUT 		
            IF (@@ERROR<>0)or (@hrperrmsg like 'F%')
            begin
                select 'F','�۳����ʿ��ʧ�ܣ�'+@hrperrmsg
				rollback tran
				DEALLOCATE lscm_kc
                return      
            end
            
			select @sql = N'exec ' + @config6845 + 'dbo.USP_LSCM_IF4THIS_KC @ifid = ' + convert(varchar(12),isnull(@kc_ifid,0))
			            +', @kfdm = "' + convert(varchar(12),isnull(@kc_kfdm,''))
			            +'", @wzdm = ' + convert(varchar(12),isnull(@kc_wzdm,0))
			            +', @xmmc = "'+ convert(varchar(64),isnull(@kc_xmmc,''))
			            +'",@pcxh=' + convert(varchar(12),isnull(@kc_pcxh,0))
			            +',@wzsl=' + convert(varchar(12),isnull(@kc_wzsl,''))
			            +',@xhfs=7,'
			            +'@ksdm="' + convert(varchar(12),isnull(@kc_ksdm,''))
			            +'",@ksmc="'+ convert(varchar(64),isnull(@kc_ksmc,''))
			            +'",@pHjmxh='+ convert(varchar(12),isnull(@kc_pHjmxh,''))
			            +',@pTxmMaster="'+convert(varchar(64),isnull(@kc_pTxmMaster,''))
			            +'",@pTxmSlave="'+convert(varchar(64),isnull(@kc_pTxmSlave,''))
			            +'",@pKsdmBr="'+convert(varchar(12),isnull(@kc_pKsdmBr,''))
			            +'",@pKsmcBr="'+convert(varchar(12),isnull(@kc_pKsmcBr,''))
			            +'",@pDrName="'+convert(varchar(64),isnull(@kc_pDrName,''))
			            +'",@blh="'+convert(varchar(12),isnull(@kc_blh,''))
			            +'",@patid="'+convert(varchar(12),isnull(@kc_patid,''))
			            +'",@patname="'+convert(varchar(64),isnull(@kc_patname,''))
			            +'",@zxczy="'+convert(varchar(12),isnull(@kc_zxczy,''))
			            +'",@zxczyxm="'+convert(varchar(64),isnull(@kc_zxczyxm,''))
			            +'",@fymxh="'+convert(varchar(12),isnull(@kc_fymxh,'')) 	
			            +'"'
			 
			select  @sql = @sql + N',@outpcxh = @outpcxh OUTPUT ,@outpcsl = @outpcsl output,@errmsg = @hrperrmsg output '
			set  @ParmDefinition = N' @outpcxh varchar(100) output,@outpcsl  varchar(100) output,  @hrperrmsg varchar(64) output '
			            
			exec sp_executesql @sql,@ParmDefinition,@outpcxh = @outpcxh OUTPUT,@outpcsl = @outpcsl OUTPUT ,@hrperrmsg=@hrperrmsg OUTPUT 		
            IF (@@ERROR<>0)or (@hrperrmsg like 'F%')
            begin
                select 'F','�۳����ʿ��ʧ�ܣ�'+@hrperrmsg
				rollback tran
				DEALLOCATE lscm_kc
                return      
            end  
            
            if @kc_wzdm = 'NULL'
				select  @kc_wzdm = 0
                      
            if exists(select 1 From sysobjects where name ='YY_SF_HRP_LOG')
            begin            
				insert into YY_SF_HRP_LOG(
				kc_ifid            
				,kc_ifid_t          
				,kc_kfdm            
				,kc_wzdm            
				,kc_xmmc            
				,kc_pcxh            
				,kc_wzsl            
				,kc_xhfs            
				,kc_ksdm            
				,kc_ksmc            
				,kc_bqdm            
				,kc_bqmc            
				,kc_pKsdmBr 
				,kc_pKsmcBr         
				,kc_pDrName         
				,kc_pNurseName      
				,kc_syxh            
				,kc_blh             
				,kc_ch              
				,kc_patid           
				,kc_patname         
				,kc_zxczy           
				,kc_zxczyxm         
				,kc_lrczy           
				,kc_lrczyxm         
				,kc_fymxh           
				,kc_pHjmxh          
				,kc_pTxmMaster      
				,kc_pTxmSlave       
				,kc_pNeedNewBill    
				,kc_memo            
				,outpcxh            
				,outpcsl            
				,hrperrmsg          
				 )
				 values
				 (
				 @kc_ifid          
				,@kc_ifid_t       
				,@kc_kfdm         
				,@kc_wzdm         
				,@kc_xmmc         
				,@kc_pcxh         
				,@kc_wzsl         
				,@kc_xhfs         
				,@kc_ksdm         
				,@kc_ksmc         
				,@kc_bqdm         
				,@kc_bqmc         
				,@kc_pKsdmBr      
				,@kc_pKsmcBr      
				,@kc_pDrName      
				,@kc_pNurseName   
				,@kc_syxh         
				,@kc_blh          
				,@kc_ch           
				,@kc_patid        
				,@kc_patname      
				,@kc_zxczy        
				,@kc_zxczyxm      
				,@kc_lrczy        
				,@kc_lrczyxm      
				,@kc_fymxh        
				,@kc_pHjmxh       
				,@kc_pTxmMaster   
				,@kc_pTxmSlave    
				,@kc_pNeedNewBill 
				,@kc_memo         
				,@outpcxh         
				,@outpcsl         
				,@hrperrmsg  
				 )            
				IF (@@ERROR<>0)
				begin
					select 'F','�۳����ʿ��ʧ�ܣ�'
					rollback tran
					DEALLOCATE lscm_kc
					return      
				end               
            end
            fetch lscm_kc into @kc_ifid
        end
        close lscm_kc
        deallocate lscm_kc
	end
	
    if @print=0 and @configdyms=0 and @isQfbz=0
	begin
		if (select config from YY_CONFIG (nolock) where id='1022')='��'   --�Һŷ�Ʊ���շѷ�Ʊһ��
			select @fplx=0
		else
			select @fplx=1

		if @acfdfp=0 
      	begin
			if @gyfpbz=0
			begin
				if @config0220=1
				select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=@fplx and ksdm=@sfksdm
				else
				select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=@fplx
				if @@rowcount=0
				begin
					select "F","û�п��÷�Ʊ��"
					rollback tran
					return
				end
			end
			else
			begin
				select @fph=fpxz, @fpjxh=0,@fpdm=isnull(fpdm,'') from SF_GYFPK(nolock) where czyh=@czyh and xtlb=@fplx
				if @@rowcount=0
				begin
					select "F","û�п��÷�Ʊ��"
					rollback tran
					return
				end
			end

			exec usp_yy_gxzsj @fplx, @czyh, @errmsg output, @gyfpbz,@fpjxh,@sfksdm,@ipdz_gxzsj
			if @@ERROR <> 0
			begin
				select "F",'���·�Ʊ��ʧ�ܣ�'
				rollback tran
				return
			end
			if @errmsg like 'F%'
			begin
				select "F",substring(@errmsg,2,49)
				rollback tran
				return
			end
		end
		else 
		begin
			--ȡ������Ʊ�� begin
			exec usp_sf_acfdfp_zfp @sjh,0,@czyh,@errmsg output,@sfksdm
			if @errmsg like 'F%'
			begin
				select "F",substring(@errmsg,2,49)
				rollback tran
				return
			end
			select top 1 @fph=fph,@fpjxh=fpjxh from SF_MZCFK(nolock) where jssjh=@sjh order by fph
		end	
	end
	
	
	
	--mit ,, 2oo3-o8-29 ,, ҽ��ȷ�ϵ���Ŀ�շѺ�fybz��Ϊ1
		--move here , �ȸ��·���jlzt�Ͳ�����, 2oo3-12-11
/*	update SF_MZCFK 
	set fybz=1,qrczyh=b.qrczyh,qrrq=b.qrrq,qrksdm=b.yfdm
	from SF_MZCFK a,SF_HJCFK b
	where a.jssjh=@sjh
	and a.hjxh=b.xh
	and b.jlzt in(3,8)
	if @@error<>0
	begin
		rollback tran
		select "F","�������ﴦ����Ϣ����"
		return
	end
*/
	if @hcsjh=''  --ҽ��ת�Է�ʱ,���������yjqrbz,��ǰ̨�����Ϊ׼
	begin
		update SF_CFMXK set yjqrbz=b.fybz,qrczyh=b.qrczyh,qrrq=b.qrrq
		from SF_CFMXK a,SF_MZCFK b
		where a.cfxh=b.xh and b.jssjh=@sjh and b.fybz=1
		if @@error<>0
		begin
			rollback tran
			select "F","�������ﴦ����ϸ��Ϣ����"
			return
		end
	end
	--add by zyh 20100304 H354 ������ȷ�ϵ�С��Ŀ��ִ�п���Ϊҽ�������ҵ���Ŀ�ڽ����ֱ���޸�ҽ��ȷ�ϱ�־Ϊ��ȷ��
	if exists(select 1 from YY_CONFIG WHERE id='H354' and config='��')
	begin
		update SF_CFMXK set yjqrbz=1,qrczyh=@czyh,qrrq=@now
		from SF_CFMXK a,SF_MZCFK b,YY_SFXXMK c
		where a.cfxh=b.xh and b.jssjh=@sjh and isnull(a.hjmxxh,0)>0 and a.cd_idm=0 and a.ypdm=c.id and a.yjqrbz=0 and c.mzyjqrbz=0
		if @@error<>0
		begin
			rollback tran
			select "F","�������ﴦ����ϸ��Ϣ����"
			return
		end

		update SF_CFMXK set yjqrbz=1,qrczyh=@czyh,qrrq=@now
		from SF_CFMXK a,SF_MZCFK b,SF_HJCFMXK c,SF_HJCFK d
		where a.cfxh=b.xh and b.jssjh=@sjh and isnull(a.hjmxxh,0)>0 and a.cd_idm=0 and a.yjqrbz=0 and a.hjmxxh=c.xh and c.cfxh=d.xh and d.ksdm=d.yfdm
		if @@error<>0
		begin
			rollback tran
			select "F","�������ﴦ����ϸ��Ϣ����"
			return
		end
	end
    -----------------------add by sqf 2012-11-22���´������wsbz 
    update SF_MZCFK  set wsbz = b.wsbz 
    from SF_MZCFK a,SF_HJCFK b
    where a.jssjh = @sjh and a.hjxh = b.xh and isnull(a.hjxh,0) > 0 and b.cflx = 3 ----��ҩ
  /*
  �жϻ��ۿ�������û�б������ע�͵�,�෢Ʊ����ʱ���ܷ�����һ�ν���һ��HJCFMX�е�һ����,�ڶ����ٽ������HJCFMX������һ���ֵ����
  ����SF_HJCFK.jlzt�ڵ�һ�ν����ʱ��͸�����,���µڶ��ν�����ʾ"�ô����Ѿ������㣬�����ظ����㣡"  
  */
--	--�жϻ��ۿ�������û�б������  
--	if exists(select 1 from SF_HJCFK a,#sfcfk b where a.patid=@patid and a.xh=b.hjxh and a.jlzt=1 and b.hjxh<>0)
--	begin
--		select "F","�ô����Ѿ������㣬�����ظ����㣡"
--		rollback tran
--		return
--	end

	update SF_MZCFK set jlzt=0,
		lrrq=(case when @jsrq='' then @now else @jsrq end),
		czyh=@czyh
		where jssjh=@sjh
	if @@error<>0
	begin
		rollback tran
		select "F","�������ﴦ����Ϣ����"
		return
	end
	--��촦���շѺ�ֱ��ȷ��  ����332134
	if exists(select 1 from YY_CONFIG nolock where id='2584' and config='��')
	begin
		update a set yjqrbz=1,qrczyh=@czyh,qrrq=@now
		from SF_CFMXK a,SF_MZCFK b(nolock)
		where a.cfxh=b.xh and b.jssjh=@sjh and b.cflx=6 and a.yjqrbz=0
		if @@error<>0
		begin
			rollback tran
			select "F","�������ﴦ����ϸҽ��״̬��Ϣ����"
			return
		end
	end
----���Ʒ����շѳɹ�������շѱ�־  SF_HJCFMXK.sjzlfabdxh<>0  ZLFA_SJBDK.SFBZ=1
	select hjxh	into #fasfcfk from VW_MZCFK (nolock)where jssjh=@sjh
	if @@rowcount=0
	begin
		select "F","�շ���Ϣ�����ڣ�"
		rollback tran
		return		
	end

	update a set a.sfbz=1 from ZLFA_SJBDK a, VW_MZHJCFMXK b
		where b.cfxh  in (select hjxh from #fasfcfk ) and a.xh=b.sjzlfabdxh
	if @@error<>0
	begin
		select "F","�����������շ���Ϣ����"
		rollback tran
		return
	end 
----end
	
	
	--mit , 2oo3-11-1o
	if @czksfbz = 1
	begin
		if @yjbz=1
		begin
			--mit , 2oo3-11-o8 , move here
			select @djje=sum(isnull(djje,0)) from SF_HJCFK 
				where xh in (select hjxh from #sfcfk)
			select @djje=isnull(@djje,0)
			if @djje<>0
			begin
				if exists(select 1 from YY_JZBRK where xh=@jzxh and jlzt=0 and djje>@djje)--gxs 20131224 add
				begin
					update YY_JZBRK set djje=djje-@djje 
					,@sjdjje=djje-@djje		--mit ,2oo3-11-19 , add
					where xh=@jzxh and jlzt=0
				end
				else
				begin
					update YY_JZBRK set djje=0
					,@sjdjje=djje-@djje		--mit ,2oo3-11-19 , add
					where xh=@jzxh and jlzt=0
				end
				if @@error<>0
				begin
					select "F","���¼��˲��˿�Ԥ����������"
					rollback tran
					return
				end
			end
		end
		
		if @yjbz=1 and @qkbz1=3
		begin
            --������ҿ�֧�����֣����ȿ۴��ҿ�ת�沿�ֽ��
            select @dbkye=isnull(dbkye,0) from YY_JZBRK where xh=@jzxh and jlzt=0  
            if @dbkye>=@qkje 
            select @dbkzf=@qkje 
            else
            select @dbkzf=@dbkye
      
			update YY_JZBRK set yjye=yjye-@qkje,dbkye=isnull(dbkye,0)-@dbkzf 
			,@sjyjye=yjye-@qkje		--mit ,2oo3-11-19 , add
			,@sctjrq=isnull(sctjrq,"")	
			where xh=@jzxh and jlzt=0
			if @@error<>0
			begin
				select "F","���¼��˲��˿�Ԥ����������"
				rollback tran
				return
			end
			
			insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh,ybdm,dbkje)
			values(0,0,@jzxh,@czyh,@czym,@now,@qkje,0,@sjyjye,1,3,null,0,'',@sjh, @ybdm,@dbkzf)
			if @@error<>0
			begin
				select 'F','����YY_JZBRYJK��¼ʱ����'
				rollback tran
				return
			end

            insert into SF_CARDZFJEK(jssjh,lx,mc,je,memo)
  values(@sjh,'3','��ֵ��֧��',@qkje,'')
            if @@error<>0
			begin
				select 'F','����YY_JZBRYJK��¼ʱ����'
				rollback tran
				return
			end

			if @tjmfbz=1 and @sctjrq<>substring(@now,1,8)
			begin
				update YY_JZBRK set tjcs=isnull(tjcs,0)+1
					,sctjrq=substring(@now,1,8)
				where xh=@jzxh and jlzt=0
				if @@error<>0
				begin
					select "F","���¼��˲��˿�Ԥ����������"
					rollback tran
					return
				end
			end
		end
	end	

	if @qkbz1 in (1,3)
	begin
		update SF_BRXXK set zhje=zhje-@qkje2,gxrq=@now where patid=@patid --add by yfq @20120531
		if @@error<>0
		begin
			select "F","���²�����Ϣ����"
			rollback tran
			return
		end
	
		select @zhje=zhje from SF_BRXXK where patid=@patid
		if @@error<>0
		begin
			select "F","�����˻�������"
			rollback tran
			return
		end		
	end

	if @tcljbz=1
	begin
		update SF_BRXXK set ljje=isnull(ljje,0)+@tcljje1,gxrq=@now --add by yfq @20120531
			where patid=@patid
		if @@error<>0
		begin
			select "F","���²�����Ϣ����"
			rollback tran
			return
		end
	end

	--���ҿ����˴��� add by chenwei 2003.12.06
	if @cardbz = 1 
	begin
		if (@cardxh <> 0) or (@cardje <> 0)
		begin
      --�Ż�����ͬʱ���д��ҿ�֧��������£�SF_BRJSKֻ��¼��ֵ��֧����Ϣ�����ҿ�֧����Ϣ��SF_CARDZFJEK���м�¼ 
      if not((@qkje>0) and (@yjbz=1) and (@qkbz1=3))
      --������ν�����С�ڴ��ҿ�֧�����ʱ�������ҿ�������Ϊ���ν�����ڶ�ν���ʱ�ᷢ��
      --���ҿ���Ϊһ��֧����ʽ��Ӧ��������֮��Ľ����Ϊ�ۿ����
      select @cardje = case when (round(yjye,1,1)-@cardje<0) and (round(yjye,1,1)>@sfje2) then @sfje2
                            when (round(yjye,1,1)-@cardje<0) and (round(yjye,1,1)<=@sfje2) then round(yjye,1,1) 
                            when (round(yjye,1,1)-@cardje>=0) and (@cardje>@sfje2) then @sfje2
                            when (round(yjye,1,1)-@cardje>=0) and (@cardje<=@sfje2) then @cardje
                       else 0  end
        from YY_CARDXXK nolock
			  where kxh=@cardxh and jlzt=0
			select @qkbz1 = '4', @qkje=@cardje
      /*
            ------------------------------------------------------
            declare @xjje_srq ut_money, --����ǰ
                    @xjje_srh ut_money  --�����
			select @xjje_srq = zfje-@qkje-isnull(@zpje,0)-isnull(@ylkje,0)-isnull(@gbje,0)-isnull(@bdyhkje,0)
	          from SF_BRJSK nolock
              where sjh=@sjh     
			select @srbz=config from YY_CONFIG (nolock) where id='2016'

			if @srbz='5'
				select @xjje_srh=round(@xjje_srq, 1)
			else if @srbz='6'
				exec usp_yy_wslr @xjje_srq,1,@xjje_srh output 
			else if @srbz>='1' and @srbz<='9'
				exec usp_yy_wslr @xjje_srq,1,@xjje_srh output,@srbz
			else
				select @xjje_srh=@xjje_srq

			select @srje=@xjje_srh-@xjje_srq
      */
            ------------------------------------------------------
			update YY_CARDXXK set zjrq=(case when @jsrq='' then @now else @jsrq end),
                                  yjye=case when yjye-@cardje<=0 then 0 else yjye-@cardje end,
								  @sjyjye=case when yjye-@cardje<=0 then 0 else yjye-@cardje end,
                                  @sjdjje = @cardje
			where kxh=@cardxh and jlzt=0
			if @@error<>0
			begin
				select "F","���´��ҿ������ʻ�������"
				rollback tran
				return
			end		

			select @kdm=a.kdm,@kmc=b.kmc,@zhje=yjye,@cardno=cardno 
			  from YY_CARDXXK a(nolock),YY_CARDFLK b(nolock) 
			  where a.kxh=@cardxh and a.kdm=b.kdm
			
			insert into YY_CARDJEK(kxh,jssjh,yjjxh,kdm,czyh,czym,lrrq,zje,zhye,yhje,yhje_zje,yhje_mx,jlzt,xtbz,memo)
			values(@cardxh,@sjh,0,@kdm,@czyh,'',(case when @jsrq='' then @now else @jsrq end),@cardje,@zhje,@yhje,0,0,0,0,'')
			if @@error<>0
			begin
				rollback tran
				select "F","���´��ҿ��������"
				return
			end

            insert into SF_CARDZFJEK(jssjh,lx,mc,je,memo)
            values(@sjh,'4','���ҿ�֧��',@cardje,'')
            if @@error<>0
			begin
				select 'F','����YY_JZBRYJK��¼ʱ����'
				rollback tran
				return
			end
		end
	end
    /*-----�Է�תҽ���ɷ�ʱ,����ԭ��¼,����¼��״̬ begin-----*/
	if @hcsjh<>''
	begin
		declare @ysjh ut_sjh,@ypatid ut_xh12,@yjzxh ut_xh12,@yqkje ut_money
		if exists(select 1 from SF_BRJSK(NOLOCK) where sjh = @hcsjh) OR	--@hcsjh��ʱ������ǡ�0������ô��Ѻ����ϸ���в������ݾͻᱨ������У��
			exists(select 1 from SF_NBRJSK(NOLOCK) where sjh = @hcsjh)
		BEGIN
			select @ysjh = tsjh,@ypatid=patid,@yqkje=(case when qkbz=3 then -qkje else 0 end) from SF_BRJSK (nolock) where sjh=@hcsjh
			update SF_BRJSK set ybjszt=2 where sjh=@hcsjh
			update SF_BRJSK set jlzt=1 where sjh=@ysjh
			if @@rowcount=0
			update SF_NBRJSK set jlzt=1 where sjh=@ysjh
			update SF_MZCFK set jlzt=1 where jssjh=@ysjh
			if @@rowcount=0
			update SF_NMZCFK set jlzt=1 where jssjh=@ysjh
			update SF_BRJSK set tsjh=@hcsjh where sjh=@sjh

			if @yqkje<>0
			begin
				select @yjzxh=xh from YY_JZBRK (nolock) where patid=@ypatid and jlzt=0 and gsbz=0   
				select @sjyjye=yjye from YY_JZBRK (nolock) where xh=@yjzxh
				insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh,ybdm,dbkje)
				select 0,0,@yjzxh,@czyh,@czym,@now,0,@yqkje,@sjyjye+@yqkje,1,4,null,0,'�Է�תҽ�����',@hcsjh,@ybdm,0
				update YY_JZBRK set yjye=@sjyjye+@yqkje where xh=@yjzxh
				update SF_BRXXK set zhje=@sjyjye+@yqkje where patid=@ypatid
			end
		END
	end
  /*-----�Է�תҽ���ɷ�ʱ,����ԭ��¼,����¼��״̬ end-----*/

	--ҩ����ˮ�Ŵ���
	if (select config from YY_CONFIG (nolock) where id='2132')='��'
	begin
		IF (select config from YY_CONFIG (nolock) where id='0081')='2' 
		BEGIN
			DECLARE cs_cfk_fyckxh CURSOR FOR SELECT DISTINCT fyckdm FROM SF_MZCFK(NOLOCK) WHERE jssjh=@sjh and cflx in (1,2,3)	-- add by gzy at 20050518
	        OPEN cs_cfk_fyckxh
	        FETCH cs_cfk_fyckxh INTO @fyckdm1
			WHILE @@fetch_status=0 
	        BEGIN        
	            if not exists(select 1 from SF_YFLSHK nolock where rq = @now8 and fyckdm = @fyckdm1)
                begin
                   insert into SF_YFLSHK(rq,xh,yfdm,fyckdm)
                   select @now8,1,@fyckdm1,@fyckdm1
                   select @yflsh =1
                end
                else 
				begin
                   update SF_YFLSHK set xh =xh +1,@yflsh = isnull(xh,0)+1 where rq = @now8 and fyckdm = @fyckdm1
                end
                select @yflsh  = isnull(@yflsh,1)
                UPDATE SF_MZCFK SET yflsh=@yflsh,fyckxh=@fyckxh+1 WHERE jssjh = @sjh AND fyckdm = @fyckdm1  -- modify by wfy 2007-03-16
                IF @@ERROR<>0 OR @@ROWCOUNT = 0
                BEGIN
                    select @errmsg='F������ˮ�Ŵ���'
					rollback tran
					DEALLOCATE cs_cfk_fyckxh
                    return      
                END
                FETCH cs_cfk_fyckxh INTO @fyckdm1
            END
            CLOSE cs_cfk_fyckxh
            DEALLOCATE cs_cfk_fyckxh
        END
		ELSE IF (select config from YY_CONFIG (nolock) where id='0081')='3' 
		begin
			if exists(select 1 from SF_MZCFK where jssjh=@sjh and cflx in (1,2,3))
			begin
                --select @yfdm=yfdm from SF_MZCFK 
				--select @yflsh=isnull(xh,0) from SF_YFLSHK(nolock) where rq =substring(@now,1,8)
				select @yflsh = xh from SF_YFLSHK(nolock) where rq =substring(@now,1,8) and yfdm=''
				if @@error<>0    
				begin    
					rollback tran
					select "F","ȡ���ҩ����ˮ�ų���"    
				    return    
				end    	

				select @yflsh = isnull(@yflsh,0)+1 -- add by wfy 2007-03-15

				if @yflsh<=1
					insert SF_YFLSHK(rq,xh,yfdm)
					values(substring(@now,1,8),@yflsh,'')  
				else
					update SF_YFLSHK set xh=xh+1 where rq =substring(@now,1,8) and yfdm=''
				if @@error<>0    
				begin    
					select "F","�������ݺų���"  
					rollback tran  
				    return    
				end    
				UPDATE SF_MZCFK SET yflsh=@yflsh WHERE jssjh = @sjh  -- modify by wfy 2007-03-16
				IF @@ERROR<>0 OR @@ROWCOUNT = 0
				BEGIN
					SELECT "F","����ҩ����ˮ�ų���"
					ROLLBACK TRAN
					RETURN
				END		
			end
		end
		else if (select config from YY_CONFIG (nolock) where id='0081')='1' 
		BEGIN
			DECLARE cs_cfk_yfdm CURSOR FOR SELECT DISTINCT yfdm FROM SF_MZCFK(NOLOCK) WHERE jssjh=@sjh	and cflx in (1,2,3)
	        OPEN cs_cfk_yfdm
	        FETCH cs_cfk_yfdm INTO @yfdm
			WHILE @@fetch_status=0 
	        BEGIN				
				select @yflsh = xh from SF_YFLSHK(nolock) where rq =substring(@now,1,8) and yfdm=@yfdm
				if @@error<>0    
				begin  
					select "F","ȡ���ҩ����ˮ�ų���"   
					rollback tran
					DEALLOCATE cs_cfk_yfdm			   
				    return    
				end    	

				select @yflsh = isnull(@yflsh,0)+1 -- add by wfy 2007-03-15

				if @yflsh<=1
					insert SF_YFLSHK(rq,xh,yfdm)
					values(substring(@now,1,8),@yflsh,@yfdm)  
				else
					update SF_YFLSHK set xh=@yflsh where rq =substring(@now,1,8) and yfdm=@yfdm
				if @@error<>0    
				begin    
					select "F","�������ݺų���" 
					rollback tran
					DEALLOCATE cs_cfk_yfdm   
				    return    
				end 
				UPDATE SF_MZCFK SET yflsh=@yflsh WHERE jssjh = @sjh and yfdm=@yfdm  -- modify by wfy 2007-03-16
				IF @@ERROR<>0 OR @@ROWCOUNT = 0
				BEGIN
					SELECT "F","����ҩ����ˮ�ų���"
					ROLLBACK TRAN
					DEALLOCATE cs_cfk_yfdm
					RETURN
				END
				FETCH cs_cfk_yfdm INTO @yfdm
			END
			CLOSE cs_cfk_yfdm
			DEALLOCATE cs_cfk_yfdm
		END
	end
	--�ַ�Ʊ��ν���ʱ,֧Ʊ�����з�̯
	if @jslb=1
	begin
		--add by xxl Լ�����ʹ��֧Ʊ֧������ֻ��ȫ��ʹ��֧Ʊ����֧�ֲ���֧Ʊ�����ֽ����߷�̯ʱ�ǳ����ӣ�δʵ�֣�����ǰ̨��ʾ
		if isnull(@zpje,0)>0
			select @zpje=(case when @qkbz1 = 4 then zfje-@qkje-isnull(@ylkje,0)-isnull(@gbje,0)-isnull(@bdyhkje,0)
                                  else zfje-@qkje-isnull(@ylkje,0)-isnull(@gbje,0)-isnull(@bdyhkje,0)-@cardje end)
			from SF_BRJSK where sjh=@sjh		
	end
	update SF_BRJSK set sfrq=(case when @jsrq='' then @now else @jsrq end),
		ybjszt=2,
		zxlsh=(case isnull(@zxlsh,'') when '' then zxlsh else @zxlsh end),
		fph=@fph,
		fpjxh=@fpjxh,
		zpje=isnull(@zpje,0),
		zph=@zph,
		pzh=(case when @qkbz1 = 4 then @cardno else pzh end),
		xjje =zfje-@qkje-isnull(@zpje,0)-isnull(@ylkje,0)-isnull(@gbje,0)-isnull(@bdyhkje,0),
		qkbz=@qkbz1,
		qkje=@qkje,
		--dnzhye=(case when @qkbz1=1 then @zhje when @qkbz1=4 then @sjyjye else dnzhye end),
		--mod by ozb 20080403 ҽ�����˵ĵ����ʻ������ǳ�ֵ������ҿ������
		dnzhye=(case when @qkbz1=1 and @pzlx not in ('10','11') then @zhje when @qkbz1=4 and @pzlx not in ('10','11') then @sjyjye else dnzhye end),
		qrrq=(case when @qrbz=0 then @now else null end),
		qrczyh=(case when @qrbz=0 then @czyh else null end)
		,ylkje=@ylkje
		,ylkysje=@ylkhcysje
		,ylksqxh=@ylksqxh
		,ylkzxlsh=@ylkzxlsh
		,ylcardno=@ylcardno	--mit ,, 2003-05-07 ,, �����������ֶ�
		,yflsh=@yflsh
		,spzlx = @spzlx
		,bdyhklsh = @bdyhklsh
		,bdyhkje = @bdyhkje
		,zlje=@zlje		--add by ozb 20060622
		,lrrq=@now
		,gxrq=@now
		,fpdybz=1
	where sjh=@sjh	
	if @@error<>0 or @@rowcount=0
	begin
		select "F","�����շѽ�����Ϣ����"
		rollback tran
		return
	end
	if (@qkbz1=3)and(@config2391=1)
	begin
		--cjt ҽ��վ�����ֽ���
		select @zsxjje= xjje,@zsyjye=qkje from SF_BRJSK where sjh=@sjh and qkbz=3
		if (@ysyjye<>@zsyjye)
		begin
			rollback tran
			select "F","����ʧ�ܣ�Ԥ��������һ�£������½��㣡"
			return
		end 
		if (@sjyjye<0)
		begin
			rollback tran
			select "F","����ʧ�ܣ����ν���Ѻ�����С��0�������½��㣡"
			return
		end 
	end


	-- by will 20110919
	IF @configdyms = 0
	begin
		if @print=0
		begin
			update SF_BRJSK set fpdybz=0,fpdyczyh=@czyh,fpdyrq = @now where sjh = @sjh
			if @@error<>0 or @@rowcount=0
			begin
				select "F","�����շѷ�Ʊ��Ϣ����"
				rollback tran
				return
			end
		end
	end
	if exists (select 1 from sysobjects where name='SF_BRJSK_FZ' and xtype='U')
	BEGIN		
		update  SF_BRJSK_FZ set fph=@fph,fpjxh=@fpjxh,fpdm=@fpdm where sjh=@sjh		 
		if @@error<>0
		begin
			select "F","��������˵�����"
			rollback tran
			return		
		end	
	END	
	if @isQfbz=1 
	begin
		update SF_BRJSK set qrbz=1 where sjh=@sjh
		if @@error<>0 or @@rowcount=0
		begin
			select "F","�����շѽ�����Ϣ����"
			rollback tran
			return
		end
	end

	--add by will for bug 103404
	declare @config2238 varchar(20)  
	select @config2238 =config from YY_CONFIG where id ='2238'
    if exists(select 1 from SF_BRJSK where sjh=@sjh and zfje=0 and @config2238='��')
		set @isQfbz=1


	--mit ,, 2003-05-07 ,, �����Ԥ�������YY_YLJYJLK
	if (@ylkhcysje<>0) and (@ylkhcyssqxh<>'') 
	begin
		declare @hcxh ut_xh12
		select @hcxh = xh 
		from YY_YLJYJLK 
		where patid=@patid and jlzt in(0,1)

		update YY_YLJYJLK
		set jlzt=2
		where xh=@hcxh
		if @@error<>0
		begin
			select "F","����������Ԥ����Ϣ����"
			rollback tran
			return
		end
		insert into YY_YLJYJLK(ylcardno,patid,ylkje,jyrq,ylksqxh,ylkzxlsh,jlzt,qxxh,sjh)
		values(@ylcardno,@patid,@ylkhcysje,@jsrq,@ylkhcyssqxh,@ylkhcyszxlsh,3,@hcxh,@sjh)
		if @@error<>0
		begin
			rollback tran
			select "F","����������Ԥ����Ϣ����"
			return
		end
	end
		
	/*���·�ҩ���ں���ҩ���ڵ�δ��ҩ������, Wang Yi 2003.02.25*/	
/*
	--��Ϊǰ̨���ô洢���̴���
	update YF_FYCKDMK set fpzs = fpzs + b.num
		from YF_FYCKDMK a, (select count(xh) num, yfdm, fyckdm 
				from SF_MZCFK (nolock) where jssjh = @sjh and cflx <> 4 group by yfdm, fyckdm) b
		where a.yfdm = b.yfdm and a.id = b.fyckdm
	if @@error<>0
	begin
		select "F","���·�ҩ������Ϣ����"
		rollback tran
		return
	end
    
	update YF_PYCKDMK set fpzs = fpzs + b.num
		from YF_PYCKDMK a, (select count(xh) num, yfdm, pyckdm 
				from SF_MZCFK (nolock) where jssjh = @sjh and cflx <> 4 group by yfdm, pyckdm) b
		where a.yfdm = b.yfdm and a.id = b.pyckdm
	if @@error<>0
	begin
		select "F","������ҩ������Ϣ����"
		rollback tran
		return
	end
*/	
    --���洦��add by tony
		declare @isdlsjfa ut_bz, --�Ƿ���ö����ۼ۷�����0�����ã�1���ã�
			@ypxtslt int--���ۼ۷���
			,@cfxh_temp int,@cfmxxh_temp int
			,@ypmc_temp ut_mc64
	select @isdlsjfa=0,@ypxtslt=0,@cfxh_temp= 0,@cfmxxh_temp =0,@ypmc_temp= ''
	if exists(select 1 from sysobjects where name='f_get_ypxtslt')	
	begin
		select @ypxtslt=dbo.f_get_ypxtslt()  
		if @ypxtslt=3 
		select @isdlsjfa=1
	end
	-- add by jch 20190412 ����ʡ��ҽԺ-�����շ�Ԥ���㶳��������ⶳ��ҵ��ͻ
	if @isdlsjfa = 1 
	begin
		declare cs_mzsf_dpcjgcl cursor for
			select b.xh,a.xh ,b.ypmc from SF_MZCFK a(nolock),SF_CFMXK b(nolock) where a.jssjh=@sjh and a.xh=b.cfxh and a.cflx in(1,2,3) --and isnull(a.hjxh,0)=0 and isnull(b.hjmxxh,0) =0
			for read only
			open cs_mzsf_dpcjgcl
			fetch cs_mzsf_dpcjgcl into @cfmxxh_temp,@cfxh_temp,@ypmc_temp
			while @@fetch_status=0
			begin
				if not exists(select 1 from YF_YPDJJLK(nolock)  where mxtbname= 'SF_CFMXK' and zd_xh =@cfxh_temp and  mxxh = @cfmxxh_temp)
				begin
					select 'F','�շ�ʱ��ȡ�����¼ҩƷ���ơ�'+@ypmc_temp+'������Ϣʧ�ܣ������Զ��ع����������շѣ�'
					rollback tran
					deallocate cs_mzsf_dpcjgcl
					return
				end
				fetch cs_mzsf_dpcjgcl into @cfmxxh_temp,@cfxh_temp,@ypmc_temp
			end
			close cs_mzsf_dpcjgcl
			deallocate cs_mzsf_dpcjgcl
	end
	else
	if @isdlsjfa=0
	begin
		declare @yfdm_djkc ut_ksdm,
		    @mxtbname_djkc ut_mc32,
		    @hjmxxh_djkc ut_xh12,
		    @cfmxxh_djkc ut_xh12,
		    @idm_djkc ut_xh9,
		    @czls_djkc ut_sl10,
			@yczls_djkc ut_sl10,  --ԭ��������
		    @rtnmsg_djkc varchar(50)   

	    if exists (select 1 from YY_CONFIG (nolock) where id='2101' and config='��')
		begin
			/* 
			update YF_YFZKC set djsl=a.djsl+b.ypsl
				from YF_YFZKC a,(select yfdm,d.cd_idm,sum(d.ypsl*d.cfts) as ypsl from SF_CFMXK d(nolock),SF_MZCFK e(nolock) 
					where d.cfxh=e.xh and e.jssjh=@sjh and e.cflx in (1,2,3)  group by yfdm, cd_idm)  b
				where a.cd_idm=b.cd_idm and b.yfdm=a.ksdm
			if @@error<>0
			begin
				select "F","������ҩ������Ϣ����"
				rollback tran
				return
			end
			*/
			--��ʼ������
			select @yfdm_djkc = '',@mxtbname_djkc = '',@hjmxxh_djkc = 0,@cfmxxh_djkc = 0,@idm_djkc = 0,@czls_djkc = 0,@yczls_djkc = 0, @rtnmsg_djkc = '';

		    declare cs_cfk_djkc cursor for select distinct a.yfdm,b.xh,b.cd_idm,b.ypsl*b.cfts
				from SF_MZCFK a(nolock) inner join SF_CFMXK b(nolock) on a.xh = b.cfxh
				where a.jssjh = @sjh and a.cflx in (1,2,3)    
	        open cs_cfk_djkc
	        fetch cs_cfk_djkc INTO @yfdm_djkc,@cfmxxh_djkc,@idm_djkc,@czls_djkc
			while @@fetch_status=0 
	        begin 
	            if @idm_djkc>0 
	            begin  
					exec usp_yf_jk_yy_freeze 1,@yfdm_djkc,'SF_CFMXK',@cfmxxh_djkc,@idm_djkc,@czls_djkc,0,@rtnmsg_djkc output
					if substring(@rtnmsg_djkc,1,1)='F'
					begin
						select 'F','����ҩƷ������,'+substring(@rtnmsg_djkc,2,len(@rtnmsg_djkc)-1)
						rollback tran
						deallocate cs_cfk_djkc
						return
					end
	            end
	            fetch cs_cfk_djkc INTO @yfdm_djkc,@cfmxxh_djkc,@idm_djkc,@czls_djkc
	        end
			close cs_cfk_djkc
			deallocate cs_cfk_djkc	
		end

	    if exists (select 1 from YY_CONFIG (nolock) where id='2422' and config='��')
		begin
			--��ʼ������
			select @yfdm_djkc = '',@mxtbname_djkc = '',@hjmxxh_djkc = 0,@cfmxxh_djkc = 0,@idm_djkc = 0,@czls_djkc = 0,@yczls_djkc = 0, @rtnmsg_djkc = '';

		    declare cs_cfk_djkc_new cursor for select distinct a.yfdm,isnull(b.hjmxxh,0),b.xh,b.cd_idm,b.ypsl*b.cfts,isnull(c.ypsl*c.cfts,b.ypsl*b.cfts) ypsl_old
				from SF_MZCFK a(nolock)
				join SF_CFMXK b(nolock) on a.xh=b.cfxh
				left join SF_HJCFMXK c(nolock) on c.cfxh=a.hjxh
				where a.jssjh=@sjh and a.cflx in (1,2,3)    
	        open cs_cfk_djkc_new
	        fetch cs_cfk_djkc_new INTO @yfdm_djkc,@hjmxxh_djkc,@cfmxxh_djkc,@idm_djkc,@czls_djkc,@yczls_djkc 
			while @@fetch_status=0 
	        begin 
	            if @idm_djkc>0 
	            begin  
					if @hjmxxh_djkc=0
					begin 
						exec usp_yf_jk_yy_freeze 1,@yfdm_djkc,'SF_CFMXK',@cfmxxh_djkc,@idm_djkc,@czls_djkc,0,@rtnmsg_djkc output
						if substring(@rtnmsg_djkc,1,1)='F'
						begin
						    select 'F','����ҩƷ������,'+substring(@rtnmsg_djkc,2,len(@rtnmsg_djkc)-1)
						    rollback tran
							deallocate cs_cfk_djkc_new
							return
						end
					end
					else
					begin 
					    --����ڶ�����־�д�����ⶳ��ҽ��վ�ж�ʱ�ⶳ������Ѿ��ⶳ������Ҫ�ⶳ
                        if exists(select 1 from YF_YPFREEZELOG a(nolock) where a.mxtbname='SF_HJCFMXK' and a.mxxh=@hjmxxh_djkc 
                             and a.yfdm=@yfdm_djkc and a.cd_idm=@idm_djkc and a.jlzt=0 )
                        begin       
							exec usp_yf_jk_yy_freeze 2,@yfdm_djkc,'SF_HJCFMXK',@hjmxxh_djkc,@idm_djkc,@yczls_djkc,0,@rtnmsg_djkc output
							if substring(@rtnmsg_djkc,1,1)='F'
							begin
								select 'F','�ⶳҩƷ������,'+substring(@rtnmsg_djkc,2,len(@rtnmsg_djkc)-1)
								rollback tran
								deallocate cs_cfk_djkc_new
								return
							end
						end 
						exec usp_yf_jk_yy_freeze 1,@yfdm_djkc,'SF_CFMXK',@cfmxxh_djkc,@idm_djkc,@czls_djkc,0,@rtnmsg_djkc output
					    if substring(@rtnmsg_djkc,1,1)='F'
						begin
						    select 'F','����ҩƷ������,'+substring(@rtnmsg_djkc,2,len(@rtnmsg_djkc)-1)
						    rollback tran
							deallocate cs_cfk_djkc_new
							return
						end
					end
	            end
	            fetch cs_cfk_djkc_new INTO @yfdm_djkc,@hjmxxh_djkc,@cfmxxh_djkc,@idm_djkc,@czls_djkc,@yczls_djkc
	        end
			close cs_cfk_djkc_new
			deallocate cs_cfk_djkc_new	        
		end
	end
	
	select @tcljybdm = config from YY_CONFIG where id = '0115'
	if charindex('"'+rtrim(@ybdm)+'"',@tcljybdm) > 0
	begin
		declare @mzpatid ut_xh12,
				@m_cardno ut_cardno,
				@tcljje2 ut_money,
				@cardtype ut_dm2
		select @m_cardno = cardno,@cardtype = cardtype,
				@tcljje2=zje-(zfje-srje)-yhje-isnull(tsyhje,0)
				from SF_BRJSK nolock where sjh = @sjh
		select @mzpatid=mzpatid from YY_BRLJXXK nolock where cardno = @m_cardno and cardtype = @cardtype
		if @@rowcount <> 0
		begin
			exec usp_zy_tcljjegl @m_cardno,@mzpatid,@tcljje2,0,0,0,2,@czyh
			if @@error <> 0 
			begin
				rollback tran
				select "F","����YY_BRLJXXK��ͳ���ۼƽ�����"
				return
			end
		end
	end	
	
	--�����»��ۿ�Ķ����Ƶ��������ִ�У��������������ĸ���	
	update SF_HJCFK set jlzt=1 
		where xh in (select hjxh from #sfcfk) and patid=@patid
	if @@error<>0
	begin
		select "F","���»�����Ϣ����"
		rollback tran
		return
	end
	
	update SF_HJCFK set jlzt=jlzt-5 
		where xh not in (select hjxh from #sfcfk) and jlzt in (5,8) and patid=@patid
			and ISNULL(alcfbz,0)=0--add h_ww 20150305 for 14182 ֻ����û��ת�밢��Ĵ���
	if @@error<>0
	begin
		select "F","���»�����Ϣ����"
		rollback tran
		return
	end


	--������ϵͳ�Զ������ 
	
    --������ϵͳ�Զ������ 	 
	if ((select config from YY_CONFIG where id='A219')='��') 
	    and ((select config from YY_CONFIG where id='A234')='��')
	    and ((select config from YY_CONFIG where id='A262')='��')
	    and ((select config from YY_CONFIG where id='A206')='1')
	begin
		--begin tran
		declare @pcxh ut_xh12  --�������  
			 ,@tm ut_mc64   --���� 
			 ,@pcfxh	ut_xh12 
			 ,@pcfmxxh	ut_xh12 
			 ,@phjcfxh	ut_xh12 
			 ,@phjcfmxxh	ut_xh12 

		declare cs_ejkc cursor for 
		select a.xh, b.xh,b.hjmxxh,c.cfxh  from SF_MZCFK a (nolock),SF_CFMXK b (nolock) ,SF_HJCFMXK c (nolock) 
		where a.xh=b.cfxh and b.hjmxxh=c.xh and jssjh=@sjh and cflx not in (1,2,3)
		for read only

		open cs_ejkc
		fetch cs_ejkc into @pcfxh,@pcfmxxh ,@phjcfmxxh,@phjcfxh
		while @@fetch_status=0
		begin 
			---��ȡ����
			select @pcxh=0,@tm='0'
			select @pcxh=isnull(wzpcxh,'0'),@tm=isnull(txm,'0') from fun_yy_mz_cljlk(@phjcfxh,@phjcfmxxh,0) 
			---�������
			if isnull(@pcxh,0)<>0 or isnull( @tm,'0')<>'0'
			begin			
				exec usp_wz_hisxhpcl @pcfxh,0,@errmsg output,@pcxh=@pcxh,@tm=@tm,@mxxh=@pcfmxxh  
				if @errmsg like 'F%' or @@error<>0  
				begin    
			 		rollback tran
					select "F","������ϵͳ�Զ��������ʱ����"+@errmsg
					deallocate cs_ejkc
					return
				END  

				---����״̬
				exec usp_yy_mz_updatecljlk @phjcfxh,@phjcfmxxh,@pcfmxxh,2,@errmsg output 
				if @errmsg like 'F%' or @@error<>0  
				begin    
			 		rollback tran
					select "F","������ϵͳ�Զ��������ʱ����--����״̬��"+@errmsg
					deallocate cs_ejkc
					return
				END   
			end

			fetch cs_ejkc into @pcfxh,@pcfmxxh,@phjcfmxxh,@phjcfxh
		end
		close cs_ejkc
		deallocate cs_ejkc
	--	commit tran
	end

-- add kcs 20190523 ��ֹǷ���Ƿ�ѱ�־<>2���⵼��©�� >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	if exists(select 1 from SF_BRJSK a(nolock) inner join YY_YBFLK b(nolock) on a.ybdm = b.ybdm where b.zhbz=2 and a.qkbz <>2 and a.sjh = @sjh)
	begin
		select 'F','Ƿ�ѻ��߱���Ƿ�����'
		return
	end
-- add kcs 20190523 ��ֹǷ���Ƿ�ѱ�־<>2���⵼��©�� <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	  
	commit tran

	
	
	--����ҽ��վ��ҩ���̣���ҩд��SF_PYQQK��ʽ��
	begin tran
	if (select config from YY_CONFIG where id='0124')='��'
	begin
		declare cs_pyqq cursor for 
		select xh,hjxh from SF_MZCFK(nolock) where jssjh=@sjh and cflx in (1,2,3)
		for read only

		open cs_pyqq
		fetch cs_pyqq into @pycfxh,@pyhjxh
		while @@fetch_status=0
		begin
			if @pyhjxh=0 or not exists(select 1 from SF_PYQQK(nolock) where hjxh=@pyhjxh and @pyhjxh>0 and jlzt=0)
			begin
				insert into SF_PYQQK(jssjh,hjxh,cfxh,cfkxh,czyh,lrrq,patid,hzxm,ybdm,py,wb,ysdm,ksdm,yfdm,qrczyh,
				qrrq,qrksdm,pyczyh,pyrq,cfts,txh,sfckdm,pyckdm,fyckdm,jsbz,
				jlzt,fybz,cflx,sycfbz,tscfbz,pybz,jcxh,memo,zje,zfyje,yhje,zfje,srje,
				fph,fpjxh,tfbz,tfje,xzks_id ,spzfbl,spzfce,pyqr,sqdxh,yflsh,ejygksdm,
				ejygbz,ksfyzd_xh,fyckxh,dpxsbz,zpwzbh,zpbh)
				select jssjh,hjxh,cfxh,xh,czyh,lrrq,patid,hzxm,ybdm,py,wb,ysdm,ksdm,yfdm,qrczyh,
				qrrq,qrksdm,pyczyh,pyrq,cfts,txh,sfckdm,pyckdm,fyckdm,1,
				jlzt,fybz,cflx,sycfbz,tscfbz,pybz,jcxh,memo,isnull(zje,0),isnull(zfyje,0),isnull(yhje,0),isnull(zfje,0),isnull(srje,0),
				fph,fpjxh,tfbz,tfje,xzks_id ,0,0,pyqr,sqdxh,yflsh,ejygksdm,
				ejygbz,ksfyzd_xh,fyckxh,dpxsbz,zpwzbh,zpbh
				from SF_MZCFK(nolock)
				where xh=@pycfxh
				if @@error<>0
				begin
					rollback tran
					select "F","����SF_PYQQK����"
					deallocate cs_pyqq
					return
				end

				select @xhtemp=@@identity

				insert into SF_PYMXK(cfxh,cd_idm,gg_idm,dxmdm,ypmc,ypdm,ypgg,ypdw,dwxs,ykxs,ypfj,ylsj,ypsl,
				ts,cfts,zfdj,yhdj,shbz,memo,flzfdj,txbl,lcxmdm,lcxmmc,zbz,yjqrbz,qrksdm,clbz,hjmxxh,
				hy_idm,hy_pdxh,gbfwje,gbfwwje,gbtsbz,btsbl,hzlybz,bgdh,bgzt,txzt,bglx,fpzh,lcxmsl,
				dydm,yyrq,yydd,zysx,yylsj,yjspbz)
				select @xhtemp,cd_idm,gg_idm,dxmdm,ypmc,ypdm,ypgg,ypdw,dwxs,ykxs,ypfj,ylsj,ypsl,
				ts,cfts,zfdj,yhdj,shbz,memo,flzfdj,txbl,lcxmdm,lcxmmc,zbz,yjqrbz,qrksdm,clbz,hjmxxh,
				hy_idm,hy_pdxh,gbfwje,gbfwwje,gbtsbz,gbtsbl,hzlybz,bgdh,bgzt,txzt,bglx,fpzh,lcxmsl,
				dydm,yyrq,yydd,zysx,yylsj,yjspbz
				from SF_CFMXK(nolock)
				where cfxh=@pycfxh
				if @@error<>0
				begin
					rollback tran
					select "F","����SF_PYMXK����"
					deallocate cs_pyqq
					return
				end
			end

			if exists(select 1 from SF_PYQQK(nolock) where hjxh=@pyhjxh and @pyhjxh>0 and pybz=1 and jlzt=0)
			begin
				update SF_MZCFK set pybz=1,pyczyh=b.pyczyh,pyrq=b.pyrq
				FROM SF_MZCFK a(nolock),SF_PYQQK b(nolock)
				where a.xh=@pycfxh and a.hjxh=b.hjxh and b.jlzt=0
				if @@error<>0
				begin
					rollback tran
					select "F","����SF_MZCFK����ҩ��־ʱ����"
					deallocate cs_pyqq
					return
				end
			end

			if exists(select 1 from SF_PYQQK(nolock) where hjxh=@pyhjxh and @pyhjxh>0 and jlzt=0)
			begin
				update SF_PYQQK set jsbz=1,jssjh=@sjh
				where hjxh=@pyhjxh and jlzt=0 and patid=@patid
				if @@error<>0
				begin
					rollback tran
					select "F","����SF_PYQQK�Ľ����־ʱ����"
					deallocate cs_pyqq
					return
				end
			end

			fetch cs_pyqq into @pycfxh,@pyhjxh
		end
		close cs_pyqq
		deallocate cs_pyqq
	end
	commit tran

	exec usp_sf_savegbxx @sjh --����ɱ���Ϣ  && 
	  
	select 	@ysybzfje = isnull(sum(je),0) from SF_JEMXK nolock where lx in ('20','22') and jssjh = @sjh
	select 	@gbje2 = isnull(sum(je),0) from SF_JEMXK nolock where lx in ('24') and jssjh = @sjh
	
	if @config1524='��'
	begin
	    exec usp_app_xxts '02',@patid,@sjh,'','','','','',@errmsg output
	end	
	/*
	if @errmsg like "F%"
	begin
		select "F",substring(@errmsg,2,49)
		return
	end
	*/	
	
	--if @acfdfp=0  or @configdyms=1 --mod by ozb 20060704 add @configdyms=1
	--begin
	--	if (select config from YY_CONFIG (nolock) where id='2036')='��'
	--	begin
	--		select "T", @zje, case when @gbbz = '0' then @zfyje-@flzfje else @gbje2 end, convert(varchar(20),@fph),   --0-3
	--			@print, @sfje2-@qkje-isnull(@gbje,0)-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,  --  4-6
	--			@tczfje, @dffjzfje, '', @qkbz1, @qkje2  --7-11
	--			,@sjyjye,@sjdjje, @kmc, @cardno, @tsyhje, @gbje,@ysybzfje, @lczje,@lcyhje,@zpje,@fpdm	-- 12 -,19,20,21,22
	--		union all
	--		select fpxmmc, sum(xmje), 0, '0', 0, sum(zfje), sum(zfyje), sum(yhje), 0, fpxmdm, 0,0,0,0,'','',0,0,0,0,0,0,''
	--			from SF_BRJSMXK where jssjh=@sjh 
	--			group by fpxmdm, fpxmmc
	--	end
	--	else begin
	--		select "T", @zje, case when @gbbz = '0' then @zfyje-@flzfje else @gbje2 end, convert(varchar(20),@fph), 
	--			@print, @sfje2-@qkje-isnull(@gbje,0)-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,
	--			@tczfje, @dffjzfje, '', @qkbz1, @qkje2
	--			,@sjyjye,@sjdjje, @kmc, @cardno,@tsyhje, @gbje,@ysybzfje, @lczje,@lcyhje,@zpje,@fpdm	-- 12 -,19,20,21,22
	--		union all
	--		select dxmmc, xmje, 0, '0', 0, zfje, zfyje, yhje, 0, dxmdm, 0,0,0,0,'','',0,0,0,0,0,0,@fpdm
	--			from SF_BRJSMXK where jssjh=@sjh
	--	end
 --   end
	--else
	--begin
 --       select  "T",sum(zje) as zje, sum(zfyje) as zfyje,convert(varchar(20),fph) as fph,@print, sum(zfje) as zfje, 
	--			@qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,
	--			@tczfje, @dffjzfje, '', @qkbz1, @qkje2,max(xh) as xh,max(cfxh) as cfxh 
	--			,@sjyjye,@sjdjje, @kmc, @cardno,@tsyhje, @gbje,@ysybzfje, @lczje,@lcyhje,@zpje	-- 14,21,22,23
	--	from SF_MZCFK  where jssjh=@sjh  
	--	group by fph		
	--	order by fph
	--end
end

return



