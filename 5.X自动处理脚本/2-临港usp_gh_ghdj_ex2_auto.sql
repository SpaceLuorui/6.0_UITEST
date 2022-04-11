create proc usp_gh_ghdj_ex2_auto
	@wkdz varchar(32),
	@jszt smallint,
	@ghbz smallint,
	@ghlb smallint,
	@czksfbz  int,    
	@cfzbz smallint,
	@patid ut_xh12,
	@czyh ut_czyh,
  	@ksdm ut_ksdm,
  	@ysdm ut_czyh,
	@ghksdm ut_ksdm,
  	@sjh ut_sjh = null,
	@lybz smallint = 0,
	@yyxh ut_xh12 = null,
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
	@jsrq ut_rq16 = '',
	@qkbz smallint = 0
	,@ylcardno ut_cardno=''
	,@ylkje ut_money=0
	,@ylkysje ut_money=0
	,@ylksqxh ut_lsh=''
	,@ylkzxlsh ut_lsh=''
	,@ylkyssqxh ut_lsh=''
	,@ylkyszxlsh ut_lsh=''
	,@yslx int=0
	,@cardxh ut_xh12=0			--add by chenwei 2003.12.06
	,@cardje ut_money=0			--ͬ��
	,@zph varchar(32) = ''
	,@zpje numeric(12,2) = 0
	,@yyrq ut_rq16=''
	,@bdyhkje ut_money = 0
	,@bdyhklsh ut_lsh = ''
	,@zlje	ut_money=0
	,@zzdjh varchar(100) = ''
	,@hcsjh ut_sjh =''  --�Է�תҽ���ɷ��õ�,ָ���Ǻ���¼��sjh
as --��421758 2018-09-14 14:30:37 4.0��׼��
/**********
[�汾��]4.0.0.0.0
[����ʱ��]2004.10.25
[����]����
[��Ȩ] Copyright ? 2004-2008�Ϻ����˴�-��������ɷ����޹�˾[����]�ҺŵǼ�
[����˵��]
	�ҺŵǼǹ���(ֻ����ghbz=2)
[����˵��]
	@wkdz varchar(32),	������ַ
	@jszt smallint,		����״̬	1=������2=���룬3=�ݽ�
	@ghbz smallint,		�Һű�־: 0=Ԥ�㣬1=�ݽ�(����1), 2=��ʽ�ݽ�(����2)
	@ghlb smallint,		�Һ����0=��ͨ��1=���2=ר�ң�3=����ר�ң�4=����Һţ�5=���6=����Һ�
	@czksfbz  int    ��ֵ���շѱ�־�� 0 :���ӳ�ֵ���շ�  ��1 �ӳ�ֵ���շ� add by szj
	@cfzbz smallint,	�����־��0=���1=����
	@patid ut_xh12,		����Ψһ��ʶ
	@czyh ut_czyh,		����Ա��
  	@ksdm ut_ksdm,		���Ҵ���
  	@ysdm ut_czyh,		ר�Ҵ���
	@ghksdm ut_ksdm,	�Һſ��Ҵ���
 	@sjh ut_sjh = null,	������վݺ�
	@lybz smallint = 0,	�Һ���Դ0=��ͨ��1=ԤԼ 2=������
	@yyxh ut_xh12 = null,	ԤԼ���
	@zhbz ut_zhbz = null,	�˻���־
	@zddm ut_zddm = null,	��ϴ���
	@zxlsh ut_lsh = null,	������ˮ��
	@jslsh ut_lsh = null,	������ˮ��
	@xmlb ut_dm2 = null,	����Ŀ
	@qfdnzhzfje numeric(12,2) = null, 	�𸶶ε����˻�֧��
	@qflnzhzfje numeric(12,2) = null,	�𸶶������ʻ�֧��
	@qfxjzfje numeric(12,2) = null,		�𸶶��ֽ�֧��
	@tclnzhzfje numeric(12,2) = null,	ͳ��������ʻ�֧��
	@tcxjzfje numeric(12,2) = null,		ͳ����ֽ�֧��
	@tczfje numeric(12,2) = null,		ͳ���ͳ��֧��
	@fjlnzhzfje numeric(12,2) = null,	���Ӷ������ʻ�֧��
	@fjxjzfje numeric(12,2) = null,		���Ӷ��ֽ�֧��
	@dffjzfje numeric(12,2) = null,		���Ӷεط�����֧��
	@dnzhye numeric(12,2) = null,		�����˻����
	@lnzhye numeric(12,2) = null,		�����˻����
	@jsrq ut_rq16 = ''					��������
	@qkbz smallint = 0					Ƿ���־0��������2��Ƿ��
--mit add 2003-05-05 ,,������ 
	,@ylcardno ut_cardno=''		����������
	,@ylkje ut_money=0		���������
	,@ylkysje ut_money=0		������Ԥ�ڽ��
	,@ylksqxh ut_lsh=����		�������������
	,@ylkzxlsh ut_lsh=����		������������ˮ��
	,@ylkyssqxh ut_lsh=����		������Ԥ���������
	,@ylkyszslsh ut_lsh=����		������Ԥ��������ˮ��
	,@yslx int=0			������Ԥ������0:����,1:����
	,@cardxh ut_xh12=0			--add by chenwei 2003.12.06
	,@cardje ut_money=0			--ͬ��
	,@zph varchar(32) = null	֧Ʊ��
	,@zpje numeric(12,2) = null	֧Ʊ���
	,@zlje	ut_money=0		������

[����ֵ]
[�����������]
[���õ�sp]
[����ʵ��]
 exec usp_gh_ghdj_ex2 "0013D35E10F4",3,2,2,1,1,2864.0,"00","3207","w859","","20070424000027",0,0,"","","","","",0,0,0,0,0,0,0,0,0,0,0,@zlje=0.00,@zzdjh=""--��Ztsql��
 
[�޸ļ�¼]
	20030912 tony	ҽ�������޸ģ�
		1��@ghbz=0ʱ���������ֶΣ��Һŷѣ����Ʒѣ�Ԥ�������
		2���˲в��˹ҺŷѲ���
		3������Ԥ�����˴��˻����Ǯ
		4������ҺŹҿ���ʱ���ƷѰ�������ȡ
	20031119 mit ���ӷ���Ѻ�����Ͷ�����
	2003.11.8 tony ҽ������ʹ�ó�ֵ��ʱ������usp_gh_ghdj_ex1��SF_BRJSKǷ���־��Ƿ������洢���̲��ٴ���
	2003.12.24 cherry ��Ӵ��ҿ��ʻ�����
modify by szj	 2004.02.18 ��ֵ����Ҫ�ṩ��
	��ſ����շ������@czksfbz �����������Ƿ�ӳ�ֵ���Ͽ�Ǯ
	20060524 ozb �޸ĹҺź����ظ������⣬ʹ����� ���������еľۺϺ������������ ʹ��max���count�������ź�����ĺ����ظ���
		ע�ⲻ����ȫ����,ֻ�ܽ��ͷ����ĸ��ʣ�Ҫ��ȫ������Ҫ���ϴ���޸ġ��������ַ������޸���洢���̣���commit֮ǰ����delay���,ͬʱ������������йҺžͿ�������
	20060622 ozb ���ӱ���������
	20070206 ozb ԤԼ�Һŵĺ���ԤԼ���Ⱥ�˳����
	20070424 wfy �ô洢����usp_gh_getghhx�õ���ǰ����
	20071111 ozb �·�Ʊ��ӡģʽ�£��Ƿ��ӡ��Ʊͨ���洢����usp_gh_getfpprintflag���  
	20071203 ozb ��ģʽ�²��ڸô洢�������߷�Ʊ�������ڷ�Ʊ��ӡʱ���߷�Ʊ
    select * from YY_CONFIG where id='1124'
    update YY_CONFIG set config='��' where id='1124'
**********/
set nocount on

