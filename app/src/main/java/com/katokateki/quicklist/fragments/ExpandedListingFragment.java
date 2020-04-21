package com.katokateki.quicklist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.activities.MainActivity;
import com.katokateki.quicklist.adapters.CommentsAdapter;
import com.katokateki.quicklist.adapters.ListingAdapter;
import com.katokateki.quicklist.model.Comments;
import com.katokateki.quicklist.model.Listing;

import java.util.ArrayList;
import java.util.Objects;

public class ExpandedListingFragment extends DialogFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1, username;
    private TextView no_comments, title, desc, price, comment_no;
    private RecyclerView recyclerView;
    private FirebaseFirestore mFirestore;
    private EditText comment_edit;
    private ImageView comment_send, imageView;
    private CommentsAdapter mAdapter;
    private Listing currentOption;
    private ArrayList<Comments> arrayList;
    private DocumentReference docRef;
    private ProgressBar pb;
    static ExpandedListingFragment newInstance(String param) {
        ExpandedListingFragment fragment = new ExpandedListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }
    private void addComment()
    {
        pb.setVisibility(View.VISIBLE);
        String comm = comment_edit.getText().toString();
        if(comm.length()!=0) {

            Comments comments1 = new Comments(comm, username, String.valueOf
                    (android.text.format.DateFormat.format("dd-MM-yyyy",
                    new java.util.Date())), String.valueOf(System.currentTimeMillis()));
            arrayList.add(comments1);
            docRef.update("comments", arrayList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Comment added",
                                    Toast.LENGTH_SHORT).show();
                            commentsEmpty(false);
                            int num = arrayList.size();
                            if(num>1)
                            {
                                no_comments.setText(String.format("%d comments", num));
                            }
                            else
                            {
                                no_comments.setText(String.format("%d comment", num));
                            }
                            mAdapter = new CommentsAdapter(getContext(), arrayList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            comment_edit.clearFocus();
                            comment_edit.setText("");
                            pb.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),
                                    "Comment could not be added",
                                    Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                        }
                    });
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_expanded_listing, container, false);
        arrayList = new ArrayList<>();

        comment_edit = inflate.findViewById(R.id.comment_edit_text);
        comment_send = inflate.findViewById(R.id.comment_send);
        comment_send.setOnClickListener(this);

        title = inflate.findViewById(R.id.store_title_ex);

        View frame = inflate.findViewById(R.id.recycler_frame);
        recyclerView = frame.findViewById(R.id.recyclerview_comments);
        mAdapter = new CommentsAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        price = inflate.findViewById(R.id.store_price_ex);
        no_comments = inflate.findViewById(R.id.number_comments);
        imageView = inflate.findViewById(R.id.store_image_ex);
        comment_no = inflate.findViewById(R.id.no_comments);
        pb = inflate.findViewById(R.id.indeterminate_progress);
        desc = inflate.findViewById(R.id.store_desc_ex);

        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        username = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        docRef = mFirestore.collection("listings").document(mParam1);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        currentOption = document.toObject(Listing.class);
                        Glide.with(imageView.getContext()).load(Objects.requireNonNull(currentOption).getImage_url()).into(imageView);
                        title.setText(currentOption.getName());
                        desc.setText(currentOption.getDescription());
                        price.setText("\u20B9" + currentOption.getPrice());
                        if (currentOption.getComments() == null) {
                            no_comments.setText("No comments yet");
                            commentsEmpty(true);
                            pb.setVisibility(View.GONE);
                        } else {
                            int num = currentOption.getComments().size();
                            if(num>1)
                            {
                                no_comments.setText(String.format("%d comments", num));
                            }
                            else
                            {
                                no_comments.setText(String.format("%d comment", num));
                            }
                            commentsEmpty(false);
                            arrayList.clear();
                            arrayList = currentOption.getComments();
                            mAdapter = new CommentsAdapter(getContext(), arrayList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            pb.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Error loading listing", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });
        return inflate;
    }
    private void commentsEmpty(boolean flag)
    {
        if(flag)
        {
            comment_no.setVisibility(View.VISIBLE);
        }
        else
        {
            comment_no.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }
    public ExpandedListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.comment_send)
        {
            addComment();
        }
    }
}
