package com.pfa.pfaapp.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.PFAMenuInfo;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuGridFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    public MenuGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pfaMenuInfo Parameter 1.
     * @return A new instance of fragment MenuGridFragment.
     */
    public static MenuGridFragment newInstance(PFAMenuInfo pfaMenuInfo) {
        MenuGridFragment fragment = new MenuGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, pfaMenuInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (getArguments() != null) {
//            PFAMenuInfo pfaMenuInfo = (PFAMenuInfo) getArguments().getSerializable(ARG_PARAM1);
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("onCreateActv" , "MenuGridFragment");

        return inflater.inflate(R.layout.fragment_menu_grid, container, false);
    }


}
