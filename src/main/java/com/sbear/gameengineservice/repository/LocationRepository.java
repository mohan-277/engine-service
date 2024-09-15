package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
