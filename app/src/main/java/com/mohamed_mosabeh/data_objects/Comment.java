package com.mohamed_mosabeh.data_objects;

public class Comment {
    private String user_id;
    private String display_name;
    private String content;
    private String recipe_id;
    
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    public String getRecipe_id() {
        return recipe_id;
    }
    
    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }
    
    public String getDisplay_name() {
        return display_name;
    }
    
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
