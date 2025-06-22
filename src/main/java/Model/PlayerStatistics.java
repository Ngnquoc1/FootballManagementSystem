package Model;

public class PlayerStatistics {
    private int playerId;
    private String playerName;
    private String nationality;
    private String teamName;
    private String position;
    private int matches;
    private int goals;

    public PlayerStatistics() {
    }

    public PlayerStatistics(int playerId, String playerName, String nationality, String teamName,
                            String position, int matches, int goals, int assists, int yellowCards, int redCards) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.nationality = nationality;
        this.teamName = teamName;
        this.position = position;
        this.matches = matches;
        this.goals = goals;
    }

    // Getters and Setters
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }
}
