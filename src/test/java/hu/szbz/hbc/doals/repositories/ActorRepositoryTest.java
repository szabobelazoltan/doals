package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.model.Actor;
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
@Sql("/sql/ActorRepositoryTest.sql")
class ActorRepositoryTest {
    @Autowired
    private ActorRepository actorRepository;

    @Test
    void test_findByExternalId_returnsExistingEntity() {
        final Optional<Actor> result = actorRepository.findByExternalId("john.doe");
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    void test_findByExternalId_whenMissing_returnsEmptyOptional() {
        final Optional<Actor> result = actorRepository.findByExternalId("gunther.grass");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
