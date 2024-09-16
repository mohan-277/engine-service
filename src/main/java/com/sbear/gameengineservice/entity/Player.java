package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private String specialization;
    private String gender;
    private String country;
    private String userName;
    private String role;
    private Integer playedMatches;
    @Setter
    @Getter
    private Long runs;
    @Getter
    @Setter
    private Long wickets;
    private Integer highScore =0;
    private Integer ballsFaced = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Integer dotBalls = 0;
    private Integer singles = 0;
    private Integer twos = 0;
    private Integer threes = 0;
    private Integer oversBowled = 0;
    private Integer wicketsTaken = 0;
    @Column(name = "is_playing")
    private Boolean isPlaying;

    @Column(name = "is_overseas")
    private Boolean isOverseas;

    @Column(name = "is_backup")
    private Boolean isBackup;

    @Lob
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    private byte[] profilePicture;

    @Lazy
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "is_out", nullable = false)
    private Boolean isOut;

    public Player(String name, LocalDate of, String specialization, String gender, String country) {
        this.name = name;
        this.dateOfBirth = of;
        this.specialization = specialization;
        this.gender = gender;
        this.country = country;
    }


    public Player() {}







}
