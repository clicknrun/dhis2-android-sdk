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

public class CategoryTests {
    @Test
    public void category_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        InputStream jsonStream = this.getClass().getClassLoader()
                .getResourceAsStream("category/category.json");

        Category category = objectMapper.readValue(jsonStream, Category.class);

        assertThat(category.uid()).isEqualTo("LFsZ8v5v7rq");
        assertThat(category.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-12-20T22:13:46.348"));
        assertThat(category.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-12-23T18:07:58.771"));

        // names
        assertThat(category.name()).isEqualTo("Implementing Partner");
        assertThat(category.displayName()).isEqualTo("Implementing Partner");

        // checking options
        assertThat(category.categoryOptions().size()).isEqualTo(16);
        assertThat(category.categoryOptions().get(0).uid()).isEqualTo("C6nZpLKjEJr");
        assertThat(category.categoryOptions().get(0).name())
                .isEqualTo("African Medical and Research Foundation");

        assertThat(category.categoryOptions().get(1).uid()).isEqualTo("CW81uF03hvV");
        assertThat(category.categoryOptions().get(15).uid()).isEqualTo("uilaJSyXt7d");
    }
}
