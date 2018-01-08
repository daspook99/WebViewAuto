package org.openauto.webviewauto.keyboard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.openauto.webviewauto.R;
import org.openauto.webviewauto.WebViewAutoActivity;
import org.openauto.webviewauto.utils.UIUtils;

import java.util.Arrays;
import java.util.List;

/*
    Create keyboard view programmatically
 */
public class KeyboardHandler {

    private String languageIso = "EN";
    private List<String> supportedIsos = Arrays.asList("EN", "RU", "HE");

    private static String[] row_1_shared = {"!","?","#","@","€","$","\u20BD","\"","=","§","&","°","`"};
    private static String[] row_2_shared = {"{","}","[","]","(",")","|","/","\\","<",">","~","´"};
    private static String[] row_3_shared = {"^","1","2","3","4","5","6","7","8","9","0","ß","'"};

    private static String[] row_4_latin = {"+","q","w","e","r","t","y","u","i","o","p","ü","%"};
    private static String[] row_5_latin = {"*","a","s","d","f","g","h","j","k","l","ö","ä",":"};
    private static String[] row_6_latin = {"\uF30E","z","x","c","v","b","n","m",",",".","-","_",";"};
    private static String[] row_7_latin = {"\uF5CA", "http","://","www."," ",".com",".de",".org"};

    private static String[] row_4_russian = {"+","й","ц","у","к","е","н","г","ш","щ","з","х","ъ"};
    private static String[] row_5_russian = {"*","ф","ы","в","а","п","р","о","л","д","ж","э",":"};
    private static String[] row_6_russian = {"\uF30E","я","ч","с","м","и","т","ь","б","ю","-","_",";"};
    private static String[] row_7_russian = {"\uF5CA", "http","://","www."," ",".com",".ru",".org"};

    private static String[] row_4_hebrew = {"+","ק","ר","א","ט","ו","ן","ם","פ","[","]","/",","};
    private static String[] row_5_hebrew = {"*","ש","ד","ג","כ","ע","י","ח","ל","ך","ף",".",":"};
    private static String[] row_6_hebrew = {"\uF30E", "ז","ס","ב","ה","נ","מ","צ","ת","ץ","-","_",";"};
    private static String[] row_7_hebrew = {"\uF5CA", "http","://","www."," ",".com",".co.il",".org"};

    private static Object[] layout_latin = {row_1_shared, row_2_shared, row_3_shared, row_4_latin, row_5_latin, row_6_latin, row_7_latin};
    private static Object[] layout_russian = {row_1_shared, row_2_shared, row_3_shared, row_4_russian, row_5_russian, row_6_russian, row_7_russian};
    private static Object[] layout_hebrew = {row_1_shared, row_2_shared, row_3_shared, row_4_hebrew, row_5_hebrew, row_6_hebrew, row_7_hebrew};

    private void cycleLayout(){
        int currentLayoutIndex = supportedIsos.indexOf(languageIso);
        int nextIndex = currentLayoutIndex + 1;
        if(nextIndex >= supportedIsos.size()){
            nextIndex = 0;
        }
        languageIso = supportedIsos.get(nextIndex);
    }

    public View createKeyboardView(WebViewAutoActivity activity, Context context){

        LinearLayout.LayoutParams W_W_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        LinearLayout.LayoutParams M_W_N = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(M_W_N);

        Object[] rows = layout_latin;
        if(languageIso.equals("RU")){
            rows = layout_russian;
        }
        if(languageIso.equals("HE")){
            rows = layout_hebrew;
        }

        for(Object row : rows){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(M_W_N);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            String[] row_ = (String[])row;
            for(String s : row_){
                Button btn = new Button(context);
                btn.setAllCaps(false);
                btn.setText(s);
                btn.setLayoutParams(W_W_1);
                btn.setOnClickListener(v -> {
                    if(s.equals("\uF5CA")){
                        cycleLayout();
                        activity.loadKeyboard(context);
                    }
                    else if(s.equals(activity.getResources().getString(R.string.key_caps))){
                        List<View> children = UIUtils.getAllChildrenBFS(container);
                        for(View letter : children){
                            if(letter instanceof Button) {
                                Button lbtn = (Button) letter;
                                lbtn.setText(UIUtils.swapCase(lbtn.getText().toString()));
                            }
                        }
                    } else {
                        activity.keyInputCallback(btn.getText().toString());
                    }
                });
                if(s.equals("\uF30E") || s.equals("\uF5CA")){
                    styleIconButton(btn);
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

    private static void styleIconButton(Button btn){
        Typeface typeface = ResourcesCompat.getFont(btn.getContext(), R.font.materialdesignicons);
        btn.setTypeface(typeface);
        ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(0xff04786d));
    }
    private static void styleSpaceButton(Button btn){
        ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(0xff04786d));
    }

}
