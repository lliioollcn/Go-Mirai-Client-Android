package cn.lliiooll.gmca.ui.components

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.gmca.R
import cn.lliiooll.gmca.utils.GMCUtils
import cn.lliiooll.gmca.utils.PColor
import cn.lliiooll.gmca.utils.sync
import kotlin.concurrent.thread


@Composable
fun GMCStatus(click: Runnable) {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)

        ) {
            // 主体
            var color by remember {
                mutableStateOf(if (GMCUtils.isEnable()) PColor.SUCCESS else PColor.ERROR)
            }
            var icon by remember {
                mutableStateOf(if (GMCUtils.isEnable()) R.drawable.ic_round_check else R.drawable.ic_round_close)
            }
            var title by remember {
                mutableStateOf(if (GMCUtils.isEnable()) "GMC已启动" else "GMC未启动")
            }
            thread {
                while (true) {
                    sync {
                        color = if (GMCUtils.isEnable()) PColor.SUCCESS else PColor.ERROR
                        icon =
                            if (GMCUtils.isEnable()) R.drawable.ic_round_check else R.drawable.ic_round_close
                        title = if (GMCUtils.isEnable()) "GMC已启动" else "GMC未启动"
                    }
                    Thread.sleep(1000)
                }
            }
            Surface(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                modifier = Modifier.clickable {
                    click.run()
                }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 10.dp, 0.dp),
                    color = color,
                    border = BorderStroke(1.dp, color),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(20.dp, 18.dp, 0.dp, 18.dp)) {
                        Surface(
                            color = Color.Unspecified,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Image(
                                painter = painterResource(icon),
                                contentDescription = "icon",
                                modifier = Modifier.size(30.dp),
                                alignment = Alignment.BottomStart
                            )
                        }

                        Surface(
                            color = Color.Unspecified,
                            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            Column {
                                Text(
                                    text = title,
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)
                                )
                            }
                        }
                    }


                }
            }
        }
    }

}