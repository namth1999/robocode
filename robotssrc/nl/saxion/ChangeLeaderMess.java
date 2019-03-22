package nl.saxion;

import java.io.Serializable;

public class ChangeLeaderMess implements Serializable {
    private String leaderChange;

    public ChangeLeaderMess(String leaderChange) {
        this.leaderChange = leaderChange;
    }

    public String getLeaderChange() {
        return leaderChange;
    }
}
