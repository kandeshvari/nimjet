package org.nimjet.psi

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FileTypeIndex
import com.intellij.util.indexing.FileBasedIndex
import generated.psi.IdentifierDef
import org.nimjet.NimFile
import org.nimjet.NimFileType
import java.util.*


object NimUtil {
	fun findProperties(project: Project, key: String): List<IdentifierDef> {
		var result: MutableList<IdentifierDef>? = null
		val virtualFiles = FileBasedIndex.getInstance().getContainingFiles<FileType, Void>(FileTypeIndex.NAME, NimFileType,
			GlobalSearchScope.allScope(project))
		for (virtualFile in virtualFiles) {
			val nimFile = PsiManager.getInstance(project).findFile(virtualFile) as NimFile?
			if (nimFile != null) {
				val properties = PsiTreeUtil.getChildrenOfType(nimFile, IdentifierDef::class.java!!)
				if (properties != null) {
					for (property in properties!!) {
						if (key == property.name) {
							if (result == null) {
								result = ArrayList<IdentifierDef>()
							}
							result.add(property)
						}
					}
				}
			}
		}
		return if (result != null) result else Collections.emptyList()
	}

	fun findProperties(project: Project): List<IdentifierDef> {
		val result = ArrayList<IdentifierDef>()
		val virtualFiles = FileBasedIndex.getInstance().getContainingFiles<FileType, Void>(FileTypeIndex.NAME, NimFileType,
			GlobalSearchScope.allScope(project))
		for (virtualFile in virtualFiles) {
			val nimFile = PsiManager.getInstance(project).findFile(virtualFile) as NimFile?
			if (nimFile != null) {
				val properties = PsiTreeUtil.getChildrenOfType(nimFile, IdentifierDef::class.java!!)
//				if (properties != null) {
//					Collections.addAll(result, properties)
//				}
			}
		}
		return result
	}
}
