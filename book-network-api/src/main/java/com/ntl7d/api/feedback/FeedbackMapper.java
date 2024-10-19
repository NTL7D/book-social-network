package com.ntl7d.api.feedback;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.ntl7d.api.book.Book;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedbackMapper {

    @Mapping(target = "book", expression = "java(toBook(request))")
    Feedback toFeedback(FeedbackRequest request);

    @Mapping(target = "ownFeedback", expression = "java(java.util.Objects.equals(feedback.getCreatedBy(), id))")
    FeedbackResponse toFeedbackResponse(Feedback feedback, UUID id);

    default Book toBook(FeedbackRequest request) {
        return Book.builder().id(request.bookId()).shareable(false).archived(false).build();
    }
}
