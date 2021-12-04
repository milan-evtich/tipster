package com.milan.tipster.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class RatedFlag {

    private boolean rated;

    private boolean tipmanRated;

    private boolean competitionRated;

    private boolean tcRated;

    private void updateRated() {
        this.rated = tipmanRated & competitionRated & tcRated;
    }

    public void setTipmanRated(Boolean tipmanRated) {
        this.tipmanRated = tipmanRated;
        updateRated();
    }

    public void setCompetitionRated(Boolean competitionRated) {
        this.competitionRated = competitionRated;
        updateRated();
    }

    public void setTcRated(Boolean tcRated) {
        this.tcRated = tcRated;
        updateRated();
    }
}
