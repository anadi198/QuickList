package com.katokateki.quicklist.adapters;
/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.model.Listing;

import java.util.Objects;


/***
 * The adapter class for the RecyclerView, contains the menu data.
 */
public class ListingAdapter extends FirestoreAdapter<ListingAdapter.ViewHolder> {

    private int lastPosition = -1;

    public interface OnListingSelectedListener {

        void onListingSelected(DocumentSnapshot listing);

    }

    private OnListingSelectedListener mListener;

    public ListingAdapter(Query query, OnListingSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ListingAdapter.ViewHolder holder,
                                 int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.mContext,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, detail, price, comments;
        private ImageView storeImage;
        private Context mContext;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.store_title);
            detail = itemView.findViewById(R.id.store_desc);
            storeImage = itemView.findViewById(R.id.store_image);
            price = itemView.findViewById(R.id.store_price);
            comments = itemView.findViewById(R.id.comments_store);
            mContext = itemView.getContext();
        }

        void bind(final DocumentSnapshot snapshot,
                  final OnListingSelectedListener listener) {
            Listing currentOption = snapshot.toObject(Listing.class);
            Glide.with(storeImage.getContext()).load(Objects.requireNonNull(currentOption).getImage_url()).into(storeImage);
            name.setText(currentOption.getName());
            detail.setText(currentOption.getDescription());
            price.setText("\u20B9" + currentOption.getPrice());
            if (currentOption.getComments() == null) {
                comments.setText("No comments yet");
            } else {
                int num = currentOption.getComments().size();
                if(num==1)
                {
                    comments.setText(String.format("%d comment", currentOption.getComments().size()));
                }
                else
                {
                    comments.setText(String.format("%d comments", currentOption.getComments().size()));
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onListingSelected(snapshot);
                    }
                }
            });
        }
    }
}

