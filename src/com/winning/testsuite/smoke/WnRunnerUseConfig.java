package com.winning.testsuite.smoke;

import org.junit.runner.JUnitCore;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.Encounter;
import ui.sdk.util.SdkTools;

public class WnRunnerUseConfig {
    public static void main(String[] args) {
        SdkTools.creatDic(Framework.resultFilePath);
        SdkTools.creatDic(Framework.saveShotPath);
        SdkTools.creatDic(Framework.uiTempDir);
        if (args.length != 0) {
            System.out.println("加载指定配置文件：" + args[0]);
            Config.load(args[0]);
        }
        else {
            System.out.println("加载默认配置文件：config");
            Config.load();
        }
        if (args.length > 1) {
            Data.suiteName = args[1];
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
