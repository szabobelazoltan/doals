package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.endpoints.ws.dto.NameComparisonModeEnum;
import hu.szbz.hbc.doals.endpoints.ws.dto.NameConditionDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.SearchParametersDto;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryListItem;
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
    void test_search() {
        final Actor actor = em.find(Actor.class, -11);
        final SearchParametersDto conditions = new SearchParametersDto();
        conditions.setName(new NameConditionDto());
        conditions.getName().setName("foo-21");
        conditions.getName().setNameComparisonMode(NameComparisonModeEnum.EQUALS);

        final PageRequest page = PageRequest.of(0, 10, Sort.by("name"));
        final Page<DirectoryEntryListItem> result = directoryEntryRepository.search(actor, conditions, page);

        assertNotNull(result);
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getNumberOfElements());
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals("979b93b7-086b-4095-81ed-87de263eda10", result.getContent().get(0).id());
    }

    @Test
    void test_findAllByParent() {
        final Actor actor = em.find(Actor.class, -12);
        final DirectoryEntry parent = em.find(DirectoryEntry.class, -80);

        final PageRequest page = PageRequest.of(0, 5, Sort.by("name"));
        final Page<DirectoryEntryListItem> result = directoryEntryRepository.findAllByParent(actor, parent, page);

        assertNotNull(result);
        assertEquals(20L, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertEquals(5, result.getSize());
        assertEquals(5, result.getNumberOfElements());
        assertNotNull(result.getContent());
        assertEquals(5, result.getContent().size());
        assertEquals("a0dcc562-2d92-471d-8bf0-175aa4b0a9a0", result.getContent().get(0).id());
        assertEquals("5d131082-1c7d-4d3e-ad4d-cad56191c948", result.getContent().get(1).id());
        assertEquals("fd2522ee-d747-4e03-86a3-bed708a15631", result.getContent().get(2).id());
        assertEquals("83e94e44-3b50-404b-809d-27a398f19936", result.getContent().get(3).id());
        assertEquals("79633ea3-88ce-41d4-abc7-c2540b495cf3", result.getContent().get(4).id());
    }
}
