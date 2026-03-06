package Venue_Event_Manager.domain.model.venue;

public record Address(
        String street,
        String streetNumber,
        String city,
        String postalCode,
        String country,
        String additionalInfo
) {

    public Address(){
        this(null, null, null, null, null, null);
    }

    public Address(String street, String streetNumber, String city, String postalCode, String country,
                   String additionalInfo){
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.additionalInfo = additionalInfo;
    }

    public Address(String street, String streetNumber, String city, String postalCode, String country){
        this(street, streetNumber, city, postalCode, country, null);
    }

    public String getStreet(){ return street; }

    public String getStreetNumber(){ return streetNumber; }

    public String getCity(){ return city; }

    public String getPostalCode(){ return postalCode; }

    public String country(){ return country; }

    public String additionalInfo(){ return additionalInfo; }

    @Override
    public String toString(){
        return "Adress{" +
                "street=" + street + "; " +
                "streetNumber='" + streetNumber + "'; " +
                "city='" + city + "'; " +
                "postalCode='" + postalCode + "'; " +
                "country=" + country + "; " +
                "additionalInfo='" + additionalInfo + "'; " +
                "}";
    }

}
