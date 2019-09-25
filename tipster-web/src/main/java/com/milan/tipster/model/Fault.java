package com.milan.tipster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fault {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long faultId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String url;
    private String message;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}
