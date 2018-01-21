package org.nimjet.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import org.nimjet.NimLanguage
import com.intellij.application.options.IndentOptionsEditor


class NimCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
        override fun createCustomSettings(settings: CodeStyleSettings) = NimCodeStyleSettings(settings)
        override fun getConfigurableDisplayName() = NimLanguage.displayName
        override fun createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings?): Configurable =
                object : CodeStyleAbstractConfigurable(settings, originalSettings, configurableDisplayName) {
                        override fun createPanel(settings: CodeStyleSettings) = NimCodeStyleMainPanel(currentSettings, settings)
                        override fun getHelpTopic(): String? = null
                }

}

private class NimCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
        TabbedLanguageCodeStylePanel(NimLanguage, currentSettings, settings) {

        override fun initTabs(settings: CodeStyleSettings?) {
                addIndentOptionsTab(settings)
                addSpacesTab(settings)
//                addWrappingAndBracesTab(settings)
                addBlankLinesTab(settings)
        }

}
