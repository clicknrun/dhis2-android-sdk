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

package org.hisp.dhis.android.core;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.dataset.DataSetModel;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

/**
 * A collection of convenience functions/abstractions to be used by the tests.
 */
public class AndroidTestUtils {

    /* A helper method to convert an integer to Boolean, where 1 is true and 0 is false*/
    public static Boolean toBoolean(Integer i) {
        if (i == 0) {
            return false;
        } else {
            return true;
        }
    }

    /* A helper method to convert a Boolean to an Integer, where true is 1 and false is 0 */
    public static Integer toInteger(Boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void testModelContentValues(ContentValues contentValues, Model m) {
        assertThat(contentValues.getAsLong(DataSetModel.Columns.ID)).isEqualTo(m.id());
    }

    public static void testIdentifiableModelContentValues(ContentValues contentValues, BaseIdentifiableObjectModel m) {
        testModelContentValues(contentValues, m);
        assertThat(contentValues.getAsString(DataSetModel.Columns.UID)).isEqualTo(m.uid());
        assertThat(contentValues.getAsString(DataSetModel.Columns.CODE)).isEqualTo(m.code());
        assertThat(contentValues.getAsString(DataSetModel.Columns.NAME)).isEqualTo(m.name());
        assertThat(contentValues.getAsString(DataSetModel.Columns.DISPLAY_NAME)).isEqualTo(m.displayName());
        assertThat(contentValues.getAsString(DataSetModel.Columns.CREATED)).isEqualTo(m.createdStr());
        assertThat(contentValues.getAsString(DataSetModel.Columns.LAST_UPDATED)).isEqualTo(m.lastUpdatedStr());
    }

    public static void testNameableModelContentValues(ContentValues contentValues, BaseNameableObjectModel m) {
        testIdentifiableModelContentValues(contentValues, m);
        assertThat(contentValues.getAsString(DataSetModel.Columns.SHORT_NAME)).isEqualTo(m.shortName());
        assertThat(contentValues.getAsString(DataSetModel.Columns.DISPLAY_SHORT_NAME)).isEqualTo(m.displayShortName());
        assertThat(contentValues.getAsString(DataSetModel.Columns.DESCRIPTION)).isEqualTo(m.description());
        assertThat(contentValues.getAsString(DataSetModel.Columns.DISPLAY_DESCRIPTION)).isEqualTo(m.displayDescription());
    }

    private static void fillModelProperties(BaseModel.Builder builder) {
        builder
                .id(2L);
    }

    public static void fillIdentifiableModelProperties(BaseIdentifiableObjectModel.Builder builder) {
        fillModelProperties(builder);
        builder
                .uid("test_uid")
                .code("test_code")
                .name("test_name")
                .displayName("test_display_name")
                .created(new Date())
                .lastUpdated(new Date());
    }

    public static void fillNameableModelProperties(BaseNameableObjectModel.Builder builder) {
        fillIdentifiableModelProperties(builder);
        builder
                .shortName("test_short_name")
                .displayShortName("test_display_short_name")
                .description("test_description")
                .displayDescription("test_display_description");
    }

    private static Object[] getModelAsObjectArray(BaseModel m) {
        return new Object[] {
            m.id()
        };
    }

    public static Object[] getIdentifiableModelAsObjectArray(BaseIdentifiableObjectModel m) {
        return Utils.appendInNewArray(getModelAsObjectArray(m),
                m.uid(), m.code(), m.name(), m.displayName(),
                m.createdStr(), m.lastUpdatedStr()
        );
    }

    public static Object[] getNameableModelAsObjectArray(BaseNameableObjectModel m) {
        return Utils.appendInNewArray(getIdentifiableModelAsObjectArray(m),
                m.shortName(), m.displayShortName(),
                m.description(), m.displayDescription()
        );
    }
}
