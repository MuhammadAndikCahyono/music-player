package com.github.anrimian.simplemusicplayer.data.utils.folders;

public class RxNode<K, T> {

    /*    private final PublishSubject<Change<RxNode<K, T>>> changeSubject = PublishSubject.create();
    private final List<RxNode<K, T>> nodes = new LinkedList<>();

    @Nonnull
    private K key;
    private T data;

    @Nullable
    private RxNode<K, T> parent;

    public RxNode(@Nonnull K key, T data, @Nullable RxNode<K, T> parent) {
        this.key = key;
        this.data = data;
        this.parent = parent;
    }

    @Nullable
    public RxNode<K, T> getParent() {
        return parent;
    }

    @Nonnull
    public List<RxNode<K, T>> getNodes() {
        return nodes;
    }

    @Nonnull
    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    public Observable<Change<RxNode<K, T>>> getChangeObservable() {
        return changeSubject;
    }

    public void addNode(RxNode<K, T> node) {
        nodes.add(node);
        changeSubject.onNext(new Change<>(NEW, node));
        notifyNodeUpdated(this);
    }

    public void setData(T data) {
        this.data = data;
        notifyNodeUpdated(this);
    }

    public void removeNode(K key) {
        RxNode<K, T> node = findChild(key);
        if (node != null) {
            removeNode(node);
        }
    }

    public void removeNode(RxNode<K, T> node) {
        nodes.remove(node);
        changeSubject.onNext(new Change<>(DELETED, node));
        notifyNodeUpdated(this);
    }

    @Nullable
    public RxNode<K, T> findChild(K key) {
        for (RxNode<K, T> node: nodes) {
            if (node.getKey().equals(key)) {
                return node;
            }
        }
        return null;
    }

    private void notifyNodeUpdated(RxNode<K, T> node) {
        changeSubject.onNext(new Change<>(MODIFY, node));
        RxNode<K, T> parent = getParent();
        if (parent != null) {
            parent.notifyNodeUpdated(this);
        }
    }

    @Override
    public String toString() {
        return "RxNode{" +
                "nodes=" + nodes +
                ", key=" + key +
                ", data=" + data +
                '}';
    }*/


}