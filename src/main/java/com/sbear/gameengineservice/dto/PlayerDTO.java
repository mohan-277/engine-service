package com.sbear.gameengineservice.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PlayerDTO {
    private Long id;
    private String name;
    private String dateOfBirth;
    private String specialization;
    private String gender;
    private String country;
    private Integer playedMatches;
    private Long runs;
    private Long wickets;
    private Integer highScore;

    private byte[] profilePicture; // Optional, if used

    public PlayerDTO(long id) {
        this.id = id;
    }
}
