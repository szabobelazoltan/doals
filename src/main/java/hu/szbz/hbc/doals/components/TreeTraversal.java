package hu.szbz.hbc.doals.components;

import hu.szbz.hbc.doals.model.DirectoryEntry;
import hu.szbz.hbc.doals.repositories.DirectoryEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@Component
public class TreeTraversal {
    @Autowired
    private DirectoryEntryRepository repository;

    public List<DirectoryEntry> traverseDownwardsAndList(DirectoryEntry rootNode) {
        return traverseDownwardsAndProcess(rootNode, new FlattenProcessor());
    }

    public <R> R traverseDownwardsAndProcess(DirectoryEntry rootNode, TreeProcessor<R> processor) {
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

    public <R> R traverseUpwardsAndProcess(DirectoryEntry leafNode, TreeProcessor<R> processor, Predicate<DirectoryEntry> exitCondition) {
        R result = processor.initResult();
        DirectoryEntry currentNode = leafNode;
        while (exitCondition.test(leafNode)) {
            result = processor.processNode(currentNode, result);
            currentNode = currentNode.getParent();
        }
        return result;
    }

    public <R> R traverseUpwardsAndProcess(DirectoryEntry leafNode, TreeProcessor<R> processor) {
        return traverseUpwardsAndProcess(leafNode, processor, node -> node != null);
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
