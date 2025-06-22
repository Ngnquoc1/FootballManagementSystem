package Model;


public class ComparisonData {
    private String metric;
    private String period1Value;
    private String period2Value;
    private String difference;
    private String percentage;

    public ComparisonData() {
    }

    public ComparisonData(String metric, String period1Value, String period2Value,
                          String difference, String percentage) {
        this.metric = metric;
        this.period1Value = period1Value;
        this.period2Value = period2Value;
        this.difference = difference;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getPeriod1Value() {
        return period1Value;
    }

    public void setPeriod1Value(String period1Value) {
        this.period1Value = period1Value;
    }

    public String getPeriod2Value() {
        return period2Value;
    }

    public void setPeriod2Value(String period2Value) {
        this.period2Value = period2Value;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "ComparisonData{" +
                "metric='" + metric + '\'' +
                ", period1Value='" + period1Value + '\'' +
                ", period2Value='" + period2Value + '\'' +
                ", difference='" + difference + '\'' +
                ", percentage='" + percentage + '\'' +
                '}';
    }
}
