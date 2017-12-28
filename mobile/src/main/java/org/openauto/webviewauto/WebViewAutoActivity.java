package org.openauto.webviewauto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarUiController;

import org.openauto.webviewauto.fragments.BrowserFragment;

import java.util.Arrays;
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

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);
        setContentView(R.layout.activity_car_main);

        CarUiController carUiController = getCarUiController();
        carUiController.getStatusBarController().showTitle();

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

    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment(Fragment fragment) {

        //load web view
        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbb.setWebChromeClient(new WebChromeClient());
        wbb.setWebViewClient(new WebViewClient());
        wbb.loadUrl(currentURL);

        //init ui elements
        final EditText browser_url_input = (EditText)findViewById(R.id.browser_url_input);
        final LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        browser_url_input.setText(currentURL);

        findViewById(R.id.browser_url_menu).setOnClickListener(view -> {
            //open menu -> Features todo: Favorites, Back, Forward etc.
            wbb.loadUrl(homeURL);
            currentURL = homeURL;
            browser_url_input.setText(currentURL);
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
            if(oldContent.length() != 0){
                int start = browser_url_input.getSelectionStart();
                browser_url_input.getText().delete(start-1, start);
            }
        });
        findViewById(R.id.browser_url_ok).setOnClickListener(view -> {
            if(inputMode == BrowserInputMode.URL_INPUT_MODE){
                currentURL = browser_url_input.getText().toString();
                browser_url_input.setText(currentURL);
                wbb.loadUrl(currentURL);
            }
            if(inputMode == BrowserInputMode.CONTENT_INPUT_MODE){
                wbb.evaluateJavascript("document.activeElement.value = '" + browser_url_input.getText().toString() + "';", null);
                browser_url_input.setText(currentURL);
            }
            //remove keyboard
            hideKeyboard();
        });


        //initialize keyboard
        LinearLayout keyboard_layout = (LinearLayout)findViewById(R.id.keyboard_layout);
        List<View> children = UIUtils.getAllChildrenBFS(keyboard_layout);
        for(View v : children){
            if(v instanceof AppCompatButton){
                ((AppCompatButton) v).setAllCaps(false);
                v.setOnClickListener(view -> {
                    AppCompatButton btn = (AppCompatButton) view;
                    if(btn.getText().toString().equals(getResources().getString(R.string.key_caps))){
                        for(View letter : children){
                            if(letter instanceof AppCompatButton) {
                                AppCompatButton lbtn = (AppCompatButton) letter;
                                String[] letters = "qwertzuiopüasdfghjklöäyxcvbnm".split("");
                                String letterChar = lbtn.getText().toString().toLowerCase();
                                if (Arrays.asList(letters).contains(letterChar)) {
                                    lbtn.setText(UIUtils.swapCase(lbtn.getText().toString()));
                                }
                            }
                        }
                        return;
                    }
                    //letter number symbols
                    browser_url_input.getText().insert(browser_url_input.getSelectionStart(), btn.getText());
                });
            }
        }



    }


}
