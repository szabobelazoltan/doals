package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.model.DirectoryEntryType;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TreeTraversalTest {
    @Mock
    private DirectoryEntryRepository repository;

    @InjectMocks
    private TreeTraversal traversal;

    @Test
    public void test_traverseDownwardsAndProcess() {
        final DirectoryEntry root = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 1)
                .set(field(DirectoryEntry::getExternalId), "37973987-7908-4ea7-914f-c1408332fe53")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.DIRECTORY)
                .create();
        final DirectoryEntry child = Instancio.of(DirectoryEntry.class)
                .set(field(DirectoryEntry::getId), 2)
                .set(field(DirectoryEntry::getExternalId), "86ecd89d-769c-4751-8027-2da1f1ee686e")
                .set(field(DirectoryEntry::getType), DirectoryEntryType.FILE)
                .create();
        final List<DirectoryEntry> children = List.of(child);
        when(repository.findAllByParent(root)).thenReturn(children);

        final List<DirectoryEntry> result = traversal.traverseDownwardsAndList(root);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(root, result.get(0));
        assertEquals(child, result.get(1));
    }
}
