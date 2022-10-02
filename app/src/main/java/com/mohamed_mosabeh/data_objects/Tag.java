package com.mohamed_mosabeh.data_objects;

public class Tag extends Category {
    
    private int recipes_count;
    private boolean trending;
    
    
    public boolean isTrending() {
        return trending;
    }
    
    public void setTrending(boolean trending) {
        this.trending = trending;
    }
    
    public int getRecipes_count() {
        return recipes_count;
    }
    
    public void setRecipes_count(int recipes_count) {
        this.recipes_count = recipes_count;
    }
}
