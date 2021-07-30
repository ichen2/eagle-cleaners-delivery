package com.eaglecleaners.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimePassedMessageTests {
    val currentTime = Date().time
    @Test
    fun testMinutes() {
        Assert.assertEquals("10 minutes", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 600000L))
    }

    @Test
    fun testMinute() {
        Assert.assertEquals("1 minute", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 60000L))
    }

    @Test
    fun testHours() {
        Assert.assertEquals("10 hours", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 36000000L))
    }

    @Test
    fun testHour() {
        Assert.assertEquals("1 hour", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 3600000L))
    }

    @Test
    fun testDaysAndHours() {
        Assert.assertEquals("10 days and 10 hours", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 900000000L))
    }

    @Test
    fun testDaysAndHour() {
        Assert.assertEquals("10 days and 1 hour", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 867600000L))
    }

    @Test
    fun testDayAndHours() {
        Assert.assertEquals("1 day and 10 hours", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 122400000L))
    }

    @Test
    fun testDayAndHour() {
        Assert.assertEquals("1 day and 1 hour", DeliveryRequestAdapter.getTimePassedMessage(currentTime, currentTime - 90000000L))
    }
}
