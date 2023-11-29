package com.example.demo.User;

import com.example.demo.ProfileImg.ProfileImg;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @OneToOne(mappedBy = "siteUser", fetch = FetchType.LAZY)
    private ProfileImg profileImg;
}
