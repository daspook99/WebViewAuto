package org.openauto.webviewauto.favorites;


import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    public static List<FavoriteEnt> getFavorites(){
        List<FavoriteEnt> favorites = new ArrayList<>();
        favorites.add(new FavoriteEnt("MENU_FAVORITES_YOUTUBE","YouTube","https://www.youtube.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_GOOGLE","Google","https://www.google.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_BBC","BBC","http://www.bbc.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_WIKI","Wikipedia","https://www.wikipedia.org/"));
        return favorites;
    }

    public static FavoriteEnt getFavoriteById(String id){
        for(FavoriteEnt f : getFavorites()){
            if(f.getId().equals(id)){
                return f;
            }
        }
        return null;
    }

}
