package com.buhzzi.vwinngjuanime.keyboards.kana

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.editor.FunctionalKeysRow
import com.buhzzi.vwinngjuanime.keyboards.latin.BackspaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.EnterKey
import com.buhzzi.vwinngjuanime.keyboards.latin.FullwidthSpaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsCategory
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsComposable
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsItem
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsKey

private enum class KanaType {
	HIRAGANA,
	KATAKANA,
}

private var kanaType by mutableStateOf(KanaType.HIRAGANA)

private enum class KanaVariation {
	SEI,
	DAKU,
	HANDAKU,
	KOGAKI,
}

private var kanaVariation by mutableStateOf(KanaVariation.SEI)

@Composable
private fun KanaKey(
	hiraganaSeiDesc: String,
	hiraganaDakuDesc: String,
	hiraganaHandakuDesc: String,
	hiraganaKogakiDesc: String,
	katakanaSeiDesc: String,
	katakanaDakuDesc: String,
	katakanaHandakuDesc: String,
	katakanaKogakiDesc: String,
	modifier: Modifier = Modifier,
) {
	val desc = when (kanaType) {
		KanaType.HIRAGANA -> when (kanaVariation) {
			KanaVariation.SEI -> hiraganaSeiDesc
			KanaVariation.DAKU -> hiraganaDakuDesc
			KanaVariation.HANDAKU -> hiraganaHandakuDesc
			KanaVariation.KOGAKI -> hiraganaKogakiDesc
		}
		KanaType.KATAKANA -> when (kanaVariation) {
			KanaVariation.SEI -> katakanaSeiDesc
			KanaVariation.DAKU -> katakanaDakuDesc
			KanaVariation.HANDAKU -> katakanaHandakuDesc
			KanaVariation.KOGAKI -> katakanaKogakiDesc
		}
	}
	OutlinedKey(
		KeyContent(desc),
		modifier,
		arrayOf(kanaType, kanaVariation),
	) {
		commitText(desc)
	}
}

@Composable
private fun ShiftKanaTypeKey(
	targetKanaType: KanaType,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		KeyContent(when (kanaType) {
			KanaType.HIRAGANA -> "平"
			KanaType.KATAKANA -> "片"
		}.reversed()),
		modifier,
		arrayOf(targetKanaType),
	) {
		kanaType = targetKanaType
	}
}

@Composable
private fun ShiftKanaVariationKey(
	targetKanaVariation: KanaVariation,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		KeyContent(when (targetKanaVariation) {
			KanaVariation.SEI -> "清"
			KanaVariation.DAKU -> "濁"
			KanaVariation.HANDAKU -> "半濁"
			KanaVariation.KOGAKI -> "小"
		}.reversed()),
		modifier,
	) {
		kanaVariation = targetKanaVariation
	}
}

