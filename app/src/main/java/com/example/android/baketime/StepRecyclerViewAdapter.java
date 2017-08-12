package com.example.android.baketime;

import android.content.Context;
import android.content.Intent;
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


    public StepRecyclerViewAdapter(FragmentManager fragmentManager, boolean mTwoPane) {
        mFragmentManager = fragmentManager;
        this.mTwoPane = mTwoPane;
        mValues = new ArrayList<>();
        Log.e(LOG_TAG, "mTwoPane is..." + mTwoPane);
        Log.e(LOG_TAG, "this.mTwoPane is..." + this.mTwoPane);
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
            holder.mIdView.setText("Step");
            holder.mContentView.setText(getStepDescriptionFromJason(mValues.get(position)));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Log.e(LOG_TAG,"inside mTwoPane is true");
//                        Bundle arguments = new Bundle();
//                        arguments.putString(RecipeStepDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
//                        fragment.setArguments(arguments);
                        mFragmentManager.beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                    }else {
                        Log.e(LOG_TAG, "inside ON Click!");
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                        intent.putExtra("step", mValues.get(position));
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

    public String getStepDescriptionFromJason(String step) {
        try {
            JSONObject stepString = new JSONObject(step);
            return stepString.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}