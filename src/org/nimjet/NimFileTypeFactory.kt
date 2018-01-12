package org.nimjet

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory

class NimFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(p0: FileTypeConsumer) {
		p0.consume(NimFileType.INSTANCE, "org/nimjet")
	}
}
