package com.milan.tipster.model;


import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamId;

    private String code;
    private String name;
    private String nameGr;
    private String flag;
    private String label;
    private Double rating;
    private Long rank; // ?

    public Team(String nameGr) {
        this.nameGr = nameGr;
    }

//    @ManyToMany(mappedBy = "teams")
//    private Set<Competition> competitions = new HashSet<>();
//
//    @ManyToMany(mappedBy = "teams")
//    private Set<Game> games = new HashSet<>();

}
