package org.openauto.webviewauto.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.openauto.webviewauto.R;

/**
 * Class to display a text input dialog
 */
public class TextInputDialog {

    public TextInputDialog(final Activity activity, final Object identifier, String title, String inputHint){
        this(activity, identifier, title, inputHint, "");
    }

    public TextInputDialog(final Activity activity, final Object identifier, String title, String inputHint, String startValue){

        final IDialogResult dialogResultActivity = (IDialogResult) activity;

        LayoutInflater factory = LayoutInflater.from(activity);
        final View view = factory.inflate(R.layout.textinput_dialog, null);

        // Set up the input
        final EditText input = view.findViewById(R.id.textinput_dialog_edittext);
        input.setText(startValue);
        input.setHint(inputHint);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, 0);
        builder.setView(view);
        builder.setTitle(title)
              //  .setMessage(message)
                .setPositiveButton(activity.getResources().getString(R.string.button_label_ok), (dialog, id) -> {

                    EditText edt = view.findViewById(R.id.textinput_dialog_edittext);
                    dialogResultActivity.passResultToActivity(edt.getText().toString(), identifier);

                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                })
                .setNegativeButton(activity.getResources().getString(R.string.button_label_cancel), (dialog, id) -> {

                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialog.dismiss();

                });

        AlertDialog alert = builder.create();
        alert.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

}
