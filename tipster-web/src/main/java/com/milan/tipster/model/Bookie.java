package com.milan.tipster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Bookie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookieId;

    @NotBlank
    @Size(max=255)
    private String name;
    @NotBlank
    @Size(max=255)
    private String code;
}
