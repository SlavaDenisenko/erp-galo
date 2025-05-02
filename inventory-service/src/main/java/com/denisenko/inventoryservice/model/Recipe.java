package com.denisenko.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_recipe")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private RecipeCategory recipeCategory;

    @Column(name = "preparation_time")
    private Integer preparationTimeInMinutes;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(length = 2000)
    private String instructions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe", orphanRemoval = true)
    private List<RecipeComposition> recipeCompositions = new ArrayList<>();

    //- TODO make extra table with printer locations
    @Column(nullable = false)
    private String location;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addRecipeComposition(RecipeComposition recipeComposition) {
        recipeCompositions.add(recipeComposition);
        recipeComposition.setRecipe(this);
    }

    public void setRecipeCompositions(List<RecipeComposition> recipeCompositions) {
        this.recipeCompositions.clear();
        if (recipeCompositions != null)
            recipeCompositions.forEach(this::addRecipeComposition);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
