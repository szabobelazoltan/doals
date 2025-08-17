package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.Accesses;
import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntriesPageDto;
import hu.szbz.hbc.doals.endpoints.ws.DirectoryEntryDetailsDto;
import hu.szbz.hbc.doals.endpoints.ws.Nodes;
import hu.szbz.hbc.doals.endpoints.ws.PageParametersDto;
import hu.szbz.hbc.doals.endpoints.ws.SearchParametersDto;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryListItem;
import hu.szbz.hbc.doals.repositories.AccessRepository;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DirectoryEntryQuery {
    @Autowired
    private DirectoryEntryRepository directoryEntryRepository;

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private DetailsMapper mapper;

    @Autowired
    private TreeTraversal treeTraversal;

    public DirectoryEntryDetailsDto getDetails(DirectoryObjectAccess access) {
        final DirectoryEntryDetailsDto details = new DirectoryEntryDetailsDto();
        details.setBasicInfo(mapper.mapBasicInfo(access.entry()));
        details.setAccess(collectAccesses(access));
        details.setLocation(traverseLocation(access.entry()));
        return details;
    }

    public DirectoryEntriesPageDto listDirectoryEntries(DirectoryObjectAccess access, PageParametersDto pageParameters) {
        final PageRequest pageRequest = convertPageParams(pageParameters);
        final Page<DirectoryEntryListItem> queryResult = directoryEntryRepository.findAllByParent(access.actor(), access.entry(), pageRequest);
        return mapper.mapResultPage(queryResult);
    }

    public DirectoryEntriesPageDto search(Actor actor, SearchParametersDto conditions, PageParametersDto pageParameters) {
        final PageRequest pageRequest = convertPageParams(pageParameters);
        final Page<DirectoryEntryListItem> queryResult = directoryEntryRepository.search(actor, conditions, pageRequest);
        return mapper.mapResultPage(queryResult);
    }

    private Accesses collectAccesses(DirectoryObjectAccess access) {
        final Accesses accesses = new Accesses();
        if (access.ownership()) {
            accesses.getAccesses().addAll(mapper.mapAccessList(accessRepository.findAllByEntry(access.entry())));
        } else {
            accesses.getAccesses().add(mapper.mapAccessResult(access));
        }
        return accesses;
    }

    private PageRequest convertPageParams(PageParametersDto dto) {
        return null;
    }

    private Nodes traverseLocation(DirectoryEntry entry) {
        final Nodes nodes = treeTraversal.traverseUpwardsAndProcess(entry, new LocationProcessor());
        Collections.reverse(nodes.getNodes());
        return nodes;
    }

    private class LocationProcessor implements TreeTraversal.TreeProcessor<Nodes> {
        @Override
        public Nodes initResult() {
            return new Nodes();
        }

        @Override
        public Nodes processNode(DirectoryEntry node, Nodes partialResult) {
            partialResult.getNodes().add(mapper.mapBasicInfo(node));
            return partialResult;
        }
    }
}
