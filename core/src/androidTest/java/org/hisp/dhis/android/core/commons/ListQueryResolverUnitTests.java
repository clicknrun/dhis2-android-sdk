/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.core.commons;

import android.content.ContentResolver;
import android.test.ProviderTestCase2;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import rx.schedulers.Schedulers;

import static com.google.common.truth.Truth.assertThat;

public class ListQueryResolverUnitTests extends ProviderTestCase2<FakeContentProvider> {
    // content resolver which is used to interact with database
    private ContentResolver contentResolver;

    // instance to test
    private ReadQueryResolver<List<TestModel>> listReadQueryResolver;
    private Disposable subscription;

    public ListQueryResolverUnitTests() {
        super(FakeContentProvider.class, FakeContentProvider.AUTHORITY.getAuthority());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        /* for testing purposes */
        Executor executor = new Executor() {
            @Override
            public void execute(@Nonnull Runnable runnable) {
                runnable.run();
            }
        };

        // ProviderTestCase2 supplies mocked content resolver
        contentResolver = getMockContentResolver();
        subscription = Disposables.empty();

        // concrete implementation of SqlBrite's content provider
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteContentResolver briteContentResolver = sqlBrite.wrapContentProvider(
                contentResolver, Schedulers.immediate());

        // passing mock / concrete fields to mocked provider
        getProvider().init(contentResolver);

        listReadQueryResolver = new ListQueryResolver<>(executor, briteContentResolver,
                contentResolver, new TestMapper(), FakeContentProvider.TABLE, Query.builder().build());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        subscription.dispose();
    }

    public void testAsTaskExecute_shouldCallContentResolver() {
        // insert dummy data to database
        contentResolver.insert(FakeContentProvider.TABLE,
                TestModel.values(11L, "valueTwo"));

        TestModel testModel = listReadQueryResolver.asTask().execute().get(0);

        assertThat(testModel.id()).isEqualTo(11L);
        assertThat(testModel.value()).isEqualTo("valueTwo");
    }

    public void testAsTaskExecuteAsynchronously_shouldCallContentResolver() {
        // insert dummy data to database
        contentResolver.insert(FakeContentProvider.TABLE,
                TestModel.values(11L, "valueTwo"));

        listReadQueryResolver.asTask()
                .execute(new Callback<List<TestModel>>() {
                    @Override
                    public void onSuccess(Task<List<TestModel>> task, List<TestModel> result) {
                        TestModel testModel = result.get(0);

                        assertThat(testModel.id()).isEqualTo(11L);
                        assertThat(testModel.value()).isEqualTo("valueTwo");
                    }

                    @Override
                    public void onFailure(Task<List<TestModel>> task, Throwable throwable) {
                        fail("onFailure() should not be called");
                    }
                });
    }

    public void testAsSingle_shouldCallContentResolverOnSubscribe() {
        // insert dummy data to database
        contentResolver.insert(FakeContentProvider.TABLE,
                TestModel.values(11L, "valueTwo"));

        // without any schedulers set, single should be
        // executed on current thread
        subscription = listReadQueryResolver.asSingle()
                .subscribe(new Consumer<List<TestModel>>() {
                    @Override
                    public void accept(List<TestModel> testModels) throws Exception {
                        TestModel testModel = testModels.get(0);

                        assertThat(testModel.id()).isEqualTo(11L);
                        assertThat(testModel.value()).isEqualTo("valueTwo");
                    }
                });
    }

    public void testAsObservable_shouldCallContentResolverOnSubscribe() {
        // insert dummy data to database
        contentResolver.insert(FakeContentProvider.TABLE,
                TestModel.values(11L, "valueTwo"));

        subscription = listReadQueryResolver.asObservable()
                .subscribe(new Consumer<List<TestModel>>() {
                    @Override
                    public void accept(List<TestModel> testModels) throws Exception {
                        TestModel testModel = testModels.get(0);

                        assertThat(testModel.id()).isEqualTo(11L);
                        assertThat(testModel.value()).isEqualTo("valueTwo");
                    }
                });
    }
}
