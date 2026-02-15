package com.example.fw.batch.message;

/**
 * 
 * フレームワークのメッセージID定数クラス
 *
 */
public final class BatchFrameworkMessageIds {
    private BatchFrameworkMessageIds() {
    }

    // トレースレベル
    // バッチAP実行制御（LogAspect）
    public static final String T_FW_BTCTRL_0001 = "t.fw.btctrl.0001";
    public static final String T_FW_BTCTRL_0002 = "t.fw.btctrl.0002";

    // 情報レベル
    // 非同期AP実行制御
    public static final String I_FW_ASYNCSV_0001 = "i.fw.asyncsv.0001";
    public static final String I_FW_ASYNCSV_0002 = "i.fw.asyncsv.0002";
    public static final String I_FW_ASYNCSV_0003 = "i.fw.asyncsv.0003";
    public static final String I_FW_ASYNCSV_0004 = "i.fw.asyncsv.0004";
    // バッチAP実行制御
    public static final String I_FW_BTCTRL_0001 = "i.fw.btctrl.0001";
    public static final String I_FW_BTCTRL_0002 = "i.fw.btctrl.0002";
    // ジョブフロー実行支援
    public static final String I_FW_JBFLW_0001 = "i.fw.jbflw.0001";

    // 警告レベル
    // 非同期AP実行制御
    public static final String W_FW_ASYNCSV_8001 = "w.fw.asyncsv.8001";
    public static final String W_FW_ASYNCSV_8002 = "w.fw.asyncsv.8002";
    public static final String W_FW_ASYNCSV_8003 = "w.fw.asyncsv.8003";
    public static final String W_FW_ASYNCSV_8004 = "w.fw.asyncsv.8004";
    public static final String W_FW_ASYNCSV_8005 = "w.fw.asyncsv.8005";
    public static final String W_FW_ASYNCSV_8006 = "w.fw.asyncsv.8006";

    // エラーレベル
    // 非同期AP実行制御
    public static final String E_FW_ASYNCSV_9001 = "e.fw.asyncsv.9001";
    public static final String E_FW_ASYNCSV_9002 = "e.fw.asyncsv.9002";
    public static final String E_FW_ASYNCSV_9003 = "e.fw.asyncsv.9003";
    public static final String E_FW_ASYNCSV_9004 = "e.fw.asyncsv.9004";
    // バッチAP実行制御
    public static final String E_FW_BATCH_9001 = "e.fw.batch.9001";
    // ジョブフロー実行支援
    public static final String E_FW_JBFLW_9001 = "e.fw.jbflw.9001";
}
