package chemilabi.trade.notebase_backend.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "faculties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

}