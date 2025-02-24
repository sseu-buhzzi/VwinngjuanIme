package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.R

internal const val lejKeyMapPattern = """
	-++++++++-
	-++++++++ 
	 +++++++  
"""

internal class LejInfo(
	val label: String,
	val keyMapPattern: String,
	val vwinList: List<VwinInfo>,
) {
	override fun toString() = "LejInfo($label, ${vwinList.joinToString { it.toString() }})"
}

internal class VwinInfo(
	val label: String,
	val druann: (PathBuilder.() -> Unit)? = null,
) {
	override fun toString() = "VwinInfo($label${druann?.let { ", $it" } ?: ""})"

	init {
		druann?.also {
			previewedVwinList.add(this)
		}
	}
}

private val previewedVwinList = mutableListOf<VwinInfo>()
@Preview(showBackground = true)
@Composable
private fun PreviewedDruannIcon() {
	Column {
		previewedVwinList.forEach { druann ->
			Row(Modifier.weight(1F)) {
				Icon(
					druann.druann?.let { druannIcon(it) } ?: Icons.Filled.QuestionMark,
					null,
				)
				Text(druann.label, style = TextStyle(fontFamily = FontFamily(Font(R.font.gen_ryu_min2_tc_b))))
			}
		}
	}
}

internal fun druannIcon(pathBuilder: PathBuilder.() -> Unit) = ImageVector.Builder(
	defaultWidth = 0x20.dp,
	defaultHeight = 0x20.dp,
	viewportWidth = 32F,
	viewportHeight = 32F,
).path(
	fill = null,
	stroke = SolidColor(Color.Black),
	strokeLineWidth = 2F,
	strokeLineCap = StrokeCap.Round,
	strokeLineJoin = StrokeJoin.Round,
	pathBuilder = pathBuilder,
).build()

