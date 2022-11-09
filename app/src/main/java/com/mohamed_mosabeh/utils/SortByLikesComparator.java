package com.mohamed_mosabeh.utils;

import com.mohamed_mosabeh.data_objects.Recipe;

import java.util.Comparator;

public class SortByLikesComparator implements Comparator<Recipe> {
    // Method
    // Sorting in ascending order
    public int compare(Recipe a, Recipe b) {
        return a.getLikes() - b.getLikes();
    }
}
