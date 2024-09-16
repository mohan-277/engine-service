package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Table(name = "over")
public class Over {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private   Long id;
    private   int numbers; //
    @OneToMany
    private List<Ball> balls = new ArrayList<>(); // one over has ball 6 some cases no Ball & bouncers will also need to be calculated


    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings; // This field should match the mappedBy in Innings


}
