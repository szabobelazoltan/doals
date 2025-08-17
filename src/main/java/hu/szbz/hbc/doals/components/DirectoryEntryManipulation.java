package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntryTypeEnum;
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


@Component
public class DirectoryEntryManipulation {
    private static final int ALL_PERMISSIONS = 7;

    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private TreeTraversal treeTraversal;

    @Autowired
    private ExternalIdGenerator externalIdGenerator;

    @Transactional
    public DirectoryObjectAccess createEntry(Actor actor, DirectoryEntryTypeEnum type, DirectoryEntry parent, String name) {
        final DirectoryEntryType entityType = mapType(type);
        final String externalId = externalIdGenerator.generate();
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
        treeTraversal.traverseUpwardsAndProcess(
                directoryEntry,
                new UndeletionTreeProcessor(DirectoryEntryStatus.ACTIVE),
                this::isTopMostParentReached
        );
        treeTraversal.traverseDownwardsAndProcess(directoryEntry, new UndeletionTreeProcessor(DirectoryEntryStatus.ACTIVE));
    }

    private boolean isTopMostParentReached(DirectoryEntry entry) {
        return entry != null && DirectoryEntryStatus.INACTIVE.equals(entry.getStatus());
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

    private class UndeletionTreeProcessor extends StatusMarkerTreeProcessor {
        public UndeletionTreeProcessor(DirectoryEntryStatus newStatus) {
            super(newStatus);
        }

        @Override
        public Void processNode(DirectoryEntry node, Void partialResult) {
            super.processNode(node, partialResult);
            node.setDeletionTimeStamp(null);
            return null;
        }
    }
}
