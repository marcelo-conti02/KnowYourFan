package com.furia.know_your_fan.entity;

import jakarta.persistence.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false)
    private String name;
    
    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private String password;

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }
    
    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;    

    @Min(0)
    @Column(nullable = false)
    private Integer age;

    @Embedded
    @NotNull
    private Address address;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String cpf;
}
