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

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

import java.util.Arrays;
import java.util.Collection;

public class SQLStatementBuilder {
    public final String tableName;
    public final String[] columns;

    public SQLStatementBuilder(String tableName, Collection<String> columns) {
        this.tableName = tableName;
        this.columns = columns.toArray(new String[columns.size()]);
    }

    private String commaSeparatedColumns() {
        return commaSeparatedArrayValues(columns);
    }

    private static String commaSeparatedArrayValues(String[] values) {
        String withBrackets = Arrays.toString(values);
        return withBrackets.substring(1, withBrackets.length() -1);
    }

    private String commaSeparatedInterrogationMarks() {
        String[] array = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            array[i] = "?";
        }
        return commaSeparatedArrayValues(array);
    }

    private String commaSeparatedColumnEqualInterrogationMark() {
        String[] array = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            array[i] = columns[i] = "=?";
        }
        return commaSeparatedArrayValues(array);
    }


    public String insert() {
        return "INSERT INTO " + tableName + " (" + commaSeparatedColumns() + ") " +
                "VALUES ( " + commaSeparatedInterrogationMarks()+ " )";
    }

    public String deleteById() {
        return "DELETE FROM " + tableName +
                " WHERE " + BaseIdentifiableObjectModel.Columns.UID + " =?;";
    }

    public String update() {
        return "UPDATE " + tableName + " SET " + commaSeparatedColumnEqualInterrogationMark() + ";";
    }
}
