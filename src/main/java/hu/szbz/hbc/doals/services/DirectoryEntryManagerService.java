package hu.szbz.hbc.doals.services;

import hu.szbz.hbc.doals.components.ActorRegistry;
import hu.szbz.hbc.doals.components.DirectoryEntryManipulation;
import hu.szbz.hbc.doals.components.DirectoryEntryQuery;
import hu.szbz.hbc.doals.components.DirectoryObjectProtection;
import hu.szbz.hbc.doals.endpoints.ws.CreateDirectoryEntryRp;
import hu.szbz.hbc.doals.endpoints.ws.CreateDirectoryEntryRq;
import hu.szbz.hbc.doals.endpoints.ws.DeleteDirectoryEntryRp;
import hu.szbz.hbc.doals.endpoints.ws.DeleteDirectoryEntryRq;
import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntriesPageDto;
import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntryDetailsDto;
import hu.szbz.hbc.doals.endpoints.ws.GetDirectoryEntryDetailsRp;
import hu.szbz.hbc.doals.endpoints.ws.GetDirectoryEntryDetailsRq;
import hu.szbz.hbc.doals.endpoints.ws.GetUserRootRp;
import hu.szbz.hbc.doals.endpoints.ws.GetUserRootRq;
import hu.szbz.hbc.doals.endpoints.ws.ListDirectoryEntriesRp;
import hu.szbz.hbc.doals.endpoints.ws.ListDirectoryEntriesRq;
import hu.szbz.hbc.doals.endpoints.ws.SearchDirectoryEntriesRp;
import hu.szbz.hbc.doals.endpoints.ws.SearchDirectoryEntriesRq;
import hu.szbz.hbc.doals.endpoints.ws.UndeleteDirectoryEntryRp;
import hu.szbz.hbc.doals.endpoints.ws.UndeleteDirectoryEntryRq;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectoryEntryManagerService {
    @Autowired
    private DirectoryObjectProtection protection;

    @Autowired
    private DirectoryEntryManipulation manipulation;

    @Autowired
    private DirectoryEntryQuery query;

    @Autowired
    private ActorRegistry actorRegistry;

    public GetDirectoryEntryDetailsRp getDetails(GetDirectoryEntryDetailsRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryObjectAccess access = protection.getAccessWithShallowPermissionCalculation(actor, rq.getBody().getId(), Permission.READ);
        final DirectoryEntryDetailsDto details = query.getDetails(access);
        final GetDirectoryEntryDetailsRp rp = new GetDirectoryEntryDetailsRp();
        rp.setBody(details);
        return rp;
    }

    public GetUserRootRp getRoot(GetUserRootRq rq) {
        final DirectoryObjectAccess access = actorRegistry.getActorRootEntry(rq.getHeader().getUserId());
        final DirectoryEntryDetailsDto details = query.getDetails(access);
        final GetUserRootRp rp = new GetUserRootRp();
        rp.setBody(details);
        return rp;
    }

    public ListDirectoryEntriesRp listDirectoryEntries(ListDirectoryEntriesRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryObjectAccess access = protection.getAccessWithShallowPermissionCalculation(actor, rq.getBody().getId(), Permission.READ);
        final DirectoryEntriesPageDto list = query.listDirectoryEntries(access, rq.getBody().getPageParameters());
        final ListDirectoryEntriesRp rp = new ListDirectoryEntriesRp();
        rp.setBody(list);
        return rp;
    }

    public SearchDirectoryEntriesRp listDirectoryEntries(SearchDirectoryEntriesRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryEntriesPageDto list = query.search(actor, rq.getBody().getSearchParameters(), rq.getBody().getPageParameters());
        final SearchDirectoryEntriesRp rp = new SearchDirectoryEntriesRp();
        rp.setBody(list);
        return rp;
    }

    public CreateDirectoryEntryRp createDirectoryEntry(CreateDirectoryEntryRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryEntry parent = protection.getAccessWithShallowPermissionCalculation(actor, rq.getBody().getParentId(), Permission.WRITE)
                .entry();
        final DirectoryObjectAccess access = manipulation.createEntry(actor, rq.getBody().getType(), parent, rq.getBody().getName());
        final DirectoryEntryDetailsDto details = query.getDetails(access);
        final CreateDirectoryEntryRp rp = new CreateDirectoryEntryRp();
        rp.setBody(details);
        return rp;
    }

    public DeleteDirectoryEntryRp deleteDirectoryEntry(DeleteDirectoryEntryRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryEntry entry = protection.getAccessWithDeepPermissionCalculation(actor, rq.getBody().getId(), Permission.DELETE)
                .entry();
        manipulation.markDeleted(entry);
        return new DeleteDirectoryEntryRp();
    }

    public UndeleteDirectoryEntryRp undeleteDirectoryEntry(UndeleteDirectoryEntryRq rq) {
        final Actor actor = actorRegistry.getActor(rq.getHeader().getUserId());
        final DirectoryObjectAccess access = protection.getAccessWithDeepPermissionCalculation(actor, rq.getBody().getId(), Permission.DELETE);
        manipulation.markUndeleted(access.entry());
        final DirectoryEntryDetailsDto rpBody = query.getDetails(access);
        final UndeleteDirectoryEntryRp rp = new UndeleteDirectoryEntryRp();
        rp.setBody(rpBody);
        return rp;
    }
}
