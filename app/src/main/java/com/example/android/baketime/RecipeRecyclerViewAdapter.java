package com.example.android.baketime;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jebus on 6/1/2017.
 */

public class RecipeRecyclerViewAdapter
        extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private final List<Recipe> mValues;
    private Context mContext;
    private final String LOG_TAG = RecipeRecyclerViewAdapter.class.getSimpleName();

    public RecipeRecyclerViewAdapter(Context context) {
        mContext = context;
        mValues = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mRecipeView.setText(mValues.get(position).name);

        if(mValues.get(position).image != null && !mValues.get(position).image.isEmpty()) {
            Picasso.with(mContext).load(mValues.get(position).image).into(holder.mImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra("recipeName", mValues.get(position).name);
            intent.putExtra("steps", mValues.get(position).steps);
            intent.putExtra("ingredients", mValues.get(position).ingredients);
            context.startActivity(intent);
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
        public final ImageView mImageView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mRecipeView;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.recipe_image);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mRecipeView = (TextView) view.findViewById(R.id.recipe);
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
        public final String image;

        public Recipe(String id, String name, String ingredients, String steps, String image) {
            this.id = id;
            this.name = name;
            this.ingredients = ingredients;
            this.steps = steps;
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}