package hu.szbz.hbc.doals.repositories;

import hu.szbz.hbc.doals.endpoints.ws.dto.SearchParametersDto;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SearchRepository {

    Page<DirectoryEntry> search(Actor actor, SearchParametersDto conditions, PageRequest pageRequest);
}
