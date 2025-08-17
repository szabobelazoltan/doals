package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.ResponseHeaderDto;
import hu.szbz.hbc.doals.exceptions.ErrorCode;
import hu.szbz.hbc.doals.exceptions.ServiceException;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryStatus;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.ActorRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TreeTraversal.class, DirectoryObjectProtection.class })
class DirectoryObjectProtectionTest {
    @MockitoBean
    private ActorRepository actorRepository;

    @MockitoBean
    private DirectoryEntryRepository directoryEntryRepository;

    @MockitoBean
    private AccessRepository accessRepository;

    @Autowired
    private DirectoryObjectProtection protection;

    @Test
    void test_getAccessWithShallowPermissionCalculation_returnsAccessObject() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getId), 1)
                .set(field(Actor::getExternalId), "john.doe")
                .create();

        final String entryId = "cb4597df-6616-4376-bc5c-76c700b97530";
        final DirectoryEntry entry = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 1)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getExternalId), entryId)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        when(directoryEntryRepository.findByExternalId(entryId)).thenReturn(Optional.of(entry));

        final Access access = Instancio.of(Access.class)
                .set(field(Access::getPermissionCode), 7)
                .set(field(Access::getActor), actor)
                .set(field(Access::getEntry), entry)
                .create();
        when(accessRepository.findByActorAndEntry(actor, entry)).thenReturn(Optional.of(access));
        when(accessRepository.findOwnershipByActorAndEntry(actor, entry)).thenReturn(Optional.of(Boolean.TRUE));

        final DirectoryObjectAccess result = protection.getAccessWithShallowPermissionCalculation(actor, entryId, Permission.READ);

        assertNotNull(result);
        assertEquals(entry, result.entry());
        assertTrue(result.ownership());
        assertEquals("rwd", result.permissions());
    }

    @Test
    void test_getAccessWithDeepPermissionCalculation() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getId), 1)
                .set(field(Actor::getExternalId), "john.doe")
                .create();

        final String entry1Id = "cb4597df-6616-4376-bc5c-76c700b97530";
        final DirectoryEntry entry1 = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 1)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .set(field(DirectoryEntry::getExternalId), entry1Id)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        when(directoryEntryRepository.findByExternalId(entry1Id)).thenReturn(Optional.of(entry1));

        final Access access1 = Instancio.of(Access.class)
                .set(field(Access::getPermissionCode), 7)
                .set(field(Access::getActor), actor)
                .set(field(Access::getEntry), entry1)
                .create();
        when(accessRepository.findByActorAndEntry(actor, entry1)).thenReturn(Optional.of(access1));
        when(accessRepository.findOwnershipByActorAndEntry(actor, entry1)).thenReturn(Optional.of(Boolean.TRUE));


        final String entry2Id = "3205cb45-061d-4f7d-881e-7dad47a1ecc5";
        final DirectoryEntry entry2 = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 2)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getExternalId), entry2Id)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        when(directoryEntryRepository.findAllByParent(entry1)).thenReturn(List.of(entry2));

        final Access access2 = Instancio.of(Access.class)
                .set(field(Access::getPermissionCode), 1)
                .set(field(Access::getActor), actor)
                .set(field(Access::getEntry), entry2)
                .create();
        when(accessRepository.findByActorAndEntry(actor, entry2)).thenReturn(Optional.of(access2));

        final DirectoryObjectAccess result = protection.getAccessWithDeepPermissionCalculation(actor, entry1Id, Permission.READ);

        assertNotNull(result);
        assertEquals(entry1, result.entry());
        assertTrue(result.ownership());
        assertEquals("r", result.permissions());
    }


    @Test
    void test_getAccessWithShallowPermissionCalculation_whenPermissionMissing_throwsException() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getId), 1)
                .set(field(Actor::getExternalId), "john.doe")
                .create();

        final String entryId = "70479784-3683-43af-9194-a94cc6414b82";
        final DirectoryEntry entry = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 3)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getExternalId), entryId)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .create();
        when(directoryEntryRepository.findByExternalId(entryId)).thenReturn(Optional.of(entry));

        when(accessRepository.findByActorAndEntry(actor, entry)).thenReturn(Optional.empty());
        when(accessRepository.findOwnershipByActorAndEntry(actor, entry)).thenReturn(Optional.empty());

        final ServiceException exception = assertThrows(ServiceException.class, () -> protection.getAccessWithShallowPermissionCalculation(actor, entryId, Permission.READ));
        assertNotNull(exception);
        assertEquals("Permission denied for actor (id=john.doe) on directory entry (id=70479784-3683-43af-9194-a94cc6414b82). Required permission: READ", exception.getMessage());

        final ResponseHeaderDto rpHeader = exception.toResponseHeader();
        assertNotNull(rpHeader);
        assertFalse(rpHeader.isSuccess());
        assertEquals(ErrorCode.PERMISSION_DENIED.name(), rpHeader.getResultCode());
    }
}
