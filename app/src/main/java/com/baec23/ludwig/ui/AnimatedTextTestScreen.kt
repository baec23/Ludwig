package com.baec23.ludwig.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baec23.ludwig.component.button.StatefulButton
import com.baec23.ludwig.component.inputfield.InputField
import com.baec23.ludwig.component.section.DisplaySection
import com.baec23.ludwig.morpher.component.text.AnimatedText

@Composable
fun AnimatedTextTestScreen() {
    var inputFieldText by remember { mutableStateOf("") }
    var animatedText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        DisplaySection(
            headerText = "Animated Text Test"
        ) {
            AnimatedText(
                modifier = Modifier
//                    .wrapContentSize()
                    .border(width = 2.dp, color = Color.Red),
                text = animatedText
            )
            InputField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = inputFieldText,
                onValueChange = { newValue -> inputFieldText = newValue })
            StatefulButton(text = "Apply") {
                animatedText = inputFieldText
            }
        }
    }
}