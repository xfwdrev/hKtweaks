/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.hades.hKtweaks.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hades.hKtweaks.R;
import com.hades.hKtweaks.fragments.BaseFragment;
import com.hades.hKtweaks.utils.AppSettings;
import com.hades.hKtweaks.utils.Utils;
import com.hades.hKtweaks.utils.ViewUtils;
import com.google.android.material.slider.Slider;

/**
 * Created by willi on 09.08.16.
 */

public class BannerResizerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragments);

        initToolBar();

        getSupportActionBar().setTitle(getString(R.string.banner_resizer));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, getFragment(),
                BannerResizerFragment.class.getSimpleName()).commit();
        findViewById(R.id.content_frame).setPadding(0,
                Math.round(ViewUtils.getActionBarSize(this)), 0, 0);
    }

    private Fragment getFragment() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(BannerResizerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BannerResizerFragment();
        }
        return fragment;
    }

    public static class BannerResizerFragment extends BaseFragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_banner_resizer, container, false);

            final int minHeight = Math.round(getResources().getDimension(R.dimen.banner_min_height));
            int defaultHeight = Math.round(getResources().getDimension(R.dimen.banner_default_height));
            int maxHeight = Math.round(getResources().getDimension(R.dimen.banner_max_height));

            final View banner = rootView.findViewById(R.id.banner_view);
            final int px = AppSettings.getBannerSize(getActivity());
            setHeight(banner, px);

            final TextView text = rootView.findViewById(R.id.seekbar_text);
            text.setText(Utils.strFormat("%d" + getString(R.string.px), px));

            final Slider seekBar = rootView.findViewById(R.id.seekbar);
            seekBar.setValueFrom(minHeight);
            seekBar.setValueTo(maxHeight);
            seekBar.setValue(px);

            seekBar.addOnChangeListener((s, value, fromUser) -> {
                text.setText(Utils.strFormat("%d" + getString(R.string.px), (int) value));
                setHeight(banner, (int) value);
            });

            seekBar.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(Slider slider) {
                    // nothing
                }

                @Override
                public void onStopTrackingTouch(Slider slider) {
                    // nothing
                }
            });

            rootView.findViewById(R.id.cancel).setOnClickListener(v ->
                    seekBar.setValue(px)
            );

            rootView.findViewById(R.id.restore).setOnClickListener(v ->
                    seekBar.setValue(defaultHeight)
            );

            rootView.findViewById(R.id.done).setOnClickListener(v -> {
                AppSettings.saveBannerSize((int) seekBar.getValue(), getActivity());
                getActivity().finish();
            });

            return rootView;
        }

        private int getAdjustedSize(int px) {
            return Math.round(px / 2.8f);
        }

        private void setHeight(View banner, int px) {
            ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
            layoutParams.height = getAdjustedSize(px);
            banner.requestLayout();
        }

    }

}
