package com.sbear.gameengineservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamSummaryDTO {
    private Integer id;
    private String name;
    private String country;
    private String teamCaptain;
    private String coach;
    private String owner;
}
