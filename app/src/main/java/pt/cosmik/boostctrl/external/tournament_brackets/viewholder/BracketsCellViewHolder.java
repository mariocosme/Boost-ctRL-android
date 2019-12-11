package pt.cosmik.boostctrl.external.tournament_brackets.viewholder;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import pt.cosmik.boostctrl.R;
import pt.cosmik.boostctrl.external.tournament_brackets.animation.SlideAnimation;

/**
 * Created by Emil on 21/10/17.
 */

public class BracketsCellViewHolder extends RecyclerView.ViewHolder {

    private TextView teamOneName;
    private TextView teamTwoName;
    private TextView teamOneScore;
    private TextView teamTwoScore;
    private RelativeLayout rootLayout;

    public BracketsCellViewHolder(View itemView) {
        super(itemView);
        teamOneName = itemView.findViewById(R.id.team_one_name);
        teamTwoName = itemView.findViewById(R.id.team_two_name);
        teamOneScore = itemView.findViewById(R.id.team_one_score);
        teamTwoScore = itemView.findViewById(R.id.team_two_score);
        rootLayout = itemView.findViewById(R.id.layout_root);
    }

    public void setAnimation(int height){
        Animation animation = new SlideAnimation(rootLayout, rootLayout.getHeight(),
                height);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        rootLayout.setAnimation(animation);
        rootLayout.startAnimation(animation);
    }

    public TextView getTeamTwoName() {
        return teamTwoName;
    }

    public TextView getTeamOneScore() {
        return teamOneScore;
    }

    public TextView getTeamTwoScore() {
        return teamTwoScore;
    }

    public TextView getTeamOneName() {
        return teamOneName;
    }
}
