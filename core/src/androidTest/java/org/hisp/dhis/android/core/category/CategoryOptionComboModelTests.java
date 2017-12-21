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
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.ColumnsAsserts;
import org.hisp.dhis.android.core.utils.ContentValuesTestUtils;
import org.hisp.dhis.android.core.utils.FillPropertiesTestUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboModelTests {
    private final CategoryOptionComboModel cocm;

    public CategoryOptionComboModelTests() {
        CategoryOptionComboModel.Builder categoryOptionComboModelBuilder = CategoryOptionComboModel.builder();
        FillPropertiesTestUtils.fillIdentifiableModelProperties(categoryOptionComboModelBuilder);
        this.cocm = categoryOptionComboModelBuilder
                .ignoreApproval(false)
                .build();
    }

    @Test
    public void create_shouldConvertToCategoryOptionComboModel() {
        MatrixCursor cursor = new MatrixCursor(CategoryOptionComboModel.Columns.all());
        cursor.addRow(Utils.appendInNewArray(ColumnsArrayUtils.getIdentifiableModelAsObjectArray(cocm),
                toInteger(cocm.ignoreApproval())));
        cursor.moveToFirst();

        CategoryOptionComboModel modelFromDB = CategoryOptionComboModel.create(cursor);
        cursor.close();

        assertThat(modelFromDB).isEqualTo(cocm);
    }

    @Test
    public void create_shouldConvertToContentValues() {
        ContentValues contentValues = cocm.toContentValues();

        ContentValuesTestUtils.testIdentifiableModelContentValues(contentValues, cocm);

        assertThat(contentValues.getAsBoolean(
                CategoryOptionComboModel.Columns.IGNORE_APPROVAL)).isEqualTo(cocm.ignoreApproval());
    }

    @Test
    public void columns_shouldReturnModelColumns() {
        String[] columnArray = CategoryOptionComboModel.Columns.all();
        List<String> columnsList = Arrays.asList(columnArray);
        assertThat(columnArray.length).isEqualTo(7);

        ColumnsAsserts.testIdentifiableModelColumns(columnsList);

        assertThat(columnsList.contains(
                CategoryOptionComboModel.Columns.IGNORE_APPROVAL)).isEqualTo(true);
    }
}