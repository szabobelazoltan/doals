package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.exceptions.ErrorCode;
import hu.szbz.hbc.doals.exceptions.ServiceException;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.ActorRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class DirectoryObjectProtection {
    private static final int NO_PERMISSIONS = 0;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private TreeTraversal treeTraversal;

    public DirectoryEntry getEntry(String entryId) {
        return directoryEntryRepository.findByExternalId(entryId)
                .orElseThrow(() -> new ServiceException(String.format("Directory entry does not exist with id: %s", entryId), ErrorCode.DIRECTORY_ENTRY_NOT_EXISTS));
    }

    public DirectoryObjectAccess getAccessWithShallowPermissionCalculation(Actor actor, String entryId, Permission requiredPermission) {
        final DirectoryEntry entry = getEntry(entryId);
        return calculateAccess(actor, entry, requiredPermission, this::getPermissionCodeForEntry);
    }

    public DirectoryObjectAccess getAccessWithDeepPermissionCalculation(Actor actor, String entryId, Permission requiredPermission) {
        final DirectoryEntry entry = getEntry(entryId);
        return calculateAccess(actor, entry, requiredPermission, this::aggregatePermissionsForTree);
    }

    private DirectoryObjectAccess calculateAccess(
            Actor actor,
            DirectoryEntry entry,
            Permission requiredPermission,
            BiFunction<Actor, DirectoryEntry, Integer> permissionCalculator) {
        final boolean ownership = accessRepository.findOwnershipByActorAndEntry(actor, entry).orElse(Boolean.FALSE);
        final int calculatedPermission = permissionCalculator.apply(actor, entry);

        if (!requiredPermission.matches(calculatedPermission)) {
            throw new ServiceException(String.format("Permission denied for actor (id=%s) on directory entry (id=%s). Required permission: %s", actor.getExternalId(), entry.getExternalId(), requiredPermission), ErrorCode.PERMISSION_DENIED);
        }

        final String permissionCombination = Permission.mapToCombinationString(calculatedPermission);
        return new DirectoryObjectAccess(actor, entry, ownership, permissionCombination);
    }

    private int getPermissionCodeForEntry(Actor actor, DirectoryEntry directoryEntry) {
        return accessRepository.findByActorAndEntry(actor, directoryEntry)
                .map(Access::getPermissionCode)
                .orElse(NO_PERMISSIONS);
    }

    private int aggregatePermissionsForTree(Actor actor, DirectoryEntry root) {
        final TreeTraversal.TreeProcessor<Integer> treeProcessor = new PermissionAggregatorTreeProcessor(actor);
        return treeTraversal.traverseDownwardsAndProcess(root, treeProcessor);
    }

    private class PermissionAggregatorTreeProcessor implements TreeTraversal.TreeProcessor<Integer> {
        private final Actor actor;

        public PermissionAggregatorTreeProcessor(Actor actor) {
            this.actor = actor;
        }

        @Override
        public Integer initResult() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Integer processNode(DirectoryEntry node, Integer partialResult) {
            final int permissionCode = getPermissionCodeForEntry(this.actor, node);
            return permissionCode & partialResult;
        }
    }
}
