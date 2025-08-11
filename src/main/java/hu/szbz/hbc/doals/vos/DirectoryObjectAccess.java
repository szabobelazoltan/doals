package hu.szbz.hbc.doals.vos;

import hu.szbz.hbc.doals.model.DirectoryEntry;

public record DirectoryObjectAccess(DirectoryEntry entry, boolean ownership, String permissions) {
}
