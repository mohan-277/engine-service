package com.sbear.gameengineservice.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRegistrationDTO {
    private Integer teamID;
    private String name;
    private String country;
    private String teamCaptain;
    private String coachName;
    private String owner;
    private String groupType;
    private Integer coachId;
}
