package com.deadlinekiller.utils

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.util.*

class MiscTest {
    @Test
    fun testGetDurationDescription() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.resources.configuration.setLocale(Locale("en"))
        assertEquals("0 minutes", getDurationDescription(context, Duration.ZERO))
        assertEquals("1 minute", getDurationDescription(context, Duration.ofMinutes(1)))
        assertEquals("2 minutes", getDurationDescription(context, Duration.ofMinutes(2)))
        assertEquals("1 hour and 0 minutes", getDurationDescription(context, Duration.ofHours(1)))
        assertEquals(
            "1 hour and 1 minute",
            getDurationDescription(context, Duration.ofHours(1).plusMinutes(1))
        )
        assertEquals(
            "1 hour and 2 minutes",
            getDurationDescription(context, Duration.ofHours(1).plusMinutes(2))
        )
        assertEquals(
            "2 hours and 0 minutes",
            getDurationDescription(context, Duration.ofHours(2))
        )
        assertEquals(
            "2 hours and 1 minute",
            getDurationDescription(context, Duration.ofHours(2).plusMinutes(1))
        )
        assertEquals(
            "2 hours and 2 minutes",
            getDurationDescription(context, Duration.ofHours(2).plusMinutes(2))
        )
        assertEquals(
            "1 day, 0 hours and 0 minutes",
            getDurationDescription(context, Duration.ofDays(1))
        )
        assertEquals(
            "1 day, 0 hours and 1 minute",
            getDurationDescription(context, Duration.ofDays(1).plusMinutes(1))
        )
        assertEquals(
            "1 day, 0 hours and 2 minutes",
            getDurationDescription(context, Duration.ofDays(1).plusMinutes(2))
        )
        assertEquals(
            "1 day, 1 hour and 0 minutes",
            getDurationDescription(context, Duration.ofDays(1).plusHours(1))
        )
        assertEquals(
            "1 day, 1 hour and 1 minute",
            getDurationDescription(context, Duration.ofDays(1).plusHours(1).plusMinutes(1))
        )
        assertEquals(
            "1 day, 1 hour and 2 minutes",
            getDurationDescription(context, Duration.ofDays(1).plusHours(1).plusMinutes(2))
        )
        assertEquals(
            "1 day, 2 hours and 0 minutes",
            getDurationDescription(context, Duration.ofDays(1).plusHours(2))
        )
        assertEquals(
            "1 day, 2 hours and 1 minute",
            getDurationDescription(context, Duration.ofDays(1).plusHours(2).plusMinutes(1))
        )
        assertEquals(
            "1 day, 2 hours and 2 minutes",
            getDurationDescription(context, Duration.ofDays(1).plusHours(2).plusMinutes(2))
        )
        assertEquals(
            "2 days, 0 hours and 0 minutes",
            getDurationDescription(context, Duration.ofDays(2))
        )
        assertEquals(
            "2 days, 0 hours and 1 minute",
            getDurationDescription(context, Duration.ofDays(2).plusMinutes(1))
        )
        assertEquals(
            "2 days, 0 hours and 2 minutes",
            getDurationDescription(context, Duration.ofDays(2).plusMinutes(2))
        )
        assertEquals(
            "2 days, 1 hour and 0 minutes",
            getDurationDescription(context, Duration.ofDays(2).plusHours(1))
        )
        assertEquals(
            "2 days, 1 hour and 1 minute",
            getDurationDescription(context, Duration.ofDays(2).plusHours(1).plusMinutes(1))
        )
        assertEquals(
            "2 days, 1 hour and 2 minutes",
            getDurationDescription(context, Duration.ofDays(2).plusHours(1).plusMinutes(2))
        )
        assertEquals(
            "2 days, 2 hours and 0 minutes",
            getDurationDescription(context, Duration.ofDays(2).plusHours(2))
        )
        assertEquals(
            "2 days, 2 hours and 1 minute",
            getDurationDescription(context, Duration.ofDays(2).plusHours(2).plusMinutes(1))
        )
        assertEquals(
            "2 days, 2 hours and 2 minutes",
            getDurationDescription(context, Duration.ofDays(2).plusHours(2).plusMinutes(2))
        )
        assertEquals(
            "-(1 minute)",
            getDurationDescription(context, Duration.ofMinutes(1).negated())
        )
        assertEquals(
            "-(2 minutes)",
            getDurationDescription(context, Duration.ofMinutes(2).negated())
        )
        assertEquals(
            "-(1 hour and 0 minutes)",
            getDurationDescription(context, Duration.ofHours(1).negated())
        )
        assertEquals(
            "-(1 hour and 1 minute)",
            getDurationDescription(context, Duration.ofHours(1).plusMinutes(1).negated())
        )
        assertEquals(
            "-(1 hour and 2 minutes)",
            getDurationDescription(context, Duration.ofHours(1).plusMinutes(2).negated())
        )
        assertEquals(
            "-(2 hours and 0 minutes)",
            getDurationDescription(context, Duration.ofHours(2).negated())
        )
        assertEquals(
            "-(2 hours and 1 minute)",
            getDurationDescription(context, Duration.ofHours(2).plusMinutes(1).negated())
        )
        assertEquals(
            "-(2 hours and 2 minutes)",
            getDurationDescription(context, Duration.ofHours(2).plusMinutes(2).negated())
        )
        assertEquals(
            "-(1 day, 0 hours and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(1).negated())
        )
        assertEquals(
            "-(1 day, 0 hours and 1 minute)",
            getDurationDescription(context, Duration.ofDays(1).plusMinutes(1).negated())
        )
        assertEquals(
            "-(1 day, 0 hours and 2 minutes)",
            getDurationDescription(context, Duration.ofDays(1).plusMinutes(2).negated())
        )
        assertEquals(
            "-(1 day, 1 hour and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(1).plusHours(1).negated())
        )
        assertEquals(
            "-(1 day, 1 hour and 1 minute)",
            getDurationDescription(
                context,
                Duration.ofDays(1).plusHours(1).plusMinutes(1).negated()
            )
        )
        assertEquals(
            "-(1 day, 1 hour and 2 minutes)",
            getDurationDescription(
                context,
                Duration.ofDays(1).plusHours(1).plusMinutes(2).negated()
            )
        )
        assertEquals(
            "-(1 day, 2 hours and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(1).plusHours(2).negated())
        )
        assertEquals(
            "-(1 day, 2 hours and 1 minute)",
            getDurationDescription(
                context,
                Duration.ofDays(1).plusHours(2).plusMinutes(1).negated()
            )
        )
        assertEquals(
            "-(1 day, 2 hours and 2 minutes)",
            getDurationDescription(
                context,
                Duration.ofDays(1).plusHours(2).plusMinutes(2).negated()
            )
        )
        assertEquals(
            "-(2 days, 0 hours and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(2).negated())
        )
        assertEquals(
            "-(2 days, 0 hours and 1 minute)",
            getDurationDescription(context, Duration.ofDays(2).plusMinutes(1).negated())
        )
        assertEquals(
            "-(2 days, 0 hours and 2 minutes)",
            getDurationDescription(context, Duration.ofDays(2).plusMinutes(2).negated())
        )
        assertEquals(
            "-(2 days, 1 hour and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(2).plusHours(1).negated())
        )
        assertEquals(
            "-(2 days, 1 hour and 1 minute)",
            getDurationDescription(
                context,
                Duration.ofDays(2).plusHours(1).plusMinutes(1).negated()
            )
        )
        assertEquals(
            "-(2 days, 1 hour and 2 minutes)",
            getDurationDescription(
                context,
                Duration.ofDays(2).plusHours(1).plusMinutes(2).negated()
            )
        )
        assertEquals(
            "-(2 days, 2 hours and 0 minutes)",
            getDurationDescription(context, Duration.ofDays(2).plusHours(2).negated())
        )
        assertEquals(
            "-(2 days, 2 hours and 1 minute)",
            getDurationDescription(
                context,
                Duration.ofDays(2).plusHours(2).plusMinutes(1).negated()
            )
        )
        assertEquals(
            "-(2 days, 2 hours and 2 minutes)",
            getDurationDescription(
                context,
                Duration.ofDays(2).plusHours(2).plusMinutes(2).negated()
            )
        )
    }
}