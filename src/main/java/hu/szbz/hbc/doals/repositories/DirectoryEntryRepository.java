package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.DirectoryEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectoryEntryRepository extends CrudRepository<DirectoryEntry, Integer>, SearchRepository {

    Optional<DirectoryEntry> findByExternalId(String externalId);

    List<DirectoryEntry> findAllByParent(DirectoryEntry parent);
}
