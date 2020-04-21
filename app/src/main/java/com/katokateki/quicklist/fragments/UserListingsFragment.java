package com.katokateki.quicklist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.adapters.ListingAdapter;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListingsFragment  extends Fragment implements View.OnClickListener,
        ListingAdapter.OnListingSelectedListener {
    private View view;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ListingAdapter mAdapter;
    private String user;

    public UserListingsFragment() {
        // Required empty public constructor
    }

    private void initRecyclerView() {
        mAdapter = new ListingAdapter(mQuery, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store_listings, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview_store);
        FirebaseFirestore.setLoggingEnabled(true);
        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("listings")
                .whereEqualTo("user", user)
                .orderBy("epoch", Query.Direction.DESCENDING);
        initRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onListingSelected(DocumentSnapshot listing) {
        ExpandedListingFragment detailsFragment = ExpandedListingFragment.newInstance(listing.getId());
        FragmentManager fm = getParentFragmentManager();
        detailsFragment.show(fm, "ExpandedListing");
    }
}