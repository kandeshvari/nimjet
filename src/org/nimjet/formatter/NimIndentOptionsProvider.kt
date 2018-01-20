package org.nimjet.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.FileTypeIndentOptionsProvider
import org.nimjet.NimFileType


class NimIndentOptionsProvider : FileTypeIndentOptionsProvider {

        override fun createIndentOptions(): CommonCodeStyleSettings.IndentOptions {
                val indentOptions = CommonCodeStyleSettings.IndentOptions()
                indentOptions.INDENT_SIZE = 4
                indentOptions.TAB_SIZE = 4
                return indentOptions
        }

        override fun createOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()

        override fun prepareForReformat(p0: PsiFile?) {}

        override fun getPreviewText(): String =
                "var \n" +
                        "  a : int = 123\n" +
                        "  b : string = \"some text\"\n" +
                        "const c = 123.45\n" +
                        "proc x(a,b,c) =\n" +
                        "  echo a,b,c\n" +
                        "type Point = object\n" +
                        "  x:int\n" +
                        "  y:int\n"

        override fun getFileType(): FileType = NimFileType
}