internal val kanaPlane: Plane = Plane({ stringResource(R.string.kana_plane) }) {
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		Column {
			FunctionalKeysRow(Modifier.weight(1F))
			Row(Modifier.weight(1F)) {
				MetaKey(Modifier.weight(1F))
				ShiftKanaTypeKey(when (kanaType) {
					KanaType.HIRAGANA -> KanaType.KATAKANA
					KanaType.KATAKANA -> KanaType.HIRAGANA
				}, Modifier.weight(1F))
				sequenceOf(
					KanaVariation.SEI,
					KanaVariation.DAKU,
					KanaVariation.HANDAKU,
					KanaVariation.KOGAKI,
				).forEach {
					ShiftKanaVariationKey(it, Modifier.weight(1F)
						.run {
							if (kanaVariation == it) border(0x1.dp, Color.hsl(0F, 0F, 0.5F))
							else this
						},
					)
				}
				SpecialsKey(kanaSpecialsPlane, Modifier.weight(1F))
				BackspaceKey(Modifier.weight(1F))
			}
			Row(Modifier.weight(5F)) {
				Column(Modifier.weight(1F)) {
					KanaKey(
						"あ", "あ゙", "あ゚", "ぁ",
						"ア", "ア゙", "ア゚", "ァ",
					Modifier.weight(1F))
					KanaKey(
						"い", "い゙", "い゚", "ぃ",
						"イ", "イ゙", "イ゚", "ィ",
					Modifier.weight(1F))
					KanaKey(
						"う", "ゔ", "う゚", "ぅ",
						"ウ", "ヴ", "ウ゚", "ゥ",
					Modifier.weight(1F))
					KanaKey(
						"え", "え゙", "え゚", "ぇ",
						"エ", "エ゙", "エ゚", "ェ",
					Modifier.weight(1F))
					KanaKey(
						"お", "お゙", "お゚", "ぉ",
						"オ", "オ゙", "オ゚", "ォ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"か", "が", "か゚", "ゕ",
						"カ", "ガ", "カ゚", "ヵ",
					Modifier.weight(1F))
					KanaKey(
						"き", "ぎ", "き゚", "",
						"キ", "ギ", "キ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"く", "ぐ", "く゚", "",
						"ク", "グ", "ク゚", "ㇰ",
					Modifier.weight(1F))
					KanaKey(
						"け", "げ", "け゚", "ゖ",
						"ケ", "ゲ", "ケ゚", "ヶ",
					Modifier.weight(1F))
					KanaKey(
						"こ", "ご", "こ゚", "",
						"コ", "ゴ", "コ゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"さ", "ざ", "さ゚", "",
						"サ", "ザ", "サ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"し", "じ", "し゚", "",
						"シ", "ジ", "シ゚", "ㇱ",
					Modifier.weight(1F))
					KanaKey(
						"す", "ず", "す゚", "",
						"ス", "ズ", "ス゚", "ㇲ",
					Modifier.weight(1F))
					KanaKey(
						"せ", "ぜ", "せ゚", "",
						"セ", "ゼ", "セ゚", "ㇳ",
					Modifier.weight(1F))
					KanaKey(
						"そ", "ぞ", "そ゚", "",
						"ソ", "ゾ", "ソ゚", "ㇴ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"た", "だ", "た゚", "",
						"タ", "ダ", "タ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"ち", "ぢ", "ち゚", "",
						"チ", "ヂ", "チ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"つ", "づ", "つ゚", "っ",
						"ツ", "ヅ", "ツ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"て", "で", "て゚", "",
						"テ", "デ", "テ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"と", "ど", "と゚", "",
						"ト", "ド", "ト゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"な", "", "な゚", "",
						"ナ", "", "ナ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"に", "", "に゚", "",
						"ニ", "", "ニ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"ぬ", "", "ぬ゚", "",
						"ヌ", "", "ヌ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"ね", "", "ね゚", "",
						"ネ", "", "ネ゚", "",
					Modifier.weight(1F))
					KanaKey(
						"の", "", "の゚", "",
						"ノ", "", "ノ゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"は", "ば", "ぱ", "",
						"ハ", "バ", "パ", "ㇵ",
					Modifier.weight(1F))
					KanaKey(
						"ひ", "び", "ぴ", "",
						"ヒ", "ビ", "ピ", "ㇶ",
					Modifier.weight(1F))
					KanaKey(
						"ふ", "ぶ", "ぷ", "",
						"フ", "ブ", "プ", "ㇷ",
					Modifier.weight(1F))
					KanaKey(
						"へ", "べ", "ぺ", "",
						"ヘ", "ベ", "ペ", "ㇸ",
					Modifier.weight(1F))
					KanaKey(
						"ほ", "ぼ", "ぽ", "",
						"ホ", "ボ", "ポ", "ㇹ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"ま", "", "", "",
						"マ", "", "", "",
					Modifier.weight(1F))
					KanaKey(
						"み", "", "", "",
						"ミ", "", "", "",
					Modifier.weight(1F))
					KanaKey(
						"む", "", "", "",
						"ム", "", "", "ㇺ",
					Modifier.weight(1F))
					KanaKey(
						"め", "", "", "",
						"メ", "", "", "",
					Modifier.weight(1F))
					KanaKey(
						"も", "", "", "",
						"モ", "", "", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"や", "", "", "ゃ",
						"ヤ", "", "", "ャ",
					Modifier.weight(1F))
					KanaKey(
						"𛀆", "", "", "",
						"𛄠", "", "", "",
					Modifier.weight(1F))
					KanaKey(
						"ゆ", "", "", "ゅ",
						"ユ", "", "", "ュ",
					Modifier.weight(1F))
					KanaKey(
						"𛀁", "", "", "",
						"𛄡", "", "", "",
					Modifier.weight(1F))
					KanaKey(
						"よ", "", "", "ょ",
						"ヨ", "", "", "ョ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"ら", "", "ら゚", "",
						"ラ", "", "ラ゚", "ㇻ",
					Modifier.weight(1F))
					KanaKey(
						"り", "", "り゚", "",
						"リ", "", "リ゚", "ㇼ",
					Modifier.weight(1F))
					KanaKey(
						"る", "", "る゚", "",
						"ル", "", "ル゚", "ㇽ",
					Modifier.weight(1F))
					KanaKey(
						"れ", "", "れ゚", "",
						"え", "", "え゚", "ㇾ",
					Modifier.weight(1F))
					KanaKey(
						"ろ", "", "ろ゚", "",
						"ロ", "", "ロ゚", "ㇿ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(
						"わ", "わ゙", "", "",
						"ワ", "ヷ", "", "ヮ",
					Modifier.weight(1F))
					KanaKey(
						"ゐ", "ゐ゙", "", "𛅐",
						"ヰ", "ヸ", "", "𛅤",
					Modifier.weight(1F))
					KanaKey(
						"𛄟", "", "", "𛄟",
						"\uD82C\uDD22", "", "", "𛄢",
					Modifier.weight(1F))
					KanaKey(
						"ゑ", "ゑ゙", "", "𛅑",
						"ヱ", "ヹ", "", "𛅥",
					Modifier.weight(1F))
					KanaKey(
						"を", "を゙", "", "𛅒",
						"ヲ", "ヺ", "", "𛅦",
					Modifier.weight(1F))
				}
			}
			Row(Modifier.weight(1F)) {
				KanaKey(
					"。", "。", "。", "。",
					"？", "？", "？", "？",
					Modifier.weight(1F))
				KanaKey(
					"、", "、", "、", "、",
					"！", "！", "！", "！",
				Modifier.weight(1F))
				FullwidthSpaceKey(Modifier.weight(4F))
				KanaKey(
					"ん", "", "", "",
					"ン", "", "", "𛅧",
				Modifier.weight(1F))
				EnterKey(Modifier.weight(2F))
			}
		}
	}
}

