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
import com.google.android.apps.auto.sdk.StatusBarController;

import org.openauto.webviewauto.fragments.BrowserFragment;

import java.util.Arrays;
import java.util.List;

public class WebViewAutoActivity extends CarActivity {

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";
    private String mCurrentFragmentTag;
    public String currentMenuItem;
    public String currentURL = "https://duckduckgo.com";

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
        StatusBarController statusBarController = carUiController.getStatusBarController();
        statusBarController.setAppBarAlpha(0.5f);
        statusBarController.setAppBarBackgroundColor(0xffff0000);

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

    public void toggleKeyboard() {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        LinearLayout keyboard = (LinearLayout)findViewById(R.id.browser_keyboard);
        final EditText input_content = (EditText)findViewById(R.id.browser_keyboard_edittext);

        if(currentMenuItem.equals("MENU_CHANGE_URL")){
            input_content.setText(currentURL);
        } else {
            input_content.setText("");
        }

        if(webview.getVisibility() != View.GONE){
            webview.setVisibility(View.GONE);
            keyboard.setVisibility(View.VISIBLE);
        } else {
            webview.setVisibility(View.VISIBLE);
            keyboard.setVisibility(View.GONE);
        }
    }

    public void enterToWebview(String input) {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.evaluateJavascript("document.activeElement.value = '" + input + "';", null);
    }

    public void changeURL(String input) {
        WebView webview = (WebView)findViewById(R.id.webview_component);
        webview.loadUrl(input);
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void updateBrowserFragment(Fragment fragment) {

        WebView wbb = (WebView)findViewById(R.id.webview_component);
        WebSettings wbset=wbb.getSettings();
        wbset.setJavaScriptEnabled(true);
        wbb.setWebChromeClient(new WebChromeClient());
        wbb.setWebViewClient(new WebViewClient());
        wbb.loadUrl(currentURL);

        //initialize keyboard
        final EditText input_content = (EditText)findViewById(R.id.browser_keyboard_edittext);
        LinearLayout keyboard_layout = (LinearLayout)findViewById(R.id.keyboard_layout);
        List<View> children = UIUtils.getAllChildrenBFS(keyboard_layout);
        for(View v : children){
            if(v instanceof AppCompatButton){
                ((AppCompatButton) v).setAllCaps(false);
                v.setOnClickListener(view -> {
                    AppCompatButton btn = (AppCompatButton) view;
                    //handle special keys
                    if(btn.getText().toString().equals(getResources().getString(R.string.key_enter))){
                        if(currentMenuItem.equals("MENU_CHANGE_URL")){
                            changeURL(input_content.getText().toString());
                        }
                        if(currentMenuItem.equals("MENU_KEYBOARD")){
                            enterToWebview(input_content.getText().toString());
                        }
                        toggleKeyboard();
                        return;
                    }
                    if(btn.getText().toString().equals(getResources().getString(R.string.key_backspace))){
                        String oldContent = input_content.getText().toString();
                        if(oldContent.length() != 0){
                            String newInput = oldContent.substring(0, oldContent.length()-1);
                            input_content.setText(newInput);
                        }
                        return;
                    }
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
                    input_content.getText().insert(input_content.getSelectionStart(), btn.getText());
                });
            }
        }



    }


}
