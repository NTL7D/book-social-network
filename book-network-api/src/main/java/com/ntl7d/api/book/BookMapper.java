package com.ntl7d.api.book;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.ntl7d.api.file.FileUtils;
import com.ntl7d.api.history.BookTransactionHistory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    @Mapping(target = "archived", constant = "false")
    Book toBook(BookRequest request);

    @Mapping(target = "owner", source = "owner.fullName")
    @Mapping(target = "cover", expression = "java(mapCover(book.getBookCover()))")
    BookResponse toBookResponse(Book book);

    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "authorName", source = "book.authorName")
    @Mapping(target = "isbn", source = "book.isbn")
    @Mapping(target = "rate", source = "book.rate")
    BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history);

    default byte[] mapCover(String bookCoverPath) {
        return FileUtils.readFileFromLocation(bookCoverPath);
    }
}
