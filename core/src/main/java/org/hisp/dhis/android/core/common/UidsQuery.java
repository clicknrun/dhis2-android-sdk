package org.hisp.dhis.android.core.common;

import com.google.auto.value.AutoValue;

import java.util.Set;

import javax.annotation.Nullable;

@AutoValue
public abstract class UidsQuery extends BaseQuery {

    @Nullable
    public abstract Set<String> uids();

    @Nullable
    abstract Integer limit();

    @Override
    boolean isValid() {
        return limit() == null || uids().size() <= limit();
    }

    public static UidsQuery create(Set<String> uids, Integer limit) {
        return new AutoValue_UidsQuery(uids, 1, BaseQuery.DEFAULT_PAGE_SIZE, BaseQuery.DEFAULT_IS_PAGING,
                BaseQuery.DEFAULT_IS_TRANSLATION_ON, BaseQuery.DEFAULT_TRANSLATION_LOCALE, uids, limit);
    }
}
