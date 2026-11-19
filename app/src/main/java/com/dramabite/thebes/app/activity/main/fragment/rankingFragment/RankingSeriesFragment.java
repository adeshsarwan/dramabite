package com.dramabite.thebes.app.activity.main.fragment.rankingFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dramabite.thebes.app.R;
import com.dramabite.thebes.app.adapter.RankingAdapter;
import com.dramabite.thebes.app.ads.PremiumPlanManager;
import com.dramabite.thebes.app.applicationPreference.ControlPreference;
import com.dramabite.thebes.app.databinding.FragmentRankingSeriesBinding;
import com.dramabite.thebes.app.engineBox.model.rank.RankSeriesModel;
import com.dramabite.thebes.app.mvvmRepo.model.RankingSeriesViewModel;

import java.util.ArrayList;
import java.util.List;

public class RankingSeriesFragment extends Fragment {
    FragmentRankingSeriesBinding binding;
    private RankingSeriesViewModel viewModel;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            binding = FragmentRankingSeriesBinding.inflate(inflater, container, false);
            binding.recycleView.setLayoutManager(
                    new LinearLayoutManager(getContext()));
            binding.recycleView.setHasFixedSize(true);
            rootView = binding.getRoot();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        setupObserver();

        return rootView;
    }
    private void setupObserver() {

        viewModel = new ViewModelProvider(this).get(RankingSeriesViewModel.class);

        viewModel.getRankingSeries().observe(getViewLifecycleOwner(), response -> {

            if (response != null
                    && response.getData() != null
                    && !response.getData().isEmpty()) {



                if (binding.recycleView.getAdapter() == null) {
                    List<RankSeriesModel.Datum> dataList = response.getData();
                    List<Object> items = new ArrayList<>();
                    int adPosition = ControlPreference.get_Home_Ranking_Ads_Position();

                    if (adPosition > 0
                            && dataList != null
                            && !PremiumPlanManager.shouldSkipAfterLoginAd(getContext())) {
                        for (int i = 0; i < dataList.size(); i++) {
                            items.add(dataList.get(i));
                            if ((i + 1) % adPosition == 0 && i != dataList.size() - 1) {
                                items.add("AD_ITEM");
                            }
                        }
                    } else if (dataList != null) {
                        items.addAll(dataList);
                    }

                    RankingAdapter adapter = new RankingAdapter(
                            getContext(),
                            items
                    );
                    binding.recycleView.setAdapter(adapter);
                }

            } else {
            }
        });
    }

}