package Venue_Event_Manager.domain.model.feedback;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a user's feedback for an event.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Review {

    //attributes
    private final long id;
    private final long user_id; //reference to User
    private final long event_id; //reference to Event
    private final int rating;
    private final String comment; //NULLABLE, default value is " "
    private final LocalDateTime created_at; // Null only for transient entities; assigned before persistence.


    //constructors
    /** Initializes an empty review with default and empty values. */
    public Review(){ this(0 ,0, 0, 0, "", null); }

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param user_id reviewer identifier
     * @param event_id reviewed event identifier
     *
     * @param rating numeric rating
     * @param comment optional comment
     * @param created_at creation timestamp */
    public Review(long id, long user_id, long event_id, int rating, String comment, LocalDateTime created_at){
        this.id = id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.rating = rating;
        this.comment = comment != null ? comment : "";
        this.created_at = created_at;
    }

    /** Creates an unsaved review.
     *
     * @param user_id reviewer identifier
     * @param event_id reviewed event identifier
     * @param rating numeric rating
     *
     * @param comment optional comment
     * @param created_at creation timestamp */
    public Review(long user_id, long event_id, int rating, String comment, LocalDateTime created_at){
        this(0 ,user_id, event_id, rating, comment, created_at);
    }


    //getters and withers
    /**
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId() { return id; }
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Review withId(long newId) {
        return new Review(newId, user_id, event_id, rating, comment, created_at);
    }

    /**
     * Performs the {@code getUserId} operation.
     * @return operation result
     */
    public long getUserId() { return user_id; }
    /**
     * Performs the {@code withUserId} operation.
     * @param newUserId newUserId value
     * @return operation result
     */
    public Review withUserId(long newUserId) {
        return new Review(id, newUserId, event_id, rating, comment, created_at);
    }

    /**
     * Performs the {@code getEventId} operation.
     * @return operation result
     */
    public long getEventId() { return event_id; }
    /**
     * Performs the {@code withEventId} operation.
     * @param newEventId newEventId value
     * @return operation result
     */
    public Review withEventId(long newEventId) {
        return new Review(id, user_id, newEventId, rating, comment, created_at);
    }

    /**
     * Performs the {@code getRating} operation.
     * @return operation result
     */
    public int getRating() { return rating; }
    /**
     * Performs the {@code withRating} operation.
     * @param newRating newRating value
     * @return operation result
     */
    public Review withRating(int newRating) {
        return new Review(id, user_id, event_id, newRating, comment, created_at);
    }

    /**
     * Performs the {@code getComment} operation.
     * @return operation result
     */
    public String getComment() { return comment; }
    /**
     * Performs the {@code withComment} operation.
     * @param newComment newComment value
     * @return operation result
     */
    public Review withComment(String newComment) {
        return new Review(id, user_id, event_id, rating, newComment, created_at);
    }

    /**
     * Performs the {@code getCreatedAt} operation.
     * @return operation result
     */
    public LocalDateTime getCreatedAt() { return created_at; }
    /**
     * Performs the {@code withCreatedAt} operation.
     * @param newCreatedAt newCreatedAt value
     * @return operation result
     */
    public Review withCreatedAt(LocalDateTime newCreatedAt) {
        return new Review(id, user_id, event_id, rating, comment, newCreatedAt);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString(){
        return "Review{" +
                "id=" + id + "; " +
                "user_id=" + user_id + "; " +
                "event_id=" + event_id + "; " +
                "rating=" + rating + "; " +
                "comment=" + comment + "; " +
                "created_at=" + created_at + ";" +
                "}";
    }

    /** Compares review based on ID or user_id, event_id and created_at uniqueness */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Review review = (Review) other;
        if (id != 0 && review.id != 0){
            return Objects.equals(id, review.id);
        }
        return Objects.equals(user_id, review.user_id) && Objects.equals(event_id, review.event_id) &&
                Objects.equals(created_at, review.created_at);
    }

    @Override
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(user_id, event_id, created_at);
    }

}
