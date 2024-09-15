package com.sbear.gameengineservice.utilities;

import java.util.ArrayList;
import java.util.List;

public class TeamUtil {
    private String name;
    private List<PlayerUtil> playerUtils;
    private String country;
    private String teamCaptain;
    private String coach;
    private String owner;
    private int totalPoints;
    private byte[] logo;
    private String icon;

    public TeamUtil(String name) {
        this.name = name;
        this.playerUtils = new ArrayList<>();
    }

    public void addPlayer(PlayerUtil playerUtil) {
        playerUtils.add(playerUtil);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<PlayerUtil> getPlayers() { return playerUtils; }
    public void setPlayers(List<PlayerUtil> playerUtils) { this.playerUtils = playerUtils; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getTeamCaptain() { return teamCaptain; }
    public void setTeamCaptain(String teamCaptain) { this.teamCaptain = teamCaptain; }
    public String getCoach() { return coach; }
    public void setCoach(String coach) { this.coach = coach; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public byte[] getLogo() { return logo; }
    public void setLogo(byte[] logo) { this.logo = logo; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

}
