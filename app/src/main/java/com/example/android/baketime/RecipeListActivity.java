package com.example.android.baketime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class RecipeListActivity extends AppCompatActivity implements OnTaskCompleted {

    private final String LOG_TAG = RecipeListActivity.class.getSimpleName();

    private RecipeRecyclerViewAdapter mAdapter;


    private boolean mTwoPane = false;

    @Override
    public void onStart(){
        super.onStart();
        FetchRecipeTask task = new FetchRecipeTask();
        task.listener=this;
        task.execute("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;

        setupRecyclerView((RecyclerView) recyclerView, mTwoPane);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, boolean mTwoPane) {
        mAdapter = new RecipeRecyclerViewAdapter(getApplicationContext());
        if(mAdapter != null && recyclerView != null) {
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onTaskCompleted(String[] result) {
        if(result == null) {
            Toasty.error(this, getText(R.string.no_recipe_found), Toast.LENGTH_LONG, true).show();
        }else {
            mAdapter.clear();
            for (int i = 0; i < result.length; i++) {

                String id = null;
                String name = null;
                String ingredients = null;
                String steps = null;
                String image = null;
                try {
                    JSONObject jsonObject = new JSONObject(result[i]);
                    id = jsonObject.get("id").toString();
                    name = jsonObject.get("name").toString();
                    ingredients = jsonObject.get("ingredients").toString();
                    steps = jsonObject.get("steps").toString();
                    image = jsonObject.get("image").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RecipeRecyclerViewAdapter.Recipe recipe = new RecipeRecyclerViewAdapter.Recipe(id, name, ingredients, steps, image);
                mAdapter.add(recipe);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
