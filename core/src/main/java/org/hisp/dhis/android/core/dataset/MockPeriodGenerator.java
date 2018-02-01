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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* TODO delete when actual generator is implemented */
final class MockPeriodGenerator implements PeriodGenerator {

    public List<PeriodModel> generatePeriods(Date startDate) {
        List<PeriodModel> periods = new ArrayList<>();

        try {
            periods.add(getYearly(2016));
            periods.add(getYearly(2017));
            periods.add(getYearly(2018));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return periods;
    }

    private PeriodModel getYearly(int year) throws ParseException {
        String startDateStr = year + "-01-01T00:00:00.000";
        Date startDate = BaseIdentifiableObject.DATE_FORMAT.parse(startDateStr);
        String endDateStr = year + "-12-31T23:59:59.999";
        Date endDate = BaseIdentifiableObject.DATE_FORMAT.parse(endDateStr);

        return PeriodModel.builder()
                .periodId("" + year)
                .periodType(PeriodType.Yearly)
                .startDate(startDate)
                .endDate(endDate).build();
    }
}
