package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.dto.AccessDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.AccessRoleEnum;
import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntriesPageDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntryBasicInfoDto;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.Actor;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryListItem;
import hu.szbz.hbc.doals.model.DirectoryEntryStatus;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DetailsMapperTest {
    private static final DetailsMapper MAPPER = new DetailsMapperImpl();

    @Test
    void test_mapAccessList() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getExternalId), "john.doe")
                .create();
        final Access entity = Instancio.of(Access.class)
                .set(field(Access::getActor), actor)
                .set(field(Access::isOwnership), true)
                .set(field(Access::getPermissionCode), 3)
                .create();

        final List<AccessDto> result = MAPPER.mapAccessList(List.of(entity));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(actor.getExternalId(), result.get(0).getUserId());
        assertEquals(AccessRoleEnum.OWNER, result.get(0).getRole());
        assertEquals("rw", result.get(0).getPermissions());
    }

    @Test
    void test_mapAccessResult() {
        final Actor actor = Instancio.of(Actor.class)
                .set(field(Actor::getExternalId), "john.doe")
                .create();
        final DirectoryObjectAccess access = new DirectoryObjectAccess(actor, null, true, "r");

        final AccessDto result = MAPPER.mapAccessResult(access);

        assertNotNull(result);
        assertEquals(actor.getExternalId(), result.getUserId());
        assertEquals(AccessRoleEnum.OWNER, result.getRole());
        assertEquals("r", result.getPermissions());
    }

    @Test
    void test_mapBasicInfo() {
        final OffsetDateTime created = OffsetDateTime.now();
        final DirectoryEntry entity = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getExternalId), UUID.randomUUID().toString())
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .set(field(DirectoryEntry::getStatus), DirectoryEntryStatus.ACTIVE)
                .set(field(DirectoryEntry::getName), "readme.txt")
                .set(field(DirectoryEntry::getCreationTimeStamp), created)
                .set(field(DirectoryEntry::getModificationTimeStamp), created)
                .set(field(DirectoryEntry::getDeletionTimeStamp), created)
                .create();

        final DirectoryEntryBasicInfoDto result = MAPPER.mapBasicInfo(entity);

        assertNotNull(result);
        assertEquals(entity.getExternalId(), result.getId());
        assertEquals(entity.getType().name(), result.getType().name());
        assertEquals(entity.getStatus().name(), result.getStatus().name());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getCreationTimeStamp(), result.getCreationDateTime());
        assertEquals(entity.getModificationTimeStamp(), result.getModificationDateTime());
        assertEquals(entity.getDeletionTimeStamp(), result.getDeletionDateTime());
    }

    @Test
    void test_mapResultPage() {
        final OffsetDateTime created = OffsetDateTime.now();
        final DirectoryEntryListItem listItem = new DirectoryEntryListItem(
                UUID.randomUUID().toString(),
                DirectoryEntryType.DIRECTORY,
                DirectoryEntryStatus.ACTIVE,
                "Documents",
                created,
                created,
                created,
                1
        );
        final Page<DirectoryEntryListItem> input = new PageImpl<>(List.of(listItem));

        final DirectoryEntriesPageDto result = MAPPER.mapResultPage(input);

        assertNotNull(result);
        assertEquals(input.getTotalPages(), result.getTotalPageCount());
        assertEquals(input.getNumber(), result.getPageIndex());
        assertFalse(result.isPreviousPage());
        assertFalse(result.isNextPage());
        assertNotNull(result.getItems());
        assertEquals(input.getNumberOfElements(), result.getItems().getItems().size());
        assertNotNull(result.getItems().getItems().get(0).getBasicInfo());
        assertEquals(listItem.id(), result.getItems().getItems().get(0).getBasicInfo().getId());
        assertEquals(listItem.type().name(), result.getItems().getItems().get(0).getBasicInfo().getType().name());
        assertEquals(listItem.status().name(), result.getItems().getItems().get(0).getBasicInfo().getStatus().name());
        assertEquals(listItem.name(), result.getItems().getItems().get(0).getBasicInfo().getName());
        assertEquals(listItem.creationTimeStamp(), result.getItems().getItems().get(0).getBasicInfo().getCreationDateTime());
        assertEquals(listItem.modificationTimeStamp(), result.getItems().getItems().get(0).getBasicInfo().getModificationDateTime());
        assertEquals(listItem.deletionTimeStamp(), result.getItems().getItems().get(0).getBasicInfo().getDeletionDateTime());
        assertEquals("r", result.getItems().getItems().get(0).getPermissions());
    }
}
