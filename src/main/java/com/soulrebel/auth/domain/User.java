package com.soulrebel.auth.domain;

import com.soulrebel.auth.domain.dto.PasswordRecovery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


@ToString
public class User {
    @MappedCollection
    private final Set<Token> tokens = new HashSet<> ();
    @MappedCollection
    private final Set<PasswordRecovery> passwordRecoveries = new HashSet<> ();
    @Getter
    @Id
    private Long id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String tfaSecret;


    @PersistenceConstructor
    private User(Long id, String firstName, String lastName, String email, String password, String tfaSecret,
                 Collection<Token> tokens, Collection<PasswordRecovery> passwordRecoveries) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.tfaSecret = tfaSecret;
        this.tokens.addAll (tokens);
        this.passwordRecoveries.addAll (passwordRecoveries);
    }

    public static User of(String firstName, String lastName, String email, String password) {
        return new User (null, firstName, lastName, email, password,
                null, Collections.emptyList (), Collections.emptyList ());
    }

    public void addToken(Token token) {
        this.tokens.add (token);
    }

    public Boolean removeToken(Token token) {
        return this.tokens.remove (token);
    }

    public Boolean removeTokenId(Predicate<? super Token> predicate) {
        return this.tokens.removeIf (predicate);
    }

    public Boolean removeTokenIf(Predicate<? super Token> predicate) {
        return this.tokens.removeIf (predicate);
    }

    public void addPasswordRecovery(PasswordRecovery passwordRecovery) {
        this.passwordRecoveries.add (passwordRecovery);
    }

    public Boolean removePasswordRecovery(PasswordRecovery passwordRecovery) {
        return this.passwordRecoveries.remove (passwordRecovery);
    }

    public Boolean removePasswordRecoveryIf(Predicate<? super PasswordRecovery> predicate) {
        return this.passwordRecoveries.removeIf (predicate);
    }
}
