package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.Category;
import payments.duo.service.CategoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/category")
@Api(description="Operations related to categories, allowed only GET methods")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("{id}")
    public Category findCategoryById(@PathVariable long id) {
        return categoryService.findCategoryById(id);
    }

    @GetMapping
    public Map<String, List<Category>> findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        //TODO add custom response
        return Map.of("categories", categories);
    }
}
