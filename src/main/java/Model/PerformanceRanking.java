package Model;

public class PerformanceRanking {
    private String period;
    private int position;
    private double performanceScore;
    private double goalRatio;
    private String notes;

    public PerformanceRanking() {
    }

    public PerformanceRanking(String period, int position, double performanceScore,
                              double goalRatio, double attendanceRatio, String fairPlayScore, String notes) {
        this.period = period;
        this.position = position;
        this.performanceScore = performanceScore;
        this.goalRatio = goalRatio;
        this.notes = notes;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    public double getGoalRatio() {
        return goalRatio;
    }

    public void setGoalRatio(double goalRatio) {
        this.goalRatio = goalRatio;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PerformanceRanking{" +
                "period='" + period + '\'' +
                ", position=" + position +
                ", performanceScore=" + performanceScore +
                ", goalRatio=" + goalRatio +
                ", notes='" + notes + '\'' +
                '}';
    }
}
