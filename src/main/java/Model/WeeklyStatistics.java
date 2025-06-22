package Model;


public class WeeklyStatistics {
    private String period;
    private int week;
    private int year;
    private int round;
    private int matchCount;
    private int goalCount;
    private double avgGoals;

    public WeeklyStatistics() {
    }

    public WeeklyStatistics(String period, int matchCount, int goalCount, double avgGoals,
                            int totalAttendance, double avgAttendance, int cardCount,
                            int yellowCardCount, int redCardCount) {
        this.period = period;
        this.matchCount = matchCount;
        this.goalCount = goalCount;
        this.avgGoals = avgGoals;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getGoalCount() {
        return goalCount;
    }

    public void setGoalCount(int goalCount) {
        this.goalCount = goalCount;
    }

    public double getAvgGoals() {
        return avgGoals;
    }

    public void setAvgGoals(double avgGoals) {
        this.avgGoals = avgGoals;
    }
    @Override
    public String toString() {
        return "WeeklyStatistics{" +
                "period='" + period + '\'' +
                ", matchCount=" + matchCount +
                ", goalCount=" + goalCount +
                ", avgGoals=" + avgGoals +
                '}';
    }
}
