package com.denisenko.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INGREDIENT")
@Getter
@Setter
public class IngredientCategory extends Category {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private IngredientCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<IngredientCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "ingredientCategory")
    private List<Ingredient> ingredients = new ArrayList<>();

    @Override
    public boolean hasContent() {
        return !children.isEmpty() || !ingredients.isEmpty();
    }
}
