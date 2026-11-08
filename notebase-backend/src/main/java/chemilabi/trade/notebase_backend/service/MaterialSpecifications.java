package chemilabi.trade.notebase_backend.service;

import chemilabi.trade.notebase_backend.model.Material;
import chemilabi.trade.notebase_backend.model.MaterialTag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class MaterialSpecifications {

    private MaterialSpecifications() {}

    private static Specification<Material> noFilter() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Material> matchesText(String q) {
        if (q == null || q.isBlank()) return noFilter();
        String pattern = "%" + escapeLike(q.trim().toLowerCase()) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern, '\\'),
                cb.like(cb.lower(root.get("description")), pattern, '\\'));
    }

    public static Specification<Material> hasAnyTag(Collection<String> tags) {
        if (tags == null || tags.isEmpty()) return noFilter();
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Material> m = sub.from(Material.class);
            Join<Material, MaterialTag> tag = m.join("tags");
            sub.select(m.get("id")).where(
                    cb.equal(m.get("id"), root.get("id")),
                    tag.get("name").in(tags));
            return cb.exists(sub);
        };
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}