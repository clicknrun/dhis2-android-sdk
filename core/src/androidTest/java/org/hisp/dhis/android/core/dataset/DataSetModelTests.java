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

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.PeriodType;
import org.hisp.dhis.android.core.dataset.DataSetModel.Columns;
import org.hisp.dhis.android.core.utils.AsObjectArrrayTestUtils;
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
public class DataSetModelTests {
    private final DataSetModel dm;

    public DataSetModelTests() {
        DataSetModel.Builder dataModelBuilder = DataSetModel.builder();
        FillPropertiesTestUtils.fillNameableModelProperties(dataModelBuilder);
        this.dm = dataModelBuilder
                .periodType(PeriodType.Monthly)
                .categoryCombo("test_categoryCombo")
                .mobile(false)
                .version(1)
                .expiryDays(10)
                .timelyDays(100)
                .notifyCompletingUser(false)
                .openFuturePeriods(0)
                .fieldCombinationRequired(false)
                .validCompleteOnly(false)
                .noValueRequiresComment(false)
                .skipOffline(false)
                .dataElementDecoration(false)
                .renderAsTabs(false)
                .renderHorizontally(false)
                .build();
    }

    @Test
    public void create_shouldConvertToDataSetModel() {
        MatrixCursor cursor = new MatrixCursor(DataSetModel.Columns.all());
        cursor.addRow(Utils.appendInNewArray(AsObjectArrrayTestUtils.getNameableModelAsObjectArray(dm),
                dm.periodType(), dm.categoryCombo(), toInteger(dm.mobile()), dm.version(),
                dm.expiryDays(), dm.timelyDays(), toInteger(dm.notifyCompletingUser()),
                dm.openFuturePeriods(), toInteger(dm.fieldCombinationRequired()),
                toInteger(dm.validCompleteOnly()), toInteger(dm.noValueRequiresComment()),
                toInteger(dm.skipOffline()),toInteger(dm.dataElementDecoration()),
                toInteger(dm.renderAsTabs()), toInteger(dm.renderHorizontally())
        ));
        cursor.moveToFirst();

        DataSetModel modelFromDB = DataSetModel.create(cursor);
        cursor.close();

        assertThat(modelFromDB).isEqualTo(dm);
    }

    @Test
    public void create_shouldConvertToContentValues() {
        ContentValues contentValues = dm.toContentValues();

        ContentValuesTestUtils.testNameableModelContentValues(contentValues, dm);

        assertThat(contentValues.getAsString(Columns.PERIOD_TYPE)).isEqualTo(dm.periodType().name());
        assertThat(contentValues.getAsString(Columns.CATEGORY_COMBO)).isEqualTo(dm.categoryCombo());
        assertThat(contentValues.getAsBoolean(Columns.MOBILE)).isEqualTo(dm.mobile());
        assertThat(contentValues.getAsInteger(Columns.VERSION)).isEqualTo(dm.version());
        assertThat(contentValues.getAsInteger(Columns.EXPIRY_DAYS)).isEqualTo(dm.expiryDays());
        assertThat(contentValues.getAsInteger(Columns.TIMELY_DAYS)).isEqualTo(dm.timelyDays());
        assertThat(contentValues.getAsBoolean(Columns.NOTIFY_COMPLETING_USER)).isEqualTo(dm.notifyCompletingUser());
        assertThat(contentValues.getAsInteger(Columns.OPEN_FUTURE_PERIODS)).isEqualTo(dm.openFuturePeriods());
        assertThat(contentValues.getAsBoolean(Columns.FIELD_COMBINATION_REQUIRED)).isEqualTo(dm.fieldCombinationRequired());
        assertThat(contentValues.getAsBoolean(Columns.VALID_COMPLETE_ONLY)).isEqualTo(dm.validCompleteOnly());
        assertThat(contentValues.getAsBoolean(Columns.NO_VALUE_REQUIRES_COMMENT)).isEqualTo(dm.noValueRequiresComment());
        assertThat(contentValues.getAsBoolean(Columns.SKIP_OFFLINE)).isEqualTo(dm.skipOffline());
        assertThat(contentValues.getAsBoolean(Columns.DATA_ELEMENT_DECORATION)).isEqualTo(dm.dataElementDecoration());
        assertThat(contentValues.getAsBoolean(Columns.RENDER_AS_TABS)).isEqualTo(dm.renderAsTabs());
        assertThat(contentValues.getAsBoolean(Columns.RENDER_HORIZONTALLY)).isEqualTo(dm.renderHorizontally());
    }

    @Test
    public void columns_shouldReturnModelColumns() {
        String[] columnArray = DataSetModel.Columns.all();
        List<String> columnsList = Arrays.asList(columnArray);
        assertThat(columnArray.length).isEqualTo(26);
        assertThat(columnsList.contains(Columns.ID)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.UID)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NAME)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SHORT_NAME)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.SKIP_OFFLINE)).isEqualTo(true);
    }
}