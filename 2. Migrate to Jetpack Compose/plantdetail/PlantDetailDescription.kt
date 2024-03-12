/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.plantdetail

import android.content.res.Configuration
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.theme.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel


// 4. PlantDetailViewModel을 사용하여 데이터 로드: Description의 매개변수로 전달
@Composable
fun PlantDetailDescription(plantDetailViewModel: PlantDetailViewModel) {
    //LiveData<Plant> 필드에서 오는 값을 관찰
    val plant by plantDetailViewModel.plant.observeAsState()

    //만약 plant 값이 null이 아니면 화면에 출력
    plant?.let {
        PlantDetailContent(it)
    }

    Surface {
        Text("Hello Compose")
    }
}

@Composable
fun PlantDetailContent(plant: Plant){
    PlantName(plant.name)
}

@Preview( name = "Step 4" )
@Composable
private fun PlantDetailContentPreview(){
    val plant = Plant("id", "Apple", "description", 3, 30, "")
    MaterialTheme{
        PlantDetailContent(plant = plant)
    }
}

/*  3.
*   이전 xml 스타일과 같이
*   가로 여백 8.dp
*   가로로 화면 가운데 표시
*/
@Composable
private fun PlantName(name: String){
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Preview ( name = "Step 3" )
@Composable
private fun PlantNamePreview(){
    MaterialTheme{
        PlantName("Apple")
    }
}
/* 5. 추가 XML 코드 이전
 */
//실험적인 API 동의
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PlantWatering(wateringInterval:Int){
    Column(Modifier.fillMaxWidth()){
        // 여러번 사용할 modifier
        val centerWithPaddinModifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .align(Alignment.CenterHorizontally)
        val normalPadding = dimensionResource(R.dimen.margin_normal)

        Text(
            text = stringResource(R.string.watering_needs_prefix),
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold,
            modifier = centerWithPaddinModifier.padding(top = normalPadding)
        )

        val wateringIntervalText = pluralStringResource(
            R.plurals.watering_needs_suffix, wateringInterval, wateringInterval
        )

        Text(
            text = wateringIntervalText,
            modifier = centerWithPaddinModifier.padding(bottom = normalPadding)
        )
    }
}

@Preview( name = "Step 5")
@Composable
private  fun PlantWateringPreview(){
    MaterialTheme{
        PlantWatering(7)
    }
}

// 제목과 상세 내용 붙이기
@Composable
fun PlantDetailContent1(plant: Plant){
    Surface{
        Column(Modifier.padding(dimensionResource(id = R.dimen.margin_normal))){
            PlantName(plant.name)
            PlantWatering(plant.wateringInterval)
        }
    }
}

@Preview( name = "Step 5-1" )
@Composable
private fun PlantDetailContentPreview1(){
    val plant = Plant("id", "Apple", "description", 3, 30, "")
    MaterialTheme{
        PlantDetailContent1(plant = plant)
    }
}

/* 6. 식물 설명 이전
*   app:renderHtml="@{viewModel.plant.description}" 이전
*   renderHtml: PlantDetailBindingAdapters.kt 파일에서 찾을 수 있는 결합 어댑터
*   xml상에서의 구현은 HtmlCompat.fromHtml을 사용하여 TextView에 텍스트를 설정하는 방식이었다.
*
*   그러나 Compose는 현재 Spanned 클래스를 지원하지 않으며 HTML형식 텍스트도 표시하지 않는다.
*   이 제한을 우회하려면 View 시스템의 TextView를 사용하여 AndoridView API를 사용하여 랜더링 해야한다.
*   이를 사용하면 factory 람다에 View를 구성할 수 있으며 후속 재구성시 호출될 update 람다를 제공한다.
*/

@Composable
fun PlantDescription(description: String){
    //HTML 형식의 데이터가 재생성 될 때 기억시킬 변수
    val htmlDescription = remember(description){
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    //화면에 TextView를 출력하고 HTML 데이터가 객체화(inflated)될 시에 업데이트
    // htmlDescription이 업데이트 되면 androidView를 리컴포즈 하고 텍스트 업데이트
    AndroidView(
        factory = { context ->
            TextView(context).apply{
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = htmlDescription
        }
    )
}

@Preview(name = "Step 6")
@Composable
private fun PlantDescriptionPreview() {
    MaterialTheme {
        PlantDescription("HTML<br><br>description")
    }
}

//HTML 형식의 상세보기 추가
@Composable
fun PlantDetailContent2(plant: Plant) {
    Surface {
        Column(Modifier.padding(dimensionResource(R.dimen.margin_normal))) {
            PlantName(plant.name)
            PlantWatering(plant.wateringInterval)
            PlantDescription(plant.description)
        }
    }
}

@Preview(name = "Step 6")
@Composable
private fun PlantDetailContent2Preview() {
    val plant = Plant("id", "Apple", "HTML<br><br>description", 3, 30, "")
    MaterialTheme {
        PlantDetailContent2(plant)
    }
}

// 8. 바꾼 속성 적용
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Step 8")
@Composable
private fun PlantDetailContentDarkPreview() {
    val plant = Plant("id", "Apple", "HTML<br><br>description", 3, 30, "")
    SunflowerTheme {
        PlantDetailContent2(plant)
    }
}
















