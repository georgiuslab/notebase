package chemilabi.trade.notebase_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "material_tag_mapping", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"material_id", "tag_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialTagMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private MaterialTag tag;
}