package com.github.anrimian.simplemusicplayer.data.utils.folders;

import com.github.anrimian.simplemusicplayer.domain.utils.changes.Change;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.observers.TestObserver;

import static com.github.anrimian.simplemusicplayer.domain.utils.changes.ChangeType.ADDED;
import static org.junit.Assert.*;

public class RxNodeTest {

    private RxNode<Integer, String> root = new RxNode<>(0, "root");

    @Test
    public void addChildTest() {
        TestObserver<Change<List<RxNode<Integer, String>>>> childObserver = root
                .getChildChangeObservable()
                .test();

        RxNode<Integer, String> rootChild1 = new RxNode<>(1, "child 1");
        root.addNode(rootChild1);

        childObserver.assertValue(change -> {
            assertEquals(ADDED, change.getChangeType());
            assertEquals(rootChild1, change.getData().get(0));

            return true;
        });

        assert root.getNodes().contains(rootChild1);

    }
}