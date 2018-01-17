package org.nimjet

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import javax.swing.Icon

class NimFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NimLanguage) {
        override fun getFileType(): FileType = NimFileType
        override fun getIcon(flags: Int): Icon? = super.getIcon(flags)
        override fun toString(): String = "Nim file"
}
