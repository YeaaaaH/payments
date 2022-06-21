package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.Category;
import payments.duo.model.response.FindAllCategoriesResponse;
import payments.duo.service.CategoryService;

import java.util.List;

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
    public FindAllCategoriesResponse findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        return new FindAllCategoriesResponse(categories);
    }
}
