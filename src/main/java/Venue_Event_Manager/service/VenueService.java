package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.venue.Address;
import Venue_Event_Manager.domain.model.venue.Venue;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.VenueRepository;

import java.time.LocalDateTime;
import java.util.List;

public class VenueService {

    private final TransactionManager transactionManager;
    private final VenueRepository venueRepository;

    /**
     * Initializes VenueService with the repository needed to handle venues.
     * @param venueRepository repository used to access venue data
     */
    public VenueService(VenueRepository venueRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.venueRepository = venueRepository;
    }

    /**
     * Gets all venues stored in database.
     * @return List of all venues
     */
    public List<Venue> getAllVenues(){
        return transactionManager.inReadOnly(conn->
                venueRepository.findAll(conn));
    }

    /**
     * Gets a venue from its id.
     * @param venueId the id of the venue to find
     * @return Venue object if found
     * @throws NotFoundException if no venue is found with such id
     */
    public Venue getVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                venueRepository.findById(conn,venueId)
                        .orElseThrow(() -> new NotFoundException("No venue found with id "+venueId)));
    }

    /**
     * Searches venues by name.
     * @param name the name or part of name to search
     * @return List of venues matching the given name
     */
    public List<Venue> searchVenueByName(String name){
        validateSearchField(name,"Venue name");

        return transactionManager.inReadOnly(conn->
                venueRepository.findByName(conn,name));
    }

    /**
     * Gets all venues in a specific city.
     * @param city the city used to filter venues
     * @return List of venues in the given city
     */
    public List<Venue> getVenuesByCity(String city){
        validateSearchField(city,"City");

        return transactionManager.inReadOnly(conn->
                venueRepository.findByCity(conn,city));
    }

    /**
     * Gets all venues in a specific country.
     * @param country the country used to filter venues
     * @return List of venues in the given country
     */
    public List<Venue> getVenuesByCountry(String country){
        validateSearchField(country,"Country");

        return transactionManager.inReadOnly(conn->
                venueRepository.findByCountry(conn,country));
    }

    /**
     * Gets all venues with at least one available space during a time interval.
     * Cancelled events are ignored when checking space availability.
     * @param begin the beginning of the time interval
     * @param end the end of the time interval
     * @return List of venues with at least one available space
     */
    public List<Venue> getVenuesWithAvailableSpaces(LocalDateTime begin, LocalDateTime end){
        validateTimeRange(begin,end);

        return transactionManager.inReadOnly(conn->
                venueRepository.findAllWithAvailableSpaces(conn,begin,end));
    }

    /**
     * Inserts a new venue in database.
     * @param venue the venue to insert
     * @return generated id of the new venue
     * @throws ValidationException if venue data are not valid
     */
    public long createVenue(Venue venue){
        validate(venue);

        return transactionManager.inTransaction(conn->
                venueRepository.insert(conn,venue));
    }

    /**
     * Updates an existing venue in database.
     * @param venue the venue object with updated data
     * @throws ValidationException if venue data or id are not valid
     */
    public void updateVenue(Venue venue){
        validateForUpdate(venue);

        transactionManager.inTransaction(conn->{
            venueRepository.update(conn,venue);
            return null;
        });
    }

    /**
     * Deletes a venue from database.
     * @param venueId the id of the venue to delete
     * @throws ValidationException if venue id is not valid
     */
    public void deleteVenue(long venueId){
        validateId(venueId);

        transactionManager.inTransaction(conn->{
            venueRepository.deleteById(conn,venueId);
            return null;
        });
    }

    /**
     * Validates all venue fields before insert.
     * @param venue the venue to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(Venue venue){
        validateVenueNotNull(venue);
        validateName(venue.getName());
        validateDescription(venue.getDescription());
        validateAddress(venue.getAddress());
    }

    /**
     * Validates all venue fields before update.
     * @param venue the venue to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForUpdate(Venue venue){
        validate(venue);
        validateId(venue.getId());
    }

    /**
     * Validates that a venue is not null.
     * @param venue the venue to validate
     * @throws ValidationException if venue is null
     */
    private void validateVenueNotNull(Venue venue){
        if(venue == null) throw new ValidationException("Venue cannot be null");
    }

    /**
     * Validates venue id.
     * @param venueId the id to validate
     * @throws ValidationException if id is not valid
     */
    private void validateId(long venueId){
        if(venueId <= 0) throw new ValidationException("Venue id is not valid");
    }

    /**
     * Validates venue name.
     * @param name the name to validate
     * @throws ValidationException if name is empty or has invalid length
     */
    private void validateName(String name){
        if(name == null || name.isBlank()) throw new ValidationException("Venue name cannot be empty");
        if(name.length() < 2 || name.length() > 100) throw new ValidationException("Venue name must be between 2 and 100 characters");
    }

    /**
     * Validates venue description.
     * @param description the description to validate
     * @throws ValidationException if description has invalid length
     */
    private void validateDescription(String description){
        if(description != null && description.length() > 1000) throw new ValidationException("Venue description cannot exceed 1000 characters");
    }

    /**
     * Validates venue address.
     * @param address the address to validate
     * @throws ValidationException if address is empty or has invalid fields
     */
    private void validateAddress(Address address){
        if(address == null) throw new ValidationException("Venue address cannot be empty");
        validateRequiredAddressField(address.street(),"Street");
        validateRequiredAddressField(address.street_number(),"Street number");
        validateRequiredAddressField(address.city(),"City");
        validateRequiredAddressField(address.postal_code(),"Postal code");
        validateRequiredAddressField(address.country(),"Country");
        validateAdditionalInfo(address.additional_info());
    }

    /**
     * Validates a required address field.
     * @param value the field value to validate
     * @param fieldName the name of the field
     * @throws ValidationException if value is empty or has invalid length
     */
    private void validateRequiredAddressField(String value, String fieldName){
        if(value == null || value.isBlank()) throw new ValidationException(fieldName+" cannot be empty");
        if(value.length() > 255) throw new ValidationException(fieldName+" cannot exceed 255 characters");
    }

    /**
     * Validates optional address additional info.
     * @param additionalInfo the additional info to validate
     * @throws ValidationException if additional info has invalid length
     */
    private void validateAdditionalInfo(String additionalInfo){
        if(additionalInfo != null && additionalInfo.length() > 1000) {
            throw new ValidationException("Additional info cannot exceed 1000 characters");
        }
    }

    /**
     * Validates a search field.
     * @param value the field value to validate
     * @param fieldName the name of the field
     * @throws ValidationException if value is empty
     */
    private void validateSearchField(String value, String fieldName){
        if(value == null || value.isBlank()) throw new ValidationException(fieldName+" cannot be empty");
    }

    /**
     * Validates a time range.
     * @param begin the beginning of the time range
     * @param end the end of the time range
     * @throws ValidationException if dates are empty or begin is after end
     */
    private void validateTimeRange(LocalDateTime begin, LocalDateTime end){
        if(begin == null || end == null) throw new ValidationException("Begin or end date cannot be empty");
        if(begin.isAfter(end) || begin.isEqual(end)) throw new ValidationException("Begin date must be before end date");
    }
}
