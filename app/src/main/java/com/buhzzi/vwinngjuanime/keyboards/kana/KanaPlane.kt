package com.buhzzi.vwinngjuanime.keyboards.kana

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.latin.BackspaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.CtrlKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.latin.SpaceKey

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
	ims: VwinngjuanIms,
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
		ims,
		KeyContent(desc),
		modifier,
		arrayOf(kanaType, kanaVariation),
	) {
		commitText(desc)
	}
}

@Composable
private fun ShiftKanaTypeKey(
	ims: VwinngjuanIms,
	targetKanaType: KanaType,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		ims,
		KeyContent(when (targetKanaType) {
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
	ims: VwinngjuanIms,
	targetKanaVariation: KanaVariation,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		ims,
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

@Composable
private fun SpecialsKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.EmojiSymbols),
		modifier,
	) {
		planeStack.add(kanaSpecialsPlane)
	}
}

internal val kanaPlane: Plane = Plane({ stringResource(R.string.kana_plane) }) { ims ->
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		Column {
			Row(Modifier.weight(1F)) {
				MetaKey(ims, Modifier.weight(1F))
				ShiftKanaTypeKey(ims, when (kanaType) {
					KanaType.HIRAGANA -> KanaType.KATAKANA
					KanaType.KATAKANA -> KanaType.HIRAGANA
				}, Modifier.weight(1F))
				sequenceOf(
					KanaVariation.SEI,
					KanaVariation.DAKU,
					KanaVariation.HANDAKU,
					KanaVariation.KOGAKI,
				).forEach {
					if (kanaVariation != it) {
						ShiftKanaVariationKey(ims, it, Modifier.weight(1F))
					}
				}
				CtrlKey(ims, Modifier.weight(1F))
				BackspaceKey(ims, Modifier.weight(1F))
			}
			Row(Modifier.weight(4F)) {
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"あ", "あ゙", "あ゚", "ぁ",
						"ア", "ア゙", "ア゚", "ァ",
					Modifier.weight(1F))
					KanaKey(ims,
						"い", "い゙", "い゚", "ぃ",
						"イ", "イ゙", "イ゚", "ィ",
					Modifier.weight(1F))
					KanaKey(ims,
						"う", "ゔ", "う゚", "ぅ",
						"ウ", "ヴ", "ウ゚", "ゥ",
					Modifier.weight(1F))
					KanaKey(ims,
						"え", "え゙", "え゚", "ぇ",
						"エ", "エ゙", "エ゚", "ェ",
					Modifier.weight(1F))
					KanaKey(ims,
						"お", "お゙", "お゚", "ぉ",
						"オ", "オ゙", "オ゚", "ォ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"か", "が", "か゚", "ゕ",
						"カ", "ガ", "カ゚", "ヵ",
					Modifier.weight(1F))
					KanaKey(ims,
						"き", "ぎ", "き゚", "",
						"キ", "ギ", "キ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"く", "ぐ", "く゚", "",
						"ク", "グ", "ク゚", "ㇰ",
					Modifier.weight(1F))
					KanaKey(ims,
						"け", "げ", "け゚", "ゖ",
						"ケ", "ゲ", "ケ゚", "ヶ",
					Modifier.weight(1F))
					KanaKey(ims,
						"こ", "ご", "こ゚", "",
						"コ", "ゴ", "コ゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"さ", "ざ", "さ゚", "",
						"サ", "ザ", "サ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"し", "じ", "し゚", "",
						"シ", "ジ", "シ゚", "ㇱ",
					Modifier.weight(1F))
					KanaKey(ims,
						"す", "ず", "す゚", "",
						"ス", "ズ", "ス゚", "ㇲ",
					Modifier.weight(1F))
					KanaKey(ims,
						"せ", "ぜ", "せ゚", "",
						"セ", "ゼ", "セ゚", "ㇳ",
					Modifier.weight(1F))
					KanaKey(ims,
						"そ", "ぞ", "そ゚", "",
						"ソ", "ゾ", "ソ゚", "ㇴ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"た", "だ", "た゚", "",
						"タ", "ダ", "タ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"ち", "ぢ", "ち゚", "",
						"チ", "ヂ", "チ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"つ", "づ", "つ゚", "っ",
						"ツ", "ヅ", "ツ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"て", "で", "て゚", "",
						"テ", "デ", "テ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"と", "ど", "と゚", "",
						"ト", "ド", "ト゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"な", "", "な゚", "",
						"ナ", "", "ナ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"に", "", "に゚", "",
						"ニ", "", "ニ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"ぬ", "", "ぬ゚", "",
						"ヌ", "", "ヌ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"ね", "", "ね゚", "",
						"ネ", "", "ネ゚", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"の", "", "の゚", "",
						"ノ", "", "ノ゚", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"は", "ば", "ぱ", "",
						"ハ", "バ", "パ", "ㇵ",
					Modifier.weight(1F))
					KanaKey(ims,
						"ひ", "び", "ぴ", "",
						"ヒ", "ビ", "ピ", "ㇶ",
					Modifier.weight(1F))
					KanaKey(ims,
						"ふ", "ぶ", "ぷ", "",
						"フ", "ブ", "プ", "ㇷ",
					Modifier.weight(1F))
					KanaKey(ims,
						"へ", "べ", "ぺ", "",
						"ヘ", "ベ", "ペ", "ㇸ",
					Modifier.weight(1F))
					KanaKey(ims,
						"ほ", "ぼ", "ぽ", "",
						"ホ", "ボ", "ポ", "ㇹ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"ま", "", "", "",
						"マ", "", "", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"み", "", "", "",
						"ミ", "", "", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"み", "", "", "",
						"ム", "", "", "ㇺ",
					Modifier.weight(1F))
					KanaKey(ims,
						"め", "", "", "",
						"メ", "", "", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"も", "", "", "",
						"モ", "", "", "",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"や", "", "", "ゃ",
						"ヤ", "", "", "ャ",
					Modifier.weight(1F))
					KanaKey(ims,
						"\uD82C\uDC06", "", "", "",
						"\uD82C\uDD20", "", "", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"ゆ", "", "", "ゅ",
						"ユ", "", "", "ュ",
					Modifier.weight(1F))
					KanaKey(ims,
						"\uD82C\uDC01", "", "", "",
						"\uD82C\uDD21", "", "", "",
					Modifier.weight(1F))
					KanaKey(ims,
						"よ", "", "", "ょ",
						"ヨ", "", "", "ョ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"ら", "", "ら゚", "",
						"ラ", "", "ラ゚", "ㇻ",
					Modifier.weight(1F))
					KanaKey(ims,
						"り", "", "り゚", "",
						"リ", "", "リ゚", "ㇼ",
					Modifier.weight(1F))
					KanaKey(ims,
						"る", "", "る゚", "",
						"ル", "", "ル゚", "ㇽ",
					Modifier.weight(1F))
					KanaKey(ims,
						"れ", "", "れ゚", "",
						"え", "", "え゚", "ㇾ",
					Modifier.weight(1F))
					KanaKey(ims,
						"ろ", "", "ろ゚", "",
						"ロ", "", "ロ゚", "ㇿ",
					Modifier.weight(1F))
				}
				Column(Modifier.weight(1F)) {
					KanaKey(ims,
						"わ", "わ゙", "", "",
						"ワ", "ヷ", "", "ヮ",
					Modifier.weight(1F))
					KanaKey(ims,
						"ゐ", "ゐ゙", "", "\uD82C\uDD50",
						"ヰ", "ヸ", "", "\uD82C\uDD64",
					Modifier.weight(1F))
					KanaKey(ims,
						"\uD82C\uDD1F", "", "", "\uD82C\uDD1F",
						"\uD82C\uDD22", "", "", "\uD82C\uDD22",
					Modifier.weight(1F))
					KanaKey(ims,
						"ゑ", "ゑ゙", "", "\uD82C\uDD51",
						"ヱ", "ヹ", "", "\uD82C\uDD65",
					Modifier.weight(1F))
					KanaKey(ims,
						"を", "を゙", "", "\uD82C\uDD52",
						"ヲ", "ヺ", "", "\uD82C\uDD66",
					Modifier.weight(1F))
				}
			}
			Row(Modifier.weight(1F)) {
				SpecialsKey(ims, Modifier.weight(1F))
				KanaKey(ims,
					"、", "、", "、", "、",
					"！", "！", "！", "！",
				Modifier.weight(1F))
				SpaceKey(ims, Modifier.weight(4F))
				KanaKey(ims,
					"。", "。", "。", "。",
					"？", "？", "？", "？",
					Modifier.weight(1F))
				KanaKey(ims,
					"ん", "", "", "",
					"ン", "", "", "\uD82C\uDD67",
				Modifier.weight(1F))
			}
		}
	}
}

