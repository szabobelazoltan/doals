package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessRepository extends CrudRepository<Access, Integer> {

    Optional<Access> findByActorAndEntry(Actor actor, DirectoryEntry entry);

    Optional<Boolean> findOwnershipByActorAndEntry(Actor actor, DirectoryEntry entry);
}
