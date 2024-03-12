package com.example.compose_study

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_study.ui.theme.Compose_StudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 구성 가능한 함수의 스타일을 지정하는 방법 - 앱 테마 설정
            Compose_StudyTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

// MyApp 컴포저블을 재사용 하여 코드 중복을 피할 수 있으므로 onCreate 콜백과 미리보기를 정리
@Composable
private fun MyApp(modifier: Modifier = Modifier) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface (color = MaterialTheme.colorScheme.primary) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(24.dp)
        )
    }
}

// -------------------------------------------------------------------------

@Composable
fun MyApp1(
    modifier: Modifier = Modifier,
    names: List<String> = listOf("World", "Compose")
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        for (name in names) {
            Greeting1(name = name)
        }
    }
}

@Composable
private fun Greeting1(name: String) {
    val expanded = remember { mutableStateOf(false)}
    val extraPadding = if(expanded.value) 48.dp else 0.dp
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier= Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding)
            ) {
                Text(text = "Hello,")
                Text(text = name)
            }
            ElevatedButton(
                onClick = { expanded.value = !expanded.value }
            ) {
                Text(if (expanded.value) "Show less" else "Show more")
            }
        }
    }
}

//8. 상태 호이스팅
// -------------------------------------------------------------------------

@Composable
fun MyApp2(modifier: Modifier = Modifier,
           names: List<String> = listOf("World", "Compose")
) {
    // = 대신 by 키워드로 매번 .value를 입력할 필요 없게 해주는 속성

    //10. 상태 유지
    // remember => 컴포저블이 컴포지션에 유지되는 동안만 작동
    // rememberSaveable => 구성 변경과 프로세스 중단에도 각 상태를 저장함
    //    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            Greetings()
        }
    }
}

@Preview
@Composable
fun MyAppPreview() {
    Compose_StudyTheme {
        MyApp2(Modifier.fillMaxSize())
    }
}

@Composable
private fun Greetings(
    modifier: Modifier = Modifier,
    names: List<String> = listOf("World", "Compose")
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        for (name in names) {
            Greeting1(name = name)
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: This state should be hoisted

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }
}

// -------------------------------------------------------------------------
//9. 성능 지연 목록 만들기

@Composable
private fun Greetings1(
    modifier: Modifier = Modifier,
    names: List<String> = List(1000) { "$it" }
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting1(name = name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyColumnPreview() {
    Compose_StudyTheme {
        Surface (Modifier.fillMaxSize()) {
            Greetings1()
        }
    }
}

// -------------------------------------------------------------------------
//11. 목록에 애니메이션 적용
@Composable
private fun Greeting_anm(name: String) {

    var expanded by remember { mutableStateOf(false) }
    // animateDpAsState: 애니메이션을 맞춤 설정 할 수 있는 animationSpec 매개변수를 선택적으로 사용함
    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello, ")
                // 12. 앱의 스타일 지정 및 테마 설정
                // MaterialTheme 내부의 색상, 모양, 글꼴 스타일을 유지하는 것이 좋으나
                // 해당 스타일에서 벗어나야하는 경우, copy 함수를 하용하여 수정할 수 있다.
                Text(text = name, style = MaterialTheme.typography.headlineMedium
                    .copy(fontWeight = FontWeight.ExtraBold))
            }
            ElevatedButton(
                onClick = { expanded = !expanded }
            ) {
                Text(if (expanded) "Show less" else "Show more")
            }

        }
    }
}

@Composable
private fun Greetings2(
    modifier: Modifier = Modifier,
    names: List<String> = List(1000) { "$it" }
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting_anm(name = name)
        }
    }
}

// 미리 보기에선 기본적으로 동적 색상이 적용됨
@Preview(
    showBackground = true,
    widthDp =  320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(showBackground = true)
@Composable
fun Anime_Preview() {
    Compose_StudyTheme {
        Surface (Modifier.fillMaxSize()) {
            Greetings2()
        }
    }
}
// -------------------------------------------------------------------------
//13. 설정 완료

@Composable
fun MyApp3(modifier: Modifier = Modifier) {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    Surface(modifier, color = MaterialTheme.colorScheme.background) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            Greetings_card()
        }
    }
}

@Composable
private fun Greetings_card(
    modifier: Modifier = Modifier,
    names: List<String> = List(1000) { "$it" }
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting_card(name = name)
        }
    }
}

@Composable
private fun Greeting_card(name: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(name)
    }
}

@Composable
private fun CardContent(name: String) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "Hello, ")
            Text(
                text = name, style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                            "padding theme elit, sed do bouncy. ").repeat(4),
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

// -------------------------------------------------------------------------



//@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    Compose_StudyTheme {
        OnboardingScreen(onContinueClicked = {}) //Do noting on click
    }
}

@Preview
@Composable
fun CardContentPreview() {
    Compose_StudyTheme {
        CardContent(name = "1") //Do noting on click
    }
}

@Preview(showBackground = true, name = "Preview")
@Composable
fun DefaultPreview() {
    Compose_StudyTheme {
        //MyApp()
        //MyApp1()
        MyApp3()
    }
}