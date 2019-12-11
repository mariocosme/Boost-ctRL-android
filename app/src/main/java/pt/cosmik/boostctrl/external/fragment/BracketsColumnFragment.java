package pt.cosmik.boostctrl.external.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.cosmik.boostctrl.BoostCtrlApplication;
import pt.cosmik.boostctrl.R;
import pt.cosmik.boostctrl.external.adapter.BracketsCellAdapter;
import pt.cosmik.boostctrl.external.model.ColumnData;
import pt.cosmik.boostctrl.external.model.MatchData;

/**
 * Created by Emil on 21/10/17.
 */

public class BracketsColumnFragment extends Fragment {

    private ColumnData columnData;
    private int sectionNumber = 0;
    private int previousBracketSize;
    private ArrayList<MatchData> list;
    private RecyclerView bracketsRV;

    private BracketsCellAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brackets_column, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        getExtras();
        initAdapter();
    }

    private void initViews() {
        bracketsRV = getView().findViewById(R.id.rv_score_board);
    }

    ArrayList<MatchData> getColumnList() {
        return list;
    }

    private void getExtras() {
        if (getArguments() != null) {
            list = new ArrayList<>();
            columnData = (ColumnData) getArguments().getSerializable("column_data");
            sectionNumber = getArguments().getInt("section_number");
            previousBracketSize = getArguments().getInt("previous_section_size");
            list.addAll(columnData.getMatches());
            setInitialHeightForList();
        }
    }

    int getSectionNumber() {
        return sectionNumber;
    }

    private void setInitialHeightForList() {
        for (MatchData data : list){
            if (sectionNumber == 0){
                data.setHeight(((BoostCtrlApplication)getActivity().getApplication()).dpToPx(131));
            }else if (sectionNumber == 1 && previousBracketSize != list.size()){
                data.setHeight(((BoostCtrlApplication)getActivity().getApplication()).dpToPx(262));
            }else if (sectionNumber == 1 && previousBracketSize == list.size()) {
                data.setHeight(((BoostCtrlApplication)getActivity().getApplication()).dpToPx(131));
            } else if (previousBracketSize > list.size()) {
                data.setHeight(((BoostCtrlApplication)getActivity().getApplication()).dpToPx(262));
            }else if (previousBracketSize == list.size()) {
                data.setHeight(((BoostCtrlApplication)getActivity().getApplication()).dpToPx(131));
            }
        }

    }

    void expandHeight(int height) {

        for (MatchData data : list) {
            data.setHeight(height);
        }
        adapter.setList(list);
    }

    void shrinkView(int height) {
        for (MatchData data : list) {
            data.setHeight(height);
        }
        adapter.setList(list);
    }
    private void initAdapter() {

//        pBar.setVisibility(View.GONE);
         adapter = new BracketsCellAdapter(getContext(), list);
        if (bracketsRV != null) {
            bracketsRV.setHasFixedSize(true);
            bracketsRV.setNestedScrollingEnabled(false);
            bracketsRV.setAdapter(adapter);
            bracketsRV.smoothScrollToPosition(0);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            bracketsRV.setLayoutManager(layoutManager);
            bracketsRV.setItemAnimator(new DefaultItemAnimator());
        }
    }

    int getCurrentBracketSize() {
        return list.size();
    }
    int getPreviousBracketSize() {
        return previousBracketSize;
    }
}
