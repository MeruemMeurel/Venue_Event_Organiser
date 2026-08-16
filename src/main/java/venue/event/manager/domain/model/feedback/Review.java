package venue.event.manager.domain.model.feedback;

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
     * Returns the id.
     * @return id
     */
    public long getId() { return id; }
    /**
     * Returns a copy with an updated id.
     * @param newId replacement id
     * @return copy with the updated id
     */
    public Review withId(long newId) {
        return new Review(newId, user_id, event_id, rating, comment, created_at);
    }

    /**
     * Returns the user id.
     * @return user id
     */
    public long getUserId() { return user_id; }
    /**
     * Returns a copy with an updated user id.
     * @param newUserId replacement user id
     * @return copy with the updated user id
     */
    public Review withUserId(long newUserId) {
        return new Review(id, newUserId, event_id, rating, comment, created_at);
    }

    /**
     * Returns the event id.
     * @return event id
     */
    public long getEventId() { return event_id; }
    /**
     * Returns a copy with an updated event id.
     * @param newEventId replacement event id
     * @return copy with the updated event id
     */
    public Review withEventId(long newEventId) {
        return new Review(id, user_id, newEventId, rating, comment, created_at);
    }

    /**
     * Returns the rating.
     * @return rating
     */
    public int getRating() { return rating; }
    /**
     * Returns a copy with an updated rating.
     * @param newRating replacement rating
     * @return copy with the updated rating
     */
    public Review withRating(int newRating) {
        return new Review(id, user_id, event_id, newRating, comment, created_at);
    }

    /**
     * Returns the comment.
     * @return comment
     */
    public String getComment() { return comment; }
    /**
     * Returns a copy with an updated comment.
     * @param newComment replacement comment
     * @return copy with the updated comment
     */
    public Review withComment(String newComment) {
        return new Review(id, user_id, event_id, rating, newComment, created_at);
    }

    /**
     * Returns the created at.
     * @return created at
     */
    public LocalDateTime getCreatedAt() { return created_at; }
    /**
     * Returns a copy with an updated created at.
     * @param newCreatedAt replacement created at
     * @return copy with the updated created at
     */
    public Review withCreatedAt(LocalDateTime newCreatedAt) {
        return new Review(id, user_id, event_id, rating, comment, newCreatedAt);
    }


    @Override
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
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(user_id, event_id, created_at);
    }

}
