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

package org.hisp.dhis.android.core.category;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.ModelFactory;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class CategoryOptionComboModel extends BaseIdentifiableObjectModel implements StatementBinder {

    public static final String TABLE = "CategoryOptionCombo";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String IGNORE_APPROVAL = "ignoreApproval";

        public static String[] all() {
            return Utils.appendInNewArray(BaseIdentifiableObjectModel.Columns.all(),IGNORE_APPROVAL);
        }
    }

    public static CategoryOptionComboModel create(Cursor cursor) {
        return AutoValue_CategoryOptionComboModel.createFromCursor(cursor);
    }

    public static ModelFactory<CategoryOptionComboModel, CategoryOptionCombo> Factory
            = new ModelFactory<CategoryOptionComboModel, CategoryOptionCombo>() {

        @Override
        public CategoryOptionComboModel fromCursor(Cursor cursor) {
            return create(cursor);
        }

        @Override
        public CategoryOptionComboModel fromPojo(CategoryOptionCombo categoryOptionCombo) {
            return CategoryOptionComboModel.builder()
                    .uid(categoryOptionCombo.uid())
                    .code(categoryOptionCombo.code())
                    .name(categoryOptionCombo.name())
                    .displayName(categoryOptionCombo.displayName())
                    .created(categoryOptionCombo.created())
                    .lastUpdated(categoryOptionCombo.lastUpdated())
                    .ignoreApproval(categoryOptionCombo.ignoreApproval())
                    .build();
        }
    };

    public static Builder builder() {
        return new $$AutoValue_CategoryOptionComboModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.IGNORE_APPROVAL)
    public abstract Boolean ignoreApproval();

    @NonNull
    public abstract ContentValues toContentValues();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 7, ignoreApproval());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder ignoreApproval(Boolean ignoreApproval);

        public abstract CategoryOptionComboModel build();
    }
}
