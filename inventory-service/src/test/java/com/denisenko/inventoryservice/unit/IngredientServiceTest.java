package com.denisenko.inventoryservice.unit;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.mapper.IngredientMapper;
import com.denisenko.inventoryservice.model.UnitOfMeasure;
import com.denisenko.inventoryservice.model.Ingredient;
import com.denisenko.inventoryservice.model.IngredientCategory;
import com.denisenko.inventoryservice.repository.IngredientRepository;
import com.denisenko.inventoryservice.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createIngredientTest() {
        Ingredient ingredient = buildIngredient();
        IngredientDto ingredientDto = buildIngredientDto();
        String idempotencyKey = UUID.randomUUID().toString();

        when(ingredientMapper.toEntity(ingredientDto)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toDTO(ingredient)).thenReturn(ingredientDto);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("idempotency:ingredient:" + idempotencyKey)).thenReturn(null);

        IngredientDto result = ingredientService.createIngredient(ingredientDto, idempotencyKey);
        assertNotNull(result);
        assertEquals("Beef", result.getName());
        assertEquals(5, result.getCategoryId());

        verify(ingredientMapper, times(1)).toEntity(ingredientDto);
        verify(ingredientRepository, times(1)).save(ingredient);
        verify(ingredientMapper, times(1)).toDTO(ingredient);
        verify(valueOperations).set("idempotency:ingredient:" + idempotencyKey, String.valueOf(ingredient.getId()), Duration.ofMinutes(10));
    }

    @Test
    void getAllIngredientsTest() {
        Ingredient ingredient1 = buildIngredient();
        IngredientCategory category = new IngredientCategory();
        category.setId(6);
        Ingredient ingredient2 = Ingredient.builder()
                .name("Sugar")
                .ingredientCategory(category)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .reorderLevel(500.0)
                .cost(new BigDecimal(20))
                .build();

        List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

        IngredientDto ingredientDto1 = buildIngredientDto();
        IngredientDto ingredientDto2 = IngredientDto.builder()
                .name("Sugar")
                .categoryId(6)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .reorderLevel(500.0)
                .cost(new BigDecimal(20))
                .build();

        List<IngredientDto> ingredientsDto = List.of(ingredientDto1, ingredientDto2);

        when(ingredientRepository.findAll()).thenReturn(ingredients);
        when(ingredientMapper.toDTO(ingredients)).thenReturn(ingredientsDto);

        List<IngredientDto> result = ingredientService.getAllIngredients();
        assertEquals(2, result.size());
        assertEquals("Beef", result.get(0).getName());
        assertEquals("Sugar", result.get(1).getName());

        verify(ingredientRepository, times(1)).findAll();
        verify(ingredientMapper, times(1)).toDTO(ingredients);
    }

    @Test
    void getIngredientTest() {
        Ingredient ingredient = buildIngredient();
        IngredientDto ingredientDto = buildIngredientDto();

        when(ingredientRepository.findById(1)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.toDTO(ingredient)).thenReturn(ingredientDto);

        IngredientDto result = ingredientService.getIngredient(1);
        assertNotNull(result);
        assertEquals("Beef", result.getName());
        assertEquals(5, result.getCategoryId());

        verify(ingredientRepository, times(1)).findById(1);
        verify(ingredientMapper, times(1)).toDTO(ingredient);
    }

    @Test
    void updateIngredientTest() {
        Ingredient existingIngredient = buildIngredient();
        IngredientCategory category = new IngredientCategory();
        category.setId(5);
        Ingredient updatedIngredient = Ingredient.builder()
                .name("Premium Beef")
                .ingredientCategory(category)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .reorderLevel(2000.0)
                .cost(new BigDecimal(500))
                .build();

        IngredientDto updatedIngredientDto = IngredientDto.builder()
                .name("Premium Beef")
                .categoryId(5)
                .quantity(0.0)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .reorderLevel(2000.0)
                .cost(new BigDecimal(500))
                .build();

        when(ingredientRepository.findById(1)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(updatedIngredient);
        when(ingredientMapper.toDTO(updatedIngredient)).thenReturn(updatedIngredientDto);

        IngredientDto result = ingredientService.updateIngredient(1, updatedIngredientDto);
        assertNotNull(result);
        assertEquals(updatedIngredientDto.getName(), result.getName());
        assertEquals(updatedIngredientDto.getUnitOfMeasure(), result.getUnitOfMeasure());
        assertEquals(updatedIngredientDto.getReorderLevel(), result.getReorderLevel());
        assertEquals(updatedIngredientDto.getCost(), result.getCost());

        verify(ingredientRepository, times(1)).findById(1);
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
        verify(ingredientMapper, times(1)).toDTO(updatedIngredient);
    }

    private Ingredient buildIngredient() {
        IngredientCategory category = new IngredientCategory();
        category.setId(5);
        return Ingredient.builder()
                .id(1)
                .name("Beef")
                .ingredientCategory(category)
                .unitOfMeasure(UnitOfMeasure.KILOGRAM)
                .reorderLevel(3.0)
                .cost(new BigDecimal(100))
                .build();
    }

    private IngredientDto buildIngredientDto() {
        return IngredientDto.builder()
                .name("Beef")
                .categoryId(5)
                .quantity(0.0)
                .unitOfMeasure(UnitOfMeasure.KILOGRAM)
                .reorderLevel(3.0)
                .cost(new BigDecimal(100))
                .build();
    }
}
