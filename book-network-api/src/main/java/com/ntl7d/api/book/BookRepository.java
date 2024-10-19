package com.ntl7d.api.book;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
        @Query("""
                        SELECT book
                        FROM Book book
                        WHERE book.archived = false
                        AND book.shareable = true
                        AND book.owner.id != :userId
                        """)
        Page<Book> findAllDisplayableBooks(Pageable pageable, UUID userId);
}
