package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.request.EventRequest;
import Venue_Event_Manager.domain.model.request.EventRequestStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.exception.ConflictException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.EventRequestRepository;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.repository.VenueRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class EventRequestService {

    private final TransactionManager transactionManager;
    private final EventRequestRepository eventRequestRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    /**
     * Initializes EventRequestService with repositories needed to handle event requests.
     * @param eventRequestRepository repository used to access event request data
     * @param userRepository repository used to access user data
     * @param venueRepository repository used to access venue data
     */
    public EventRequestService(EventRequestRepository eventRequestRepository, UserRepository userRepository,
                               VenueRepository venueRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.eventRequestRepository = eventRequestRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    /**
     * Gets all event requests stored in database.
     * @return List of all event requests
     */
    public List<EventRequest> getAllRequests(){
        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAll(conn));
    }

    /**
     * Gets an event request from its id.
     * @param requestId the id of the event request to find
     * @return EventRequest object if found
     * @throws NotFoundException if no event request is found with such id
     */
    public EventRequest getRequest(long requestId){
        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findById(conn,requestId)
                        .orElseThrow(() -> new NotFoundException("No event request found with id "+requestId)));
    }

    /**
     * Gets all event requests made by a specific requester.
     * @param requesterId the id of the requester
     * @return List of event requests made by the requester
     */
    public List<EventRequest> getRequestsByRequester(long requesterId){
        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByRequesterId(conn,requesterId));
    }

    /**
     * Gets all event requests assigned to a specific handler.
     * @param handlerId the id of the handler
     * @return List of event requests assigned to the handler
     */
    public List<EventRequest> getRequestsByHandler(long handlerId){
        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByHandlerId(conn,handlerId));
    }

    /**
     * Gets all event requests linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of event requests linked to the venue
     */
    public List<EventRequest> getRequestsByVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Gets all event requests with a specific status.
     * @param status the status used to filter requests
     * @return List of event requests with the given status
     */
    public List<EventRequest> getRequestsByStatus(EventRequestStatus status){
        validateStatus(status);

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByStatus(conn,status));
    }

    /**
     * Gets all event requests starting exactly at a specific date and time.
     * @param start the start date and time
     * @return List of event requests starting at the given date and time
     */
    public List<EventRequest> getRequestsStartingAt(LocalDateTime start){
        validateDateTime(start,"Start date");

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByStartDate(conn,start));
    }

    /**
     * Gets all event requests ending exactly at a specific date and time.
     * @param end the end date and time
     * @return List of event requests ending at the given date and time
     */
    public List<EventRequest> getRequestsEndingAt(LocalDateTime end){
        validateDateTime(end,"End date");

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllByEndDate(conn,end));
    }

    /**
     * Gets all event requests starting after a specific date and time.
     * @param dateTime the threshold date and time
     * @return List of event requests starting after the threshold
     */
    public List<EventRequest> getRequestsAfter(LocalDateTime dateTime){
        validateDateTime(dateTime,"Date");

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllAfter(conn,dateTime));
    }

    /**
     * Gets all event requests starting before a specific date and time.
     * @param dateTime the threshold date and time
     * @return List of event requests starting before the threshold
     */
    public List<EventRequest> getRequestsBefore(LocalDateTime dateTime){
        validateDateTime(dateTime,"Date");

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllBefore(conn,dateTime));
    }

    /**
     * Gets all event requests between two dates.
     * @param start the beginning of the time range
     * @param end the end of the time range
     * @return List of event requests included in the time range
     */
    public List<EventRequest> getRequestsBetween(LocalDateTime start, LocalDateTime end){
        validateTimeRange(start,end);

        return transactionManager.inReadOnly(conn->
                eventRequestRepository.findAllBetween(conn,start,end));
    }

    /**
     * Inserts a new event request in database.
     * @param request the event request to insert
     * @return generated id of the new request
     * @throws ValidationException if request data are not valid
     */
    public long createRequest(EventRequest request){
        validateRequestNotNull(request);
        EventRequest requestToInsert = request.getCreatedAt() == null
                ? request.withCreatedAt(LocalDateTime.now())
                : request;
        validate(requestToInsert);

        return transactionManager.inTransaction(conn->
                eventRequestRepository.insert(conn,requestToInsert));
    }

    /**
     * Updates an existing event request in database.
     * @param request the event request object with updated data
     * @throws ValidationException if request data or id are not valid
     */
    public void updateRequest(EventRequest request){
        validateForUpdate(request);

        transactionManager.inTransaction(conn->{
            eventRequestRepository.update(conn,request);
            return null;
        });
    }

    /**
     * Deletes an event request from database.
     * @param requestId the id of the event request to delete
     * @throws ValidationException if request id is not valid
     */
    public void deleteRequest(long requestId){
        validateId(requestId,"Request id");

        transactionManager.inTransaction(conn->{
            eventRequestRepository.deleteById(conn,requestId);
            return null;
        });
    }

    /**
     * Assigns an admin handler to a pending request.
     * @param requestId the id of the request
     * @param handlerId the id of the admin handler
     * @throws NotFoundException if no request is found with such id
     * @throws ValidationException if handler is not valid
     * @throws ConflictException if request is not pending
     */
    public void assignHandler(long requestId, long handlerId){
        validateId(requestId,"Request id");
        validateId(handlerId,"Handler id");

        transactionManager.inTransaction(conn->{
            validateHandlerId(conn,handlerId);

            EventRequest request = eventRequestRepository.findById(conn,requestId)
                    .orElseThrow(() -> new NotFoundException("No event request found with id "+requestId));
            validateRequestIsPending(request);

            eventRequestRepository.update(conn,request.withHandlerId(handlerId));
            return null;
        });
    }

    /**
     * Accepts a pending request and stores the proposed quote.
     * @param requestId the id of the request
     * @param quote the accepted quote
     * @throws NotFoundException if no request is found with such id
     * @throws ValidationException if quote is not valid or no handler is assigned
     * @throws ConflictException if request is not pending
     */
    public void acceptRequest(long requestId, BigDecimal quote){
        validateId(requestId,"Request id");
        validateAcceptedQuote(quote);

        transactionManager.inTransaction(conn->{
            EventRequest request = eventRequestRepository.findById(conn,requestId)
                    .orElseThrow(() -> new NotFoundException("No event request found with id "+requestId));
            validateRequestIsPending(request);
            validateRequestHasHandler(request);

            EventRequest acceptedRequest = request
                    .withStatus(EventRequestStatus.ACCEPTED)
                    .withQuote(quote)
                    .withClosedAt(LocalDateTime.now());

            eventRequestRepository.update(conn,acceptedRequest);
            return null;
        });
    }

    /**
     * Rejects a pending request.
     * @param requestId the id of the request
     * @throws NotFoundException if no request is found with such id
     * @throws ConflictException if request is not pending
     */
    public void rejectRequest(long requestId){
        validateId(requestId,"Request id");

        transactionManager.inTransaction(conn->{
            EventRequest request = eventRequestRepository.findById(conn,requestId)
                    .orElseThrow(() -> new NotFoundException("No event request found with id "+requestId));
            validateRequestIsPending(request);

            EventRequest rejectedRequest = request
                    .withStatus(EventRequestStatus.REJECTED)
                    .withClosedAt(LocalDateTime.now());

            eventRequestRepository.update(conn,rejectedRequest);
            return null;
        });
    }

    /**
     * Cancels a pending request by requester action.
     * @param requestId the id of the request
     * @throws NotFoundException if no request is found with such id
     * @throws ConflictException if request is not pending
     */
    public void cancelRequest(long requestId){
        validateId(requestId,"Request id");

        transactionManager.inTransaction(conn->{
            EventRequest request = eventRequestRepository.findById(conn,requestId)
                    .orElseThrow(() -> new NotFoundException("No event request found with id "+requestId));
            validateRequestIsPending(request);

            EventRequest cancelledRequest = request
                    .withStatus(EventRequestStatus.CANCELLED)
                    .withClosedAt(LocalDateTime.now());

            eventRequestRepository.update(conn,cancelledRequest);
            return null;
        });
    }

    /**
     * Validates all request fields before insert.
     * @param request the request to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(EventRequest request){
        validateRequestNotNull(request);
        validateRequesterId(request.getRequesterId());
        if(request.getHandlerId() != null) validateHandlerId(request.getHandlerId());
        validateVenueId(request.getVenueId());
        validateName(request.getName());
        validateDescription(request.getDescription());
        validateTimeRange(request.getBeginDatetime(),request.getEndDatetime());
        validateStatus(request.getStatus());
        validateDateTime(request.getCreatedAt(),"Created date");
        validateClosedAt(request.getClosedAt(),request.getCreatedAt());
        validateQuote(request.getQuote());
    }

    /**
     * Validates all request fields before update.
     * @param request the request to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForUpdate(EventRequest request){
        validate(request);
        validateId(request.getId(),"Request id");
    }

    /**
     * Validates that request is not null.
     * @param request the request to validate
     * @throws ValidationException if request is null
     */
    private void validateRequestNotNull(EventRequest request){
        if(request == null) throw new ValidationException("Event request cannot be null");
    }

    /**
     * Validates requester id and checks that requester exists and is not an admin.
     * @param requesterId the requester id to validate
     * @throws ValidationException if requester is not valid
     */
    private void validateRequesterId(long requesterId){
        validateId(requesterId,"Requester id");

        transactionManager.inReadOnly(conn->{
            User requester = userRepository.findById(conn,requesterId)
                    .orElseThrow(() -> new ValidationException("No requester found with id "+requesterId));
            if(requester.isAdmin()) throw new ValidationException("Requester cannot be an admin");
            return null;
        });
    }

    /**
     * Validates handler id and checks that handler exists and is an admin.
     * @param handlerId the handler id to validate
     * @throws ValidationException if handler is not valid
     */
    private void validateHandlerId(long handlerId){
        validateId(handlerId,"Handler id");

        transactionManager.inReadOnly(conn->{
            validateHandlerId(conn,handlerId);
            return null;
        });
    }

    /**
     * Validates handler id using an existing connection.
     * @param conn the db connection
     * @param handlerId the handler id to validate
     * @throws ValidationException if handler is not valid
     */
    private void validateHandlerId(Connection conn, long handlerId){
        User handler = userRepository.findById(conn,handlerId)
                .orElseThrow(() -> new ValidationException("No handler found with id "+handlerId));
        if(!handler.isAdmin()) throw new ValidationException("Handler must be an admin");
    }

    /**
     * Validates venue id and checks that venue exists.
     * @param venueId the venue id to validate
     * @throws ValidationException if venue is not valid
     */
    private void validateVenueId(long venueId){
        validateId(venueId,"Venue id");

        transactionManager.inReadOnly(conn->{
            venueRepository.findById(conn,venueId)
                    .orElseThrow(() -> new ValidationException("No venue found with id "+venueId));
            return null;
        });
    }

    /**
     * Validates positive id.
     * @param id the id to validate
     * @param label the name of the id field
     * @throws ValidationException if id is not valid
     */
    private void validateId(long id, String label){
        if(id <= 0) throw new ValidationException(label+" is not valid");
    }

    /**
     * Validates request name.
     * @param name the name to validate
     * @throws ValidationException if name is empty or has invalid length
     */
    private void validateName(String name){
        if(name == null || name.isBlank()) throw new ValidationException("Request name cannot be empty");
        if(name.length() < 2 || name.length() > 100) throw new ValidationException("Request name must be between 2 and 100 characters");
    }

    /**
     * Validates request description.
     * @param description the description to validate
     * @throws ValidationException if description has invalid length
     */
    private void validateDescription(String description){
        if(description != null && description.length() > 1000) {
            throw new ValidationException("Request description cannot exceed 1000 characters");
        }
    }

    /**
     * Validates status.
     * @param status the status to validate
     * @throws ValidationException if status is null
     */
    private void validateStatus(EventRequestStatus status){
        if(status == null) throw new ValidationException("Request status cannot be null");
    }

    /**
     * Validates date and time value.
     * @param dateTime the date and time to validate
     * @param label the name of the field
     * @throws ValidationException if date and time is null
     */
    private void validateDateTime(LocalDateTime dateTime, String label){
        if(dateTime == null) throw new ValidationException(label+" cannot be empty");
    }

    /**
     * Validates request time range.
     * @param begin the beginning date and time
     * @param end the ending date and time
     * @throws ValidationException if dates are empty or invalid
     */
    private void validateTimeRange(LocalDateTime begin, LocalDateTime end){
        validateDateTime(begin,"Begin date");
        validateDateTime(end,"End date");
        if(begin.isAfter(end) || begin.isEqual(end)) throw new ValidationException("Begin date must be before end date");
    }

    /**
     * Validates closed date.
     * @param closedAt the closed date to validate
     * @param createdAt the created date used as lower bound
     * @throws ValidationException if closed date is before created date
     */
    private void validateClosedAt(LocalDateTime closedAt, LocalDateTime createdAt){
        if(closedAt != null && createdAt != null && closedAt.isBefore(createdAt)) {
            throw new ValidationException("Closed date cannot be before created date");
        }
    }

    /**
     * Validates quote.
     * @param quote the quote to validate
     * @throws ValidationException if quote is negative
     */
    private void validateQuote(BigDecimal quote){
        if(quote != null && quote.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Quote cannot be negative");
        }
    }

    /**
     * Validates accepted quote.
     * @param quote the quote to validate
     * @throws ValidationException if quote is empty or negative
     */
    private void validateAcceptedQuote(BigDecimal quote){
        if(quote == null) throw new ValidationException("Quote cannot be empty");
        validateQuote(quote);
    }

    /**
     * Validates that request is pending.
     * @param request the request to validate
     * @throws ConflictException if request is not pending
     */
    private void validateRequestIsPending(EventRequest request){
        if(request.getStatus() != EventRequestStatus.PENDING) {
            throw new ConflictException("Event request is not pending");
        }
    }

    /**
     * Validates that request has an assigned handler.
     * @param request the request to validate
     * @throws ValidationException if request has no handler
     */
    private void validateRequestHasHandler(EventRequest request){
        if(request.getHandlerId() == null) {
            throw new ValidationException("Event request must have an assigned handler before acceptance");
        }
    }
}
