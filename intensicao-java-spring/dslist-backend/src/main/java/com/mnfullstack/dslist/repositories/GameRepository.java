package com.mnfullstack.dslist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnfullstack.dslist.entities.Game;

public interface GameRepository extends JpaRepository<Game, Long>  {

}
