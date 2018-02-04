package org.hisp.dhis.android.core.category;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.DeletableStore;

import java.util.List;

public interface CategoryOptionStore extends DeletableStore {

    long insert(@NonNull CategoryOption categoryOption);

    int delete(@NonNull String uid);

    boolean update(@NonNull CategoryOption oldCategoryOption,
            @NonNull CategoryOption newCategoryOption);

    List<CategoryOption> queryAll();
}
