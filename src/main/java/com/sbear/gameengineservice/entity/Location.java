package com.sbear.gameengineservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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

}
