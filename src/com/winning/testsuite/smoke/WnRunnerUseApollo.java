package com.winning.testsuite.smoke;

import org.junit.runner.JUnitCore;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.Encounter;
import ui.sdk.util.SdkTools;

public class WnRunnerUseApollo {
    public static void main(String[] args) {
        SdkTools.creatDic(Framework.resultFilePath);
        SdkTools.creatDic(Framework.saveShotPath);
        SdkTools.creatDic(Framework.uiTempDir);

        
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","autoTest");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        if (args.length > 0) {
            Data.suiteName = args[0];
        }

        if (Data.getEncounterFromFile) {
            Encounter.init();
        }

        try {
            JUnitCore.runClasses(Class.forName(Data.suiteName));
        }
        catch (Exception e) {
            System.out.println("执行用例集失败!");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
