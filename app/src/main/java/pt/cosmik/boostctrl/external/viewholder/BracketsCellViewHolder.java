package pt.cosmik.boostctrl.external.viewholder;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import pt.cosmik.boostctrl.R;
import pt.cosmik.boostctrl.external.animation.SlideAnimation;

/**
 * Created by Emil on 21/10/17.
 */

public class BracketsCellViewHolder extends RecyclerView.ViewHolder {

    private TextView homeTeamName;
    private TextView awayTeamName;
    private TextView homeTeamScore;
    private TextView awayTeamScore;
    private ImageView homeTeamIV;
    private ImageView awayTeamIV;
    private RelativeLayout rootLayout;

    public BracketsCellViewHolder(View itemView) {
        super(itemView);
        homeTeamName = itemView.findViewById(R.id.home_team_name_text);
        awayTeamName = itemView.findViewById(R.id.away_team_name_text);
        homeTeamScore = itemView.findViewById(R.id.home_team_score_text);
        awayTeamScore = itemView.findViewById(R.id.away_team_score_text);
        homeTeamIV = itemView.findViewById(R.id.home_team_iv);
        awayTeamIV = itemView.findViewById(R.id.away_team_iv);
        rootLayout = itemView.findViewById(R.id.layout_root);
    }

    public void setAnimation(int height) {
        Animation animation = new SlideAnimation(rootLayout, rootLayout.getHeight(),
                height);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        rootLayout.setAnimation(animation);
        rootLayout.startAnimation(animation);
    }

    public TextView getAwayTeamName() {
        return awayTeamName;
    }

    public TextView getHomeTeamScore() {
        return homeTeamScore;
    }

    public TextView getAwayTeamScore() {
        return awayTeamScore;
    }

    public TextView getHomeTeamName() {
        return homeTeamName;
    }

    public ImageView getHomeTeamIV() {
        return homeTeamIV;
    }

    public ImageView getAwayTeamIV() {
        return awayTeamIV;
    }
}
