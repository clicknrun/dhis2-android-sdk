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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.PeriodType;
import org.hisp.dhis.android.core.dataset.DataSetModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toBoolean;

@RunWith(AndroidJUnit4.class)
public class DataSetModelTests {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";

    private static final PeriodType PERIOD_TYPE = PeriodType.Monthly;
    private static final String CATEGORY_COMBO = "test_categoryCombo";
    private static final Integer MOBILE = 0;
    private static final Integer VERSION = 1;
    private static final Integer EXPIRY_DAYS = 1;
    private static final Integer TIMELY_DAYS = 1;
    private static final Integer NOTIFY_COMPLETING_USER = 0;
    private static final Integer OPEN_FUTURE_PERIODS = 1;
    private static final Integer FIELD_COMBINATION_REQUIRED = 0;
    private static final Integer VALID_COMPLETE_ONLY = 0;
    private static final Integer NO_VALUE_REQUIRES_COMMENT = 0;
    private static final Integer SKIP_OFFLINE = 0;
    private static final Integer DATA_ELEMENT_DECORATION = 0;
    private static final Integer RENDER_AS_TABS = 0;
    private static final Integer RENDER_HORIZONTALLY = 0;

    private final Date date;
    private final String dateString;

    public DataSetModelTests() {
        this.date = new Date();
        this.dateString = BaseIdentifiableObject.DATE_FORMAT.format(date);
    }
    @Test
    public void create_shouldConvertToDataSetModel() {
        MatrixCursor cursor = new MatrixCursor(DataSetModel.columnArray());
        cursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME,
                dateString, dateString,
                SHORT_NAME, DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION,
                
                PERIOD_TYPE, CATEGORY_COMBO, MOBILE, VERSION, EXPIRY_DAYS, TIMELY_DAYS, 
                NOTIFY_COMPLETING_USER, OPEN_FUTURE_PERIODS, FIELD_COMBINATION_REQUIRED, 
                VALID_COMPLETE_ONLY, NO_VALUE_REQUIRES_COMMENT, SKIP_OFFLINE, 
                DATA_ELEMENT_DECORATION, RENDER_AS_TABS, RENDER_HORIZONTALLY
        });
        cursor.moveToFirst();

        DataSetModel model = DataSetModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.uid()).isEqualTo(UID);
        assertThat(model.code()).isEqualTo(CODE);
        assertThat(model.name()).isEqualTo(NAME);
        assertThat(model.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(model.created()).isEqualTo(date);
        assertThat(model.lastUpdated()).isEqualTo(date);
        assertThat(model.shortName()).isEqualTo(SHORT_NAME);
        assertThat(model.displayShortName()).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(model.description()).isEqualTo(DESCRIPTION);
        assertThat(model.displayDescription()).isEqualTo(DISPLAY_DESCRIPTION);
        
        assertThat(model.periodType()).isEqualTo(PERIOD_TYPE);
        assertThat(model.categoryCombo()).isEqualTo(CATEGORY_COMBO);
        assertThat(model.mobile()).isEqualTo(toBoolean(MOBILE));
        assertThat(model.version()).isEqualTo(VERSION);
        assertThat(model.expiryDays()).isEqualTo(EXPIRY_DAYS);
        assertThat(model.timelyDays()).isEqualTo(TIMELY_DAYS);
        assertThat(model.notifyCompletingUser()).isEqualTo(toBoolean(NOTIFY_COMPLETING_USER));
        assertThat(model.openFuturePeriods()).isEqualTo(OPEN_FUTURE_PERIODS);
        assertThat(model.fieldCombinationRequired()).isEqualTo(toBoolean(FIELD_COMBINATION_REQUIRED));
        assertThat(model.validCompleteOnly()).isEqualTo(toBoolean(VALID_COMPLETE_ONLY));
        assertThat(model.noValueRequiresComment()).isEqualTo(toBoolean(NO_VALUE_REQUIRES_COMMENT));
        assertThat(model.skipOffline()).isEqualTo(toBoolean(SKIP_OFFLINE));
        assertThat(model.dataElementDecoration()).isEqualTo(toBoolean(DATA_ELEMENT_DECORATION));
        assertThat(model.renderAsTabs()).isEqualTo(toBoolean(RENDER_AS_TABS));
        assertThat(model.renderHorizontally()).isEqualTo(toBoolean(RENDER_HORIZONTALLY));
    }

    @Test
    public void create_shouldConvertToContentValues() {
        DataSetModel model = DataSetModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .periodType(PERIOD_TYPE)
                .categoryCombo(CATEGORY_COMBO)
                .mobile(toBoolean(MOBILE))
                .version(VERSION)
                .expiryDays(EXPIRY_DAYS)
                .timelyDays(TIMELY_DAYS)
                .notifyCompletingUser(toBoolean(NOTIFY_COMPLETING_USER))
                .openFuturePeriods(OPEN_FUTURE_PERIODS)
                .fieldCombinationRequired(toBoolean(FIELD_COMBINATION_REQUIRED))
                .validCompleteOnly(toBoolean(VALID_COMPLETE_ONLY))
                .noValueRequiresComment(toBoolean(NO_VALUE_REQUIRES_COMMENT))
                .skipOffline(toBoolean(SKIP_OFFLINE))
                .dataElementDecoration(toBoolean(DATA_ELEMENT_DECORATION))
                .renderAsTabs(toBoolean(RENDER_AS_TABS))
                .renderHorizontally(toBoolean(RENDER_HORIZONTALLY))
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(dateString);
        assertThat(contentValues.getAsString(Columns.SHORT_NAME)).isEqualTo(SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_SHORT_NAME)).isEqualTo(DISPLAY_SHORT_NAME);
        assertThat(contentValues.getAsString(Columns.DESCRIPTION)).isEqualTo(DESCRIPTION);
        assertThat(contentValues.getAsString(Columns.DISPLAY_DESCRIPTION)).isEqualTo(DISPLAY_DESCRIPTION);

        assertThat(contentValues.getAsString(Columns.PERIOD_TYPE)).isEqualTo(PERIOD_TYPE.name());
        assertThat(contentValues.getAsString(Columns.CATEGORY_COMBO)).isEqualTo(CATEGORY_COMBO);
        assertThat(contentValues.getAsBoolean(Columns.MOBILE)).isEqualTo(toBoolean(MOBILE));
        assertThat(contentValues.getAsInteger(Columns.VERSION)).isEqualTo(VERSION);
        assertThat(contentValues.getAsInteger(Columns.EXPIRY_DAYS)).isEqualTo(EXPIRY_DAYS);
        assertThat(contentValues.getAsInteger(Columns.TIMELY_DAYS)).isEqualTo(TIMELY_DAYS);
        assertThat(contentValues.getAsBoolean(Columns.NOTIFY_COMPLETING_USER)).isEqualTo(toBoolean(NOTIFY_COMPLETING_USER));
        assertThat(contentValues.getAsInteger(Columns.OPEN_FUTURE_PERIODS)).isEqualTo(OPEN_FUTURE_PERIODS);
        assertThat(contentValues.getAsBoolean(Columns.FIELD_COMBINATION_REQUIRED)).isEqualTo(toBoolean(FIELD_COMBINATION_REQUIRED));
        assertThat(contentValues.getAsBoolean(Columns.VALID_COMPLETE_ONLY)).isEqualTo(toBoolean(VALID_COMPLETE_ONLY));
        assertThat(contentValues.getAsBoolean(Columns.NO_VALUE_REQUIRES_COMMENT)).isEqualTo(toBoolean(NO_VALUE_REQUIRES_COMMENT));
        assertThat(contentValues.getAsBoolean(Columns.SKIP_OFFLINE)).isEqualTo(toBoolean(SKIP_OFFLINE));
        assertThat(contentValues.getAsBoolean(Columns.DATA_ELEMENT_DECORATION)).isEqualTo(toBoolean(DATA_ELEMENT_DECORATION));
        assertThat(contentValues.getAsBoolean(Columns.RENDER_AS_TABS)).isEqualTo(toBoolean(RENDER_AS_TABS));
        assertThat(contentValues.getAsBoolean(Columns.RENDER_HORIZONTALLY)).isEqualTo(toBoolean(RENDER_HORIZONTALLY));
    }

    @Test
    public void columns_shouldReturnModelColumns() {
        assertThat(DataSetModel.columnSet().contains(Columns.SKIP_OFFLINE)).isTrue();
    }
}
