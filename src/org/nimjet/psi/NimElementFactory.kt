package org.nimjet.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import generated.psi.IdentifierDef
import org.nimjet.NimFile
import org.nimjet.NimFileType


class NimElementFactory {
	companion object {
		fun createProperty(project: Project, name: String): IdentifierDef {
			val file = createFile(project, name)
			return file.firstChild as IdentifierDef
		}

		fun createFile(project: Project, text: String): NimFile {
			val name = "dummy.simple"
			return PsiFileFactory.getInstance(project).createFileFromText(name, NimFileType, text) as NimFile
		}
	}
}
