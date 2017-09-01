package com.example.android.baketime;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.Context.WINDOW_SERVICE;

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
    public static final String ARG_STEP_POS = "currentStepPosition";
    public static final String ARG_STEPS = "currentStepList";
    private String stepDescription = null;
    private String stepVideoURL = null;
    private String[] stepsArray;
    private int stepPosition = 1;

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

        if (getArguments().containsKey(ARG_STEP) && getArguments().getString(ARG_STEP) != null) {
            Log.e(LOG_TAG, "testing for Args");
            Log.e(LOG_TAG, "ARG_STEP:" + getArguments().getString(ARG_STEP));
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getArguments().getString(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getArguments().getString(ARG_STEP));
        } else {
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getActivity().getIntent().getStringExtra(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getActivity().getIntent().getStringExtra(ARG_STEP));
        }

        //setting current step position
        //TODO might need to handle these buttons differently if tablet mode
        if(getArguments().containsKey(ARG_STEP_POS)){
            stepPosition = getArguments().getInt(ARG_STEP_POS);
        } else {
            stepPosition = getActivity().getIntent().getIntExtra(ARG_STEP_POS, 1);
        }
        stepsArray = getActivity().getIntent().getStringArrayExtra(ARG_STEPS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        SimpleExoPlayerView mPlayerView;
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();

        int orientation = display.getRotation();
        Log.e(LOG_TAG, "orientation:" + orientation);

        // Initialize the player view.
        //TODO this might force fullscreen when in Tablet mode aswell, probably need to handle that separately.
        if(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
            Log.e(LOG_TAG, "Ben getting view from Activity because Landscape");
            mPlayerView = (SimpleExoPlayerView) getActivity().findViewById(R.id.player_view);
        }else{
            rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
            mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.player_view);
        }

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
            //mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            //Log.e(LOG_TAG, "Ben after setting resize mode!");

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

        if(rootView != null) {
            ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(stepDescription);
            final Button nextButton = (Button) rootView.findViewById(R.id.next_button);
            final Button previousButton = (Button) rootView.findViewById(R.id.prev_button);
            //Hide buttons as necessary
            if(stepPosition+1 == stepsArray.length) {
                nextButton.setVisibility(View.INVISIBLE);
            }
            if(stepPosition == 1) {
                previousButton.setVisibility(View.INVISIBLE);
            }

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if((stepPosition+1) < stepsArray.length) {
                    Bundle arguments = new Bundle();
                    FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                    arguments.putString(RecipeStepDetailFragment.ARG_STEP, stepsArray[stepPosition + 1]);
                    arguments.putInt(RecipeStepDetailFragment.ARG_STEP_POS, stepPosition + 1);
                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    mFragmentManager.beginTransaction()
                            .replace(R.id.recipestep_detail_container, fragment)
                            .commit();
                }
                Log.e(LOG_TAG,"Ben in onclick of next button");
                }
            });



            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((stepPosition-1) >= 1) {
                        Bundle arguments = new Bundle();
                        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                        arguments.putString(RecipeStepDetailFragment.ARG_STEP, stepsArray[stepPosition - 1]);
                        arguments.putInt(RecipeStepDetailFragment.ARG_STEP_POS, stepPosition - 1);
                        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                        fragment.setArguments(arguments);
                        mFragmentManager.beginTransaction()
                                .replace(R.id.recipestep_detail_container, fragment)
                                .commit();
                    }
                    Log.e(LOG_TAG,"Ben in onclick of prev button");
                }
            });



            return rootView;
        }

        return mPlayerView;
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
