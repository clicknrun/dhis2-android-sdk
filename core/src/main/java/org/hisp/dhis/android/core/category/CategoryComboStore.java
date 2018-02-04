package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryComboStore extends DeletableStore {
    long insert(@NonNull CategoryCombo categoryCombo);

    boolean update(@NonNull CategoryCombo oldCategoryCombo,
            @NonNull CategoryCombo newCategoryCombo);

    int delete(@NonNull String uid);

    List<CategoryCombo> queryAll();
}
