package com.marcionavarro.authserver.repositories;

import com.marcionavarro.authserver.entities.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

    Optional<OAuthClient> findByClientId(String clientId);

}
