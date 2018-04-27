package com.example.sergi.multimediaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    public FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1313;
    private GoogleMap mMap;
    public Bitmap bitmap;
    public LatLng position;
    FirebaseFirestore db;
    private int i = 0;
    private HashMap<Integer, Point> points = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            db = FirebaseFirestore.getInstance();
        } else {
            doLogin();
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        downLoadPoints();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                i++;

                position = latLng;
                dispatchTakePictureIntent();
            }
        });
    }

    public void savePoints(Point point) {
        db.collection("Points")
                .add(point)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d("Save", "DocumentSnapshot added with ID: " + documentReference.getId());
                        reload();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Save", "Error adding document", e);
                    }
                });

    }

    public void downLoadPoints() {
        db.collection("Points")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                //Log.d("GET", document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                //Log.d("Foto", (String) data.get("foto"));
                                drawPoints( (String) data.get("foto"),(double) data.get("lat"), (double) data.get("lon"));
                            }
                        } else {
                            Log.w("GET", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void drawPoints(String foto, double lat, double lon) {
        i++;

        Point point = new Point(foto, lat,  lon);
        points.put(i,point);

        LatLng latLng = new LatLng(point.getLat(),point.getLon());
        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(i))) ;
        mMap.setOnMarkerClickListener((Marker marker) -> {
            //Log.d("Anda", marker.getTitle());
            //Log.d("Anda", points.toString());
            Point geoPoint = points.get(Integer.valueOf(marker.getTitle()));
            Intent intent = new Intent(getApplicationContext(), PointMainActivity.class);
            //Log.d("Anda1", geoPoint.toString());
            intent.putExtra("point", geoPoint.getFoto());
            startActivity(intent);
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            bitmap = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Point point = new Point(encoded ,this.position.latitude, this.position.longitude);

            savePoints(point);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void doLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build())).build(),
                RC_SIGN_IN);
    }

    public void reload() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
