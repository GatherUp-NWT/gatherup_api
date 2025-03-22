package org.app.eventservice.repository;

import org.app.eventservice.entity.Agenda;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends CrudRepository<Agenda, Long> {
}
