package org.openauto.webviewauto;

import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;

import org.openauto.webviewauto.fragments.BrowserFragment;

public class MainMenuHandler {


    public static void buildMainMenu(final WebViewAutoActivity activity){

        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity));

        mainMenu.addMenuItem("MENU_BROWSER", new MenuItem.Builder()
                .setTitle("Browser")
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_CHANGE_URL", new MenuItem.Builder()
                .setTitle("Change URL")
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_KEYBOARD", new MenuItem.Builder()
                .setTitle("Keyboard")
                .setType(MenuItem.Type.ITEM)
                .build());

        MenuController menuController = activity.getCarUiController().getMenuController();
        menuController.setRootMenuAdapter(mainMenu);
        menuController.showMenuButton();

    }


    public static ListMenuAdapter.MenuCallbacks createMenuCallbacks(final WebViewAutoActivity activity){

        final ListMenuAdapter.MenuCallbacks mMenuCallbacks = new ListMenuAdapter.MenuCallbacks() {
            @Override
            public void onMenuItemClicked(String name) {
                if("MENU_BROWSER".equals(name)){
                    activity.toggleKeyboard();
                }
                if("MENU_CHANGE_URL".equals(name)){
                    activity.currentMenuItem = "MENU_CHANGE_URL";
                    activity.toggleKeyboard();
                }
                if("MENU_KEYBOARD".equals(name)){
                    activity.currentMenuItem = "MENU_KEYBOARD";
                    activity.toggleKeyboard();
                }
            }

            @Override
            public void onEnter() {
            }

            @Override
            public void onExit() {
                activity.updateStatusBarTitle();
            }
        };
        return mMenuCallbacks;
    }


}
