package com.mohamed_mosabeh.data_objects;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HighlightedRecipe {
    private String id;
    private String category;
    
    private String curator_name;
    private String curator_comment;
    private double curator_rating;
    
    private String description;
    
    private int duration;
    
    private String icon;
    private String name;
    private int servings;
    private long timestamp;
    private String username;
    private ArrayList<String> tags = new ArrayList<String>();
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getServings() {
        return servings;
    }
    
    public void setServings(int servings) {
        this.servings = servings;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
