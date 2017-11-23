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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DataSetTests {

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(DataSetModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void dataSet_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        DataSet dataSet = objectMapper.readValue("{" +
                        "\"code\":\"DS_394131\"," +
                        "\"lastUpdated\":\"2015-08-09T12:35:36.743\"," +
                        "\"id\":\"lyLU2wR22tC\"," +
                        "\"created\":\"2012-06-10T00:36:10.036\"," +
                        "\"name\":\"ART monthly summary\"," +
                        "\"shortName\":\"ART 2010\"," +
                        "\"validCompleteOnly\":false," +
                        "\"dataElementDecoration\":false," +
                        "\"displayName\":\"ART monthly summary\"," +
                        "\"notifyCompletingUser\":false," +
                        "\"noValueRequiresComment\":false," +
                        "\"skipOffline\":false," +
                        "\"displayShortName\":\"ART 2010\"," +
                        "\"fieldCombinationRequired\":false," +
                        "\"renderHorizontally\":false," +
                        "\"renderAsTabs\":false," +
                        "\"mobile\":false," +
                        "\"version\":22," +
                        "\"timelyDays\":0," +
                        "\"periodType\":\"Monthly\"," +
                        "\"openFuturePeriods\":0," +
                        "\"expiryDays\":0," +
                        /*"\"categoryCombo\":{\"id\":\"O4VaNks6tta\"}" +*/
                        "\"categoryCombo\":\"O4VaNks6tta\"" +
                        "}",
                DataSet.class);

        assertThat(dataSet.code()).isEqualTo("DS_394131");
        assertThat(dataSet.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-08-09T12:35:36.743"));
        assertThat(dataSet.uid()).isEqualTo("lyLU2wR22tC");
        assertThat(dataSet.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-06-10T00:36:10.036"));
        assertThat(dataSet.name()).isEqualTo("ART monthly summary");
        assertThat(dataSet.shortName()).isEqualTo("ART 2010");
        assertThat(dataSet.validCompleteOnly()).isEqualTo(false);
        assertThat(dataSet.dataElementDecoration()).isEqualTo(false);
        assertThat(dataSet.notifyCompletingUser()).isEqualTo(false);
        assertThat(dataSet.noValueRequiresComment()).isEqualTo(false);
        assertThat(dataSet.skipOffline()).isEqualTo(false);
        assertThat(dataSet.displayShortName()).isEqualTo("ART 2010");
        assertThat(dataSet.fieldCombinationRequired()).isEqualTo(false);
        assertThat(dataSet.renderHorizontally()).isEqualTo(false);
        assertThat(dataSet.renderAsTabs()).isEqualTo(false);
        assertThat(dataSet.mobile()).isEqualTo(false);
        assertThat(dataSet.version()).isEqualTo(22);
        assertThat(dataSet.timelyDays()).isEqualTo(0);
        assertThat(dataSet.periodType()).isEqualTo("Monthly");
        assertThat(dataSet.openFuturePeriods()).isEqualTo(0);
        assertThat(dataSet.expiryDays()).isEqualTo(0);
        // TODO assertThat(dataSet.categoryCombo()).isEqualTo("");
    }
}
