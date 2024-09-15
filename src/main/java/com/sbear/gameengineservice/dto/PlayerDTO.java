package com.sbear.gameengineservice.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public PlayerDTO(String name, String specialization, String date, String gender, String country) {
        this.name = name;
        this.dateOfBirth = date;
        this.specialization = specialization;
        this.gender = gender;
        this.country = country;

    }
}
