package de.tu_darmstadt.informatik.newapp.GameActivity.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.tu_darmstadt.informatik.newapp.GameActivity.Adapter.HighScoreAdapter;
import de.tu_darmstadt.informatik.newapp.GameActivity.DataBase.DataBaseHandler;
import de.tu_darmstadt.informatik.newapp.GameActivity.Model.UserProfile;
import de.tu_darmstadt.informatik.newapp.R;

/** Created by Roshan on 12/20/2016.  */

public class HighSoreListDialog extends DialogFragment {

    @BindView(R.id.recyclerViewScore)
    RecyclerView mRecyclerView;
    private HighScoreAdapter highScoreAdapter;
    List<UserProfile> mUserProfileList;

    public static HighSoreListDialog newInstance() {
        return new HighSoreListDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.layout_scores, container, false);
        ButterKnife.bind(this,v);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //setadapter
        highScoreAdapter = new HighScoreAdapter(getContext());
        mRecyclerView.setAdapter(highScoreAdapter);
        DataBaseHandler dataBaseHandler = new DataBaseHandler(getActivity());
        mUserProfileList= dataBaseHandler.getAllProfiles();
        highScoreAdapter.addAll(mUserProfileList);

        //get your recycler view and populate it.
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        return dialog;
    }
}
