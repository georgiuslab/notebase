package chemilabi.trade.notebase_backend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;


}
