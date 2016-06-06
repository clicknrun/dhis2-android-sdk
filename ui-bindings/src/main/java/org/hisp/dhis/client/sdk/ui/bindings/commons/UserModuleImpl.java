package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.core.common.network.Configuration;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;

final class UserModuleImpl implements UserModule {

    @NonNull
    private final AppModule appModule;

    @Nullable
    private CurrentUserInteractor currentUserInteractor;

    @Nullable
    private LauncherPresenter launcherPresenter;

    @Nullable
    private LoginPresenter loginPresenter;

    @Nullable
    private HomePresenter homePresenter;

    public UserModuleImpl(@NonNull AppModule appModule, @NonNull String serverUrl) {
        this.appModule = appModule;

        // configure D2
        Configuration configuration = new Configuration(serverUrl);
        D2.configure(configuration).toBlocking().first();
    }

    @Override
    public CurrentUserInteractor providesCurrentUserInteractor() {
        if (currentUserInteractor == null) {
            currentUserInteractor = D2.me();
        }

        return currentUserInteractor;
    }

    @Override
    public LauncherPresenter providesLauncherPresenter() {
        if (launcherPresenter == null) {
            launcherPresenter = new LauncherPresenterImpl(providesCurrentUserInteractor());
        }

        return launcherPresenter;
    }

    @Override
    public LoginPresenter providesLoginPresenter() {
        if (loginPresenter == null) {
            loginPresenter = new LoginPresenterImpl(providesCurrentUserInteractor(),
                    appModule.providesApiExceptionHandler(),
                    appModule.providesLogger());
        }

        return loginPresenter;
    }

    @Override
    public HomePresenter providesHomePresenter() {
        if (homePresenter == null) {
            homePresenter = new HomePresenterImpl(
                    providesCurrentUserInteractor(), appModule.providesSyncDateWrapper(),
                    appModule.providesLogger());
        }

        return homePresenter;
    }

    @Override
    public ProfilePresenter providesProfilePresenter() {
        return null;
    }

}
