package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntryTypeEnum;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TreeTraversal.class,
        DirectoryEntryManipulation.class
})
class DirectoryEntryManipulationTest {
    @MockitoBean
    private DirectoryEntryRepository directoryEntryRepository;

    @MockitoBean
    private AccessRepository accessRepository;

    @MockitoBean
    private ExternalIdGenerator externalIdGenerator;

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

        final String externalId = "foo-x";
        when(externalIdGenerator.generate()).thenReturn(externalId);

        final String name = "foo";
        final DirectoryObjectAccess result = manipulation.createEntry(actor, DirectoryEntryTypeEnum.DIRECTORY, parent, name);

        assertNotNull(result);
        assertEquals(entryResult, result.entry());
        assertEquals(actor, result.actor());
        assertEquals(accessResult.isOwnership(), result.ownership());
        assertEquals("rw", result.permissions());

        verify(directoryEntryRepository).save(argThat(e ->
                externalId.equals(e.getExternalId()) &&
                DirectoryEntryType.DIRECTORY.equals(e.getType()) &&
                parent.equals(e.getParent()) &&
                name.equals(e.getName())));
        verify(accessRepository).save(argThat(a ->
                actor.equals(a.getActor()) &&
                entryResult.equals(a.getEntry()) &&
                7 == a.getPermissionCode() &&
                a.isOwnership()));
    }

    @Test
    void test_markUndeleted() {
        final OffsetDateTime deletionDateTime = OffsetDateTime.now();
        final DirectoryEntry activeParent = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "AP")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        final DirectoryEntry inactiveParent = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "IP")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .set(field(DirectoryEntry::getDeletionTimeStamp), deletionDateTime)
                .set(field(DirectoryEntry::getParent), activeParent)
                .create();
        final DirectoryEntry target = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "TAR")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .set(field(DirectoryEntry::getDeletionTimeStamp), deletionDateTime)
                .set(field(DirectoryEntry::getParent), inactiveParent)
                .create();
        final DirectoryEntry child = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "CLD")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .set(field(DirectoryEntry::getDeletionTimeStamp), deletionDateTime)
                .set(field(DirectoryEntry::getParent), target)
                .create();
        when(directoryEntryRepository.findAllByParent(target)).thenReturn(List.of(child));

        manipulation.markUndeleted(target);

        assertEquals(DirectoryEntryStatus.ACTIVE, inactiveParent.getStatus());
        assertEquals(DirectoryEntryStatus.ACTIVE, target.getStatus());
        assertEquals(DirectoryEntryStatus.ACTIVE, child.getStatus());
        assertNull(inactiveParent.getDeletionTimeStamp());
        assertNull(target.getDeletionTimeStamp());
        assertNull(child.getDeletionTimeStamp());
    }

    @Test
    void test_markDeleted_markInactive() {
        final DirectoryEntry target = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "TAR")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        final DirectoryEntry child = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "CLD")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        when(directoryEntryRepository.findAllByParent(target)).thenReturn(List.of(child));

        manipulation.markDeleted(target);

        assertNotNull(target.getDeletionTimeStamp());
        assertEquals(DirectoryEntryStatus.INACTIVE, target.getStatus());
        assertNotNull(child.getDeletionTimeStamp());
        assertEquals(DirectoryEntryStatus.INACTIVE, child.getStatus());
    }

    @Test
    void test_markDeleted_markRemoved() {
        final DirectoryEntry target = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "TAR")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .create();
        final DirectoryEntry child = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "CLD")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .create();
        when(directoryEntryRepository.findAllByParent(target)).thenReturn(List.of(child));

        manipulation.markDeleted(target);

        assertNotNull(target.getDeletionTimeStamp());
        assertEquals(DirectoryEntryStatus.REMOVED, target.getStatus());
        assertNotNull(child.getDeletionTimeStamp());
        assertEquals(DirectoryEntryStatus.REMOVED, child.getStatus());
    }

    @Test
    void test_markDeleted_doNothing_whenAlreadyMarkedRemoved() {
        final DirectoryEntry target = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), "TAR")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.INACTIVE)
                .create();
        when(directoryEntryRepository.findAllByParent(target)).thenReturn(List.of());

        manipulation.markDeleted(target);

        assertNotNull(target.getDeletionTimeStamp());
        assertEquals(DirectoryEntryStatus.REMOVED, target.getStatus());
    }
}
