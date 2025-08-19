package hu.szbz.hbc.doals.endpoints.ws;

import hu.szbz.hbc.doals.services.DirectoryEntryManagerService;
import hu.szbz.hbc.doals.utils.SoapUtil;
import jakarta.xml.ws.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

@Endpoint
public class DirectoryEntryServiceImpl implements DirectoryEntryServicePort {
    @Autowired
    private DirectoryEntryManagerService managerService;

    @Override
    public GetUserRootRp getUserRoot(GetUserRootRq parameters) {
        return SoapUtil.processRequest(managerService::getRoot, GetUserRootRp::new, parameters);
    }

    @Override
    public GetDirectoryEntryDetailsRp getDirectoryEntryDetails(GetDirectoryEntryDetailsRq parameters) {
        return SoapUtil.processRequest(managerService::getDetails, GetDirectoryEntryDetailsRp::new, parameters);
    }

    @Override
    public DeleteDirectoryEntryRp deleteDirectoryEntry(DeleteDirectoryEntryRq parameters) {
        return SoapUtil.processRequest(managerService::deleteDirectoryEntry, DeleteDirectoryEntryRp::new, parameters);
    }

    @Override
    public UndeleteDirectoryEntryRp undeleteDirectoryEntry(UndeleteDirectoryEntryRq parameters) {
        return SoapUtil.processRequest(managerService::undeleteDirectoryEntry, UndeleteDirectoryEntryRp::new, parameters);
    }

    @Override
    public CreateDirectoryEntryRp createDirectoryEntry(CreateDirectoryEntryRq parameters) {
        return SoapUtil.processRequest(managerService::createDirectoryEntry, CreateDirectoryEntryRp::new, parameters);
    }

    @Override
    public ListDirectoryEntriesRp listDirectoryEntries(ListDirectoryEntriesRq parameters) {
        return SoapUtil.processRequest(managerService::listDirectoryEntries, ListDirectoryEntriesRp::new, parameters);
    }

    @Override
    public SearchDirectoryEntriesRp searchDirectoryEntries(SearchDirectoryEntriesRq parameters) {
        return SoapUtil.processRequest(managerService::search, SearchDirectoryEntriesRp::new, parameters);
    }

    @Override
    public RenameDirectoryEntryRp renameDirectoryEntry(RenameDirectoryEntryRq parameters) {
        return null;
    }

    @Override
    public MoveDirectoryEntryRp moveDirectoryEntry(MoveDirectoryEntryRq parameters) {
        return null;
    }
}
