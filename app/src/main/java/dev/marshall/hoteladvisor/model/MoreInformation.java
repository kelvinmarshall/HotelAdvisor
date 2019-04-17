package dev.marshall.hoteladvisor.model;

/**
 * Created by Marshall on 31/03/2018.
 */

public class MoreInformation {
    private String Dining;
    private String Recreation;
    private String Near;
    private String Additional;

    public MoreInformation() {
    }

    public MoreInformation(String dining, String recreation, String near, String additional) {
        Dining = dining;
        Recreation = recreation;
        Near = near;
        Additional = additional;
    }

    public String getDining() {
        return Dining;
    }

    public void setDining(String dining) {
        Dining = dining;
    }

    public String getRecreation() {
        return Recreation;
    }

    public void setRecreation(String recreation) {
        Recreation = recreation;
    }

    public String getNear() {
        return Near;
    }

    public void setNear(String near) {
        Near = near;
    }

    public String getAdditional() {
        return Additional;
    }

    public void setAdditional(String additional) {
        Additional = additional;
    }
}

