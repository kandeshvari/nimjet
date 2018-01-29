package org.nimjet.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

import org.nimjet.NimIcons

class NimColorSettingsPage : ColorSettingsPage {

	private val ATTRS = arrayOf(
		AttributesDescriptor("Line comment", NimSyntaxHighlighter.LINE_COMMENT),
		AttributesDescriptor("Block comment", NimSyntaxHighlighter.BLOCK_COMMENT),
		AttributesDescriptor("Doc comment", NimSyntaxHighlighter.DOC_COMMENTS),
		AttributesDescriptor("Identifier", NimSyntaxHighlighter.IDENTIFIER),
		AttributesDescriptor("Keyword", NimSyntaxHighlighter.KEYWORD),
		AttributesDescriptor("String", NimSyntaxHighlighter.STRING),
		AttributesDescriptor("Number", NimSyntaxHighlighter.NUMBER),
		AttributesDescriptor("Operator", NimSyntaxHighlighter.OPERATOR),
		AttributesDescriptor("Dot", NimSyntaxHighlighter.DOT),
		AttributesDescriptor("Comma", NimSyntaxHighlighter.COMMA),
		AttributesDescriptor("Brackets", NimSyntaxHighlighter.BRACKETS),
		AttributesDescriptor("Braces", NimSyntaxHighlighter.BRACES),
		AttributesDescriptor("Parentheses", NimSyntaxHighlighter.PARENTHESES),
		AttributesDescriptor("Pragma", NimSyntaxHighlighter.PRAGMA),
		AttributesDescriptor("Function declaration", NimSyntaxHighlighter.FUNCTION_DECLARATION),
		AttributesDescriptor("Function call", NimSyntaxHighlighter.FUNCTION_CALL),
		AttributesDescriptor("Type definition", NimSyntaxHighlighter.TYPE_DEFINITION),
		AttributesDescriptor("Type use", NimSyntaxHighlighter.TYPE),
		AttributesDescriptor("Public operator", NimSyntaxHighlighter.PUBLIC_OPERATOR),
		AttributesDescriptor("Function parameter", NimSyntaxHighlighter.FUNCTION_PARAMETER),
		AttributesDescriptor("Built-in type", NimSyntaxHighlighter.BUILTIN_TYPE),
		AttributesDescriptor("Output parameter 'result'", NimSyntaxHighlighter.RESULT_PARAM)
	)

	override fun getHighlighter(): SyntaxHighlighter = NimSyntaxHighlighter()
	override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
	override fun getIcon(): Icon? = NimIcons.FILE
	override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRS
	override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY;
	override fun getDisplayName(): String = "Nim"

	override fun getDemoText(): String =
		"# create and greet someone\n" +
                "type Person = object\n" +
                "  name: string\n" +
                "  age: int\n" +
                "\n" +
                "proc greet(p: Person) =\n" +
                "  echo \"Hi, I'm \", p.name, \".\"\n" +
                "  echo \"I am \", p.age, \" years old.\"\n" +
                "\n" +
                "let p = Person(name:\"Jon\", age:18)\n" +
                "p.greet() # or greet(p)" +
                "" +
                "# declare a C procedure..\n" +
                "proc unsafeScanf(f: File, s: cstring)\n" +
                "  {.varargs,\n" +
                "    importc: \"fscanf\",\n" +
                "    header: \"<stdio.h>\".}\n" +
                "\n" +
                "# ..and use it...\n" +
                "var x: cint\n" +
                "stdin.unsafeScanf(\"%d\", addr x)" +
                "" +
                "# compute average line length\n" +
                "var\n" +
                "  sum = 0\n" +
                "  count = 0\n" +
                "\n" +
                "for line in stdin.lines:\n" +
                "  sum += line.len\n" +
                "  count += 1\n" +
                "\n" +
                "echo(\"Average line length: \",\n" +
                "  if count > 0: sum / count else: 0)"
}
