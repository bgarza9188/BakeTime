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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
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
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

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
     * The fragment argument representing the step ID that this fragment
     * represents.
     */
    public static final String ARG_STEP_DESCRIPTION = "stepShortDescription";
    public static final String ARG_STEP = "stepObject";
    public static final String ARG_STEP_POS = "currentStepPosition";
    public static final String ARG_STEPS = "currentStepList";
    public static final String ARG_TWO_PANE_FLAG = "isTwoPaneMode";
    private String stepDescription = null;
    private String stepVideoURL = null;
    private String stepThumbnailURL = null;
    private String[] stepsArray;
    private int stepPosition = 1;
    private long position;
    private String SELECTED_POSITION = "selectedPosition";
    private SimpleExoPlayerView mPlayerView = null;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepDetailFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayerView != null && position != C.TIME_UNSET) {
            initializePlayer(mPlayerView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            position = mPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If the fragment arguments are present this means that the fragment was started by either
        //the tablet mode activity or itself.
        if (getArguments().containsKey(ARG_STEP) && getArguments().getString(ARG_STEP) != null) {
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getArguments().getString(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getArguments().getString(ARG_STEP));
            stepThumbnailURL = StepRecyclerViewAdapter.getStepThumbnailURL(getArguments().getString(ARG_STEP));
        } else {
            stepDescription = StepRecyclerViewAdapter.getStepDescriptionFromJSON(getActivity().getIntent().getStringExtra(ARG_STEP));
            stepVideoURL = StepRecyclerViewAdapter.getStepVideoURL(getActivity().getIntent().getStringExtra(ARG_STEP));
            stepThumbnailURL = StepRecyclerViewAdapter.getStepThumbnailURL(getActivity().getIntent().getStringExtra(ARG_STEP));
        }

        //Setting current step position
        if(getArguments().containsKey(ARG_STEP_POS)){
            stepPosition = getArguments().getInt(ARG_STEP_POS);
        } else {
            stepPosition = getActivity().getIntent().getIntExtra(ARG_STEP_POS, 1);
        }
        //Setting array of steps.
        if(getActivity().getIntent().getStringArrayExtra(ARG_STEPS) != null) {
            stepsArray = getActivity().getIntent().getStringArrayExtra(ARG_STEPS);
        }
        //Setting video player position.
        position = C.TIME_UNSET;
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong(SELECTED_POSITION, C.TIME_UNSET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int orientation = display.getRotation();
        boolean isTwoPane = false;

        //Determine if we're in tablet mode.
        if(getArguments().containsKey(ARG_TWO_PANE_FLAG)
                && getArguments().getBoolean(ARG_TWO_PANE_FLAG)){
            isTwoPane = true;
        }

        // Initialize the player view. This is done dynamically to handle orientation changes.
        if(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270 && isTwoPane == false) {
            //{LANDSCAPE}
            // Hide status bar
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mPlayerView = (SimpleExoPlayerView) getActivity().findViewById(R.id.player_view);
        }else{
            rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
            mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.player_view);
        }

        //Initialize Player
        if(savedInstanceState == null) {
            initializePlayer(mPlayerView);
        }

        if(rootView != null) {
            ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(stepDescription);
            ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.step_thumbnail);
            //Setting thumbnail image if any.
            if(stepThumbnailURL != null && !stepThumbnailURL.isEmpty()) {
                Picasso.with(getContext()).load(stepThumbnailURL).into(thumbnailImageView);
            }
            final Button nextButton = (Button) rootView.findViewById(R.id.next_button);
            final Button previousButton = (Button) rootView.findViewById(R.id.prev_button);
            //Hide buttons as necessary
            if(stepsArray == null || stepPosition+1 == stepsArray.length) {
                nextButton.setVisibility(View.INVISIBLE);

            }
            if(stepsArray == null || stepPosition == 1) {
                previousButton.setVisibility(View.INVISIBLE);
            }
            //If at end of step list in phone mode.
            if(stepsArray != null && stepPosition+1 == stepsArray.length){
                Toasty.success(getActivity(), getText(R.string.success_msg), Toast.LENGTH_LONG, true).show();
            }
            //If next button is clicked
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
                }
            });
            //If previous button is clicked
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
                }
            });
            return rootView;
        }

        return mPlayerView;
    }

    private void initializePlayer(SimpleExoPlayerView mPlayerView) {
        if(stepVideoURL != null && !stepVideoURL.isEmpty()) {
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
            if (position != C.TIME_UNSET) {
                mPlayer.seekTo(position);
            }
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        } else {
            //This player will show a blank video since one wasn't provided with the step.
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
                    (getResources(), R.drawable.video_not_available));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putLong(SELECTED_POSITION, position);
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
