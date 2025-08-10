package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/AccessRepositoryTest.sql")
class AccessRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private AccessRepository accessRepository;

    @Test
    void test_findByActorAndEntry_whenExistsForActorAndEntry_returnsEntity() {
        final Actor actor = em.find(Actor.class, -5);
        final DirectoryEntry entry = em.find(DirectoryEntry.class, -10000);

        final Optional<Access> result = accessRepository.findByActorAndEntry(actor, entry);

        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    void test_findByActorAndEntry_whenMissesForActorAndEntry_returnsEmptyOptional() {
        final Actor actor = em.find(Actor.class, -6);
        final DirectoryEntry entry = em.find(DirectoryEntry.class, -10001);

        final Optional<Access> result = accessRepository.findByActorAndEntry(actor, entry);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