declare	@now ut_rq16,		--��ǰʱ��
		@ybdm ut_ybdm,		--ҽ������
		@zfbz smallint,		--������־
		@rowcount int,
		@error int,
		@zje ut_money,		--�ܽ��
		@zfyje ut_money,	--�Էѽ��
		@yhje ut_money,		--�Żݽ��
		@ybje ut_money,		--������ҽ������Ľ��
		@pzlx ut_dm2,		--ƾ֤����
		@sfje ut_money,		--ʵ�ս��
		@sfje1 ut_money,	--ʵ�ս��(�����Էѽ��)
		@errmsg varchar(100),
		@srbz char(1),		--�����־
		@srje ut_money,		--������
		@sfje2 ut_money,	--������ʵ�ս��
		@xhtemp ut_xh12,
		@ksmc ut_mc32,		--��������
		@ysmc ut_mc64,		--ҽ������
		@xmzfbl float,		--��Ŀ�Ը�����
		@xmce ut_money,		--�Ը����ʹ����Ը������ܵĲ��
		@fph bigint,			--��Ʊ��
		@fpjxh ut_xh12,		--��Ʊ�����
		@print smallint,	--�Ƿ��ӡ0��ӡ��1����,2�ڳ�ֵ����ʱ���Ѿ���ӡ,3�·�Ʊģʽ�´�ӡ
		@ghhx int,			--�Һź���
		@ghzdxh ut_xh12,	--�Һ��˵����
		@brlx ut_dm2,		--��������
		@pzh ut_pzh,		--ƾ֤��
		@qkbz1 smallint,	--Ƿ���־0��������1�����ˣ�2��Ƿ�� 3���۳�ֵ��
		@zhje ut_money,		--�˻����
		@qkje ut_money,		--Ƿ������˽�
		@scybdm ut_ybdm,	--�˲в���ҽ������
		@yjbz ut_bz,		--�Ƿ�ʹ�ó�ֵ��
		@yjye ut_money,		--Ԥ�������
		@ybldbz varchar(2),	--ҽ���Ƿ���������
		@ghf ut_money,		--�Һŷ�
		@zlf ut_money,		--���Ʒ�
		@qrbz ut_bz,		--ȷ�ϱ�־0����ȷ�ϣ�1δȷ�ϣ�2��ȷ��
		@yjyebz varchar(2),	--��ֵ�������Ƿ���������շ�
		@yjdybz varchar(2)	--��ֵ���Һ��Ƿ��ӡ��Ʊ
		,@sjyjye ut_money	--ʵ��Ѻ�����
		,@sjdjje ut_money,	--ʵ�ʶ�����		--mit , 2oo3-11-19
		@cardbz ut_bz,	    	--�Ƿ�ʹ�ô��ҿ�
		@kdm ut_dm2,		--���ҿ��������
		@kmc ut_mc16,		--���ҿ�����
		@cardno ut_pzh,		--���ҿ�����
		@tcljbz ut_bz,		--ͳ���ۼƱ�־
		@tcljje ut_money,	--ͳ���ۼƽ��
		@yyjlbz ut_bz       	--�෽ԤԼ��¼��־ (0��ʹ�ã� 1ʹ�� )
		,@qkje2 ut_money
		,@zhdcsx ut_money 	--�˻���������
		,@gyfpbz ut_bz		--���÷�Ʊ
		,@gbje ut_money		--�ɱ����		
		,@ybzfje ut_money   --ҽ���Ը����
		,@gbbz ut_bz   
		,@ghysdm  ut_czyh
		,@jmje	ut_money	--���Ѽ�����
		,@hxfs	ut_bz		--����ʽ ��ԤԼ���˵ĹҺź����Ƿ��ԤԼ������+1��ʼ 1 �� 0 �� add by ozb 20070131
		,@maxyys	int	--ԤԼ��
		,@tcljybdm varchar(500)  --ͳ���ۼ�ҽ������
		,@nconfigdyms	ut_bz	--0 �ɴ�ӡģʽ 1 �´�ӡģʽ
		,@ntry int	--���Լ�����
		,@delaytime datetime
		,@yyxh1  ut_xh12	--ԤԼ���
		,@djje	ut_money	--������
        ,@ghjzsj varchar(8)
        --,@fsdgh  ut_bz      --��ʱ��ιҺű�־1��0��
        ,@pbmxxh ut_xh12
        ,@ghrq   ut_rq16
        ,@hxlx int
        ,@hxfs_new    smallint  	/*����ʽ	0 ռ�ŷ�ʽ����ԤԼ�˼��ŹҺž��Ǽ��ţ���ҽ��	
									1 ԤԼ�ź��ֳ��ŷ�����㣬������ԤԼ��Ϊ1 3 5 7 9��������ԤԼ����ֻ�ܹҺ�ԤԼ���ϣ��ֳ�����ֻ�ܹ�2 4 6 8�Ⱥ�
									2 ԤԼ�ź��ֳ����˺�������,���ֳ���ԤԼ���˶��������ȵ�
									3 ��ԤԼ����ԤԼ���ã���ԤԼ�����ֳ��ţ���ԤԼ����ԤԼ�ţ���ģʽ��
						*/	
		
declare @jzxh ut_xh12,
		@czym ut_mc64,
		@yydj ut_bz,
        @fpbz ut_bz,		---0��ӡ1����ӡ
        @config0220 ut_bz	--1�����źͿ��Ҵ����췢Ʊģʽ0��ͳģʽ
        ,@fpdybz	ut_bz
        ,@jhxh ut_xh12 --�Ӻ����
        ,@fpdm varchar(16)
        ,@config1457 char(2)
        ,@config1524 char(2)
		,@config1582 char(4)
		,@yjxh       varchar(20)
		,@linksvr    varchar(100) --���ӷ�����
		,@sql        varchar(2000)

select @czym=name from czryk where id=@czyh
select @yydj=c.yydj from YY_KSBMK k(nolock),YY_JBCONFIG c(nolock) 
            where k.id= @ksdm and k.yydm=c.id 
select @yydj = isnull(@yydj,1)

select @hxfs_new = -1
select @hxfs_new = config from YY_CONFIG (nolock) where id = '1235'
if @hxfs_new is null
    select @hxfs_new = 0

declare @iskmgh ut_bz
select @iskmgh= case  when config= '��' then 1 else 0 end  from YY_CONFIG (nolock) where id='1319'   
select @iskmgh=ISNULL(@iskmgh,0)

if (select config from YY_CONFIG (nolock) where id='0220')='��'      
	select @config0220=1      
else      
	select @config0220=0   
if exists (select 1 from YY_CONFIG(nolock) where id='1457' and config='��')
    select @config1457='��'
else
    select @config1457='��'
   
if exists (select 1 from YY_CONFIG(nolock) where id='1524' and config='��')
    select @config1524='��'
else
    select @config1524='��'  

if exists (select 1 from YY_CONFIG(nolock) where id='1582' and config='��')
    select @config1582='��'
else
    select @config1582='��'  
	 
