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

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.StatementBinder;

import java.util.Set;

@AutoValue
public abstract class CategoryModel extends BaseIdentifiableObjectModel implements StatementBinder {

    public static final String TABLE = "Category";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {

        public static String[] all() {
            return BaseIdentifiableObjectModel.Columns.all();
        }
    }

    public static CategoryModel create(Cursor cursor) {
        return AutoValue_CategoryModel.createFromCursor(cursor);
    }

    public static CategoryModel create(Category category) {
        return CategoryModel.builder()
                .uid(category.uid())
                .code(category.code())
                .name(category.name())
                .displayName(category.displayName())
                .created(category.created())
                .lastUpdated(category.lastUpdated())
                .build();
    }

    public static Set<String> columnSet() {
        return CategoryModel.builder().build().toContentValues().keySet();
    }

    public static String[] columnArray() {
        Set<String> keySet = columnSet();
        return keySet.toArray(new String[keySet.size()]);
    }

    public static Builder builder() {
        return new $$AutoValue_CategoryModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract CategoryModel build();
    }
}
