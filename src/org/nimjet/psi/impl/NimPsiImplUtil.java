package org.nimjet.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import generated.psi.*;
import org.nimjet.psi.ElementTypes;
import org.nimjet.psi.NimElementFactory;

import java.util.List;

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

	public PsiReference getReference(Identifier element) {
		PsiElement parent = element.getParent();
		if (parent instanceof PsiNamedElement)
			return null;

		if (parent instanceof IdentifierExpr) {
			PsiElement grand = parent.getParent();

			if (grand instanceof Pragma || grand instanceof ImportStmt)
				return null;

//			if (grand instanceof SimpleTypeDesc || grand instanceof ObjectCtor || grand instanceof TupleTypeExpr ||
//				grand instanceof BracketExpr && grand.getParent() instanceof SimpleTypeDesc)
//				return new TypeReference(this);
//
//			if (grand instanceof CallExpr && parent == ((CallExpr) grand).getCallee())
//				return new ProcReference(this, ((CallExpr) grand).getArgumentList());
		}

//		if (parent instanceof DotExpr) {
//			PsiElement grand = parent.getParent();
//			List<Expression> args = null;
//			if (grand instanceof CallExpr && ((CallExpr) grand).getCallee() == parent)
//				args = ((CallExpr) grand).getArgumentList();
//			return new MemberReference(((DotExpr) parent).getReceiver(), this, args);
//		}
//
//		if (parent instanceof CtorArg) {
//			PsiElement ctor = parent.getParent();
//			if (ctor instanceof ObjectCtor) {
//				return new MemberReference((Expression) ctor, this);
//			}
//		}

		return new IdentifierReference(this);
	}
}
