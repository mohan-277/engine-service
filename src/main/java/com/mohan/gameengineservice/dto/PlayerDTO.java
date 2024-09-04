package com.mohan.gameengineservice.dto;

import com.mohan.gameengineservice.entity.Wicket;
import lombok.*;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
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
    private Double strikeRate;
    private Integer numberOf50s;
    private Integer numberOf100s;
    private byte[] profilePicture; // Optional, if used

}
