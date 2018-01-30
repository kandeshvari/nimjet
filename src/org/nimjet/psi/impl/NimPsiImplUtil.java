package org.nimjet.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import generated.psi.IdentifierDef;
import org.nimjet.psi.ElementTypes;
import org.nimjet.psi.NimElementFactory;

public class NimPsiImplUtil {
	public static String getName(IdentifierDef element) {
		return element.getName();
	}

	public static PsiElement setName(IdentifierDef element, String newName) {
		ASTNode keyNode = element.getNode().findChildByType(ElementTypes.IDENT);
		if (keyNode != null) {

			IdentifierDef property = NimElementFactory.Companion.createProperty(element.getProject(), newName);
			ASTNode newKeyNode = property.getFirstChild().getNode();
			element.getNode().replaceChild(keyNode, newKeyNode);
		}
		return element;
	}

	public static PsiElement getNameIdentifier(IdentifierDef element) {
		ASTNode keyNode = element.getNode().findChildByType(ElementTypes.IDENT);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}
}
