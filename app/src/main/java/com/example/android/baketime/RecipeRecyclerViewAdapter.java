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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jebus on 6/1/2017.
 */

public class RecipeRecyclerViewAdapter
        extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private final List<Recipe> mValues;

    private boolean mTwoPane = false;

    private FragmentManager mFragmentManager;

    private final String LOG_TAG = RecipeRecyclerViewAdapter.class.getSimpleName();

    public RecipeRecyclerViewAdapter(FragmentManager fragmentManager, boolean mTwoPane) {
        mFragmentManager = fragmentManager;
        mValues = new ArrayList<>();
        this.mTwoPane = mTwoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipestep_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Log.e(LOG_TAG,"inside mTwoPane is true");
                    Bundle arguments = new Bundle();
                    arguments.putString(RecipeStepDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    mFragmentManager.beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Log.e(LOG_TAG,"inside mTwoPane is false");
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RecipeStepListActivity.class);
                    //intent.putExtra(RecipeStepDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    intent.putExtra("recipeName", mValues.get(position).name);
                    intent.putExtra("steps", mValues.get(position).steps);
                    intent.putExtra("ingredients", mValues.get(position).ingredients);
                    context.startActivity(intent);
                }
            }
        });
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

    public void add(Recipe test) {
        if(mValues != null) {
            mValues.add(test);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * Recipe
     */
    public static class Recipe {
        public final String id;
        public final String name;
        public final String ingredients;
        public final String steps;

        public Recipe(String id, String name, String ingredients, String steps) {
            this.id = id;
            this.name = name;
            this.ingredients = ingredients;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}