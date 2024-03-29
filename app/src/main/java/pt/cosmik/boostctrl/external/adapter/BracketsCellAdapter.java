package pt.cosmik.boostctrl.external.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pt.cosmik.boostctrl.R;
import pt.cosmik.boostctrl.external.model.MatchData;
import pt.cosmik.boostctrl.external.viewholder.BracketsCellViewHolder;

/**
 * Created by Emil on 21/10/17.
 */

public class BracketsCellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<MatchData> list;
    private Context context;

    public BracketsCellAdapter(Context context, ArrayList<MatchData> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_cell_brackets, parent, false);
        return new BracketsCellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BracketsCellViewHolder viewHolder;
        if (holder instanceof BracketsCellViewHolder){
            viewHolder = (BracketsCellViewHolder) holder;
            setFields(viewHolder, position);
        }
    }

    private void setFields(final BracketsCellViewHolder viewHolder, final int position) {
        boolean handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.setAnimation(list.get(position).getHeight());
            }
        }, 100);

        viewHolder.getAwayTeamName().setVisibility(View.VISIBLE);
        viewHolder.getAwayTeamScore().setVisibility(View.VISIBLE);
        viewHolder.getAwayTeamIV().setVisibility(View.VISIBLE);

        if (list.get(position).getCompetitorTwo() == null) {
            viewHolder.getHomeTeamName().setText(list.get(position).getCompetitorOne().getName());
            viewHolder.getHomeTeamScore().setText(list.get(position).getCompetitorOne().getScore());
            Glide.with(context).load(list.get(position).getCompetitorOne().getImage()).into(viewHolder.getHomeTeamIV());

            viewHolder.getAwayTeamName().setVisibility(View.GONE);
            viewHolder.getAwayTeamScore().setVisibility(View.GONE);
            viewHolder.getAwayTeamIV().setVisibility(View.GONE);
        }
        else {
            viewHolder.getHomeTeamName().setText(list.get(position).getCompetitorOne().getName());
            viewHolder.getAwayTeamName().setText(list.get(position).getCompetitorTwo().getName());
            viewHolder.getHomeTeamScore().setText(list.get(position).getCompetitorOne().getScore());
            viewHolder.getAwayTeamScore().setText(list.get(position).getCompetitorTwo().getScore());
            Glide.with(context).load(list.get(position).getCompetitorOne().getImage()).into(viewHolder.getHomeTeamIV());
            Glide.with(context).load(list.get(position).getCompetitorTwo().getImage()).into(viewHolder.getAwayTeamIV());
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(ArrayList<MatchData> columnList) {
        this.list = columnList;
        notifyDataSetChanged();
    }
}
