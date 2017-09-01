package com.example.android.baketime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeListActivity extends AppCompatActivity implements OnTaskCompleted {

    private final String LOG_TAG = RecipeListActivity.class.getSimpleName();

    private RecipeRecyclerViewAdapter mAdapter;


    private boolean mTwoPane = false;

    @Override
    public void onStart(){
        super.onStart();
        Log.e(LOG_TAG,"onStart");
        FetchRecipeTask task = new FetchRecipeTask();
        task.listener=this;
        task.execute("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "before setup Recycler View!!!");
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;

//        if (findViewById(R.id.recipe_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            Log.e(LOG_TAG, "setting mTwoPane To true");
//            mTwoPane = true;
//        }
        setupRecyclerView((RecyclerView) recyclerView, mTwoPane);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, boolean mTwoPane) {
        Log.e("setupRecyclerView", "null checks");
        mAdapter = new RecipeRecyclerViewAdapter();
        if(mAdapter != null && recyclerView != null) {
            recyclerView.setAdapter(mAdapter);
            Log.e("after","adapter is set");
        }
    }

    @Override
    public void onTaskCompleted(String[] result) {
        if(result == null) {
            Log.e(LOG_TAG, "no restults :/");
        }else {
            mAdapter.clear();
            for (int i = 0; i < result.length; i++) {

                String id = null;
                String name = null;
                String ingredients = null;
                String steps = null;
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    id = jsonObject.get("id").toString();
                    name = jsonObject.get("name").toString();
                    ingredients = jsonObject.get("ingredients").toString();
                    steps = jsonObject.get("steps").toString();
                    //Log.e(LOG_TAG, "json ID:" + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RecipeRecyclerViewAdapter.Recipe recipe = new RecipeRecyclerViewAdapter.Recipe(id, name, ingredients, steps);
                mAdapter.add(recipe);
                mAdapter.notifyDataSetChanged();
                //Log.e(LOG_TAG, "results:" + i + " " + result[i]);
            }
        }
    }

}
