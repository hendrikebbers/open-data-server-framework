package com.openelements.data.server.internal.handler;

import com.openelements.data.db.FileEntity;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
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

    private record FileData(String fileName, byte[] fileContent, ContentTypes contentType) {
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        //Get file identifier from request
        final UUID id = Optional.ofNullable(req.path().segments().getLast())
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
            HttpUtils.setContentDispositionAsAttachment(res, data.fileName());
            HttpUtils.setContentType(res, ContentTypes.APPLICATION_OCTET_STREAM);
        } else {
            HttpUtils.setContentType(res, data.contentType());
        }
        res.send(data.fileContent());
    }
}
