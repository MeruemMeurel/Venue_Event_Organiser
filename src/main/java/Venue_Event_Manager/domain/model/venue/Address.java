package Venue_Event_Manager.domain.model.venue;

/**
 * Value Object representing a physical address.
 * Implemented as a record for native immutability and concise data handling.
 *
 * @param street street name
 * @param street_number street number
 * @param city city name
 * @param postal_code postal code
 * @param country country name
 * @param additional_info optional address details
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

    /**
     * Creates an address without optional additional information.
     *
     * @param street street name
     * @param street_number street number
     * @param city city name
     * @param postal_code postal code
     * @param country country name
     */
    public Address(String street, String street_number, String city, String postal_code, String country){
        this(street, street_number, city, postal_code, country, null);
    }


    //Wither methods for state transitions
    /**
     * Returns a copy with a different street.
     * @param newStreet replacement street
     * @return a copy with the supplied street
     */
    public Address withStreet(String newStreet){
        return new Address(newStreet, street_number, city, postal_code, country, additional_info);
    }
    /**
     * Returns a copy with a different street number.
     * @param newStreetNumber replacement street number
     * @return a copy with the supplied street number
     */
    public Address withStreetNumber(String newStreetNumber){
        return new Address(street, newStreetNumber, city, postal_code, country, additional_info);
    }
    /**
     * Returns a copy with a different city.
     * @param newCity replacement city
     * @return a copy with the supplied city
     */
    public Address withCity(String newCity){
        return new Address(street, street_number, newCity, postal_code, country, additional_info);
    }
    /**
     * Returns a copy with a different postal code.
     * @param newPostalCode replacement postal code
     * @return a copy with the supplied postal code
     */
    public Address withPostalCode(String newPostalCode){
        return new Address(street, street_number, city, newPostalCode, country, additional_info);
    }
    /**
     * Returns a copy with a different country.
     * @param newCountry replacement country
     * @return a copy with the supplied country
     */
    public Address withCountry(String newCountry){
        return new Address(street, street_number, city, postal_code, newCountry, additional_info);
    }
    /**
     * Returns a copy with different additional information.
     * @param newAdditionalInfo replacement additional details
     * @return a copy with the supplied details
     */
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
