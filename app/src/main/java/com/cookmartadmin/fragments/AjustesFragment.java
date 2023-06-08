package com.cookmartadmin.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cookmartadmin.R;
import com.cookmartadmin.SplashScreen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AjustesFragment extends Fragment {
    private TextView contactEmail, contactAbout, contactStoreName, contactAddress;
    private TextView email, name;
    private CircleImageView profileImage;
    private String lastIndex;
    private String storeName, contactEmailStr, storeAddress, about;
    private ProgressDialog progressDialog;
    private Uri imagePath;
    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);
        try {
            ImageView editDialog = view.findViewById(R.id.edit_dialog_btn);
            contactAbout = view.findViewById(R.id.ajustes_informacion_tienda);
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait while we are fetching the data.");
            progressDialog.setCanceledOnTouchOutside(false);
            profileImage = view.findViewById(R.id.ajustes_imagen_tienda);
            CardView editStoreBtn = view.findViewById(R.id.frag_settings_edit_storeImage_btn);
            contactAddress = view.findViewById(R.id.settings_store_address);
            contactStoreName = view.findViewById(R.id.ajustes_nombre_tienda);
            contactEmail = view.findViewById(R.id.setting_contact_email);
            email = view.findViewById(R.id.ajustes_correo);
            name = view.findViewById(R.id.ajustes_nombre_admin);
            editStoreBtn.setOnClickListener(v -> Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select Your Profile Image"), 131);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }

            }).check());
            editDialog.setOnClickListener(v -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View vObj = LayoutInflater.from(getContext()).inflate(R.layout.layout_edit_detalles, null);
                builder.setView(vObj);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                EditText dialogName = vObj.findViewById(R.id.dialog_edit_detalles_tienda_nombre);
                EditText dialogEmail = vObj.findViewById(R.id.dialog_edit_detalles_correo);
                EditText dialogAddress = vObj.findViewById(R.id.dialog_edit_detalles_direccion_tienda);
                EditText dialogAbout = vObj.findViewById(R.id.dialog_edit_detalles_informacion);
                TextView dialogCancel = vObj.findViewById(R.id.dialog_edit_detalles_canelar_btn);
                TextView dialogSave = vObj.findViewById(R.id.dialog_edit_detalles_guardar_btn);
                dialogName.setText(storeName);
                dialogEmail.setText(contactEmailStr);
                dialogAddress.setText(storeAddress);
                dialogAbout.setText(about);
                dialogSave.setOnClickListener(v12 -> {
                    progressDialog.show();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("informacion", dialogAbout.getText().toString());
                    hashMap.put("storeAddress", dialogAddress.getText().toString());
                    hashMap.put("storeName", dialogName.getText().toString());
                    hashMap.put("lastIndex",lastIndex);
                    hashMap.put("storeImage",imageUrl);
                    hashMap.put("storeEmail", dialogEmail.getText().toString());
                    FirebaseDatabase.getInstance().getReference("infoTienda")
                            .setValue(hashMap).addOnSuccessListener(aVoid -> {
                        progressDialog.cancel();
                        alertDialog.cancel();
                        contactStoreName.setText(hashMap.get("storeName"));
                        contactEmail.setText(hashMap.get("storeEmail"));
                        contactAddress.setText(hashMap.get("storeAddress"));
                        contactAbout.setText(hashMap.get("informacion"));
                        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    });
                });
                dialogCancel.setOnClickListener(v1 -> alertDialog.cancel());

                alertDialog.show();
            });
            getDataSettings();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getDataSettings() {
        progressDialog.show();
        name.setText(SplashScreen.sharedPreferences.getString("nombre", "Nombre apellidos"));
        email.setText(SplashScreen.sharedPreferences.getString("correo", "ejemplo@gmail.com"));
        FirebaseDatabase.getInstance().getReference("informacion")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.cancel();
                        if (snapshot.exists()) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                            if (hashMap.containsKey("informacion")) {
                                contactAbout.setText(hashMap.get("informacion"));
                                about=hashMap.get("informacion");
                            }
                            if(hashMap.containsKey("lastIndex")){
                                lastIndex=hashMap.get("lastIndex");
                            }
                            if(hashMap.containsKey("storeImage")){
                                imageUrl=hashMap.get("storeImage");

                                Picasso.get().load(hashMap.get("storeImage")).into(profileImage);
                            }
                            if (hashMap.containsKey("storeAddress")) {
                                storeAddress=hashMap.get("storeAddress");
                                contactAddress.setText(hashMap.get("storeAddress"));
                            }
                            if (hashMap.containsKey("storeName")) {
                                storeName=hashMap.get("storeName");
                                contactStoreName.setText(hashMap.get("storeAddress"));
                            }
                            if (hashMap.containsKey("storeEmail")) {
                                contactEmailStr=hashMap.get("storeEmail");
                                contactEmail.setText(hashMap.get("storeEmail"));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.cancel();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadStoreImage() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Espere por favor..");
        progressDialog.setMessage("We are adding to our database.");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        FirebaseStorage.getInstance().getReference("infoTienda").child("storeImage")
                .putFile(imagePath).addOnCompleteListener(task -> FirebaseStorage.getInstance().getReference("infoTienda").child("storeImage")
                .getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.cancel();
                    imageUrl=uri.toString();
                    FirebaseDatabase.getInstance().getReference("infoTienda").child("storeImage")
                            .setValue(uri.toString());
                })).addOnFailureListener(e -> {
            progressDialog.cancel();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 131 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                uploadStoreImage();
                profileImage.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}