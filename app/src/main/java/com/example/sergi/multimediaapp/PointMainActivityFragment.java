package com.example.sergi.multimediaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PointMainActivityFragment extends Fragment {

    public PointMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_main, container, false);

        Intent i = getActivity().getIntent();
        String foto;
        ImageView img = (ImageView) view.findViewById(R.id.imgFoto);

        if (i != null) {
           foto = (String) i.getSerializableExtra("point");

            if (foto != null) {
                parseFoto(foto,img);
            }
        }

        return view;
    }

    private void parseFoto(String foto, ImageView img) {
        byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
    }
}
