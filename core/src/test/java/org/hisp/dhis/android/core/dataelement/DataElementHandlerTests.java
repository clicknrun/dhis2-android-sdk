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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataElementHandlerTests {

    @Mock
    private IdentifiableObjectStore<DataElementModel> dataElementStore;

    @Mock
    private OptionSetHandler optionSetHandler;

    @Mock
    private DataElement dataElement;

    @Mock
    private OptionSet optionSet;

    // object to test
    private DataElementHandler dataElementHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataElementHandler = new DataElementHandler(dataElementStore, optionSetHandler);
        when(dataElement.uid()).thenReturn("test_data_element_uid");
        when(dataElement.optionSet()).thenReturn(optionSet);
        when(dataElementStore.update(any(DataElementModel.class))).thenReturn(1);
    }

    @Test
    public void doNothing_shouldDoNothingWhenPassingInNull() throws Exception {
        dataElementHandler.handle(null);

        verify(dataElementStore, never()).delete(anyString());
        verify(dataElementStore, never()).update(any(DataElementModel.class));
        verify(dataElementStore, never()).insert(any(DataElementModel.class));
        verify(optionSetHandler, never()).handle(any(OptionSet.class));
    }

    @Test
    public void delete_shouldDeleteDataElement() throws Exception {
        when(dataElement.deleted()).thenReturn(Boolean.TRUE);

        dataElementHandler.handle(dataElement);

        // verify that delete is called once
        verify(dataElementStore, times(1)).delete(dataElement.uid());

        // verify that update and insert is never called
        verify(dataElementStore, never()).update(any(DataElementModel.class));
        verify(dataElementStore, never()).insert(any(DataElementModel.class));

        // verify that option set handler is called once
        verify(optionSetHandler, times(1)).handle(any(OptionSet.class));
    }

    @Test
    public void update_shouldUpdateDataElement() throws Exception {
        dataElementHandler.handle(dataElement);

        verify(dataElementStore, times(1)).update(any(DataElementModel.class));

        // verify that delete or insert is never called
        verify(dataElementStore, never()).delete(anyString());
        verify(dataElementStore, never()).insert(any(DataElementModel.class));

        // verify that option set handler is called once
        verify(optionSetHandler, times(1)).handle(any(OptionSet.class));
    }

    @Test
    public void insert_shouldInsertDataElement() throws Exception {
        dataElementHandler.handle(dataElement);

        // verify that insert is called once
        verify(dataElementStore, times(1)).insert(any(DataElementModel.class));

        // verify that update is called once since we update before we insert
        verify(dataElementStore, times(1)).update(any(DataElementModel.class));

        // verify that delete is never called
        verify(dataElementStore, never()).delete(anyString());

        // verify that option set handler is called once
        verify(optionSetHandler, times(1)).handle(any(OptionSet.class));
    }
}
