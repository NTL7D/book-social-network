package com.ntl7d.api.history;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTransactionHistoryRepository
                extends JpaRepository<BookTransactionHistory, UUID> {

        @Query("""
                        SELECT history
                        FROM BookTransactionHistory history
                        WHERE history.user.id = :userId
                        """)
        Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, UUID userId);

        @Query("""
                        SELECT history
                        FROM BookTransactionHistory history
                        WHERE history.book.owner.id = :userId
                        """)
        Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, UUID userId);

        @Query("""
                        SELECT (COUNT(*) > 0) as isBorrowed
                        FROM BookTransactionHistory history
                        WHERE history.user.id = :userId
                        AND history.book.id = :bookId
                        AND history.returnApproved = false
                        """)
        boolean isAlreadyBorrowedByUser(@Param("bookId") UUID bookId,
                        @Param("userId") UUID userId);

        @Query("""
                        SELECT history
                        FROM BookTransactionHistory history
                        WHERE history.user.id = :userId
                        AND history.book.id = :bookId
                        AND history.returned = false
                        AND history.returnApproved = false
                        """)
        Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") UUID bookId,
                        @Param("userId") UUID userId);

        @Query("""
                        SELECT history
                        FROM BookTransactionHistory history
                        WHERE history.book.owner.id = :userId
                        AND history.book.id = :bookId
                        AND history.returned = true
                        AND history.returnApproved = false
                        """)
        Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") UUID bookId,
                        @Param("userId") UUID userId);

}
