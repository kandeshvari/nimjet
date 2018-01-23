package org.nimjet.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.nimjet.NimLanguage
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings


class NimLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
        override fun getCodeSample(p0: SettingsType): String =
                "var \n" +
                        "  a : int = 123\n" +
                        "  b : string = \"some text\"\n" +
                        "const c = 123.45\n" +
                        "proc x(a,b,c) =\n" +
                        "  echo a,b,c\n" +
                        "type Point = object\n" +
                        "  x:int\n" +
                        "  y:int\n"

        override fun getLanguage(): Language = NimLanguage

        override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: LanguageCodeStyleSettingsProvider.SettingsType) {
                if (settingsType == LanguageCodeStyleSettingsProvider.SettingsType.SPACING_SETTINGS) {
                        consumer.showStandardOptions("SPACE_AFTER_COLON")
                        consumer.moveStandardOption("SPACE_AFTER_COLON", "Other" )
                        consumer.showStandardOptions("SPACE_AFTER_COMMA")
                        consumer.renameStandardOption("SPACE_AFTER_COMMA", "spaces after ','")
                        consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
                        consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "spaces around operators")

                }
                else if (settingsType == LanguageCodeStyleSettingsProvider.SettingsType.BLANK_LINES_SETTINGS) {
//                        consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
                        consumer.showStandardOptions("BLANK_LINES_AROUND_METHOD")
                        consumer.renameStandardOption("BLANK_LINES_AROUND_METHOD", "Lines before block")
                }
        }
        override fun getDefaultCommonSettings() : CommonCodeStyleSettings {
                val defaultSettings = CommonCodeStyleSettings(NimLanguage)
                val indentOptions = defaultSettings.initIndentOptions()
                indentOptions.INDENT_SIZE = 4
                indentOptions.CONTINUATION_INDENT_SIZE = 4
                indentOptions.TAB_SIZE = 4
                indentOptions.USE_TAB_CHARACTER = false
                return defaultSettings
        }

        override fun getIndentOptionsEditor(): IndentOptionsEditor {
                return NimIndentOptionsEditor()
        }
}

class NimIndentOptionsEditor : IndentOptionsEditor() {
        override fun addComponents() {
                super.addComponents()
                myCbUseTab.isEnabled = false;
                myTabSizeLabel.isEnabled = true
                myTabSizeField.isEnabled = true
                myIndentLabel.isEnabled = true
                myIndentField.isEnabled = true

                myTabSizeLabel.isVisible = true
                myTabSizeField.isVisible = true
                myIndentLabel.isVisible = true
                myIndentField.isVisible = true
        }

        override fun setEnabled(enabled: Boolean) {
        }
}

