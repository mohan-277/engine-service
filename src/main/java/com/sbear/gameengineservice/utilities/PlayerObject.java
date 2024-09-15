package com.sbear.gameengineservice.utilities;

public class PlayerObject {
    private PlayerUtil playerUtil;
    private int score;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private int dotBalls;
    private int singles;
    private int twos;
    private int threes;
    private int oversBowled;
    private int wicketsTaken;
    private int ballsBowled;


    public PlayerObject(PlayerUtil playerUtil) {
        this.playerUtil = playerUtil;
        this.score = 0;
        this.ballsFaced = 0;
        this.fours = 0;
        this.sixes = 0;
    }

    public PlayerUtil getPlayer() { return playerUtil; }
    public void setPlayer(PlayerUtil playerUtil) { this.playerUtil = playerUtil; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }
    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }
    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }
    public int getDotBalls() {
        return dotBalls;
    }

    public void updateStats(int runs) {
        this.score += runs;
        this.ballsFaced += 1;
        if (runs == 4) this.fours += 1;
        if (runs == 6) this.sixes += 1;
    }

    public void addScore(int runs) {
        this.score += runs;
        this.ballsFaced++;
        if (runs == 0) {
            addDotBall();
        }
        switch (runs) {
            case 0: dotBalls++; break;
            case 1: singles++; break;
            case 2: twos++; break;
            case 3: threes++; break;
            case 4: fours++; break;
            case 6: sixes++; break;
        }
    }



    public void addFour() {
        this.fours++;
    }

    public void addSix() {
        this.sixes++;
    }


    public int getSingles() {
        return singles;
    }

    public int getTwos() {
        return twos;
    }

    public int getThrees() {
        return threes;
    }

    public void addBallFaced() {
        this.ballsFaced++;
    }

    public int getOversBowled() {
        return oversBowled;
    }

    public void addOverBowled() {
        this.oversBowled++;
    }

    public int getWicketsTaken() {
        return wicketsTaken;
    }

    public void addWicket() {
        this.wicketsTaken++;
    }

    public void addDotBall() {
        this.dotBalls++;
    }


    public void setWicketsTaken(int wicketsTaken) {
        this.wicketsTaken = wicketsTaken;
    }

    public int getBallsBowled() {
        return ballsBowled;
    }

    public void setBallsBowled(int ballsBowled) {
        this.ballsBowled = ballsBowled;
    }

    public void incrementWickets() {
        this.wicketsTaken++;
    }

    public void incrementBallsBowled() {
        this.ballsBowled++;
    }
}
