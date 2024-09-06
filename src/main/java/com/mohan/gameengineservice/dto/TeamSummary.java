package com.mohan.gameengineservice.dto;


public interface TeamSummary {   // this for the data collecting from the jpa easy way
    Integer getTeamID();
    Long getCoachId();
    String getName();
    String getCountry();
    String getTeamCaptain();
    String getCoach();

    String getOwner();
}
