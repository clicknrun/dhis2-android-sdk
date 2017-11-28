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
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.StatementBinder;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class DataSetDataElementLinkModel extends BaseModel implements StatementBinder {
    public static final String TABLE = "DataSetDataElementLink";

    public static class Columns extends BaseModel.Columns {
        public static final String DATA_SET = "dataSet";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String CATEGORY_COMBO = "categoryCombo";

        public static String[] all() {
            return Utils.appendInNewArray(BaseModel.Columns.all(),
                    DATA_SET, DATA_ELEMENT, CATEGORY_COMBO);
        }
    }

    public static DataSetDataElementLinkModel create(Cursor cursor) {
        return AutoValue_DataSetDataElementLinkModel.createFromCursor(cursor);
    }

    public static DataSetDataElementLinkModel create(
            String dataSetUid, String dataElementUid, String categoryComboUid) {
        return DataSetDataElementLinkModel.builder()
                .dataSet(dataSetUid)
                .dataElement(dataElementUid)
                .categoryCombo(categoryComboUid)
                .build();
    }

    public static Builder builder() {
        return new $$AutoValue_DataSetDataElementLinkModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.DATA_SET)
    public abstract String dataSet();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.CATEGORY_COMBO)
    public abstract String categoryCombo();

    @NonNull
    public abstract ContentValues toContentValues();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, dataSet());
        sqLiteBind(sqLiteStatement, 2, dataElement());
        sqLiteBind(sqLiteStatement, 3, categoryCombo());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder dataSet(String dataSet);

        public abstract Builder dataElement(String dataElement);

        public abstract Builder categoryCombo(String categoryCombo);

        public abstract DataSetDataElementLinkModel build();
    }
}
