package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Actor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends CrudRepository<Actor, Integer> {

    Optional<Actor> findByExternalId(String externalId);
}
