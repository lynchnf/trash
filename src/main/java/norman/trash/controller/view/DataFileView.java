package norman.trash.controller.view;

import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;

import java.util.Date;

public class DataFileView {
    private Long id;
    private String originalFilename;
    private String contentType;
    private Long size;
    private Date uploadTimestamp;
    private DataFileStatus status;

    public DataFileView(DataFile dataFile) {
        id = dataFile.getId();
        originalFilename = dataFile.getOriginalFilename();
        contentType = dataFile.getContentType();
        size = dataFile.getSize();
        uploadTimestamp = dataFile.getUploadTimestamp();
        status = dataFile.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public DataFileStatus getStatus() {
        return status;
    }
}
