package com.example.android.baketime;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * A fragment representing a single Recipe Step detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment {

    private final String LOG_TAG = RecipeStepDetailFragment.class.getSimpleName();
    private SimpleExoPlayer mPlayer;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_STEP = "stepObject";
    private String stepDescription = null;
    private String stepVideoURL = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "onCreate");

        //TODO might need to do something here for two panes VS single pane
        if (getArguments().containsKey(ARG_STEP) && getArguments().getString(ARG_STEP) != null) {
            Log.e(LOG_TAG, "testing for Args");
            Log.e(LOG_TAG, "ARG_STEP:" + getArguments().getString(ARG_STEP));
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getArguments().getString(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getArguments().getString(ARG_STEP));
        } else {
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getActivity().getIntent().getStringExtra(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getActivity().getIntent().getStringExtra(ARG_STEP));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
        // Initialize the player view.
        SimpleExoPlayerView mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
        if(stepVideoURL != null && !stepVideoURL.isEmpty()) {
            Log.e(LOG_TAG, "stepVideoURL:" + stepVideoURL);

            // 1. Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();


            // 2. Create the player
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);


            // Bind the player to the view.
            mPlayerView.setPlayer(mPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mPlayer.addListener((ExoPlayer.EventListener) getActivity());

            // Prepare the MediaSource.
            // Produces DataSource instances through which media data is loaded.
            // This is the MediaSource representing the media to be played.
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(stepVideoURL), new DefaultHttpDataSourceFactory(
                    "BakeTime"), new DefaultExtractorsFactory(), null, null);
            // Prepare the player with the source.
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        } else {
            // 1. Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            // 2. Create the player
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

            // Bind the player to the view.
            mPlayerView.setPlayer(mPlayer);
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.question_mark));
        }

        ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(stepDescription);

        return rootView;
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }
    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayer != null)
            releasePlayer();
    }
}
