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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class ParentPeriodGeneratorImpl implements ParentPeriodGenerator {

    static final int DAILY_PERIODS = 60;
    static final int WEEKLY_PERIODS = 13;
    static final int MONTHLY_PERIODS = 12;
    static final int BIMONTHLY_PERIODS = 6;
    static final int QUARTER_PERIODS = 5;
    static final int SIXMONTHLY_PERIODS = 5;
    static final int YEARLY_PERIODS = 5;

    private final PeriodGenerator daily;
    private final PeriodGenerator weekly;
    private final PeriodGenerator weeklyWednesday;
    private final PeriodGenerator weeklyThursday;
    private final PeriodGenerator weeklySaturday;
    private final PeriodGenerator weeklySunday;
    private final PeriodGenerator monthly;
    private final PeriodGenerator biMonthly;
    private final PeriodGenerator quarter;
    private final PeriodGenerator sixMonthly;
    private final PeriodGenerator sixMonthlyApril;
    private final PeriodGenerator yearly;
    private final PeriodGenerator financialApril;
    private final PeriodGenerator financialJuly;
    private final PeriodGenerator financialOct;

    ParentPeriodGeneratorImpl(PeriodGenerator daily,
                              PeriodGenerator weekly,
                              PeriodGenerator weeklyWednesday,
                              PeriodGenerator weeklyThursday,
                              PeriodGenerator weeklySaturday,
                              PeriodGenerator weeklySunday,
                              PeriodGenerator monthly,
                              PeriodGenerator biMonthly,
                              PeriodGenerator quarter,
                              PeriodGenerator sixMonthly,
                              PeriodGenerator sixMonthlyApril,
                              PeriodGenerator yearly,
                              PeriodGenerator financialApril,
                              PeriodGenerator financialJuly,
                              PeriodGenerator financialOct) {
        this.daily = daily;
        this.weekly = weekly;
        this.weeklyWednesday = weeklyWednesday;
        this.weeklyThursday = weeklyThursday;
        this.weeklySaturday = weeklySaturday;
        this.weeklySunday = weeklySunday;
        this.monthly = monthly;
        this.biMonthly = biMonthly;
        this.quarter = quarter;
        this.sixMonthly = sixMonthly;
        this.sixMonthlyApril = sixMonthlyApril;
        this.yearly = yearly;
        this.financialApril = financialApril;
        this.financialJuly = financialJuly;
        this.financialOct = financialOct;
    }

    public List<PeriodModel> generatePeriods() {
        List<PeriodModel> periods = new ArrayList<>();
        periods.addAll(daily.generateLastPeriods(DAILY_PERIODS));

        periods.addAll(weekly.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weeklyWednesday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weeklyThursday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weeklySaturday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weeklySunday.generateLastPeriods(WEEKLY_PERIODS));

        periods.addAll(monthly.generateLastPeriods(MONTHLY_PERIODS));

        periods.addAll(biMonthly.generateLastPeriods(BIMONTHLY_PERIODS));

        periods.addAll(quarter.generateLastPeriods(QUARTER_PERIODS));

        periods.addAll(sixMonthly.generateLastPeriods(SIXMONTHLY_PERIODS));
        periods.addAll(sixMonthlyApril.generateLastPeriods(SIXMONTHLY_PERIODS));

        periods.addAll(yearly.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(financialApril.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(financialJuly.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(financialOct.generateLastPeriods(YEARLY_PERIODS));

        return periods;
    }

    static ParentPeriodGeneratorImpl create() {
        Calendar calendar = Calendar.getInstance();
        return new ParentPeriodGeneratorImpl(
                new DailyPeriodGenerator(calendar),
                WeeklyPeriodGeneratorFactory.weekly(calendar),
                WeeklyPeriodGeneratorFactory.wednesday(calendar),
                WeeklyPeriodGeneratorFactory.thursday(calendar),
                WeeklyPeriodGeneratorFactory.saturday(calendar),
                WeeklyPeriodGeneratorFactory.sunday(calendar),
                new MonthlyPeriodGenerator(calendar),
                NMonthlyPeriodGeneratorFactory.biMonthly(calendar),
                NMonthlyPeriodGeneratorFactory.quarter(calendar),
                NMonthlyPeriodGeneratorFactory.sixMonthly(calendar),
                NMonthlyPeriodGeneratorFactory.sixMonthlyApril(calendar),
                YearlyPeriodGeneratorFactory.yearly(calendar),
                YearlyPeriodGeneratorFactory.financialApril(calendar),
                YearlyPeriodGeneratorFactory.financialJuly(calendar),
                YearlyPeriodGeneratorFactory.financialOct(calendar)
        );
    }
}
