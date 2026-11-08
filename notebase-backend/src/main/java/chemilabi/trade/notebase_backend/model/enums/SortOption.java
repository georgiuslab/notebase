package chemilabi.trade.notebase_backend.model.enums;

import org.springframework.data.domain.Sort;

public enum SortOption {
    NEWEST(Sort.by(Sort.Direction.DESC, "createdAt", "id")),
    LIKES(Sort.by(Sort.Direction.DESC, "likesCount", "createdAt", "id"));

    private final Sort sort;

    SortOption(Sort sort) { this.sort = sort; }

    public Sort toSort() { return sort; }
}
