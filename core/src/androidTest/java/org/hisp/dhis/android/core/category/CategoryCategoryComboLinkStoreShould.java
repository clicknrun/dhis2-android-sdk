package org.hisp.dhis.android.core.category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.hisp.dhis.android.core.common.CategoryComboMother.generateCategoryCombo;
import static org.junit.Assert.assertEquals;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.CategoryMother;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class CategoryCategoryComboLinkStoreShould extends AbsStoreTestCase {

    private static final String UPDATED_CATEGORY_UID = "UPDATED_CATEGORY_UID";
    private static final String DEFAULT_CATEGORY_COMBO_UID = "DEFAULT_CATEGORY_COMBO_UID";
    private static final String DEFAULT_CATEGORY_UID = "DEFAULT_CATEGORY_UID";
    private CategoryCategoryComboLinkStore store;
    private Category newCategory;
    private CategoryCombo newCategoryCombo;
    private CategoryCategoryComboLinkModel newCategoryCategoryComboLinkModel;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = new CategoryCategoryComboLinkStoreImpl(databaseAdapter());

    }

    @Test
    @MediumTest
    public void insert_a_category_combo_link() throws Exception {

        givenAllTheCategoryComboLinkDependencies();

        whenInsertNewCategoryComboLink();

        thenAssertTheNewCategoryComboLinkWasInserted();
    }

    private void givenAllTheCategoryComboLinkDependencies() {
        givenACategory();

        givenACategoryCombo();

        givenACategoryComboLinkModel();
    }

    @Test
    @MediumTest
    public void delete_a_category_combo_link() throws Exception {

        givenAllTheCategoryComboLinkDependencies();

        whenInsertNewCategoryComboLink();

        whenDeleteACategoryComboLink();

        assertCategoryCategoryComboLinkIsDeleted();
    }

    @Test
    @MediumTest
    public void update_a_category_combo_link() throws Exception {

        givenAllTheCategoryComboLinkDependencies();

        whenInsertNewCategoryComboLink();

        whenUpdateANewCategoryComboLink();

        thenAssertThatCategoryComboWasUpdated();

    }

    @Test
    @MediumTest
    public void delete_all_elements_on_category_combos_link_table() {

        givenAllTheCategoryComboLinkDependencies();

        whenInsertNewCategoryComboLink();

        thenAssertTheNewCategoryComboLinkWasInserted();

        whenDeleteAllCategoryComboLink();

        thenAssertThereAreNoItemsOnCategoryComboLinkTable();
    }

    @Test
    @MediumTest
    public void query_all_category_combos_link() {

        givenAllTheCategoryComboLinkDependencies();

        whenInsertNewCategoryComboLink();

        thenAssertTheNewCategoryComboLinkWasInserted();

        thenAssertQueryAllBringData();

    }

    private void thenAssertThereAreNoItemsOnCategoryComboLinkTable() {
        List<CategoryCategoryComboLink> items = store.queryAll();
        assertTrue(items.isEmpty());
    }

    private void thenAssertQueryAllBringData() {
        List<CategoryCategoryComboLink> items = store.queryAll();
        assertFalse(items.isEmpty());
    }

    private void whenDeleteAllCategoryComboLink() {
        store.delete();
    }

    private void thenAssertThatCategoryComboWasUpdated() {
        List<CategoryCategoryComboLink> list = store.queryAll();
        assertTrue(Objects.equals(list.get(0).category(), UPDATED_CATEGORY_UID));
    }

    private void whenUpdateANewCategoryComboLink() {
        Category category = CategoryMother.generateCategory(UPDATED_CATEGORY_UID);
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(category);

        CategoryCategoryComboLinkModel new1CategoryCategoryComboLinkModel =
                CategoryCategoryComboLinkModel.builder()
                        .category(UPDATED_CATEGORY_UID)
                        .categoryCombo(DEFAULT_CATEGORY_COMBO_UID)
                        .build();

        store.update(newCategoryCategoryComboLinkModel, new1CategoryCategoryComboLinkModel);
    }

    private void givenACategory() {
        newCategory = generateCategory();
    }

    private Category generateCategory() {
        return CategoryMother.generateCategory(DEFAULT_CATEGORY_UID);
    }

    private void givenACategoryCombo() {
        newCategoryCombo = generateCategoryCombo(DEFAULT_CATEGORY_COMBO_UID);
    }

    private void givenACategoryComboLinkModel() {
        newCategoryCategoryComboLinkModel = CategoryCategoryComboLinkModel.builder()
                .category(DEFAULT_CATEGORY_UID)
                .categoryCombo(DEFAULT_CATEGORY_COMBO_UID)
                .build();
    }

    private void whenInsertNewCategory() {
        CategoryStoreImpl categoryStore = new CategoryStoreImpl(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewCategoryCombo() {
        CategoryComboStoreImpl comboStore = new CategoryComboStoreImpl(databaseAdapter());
        comboStore.insert(newCategoryCombo);
    }

    private void whenInsertNewCategoryComboLink() {
        whenInsertNewCategory();
        whenInsertNewCategoryCombo();

        lastInsertedID = store.insert(newCategoryCategoryComboLinkModel);
    }

    private void whenDeleteACategoryComboLink() {
        store.delete(newCategoryCategoryComboLinkModel);
    }

    private void thenAssertTheNewCategoryComboLinkWasInserted() {
        assertEquals(lastInsertedID, 1);
    }

    private void assertCategoryCategoryComboLinkIsDeleted() {
        CategoryCategoryComboLink categoryCategoryComboLink = findCategoryCategoryComboLink(
                newCategoryCategoryComboLinkModel);
        assertTrue(categoryCategoryComboLink == null);
    }

    @Nullable
    private CategoryCategoryComboLink findCategoryCategoryComboLink(
            @NonNull CategoryCategoryComboLinkModel categoryCategoryComboLink) {
        CategoryCategoryComboLink foundLink = null;

        List<CategoryCategoryComboLink> list = store.queryAll();

        for (CategoryCategoryComboLink item : list) {
            if (Objects.equals(categoryCategoryComboLink.category(), item.category()) &&
                    Objects.equals(categoryCategoryComboLink.categoryCombo(),
                            item.categoryCombo())) {
                foundLink = item;
            }
        }
        return foundLink;
    }

}