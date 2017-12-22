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
package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class GenericHandlerTests {

    private interface TestCall<A> {
        void call(A a);
    }

    @Mock
    private TestCall<DataElement> testCall;

    @Mock
    private IdentifiableObjectStore<DataElementModel> store;

    @Mock
    private DataElement pojo;

    @Mock
    private DataElement pojo2;

    private GenericHandler<DataElement, DataElementModel> genericHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(pojo.uid()).thenReturn("test_data_element_uid");
        when(pojo2.uid()).thenReturn("another_pojo_uid");

        genericHandler = new GenericHandlerImpl<DataElement, DataElementModel>(store) {
            @Override
            protected DataElementModel pojoToModel(DataElement dataElement) {
                return DataElementModel.Factory.fromPojo(dataElement);
            }

            @Override
            protected void afterObjectPersisted(DataElement dataElement) {
                testCall.call(dataElement);
            }
        };
    }

    @Test
    public void doNothing_shouldDoNothingWhenPassingInNull() throws Exception {
        genericHandler.handle(null);

        verify(store, never()).delete(anyString());
        verify(store, never()).update(any(DataElementModel.class));
        verify(store, never()).insert(any(DataElementModel.class));
        verify(store, never()).updateOrInsert(any(DataElementModel.class));
    }

    @Test
    public void delete_shouldDeleteDataElement() throws Exception {
        when(pojo.deleted()).thenReturn(Boolean.TRUE);

        genericHandler.handle(pojo);

        verify(store).delete(pojo.uid());
        verify(store, never()).update(any(DataElementModel.class));
        verify(store, never()).insert(any(DataElementModel.class));
        verify(store, never()).updateOrInsert(any(DataElementModel.class));
    }

    @Test
    public void update_shouldCallUpdateOrInsert() throws Exception {
        genericHandler.handle(pojo);

        verify(store).updateOrInsert(any(DataElementModel.class));
        verify(store, never()).update(any(DataElementModel.class));
        verify(store, never()).insert(any(DataElementModel.class));
        verify(store, never()).delete(anyString());
    }

    @Test
    public void handle_shouldCallAfterObjectPersisted() throws Exception {
        genericHandler.handle(pojo);
        verify(testCall).call(pojo);
    }

    @Test
    public void handleMany_shouldCallNTimesTheStoreAndNTimesAfterObjectPersisted() throws Exception {
        genericHandler.handleMany(Arrays.asList(pojo, pojo2));

        verify(store, times(2)).updateOrInsert(any(DataElementModel.class));
        verify(store, never()).update(any(DataElementModel.class));
        verify(store, never()).insert(any(DataElementModel.class));
        verify(store, never()).delete(anyString());

        verify(testCall).call(pojo);
        verify(testCall).call(pojo2);
    }
}
