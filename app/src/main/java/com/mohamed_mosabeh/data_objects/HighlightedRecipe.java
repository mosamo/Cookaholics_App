package com.mohamed_mosabeh.data_objects;

public class HighlightedRecipe extends Recipe {
    
    private String curator_name;
    private String curator_comment;
    private double curator_rating;
    
    public String getCurator_name() {
        return curator_name;
    }
    
    public void setCurator_name(String curator_name) {
        this.curator_name = curator_name;
    }
    
    public String getCurator_comment() {
        return curator_comment;
    }
    
    public void setCurator_comment(String curator_comment) {
        this.curator_comment = curator_comment;
    }
    
    public double getCurator_rating() {
        return curator_rating;
    }
    public void setCurator_rating(double curator_rating) {
        this.curator_rating = curator_rating;
    }
    
}
