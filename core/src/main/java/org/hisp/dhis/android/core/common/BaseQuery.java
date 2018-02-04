package org.hisp.dhis.android.core.common;

import java.util.Locale;

public abstract class BaseQuery {
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final String DEFAULT_TRANSLATION_LOCALE = Locale.ENGLISH.toString();
    public static final boolean DEFAULT_IS_TRANSLATION_ON = false;

    public abstract int page();

    public abstract int pageSize();

    public abstract boolean paging();

    public abstract boolean isTranslationOn();

    public abstract String translationLocale();

    protected static abstract class Builder<T extends BaseQuery.Builder> {
        public abstract T page(int page);

        public abstract T pageSize(int pageSize);

        public abstract T paging(boolean paging);

        public abstract T isTranslationOn(boolean isTranslationOn);

        public abstract T translationLocale(String translationLocale);
    }
}
