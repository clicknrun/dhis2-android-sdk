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
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Date;

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
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
                       @Nullable String shortName, @Nullable String displayShortName,
                       @Nullable String description, @Nullable String displayDescription,

                       @NonNull String periodType, @NonNull String categoryCombo,
                       @Nullable Boolean mobile, @Nullable Integer version, @Nullable Integer expiryDays,
                       @Nullable Integer timelyDays, @Nullable Boolean notifyCompletingUser,
                       @Nullable Integer openFuturePeriods, @Nullable Boolean fieldCombinationRequired,
                       @Nullable Boolean validCompleteOnly, @Nullable Boolean noValueRequiresComment,
                       @Nullable Boolean skipOffline, @Nullable Boolean dataElementDecoration,
                       @Nullable Boolean renderAsTabs, @Nullable Boolean renderHorizontally) {
        isNull(uid);
        bindArguments(insertStatement, uid, code, name, displayName, created, lastUpdated,
                shortName, displayShortName, description, displayDescription,
                periodType, categoryCombo, mobile, version, expiryDays, timelyDays,
                notifyCompletingUser, openFuturePeriods, fieldCombinationRequired,
                validCompleteOnly, noValueRequiresComment, skipOffline,
                dataElementDecoration, renderAsTabs, renderHorizontally);

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
    public int update(@NonNull String uid, @Nullable String code, @NonNull String name,
                      @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
                      @Nullable String shortName, @Nullable String displayShortName,
                      @Nullable String description, @Nullable String displayDescription,

                      @NonNull String periodType, @NonNull String categoryCombo,
                      @Nullable Boolean mobile, @Nullable Integer version, @Nullable Integer expiryDays,
                      @Nullable Integer timelyDays, @Nullable Boolean notifyCompletingUser,
                      @Nullable Integer openFuturePeriods, @Nullable Boolean fieldCombinationRequired,
                      @Nullable Boolean validCompleteOnly, @Nullable Boolean noValueRequiresComment,
                      @Nullable Boolean skipOffline, @Nullable Boolean dataElementDecoration,
                      @Nullable Boolean renderAsTabs, @Nullable Boolean renderHorizontally,
                      @NonNull String whereDataSetUid) {
        isNull(uid);
        isNull(whereDataSetUid);
        bindArguments(updateStatement, uid, code, name, displayName, created, lastUpdated,
                shortName, displayShortName, description, displayDescription,
                periodType, categoryCombo, mobile, version, expiryDays, timelyDays,
                notifyCompletingUser, openFuturePeriods, fieldCombinationRequired,
                validCompleteOnly, noValueRequiresComment, skipOffline,
                dataElementDecoration, renderAsTabs, renderHorizontally);

        // bind the where argument
        sqLiteBind(updateStatement, 26, whereDataSetUid);

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(DataSetModel.TABLE, updateStatement);
        updateStatement.clearBindings();
        return update;
    }

    private void bindArguments(@NonNull SQLiteStatement sqLiteStatement,
                               @NonNull String uid, @Nullable String code, @NonNull String name,
                               @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
                               @Nullable String shortName, @Nullable String displayShortName,
                               @Nullable String description, @Nullable String displayDescription,

                               @NonNull String periodType, @NonNull String categoryCombo,
                               @Nullable Boolean mobile, @Nullable Integer version, @Nullable Integer expiryDays,
                               @Nullable Integer timelyDays, @Nullable Boolean notifyCompletingUser,
                               @Nullable Integer openFuturePeriods, @Nullable Boolean fieldCombinationRequired,
                               @Nullable Boolean validCompleteOnly, @Nullable Boolean noValueRequiresComment,
                               @Nullable Boolean skipOffline, @Nullable Boolean dataElementDecoration,
                               @Nullable Boolean renderAsTabs, @Nullable Boolean renderHorizontally) {

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, shortName);
        sqLiteBind(sqLiteStatement, 8, displayShortName);
        sqLiteBind(sqLiteStatement, 9, description);
        sqLiteBind(sqLiteStatement, 10, displayDescription);

        sqLiteBind(sqLiteStatement, 11, periodType);
        sqLiteBind(sqLiteStatement, 12, categoryCombo);
        sqLiteBind(sqLiteStatement, 13, mobile);
        sqLiteBind(sqLiteStatement, 14, version);
        sqLiteBind(sqLiteStatement, 15, expiryDays);
        sqLiteBind(sqLiteStatement, 16, timelyDays);
        sqLiteBind(sqLiteStatement, 17, notifyCompletingUser);
        sqLiteBind(sqLiteStatement, 18, openFuturePeriods);
        sqLiteBind(sqLiteStatement, 19, fieldCombinationRequired);
        sqLiteBind(sqLiteStatement, 20, validCompleteOnly);
        sqLiteBind(sqLiteStatement, 21, noValueRequiresComment);
        sqLiteBind(sqLiteStatement, 22, skipOffline);
        sqLiteBind(sqLiteStatement, 23, dataElementDecoration);
        sqLiteBind(sqLiteStatement, 24, renderAsTabs);
        sqLiteBind(sqLiteStatement, 25, renderHorizontally);
    }
}
