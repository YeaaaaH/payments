package payments.duo.service.impl;

import org.springframework.stereotype.Service;
import payments.duo.exception.CategoryNotFoundException;
import payments.duo.model.Category;
import payments.duo.repository.CategoryRepository;
import payments.duo.service.CategoryService;

import java.util.List;

import static payments.duo.utils.Constants.CATEGORY_NOT_FOUND_MESSAGE;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, id)));
    }
}
