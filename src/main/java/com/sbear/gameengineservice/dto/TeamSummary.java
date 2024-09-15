package com.sbear.gameengineservice.dto;




public interface TeamSummary {
    Integer getTeamID();
    Long getCoachId();
    String getName();
    String getCountry();
    String getTeamCaptain();
    String getCoachName();

    String getOwner();
}
