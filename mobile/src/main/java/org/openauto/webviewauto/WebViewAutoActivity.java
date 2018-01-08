package org.openauto.webviewauto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.apps.auto.sdk.CarActivity;

import org.openauto.webviewauto.fragments.BrowserFragment;
import org.openauto.webviewauto.keyboard.KeyboardHandler;

import java.util.ArrayList;
import java.util.List;

public class WebViewAutoActivity extends CarActivity {

    private enum BrowserInputMode {
        URL_INPUT_MODE, CONTENT_INPUT_MODE
    }

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";
    private String mCurrentFragmentTag;

    public String homeURL = "https://duckduckgo.com";
    public String currentURL = homeURL;
    public BrowserInputMode inputMode = BrowserInputMode.URL_INPUT_MODE;
    public String originalAgentString = "";
    public String currentBrowserMode = "MOBILE";

    public List<String> urlHistory = new ArrayList<>();

    public KeyboardHandler handler = new KeyboardHandler();

    @Override
    public void onCreate(Bundle bundle) {

        //android.os.Debug.waitForDebugger();

        ActivityAccessHelper.getInstance().activity = this;

        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);
        setContentView(R.layout.activity_car_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        BrowserFragment browserFragment = new BrowserFragment();

        //Add fragments
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, browserFragment, BrowserFragment.TAG)
                .detach(browserFragment)
                .commitNow();

        String initialFragmentTag = BrowserFragment.TAG;

        if (bundle != null && bundle.containsKey(CURRENT_FRAGMENT_KEY)) {
            initialFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY);
        }
        switchToFragment(initialFragmentTag);

        //Build main menu
        MainMenuHandler.buildMainMenu(this);

        //Status bar controller
        getCarUiController().getMenuController().hideMenuButton();
        getCarUiController().getStatusBarController().hideMicButton();
        getCarUiController().getStatusBarController().hideTitle();
        getCarUiController().getStatusBarController().hideAppHeader();
        getCarUiController().getStatusBarController().setAppBarAlpha(0f);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks,
                false);

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        switchToFragment(mCurrentFragmentTag);
    }

    public void switchToFragment(String tag) {
        if (tag.equals(mCurrentFragmentTag)) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment = mCurrentFragmentTag == null ? null : manager.findFragmentByTag(mCurrentFragmentTag);
        Fragment newFragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.detach(currentFragment);
        }
        transaction.attach(newFragment);
        transaction.commit();
        mCurrentFragmentTag = tag;
    }

    private final FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            updateStatusBarTitle();
            updateFragmentContent(f);
        }
    };

    public void updateStatusBarTitle() {
        CarFragment fragment = (CarFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        getCarUiController().getStatusBarController().setTitle(fragment.getTitle());
    }

    public void updateFragmentContent(Fragment fragment) {
        if(fragment instanceof BrowserFragment){
            updateBrowserFragment(fragment);
        }
    }

    public void showKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        webview.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
    }

    public void hideKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        webview.setVisibility(View.VISIBLE);
        keyboard.setVisibility(View.GONE);
    }

    public void sendURLToCar(String enteredText){
        final EditText browser_url_input = (EditText)findViewById(R.id.browser_url_input);
        browser_url_input.setText(enteredText);
    }

    public void sendStringToCar(String enteredText){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        wbb.evaluateJavascript("document.activeElement.value = '" + enteredText + "';", null);
    }

    public void keyInputCallback(String enteredKey){
        final EditText browser_url_input = (EditText)findViewById(R.id.browser_url_input);
        browser_url_input.getText().insert(browser_url_input.getSelectionStart(), enteredKey);
    }

    public void changeURL(String url){
        //set the new url into the url input bar
        final EditText browser_url_input = (EditText)findViewById(R.id.browser_url_input);
        browser_url_input.setText(url);
        //load the new url
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        //Add URLs for Desktop mode here
        List<String> desktopModeURLs = new ArrayList<>();
        //desktopModeURLs.add("");
        if(desktopModeURLs.contains(url) && !currentBrowserMode.equals("DESKTOP")){
            setDesktopMode();
            currentBrowserMode = "DESKTOP";
        } else {
            setMobileMode();
            currentBrowserMode = "MOBILE";
        }
        wbb.loadUrl(url);
        //remember the current url
        currentURL = url;
        //add url to history if last item is not already in the history
        if(!urlHistory.isEmpty() && !urlHistory.get(urlHistory.size()-1).equals(url)){
            urlHistory.add(url);
        }
    }

    private void setDesktopMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        originalAgentString = wbset.getUserAgentString();
        wbset.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        wbset.setUseWideViewPort(true);
        wbset.setLoadWithOverviewMode(true);
    }
    private void setMobileMode(){
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setUserAgentString(originalAgentString);
        wbset.setUseWideViewPort(false);
        wbset.setLoadWithOverviewMode(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment(Fragment fragment) {

        //load web view
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbset.setDomStorageEnabled(true);
        wbb.setWebChromeClient(new WebChromeClient());
        wbb.setWebViewClient(new WebViewClient());
        CookieManager.getInstance().setAcceptThirdPartyCookies(wbb,true);

        //Uncomment these lines to emulate a desktop PC - Use for special requirements
        //setDesktopMode();

        wbb.loadUrl(currentURL);
        urlHistory.add(currentURL);

        //init ui elements
        final EditText browser_url_input = (EditText)findViewById(R.id.browser_url_input);
        browser_url_input.setText(currentURL);

        //init keyboard
        final LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        loadKeyboard(fragment.getContext());

        findViewById(R.id.browser_url_menu).setOnClickListener(view -> {
            //open menu -> Features todo: Favorites, Back, Forward etc.
            getCarUiController().getDrawerController().openDrawer();
        });
        findViewById(R.id.browser_url_keyboard_toggle).setOnClickListener(view -> {
            if(browser_url_input.hasFocus() && keyboard.getVisibility() == View.GONE){
                inputMode = BrowserInputMode.URL_INPUT_MODE;
                showKeyboard();
                return;
            }
            if(!browser_url_input.hasFocus() && keyboard.getVisibility() == View.GONE){
                inputMode = BrowserInputMode.CONTENT_INPUT_MODE;
                browser_url_input.setText("");
                showKeyboard();
                return;
            }
            if(keyboard.getVisibility() == View.VISIBLE){
                hideKeyboard();
            }
        });
        findViewById(R.id.browser_url_backspace).setOnClickListener(view -> {
            String oldContent = browser_url_input.getText().toString();
            int selStart = browser_url_input.getSelectionStart();
            if(oldContent.length() != 0 && selStart > 0){
                browser_url_input.getText().delete(selStart-1, selStart);
            }
        });
        findViewById(R.id.browser_url_ok).setOnClickListener(view -> {
            if(inputMode == BrowserInputMode.URL_INPUT_MODE){
                String newURL = browser_url_input.getText().toString();
                changeURL(newURL);
            }
            if(inputMode == BrowserInputMode.CONTENT_INPUT_MODE){
                wbb.evaluateJavascript("document.activeElement.value = '" + browser_url_input.getText().toString() + "';", null);
                browser_url_input.setText(currentURL);
            }
            //remove keyboard
            hideKeyboard();
        });

        findViewById(R.id.browser_url_submit).setOnClickListener(view -> {
            wbb.evaluateJavascript("document.activeElement.form.submit();", null);
        });

    }

    public void loadKeyboard(Context context){
        final LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        keyboard.removeAllViews();
        keyboard.addView(handler.createKeyboardView(this, context));
    }

}
