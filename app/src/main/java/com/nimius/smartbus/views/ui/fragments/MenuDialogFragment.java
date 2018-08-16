package com.nimius.smartbus.views.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.FragmentMenuBinding;
import com.nimius.smartbus.views.service.repository.ApiConstant;
import com.nimius.smartbus.views.ui.activities.PickUpInfoActivity;
import com.nimius.smartbus.views.ui.activities.MenuDetailActivity;
import com.nimius.smartbus.views.utility.Constants;

public class MenuDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = "MenuDialogFragment";
    FragmentMenuBinding fragmentMenuBinding;
    private BottomSheetBehavior bottomSheetBehavior;
    private Context mContext;

    public static MenuDialogFragment newInstance() {
        return new MenuDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        fragmentMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_menu, null, false);
        fragmentMenuBinding.setMenuDialogFragment(this);
        dialog.setContentView(fragmentMenuBinding.getRoot());


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) fragmentMenuBinding.getRoot().getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        View parent = (View) fragmentMenuBinding.getRoot().getParent();
        parent.setFitsSystemWindows(true);
        bottomSheetBehavior = BottomSheetBehavior.from(parent);
        fragmentMenuBinding.getRoot().measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        params.height = screenHeight;
        parent.setLayoutParams(params);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    public void onButtonClick(View view) {
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_close_menu:
                dismiss();
                break;
            case R.id.tv_pick_info:
                startActivity(new Intent(getActivity(), PickUpInfoActivity.class));
                break;
            case R.id.tv_terminal:
                intent = new Intent(mContext, MenuDetailActivity.class);
                intent.putExtra(Constants.EXTRA_PRISMIC_DOC_ID, ApiConstant.documentId_terminal);
                startActivity(intent);
                break;
            case R.id.tv_contact_support:
                intent = new Intent(mContext, MenuDetailActivity.class);
                intent.putExtra(Constants.EXTRA_IS_CONTACT_US, true);
                intent.putExtra(Constants.EXTRA_PRISMIC_DOC_ID, ApiConstant.documentId_contact_us);
                startActivity(intent);
                break;
            case R.id.tv_missed_pickup:
                intent = new Intent(mContext, MenuDetailActivity.class);
                intent.putExtra(Constants.EXTRA_PRISMIC_DOC_ID, ApiConstant.documentId_missed_pickup);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


}