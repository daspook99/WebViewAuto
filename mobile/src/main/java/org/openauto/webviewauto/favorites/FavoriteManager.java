package org.openauto.webviewauto.favorites;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.openauto.webviewauto.utils.IOHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    private Context context;
    public List<FavoriteEnt> favorites;

    public FavoriteManager(Context context){
        this.context = context;
        readFavorites();
    }

    private static List<FavoriteEnt> getDefaultFavorites(){
        List<FavoriteEnt> favorites = new ArrayList<>();
        favorites.add(new FavoriteEnt("MENU_FAVORITES_YouTube","YouTube","https://www.youtube.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_Google","Google","https://www.google.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_BBC","BBC","http://www.bbc.com/"));
        favorites.add(new FavoriteEnt("MENU_FAVORITES_Wikipedia","Wikipedia","https://www.wikipedia.org/"));
        return favorites;
    }

    public FavoriteEnt getFavoriteById(String id){
        for(FavoriteEnt f : favorites){
            if(f.getId().equals(id)){
                return f;
            }
        }
        return null;
    }

    public void addFavorite(FavoriteEnt newFav){
        this.favorites.add(newFav);
    }

    public void removeFavorite(FavoriteEnt favToRemove){
        this.favorites.remove(favToRemove);
    }

    public void persistFavorites(){
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        IOHandler ioHandler = new IOHandler(context);
        ioHandler.saveObject(json, "favorites.json");
    }

    private void readFavorites(){
        IOHandler ioHandler = new IOHandler(context);
        String favoritesJson = (String)ioHandler.readObject("favorites.json");
        if(favoritesJson == null){
            favorites = getDefaultFavorites();
        } else {
            Gson gson = new Gson();
            Type favListType = new TypeToken<List<FavoriteEnt>>() {}.getType();
            List<FavoriteEnt> favsFromFile = gson.fromJson(favoritesJson, favListType);
            favorites = favsFromFile;
        }
    }

}
