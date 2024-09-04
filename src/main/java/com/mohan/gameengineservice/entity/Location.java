package com.mohan.gameengineservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Location {
    @Id
   private long id;
   private String country;
   private String ground;

//    @OneToMany(mappedBy = "location")
//    private List<CricketMatch> matches;
}
