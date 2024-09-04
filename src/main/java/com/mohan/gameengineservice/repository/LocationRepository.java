package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
