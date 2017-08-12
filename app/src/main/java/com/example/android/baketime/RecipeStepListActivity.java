package com.example.android.baketime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * An activity representing a list of Recipe Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity{

    private final String LOG_TAG = RecipeStepListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG,"onCreate");
        setContentView(R.layout.activity_recipestep_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(getIntent().getStringExtra("recipeName") != null){
            Log.e(LOG_TAG,"setting name: " + getIntent().getStringExtra("recipeName"));
            toolbar.setTitle(getIntent().getStringExtra("recipeName"));
        }else{
            toolbar.setTitle(getTitle());
        }
        setSupportActionBar(toolbar);


        if (findViewById(R.id.step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/layout-w600dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.e(LOG_TAG,"found detail container");
            getIntent().putExtra("mTwoPane", true);
        }

        if (savedInstanceState == null) {
            RecipeStepListFragment fragment = new RecipeStepListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_list_container, fragment)
                    .commit();
        }
    }
}

