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

package org.hisp.dhis.android.core.dataset.utils;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
public class GenericStore<M extends BaseIdentifiableObjectModel> {
    private final DatabaseAdapter databaseAdapter;
    private final SQLStatementWrapper statements;
    private final SQLStatementBinder<M> binder;
    private final SQLStatementBuilder builder;

    public GenericStore(DatabaseAdapter databaseAdapter, SQLStatementWrapper statements,
                        SQLStatementBinder<M> binder, SQLStatementBuilder builder) {
        this.databaseAdapter = databaseAdapter;
        this.statements = statements;
        this.binder = binder;
        this.builder = builder;
    }

    public long insert(@NonNull M m) {
        isNull(m);
        binder.bindArguments(statements.insert, m);

        // execute and clear bindings
        Long insert = databaseAdapter.executeInsert(builder.tableName, statements.insert);
        statements.insert.clearBindings();
        return insert;
    }

    public int delete(@NonNull String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(statements.deleteById, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(builder.tableName, statements.deleteById);
        statements.deleteById.clearBindings();
        return delete;
    }

    public int update(@NonNull M m) {
        isNull(m);
        binder.bindArguments(statements.update, m);

        // bind the where argument
        sqLiteBind(statements.update, builder.columns.length + 1, m.uid());

        // execute and clear bindings
        int update = databaseAdapter.executeUpdateDelete(builder.tableName, statements.update);
        statements.update.clearBindings();
        return update;
    }

    public void updateOrInsert(@NonNull M m) {
        int updatedRow = update(m);
        if (updatedRow <= 0) {
            insert(m);
        }
    }
}


