package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "coaches")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;

}
