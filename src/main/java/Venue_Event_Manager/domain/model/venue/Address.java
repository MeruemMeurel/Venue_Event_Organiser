package Venue_Event_Manager.domain.model.venue;

/**
 * Value Object representing a physical address.
 * Implemented as a record for native immutability and concise data handling.
 */
public record Address(
        String street,
        String streetNumber,
        String city,
        String postalCode,
        String country,
        String additionalInfo
) {

    /** Creates an empty address with null fields. */
    public Address(){
        this(null, null, null, null, null, null);
    }

    /** Creates an address without optional additional info. */
    public Address(String street, String streetNumber, String city, String postalCode, String country){
        this(street, streetNumber, city, postalCode, country, null);
    }


    //Wither methods for state transitions
    public Address withStreet(String newStreet){
        return new Address(newStreet,streetNumber,city,postalCode,country,additionalInfo);
    }
    public Address withStreetNumber(String newStreetNumber){
        return new Address(street,newStreetNumber,city,postalCode,country,additionalInfo);
    }
    public Address withCity(String newCity){
        return new Address(street,streetNumber,newCity,postalCode,country,additionalInfo);
    }
    public Address withPostalCode(String newPostalCode){
        return new Address(street,streetNumber,city,newPostalCode,country,additionalInfo);
    }
    public Address withCountry(String newCountry){
        return new Address(street,streetNumber,city,postalCode,newCountry,additionalInfo);
    }
    public Address withAdditionalInfo(String newAdditionalInfo){
        return new Address(street,streetNumber,city,postalCode,country,newAdditionalInfo);
    }


    @Override
    public String toString(){
        return "Address{" +
                "street=" + street + "; " +
                "streetNumber=" + streetNumber + "; " +
                "city=" + city + "; " +
                "postalCode=" + postalCode + "; " +
                "country=" + country + "; " +
                "additionalInfo=" + additionalInfo + ";" +
                "}";
    }

}