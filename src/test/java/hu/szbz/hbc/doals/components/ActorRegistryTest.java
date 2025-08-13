package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.mq.dto.PublishUserCreatedDg;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.ActorRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActorRegistryTest {
    private static final int ROOT_PERMISSION = 3;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private DirectoryEntryRepository directoryEntryRepository;

    @Mock
    private AccessRepository accessRepository;

    @Mock
    private ExternalIdGenerator externalIdGenerator;

    @Mock
    private ActorRegistry self;

    @InjectMocks
    private ActorRegistry registry;

    @Test
    void test_getActorRootEntry_retreiveExistingEntry() {
        final Actor actor = Instancio.create(Actor.class);
        final DirectoryEntry root = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .create();
        final Access access = Access.createNew(actor, root, true, 3);

        when(actorRepository.findByExternalId(actor.getExternalId())).thenReturn(Optional.of(actor));
        when(accessRepository.findActorRootAccess(actor)).thenReturn(access);

        final DirectoryObjectAccess result = registry.getActorRootEntry(actor.getExternalId());

        assertNotNull(result);
        assertEquals(actor, result.actor());
        assertEquals(root, result.entry());
        assertTrue(result.ownership());
        assertEquals("rw", result.permissions());
    }

    @Test
    void test_registerActor() {
        final PublishUserCreatedDg.Body dgBody = new PublishUserCreatedDg.Body();
        dgBody.setId("john.doe");
        dgBody.setName("John Doe");

        final Actor actor = Instancio.create(Actor.class);
        final ArgumentCaptor<Actor> actorCaptor = ArgumentCaptor.forClass(Actor.class);
        when(actorRepository.save(actorCaptor.capture())).thenReturn(actor);

        final DirectoryEntry root = Instancio.create(DirectoryEntry.class);
        final ArgumentCaptor<DirectoryEntry> entryCaptor = ArgumentCaptor.forClass(DirectoryEntry.class);
        when(directoryEntryRepository.save(entryCaptor.capture())).thenReturn(root);

        final Access access = Instancio.create(Access.class);
        final ArgumentCaptor<Access> accessCaptor = ArgumentCaptor.forClass(Access.class);
        when(accessRepository.save(accessCaptor.capture())).thenReturn(access);

        registry.registerActor(dgBody);

        final Actor createdActor = actorCaptor.getValue();
        assertEquals(dgBody.getId(), createdActor.getExternalId());

        final DirectoryEntry createdEntry = entryCaptor.getValue();
        assertEquals(DirectoryEntryType.DIRECTORY, createdEntry.getType());
        assertEquals(dgBody.getName(), createdEntry.getName());
        assertNull(createdEntry.getParent());

        final Access createdAccess = accessCaptor.getValue();
        assertEquals(actor, createdAccess.getActor());
        assertEquals(root, createdAccess.getEntry());
        assertEquals(3, createdAccess.getPermissionCode());
        assertTrue(createdAccess.isOwnership());
    }
}
