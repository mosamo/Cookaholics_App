package com.mohamed_mosabeh.data_objects;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Recipe {
    
    // attributes
    private String id;
    private String category;
    
    private String description;
    private int duration;
    private String icon;
    private String name;
    private int servings;
    private Long timestamp;
    private String user_id;
    private String display_name;
    private ArrayList<String> tags = new ArrayList<String>();
    
    
    private String cuisine;
    private int likes;
    private int reports;
    private ArrayList<RecipeStep> steps = new ArrayList<RecipeStep>();
    
    // I'm not saving a Tag object ArrayList in tags attribute. just the names:
    // so we are using String instead of Tag this is for performance reasons..
    // ..and we extremely rarely need tag data in recipe page
    
    private boolean highlighted;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public final int getLikes() {
        return likes;
    }
    
    public final void setLikes(int likes) {
        // shouldn't be settable
        this.likes = likes;
    }
    
    public final int getReports() {
        return reports;
    }
    
    public final void setReports(int reports) {
        // shouldn't be settable
        this.reports = reports;
    }
    
    public ArrayList<String> getTags() {
        if (tags.size() == 0) {
            return null;
        }
        return tags;
    }
    
    public void setTags(@Nullable ArrayList<String> tags) {
        if (tags != null) {
            this.tags = tags;
        }
    }
    
    public final ArrayList<RecipeStep> getSteps() {
        if (steps.size() == 0) {
            return null;
        }
        return steps;
    }
    
    public final void setSteps(@Nullable ArrayList<RecipeStep> steps) {
        if (steps != null) {
            this.steps = steps;
        }
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public final boolean isHighlighted() {
        return highlighted;
    }
    
    public final void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
    
    @Override
    public String toString() {
        return "Recipe {" +
                "\n\tName: " + getName() +
                "\n\tCategory: " + getCategory() +
                "\n\tCuisine: " + getCuisine() +
                "\n\tDescription: " + getDescription() +
                "\n\tUsername: " + getDisplay_name() +
                "\n\tUser Id: " + getUser_id() +
                "\n\tLikes: " + getLikes() +
                "\n\tReports: " + getReports() +
                "\n\tSteps: " + getStepsString() +
                "\n\tTags: " + getTags() +
                "\n\ticon: " + getIcon() +
                "\n\ttimestamp: " + getTimestamp() +
                "\n\tservings: " + getServings() +
                "\n\tduration: " + getDuration() + " minutes" +
                "\n\thighlighted: " + isHighlighted() +
                "\n}";
    }
    
    public String getTagsString() {
        if (tags.size() > 0) {
            String str = "";
            int i = 0;
            for (String tag : tags) {
                str += "#" + tag + " ";
            }
            return str;
        }
        return "\n\t\tNo Tags";
    }
    
    private String getStepsString() {
        if (steps.size() > 0) {
            String str = "";
            int i = 0;
            for (RecipeStep step : steps) {
                str += "\n\t\t " + i++ + ":";
                str += "\n\t\t\t Header: " + step.getHeader();
                str += "\n\t\t\t Content: " + step.getContent();
                str += "\n\t\t\t Image: " + step.getImage_ref();
            }
            return str;
        }
        return "No Instructions";
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public int getServings() {
        return servings;
    }
    
    public void setServings(int servings) {
        this.servings = servings;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public final String getCuisine() {
        return cuisine;
    }
    
    public final void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    
    public String getDisplay_name() {
        return display_name;
    }
    
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
