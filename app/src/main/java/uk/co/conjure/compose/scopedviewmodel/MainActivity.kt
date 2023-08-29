package uk.co.conjure.compose.scopedviewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.co.conjure.compose.scopedviewmodel.ui.theme.ScopedViewModelComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScopedViewModelComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row {
                        SharedVmDemo()
                        DetachDemo()
                        NestedDemo()
                    }
                }
            }
        }
    }
}



@Composable
private fun SharedVmDemo() {
    CreateScope { vm: CounterViewModel ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CounterDisplay()
            CounterWidget(vm, Color.Red)
        }
    }
}

/**
 * This demo shows that creating a new scope will provide a new ViewModel.
 */
@Composable
private fun NestedDemo() {
    CreateScope { vm: CounterViewModel ->
        Column(
            modifier = Modifier
                .background(Color.Green)
                .padding(16.dp)
        ) {
            CounterButton(vm.value.value, onClick = { vm.increment() })
            CreateScope { vm: CounterViewModel ->
                CounterWidget(vm, Color.Yellow)
            }
        }
    }
}

/**
 * This demo shows that when a composable is detached, the view model is cleared too.
 */
@Composable
private fun DetachDemo() {
    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val attached = remember { mutableStateOf(true) }
        Button(onClick = { attached.value = !attached.value }) {
            Text(text = if (attached.value) "Detach" else "Attach")
        }
        if (attached.value) {
            CreateScope { vm: CounterViewModel ->
                CounterWidget(vm, Color.Blue)
            }
        }
    }
}

@Composable
fun CounterWidget(vm: CounterViewModel, color: Color) {
    Column(
        modifier = Modifier
            .background(color)
            .padding(16.dp)
    ) {
        CounterButton(vm.value.value, onClick = { vm.increment() })
        CounterButton()
    }
}

@Composable
fun CounterDisplay() {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Counter:")
            CounterDisplayText()
        }
    }
}

@Composable
fun CounterDisplayText() {
    val vm = scopedViewModel<CounterViewModel>()
    Text(text = "${vm.value.value}")
}


@Composable
fun CounterButton(value: Int, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "$value")
    }
}

@Composable
fun CounterButton(vm: CounterViewModel = scopedViewModel()) {
    Button(onClick = { vm.increment() }) {
        Text(text = "${vm.value.value}")
    }
}