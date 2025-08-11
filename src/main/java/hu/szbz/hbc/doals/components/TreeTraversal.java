package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class TreeTraversal {
    @Autowired
    private DirectoryEntryRepository repository;

    public List<DirectoryEntry> traverseAndList(DirectoryEntry rootNode) {
        return traverseAndProcess(rootNode, new FlattenProcessor());
    }

    public <R> R traverseAndProcess(DirectoryEntry rootNode, TreeProcessor<R> processor) {
        R result = processor.initResult();
        Queue<DirectoryEntry> queue = new LinkedList<>();
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            DirectoryEntry currentNode = queue.remove();
            result = processor.processNode(currentNode, result);
            if (currentNode.isDirectory()) {
                repository.findAllByParent(currentNode).forEach(queue::offer);
            }
        }
        return result;
    }

    public interface TreeProcessor<T> {
        T initResult();

        T processNode(DirectoryEntry node, T partialResult);
    }

    private class FlattenProcessor implements TreeProcessor<List<DirectoryEntry>> {
        @Override
        public List<DirectoryEntry> initResult() {
            return new LinkedList<>();
        }

        @Override
        public List<DirectoryEntry> processNode(DirectoryEntry node, List<DirectoryEntry> partialResult) {
            partialResult.add(node);
            return partialResult;
        }
    }
}
