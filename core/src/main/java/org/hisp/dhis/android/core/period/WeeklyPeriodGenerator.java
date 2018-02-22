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

class WeeklyPeriodGenerator extends AbstractPeriodGenerator {
    private final int weekStartDay;
    private final String suffix;

    WeeklyPeriodGenerator(Calendar calendar, PeriodType periodType, int weekStartDay, String suffix) {
        super(calendar, "yyyy", periodType);
        this.weekStartDay = weekStartDay;
        this.suffix = suffix;
    }

    @Override
    protected void setCalendarToStartDate() {
        calendar.getTime();
        calendar.setFirstDayOfWeek(weekStartDay);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay);
    }

    @Override
    protected void setCalendarToFirstPeriod(int count) {
        calendar.add(Calendar.WEEK_OF_YEAR, -count + 1);
    }

    @Override
    protected String generateId() {
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay + 3);
        Date fourthWeekDay = calendar.getTime();
        String year = idFormatter.format(fourthWeekDay);
        Integer weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        return year + suffix + weekOfYear;
    }

    @Override
    protected Date getEndDateAndUpdateCalendar() {
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay + 6);
        return calendar.getTime();
    }
}