private val kanaSpecialsPlane = Plane({ stringResource(R.string.kana_specials_plane) }) {
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		SpecialsComposable(remember { listOf(
			SpecialsCategory("述\n字", buildList {
				addAll(('⿰' .. '⿻').map { SpecialsItem(it) })
				addAll(('〾' .. '〿').map { SpecialsItem(it) })
			}),
			SpecialsCategory("文\n法", buildList {
				addAll(('　' .. '〠').map { SpecialsItem(it) })
				add(SpecialsItem('〰'))
				add(SpecialsItem('〶'))
				addAll(('〻' .. '〽').map { SpecialsItem(it) })
				addAll(('゛' .. 'ゞ').map { SpecialsItem(it) })
				add(SpecialsItem("◌゙", "゙"))
				add(SpecialsItem("◌゚", "゚"))
				addAll(('゠' .. '゠').map { SpecialsItem(it) })
				addAll(('・' .. 'ヾ').map { SpecialsItem(it) })
			}),
			SpecialsCategory("舊\n遣", buildList {
				addAll(('〱' .. '〵').map { SpecialsItem(it) })
				addAll(('ゟ' .. 'ゟ').map { SpecialsItem(it) })
				addAll(('ヿ' .. 'ヿ').map { SpecialsItem(it) })
			}),
			SpecialsCategory("序\n號", buildList {
				addAll(('㈠' .. '㍰').map { SpecialsItem(it) })
				addAll(('㍺' .. '㍿').map { SpecialsItem(it) })
				addAll(('㏠' .. '㏾').map { SpecialsItem(it) })
			}),
			SpecialsCategory("單\n位", buildList {
				addAll(('㍱' .. '㍹').map { SpecialsItem(it) })
				addAll(('㎀' .. '㏟').map { SpecialsItem(it) })
				addAll(('㏿' .. '㏿').map { SpecialsItem(it) })
			}),
		) })
	}
}
