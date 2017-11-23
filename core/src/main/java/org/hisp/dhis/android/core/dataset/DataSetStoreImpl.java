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

package org.hisp.dhis.android.core.dataset;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class DataSetStoreImpl implements DataSetStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + DataSetModel.TABLE + " (" +

            DataSetModel.Columns.UID + ", " +
            DataSetModel.Columns.CODE + ", " +
            DataSetModel.Columns.NAME + ", " +
            DataSetModel.Columns.DISPLAY_NAME + ", " +
            DataSetModel.Columns.CREATED + ", " +
            DataSetModel.Columns.LAST_UPDATED + ", " +
            DataSetModel.Columns.SHORT_NAME + ", " +
            DataSetModel.Columns.DISPLAY_SHORT_NAME + ", " +
            DataSetModel.Columns.DESCRIPTION + ", " +
            DataSetModel.Columns.DISPLAY_DESCRIPTION + ", " +

            DataSetModel.Columns.PERIOD_TYPE + ", " +
            DataSetModel.Columns.CATEGORY_COMBO + ", " +
            DataSetModel.Columns.MOBILE + ", " +
            DataSetModel.Columns.VERSION + ", " +
            DataSetModel.Columns.EXPIRY_DAYS + ", " +
            DataSetModel.Columns.TIMELY_DAYS + ", " +
            DataSetModel.Columns.NOTIFY_COMPLETING_USER + ", " +
            DataSetModel.Columns.OPEN_FUTURE_PERIODS + ", " +
            DataSetModel.Columns.FIELD_COMBINATION_REQUIRED + ", " +
            DataSetModel.Columns.VALID_COMPLETE_ONLY + ", " +
            DataSetModel.Columns.NO_VALUE_REQUIRES_COMMENT + ", " +
            DataSetModel.Columns.SKIP_OFFLINE + ", " +
            DataSetModel.Columns.DATA_ELEMENT_DECORATION + ", " +
            DataSetModel.Columns.RENDER_AS_TABS + ", " +
            DataSetModel.Columns.RENDER_HORIZONTALLY + ") " +

            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String UPDATE_STATEMENT = "UPDATE " + DataSetModel.TABLE + " SET " +
            DataSetModel.Columns.UID + ", " +
            DataSetModel.Columns.CODE + ", " +
            DataSetModel.Columns.NAME + ", " +
            DataSetModel.Columns.DISPLAY_NAME + ", " +
            DataSetModel.Columns.CREATED + ", " +
            DataSetModel.Columns.LAST_UPDATED + ", " +
            DataSetModel.Columns.SHORT_NAME + ", " +
            DataSetModel.Columns.DISPLAY_SHORT_NAME + ", " +
            DataSetModel.Columns.DESCRIPTION + ", " +
            DataSetModel.Columns.DISPLAY_DESCRIPTION + ", " +

            DataSetModel.Columns.PERIOD_TYPE + ", " +
            DataSetModel.Columns.CATEGORY_COMBO + ", " +
            DataSetModel.Columns.MOBILE + ", " +
            DataSetModel.Columns.VERSION + ", " +
            DataSetModel.Columns.EXPIRY_DAYS + ", " +
            DataSetModel.Columns.TIMELY_DAYS + ", " +
            DataSetModel.Columns.NOTIFY_COMPLETING_USER + ", " +
            DataSetModel.Columns.OPEN_FUTURE_PERIODS + ", " +
            DataSetModel.Columns.FIELD_COMBINATION_REQUIRED + ", " +
            DataSetModel.Columns.VALID_COMPLETE_ONLY + ", " +
            DataSetModel.Columns.NO_VALUE_REQUIRES_COMMENT + ", " +
            DataSetModel.Columns.SKIP_OFFLINE + ", " +
            DataSetModel.Columns.DATA_ELEMENT_DECORATION + ", " +
            DataSetModel.Columns.RENDER_AS_TABS + ", " +
            DataSetModel.Columns.RENDER_HORIZONTALLY + ") " +

            " WHERE " + DataSetModel.Columns.UID + " =?;";

    private static final String DELETE_STATEMENT = "DELETE FROM " + DataSetModel.TABLE +
            " WHERE " + DataSetModel.Columns.UID + " =?;";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement updateStatement;
    private final SQLiteStatement deleteStatement;

    private final DatabaseAdapter databaseAdapter;

    public DataSetStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.updateStatement = databaseAdapter.compileStatement(UPDATE_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@NonNull DataSetModel dataSetModel) {
        isNull(dataSetModel);
        bindArguments(insertStatement, dataSetModel);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(DataSetModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return insert;
    }

    @Override
    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(DataSetModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public int update(@NonNull DataSetModel dataSetModel, @NonNull String whereDataSetUid) {
        isNull(dataSetModel);
        isNull(whereDataSetUid);
        bindArguments(updateStatement, dataSetModel);

        // bind the where argument
        sqLiteBind(updateStatement, 26, whereDataSetUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(DataSetModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
                               @NonNull DataSetModel dsm) {

        sqLiteBind(sqLiteStatement, 1, dsm.uid());
        sqLiteBind(sqLiteStatement, 2, dsm.code());
        sqLiteBind(sqLiteStatement, 3, dsm.name());
        sqLiteBind(sqLiteStatement, 4, dsm.displayName());
        sqLiteBind(sqLiteStatement, 5, dsm.created());
        sqLiteBind(sqLiteStatement, 6, dsm.lastUpdated());
        sqLiteBind(sqLiteStatement, 7, dsm.shortName());
        sqLiteBind(sqLiteStatement, 8, dsm.displayShortName());
        sqLiteBind(sqLiteStatement, 9, dsm.description());
        sqLiteBind(sqLiteStatement, 10, dsm.displayDescription());

        sqLiteBind(sqLiteStatement, 11, dsm.periodType());
        sqLiteBind(sqLiteStatement, 12, dsm.categoryCombo());
        sqLiteBind(sqLiteStatement, 13, dsm.mobile());
        sqLiteBind(sqLiteStatement, 14, dsm.version());
        sqLiteBind(sqLiteStatement, 15, dsm.expiryDays());
        sqLiteBind(sqLiteStatement, 16, dsm.timelyDays());
        sqLiteBind(sqLiteStatement, 17, dsm.notifyCompletingUser());
        sqLiteBind(sqLiteStatement, 18, dsm.openFuturePeriods());
        sqLiteBind(sqLiteStatement, 19, dsm.fieldCombinationRequired());
        sqLiteBind(sqLiteStatement, 20, dsm.validCompleteOnly());
        sqLiteBind(sqLiteStatement, 21, dsm.noValueRequiresComment());
        sqLiteBind(sqLiteStatement, 22, dsm.skipOffline());
        sqLiteBind(sqLiteStatement, 23, dsm.dataElementDecoration());
        sqLiteBind(sqLiteStatement, 24, dsm.renderAsTabs());
        sqLiteBind(sqLiteStatement, 25, dsm.renderHorizontally());
    }
}
