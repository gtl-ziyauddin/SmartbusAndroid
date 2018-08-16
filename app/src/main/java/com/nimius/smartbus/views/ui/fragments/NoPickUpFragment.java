package com.nimius.smartbus.views.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.FragmentNoPickupBinding;
import com.nimius.smartbus.views.callback.ReplaceFragmentCallback;


public class NoPickUpFragment extends BaseFragment {
    ReplaceFragmentCallback replaceFragmentCallback;
    FragmentNoPickupBinding noPickupBinding;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        replaceFragmentCallback = (ReplaceFragmentCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setView(R.layout.fragment_no_pickup, true);

    }


    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
        noPickupBinding = getBinding();
        noPickupBinding.setNoPickUpFragment(this);
    }

    @Override
    public void loadData() {

    }


    /**
     * CLICK EVENT LISTENER
     *
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_another_pick:
                AuthenticateFragment authenticateFragment = new AuthenticateFragment();
                replaceFragmentCallback.changeFragment(authenticateFragment, "");
                break;

        }
    }


}
