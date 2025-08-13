package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRepository extends CrudRepository<Access, Integer> {

    Optional<Access> findByActorAndEntry(Actor actor, DirectoryEntry entry);

    Optional<Boolean> findOwnershipByActorAndEntry(Actor actor, DirectoryEntry entry);

    List<Access> findAllByEntry(DirectoryEntry entry);

    @Query("SELECT a FROM Access a WHERE a.ownership = TRUE AND " +
            "a.actor = actor AND " +
            "a.entry.parent IS NULL")
    Access findActorRootAccess(Actor actor);
}
