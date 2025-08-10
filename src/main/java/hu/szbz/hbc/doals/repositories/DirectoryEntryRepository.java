package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectoryEntryRepository extends CrudRepository<DirectoryEntry, Integer>, SearchRepository {

    Page<DirectoryEntry> findAllByParent(DirectoryEntry parent, PageRequest pageRequest);

    Optional<DirectoryEntry> findByExternalId(String externalId);

    @Query("SELECT a.entry FROM Access a WHERE a.actor = :actor " +
            "AND a.role = hu.szbz.hbc.doals.model.AccessRole.OWNER " +
            "AND a.entry.parent IS NULL")
    DirectoryEntry findUserRoot(@Param("actor") Actor actor);
}
