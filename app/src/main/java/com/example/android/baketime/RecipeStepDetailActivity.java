package com.example.android.baketime;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

/**
 * An activity representing a single Recipe Step detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeStepListActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity implements View.OnClickListener, ExoPlayer.EventListener{

    private final String LOG_TAG = RecipeStepDetailActivity.class.getSimpleName();
    private final String nonUIFragment = "nonUIStepDetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        if(toolbar != null) {
            if (getIntent().getStringExtra("recipeName") != null) {
                toolbar.setTitle(getIntent().getStringExtra("recipeName"));
            } else {
                toolbar.setTitle(getTitle());
            }
            setSupportActionBar(toolbar);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(RecipeStepDetailFragment.ARG_STEP,
                    getIntent().getStringExtra(RecipeStepDetailFragment.ARG_STEP));
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            if(findViewById(R.id.recipestep_detail_container) != null) {
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipestep_detail_container, fragment)
                    .commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(fragment, nonUIFragment).commit();
            }
        } else {
            if(getSupportFragmentManager().findFragmentByTag(nonUIFragment) != null) {
                RecipeStepDetailFragment fragment = (RecipeStepDetailFragment) getSupportFragmentManager().findFragmentByTag(nonUIFragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(getSupportFragmentManager()
                        .findFragmentByTag(nonUIFragment)).commit();
                RecipeStepDetailFragment uiFragment = new RecipeStepDetailFragment();
                uiFragment.position = fragment.position;
                if(findViewById(R.id.recipestep_detail_container) != null) {
                    Bundle arguments = new Bundle();
                    arguments.putString(RecipeStepDetailFragment.ARG_STEP,
                            getIntent().getStringExtra(RecipeStepDetailFragment.ARG_STEP));
                    uiFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.recipestep_detail_container, uiFragment)
                            .commit();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This will give it 'Back Button' functionality.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
