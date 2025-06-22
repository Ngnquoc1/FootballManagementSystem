package Model;

import java.security.Timestamp;
import java.util.List;
import java.util.Map;

public class MatchStatistics {
    private int matchId;
    private Timestamp dateTime;
    private int round;
    private String stadium;
    private int attendance;
    private String homeTeam;
    private String awayTeam;
    private int homeGoals;
    private int awayGoals;
    private List<Map<String, Object>> goals;

    public MatchStatistics() {
    }

    public MatchStatistics(int matchId, Timestamp dateTime, int round, String stadium, int attendance,
                           String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        this.matchId = matchId;
        this.dateTime = dateTime;
        this.round = round;
        this.stadium = stadium;
        this.attendance = attendance;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    // Getters and Setters
    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public List<Map<String, Object>> getGoals() {
        return goals;
    }

    public void setGoals(List<Map<String, Object>> goals) {
        this.goals = goals;
    }
}
