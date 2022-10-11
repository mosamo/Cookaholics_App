package com.mohamed_mosabeh.data_objects;

import android.graphics.Bitmap;

public class Category {
    private String image;
    private int hits;
    private String name;
    private Bitmap picture;
    
    public String getImage() {
        return image;
    }
    
    final public void setImage(String image) {
        this.image = image;
    }
    
    final public int getHits() {
        return hits;
    }
    
    public void setHits(int hits) {
        this.hits = hits;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Bitmap getPicture() {
        return picture;
    }
    
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
