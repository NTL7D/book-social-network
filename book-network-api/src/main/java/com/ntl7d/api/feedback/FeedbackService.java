package com.ntl7d.api.feedback;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.ntl7d.api.book.Book;
import com.ntl7d.api.book.BookRepository;
import com.ntl7d.api.common.PageResponse;
import com.ntl7d.api.exception.OperationNotPermittedException;
import com.ntl7d.api.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {
        private final BookRepository bookRepository;
        private final FeedbackRepository feedbackRepository;
        private final FeedbackMapper feedbackMapper;

        private static final String CANT_FIND_BOOK_ID = "No book found with id: ";
        private static final String CANT_SEND_FEEDBACK_TO_ARCHIVED_OR_NOT_SHAREABLE = "You cannot give a feedback for an archived or not shareable book";
        private static final String CANT_SEND_FEEDBACK_YOUR_OWN_BOOK = "You cannot give a feedback to your own book";

        public String saveFeedback(FeedbackRequest request, Authentication connectedUser) {
                Book book = bookRepository.findById(request.bookId()).orElseThrow(
                                () -> new EntityNotFoundException(CANT_FIND_BOOK_ID + request.bookId()));

                if (book.isArchived() || !book.isShareable()) {
                        throw new OperationNotPermittedException(
                                        CANT_SEND_FEEDBACK_TO_ARCHIVED_OR_NOT_SHAREABLE);
                }

                User user = ((User) connectedUser.getPrincipal());

                if (Objects.equals(book.getOwner().getId(), user.getId())) {
                        throw new OperationNotPermittedException(CANT_SEND_FEEDBACK_YOUR_OWN_BOOK);
                }

                Feedback feedback = feedbackMapper.toFeedback(request);

                return feedbackRepository.save(feedback).getId().toString();
        }

        public PageResponse<FeedbackResponse> findAllFeedbackByBook(UUID bookId, int page, int size,
                        Authentication connectedUser) {
                Pageable pageable = PageRequest.of(page, size);

                User user = ((User) connectedUser.getPrincipal());

                Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);

                List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback, user.getId()))
                                .toList();

                return new PageResponse<>(feedbackResponses, feedbacks.getNumber(), feedbacks.getSize(),
                                feedbacks.getTotalElements(), feedbacks.getTotalPages(), feedbacks.isFirst(),
                                feedbacks.isLast());
        }

}
