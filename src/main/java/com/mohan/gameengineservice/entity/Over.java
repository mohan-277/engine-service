package com.mohan.gameengineservice.entity;

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
public class Over {
    @Id
    private   long id;
    private   int numbers; //
    @OneToMany
    private List<Ball> balls = new ArrayList<>(); // one over has 6 ball some cases  noball , bouncers  will also need to be calculated


    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings; // This field should match the mappedBy in Innings
}
