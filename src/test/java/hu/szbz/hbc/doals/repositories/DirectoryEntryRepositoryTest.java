package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.endpoints.ws.dto.NameComparisonModeEnum;
import hu.szbz.hbc.doals.endpoints.ws.dto.NameConditionDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.SearchParametersDto;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/DirectoryEntryRepositoryTest.sql")
class DirectoryEntryRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Test
    void test_findUserRoot_returnsEntity_forActor() {
        final Actor actor = em.find(Actor.class, -10);
        final DirectoryEntry result = directoryEntryRepository.findUserRoot(actor);
        assertNotNull(result);
        assertEquals("466cf730-397e-4cb4-b20e-86ad0fee7a03", result.getExternalId());
    }

    @Test
    void test_search() {
        final Actor actor = em.find(Actor.class, -11);
        final SearchParametersDto conditions = new SearchParametersDto();
        conditions.setName(new NameConditionDto());
        conditions.getName().setName("foo-21");
        conditions.getName().setNameComparisonMode(NameComparisonModeEnum.EQUALS);

        final PageRequest page = PageRequest.of(0, 10, Sort.by("name"));
        final Page<DirectoryEntry> result = directoryEntryRepository.search(actor, conditions, page);

        assertNotNull(result);
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getNumberOfElements());
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals("979b93b7-086b-4095-81ed-87de263eda10", result.getContent().get(0).getExternalId());
    }
}
