package com.katokateki.quicklist.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.model.Listing;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddListingFragment extends DialogFragment implements View.OnClickListener {
    private EditText name, desc, price;
    private ImageView imgButton;
    private MaterialButton addList, cancel;
    private Uri image_uri;
    private static int RESULT_LOAD_IMAGE = 1;
    private LinearLayout progress;
    private ProgressBar pb;
    private TextView tv;
    private View v;
    public AddListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width =  LinearLayout.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_listing, container,
                false);
        name = rootView.findViewById(R.id.product_name_add);
        desc = rootView.findViewById(R.id.product_desc_add);
        price = rootView.findViewById(R.id.product_price_add);
        imgButton = rootView.findViewById(R.id.image_add_listing);
        addList = rootView.findViewById(R.id.add_listing);
        cancel = rootView.findViewById(R.id.cancel_listing);
        addList.setOnClickListener(this);
        cancel.setOnClickListener(this);
        imgButton.setOnClickListener(this);
        progress = rootView.findViewById(R.id.progress_add_listing);
        pb = rootView.findViewById(R.id.listing_progress);
        tv = rootView.findViewById(R.id.progress_text_add);
        v = rootView.findViewById(R.id.dark_view);
        makeInvisible();
        return rootView;
    }
    private void makeVisible()
    {
        progress.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
    }
    private void makeInvisible()
    {
        progress.setVisibility(View.GONE);
        v.setVisibility(View.GONE);
    }
    private void addListing() {
        makeVisible();
        String title, description, money, time;
        title = name.getText().toString();
        description = desc.getText().toString();
        money = price.getText().toString();
        time = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy",
                new java.util.Date()));
        if (title.length() == 0) {
            Toast.makeText(getContext(), "Please give a title to your product!",
                    Toast.LENGTH_SHORT).show();
        } else if (description.length() == 0) {
            Toast.makeText(getContext(), "Please give a description for your product!",
                    Toast.LENGTH_SHORT).show();
        } else if (money.length() == 0) {
            Toast.makeText(getContext(), "Invalid product price", Toast.LENGTH_SHORT).show();
        }
        else if (image_uri==null) {
            Toast.makeText(getContext(), "Please choose a picture first!", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            CollectionReference listings = mFirestore.collection("listings");
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/" + uid +
                    image_uri.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(image_uri);
            tv.setText("Uploading image...");
            Task<Uri> urlTask = uploadTask.addOnProgressListener
                    (new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (90.0 * taskSnapshot.getBytesTransferred()) /
                            taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                    pb.setProgress((int)progress);
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception{
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        tv.setText("Adding product listing...");
                        Uri downloadUri = task.getResult();
                        Listing listing = new Listing(uid, title, description,
                                Objects.requireNonNull(downloadUri).toString(),
                                money, null, time, String.valueOf(System.currentTimeMillis()));
                        listings.add(listing)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        pb.setProgress(100);
                                        makeInvisible();
                                        Toast.makeText(getContext(), "Listing added!",
                                                Toast.LENGTH_SHORT).show();
                                        exitDialog();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),
                                                "Listing could not be added",
                                                Toast.LENGTH_SHORT).show();
                                        exitDialog();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Upload failed, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void exitDialog()
    {
        this.dismiss();
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                RESULT_LOAD_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                image_uri = data.getData();
                Picasso.get().load(image_uri).into(imgButton);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_listing:
                addListing();
                break;
            case R.id.cancel_listing:
                this.dismiss();
                break;
            case R.id.image_add_listing:
                selectImage();
                break;
        }
    }
}
