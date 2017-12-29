package org.openauto.webviewauto;

import android.util.Log;

import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class MainMenuHandler {

    public static Map<String,String> favorites = new HashMap();

    public static void buildMainMenu(final WebViewAutoActivity activity){

        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity, mainMenu));

        mainMenu.addMenuItem("MENU_HOME", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_home))
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_BACK", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_back))
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem("MENU_FAVORITES", new MenuItem.Builder()
                .setTitle(activity.getResources().getString(R.string.menu_favorites))
                .setType(MenuItem.Type.SUBMENU)
                .build());

        //Build menu for favorites, TODO: Dynamic favorite handling (Add fav, Remove fav)
        favorites.put("MENU_FAVORITES_YOUTUBE|YouTube", "https://www.youtube.com/");
        favorites.put("MENU_FAVORITES_GOOGLE|Google", "https://www.google.com/");
        favorites.put("MENU_FAVORITES_BBC|BBC", "http://www.bbc.com/");
        favorites.put("MENU_FAVORITES_WIKI|Wikipedia", "https://www.wikipedia.org/");

        ListMenuAdapter favMenu = new ListMenuAdapter();
        favMenu.setCallbacks(MainMenuHandler.createMenuCallbacks(activity, mainMenu));
        for(Map.Entry e : favorites.entrySet()){
            favMenu.addMenuItem((String)e.getKey(), new MenuItem.Builder()
                    .setTitle(((String) e.getKey()).split("\\|")[1])
                    .setType(MenuItem.Type.ITEM)
                    .build());
        }
        mainMenu.addSubmenu("MENU_FAVORITES", favMenu);

        MenuController menuController = activity.getCarUiController().getMenuController();
        menuController.setRootMenuAdapter(mainMenu);
        menuController.showMenuButton();

    }


    public static ListMenuAdapter.MenuCallbacks createMenuCallbacks(final WebViewAutoActivity activity, ListMenuAdapter mainMenu){

        final ListMenuAdapter.MenuCallbacks mMenuCallbacks = new ListMenuAdapter.MenuCallbacks() {
            @Override
            public void onMenuItemClicked(String name) {
                Log.i("Test", name);

                if("MENU_HOME".equals(name)){
                    activity.changeURL(activity.homeURL);
                }
                if("MENU_BACK".equals(name)){
                    //TODO: implement a history
                }
                if(name.startsWith("MENU_FAVORITES_")){
                   activity.changeURL(favorites.get(name));
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
