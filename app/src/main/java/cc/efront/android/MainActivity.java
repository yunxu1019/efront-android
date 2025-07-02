package cc.efront.android;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowInsets;
import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.provider.Settings;

class CustomWebViewClient extends WebViewClient {
    private int statusBarHeight = 0;

    public void setStatusBarHeight(int height) {
        statusBarHeight = height;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // Adjust padding via JavaScript if setPadding fails
    }
}

public class MainActivity extends Activity {
    private WebView webView;

    void initWebView() {
        if (webView != null) {
            return;
        }
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("efront-android");
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new CustomWebViewClient());
    }

    void setFlags() {
        // android.view.WindowManager.LayoutParams;
        // FLAT_SECURE 0X00002000 禁止截屏录屏
        // FLAG_DIM_BEHIND 0X00000002
        // FLAG_BLUR_BEHIND 0X00000004 // 废弃，7.0+不支持
        // FLAG_NOT_FOCUSABLE 0X00000008
        // FLAG_NOT_TOUCHABLE 0X00000010
        // FLAG_NOT_TOUCH_MODAL 0X00000020
        // FLAG_TRANSLUCENT_STATUS 0X04000000
        // FLAG_TRANSLUCENT_NOVIGATION 0X08000000
        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUDS 0X80000000
        // FLAG_KEEP_SCREEN_ON 0X00000080
        // FLAG_TURN_SCREEN_ON 0X00200000
        // FLAG_SHOW_WHEN_LOCKED 0X00080000
        // FLAG_LAYOUT_INSET_DECOR 0X00010000
        // FLAG_LAYOUT_ATACHED_IN_DECOR 0X40000000
        // FLAT_LAYOUT_IN_SCREEN 0X00000100
        // FLAT_LAYOUT_NO_LIMITS 0X00000200
        // FLAG_FULLSCREEN 0X00000400
        // FLAG_FORCE_NOT_FULLSCREEN 0X00000800
        // FLAG_FORCE_NOT_FULLSCREEN 0X00000800
        // int FLAG_LAYOUT_ATACHED_IN_DECOR = 0X40000000;
        int FLAG_FULLSCREEN = 0X00000400;
        int FLAG_TRANSLUCENT_STATUS = 0X04000000;
        int FLAG_TRANSLUCENT_NOVIGATION = 0X08000000;
        Window w = getWindow();
        int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
        // w.addFlags(FLAG_FULLSCREEN | FLAG_TRANSLUCENT_STATUS |
        // FLAG_TRANSLUCENT_NOVIGATION);
        w.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        layout.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight(getApplicationContext()));
        layout.setBackgroundColor(0xff333333);
        initWebView();
        setFlags();
        layout.addView(webView, params);
        webView.loadUrl("file:///android_asset/index.html");
    }

    int getStatusBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = getWindow().getDecorView().getRootWindowInsets();
            if (insets != null) {
                return insets.getStableInsetTop();
            }
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    boolean hasNavigationBar() {
        Resources resources = getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    boolean hasNavigationBar(Context context) {
        int navigationMode = 0;
        try {
            navigationMode = Settings.Secure.getInt(context.getContentResolver(), "navigation_mode");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        // navigation_mode 的值：
        // 0: 三键导航（有底部悬浮条）
        // 2: 手势导航（通常无底部悬浮条）
        return navigationMode == 0;
    }

    int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar())
            return 0;
        if (!hasNavigationBar(context)) {
            // 不适合底部有操作的页面
            // return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

}
