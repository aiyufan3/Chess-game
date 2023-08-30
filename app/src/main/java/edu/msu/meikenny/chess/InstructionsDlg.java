package edu.msu.meikenny.chess;

import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;

public class InstructionsDlg extends DialogFragment {


    public InstructionsDlg() {
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.instructions_dlg_title);

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.instructions, null))
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                });

        final Dialog dlg = builder.create();

        dlg.setOnShowListener(dialog -> {
        });

        return dlg;
    }
}