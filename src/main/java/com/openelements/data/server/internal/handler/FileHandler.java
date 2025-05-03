package com.openelements.data.server.internal.handler;

import com.openelements.data.db.FileEntity;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import java.util.UUID;

public class FileHandler implements Handler {

    private final DbHandler dbHandler;

    private final boolean download = false;

    public FileHandler(DbHandler dbHandler) {this.dbHandler = dbHandler;}

    @Override
    public void handle(ServerRequest serverRequest, ServerResponse serverResponse) throws Exception {
        //Get file identifier from request
        final UUID id = Optional.ofNullable(serverRequest.path().segments().getLast())
                .map(segment -> segment.value())
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("File ID is required"));

        //load file from DB
        FileData data = dbHandler.runInTransaction(entityManager -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<FileEntity> cq = cb.createQuery(FileEntity.class);
            final Root<FileEntity> rootEntry = cq.from(FileEntity.class);
            cq.select(rootEntry).where(cb.equal(rootEntry.get("id"), id));
            try {
                FileEntity entity = entityManager.createQuery(cq).getSingleResult();
                return new FileData(entity.getName(), entity.getContent(), entity.getContentType());
            } catch (final NoResultException e) {
                throw new IllegalArgumentException("Can not find file for id " + id, e);
            }
        });

        // Set the content type to application/octet-stream for file download
        if (download) {
            HttpUtils.setContentDispositionAsAttachment(serverResponse, data.fileName());
            serverResponse.headers().contentType(ContentTypes.APPLICATION_OCTET_STREAM);
        } else {
            serverResponse.headers().contentType(data.contentType());
        }
        serverResponse.send(data.fileContent());
    }

    private record FileData(String fileName, byte[] fileContent, ContentTypes contentType) {
    }

}
