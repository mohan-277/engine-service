package com.mohan.gameengineservice.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class TeamDTO {
    private String name;
    private String country;
    private String teamCaptain;
    private String coach;
    private String owner;
    private List<PlayerDTO> players;
}
