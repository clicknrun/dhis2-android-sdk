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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CategoryOptionTests {

    @Test
    public void categoryOption_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        InputStream jsonStream = this.getClass().getClassLoader()
                .getResourceAsStream("category/categoryOption.json");

        CategoryOption option = objectMapper.readValue(jsonStream, CategoryOption.class);


        assertThat(option.uid()).isEqualTo("i4Nbp8S2G6A");
        assertThat(option.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-12-20T22:14:03.322"));
        assertThat(option.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-07-04T18:26:53.030"));

        assertThat(option.name()).isEqualTo("Improve access to clean water");
        assertThat(option.shortName()).isEqualTo("Improve access to clean water");
        assertThat(option.displayName()).isEqualTo("Improve access to clean water");
        assertThat(option.displayShortName()).isEqualTo("Improve access to clean water");

        assertThat(option.startDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-01T00:00:00.000"));
        assertThat(option.endDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2018-08-01T00:00:00.000"));

        // check if list maintains order of the items in payload
        assertThat(option.categoryOptionCombos().get(0).uid()).isEqualTo("SeWJkpLAyLt");
        assertThat(option.categoryOptionCombos().get(1).uid()).isEqualTo("CQMZckgv9d2");
        assertThat(option.categoryOptionCombos().get(2).uid()).isEqualTo("fC3z1lcAW5x");
        assertThat(option.categoryOptionCombos().get(3).uid()).isEqualTo("N7QFN41eTN8");
        assertThat(option.categoryOptionCombos().get(4).uid()).isEqualTo("XVl0bL5Bl4q");
        assertThat(option.categoryOptionCombos().get(5).uid()).isEqualTo("h5i1N1wcq9G");
        assertThat(option.categoryOptionCombos().get(6).uid()).isEqualTo("pO5CEqK6c1s");
        assertThat(option.categoryOptionCombos().get(7).uid()).isEqualTo("M0PqoB8pblq");
        assertThat(option.categoryOptionCombos().get(8).uid()).isEqualTo("KnS3rABktqN");
        assertThat(option.categoryOptionCombos().get(9).uid()).isEqualTo("i3HQ1ziPjC7");
        assertThat(option.categoryOptionCombos().get(10).uid()).isEqualTo("RRJ9WsqSrVs");
        assertThat(option.categoryOptionCombos().get(11).uid()).isEqualTo("BN9KwUloeSL");
        assertThat(option.categoryOptionCombos().get(12).uid()).isEqualTo("E29j0LFxvSV");
        assertThat(option.categoryOptionCombos().get(13).uid()).isEqualTo("QjyqqJMm0X7");
        assertThat(option.categoryOptionCombos().get(14).uid()).isEqualTo("RuYW1OENYhV");
        assertThat(option.categoryOptionCombos().get(15).uid()).isEqualTo("cUb8seKgn2y");
        assertThat(option.categoryOptionCombos().get(16).uid()).isEqualTo("GZMZ9gZMvGs");
    }
}
