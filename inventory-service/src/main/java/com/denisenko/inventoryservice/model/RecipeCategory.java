package com.denisenko.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("RECIPE")
@Getter
@Setter
public class RecipeCategory extends Category {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private RecipeCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<RecipeCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "recipeCategory")
    private List<Recipe> recipes = new ArrayList<>();

    @Override
    public boolean hasContent() {
        return !children.isEmpty() || !recipes.isEmpty();
    }
}