select @now=convert(char(8),getdate(),112)+convert(char(8),getdate(),8),
	@zje=0, @zfyje=0, @yhje=0, @ybje=0,
	@sfje=0, @sfje1=0, @srje=0, @sfje2=0, 
	@xmzfbl=0, @xmce=0, @print=0, @ghhx=0,
	@qkbz1=0, @qkje=0, @yjbz=0, @yjye=0, @ghf=0, @zlf=0, @qrbz=0
	,@sjyjye=0,@sjdjje=0,@cardbz=0,@kdm='',@kmc='',@cardno='',@tcljbz=0, @tcljje=0, @gyfpbz=0, @gbje=0
	,@ybzfje = 0,@gbbz = '0',@jmje=0,@hxfs=0,@maxyys=0,@fpbz =0,@ghjzsj='',@fpdybz=0,@fpdm=''

select @ghhx=ghhx from GH_GHZDK where jssjh=@sjh
--add by ozb 2007-11-08 ��ӡģʽ
if exists(select 1 from YY_CONFIG(nolock) where id='1117' and config='��')
	select @nconfigdyms=1
else
	select @nconfigdyms=0
--�Ƿ�ʹ�ô��ҿ�����
if (select config from YY_CONFIG (nolock) where id='1067')='��'
	select   @cardbz=0
else
 	select   @cardbz=1

if (select config from YY_CONFIG (nolock) where id='1086')='��'
	select @yyjlbz = 1
else
	select @yyjlbz = 0

if (select config from YY_CONFIG (nolock) where id='2135')='��'
	select @gyfpbz = 1
else
	select @gyfpbz = 0

if (select config from YY_CONFIG (nolock) where id='1128')='��'
	select @hxfs = 1
else
	select @hxfs = 0
--if (select config from YY_CONFIG (nolock) where id='1171')='��'
--    select @fsdgh=1
--else
--    select @fsdgh=0


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



