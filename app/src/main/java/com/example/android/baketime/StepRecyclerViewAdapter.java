package com.example.android.baketime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 8/8/2017.
 */

public class StepRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final String LOG_TAG = StepRecyclerViewAdapter.class.getSimpleName();
    private boolean mTwoPane = false;

    private FragmentManager mFragmentManager;
    private Context mContext;


    public StepRecyclerViewAdapter(Context context, FragmentManager fragmentManager, boolean mTwoPane) {
        mContext = context;
        mFragmentManager = fragmentManager;
        this.mTwoPane = mTwoPane;
        mValues = new ArrayList<>();
    }

    @Override
    public RecipeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_list_content, parent, false);
        return new RecipeRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeRecyclerViewAdapter.ViewHolder holder, final int position) {
        if(position == 0){
            holder.mIdView.setText("Ingredients");
            holder.mContentView.setText(mValues.get(position));
        }
        else if(position>0){
            holder.mIdView.setText(mContext.getString(R.string.step_with_space) + position);
            holder.mContentView.setText(getStepShortDescriptionFromJSON(mValues.get(position)));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Log.e(LOG_TAG,"inside mTwoPane is true");
                        Bundle arguments = new Bundle();
                        arguments.putString(RecipeStepDetailFragment.ARG_STEP, mValues.get(position));
                        arguments.putBoolean(RecipeStepDetailFragment.ARG_TWO_PANE_FLAG, true);
                        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                        fragment.setArguments(arguments);
                        mFragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                    }else {
                        Log.e(LOG_TAG, "inside ON Click!");
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                        intent.putExtra(RecipeStepDetailFragment.ARG_STEP_POS, position);
                        intent.putExtra(RecipeStepDetailFragment.ARG_STEP, mValues.get(position));
                        intent.putExtra(RecipeStepDetailFragment.ARG_STEPS, valuesToStringArray());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public String[] valuesToStringArray() {
        String[] result = new String[mValues.size()];
        for(int i = 1; i < mValues.size(); i++){
            result[i] = mValues.get(i);
        }
        return result;
    }

    public void clear() {
        if(mValues != null) {
            mValues.clear();
        }
    }

    public void add(String step) {
        if(mValues != null) {
            mValues.add(step);
        }
    }

    public static String getStepDescriptionFromJSON(String step) {
        try {
            JSONObject stepString = new JSONObject(step);
            return stepString.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStepShortDescriptionFromJSON(String step) {
        try {
            JSONObject stepString = new JSONObject(step);
            return stepString.getString("shortDescription");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStepVideoURL(String step) {
        try {
            JSONObject stepString = new JSONObject(step);
            return stepString.getString("videoURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}