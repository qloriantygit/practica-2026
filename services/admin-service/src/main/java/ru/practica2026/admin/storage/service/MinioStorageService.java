package ru.practica2026.admin.storage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageService(
            MinioClient minioClient,

            @Value("${app.storage.minio.bucket}")
            String bucket
    ) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public String store(
            String objectName,
            byte[] content
    ) {
        Path temporaryFile = null;

        try {
            ensureBucketExists();

            temporaryFile =
                    Files.createTempFile(
                            "practica-storage-",
                            ".bin"
                    );

            Files.write(
                    temporaryFile,
                    content
            );

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(
                                    temporaryFile.toString()
                            )
                            .build()
            );

            return objectName;
        }
        catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to store object in MinIO: "
                            + objectName,
                    exception
            );
        }
        finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(
                            temporaryFile
                    );
                }
                catch (Exception ignored) {
                    // Temporary file cleanup must not hide
                    // the original operation result.
                }
            }
        }
    }

    private void ensureBucketExists()
            throws Exception {

        boolean exists =
                minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(bucket)
                                .build()
                );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }
}
