package chemilabi.trade.notebase_backend.aws;

import chemilabi.trade.notebase_backend.dto.PresignedUrlResponseDto;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    private static final long MAX_SIZE_BYTES = 20L * 1024 * 1024; // 20 MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PDF_MAGIC = "%PDF-";

    private static final long MAX_AVATAR_SIZE_BYTES = 5L * 1024 * 1024; // მაქსიმუმ 5MB პროფილის ფოტოსთვის
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/jpg");

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(@Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucket-name}") String bucketName,
                     @Value("${aws.credentials.access-key}") String accessKey,
                     @Value("${aws.credentials.secret-key}") String secretKey) {
        this.bucketName = bucketName;

        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials)
                .build();

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentials)
                .build();
    }

    @PreDestroy
    void close() {
        s3Presigner.close();
        s3Client.close();
    }

    private String userPrefix(String safeEmail) {
        return "pdfs/" + safeEmail + "/";
    }

    private String avatarPrefix(String safeEmail) {
        return "avatars/" + safeEmail + "/";
    }

    public PresignedUrlResponseDto generatePresignedAvatarUploadUrl(String fileName,
                                                                    String contentType,
                                                                    long fileSize,
                                                                    String safeEmail) {
        if (fileName == null || (!fileName.toLowerCase().endsWith(".jpg") &&
                !fileName.toLowerCase().endsWith(".jpeg") &&
                !fileName.toLowerCase().endsWith(".png"))) {
            throw new IllegalArgumentException("დასაშვებია მხოლოდ JPG/JPEG ან PNG ფოტოები");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("არასწორი ფოტოს ფორმატი");
        }
        if (fileSize < 1 || fileSize > MAX_AVATAR_SIZE_BYTES) {
            throw new IllegalArgumentException("ფოტოს ზომა არ უნდა აღემატებოდეს 5MB-ს");
        }

        String safeName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = avatarPrefix(safeEmail) + UUID.randomUUID() + "_" + safeName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(fileSize)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponseDto(presigned.url().toString(), key);
    }

    public PresignedUrlResponseDto generatePresignedUploadUrl(String fileName,
                                                              String contentType,
                                                              long fileSize,
                                                              String safeEmail) {
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("დასაშვებია მხოლოდ PDF ფაილების ატვირთვა");
        }
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException("დასაშვებია მხოლოდ PDF ფაილების ატვირთვა");
        }
        if (fileSize < 1 || fileSize > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("ფაილის ზომა უნდა იყოს 1 ბაიტიდან 20MB-მდე");
        }

        String safeName = fileName.replaceAll("[^a-zA-Z0-9._\\u10A0-\\u10FF\\u0400-\\u04FF-]", "_");
        String key = userPrefix(safeEmail) + UUID.randomUUID() + "_" + safeName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(PDF_CONTENT_TYPE)
                .contentLength(fileSize)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponseDto(presigned.url().toString(), key);
    }

    /**
     * ატვირთვის დადასტურებისას ამოწმებს, რომ ფაილი ნამდვილად არსებობს,
     * ეკუთვნის ამ მომხმარებელს, ზომა ნორმაშია და შიგთავსი რეალური PDF-ია.
     * არასწორ ფაილს შლის და აგდებს ერორს.
     */
    public void verifyUploadedPdf(String key, String safeEmail) {
        if (key == null || !key.startsWith(userPrefix(safeEmail))) {
            throw new IllegalArgumentException("არასწორი FileKey");
        }

        HeadObjectResponse head;
        try {
            head = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName).key(key).build());
        } catch (NoSuchKeyException e) {
            throw new IllegalArgumentException("ფაილი S3-ზე ვერ მოიძებნა");
        }

        if (head.contentLength() < 1 || head.contentLength() > MAX_SIZE_BYTES) {
            deleteQuietly(key);
            throw new IllegalArgumentException("ფაილი ცარიელია ან ზომა აღემატება 20მბ-ს");
        }

        ResponseBytes<GetObjectResponse> first = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName).key(key)
                        .range("bytes=0-4")
                        .build());

        String magic = new String(first.asByteArray(), StandardCharsets.US_ASCII);
        if (!PDF_MAGIC.equals(magic)) {
            deleteQuietly(key);
            throw new IllegalArgumentException("ფაილი არ არის PDF ფორმატის");
        }
    }

    public void verifyUploadedAvatar(String key, String safeEmail) {
        if (key == null || !key.startsWith(avatarPrefix(safeEmail))) {
            throw new IllegalArgumentException("არასწორი Avatar FileKey");
        }

        HeadObjectResponse head;
        try {
            head = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName).key(key).build());
        } catch (NoSuchKeyException e) {
            throw new IllegalArgumentException("ფოტო S3-ზე ვერ მოიძებნა");
        }

        if (head.contentLength() < 1 || head.contentLength() > MAX_AVATAR_SIZE_BYTES) {
            deleteQuietly(key);
            throw new IllegalArgumentException("ფოტო ცარიელია ან ზომა აღემატება 5MB-ს");
        }
    }

    private void deleteQuietly(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName).key(key).build());
        } catch (Exception ignored) {

        }
    }

    public String generatePresignedDownloadUrl(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}