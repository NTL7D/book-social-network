package com.ntl7d.api.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    private BookSpecification() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Book> withOwner(String ownerId) {
        return (root, query, cb) -> cb.equal(root.get("createdBy"), ownerId);
    }
}
