package cn.lliiooll.gmca.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.gmca.R
import cn.lliiooll.gmca.utils.makeToastLong
import cn.lliiooll.gmca.utils.toDp


@Preview(showBackground = true)
@Composable
fun PTitleBar() {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 20.dp, 10.dp, 10.dp),
        ) {
            Text(
                text = "GMCAndroid",
                modifier = Modifier
                    .weight(1f, true),
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
            )
            val ctx = LocalContext.current
            var status by remember {
                mutableStateOf(true)
            }
            Surface(shape = CircleShape) {
                val f: Float by animateFloatAsState(
                    targetValue = if (status) 0f else 90f,
                    animationSpec = tween(
                        durationMillis = 300
                    ),
                    finishedListener = {

                    }
                )
                Image(
                    painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_more_dark else R.drawable.ic_more_light),
                    contentDescription = "icon_more",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            status = !status
                            // 弹出dialog
                        }
                        .graphicsLayer {
                            transformOrigin = TransformOrigin.Center
                            rotationZ = f
                        }
                )

                val m_h: Float by animateFloatAsState(
                    targetValue = if (status) 0f else 120f,
                )

                DropdownMenu(
                    expanded = !status,
                    onDismissRequest = {
                        status = true
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(m_h.dp)
                ) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "关于"
                            )
                        },
                        onClick = {
                            status = true
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "开启自启动"
                            )
                        },
                        onClick = {
                            status = true
                            ctx.makeToastLong("由于系统限制，请到系统自带的安全中心给予GMCAndroid自启权限")
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

            }

        }
    }
}

/**
 * 沉浸式状态栏
 */
@Composable
fun StatusBar() {
    val ctx = LocalView.current.context as Activity

    Surface(
        modifier = Modifier
            .height(
                Dp(
                    getStatusBarHeight(ctx)
                        .toDp(ctx)
                )
            )
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {

    }


}

fun getStatusBarHeight(ctx: Context?): Int {
    var result = 0
    //获取状态栏高度的资源id
    if (ctx == null) {
        return result
    }
    val resourceId: Int = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = ctx.getResources().getDimensionPixelSize(resourceId)
    }
    return result
}