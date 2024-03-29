package pt.cosmik.boostctrl.external.model;

import java.io.Serializable;

/**
 * Created by Emil on 21/10/17.
 */

public class CompetitorData implements Serializable {

    private String name;
    private String score;
    private String image;

    public CompetitorData(String name, String score, String image) {
        this.name = name;
        this.score = score;
        this.image = image;
    }

    public String getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }
}