internal const val KANA_SPECIALS_TITLE_SIZE = 0x40
internal const val KANA_SPECIALS_ITEM_ROW_SIZE = 0x40
internal const val KANA_SPECIALS_ROW_COUNT = 4

internal val kanaSpecialsPlane = Plane({ stringResource(R.string.kana_specials_plane) }) { ims ->
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		Column {
			val categories = remember { mutableStateListOf(
				"述\n字" to sequenceOf(
					'⿰' .. '⿻',
					'〾' .. '〿',
				).flatten().map { "$it" to "$it" },
				"文\n法" to sequenceOf(
					'、' .. '〠',
					'〰' .. '〰',
					'〶' .. '〶',
					'〻' .. '〽',
					'゙' .. 'ゞ',
					'゠' .. '゠',
					'・' .. 'ヾ',
				).flatten().map { "$it" to "$it" },
				"舊\n遣" to sequenceOf(
					'〱' .. '〵',
					'ゟ' .. 'ゟ',
					'ヿ' .. 'ヿ',
				).flatten().map { "$it" to "$it" },
				"序\n號" to sequenceOf(
					'㈠' .. '㍰',
					'㍺' .. '㍿',
					'㏠' .. '㏾',
				).flatten().map { "$it" to "$it" },
				"單\n位" to sequenceOf(
					'㍱' .. '㍹',
					'㎀' .. '㏟',
					'㏿' .. '㏿',
				).flatten().map { "$it" to "$it" },
//				R.string.qwerty_plane,
//				R.string.qwerty_plane,
//				R.string.qwerty_plane,
//				R.string.qwerty_plane,
			) }
			var selectedCategory by remember { mutableStateOf(categories.first().second) }
			Row(Modifier.weight(1F)) {
				OutlinedKey(
					ims,
					KeyContent(Icons.AutoMirrored.Filled.ArrowBackIos),
					Modifier.weight(1F),
				) {
					planeStack.removeLastOrNull() ?: planeStack.add(kanaPlane)
				}
				OutlinedSpace(Modifier.weight(5F)) {
					LazyRow {
						items(categories) { (categoryTitle, category) ->
							OutlinedKey(
								ims,
								KeyContent(categoryTitle),
								Modifier.width(KANA_SPECIALS_TITLE_SIZE.dp),
							) {
								selectedCategory = category
							}
						}
					}
				}
				BackspaceKey(ims, Modifier.weight(1F))
			}
			OutlinedSpace(Modifier.weight(4F)) {
				LazyRow {
					items(
						selectedCategory.windowed(KANA_SPECIALS_ROW_COUNT, KANA_SPECIALS_ROW_COUNT, true).toList()
					) { row ->
						Column(Modifier.width(KANA_SPECIALS_ITEM_ROW_SIZE.dp)) {
							row.forEach { (text, label) ->
								OutlinedKey(
									ims,
									KeyContent(label),
									Modifier.weight(1F),
								) {
									commitText(text)
								}
							}
							(KANA_SPECIALS_ROW_COUNT - row.size).takeIf { it > 0 }?.apply {
								Box(Modifier.weight(toFloat()))
							}
						}
					}
				}
			}
		}
	}
}
