package org.nimjet

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class NimFileType : LanguageFileType(NimLanguage.INSTANCE) {
	companion object {
		val INSTANCE = NimFileType()
	}
	override fun getName(): String {
		return "Nim file"
	}

	override fun getDefaultExtension(): String {
		return "org/nimjet"
	}

	override fun getDescription(): String {
		return "Nim language file"
	}

	override fun getIcon(): Icon? {
		return NimIcons.FILE
	}
}
