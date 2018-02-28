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

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;

@AutoValue
public abstract class ValueTypeRendering {
    private static final String DESKTOP = "DESKTOP";
    private static final String MOBILE = "MOBILE";

    private static final NestedField<ValueTypeRendering, ValueTypeDeviceRendering> desktop
            = NestedField.create(DESKTOP);
    private static final NestedField<ValueTypeRendering, ValueTypeDeviceRendering> mobile
            = NestedField.create(MOBILE);

    public static final Fields<ValueTypeRendering> allFields =
            Fields.<ValueTypeRendering>builder().fields(
                    desktop.with(ValueTypeDeviceRendering.allFields),
                    mobile.with(ValueTypeDeviceRendering.allFields)).build();

    @Nullable
    @JsonProperty(DESKTOP)
    public abstract ValueTypeDeviceRendering desktop();

    @Nullable
    @JsonProperty(MOBILE)
    public abstract ValueTypeDeviceRendering mobile();

    @JsonCreator
    public static ValueTypeRendering create(
            @JsonProperty(DESKTOP) ValueTypeDeviceRendering desktop,
            @JsonProperty(MOBILE) ValueTypeDeviceRendering mobile) {

        return new AutoValue_ValueTypeRendering(desktop, mobile);
    }
}