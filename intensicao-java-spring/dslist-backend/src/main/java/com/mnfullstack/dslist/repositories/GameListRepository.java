package com.mnfullstack.dslist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnfullstack.dslist.entities.GameList;

public interface GameListRepository extends JpaRepository<GameList, Long>  {

}
