package org.nimjet.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import generated.psi.*;
import org.nimjet.psi.NimNamedElement;

public class NimPsiImplUtil {


	/** Identifier **/
	public static String getName(Identifier element) {
		return IdentifierImplUtil.getName(element);
	}

	public static PsiElement setName(Identifier element, String newName) {
		return IdentifierImplUtil.setName(element, newName);
	}

	public static NimNamedElement getNameIdentifier(Identifier element) {
		return IdentifierImplUtil.getNameIdentifier(element);
	}

	public static PsiReference getReference(Identifier element) {
		return IdentifierImplUtil.getReference(element);
	}
	/* END: Identifier */


	/** ProcDef **/
	public static String getName(ProcDef element) {
		return ProcDefImplUtil.getName(element);
	}

	public static PsiElement setName(ProcDef element, String newName) {
		return ProcDefImplUtil.setName(element, newName);
	}

	public static NimNamedElement getNameIdentifier(ProcDef element) {
		return ProcDefImplUtil.getNameIdentifier(element);
	}
	/* END: ProcDef */
}
