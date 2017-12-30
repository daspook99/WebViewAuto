package org.openauto.webviewauto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openauto.webviewauto.favorites.FavoriteEnt;
import org.openauto.webviewauto.favorites.FavoriteManager;

/**
 * Activity for phone
 */
public class WebViewPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);

        FavoriteManager favoriteManager = new FavoriteManager(this);
        reloadFavList();

        Button add_favorite_button = findViewById(R.id.add_favorite_button);
        add_favorite_button.setOnClickListener(v -> {
            EditText new_fav_title = findViewById(R.id.new_fav_title);
            EditText new_fav_url = findViewById(R.id.new_fav_url);
            FavoriteEnt newFav = new FavoriteEnt("MENU_FAVORITES_" + new_fav_title.getText().toString(), new_fav_title.getText().toString(), new_fav_url.getText().toString());
            favoriteManager.addFavorite(newFav);
            favoriteManager.persistFavorites();
            reloadFavList();
        });

    }

    private void reloadFavList(){
        LinearLayout favorite_container = findViewById(R.id.favorite_container);
        FavoriteManager favoriteManager = new FavoriteManager(this);
        for(FavoriteEnt e : favoriteManager.favorites){
            LinearLayout rowContainer = new LinearLayout(getApplicationContext());
            rowContainer.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            rowContainer.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(getApplicationContext());
            textView.setText(e.getUrl());
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            rowContainer.addView(textView);
            Button removeButton = new Button(getApplicationContext());
            removeButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            removeButton.setText(getResources().getString(R.string.button_label_remove));
            removeButton.setTag(e);
            removeButton.setOnClickListener(v -> {
                favoriteManager.removeFavorite((FavoriteEnt)v.getTag());
                favoriteManager.persistFavorites();
                reloadFavList();
            });
            rowContainer.addView(removeButton);

            favorite_container.addView(rowContainer);

        }
    }


}
