package lms.leave;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** File minh chứng của đơn xin nghỉ; nội dung trên đĩa (FileStorage), tên gốc chỉ để hiển thị/tải về. */
@Entity
@Table(name = "leave_request_files")
public class LeaveRequestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "request_id", nullable = false, updatable = false)
    private Long requestId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long size;

    @Column(name = "storage_key", nullable = false, updatable = false, length = 43)
    private String storageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LeaveRequestFile() {
        // JPA
    }

    public LeaveRequestFile(long requestId, String name, String contentType, long size, String storageKey) {
        this.requestId = requestId;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.storageKey = storageKey;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
