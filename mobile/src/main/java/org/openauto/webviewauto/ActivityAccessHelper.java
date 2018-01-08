package org.openauto.webviewauto;

public final class ActivityAccessHelper {

    public WebViewAutoActivity activity;
    private static volatile ActivityAccessHelper instance = null;

    private ActivityAccessHelper() {}

    public static ActivityAccessHelper getInstance() {
        if (instance == null) {
            synchronized(ActivityAccessHelper.class) {
                if (instance == null) {
                    instance = new ActivityAccessHelper();
                }
            }
        }
        return instance;
    }
}