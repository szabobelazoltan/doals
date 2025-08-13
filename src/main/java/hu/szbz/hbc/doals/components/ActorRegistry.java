package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.mq.dto.PublishUserCreatedDg;
import hu.szbz.hbc.doals.exceptions.ErrorCode;
import hu.szbz.hbc.doals.exceptions.ServiceException;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.ActorRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActorRegistry {
    private static final int ROOT_PERMISSION = 3;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private ExternalIdGenerator externalIdGenerator;

    @Autowired
    private ActorRegistry self;

    public Actor getActor(String actorId) {
        return actorRepository.findByExternalId(actorId)
                .orElseThrow(() -> new ServiceException(String.format("Actor is not found with id: %s", actorId), ErrorCode.UNKNOWN_ACTOR));
    }

    public DirectoryObjectAccess getActorRootEntry(String actorId) {
        final Actor actor = getActor(actorId);
        final Access access = accessRepository.findActorRootAccess(actor);
        return new DirectoryObjectAccess(actor, access.getEntry(), access.isOwnership(), Permission.mapToCombinationString(access.getPermissionCode()));
    }

    @Transactional
    public void registerActor(PublishUserCreatedDg.Body actorParams) {
        final Actor actor = actorRepository.save(new Actor(actorParams.getId()));
        final String entryName = actorParams.getName();
        final String entryId = externalIdGenerator.generate();
        final DirectoryEntry rootEntry = directoryEntryRepository.save(DirectoryEntry.createNew(entryId, DirectoryEntryType.DIRECTORY, null, entryName));
        final Access access = Access.createNew(actor, rootEntry, true, ROOT_PERMISSION);
        accessRepository.save(access);
    }
}
