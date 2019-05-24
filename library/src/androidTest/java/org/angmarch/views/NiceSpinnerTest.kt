package org.angmarch.views

import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed

import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 *  In its default behavior, the pop-up listview always shows all the other items that are not selected.
 *
 *  Example:
 *
 *     selected: p0    selected: p1    selected: p2
 *     ------------    ------------    ------------
 *     list:     p1    list:     p0    list:     p0
 *               p2              p2              p1
 */

@RunWith(Enclosed::class)
class NiceSpinnerTest {

    @RunWith(Parameterized::class)
    class NiceSpinnerListenerTest(private val selectedIndex: Int, private val expectedIndex: Int) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters(name = "selection index: {0} | expected selected: {1}")
            fun parameters() = listOf(
                    arrayOf(0, 1),
                    arrayOf(1, 2)
            )
        }

        private lateinit var spinner: NiceSpinner

        private val data = ArrayList<String>().apply {
            add("p0")
            add("p1")
            add("p2")
        }

        @Before
        fun setUp() {
            spinner = NiceSpinner(InstrumentationRegistry.getInstrumentation().context)
            spinner.attachDataSource(data)
        }

        @Test
        fun selectsTheExpectedItem() {
            val testListener = TestOnItemSelectedListener()

            spinner.onSpinnerItemSelectedListener = testListener

            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                spinner.performItemClick(null, selectedIndex, 0)
            }

            with(spinner) {
                assertThat(text.toString()).isEqualTo(data[expectedIndex])
                assertThat(selectedItem).isEqualTo(data[expectedIndex])
                assertThat(selectedIndex).isEqualTo(expectedIndex)
            }

            assertThat(testListener.lastPosition).isEqualTo(expectedIndex)
            assertThat(data[testListener.lastPosition]).isEqualTo(data[expectedIndex])
        }
    }

    @RunWith(Parameterized::class)
    class NiceSpinnerSelectionTest(private val index: Int) {

        private val data = ArrayList<String>().apply {
            add("p0")
            add("p1")
            add("p2")
        }

        companion object {

            @JvmStatic
            @Parameterized.Parameters(name = "selection index: {0}")
            fun parameters() = listOf(0, 1, 2)
        }

        private lateinit var spinner: NiceSpinner

        @Before
        fun setUp() {
            spinner = NiceSpinner(InstrumentationRegistry.getInstrumentation().context)
            spinner.attachDataSource(data)
        }

        @Test
        fun getItemAtPositionReturnsTheExpectedItem() {
            assertThat(spinner.getItemAtPosition(index)).isEqualTo(data[index])
        }
    }

}

class TestOnItemSelectedListener : OnSpinnerItemSelectedListener {

    var lastPosition: Int = 0

    override fun onItemSelected(parent: NiceSpinner?, view: View?, position: Int, id: Long) {
        lastPosition = position
    }
}
