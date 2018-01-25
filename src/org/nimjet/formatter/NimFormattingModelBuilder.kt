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

	/**
	 * see https://github.com/JetBrains/intellij-community/blob/master/platform/lang-api/src/com/intellij/formatting/SpacingBuilder.java
	 * for side efefects :(
	 * **/
        private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
                return SpacingBuilder(settings, NimLanguage)

                        // NO blank line before `var`/`const`/`let` definition
	                .between(VAR_DEF, VAR_DEF).blankLines(0)

	                // NO blank lines before no-indent tokens
	                .before(AVOID_INDENT).blankLines(0)

                        // space around `=` in assignment
                        .aroundInside(T_EQ, ASSIGNMENT_EXPR).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)

                        // NO space around `=` in calls
                        .aroundInside(T_EQ, CALL_EXPR).none()

                        // blank line before block sections (`proc`, `type`, ...)
	                .before(PROC_DEF).blankLines(settings.BLANK_LINES_AROUND_METHOD)

                        // NO space before `:`
                        .before(T_COLON).none()

                        // NO space before `:`
                        .after(T_COLON).spaceIf(settings.SPACE_AFTER_COLON)

                        // NO space after '('
                        .after(T_LPAREN).none()

                        // NO space before ')'
                        .before(T_RPAREN).none()

                        // NO space after '['
                        .after(T_LBRACKET).none()

                        // NO space before ']'
                        .before(T_RBRACKET).none()

                        // NO space after '{'
                        .after(T_LBRACE).none()

                        // NO space before '}'
                        .before(T_RBRACE).none()


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
