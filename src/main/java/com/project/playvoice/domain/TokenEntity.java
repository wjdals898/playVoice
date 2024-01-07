package com.project.playvoice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token_entity")
public class TokenEntity {

    @Id
    @Column(nullable = false)
    private long id;

    @Column(name="token", nullable = false)
    private String refreshToken;
}
