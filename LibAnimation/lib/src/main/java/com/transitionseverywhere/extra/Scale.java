/*
 * Copyright (C) 2016 Andrey Kulikov (andkulikov@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.transitionseverywhere.extra;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionValues;
import androidx.transition.Visibility;

import com.transitionseverywhere.utils.TransitionUtils;

import org.ayo.animate.R;

/**
 * This transition tracks changes to the visibility of target views in the
 * start and end scenes and scales views up or down. Visibility is determined by both the
 * {@link View#setVisibility(int)} state of the view as well as whether it
 * is parented in the current view hierarchy. Disappearing Views are
 * limited as described in {@link Visibility#onDisappear(ViewGroup,
 * TransitionValues, int, TransitionValues, int)}.
 * <p/>
 * Created by Andrey Kulikov on 13/03/16.
 */
public class Scale extends Visibility {

    static final String PROPNAME_SCALE_X = "scale:scaleX";
    static final String PROPNAME_SCALE_Y = "scale:scaleY";

    private float mDisappearedScale = 0f;

    public Scale() {
    }

    /**
     * @param disappearedScale Value of scale on start of appearing or in finish of disappearing.
     *                         Default value is 0. Can be useful for mixing some Visibility
     *                         transitions, for example Scale and Fade
     */
    public Scale(float disappearedScale) {
        setDisappearedScale(disappearedScale);
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        transitionValues.values.put(PROPNAME_SCALE_X, transitionValues.view.getScaleX());
        transitionValues.values.put(PROPNAME_SCALE_Y, transitionValues.view.getScaleY());
    }

    /**
     * @param disappearedScale Value of scale on start of appearing or in finish of disappearing.
     *                         Default value is 0. Can be useful for mixing some Visibility
     *                         transitions, for example Scale and Fade
     * @return This Scale object.
     */
    @NonNull
    public Scale setDisappearedScale(float disappearedScale) {
        if (disappearedScale < 0f) {
            throw new IllegalArgumentException("disappearedScale cannot be negative!");
        }
        mDisappearedScale = disappearedScale;
        return this;
    }

    public Scale(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Scale);
        setDisappearedScale(a.getFloat(R.styleable.Scale_disappearedScale, mDisappearedScale));
        a.recycle();
    }

    @Nullable
    private Animator createAnimation(@NonNull final View view, float startScale, float endScale, @Nullable TransitionValues values) {
        final float initialScaleX = view.getScaleX();
        final float initialScaleY = view.getScaleY();
        float startScaleX = initialScaleX * startScale;
        float endScaleX = initialScaleX * endScale;
        float startScaleY = initialScaleY * startScale;
        float endScaleY = initialScaleY * endScale;

        if (values != null) {
            Float savedScaleX = (Float) values.values.get(PROPNAME_SCALE_X);
            Float savedScaleY = (Float) values.values.get(PROPNAME_SCALE_Y);
            // if saved value is not equal initial value it means that previous
            // transition was interrupted and in the onTransitionEnd
            // we've applied endScale. we should apply proper value to
            // continue animation from the interrupted state
            if (savedScaleX != null && savedScaleX != initialScaleX) {
                startScaleX = savedScaleX;
            }
            if (savedScaleY != null && savedScaleY != initialScaleY) {
                startScaleY = savedScaleY;
            }
        }

        view.setScaleX(startScaleX);
        view.setScaleY(startScaleY);

        Animator animator = TransitionUtils.mergeAnimators(
            ObjectAnimator.ofFloat(view, View.SCALE_X, startScaleX, endScaleX),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, startScaleY, endScaleY));
        addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                view.setScaleX(initialScaleX);
                view.setScaleY(initialScaleY);
                transition.removeListener(this);
            }
        });
        return animator;
    }

    @Nullable
    @Override
    public Animator onAppear(@NonNull ViewGroup sceneRoot, @NonNull final View view, @Nullable TransitionValues startValues,
                             @Nullable TransitionValues endValues) {
        return createAnimation(view, mDisappearedScale, 1f, startValues);
    }

    @Override
    public Animator onDisappear(@NonNull ViewGroup sceneRoot, @NonNull final View view, @Nullable TransitionValues startValues,
                                @Nullable TransitionValues endValues) {
        return createAnimation(view, 1f, mDisappearedScale, startValues);
    }

}
