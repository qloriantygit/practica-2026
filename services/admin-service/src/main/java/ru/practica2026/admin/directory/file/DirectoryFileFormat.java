package ru.practica2026.admin.directory.file;

public enum DirectoryFileFormat {

    JSON(
            "json",
            "application/json"
    ),

    CSV(
            "csv",
            "text/csv; charset=UTF-8"
    );

    private final String extension;
    private final String contentType;

    DirectoryFileFormat(
            String extension,
            String contentType
    ) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
