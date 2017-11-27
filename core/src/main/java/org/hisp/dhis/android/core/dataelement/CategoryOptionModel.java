/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;
import java.util.Set;

@AutoValue
public abstract class CategoryOptionModel extends BaseIdentifiableObjectModel implements StatementBinder {

    public static final String TABLE = "CategoryOption";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String SHORT_NAME = "shortName";
        public static final String DISPLAY_SHORT_NAME = "displayShortName";

        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
    }

    public static CategoryOptionModel create(Cursor cursor) {
        return AutoValue_CategoryOptionModel.createFromCursor(cursor);
    }

    public static CategoryOptionModel create(CategoryOption categoryOption) {
        return CategoryOptionModel.builder()
                .uid(categoryOption.uid())
                .code(categoryOption.code())
                .name(categoryOption.name())
                .displayName(categoryOption.displayName())
                .created(categoryOption.created())
                .lastUpdated(categoryOption.lastUpdated())

                .shortName(categoryOption.shortName())
                .displayShortName(categoryOption.displayShortName())
                .startDate(categoryOption.startDate())
                .endDate(categoryOption.endDate())
                .build();
    }

    public static Set<String> columnSet() {
        return CategoryOptionModel.builder().build().toContentValues().keySet();
    }

    public static String[] columnArray() {
        Set<String> keySet = columnSet();
        return keySet.toArray(new String[keySet.size()]);
    }

    public static Builder builder() {
        return new $$AutoValue_CategoryOptionModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.SHORT_NAME)
    public abstract String shortName();

    @Nullable
    @ColumnName(Columns.DISPLAY_SHORT_NAME)
    public abstract String displayShortName();

    @Nullable
    @ColumnName(Columns.START_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date startDate();

    @Nullable
    @ColumnName(Columns.END_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date endDate();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder shortName(String shortName);

        public abstract Builder displayShortName(String displayShortName);

        public abstract Builder startDate(Date startDate);

        public abstract Builder endDate(Date endDate);

        public abstract CategoryOptionModel build();
    }
}
