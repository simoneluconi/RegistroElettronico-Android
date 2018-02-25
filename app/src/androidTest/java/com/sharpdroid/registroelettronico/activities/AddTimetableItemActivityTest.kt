package com.sharpdroid.registroelettronico.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralClickAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.NavigationViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.InputDevice
import android.view.MotionEvent
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.views.timetable.TimetableLayout
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {

        //rule.launchActivity(Intent())
    }

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
    }

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