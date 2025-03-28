package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
	override fun toString() = "VwinInfo($label${druann?.let { ", <druann>" } ?: ""})"
}

@Preview(showBackground = true)
@Composable
private fun PreviewedDruannIcon() {
	Column {
		druannMap.forEach { (vwin, druann) ->
			Row(Modifier.weight(1F)) {
				Icon(druannIcon(druann), null)
				Text(vwin, style = TextStyle(fontFamily = FontFamily(Font(R.font.gen_ryu_min2_tc_b))))
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






internal val defaultMappedChars = listOf(
	'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
	'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
	'z', 'x', 'c', 'v', 'b', 'n', 'm',
)

internal object KeyMapPatternChar {
	const val INACTIVE = '-'
	const val ACTIVE = '+'
}

internal const val LEJ_KEY_MAP_PATTERN = """
	-++++++++-
	-++++++++.
	.+++++++..
"""

internal val lejKeyMapPatternMap = mapOf(
	"一lej綫條" to """
		--++++++--
		--++++++-.
		.--+++--..
	""".trimIndent(),
	"屮lej草木" to """
		+++++++++-
		+++++++++.
		.+++++++..
	""".trimIndent(),
	"小lej集合" to """
		----------
		--++++++-.
		.-------..
	""".trimIndent(),
	"口lej" to """
		----------
		---++++--.
		.-------..
	""".trimIndent(),
	"止lej足脛" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"又lej" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"目lej" to """
		----------
		--++++++-.
		.-------..
	""".trimIndent(),
	"隹lej" to """
		----------
		--+++++--.
		.-------..
	""".trimIndent(),
	"幺lej絲物" to """
		---++++---
		---++++--.
		.-------..
	""".trimIndent(),
	"肉lej身體" to """
		--++++++--
		-+++++++-.
		.-++++++..
	""".trimIndent(),
	"工lej工具" to """
		-++++++++-
		-++++++++.
		.-++++++..
	""".trimIndent(),
	"皿lej器皿" to """
		--+++++---
		--+++++--.
		.-+++++-..
	""".trimIndent(),
	"宀lej建築" to """
		---++++---
		---++++--.
		.--++++-..
	""".trimIndent(),
	"人lej" to """
		--++++++--
		-+++++++-.
		.-++++++..
	""".trimIndent(),
	"毛lej毛髮" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"文lej花紋" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"豸lej畜生" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"大lej" to """
		---+++----
		---+++---.
		.--+++--..
	""".trimIndent(),
	"水lej水流" to """
		----------
		--++++++-.
		.-------..
	""".trimIndent(),
	"戈lej兵器" to """
		--++++++--
		--++++++-.
		.-+++++-..
	""".trimIndent(),
	"虫lej" to """
		----------
		--+++++--.
		.-------..
	""".trimIndent(),
	"土lej地形" to """
		--++++++--
		--++++++-.
		.-++++++..
	""".trimIndent(),
	"乙lej氣動" to """
		----------
		--++++++-.
		.-------..
	""".trimIndent(),
)




internal val druannMap = mapOf<String, (PathBuilder.() -> Unit)>(
	"一" to {
		moveTo(4F, 16F)
		horizontalLineTo(28F)
	},
	"丄" to {
		moveTo(12F, 8F)
		horizontalLineTo(20F)
		moveTo(4F, 16F)
		horizontalLineTo(28F)
	},
	"丅" to {
		moveTo(4F, 16F)
		horizontalLineTo(28F)
		moveTo(12F, 24F)
		horizontalLineTo(20F)
	},
	"丨" to {
		moveTo(16F, 4F)
		verticalLineTo(28F)
	},
	"八" to {
		moveTo(8F, 4F)
		quadToRelative(8F, 12F, 0F, 24F)
		moveTo(24F, 4F)
		quadToRelative(-8F, 12F, 0F, 24F)
	},
	"十" to {
		moveTo(16F, 4F)
		verticalLineTo(28F)
		moveTo(4F, 16F)
		horizontalLineTo(28F)
	},
	"乃" to {
		moveTo(4F, 4F)
		horizontalLineTo(24F)
		curveToRelative(4F, 0F, 4F, 6F, 0F, 6F)
		horizontalLineTo(12F)
		curveToRelative(-4F, 0F, -4F, 6F, 0F, 6F)
		horizontalLineTo(24F)
		curveToRelative(4F, 0F, 4F, 12F, -20F, 12F)
	},
	"厶" to {
		moveTo(16F, 4F)
		verticalLineTo(16F)
		quadToRelative(0F, 4F, -4F, 4F)
		reflectiveQuadToRelative(-4F, 4F)
		reflectiveQuadToRelative(4F, 4F)
		horizontalLineToRelative(8F)
		quadToRelative(4F, 0F, 4F, -4F)
		reflectiveQuadToRelative(-4F, -4F)
	},
	"丿" to {
		moveTo(24F, 4F)
		curveTo(24F, 16F, 8F, 4F, 8F, 28F)
	},
	"乀" to {
		moveTo(8F, 4F)
		curveTo(8F, 16F, 24F, 4F, 24F, 28F)
	},
	"𠂆" to {
		moveTo(24F, 4F)
		quadTo(8F, 8F, 8F, 28F)
	},
	"乁" to {
		moveTo(8F, 4F)
		quadTo(24F, 8F, 24F, 28F)
	},
	"𠃊" to {
		moveTo(8F, 4F)
		verticalLineTo(20F)
		quadToRelative(0F, 4F, 4F, 4F)
		quadTo(24F, 24F, 24F, 28F)
	},
	"四" to {
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
	"七" to {
		moveTo(4F, 12F)
		horizontalLineTo(28F)
		moveTo(16F, 4F)
		verticalLineTo(16F)
		curveToRelative(0F, 4F, 4F, 0F, 4F, 12F)
	},
	"𠂹" to {
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
	"𥝌" to {
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
	"𢎘" to {
		moveTo(4F, 4F)
		horizontalLineTo(24F)
		curveToRelative(8F, 0F, 0F, 8F, -8F, 8F)
		moveTo(8F, 4F)
		verticalLineToRelative(8F)
		curveToRelative(0F, 4F, 8F, 4F, 8F, 8F)
		verticalLineTo(28F)
	}, // 圅上 TODO 𢎘
	"𠧪" to {
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
	"𠚕" to {
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
	"𪞶" to {
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
	"𣥂" to {
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
	"⿲爪爪𠄌" to {
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
	"⿰丨⿱一キ" to {
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
	"⿸广⿻〢コ" to {
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
	"⿳丿口一" to {
		moveTo(4F, 4F)
		verticalLineTo(28F)
		moveTo(8F, 4F)
		quadToRelative(0F, 4F, 4F, 4F)
		curveToRelative(4F, 0F, 4F, 8F, 0F, 8F)
		reflectiveCurveToRelative(-4F, 4F, 0F, 4F)
		quadToRelative(16F, 0F, 16F, 8F)
	}, // TODO ⿳丿口一
	"⿹勹灬" to {
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
	"𠃉" to {
		moveTo(8F, 4F)
		quadToRelative(0F, 4F, 4F, 4F)
		reflectiveQuadToRelative(4F, 4F)
		curveToRelative(0F, 4F, -8F, 4F, -8F, 8F)
		quadToRelative(0F, 8F, 16F, 8F)
	}, // TODO 𠃉
	"𠔾" to {
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
	"𦣻" to {
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
	"𠦒" to {
		moveTo(8F, 4F)
		curveToRelative(0F, 24F, 16F, 24F, 16F, 0F)
		moveTo(4F, 8F)
		horizontalLineTo(28F)
		moveTo(4F, 16F)
		horizontalLineTo(28F)
		moveTo(16F, 8F)
		verticalLineTo(28F)
	}, // TODO 𠦒
	"𠀠" to {
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
	"𠙴" to {
		moveTo(4F, 4F)
		verticalLineTo(24F)
		quadToRelative(0F, 4F, 4F, 4F)
		horizontalLineTo(24F)
		quadToRelative(4F, 0F, 4F, -4F)
		verticalLineTo(4F)
	}, // TODO 𠙴
	"𠂋" to {
		moveTo(28F, 4F)
		quadTo(8F, 4F, 8F, 8F)
		quadTo(8F, 28F, 4F, 28F)
		moveTo(8F, 8F)
		horizontalLineTo(28F)
	}, // 后左上 TODO 𠂋
	"刁" to {
		moveTo(4F, 4F)
		quadTo(24F, 4F, 24F, 8F)
		quadTo(24F, 28F, 28F, 28F)
		moveTo(24F, 8F)
		horizontalLineTo(4F)
	}, // TODO 刁
	"𠘧" to {
		moveTo(8F, 4F)
		verticalLineTo(8F)
		quadTo(8F, 24F, 4F, 24F)
		moveTo(8F, 8F)
		horizontalLineTo(20F)
		curveToRelative(8F, 0F, 8F, 8F, 0F, 8F)
		reflectiveCurveToRelative(-8F, 8F, 0F, 8F)
		quadTo(28F, 24F, 28F, 28F)
	}, // TODO 𠘧
	"𠁥" to {
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
	"⿰丿匕" to {
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
	"⿱亠囗" to {
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
	"𠃬" to {
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
	"𠀀" to {
		moveTo(4F, 4F)
		horizontalLineTo(28F)
		moveTo(16F, 4F)
		verticalLineTo(8F)
		quadTo(16F, 12F, 12F, 12F)
		curveTo(4F, 12F, 4F, 28F, 24F, 28F)
	}, // TODO 𠀀
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
