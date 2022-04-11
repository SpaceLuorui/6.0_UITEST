package com.winning.testsuite.workflow;

import java.util.*;

import com.winning.testsuite.workflow.SingleCondition;

import ui.sdk.util.SdkTools;


public class ConditionProcessor {

    public static int PositiveCaseCount=0;   //正向条件集和数
    public static Random random=new Random();
    //单用例条件集
    public class SingleCaseConditionSet implements Cloneable{
        public String ConditionSetType="";   //条件描述
        public ArrayList<SingleCondition> ConditionList=new ArrayList<SingleCondition>();
        SingleCaseConditionSet(){
            ConditionList=new ArrayList<SingleCondition>();
            ConditionSetType="";
        }
        SingleCaseConditionSet(ArrayList<SingleCondition> CL,String type){
            ConditionList=CL;
            ConditionSetType=type;
        }

        @Override
        public Object clone(){
            SingleCaseConditionSet  SCC=null;
            try {
                SCC=(SingleCaseConditionSet)super.clone();
                SCC.ConditionList=new ArrayList<SingleCondition>();
                for(SingleCondition SC:  ConditionList){
                    SingleCondition SC_clone=(SingleCondition)SC.clone();
                    SCC.ConditionList.add(SC_clone);
                }

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return  SCC;
        }
    }

    //传入设定的条件集,生成正向和反向用例
    public  ArrayList<SingleCaseConditionSet> getCasesConditionSet(List<SingleCondition> CL){
//        for(SingleCondition SC : CL){
//            //取最大数为正向用例数
//            if(SC.EffectiveRange.size()>PositiveCaseCount)
//                PositiveCaseCount= SC.EffectiveRange.size();
//        }

        //存放条件集和的集和
        ArrayList<SingleCaseConditionSet> CasesConditionSet= new ArrayList<SingleCaseConditionSet>();

        //生成正向用例
//        for (int tcxh = 0; tcxh < PositiveCaseCount; tcxh++) {
            //单用例条件集
            ArrayList<SingleCondition> SingleConditionSet = new ArrayList<SingleCondition>();
            for (SingleCondition SC : CL) {
                String ConVal_code_Eff="";   //取有效值
                //非随机条件
//                if (tcxh < SC.EffectiveRange.size())     //当前用例序号小于可用范围最大数，并且不是随机条件,依照序号取可用范围内的值
//                {
//                    ConVal_code_Eff = SC.EffectiveRange.get(tcxh);
//                } else {

                    int ra = random.nextInt(SC.EffectiveRange.size());
                    ConVal_code_Eff = SC.EffectiveRange.get(ra);
//                }

                //根据设定值生成单条件
                SingleCondition SC_new=(SingleCondition)SC.clone();
                SC_new.ConVal_code=ConVal_code_Eff;
                //将单条件添加到单用例的条件集
                SingleConditionSet.add(SC_new);

            }
            SingleCaseConditionSet  SCCS= new SingleCaseConditionSet(SingleConditionSet,"正向条件集");
            //将单用例条件集添加到所有用例条件集
            CasesConditionSet.add(SCCS);

//        }



//        生成反向用例
        int i=0;
        if(CasesConditionSet.size()>0)
            for(SingleCondition SC : CL)
            {
                //有些用例，反向集和为空，则不生成
                if(SC.UnEffectiveRange.size()==0)
                    continue;

                //根据传入的条件集和，取一个正向条件集，
                int ran=random.nextInt(CasesConditionSet.size());
                SingleCaseConditionSet  SCCS_ran=(SingleCaseConditionSet)CasesConditionSet.get(ran).clone();

                //在选择的正向集合中根据类别查找
                for(int lc=0;lc<SCCS_ran.ConditionList.size();lc++){
                    //取单条件
                    SingleCondition SCCS_ran_sc =SCCS_ran.ConditionList.get(lc);
                    if(SC.ConType_code.equals(SCCS_ran_sc.ConType_code))
                    {
                        SdkTools.logger.assertFalse(SC.UnEffectiveRange.size()<=0,SC.toString()+"无效集和不能为空");
                        int ung=random.nextInt(SC.UnEffectiveRange.size());
                        SCCS_ran_sc.ConVal_code=SC.UnEffectiveRange.get(ung);
//                    SCCS_ran.ConditionList.get(lc).ConVal_code=SC.UnEffectiveRange.get(ung);
                        SCCS_ran.ConditionSetType="反向条件集-"+String.valueOf(i);
                        CasesConditionSet.add(SCCS_ran);
                        i++;
                        break;
                    }
                }


            }


        return  CasesConditionSet;

    }


}
