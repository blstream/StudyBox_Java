package com.bls.patronage.resources;

import com.bls.patronage.StorageContexts;
import com.bls.patronage.StorageException;
import com.bls.patronage.StorageService;
import com.bls.patronage.api.FlashcardRepresentation;
import com.bls.patronage.db.dao.FlashcardDAO;
import com.bls.patronage.db.model.Flashcard;
import com.bls.patronage.db.model.User;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.params.UUIDParam;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Path("/decks/{deckId}/flashcards/{flashcardId}")
@Produces(MediaType.APPLICATION_JSON)
public class FlashcardResource {
    private final FlashcardDAO flashcardDAO;
    private final StorageService storageService;

    public FlashcardResource(FlashcardDAO flashcardDAO, StorageService storageService) {
        this.flashcardDAO = flashcardDAO;
        this.storageService = storageService;
    }

    @GET
    public FlashcardRepresentation getFlashcard(
            @Valid @PathParam("flashcardId") UUIDParam flashcardId) {

        return new FlashcardRepresentation(
                flashcardDAO.getFlashcardById(flashcardId.get())
        );
    }

    @DELETE
    public void deleteFlashcard(
            @Valid @PathParam("flashcardId") UUIDParam flashcardId) {

        flashcardDAO.getFlashcardById(flashcardId.get());
        flashcardDAO.deleteFlashcard(flashcardId.get());
    }

    @PUT
    public FlashcardRepresentation updateFlashcard(
            @Valid @PathParam("flashcardId") UUIDParam flashcardId,
            @Valid FlashcardRepresentation flashcard,
            @Valid @PathParam("deckId") UUIDParam deckId) {


        flashcardDAO.getFlashcardById(flashcardId.get());
        flashcardDAO.updateFlashcard(flashcard.setId(flashcardId.get()).setDeckId(deckId.get()).map());


        return flashcard;
    }

    @Path("/questionImage")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public FlashcardRepresentation postQuestionImage(
            @Auth User user,
            @Valid @PathParam("flashcardId") UUIDParam flashcardId,
            @Valid @PathParam("deckId") UUIDParam deckId,
            @FormDataParam("file") InputStream inputStream,
            @Context HttpServletRequest request) throws StorageException, MalformedURLException {

        final UUID questionImageId = storageService.create(inputStream, StorageContexts.FLASHCARDS, user.getId());
        final URL questionImageURL = storageService.createPublicURL(request, StorageResource.class, user.getId(), StorageContexts.FLASHCARDS, questionImageId);

        final Flashcard result = flashcardDAO.getFlashcardById(flashcardId.get()).setQuestionImageURL(questionImageURL.toString());
        flashcardDAO.updateFlashcard(result);

        return new FlashcardRepresentation(result);
    }

    @Path("/answerImage")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public FlashcardRepresentation postAnswerImage(
            @Auth User user,
            @Valid @PathParam("flashcardId") UUIDParam flashcardId,
            @Valid @PathParam("deckId") UUIDParam deckId,
            @FormDataParam("file") InputStream inputStream,
            @Context HttpServletRequest request) throws StorageException, MalformedURLException {

        final UUID answerImageId = storageService.create(inputStream, StorageContexts.FLASHCARDS, user.getId());
        final URL answerImageURL = storageService.createPublicURL(request, StorageResource.class, user.getId(), StorageContexts.FLASHCARDS, answerImageId);

        final Flashcard result = flashcardDAO.getFlashcardById(flashcardId.get()).setAnswerImageURL(answerImageURL.toString());
        flashcardDAO.updateFlashcard(result);

        return new FlashcardRepresentation(result);
    }
}
