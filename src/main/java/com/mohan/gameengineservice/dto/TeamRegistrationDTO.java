package com.mohan.gameengineservice.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TeamRegistrationDTO {
    private Integer teamID;
    private String name;
    private String country;
    private String teamCaptain;
    private String coach;
    private String owner;
    private String groupType;
}
