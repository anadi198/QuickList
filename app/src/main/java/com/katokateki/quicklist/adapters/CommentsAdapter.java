package com.katokateki.quicklist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.model.Comments;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>  {

    // Member variables.
    private ArrayList<Comments> mMenuData;
    private Context mContext;

    public CommentsAdapter(Context context, ArrayList<Comments> menuData) {
        this.mMenuData = menuData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder holder,
                                 int position) {
        try{
            Comments currentOption = mMenuData.get(position);
            holder.bindTo(currentOption);
        }
        catch(Exception e){
            //
        }
    }

    @Override
    public int getItemCount() {
        return mMenuData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, comment, date;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_user);
            comment = itemView.findViewById(R.id.comment_content);
            date = itemView.findViewById(R.id.comment_time);
        }
        void bindTo(Comments currentOption){
            name.setText(currentOption.getFromUser());
            comment.setText(currentOption.getComment());
            date.setText(currentOption.getTime());
        }
    }
}
