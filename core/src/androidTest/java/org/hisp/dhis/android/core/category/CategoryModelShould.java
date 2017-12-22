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
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CODE;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.CREATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DELETED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.fillIdentifiableModelProperties;

@RunWith(AndroidJUnit4.class)
public class CategoryModelShould extends IdentifiableModelShould<CategoryModel, Category> {

    public CategoryModelShould() {
        super(CategoryModel.Columns.all(), 6, CategoryModel.Factory);
    }

    @Override
    protected CategoryModel buildModel() {
        CategoryModel.Builder categoryModelBuilder = CategoryModel.builder();
        fillIdentifiableModelProperties(categoryModelBuilder);
        return categoryModelBuilder.build();
    }

    @Override
    protected Category buildPojo() {
        return Category.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED,
                new ArrayList<CategoryOption>(), DELETED);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return ColumnsArrayUtils.getIdentifiableModelAsObjectArray(model);
    }
}