package org.app.eventservice.repository;

import org.app.eventservice.entity.Location;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, Long> {
}
