package hu.szbz.hbc.doals.model;

import java.time.OffsetDateTime;

public record DirectoryEntryListItem(String id,
                                     DirectoryEntryType type,
                                     DirectoryEntryStatus status,
                                     String name,
                                     OffsetDateTime creationTimeStamp,
                                     OffsetDateTime modificationTimeStamp,
                                     OffsetDateTime deletionTimeStamp,
                                     int permissionCode) {
}
