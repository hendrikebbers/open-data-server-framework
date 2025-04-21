package com.openelements.data.server.internal.handler;

import com.openelements.data.db.FileEntity;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.util.UUID;

public class FileDownloadHandler implements Handler {

    private final DbHandler dbHandler;

    public FileDownloadHandler(DbHandler dbHandler) {this.dbHandler = dbHandler;}

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        //Get file identifier from request
        final UUID id = req.queryParams().first("id")
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("File ID is required"));

        // Get content type from request headers
        final ContentTypes contentType = HttpUtils.getContentType(req)
                .orElse(ContentTypes.APPLICATION_OCTET_STREAM);

        //load file from DB
        FileEntity fileEntity = dbHandler.getEntityById(id, FileEntity.class)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        //get file name and content type from DB
        final String fileName = fileEntity.getName();
        final byte[] fileContent = fileEntity.getContent();

        // Set the content type to application/octet-stream for file download
        HttpUtils.setContentDispositionAsAttachment(req, fileName);
        HttpUtils.setContentType(res, contentType);
        res.send(fileContent);
    }
}
