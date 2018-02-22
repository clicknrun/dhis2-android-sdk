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

package org.hisp.dhis.android.core.period;

import java.util.Calendar;
import java.util.Date;

class QuarterPeriodGenerator extends AbstractPeriodGenerator {

    QuarterPeriodGenerator(Calendar calendar) {
        super(calendar, "yyyy", PeriodType.Quarterly);
    }

    @Override
    protected void setCalendarToStartDate() {
        calendar.set(Calendar.DATE, 1);
        int currentMonth = calendar.get(Calendar.MONTH);
        int startMonth = currentMonth - (currentMonth % 3);
        calendar.set(Calendar.MONTH, startMonth);
    }

    @Override
    protected void setCalendarToFirstPeriod(int count) {
        calendar.add(Calendar.MONTH, -(3 * (count - 1)));
    }

    @Override
    protected String generateId() {
        int periodNumber = calendar.get(Calendar.MONTH) / 3 + 1;
        return idFormatter.format(calendar.getTime()) + "Q" + periodNumber;
    }

    @Override
    protected Date getEndDateAndUpdateCalendar() {
        calendar.add(Calendar.MONTH, 3);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
}
