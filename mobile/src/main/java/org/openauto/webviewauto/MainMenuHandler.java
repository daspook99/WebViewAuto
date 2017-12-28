package org.openauto.webviewauto;

import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;

public class MainMenuHandler {


    public static void buildMainMenu(final WebViewAutoActivity activity){

        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity));

        mainMenu.addMenuItem("MENU_HOME", new MenuItem.Builder()
                .setTitle("Home")
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_BACK", new MenuItem.Builder()
                .setTitle("Back")
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_FAVORITES", new MenuItem.Builder()
                .setTitle("Favorites")
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

                }
                if("MENU_CHANGE_URL".equals(name)){

                }
                if("MENU_KEYBOARD".equals(name)){

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
