package org.nimjet.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.formatting.SpacingBuilder
import org.nimjet.NimLanguage
import org.nimjet.psi.ElementTypes.*


class NimFormattingModelBuilder : FormattingModelBuilder {
        override fun getRangeAffectingIndent(p0: PsiFile?, p1: Int, p2: ASTNode?): TextRange? = null

        override fun createModel(element: PsiElement?, settings: CodeStyleSettings): FormattingModel {
                return FormattingModelProvider
                        .createFormattingModelForPsiFile(element?.containingFile,
                                NimBlock(element!!.node,
                                        Wrap.createWrap(WrapType.NONE, false),
                                        Alignment.createAlignment(),
                                        createSpaceBuilder(settings)),
                                settings)
        }

        private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
                return SpacingBuilder(settings, NimLanguage)
                        // space around `=`
                        .around(T_EQ).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)

                        // blank line before `proc`
                        .before(PROCS_DEF).blankLines(settings.BLANK_LINES_AROUND_CLASS)

                        // NO space before `:`
                        .before(T_COLON).none()

                        // NO space before `,`
                        .before(T_COMMA).none()

                        // space after `,` in variable definition
                        .afterInside(T_COLON, VAR_DEF).spaces(1)

                        // space between `var` and variable definition
                        .beforeInside(VAR_DEF, VAR_SECT).spaces(1)

                        // space between `proc` and proc definition
                        .beforeInside(IDENTIFIER, PROC_DEF).spaces(1)

        }
}
