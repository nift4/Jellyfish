package acr.browser.lightning.locale;

import android.content.res.Resources;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;


/*
 * This file was taken from the Mozilla Focus project:
 * https://github.com/mozilla-mobile/focus-android
 */

/**
 * SL: Should we use this too?
 */
public abstract class LocaleAwarePreferenceFragment extends PreferenceFragmentCompat {
    private Locale cachedLocale = null;
    private AnimationSet animationSet;

    public void cancelAnimation() {
        if (animationSet != null) {
            animationSet.setDuration(0);
            animationSet.cancel();
        }
    }

    /**
     * Is called whenever the application locale has changed. Your fragment must either update
     * all localised Strings, or replace itself with an updated version.
     */
    public abstract void applyLocale();

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);
        if (animation == null && nextAnim != 0) {
            try {
                animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            } catch (Resources.NotFoundException e) {
                return null;
            }
        }

        if (animation != null) {
            final AnimationSet animSet = new AnimationSet(true);
            animSet.addAnimation(animation);
            this.animationSet = animSet;
            return animSet;
        } else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocaleManager.getInstance()
                .correctLocale(getContext(), getResources(), getResources().getConfiguration());

        if (cachedLocale == null) {
            cachedLocale = Locale.getDefault();
        } else {
            Locale newLocale = LocaleManager.getInstance().getCurrentLocale(getActivity().getApplicationContext());

            if (newLocale == null) {
                // Using system locale:
                newLocale = Locale.getDefault();
            }
            if (!newLocale.equals(cachedLocale)) {
                cachedLocale = newLocale;
                applyLocale();
            }
        }
    }
}