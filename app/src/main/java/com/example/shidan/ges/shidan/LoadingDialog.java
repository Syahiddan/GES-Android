package com.example.shidan.ges.shidan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.example.shidan.ges.R;

public class LoadingDialog extends AppCompatDialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

    LayoutInflater layoutInflater = getActivity().getLayoutInflater();

    View view = View.inflate(getContext(), R.layout.loading_dialog,null);

    builder.setView(view);
    //builder.setTitle("Title shidan");
//        builder.setNegativeButton("negative", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.setPositiveButton("positive", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

    return builder.create();
  }
}
