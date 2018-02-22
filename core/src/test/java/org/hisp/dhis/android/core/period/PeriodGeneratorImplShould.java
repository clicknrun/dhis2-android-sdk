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

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PeriodGeneratorImplShould {

    @Mock
    private PeriodGenerator dailyPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyWednesdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyThursdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklySaturdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklySundayPeriodGenerator;

    @Mock
    private PeriodGenerator monthlyPeriodGenerator;

    @Mock
    private PeriodGenerator quarterPeriodGenerator;

    @Mock
    private PeriodGenerator biMonthlyPeriodGenerator;

    @Mock
    private PeriodGenerator sixMonthlyPeriodGenerator;

    @Mock
    private PeriodGenerator sixMonthlyAprilPeriodGenerator;

    @Mock
    private PeriodGenerator yearlyPeriodGenerator;

    @Mock
    private PeriodGenerator financialAprilPeriodGenerator;

    @Mock
    private PeriodGenerator financialJulyPeriodGenerator;

    @Mock
    private PeriodGenerator financialOctPeriodGenerator;

    @Mock
    private PeriodModel dailyPeriod;

    // object to test
    private ParentPeriodGeneratorImpl periodGenerator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        periodGenerator = new ParentPeriodGeneratorImpl(dailyPeriodGenerator, weeklyPeriodGenerator,
                weeklyWednesdayPeriodGenerator, weeklyThursdayPeriodGenerator, weeklySaturdayPeriodGenerator,
                weeklySundayPeriodGenerator, monthlyPeriodGenerator, biMonthlyPeriodGenerator,
                quarterPeriodGenerator, sixMonthlyPeriodGenerator, sixMonthlyAprilPeriodGenerator,
                yearlyPeriodGenerator, financialAprilPeriodGenerator, financialJulyPeriodGenerator,
                financialOctPeriodGenerator);
        
        when(dailyPeriodGenerator.generateLastPeriods(60)).thenReturn(Lists.newArrayList(dailyPeriod));
    }

    @Test
    public void request_60_daily_periods() throws Exception {
        periodGenerator.generatePeriods();
        verify(dailyPeriodGenerator).generateLastPeriods(60);
    }

    @Test
    public void return_daily_periods() throws Exception {
        periodGenerator.generatePeriods();
        assertThat(periodGenerator.generatePeriods().contains(dailyPeriod)).isEqualTo(true);
    }
}