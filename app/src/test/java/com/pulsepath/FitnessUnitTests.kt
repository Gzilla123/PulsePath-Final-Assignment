package com.pulsepath

import com.pulsepath.models.WorkoutSession
import com.pulsepath.models.WorkoutType
import com.pulsepath.utils.CalorieCalculator
import org.junit.Assert.*
import org.junit.Test

class FitnessUnitTests {

    @Test fun `workout defaults to RUNNING type`() {
        assertEquals(WorkoutType.RUNNING, WorkoutSession().type)
    }

    @Test fun `workout id is unique`() {
        assertNotEquals(WorkoutSession().id, WorkoutSession().id)
    }

    @Test fun `duration defaults to zero`() {
        assertEquals(0L, WorkoutSession().durationSeconds)
    }

    @Test fun `calories from running 30 minutes is reasonable`() {
        val cal = CalorieCalculator.calculate(WorkoutType.RUNNING, 1800)
        assertTrue("Expected 100-500 kcal, got $cal", cal in 100..500)
    }

    @Test fun `cycling burns fewer calories than running same duration`() {
        val running = CalorieCalculator.calculate(WorkoutType.RUNNING, 3600)
        val cycling = CalorieCalculator.calculate(WorkoutType.CYCLING, 3600)
        assertTrue(running > cycling)
    }

    @Test fun `walking burns fewer calories than cycling`() {
        val walking = CalorieCalculator.calculate(WorkoutType.WALKING, 3600)
        val cycling = CalorieCalculator.calculate(WorkoutType.CYCLING, 3600)
        assertTrue(cycling > walking)
    }

    @Test fun `calories from steps calculation`() {
        val cal = CalorieCalculator.caloriesFromSteps(10000)
        assertEquals(400, cal)
    }

    @Test fun `steps to km conversion`() {
        val km = CalorieCalculator.stepsToKm(10000)
        assertTrue(km in 6.0..9.0)
    }

    @Test fun `workout session stores userId`() {
        val session = WorkoutSession(userId = "user123")
        assertEquals("user123", session.userId)
    }

    @Test fun `workout synced flag defaults false`() {
        assertFalse(WorkoutSession().syncedToCloud)
    }

    @Test fun `copy preserves id`() {
        val s = WorkoutSession(userId = "u1")
        val copy = s.copy(steps = 500)
        assertEquals(s.id, copy.id)
    }

    @Test fun `zero duration returns zero calories`() {
        assertEquals(0, CalorieCalculator.calculate(WorkoutType.RUNNING, 0))
    }
}
