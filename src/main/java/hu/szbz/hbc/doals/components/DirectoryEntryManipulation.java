package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntryTypeEnum;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryStatus;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DirectoryEntryManipulation {
    private static final int ALL_PERMISSIONS = 7;

    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private TreeTraversal treeTraversal;

    @Transactional
    public DirectoryObjectAccess createEntry(Actor actor, DirectoryEntryTypeEnum type, DirectoryEntry parent, String name) {
        final DirectoryEntryType entityType = mapType(type);
        final String externalId = UUID.randomUUID().toString();
        final DirectoryEntry entry = directoryEntryRepository.save(DirectoryEntry.createNew(externalId, entityType, parent, name));
        final Access access = accessRepository.save(Access.createNew(actor, entry, true, ALL_PERMISSIONS));
        return new DirectoryObjectAccess(actor, entry, access.isOwnership(), Permission.mapToCombinationString(access.getPermissionCode()));
    }

    @Transactional
    public void markDeleted(DirectoryEntry directoryEntry) {
        if (DirectoryEntryStatus.REMOVED.equals(directoryEntry.getStatus())) return;
        final DirectoryEntryStatus newStatus = switch (directoryEntry.getStatus()) {
            case ACTIVE -> DirectoryEntryStatus.INACTIVE;
            default -> DirectoryEntryStatus.REMOVED;
        };
        treeTraversal.traverseDownwardsAndProcess(directoryEntry, new StatusMarkerTreeProcessor(newStatus));
    }

    @Transactional
    public void markUndeleted(DirectoryEntry directoryEntry) {
        if (DirectoryEntryStatus.REMOVED.equals(directoryEntry.getStatus())) return;
        treeTraversal.traverseDownwardsAndProcess(directoryEntry, new StatusMarkerTreeProcessor(DirectoryEntryStatus.ACTIVE));
    }

    private DirectoryEntryType mapType(DirectoryEntryTypeEnum type) {
        return switch (type) {
            case DIRECTORY -> DirectoryEntryType.DIRECTORY;
            case FILE -> DirectoryEntryType.FILE;
            default -> throw new IllegalArgumentException();
        };
    }

    private class StatusMarkerTreeProcessor implements TreeTraversal.TreeProcessor<Void> {
        private final DirectoryEntryStatus newStatus;

        public StatusMarkerTreeProcessor(DirectoryEntryStatus newStatus) {
            this.newStatus = newStatus;
        }

        @Override
        public Void initResult() {
            return null;
        }

        @Override
        public Void processNode(DirectoryEntry node, Void partialResult) {
            node.setStatus(this.newStatus);
            return null;
        }
    }
}
