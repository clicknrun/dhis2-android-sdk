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

package org.hisp.dhis.android.core.dataelement;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.NameableModelShould;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElementModel.Columns;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
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
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DESCRIPTION;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_DESCRIPTION;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.DISPLAY_SHORT_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.LAST_UPDATED;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.SHORT_NAME;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.UID;
import static org.hisp.dhis.android.core.utils.FillPropertiesTestUtils.fillNameableModelProperties;

@RunWith(AndroidJUnit4.class)
public class DataElementModelShould extends NameableModelShould<DataElementModel, DataElement> {
    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final boolean ZERO_IS_SIGNIFICANT = false;
    private static final String AGGREGATION_TYPE = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";
    private static final String OPTION_SET = UID;

    public DataElementModelShould() {
        super(DataElementModel.Columns.all(), 19, DataElementModel.Factory);
    }

    @Override
    protected DataElementModel buildModel() {
        DataElementModel.Builder dataElementModelBuilder = DataElementModel.builder();
        fillNameableModelProperties(dataElementModelBuilder);
        return dataElementModelBuilder
                .valueType(VALUE_TYPE)
                .zeroIsSignificant(ZERO_IS_SIGNIFICANT)
                .aggregationType(AGGREGATION_TYPE)
                .formName(FORM_NAME)
                .numberType(NUMBER_TYPE)
                .domainType(DOMAIN_TYPE)
                .dimension(DIMENSION)
                .displayFormName(DISPLAY_FORM_NAME)
                .optionSet(OPTION_SET).build();
    }

    @Override
    protected DataElement buildPojo() {
        return DataElement.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, SHORT_NAME,
                DISPLAY_SHORT_NAME, DESCRIPTION, DISPLAY_DESCRIPTION, VALUE_TYPE,
                ZERO_IS_SIGNIFICANT, AGGREGATION_TYPE, FORM_NAME, NUMBER_TYPE,
                DOMAIN_TYPE, DIMENSION, DISPLAY_FORM_NAME,
                OptionSet.create(UID, CODE, NAME, DISPLAY_NAME, CREATED, LAST_UPDATED, 0,
                        ValueType.AGE, new ArrayList<Option>(), DELETED),
                CategoryCombo.create("cc_uid", CODE, NAME, DISPLAY_NAME, CREATED,LAST_UPDATED,
                        new ArrayList<Category>(), new ArrayList<CategoryOptionCombo>(), DELETED),
                DELETED);
    }

    @Override
    protected Object[] getModelAsObjectArray() {
        return Utils.appendInNewArray(ColumnsArrayUtils.getNameableModelAsObjectArray(model),
                model.valueType(), toInteger(model.zeroIsSignificant()), model.aggregationType(),
                model.formName(),model.numberType(), model.domainType(), model.dimension(),
                model.displayFormName(),model.optionSet());
    }

    @Test
    public void have_extra_data_element_model_columns() {
        List<String> columnsList = Arrays.asList(columns);

        assertThat(columnsList.contains(Columns.VALUE_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.ZERO_IS_SIGNIFICANT)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.AGGREGATION_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.FORM_NAME)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.NUMBER_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DOMAIN_TYPE)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DIMENSION)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.DISPLAY_FORM_NAME)).isEqualTo(true);
        assertThat(columnsList.contains(Columns.OPTION_SET)).isEqualTo(true);
    }
}
