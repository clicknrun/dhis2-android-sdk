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

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableModelShould;
import org.hisp.dhis.android.core.utils.ColumnsArrayUtils;
import org.hisp.dhis.android.core.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.AndroidTestUtils.toInteger;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.fillIdentifiableModelProperties;

@RunWith(AndroidJUnit4.class)
public class CategoryOptionComboModelShould extends IdentifiableModelShould<CategoryOptionComboModel, CategoryOptionCombo> {

    public CategoryOptionComboModelShould() {
        super(CategoryOptionComboModel.Columns.all(), 7, CategoryOptionComboModel.Factory);
    }

    @Override
    protected CategoryOptionComboModel buildModel() {
        CategoryOptionComboModel.Builder categoryOptionComboModel = CategoryOptionComboModel.builder();
        fillIdentifiableModelProperties(categoryOptionComboModel);
        return categoryOptionComboModel.ignoreApproval(false).build();
    }

    @Override
    protected CategoryOptionCombo buildPojo() {
        return CategoryOptionCombo.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED,
                false, new ArrayList<CategoryOption>(), DELETED);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getIdentifiableModelAsObjectArray(model),
                toInteger(model.ignoreApproval()));
    }

    @Test
    public void have_extra_category_option_combo_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(CategoryOptionComboModel.Columns.IGNORE_APPROVAL))
                .isEqualTo(true);
    }
}