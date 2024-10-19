package com.ntl7d.api.book;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ntl7d.api.common.PageResponse;
import com.ntl7d.api.exception.OperationNotPermittedException;
import com.ntl7d.api.file.FileStorageService;
import com.ntl7d.api.history.BookTransactionHistory;
import com.ntl7d.api.history.BookTransactionHistoryRepository;
import com.ntl7d.api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
        private final BookRepository bookRepository;
        private final BookTransactionHistoryRepository transactionHistoryRepository;
        private final BookMapper bookMapper;
        private final FileStorageService fileStorageService;

        private static final String CREATED_DATE = "createdDate";
        private static final String CANT_FIND_BOOK_ID = "No book found with id: ";
        private static final String CANT_BORROW_ARCHIVED_OR_NOT_SHAREABLE = "The request book cannot be borrowed since it is archived or not shareable";
        private static final String CANT_BORROW_OR_RETURN_YOUR_OWN_BOOK = "You cannot borrow or return your own book";

        public String saveBook(BookRequest request, Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Book book = bookMapper.toBook(request);
                book.setOwner(user);
                return bookRepository.save(book).getId().toString();
        }

        public BookResponse findBookById(UUID bookId) {
                return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));
        }

        public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
                User user = ((User) connectedUser.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by(CREATED_DATE).descending());
                Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
                List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse).toList();
                return new PageResponse<>(
                                bookResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());
        }

        public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(CREATED_DATE).descending());
                Page<Book> books = bookRepository.findAll(BookSpecification.withOwner(connectedUser.getName()),
                                pageable);
                List<BookResponse> booksResponse = books.stream()
                                .map(bookMapper::toBookResponse)
                                .toList();
                return new PageResponse<>(
                                booksResponse,
                                books.getNumber(),
                                books.getSize(),
                                books.getTotalElements(),
                                books.getTotalPages(),
                                books.isFirst(),
                                books.isLast());
        }

        // Borrowing and Returning
        public String updateShareableStatus(UUID bookId, Authentication connectedUser) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                User user = ((User) connectedUser.getPrincipal());

                if (!Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException(
                                        "You cannot update other books shareable status");
                }

                book.setShareable(!book.isShareable());
                bookRepository.save(book);

                return bookId.toString();
        }

        public String updateArchivedStatus(UUID bookId, Authentication connectedUser) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                User user = ((User) connectedUser.getPrincipal());

                if (!Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException(
                                        "You cannot update other books archived status");
                }

                book.setShareable(!book.isArchived());
                bookRepository.save(book);

                return bookId.toString();
        }

        public String borrowBook(UUID bookId, Authentication connectedUser) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException(
                                        CANT_BORROW_ARCHIVED_OR_NOT_SHAREABLE);
                }

                User user = ((User) connectedUser.getPrincipal());

                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException("You cannot borrow your own book");
                }

                final boolean isAlreadyBorrowed = transactionHistoryRepository
                                .isAlreadyBorrowedByUser(bookId, user.getId());

                if (isAlreadyBorrowed) {
                        throw new OperationNotPermittedException(
                                        "You have already borrowed this book");
                }

                BookTransactionHistory history = BookTransactionHistory.builder().user(user)
                                .book(book).returned(false).returnApproved(false).build();

                return transactionHistoryRepository.save(history).getId().toString();
        }

        public String returnBorrowBook(UUID bookId, Authentication connectedUser) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException(
                                        CANT_BORROW_ARCHIVED_OR_NOT_SHAREABLE);
                }

                User user = ((User) connectedUser.getPrincipal());

                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException(
                                        CANT_BORROW_OR_RETURN_YOUR_OWN_BOOK);
                }

                BookTransactionHistory history = transactionHistoryRepository
                                .findByBookIdAndUserId(bookId, user.getId())
                                .orElseThrow(() -> new OperationNotPermittedException(
                                                "You have not borrowed this book"));
                history.setReturned(true);

                return transactionHistoryRepository.save(history).getId().toString();
        }

        public String approveReturnBorrowBook(UUID bookId, Authentication connectedUser) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException(
                                        CANT_BORROW_ARCHIVED_OR_NOT_SHAREABLE);
                }

                User user = ((User) connectedUser.getPrincipal());

                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException(
                                        CANT_BORROW_OR_RETURN_YOUR_OWN_BOOK);
                }

                BookTransactionHistory history = transactionHistoryRepository
                                .findByBookIdAndOwnerId(bookId, user.getId())
                                .orElseThrow(() -> new OperationNotPermittedException(
                                                "The book is not returned yet. You cannot approve its return"));
                history.setReturnApproved(true);

                return transactionHistoryRepository.save(history).getId().toString();
        }

        public void uploadBookCover(MultipartFile file, Authentication connectedUser,
                        UUID bookId) {
                Book book = bookRepository.findById(bookId).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + bookId));

                User user = ((User) connectedUser.getPrincipal());

                var bookCover = fileStorageService.saveFile(file, user.getId());

                book.setBookCover(bookCover);

                bookRepository.save(book);

        }

}
