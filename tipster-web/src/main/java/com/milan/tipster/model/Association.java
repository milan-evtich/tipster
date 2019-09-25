package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long associationId;

    @NotBlank
    @Size(max=255)
    private String name;
    @NotBlank
    @Size(max=255)
    private String code;

}
