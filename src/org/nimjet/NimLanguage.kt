package org.nimjet

import com.intellij.lang.Language

class NimLanguage : Language("Nim", "text/nim") {
	companion object {
		val INSTANCE = NimLanguage()
	}
}
