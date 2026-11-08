package chemilabi.trade.notebase_backend.model;

import chemilabi.trade.notebase_backend.model.enums.MaterialStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "materials")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false)
    private MaterialStatus status;

    @Column(name = "likes_count", nullable = false, columnDefinition = "integer default 0")
    private int likesCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", referencedColumnName = "keycloak_id", nullable = false)
    private User uploader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToMany
    @JoinTable(
            name = "material_tag_mapping",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<MaterialTag> tags = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "material")
    private Set<Report> reports = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MaterialStatus.PENDING;
        }
    }
}