if @ghbz=2 --��ʽ�ݽ�
begin
	select @ybdm=a.ybdm, @qkbz1=qkbz, @qkje=qkje, @zje=zje, @sfje2=zfje, @zfyje=zfyje, @qrbz=qrbz, 
		@tcljbz=tcljbz, @tcljje=zje-zfyje, @pzlx=b.pzlx,@zhdcsx = b.zhdcsx, @gbje=gbje
		,@gbbz = a.gbbz,@ybzfje = (a.zfje - a.srje-a.zfyje),@jmje=jmje,@fpdybz=fpdybz
		from SF_BRJSK a(nolock),YY_YBFLK b(nolock) where sjh=@sjh and a.ybdm=b.ybdm
	if @@rowcount=0
	begin
		select "F","�ùҺŽ����¼�����ڣ�"
		return
	end

	--ʹ�������ӿڲ���ӡ�Һŷ�Ʊ�����շѴ��в����� zwj 2008-10-13
	if (select config from YY_CONFIG (nolock) where id='1156')='��'
		select @qkbz1=3,@qkje=0,@print=1,@fpdybz=1

	--Ƿ��˵ļ��˽��Ҫ��ӡ����
	if @qkbz1 = 1
		select @qkje2 = isnull(je,0) from SF_JEMXK(nolock) where jssjh = @sjh and lx = '01' 
	else 
		select @qkje2 = @qkje
	if @qkbz=2 --and (select zhbz from YY_YBFLK (nolock) where ybdm=@ybdm)<>'2'   --�Ƿ�Ƿ��--wudong
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

	select @jzxh=case when isnull(zcbz,0)=0 then xh else isnull(zkxh,xh) end ,
           @fpbz =isnull(fpbz,0)
		from YY_JZBRK(nolock) where patid=@patid and jlzt=0
	if @@rowcount=0
    begin
		select @yjye=0
        select @fpbz=0
    end
	else
	begin
		select @yjye=yjye from YY_JZBRK(nolock) where xh=@jzxh and jlzt=0
		if @@rowcount=0
			select @yjye=0
		else
			select @yjbz=1
	end	
    if (select config from YY_CONFIG (nolock) where id='0133')='��' and @fpbz ='1'
		select @print=2
	
	if @qkbz=2
		select @qkbz1=2, @qkje=@sfje2

	select xh, ksdm, ksmc, ysdm, ysmc, ghlb, lybz, yyxh, blh, patid, hzxm, czrq, czyh,ghysdm,ghksdm,pbmxxh,ghrq
		into #ghzd
		from GH_GHZDK where jssjh=@sjh
	if @@rowcount=0
	begin
		select "F","�Һ���Ϣ�����ڣ�"
		return		
	end

	if (select config from YY_CONFIG (nolock) where id='1006')='��' --�Һŵ��ݴ�ӡ
		select @print=1

	if not exists(select 1 from #ghzd where ghlb not in (5,8)) 
		and (select config from YY_CONFIG (nolock) where id='1031')='��' --�����Ƿ��ӡ��Ʊ
		select @print=1

	--if @qrbz in (1,3)
	if @fpdybz = 1
		select @print=1

   --add by gxf 2007-6-7 
     declare @bdyfplb varchar(128)
	 select @bdyfplb = config from YY_CONFIG where id = '1141'
	 if @sfje2 = 0 
			if charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+@bdyfplb+',')>0 
				select @print=1,@fpdybz=1--@qrbz=1
	--add by gxf 2007-6-7
	
	--add by liuchun ����185446  20131126 begin
	--declare @config1314 varchar(10)
	--select @config1314 = config from YY_CONFIG where id = '1314'
	--if (select config from YY_CONFIG (nolock) where id='1006')='��' and @print in (0,3) --�Һŵ��ݴ�ӡ
	--begin
	--	if @config1314 = '��'
	--	begin
	--		select @print = 1
	--	end
	--end
	--add by liuchun ����185446  20131126 end
	--add by yhw for 4117 �ϴ�ӡģʽ�²���ӡ��Ʊ��ҽ������  20140916 begin
    declare @bdyfpybdm varchar(128)
	select @bdyfpybdm = config from YY_CONFIG where id = '1362'	
	if @nconfigdyms=0  
	begin
		if charindex(',' + LTrim(RTrim(@ybdm)) + ',',','+@bdyfpybdm+',')>0
			select @print = 1
	end
	--add by yhw for 4117   20140916 end 
	if @config1457='��' and @cardxh<>0 and @cardje<>0			--moved by YC for bug78577
	begin
	    select @print=1
	end 
		
	--add by ozb begin 2007-11-11 ���ݴ洢���̷��صĴ�ӡ��־�����Ƿ��ӡ
	if @nconfigdyms=1
	begin
        if exists(select 1 from YY_CONFIG where id='1124' and config='��')
			if @lybz = 2  --2010-11-05 by sdb �������Ų���Ʊ������Ӧ���Ǵ�ӡ��
            begin
				select @print=1		--2007-12-03 del by ozb��ģʽ�£����������߷�Ʊ�����ڴ�ӡ��Ʊʱ�߷�Ʊ
									--2010-07-06 modify by gwh �����ҺŻ��� ����ӡ��Ʊ
            end
            else
                select @print = 3
        else
            select @print=3
             
		/*
		exec usp_gh_getfpprintflag @sjh,0,@errmsg output
		if left(@errmsg,1)='F'
		begin 
			select 'F',substring(@errmsg,2,49)
			return
		end	
		else
			select @print= case cast(substring(@errmsg,2,49) as int) when 0 then 1 else 0 end
		*/
	end
	--add by ozb end 2007-11-11 ���ݴ洢���̷��صĴ�ӡ��־�����Ƿ��ӡ
	/*if @config1457='��' and @cardxh<>0 and @cardje<>0			--modified by YC for bug78577
	begin
	    select @print=1
	end */
	if @print=0
	begin
		if @gyfpbz=0
		begin
			if @config0220=1
			select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=0 and ksdm=@ghksdm
			else
			select @fph=fpxz, @fpjxh=xh,@fpdm=isnull(fpdm,'') from SF_FPDJK(nolock) where lyry=@czyh and jlzt=1 and xtlb=0
			if @@rowcount=0
			begin
				select "F","û�п��÷�Ʊ��"
				return
			end
		end
		else
		begin
			select @fph=fpxz, @fpjxh=0,@fpdm=isnull(fpdm,'') from SF_GYFPK(nolock) where czyh=@czyh and xtlb=0
			if @@rowcount=0
			begin
				select "F","û�п��÷�Ʊ��"
				return
			end
		end
	end
	else 
		select @fph=null, @fpjxh=null,@fpdm=''

	begin tran

	if exists(select 1 from #ghzd where lybz=1)
	begin
		update GH_GHYYK set jlzt=1 
		where exists(select 1 from #ghzd a where a.lybz=1 and GH_GHYYK.xh=a.yyxh)

		if @@error<>0
		begin
			select "F","���¹Һ�ԤԼ��Ϣ����"
			rollback tran
			return
		end
	end
	
	/*����Һŷ���*/
	declare cs_ghzd cursor for 
	select xh, ksdm, ysdm, ghlb, yyxh,pbmxxh,ghrq from #ghzd
	for read only

	open cs_ghzd
	fetch cs_ghzd into @ghzdxh, @ksdm, @ysdm, @ghlb, @yyxh,@pbmxxh,@ghrq
	while @@fetch_status=0
	begin
		--mod by ozb 20060524 begin ���ٺ����ظ��Ŀ�����
		--del
		--if @ghlb in (2,3,6,9) --ר����
		--	select @ghhx=isnull(count(ghhx),0) from GH_GHZDK
		--	where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6,9) and isnull(gfbz,0)<>1
		--else if @ghlb<>9
		--	select @ghhx=isnull(count(ghhx),0) from GH_GHZDK
		--	where ghrq like substring(@now,1,8)+'%' and ghksdm=@ksdm and jlzt in (0,1) and ghlb not in (2,3,6,9)
		--add
--modify by wfy 2007-04-24 �ô洢����ȡghhx
-- 		if @ghlb in (2,3,6,9) --ר����
-- 		begin
-- 			if @hxfs=0
-- 			begin
-- 				select @ghhx=isnull(max(ghhx),0) from GH_GHZDK (nolock)
-- 				where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6,9) and isnull(gfbz,0)<>1
-- 			end
-- 			else
-- 			begin
-- 				if @ghlb=9
-- 				begin
-- 					select @ghhx=count(a.xh) from GH_GHYYK a
-- 					where  a.yyrq=substring(@now,1,8) and a.ysdm=@ysdm and a.xh<@yyxh
-- 				end
-- 				else
-- 				begin
-- 					select @maxyys=count(a.xh) from GH_GHYYK a
-- 						where  a.yyrq=substring(@now,1,8) and a.ysdm=@ysdm
-- 					select @ghhx=isnull(max(ghhx),@maxyys) from GH_GHZDK (nolock)
-- 					where ghrq like substring(@now,1,8)+'%' and ghysdm=@ysdm and jlzt in (0,1) and ghlb in (2,3,6) and isnull(gfbz,0)<>1
-- 				end
-- 				
-- 			end
-- 		end
-- 		else if @ghlb<>9
-- 				select @ghhx=isnull(max(ghhx),0) from GH_GHZDK (nolock)
-- 				where ghrq like substring(@now,1,8)+'%' and ghksdm=@ksdm and jlzt in (0,1) and ghlb not in (2,3,6,9)
		
		--mod by ozb 20060524 end ���ٺ����ظ��Ŀ����� 
		select @errmsg='F',@ntry=0,@ghjzsj=''
		if @hcsjh=''  --�Է�תҽ���ɷ�ʱ,���������ɹҺź���
		begin
		  if @ghhx=0
		  begin
			  while left(@errmsg,1)='F' and @ntry<3	
			  begin
				  select @errmsg='',@ntry=@ntry+1
				  if @ntry>1 --ozb �ӳ����ʱ�䣬���0.1�룬�п�����ɸ����ص�ӵ�£��д�ʵ��֤��������������ӳ�һ�����ʱ�䣬���������̶�����Ի��������
				  begin
					  select @delaytime='00:00:00.0'+cast(cast(rand()*100 as int) as varchar(2))
					  waitfor delay @delaytime
					  if @@error<>0	--��ֹ���ϳ����޸ĺ�ʱ���ʽ����ȷ
					  begin
						  rollback tran
						  deallocate cs_ghzd
						  select "F","�������ʱ����"
						  return		
					  end
				  end        		
				if exists(select 1 from YY_CONFIG(nolock) where id='1373' and config = '��')  
				begin
					exec usp_pb_operateghhx 0,@pbmxxh,0,0,0,@yyxh,@ghhx output ,@errmsg output 
				end
				else
				begin     		
					exec usp_gh_getghhx @ghlb,@ksdm,@ysdm,0,@yyxh,@ghhx output,@errmsg output,@pbmxxh
				end
				  if @@error<>0 
				  begin
					  rollback tran
					  deallocate cs_ghzd
					  select "F","�������ʱ����"
					  return		
				  end
			  end
			  if left(@errmsg,1)='F'
			  begin
				  rollback tran
				  deallocate cs_ghzd
				  select "F","�������ʱ����"+substring(@errmsg,2,49)
				  return	
			  end
			  select @errmsg=''
		  end
		end
		--select @ghhx=isnull(@ghhx,0)+1

		if @ghlb = 9 and @yyjlbz = 1
		update GH_GHYYK
		set jlzt = 1
		where jlzt = 0 and xh =@yyxh
		if @@error<>0
		begin
			select "F","����Һ�ԤԼ��Ϣ����"
			rollback tran
			deallocate cs_ghzd
			return		
		end	
		if @pzlx in('10','11')
			select @ghrq=(case when @jsrq='' then @now else @jsrq end)
		--�°�ԤԼ�Һ�ʹ��aorigele 20100730
		--����GH_GHZDK @pbmxxh ������
		--�Է�תҽ���ɷ�ʱ,���������ɹҺź���
		declare @pbkm_temp varchar(20)
		if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '��') and (@pbmxxh > 0) and (@hcsjh='') and (@iskmgh=0) and (@ghhx=0)
		begin 
			--s_yh  �Ƶ���ȡ����֮�� for bug 76393
			--if (substring(@ghrq,1,8)<>substring(@now,1,8))   
			--begin    
			--	declare @zxrq_temp varchar(8),@kssj_temp varchar(8)     
			--	select @zxrq_temp=zxrq,@kssj_temp=kssj from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh  s_yh 			
			--	if substring(@now,1,8)<>@zxrq_temp       
			--	begin  
			--		select @ghrq=@zxrq_temp+@kssj_temp+':00'   
			--	end  
			--end	         
			select @hxlx = -1;
			select @pbkm_temp = a.pbkm from GH_YY_PBZB a(nolock),GH_YY_PBZBMX b(nolock) 
				where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh
			begin
				exec usp_gh_getNewghhx @pbmxxh,0,0,@ghhx output ,@errmsg output,@hxfs_new,@ghlb,@ghhx,@yyxh	 --�����ɺ���
			    
				if isnull(@ghhx,0) <=0
				begin
					select "F","��ȡ�Һź������"
					rollback tran
					deallocate cs_ghzd
					return	
				end; 
				if left(@errmsg,1)='F'
				begin
					select "F",@errmsg
					rollback tran
					deallocate cs_ghzd
					return	
				end
				if @hxfs_new = '1'
				begin
					if @ghlb = 9 
						select @hxlx = 1
					else 
						select @hxlx = 0
					
					select @pbmxxh = a.pbmxid from GH_YY_PBZBMX_HX  a(nolock),GH_YY_PBZBMX b(nolock)  
						where a.pbmxid=b.pbmxid and b.pbkm=@pbkm_temp and b.zxrq = substring(@now,1,8) 
							and hxlx = @hxlx and ghhx = @ghhx and a.jlzt <> 2 --alter by ylj 20110819 
					--select @pbmxxh 
				end
			end;
			--s_yh for bug 76393		
			if (substring(@ghrq,1,8)<>substring(@now,1,8))   
			begin    
				declare @zxrq_temp varchar(8),@kssj_temp varchar(8)     
				--select @zxrq_temp=zxrq,@kssj_temp=kssj from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh  s_yh 
				select @zxrq_temp=zxrq from GH_YY_PBZBMX(nolock) where pbmxid=@pbmxxh    
				select @kssj_temp=hxsjd from GH_YY_PBZBMX_HX(nolock) where pbmxid=@pbmxxh and ghhx = @ghhx 
				if substring(@now,1,8)<>@zxrq_temp       
				begin  
					select @ghrq=@zxrq_temp+@kssj_temp+':00'   
				end  
			end	   
		end;
		else if (@iskmgh=1) and  (@pbmxxh > 0) and (@hcsjh='') and (@ghhx=0)--yxc
		begin			 
			select @hxlx = -1;
			select @pbkm_temp = a.pbkm from GH_KM_PBZB a(nolock),GH_KM_PBZBMX b(nolock) 
				where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh
			begin
				exec usp_gh_getNewghhx_km @pbmxxh,0,0,@ghhx output ,@errmsg output,@hxfs_new,@ghlb,@ghhx,@yyxh,@updatehx=1	 --�����ɺ���
			    
				if isnull(@ghhx,0) <=0
				begin
					select "F","��ȡ�Һź������"
					rollback tran
					deallocate cs_ghzd
					return	
				end; 
				if @hxfs_new = '1'
				begin
					if @ghlb = 9 
						select @hxlx = 1
					else 
						select @hxlx = 0
					
					select @pbmxxh = a.pbmxid from GH_KM_PBZBMX_HX  a(nolock),GH_KM_PBZBMX b(nolock)  
						where a.pbmxid=b.pbmxid and b.pbkm=@pbkm_temp and b.zxrq = substring(@now,1,8) 
							and hxlx = @hxlx and ghhx = @ghhx and a.jlzt <> 2 --alter by ylj 20110819 
				end
			end;		
		end
		--add by aorigele
		if exists(select 1 from YY_CONFIG(nolock) where id='1373' and config = '��')  
		begin
			exec usp_pb_operateghhx 1,@pbmxxh,0,0,0,@yyxh,@ghhx output ,@errmsg output
			if substring(@errmsg,1,1)='F'
			begin
				select "F",@errmsg
				rollback tran
				deallocate cs_ghzd
				return
			end
			--����ԤԼ���־
			--if @ghlb in (9)
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1 where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","����ԤԼ���־ʧ�ܣ���˶�ԤԼ��ϢGH_SH_GHYYK��"
					return
				end;
			end;
		end
		if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '��') and (@pbmxxh > 0) and (@iskmgh=0)
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx=@ghhx,
				czyh=@czyh,
				czrq=@now,
				gfbz = 0,
				--@ghlb smallint,		
				--�Һ����0=��ͨ��1=���2=ר�ң�3=����ר�ң�4=����Һţ�5=���6=����Һ�,7=��Һŷ�, 8=��ѹҺ�
				ghjzsj=@ghjzsj
				where xh=@ghzdxh 
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","���¹Һ��˵���Ϣ����"
				return
			end
			
		    -- add by aorigele 20111025
		    if exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '��')
				and @hxfs_new =2  --����5����������û�з������
		    begin
				--���¹��򣺵�ǰҽ���������Ű��л�ȡ������У�Ȼ�����ָ�������״̬
				--��ȡ��ǰ�����Ӧ��pbmxid					
				declare @pbmxidnew ut_xh12,@pbkm varchar(20),@bmkmlx ut_bz
				 select @pbmxidnew = -1;
				select @pbkm = a.pbkm ,@bmkmlx = a.pbkmlx 
				  from GH_YY_PBZB a(nolock) ,GH_YY_PBZBMX b(nolock)
				 where a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh	
				 
				 								
				select top 1 @pbmxidnew = c.pbmxid
				     from GH_YY_PBZB a(nolock) ,GH_YY_PBZBMX b(nolock),GH_YY_PBZBMX_HX c(nolock)
					where a.pbzbid = b.pbzbid and b.pbmxid = c.pbmxid 
					  and a.pbkm = @pbkm
					  and a.pbkmlx = @bmkmlx
					  and b.zxrq = substring(@now,1,8) 
					  and c.ghhx = @ghhx	
				exec usp_gh_setyyghhx @pbmxidnew,@ghhx,-1,2,0,@errmsg output
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;						  				   			      	
		    end
		    else if exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '��')
				and @hxfs_new =4  --����6����������û�з������ ��������ģʽ
			begin
				if @ghlb in (9) --��ͨ���߹����棬���Բ���
				begin
					select @pbmxxh = zjxh from GH_SH_GHYYK b(nolock)where b.xh = @yyxh
					--��ǰʱ����� ��־һ������ ���þͿ���
					update GH_YY_PBZBMX_HX 
					   set jlzt = 2
					 where pbmxid = @pbmxxh 
					   and ghhx in
					   ( select MIN(a.ghhx)
						  from GH_YY_PBZBMX_HX  a(nolock) ,GH_SH_GHYYK b(nolock)
						 where a.pbmxid = b.zjxh							   
						   and b.xh = @yyxh
						   --��ǰʱ�����
						   and a.sjd = b.Child_sjd
						   --���ú���
						   and a.jlzt in (0,1)							   
					   )
					if @errmsg <> 'T'
					begin
						rollback tran
						deallocate cs_ghzd
						select "F",@errmsg
						return
					end;
					update GH_GHZDK 
					   set gfbz = (select top 1 a.pbkmlx from GH_YY_PBZB a(nolock),GH_YY_PBZBMX b(nolock) where 
					                   a.pbzbid = b.pbzbid and b.pbmxid = @pbmxxh	
					               )						
					 where xh=@ghzdxh 
					if @@error<>0
					begin
						rollback tran
						deallocate cs_ghzd
						select "F","���¹Һ��˵���Ϣ����"
						return
					end					   
				end 
			end
		    else if (exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '��')
				and @hxfs_new =3 ) --����4����ģʽ���� ����ģʽ��
			begin
				if @ghlb in (9) --��ͨ���߹�����
				begin
					--ֻ����ԤԼ����				
					select @hxlx = -1;
					select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
					--jlzt = 2 �Һ���ȷ��
					exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output
					if @errmsg <> 'T'
					begin
						rollback tran
						deallocate cs_ghzd
						select "F",@errmsg
						return
					end;
				end;
			end
			else if (exists(select 1 from YY_CONFIG (nolock) where id='1024' and config = '��')
				and @hxfs_new =5 ) --����5,����ģʽ�����ӹҺ���ŵĴ��룬���ߺ�ʱ���·���������
			begin
				select @hxlx = -1;
				select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
				exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;
			end
			else
		    begin
				select @hxlx = -1;
				select @hxlx = hxlx from GH_YY_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
				--jlzt = 2 �Һ���ȷ��
				exec usp_gh_setyyghhx @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;
			end;			
			--����ԤԼ���־
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1
				where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","����ԤԼ���־ʧ�ܣ���˶�ԤԼ��ϢGH_SH_GHYYK��"
					return
				end;
			end;
		end
		else if (@pbmxxh > 0) and (@iskmgh = 1)
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx= @ghhx ,
				czyh=@czyh,
				czrq=@now,			
				ghjzsj=@ghjzsj,
				fzghhx=case when isnull(fzghhx,'')='' then convert(char(4),@ghhx) else fzghhx end
				from GH_GHZDK  
				where @ghzdxh=xh
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","���¹Һ��˵���Ϣ����"
				return
			end	
			
			select @hxlx = -1;
			select @hxlx = hxlx from GH_KM_PBZBMX_HX c(nolock) where pbmxid = @pbmxxh and ghhx = @ghhx
			--jlzt = 2 �Һ���ȷ��
			exec usp_gh_setyyghhx_km @pbmxxh,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
			if @errmsg <> 'T'
			begin
				rollback tran
				deallocate cs_ghzd
				select "F",@errmsg
				return
			end;
			
			select @jhxh=a.xh from GH_KM_JHXXK a(nolock) where a.patid=@patid and a.pbmxid=@pbmxxh and 
			a.jlzt=0 and substring(a.jhrq,1,8)=substring(@now,1,8)
			if @@rowcount = 1
			begin
				update GH_KM_JHXXK set jlzt=1 where xh=@jhxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					select "F","����GH_KM_JHXXK��־ʧ�ܣ���˶ԼӺ���Ϣ��"
					return
				end;								
			end
			--����ԤԼ���־
			if @yyxh>0
			begin
				update GH_SH_GHYYK SET jlzt = 1
				where xh = @yyxh
				if (@@error <>0) or (@@rowcount = 0)
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","����ԤԼ���־ʧ�ܣ���˶�ԤԼ��ϢGH_SH_GHYYK��"
					return
				end;
			end;					
		end
		else --��ģʽ
		begin
			update GH_GHZDK set jlzt=0,
				ghrq=@ghrq,
				ghhx=(case when isnull(b.gfbz,0)=1 then b.yyxh else @ghhx end),
				czyh=@czyh,
				czrq=@now,
				gfbz = isnull(b.gfbz,0),
				ghjzsj=@ghjzsj
				from GH_GHZDK a left join GH_GHYYK b(nolock) on a.yyxh=b.xh
				where a.xh=@ghzdxh  --sql2012 yfq				
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","���¹Һ��˵���Ϣ����"
				return
			end
			if exists(select 1 from YY_CONFIG (nolock) where id='1188' and config = '��') and (@hxfs_new=6) 
			begin
				exec usp_gh_setyyghhx -1,@ghhx,@hxlx,2,0,@errmsg output,@ghzdxh
				if @errmsg <> 'T'
				begin
					rollback tran
					deallocate cs_ghzd
					select "F",@errmsg
					return
				end;			
			end			
		end;
		
		--��fzghhxΪ�յĸ���Ϊ�Һź���
		update GH_GHZDK set 
			fzghhx=case when isnull(fzghhx,0)=0 then convert(char(4),ghhx) else fzghhx end
		where @ghzdxh=xh
		if @@error<>0
		begin
			rollback tran
			deallocate cs_ghzd
			select "F","���¹Һ��˵���Ϣ����"
			return
		end	
			
		--add by sdb 2011-04-25 �����Һ����Ƿ��Զ��ж�����ר�Ҵ��ڳ���͸���ҺŷѵĲ���(�Ϻ���ҽ��һ����û������ҽ���������) BEGIN
		if (select config from YY_CONFIG (nolock) where id = '1219') = '��'
		begin
			if exists (select 1 from GH_ZYY_ZJGHFSZ where id = @ysdm and jlzt = 0 and isnull(ghf,'') <> '')
			begin--�������ר������Ѹ����Һ���Ϣ�嵽GH_GHZDK_MZ��
				insert into GH_GHZDK_MZ(jssjh,patid,ghrq,ysdm,ysmc) 
				select jssjh,patid,ghrq,ysdm,ysmc from GH_GHZDK a where a.xh=@ghzdxh
						--and a.jssjh not in(select jssjh from GH_GHZDK_MZ)
				if @@error<>0
				begin
					rollback tran
					deallocate cs_ghzd
					select "F","���¹Һ��˵���Ϣ����"
					return
				end
			end
		end		
		--add by sdb 2011-04-25 �����Һ����Ƿ��Զ��ж�����ר�Ҵ��ڳ���͸���ҺŷѵĲ��� END
		
		if @ghlb=1 
		begin
			insert into SF_JZDJK(ghxh,patid,cardno,sex,birth,ybdm,lxdz,dwbm,dwmc,ksdm)
			select b.xh,a.patid,a.cardno,a.sex,a.birth,a.ybdm,a.lxdz,a.dwbm,a.dwmc,b.ksdm
			from SF_BRXXK a(nolock),GH_GHZDK b(nolock) where a.patid=b.patid and b.xh=@ghzdxh
			if @@error<>0
			begin
				rollback tran
				deallocate cs_ghzd
				select "F","���뼱��Ǽǿ�ʧ��!"
				return
			end
		end
		fetch cs_ghzd into @ghzdxh, @ksdm, @ysdm, @ghlb, @yyxh,@pbmxxh,@ghrq
	end
	close cs_ghzd
	deallocate cs_ghzd
	/*****
	 @qkbz1=1 :1��ͨ���˻��Ĳ��� 2����������ʱ�˻�����,������û�����,�˻����
				����YY_YBFLK �е�zhdcsx �����õ�ֵ
	******/ 
	update SF_BRXXK set zjrq=substring(@now,1,8), zhje=zhje-(case when @qkbz1=1 then @qkje2 else 0 end), ghbz=1,
		ylxm=(case when @pzlx='11' then @xmlb else ylxm end),
		ljje=ljje+(case when @tcljbz=1 then @tcljje else 0 end),
    gxrq=@now --add by yfq @20120531 
		where patid=@patid
	if @@error<>0
	begin
		select "F","���²�����Ϣ����"
		rollback tran
		return
	end

	if @yjbz=1 and @qkbz1=3
	begin
		update YY_JZBRK set yjye=yjye-@qkje 
		,@sjyjye=yjye-@qkje,@sjdjje=djje		--mit ,2oo3-11-19 , add
		where xh=@jzxh and jlzt=0
		if @@error<>0
		begin
			select "F","���¼��˲��˿�Ԥ����������"
			rollback tran
			return
		end
		
		insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh)
		values(0,0,@jzxh,@czyh,@czym,@now,@qkje,0,@sjyjye,1,3,null,0,'',@sjh)
		if @@error<>0
		begin
			select 'F','����YY_JZBRYJK��¼ʱ����'
			rollback tran
			return
		end
	end

	select @zhje=zhje from SF_BRXXK where patid=@patid
	if @@error<>0
	begin
		select "F","�����˻�������"
		rollback tran
		return
	end


	--���ҿ����˴��� add by chenwei 2003.12.06
	if @cardbz = 1 
	begin
		if (@cardxh <> 0) or (@cardje <> 0)
		begin
			select @qkbz1 = '4', @qkje=@cardje
			
			update YY_CARDXXK set yjye=yjye-@cardje,zjrq=(case when @jsrq='' then @now else @jsrq end),
								  @sjyjye=yjye-@qkje,@sjdjje = @qkje
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
			values(@cardxh,@sjh,0,@kdm,@czyh,@czym,(case when @jsrq='' then @now else @jsrq end),@qkje,@zhje,@yhje,0,0,0,0,'')
			if @@error<>0
			begin
				rollback tran
				select "F","���´��ҿ��������"
				return
			end

			select @sfje2=@sfje2+@qkje

		end
	end
	--�����������˻��������,��Ϊ�˻�����ʱ��,���������ĸ���,������Ϊ��
	if @ghlb=3  
	begin  
		update SF_BRXXK set zhje =0,gxrq=@now where patid=@patid  --add by yfq @20120531  
		select @zhje=0  
	end
	
	--l__zy add by bug279761 �ֽ�֧�����ϼƵ�zpje��
	if @zph='1' 
	begin
	    select @zph='',@zpje=0
	end
	
	update SF_BRJSK set sfrq=(case when @jsrq='' then @now else @jsrq end),
		ybjszt=2,
		zxlsh=(case isnull(@zxlsh,'') when '' then zxlsh else @zxlsh end),
		fph=@fph,
		fpjxh=@fpjxh,
		pzh=(case when @qkbz1 = 4 then @cardno else pzh end),
		xjje=zfje-@zpje-@qkje-isnull(@ylkje,0)-@gbje-@bdyhkje, --modiby by chenwei 2003.12.22 
		qkbz=@qkbz1,
		qkje=@qkje,
		dnzhye=(case when @qkbz1=1 then @zhje when @qkbz1=4 then @sjyjye else dnzhye end)
		,ylkje=@ylkje
		,ylkysje=@ylkysje
		,ylksqxh=@ylksqxh
		,ylkzxlsh=@ylkzxlsh
		,ylcardno=@ylcardno	--mit ,, 2003-05-05 ,, �����������ֶ�
		,qrrq=(case when @fpdybz=0 then @now else null end)	--tony ҽ�������޸�
		,qrczyh=(case when @fpdybz=0 then @czyh else null end)
		,zpje=isnull(@zpje,0)
		,zph=@zph
		,bdyhkje = @bdyhkje
		,bdyhklsh = @bdyhklsh
		,zlje=@zlje
		,lrrq=@now --�Ż����̱���ר��
		,gxrq=@now --add by yfq @20120528
		--,qrbz=case when @qrbz<>qrbz then @qrbz else qrbz end
		,fpdybz=@fpdybz
		where sjh=@sjh
	if @@error<>0 or @@rowcount=0
	begin
		select "F","���¹ҺŽ�����Ϣ����"
		rollback tran
		return
	end
	IF @nconfigdyms= 0--�Ϸ�Ʊģʽ�£���Ʊ��ӡ��Ϣ����
	begin
		if exists(select 1 from YY_CONFIG(nolock) where id = '1056' and config = '��')
			and exists(select 1 from SF_BRJSK(nolock) where sjh = @sjh and zfje-@zpje-@qkje-isnull(@ylkje,0)-@gbje-@bdyhkje=0
			and @qkje > 0 and @qkbz1=3)
			select @print=1
		if  @print = 0
		begin
			update SF_BRJSK SET fpdybz = 0,fpdyczyh=@czyh,fpdyrq=@now where sjh = @sjh
			if @@error<>0 or @@rowcount=0
			begin
				select "F","���¹Һŷ�Ʊ��Ϣ����"
				rollback tran
				return
			end
		end
		else
		begin
			update SF_BRJSK SET fpdybz = 1,fpdyczyh=null,fpdyrq=null,fph=null,fpjxh=null,@fph=null,@fpjxh=null where sjh = @sjh
			if @@error<>0 or @@rowcount=0
			begin
				select "F","���¹Һŷ�Ʊ��Ϣ����"
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
	--mit ,, 2003-05-05 ,, �����Ԥ�������YY_YLJYJLK
	if @ylkysje>0
	begin
		insert into YY_YLJYJLK(ylcardno,patid,ylkje,jyrq,ylksqxh,ylkzxlsh,jlzt,qxxh,sjh)
		values(@ylcardno,@patid,@ylkysje,@jsrq,@ylkyssqxh,@ylkyszxlsh,@yslx,null,@sjh)
	end
	if @@error<>0
	begin
		select "F","����������Ԥ����Ϣ����"
		rollback tran
		return
	end

	if @print=0
	begin
		exec usp_yy_gxzsj 0, @czyh, @errmsg output,@gyfpbz,@fpjxh,@ghksdm
		if @errmsg like 'F%'
		begin
			select "F",substring(@errmsg,2,49)
			rollback tran
			return
		end
	end
	--Wxp20070317 �Զ�����ҽԺת�ﴦ�������ԷѺ������Ż�50%
	--������ @zzdjh <> '',�Ƕ�����ҽԺ����ͨ�Һŷѣ���Χ���ƣ��ҺŷѺ����Ʒ�
	if (@zzdjh <> '' and @yydj > 1 and @ghlb in (0,1)) 
	begin
		update GH_ZZBRJLK set jszt = 2  where jssjh = @sjh 
		if @@error<>0
		begin
			select "F","����ת�ﵥ��¼(GH_ZZBRJLK)״̬=2ʱ����"
			rollback tran
			return		
		end
		update GH_ZZBRDJK set jlzt = 1  where zzdjh = @zzdjh and jlzt = 0 
		if @@error<>0
		begin
			select "F","����ת�ﵥ(GH_ZZBRDJK)״̬=1ʱ����"
			rollback tran
			return		
		end
		select @jmje = jmghf+jmzlf from GH_ZZBRJLK where jssjh = @sjh
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
			exec usp_zy_tcljjegl @m_cardno,@mzpatid,@tcljje2,0,0,0,0,@czyh
			if @@error <> 0 
			begin
				rollback tran
				select "F","����YY_BRLJXXK��ͳ���ۼƽ�����"
				return
			end
		end
	end
	IF (SELECT ISNULL(config,"��") FROM YY_CONFIG WHERE id="1137")="��"
	BEGIN
		EXECUTE usp_gh_ghdpjk @sjh, 0
		IF @@error<>0
		BEGIN
			ROLLBACK
			SELECT "F","ȡ�Һ����ݴ���"
			RETURN
		END
	END

	--zyh 20080229 �ⶳԤԼ�ҺŲ����ʻ� sunyu 20080307 �Ѹöδ����usp_gh_ghdj�Ƶ�ex2�У���ʾ�Һųɹ����ٽⶳ
	select @yyxh1=yyxh from #ghzd
	if @yyxh1> 0 
	begin
		select @djje=djje from GH_GHYYK (nolock) where xh=@yyxh1 and djbz=1
		select @djje=isnull(@djje,0)
		if @djje > 0 
		begin
			if exists(select 1 from YY_JZBRK where patid=@patid and djje>@djje)--gxs 20131224 add
			begin
				update YY_JZBRK set djje=djje - @djje where patid=@patid
			end
			else
			begin
				update YY_JZBRK set djje=0 where patid=@patid
			end
			if @@error<>0
			begin
				select "F","ԤԼ���˽ⶳ�ʻ�ʱ����"
				rollback tran
				return		
			end

			update GH_GHYYK set djje=0, djbz=0 where xh=@yyxh1
			if @@error<>0
			begin
				select "F","ԤԼ���˽ⶳ�ʻ�ʱ����"
				rollback tran
				return		
			end
		end
	end
  /*-----�Է�תҽ��ʱ,���¼�¼״̬ begin-----*/
	if @hcsjh<>''
	begin
		declare @ysjh ut_sjh, --ԭ�շѼ�¼sjh
			@ypatid ut_xh12, --ԭ�շѼ�¼patid
			@yjzxh ut_xh12, --ԭ�շѼ�¼��Ӧ��jzxh  
			@yqkje ut_money --ԭ�շѼ�¼��qkje qkbz=3
		select @ysjh=tsjh,@ypatid=patid,@yqkje=isnull(case when qkbz=3 then -qkje else 0 end,0) from SF_BRJSK where sjh=@hcsjh
		update SF_BRJSK set ybjszt=2 where sjh=@hcsjh --���º���¼��ybjszt
		--����ԭ�շѼ�¼,ԭGH_GHZDK��jlzt
		update SF_BRJSK set jlzt=1 where sjh=@ysjh
		if @@rowcount=0
		update SF_NBRJSK set jlzt=1 where sjh=@ysjh
		update GH_GHZDK set jlzt=1 where jssjh=@ysjh
		if @@rowcount=0
		update GH_NGHZDK set jlzt=1 where jssjh=@ysjh
		--���������ɵ��շѼ�¼��tsjh
		update SF_BRJSK set tsjh=@hcsjh where sjh=@sjh
		--����ԭ�շѼ�¼�Ŀۿ����
		if @yqkje<>0
		begin
			select @yjzxh=xh from YY_JZBRK (nolock) where patid=@ypatid and jlzt=0 and gsbz=0 
			select @sjyjye=yjye from YY_JZBRK (nolock) where xh=@yjzxh
			insert into YY_JZBRYJK(fpjxh,fph,jzxh,czyh,czym,lrrq,jje,dje,yje,zffs,czlb,hcxh,jlzt,memo,sjh,ybdm,dbkje)
			select 0,0,@yjzxh,@czyh,@czym,@now,0,@yqkje,@sjyjye+@yqkje,1,4,null,0,'�Է�תҽ�����',@hcsjh,NULL,0
			update YY_JZBRK set yjye=@sjyjye+@yqkje where xh=@yjzxh
			update SF_BRXXK set zhje=@sjyjye+@yqkje where patid=@ypatid
		end
	end
  /*-----�Է�תҽ��ʱ,���¼�¼״̬ end-----*/

	--�����˻����ۿ���¼�ж� bug110015
	declare @qkjlcount int
	select @qkjlcount = 0
	if @yjbz=1 and @qkbz1=3
	begin
		if exists(select 1 from YY_JZBRK nolock where xh = @jzxh and yjye < 0)
		begin
			select 'F','�Һſۿ�����'
			rollback tran
			return	
		end
		select @qkjlcount = count(*) from YY_JZBRYJK nolock where jzxh = @jzxh and czlb = 3 and sjh = @sjh
		if @qkjlcount > 1
		begin
			select 'F','��ǰ�Һ��ѿ۷ѣ���ȷ�ϣ�'
			rollback tran
			return
		end
	end

