package chemilabi.trade.notebase_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "actor")
    private Set<AuditLog> auditLogs = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "uploader")
    private Set<Material> materials = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Report> reports = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_materials",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "keycloak_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    @Builder.Default
    private Set<Material> favorites = new HashSet<>();
}