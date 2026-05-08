package com.marcionavarro.authserver.entities;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "tb_user")
public class UserEntity implements UserDetails {

    // ---- Campos internos do banco --------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    public UserEntity() {
    }

    public UserEntity(Long id,
                      String password,
                      String sub,
                      String name,
                      String picture,
                      String email,
                      boolean emailVerified) {
        this.id = id;
        this.password = password;
        this.sub = sub;
        this.name = name;
        this.picture = picture;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    // ---- Scope openid -------------------------------------

    /* Subject identifier exposto nos tokens. UUID - não vaza dado pessoal. */
    @Column(unique = true, nullable = false)
    private String sub;

    // ---- Scope Profile -------------------------------------
    private String name;
    private String picture;

    // ---- Scope Email -------------------------------------
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
