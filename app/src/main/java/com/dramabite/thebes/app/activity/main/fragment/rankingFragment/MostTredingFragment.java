package com.dramabite.thebes.app.activity.main.fragment.rankingFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dramabite.thebes.app.R;
import com.dramabite.thebes.app.databinding.FragmentMostTredingBinding;


public class MostTredingFragment extends Fragment {

  FragmentMostTredingBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentMostTredingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}