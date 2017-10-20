package com.utn.mobile.keepapp;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class RecordsAndExercisesFragment extends Fragment {

    private AppBarLayout appBar;
    private TabLayout tabs;
    private ViewPager viewPager;

    public RecordsAndExercisesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_records_and_exercises, container, false);
        insertTabs(container);

        viewPager = (ViewPager) rootView.findViewById(R.id.pagerRecords);
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        return rootView;
    }

    private void insertTabs(ViewGroup container) {
        View parent = (View) container.getParent();
        appBar = (AppBarLayout) parent.findViewById(R.id.appbar);
        tabs = new TabLayout(getContext());
        //tabs.setTabTextColors(R.color.colorNonSelected, R.color.colorSelected);
        appBar.addView(tabs);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsAdapter adapter = new SectionsAdapter(getFragmentManager());
        adapter.addFragment(new RecordsFragment(), "Records");
        adapter.addFragment(new EjerciciosFragment(), "Ejercicios"); //TODO crear fragmento
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBar.removeView(tabs);
    }

    public class SectionsAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
