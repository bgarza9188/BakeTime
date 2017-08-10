package com.example.android.baketime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A fragment representing a single Recipe Step detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepListFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private final String LOG_TAG = RecipeStepListFragment.class.getName();
    private StepRecyclerViewAdapter mAdapter;
    private final String INGREDIENTS = "ingredients";
    private final String INGREDIENT = "ingredient";
    private final String STEPS = "steps";

    /**
     * The dummy content this fragment is presenting.
     */
    private RecipeRecyclerViewAdapter.Recipe recipe;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepListFragment() {
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e(LOG_TAG,"onStart");
        Log.e(LOG_TAG,"steps: " + getActivity().getIntent().getStringExtra(STEPS));
        Log.e(LOG_TAG,"ingredients: " + getActivity().getIntent().getStringExtra(INGREDIENTS));
        if(getActivity().getIntent().getStringExtra(INGREDIENTS) != null && getActivity().getIntent().getStringExtra(STEPS) != null) {
            StringBuilder ingredients = new StringBuilder();
            try {
                JSONArray ingredientsArray = new JSONArray(getActivity().getIntent().getStringExtra(INGREDIENTS));
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    ingredients.append(ingredientsArray.getJSONObject(i).getString(INGREDIENT));
                    ingredients.append(", ");
                }
                mAdapter.add(ingredients.toString());
                JSONArray stepsArray = new JSONArray(getActivity().getIntent().getStringExtra(STEPS));
                for (int i = 1; i < stepsArray.length(); i++) {
                    mAdapter.add(stepsArray.getJSONObject(i).getString("description"));
                }
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "onCreate");
//
//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//            recipe = RecipeRecyclerViewAdapter.Recipe.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
//        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_list, container, false);

//        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.recipestep_detail)).setText(mItem.details);
//        }

        View recyclerView = rootView;
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView, false);

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, boolean mTwoPane) {
        mAdapter = new StepRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);
    }

}
