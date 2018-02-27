package com.sharpdroid.registroelettronico.activities

import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.DrawerMatchers
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.widget.DrawerLayout
import android.view.InputDevice
import android.view.MotionEvent
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivityTest {
    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun openDrawer() {
        Espresso.onView(instanceOf(DrawerLayout::class.java))
                .check(matches(isClosed()))
                .perform(DrawerActions.open())


        Thread.sleep(1000)
        matches(DrawerMatchers.isOpen())
    }

/*
    @Test
    fun scheduleOpenActivityTest() {
        onView(withId(R.id.material_drawer_slider_layout))
                //.check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.START)))
                .perform(DrawerActions.open())
                .perform(NavigationViewActions.navigateTo(R.id.schedule))
        Thread.sleep(1000)

        onView(Matchers.instanceOf(TimetableLayout::class.java))
                .perform(clickXY(20, 20))
                .perform(clickXY(20, 20))
    }*/

    private fun clickXY(x: Int, y: Int): ViewAction {
        return GeneralClickAction(
                Tap.SINGLE,
                CoordinatesProvider { view ->
                    val screenPos = IntArray(2)
                    view.getLocationOnScreen(screenPos)

                    val screenX = (screenPos[0] + x).toFloat()
                    val screenY = (screenPos[1] + y).toFloat()

                    floatArrayOf(screenX, screenY)
                },
                Press.FINGER,
                InputDevice.SOURCE_MOUSE,
                MotionEvent.BUTTON_PRIMARY)
    }
}