package Venue_Event_Manager.domain.model.venue;

/**
 * Value Object representing a physical address.
 * Implemented as a record for native immutability and concise data handling.
 */
public record Address(
        String street,
        String street_number,
        String city,
        String postal_code,
        String country,
        String additional_info
) {

    /** Creates an empty address with null fields. */
    public Address(){
        this(null, null, null, null, null, null);
    }

    /** Creates an address without optional additional info. */
    public Address(String street, String street_number, String city, String postal_code, String country){
        this(street, street_number, city, postal_code, country, null);
    }


    //Wither methods for state transitions
    public Address withStreet(String newStreet){
        return new Address(newStreet, street_number, city, postal_code, country, additional_info);
    }
    public Address withStreetNumber(String newStreetNumber){
        return new Address(street, newStreetNumber, city, postal_code, country, additional_info);
    }
    public Address withCity(String newCity){
        return new Address(street, street_number, newCity, postal_code, country, additional_info);
    }
    public Address withPostalCode(String newPostalCode){
        return new Address(street, street_number, city, newPostalCode, country, additional_info);
    }
    public Address withCountry(String newCountry){
        return new Address(street, street_number, city, postal_code, newCountry, additional_info);
    }
    public Address withAdditionalInfo(String newAdditionalInfo){
        return new Address(street, street_number, city, postal_code, country, newAdditionalInfo);
    }


    @Override
    public String toString(){
        return "Address{" +
                "street=" + street + "; " +
                "streetNumber=" + street_number + "; " +
                "city=" + city + "; " +
                "postalCode=" + postal_code + "; " +
                "country=" + country + "; " +
                "additionalInfo=" + additional_info + ";" +
                "}";
    }

}