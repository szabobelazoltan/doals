package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntryTypeEnum;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryStatus;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TreeTraversal.class, DirectoryEntryManipulation.class })
class DirectoryEntryManipulationTest {
    @MockitoBean
    private DirectoryEntryRepository directoryEntryRepository;

    @MockitoBean
    private AccessRepository accessRepository;

    @Autowired
    private TreeTraversal treeTraversal;

    @Autowired
    private DirectoryEntryManipulation manipulation;

    @Test
    void test_createEntry() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getId), 1)
                .set(field(Actor::getExternalId), "john.doe")
                .create();
        final DirectoryEntry parent = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 1)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getExternalId), "d201de8c-b432-46fc-b311-7b722718b7b1")
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();

        final DirectoryEntry entryResult = Instancio.create(DirectoryEntry.class);
        when(directoryEntryRepository.save(any())).thenReturn(entryResult);
        final Access accessResult = Instancio.of(Access.class)
                .set(field(Access::getPermissionCode), 3)
                .create();
        when(accessRepository.save(any())).thenReturn(accessResult);

        final String name = "foo";
        final DirectoryObjectAccess result = manipulation.createEntry(actor, DirectoryEntryTypeEnum.DIRECTORY, parent, name);

        assertNotNull(result);
        assertEquals(entryResult, result.entry());
        assertEquals(actor, result.actor());
        assertEquals(accessResult.isOwnership(), result.ownership());
        assertEquals("rw", result.permissions());

        verify(directoryEntryRepository).save(argThat(e ->
                e.getExternalId() != null &&
                DirectoryEntryType.DIRECTORY.equals(e.getType()) &&
                parent.equals(e.getParent()) &&
                name.equals(e.getName())));
        verify(accessRepository).save(argThat(a ->
                actor.equals(a.getActor()) &&
                entryResult.equals(a.getEntry()) &&
                7 == a.getPermissionCode() &&
                a.isOwnership()));
    }
}