-- add kcs 20190523 ��ֹǷ���Ƿ�ѱ�־<>2���⵼��©�� >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	if exists(select 1 from SF_BRJSK a(nolock) inner join YY_YBFLK b(nolock) on a.ybdm = b.ybdm where b.zhbz=2 and a.qkbz <>2 and a.sjh = @sjh)
	begin
		select 'F','Ƿ�ѻ��߱���Ƿ�����'
		return
	end
-- add kcs 20190523 ��ֹǷ���Ƿ�ѱ�־<>2���⵼��©�� <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	commit tran
--  20190722 ����ɱ���Ϣ
   exec usp_sf_savegbxx @sjh --����ɱ���Ϣ  &&
--  20190722 ����ɱ���Ϣ


	--��Ϣ����
	if @config1524='��'
	begin
	    exec usp_app_xxts '01',@patid,@ghzdxh,'','','','','',@errmsg output
	end	
	if @config1582='��'
	begin
	    select @yjxh= isnull(yjxh,'0') from GH_GHZDK (nolock) where xh=@ghzdxh
		if  (@yjxh<>'') and (@yjxh<>'0')
		begin
			select @linksvr=config from YY_CONFIG where id='1566' -- @linksvr='testlinksvr.NIS.dbo.'
			select @linksvr=ltrim(rtrim(ISNULL(@linksvr,''))) 
			select @sql='declare @rettype      varchar(200),@errmsg1 varchar(200)  exec '+@linksvr+'usp_his5_jzhs_writebackghbj '+rtrim(convert(varchar(20),@yjxh))+','+@ksdm+',@rettype output,@errmsg1 output' 
			exec(@sql)
		end
	end
	/*
	if @errmsg like "F%"
	begin
		select "F",substring(@errmsg,2,49)
		return
	end
	*/
	
	--if @gbbz = '1' 
	--begin
	--	select 	@ybzfje = sum(je) from SF_JEMXK nolock where lx in ('20','22') and jssjh = @sjh
	--end
	--if (select config from YY_CONFIG (nolock) where id='1025')='��' --��ӡ�Ƿ��÷�Ʊ����
	--begin	
	--	select " T", @zje, @zfyje, convert(varchar(20),@fph), @print, @sfje2-@qkje-@gbje-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,-- 0-6
	--		@tczfje, @dffjzfje, '', @qkbz1, @qkje2  -- 7-11
	--		,@sjyjye ,@sjdjje, @kmc, @cardno, @gbje,@ybzfje,@jmje,@fpdm  --12-17
	--	union all
	--	select fpxmmc, sum(xmje), 0, 0, 0, 0, 0, 0, 0, fpxmdm, 0, 0 ,0,0, '', '', 0,0,0,''
	--		from SF_BRJSMXK where jssjh=@sjh group by fpxmdm, fpxmmc
	--end
	--else 
	--begin
	--	select " T", @zje, @zfyje, convert(varchar(20),@fph), @print, @sfje2-@qkje-@gbje-isnull(@bdyhkje,0), @qfdnzhzfje+@qflnzhzfje+@tclnzhzfje+@fjlnzhzfje,
	--		@tczfje, @dffjzfje, '', @qkbz1, @qkje2
	--		,@sjyjye ,@sjdjje, @kmc, @cardno, @gbje,@ybzfje,@jmje,@fpdm
	--	union all
	--	select dxmmc, xmje, 0, 0, 0, 0, 0, 0, 0, dxmdm, 0, 0 ,0,0, '', '', 0,0,0,''
	--		from SF_BRJSMXK where jssjh=@sjh
	--end		
end

return


