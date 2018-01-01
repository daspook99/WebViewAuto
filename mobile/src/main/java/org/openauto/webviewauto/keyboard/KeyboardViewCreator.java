package org.openauto.webviewauto.keyboard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import org.openauto.webviewauto.R;

/*
    Create keyboard view programmatically
 */
public class KeyboardViewCreator {

    private static String[] row_1_shared = {"!","?","#","@","€","$","\u20BD","\"","=","§","&","°","`"};
    private static String[] row_2_shared = {"{","}","[","]","(",")","|","/","\\","<",">","~","´"};
    private static String[] row_3_shared = {"^","1","2","3","4","5","6","7","8","9","0","ß","'"};

    private static String[] row_4_latin = {"+","q","w","e","r","t","y","u","i","o","p","ü","%"};
    private static String[] row_5_latin = {"*","a","s","d","f","g","h","j","k","l","ö","ä",":"};
    private static String[] row_6_latin = {"\uF30E","z","x","c","v","b","n","m",",",".","-","_",";"};
    private static String[] row_7_latin = {"http","://","www."," ",".com",".de",".org"};

    private static String[] row_4_russian = {"+","й","ц","у","к","е","н","г","ш","щ","з","х","ъ"};
    private static String[] row_5_russian = {"*","ф","ы","в","а","п","р","о","л","д","ж","э",":"};
    private static String[] row_6_russian = {"\uF30E","я","ч","с","м","и","т","ь","б","ю","-","_",";"};
    private static String[] row_7_russian = {"http","://","www."," ",".com",".ru",".org"};

    private static Object[] layout_latin = {row_1_shared, row_2_shared, row_3_shared, row_4_latin, row_5_latin, row_6_latin, row_7_latin};
    private static Object[] layout_russian = {row_1_shared, row_2_shared, row_3_shared, row_4_russian, row_5_russian, row_6_russian, row_7_russian};

    public static View createKeyboardView(Context context, String iso){

        LinearLayout.LayoutParams W_W_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        LinearLayout.LayoutParams M_W_N = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(M_W_N);

        Object[] rows = layout_latin;
        if(iso.equals("RUSSIAN")){
            rows = layout_russian;
        }

        for(Object row : rows){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(M_W_N);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            String[] row_ = (String[])row;
            for(String s : row_){
                AppCompatButton btn = new AppCompatButton(context);
                btn.setAllCaps(false);
                btn.setText(s);
                btn.setLayoutParams(W_W_1);
                if(s.equals("\uF30E")){
                    styleCapsButton(btn);
                }
                if(s.equals(" ")){
                    styleSpaceButton(btn);
                }
                linearLayout.addView(btn);
            }
            container.addView(linearLayout);
        }

        return container;
    }

    private static void styleCapsButton(AppCompatButton btn){
        Typeface typeface = ResourcesCompat.getFont(btn.getContext(), R.font.materialdesignicons);
        btn.setTypeface(typeface);
        ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(0xff04786d));
    }
    private static void styleSpaceButton(AppCompatButton btn){
        ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(0xff04786d));
    }

}
