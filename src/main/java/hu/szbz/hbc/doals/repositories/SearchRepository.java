package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.endpoints.ws.dto.SearchParametersDto;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SearchRepository {

    Page<DirectoryEntryListItem> search(Actor actor, SearchParametersDto conditions, PageRequest pageRequest);

    Page<DirectoryEntryListItem> findAllByParent(Actor actor, DirectoryEntry parent, PageRequest pageRequest);
}
