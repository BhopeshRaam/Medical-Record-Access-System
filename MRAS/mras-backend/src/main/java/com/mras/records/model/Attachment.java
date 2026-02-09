package com.mras.records.model;

public class Attachment {
    private String storage;   // GRIDFS / S3 / URL
    private String fileId;    // GridFS file _id as String
    private String url;       // if S3/URL
    private String filename;
    private String mimeType;
    private Long sizeBytes;

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
}
