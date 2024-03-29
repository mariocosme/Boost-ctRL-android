package pt.cosmik.boostctrl.external.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import pt.cosmik.boostctrl.external.fragment.BracketsColumnFragment;
import pt.cosmik.boostctrl.external.model.ColumnData;


/**
 * Created by Emil on 21/10/17.
 */

public class BracketsSectionAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<ColumnData> sectionList;


    public BracketsSectionAdapter(FragmentManager fm, ArrayList<ColumnData> sectionList) {
        super(fm);
        this.sectionList = sectionList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("column_data", this.sectionList.get(position));
        BracketsColumnFragment fragment = new BracketsColumnFragment();
        bundle.putInt("section_number", position);
        if (position > 0)
            bundle.putInt("previous_section_size", sectionList.get(position - 1).getMatches().size());
        else bundle.putInt("previous_section_size", sectionList.get(position).getMatches().size());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return this.sectionList.size();
    }

    public void setItems(ArrayList<ColumnData> items) {
        this.sectionList = items;
        this.notifyDataSetChanged();
    }
}
