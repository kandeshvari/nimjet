package org.nimjet

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import generated.psi.Statement
import javax.swing.Icon

class NimFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NimLanguage) {
	override fun getFileType(): FileType = NimFileType
	override fun getIcon(flags: Int): Icon? = super.getIcon(flags)
	override fun toString(): String = "Nim file"
	fun getStatements(): List<Statement> {
		return PsiTreeUtil.getChildrenOfTypeAsList(this, Statement::class.java)
	}

}
