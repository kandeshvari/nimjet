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
                        // NO blank line before object fields
                        .beforeInside(OBJECT_FIELDS, OBJECT_DEF).blankLines(0)

                        // space around `=`
                        .around(T_EQ).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)

                        // blank line before block sections (`proc`, `type`, ...)
                        .before(BLOCK_SECT).blankLines(1)
//                        .before(BLOCK_SECT).blankLines(settings.BLANK_LINES_AROUND_METHOD)

                        // NO space before `:`
                        .before(T_COLON).none()

                        // NO space before `:`
                        .after(T_COLON).spaceIf(settings.SPACE_AFTER_COLON)

                        // NO space before `,`
                        .before(T_COMMA).none()

                        // space after `,`
                        .after(T_COMMA).spaceIf(settings.SPACE_AFTER_COMMA)

                        // space between `var` and variable definition
                        .beforeInside(VAR_DEF, VAR_SECT).spaces(1)

                        // space between `type` and variable definition
                        .beforeInside(TYPE_DEF, TYPE_SECT).spaces(1)

                        // space between `proc` and proc definition
                        .beforeInside(IDENTIFIER, PROC_DEF).spaces(1)


        }
}
