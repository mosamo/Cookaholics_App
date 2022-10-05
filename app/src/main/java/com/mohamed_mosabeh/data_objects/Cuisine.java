package com.mohamed_mosabeh.data_objects;

public class Cuisine extends Category {
    private String country_code;
    
    
    // Country Code methods can be used to dynamically display unicode flags: 🇦🇪 🇦🇷 🇧🇪
    // depending on the layout considerations, we may not use this
    // but I'm putting it as an option for future proofing
    
    public String getCountry_code() {
        return country_code;
    }
    
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
