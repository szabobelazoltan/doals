package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.endpoints.ws.dto.AccessDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.AccessRoleEnum;
import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntriesPageDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntryBasicInfoDto;
import hu.szbz.hbc.doals.endpoints.ws.dto.DirectoryEntryListItemDto;
import hu.szbz.hbc.doals.model.Access;
import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryListItem;
import hu.szbz.hbc.doals.model.Permission;
import hu.szbz.hbc.doals.vos.DirectoryObjectAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = { Permission.class }
)
public interface DetailsMapper {
    List<AccessDto> mapAccessList(List<Access> entities);

    @Mapping(target = "userId", source = "actor.externalId")
    @Mapping(target = "role", expression = "java( mapRole(entity.isOwnership()) )")
    @Mapping(target = "permissions", expression = "java( Permission.mapToCombinationString(entity.getPermissionCode()) )")
    AccessDto mapAccess(Access entity);

    default AccessRoleEnum mapRole(boolean ownership) {
        return ownership ? AccessRoleEnum.OWNER : AccessRoleEnum.READONLY;
    }

    @Mapping(target = "userId", source = "actor.externalId")
    @Mapping(target = "role", expression = "java( mapRole(result.ownership()) )")
    @Mapping(target = "permissions", source = "permissions")
    AccessDto mapAccessResult(DirectoryObjectAccess result);

    @Mapping(target = "id", source = "externalId")
    @Mapping(target = "creationDateTime", source = "creationTimeStamp")
    @Mapping(target = "modificationDateTime", source = "modificationTimeStamp")
    @Mapping(target = "deletionDateTime", source = "deletionTimeStamp")
    DirectoryEntryBasicInfoDto mapBasicInfo(DirectoryEntry entity);

    @Mapping(target = "basicInfo.id", source = "id")
    @Mapping(target = "basicInfo.type", source = "type")
    @Mapping(target = "basicInfo.status", source = "status")
    @Mapping(target = "basicInfo.name", source = "name")
    @Mapping(target = "basicInfo.creationDateTime", source = "creationTimeStamp")
    @Mapping(target = "basicInfo.modificationDateTime", source = "modificationTimeStamp")
    @Mapping(target = "basicInfo.deletionDateTime", source = "deletionTimeStamp")
    @Mapping(target = "permissions", expression = "java( Permission.mapToCombinationString(queryItem.permissionCode()) )")
    DirectoryEntryListItemDto mapListItem(DirectoryEntryListItem queryItem);

    List<DirectoryEntryListItemDto> mapListItems(List<DirectoryEntryListItem> queryResults);

    default DirectoryEntriesPageDto mapResultPage(Page<DirectoryEntryListItem> queryResult) {
        final DirectoryEntriesPageDto dto = new DirectoryEntriesPageDto();
        dto.setItems(new DirectoryEntriesPageDto.Items());
        dto.getItems().getItems().addAll(mapListItems(queryResult.getContent()));
        dto.setTotalPageCount(queryResult.getTotalPages());
        dto.setPageIndex(queryResult.getNumber());
        dto.setPreviousPage(queryResult.hasPrevious());
        dto.setNextPage(queryResult.hasNext());
        return dto;
    }
}
