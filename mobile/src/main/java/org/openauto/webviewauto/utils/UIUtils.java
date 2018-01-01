package org.openauto.webviewauto.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class UIUtils {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static String swapCase(final String str) {
        if (isEmpty(str)) {
            return str;
        }

        final int strLen = str.length();
        final int newCodePoints[] = new int[strLen]; // cannot be longer than the char array
        int outOffset = 0;
        for (int i = 0; i < strLen; ) {
            final int oldCodepoint = str.codePointAt(i);
            final int newCodePoint;
            if (Character.isUpperCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
            } else if (Character.isLowerCase(oldCodepoint)) {
                newCodePoint = Character.toUpperCase(oldCodepoint);
            } else {
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset++] = newCodePoint;
            i += Character.charCount(newCodePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     * https://stackoverflow.com/questions/18668897/android-get-all-children-elements-of-a-viewgroup/18669307
     */
    public static List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<>();
        List<View> unvisited = new ArrayList<>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return visited;
    }


    public static void showSnackbar(Activity act, String msg, int length){
        Snackbar.make(act.findViewById(android.R.id.content), msg, length).show();
    }

}
