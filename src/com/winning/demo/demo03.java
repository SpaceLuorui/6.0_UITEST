package com.winning.demo;

import com.winning.user.winex.WINEXTestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

/**
 * 患者挂号登记
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class demo03 extends WINEXTestBase {
    public demo03(){
        super();
    }
    static{
        SdkTools.initReport("HIS", "demo03.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01(){

    }
}
