package com.ntl7d.api.book;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ntl7d.api.common.PageResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private final BookService bookService;

    @PostMapping()
    public ResponseEntity<String> saveBook(@RequestBody @Valid BookRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.saveBook(request, connectedUser));
    }

    @GetMapping("{bookId}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable UUID bookId) {
        return ResponseEntity.ok(bookService.findBookById(bookId));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    // ------------------ Borrowing and Returning -----------------------------
    @PatchMapping("shareable/{bookId}")
    public ResponseEntity<String> updateShareableStatus(@PathVariable UUID bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("archived/{bookId}")
    public ResponseEntity<String> updateArchivedStatus(@PathVariable UUID bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @PostMapping("borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable UUID bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.borrowBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/{bookId}")
    public ResponseEntity<String> returnBorrowBook(@PathVariable UUID bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.returnBorrowBook(bookId, connectedUser));
    }

    @PatchMapping("borrow/return/approve/{bookId}")
    public ResponseEntity<String> approveReturnBorrowBook(@PathVariable UUID bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.approveReturnBorrowBook(bookId, connectedUser));
    }

    @PostMapping(value = "cover/{bookId}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCover(@PathVariable UUID bookId,
            @Parameter() @RequestPart("file") MultipartFile file, Authentication connectedUser) {
        bookService.uploadBookCover(file, connectedUser, bookId);

        return ResponseEntity.accepted().build();
    }

}
