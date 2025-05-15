package Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScorerRanking {
    private final IntegerProperty rank;
    private final StringProperty name;
    private final StringProperty club;
    private final IntegerProperty goals;
    private final IntegerProperty penalty;
    private final IntegerProperty matches;
    private final IntegerProperty minutes;

    public ScorerRanking(int rank, String name, String club, int goals, int penalty, int matches, int minutes) {
        this.rank = new SimpleIntegerProperty(rank);
        this.name = new SimpleStringProperty(name);
        this.club = new SimpleStringProperty(club);
        this.goals = new SimpleIntegerProperty(goals);
        this.penalty = new SimpleIntegerProperty(penalty);
        this.matches = new SimpleIntegerProperty(matches);
        this.minutes = new SimpleIntegerProperty(minutes);
    }

    // Getters
    public int getRank() { return rank.get(); }
    public String getName() { return name.get(); }
    public String getClub() { return club.get(); }
    public int getGoals() { return goals.get(); }
    public int getPenalty() { return penalty.get(); }
    public int getMatches() { return matches.get(); }
    public int getMinutes() { return minutes.get(); }

    // Property getters
    public IntegerProperty rankProperty() { return rank; }
    public StringProperty nameProperty() { return name; }
    public StringProperty clubProperty() { return club; }
    public IntegerProperty goalsProperty() { return goals; }
    public IntegerProperty penaltyProperty() { return penalty; }
    public IntegerProperty matchesProperty() { return matches; }
    public IntegerProperty minutesProperty() { return minutes; }
}