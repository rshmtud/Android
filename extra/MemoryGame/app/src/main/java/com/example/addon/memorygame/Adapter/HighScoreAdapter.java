package com.example.addon.memorygame.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.addon.memorygame.R;
import com.example.addon.memorygame.Model.UserProfile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**  * Created by Roshan on 12/20/2016.  */
public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.ViewHolder> implements AdapterOperation<UserProfile> {

    private Context mContext;
    private List<UserProfile> mUserList= new ArrayList<>();

    public HighScoreAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.highscore_lyt,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtSrNo.setText(position+1+".");
        holder.txtName.setText(mUserList.get(position).getUserName());
        holder.txtScore.setText(mUserList.get(position).getScore()+"");

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public void addAll(List<UserProfile> userProfileList) {
        for(UserProfile user:userProfileList)
        {
            add(user);
        }

    }

    @Override
    public void add(UserProfile userProfile) {
        mUserList.add(userProfile);
        notifyItemInserted(mUserList.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        return (getItemCount()==0);
    }

    @Override
    public void remove(UserProfile item) {
        int pos= mUserList.indexOf(item);
                if(pos>-1)
                {
                    mUserList.remove(pos);
                    notifyItemRemoved(pos);
                }

    }

    @Override
    public void clear() {

        while (getItemCount()>0)
        {
            remove(getItem(0));
        }
    }

    @Override
    public UserProfile getItem(int position) {
        return mUserList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtSrNo)
        TextView txtSrNo;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtScore)
        TextView txtScore;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