internal val lejList: List<LejInfo> = listOf(
	LejInfo("一", """
		--++++++--
		--++++++- 
		--+++--  
	""", listOf(
		VwinInfo("一") {
			moveTo(4F, 16F)
			horizontalLineTo(28F)
		},
		VwinInfo("丄") {
			moveTo(12F, 8F)
			horizontalLineTo(20F)
			moveTo(4F, 16F)
			horizontalLineTo(28F)
		},
		VwinInfo("丅") {
			moveTo(4F, 16F)
			horizontalLineTo(28F)
			moveTo(12F, 24F)
			horizontalLineTo(20F)
		},
		VwinInfo("丨") {
			moveTo(16F, 4F)
			verticalLineTo(28F)
		},
		VwinInfo("八") {
			moveTo(8F, 4F)
			quadToRelative(8F, 12F, 0F, 24F)
			moveTo(24F, 4F)
			quadToRelative(-8F, 12F, 0F, 24F)
		},
		VwinInfo("十") {
			moveTo(16F, 4F)
			verticalLineTo(28F)
			moveTo(4F, 16F)
			horizontalLineTo(28F)
		},
		VwinInfo("乃") {
			moveTo(4F, 4F)
			horizontalLineTo(24F)
			curveToRelative(4F, 0F, 4F, 6F, 0F, 6F)
			horizontalLineTo(12F)
			curveToRelative(-4F, 0F, -4F, 6F, 0F, 6F)
			horizontalLineTo(24F)
			curveToRelative(4F, 0F, 4F, 12F, -20F, 12F)
		},
		VwinInfo("厶") {
			moveTo(16F, 4F)
			verticalLineTo(16F)
			quadToRelative(0F, 4F, -4F, 4F)
			reflectiveQuadToRelative(-4F, 4F)
			reflectiveQuadToRelative(4F, 4F)
			horizontalLineToRelative(8F)
			quadToRelative(4F, 0F, 4F, -4F)
			reflectiveQuadToRelative(-4F, -4F)
		},
		VwinInfo("丿") {
			moveTo(24F, 4F)
			curveTo(24F, 16F, 8F, 4F, 8F, 28F)
		},
		VwinInfo("乀") {
			moveTo(8F, 4F)
			curveTo(8F, 16F, 24F, 4F, 24F, 28F)
		},
		VwinInfo("𠂆") {
			moveTo(24F, 4F)
			quadTo(8F, 8F, 8F, 28F)
		},
		VwinInfo("乁") {
			moveTo(8F, 4F)
			quadTo(24F, 8F, 24F, 28F)
		},
		VwinInfo("𠃊") {
			moveTo(8F, 4F)
			verticalLineTo(20F)
			quadToRelative(0F, 4F, 4F, 4F)
			quadTo(24F, 24F, 24F, 28F)
		},
		VwinInfo("四") {
			moveTo(4F, 4F)
			horizontalLineTo(28F)
			verticalLineTo(24F)
			curveToRelative(0F, 4F, -24F, 4F, -24F, 0F)
			close()
			moveTo(12F, 4F)
			quadToRelative(0F, 16F, -8F, 16F)
			moveTo(20F, 4F)
			quadToRelative(0F, 16F, 8F, 16F)
		},
		VwinInfo("七") {
			moveTo(4F, 12F)
			horizontalLineTo(28F)
			moveTo(16F, 4F)
			verticalLineTo(16F)
			curveToRelative(0F, 4F, 4F, 0F, 4F, 12F)
		},
	)),
	LejInfo("屮", """
		-++++++++-
		+++++++++ 
		+++++++  
	""", listOf(
		VwinInfo("屮"),
		VwinInfo("㐀"),
		VwinInfo("丯"),
		VwinInfo("亇"),
		VwinInfo("來"),
		VwinInfo("木"),
		VwinInfo("丫"),
		VwinInfo("才"),
		VwinInfo("乇"),
		VwinInfo("𠂹") {
			moveTo(20F, 4F)
			quadToRelative(-4F, 4F, -4F, 8F)
			verticalLineTo(28F)
			moveTo(4F, 12F)
			lineToRelative(4F, -4F)
			lineToRelative(4F, 4F)
			moveToRelative(8F, 0F)
			lineToRelative(4F, -4F)
			lineToRelative(4F, 4F)
			moveTo(4F, 20F)
			lineToRelative(4F, -4F)
			lineToRelative(4F, 4F)
			moveToRelative(8F, 0F)
			lineToRelative(4F, -4F)
			lineToRelative(4F, 4F)
		}, // 垂上 TODO 𠂹
		VwinInfo("𥝌") {
			moveTo(20F, 4F)
			quadToRelative(-4F, 4F, -4F, 8F)
			verticalLineTo(28F)
			moveTo(8F, 8F)
			quadToRelative(0F, 8F, 8F, 8F)
			reflectiveQuadToRelative(8F, -8F)
			moveTo(8F, 28F)
			quadToRelative(0F, -8F, 8F, -8F)
			reflectiveQuadToRelative(8F, 8F)
		}, // 禾反 TODO 𥝌
		VwinInfo("𢎘") {
			moveTo(4F, 4F)
			horizontalLineTo(24F)
			curveToRelative(8F, 0F, 0F, 8F, -8F, 8F)
//		lineToRelative(-8F, 4F)
			moveTo(8F, 4F)
			verticalLineToRelative(8F)
			curveToRelative(0F, 4F, 8F, 4F, 8F, 8F)
			verticalLineTo(28F)
		}, // 圅上 TODO 𢎘
		VwinInfo("𠧪") {
			moveTo(16F, 4F)
			verticalLineToRelative(8F)
			lineToRelative(6F, -6F)
			moveTo(8F, 12F)
			verticalLineTo(24F)
			curveTo(8F, 28F, 24F, 28F, 24F, 24F)
			verticalLineTo(12F)
			close()
			moveTo(8F, 18F)
			lineToRelative(8F, -6F)
			lineToRelative(8F, 6F)
			moveTo(8F, 24F)
			lineToRelative(8F, -6F)
			lineToRelative(8F, 6F)
		}, // TODO 𠧪
		VwinInfo("齊"),
		VwinInfo("片"),
		VwinInfo("爿"),
		VwinInfo("禾"),
		VwinInfo("米"),
		VwinInfo("尗"),
		VwinInfo("耑"),
		VwinInfo("韭"),
		VwinInfo("瓜"),
		VwinInfo("不"),
		VwinInfo("未"),
	)),
	LejInfo("小", """
		----------
		--++++++- 
		-------  
	""", listOf(
		VwinInfo("玉"),
		VwinInfo("小"),
		VwinInfo("冊"),
		VwinInfo("予"),
		VwinInfo("毌"),
		VwinInfo("吕"),
	)),
	LejInfo("口", """
		----------
		---++++-- 
		-------  
	""", listOf(
		VwinInfo("口"),
		VwinInfo("𠚕") {
			moveTo(4F, 4F)
			verticalLineTo(24F)
			curveTo(4F, 28F, 28F, 28F, 28F, 24F)
			verticalLineTo(4F)
			moveTo(4F, 12F)
			lineToRelative(6F, -6F)
			lineToRelative(6F, 6F)
			lineToRelative(6F, -6F)
			lineToRelative(6F, 6F)
			close()
			moveTo(4F, 24F)
			lineToRelative(6F, -6F)
			lineToRelative(6F, 6F)
			lineToRelative(6F, -6F)
			lineToRelative(6F, 6F)
		}, // 牙下 TODO 𠚕
		VwinInfo("𪞶") {
			moveTo(16F, 4F)
			verticalLineToRelative(4F)
			lineToRelative(8F, 4F)
			moveToRelative(-8F, -4F)
			lineToRelative(-8F, 4F)
			moveTo(8F, 8F)
			verticalLineTo(24F)
			curveTo(8F, 28F, 24F, 28F, 24F, 24F)
			verticalLineTo(8F)
			moveTo(12F, 18F)
			horizontalLineToRelative(8F)
		}, // 自省 TODO 𪞶
		VwinInfo("亼"),
	)),
	LejInfo("止", """
		---++++---
		---++++-- 
		--+++--  
	""", listOf(
		VwinInfo("止"),
		VwinInfo("𣥂") {
			moveTo(16F, 4F)
			verticalLineTo(28F)
			moveTo(4F, 4F)
			verticalLineTo(16F)
			quadToRelative(0F, 4F, 12F, 4F)
			moveTo(28F, 4F)
			verticalLineTo(24F)
			quadToRelative(0F, 4F, -12F, 4F)
			horizontalLineTo(4F)
		}, // 步下 TODO 𣥂
		VwinInfo("彳"),
		VwinInfo("亍"),
		VwinInfo("廴"),
		VwinInfo("足"),
		VwinInfo("疋"),
		VwinInfo("夊"),
		VwinInfo("㐄"),
		VwinInfo("夂"),
		VwinInfo("久"),
	)),
	LejInfo("又", """
		---+++----
		---++++-- 
		--+++--  
	""", listOf(
		VwinInfo("釆"),
		VwinInfo("爪"),
		VwinInfo("又"),
		VwinInfo("父"),
		VwinInfo("𠂇"),
		VwinInfo("寸"),
		VwinInfo("⿲爪爪𠄌") {
			moveTo(4F, 4F)
			verticalLineTo(20F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineToRelative(4F)
			moveToRelative(-8F, -8F)
			horizontalLineToRelative(8F)
			moveToRelative(-8F, -8F)
			horizontalLineToRelative(8F)
			moveTo(16F, 4F)
			verticalLineTo(20F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineToRelative(4F)
			moveToRelative(-8F, -8F)
			horizontalLineToRelative(8F)
			moveToRelative(-8F, -8F)
			horizontalLineToRelative(8F)
			moveTo(28F, 4F)
			verticalLineTo(24F)
			quadToRelative(0F, 4F, -4F, 4F)
			horizontalLineTo(4F)
		}, // TODO ⿲爪爪𠄌
		VwinInfo("手"),
		VwinInfo("九"),
		VwinInfo("丑"),
	)),
	LejInfo("目", """
		----------
		--++++++- 
		-------  
	""", listOf(
		VwinInfo("臣"),
		VwinInfo("目"),
		VwinInfo("眉"),
		VwinInfo("⿰丨⿱一キ") {
			moveTo(28F, 4F)
			horizontalLineTo(4F)
			verticalLineTo(20F)
			quadTo(4F, 28F, 12F, 28F)
			horizontalLineTo(16F)
			verticalLineTo(4F)
			moveTo(4F, 12F)
			horizontalLineTo(28F)
			moveTo(4F, 20F)
			horizontalLineTo(28F)
		}, // TODO ⿰丨⿱一キ
		VwinInfo("⿸广⿻〢コ") {
			moveTo(16F, 4F)
			verticalLineToRelative(4F)
			moveTo(8F, 4F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineTo(20F)
			quadToRelative(4F, 0F, 4F, -4F)
			moveTo(12F, 8F)
			verticalLineTo(18F)
			curveToRelative(0F, 4F, 8F, 4F, 8F, 0F)
			verticalLineTo(8F)
			moveTo(4F, 12F)
			horizontalLineTo(24F)
			curveToRelative(4F, 0F, 4F, 4F, 0F, 4F)
			horizontalLineTo(8F)
			quadTo(4F, 16F, 4F, 28F)
		}, // TODO ⿸广⿻〢コ
		VwinInfo("民"),
	)),
	LejInfo("隹", """
		----------
		--+++++-- 
		-------  
	""", listOf(
		VwinInfo("隹"),
		VwinInfo("⿳丿口一") {
			moveTo(4F, 4F)
			verticalLineTo(28F)
			moveTo(8F, 4F)
			quadToRelative(0F, 4F, 4F, 4F)
			curveToRelative(4F, 0F, 4F, 8F, 0F, 8F)
			reflectiveCurveToRelative(-4F, 4F, 0F, 4F)
			quadToRelative(16F, 0F, 16F, 8F)
		}, // TODO ⿳丿口一
		VwinInfo("⿹勹灬") {
			moveTo(28F, 28F)
			quadTo(28F, 4F, 24F, 4F)
			horizontalLineTo(8F)
			quadTo(4F, 4F, 4F, 8F)
			reflectiveQuadTo(8F, 12F)
			horizontalLineTo(16F)
			quadTo(20F, 12F, 20F, 28F)
			moveTo(24F, 28F)
			quadTo(24F, 8F, 20F, 8F)
			horizontalLineTo(4F)
			moveTo(16F, 12F)
			quadToRelative(0F, 4F, -4F, 4F)
			quadTo(4F, 16F, 4F, 28F)
			moveTo(12F, 16F)
			verticalLineTo(28F)
		}, // TODO ⿹勹灬
		VwinInfo("燕"),
		VwinInfo("𠃉") {
			moveTo(8F, 4F)
			quadToRelative(0F, 4F, 4F, 4F)
			reflectiveQuadToRelative(4F, 4F)
			curveToRelative(0F, 4F, -8F, 4F, -8F, 8F)
			quadToRelative(0F, 8F, 16F, 8F)
		}, // TODO 𠃉
	)),
	LejInfo("幺", """
		---++++---
		---++++-- 
		-------  
	""", listOf(
		VwinInfo("幺"),
		VwinInfo("叀"),
		VwinInfo("玄"),
		VwinInfo("糸"),
		VwinInfo("巠"),
		VwinInfo("𠔾") {
			moveTo(4F, 28F)
			curveTo(16F, 28F, 16F, 16F, 12F, 16F)
			reflectiveCurveToRelative(-8F, -8F, 4F, -8F)
			reflectiveCurveToRelative(8F, 8F, 4F, 8F)
			reflectiveCurveTo(16F, 28F, 28F, 28F)
			moveTo(16F, 4F)
			verticalLineToRelative(4F)
			moveTo(4F, 20F)
			horizontalLineTo(28F)
		}, // TODO 𠔾
		VwinInfo("率"),
		VwinInfo("己"),
	)),
	LejInfo("肉", """
		--++++++--
		--++++++- 
		-++++++  
	""", listOf(
		VwinInfo("牙"),
		VwinInfo("自"),
		VwinInfo("歺"),
		VwinInfo("冎"),
		VwinInfo("肉"),
		VwinInfo("角"),
		VwinInfo("白"),
		VwinInfo("呂"),
		VwinInfo("𦣻") {
			moveTo(24F, 4F)
			horizontalLineTo(8F)
			verticalLineTo(24F)
			curveTo(8F, 28F, 24F, 28F, 24F, 24F)
			verticalLineTo(8F)
			moveTo(16F, 4F)
			verticalLineToRelative(4F)
			lineToRelative(8F, 4F)
			moveToRelative(-8F, -4F)
			lineToRelative(-8F, 4F)
			moveToRelative(4F, 4F)
			horizontalLineToRelative(8F)
			moveToRelative(-8F, 4F)
			horizontalLineToRelative(8F)
		}, // TODO 𦣻
		VwinInfo("甶"),
		VwinInfo("彑"),
		VwinInfo("囟"),
		VwinInfo("心"),
		VwinInfo("耳"),
		VwinInfo("𦣝"),
		VwinInfo("卵"),
		VwinInfo("且"),
		VwinInfo("甲"),
	)),
	LejInfo("工", """
		-++++++++-
		-++++++++ 
		-++++++  
	""", listOf(
		VwinInfo("𠦒") {
			moveTo(8F, 4F)
			curveToRelative(0F, 24F, 16F, 24F, 16F, 0F)
			moveTo(4F, 8F)
			horizontalLineTo(28F)
			moveTo(4F, 16F)
			horizontalLineTo(28F)
			moveTo(16F, 8F)
			verticalLineTo(28F)
		}, // TODO 𠦒
		VwinInfo("冓"),
		VwinInfo("冉"),
		VwinInfo("乂"),
		VwinInfo("𠀠") {
			moveTo(4F, 8F)
			horizontalLineTo(28F)
			moveTo(8F, 4F)
			verticalLineTo(24F)
			curveToRelative(0F, 4F, 16F, 4F, 16F, 0F)
			verticalLineTo(4F)
			moveTo(8F, 8F)
			lineToRelative(16F, 16F)
			moveTo(24F, 8F)
			lineToRelative(-16F, 16F)
		}, // TODO 𠀠
		VwinInfo("丌"),
		VwinInfo("工"),
		VwinInfo("丶"),
		VwinInfo("弋"),
		VwinInfo("珡"),
		VwinInfo("匚"),
		VwinInfo("曲"),
		VwinInfo("弓"),
		VwinInfo("力"),
		VwinInfo("几"),
		VwinInfo("嘼"),
		VwinInfo("單"),
		VwinInfo("丁"),
		VwinInfo("壬"),
		VwinInfo("癸"),
		VwinInfo("辰"),
		VwinInfo("午"),
	)),
	LejInfo("皿", """
		---++++---
		--++++++- 
		--++++-  
	""", listOf(
		VwinInfo("鬲"),
		VwinInfo("用"),
		VwinInfo("豆"),
		VwinInfo("皿"),
		VwinInfo("𠙴") {
			moveTo(4F, 4F)
			verticalLineTo(24F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineTo(24F)
			quadToRelative(4F, 0F, 4F, -4F)
			verticalLineTo(4F)
		}, // TODO 𠙴
		VwinInfo("缶"),
		VwinInfo("臼"),
		VwinInfo("壺"),
		VwinInfo("甾"),
		VwinInfo("瓦"),
		VwinInfo("凡"),
		VwinInfo("斗"),
		VwinInfo("升"),
		VwinInfo("酉"),
	)),
	LejInfo("宀", """
		---++++---
		---++++-- 
		--++++-  
	""", listOf(
		VwinInfo("丹"),
		VwinInfo("丼"),
		VwinInfo("㫃"),
		VwinInfo("囧"),
		VwinInfo("彔"),
		VwinInfo("宀"),
		VwinInfo("舟"),
		VwinInfo("囪"),
		VwinInfo("𡆧") {
			moveTo(8F, 4F)
			quadToRelative(-4F, 0F, -4F, 4F)
			verticalLineTo(24F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineTo(24F)
			quadToRelative(4F, 0F, 4F, -4F)
			verticalLineTo(8F)
			quadToRelative(0F, -4F, -4F, -4F)
			close()
			moveTo(16F, 4F)
			verticalLineTo(28F)
			moveTo(9F, 8F)
			quadToRelative(4F, 8F, 0F, 16F)
			moveTo(23F, 8F)
			quadToRelative(-4F, 8F, 0F, 16F)
		}, // TODO 𡆧
		VwinInfo("西"),
		VwinInfo("戶"),
		VwinInfo("車"),
	)),
	LejInfo("人", """
		--++++++--
		--++++++- 
		-++++++  
	""", listOf(
		VwinInfo("人"),
		VwinInfo("𠤎"),
		VwinInfo("匕"),
		VwinInfo("身"),
		VwinInfo("㐆"),
		VwinInfo("尸"),
		VwinInfo("儿"),
		VwinInfo("𠂋") {
			moveTo(28F, 4F)
			quadTo(8F, 4F, 8F, 8F)
			quadTo(8F, 28F, 4F, 28F)
			moveTo(8F, 8F)
			horizontalLineTo(28F)
		}, // 后左上 TODO 𠂋
		VwinInfo("刁") {
			moveTo(4F, 4F)
			quadTo(24F, 4F, 24F, 8F)
			quadTo(24F, 28F, 28F, 28F)
			moveTo(24F, 8F)
			horizontalLineTo(4F)
		}, // TODO 刁
		VwinInfo("卩"),
		VwinInfo("㔿"),
		VwinInfo("勹"),
		VwinInfo("女"),
		VwinInfo("子"),
		VwinInfo("了"),
		VwinInfo("孑"),
		VwinInfo("孓"),
		VwinInfo("𠫓"),
	)),
	LejInfo("毛", """
		---++++---
		---++++-- 
		---++--  
	""", listOf(
		VwinInfo("革"),
		VwinInfo("𠘧") {
			moveTo(8F, 4F)
			verticalLineTo(8F)
			quadTo(8F, 24F, 4F, 24F)
			moveTo(8F, 8F)
			horizontalLineTo(20F)
			curveToRelative(8F, 0F, 8F, 8F, 0F, 8F)
			reflectiveCurveToRelative(-8F, 8F, 0F, 8F)
			quadTo(28F, 24F, 28F, 28F)
		}, // TODO 𠘧
		VwinInfo("羽"),
		VwinInfo("冖"),
		VwinInfo("衣"),
		VwinInfo("毛"),
		VwinInfo("冄"),
		VwinInfo("而"),
		VwinInfo("非"),
		VwinInfo("卂"),
	)),
	LejInfo("文", """
		---+++----
		---+++--- 
		--+++--  
	""", listOf(
		VwinInfo("丩"),
		VwinInfo("卜"),
		VwinInfo("爻"),
		VwinInfo("入"),
		VwinInfo("回"),
		VwinInfo("月"),
		VwinInfo("文"),
		VwinInfo("夕"),
		VwinInfo("彡"),
	)),
	LejInfo("豸", """
		---++++---
		---++++-- 
		---++--  
	""", listOf(
		VwinInfo("牛"),
		VwinInfo("𠁥") {
			moveTo(12F, 4F)
			lineToRelative(4F, 8F)
			moveToRelative(4F, -8F)
			lineToRelative(-4F, 8F)
			verticalLineTo(28F)
			moveTo(4F, 8F)
			horizontalLineTo(14F)
			moveTo(18F, 8F)
			horizontalLineTo(28F)
		}, // TODO 𠁥
		VwinInfo("羊"),
		VwinInfo("虍"),
		VwinInfo("牛"),
		VwinInfo("豕"),
		VwinInfo("豸"),
		VwinInfo("⿰丿匕") {
			moveTo(12F, 4F)
			quadToRelative(0F, 4F, -4F, 4F)
			quadTo(4F, 8F, 4F, 28F)
			moveTo(20F, 4F)
			quadToRelative(0F, 4F, 4F, 4F)
			curveToRelative(4F, 0F, 4F, 4F, 0F, 4F)
			quadTo(16F, 12F, 16F, 28F)
			moveTo(24F, 12F)
			quadTo(24F, 28F, 28F, 28F)
		}, // TODO ⿰丿匕
		VwinInfo("犬"),
		VwinInfo("魚"),
	)),
	LejInfo("大", """
		---+++----
		---+++--- 
		--+++--  
	""", listOf(
		VwinInfo("天"),
		VwinInfo("屰"),
		VwinInfo("大"),
		VwinInfo("亦"),
		VwinInfo("夨"),
		VwinInfo("夭"),
		VwinInfo("交"),
		VwinInfo("亢"),
		VwinInfo("叕"),
	)),
	LejInfo("水", """
		----------
		--++++++- 
		-------  
	""", listOf(
		VwinInfo("水"),
		VwinInfo("𡿨"),
		VwinInfo("泉"),
		VwinInfo("永"),
		VwinInfo("𠂢"),
		VwinInfo("雨"),
	)),
	LejInfo("戈", """
		--++++++--
		--++++++- 
		-+++++-  
	""", listOf(
		VwinInfo("王"),
		VwinInfo("士"),
		VwinInfo("干"),
		VwinInfo("盾"),
		VwinInfo("刀"),
		VwinInfo("刃"),
		VwinInfo("矢"),
		VwinInfo("勿"),
		VwinInfo("戈"),
		VwinInfo("或"),
		VwinInfo("我"),
		VwinInfo("亅"),
		VwinInfo("𠄌"),
		VwinInfo("斤"),
		VwinInfo("矛"),
		VwinInfo("戊"),
		VwinInfo("卯"),
	)),
	LejInfo("虫", """
		----------
		--+++++-- 
		-------  
	""", listOf(
		VwinInfo("貝"),
		VwinInfo("求"),
		VwinInfo("虫"),
		VwinInfo("它"),
		VwinInfo("巳"),
	)),
	LejInfo("土", """
		--++++++--
		--++++++- 
		-+++++-  
	""", listOf(
		VwinInfo("凵"),
		VwinInfo("⿱亠囗") {
			moveTo(16F, 4F)
			verticalLineToRelative(4F)
			lineToRelative(12F, 8F)
			moveToRelative(-12F, -8F)
			lineToRelative(-12F, 8F)
			moveTo(12F, 16F)
			horizontalLineToRelative(8F)
			verticalLineTo(20F)
			curveToRelative(0F, 4F, -8F, 4F, -8F, 0F)
			close()
		}, // TODO ⿱亠囗
		VwinInfo("冂"),
		VwinInfo("囗"),
		VwinInfo("山"),
		VwinInfo("广"),
		VwinInfo("厂"),
		VwinInfo("㇈"),
		VwinInfo("氏"),
		VwinInfo("土"),
		VwinInfo("田"),
		VwinInfo("𠃬") {
			moveTo(4F, 4F)
			horizontalLineTo(24F)
			quadToRelative(4F, 0F, 4F, 4F)
			verticalLineTo(12F)
			quadToRelative(0F, 4F, -4F, 4F)
			horizontalLineTo(8F)
			quadToRelative(-4F, 0F, -4F, 4F)
			verticalLineTo(24F)
			quadToRelative(0F, 4F, 4F, 4F)
			horizontalLineTo(28F)
			moveTo(8F, 8F)
			horizontalLineToRelative(12F)
			curveToRelative(4F, 0F, 4F, 4F, 0F, 4F)
			horizontalLineToRelative(-12F)
			moveTo(24F, 20F)
			horizontalLineToRelative(-12F)
			curveToRelative(-4F, 0F, -4F, 4F, 0F, 4F)
			horizontalLineToRelative(12F)
		}, // TODO 𠃬
		VwinInfo("𠂤"),
		VwinInfo("𨸏"),
		VwinInfo("厽"),
		VwinInfo("宁"),
		VwinInfo("亞"),
	)),
	LejInfo("乙", """
		----------
		--++++++- 
		-------  
	""", listOf(
		VwinInfo("气"),
		VwinInfo("丂"),
		VwinInfo("𠀀") {
			moveTo(4F, 4F)
			horizontalLineTo(28F)
			moveTo(16F, 4F)
			verticalLineTo(8F)
			quadTo(16F, 12F, 12F, 12F)
			curveTo(4F, 12F, 4F, 28F, 24F, 28F)
		}, // TODO 𠀀
		VwinInfo("火"),
		VwinInfo("㐅"),
		VwinInfo("乙"),
	)),
)

/*
	一	zi
	屮	truioh
	小	seau
	口	khou
	止	droi
	又	yiow
	目	muh
	隹	druei
	幺	qiau
	肉	zrow
	工	gung
	皿	mhin
	宀	mjan
	人	zrwin
	毛	mauq
	文	vwin
	豸	druih
	大	dah
	水	sroei
	戈	guio
	虫	xoei
	土	thu
	乙	qhi
 */
