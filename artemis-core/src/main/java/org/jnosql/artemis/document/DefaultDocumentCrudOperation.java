package org.jnosql.artemis.document;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.TTL;
import org.jnosql.diana.api.document.DocumentCollectionEntity;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentQuery;

@DocumentCRUDInterceptor
class DefaultDocumentCrudOperation implements DocumentCrudOperation {


    private final DocumentEntityConverter converter;

    private final Instance<DocumentCollectionManager> manager;

    private final DocumentPersistManager documentPersistManager;

    @Inject
    DefaultDocumentCrudOperation(DocumentEntityConverter converter, Instance<DocumentCollectionManager> manager, DocumentPersistManager documentPersistManager) {
        this.converter = converter;
        this.manager = manager;
        this.documentPersistManager = documentPersistManager;
    }

    @Override
    public <T> T save(T entity) throws NullPointerException {
        documentPersistManager.firePreEntity(entity);
        DocumentCollectionEntity document = converter.toDocument(Objects.requireNonNull(entity, "entity is required"));
        documentPersistManager.firePreDocument(document);
        DocumentCollectionEntity documentCollection = manager.get().save(document);
        documentPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        documentPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void saveAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().saveAsync(converter.toDocument(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> T save(T entity, TTL ttl) {
        documentPersistManager.firePreEntity(entity);
        DocumentCollectionEntity document = converter.toDocument(Objects.requireNonNull(entity, "entity is required"));
        documentPersistManager.firePreDocument(document);
        DocumentCollectionEntity documentCollection = manager.get().save(document, ttl);
        documentPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        documentPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void saveAsync(T entity, TTL ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toDocument(entity), ttl);
    }

    @Override
    public <T> void saveAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().saveAsync(converter.toDocument(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> void saveAsync(T entity, TTL ttl, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        Objects.requireNonNull(ttl, "ttl is required");
        manager.get().saveAsync(converter.toDocument(entity), ttl,
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public <T> T update(T entity) {
        documentPersistManager.firePreEntity(entity);
        DocumentCollectionEntity document = converter.toDocument(Objects.requireNonNull(entity, "entity is required"));
        documentPersistManager.firePreDocument(document);
        DocumentCollectionEntity documentCollection = manager.get().update(document);
        documentPersistManager.firePostDocument(documentCollection);
        T entityUpdated = converter.toEntity((Class<T>) entity.getClass(), documentCollection);
        documentPersistManager.firePostEntity(entityUpdated);
        return entityUpdated;
    }

    @Override
    public <T> void updateAsync(T entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().updateAsync(converter.toDocument(Objects.requireNonNull(entity, "entity is required")));
    }

    @Override
    public <T> void updateAsync(T entity, Consumer<T> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Objects.requireNonNull(entity, "entity is required");
        manager.get().updateAsync(converter.toDocument(entity),
                d -> {
                    T value = converter.toEntity((Class<T>) entity.getClass(), d);
                    callBack.accept(value);
                });
    }

    @Override
    public void delete(DocumentQuery query) {
        manager.get().delete(query);
    }

    @Override
    public void deleteAsync(DocumentQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query);
    }

    @Override
    public void deleteAsync(DocumentQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().deleteAsync(query, callBack);
    }

    @Override
    public <T> List<T> find(DocumentQuery query) throws NullPointerException {
        List<DocumentCollectionEntity> entities = manager.get().find(query);
        Function<DocumentCollectionEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void findAsync(DocumentQuery query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        Function<DocumentCollectionEntity, T> function = e -> converter.toEntity(e);
        manager.get().findAsync(query, es -> {
            callBack.accept(es.stream().map(function).collect(Collectors.toList()));
        });
    }

    @Override
    public <T> List<T> nativeQuery(String query) throws UnsupportedOperationException {
        List<DocumentCollectionEntity> entities = manager.get().nativeQuery(query);
        Function<DocumentCollectionEntity, T> function = e -> converter.toEntity(e);
        return entities.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public <T> void nativeQueryAsync(String query, Consumer<List<T>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
        manager.get().nativeQueryAsync(query, es -> {
            Function<DocumentCollectionEntity, T> function = e -> converter.toEntity(e);
            callBack.accept(es.stream().map(function).collect(Collectors.toList()));
        });
    }
}
